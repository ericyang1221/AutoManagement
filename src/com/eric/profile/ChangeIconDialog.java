package com.eric.profile;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.eric.autowifi.R;

public class ChangeIconDialog extends Dialog {
	private Context context;
	private OnClickListener onClickListener;

	protected ChangeIconDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public ChangeIconDialog(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.change_icon_dialog);
		this.setTitle(R.string.profile_icon);

		findViewById(R.id.change_icon_cancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (onClickListener != null) {
							onClickListener
									.onCancelClick(ChangeIconDialog.this);
						}
					}
				});

		ListView lv = (ListView) findViewById(R.id.change_icon_lv);
		ChangeIconListViewAdapter adapter = new ChangeIconListViewAdapter();
		lv.setAdapter(adapter);
	}

	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	public interface OnClickListener {
		void onCancelClick(Dialog dialog);

		void onIconClick(Dialog dialog,int id);
	}

	class ChangeIconListViewAdapter extends BaseAdapter {
		private List<int[]> iconList;

		public ChangeIconListViewAdapter() {
			iconList = new ArrayList<int[]>();
			iconList.add(new int[] { R.drawable.i_0, R.drawable.i_1,
					R.drawable.i_2, R.drawable.i_3 });
			iconList.add(new int[] { R.drawable.i_4, R.drawable.i_5,
					R.drawable.i_6, R.drawable.i_7 });
			iconList.add(new int[] { R.drawable.i_8, R.drawable.i_9,
					R.drawable.i_10, R.drawable.i_11 });
			iconList.add(new int[] { R.drawable.i_12, R.drawable.i_13,
					R.drawable.i_14, R.drawable.i_15 });
			iconList.add(new int[] { R.drawable.i_16, R.drawable.i_17,
					R.drawable.i_18, R.drawable.i_19 });
			iconList.add(new int[] { R.drawable.i_20, R.drawable.i_21,
					R.drawable.i_22, R.drawable.i_23 });
			iconList.add(new int[] { R.drawable.i_24, R.drawable.i_25,
					R.drawable.i_26, R.drawable.i_27 });
			iconList.add(new int[] { R.drawable.i_28, R.drawable.i_29,
					R.drawable.i_30, R.drawable.i_31 });
			iconList.add(new int[] { R.drawable.i_32, R.drawable.i_33,
					R.drawable.i_34, R.drawable.i_35 });
			iconList.add(new int[] { R.drawable.i_36, R.drawable.i_37,
					R.drawable.i_38, R.drawable.i_39 });
			iconList.add(new int[] { R.drawable.i_40, R.drawable.i_41,
					R.drawable.i_42, R.drawable.i_43 });
			iconList.add(new int[] { R.drawable.i_44, R.drawable.i_45,
					R.drawable.i_46, R.drawable.i_47 });
			iconList.add(new int[] { R.drawable.i_48, R.drawable.i_49,
					R.drawable.i_50, R.drawable.i_51 });
			iconList.add(new int[] { R.drawable.i_52, R.drawable.i_53,
					R.drawable.i_54, R.drawable.i_55 });
			iconList.add(new int[] { R.drawable.i_56, R.drawable.i_57,
					R.drawable.i_58, R.drawable.i_59 });
			iconList.add(new int[] { R.drawable.i_60, R.drawable.i_61,
					R.drawable.i_62, R.drawable.i_63 });
			iconList.add(new int[] { R.drawable.i_64, R.drawable.i_65,
					R.drawable.i_66, R.drawable.i_67 });
			iconList.add(new int[] { R.drawable.i_68, R.drawable.i_69,
					R.drawable.i_70, R.drawable.i_71 });
			iconList.add(new int[] { R.drawable.i_72, R.drawable.i_73,
					R.drawable.i_74, R.drawable.i_75 });
			iconList.add(new int[] { R.drawable.i_76, R.drawable.i_77,
					R.drawable.i_78, R.drawable.i_79 });
			iconList.add(new int[] { R.drawable.i_80, R.drawable.i_81,
					R.drawable.i_82, R.drawable.i_83 });
			iconList.add(new int[] { R.drawable.i_84, R.drawable.i_85,
					R.drawable.i_86, R.drawable.i_87 });
			iconList.add(new int[] { R.drawable.i_88, R.drawable.i_89,
					R.drawable.i_90, R.drawable.i_91 });
			iconList.add(new int[] { R.drawable.i_92, R.drawable.i_93,
					R.drawable.i_94, R.drawable.i_95 });
			iconList.add(new int[] { R.drawable.i_96, R.drawable.i_97,
					R.drawable.i_98, R.drawable.i_99 });
			iconList.add(new int[] { R.drawable.i_100, R.drawable.i_101,
					R.drawable.i_102, R.drawable.i_103 });
			iconList.add(new int[] { R.drawable.i_104, R.drawable.i_105,
					R.drawable.i_106, R.drawable.i_107 });
			iconList.add(new int[] { R.drawable.i_108, R.drawable.i_109,
					R.drawable.i_110, R.drawable.i_111 });
			iconList.add(new int[] { R.drawable.i_112, R.drawable.i_113,
					R.drawable.i_114, R.drawable.i_115 });
			iconList.add(new int[] { R.drawable.i_116, R.drawable.i_117,
					R.drawable.i_118, R.drawable.i_119 });
			iconList.add(new int[] { R.drawable.i_120, R.drawable.i_121,
					R.drawable.i_122, R.drawable.i_123 });
			iconList.add(new int[] { R.drawable.i_124, R.drawable.i_125,
					R.drawable.i_126, R.drawable.i_127 });
			iconList.add(new int[] { R.drawable.i_128, R.drawable.i_129,
					R.drawable.i_130, R.drawable.i_131 });
			iconList.add(new int[] { R.drawable.i_132, R.drawable.i_133,
					R.drawable.i_134, R.drawable.i_135 });
			iconList.add(new int[] { R.drawable.i_136, R.drawable.i_137,
					R.drawable.i_138, R.drawable.i_139 });
			iconList.add(new int[] { R.drawable.i_140, R.drawable.i_141,
					R.drawable.i_142, R.drawable.i_143 });
		}

		@Override
		public int getCount() {
			return iconList.size();
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
			int[] icons = iconList.get(position);
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.change_icon_lv_item, null);
				holder = new ViewHolder();
				holder.ibs[0] = (ImageButton) convertView.findViewById(R.id.i1);
				holder.ibs[1] = (ImageButton) convertView.findViewById(R.id.i2);
				holder.ibs[2] = (ImageButton) convertView.findViewById(R.id.i3);
				holder.ibs[3] = (ImageButton) convertView.findViewById(R.id.i4);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (icons.length == holder.ibs.length) {
				for (int i = 0; i < icons.length; i++) {
					ImageButton ib = holder.ibs[i];
					final int id = icons[i];
					ib.setImageResource(icons[i]);
					ib.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if (onClickListener != null) {
								onClickListener.onIconClick(ChangeIconDialog.this,id);
							}
						}
					});
				}
			}
			return convertView;
		}

	}

	static class ViewHolder {
		ImageButton[] ibs = new ImageButton[4];
	}

}
