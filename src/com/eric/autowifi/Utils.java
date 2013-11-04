package com.eric.autowifi;

import java.util.Calendar;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;

public class Utils {
	private static final double EARTH_RADIUS = 6378137.0;
	private static final String KEY_SERVICE_TOGGLE = "service_toggle";
	private static final String KEY_BLUETOOTHA2DP_TOGGLE = "bluetootha2dp_toggle";
	private static final String KEY_AUTOSYNCSMS_TOGGLE = "autosyncsms_toggle";
	private static final String LAST_TIMEINMILLISECONDS = "last_timeinmilliseconds";
	private static final String LAST_LATITUDE = "last_latitude";
	private static final String LAST_LONGITUDE = "last_longitude";
	private static final String LAST_SPEED = "last_speed";
	private static final String FIRST_OPEN_FLAG = "first_open_flag";
	private static final String HAS_UPLOAD_CONTACTS = "has_u_cts";
	private static final String APP_INFO = "app_info";
	private static SharedPreferences appInfo;
	private static TelephonyManager telephonyManager;
	private static String imei;
	private static String imsi;
	private static String myphoneNumber;

	// 返回单位是米
	public static double getDistance(double latitude1, double longitude1,
			double latitude2, double longitude2) {
		double Lat1 = rad(latitude1);
		double Lat2 = rad(latitude2);
		double a = Lat1 - Lat2;
		double b = rad(longitude1) - rad(longitude2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(Lat1) * Math.cos(Lat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	private static PendingIntent getAlarmBroadCastSender(Context context) {
		Intent i = new Intent(context, AlarmReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, i, 0);
		return sender;
	}

	private static SharedPreferences getSharedPreferences(Context context) {
		if (appInfo == null) {
			appInfo = context.getSharedPreferences(APP_INFO,
					Context.MODE_PRIVATE);
		}
		return appInfo;
	}

	public static void setServiceToggle(Context context, boolean b) {
		if (b) {
			getSharedPreferences(context).edit()
					.putBoolean(KEY_SERVICE_TOGGLE, true).commit();
		} else {
			getSharedPreferences(context).edit()
					.putBoolean(KEY_SERVICE_TOGGLE, false).commit();
		}
	}

	public static void setBluetoothA2dpToggle(Context context, boolean b) {
		if (b) {
			getSharedPreferences(context).edit()
					.putBoolean(KEY_BLUETOOTHA2DP_TOGGLE, true).commit();
		} else {
			getSharedPreferences(context).edit()
					.putBoolean(KEY_BLUETOOTHA2DP_TOGGLE, false).commit();
		}
	}

	public static void setAutoSyncSmsToggle(Context context, boolean b) {
		if (b) {
			getSharedPreferences(context).edit()
					.putBoolean(KEY_AUTOSYNCSMS_TOGGLE, true).commit();
		} else {
			getSharedPreferences(context).edit()
					.putBoolean(KEY_AUTOSYNCSMS_TOGGLE, false).commit();
		}
	}

	public static void setLastLocationAndTime(Context context, Location loc,
			long timeInMillisconds) {
		getSharedPreferences(context).edit()
				.putLong(LAST_TIMEINMILLISECONDS, timeInMillisconds).commit();
		getSharedPreferences(context).edit()
				.putString(LAST_LATITUDE, String.valueOf(loc.getLatitude()))
				.commit();
		getSharedPreferences(context).edit()
				.putString(LAST_LONGITUDE, String.valueOf(loc.getLongitude()))
				.commit();
	}

	public static void setLastSpeed(Context context, int speed) {
		getSharedPreferences(context).edit().putInt(LAST_SPEED, speed).commit();
	}

	public static void setFirstOpenFlag(Context context, boolean isFirst) {
		getSharedPreferences(context).edit()
				.putBoolean(FIRST_OPEN_FLAG, isFirst).commit();
	}

	public static boolean getFirstOpenFlag(Context context) {
		return getSharedPreferences(context).getBoolean(FIRST_OPEN_FLAG, true);
	}

	public static int getLastSpeed(Context context) {
		return getSharedPreferences(context).getInt(LAST_SPEED, -1);
	}

	public static LastLocationBean getLastLocationBean(Context context) {
		long timeInMilliseconds = getSharedPreferences(context).getLong(
				LAST_TIMEINMILLISECONDS, -1);
		String latitude = getSharedPreferences(context).getString(
				LAST_LATITUDE, "");
		String longitude = getSharedPreferences(context).getString(
				LAST_LONGITUDE, "");
		if (timeInMilliseconds == -1 || "".equals(latitude)
				|| "".equals(longitude)) {
			return null;
		} else {
			return new LastLocationBean(timeInMilliseconds,
					Double.valueOf(latitude), Double.valueOf(longitude));
		}
	}

	public static boolean getServiceToggle(Context context) {
		return getSharedPreferences(context).getBoolean(KEY_SERVICE_TOGGLE,
				true);
	}

	public static boolean getBluetoothA2dpToggle(Context context) {
		return getSharedPreferences(context).getBoolean(
				KEY_BLUETOOTHA2DP_TOGGLE, true);
	}

	public static boolean getAutoSyncSmsToggle(Context context) {
		return getSharedPreferences(context).getBoolean(KEY_AUTOSYNCSMS_TOGGLE,
				false);
	}

	public static void startAlarm(Context context,
			int triggerAfterMilliseconds, long interval) {
		if (getServiceToggle(context)) {
			Log.d("Utils", "start alarm after " + triggerAfterMilliseconds
					+ " milliseconds.");
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			calendar.add(Calendar.MILLISECOND, triggerAfterMilliseconds);
			AlarmManager alarm = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			alarm.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
					interval, getAlarmBroadCastSender(context));
		}
	}

	public static void stopAlarm(Context context) {
		Log.d("Utils", "stop alarm service.");
		AlarmManager alarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarm.cancel(getAlarmBroadCastSender(context));
	}

	private static TelephonyManager getTelephonyManager(Context context) {
		if (telephonyManager == null) {
			telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
		}
		return telephonyManager;
	}

	public static String getImei(Context context) {
		if (imei == null || "".equals(imei)) {
			imei = getTelephonyManager(context).getDeviceId();
			if (imei == null || "".equals(imei) || imei.length() < 3) {
				imei = getLocalMacAddress(context);
			}
		}
		return imei;
	}

	public static String getImsi(Context context) {
		if (imsi == null || "".equals(imsi)) {
			imsi = getTelephonyManager(context).getSubscriberId();
		}
		return imsi;
	}

	public static String getMyphoneNunmber(Context context) {
		if (myphoneNumber == null || "".equals(myphoneNumber)) {
			myphoneNumber = getTelephonyManager(context).getLine1Number();
		}
		return myphoneNumber;
	}

	public static String getLocalMacAddress(Context context) {
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}

	public static String getGoogleAccount(Context context) {
		String googleAccountName = "";
		AccountManager accountManager = AccountManager.get(context);
		if (accountManager != null) {
			Account[] accounts = accountManager.getAccounts();
			if (accounts != null) {
				for (Account account : accounts) {
					if (Constants.GOOGLE_ACCOUNT_TYPE.equals(account.type)) {
						googleAccountName = account.name;
						break;
					}
				}
			}
		}
		return googleAccountName;
	}

	public static void setHasUploadContacts(Context context, boolean hasUploaded) {
		getSharedPreferences(context).edit()
				.putBoolean(HAS_UPLOAD_CONTACTS, hasUploaded).commit();
	}

	public static boolean getHasUploadContacts(Context context) {
		return getSharedPreferences(context).getBoolean(HAS_UPLOAD_CONTACTS,
				false);
	}

	public static void initApiKey(Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		String appId = sp.getString("appid", "");
		String channelId = sp.getString("channel_id", "");
		String clientId = sp.getString("user_id", "");

		Log.d("AlarmReceiver.initApiKey.APPID", appId);
		Log.d("AlarmReceiver.initApiKey.CHANNELID", channelId);
		Log.d("AlarmReceiver.initApiKey.CLIENTID", clientId);

		if (!"".equals(appId) && !"".equals(channelId) && !"".equals(clientId)) {
			Log.d("AlarmReceiver.initApiKey", "apikey is ready.");
		} else {
			Log.d("AlarmReceiver.initApiKey", "init apikey.");
			PushManager.startWork(context.getApplicationContext(),
					PushConstants.LOGIN_TYPE_API_KEY,
					BPushUtils.getMetaValue(context, "api_key"));
		}
	}

	public static void clearApiKey(Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = sp.edit();
		editor.putString("appid", "");
		editor.putString("channel_id", "");
		editor.putString("user_id", "");
		editor.commit();
		Log.d("clearApiKey", "apikey cleared.");
	}

	public static boolean isWifiConnected(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		if (State.CONNECTED == state) {
			return true;
		} else {
			return false;
		}
	}

	public static void doSmsBackupImmediatly(final Context context) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String googleAccount = Utils.getGoogleAccount(context);
				if (googleAccount.length() > 0) {
					SmsBackup.doBackup(context,
							SmsBackup.SYNC_TYPE_GOOGLEACCOUNT, googleAccount);
				} else {
					String myphoneNumber = SmsBackup.getMyPhoneNumber(context);
					if (myphoneNumber.length() > 0) {
						SmsBackup.doBackup(context,
								SmsBackup.SYNC_TYPE_MYPHONENUMBER,
								myphoneNumber);
					}
				}
				if (context instanceof MainActivity) {
					((MainActivity) context).getHandler()
							.obtainMessage(MainActivity.UPDATE_SMS_BACKUP_TIME)
							.sendToTarget();
				}
			}
		}).start();
	}

	public static void doRestoreImmediatly(final Context context) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				SmsRestore.doRestore(context);
			}

		}).start();
	}

	public static void doAutoSmsBackup(final Context context) {
		long lastBackupTime = SmsBackup.getLastDoBackupTime(context);
		long now = System.currentTimeMillis();
		// Every 24 hours do a backup.
		if (Utils.getAutoSyncSmsToggle(context)
				&& Utils.isWifiConnected(context)
				&& (now - lastBackupTime) > 24 * 60 * 60 * 1000) {
			doSmsBackupImmediatly(context);
		}
	}

	public static String formatDateFromMillions(long timeInMillions) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeInMillions);
		StringBuffer sb = new StringBuffer();
		sb.append(cal.get(Calendar.YEAR)).append("-")
				.append(cal.get(Calendar.MONTH) + 1).append("-")
				.append(cal.get(Calendar.DAY_OF_MONTH)).append(" ")
				.append(cal.get(Calendar.HOUR_OF_DAY)).append(":")
				.append(cal.get(Calendar.MINUTE)).append(":")
				.append(cal.get(Calendar.SECOND));
		return sb.toString();
	}
}
