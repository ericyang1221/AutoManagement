package com.eric.autowifi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import android.content.Context;

public class SdLogger {
	private final static String LEVEL_DEBUG = "D";
	private final static String LEVEL_WARNING = "W";
	private final static String LEVEL_ERROR = "E";
	private final static String LEVEL_INFO = "I";
	private static Boolean isSdExist = null;
	private static String logFilePath = null;
	private static File logDir = null;

	public static boolean isSdExist() {
		if (isSdExist == null) {
			if (android.os.Environment.MEDIA_MOUNTED
					.equals(android.os.Environment.getExternalStorageState())) {
				isSdExist = true;
			} else {
				isSdExist = false;
			}
		}
		return isSdExist;
	}

	private static File getLogFolder(Context ctx) {
		if (isSdExist()) {
			if (logDir == null || !logDir.exists()) {
				String logDirPath = android.os.Environment
						.getExternalStorageDirectory().getAbsolutePath()
						+ "/"
						+ Utils.getAppFolder(ctx) + "/log";
				logDir = new File(logDirPath);
				if (!logDir.exists()) {
					logDir.mkdirs();
				}
			}
		}
		return logDir;
	}

	private static File getLogFile(Context ctx) {
		Calendar cal = Calendar.getInstance();
		File f = null;
		if (isSdExist()) {
			logFilePath = getLogFolder(ctx).getAbsolutePath()
					+ "/smsUploadLog_" + cal.get(Calendar.YEAR) + "_"
					+ (cal.get(Calendar.MONTH) + 1) + "_"
					+ cal.get(Calendar.DAY_OF_MONTH) + ".log";
			f = new File(logFilePath);
			if (!f.exists()) {
				try {
					f.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					logFilePath = null;
				}
			}
		}
		return f;
	}

	private static void log(Context ctx, String logLevel, String tag,
			String text) {
		File f = getLogFile(ctx);
		if (f != null && f.exists()) {
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(f,
						true));
				Calendar cal = Calendar.getInstance();
				String month = (cal.get(Calendar.MONTH) + 1) > 9 ? String
						.valueOf(cal.get(Calendar.MONTH) + 1) : "0"
						+ (cal.get(Calendar.MONTH) + 1);
				String day = cal.get(Calendar.DAY_OF_MONTH) > 9 ? String
						.valueOf(cal.get(Calendar.DAY_OF_MONTH)) : "0"
						+ cal.get(Calendar.DAY_OF_MONTH);
				String hour = cal.get(Calendar.HOUR_OF_DAY) > 9 ? String
						.valueOf(cal.get(Calendar.HOUR_OF_DAY)) : "0"
						+ cal.get(Calendar.HOUR_OF_DAY);
				String min = cal.get(Calendar.MINUTE) > 9 ? String.valueOf(cal
						.get(Calendar.MINUTE)) : "0" + cal.get(Calendar.MINUTE);
				String sec = cal.get(Calendar.SECOND) > 9 ? String.valueOf(cal
						.get(Calendar.SECOND)) : "0" + cal.get(Calendar.SECOND);
				String mil = String.valueOf(cal.get(Calendar.MILLISECOND));
				String time = month + "-" + day + " " + hour + ":" + min + ":"
						+ sec + "." + mil;
				String line = logLevel + "\t" + time + "\t"
						+ Utils.getAppName(ctx) + "\t" + tag + "\t" + text;
				writer.append(line);
				writer.newLine();
				writer.flush();
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void logD(Context ctx, String tag, String text) {
		log(ctx, LEVEL_DEBUG, tag, text);
	}

	public static void logI(Context ctx, String tag, String text) {
		log(ctx, LEVEL_INFO, tag, text);
	}

	public static void logE(Context ctx, String tag, String text) {
		log(ctx, LEVEL_ERROR, tag, text);
	}

	public static void logW(Context ctx, String tag, String text) {
		log(ctx, LEVEL_WARNING, tag, text);
	}
}
