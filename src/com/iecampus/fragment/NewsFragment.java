package com.iecampus.fragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.iecampus.activity.NewsDetailActivity;
import com.iecampus.activity.R;
import com.iecampus.adapter.NewsAdapter;
import com.iecampus.moldel.NewsEntity;
import com.iecampus.utils.JsonArrayRequests;
import com.iecampus.utils.JsonUtil;
import com.iecampus.utils.ToastUtil;
import com.iecampus.utils.VolleyUtil;
import com.iecampus.view.HeadListView;
import com.iecampus.view.MyProgressDialog;
import com.iecampus.view.PullToRefreshView;
import com.iecampus.view.PullToRefreshView.OnFooterRefreshListener;
import com.iecampus.view.PullToRefreshView.OnHeaderRefreshListener;

/**
 * 新闻列表页面Fragment
 * 
 * @author lfh
 * 
 */
@SuppressLint("ValidFragment")
public class NewsFragment extends Fragment implements OnHeaderRefreshListener,
		OnFooterRefreshListener {
	/** 当前Fragment绑定的Activity */
	private Activity activity;
	/** 初始化请求服务器返回的新闻信息集合 */
	private List<NewsEntity> newsList = null;
	/** 上下拉刷新请求服务器返回的新闻信息集合 */
	private List<NewsEntity> list = null;
	/** 自定义带时间条的新闻列表 */
	private HeadListView newsListView;
	/** 新闻列表适配器 */
	private NewsAdapter newsAdapter;
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
	/** 自定义上下拉刷新的View */
	private PullToRefreshView mPullToRefreshView;
	/** 新闻集合的实体类型 */
	private Type type = new TypeToken<List<NewsEntity>>() {
	}.getType();
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
	private boolean isDetached = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		this.activity = activity;
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
		Log.i("test", "NewsFragment-----onCreateView");

		if (rootView != null) {
			ViewGroup parent = (ViewGroup) rootView.getParent();
			if (parent != null) {
				parent.removeView(rootView);
			}
			return rootView;
		}

		activity = getActivity();
		progressDialog = new MyProgressDialog(activity);
		newsList = new ArrayList<NewsEntity>();

		rootView = LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_news, null);

		newsListView = (HeadListView) rootView.findViewById(R.id.mListView);
		btn_refresh = (ImageView) rootView.findViewById(R.id.btn_refresh);

		notify_view = (RelativeLayout) rootView.findViewById(R.id.notify_view);
		notify_view_text = (TextView) rootView
				.findViewById(R.id.notify_view_text);
		mPullToRefreshView = (PullToRefreshView) rootView
				.findViewById(R.id.main_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);

		url = activity.getString(R.string.hostAddress)
				+ "news_getAllNews?&currentpage=" + currentpage + "&pagesize="
				+ pagesize;
		
		Log.i("test", "url===="+url);

		btn_refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				requestData();
			}
		});

		requestData();
		
		newsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(activity,
						NewsDetailActivity.class);
				intent.putExtra("content", newsList.get(position).getContent());
				intent.putExtra("time", newsList.get(position).getPublishTime());
				intent.putExtra("title", newsList.get(position).getTitle());
				intent.putExtra("imglist", newsList.get(position).getPicList());
				startActivity(intent);
				activity.overridePendingTransition(
						R.anim.slide_in_right, R.anim.slide_out_left);
			}
		});

		return rootView;
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
		JsonArrayRequests request = new JsonArrayRequests(url,
				new Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {
						newsList.clear();
						newsList = JsonUtil.json2List(response.toString(), type);
						newsAdapter = new NewsAdapter(activity, newsList);
						newsListView.setAdapter(newsAdapter);
						progressDialog.dismiss();
						currentpage++;
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						if (isDetached) {
							return;
						}
						Log.e("test", arg0.toString());
						ToastUtil.showToast(getActivity(), getResources()
								.getString(R.string.failedconnection));
						btn_refresh.setVisibility(View.VISIBLE);
						progressDialog.dismiss();
					}
				});
		// 请求加上Tag,用于取消请求
		request.setTag(this);
		VolleyUtil.getQueue(getActivity()).add(request);
	}

	/**
	 * 定义一个Handler对象 用来处理不同的Message
	 */
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SUCCEED:
				btn_refresh.setVisibility(View.GONE);
				newsAdapter = new NewsAdapter(activity, newsList);
				newsListView.setAdapter(newsAdapter);
				newsListView.setOnScrollListener(newsAdapter);
				newsListView.setPinnedHeaderView(LayoutInflater.from(activity)
						.inflate(R.layout.item_news_date, newsListView, false));
				newsListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Intent intent = new Intent(activity,
								NewsDetailActivity.class);
						intent.putExtra("news_url",
								newsAdapter.getItem(position).getLink());
						startActivity(intent);
						activity.overridePendingTransition(
								R.anim.slide_in_right, R.anim.slide_out_left);
					}
				});
				progressDialog.dismiss();
				break;
			case FAILURE:
				btn_refresh.setVisibility(View.VISIBLE);
				progressDialog.dismiss();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	/**
	 * 弹出更新成功提示框
	 * 
	 * @param num
	 *            更新的新闻数目
	 */
	private void initNotify(final int num) {
		// handler.post(r);是把r加到消息队列，但并未开辟新线程。等到消息被取出时才执行。
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				notify_view_text.setText(String.format(
						getString(R.string.notify_news_update), num));
				notify_view.setVisibility(View.VISIBLE);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						notify_view.setVisibility(View.GONE);
					}
				}, 2000);
			}
		}, 1000);
	}

	/**
	 * 刷新数据
	 * 
	 * @param currentpage
	 *            当前页码
	 */
	private void refreshData() {
		url = activity.getString(R.string.hostAddress)
				+ "news_getAllNews?&currentpage=" + currentpage + "&pagesize="
				+ pagesize;
		list = new ArrayList<NewsEntity>();
		JsonArrayRequests request = new JsonArrayRequests(url,
				new Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {
						list.clear();
						list = JsonUtil.json2List(response.toString(), type);
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						if (isDetached) {
							return;
						}
						ToastUtil.showToast(getActivity(), getResources()
								.getString(R.string.failedconnection));
					}
				});
		// 请求加上Tag,用于取消请求
		request.setTag(this);
		VolleyUtil.getQueue(activity).add(request);
	}

	/**
	 * 上拉加载更多监听
	 */
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		Log.i("test", "currentpage===" + currentpage);
		Log.i("test", "url===" + url);
		refreshData();
		// 在主线程中更新UI
		mPullToRefreshView.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (list != null && list.size() != 0) {
					newsList.addAll(list);
					newsAdapter.notifyDataSetChanged();
					newsListView
							.setSelection(currentpage * pagesize - pagesize);
					currentpage++;
				} else {
					ToastUtil.showToast(activity, "没有更多数据了");
				}
				mPullToRefreshView.onFooterRefreshComplete();
			}
		}, 1000);
	}

	/**
	 * 下拉刷新监听
	 */
	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		currentpage = 1;
		refreshData();
		// 在主线程中更新UI
		mPullToRefreshView.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (list != null && list.size() != 0) {
					int num1 = newsList.get(0).getId();
					int num = list.get(0).getId() - num1;
					initNotify(num);

					newsList.clear();
					newsList.addAll(list);
					newsAdapter.notifyDataSetChanged();
					currentpage = 2;
				} else {
					ToastUtil.showToast(activity, "数据更新失败，请检查网络！");
				}
				mPullToRefreshView.onHeaderRefreshComplete();
			}
		}, 1000);
	}

	/**
	 * 摧毁视图
	 */
	@Override
	public void onDestroyView() {
	//	newsAdapter = null;
		VolleyUtil.getQueue(getActivity()).cancelAll(this);
		super.onDestroyView();
		Log.i("test", "NewsFragment----onDestroyView");
	}

	/**
	 * 摧毁该Fragment，一般是FragmentActivity 被摧毁的时候伴随着摧毁
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
