package com.eric.profile;

import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.eric.autowifi.R;
import com.eric.profile.ProfileService.MyBinder;
import com.eric.profile.beans.ProfileBean;
import com.eric.profile.db.ProfileDB;

public class ProfileCatagoryActivity extends Activity {
	ProfileCatagoryListViewAdapter adapter;
	private ProfileService profileService;
	private boolean mBound = false;
	private boolean isFromNotification;

	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			MyBinder binder = (MyBinder) service;
			profileService = binder.getService();
			mBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_catagory);

		isFromNotification = this.getIntent().getBooleanExtra(
				"isFromNotification", false);
		System.out.println(isFromNotification);

		findViewById(R.id.profile_edit).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent i = new Intent(ProfileCatagoryActivity.this,
								ProfileEditorActivity.class);
						startActivity(i);
					}
				});

		ListView lv = (ListView) findViewById(R.id.pc_lv);
		adapter = new ProfileCatagoryListViewAdapter();
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int arg2,
					long arg3) {
				ProfileBean pb = (ProfileBean) v.getTag(R.id.profile_bean);
				profileService.changeProfile(pb);
				if (isFromNotification) {
					finish();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onResume() {
		if (adapter != null) {
			adapter.updateData();
			adapter.notifyDataSetChanged();
		}
		super.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();
		Intent intent = new Intent(ProfileCatagoryActivity.this,
				ProfileService.class);
		this.startService(intent);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
	}

	class ProfileCatagoryListViewAdapter extends BaseAdapter {
		private ProfileDB pdb;
		private List<ProfileBean> pbList;

		public ProfileCatagoryListViewAdapter() {
			pdb = new ProfileDB(ProfileCatagoryActivity.this);
			updateData();
		}

		public void updateData() {
			pbList = pdb.selectAll();
		}

		@Override
		public int getCount() {
			return pbList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ProfileBean pb = pbList.get(position);
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(ProfileCatagoryActivity.this)
						.inflate(R.layout.profile_catagory_item, null);
				holder = new ViewHolder();
				holder.icon = (ImageView) convertView
						.findViewById(R.id.pci_icon);
				holder.title = (TextView) convertView
						.findViewById(R.id.pci_title);
				holder.desc = (TextView) convertView
						.findViewById(R.id.pci_desc);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (pb.isAuto()) {
				holder.icon.setImageBitmap(null);
				holder.title.setText(R.string.profile_auto);
				holder.desc.setText(R.string.auto_select_profile);
			} else {
				holder.icon.setImageResource(pb.getProfileIcon());
				holder.title.setText(pb.getProfileName());
				if (ProfileBean.TRIGGER_TYPE_MANUAL_OR_TIME == pb
						.getTriggerType()) {
					holder.desc.setText(R.string.manual_or_time_trigger);
				} else if (ProfileBean.TRIGGER_TYPE_WIFI == pb.getTriggerType()) {
					holder.desc.setText(R.string.wifi_trigger);
				} else {
					holder.desc.setText("");
				}
			}
			convertView.setTag(R.id.profile_bean, pb);
			return convertView;
		}

	}

	static class ViewHolder {
		TextView title;
		TextView desc;
		ImageView icon;
	}
}
