package com.eric.profile;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

import com.eric.autowifi.R;

public class ProfileSettingActivity extends FragmentActivity {
	private boolean isTrigger;
	private boolean isSounds;
	private boolean isComm;
	private boolean isOther;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_setting);
		initTab();
	}

	private void initTab() {
		View trigger = findViewById(R.id.ps_trigger);
		final View triggerMark = findViewById(R.id.ps_trigger_mark);
		View sounds = findViewById(R.id.ps_sounds);
		final View soundsMark = findViewById(R.id.ps_sounds_mark);
		View comm = findViewById(R.id.ps_comm);
		final View commMark = findViewById(R.id.ps_comm_mark);
		View other = findViewById(R.id.ps_other);
		final View otherMark = findViewById(R.id.ps_other_mark);
		trigger.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isTrigger) {
					triggerMark.setVisibility(View.VISIBLE);
					soundsMark.setVisibility(View.INVISIBLE);
					commMark.setVisibility(View.INVISIBLE);
					otherMark.setVisibility(View.INVISIBLE);
					goTrigger();
				}
			}
		});
		sounds.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isSounds) {
					triggerMark.setVisibility(View.INVISIBLE);
					soundsMark.setVisibility(View.VISIBLE);
					commMark.setVisibility(View.INVISIBLE);
					otherMark.setVisibility(View.INVISIBLE);
					goSounds();
				}
			}
		});
		comm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isComm) {
					triggerMark.setVisibility(View.INVISIBLE);
					soundsMark.setVisibility(View.INVISIBLE);
					commMark.setVisibility(View.VISIBLE);
					otherMark.setVisibility(View.INVISIBLE);
					goComm();
				}
			}
		});
		other.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isOther) {
					triggerMark.setVisibility(View.INVISIBLE);
					soundsMark.setVisibility(View.INVISIBLE);
					commMark.setVisibility(View.INVISIBLE);
					otherMark.setVisibility(View.VISIBLE);
					goOther();
				}
			}
		});

		trigger.performClick();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	private void goTrigger() {
		TriggerFragment triggerFragment = new TriggerFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.pfs_main_layout, triggerFragment).commit();
	}

	private void goSounds() {
		SoundsFragment soundsFragment = new SoundsFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.pfs_main_layout, soundsFragment).commit();
	}

	private void goComm() {
		CommFragment commFragment = new CommFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.pfs_main_layout, commFragment).commit();
	}

	private void goOther() {
		OtherFragment otherFragment = new OtherFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.pfs_main_layout, otherFragment).commit();
	}

}
