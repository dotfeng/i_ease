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
	/** ��ǰFragment�󶨵�Activity */
	private Activity activity;
	/** ��Ʒ�б� */
	private GridView goodsListView;
	/** �Զ���������ˢ�µ�View */
	private PullToRefreshView mPullToRefreshView;
	/** ��Ʒ���͵ļ��ϵ�ʵ������ */
	private Type type;
	/** ��������Ľ����� */
	private MyProgressDialog progressDialog;
	/** ��Ʒ�б������� */
	private GoodsListAdapter adapter;
	/** ��ǰFragment�����ͣ����»������ */
	private String fragment_type;
	/** ��ʼ��������������ص���Ʒ��Ϣ���� */
	private List<Goods> goodsList = null;
	/** ������ˢ��������������ص���Ʒ��Ϣ���� */
	private List<Goods> list = null;
	/** ��������ʧ�ܺ���ֵ�ˢ�°�ť */
	private ImageView btn_refresh;
	/** ��������ɹ��ı�ʶ */
	public final static int SUCCEED = 0;
	/** ��������ʧ�ܵı�ʶ */
	public final static int FAILURE = 1;
	/** �������³ɹ���ʾ��Ĳ��� */
	private RelativeLayout notify_view;
	/** ����������Ŀ���ı��� */
	private TextView notify_view_text;
	/** ��ǰҳ�� */
	private int currentpage = 1;
	/** һҳ�������� */
	private int pagesize = 10;
	/** Gson������ */
	private Gson gson;
	/** ����Fragment view */
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
	 * ��ȡ��ǰFragment�����url
	 * 
	 * @return ����url
	 */
	private String getUrl() {
		String url = null;
		// ����һ��������ָ��
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
	 * ���ݳ�ʼ�����ӷ�������������
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
						ToastUtil.showToast(getActivity(), "��ȡ����ʧ��");
						progressDialog.dismiss();
						btn_refresh.setVisibility(View.VISIBLE);
					}
				});
		// �������Tag,����ȡ������
		request.setTag(this);
		VolleyUtil.getQueue(activity).add(request);

	}

	/**
	 * ˢ������
	 * 
	 * @param currentpage
	 *            ��ǰҳ��
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
		// �������Tag,����ȡ������
		request.setTag(this);
		VolleyUtil.getQueue(activity).add(request);
	}

	/**
	 * ����ˢ�¼���
	 */
	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		currentpage = 1;
		refreshData();
		// �����߳��и���UI
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
					ToastUtil.showToast(activity, "���ݸ���ʧ�ܣ��������磡");
				}
				mPullToRefreshView.onHeaderRefreshComplete();
			}
		}, 2000);
	}

	/**
	 * �������ظ������
	 */
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		Log.i("test", "currentpage===" + currentpage);
		refreshData();
		// �����߳��и���UI
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
					ToastUtil.showToast(activity, "û�и���������");
				}
				mPullToRefreshView.onFooterRefreshComplete();
			}
		}, 2000);
	}

	/**
	 * ����һ��Handler���� ��������ͬ��Message
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
	 * �������³ɹ���ʾ��
	 * 
	 * @param num
	 *            ���µ�������Ŀ
	 */
	private void initNotify(final int num) {
		// handler.post(r);�ǰ�r�ӵ���Ϣ���У�����δ�������̡߳��ȵ���Ϣ��ȡ��ʱ��ִ�С�
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
