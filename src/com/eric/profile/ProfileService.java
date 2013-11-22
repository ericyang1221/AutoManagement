package com.eric.profile;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.eric.autowifi.MyApplication;
import com.eric.autowifi.R;
import com.eric.autowifi.Utils;
import com.eric.profile.db.ProfileBean;
import com.eric.profile.db.ProfileDB;

public class ProfileService extends Service {
	private final String TAG = "ProfileService";
	private MyBinder mBinder = new MyBinder();
	private ProfileBean pb;
	private Notification notification;
	private PendingIntent pendingIntent;
	private NotificationManager nm;

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind");
		return mBinder;
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		int currentId = Utils.getCurrentProfileId(this);
		ProfileDB pdb = ((MyApplication) this.getApplication()).getProfileDB();
		pb = pdb.selectProfileById(currentId);
		if (pb == null) {
			Log.d(TAG, "current profile id:" + currentId);
		} else {
			Log.d(TAG, "current profile:" + pb.getProfileName());
		}
		nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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
		notification = new Notification(pb.getProfileIcon(),
				pb.getProfileName(), System.currentTimeMillis());
		Intent notificationIntent = new Intent(this,
				ProfileCatagoryActivity.class);
		pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
				0);
		notification.setLatestEventInfo(this, getString(R.string.app_name),
				pb.getProfileName(), pendingIntent);
		this.startForeground(1, notification);
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
		notification.icon = pb.getProfileIcon();
		notification.setLatestEventInfo(this, getString(R.string.app_name),
				pb.getProfileName(), pendingIntent);
		nm.notify(0, notification);
	}
}
