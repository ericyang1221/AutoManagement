package com.eric.profile;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.eric.autowifi.MyApplication;
import com.eric.autowifi.R;
import com.eric.profile.db.ProfileBean;
import com.eric.profile.db.ProfileDB;

public class ProfileEditorActivity extends Activity {
	private ProfileEditorListViewAdapter adapter;
	private ProfileDB pdb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_editor);
		pdb = ((MyApplication) this.getApplication()).getProfileDB();
		initTitleBar();
		initListView();
	}

	@Override
	protected void onResume() {
		if (adapter != null) {
			adapter.updateData();
			adapter.notifyDataSetChanged();
		}
		super.onResume();
	}

	private void initTitleBar() {
		final AddProfileDialog dialog = new AddProfileDialog(
				ProfileEditorActivity.this,null);
		dialog.setOnClickListener(new AddProfileDialog.OnClickListener() {
			@Override
			public void onOkClick(Dialog dialog, String profileName) {
				ProfileBean pb = new ProfileBean(profileName);
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
			public void onItemClick(AdapterView<?> arg0, View v, int arg2,
					long arg3) {
				Intent i = new Intent(ProfileEditorActivity.this,
						ProfileSettingActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("profileBean",
						(ProfileBean) v.getTag(R.id.profile_bean));
				i.putExtras(bundle);
				startActivity(i);
			}

		});
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, final View v,
					int arg2, long arg3) {
				ViewHolder holder = (ViewHolder) v.getTag();
				new AlertDialog.Builder(ProfileEditorActivity.this)
						.setTitle(holder.title.getText().toString())
						.setItems(
								new String[] { getString(R.string.rename),
										getString(R.string.delete) },
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										switch (which) {
										case 0:
											showRenameDialog(v);
											break;
										case 1:
											showDeleteDialog(v);
											break;
										default:
											break;
										}
										dialog.dismiss();
									}
								}).show();
				return true;
			}

		});
	}

	private void showDeleteDialog(final View v) {
		AlertDialog.Builder builder = new Builder(ProfileEditorActivity.this);
		builder.setMessage(getString(R.string.sure_to_delete));
		builder.setTitle(getString(R.string.info));
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ViewHolder holder = (ViewHolder) v.getTag();
						int id = holder.id;
						pdb.deleteProfileById(id);
						adapter.updateData();
						adapter.notifyDataSetChanged();
					}
				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	private void showRenameDialog(final View v) {
		final ProfileBean pb = (ProfileBean) v.getTag(R.id.profile_bean);
		if (pb != null) {
			final AddProfileDialog dialog = new AddProfileDialog(
					ProfileEditorActivity.this, pb.getProfileName());
			dialog.setOnClickListener(new AddProfileDialog.OnClickListener() {
				@Override
				public void onOkClick(Dialog dialog, String profileName) {
					pb.setProfileName(profileName);
					pdb.update(pb);
					adapter.updateData();
					adapter.notifyDataSetChanged();
					dialog.dismiss();
				}

				@Override
				public void onCancelClick(Dialog dialog) {
					dialog.dismiss();
				}
			});
			dialog.show();
		}
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
		private List<ProfileBean> pbList;

		public ProfileEditorListViewAdapter() {
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
			if (ProfileBean.TRIGGER_TYPE_MANUAL_OR_TIME == pb.getTriggerType()) {
				holder.desc.setText(R.string.manual_or_time_trigger);
			} else if (ProfileBean.TRIGGER_TYPE_WIFI == pb.getTriggerType()) {
				holder.desc.setText(R.string.wifi_trigger);
			} else {
				holder.desc.setText("");
			}
			holder.id = pb.getId();
			convertView.setTag(R.id.profile_bean, pb);
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
