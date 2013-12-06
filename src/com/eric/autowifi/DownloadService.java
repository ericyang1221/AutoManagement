package com.eric.autowifi;

import java.io.File;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;

public class DownloadService extends Service {
	private final String TAG = "DownloadService";
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
		final Handler mHandler = new Handler();
		new Thread(new Runnable() {
			@Override
			public void run() {
				final UpdateBean ub = UpdateManager.checkUpdate(context);
				if (ub != null) {
					Utils.setLastCheckUpdateTime(context,
							System.currentTimeMillis());
					Log.d(TAG, "isNeedUpdate:" + ub.isNeedUpdate());
					if (ub.isNeedUpdate()) {
						final AlertDialog.Builder builder = new Builder(context);
						String msg = (ub.getDesc() != null && !"".equals(ub
								.getDesc())) ? ub.getDesc() : context
								.getString(R.string.sure_to_update);
						builder.setMessage(msg);
						builder.setTitle(R.string.app_name);
						builder.setPositiveButton(R.string.update,
								new OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										HttpRequestHelper hrh = new HttpRequestHelper();
										String subPath = Utils
												.getAppFolder(context)
												+ "/downloads/";
										String fileName = Utils
												.getAppName(context)
												+ "v"
												+ ub.getServerVersion()
												+ ".apk";
										File resultFile = hrh.downFile(
												ub.getUpdateUrl(), subPath,
												fileName);
										if (resultFile != null) {
											Intent intent = new Intent(
													Intent.ACTION_VIEW);
											intent.setDataAndType(
													Uri.fromFile(resultFile),
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
								});
						builder.setNegativeButton(R.string.cancel,
								new OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								});
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								final AlertDialog dialog = builder.create();
								dialog.getWindow()
										.setType(
												(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
								if (!dialog.isShowing()) {
									dialog.show();
								}
							}
						});
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