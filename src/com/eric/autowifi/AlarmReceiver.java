package com.eric.autowifi;

import java.util.Collections;
import java.util.List;

import com.eric.autowifi.beans.LocationBean;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
	WifiManager wifiManager;

	@Override
	public void onReceive(final Context context, Intent arg1)
	{
		Utils.initApiKey(context);
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
					Collections.sort(lbList, new DisAscComparator(lat, lng));
					LocationBean nearest = lbList.get(0);
					double d = Utils.getDistance(lat, lng,
							nearest.getLatitude(), nearest.getLongitude());
					Log.d("nearest distence", String.valueOf(d));
					if (d < Constants.DEFAULT_OPENWIFI_RADIUS)
					{
						openWifi(context);
						ConnectivityManager connManager = (ConnectivityManager) context
								.getSystemService(Context.CONNECTIVITY_SERVICE);
						State state = connManager.getNetworkInfo(
								ConnectivityManager.TYPE_WIFI).getState();
						if (State.CONNECTED == state)
						{
							Utils.stopAlarm(context);
						}
					}
					else
					{
						closeWifi(context);
						long tmpInterval = (long) ((double) d
								/ getSpeed(context) * 60 * 60 * 1000);
						long interval = tmpInterval > Constants.MAX_ALARM_INTERVAL ? Constants.MAX_ALARM_INTERVAL
								: tmpInterval < Constants.DEFAULT_ALARM_INTERVAL ? Constants.DEFAULT_ALARM_INTERVAL
										: tmpInterval;
						Log.d("AlarmReceiver", "interval = " + interval);
						Utils.startAlarm(
								context,
								interval == -1 ? (int) Constants.MAX_ALARM_INTERVAL
										: (int) interval, interval);
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
					Log.d("AlarmReceiver", "location list is empty.");
				}
			}
		});
	}

	private void openWifi(Context context)
	{
		Log.d("AlarmReceiver", "open wifi");
		if (wifiManager == null)
		{
			wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
		}
		if (!wifiManager.isWifiEnabled())
		{
			wifiManager.setWifiEnabled(true);
		}
	}

	private void closeWifi(Context context)
	{
		Log.d("AlarmReceiver", "close wifi");
		if (wifiManager == null)
		{
			wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
		}
		if (wifiManager.isWifiEnabled())
		{
			wifiManager.setWifiEnabled(false);
		}
	}

	private int getSpeed(Context context)
	{
		int speed;
		int s = Utils.getLastSpeed(context);
		if (s == -1)
		{
			speed = Constants.DEFAULT_SPEED_IN_M_PER_HOUR;
		}
		else
		{
			speed = s > Constants.MAX_SPEED_IN_M_PER_HOUR ? Constants.MAX_SPEED_IN_M_PER_HOUR
					: s == 0 ? 1 : s;
		}
		Log.d("AlarmReceiver", "last speed is : " + speed);
		return speed;
	}
}
