package com.iecampus.fragment;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.iecampus.activity.AddTopicActivity;
import com.iecampus.activity.R;
import com.iecampus.adapter.TestSectionedAdapter;
import com.iecampus.moldel.Topic;
import com.iecampus.moldel.TopicReply;
import com.iecampus.utils.HttpUtils;
import com.iecampus.utils.JsonUtil;
import com.iecampus.utils.ToastUtil;
import com.iecampus.view.MyProgressDialog;
import com.iecampus.view.PinnedHeaderListView;
import com.iecampus.view.PullToRefreshView;
import com.iecampus.view.PullToRefreshView.OnFooterRefreshListener;
import com.iecampus.view.PullToRefreshView.OnHeaderRefreshListener;

public class HiFragment extends Fragment implements OnFooterRefreshListener,
		OnHeaderRefreshListener, OnClickListener {
	private Activity activity;
	private List<Topic> topicList = null;
	private List<TopicReply> topicReplyList = null;

	private List<Topic> tempTopicList = null;
	private List<TopicReply> tempTopicReplyList = null;

	private PullToRefreshView mPullToRefreshView; // 自定义上下拉刷新的View
	private Type topicType = new TypeToken<List<Topic>>() {
	}.getType();
	private Type topicReplyType = new TypeToken<List<TopicReply>>() {
	}.getType();
	private PinnedHeaderListView listView;
	/** 加载数据失败后出现的刷新按钮 */
	private ImageView btn_refresh;
	/** 网络请求成功的标识 */
	public final static int SUCCEED = 0;
	/** 网络请求失败的标识 */
	public final static int FAILURE = 1;
	/** 弹出更新成功提示框的布局 */
	private RelativeLayout notify_view;
	/** 弹出更新数目的文本框 */
	private TextView notify_view_text;
	/** 请求网络的进度条 */
	private MyProgressDialog progressDialog;
	/** 缓存Fragment view */
	private View rootView;
	/** 当前页码 */
	private int currentpage = 1;
	/** 一页的数据量 */
	private int pagesize = 10;
	/** 请求数据的URL地址 */
	private String url;
	String[][] comments = null;
	private ImageView add_topic;
	private boolean isDetached = true;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		isDetached = false;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		isDetached = true;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("test", "HiFragment----onCreateView");

		if (rootView != null) {
			ViewGroup parent = (ViewGroup) rootView.getParent();
			if (parent != null) {
				parent.removeView(rootView);
			}
			return rootView;
		}

		activity = getActivity();
		progressDialog = new MyProgressDialog(activity);
		topicList = new ArrayList<Topic>();
		topicReplyList = new ArrayList<TopicReply>();

		rootView = LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_hi, null);
		add_topic = (ImageView) rootView.findViewById(R.id.add_topic);
		listView = (PinnedHeaderListView) rootView
				.findViewById(R.id.pinnedListView);
		btn_refresh = (ImageView) rootView.findViewById(R.id.btn_refresh);

		notify_view = (RelativeLayout) rootView.findViewById(R.id.notify_view);
		notify_view_text = (TextView) rootView
				.findViewById(R.id.notify_view_text);
		mPullToRefreshView = (PullToRefreshView) rootView
				.findViewById(R.id.main_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		IntentFilter filter = new IntentFilter("com.ie.update");
		getActivity().registerReceiver(broadreceiver, filter);

		url = activity.getString(R.string.hostAddress)
				+ "topic_gettopics?currentpage=" + currentpage + "&pagesize="
				+ pagesize;

		Log.i("test", "url====" + url);
		add_topic.setOnClickListener(this);
		btn_refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				requestData();
			}
		});

		requestData();

		return rootView;

	}

	private void analysisData(List<Topic> topicList,
			List<TopicReply> topReplyList) {
		comments = new String[topicList.size()][60];
		for (int i = 0; i < topicList.size(); i++) {
			for (int j = 0; j < 60; j++) {
				comments[i][j] = "#";
			}
		}
		int k = 0;
		for (int m = 0; m < topicList.size(); m++) {
			k = 0;
			for (int n = 0; n < topReplyList.size(); n++) {
				if (topicList.get(m).getTid() == topReplyList.get(n).getTid()) {
					// Log.i("test", "m========" + m + "  k" + k);
					comments[m][k++] = topReplyList.get(n).getContent();
				}
			}
		}
	}

	/**
	 * 初始化数据 从服务器获取数据并缓存
	 */
	private void requestData() {
		btn_refresh.setVisibility(View.GONE);
		if (!isDetached) {
			progressDialog.show();
		}
		currentpage = 1;

		AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				topicList.clear();
				topicReplyList.clear();
				String json;
				try {
					json = HttpUtils.getJsonContent(url);
				} catch (SocketTimeoutException e) {
					return "failed";
				} catch (IOException e) {
					return "failed";
				}
				Log.i("test", "json===" + json);

				String jsonArray[] = json.split("\\*\\*\\#\\#");
				topicList = JsonUtil.json2List(jsonArray[0], topicType);
				topicReplyList = JsonUtil.json2List(jsonArray[1],
						topicReplyType);
				analysisData(topicList, topicReplyList);
				currentpage++;
				progressDialog.dismiss();
				return "success";
			}

			@Override
			protected void onPostExecute(String result) {
				if (isDetached) {
					return;
				}
				
				if (result != "success") {
					ToastUtil.showToast(getActivity(), getResources()
							.getString(R.string.failedconnection));
					btn_refresh.setVisibility(View.VISIBLE);
				}
				TestSectionedAdapter sectionedAdapter = new TestSectionedAdapter(
						topicList, comments, getActivity());
				listView.setAdapter(sectionedAdapter);
				progressDialog.dismiss();

			}

		};
		task.execute();

	}

	private void refreshData() {
		url = activity.getString(R.string.hostAddress)
				+ "topic_gettopics?currentpage=" + currentpage + "&pagesize="
				+ pagesize;
		Log.i("test", "currentpage===" + currentpage);
		Log.i("test", "url===" + url);
		tempTopicList = new ArrayList<Topic>();
		tempTopicReplyList = new ArrayList<TopicReply>();
		AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				tempTopicList.clear();
				tempTopicReplyList.clear();
				String json;
				try {
					json = HttpUtils.getJsonContent(url);
				} catch (SocketTimeoutException e) {
					return "timeout";
				} catch (IOException e) {
					return "noresult";
				}
				Log.i("test", "json===" + json);
				if (json.startsWith("<html>") || json.startsWith("false")) {
					return "noresult";
				}
				String jsonArray[] = json.split("\\*\\*\\#\\#");
				tempTopicList = JsonUtil.json2List(jsonArray[0], topicType);
				tempTopicReplyList = JsonUtil.json2List(jsonArray[1],
						topicReplyType);
				return "success";
			}

			@Override
			protected void onPostExecute(String result) {
				if (isDetached) {
					return;
				}
				
				if (result.equals("noresult")) {
					ToastUtil.showToast(getActivity(), "没有更多数据了");
				} else if (result.equals("timeout")) {
					ToastUtil.showToast(getActivity(),
							getString(R.string.failedconnection));
				}

			}

		};
		task.execute();
	}

	/**
	 * 上拉加载更多监听
	 */
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		refreshData();
		mPullToRefreshView.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (tempTopicList != null && tempTopicList.size() != 0) {
					topicList.addAll(tempTopicList);
					topicReplyList.addAll(tempTopicReplyList);
					analysisData(topicList, topicReplyList);
					TestSectionedAdapter sectionedAdapter = new TestSectionedAdapter(
							topicList, comments, getActivity());
					listView.setAdapter(sectionedAdapter);
					// Log.i("test",
					// "topicReplyList.size==="+topicReplyList.size());
					// Log.i("test",
					// "section===="+tempTopicList.size()+topicReplyList.size());
					listView.setSelection(currentpage * pagesize * 3 - pagesize
							* 3);

					// listView.setSelection(tempTopicList.size()+topicReplyList.size());
					currentpage++;
				}
				mPullToRefreshView.onFooterRefreshComplete();
			}
		}, 2000);

	}

	/**
	 * 下拉刷新监听
	 */
	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		currentpage = 1;
		refreshData();
		mPullToRefreshView.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (tempTopicList != null && tempTopicList.size() != 0) {
					topicList.clear();
					topicList.addAll(tempTopicList);
					topicReplyList.clear();
					topicReplyList.addAll(tempTopicReplyList);
					analysisData(topicList, topicReplyList);
					TestSectionedAdapter sectionedAdapter = new TestSectionedAdapter(
							topicList, comments, getActivity());
					listView.setAdapter(sectionedAdapter);
					currentpage = 2;
				}
				mPullToRefreshView.onHeaderRefreshComplete();
			}
		}, 2000);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.i("test", "HiFragment----onDestroyView");
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(broadreceiver);
		super.onDestroy();
		Log.i("test", "HiFragment----onDestroy");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_topic:
			startActivityForResult((new Intent(getActivity(),
					AddTopicActivity.class)), 1001);
			break;

		default:
			break;
		}

	}

	public  BroadcastReceiver broadreceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("com.ie.update")){
				System.out.println("广播中");
				requestData();
			}
			
		}
	};
}
