package com.iecampus.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.iecampus.activity.R;
import com.iecampus.moldel.Comment;
import com.iecampus.utils.LruImageCache;
import com.iecampus.utils.StringUtil;
import com.iecampus.utils.VolleyUtil;
import com.iecampus.view.MyListView;

public class CommentAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ViewHolder viewHolder;
	private Context context;
	private OnClickListener replyToCommentListener;
	private OnClickListener replyToReplyListener;
	private List<Comment> commentList;
	private ImageLoader imageLoader;

	public CommentAdapter(Context context, List<Comment> commentList,
			OnClickListener replyToCommentListener,
			OnClickListener replyToReplyListener) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.commentList = new ArrayList<Comment>();
		this.commentList.addAll(commentList);
		this.replyToCommentListener = replyToCommentListener;
		this.replyToReplyListener = replyToReplyListener;
		this.imageLoader = new ImageLoader(VolleyUtil.getQueue(context),
				new LruImageCache());
	}

	public void clearList() {
		this.commentList.clear();
	}

	public void updateList(List<Comment> commentList) {
		this.commentList.addAll(commentList);
	}

	@Override
	public int getCount() {
		return commentList.size();
	}

	@Override
	public Comment getItem(int position) {
		return commentList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_comment, null);
			viewHolder = new ViewHolder();
			viewHolder.iv_comment_headpicture = (ImageView) convertView
					.findViewById(R.id.iv_comment_headpicture);
			viewHolder.tv_comment_username = (TextView) convertView
					.findViewById(R.id.tv_comment_username);
			viewHolder.tv_comment_datetime = (TextView) convertView
					.findViewById(R.id.tv_comment_datetime);
			viewHolder.tv_comment_content = (TextView) convertView
					.findViewById(R.id.tv_comment_content);
			viewHolder.comment_reply_divider_line = convertView
					.findViewById(R.id.comment_reply_divider_line);
			viewHolder.btn_comment_reply = (TextView) convertView
					.findViewById(R.id.tv_user_reply);
			viewHolder.lv_user_comment_replys = (MyListView) convertView
					.findViewById(R.id.lv_user_comment_replys);
			viewHolder.btn_comment_reply.setTag(position);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Comment comment = getItem(position);
		viewHolder.tv_comment_username.setText(comment.getUsername());
		viewHolder.tv_comment_content.setText(comment.getContent());
		viewHolder.tv_comment_datetime.setText(comment.getDatetime());
		Log.i("test","picture======"+comment.getHeadpicture() );
		if (!TextUtils.isEmpty(comment.getHeadpicture())) {
			this.setImage(viewHolder.iv_comment_headpicture,
					context.getString(R.string.hostAddress)+comment.getHeadpicture());
		} else {
			viewHolder.iv_comment_headpicture
					.setImageResource(R.drawable.shifan);
		}
		// 设置评论列表的点击效果透明
		viewHolder.lv_user_comment_replys.setSelector(new ColorDrawable(
				Color.TRANSPARENT));
		// 判断当前评论是否有回复
		if (commentList.get(position).getReplyList() != null
				&& commentList.get(position).getReplyList().size() != 0) {
			viewHolder.comment_reply_divider_line.setVisibility(View.VISIBLE);
			viewHolder.lv_user_comment_replys
					.setAdapter(new CommentReplyAdapter(context, commentList
							.get(position).getReplyList(), position,
							replyToReplyListener));
		} else {
			viewHolder.comment_reply_divider_line.setVisibility(View.GONE);
			viewHolder.lv_user_comment_replys.setAdapter(null);
		}
		// 记录点击回复按钮时对应的position,用于确定所回复的对象
		viewHolder.btn_comment_reply.setTag(position);
		viewHolder.btn_comment_reply.setOnClickListener(replyToCommentListener);
		return convertView;
	}

	public class ViewHolder {
		private ImageView iv_comment_headpicture; // 评论者 头像
		private TextView tv_comment_username; // 评论者 昵称
		private TextView tv_comment_content; // 评论者 一级品论内容
		private TextView tv_comment_datetime; //
		private View comment_reply_divider_line;
		private TextView btn_comment_reply; // 评论者 二级评论按钮
		private MyListView lv_user_comment_replys; // 评论者 二级品论内容列表
	}

	void setImage(ImageView imageView, String url) {
		ImageContainer container;
		try {
			// 如果当前ImageView上存在请求，先取消
			if (imageView.getTag() != null) {
				container = (ImageContainer) imageView.getTag();
				container.cancelRequest();
			}
		} catch (Exception e) {

		}
		ImageListener listener = ImageLoader.getImageListener(imageView,
				R.drawable.shifan, R.drawable.shifan);
		container = imageLoader.get(StringUtil.preUrl(url), listener);
		// 在ImageView上存储当前请求的Container，用于取消请求
		imageView.setTag(container);
	}
}
