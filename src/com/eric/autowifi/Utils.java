package com.eric.autowifi;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.eric.profile.ProfileCatagoryActivity;
import com.eric.profile.ProfileService;
import com.eric.profile.db.ProfileBean;
import com.eric.profile.db.ProfileDB;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Utils {
	private static final String TAG = "Utils";
	private static final int GPS_TOGGLE = 3;
	// private static final int SYNC_TOGGLE = 2;
	private static final double EARTH_RADIUS = 6378137.0;
	private static final String KEY_SERVICE_TOGGLE = "service_toggle";
	private static final String KEY_BLUETOOTHA2DP_TOGGLE = "bluetootha2dp_toggle";
	private static final String KEY_AUTOSYNCSMS_TOGGLE = "autosyncsms_toggle";
	private static final String KEY_PROFILE_TOGGLE = "profile_toggle";
	private static final String LAST_TIMEINMILLISECONDS = "last_timeinmilliseconds";
	private static final String LAST_LATITUDE = "last_latitude";
	private static final String LAST_LONGITUDE = "last_longitude";
	private static final String LAST_SPEED = "last_speed";
	private static final String FIRST_OPEN_FLAG = "first_open_flag";
	private static final String HAS_UPLOAD_CONTACTS = "has_u_cts";
	private static final String LAST_CHECKUPDATE_TIME = "last_checkupdate_time";
	private static final String APP_INFO = "app_info";
	private static final String CURRENT_PROFILE_ID = "current_profile_id";
	private static SharedPreferences appInfo;
	private static TelephonyManager telephonyManager;
	private static String imei;
	private static String imsi;
	private static String myphoneNumber;
	private static String appName;

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

	public static void setProfileToggle(Context context, boolean b) {
		if (b) {
			getSharedPreferences(context).edit()
					.putBoolean(KEY_PROFILE_TOGGLE, true).commit();
		} else {
			getSharedPreferences(context).edit()
					.putBoolean(KEY_PROFILE_TOGGLE, false).commit();
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

	public static void setCurrentProfileId(Context context, int id) {
		getSharedPreferences(context).edit().putInt(CURRENT_PROFILE_ID, id)
				.commit();
	}

	public static int getCurrentProfileId(Context context) {
		return getSharedPreferences(context).getInt(CURRENT_PROFILE_ID,
				ProfileBean.DEFAULT_PROFILE_ID);
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

	public static boolean getProfileToggle(Context context) {
		return getSharedPreferences(context).getBoolean(KEY_PROFILE_TOGGLE,
				false);
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

	public static Intent doRestoreImmediatly(final Context context) {
		Intent intent = new Intent(context, SmsRestoreService.class);
		context.startService(intent);
		return intent;
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

	/**
	 * 返回当前程序版本名
	 */
	public static String getAppVersionName(Context context) {
		String versionName = "";
		try {
			// ---get the package info---
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			if (versionName == null || versionName.length() <= 0) {
				return "";
			}
		} catch (Exception e) {
			Log.e("VersionInfo", "Exception", e);
		}
		return versionName;
	}

	public static void setLastCheckUpdateTime(Context context, long time) {
		getSharedPreferences(context).edit()
				.putLong(LAST_CHECKUPDATE_TIME, time).commit();
	}

	public static long getLastCheckUpdateTime(Context context) {
		return getSharedPreferences(context).getLong(LAST_CHECKUPDATE_TIME, 0);
	}

	public static String getAppName(Context ctx) {
		if (appName == null) {
			appName = ctx.getResources().getString(R.string.app_name);
		}
		return appName;
	}

	public static String getAppFolder(Context context) {
		return getAppName(context);
	}

	public static int getRingerMode(Context context) {
		AudioManager audio = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		return audio.getRingerMode();
	}

	public static String formatProfileWifiName(List<String> selectedList) {
		if (selectedList != null) {
			StringBuffer sb = new StringBuffer();
			for (String s : selectedList) {
				sb.append(s.substring(1, s.length() - 1)).append(",");
			}
			sb.deleteCharAt(sb.length() - 1);
			return sb.toString();
		} else {
			return "";
		}
	}

	public static String formatProfileWifiName(String wifiNames) {
		Gson gson = new Gson();
		Type type = new TypeToken<List<String>>() {
		}.getType();
		List<String> selectedList = gson.fromJson(wifiNames, type);
		return formatProfileWifiName(selectedList);
	}

	public static void switchWifi(Context context, boolean isEnabled) {
		WifiManager manager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		manager.setWifiEnabled(isEnabled);
	}

	public static void switchBluetooth(boolean isEnabled) {
		BluetoothAdapter bluetoothadapter = BluetoothAdapter
				.getDefaultAdapter();
		if (isEnabled) {
			bluetoothadapter.enable();
		} else {
			bluetoothadapter.disable();
		}
	}

	private static boolean isGpsOpen(final Context context) {
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	// WIFI：0；背光高度：1；同步数据：2；GSP：3；蓝牙：4
	private static void switchToggle(Context context, int which) {
		Intent in = new Intent();
		in.setClassName("com.android.settings",
				"com.android.settings.widget.SettingsAppWidgetProvider");// 利用java反射功能，发送广播：
		in.addCategory("android.intent.category.ALTERNATIVE");
		in.setData(Uri.parse("custom:" + which));
		try {
			PendingIntent.getBroadcast(context, 0, in, 0).send();
		} catch (CanceledException e) {
			e.printStackTrace();
		}
	}

	public static void switchGps(Context context, boolean isEnabled) {
		if (isEnabled) {
			if (!isGpsOpen(context)) {
				switchToggle(context, GPS_TOGGLE);
			}
		} else {
			if (isGpsOpen(context)) {
				switchToggle(context, GPS_TOGGLE);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static void changeSettings(Context context, ProfileBean pb) {
		Log.d(TAG, "changeSetting:" + pb.getProfileName());
		int ringerMode = pb.getRingMode();
		int ringerVolumn = pb.getRingVolumn();
		int notificationMode = pb.getNotificationMode();
		int notificationVolumn = pb.getNotificationVolumn();
		int bluetooth = pb.getBluetooth();
		int gps = pb.getGps();
		// int sync = pb.getSyncData();
		int wifi = pb.getWifi();

		AudioManager am = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		if (ProfileBean.SOUND_RING == ringerMode) {
			if (am.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
				am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			}
			am.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,
					AudioManager.VIBRATE_SETTING_OFF);
			int ringerMax = am.getStreamMaxVolume(AudioManager.STREAM_RING);
			int rv = ringerVolumn * ringerMax / 100;
			am.setStreamVolume(AudioManager.STREAM_RING, rv,
					AudioManager.FLAG_ALLOW_RINGER_MODES);
			Log.d(TAG, "Set ringerVolumn to : " + rv);
			Log.d(TAG, "VIBRATE_SETTING_OFF");
		} else if (ProfileBean.SOUND_VIBRATE == ringerMode) {
			if (am.getRingerMode() != AudioManager.RINGER_MODE_VIBRATE) {
				am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			}
			am.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,
					AudioManager.VIBRATE_SETTING_ON);
			Log.d(TAG, "VIBRATE_SETTING_ON");
		} else if (ProfileBean.SOUND_SILENT == ringerMode) {
			if (am.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
				am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			}
			am.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,
					AudioManager.VIBRATE_SETTING_OFF);
			Log.d(TAG, "VIBRATE_SETTING_OFF");
		} else if (ProfileBean.SOUND_RING_AND_VIBRATE == ringerMode) {
			if (am.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
				am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			}
			am.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,
					AudioManager.VIBRATE_SETTING_ON);
			int ringerMax = am.getStreamMaxVolume(AudioManager.STREAM_RING);
			int rv = ringerVolumn * ringerMax / 100;
			am.setStreamVolume(AudioManager.STREAM_RING, rv,
					AudioManager.FLAG_ALLOW_RINGER_MODES);
			Log.d(TAG, "Set ringerVolumn to : " + rv);
			Log.d(TAG, "VIBRATE_SETTING_ON");
		} else {
			// nochange , do nothing
		}

		if (ProfileBean.SOUND_RING == notificationMode) {
			int notificationMax = am
					.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
			int rv = notificationVolumn * notificationMax / 100;
			am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, rv,
					AudioManager.FLAG_ALLOW_RINGER_MODES);
			Log.d(TAG, "Set notificationVolumn to : " + rv);
		} else if (ProfileBean.SOUND_VIBRATE == notificationMode) {
			am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0,
					AudioManager.FLAG_ALLOW_RINGER_MODES);
		} else if (ProfileBean.SOUND_SILENT == notificationMode) {
			am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0,
					AudioManager.FLAG_ALLOW_RINGER_MODES);
		} else if (ProfileBean.SOUND_RING_AND_VIBRATE == notificationMode) {
			int notificationMax = am
					.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
			int rv = notificationVolumn * notificationMax / 100;
			am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, rv,
					AudioManager.FLAG_ALLOW_RINGER_MODES);
			Log.d(TAG, "Set notificationVolumn to : " + rv);
		} else {
			// nochange , do nothing
		}

		if (ProfileBean.COMM_ON == wifi) {
			Utils.switchWifi(context, true);
		} else if (ProfileBean.COMM_OFF == wifi) {
			Utils.switchWifi(context, false);
		} else {
			// no change , do nothing.
		}

		if (ProfileBean.COMM_ON == bluetooth) {
			Utils.switchBluetooth(true);
		} else if (ProfileBean.COMM_OFF == bluetooth) {
			Utils.switchBluetooth(false);
		} else {
			// no change , do nothing.
		}

		if (ProfileBean.COMM_ON == gps) {
			Utils.switchGps(context, true);
		} else if (ProfileBean.COMM_OFF == gps) {
			Utils.switchGps(context, false);
		} else {
			// no change , do nothing.
		}

		Toast.makeText(context, pb.getProfileName(), Toast.LENGTH_SHORT).show();
		updateProfileNotification(context, pb);
	}

	public static void doAutoWifiProfile(Context context) {
		int pid = Utils.getCurrentProfileId(context);
		if (ProfileBean.PROFILE_AUTO_ID == pid) {
			MyApplication myApp = (MyApplication) context
					.getApplicationContext();
			ProfileDB pdb = myApp.getProfileDB();
			List<ProfileBean> pbList = pdb.selectAll();
			for (ProfileBean pb : pbList) {
				if (pb != null
						&& ProfileBean.TRIGGER_TYPE_WIFI == pb.getTriggerType()) {
					String triggeredWifi = pb.getTriggeredWifi();
					if (triggeredWifi != null && triggeredWifi.length() > 0) {
						WifiManager wifiManager = (WifiManager) context
								.getSystemService(Context.WIFI_SERVICE);
						WifiInfo wifiInfo = wifiManager.getConnectionInfo();
						String wifi = wifiInfo.getSSID();

						Gson gson = new Gson();
						Type type = new TypeToken<List<String>>() {
						}.getType();
						List<String> selectedList = gson.fromJson(
								triggeredWifi, type);
						for (String selectedWifi : selectedList) {
							if (selectedWifi.startsWith("\"")
									|| selectedWifi.endsWith("\"")) {
								selectedWifi = selectedWifi.substring(1,
										selectedWifi.length() - 1);
							}
							if (selectedWifi.equals(wifi)) {
								Utils.changeSettings(context, pb);
								break;
							}
						}
					}
					break;
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static void updateProfileNotification(Context context, ProfileBean pb) {
		Notification notification = new Notification();
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		Intent notificationIntent = new Intent(context,
				ProfileCatagoryActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notification.icon = pb.getProfileIcon();
		notification.when = System.currentTimeMillis();
		notification.setLatestEventInfo(context,
				context.getString(R.string.app_name), pb.getProfileName(),
				pendingIntent);
		nm.cancel(ProfileService.PROFILE_NOTIFICATION_ID);
		nm.notify(ProfileService.PROFILE_NOTIFICATION_ID, notification);
	}
}
