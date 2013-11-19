package com.eric.profile;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
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

public class ProfileEditorActivity extends Activity {
	private ProfileEditorListViewAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_editor);
		initTitleBar();
		initListView();
	}

	private void initTitleBar() {
		final AddProfileDialog dialog = new AddProfileDialog(
				ProfileEditorActivity.this);
		dialog.setOnClickListener(new AddProfileDialog.OnClickListener() {
			@Override
			public void onOkClick(Dialog dialog, String profileName) {
				ProfileBean pb = new ProfileBean(profileName);
				ProfileDB pdb = new ProfileDB(ProfileEditorActivity.this);
				pdb.insert(pb);
				adapter.updateData();
				adapter.notifyDataSetChanged();
				dialog.dismiss();
			}

			@Override
			public void onCancelClick(Dialog dialog) {
				dialog.dismiss();
			}
		});
		findViewById(R.id.pe_add).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.show();
			}
		});
	}

	private void initListView() {
		ListView lv = (ListView) findViewById(R.id.pe_lv);
		adapter = new ProfileEditorListViewAdapter();
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

			}

		});
		lv.setOnLongClickListener(new OnLongClickListener(){
			@Override
			public boolean onLongClick(View v) {
				ViewHolder holder = (ViewHolder)v.getTag();
				int id = holder.id;
				return true;
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

	class ProfileEditorListViewAdapter extends BaseAdapter {
		private ProfileDB pdb;
		private List<ProfileBean> pbList;

		public ProfileEditorListViewAdapter() {
			pdb = new ProfileDB(ProfileEditorActivity.this);
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
				convertView = LayoutInflater.from(ProfileEditorActivity.this)
						.inflate(R.layout.profile_editor_item, null);
				holder = new ViewHolder();
				holder.icon = (ImageView) convertView
						.findViewById(R.id.pei_icon);
				holder.title = (TextView) convertView
						.findViewById(R.id.pei_title);
				holder.desc = (TextView) convertView
						.findViewById(R.id.pei_desc);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.icon.setImageResource(pb.getProfileIcon());
			holder.title.setText(pb.getProfileName());
			holder.desc.setText("test");
			holder.id = pb.getId();
			return convertView;
		}

	}

	static class ViewHolder {
		int id;
		TextView title;
		TextView desc;
		ImageView icon;
	}
}
