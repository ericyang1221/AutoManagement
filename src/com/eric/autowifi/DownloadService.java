package com.eric.autowifi;

import java.io.File;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

public class DownloadService extends Service {
	private Context context;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		context = this.getApplicationContext();
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final UpdateBean ub = UpdateManager.checkUpdate(context);
				if (ub != null) {
					Utils.setLastCheckUpdateTime(context,
							System.currentTimeMillis());
					if (ub.isNeedUpdate()) {
						HttpRequestHelper hrh = new HttpRequestHelper();
						String subPath = Utils.getAppFolder(context)
								+ "/downloads/";
						String fileName = Utils.getAppName(context) + "v"
								+ ub.getServerVersion() + ".apk";
						File resultFile = hrh.downFile(ub.getUpdateUrl(),
								subPath, fileName);
						if (resultFile != null) {
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setDataAndType(Uri.fromFile(resultFile),
									"application/vnd.android.package-archive");
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
							Log.d("DownloadService.onStartCommand",
									"Update successfully.");
						} else {
							Log.d("DownloadService.onStartCommand",
									"Update failed.");
						}
					}
				} else {
					Log.d("DownloadService.onStartCommand",
							"No need to update.");
				}
				stopSelf();
			}
		}).start();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
}