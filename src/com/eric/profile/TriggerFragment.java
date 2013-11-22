package com.eric.profile;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eric.autowifi.R;
import com.eric.autowifi.Utils;
import com.eric.profile.db.ProfileBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@SuppressLint("NewApi")
public class TriggerFragment extends AutoManagementFragment {
	private final String TAG = "TriggerFragment";
	private View triggerType;
	private View date1;
	private View date2;
	private View date3;
	private View date4;
	private TextView date1Text;
	private TextView date2Text;
	private TextView date3Text;
	private TextView date4Text;
	private LinearLayout wifi;
	private TextView triggerTypeText;
	private TextView triggerWifiText;
	private String[] wifiArray;
	private boolean[] wifiArrayIsChecked;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.trigger_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		triggerType = this.getActivity().findViewById(R.id.tf_trigger_type);
		triggerTypeText = (TextView) this.getActivity().findViewById(
				R.id.tf_trigger_type_text);
		triggerWifiText = (TextView) this.getActivity().findViewById(
				R.id.tf_trigger_wifi_text);
		date1 = this.getActivity().findViewById(R.id.tf_trigger_days_and_time1);
		date2 = this.getActivity().findViewById(R.id.tf_trigger_days_and_time2);
		date3 = this.getActivity().findViewById(R.id.tf_trigger_days_and_time3);
		date4 = this.getActivity().findViewById(R.id.tf_trigger_days_and_time4);

		date1Text = (TextView) this.getActivity().findViewById(
				R.id.tf_trigger_days_and_time_text1);
		date2Text = (TextView) this.getActivity().findViewById(
				R.id.tf_trigger_days_and_time_text2);
		date3Text = (TextView) this.getActivity().findViewById(
				R.id.tf_trigger_days_and_time_text3);
		date4Text = (TextView) this.getActivity().findViewById(
				R.id.tf_trigger_days_and_time_text4);

		wifi = (LinearLayout) this.getActivity().findViewById(
				R.id.tf_trigger_wifi);

		triggerType.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showTriggerTypeDialog();
			}

		});
		wifi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showWifiTriggerDialog();
			}

		});

		OnClickListener ocl = new OnClickListener() {

			@Override
			public void onClick(View v) {
				showTimeTriggerDialog(v.getId());
			}

		};
		date1.setOnClickListener(ocl);
		date2.setOnClickListener(ocl);
		date3.setOnClickListener(ocl);
		date4.setOnClickListener(ocl);
	}

	@Override
	public void onStart() {
		updateTriggerType(pb.getTriggerType(), true);
		super.onStart();
	}

	public void showTriggerTypeDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				this.getActivity());
		builder.setTitle(getString(R.string.trigger_type));
		builder.setSingleChoiceItems(R.array.trigger_types,
				pb.getTriggerType(), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						updateTriggerType(which, false);
						dialog.dismiss();
					}
				});
		builder.setPositiveButton(getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		AlertDialog ad = builder.create();
		ad.show();
	}

	public void showTimeTriggerDialog(final int which) {
		String date = null;
		switch (which) {
		case R.id.tf_trigger_days_and_time1:
			date = pb.getTriggerDate1();
			break;
		case R.id.tf_trigger_days_and_time2:
			date = pb.getTriggerDate2();
			break;
		case R.id.tf_trigger_days_and_time3:
			date = pb.getTriggerDate3();
			break;
		case R.id.tf_trigger_days_and_time4:
			date = pb.getTriggerDate4();
			break;
		default:
			break;
		}
		TimeTriggerDialog ttd = new TimeTriggerDialog(this.getActivity(), date);
		ttd.show();
		ttd.setOnClickListener(new TimeTriggerDialog.OnClickListener() {

			@Override
			public void onOkClick(Dialog dialog, int hourOfDay, int minute,
					boolean isMonChecked, boolean isTueChecked,
					boolean isWedChecked, boolean isThuChecked,
					boolean isFriChecked, boolean isSatChecked,
					boolean isSunChecked) {
				StringBuffer sb = new StringBuffer();
				if (isMonChecked) {
					sb.append(TimeTriggerDialog.MON).append(
							TimeTriggerDialog.SP);
				}
				if (isTueChecked) {
					sb.append(TimeTriggerDialog.TUE).append(
							TimeTriggerDialog.SP);
				}
				if (isWedChecked) {
					sb.append(TimeTriggerDialog.WED).append(
							TimeTriggerDialog.SP);
				}
				if (isThuChecked) {
					sb.append(TimeTriggerDialog.THU).append(
							TimeTriggerDialog.SP);
				}
				if (isFriChecked) {
					sb.append(TimeTriggerDialog.FRI).append(
							TimeTriggerDialog.SP);
				}
				if (isSatChecked) {
					sb.append(TimeTriggerDialog.SAT).append(
							TimeTriggerDialog.SP);
				}
				if (isSunChecked) {
					sb.append(TimeTriggerDialog.SUN).append(
							TimeTriggerDialog.SP);
				}
				if (sb.length() > 0) {
					sb.deleteCharAt(sb.length() - 1);
					sb.append(" ").append(hourOfDay).append(":").append(minute);
				} else {
					sb.setLength(0);
				}
				switch (which) {
				case R.id.tf_trigger_days_and_time1:
					pb.setTriggerDate1(sb.toString());
					date1Text.setText(sb.toString());
					break;
				case R.id.tf_trigger_days_and_time2:
					pb.setTriggerDate2(sb.toString());
					date2Text.setText(sb.toString());
					break;
				case R.id.tf_trigger_days_and_time3:
					pb.setTriggerDate3(sb.toString());
					date3Text.setText(sb.toString());
					break;
				case R.id.tf_trigger_days_and_time4:
					pb.setTriggerDate4(sb.toString());
					date4Text.setText(sb.toString());
					break;
				default:
					break;
				}
				pdb.update(pb);
				dialog.dismiss();
			}

			@Override
			public void onCancelClick(Dialog dialog) {
				dialog.dismiss();
			}
		});
	}

	private void showWifiTriggerDialog() {
		final List<String> selectedList = new ArrayList<String>();
		for (int i = 0; i < wifiArrayIsChecked.length; i++) {
			if (wifiArrayIsChecked[i]) {
				selectedList.add(wifiArray[i]);
			}
		}
		new AlertDialog.Builder(this.getActivity())
				.setTitle(getString(R.string.wifi_trigger))
				.setMultiChoiceItems(wifiArray, wifiArrayIsChecked,
						new OnMultiChoiceClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which, boolean isChecked) {
								if (isChecked) {
									selectedList.add(wifiArray[which]);
								} else {
									selectedList.remove(wifiArray[which]);
								}
							}
						})
				.setPositiveButton(getString(R.string.ok),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Gson gson = new Gson();
								String triggeredWifi = gson
										.toJson(selectedList);
								Log.d(TAG, triggeredWifi);
								pb.setTriggeredWifi(triggeredWifi);
								pdb.update(pb);
								triggerWifiText
										.setText(Utils.formatProfileWifiName(selectedList));
							}
						}).show();
	}

	private void updateTriggerType(int which, boolean isInit) {
		if (ProfileBean.TRIGGER_TYPE_MANUAL_OR_TIME == which) {
			triggerTypeText.setText(getString(R.string.manual_or_time_trigger));
			date1.setVisibility(View.VISIBLE);
			date2.setVisibility(View.VISIBLE);
			date3.setVisibility(View.VISIBLE);
			date4.setVisibility(View.VISIBLE);
			wifi.setVisibility(View.GONE);

			date1Text.setText(pb.getTriggerDate1());
			date2Text.setText(pb.getTriggerDate2());
			date3Text.setText(pb.getTriggerDate3());
			date4Text.setText(pb.getTriggerDate4());
		} else if (ProfileBean.TRIGGER_TYPE_WIFI == which) {
			triggerTypeText.setText(getString(R.string.wifi_trigger));
			date1.setVisibility(View.GONE);
			date2.setVisibility(View.GONE);
			date3.setVisibility(View.GONE);
			date4.setVisibility(View.GONE);
			wifi.setVisibility(View.VISIBLE);

			final WifiManager wifiManager = (WifiManager) this.getActivity()
					.getSystemService(Context.WIFI_SERVICE);
			List<WifiConfiguration> configs = wifiManager
					.getConfiguredNetworks();
			if (configs == null) {
				wifiArray = null;
				wifiArrayIsChecked = null;
				wifi.setEnabled(false);
				for (int i = 0; i < wifi.getChildCount(); i++) {
					wifi.getChildAt(i).setEnabled(false);
				}
				triggerWifiText.setText(R.string.please_open_wifi);
			} else {
				int size = configs.size();
				wifiArray = new String[size];
				wifiArrayIsChecked = new boolean[size];
				Gson gson = new Gson();
				Type type = new TypeToken<List<String>>() {
				}.getType();
				List<String> selectedList = gson.fromJson(
						pb.getTriggeredWifi(), type);
				for (int i = 0; i < size; i++) {
					wifiArray[i] = configs.get(i).SSID;
					wifiArrayIsChecked[i] = false;
					if (selectedList != null) {
						for (String s : selectedList) {
							if (wifiArray[i].equals(s)) {
								wifiArrayIsChecked[i] = true;
								break;
							}
						}
					}
				}
				triggerWifiText.setText(Utils.formatProfileWifiName(selectedList));
			}
		} else {
			which = 0;
		}
		if (!isInit) {
			pb.setTriggerType(which);
			pdb.update(pb);
		}
	}
}
