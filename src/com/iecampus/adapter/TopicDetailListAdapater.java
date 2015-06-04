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

public class TopicDetailListAdapater extends BaseAdapter {
	private List<Comments> list;
	private Context context;

	private class ViewHolder {
		private TextView detail_comments;
		private ImageView detail_userimg;
		private TextView detail_lou;
	}

	public TopicDetailListAdapater(List<Comments> list, Context context) {
		this.list = list;
		this.context = context;
	}

	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Comments getItem(int position) {

		return this.list.get(position);
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
			convertView = inflater
					.inflate(R.layout.topic_detail_listitem, null);
			viewholder = new ViewHolder();
			viewholder.detail_comments = (TextView) convertView
					.findViewById(R.id.detail_comments);
			viewholder.detail_userimg = (ImageView) convertView
					.findViewById(R.id.detail_userimg);
			viewholder.detail_lou = (TextView) convertView
					.findViewById(R.id.detail_lou);
			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}
		viewholder.detail_comments.setText(this.getItem(position).getContent());
		viewholder.detail_lou.setText(getCount()-position + "#");
		return convertView;
	}
}
