package com.iecampus.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Instrumentation;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iecampus.adapter.TopicDetailListAdapater;
import com.iecampus.moldel.Comments;
import com.iecampus.utils.Constants;
import com.iecampus.utils.PreferenceUtils;
import com.iecampus.utils.ToastUtil;
import com.iecampus.utils.UtilTools;
import com.iecampus.view.MyProgressDialog;

public class TopicDetailActivity extends Activity implements OnClickListener {
	private TopicDetailListAdapater adapter;// 适配器
	private TextView title, comments_num, back;// 讨论的题目和评论数目
	private List<Comments> list;// 评论的list数据集合
	private ListView topic_detail_listview;// 装载评论的listview
	private Button btn_taolun, btnSendComment;// 参与评论按钮
	private String data[];// 从上一个页面传过来的String数组数据
	private EditText etComment;// 发布评论的编辑框
	private Comments coments;// 评论的实体类
	private RelativeLayout title_laout, rl_input;// 装载标题的ralativelayout布局
	private ImageView topic_detail_back, topic_detail_share;// 返回和分享
	private int tid;
	private MyProgressDialog dialog;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.setContentView(R.layout.activity_topic_detail);
		super.onCreate(savedInstanceState);
		initview();
		initdata();
		initadapter();
	}

	private void initview() {
		// 实例化view
		this.comments_num = (TextView) findViewById(R.id.comments_num);
		this.title = (TextView) findViewById(R.id.topic_detail_title);
		this.topic_detail_listview = (ListView) findViewById(R.id.topic_detail_listview);
		this.btn_taolun = (Button) findViewById(R.id.btn_taolun);
		this.title_laout = (RelativeLayout) findViewById(R.id.suiji);
		this.etComment = (EditText) findViewById(R.id.etComment);
		this.rl_input = (RelativeLayout) findViewById(R.id.rl_input);
		this.btnSendComment = (Button) findViewById(R.id.btnSendComment);
		this.topic_detail_back = (ImageView) findViewById(R.id.topic_detail_back);
		this.topic_detail_share = (ImageView) findViewById(R.id.topic_detail_share);
		this.back = (TextView) findViewById(R.id.back);
		this.dialog = new MyProgressDialog(this);
		// 添加监听器
		this.btn_taolun.setOnClickListener(this);
		this.btnSendComment.setOnClickListener(this);
		this.topic_detail_back.setOnClickListener(this);
		this.topic_detail_share.setOnClickListener(this);
		this.back.setOnClickListener(this);

		this.btn_taolun.addOnLayoutChangeListener(new OnLayoutChangeListener() {

			@Override
			public void onLayoutChange(View v, int left, int top, int right,
					int bottom, int oldLeft, int oldTop, int oldRight,
					int oldBottom) {
				Log.i("test", "button正在改变");

			}
		});
	}

	/**
	 * 初始化数据
	 */
	private void initdata() {
		list = new ArrayList<Comments>();
		Intent intent = this.getIntent();
		this.title.setText(intent.getStringExtra("title"));
		this.title_laout.setBackgroundResource(intent.getIntExtra("colorid",
				R.color.main));
		this.comments_num.setText("已有" + intent.getStringExtra("comment_num")
				+ "人参与评论");
		data = intent.getStringArrayExtra("list");
		tid = intent.getIntExtra("tid", 0);
		for (String str : data) {
			coments = new Comments();
			coments.setContent(str);
			list.add(coments);
		}
	}

	private void initadapter() {
		adapter = new TopicDetailListAdapater(list, TopicDetailActivity.this);
		this.topic_detail_listview.setAdapter(adapter);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_taolun:
			if (!CheckSign()) {
				ShowLoginDialog();
				return;
			}
			this.rl_input.setVisibility(View.VISIBLE);
			popupInputMethodWindow();
			this.etComment.setFocusable(true);
			break;
		case R.id.btnSendComment:
			dialog.show();

			this.rl_input.setVisibility(View.GONE);
			String content = this.etComment.getText().toString();
			if (content.equals("")) {
				ToastUtil.showToast(this, "你输入的内容为空！");
				popupInputMethodWindow();
				dialog.dismiss();
				return;
			}

			addreply(content);

			this.etComment.setText(null);
			popupInputMethodWindow();
			break;
		case R.id.topic_detail_back:
			new Thread() {
				@Override
				public void run() {
					Instrumentation inst = new Instrumentation();
					inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
				}
			}.start();
			break;
		case R.id.topic_detail_share:
			Intent in = new Intent(Intent.ACTION_SEND);
			in.setType("text/plain");
			String msg = "爱易，最迅捷的校园交易平台"+"http://iesunny.gotoip2.com/file/IECampus.apk";
			in.putExtra(Intent.EXTRA_TEXT, msg);
			startActivity(Intent.createChooser(in, "选择分享"));
			break;
		case R.id.back:
			finish();
			break;
		default:
			break;
		}

	}

	public void addreply(final String content) {

		int uid = PreferenceUtils.getPrefInt(TopicDetailActivity.this,
				Constants.USERID, 0);
		String username = PreferenceUtils.getPrefString(
				TopicDetailActivity.this, Constants.USERNAME, "");

		String url = getString(R.string.hostAddress) + "topicreply_addreply";
		AjaxParams params = new AjaxParams();
		params.put("r_uid", String.valueOf(uid));
		params.put("r_tid", String.valueOf(tid));
		params.put("r_username", username);
		params.put("r_content", content);
		params.put("r_date", UtilTools.getDate());

		FinalHttp fh = new FinalHttp();
		fh.get(url, params, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String state) {
				if (Boolean.parseBoolean(state)) {
					Intent intent = new Intent("com.ie.update");
					TopicDetailActivity.this.sendBroadcast(intent);
					coments = new Comments();
					coments.setContent(content);
					list.add(coments);
					TopicDetailActivity.this.initadapter();
					adapter.notifyDataSetChanged();
					ToastUtil.showToast(TopicDetailActivity.this, "评论发布成功！");
				} else {
					ToastUtil.showToast(TopicDetailActivity.this, "评论发布失败！");
				}
				dialog.dismiss();
			}
		});

		
	}

	/**
	 * 输入法的弹出
	 */
	private void popupInputMethodWindow() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) TopicDetailActivity.this.etComment
						.getContext().getSystemService(
								Service.INPUT_METHOD_SERVICE);
				// imm.showSoftInput(GoodsDetailActivity.this.etComment, 0);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}, 0);
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
		AlertDialog dialog = new AlertDialog.Builder(TopicDetailActivity.this)
				.setMessage("登录后才可以评论哦，现在登录？")
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(TopicDetailActivity.this,
								LoginActivity.class);
						startActivity(intent);
						finish();
					}
				}).create();
		dialog.show();
	}

	@Override
	protected void onDestroy() {
		Log.i("test", "ondestroy");
		setResult(1);
		finish();
		super.onDestroy();
	}
	@Override
	protected void onStop() {
		Log.i("test", "onstop");
		super.onStop();
	}
}
