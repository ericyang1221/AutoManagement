package com.eric.autowifi;

import java.util.List;

public class ContactJSONWrapper {
	private String imei;
	private List<ContactBean> cbList;

	public ContactJSONWrapper(String imei, List<ContactBean> cbList)
	{
		this.imei = imei;
		this.cbList = cbList;
	}

	public String getImei()
	{
		return imei;
	}

	public void setImei(String imei)
	{
		this.imei = imei;
	}

	public List<ContactBean> getCbList()
	{
		return cbList;
	}

	public void setCbList(List<ContactBean> cbList)
	{
		this.cbList = cbList;
	}

}
