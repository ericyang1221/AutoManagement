package com.eric.autowifi;

import java.util.Comparator;

public class DisAscComparator implements Comparator<LocationBean> {
	private double lat;
	private double lng;

	public DisAscComparator(double lat, double lng)
	{
		this.lat = lat;
		this.lng = lng;
	}

	@Override
	public int compare(LocationBean lhs, LocationBean rhs)
	{
		int ret = 0;
		double lat_a = lhs.getLatitude();
		double lng_a = lhs.getLongitude();
		double lat_b = rhs.getLatitude();
		double lng_b = rhs.getLongitude();
		if (countDis(lat_a, lng_a, lat_b, lng_b))
		{
			ret = 1;
		}
		else
		{
			ret = -1;
		}
		return ret;
	}

	private boolean countDis(double lat_a, double lng_a, double lat_b,
			double lng_b)
	{
		boolean dis = false;

		double d1 = Utils.getDistance(lat_a, lng_a, lat, lng);
		double d2 = Utils.getDistance(lat_b, lng_b, lat, lng);
		if (d1 > d2)
		{
			dis = true;
		}
		return dis;
	}
}
