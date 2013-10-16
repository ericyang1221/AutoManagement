package com.eric.autowifi;

import java.util.Collections;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.util.Log;

public class WifiStateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent)
	{
		Log.d("WifiStateReceiver", "wifi state changed.");
		// 获得网络连接服务
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// State state = connManager.getActiveNetworkInfo().getState();
		// 获取WIFI网络连接状态
		State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		// 判断是否正在使用WIFI网络
		Log.d("wifi state", state.toString());
		if (State.CONNECTED == state)
		{
			Utils.stopAlarm(context);
			Utils.initApiKey(context);
			Utils.doAutoSmsBackup(context);
			LocationManagerUtil l = new LocationManagerUtil(context);
			l.requestLocation(new OnLocationChangeListener() {
				@Override
				public void onLocationChanged(Location location)
				{
					double lat = location.getLatitude();
					double lng = location.getLongitude();
					LocationDB ldb = new LocationDB(context);
					List<LocationBean> lbList = ldb.selectAll();
					if (lbList != null && !lbList.isEmpty())
					{
						Collections
								.sort(lbList, new DisAscComparator(lat, lng));
						LocationBean nearest = lbList.get(0);
						double d = Utils.getDistance(lat, lng,
								nearest.getLatitude(), nearest.getLongitude());
						if (d > Constants.DEFAULT_INSERT_RADIUS)
						{
							ldb.insert(new LocationBean(0, lat, lng));
							Log.d("ldb.insert distence", String.valueOf(d));
						}
						// for (LocationBean l : lbList)
						// {
						// double ds = Utils.getDistance(lat, lng,
						// l.getLatitude(), l.getLongitude());
						// System.out.println(ds);
						// }
					}
					else
					{
						ldb.insert(new LocationBean(0, lat, lng));
						Log.d("WifiStateReceiver",
								"first location has inserted.");
					}
				}
			});
		}
		else
		{
			Utils.startAlarm(context,
					Constants.DISCONNECT_TO_CONNECT_TRIGGER_AFTER_MILLISECONDS,
					Constants.DEFAULT_ALARM_INTERVAL);
		}
		// // 获取GPRS网络连接状态
		// state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
		// .getState();
		// // 判断是否正在使用GPRS网络
		// if (State.CONNECTED != state)
		// {
		// }
	}
}