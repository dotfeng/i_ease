package com.iecampus.adapter;

import java.util.List;



import com.iecampus.activity.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PubCategoryListViewAdapter extends BaseAdapter {
	private List<String> list;
	private Context context;

	public class ViewHolder {
		private TextView tx;
	}

	public PubCategoryListViewAdapter(List<String> list, Context context) {
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public String getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewholder;
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.pub_category_item, null);
			viewholder = new ViewHolder();
			viewholder.tx = (TextView) convertView.findViewById(R.id.tx);
			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}
		viewholder.tx.setText(this.list.get(position));
		return convertView;
	}

}
