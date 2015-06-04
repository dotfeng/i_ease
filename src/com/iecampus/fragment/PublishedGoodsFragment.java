package com.iecampus.fragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.iecampus.activity.GoodsDetailActivity;
import com.iecampus.activity.R;
import com.iecampus.activity.TopicDetailActivity;
import com.iecampus.adapter.CollectionGoodsAdapter;
import com.iecampus.adapter.PublishedAdapter;
import com.iecampus.adapter.PublishedAdapter.PublishAction;
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

public class PublishedGoodsFragment extends Fragment implements
		OnFooterRefreshListener, PublishAction, OnClickListener {
	private Activity activity;
	private View rootView = null;
	/** 初始化请求服务器返回的收藏信息集合 */
	private List<Goods> goodsList = null;
	/** 上拉加载更多请求服务器返回的收藏信息集合 */
	private List<Goods> list = null;
	private PublishedAdapter adapter;
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
	private ImageView tag;
	private boolean isDetached = true;
	private PopupWindow popupWindow;
    private EditText title,price,detail,place;
    private int gid;
	public PublishedGoodsFragment() {

	}

	public PublishedGoodsFragment(Activity activity, String collectionType) {
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
				+ "goods_getGoodsByUid?type=" + collectionType + "&goods_uid="
				+ uid + "&current_page=" + currentpage + "&pagesize="
				+ pagesize;

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
						adapter = new PublishedAdapter(goodsList, activity,
								PublishedGoodsFragment.this);
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
	 * 刷新数据
	 * 
	 * @param currentpage
	 *            当前页码
	 */
	private void refreshData() {
		url = activity.getString(R.string.hostAddress)
				+ "goods_getGoodsByUid?type=" + collectionType + "&goods_uid="
				+ uid + "&current_page=" + currentpage + "&pagesize="
				+ pagesize;
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
					goodsList.addAll(list);
					adapter.notifyDataSetChanged();
					goodsListView.setSelection(currentpage * pagesize
							- pagesize);
					currentpage++;
				} else {
					if (isDetached) {
						return;
					}
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

	@Override
	public void OnDeleteClick(int gid,int position) {
		showPopupWindow(position);
		// DeleteGoods(gid);
	}

	private void updateGoods(int gid) {

		if (!isDetached) {
			progressDialog.show();
		}
		String url = getString(R.string.hostAddress) + "goods_updateGoods";
		AjaxParams params = new AjaxParams();
		params.put("goods_gid", gid + "");
		FinalHttp fh = new FinalHttp();
		fh.post(url, null, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String state) {
				if (isDetached) {
					return;
				}
				if (Boolean.parseBoolean(state)) {
					progressDialog.dismiss();
					requestData();
					int number_published = PreferenceUtils.getPrefInt(
							getActivity(), Constants.PUBLISHNUMBER, 0);
					PreferenceUtils.setPrefInt(getActivity(),
							Constants.PUBLISHNUMBER, --number_published);
					ToastUtil.showToast(getActivity(), "删除成功！");
				} else {
					progressDialog.dismiss();
					ToastUtil.showToast(getActivity(), "删除失败！");
				}

			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				if (isDetached) {
					return;
				}
				progressDialog.dismiss();
				ToastUtil.showToast(getActivity(), "删除失败！");
			}
		});

	}

	private void showPopupWindow(int position) {
		View view = (RelativeLayout) LayoutInflater.from(getActivity())
				.inflate(R.layout.activity_pubulished_update_popmenu, null);
		title = (EditText) view.findViewById(R.id.update_title);
		price = (EditText) view.findViewById(R.id.update_price);
		detail = (EditText) view.findViewById(R.id.update_detail);
		place = (EditText) view.findViewById(R.id.update_place);
		title.setText(goodsList.get(position).getGoods_name());
		price.setText(goodsList.get(position).getPrice()+"");
		detail.setText(goodsList.get(position).getDetail());
		place.setText(goodsList.get(position).getRequirements());
		Button pb = (Button) view.findViewById(R.id.update_publish);
		pb.setOnClickListener(this);
		if (popupWindow == null) {
			popupWindow = new PopupWindow(getActivity());
		}
		popupWindow.setFocusable(true); // 设置PopupWindow可获得焦点
		popupWindow.setTouchable(true); // 设置PopupWindow可触摸
		popupWindow.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
		popupWindow.setContentView(view);
		popupWindow.setWidth(LayoutParams.MATCH_PARENT); // 设置SelectPicPopupWindow弹出窗体的宽
		popupWindow.setHeight(LayoutParams.WRAP_CONTENT); // 设置SelectPicPopupWindow弹出窗体的高
		popupWindow.setAnimationStyle(R.style.AnimBottom); // 设置SelectPicPopupWindow弹出窗体动画效果

		ColorDrawable dw = new ColorDrawable(0xb0000000); // 实例化一个ColorDrawable颜色为半透明
		popupWindow.setBackgroundDrawable(dw); // 设置SelectPicPopupWindow弹出窗体的背景
		popupWindow.showAtLocation(
				getActivity().findViewById(R.id.publish_activity),
				Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
		popupWindow.update();
	}

	@Override
	public void OnCompleteClick(int gid) {
		if (!isDetached) {
			progressDialog.show();
		}
		String url = getString(R.string.hostAddress)
				+ "goods_goodsTag?goods_gid=" + gid;
		FinalHttp fh = new FinalHttp();
		fh.get(url, null, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String result) {
				if (isDetached) {
					return;
				}
				if (Boolean.parseBoolean(result)) {
					adapter = new PublishedAdapter(goodsList, getActivity(), PublishedGoodsFragment.this);
					goodsListView.setAdapter(adapter);
					adapter.notifyDataSetChanged();
					ToastUtil.showToast(getActivity(), "交易成功！");
				} else {
					ToastUtil.showToast(getActivity(), "标记失败！");
				}
				progressDialog.dismiss();
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				if (isDetached) {
					return;
				}
				ToastUtil.showToast(getActivity(), "标记失败！");
				progressDialog.dismiss();
			}

		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.update_publish:
		//	updateGoods(gid);
			break;

		default:
			break;
		}

	}
}
