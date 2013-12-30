package com.eric.autowifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ApplicationInstallUninstallReceiver extends BroadcastReceiver {
	private final String TAG = "ApplicationInstallUninstallReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		if ("android.intent.action.PACKAGE_REMOVED".equals(intent.getAction())) {
			// 判断是否是替代原软件
			final boolean replacing = intent.getBooleanExtra(
					Intent.EXTRA_REPLACING, false);
			if (!replacing) {// 安装新软件
				final int uid = intent.getIntExtra(Intent.EXTRA_UID, -123);
				Log.d(TAG, "New install , uid is " + uid);
			} else {// 更新软件
				final int uid = intent.getIntExtra(Intent.EXTRA_UID, -123);
				Utils.setLastSync(context, 0);
				Log.d(TAG, "Replace app , true uid is " + uid);
			}
		} else if ("android.intent.action.PACKAGE_ADDED".equals(intent
				.getAction())) {
			final int uid = intent.getIntExtra(Intent.EXTRA_UID, -123);
			Log.d(TAG, "PACKAGE_ADDED , uid is " + uid);
		}
	}
}
