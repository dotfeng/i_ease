package com.iecampus.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iecampus.activity.R;
import com.iecampus.moldel.Reply;

public class CommentReplyAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ViewHolder viewHolder;
	private OnClickListener replyToReplyListener;
	private int parentPosition = -1;
	private boolean isClickCheckAll = false;
	private List<Reply> replyList;

	public CommentReplyAdapter(Context context, List<Reply> replyList,
			int parentPosition, OnClickListener replyToReplyListener) {
		this.inflater = LayoutInflater.from(context);
		this.parentPosition = parentPosition;
		this.replyToReplyListener = replyToReplyListener;
		this.replyList = replyList;
	}

	@Override
	public int getCount() {
		return replyList.size();
	}

	public void clearList() {
		this.replyList.clear();
	}

	public void updateList(List<Reply> replyList) {
		this.replyList.addAll(replyList);
	}

	@Override
	public Reply getItem(int position) {
		return replyList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_comment_reply, null);
			viewHolder = new ViewHolder();
			viewHolder.tv_comment_reply_writer_and_text = (TextView) convertView
					.findViewById(R.id.tv_comment_reply_writer_and_text);
			// comment_rest
			viewHolder.rl_comment_check_all = (RelativeLayout) convertView
					.findViewById(R.id.rl_comment_check_all);
			// comment_rest_text
			viewHolder.tv_comment_check_all = (TextView) convertView
					.findViewById(R.id.tv_comment_check_all);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();

		}
		Reply reply = getItem(position);
		// viewHolder.tv_comment_reply_text.setText(reply.getContent());
		// 判断是回复楼主或者回复其他人
		if (TextUtils.isEmpty(reply.getReplyTo())) {
			String text = reply.getUsername() + "：" + reply.getContent();
			int usernameStart = reply.getUsername().length() + 1;
			SpannableStringBuilder ss = new SpannableStringBuilder(text);
			ss.setSpan(new ForegroundColorSpan(Color.parseColor("#5A5A5A")),
					usernameStart, usernameStart + reply.getContent().length(),
					Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			viewHolder.tv_comment_reply_writer_and_text.setText(ss);
		} else {
			int usernameStart = reply.getUsername().length();
			String text = reply.getUsername() + " 回复 " + reply.getReplyTo()
					+ "：" + reply.getContent();
			int labelReplyStart = text.length() - reply.getContent().length();
			SpannableStringBuilder ss = new SpannableStringBuilder(text);
			// 设置指定位置文字的背景颜色（将“回复”设置成灰色）
			ss.setSpan(new ForegroundColorSpan(Color.parseColor("#5A5A5A")),
					usernameStart, usernameStart + 3,
					Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			ss.setSpan(new ForegroundColorSpan(Color.parseColor("#5A5A5A")),
					labelReplyStart, labelReplyStart
							+ reply.getContent().length(),
					Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			viewHolder.tv_comment_reply_writer_and_text.setText(ss);
		}
		// 记录发表回复时，对应的position,用于确定所回复的对象，如果是楼中楼，还需记录父节点的position
		convertView.setTag(R.id.tag_first, parentPosition);
		convertView.setTag(R.id.tag_second, position);
		convertView.setOnClickListener(replyToReplyListener);
		hiddenAndShowComments(position, viewHolder.tv_comment_reply_writer_and_text,
				viewHolder.rl_comment_check_all,
				viewHolder.tv_comment_check_all);
		return convertView;
	}
	
	/**
	 * 隐藏和查看全部评论
	 * @param position
	 * @param tv_comment_reply_writer_and_text
	 * @param rl_comment_check_all
	 * @param tv_comment_check_all
	 */
	private void hiddenAndShowComments(int position, final TextView tv_comment_reply_writer_and_text,
			final RelativeLayout rl_comment_check_all, final TextView tv_comment_check_all) {
		if (replyList.size() > 0) {
			tv_comment_reply_writer_and_text.setVisibility(View.VISIBLE);
			//当回复数量>3时，显示第一条回复，并显示“查看全部回复”按钮，监听点击事件
			if (replyList.size() > 3 && position == 0) {
				rl_comment_check_all.setVisibility(View.VISIBLE);
				tv_comment_check_all.setText("查看全部回复(" + (replyList.size() - 3) + ")");
				rl_comment_check_all.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						isClickCheckAll = true;
						rl_comment_check_all.setVisibility(View.GONE);
						tv_comment_reply_writer_and_text.setVisibility(View.VISIBLE);
					}
				});
				//将除了第一条回复和最后两条回复之外的其他回复隐藏掉，不进行显示 
			} else if (position > 0 && position < replyList.size() - 2) {
				tv_comment_reply_writer_and_text.setVisibility(View.GONE);
				rl_comment_check_all.setVisibility(View.GONE);
			}
		}
		//当“查看全部回复”按钮被点击后，显示全部回复，隐藏“查看全部回复”按钮
		if (isClickCheckAll == true) {
			tv_comment_reply_writer_and_text.setVisibility(View.VISIBLE);
			rl_comment_check_all.setVisibility(View.GONE);
		}
	}

	public class ViewHolder {
		private TextView tv_comment_reply_writer_and_text; // 评论者昵称和内容
		private RelativeLayout rl_comment_check_all;       //查看全部评论的布局
		private TextView tv_comment_check_all;             //查看全部评论的文本
	}
}
