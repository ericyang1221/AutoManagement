package com.eric.autowifi;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationManagerUtil implements LocationListener {
	private LocationManager locationManager;
	private OnLocationChangeListener onLocationChangeListener;
	private Context context;

	public LocationManagerUtil(Context context)
	{
		this.context = context;
	}

	public void requestLocation(
			OnLocationChangeListener onLocationChangeListener)
	{
		this.onLocationChangeListener = onLocationChangeListener;
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 1000, 0, this);
		}
		else
		{
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 1000, 0, this);
		}
	}

	@Override
	public void onLocationChanged(Location location)
	{
		locationManager.removeUpdates(this);
		LastLocationBean llb = Utils.getLastLocationBean(context);
		if (llb != null)
		{
			double distenceInM = Utils.getDistance(llb.getLatitude(),
					llb.getLongitude(), location.getLatitude(),
					location.getLongitude());
			long time = System.currentTimeMillis()
					- llb.getTimeInMilliseconds();
			double timeInHour = (double)time / 1000 / 60 / 60;
			int speed = (int) (distenceInM < Constants.DEFAULT_OPENWIFI_RADIUS ? 0
					: (distenceInM / timeInHour));
			Utils.setLastSpeed(context, speed);
		}
		if (onLocationChangeListener != null)
		{
			onLocationChangeListener.onLocationChanged(location);
		}
		Utils.setLastLocationAndTime(context, location,
				System.currentTimeMillis());
	}

	@Override
	public void onProviderDisabled(String provider)
	{
	}

	@Override
	public void onProviderEnabled(String provider)
	{
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
	}
}
