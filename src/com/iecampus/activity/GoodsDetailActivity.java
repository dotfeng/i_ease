package com.iecampus.activity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Instrumentation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.reflect.TypeToken;
import com.iecampus.adapter.CommentAdapter;
import com.iecampus.moldel.Comment;
import com.iecampus.moldel.NewsEntity;
import com.iecampus.moldel.Reply;
import com.iecampus.moldel.User;
import com.iecampus.utils.Constants;
import com.iecampus.utils.JsonArrayRequests;
import com.iecampus.utils.JsonUtil;
import com.iecampus.utils.PreferenceUtils;
import com.iecampus.utils.ToastUtil;
import com.iecampus.utils.UtilTools;
import com.iecampus.utils.VolleyUtil;
import com.iecampus.view.ImageCycleView;
import com.iecampus.view.PullToRefreshView;
import com.iecampus.view.ImageCycleView.ImageCycleViewListener;
import com.iecampus.view.MyProgressDialog;
import com.iecampus.view.PullToRefreshView.OnFooterRefreshListener;
import com.iecampus.view.PullToRefreshView.OnHeaderRefreshListener;
import com.tencent.tauth.Tencent;

public class GoodsDetailActivity extends Activity implements OnClickListener,
		OnFooterRefreshListener {
	private FinalBitmap fb;

	private ImageView back, share;

	private ImageCycleView imageCycleView;
	private ArrayList<String> advImageUrl = null;
	private ArrayList<String> advImageName = null;

	private TextView price, title, place, detail;// �۸񣬱��⣬���׵ص㣬����
	private TextView tv_username, tv_department, tv_time_publish;

	private ListView lv_user_comments;
	private Button btn_reply;
	private EditText edt_reply;
	private CommentAdapter commentAdapter;
	private static final int ONE_COMMENT_CODE = -1;
	private List<Comment> commentList;
	private List<Comment> list;
	private List<Reply> replyList;
	private Type type;

	/** �Զ���������ˢ�µ�View */
	private PullToRefreshView mPullToRefreshView;
	/** ��ǰҳ�� */
	private int currentpage = 1;
	/** һҳ�������� */
	private int pagesize = 100;
	/** �������ݵ�URL��ַ */
	private String url;

	private ImageButton collect_btn, connect_btn, comments_btn,
			collect_btn_click;
	private PopupWindow popupWindow;// ѡ����ϵ��ʽ�ĶԻ���

	// �л���ǰ��ʾ��ͼƬ
	private ScrollView scrollviewlayout;
	private String tellphone = "";
	private String qq = "";
	private int uid, gid;
	private boolean isgoods;
	/** ��������Ľ����� */
	private MyProgressDialog progressDialog;
	private Tencent mTencent;
	private String Appid = "1104627598";
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// ����������� ���õ�ǰ��ҳ�� ���� Ϊ int ����
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.goods_detail);
		super.onCreate(savedInstanceState);
		fb = FinalBitmap.create(this);// ��ʼ��FinalBitmapģ��
		initview();
		setData();
		if (advImageUrl != null)
			imageCycleView.setImageResources(advImageUrl, advImageName,
					advCycleViewListener);

		commentList = new ArrayList<Comment>();
		type = new TypeToken<List<Comment>>() {
		}.getType();
	}

	private void setData() {
		Intent intent = getIntent();
		float price = intent.getFloatExtra("price", 9999.9999f);
		String title = intent.getStringExtra("title");
		String place = intent.getStringExtra("place");
		String detail = intent.getStringExtra("detail");
		String publish_time = intent.getStringExtra("publish_time");
		String pathList = intent.getStringExtra("pathList");
		uid = intent.getIntExtra("uid", 0);
		gid = intent.getIntExtra("gid", 0);
		isgoods = intent.getBooleanExtra("isgoods", true);
		this.price.setText(String.valueOf(price));
		this.title.setText(title);
		this.place.setText(place);
		this.detail.setText(detail);
		tv_time_publish.setText("����ʱ�䣺" + publish_time);

		Log.i("test", "pathList====" + pathList);

		if (pathList != null && !pathList.equals("")) {
			advImageUrl = new ArrayList<String>();
			String[] path = pathList.split(",");
			Log.i("test", "path.length====" + path.length);
			for (int i = 0; i < path.length; i++) {
				Log.i("test", "path====" + path[i]);
				advImageUrl.add(getString(R.string.hostAddress) + path[i]);
			}
			Log.i("test", "aaaa=====" + advImageUrl.size());
		}

		requestUserInfo();
		requestIsCollect();
		requestCommentData();
	}

	/**
	 * ��ʼ��view
	 */
	private void initview() {
		imageCycleView = (ImageCycleView) findViewById(R.id.adv_cycle_view);
		tv_username = (TextView) findViewById(R.id.username);
		tv_department = (TextView) findViewById(R.id.department);
		tv_time_publish = (TextView) findViewById(R.id.time_publish);

		this.price = (TextView) findViewById(R.id.price_detail);
		this.title = (TextView) findViewById(R.id.title_detail);
		// mPullToRefreshView = (PullToRefreshView)
		// findViewById(R.id.main_pull_refresh_view);
		lv_user_comments = (ListView) this.findViewById(R.id.comment_listview);
		this.comments_btn = (ImageButton) findViewById(R.id.comments_btn);
		this.collect_btn = (ImageButton) findViewById(R.id.collect_btn);
		this.connect_btn = (ImageButton) findViewById(R.id.connect_btn);
		this.place = (TextView) findViewById(R.id.place);
		this.detail = (TextView) findViewById(R.id.detail);
		this.back = (ImageView) findViewById(R.id.back);
		this.share = (ImageView) findViewById(R.id.share);
		this.collect_btn_click = (ImageButton) findViewById(R.id.collect_btn_click);
		this.scrollviewlayout = (ScrollView) findViewById(R.id.scrollviewlayout);
		progressDialog = new MyProgressDialog(this);
		android.view.ViewGroup.LayoutParams params = scrollviewlayout
				.getLayoutParams();
		params.height = getDefautHeight() - 100;
		this.scrollviewlayout.setLayoutParams(params);
		this.scrollviewlayout.smoothScrollTo(0, 20);

		this.comments_btn.setOnClickListener(this);
		this.collect_btn.setOnClickListener(this);
		this.connect_btn.setOnClickListener(this);
		this.back.setOnClickListener(this);
		this.share.setOnClickListener(this);
		this.collect_btn_click.setOnClickListener(this);

		// mPullToRefreshView.setOnFooterRefreshListener(this);
		// mPullToRefreshView.setOnHeaderRefreshListener(this);
		// mPullToRefreshView.PULL_DOWN_ENABLE = 0;
	}

	private void requestUserInfo() {
		JsonObjectRequest request = new JsonObjectRequest(
				Constants.GET_USERINFO_REQUEST_URL + "userid=" + uid, null,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						Log.i("test", "json=====" + response.toString());

						User u = new User();
						u = JsonUtil.json2Entity(response.toString(),
								User.class);
						tv_username.setText(u.getUsername());
						String email = u.getEmail();
						if (!email.equals("")) {
							String dex = email.substring(3, 6);
							tv_department.setText(u.getEmail().replace(dex,
									"***"));
						}
						qq = u.getQq();
						tellphone = u.getTel();
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						Log.i("test", "error======" + arg0.toString());
						ToastUtil.showToast(GoodsDetailActivity.this,
								"��ȡ�û���Ϣʧ��");
					}
				});
		// �������Tag,����ȡ������
		request.setTag(this);
		VolleyUtil.getQueue(this).add(request);
	}

	private void requestCommentData() {
		Log.i("test", "gid===" + gid);
		url = getString(R.string.hostAddress)
				+ "comment_getCommentList?goods_id=" + gid + "&currentpage="
				+ currentpage + "&pagesize=" + pagesize;
		Log.i("test", "url===========" + url);
		JsonArrayRequests request = new JsonArrayRequests(url,
				new Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {
						Log.i("text", "json====" + response.toString());
						commentList = JsonUtil.json2List(response.toString(),
								type);
						if(commentList==null||commentList.size()==0){
							commentAdapter = new CommentAdapter(
									GoodsDetailActivity.this, commentList,
									replyToCommentListener, replyToReplyListener);
							lv_user_comments.setAdapter(commentAdapter);
							ToastUtil.showToast(GoodsDetailActivity.this,
									"��ʱû������");
						}else{
							for (int i = 0; i < commentList.size(); i++) {
								Collections.sort(commentList.get(i).getReplyList(),
										comp);
							}
							commentAdapter = new CommentAdapter(
									GoodsDetailActivity.this, commentList,
									replyToCommentListener, replyToReplyListener);
							lv_user_comments.setAdapter(commentAdapter);
							setListViewHeightBasedOnChildren(lv_user_comments);
							currentpage++;
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						Log.i("text", "error======" + arg0.toString());
						ToastUtil.showToast(GoodsDetailActivity.this,
								"��ȡ��������ʧ��");
					}
				});
		// �������Tag,����ȡ������
		request.setTag(this);
		VolleyUtil.getQueue(GoodsDetailActivity.this).add(request);
	}

	/**
	 * ˢ������
	 * 
	 * @param currentpage
	 *            ��ǰҳ��
	 */
	private void refreshData() {
		url = getString(R.string.hostAddress)
				+ "comment_getCommentList?goods_id=" + gid + "&currentpage="
				+ currentpage + "&pagesize=" + pagesize;
		list = new ArrayList<Comment>();
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
						ToastUtil.showToast(
								GoodsDetailActivity.this,
								getResources().getString(
										R.string.failedconnection));
					}
				});
		// �������Tag,����ȡ������
		request.setTag(this);
		VolleyUtil.getQueue(GoodsDetailActivity.this).add(request);
	}

	/**
	 * �������ظ������
	 */
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		Log.i("test", "currentpage===" + currentpage);
		Log.i("test", "url===" + url);
		refreshData();
		// �����߳��и���UI
		mPullToRefreshView.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (list != null && list.size() != 0) {
					commentList.addAll(list);
					commentAdapter.notifyDataSetChanged();
					lv_user_comments.setSelection(currentpage * pagesize
							- pagesize);
					currentpage++;
				} else {
					ToastUtil.showToast(GoodsDetailActivity.this, "û�и���������");
				}
				mPullToRefreshView.onFooterRefreshComplete();
			}
		}, 1000);
	}

	/**
	 * �ظ����۵ļ������ظ�¥����
	 */
	private OnClickListener replyToCommentListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			onCreateDialog(ONE_COMMENT_CODE, position);
		}
	};

	/**
	 * ����ظ��ļ�����¥��¥��
	 */
	private OnClickListener replyToReplyListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int parentPosition = (Integer) v.getTag(R.id.tag_first);
			int childPosition = (Integer) v.getTag(R.id.tag_second);
			onCreateDialog(parentPosition, childPosition);
		}
	};

	/**
	 * �������۵ĶԻ���
	 * 
	 * @param parentPositon
	 *            ���ڵ��position
	 * @param childPostion
	 *            �ӽڵ��position
	 * @return
	 */
	protected Dialog onCreateDialog(final int parentPositon,
			final int childPostion) {
		final Dialog customDialog = new Dialog(this);
		LayoutInflater inflater = getLayoutInflater();
		View mView = inflater.inflate(R.layout.dialog_comment, null);
		edt_reply = (EditText) mView.findViewById(R.id.edt_comments);
		btn_reply = (Button) mView.findViewById(R.id.btn_send);

		customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		customDialog.setContentView(mView);
		customDialog.show();

		if (childPostion != -1) {
			if (parentPositon != -1) {
				edt_reply.setHint("@"
						+ commentList.get(parentPositon).getReplyList()
								.get(childPostion).getUsername());
			} else {
				edt_reply.setHint("@"
						+ commentList.get(childPostion).getUsername());
			}
		} else {
			edt_reply.setHint("����������");
		}

		// ���ͻظ���ť�ļ���
		btn_reply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (childPostion) {
				case ONE_COMMENT_CODE:
					if (TextUtils.isEmpty(edt_reply.getText().toString())) {
						Toast.makeText(GoodsDetailActivity.this, "���ݲ���Ϊ��",
								Toast.LENGTH_SHORT).show();
					} else {
						final Comment comment = new Comment();
						comment.setUsername(PreferenceUtils.getPrefString(
								GoodsDetailActivity.this, Constants.USERNAME,
								"none"));
						comment.setContent(edt_reply.getText().toString());
						comment.setHeadpicture(PreferenceUtils.getPrefString(
								GoodsDetailActivity.this,
								Constants.USERIMAGEPATH, ""));

						AjaxParams params = new AjaxParams();
						params.put("comment.gid", gid + "");
						params.put("comment.headpicture",
								comment.getHeadpicture());
						params.put("comment.username", comment.getUsername());
						params.put("comment.content", comment.getContent());
						params.put("comment.datetime", UtilTools.getDate());

						customDialog.dismiss();
						edt_reply.setText("");

						FinalHttp fb = new FinalHttp();
						String url = "http://iesunny.gotoip2.com/comment_addComment";
						fb.post(url, params, new AjaxCallBack<String>() {

							@Override
							public void onSuccess(String t) {
								if (t.toString().equals("true")) {
									commentList.add(comment);
									commentAdapter.clearList();
									commentAdapter.updateList(commentList);
								    commentAdapter.notifyDataSetChanged();
									setListViewHeightBasedOnChildren(lv_user_comments);
									handler.post(new Runnable() {
										@Override
										public void run() {
											scrollviewlayout
													.fullScroll(ScrollView.FOCUS_DOWN);
										}
									});
									Log.i("test", "����ɹ�");
									ToastUtil.showToast(
											GoodsDetailActivity.this, "����ɹ�");
								}
								super.onSuccess(t);
							}

							@Override
							public void onFailure(Throwable t, int errorNo,
									String strMsg) {
								Log.i("text", "error======" + strMsg);
								ToastUtil.showToast(GoodsDetailActivity.this,
										"����ʧ��");
							}
						});

					}
					break;
				default:
					if (TextUtils.isEmpty(edt_reply.getText().toString())) {
						Toast.makeText(GoodsDetailActivity.this, "���ݲ���Ϊ��",
								Toast.LENGTH_SHORT).show();
					} else {
						Reply reply = new Reply();
						reply.setUsername(PreferenceUtils.getPrefString(
								GoodsDetailActivity.this, Constants.USERNAME,
								"none"));
						reply.setContent(edt_reply.getText().toString());

						AjaxParams params = new AjaxParams();
						params.put("reply.username", reply.getUsername());
						params.put("reply.content", reply.getContent());
						params.put("reply.datetime", UtilTools.getDate());

						if (parentPositon != -1) {
							reply.setReplyTo(commentList.get(parentPositon)
									.getReplyList().get(childPostion)
									.getUsername());
							commentList.get(parentPositon).getReplyList()
									.add(reply);
							params.put("reply.cid",
									commentList.get(parentPositon).getCid()
											+ "");
							params.put("reply.replyTo", reply.getReplyTo());
						} else {
							replyList = commentList.get(childPostion)
									.getReplyList();
							replyList.add(reply);
							commentList.get(childPostion).setReplyList(
									replyList);
							params.put("reply.cid",
									commentList.get(childPostion).getCid() + "");
						}
						customDialog.dismiss();
						edt_reply.setText("");
						FinalHttp fb = new FinalHttp();
						String url = "http://iesunny.gotoip2.com/comment_addReply";
						fb.post(url, params, new AjaxCallBack<String>() {
							@Override
							public void onSuccess(String t) {
								if (t.toString().equals("true")) {
									commentAdapter.clearList();
									commentAdapter.updateList(commentList);
									commentAdapter.notifyDataSetChanged();
									setListViewHeightBasedOnChildren(lv_user_comments);
									Log.i("test", "childPostion==="+childPostion);
									Log.i("test", "commentList.size()==="+commentList.size());
									if(childPostion==commentList.size()-1){
										handler.post(new Runnable() {
											@Override
											public void run() {
												scrollviewlayout
														.fullScroll(ScrollView.FOCUS_DOWN);
											}
										});
									}
									Log.i("test", "�ظ��ɹ�");
									ToastUtil.showToast(
											GoodsDetailActivity.this, "�ظ��ɹ�");
								}
								super.onSuccess(t);
							}

							@Override
							public void onFailure(Throwable t, int errorNo,
									String strMsg) {
								Log.i("text", "error======" + strMsg);
								ToastUtil.showToast(GoodsDetailActivity.this,
										"�ظ�ʧ��");
							}
						});

					}
					break;
				}
			}
		});
		return customDialog;
	}

	/**
	 * ���ظ���id��������
	 */
	Comparator<Reply> comp = new Comparator<Reply>() {
		public int compare(Reply p1, Reply p2) {
			if (p1.getRid() < p2.getRid())
				return -1;
			else if (p1.getRid() == p2.getRid())
				return 0;
			else if (p1.getRid() > p2.getRid())
				return 1;
			return 0;
		}
	};

	/**
	 * ����ѡ����ϵ��ʽ�ĶԻ���
	 */
	private void showPopupWindow() {
		View view = (RelativeLayout) LayoutInflater.from(this).inflate(
				R.layout.layout_select_connect_popmenu, null);
		TextView camera = (TextView) view
				.findViewById(R.id.message_popmenu_camera);
		TextView photos = (TextView) view
				.findViewById(R.id.message_popmenu_photos);
		TextView cancel = (TextView) view
				.findViewById(R.id.message_popmenu_cancel);
		TextView qq = (TextView) view.findViewById(R.id.message_popmenu_qq);
		camera.setOnClickListener(this);
		photos.setOnClickListener(this);
		cancel.setOnClickListener(this);
		qq.setOnClickListener(this);
		if (popupWindow == null) {
			popupWindow = new PopupWindow(this);
		}
		popupWindow.setFocusable(true); // ����PopupWindow�ɻ�ý���
		popupWindow.setTouchable(true); // ����PopupWindow�ɴ���
		popupWindow.setOutsideTouchable(true); // ���÷�PopupWindow����ɴ���
		popupWindow.setContentView(view);
		popupWindow.setWidth(LayoutParams.MATCH_PARENT); // ����SelectPicPopupWindow��������Ŀ�
		popupWindow.setHeight(600); // ����SelectPicPopupWindow��������ĸ�
		popupWindow.setAnimationStyle(R.style.AnimBottom); // ����SelectPicPopupWindow�������嶯��Ч��

		ColorDrawable dw = new ColorDrawable(0xb07EC5D3); // ʵ����һ��ColorDrawable��ɫΪ��͸��
		popupWindow.setBackgroundDrawable(dw); // ����SelectPicPopupWindow��������ı���
		popupWindow.showAtLocation(
				GoodsDetailActivity.this.findViewById(R.id.goodsdetail),
				Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
		popupWindow.update();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.collect_btn:
			if (!CheckSign()) {
				ShowLoginDialog();
				return;
			}

			addCollect();

			break;
		case R.id.collect_btn_click:
			if (!CheckSign()) {
				ShowLoginDialog();
				return;
			}

			cancleCollect();

			break;
		case R.id.connect_btn:
			if (!CheckSign()) {
				ShowLoginDialog();
				return;
			}
			showPopupWindow();
			break;
		case R.id.comments_btn:
			if (!CheckSign()) {
				ShowLoginDialog();
				return;
			}
			onCreateDialog(ONE_COMMENT_CODE, ONE_COMMENT_CODE);
			break;
		case R.id.message_popmenu_camera:
			if(TextUtils.isEmpty(tellphone)){
				ToastUtil.showToast(GoodsDetailActivity.this, "������û���ֻ���ϵ��ʽ");
				return;
			}
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel://"
					+ tellphone));
			startActivity(intent);
			break;
		case R.id.message_popmenu_photos:
			TelephonyManager phoneMgr = (TelephonyManager) GoodsDetailActivity.this
					.getSystemService(Context.TELEPHONY_SERVICE);
			// String mynumber = phoneMgr.getLine1Number();
			Uri uri = Uri.parse("smsto:" + tellphone);
			Intent intents = new Intent();
			intents.setAction(Intent.ACTION_SENDTO);
			intents.putExtra("sms_body", "");
			intents.setType("vnd.android-dir/mms-sms");
			intents.setData(uri);
			startActivity(intents);
			break;
		case R.id.message_popmenu_cancel:
			popupWindow.dismiss();
			break;
		case R.id.back:
			new Thread() {
				@Override
				public void run() {
					Instrumentation inst = new Instrumentation();
					inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
				}
			}.start();
			break;
		case R.id.share:

			Intent in = new Intent(Intent.ACTION_SEND);
			in.setType("text/plain");
			String msg = "���ף���Ѹ�ݵ�У԰����ƽ̨"
					+ "http://iesunny.gotoip2.com/file/IECampus.apk";

			in.putExtra(Intent.EXTRA_TEXT, msg);
			startActivity(Intent.createChooser(in, "ѡ�����"));
			break;
		case R.id.message_popmenu_qq:
			if(TextUtils.isEmpty(qq)){
				ToastUtil.showToast(GoodsDetailActivity.this, "������û��qq��ϵ��ʽ");
				return;
			}
			mTencent = Tencent.createInstance(Appid, GoodsDetailActivity.this);
			int ret = mTencent.startWPAConversation(GoodsDetailActivity.this,qq, "");
			break;
		default:
			break;
		}

	}

	private void requestIsCollect() {
		progressDialog.show();
		StringRequest request = new StringRequest(
				Constants.GET_ISCOLLECT_REQUEST_URL + "co_uid=" + uid
						+ "&co_gid=" + gid + "&type=" + isgoods,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						if (response.toString().equals("true")) {
							collect_btn_click.setVisibility(View.VISIBLE);
							collect_btn.setVisibility(View.GONE);
							// �ղع�����ť���ܵ��
							// ToastUtil.showToast(GoodsDetailActivity.this,
							// "�Ѿ��ղ�");
						} else {
							// û�ղع�����ť���Ե��
							collect_btn_click.setVisibility(View.GONE);
							collect_btn.setVisibility(View.VISIBLE);
							// ToastUtil.showToast(GoodsDetailActivity.this,
							// "δ�ղ�");
						}
						progressDialog.dismiss();
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						progressDialog.dismiss();
						ToastUtil.showToast(GoodsDetailActivity.this,
								"��ȡ�ղ���Ϣʧ��");

					}
				});

		// �������Tag,����ȡ������
		request.setTag(this);
		VolleyUtil.getQueue(this).add(request);
	}

	private void addCollect() {
		progressDialog.show();
		AjaxParams params = new AjaxParams();
		params.put("collection.uid",
				PreferenceUtils.getPrefInt(this, Constants.USERID, 0) + "");
		params.put("collection.gid", String.valueOf(gid));
		params.put("collection.isgoods", String.valueOf(isgoods));
		FinalHttp fh = new FinalHttp();
		fh.get(Constants.SET_COLLECT_REQUEST_URL, params,
				new AjaxCallBack<String>() {
					@Override
					public void onSuccess(String state) {
						if (Boolean.parseBoolean(state)) {
							ToastUtil.showToast(GoodsDetailActivity.this,
									"�ղسɹ�");
							int count = PreferenceUtils.getPrefInt(
									GoodsDetailActivity.this,
									Constants.COLLECTIONNUBER, 0);
							PreferenceUtils.setPrefInt(
									GoodsDetailActivity.this,
									Constants.COLLECTIONNUBER, ++count);
							collect_btn_click.setVisibility(View.VISIBLE);
							collect_btn.setVisibility(View.GONE);
							progressDialog.dismiss();
						} else {
							progressDialog.dismiss();
							ToastUtil.showToast(GoodsDetailActivity.this,
									"�ղ�ʧ��");
						}
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						Log.i("test", "error====" + strMsg);
						super.onFailure(t, errorNo, strMsg);
						progressDialog.dismiss();
					}
				});
	}

	private void cancleCollect() {
		progressDialog.show();
		AjaxParams params = new AjaxParams();
		params.put("collection.uid",
				PreferenceUtils.getPrefInt(this, Constants.USERID, 0) + "");
		params.put("collection.gid", String.valueOf(gid));
		params.put("collection.isgoods", String.valueOf(isgoods));
		FinalHttp fh = new FinalHttp();
		fh.get(Constants.CANCLE_COLLECT_REQUEST_URL, params,
				new AjaxCallBack<String>() {
					@Override
					public void onSuccess(String state) {
						if (Boolean.parseBoolean(state)) {
							ToastUtil.showToast(GoodsDetailActivity.this,
									"ȡ���ɹ�");
							int count = PreferenceUtils.getPrefInt(
									GoodsDetailActivity.this,
									Constants.COLLECTIONNUBER, 0);
							PreferenceUtils.setPrefInt(
									GoodsDetailActivity.this,
									Constants.COLLECTIONNUBER, --count);
							collect_btn_click.setVisibility(View.GONE);
							collect_btn.setVisibility(View.VISIBLE);
							progressDialog.dismiss();
						} else {
							progressDialog.dismiss();
							ToastUtil.showToast(GoodsDetailActivity.this,
									"ȡ��ʧ��");
						}
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						Log.i("test", "error====" + strMsg);
						super.onFailure(t, errorNo, strMsg);
						progressDialog.dismiss();
					}
				});
	}

	public boolean CheckSign() {
		String username = PreferenceUtils.getPrefString(this,
				Constants.USERNAME, "");
		if (username.equals("") || username == null) {
			return false;
		}
		return true;

	}

	public void ShowLoginDialog() {
		AlertDialog dialog = new AlertDialog.Builder(GoodsDetailActivity.this)
				.setMessage("�ף��㻹û��½Ŷ�����ڵ�¼��")
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(GoodsDetailActivity.this,
								LoginActivity.class);
						startActivity(intent);
					}
				}).create();
		dialog.show();
	}

	public int getDefautHeight() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int width = metrics.widthPixels;// ��Ļ�Ŀ�dp
		int height = metrics.heightPixels;// ��Ļ�ĸ�dp
		return height;

	}

	/*
	 * ��̬����ListView�齨�ĸ߶�
	 */
	public void setListViewHeightBasedOnChildren(ListView listView) {

		ListAdapter listAdapter = listView.getAdapter();

		if (listAdapter == null) {

			return;

		}

		int totalHeight = 0;

		for (int i = 0; i < listAdapter.getCount(); i++) {

			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();

		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();

		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);

	}

	private ImageCycleViewListener advCycleViewListener = new ImageCycleViewListener() {
		@Override
		public void onImageClick(int position, View imageView) {

		}

		@Override
		public void displayImage(String imageURL, ImageView imageView) {
			fb.display(imageView, imageURL);
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		imageCycleView.startImageCycle();
	};

	@Override
	protected void onPause() {
		super.onPause();
		imageCycleView.pushImageCycle();
	}

	@Override
	protected void onDestroy() {
		VolleyUtil.getQueue(this).cancelAll(this);
		super.onDestroy();
		imageCycleView.pushImageCycle();
	}

}
