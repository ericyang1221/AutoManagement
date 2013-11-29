package com.eric.profile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.eric.autowifi.MyApplication;
import com.eric.autowifi.Utils;
import com.eric.profile.db.ProfileBean;
import com.eric.profile.db.ProfileDB;

public class ProfileService extends Service {
	private final String TAG = "ProfileService";
	public static final int PROFILE_NOTIFICATION_ID = 1986;
	private final int WEEKLY_INTERVAL = 7 * 24 * 60 * 60 * 1000;
	private MyBinder mBinder = new MyBinder();
	private ProfileBean pb;
	private int requestCode = 0;
	private ProfileDB pdb;

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind");
		return mBinder;
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		requestCode = 0;
		int currentId = Utils.getCurrentProfileId(this);
		pdb = ((MyApplication) this.getApplication()).getProfileDB();
		pb = pdb.selectProfileById(currentId);
		if (pb == null) {
			Log.d(TAG, "current profile id:" + currentId);
		} else {
			Log.d(TAG, "current profile:" + pb.getProfileName());
		}
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		this.stopForeground(true);
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");

		if (pb != null) {
			if (pb.isAuto()) {
				doAuto();
			} else {
				clearAlarms();
				Utils.changeSettings(this, pb);
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "onUnbind");
		return super.onUnbind(intent);
	}

	public class MyBinder extends Binder {
		ProfileService getService() {
			return ProfileService.this;
		}
	}

	public void changeProfile(ProfileBean pb) {
		this.pb = pb;
		Utils.setCurrentProfileId(this, pb.getId());
		Log.d(TAG, "current profile:" + pb.getProfileName());
		if (pb.isAuto()) {
			doAuto();
		} else {
			clearAlarms();
			Utils.changeSettings(this, pb);
		}
	}

	private void doAuto() {
		Toast.makeText(this, pb.getProfileName(), Toast.LENGTH_SHORT).show();
		Utils.doAutoWifiProfile(this);
		doAutoTimerProfile();
	}

	private void doAutoTimerProfile() {
		clearAlarms();
		List<ProfileBean> pbList = pdb.selectAll();
		for (ProfileBean _pb : pbList) {
			if (ProfileBean.TRIGGER_TYPE_MANUAL_OR_TIME == _pb.getTriggerType()) {
				String td1 = _pb.getTriggerDate1();
				String td2 = _pb.getTriggerDate2();
				String td3 = _pb.getTriggerDate3();
				String td4 = _pb.getTriggerDate4();
				if (td1 != null && td1.length() > 0) {
					startAlarm(td1, _pb);
				}
				if (td2 != null && td2.length() > 0) {
					startAlarm(td2, _pb);
				}
				if (td3 != null && td3.length() > 0) {
					startAlarm(td3, _pb);
				}
				if (td4 != null && td4.length() > 0) {
					startAlarm(td4, _pb);
				}
			}
		}
	}

	private void startAlarm(String triggerDate, ProfileBean _pb) {
		String[] dateAndTime = triggerDate.split(TimeTriggerDialog.DTSP);
		if (dateAndTime != null && dateAndTime.length == 2) {
			String[] day = dateAndTime[0].split(TimeTriggerDialog.DSP);
			String[] time = dateAndTime[1].split(TimeTriggerDialog.TSP);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			int today = calendar.get(Calendar.DAY_OF_WEEK) - 1;
			int dom = calendar.get(Calendar.DAY_OF_MONTH);
			if (today < 1) {
				today = 7;
			}
			int hh = calendar.get(Calendar.HOUR_OF_DAY);
			int mi = calendar.get(Calendar.MINUTE);
			List<Integer> triDays = new ArrayList<Integer>();
			for (String d : day) {
				int triDay = -1;
				if (TimeTriggerDialog.MON.equals(d)) {
					triDay = 1;
				} else if (TimeTriggerDialog.TUE.equals(d)) {
					triDay = 2;
				} else if (TimeTriggerDialog.WED.equals(d)) {
					triDay = 3;
				} else if (TimeTriggerDialog.THU.equals(d)) {
					triDay = 4;
				} else if (TimeTriggerDialog.FRI.equals(d)) {
					triDay = 5;
				} else if (TimeTriggerDialog.SAT.equals(d)) {
					triDay = 6;
				} else if (TimeTriggerDialog.SUN.equals(d)) {
					triDay = 7;
				}
				if (triDay != -1) {
					triDays.add(triDay);
				}
			}
			int triHh = 0;
			int triMi = 0;
			if (time != null && time.length == 2) {
				try {
					triHh = Integer.valueOf(time[0]);
					triMi = Integer.valueOf(time[1]);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			for (int i = 0; i < triDays.size(); i++) {
				int td = triDays.get(i);
				if (td < today) {
					int d = today - td + 1;
					calendar.set(Calendar.DAY_OF_MONTH, dom + d);
					calendar.set(Calendar.HOUR, triHh);
					calendar.set(Calendar.MINUTE, triMi);
				} else if (td == today) {
					if (triHh < hh) {
						calendar.set(Calendar.DAY_OF_MONTH, dom + 7);
						calendar.set(Calendar.HOUR, triHh);
						calendar.set(Calendar.MINUTE, triMi);
					} else if (triHh == hh) {
						if (triMi < mi) {
							calendar.set(Calendar.DAY_OF_MONTH, dom + 7);
							calendar.set(Calendar.HOUR, triHh);
							calendar.set(Calendar.MINUTE, triMi);
						} else if (triMi == mi) {
							// noting to do.trigger now.
						} else {
							calendar.set(Calendar.MINUTE, triMi);
						}
					} else {
						calendar.set(Calendar.HOUR, triHh);
						calendar.set(Calendar.MINUTE, triMi);
					}
				} else {
					calendar.set(Calendar.DAY_OF_MONTH, td);
					calendar.set(Calendar.HOUR, triHh);
					calendar.set(Calendar.MINUTE, triMi);
				}
				AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
				alarm.setRepeating(AlarmManager.RTC_WAKEUP,
						calendar.getTimeInMillis(), WEEKLY_INTERVAL,
						getAlarmBroadCastSender(_pb));
				Log.d(TAG, "startProfileAlarm:" + _pb.getProfileName()
						+ " after:" + calendar.getTimeInMillis());
			}
		}
	}

	private PendingIntent getAlarmBroadCastSender(ProfileBean _pb) {
		Intent i = new Intent(this, ChangeSettingAlarmReceiver.class);
		i.putExtra("pb", _pb);
		PendingIntent sender = PendingIntent.getBroadcast(this, requestCode++,
				i, 0);
		return sender;
	}

	private void clearAlarms() {
		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(this, ChangeSettingAlarmReceiver.class);
		for (int i = 0; i < requestCode; i++) {
			PendingIntent sender = PendingIntent.getBroadcast(this,
					requestCode, intent, 0);
			alarm.cancel(sender);
		}
		requestCode = 0;
	}
}
