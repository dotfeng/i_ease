package com.iecampus.fragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.iecampus.activity.GoodsDetailActivity;
import com.iecampus.activity.R;
import com.iecampus.adapter.CollectionGoodsAdapter;
import com.iecampus.moldel.Goods;
import com.iecampus.utils.Constants;
import com.iecampus.utils.JsonArrayRequests;
import com.iecampus.utils.JsonUtil;
import com.iecampus.utils.PreferenceUtils;
import com.iecampus.utils.ToastUtil;
import com.iecampus.utils.VolleyUtil;
import com.iecampus.view.MyProgressDialog;
import com.iecampus.view.PullToRefreshView;
import com.iecampus.view.PullToRefreshView.OnFooterRefreshListener;

public class CollectionGoodsFragment extends Fragment implements
		OnFooterRefreshListener {
	private Activity activity;
	private View rootView = null;
	/** 初始化请求服务器返回的收藏信息集合 */
	private List<Goods> goodsList = null;
	/** 上拉加载更多请求服务器返回的收藏信息集合 */
	private List<Goods> list = null;
	private CollectionGoodsAdapter adapter;
	/** 自定义上下拉刷新的View */
	private PullToRefreshView mPullToRefreshView;
	private ListView goodsListView;
	private MyProgressDialog progressDialog;
	/** 加载数据失败后出现的刷新按钮 */
	private ImageView btn_refresh;
	private Type type = new TypeToken<List<Goods>>() {
	}.getType();
	/** 当前页码 */
	private int currentpage = 1;
	/** 一页的数据量 */
	private int pagesize = 10;
	/** 请求数据的URL地址 */
	private String url;
	private String collectionType;

	private int uid;
	private boolean isDetached = true;

	public CollectionGoodsFragment() {

	}

	public CollectionGoodsFragment(Activity activity, String collectionType) {
		this.activity = activity;
		this.collectionType = collectionType;
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

		if (rootView != null) {
			ViewGroup parent = (ViewGroup) rootView.getParent();
			if (parent != null) {
				parent.removeView(rootView);
			}
			return rootView;
		}

		activity = getActivity();
		progressDialog = new MyProgressDialog(activity);
		goodsList = new ArrayList<Goods>();

		rootView = inflater.inflate(R.layout.fragment_collection_goods, null);
		goodsListView = (ListView) rootView.findViewById(R.id.goodsListView);
		btn_refresh = (ImageView) rootView.findViewById(R.id.btn_refresh);
		mPullToRefreshView = (PullToRefreshView) rootView
				.findViewById(R.id.main_pull_refresh_view);
		mPullToRefreshView.PULL_DOWN_ENABLE = 0;
		mPullToRefreshView.setOnFooterRefreshListener(this);

		uid = PreferenceUtils.getPrefInt(getActivity(), Constants.USERID, 1);
		url = activity.getString(R.string.hostAddress)
				+ "collection_getCollectionList?type=" + collectionType
				+ "&co_uid=" + uid + "&currentpage=" + currentpage
				+ "&pagesize=" + pagesize;

		Log.i("test", "url====" + url);

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
				Intent intent = new Intent(activity, GoodsDetailActivity.class);
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
				activity.overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		});
		requestData();
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
						Log.i("test", "json===" + response.toString());
						goodsList.clear();
						goodsList = JsonUtil.json2List(response.toString(),
								type);
						adapter = new CollectionGoodsAdapter(goodsList,
								activity);
						goodsListView.setAdapter(adapter);
						progressDialog.dismiss();
						currentpage++;
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						Log.e("test", arg0.toString());
						if (isDetached) {
							return;
						}
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
	 * 刷新数据
	 * 
	 * @param currentpage
	 *            当前页码
	 */
	private void refreshData() {
		url = activity.getString(R.string.hostAddress)
				+ "collection_getCollectionList?type=" + collectionType
				+ "&co_uid=" + uid + "&currentpage=" + currentpage
				+ "&pagesize=" + pagesize;
		list = new ArrayList<Goods>();
		JsonArrayRequests request = new JsonArrayRequests(url,
				new Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {
						Log.i("test", "json===" + response.toString());
						list.clear();
						list = JsonUtil.json2List(response.toString(), type);
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
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
					goodsList.addAll(list);
					adapter.notifyDataSetChanged();
					goodsListView.setSelection(currentpage * pagesize
							- pagesize);
					currentpage++;
				} else {
					ToastUtil.showToast(activity, "没有更多数据了");
				}
				mPullToRefreshView.onFooterRefreshComplete();
			}
		}, 1000);
	}

	/**
	 * 摧毁视图
	 */
	@Override
	public void onDestroyView() {
		// newsAdapter = null;
		VolleyUtil.getQueue(getActivity()).cancelAll(this);
		super.onDestroyView();
		Log.i("test", "CollectionGoodsFragment----onDestroyView");
	}
}
