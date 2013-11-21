package com.eric.profile;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.eric.autowifi.R;
import com.eric.profile.db.ProfileBean;

public class SetVolumnDialog extends Dialog {
	private OnClickListener onClickListener;
	private CheckBox cb;
	private TextView progressText;
	private SeekBar sb;
	private int volumn;
	private int lastVolumn;
	private String title;

	protected SetVolumnDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public SetVolumnDialog(Context context, int ringVolumn,String title) {
		super(context);
		this.volumn = ringVolumn;
		this.title = title;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.set_volumn_dialog);
		this.setTitle(title);

		cb = (CheckBox) findViewById(R.id.sv_nochange);
		progressText = (TextView) findViewById(R.id.sv_progress);
		sb = (SeekBar) findViewById(R.id.sv_seekbar);

		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				progressText.setText(progress + "%");
				volumn = progress;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

		});

		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					disableVolumn();
					if(volumn != ProfileBean.VOLUMN_NO_CHANGE){
						lastVolumn = volumn;
					}
					volumn = ProfileBean.VOLUMN_NO_CHANGE;
				} else {
					enableVolumn();
					volumn = lastVolumn;
					sb.setProgress(volumn);
					progressText.setText(volumn + "%");
				}
			}

		});

		findViewById(R.id.sv_ok).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (onClickListener != null) {
					onClickListener.onOkClick(SetVolumnDialog.this, volumn);
				}
			}
		});
		findViewById(R.id.sv_cancel).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (onClickListener != null) {
							onClickListener.onCancelClick(SetVolumnDialog.this);
						}
					}
				});

		if (volumn == ProfileBean.VOLUMN_NO_CHANGE) {
			disableVolumn();
			cb.setChecked(true);
		} else {
			progressText.setText(volumn + "%");
			sb.setProgress(volumn);
		}
	}

	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	public interface OnClickListener {
		void onOkClick(Dialog dialog, int volumn);

		void onCancelClick(Dialog dialog);
	}

	private void disableVolumn() {
		progressText.setEnabled(false);
		sb.setEnabled(false);
	}

	private void enableVolumn() {
		progressText.setEnabled(true);
		sb.setEnabled(true);
	}
}
