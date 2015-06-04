package com.iecampus.fragment;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iecampus.activity.GoodsDetailActivity;
import com.iecampus.activity.R;
import com.iecampus.adapter.GoodsListAdapter;
import com.iecampus.moldel.Goods;
import com.iecampus.utils.JsonArrayRequests;
import com.iecampus.utils.JsonUtil;
import com.iecampus.utils.ToastUtil;
import com.iecampus.utils.VolleyUtil;
import com.iecampus.view.MyProgressDialog;
import com.iecampus.view.PullToRefreshView;
import com.iecampus.view.PullToRefreshView.OnFooterRefreshListener;
import com.iecampus.view.PullToRefreshView.OnHeaderRefreshListener;

public class GoodsFragment extends Fragment implements OnFooterRefreshListener,
		OnHeaderRefreshListener {
	/** 当前Fragment绑定的Activity */
	private Activity activity;
	/** 商品列表 */
	private GridView goodsListView;
	/** 自定义上下拉刷新的View */
	private PullToRefreshView mPullToRefreshView;
	/** 商品类型的集合的实体类型 */
	private Type type;
	/** 请求网络的进度条 */
	private MyProgressDialog progressDialog;
	/** 商品列表适配器 */
	private GoodsListAdapter adapter;
	/** 当前Fragment的类型，最新或者最火 */
	private String fragment_type;
	/** 初始化请求服务器返回的商品信息集合 */
	private List<Goods> goodsList = null;
	/** 上下拉刷新请求服务器返回的商品信息集合 */
	private List<Goods> list = null;
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
	/** 当前页码 */
	private int currentpage = 1;
	/** 一页的数据量 */
	private int pagesize = 10;
	/** Gson工具类 */
	private Gson gson;
	/** 缓存Fragment view */
	private View rootView;
	private boolean isDetached = true;
	
	public GoodsFragment() {

	}

	public GoodsFragment(Activity activity, String fragment_type) {
		this.activity = activity;
		this.fragment_type = fragment_type;
	}

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
		Log.i("test", "GoodsFragment----onCreateView");
		if (rootView != null) {
			ViewGroup parent = (ViewGroup) rootView.getParent();
			if (parent != null) {
				parent.removeView(rootView);
			}
			return rootView;
		}

		goodsList = new ArrayList<Goods>();
		type = new TypeToken<List<Goods>>() {
		}.getType();
		gson = new Gson();
		progressDialog = new MyProgressDialog(activity);

		rootView = inflater.inflate(R.layout.goods_fragment, null);
		goodsListView = (GridView) rootView.findViewById(R.id.mylist);
		btn_refresh = (ImageView) rootView.findViewById(R.id.btn_refresh);
		notify_view = (RelativeLayout) rootView.findViewById(R.id.notify_view);
		notify_view_text = (TextView) rootView
				.findViewById(R.id.notify_view_text);
		mPullToRefreshView = (PullToRefreshView) rootView
				.findViewById(R.id.main_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);

		btn_refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				requestData();
			}
		});
		
		goodsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Goods good = new Goods();
				good = goodsList.get(position);
				Intent intent = new Intent(activity,
						GoodsDetailActivity.class);
				intent.putExtra("price", good.getPrice());
				intent.putExtra("title", good.getGoods_name());
				intent.putExtra("place", good.getRequirements());
				intent.putExtra("detail", good.getDetail());
				intent.putExtra("uid", good.getUid());
				intent.putExtra("gid", good.getGid());
				intent.putExtra("isgoods", good.getIsgoods());
				intent.putExtra("publish_time", good.getDate());
				intent.putExtra("pathList", good.getGoods_imagepath());
				activity.startActivity(intent);
				activity.overridePendingTransition(
						R.anim.slide_in_right, R.anim.slide_out_left);
			}
		});

		requestData();

		return rootView;
	}

	/**
	 * 获取当前Fragment请求的url
	 * 
	 * @return 返回url
	 */
	private String getUrl() {
		String url = null;
		// 下面一行曾报空指针
		if (!TextUtils.isEmpty(fragment_type)&&fragment_type.startsWith("newest?type")
				|| fragment_type.startsWith("mostpopular?type")) {
			url = activity.getString(R.string.hostAddress) + "goods_"
					+ fragment_type + "&current_page=" + currentpage
					+ "&pagesize=" + pagesize;
		} else {
			try {
				fragment_type = new String(fragment_type.getBytes("UTF-8"),
						"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			url = activity.getString(R.string.hostAddress) + "goods_"
					+ fragment_type + "&current_page=" + currentpage
					+ "&pagesize=" + pagesize;
		}
		Log.i("test", "url========" + url);
		return url;
	}

	/**
	 * 数据初始化，从服务器请求数据
	 */
	private void requestData() {
		btn_refresh.setVisibility(View.GONE);
		progressDialog.show();
		currentpage = 1;
		String url = getUrl();
		JsonArrayRequests request = new JsonArrayRequests(url,
				new Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {
						Log.i("text", response.toString());
						goodsList = JsonUtil.json2List(response.toString(),
								type);
						adapter = new GoodsListAdapter(goodsList, activity);
						goodsListView.setAdapter(adapter);
						progressDialog.dismiss();
						currentpage++;
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						if (isDetached) {
							return;
						}
						ToastUtil.showToast(getActivity(), "获取数据失败");
						progressDialog.dismiss();
						btn_refresh.setVisibility(View.VISIBLE);
					}
				});
		// 请求加上Tag,用于取消请求
		request.setTag(this);
		VolleyUtil.getQueue(activity).add(request);

	}

	/**
	 * 刷新数据
	 * 
	 * @param currentpage
	 *            当前页码
	 */
	private void refreshData() {
		list = new ArrayList<Goods>();
		String url = getUrl();
		Log.i("test", url);
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
					int num1 = goodsList.get(0).getGid();
					int num = list.get(0).getGid() - num1;
					initNotify(num);

					goodsList.clear();
					goodsList.addAll(list);
					adapter.notifyDataSetChanged();
					currentpage = 2;
				} else {
					ToastUtil.showToast(activity, "数据更新失败，请检查网络！");
				}
				mPullToRefreshView.onHeaderRefreshComplete();
			}
		}, 2000);
	}

	/**
	 * 上拉加载更多监听
	 */
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		Log.i("test", "currentpage===" + currentpage);
		refreshData();
		// 在主线程中更新UI
		mPullToRefreshView.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (list != null && list.size() != 0) {
					goodsList.addAll(list);
					adapter.notifyDataSetChanged();
					goodsListView.setSelection(currentpage * pagesize / 2
							- pagesize / 2);
					currentpage++;
				} else {
					ToastUtil.showToast(activity, "没有更多数据了");
				}
				mPullToRefreshView.onFooterRefreshComplete();
			}
		}, 2000);
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
				adapter = new GoodsListAdapter(goodsList, activity);
				goodsListView.setAdapter(adapter);
				goodsListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Goods good = new Goods();
						good = list.get(position);
						Intent intent = new Intent(activity,
								GoodsDetailActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("price", good.getPrice());
						intent.putExtra("title", good.getGoods_name());
						intent.putExtra("place", good.getRequirements());
						intent.putExtra("detail", good.getDetail());
						intent.putExtra("pathList", good.getGoods_imagepath());
						activity.startActivity(intent);
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

	@Override
	public void onDestroyView() {
		VolleyUtil.getQueue(getActivity()).cancelAll(this);
		super.onDestroyView();
		Log.i("test", "GoodsFragment----onDestroyView");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("test", "GoodsFragment----onDestroy");
	}
}
