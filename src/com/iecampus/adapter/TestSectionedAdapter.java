package com.iecampus.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iecampus.activity.R;
import com.iecampus.activity.TopicDetailActivity;
import com.iecampus.moldel.Topic;
import com.iecampus.view.SectionedBaseAdapter;

public class TestSectionedAdapter extends SectionedBaseAdapter {
	private Context context;
	private int color[] = { R.color.main, R.color.reds, R.color.pink,
			R.color.green, R.color.main, R.color.reds, R.color.pink,
			R.color.main, R.color.reds, R.color.green };
	private List<Topic> title;
	private String comments[][];

	public TestSectionedAdapter(List<Topic> title, String comments[][],
			Context context) {
		this.title = title;
		this.comments = comments;
		this.context = context;
	}

	@Override
	public Object getItem(int section, int position) {
		return null;
	}

	@Override
	public long getItemId(int section, int position) {
		return 0;
	}

	@Override
	public int getSectionCount() {
		if (title == null) {
			return 0;
		}
		return title.size();
	}

	@Override
	public int getCountForSection(int section) {
		int length = 0;
		for (int i = 0; i < comments[section].length; i++) {
			if (!comments[section][i].equals("#"))
				length++;
		}
		if (length >= 3) {
			return 3;
		}
		if (length == 0) {
			return 1;
		}
		return length;
	}

	@Override
	public View getItemView(int section, int position, View convertView,
			ViewGroup parent) {
		RelativeLayout layout = null;
		if (convertView == null) {
			LayoutInflater inflator = (LayoutInflater) parent.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			layout = (RelativeLayout) inflator.inflate(
					R.layout.section_list_item, null);
		} else {
			layout = (RelativeLayout) convertView;
		}
		LinearLayout number_laout = (LinearLayout) layout
				.findViewById(R.id.numbers_laout);
		final int po = section;
		TextView number = (TextView) layout.findViewById(R.id.numbers);
		if (position == 2 || (getCountForSection(po) - 1) == position) {
			number_laout.setVisibility(View.VISIBLE);
			number.setText("已经有" + getCommentsLength(comments[po]) + "人参与评论");
		} else if (getCountForSection(po) == 1
				&& getCommentsLength(comments[po]) == 0) {
			number_laout.setVisibility(View.VISIBLE);
			number.setText("已经有" + comments[po].length + "人参与评论");

		} else {
			number_laout.setVisibility(View.GONE);
		}

		layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, TopicDetailActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("title", title.get(po).getTitle());
				intent.putExtra("comment_num", getCommentsLength(comments[po])
						+ "");
				intent.putExtra("list", getValuebleComments(comments[po]));
				intent.putExtra("colorid", color[po % 10]);
				intent.putExtra("tid", title.get(po).getTid());
		
				context.startActivity(intent);
			}
		});
		if (comments[section][position].equals("#"))
			((TextView) layout.findViewById(R.id.textItem))
					.setText("暂时还没有人发表评论！");
		else {
			((TextView) layout.findViewById(R.id.textItem))
					.setText(comments[section][position]);
		}
		return layout;
	}

	@Override
	public View getSectionHeaderView(int section, View convertView,
			ViewGroup parent) {
		LinearLayout layout = null;
		if (convertView == null) {
			LayoutInflater inflator = (LayoutInflater) parent.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			layout = (LinearLayout) inflator
					.inflate(R.layout.header_item, null);
		} else {
			layout = (LinearLayout) convertView;
		}
		if (getSectionCount() != 0) {
			((TextView) layout.findViewById(R.id.textItem)).setText(title.get(
					section).getTitle());
		}

		layout.setBackgroundResource(color[section % 10]);
		return layout;
	}

	public int getCommentsLength(String[] str) {
		int length = 0;
		for (int i = 0; i < str.length; i++) {
			if (!str[i].equals("#"))
				length++;
		}
		return length;
	}

	public String[] getValuebleComments(String[] str) {
		int length = 0;
		for (int i = 0; i < str.length; i++) {
			if (!str[i].equals("#"))
				length++;
			else {
				break;
			}
		}
		String[] comments = new String[length];
		for (int i = 0; i < str.length; i++) {
			if (!str[i].equals("#")) {
				comments[i] = str[i];
			} else {
				break;
			}
		}
		return comments;
	}
}
