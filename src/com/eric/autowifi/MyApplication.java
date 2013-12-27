package com.eric.autowifi;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

import com.eric.autowifi.utils.CrashSender;
import com.eric.profile.db.ProfileDB;

@ReportsCrashes(formKey = "", customReportContent = {
		ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION,
		ReportField.PHONE_MODEL, ReportField.BRAND, ReportField.STACK_TRACE }, formUri = "http://www.backendofyourchoice.com/reportpath")
public class MyApplication extends Application {
	private ProfileDB pdb;

	@Override
	public void onCreate() {
		super.onCreate();
		ACRA.init(this);
		CrashSender sender = new CrashSender(this);
		ACRA.getErrorReporter().setReportSender(sender);
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
