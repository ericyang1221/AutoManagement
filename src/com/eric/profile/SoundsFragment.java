package com.eric.profile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eric.autowifi.R;
import com.eric.profile.db.ProfileBean;

@SuppressLint("NewApi")
public class SoundsFragment extends AutoManagementFragment {
	private View ringerMode;
	private TextView ringerModeText;
	private View ringerVolumn;
	private TextView ringerVolumnText;
	private View notificationMode;
	private TextView notificationModeText;
	private View notificationVolumn;
	private TextView notificationVolumnText;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.sounds_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		ringerMode = this.getActivity().findViewById(R.id.sf_ring_mode);
		ringerModeText = (TextView) this.getActivity().findViewById(
				R.id.sf_ring_mode_text);
		ringerVolumn = this.getActivity().findViewById(R.id.sf_ring_volumn);
		ringerVolumnText = (TextView) this.getActivity().findViewById(
				R.id.sf_ring_volumn_text);
		notificationMode = this.getActivity().findViewById(
				R.id.sf_notification_mode);
		notificationModeText = (TextView) this.getActivity().findViewById(
				R.id.sf_notification_mode_text);
		notificationVolumn = this.getActivity().findViewById(
				R.id.sf_notification_volumn);
		notificationVolumnText = (TextView) this.getActivity().findViewById(
				R.id.sf_notification_volumn_text);

		ringerMode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showRingerModeDialog();
			}
		});

		ringerVolumn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showRingerVolumnDialog(R.id.sf_ring_volumn);
			}

		});

		notificationMode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showNotificationModeDialog();
			}
		});

		notificationVolumn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showRingerVolumnDialog(R.id.sf_notification_volumn);
			}

		});

		updateRingerMode(pb.getRingMode(), ringerModeText, ringerVolumn);
		updateRingerVolumn(pb.getRingVolumn(), ringerVolumnText);
		updateRingerMode(pb.getNotificationMode(), notificationModeText,
				notificationVolumn);
		updateRingerVolumn(pb.getNotificationVolumn(), notificationVolumnText);
	}

	private void showRingerModeDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				this.getActivity());
		builder.setTitle(getString(R.string.ring_mode));
		builder.setSingleChoiceItems(R.array.ringer_modes, pb.getRingMode(),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						pb.setRingMode(which);
						updateRingerMode(which, ringerModeText, ringerVolumn);
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

	private void showNotificationModeDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				this.getActivity());
		builder.setTitle(getString(R.string.notification_mode));
		builder.setSingleChoiceItems(R.array.ringer_modes,
				pb.getNotificationMode(),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						pb.setNotificationMode(which);
						updateRingerMode(which, notificationModeText,
								notificationVolumn);
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

	private void showRingerVolumnDialog(final int id) {
		String title = "";
		int volumn = 0;
		if (R.id.sf_ring_volumn == id) {
			title = getString(R.string.ring_volumn);
			volumn = pb.getRingVolumn();
		} else if (R.id.sf_notification_volumn == id) {
			title = getString(R.string.notification_volumn);
			volumn = pb.getNotificationVolumn();
		}
		SetVolumnDialog svd = new SetVolumnDialog(this.getActivity(),
				volumn, title);
		svd.setOnClickListener(new SetVolumnDialog.OnClickListener() {

			@Override
			public void onOkClick(Dialog dialog, int volumn) {
				if (R.id.sf_ring_volumn == id) {
					pb.setRingVolumn(volumn);
					updateRingerVolumn(volumn, ringerVolumnText);
				} else if (R.id.sf_notification_volumn == id) {
					pb.setNotificationVolumn(volumn);
					updateRingerVolumn(volumn, notificationVolumnText);
				}
				pdb.update(pb);
				dialog.dismiss();
			}

			@Override
			public void onCancelClick(Dialog dialog) {
				dialog.dismiss();
			}
		});
		svd.show();
	}

	private void updateRingerMode(int mode, TextView modeText, View volumnView) {
		switch (mode) {
		case ProfileBean.NO_CHANGE:
			modeText.setText(getString(R.string.no_change));
			volumnView.setVisibility(View.GONE);
			break;
		case ProfileBean.SOUND_RING:
			modeText.setText(getString(R.string.sound));
			volumnView.setVisibility(View.VISIBLE);
			break;
		case ProfileBean.SOUND_RING_AND_VIBRATE:
			modeText.setText(getString(R.string.sound_and_vibrate));
			volumnView.setVisibility(View.VISIBLE);
			break;
		case ProfileBean.SOUND_SILENT:
			modeText.setText(getString(R.string.silent));
			volumnView.setVisibility(View.GONE);
			break;
		case ProfileBean.SOUND_VIBRATE:
			modeText.setText(getString(R.string.vibrate));
			volumnView.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}

	private void updateRingerVolumn(int volumn, TextView volumnText) {
		if (volumn == ProfileBean.VOLUMN_NO_CHANGE) {
			volumnText.setText(getString(R.string.no_change));
		} else {
			volumnText.setText(volumn + "%");
		}
	}
}
