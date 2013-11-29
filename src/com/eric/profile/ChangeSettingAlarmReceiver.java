package com.eric.profile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.eric.autowifi.Utils;
import com.eric.profile.db.ProfileBean;

public class ChangeSettingAlarmReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(final Context context, Intent intent) {
		ProfileBean pb = (ProfileBean) intent.getSerializableExtra("pb");
		Utils.changeSettings(context, pb);
	}
}
