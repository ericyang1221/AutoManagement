package com.eric.autowifi;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.eric.autowifi.beans.SMSBean;
import com.eric.autowifi.beans.SMSJSONWrapper;
import com.eric.autowifi.utils.ServerConfig;
import com.google.gson.Gson;

public class SmsBackup {
	private final static int BACKUP_COUNT_PER_REQUEST = 20;
	public static final String SYNC_TYPE_GOOGLEACCOUNT = "SYNC_TYPE_GOOGLEACCOUNT";
	public static final String SYNC_TYPE_MYPHONENUMBER = "SYNC_TYPE_MYPHONENUMBER";
	public static final String SYNC_TYPE_IMEI = "SYNC_TYPE_IMEI";
	public final static String SMS_URI_ALL = "content://sms/";
	// private final static String SMS_URI_INBOX = "content://sms/inbox";
	// private final static String SMS_URI_SEND = "content://sms/sent";
	// private final static String SMS_URI_DRAFT = "content://sms/draft";
	private static final String SMS_BACKUP = "sms_backup";
	private static final String LAST_BACKUP_TIME = "last_backup_time";
	private static final String LAST_BACKUP_SMS_TIME = "last_backup_sms_time";
	private static final String MY_PHONE_NUMBER = "MY_PHONE_NUMBER";
	private static SharedPreferences smsBackupSP;

	private static SharedPreferences getSharedPreferences(Context context)
	{
		if (smsBackupSP == null)
		{
			smsBackupSP = context.getSharedPreferences(SMS_BACKUP,
					Context.MODE_PRIVATE);
		}
		return smsBackupSP;
	}

	public static long getLastDoBackupTime(Context context)
	{
		return getSharedPreferences(context).getLong(LAST_BACKUP_TIME, 0);
	}

	public static long getLastBackupSMSTime(Context context)
	{
		return getSharedPreferences(context).getLong(LAST_BACKUP_SMS_TIME, 0);
	}

	public static boolean setLastDoBackupTime(Context context, long time)
	{
		Editor e = getSharedPreferences(context).edit();
		e.putLong(LAST_BACKUP_TIME, time);
		return e.commit();
	}

	public static boolean setLastBackupSMSTime(Context context, long time)
	{
		Editor e = getSharedPreferences(context).edit();
		e.putLong(LAST_BACKUP_SMS_TIME, time);
		return e.commit();
	}

	public static boolean setMyPhoneNumber(Context context, String myPhoneNumber)
	{
		Editor e = getSharedPreferences(context).edit();
		e.putString(MY_PHONE_NUMBER, myPhoneNumber);
		return e.commit();
	}

	public static boolean clearMyPhoneNumber(Context context)
	{
		Editor e = getSharedPreferences(context).edit();
		e.putString(MY_PHONE_NUMBER, "");
		return e.commit();
	}

	public static String getMyPhoneNumber(Context context)
	{
		return getSharedPreferences(context).getString(MY_PHONE_NUMBER, "");
	}

	public static boolean clearLastBackupSMSTime(Context context)
	{
		Editor e = getSharedPreferences(context).edit();
		e.putLong(LAST_BACKUP_SMS_TIME, 0);
		return e.commit();
	}

	public static int getSmsCountFrom(Context context, long from)
	{
		ContentResolver cr = context.getContentResolver();
		Uri uri = Uri.parse(SMS_URI_ALL);
		Cursor cur = cr.query(uri, null, "date > ?",
				new String[] { String.valueOf(from) }, "date desc");
		return cur.getCount();
	}

	public static void doBackup(final Context context, String type,
			String typeValue)
	{
		String lastSmsbody = null;
		int count = 0;
		List<SMSBean> sbList = new ArrayList<SMSBean>();
		long lastBackupSMSTime = getLastBackupSMSTime(context);
		ContentResolver cr = context.getContentResolver();
		String[] projection = new String[] { "_id", "address", "person",
				"body", "date", "type" };
		Uri uri = Uri.parse(SMS_URI_ALL);
		Cursor cur = cr.query(uri, projection, "date > ?",
				new String[] { String.valueOf(lastBackupSMSTime) }, "date asc");
		int nameColumn = cur.getColumnIndex("person");
		int phoneNumberColumn = cur.getColumnIndex("address");
		int smsbodyColumn = cur.getColumnIndex("body");
		int dateColumn = cur.getColumnIndex("date");
		int typeColumn = cur.getColumnIndex("type");
		if (cur.moveToFirst())
		{
			String name;
			String phoneNumber;
			String smsbody;
			long date;
			int typeId;
			do
			{
				name = cur.getString(nameColumn);
				phoneNumber = cur.getString(phoneNumberColumn);
				smsbody = cur.getString(smsbodyColumn);
				date = cur.getLong(dateColumn);
				typeId = cur.getInt(typeColumn);
				if (smsbody.equals(lastSmsbody))
				{
					lastSmsbody = smsbody;
					continue;
				}
				lastSmsbody = smsbody;
				SMSBean sb = new SMSBean(name, phoneNumber, smsbody, date,
						typeId);
				sbList.add(sb);
				count++;
				if (count == BACKUP_COUNT_PER_REQUEST)
				{
					if (Utils.getAutoSyncSmsToggle(context)
							&& Utils.isWifiConnected(context))
					{
						if (sendBackupRequest(context, sbList, type, typeValue))
						{
							setLastBackupSMSTime(context, date);
							setLastDoBackupTime(context,
									System.currentTimeMillis());
							sbList.clear();
							count = 0;
						}
						else
						{
							if (cur != null && !cur.isClosed())
							{
								cur.close();
								cur = null;
							}
							return;
						}
					}
					else
					{
						if (cur != null && !cur.isClosed())
						{
							cur.close();
							cur = null;
						}
						return;
					}
				}
			} while (cur.moveToNext());
			if (!sbList.isEmpty())
			{
				if (sendBackupRequest(context, sbList, type, typeValue))
				{
					setLastBackupSMSTime(context, date);
					setLastDoBackupTime(context, System.currentTimeMillis());
				}
				else
				{
					Log.e("SmsBackup.doBackup", "The last 50 sms backup error!");
				}
			}
		}
		else
		{
			Log.d("SmsBackup.doBackup",
					"No need to backup,it is already newest.");
			if (context instanceof Activity) {
				((Activity) context).runOnUiThread(new Runnable() {
					@Override
					public void run()
					{
						Toast.makeText(context,
								context.getString(R.string.already_the_newest),
								Toast.LENGTH_SHORT).show();
					}
				});
			}
			SmsBackup.setLastDoBackupTime(context, System.currentTimeMillis());
		}
		if (cur != null && !cur.isClosed())
		{
			cur.close();
			cur = null;
		}
	}

	private static boolean sendBackupRequest(Context context,
			List<SMSBean> sbList, String syncType, String typeValue)
	{
		boolean isSuccess = false;
		String json = getSMSInJSON(context, sbList, syncType, typeValue);
		System.out.println(json);
		HttpRequestHelper hrh = new HttpRequestHelper();
		String url = ServerConfig.UPLOAD_SMS_URL;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("smsJson", json));
		JSONObject jo = hrh.sendPostRequestAndReturnJson(url, params);
		if (jo != null && jo.has("ret"))
		{
			int ret = -1;
			try
			{
				ret = jo.getInt("ret");
				if (ret > -1)
				{
					isSuccess = true;
				}
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		return isSuccess;
	}

	private static String getSMSInJSON(Context context, List<SMSBean> sbList,
			String syncType, String typeValue)
	{
		if (sbList != null)
		{
			SMSJSONWrapper cw = new SMSJSONWrapper(Utils.getImei(context),
					syncType, typeValue, sbList);
			Gson g = new Gson();
			return g.toJson(cw);
		}
		else
		{
			return null;
		}
	}
}
