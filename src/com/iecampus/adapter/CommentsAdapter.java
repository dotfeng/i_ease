package com.iecampus.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iecampus.activity.R;
import com.iecampus.moldel.Comments;

public class CommentsAdapter extends BaseAdapter {
	private Context context;
	private List<Comments> list;

	public class ViewHolder {
		private ImageView comments_userimgs;
		private TextView comments_username;
		private TextView comments_content;
		private TextView lou;
		private View myview_bottom;
	}

	public CommentsAdapter(Context context, List<Comments> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {

		return list.size();
	}

	@Override
	public Comments getItem(int position) {

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
			convertView = inflater.inflate(R.layout.comments_listview_item,
					null);
			viewholder = new ViewHolder();
			viewholder.comments_content = (TextView) convertView
					.findViewById(R.id.comments_content);
			/*
			 * viewholder.comments_userimgs = (ImageView) convertView
			 * .findViewById(R.id.comments_userimgs);
			 */
			viewholder.comments_username = (TextView) convertView
					.findViewById(R.id.comments_username);
			viewholder.myview_bottom = (View) convertView
					.findViewById(R.id.myview_bottom);
			viewholder.lou = (TextView) convertView.findViewById(R.id.lou);
			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}
		viewholder.comments_username.setText(this.getItem(position)
				.getUsername());
		viewholder.comments_content
				.setText(this.getItem(position).getContent());
		viewholder.lou.setText(position + 1 + "#");

		return convertView;
	}
}
