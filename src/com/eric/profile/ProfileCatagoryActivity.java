package com.eric.profile;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.eric.profile.db.ProfileBean;
import com.eric.profile.db.ProfileDB;

public class ProfileCatagoryActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_catagory);

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
		ProfileCatagoryListViewAdapter adapter = new ProfileCatagoryListViewAdapter();
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

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
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
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
			holder.icon.setImageResource(pb.getProfileIcon());
			holder.title.setText(pb.getProfileName());
			holder.desc.setText("test");
			return convertView;
		}

	}

	static class ViewHolder {
		TextView title;
		TextView desc;
		ImageView icon;
	}
}
