package com.eric.autowifi.beans;

import java.util.List;


public class SMSJSONWrapper {
	private String imei;
	private String syncType;
	private String typeValue;
	private List<SMSBean> sbList;

	public SMSJSONWrapper()
	{
	}

	public SMSJSONWrapper(String imei, String syncType, String typeValue,
			List<SMSBean> sbList)
	{
		this.imei = imei;
		this.syncType = syncType;
		this.typeValue = typeValue;
		this.sbList = sbList;
	}

	public String getImei()
	{
		return imei;
	}

	public void setImei(String imei)
	{
		this.imei = imei;
	}

	public String getSyncType()
	{
		return syncType;
	}

	public void setSyncType(String syncType)
	{
		this.syncType = syncType;
	}

	public String getTypeValue()
	{
		return typeValue;
	}

	public void setTypeValue(String typeValue)
	{
		this.typeValue = typeValue;
	}

	public List<SMSBean> getSbList()
	{
		return sbList;
	}

	public void setSbList(List<SMSBean> sbList)
	{
		this.sbList = sbList;
	}
}
