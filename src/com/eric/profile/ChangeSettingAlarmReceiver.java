package com.eric.profile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.eric.autowifi.Utils;
import com.eric.profile.beans.ProfileBean;

public class ChangeSettingAlarmReceiver extends BroadcastReceiver {
	private final String TAG = "ChangeSettingAlarmReceiver";

	@Override
	public void onReceive(final Context context, Intent intent) {
		Log.d(TAG, "onReceive.changeSettings");
		ProfileBean pb = (ProfileBean) intent.getSerializableExtra("pb");
		Utils.changeSettings(context, pb);
	}
}
