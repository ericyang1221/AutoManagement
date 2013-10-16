package com.eric.autowifi;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;

import com.google.gson.Gson;

public class SmsRestore {
	private final static int RESTORE_COUNT_PER_REQUEST = 20;
	private final static String ADDRESS = "address";
	private final static String DATE = "date";
	private final static String READ = "read";
	private final static String STATUS = "status";
	private final static String TYPE = "type";
	private final static String BODY = "body";
	private static boolean isRestoreRunning = false;

	public static void doRestore(final Context context)
	{
		if (!isRestoreRunning)
		{
			isRestoreRunning = true;
			long lastBackupSmsDate = getLastBackupSmsDate(context);
			long firstSmsDate = getFirstSmsDate(context);
			if (firstSmsDate < lastBackupSmsDate)
			{
				((Activity) context).runOnUiThread(new Runnable() {
					@Override
					public void run()
					{
						Toast.makeText(context,
								R.string.clear_your_local_sms_first,
								Toast.LENGTH_LONG).show();
					}
				});
				isRestoreRunning = false;
				return;
			}
			long count = getTotalSmsCount(context);
			List<SMSBean> sbList = new ArrayList<SMSBean>();
			long limitX = 0;
			MainActivity ma = (MainActivity) context;
			Handler restoreHandler = ma.getRestoreHandler();
			if (count > 0)
			{
				int times = (int) (count / RESTORE_COUNT_PER_REQUEST) + 1;
				double each = (double) 50 / times;
				for (int i = 0; i < times; i++)
				{
					SMSJSONWrapper wrapper = downloadSms(context, limitX, 0);
					limitX = Long.valueOf(wrapper.getImei());
					sbList.addAll(wrapper.getSbList());
					System.out.println(sbList.size());
					restoreHandler
							.obtainMessage(0, (int) (each * (i + 1)), 100)
							.sendToTarget();
				}
				System.out.println("TOTAL SIZE = " + sbList.size());
			}
			else
			{
				// no backup sms
				isRestoreRunning = false;
				return;
			}
			if (sbList.isEmpty())
			{
				// no backup sms
				isRestoreRunning = false;
				return;
			}
			else
			{
				List<Uri> uriList = new ArrayList<Uri>();
				int sbSize = sbList.size();
				double each = (double) 50 / sbSize;
				for (int i = 0; i < sbSize; i++)
				{
					try
					{
						uriList.add(insertSMS(context, sbList.get(i)));
						restoreHandler.obtainMessage(0,
								(int) (each * (i + 1)) + 50, 100)
								.sendToTarget();
					} catch (Exception e)
					{
						int totalRollBack = uriList.size();
						double rollbackEach = (double) 100 / totalRollBack;
						for (int j = 0; j < totalRollBack; j++)
						{
							context.getContentResolver().delete(uriList.get(j),
									null, null);
							restoreHandler.obtainMessage(0,
									(int) (100 - rollbackEach * (j + 1)), 100)
									.sendToTarget();
						}
						break;
					}
				}
			}
			isRestoreRunning = false;
		}
		else
		{
			((Activity) context).runOnUiThread(new Runnable() {
				@Override
				public void run()
				{
					Toast.makeText(context, R.string.restore_already_running,
							Toast.LENGTH_LONG).show();
				}
			});
		}
	}

	public static boolean isRestoreRunning()
	{
		return isRestoreRunning;
	}

	private static SMSJSONWrapper downloadSms(Context context, long limitX,
			long from)
	{
		HttpRequestHelper hrh = new HttpRequestHelper();
		String url = "http://0.locationtracker.duapp.com/downloadSmses";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String typeValue;
		if (!"".equals(typeValue = Utils.getGoogleAccount(context)))
		{
			params.add(new BasicNameValuePair("type",
					SmsBackup.SYNC_TYPE_GOOGLEACCOUNT));
		}
		else if (!"".equals(typeValue = SmsBackup.getMyPhoneNumber(context)))
		{
			params.add(new BasicNameValuePair("type",
					SmsBackup.SYNC_TYPE_MYPHONENUMBER));
		}
		else
		{
			return null;
		}
		params.add(new BasicNameValuePair("typeValue", typeValue));
		params.add(new BasicNameValuePair("from", String.valueOf(from)));
		params.add(new BasicNameValuePair("limitX", String.valueOf(limitX)));
		params.add(new BasicNameValuePair("limitY", String
				.valueOf(RESTORE_COUNT_PER_REQUEST)));
		JSONObject jo = hrh.sendPostRequestAndReturnJson(url, params);
		if (jo != null && jo.has("ret"))
		{
			// ret error.
			return null;
		}
		else if (jo != null)
		{
			Gson g = new Gson();
			try
			{
				SMSJSONWrapper cw = g.fromJson(jo.toString(),
						SMSJSONWrapper.class);
				return cw;
			} catch (Exception e)
			{
				// json format error.
				return null;
			}
		}
		else
		{
			// network error.
			return null;
		}
	}

	private static long getTotalSmsCount(Context context)
	{
		long error = -1;
		HttpRequestHelper hrh = new HttpRequestHelper();
		String url = "http://0.locationtracker.duapp.com/getSmsCount";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String typeValue;
		if (!"".equals(typeValue = Utils.getGoogleAccount(context)))
		{
			params.add(new BasicNameValuePair("type",
					SmsBackup.SYNC_TYPE_GOOGLEACCOUNT));
		}
		else if (!"".equals(typeValue = SmsBackup.getMyPhoneNumber(context)))
		{
			params.add(new BasicNameValuePair("type",
					SmsBackup.SYNC_TYPE_MYPHONENUMBER));
		}
		else
		{
			return error;
		}
		params.add(new BasicNameValuePair("typeValue", typeValue));
		JSONObject jo = hrh.sendPostRequestAndReturnJson(url, params);
		if (jo != null && jo.has("ret"))
		{
			long ret = -1;
			try
			{
				ret = jo.getLong("ret");
				if (ret > -1)
				{
					return ret;
				}
				else
				{
					return error;
				}
			} catch (JSONException e)
			{
				e.printStackTrace();
				return error;
			}
		}
		else
		{
			return error;
		}
	}

	private static long getLastBackupSmsDate(Context context)
	{
		long error = -1;
		HttpRequestHelper hrh = new HttpRequestHelper();
		String url = "http://0.locationtracker.duapp.com/getLastBackupSmsDate";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String typeValue;
		if (!"".equals(typeValue = Utils.getGoogleAccount(context)))
		{
			params.add(new BasicNameValuePair("type",
					SmsBackup.SYNC_TYPE_GOOGLEACCOUNT));
		}
		else if (!"".equals(typeValue = SmsBackup.getMyPhoneNumber(context)))
		{
			params.add(new BasicNameValuePair("type",
					SmsBackup.SYNC_TYPE_MYPHONENUMBER));
		}
		else
		{
			return error;
		}
		params.add(new BasicNameValuePair("typeValue", typeValue));
		JSONObject jo = hrh.sendPostRequestAndReturnJson(url, params);
		if (jo != null && jo.has("ret"))
		{
			long ret = -1;
			try
			{
				ret = jo.getLong("ret");
				if (ret > -1)
				{
					return ret;
				}
				else
				{
					return error;
				}
			} catch (JSONException e)
			{
				e.printStackTrace();
				return error;
			}
		}
		else
		{
			return error;
		}
	}

	private static long getFirstSmsDate(Context context)
	{
		long ret;
		ContentResolver cr = context.getContentResolver();
		String[] projection = new String[] { "date" };
		Uri uri = Uri.parse(SmsBackup.SMS_URI_ALL);
		Cursor cur = cr.query(uri, projection, null, null, "date asc");
		int dateColumn = cur.getColumnIndex("date");
		if (cur.moveToFirst())
		{
			ret = cur.getLong(dateColumn);
			;
		}
		else
		{
			ret = Long.MAX_VALUE;
		}
		if (cur != null && !cur.isClosed())
		{
			cur.close();
			cur = null;
		}
		return ret;
	}

	private static Uri insertSMS(Context context, SMSBean sb)
	{
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
}
