package com.eric.autowifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent)
	{
		// Utils.clearApiKey(context);
		Utils.startAlarm(context,
				Constants.BOOTCOMPLETE_TRIGGER_AFTER_MILLISECONDS,
				Constants.DEFAULT_ALARM_INTERVAL);
	}
}
