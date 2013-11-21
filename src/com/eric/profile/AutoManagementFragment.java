package com.eric.profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.eric.autowifi.MyApplication;
import com.eric.profile.db.ProfileBean;
import com.eric.profile.db.ProfileDB;

public class AutoManagementFragment extends Fragment {
	protected ProfileBean pb;
	protected ProfileDB pdb;
	protected MyApplication myApp;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		pb = (ProfileBean) this.getArguments().getSerializable("profileBean");
		myApp = (MyApplication)this.getActivity().getApplication();
		pdb = myApp.getProfileDB();
	}
}
