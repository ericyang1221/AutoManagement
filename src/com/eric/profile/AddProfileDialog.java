package com.eric.profile;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.eric.autowifi.R;

public class AddProfileDialog extends Dialog {
	private OnClickListener onClickListener;
	private String profileName = null;

	protected AddProfileDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public AddProfileDialog(Context context, String profileName) {
		super(context);
		this.profileName = profileName;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.add_profile_dialog);
		this.setTitle(R.string.add_profile);

		final EditText et = (EditText) findViewById(R.id.apd_profile_name);
		if (profileName != null) {
			et.setText(profileName);
		}
		findViewById(R.id.apd_ok).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (onClickListener != null) {
							onClickListener.onOkClick(AddProfileDialog.this, et
									.getText().toString());
						}
					}
				});
		findViewById(R.id.apd_cancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (onClickListener != null) {
							onClickListener
									.onCancelClick(AddProfileDialog.this);
						}
					}
				});
	}

	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	public interface OnClickListener {
		void onOkClick(Dialog dialog, String profileName);

		void onCancelClick(Dialog dialog);
	}

}
