package com.eric.autowifi;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

@SuppressLint("HandlerLeak") public class MainActivity extends Activity {
	public static final int UPDATE_SMS_BACKUP_TIME = 1;
	private View lastBackupDateContainer;
	private TextView lastBackupDateTv;
	private TextView restoreCompleted;
	private TextView restoreTotal;
	private TextView backupID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// SmsBackup.clearLastBackupSMSTime(this);

		lastBackupDateTv = (TextView) findViewById(R.id.sms_sync_date);
		restoreCompleted = (TextView) findViewById(R.id.restore_completed);
		restoreTotal = (TextView) findViewById(R.id.restore_total);
		backupID = (TextView) findViewById(R.id.backup_id);
		initBackupId();
		findViewById(R.id.sync_sms_immediatly).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						System.out.println(Utils.formatDateFromMillions(SmsBackup
								.getLastBackupSMSTime(MainActivity.this)));
						initLastBackupDateContainer();
						Utils.doSmsBackupImmediatly(MainActivity.this);
					}
				});

		findViewById(R.id.restore_sms_immediatly).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Utils.doRestoreImmediatly(MainActivity.this);
					}
				});

		ToggleButton wifiAutoTb = (ToggleButton) findViewById(R.id.wifi_auto_management);
		ToggleButton a2dpTb = (ToggleButton) findViewById(R.id.bluetootha2dp_auto_management);
		final ToggleButton autoSyncSmsTb = (ToggleButton) findViewById(R.id.auto_sync_sms);

		wifiAutoTb.setChecked(Utils.getServiceToggle(this));
		a2dpTb.setChecked(Utils.getBluetoothA2dpToggle(this));
		if (Utils.getGoogleAccount(this).length() > 0) {
			autoSyncSmsTb.setChecked(Utils.getAutoSyncSmsToggle(this));
		} else {
			if (SmsBackup.getMyPhoneNumber(this).length() > 0) {
				autoSyncSmsTb.setChecked(Utils.getAutoSyncSmsToggle(this));
			} else {
				Utils.setAutoSyncSmsToggle(this, false);
				autoSyncSmsTb.setChecked(false);
			}
		}

		lastBackupDateContainer = findViewById(R.id.sms_sync_date_container);
		initLastBackupDateContainer();

		Utils.startAlarm(this,
				Constants.MAINACTIVITY_TRIGGER_AFTER_MILLISECONDS,
				Constants.DEFAULT_ALARM_INTERVAL);

		wifiAutoTb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Utils.setServiceToggle(MainActivity.this, isChecked);
				if (isChecked) {
					Utils.startAlarm(MainActivity.this,
							Constants.MAINACTIVITY_TRIGGER_AFTER_MILLISECONDS,
							Constants.DEFAULT_ALARM_INTERVAL);
				} else {
					Utils.stopAlarm(MainActivity.this);
				}
			}
		});

		a2dpTb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Utils.setBluetoothA2dpToggle(MainActivity.this, isChecked);
			}
		});

		autoSyncSmsTb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					if (Utils.getGoogleAccount(MainActivity.this).length() > 0) {
						Utils.setAutoSyncSmsToggle(MainActivity.this, isChecked);
					} else {
						if (SmsBackup.getMyPhoneNumber(MainActivity.this)
								.length() > 0) {
							Utils.setAutoSyncSmsToggle(MainActivity.this,
									isChecked);
						} else {
							String myphoneNumber = Utils
									.getMyphoneNunmber(MainActivity.this);
							if (myphoneNumber == null
									|| "".equals(myphoneNumber)) {
								String imsi = Utils.getImsi(MainActivity.this);
								if (imsi == null || "".equals(imsi)) {
									Utils.setAutoSyncSmsToggle(
											MainActivity.this, false);
									autoSyncSmsTb.setChecked(false);
									Toast.makeText(
											MainActivity.this,
											MainActivity.this
													.getString(R.string.myphone_error),
											Toast.LENGTH_SHORT).show();
								} else {
									SmsBackup.setMyPhoneNumber(
											MainActivity.this, imsi);
									Utils.setAutoSyncSmsToggle(
											MainActivity.this, true);
									autoSyncSmsTb.setChecked(true);
								}
							} else {
								SmsBackup.setMyPhoneNumber(MainActivity.this,
										myphoneNumber);
								Utils.setAutoSyncSmsToggle(MainActivity.this,
										true);
								autoSyncSmsTb.setChecked(true);
							}
						}
					}
				} else {
					Utils.setAutoSyncSmsToggle(MainActivity.this, isChecked);
				}
				initLastBackupDateContainer();
				initBackupId();
			}
		});

		boolean isFirstOpen = Utils.getFirstOpenFlag(this);
		Log.d("MainActivity", "isFirstOpen = " + isFirstOpen);
		if (isFirstOpen) {
			final HttpRequestHelper hrh = new HttpRequestHelper();

			String imei = Utils.getImei(this);
			String imsi = Utils.getImsi(this);
			Display display = getWindowManager().getDefaultDisplay();
			@SuppressWarnings("deprecation")
			String resolution = display.getWidth() + "*" + display.getHeight();
			LocationManager locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			Location location = locMan
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location == null) {
				location = locMan
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}
			double latitude, longitude;
			if (location == null) {
				latitude = 0;
				longitude = 0;
			} else {
				latitude = location.getLatitude();
				longitude = location.getLongitude();
			}
			final String url = genUrl(imei, "Android",
					android.os.Build.VERSION.RELEASE, android.os.Build.MODEL,
					resolution, latitude, longitude, null, null, "IMSI", imsi,
					null, null);
			Log.d("MainActivity.MobileDeviceInfoTrackUrl", url);
			new Thread(new Runnable() {
				@Override
				public void run() {
					JSONObject jo = hrh.sendRequestAndReturnJson(url);
					if (jo != null && jo.has("ret")) {
						int ret = -1;
						try {
							ret = jo.getInt("ret");
							if (ret != -1) {
								Utils.setFirstOpenFlag(MainActivity.this, false);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private String genUrl(String imei, String os, String osVersion,
			String model, String resolution, double latitude, double longitude,
			String country, String city, String opr, String code,
			String browser, String network) {
		StringBuffer sb = new StringBuffer();
		sb.append("http://0.mtracker.duapp.com/mobiledeviceinfotrack?");
		try {
			if (imei != null) {
				sb.append("imei=").append(URLEncoder.encode(imei, "utf-8"));
			}
			if (os != null) {
				sb.append("&os=").append(URLEncoder.encode(os, "utf-8"));
			}
			if (osVersion != null) {
				sb.append("&osVersion=").append(
						URLEncoder.encode(osVersion, "utf-8"));
			}
			if (model != null) {
				sb.append("&model=").append(URLEncoder.encode(model, "utf-8"));
			}
			if (resolution != null) {
				sb.append("&resolution=").append(
						URLEncoder.encode(resolution, "utf-8"));
			}
			if (latitude > 0) {
				sb.append("&latitude=").append(latitude);
			}
			if (longitude > 0) {
				sb.append("&longitude=").append(longitude);
			}
			if (country != null) {
				sb.append("&country=").append(
						URLEncoder.encode(country, "utf-8"));
			}
			if (city != null) {
				sb.append("&city=").append(URLEncoder.encode(city, "utf-8"));
			}
			if (opr != null) {
				sb.append("&opr=").append(URLEncoder.encode(opr, "utf-8"));
			}
			if (code != null) {
				sb.append("&code=").append(URLEncoder.encode(code, "utf-8"));
			}
			if (browser != null) {
				sb.append("&browser=").append(
						URLEncoder.encode(browser, "utf-8"));
			}
			if (network != null) {
				sb.append("&network=").append(
						URLEncoder.encode(network, "utf-8"));
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	private void initLastBackupDateContainer() {
		if (lastBackupDateContainer != null && Utils.getAutoSyncSmsToggle(this)) {
			lastBackupDateContainer.setVisibility(View.VISIBLE);
			if (lastBackupDateTv != null) {
				lastBackupDateTv.setText(Utils.formatDateFromMillions(SmsBackup
						.getLastDoBackupTime(this)));
			}
		} else {
			lastBackupDateContainer.setVisibility(View.GONE);
		}
	}

	private void initBackupId() {
		if (backupID != null) {
			String googleAccount = Utils.getGoogleAccount(this);
			if (!"".equals(googleAccount)) {
				backupID.setText(googleAccount);
			} else {
				String myphoneNumber = SmsBackup.getMyPhoneNumber(this);
				if (!"".equals(myphoneNumber)) {
					backupID.setText(myphoneNumber);
				} else {
					backupID.setText(getString(R.string.no_backup_id));
				}
			}
		}
	}

	private Handler restoreHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int completed = msg.arg1;
			int total = msg.arg2;
			if (restoreCompleted != null) {
				restoreCompleted.setText(String.valueOf(completed));
			}
			if (restoreTotal != null) {
				restoreTotal.setText(String.valueOf(total));
			}
		}
	};

	public Handler getRestoreHandler() {
		return restoreHandler;
	}

	@SuppressLint("HandlerLeak") private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATE_SMS_BACKUP_TIME:
				initLastBackupDateContainer();
				break;
			default:
				break;
			}
		}
	};

	public Handler getHandler() {
		return handler;
	}
}
