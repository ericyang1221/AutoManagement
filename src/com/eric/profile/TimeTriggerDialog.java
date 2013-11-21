package com.eric.profile;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TimePicker;

import com.eric.autowifi.R;

public class TimeTriggerDialog extends Dialog {
	public static final String SP = "-";
	public static final String MON = "Mon";
	public static final String TUE = "Tue";
	public static final String WED = "Wed";
	public static final String THU = "Thu";
	public static final String FRI = "Fri";
	public static final String SAT = "Sat";
	public static final String SUN = "Sun";
	private OnClickListener onClickListener;
	private int hourOfDay;
	private int minute;
	private boolean isMonChecked;
	private boolean isTueChecked;
	private boolean isWedChecked;
	private boolean isThuChecked;
	private boolean isFriChecked;
	private boolean isSatChecked;
	private boolean isSunChecked;

	protected TimeTriggerDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public TimeTriggerDialog(Context context, String date) {
		super(context);
		if (date != null) {
			String[] ds = date.split(" ");
			if (ds.length > 1) {
				String[] checked = ds[0].split(SP);
				String[] time = ds[1].split(":");
				for (String s : checked) {
					if (MON.equals(s)) {
						isMonChecked = true;
						break;
					} else if (TUE.equals(s)) {
						isTueChecked = true;
						break;
					} else if (WED.equals(s)) {
						isWedChecked = true;
						break;
					} else if (THU.equals(s)) {
						isThuChecked = true;
						break;
					} else if (FRI.equals(s)) {
						isFriChecked = true;
						break;
					} else if (SAT.equals(s)) {
						isSatChecked = true;
						break;
					} else if (SUN.equals(s)) {
						isSunChecked = true;
						break;
					}
				}
				if (time.length > 1) {
					hourOfDay = Integer.valueOf(time[0]);
					minute = Integer.valueOf(time[1]);
				}
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.time_trigger_dialog);
		this.setTitle(R.string.time_trigger);

		TimePicker tp = (TimePicker) findViewById(R.id.ttd_tp);
		tp.setIs24HourView(true);
		tp.setCurrentHour(hourOfDay);
		tp.setCurrentMinute(minute);

		tp.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				TimeTriggerDialog.this.hourOfDay = hourOfDay;
				TimeTriggerDialog.this.minute = minute;
			}
		});

		CheckBox monCb = (CheckBox) findViewById(R.id.ttf_mon);
		CheckBox tueCb = (CheckBox) findViewById(R.id.ttf_tue);
		CheckBox wedCb = (CheckBox) findViewById(R.id.ttf_wed);
		CheckBox thuCb = (CheckBox) findViewById(R.id.ttf_thu);
		CheckBox friCb = (CheckBox) findViewById(R.id.ttf_fri);
		CheckBox satCb = (CheckBox) findViewById(R.id.ttf_sat);
		CheckBox sunCb = (CheckBox) findViewById(R.id.ttf_sun);
		
		monCb.setChecked(isMonChecked);
		tueCb.setChecked(isTueChecked);
		wedCb.setChecked(isWedChecked);
		thuCb.setChecked(isThuChecked);
		friCb.setChecked(isFriChecked);
		satCb.setChecked(isSatChecked);
		sunCb.setChecked(isSunChecked);

		monCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				isMonChecked = isChecked;
			}

		});
		tueCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				isTueChecked = isChecked;
			}

		});
		wedCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				isWedChecked = isChecked;
			}

		});
		thuCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				isThuChecked = isChecked;
			}

		});
		friCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				isFriChecked = isChecked;
			}

		});
		satCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				isSatChecked = isChecked;
			}

		});
		sunCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				isSunChecked = isChecked;
			}

		});

		findViewById(R.id.ttp_ok).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (onClickListener != null) {
							onClickListener.onOkClick(TimeTriggerDialog.this,
									hourOfDay, minute, isMonChecked,
									isTueChecked, isWedChecked, isThuChecked,
									isFriChecked, isSatChecked, isSunChecked);
						}
					}
				});
		findViewById(R.id.ttp_cancel).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (onClickListener != null) {
							onClickListener
									.onCancelClick(TimeTriggerDialog.this);
						}
					}
				});
	}

	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	public interface OnClickListener {
		void onOkClick(Dialog dialog, int hourOfDay, int minute,
				boolean isMonChecked, boolean isTueChecked,
				boolean isWedChecked, boolean isThuChecked,
				boolean isFriChecked, boolean isSatChecked, boolean isSunChecked);

		void onCancelClick(Dialog dialog);
	}

}
