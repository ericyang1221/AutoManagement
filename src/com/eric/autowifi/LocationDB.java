package com.eric.autowifi;

import java.util.ArrayList;
import java.util.List;

import com.eric.autowifi.beans.LocationBean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocationDB extends SQLiteOpenHelper {
	private static final String DB_NAME = "LOCATIONDB";
	private final String TBL_NAME = "LOCATION";
	private Context context;

	public LocationDB(Context context) {
		super(context, DB_NAME, null, 1);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "Create table IF NOT EXISTS " + TBL_NAME
				+ "(id integer PRIMARY KEY,latitude integer,longitude integer)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "DROP TABLE IF EXISTS " + TBL_NAME;
		db.execSQL(sql);
		onCreate(db);
	}

	public List<LocationBean> selectAll() {
		List<LocationBean> lbList = new ArrayList<LocationBean>();
		String[] columns = new String[] { "id", "latitude", "longitude" };
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
			double latitude = c.getDouble(1);
			double longitude = c.getDouble(2);
			LocationBean lb = new LocationBean(id, latitude, longitude);
			lbList.add(lb);
			c.moveToNext();
		}
		c.close();
		c = null;
		this.getReadableDatabase().close();
		return lbList;
	}

	public List<LocationBean> selectByLatAndLng(double lat, double lng) {
		List<LocationBean> lbList = new ArrayList<LocationBean>();
		String[] columns = new String[] { "id", "latitude", "longitude" };
		String whereClause = "latitude=? and longitude=?";
		String[] whereArgs = new String[] { String.valueOf(lat),
				String.valueOf(lng) };
		String groupBy = null;
		String having = null;
		String orderBy = null;
		String limit = null;
		Cursor c = this.getReadableDatabase().query(TBL_NAME, columns,
				whereClause, whereArgs, groupBy, having, orderBy, limit);
		c.moveToFirst();
		for (int i = 0; i < c.getCount(); i++) {
			int id = c.getInt(0);
			double latitude = c.getDouble(1);
			double longitude = c.getDouble(2);
			LocationBean lb = new LocationBean(id, latitude, longitude);
			lbList.add(lb);
			c.moveToNext();
		}
		c.close();
		c = null;
		this.getReadableDatabase().close();
		return lbList;
	}

	public long insert(LocationBean lb) {
		if (lb == null) {
			throw new IllegalArgumentException("LocationBean");
		}
		ContentValues values = new ContentValues();
		// values.put(ID, fiBean.getId());
		values.put("latitude", lb.getLatitude());
		values.put("longitude", lb.getLongitude());
		long ret = this.getWritableDatabase().insert(TBL_NAME, "", values);
		if (ret != -1) {
			Utils.setLastSync(context, System.currentTimeMillis());
		}
		this.getWritableDatabase().close();
		return ret;
	}
}
