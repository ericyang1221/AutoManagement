package com.eric.autowifi;

import java.util.Collections;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiStateReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(final Context context, Intent intent) {
		NetworkInfo networkInfo = intent
				.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
		DetailedState state = networkInfo.getDetailedState();
		Log.d("DetailedState", String.valueOf(state));
		if (DetailedState.CONNECTED == state) {
			Log.d("WifiStateReceiver", "WIFI CONNECTED.");
			Utils.stopAlarm(context);
			Utils.initApiKey(context);
			Utils.doAutoSmsBackup(context);
			UpdateManager.doUpdate(context);
			LocationManagerUtil l = new LocationManagerUtil(context);
			l.requestLocation(new OnLocationChangeListener() {
				@Override
				public void onLocationChanged(Location location) {
					double lat = location.getLatitude();
					double lng = location.getLongitude();
					LocationDB ldb = new LocationDB(context);
					List<LocationBean> lbList = ldb.selectAll();
					if (lbList != null && !lbList.isEmpty()) {
						Collections
								.sort(lbList, new DisAscComparator(lat, lng));
						LocationBean nearest = lbList.get(0);
						double d = Utils.getDistance(lat, lng,
								nearest.getLatitude(), nearest.getLongitude());
						if (d > Constants.DEFAULT_INSERT_RADIUS) {
							ldb.insert(new LocationBean(0, lat, lng));
							Log.d("ldb.insert distence", String.valueOf(d));
						}
						// for (LocationBean l : lbList)
						// {
						// double ds = Utils.getDistance(lat, lng,
						// l.getLatitude(), l.getLongitude());
						// System.out.println(ds);
						// }
					} else {
						ldb.insert(new LocationBean(0, lat, lng));
						Log.d("WifiStateReceiver",
								"first location has inserted.");
					}
				}
			});

			Utils.doAutoWifiProfile(context);
		} else if (DetailedState.DISCONNECTED == state) {
			Log.d("WifiStateReceiver", "WIFI DISCONNECTED.");
			Utils.startAlarm(context,
					Constants.DISCONNECT_TO_CONNECT_TRIGGER_AFTER_MILLISECONDS,
					Constants.DEFAULT_ALARM_INTERVAL);
		} else {
			Log.d("WifiStateReceiver", "WIFI OTHER STATES.");
		}
	}
}