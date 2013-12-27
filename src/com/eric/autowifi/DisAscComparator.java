package com.eric.autowifi;

import java.util.Comparator;

import com.eric.autowifi.beans.LocationBean;

public class DisAscComparator implements Comparator<LocationBean> {
	private double lat;
	private double lng;

	public DisAscComparator(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}

	@Override
	public int compare(LocationBean lhs, LocationBean rhs) {
		double lat_a = lhs.getLatitude();
		double lng_a = lhs.getLongitude();
		double lat_b = rhs.getLatitude();
		double lng_b = rhs.getLongitude();
		return countDis(lat_a, lng_a, lat_b, lng_b);
	}

	private int countDis(double lat_a, double lng_a, double lat_b, double lng_b) {
		int dis;

		double d1 = Utils.getDistance(lat_a, lng_a, lat, lng);
		double d2 = Utils.getDistance(lat_b, lng_b, lat, lng);
		if (d1 > d2) {
			dis = 1;
		} else if (d1 == d2) {
			dis = 0;
		} else {
			dis = -1;
		}
		return dis;
	}
}
