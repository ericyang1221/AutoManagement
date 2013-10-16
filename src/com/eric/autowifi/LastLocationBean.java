package com.eric.autowifi;

public class LastLocationBean {
	private long timeInMilliseconds;
	private double latitude;
	private double longitude;

	public LastLocationBean(long timeInMilliseconds, double latitude,
			double longitude)
	{
		this.timeInMilliseconds = timeInMilliseconds;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public long getTimeInMilliseconds()
	{
		return timeInMilliseconds;
	}

	public void setTimeInMilliseconds(long timeInMilliseconds)
	{
		this.timeInMilliseconds = timeInMilliseconds;
	}

	public double getLatitude()
	{
		return latitude;
	}

	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}

	public double getLongitude()
	{
		return longitude;
	}

	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}

}
