package com.eric.autowifi;

import java.util.ArrayList;
import java.util.List;

public class ContactBean {
	private String name;
	private List<PhoneBean> phoneList;
	private List<AddressBean> addressList;

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void addPhone(String phoneType, String phone)
	{
		if (phoneList == null)
		{
			phoneList = new ArrayList<PhoneBean>();
		}
		phoneList.add(new PhoneBean(phoneType, phone));
	}

	public List<PhoneBean> getPhones()
	{
		return phoneList;
	}

	public void addAddress(String street, String city, String region,
			String postCode, String formatAddress)
	{
		if (addressList == null)
		{
			addressList = new ArrayList<AddressBean>();
		}
		addressList.add(new AddressBean(street, city, region, postCode,
				formatAddress));
	}

	public List<AddressBean> getAddresses()
	{
		return addressList;
	}

	public class PhoneBean {
		private String phoneType;
		private String phone;

		public PhoneBean(String phoneType, String phone)
		{
			this.phoneType = phoneType;
			this.phone = phone;
		}

		public String getPhoneType()
		{
			return phoneType;
		}

		public void setPhoneType(String phoneType)
		{
			this.phoneType = phoneType;
		}

		public String getPhone()
		{
			return phone;
		}

		public void setPhone(String phone)
		{
			this.phone = phone;
		}

	}

	public class AddressBean {
		private String street;
		private String city;
		private String region;
		private String postCode;
		private String formatAddress;

		public AddressBean(String street, String city, String region,
				String postCode, String formatAddress)
		{
			this.street = street;
			this.city = city;
			this.region = region;
			this.postCode = postCode;
			this.formatAddress = formatAddress;
		}

		public String getStreet()
		{
			return street;
		}

		public void setStreet(String street)
		{
			this.street = street;
		}

		public String getCity()
		{
			return city;
		}

		public void setCity(String city)
		{
			this.city = city;
		}

		public String getRegion()
		{
			return region;
		}

		public void setRegion(String region)
		{
			this.region = region;
		}

		public String getPostCode()
		{
			return postCode;
		}

		public void setPostCode(String postCode)
		{
			this.postCode = postCode;
		}

		public String getFormatAddress()
		{
			return formatAddress;
		}

		public void setFormatAddress(String formatAddress)
		{
			this.formatAddress = formatAddress;
		}

	}
}
