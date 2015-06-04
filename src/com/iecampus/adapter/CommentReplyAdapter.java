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
		// �ж��ǻظ�¥�����߻ظ�������
		if (TextUtils.isEmpty(reply.getReplyTo())) {
			String text = reply.getUsername() + "��" + reply.getContent();
			int usernameStart = reply.getUsername().length() + 1;
			SpannableStringBuilder ss = new SpannableStringBuilder(text);
			ss.setSpan(new ForegroundColorSpan(Color.parseColor("#5A5A5A")),
					usernameStart, usernameStart + reply.getContent().length(),
					Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			viewHolder.tv_comment_reply_writer_and_text.setText(ss);
		} else {
			int usernameStart = reply.getUsername().length();
			String text = reply.getUsername() + " �ظ� " + reply.getReplyTo()
					+ "��" + reply.getContent();
			int labelReplyStart = text.length() - reply.getContent().length();
			SpannableStringBuilder ss = new SpannableStringBuilder(text);
			// ����ָ��λ�����ֵı�����ɫ�������ظ������óɻ�ɫ��
			ss.setSpan(new ForegroundColorSpan(Color.parseColor("#5A5A5A")),
					usernameStart, usernameStart + 3,
					Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			ss.setSpan(new ForegroundColorSpan(Color.parseColor("#5A5A5A")),
					labelReplyStart, labelReplyStart
							+ reply.getContent().length(),
					Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			viewHolder.tv_comment_reply_writer_and_text.setText(ss);
		}
		// ��¼����ظ�ʱ����Ӧ��position,����ȷ�����ظ��Ķ��������¥��¥�������¼���ڵ��position
		convertView.setTag(R.id.tag_first, parentPosition);
		convertView.setTag(R.id.tag_second, position);
		convertView.setOnClickListener(replyToReplyListener);
		hiddenAndShowComments(position, viewHolder.tv_comment_reply_writer_and_text,
				viewHolder.rl_comment_check_all,
				viewHolder.tv_comment_check_all);
		return convertView;
	}
	
	/**
	 * ���غͲ鿴ȫ������
	 * @param position
	 * @param tv_comment_reply_writer_and_text
	 * @param rl_comment_check_all
	 * @param tv_comment_check_all
	 */
	private void hiddenAndShowComments(int position, final TextView tv_comment_reply_writer_and_text,
			final RelativeLayout rl_comment_check_all, final TextView tv_comment_check_all) {
		if (replyList.size() > 0) {
			tv_comment_reply_writer_and_text.setVisibility(View.VISIBLE);
			//���ظ�����>3ʱ����ʾ��һ���ظ�������ʾ���鿴ȫ���ظ�����ť����������¼�
			if (replyList.size() > 3 && position == 0) {
				rl_comment_check_all.setVisibility(View.VISIBLE);
				tv_comment_check_all.setText("�鿴ȫ���ظ�(" + (replyList.size() - 3) + ")");
				rl_comment_check_all.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						isClickCheckAll = true;
						rl_comment_check_all.setVisibility(View.GONE);
						tv_comment_reply_writer_and_text.setVisibility(View.VISIBLE);
					}
				});
				//�����˵�һ���ظ�����������ظ�֮��������ظ����ص�����������ʾ 
			} else if (position > 0 && position < replyList.size() - 2) {
				tv_comment_reply_writer_and_text.setVisibility(View.GONE);
				rl_comment_check_all.setVisibility(View.GONE);
			}
		}
		//�����鿴ȫ���ظ�����ť���������ʾȫ���ظ������ء��鿴ȫ���ظ�����ť
		if (isClickCheckAll == true) {
			tv_comment_reply_writer_and_text.setVisibility(View.VISIBLE);
			rl_comment_check_all.setVisibility(View.GONE);
		}
	}

	public class ViewHolder {
		private TextView tv_comment_reply_writer_and_text; // �������ǳƺ�����
		private RelativeLayout rl_comment_check_all;       //�鿴ȫ�����۵Ĳ���
		private TextView tv_comment_check_all;             //�鿴ȫ�����۵��ı�
	}
}
