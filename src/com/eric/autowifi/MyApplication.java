package com.eric.autowifi;

import android.app.Application;

import com.eric.profile.db.ProfileDB;

public class MyApplication extends Application {
	private ProfileDB pdb;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	public ProfileDB getProfileDB() {
		if (pdb == null) {
			pdb = new ProfileDB(this);
		}
		return pdb;
	}
}
