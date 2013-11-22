package com.eric.profile.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProfileDB extends SQLiteOpenHelper {
	private static final String DB_NAME = "PROFILE_DB";
	private final String TBL_NAME = "PROFILE";

	public ProfileDB(Context context) {
		super(context, DB_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "Create table IF NOT EXISTS "
				+ TBL_NAME
				+ "(id integer PRIMARY KEY,profile_name varchar(50),profile_icon integer,trigger_type integer,triggered_wifi varchar(255),trigger_date1 varchar(200),trigger_date2 varchar(200),trigger_date3 varchar(200),trigger_date4 varchar(200),ring_mode integer,ring_volumn integer,notification_mode integer,notification_volumn integer,wifi integer,gps integer,bluetooth integer,sync_data integer)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "DROP TABLE IF EXISTS " + TBL_NAME;
		db.execSQL(sql);
		onCreate(db);
	}

	public List<ProfileBean> selectAll() {
		List<ProfileBean> pbList = new ArrayList<ProfileBean>();
		String[] columns = new String[] { "id", "profile_name", "profile_icon",
				"trigger_type", "triggered_wifi", "trigger_date1",
				"trigger_date2", "trigger_date3", "trigger_date4", "ring_mode",
				"ring_volumn", "notification_mode", "notification_volumn",
				"wifi", "gps", "bluetooth", "sync_data" };
		String whereClause = null;
		String[] whereArgs = null;
		String groupBy = null;
		String having = null;
		String orderBy = null;
		String limit = null;
		Cursor c = this.getReadableDatabase().query(TBL_NAME, columns,
				whereClause, whereArgs, groupBy, having, orderBy, limit);
		c.moveToFirst();
		for (int i = 0; i < c.getCount(); i++) {
			int id = c.getInt(0);
			String profileName = c.getString(1);
			int profileIcon = c.getInt(2);
			int triggerType = c.getInt(3);
			String triggeredWifi = c.getString(4);
			String triggerDate1 = c.getString(5);
			String triggerDate2 = c.getString(6);
			String triggerDate3 = c.getString(7);
			String triggerDate4 = c.getString(8);
			int ringMode = c.getInt(9);
			int ringVolumn = c.getInt(10);
			int notificationMode = c.getInt(11);
			int notificationVolumn = c.getInt(12);
			int wifi = c.getInt(13);
			int gps = c.getInt(14);
			int bluetooth = c.getInt(15);
			int syncData = c.getInt(16);
			ProfileBean pb = new ProfileBean(id, profileName, profileIcon,
					triggerType, triggeredWifi, triggerDate1, triggerDate2,
					triggerDate3, triggerDate4, ringMode, ringVolumn,
					notificationMode, notificationVolumn, wifi, gps, bluetooth,
					syncData);
			pbList.add(pb);
			c.moveToNext();
		}
		c.close();
		c = null;
		this.getReadableDatabase().close();
		return pbList;
	}

	public ProfileBean selectProfileById(int pid) {
		ProfileBean pb = null;
		String[] columns = new String[] { "id", "profile_name", "profile_icon",
				"trigger_type", "triggered_wifi", "trigger_date1",
				"trigger_date2", "trigger_date3", "trigger_date4", "ring_mode",
				"ring_volumn", "notification_mode", "notification_volumn",
				"wifi", "gps", "bluetooth", "sync_data" };
		String whereClause = "id=?";
		String[] whereArgs = new String[] { String.valueOf(pid) };
		String groupBy = null;
		String having = null;
		String orderBy = null;
		String limit = null;
		Cursor c = this.getReadableDatabase().query(TBL_NAME, columns,
				whereClause, whereArgs, groupBy, having, orderBy, limit);
		if (c.getCount() == 1) {
			c.moveToFirst();
			int id = c.getInt(0);
			String profileName = c.getString(1);
			int profileIcon = c.getInt(2);
			int triggerType = c.getInt(3);
			String triggeredWifi = c.getString(4);
			String triggerDate1 = c.getString(5);
			String triggerDate2 = c.getString(6);
			String triggerDate3 = c.getString(7);
			String triggerDate4 = c.getString(8);
			int ringMode = c.getInt(9);
			int ringVolumn = c.getInt(10);
			int notificationMode = c.getInt(11);
			int notificationVolumn = c.getInt(12);
			int wifi = c.getInt(13);
			int gps = c.getInt(14);
			int bluetooth = c.getInt(15);
			int syncData = c.getInt(16);
			pb = new ProfileBean(id, profileName, profileIcon, triggerType,
					triggeredWifi, triggerDate1, triggerDate2, triggerDate3,
					triggerDate4, ringMode, ringVolumn, notificationMode,
					notificationVolumn, wifi, gps, bluetooth, syncData);
		}
		c.close();
		c = null;
		this.getReadableDatabase().close();
		return pb;
	}

	public long insert(ProfileBean pb) {
		if (pb == null) {
			throw new IllegalArgumentException("LocationBean");
		}
		ContentValues values = new ContentValues();
		values.put("profile_name", pb.getProfileName());
		values.put("profile_icon", pb.getProfileIcon());
		values.put("trigger_type", pb.getTriggerType());
		values.put("triggered_wifi", pb.getTriggeredWifi());
		values.put("trigger_date1", pb.getTriggerDate1());
		values.put("trigger_date2", pb.getTriggerDate2());
		values.put("trigger_date3", pb.getTriggerDate3());
		values.put("trigger_date4", pb.getTriggerDate4());
		values.put("ring_mode", pb.getRingMode());
		values.put("ring_volumn", pb.getRingVolumn());
		values.put("notification_mode", pb.getNotificationMode());
		values.put("notification_volumn", pb.getNotificationVolumn());
		values.put("wifi", pb.getWifi());
		values.put("gps", pb.getGps());
		values.put("bluetooth", pb.getBluetooth());
		values.put("sync_data", pb.getSyncData());
		long ret = this.getWritableDatabase().insert(TBL_NAME, "", values);
		this.getWritableDatabase().close();
		return ret;
	}

	public long update(ProfileBean pb) {
		if (pb == null) {
			throw new IllegalArgumentException("LocationBean");
		}
		ContentValues values = new ContentValues();
		values.put("profile_name", pb.getProfileName());
		values.put("profile_icon", pb.getProfileIcon());
		values.put("trigger_type", pb.getTriggerType());
		values.put("triggered_wifi", pb.getTriggeredWifi());
		values.put("trigger_date1", pb.getTriggerDate1());
		values.put("trigger_date2", pb.getTriggerDate2());
		values.put("trigger_date3", pb.getTriggerDate3());
		values.put("trigger_date4", pb.getTriggerDate4());
		values.put("ring_mode", pb.getRingMode());
		values.put("ring_volumn", pb.getRingVolumn());
		values.put("notification_mode", pb.getNotificationMode());
		values.put("notification_volumn", pb.getNotificationVolumn());
		values.put("wifi", pb.getWifi());
		values.put("gps", pb.getGps());
		values.put("bluetooth", pb.getBluetooth());
		values.put("sync_data", pb.getSyncData());
		String whereClause = "id=?";
		String[] whereArgs = new String[] { String.valueOf(pb.getId()) };
		long ret = this.getWritableDatabase().update(TBL_NAME, values,
				whereClause, whereArgs);
		this.getWritableDatabase().close();
		return ret;
	}

	public void deleteProfileById(int id) {
		String whereClause = "id=?";
		String[] whereArgs = { String.valueOf(id) };
		this.getWritableDatabase().delete(TBL_NAME, whereClause, whereArgs);
		this.getWritableDatabase().close();
	}
}
