package com.eric.autowifi;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UpdateManager {
	private final static String TAG = "UpdateManager";

	public static void doUpdate(final Context context) {
		Log.d(TAG, "doUpdate");
		long lastCheckupdateTime = Utils.getLastCheckUpdateTime(context);
		// lastCheckupdateTime = 0;
		long d = System.currentTimeMillis() - lastCheckupdateTime;
		// do update every week.
		Log.d("UpdateManager.doUpdate", "Days from last update," + d / 1000
				/ 60 / 60 / 24);
		if (d > (7 * 24 * 60 * 60 * 1000)) {
			Log.d(TAG, "updating");
			Intent downloadIntent = new Intent(context, DownloadService.class);
			context.startService(downloadIntent);
		}
	}

	public static UpdateBean checkUpdate(Context context) {
		UpdateBean ub = null;
		try {
			ub = sendCheckUpdateRequest();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String currentVersion = Utils.getAppVersionName(context);
		if (ub != null) {
			String serverVersion = ub.getServerVersion();
			try {
				Double cv = Double.valueOf(currentVersion);
				Double sv = Double.valueOf(serverVersion);
				if ((sv - cv) > 0) {
					Log.d("UpdateManager.checkUpdate",
							"ServerVersion > CurrentVersion");
					String updateUrl = ub.getUpdateUrl();
					if (updateUrl != null && updateUrl.length() > 0) {
						ub.setNeedUpdate(true);
					} else {
						ub.setNeedUpdate(false);
					}
				} else {
					Log.d("UpdateManager.checkUpdate",
							"ServerVersion <= CurrentVersion.No need update.");
					ub.setNeedUpdate(false);
				}
			} catch (Exception e) {
				e.printStackTrace();
				ub.setNeedUpdate(false);
			}
		}
		return ub;
	}

	private static UpdateBean sendCheckUpdateRequest() throws JSONException {
		UpdateBean ub = null;
		HttpRequestHelper hrh = new HttpRequestHelper();
		String url = "http://0.locationtracker.duapp.com/static/baseCheck.json";
		JSONObject jo = hrh.sendRequestAndReturnJson(url);
		if (jo != null) {
			ub = new UpdateBean();
			ub.setNeedUpdate(false);
			if (jo.has("serverVersion")) {
				ub.setServerVersion(jo.getString("serverVersion"));
			}
			if (jo.has("url")) {
				ub.setUpdateUrl(jo.getString("url"));
			}
			if (jo.has("desc")) {
				ub.setDesc(jo.getString("desc"));
			}
		}
		return ub;
	}
}
