package com.eric.profile.db;

import java.io.Serializable;

import com.eric.autowifi.R;

public class ProfileBean implements Serializable {
	public static final int TRIGGER_TYPE_MANUAL_OR_TIME = 0;
	public static final int TRIGGER_TYPE_WIFI = 1;
	public static final int NO_CHANGE = 0;
	public static final int SOUND_RING = 1;
	public static final int SOUND_RING_AND_VIBRATE = 2;
	public static final int SOUND_VIBRATE = 3;
	public static final int SOUND_SILENT = 4;
	public static final int VOLUMN_NO_CHANGE = -1;
	public static final int COMM_ON = 1;
	public static final int COMM_OFF = 2;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String profileName;
	private int profileIcon;
	private int triggerType;
	private String triggeredWifi;
	private String triggerDate1;
	private String triggerDate2;
	private String triggerDate3;
	private String triggerDate4;
	private int ringMode;
	private int ringVolumn;
	private int notificationMode;
	private int notificationVolumn;
	private int wifi;
	private int gps;
	private int bluetooth;
	private int syncData;

	public ProfileBean() {
	}

	public ProfileBean(String profileName) {
		this.profileName = profileName;
		profileIcon = R.drawable.ic_launcher;
		this.triggerType = 0;
		this.triggerDate1 = null;
		this.triggerDate2 = null;
		this.triggerDate3 = null;
		this.triggerDate4 = null;
		this.ringMode = 0;
		this.ringVolumn = 0;
		this.notificationMode = 0;
		this.notificationVolumn = 0;
		this.wifi = 0;
		this.gps = 0;
		this.bluetooth = 0;
		this.syncData = 0;
	}

	public ProfileBean(int id, String profileName, int profileIcon,
			int triggerType,String triggeredWifi, String triggerDate1, String triggerDate2,
			String triggerDate3, String triggerDate4, int ringMode,
			int ringVolumn, int notificationMode, int notificationVolumn,
			int wifi, int gps, int bluetooth, int syncData) {
		this.id = id;
		this.profileName = profileName;
		this.profileIcon = profileIcon;
		this.triggerType = triggerType;
		this.triggeredWifi = triggeredWifi;
		this.triggerDate1 = triggerDate1;
		this.triggerDate2 = triggerDate2;
		this.triggerDate3 = triggerDate3;
		this.triggerDate4 = triggerDate4;
		this.ringMode = ringMode;
		this.ringVolumn = ringVolumn;
		this.notificationMode = notificationMode;
		this.notificationVolumn = notificationVolumn;
		this.wifi = wifi;
		this.gps = gps;
		this.bluetooth = bluetooth;
		this.syncData = syncData;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public int getProfileIcon() {
		return profileIcon;
	}

	public void setProfileIcon(int profileIcon) {
		this.profileIcon = profileIcon;
	}

	public int getTriggerType() {
		return triggerType;
	}

	public void setTriggerType(int triggerType) {
		this.triggerType = triggerType;
	}

	public String getTriggerDate1() {
		return triggerDate1;
	}

	public void setTriggerDate1(String triggerDate1) {
		this.triggerDate1 = triggerDate1;
	}

	public String getTriggerDate2() {
		return triggerDate2;
	}

	public void setTriggerDate2(String triggerDate2) {
		this.triggerDate2 = triggerDate2;
	}

	public String getTriggerDate3() {
		return triggerDate3;
	}

	public void setTriggerDate3(String triggerDate3) {
		this.triggerDate3 = triggerDate3;
	}

	public String getTriggerDate4() {
		return triggerDate4;
	}

	public void setTriggerDate4(String triggerDate4) {
		this.triggerDate4 = triggerDate4;
	}

	public int getRingMode() {
		return ringMode;
	}

	public void setRingMode(int ringMode) {
		this.ringMode = ringMode;
	}

	public int getRingVolumn() {
		return ringVolumn;
	}

	public void setRingVolumn(int ringVolumn) {
		this.ringVolumn = ringVolumn;
	}

	public int getNotificationMode() {
		return notificationMode;
	}

	public void setNotificationMode(int notificationMode) {
		this.notificationMode = notificationMode;
	}

	public int getNotificationVolumn() {
		return notificationVolumn;
	}

	public void setNotificationVolumn(int notificationVolumn) {
		this.notificationVolumn = notificationVolumn;
	}

	public int getWifi() {
		return wifi;
	}

	public void setWifi(int wifi) {
		this.wifi = wifi;
	}

	public int getGps() {
		return gps;
	}

	public void setGps(int gps) {
		this.gps = gps;
	}

	public int getBluetooth() {
		return bluetooth;
	}

	public void setBluetooth(int bluetooth) {
		this.bluetooth = bluetooth;
	}

	public int getSyncData() {
		return syncData;
	}

	public void setSyncData(int syncData) {
		this.syncData = syncData;
	}

	public String getTriggeredWifi() {
		return triggeredWifi;
	}

	public void setTriggeredWifi(String triggeredWifi) {
		this.triggeredWifi = triggeredWifi;
	}

}
