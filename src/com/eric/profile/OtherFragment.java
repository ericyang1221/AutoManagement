package com.eric.profile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.eric.autowifi.R;

@SuppressLint("NewApi")
public class OtherFragment extends AutoManagementFragment {
	private View changeIcon;
	private ImageView iconImg;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.other_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		changeIcon = this.getActivity().findViewById(R.id.of_change_icon);
		iconImg = (ImageView) this.getActivity().findViewById(R.id.of_icon_img);
		final ChangeIconDialog cid = new ChangeIconDialog(this.getActivity());
		cid.setOnClickListener(new ChangeIconDialog.OnClickListener() {

			@Override
			public void onIconClick(Dialog dialog, int id) {
				iconImg.setImageResource(id);
				pb.setProfileIcon(id);
				pdb.update(pb);
				dialog.dismiss();
			}

			@Override
			public void onCancelClick(Dialog dialog) {
				dialog.dismiss();
			}
		});
		changeIcon.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				cid.show();
			}
		});
	}
	
	@Override
	public void onStart() {
		super.onStart();
		iconImg.setImageResource(pb.getProfileIcon());
	}
}
