package com.eric.autowifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PowerConnectedReceiver extends BroadcastReceiver {
	public void onReceive(Context context, Intent intent)
	{
		if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED))
		{
			Log.d("DEBUG", "Power connected...");
			Utils.doAutoSmsBackup(context);
		}
	}
}