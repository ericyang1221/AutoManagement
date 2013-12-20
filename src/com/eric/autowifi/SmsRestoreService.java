package com.eric.autowifi;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.eric.autowifi.beans.SMSBean;
import com.eric.autowifi.beans.SMSJSONWrapper;
import com.google.gson.Gson;

public class SmsRestoreService extends Service {
	private MyBinder mBinder = new MyBinder();
	private final static int RESTORE_COUNT_PER_REQUEST = 20;
	private final static String ADDRESS = "address";
	private final static String DATE = "date";
	private final static String READ = "read";
	private final static String STATUS = "status";
	private final static String TYPE = "type";
	private final static String BODY = "body";
	private static boolean isRestoreRunning = false;
	private SmsRestoreListener smsRestoreListener;

	public interface SmsRestoreListener {
		public void onSmsAlreadyExist();

		public void onProgressChange(int completed, int total);

		public void onRestoreAlreadyRunning();

		public void onRestoreDone();
	}

	public void doRestore(final Context context) {
		long lastBackupSmsDate = getLastBackupSmsDate(context);
		long firstSmsDate = getFirstSmsDate(context);
		if (firstSmsDate < lastBackupSmsDate) {
			if (smsRestoreListener != null) {
				smsRestoreListener.onSmsAlreadyExist();
			}
			isRestoreRunning = false;
			if (smsRestoreListener != null) {
				smsRestoreListener.onRestoreDone();
			}
			this.stopSelf();
			return;
		}

		long count = getTotalSmsCount(context);
		List<SMSBean> sbList = new ArrayList<SMSBean>();
		long limitX = 0;
		if (count > 0) {
			int times = (int) (count / RESTORE_COUNT_PER_REQUEST) + 1;
			double each = (double) 50 / times;
			for (int i = 0; i < times; i++) {
				SMSJSONWrapper wrapper = downloadSms(context, limitX, 0);
				limitX = Long.valueOf(wrapper.getImei());
				sbList.addAll(wrapper.getSbList());
				System.out.println(sbList.size());
				if (smsRestoreListener != null) {
					smsRestoreListener.onProgressChange((int) (each * (i + 1)),
							100);
				}
			}
			System.out.println("TOTAL SIZE = " + sbList.size());
		} else {
			// no backup sms
			isRestoreRunning = false;
			this.stopSelf();
			if (smsRestoreListener != null) {
				smsRestoreListener.onRestoreDone();
			}
			return;
		}
		if (sbList.isEmpty()) {
			// no backup sms
			isRestoreRunning = false;
			this.stopSelf();
			if (smsRestoreListener != null) {
				smsRestoreListener.onRestoreDone();
			}
			return;
		} else {
			List<Uri> uriList = new ArrayList<Uri>();
			int sbSize = sbList.size();
			double each = (double) 50 / sbSize;
			for (int i = 0; i < sbSize; i++) {
				try {
					uriList.add(insertSMS(context, sbList.get(i)));
					if (smsRestoreListener != null) {
						smsRestoreListener.onProgressChange(
								(int) (each * (i + 1)) + 50, 100);
					}
				} catch (Exception e) {
					int totalRollBack = uriList.size();
					double rollbackEach = (double) 100 / totalRollBack;
					for (int j = 0; j < totalRollBack; j++) {
						context.getContentResolver().delete(uriList.get(j),
								null, null);
						if (smsRestoreListener != null) {
							smsRestoreListener.onProgressChange(
									(int) (100 - rollbackEach * (j + 1)), 100);
						}
					}
					break;
				}
			}
		}
		isRestoreRunning = false;
		this.stopSelf();
		if (smsRestoreListener != null) {
			smsRestoreListener.onRestoreDone();
		}
	}

	public boolean isRestoreRunning() {
		return isRestoreRunning;
	}

	private SMSJSONWrapper downloadSms(Context context, long limitX, long from) {
		HttpRequestHelper hrh = new HttpRequestHelper();
		String url = "http://0.locationtracker.duapp.com/downloadSmses";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String typeValue;
		if (!"".equals(typeValue = Utils.getGoogleAccount(context))) {
			params.add(new BasicNameValuePair("type",
					SmsBackup.SYNC_TYPE_GOOGLEACCOUNT));
		} else if (!"".equals(typeValue = SmsBackup.getMyPhoneNumber(context))) {
			params.add(new BasicNameValuePair("type",
					SmsBackup.SYNC_TYPE_MYPHONENUMBER));
		} else {
			return null;
		}
		params.add(new BasicNameValuePair("typeValue", typeValue));
		params.add(new BasicNameValuePair("from", String.valueOf(from)));
		params.add(new BasicNameValuePair("limitX", String.valueOf(limitX)));
		params.add(new BasicNameValuePair("limitY", String
				.valueOf(RESTORE_COUNT_PER_REQUEST)));
		JSONObject jo = hrh.sendPostRequestAndReturnJson(url, params);
		if (jo != null && jo.has("ret")) {
			// ret error.
			return null;
		} else if (jo != null) {
			Gson g = new Gson();
			try {
				SMSJSONWrapper cw = g.fromJson(jo.toString(),
						SMSJSONWrapper.class);
				return cw;
			} catch (Exception e) {
				// json format error.
				return null;
			}
		} else {
			// network error.
			return null;
		}
	}

	private static long getTotalSmsCount(Context context) {
		long error = -1;
		HttpRequestHelper hrh = new HttpRequestHelper();
		String url = "http://0.locationtracker.duapp.com/getSmsCount";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String typeValue;
		if (!"".equals(typeValue = Utils.getGoogleAccount(context))) {
			params.add(new BasicNameValuePair("type",
					SmsBackup.SYNC_TYPE_GOOGLEACCOUNT));
		} else if (!"".equals(typeValue = SmsBackup.getMyPhoneNumber(context))) {
			params.add(new BasicNameValuePair("type",
					SmsBackup.SYNC_TYPE_MYPHONENUMBER));
		} else {
			return error;
		}
		params.add(new BasicNameValuePair("typeValue", typeValue));
		JSONObject jo = hrh.sendPostRequestAndReturnJson(url, params);
		if (jo != null && jo.has("ret")) {
			long ret = -1;
			try {
				ret = jo.getLong("ret");
				if (ret > -1) {
					return ret;
				} else {
					return error;
				}
			} catch (JSONException e) {
				e.printStackTrace();
				return error;
			}
		} else {
			return error;
		}
	}

	private long getLastBackupSmsDate(Context context) {
		long error = -1;
		HttpRequestHelper hrh = new HttpRequestHelper();
		String url = "http://0.locationtracker.duapp.com/getLastBackupSmsDate";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String typeValue;
		if (!"".equals(typeValue = Utils.getGoogleAccount(context))) {
			params.add(new BasicNameValuePair("type",
					SmsBackup.SYNC_TYPE_GOOGLEACCOUNT));
		} else if (!"".equals(typeValue = SmsBackup.getMyPhoneNumber(context))) {
			params.add(new BasicNameValuePair("type",
					SmsBackup.SYNC_TYPE_MYPHONENUMBER));
		} else {
			return error;
		}
		params.add(new BasicNameValuePair("typeValue", typeValue));
		JSONObject jo = hrh.sendPostRequestAndReturnJson(url, params);
		if (jo != null && jo.has("ret")) {
			long ret = -1;
			try {
				ret = jo.getLong("ret");
				if (ret > -1) {
					return ret;
				} else {
					return error;
				}
			} catch (JSONException e) {
				e.printStackTrace();
				return error;
			}
		} else {
			return error;
		}
	}

	private long getFirstSmsDate(Context context) {
		long ret;
		ContentResolver cr = context.getContentResolver();
		String[] projection = new String[] { "date" };
		Uri uri = Uri.parse(SmsBackup.SMS_URI_ALL);
		Cursor cur = cr.query(uri, projection, null, null, "date asc");
		int dateColumn = cur.getColumnIndex("date");
		if (cur.moveToFirst()) {
			ret = cur.getLong(dateColumn);
			;
		} else {
			ret = Long.MAX_VALUE;
		}
		if (cur != null && !cur.isClosed()) {
			cur.close();
			cur = null;
		}
		return ret;
	}

	private Uri insertSMS(Context context, SMSBean sb) {
		ContentValues values = new ContentValues();
		/* 手机号 */
		values.put(ADDRESS, sb.getPhoneNumber());
		/* 时间 */
		values.put(DATE, sb.getDate());
		// read 是否阅读 0未读， 1已读
		values.put(READ, 1);
		// status 状态 -1接收，0 complete, 64 pending, 128 failed
		values.put(STATUS, -1);
		/* 类型1为收件箱，2为发件箱 */
		values.put(TYPE, sb.getType());
		/* 短信体内容 */
		values.put(BODY, sb.getSmsbody());
		/* 插入数据库操作 */
		return context.getContentResolver().insert(Uri.parse("content://sms"),
				values);
	}

	public void setSmsRestoreListener(SmsRestoreListener smsRestoreListener) {
		this.smsRestoreListener = smsRestoreListener;
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d("SmsRestoreService", "onBind");
		return mBinder;
	}

	@Override
	public void onCreate() {
		Log.d("SmsRestoreService", "onCreate");
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		Log.d("SmsRestoreService", "onDestroy");
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("SmsRestoreService", "onStartCommand");
		if (!isRestoreRunning) {
			isRestoreRunning = true;
			new Thread(new Runnable() {
				@Override
				public void run() {
					doRestore(SmsRestoreService.this);
				}
			}).start();
		} else {
			if (smsRestoreListener != null) {
				smsRestoreListener.onRestoreAlreadyRunning();
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d("SmsRestoreService", "onUnbind");
		smsRestoreListener = null;
		return super.onUnbind(intent);
	}

	public class MyBinder extends Binder {
		SmsRestoreService getService() {
			return SmsRestoreService.this;
		}
	}
}
