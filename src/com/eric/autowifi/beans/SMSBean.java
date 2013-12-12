package com.eric.autowifi.beans;

public class SMSBean {
	private String name;
	private String phoneNumber;
	private String smsbody;
	private long date;
	private int type;

	public SMSBean()
	{
	}

	public SMSBean(String name, String phoneNumber, String smsbody, long date,
			int type)
	{
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.smsbody = smsbody;
		this.date = date;
		this.type = type;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getPhoneNumber()
	{
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber)
	{
		this.phoneNumber = phoneNumber;
	}

	public String getSmsbody()
	{
		return smsbody;
	}

	public void setSmsbody(String smsbody)
	{
		this.smsbody = smsbody;
	}

	public long getDate()
	{
		return date;
	}

	public void setDate(long date)
	{
		this.date = date;
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

}
