package com.eric.autowifi.utils;

import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;

import android.content.Context;

import com.eric.autowifi.SdLogger;

public class CrashSender implements ReportSender {
	private final String TAG = "CrashSender";
	private Context context;

	public CrashSender(Context context) {
		this.context = context;
	}

	@Override
	public void send(CrashReportData crashReportData)
			throws ReportSenderException {
		SdLogger.logE(context, TAG, crashReportData.toString());
	}
}
