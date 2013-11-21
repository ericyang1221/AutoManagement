package com.eric.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eric.autowifi.R;

@SuppressLint("NewApi")
public class OtherFragment extends AutoManagementFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.other_fragment, container, false);
	}
}
