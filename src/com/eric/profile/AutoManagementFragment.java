package com.eric.profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.eric.autowifi.MyApplication;
import com.eric.profile.beans.ProfileBean;
import com.eric.profile.db.ProfileDB;

public class AutoManagementFragment extends Fragment {
	protected ProfileBean pb;
	protected ProfileDB pdb;
	protected MyApplication myApp;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		int pid = this.getArguments().getInt("pid");
		myApp = (MyApplication) this.getActivity().getApplication();
		pdb = myApp.getProfileDB();
		pb = pdb.selectProfileById(pid);
	}

	@Override
	public void onStart() {
		super.onStart();
	}
}
