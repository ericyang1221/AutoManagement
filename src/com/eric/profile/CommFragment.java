package com.eric.profile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eric.autowifi.R;
import com.eric.profile.beans.ProfileBean;

@SuppressLint("NewApi")
public class CommFragment extends AutoManagementFragment {
	private View wifi;
	private TextView wifiText;
	private View gps;
	private TextView gpsText;
	private View bluetooth;
	private TextView bluetoothText;
	private View sync;
	private TextView syncText;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.comm_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		wifi = this.getActivity().findViewById(R.id.cf_wifi);
		wifiText = (TextView) this.getActivity()
				.findViewById(R.id.cf_wifi_text);
		gps = this.getActivity().findViewById(R.id.cf_gps);
		gpsText = (TextView) this.getActivity().findViewById(R.id.cf_gps_text);
		bluetooth = this.getActivity().findViewById(R.id.cf_bluetooth);
		bluetoothText = (TextView) this.getActivity().findViewById(
				R.id.cf_bluetooth_text);
		sync = this.getActivity().findViewById(R.id.cf_sync_date);
		syncText = (TextView) this.getActivity().findViewById(
				R.id.cf_sync_date_text);

		OnClickListener ocl = new OnClickListener() {
			@Override
			public void onClick(View v) {
				String title = null;
				int whichSelected = 0;
				switch (v.getId()) {
				case R.id.cf_bluetooth:
					title = getString(R.string.bluetooth);
					whichSelected = pb.getBluetooth();
					break;
				case R.id.cf_gps:
					title = getString(R.string.gps);
					whichSelected = pb.getGps();
					break;
				case R.id.cf_sync_date:
					title = getString(R.string.sync_date);
					whichSelected = pb.getSyncData();
					break;
				case R.id.cf_wifi:
					title = getString(R.string.wifi);
					whichSelected = pb.getWifi();
					break;
				default:
					break;
				}
				showSwitchDialog(v.getId(), title, whichSelected);
			}
		};

		wifi.setOnClickListener(ocl);
		gps.setOnClickListener(ocl);
		bluetooth.setOnClickListener(ocl);
		sync.setOnClickListener(ocl);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		updateSwitchText(pb.getWifi(), wifiText);
		updateSwitchText(pb.getGps(), gpsText);
		updateSwitchText(pb.getBluetooth(), bluetoothText);
		updateSwitchText(pb.getSyncData(), syncText);
	}

	private void showSwitchDialog(final int id, String title, int whichSelected) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				this.getActivity());
		builder.setTitle(title);
		builder.setSingleChoiceItems(R.array.comm_switch, whichSelected,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (id) {
						case R.id.cf_bluetooth:
							pb.setBluetooth(which);
							updateSwitchText(which, bluetoothText);
							break;
						case R.id.cf_gps:
							pb.setGps(which);
							updateSwitchText(which, gpsText);
							break;
						case R.id.cf_sync_date:
							pb.setSyncData(which);
							updateSwitchText(which, syncText);
							break;
						case R.id.cf_wifi:
							pb.setWifi(which);
							updateSwitchText(which, wifiText);
							break;
						default:
							break;
						}
						pdb.update(pb);
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

	private void updateSwitchText(int mode, TextView switchText) {
		switch (mode) {
		case ProfileBean.NO_CHANGE:
			switchText.setText(getString(R.string.no_change));
			break;
		case ProfileBean.COMM_ON:
			switchText.setText(getString(R.string.on));
			break;
		case ProfileBean.COMM_OFF:
			switchText.setText(getString(R.string.off));
			break;
		default:
			break;
		}
	}
}
