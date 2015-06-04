package com.iecampus.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.iecampus.dialog.SweetAlertDialog;
import com.iecampus.fragment.HiFragment;
import com.iecampus.fragment.HomeFragment;
import com.iecampus.fragment.MeFragment;
import com.iecampus.fragment.NewsFragment;
import com.iecampus.fragment.PubFragment;
import com.iecampus.update.AppUpdateService;
import com.iecampus.utils.Constants;
import com.iecampus.utils.NetWorkUtil;
import com.iecampus.utils.ToastUtil;
import com.iecampus.utils.VolleyUtil;
import com.iecampus.view.MenuView;
import com.iecampus.view.MenuView.updateListener;
import com.iecampus.view.MyProgressDialog;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends FragmentActivity implements updateListener {

	private Button categotybt;
	// 定义FragmentTabHost对象
	private FragmentTabHost mTabHost;

	// 定义一个布局
	private LayoutInflater layoutInflater;

	// 定义数组来存放Fragment界面
	private Class fragmentArray[] = { HomeFragment.class, HiFragment.class,
			PubFragment.class, NewsFragment.class, MeFragment.class };

	// 定义数组来存放按钮图片
	private int mImageViewArray[] = { R.drawable.tabbar_icon_home_select,
			R.drawable.tabbar_icon_hi_select,
			R.drawable.tabbar_icon_pub_select,
			R.drawable.tabbar_icon_news_select,
			R.drawable.tabbar_icon_me_select };

	// Tab选项卡的文字
	private String mTextviewArray[] = { "首页", "嗨一下", "", "新闻", "我" };

	private SlidingMenu menu;// 侧滑菜单
	// 退出时间
	private long currentBackPressedTime = 0;
	// 退出间隔
	private static final int BACK_PRESSED_INTERVAL = 2000;

	private int vc;// 获取当前版本号

	private ProgressBar pb;
	private TextView tv;
	public static int loading_process;

	private ProgressDialog progressDialog;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initView();
		vc = getVersionCode(this);
		chekedVersionCode();
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		// 实例化布局对象
		layoutInflater = LayoutInflater.from(this);

		// 实例化TabHost对象，得到TabHost
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		// mMenu = (SlidingMenu) findViewById(R.id.layout_main);
		// 得到fragment的个数
		int count = fragmentArray.length;

		for (int i = 0; i < count; i++) {
			// 为每一个Tab按钮设置图标、文字和内容
			TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i])
					.setIndicator(getTabItemView(i));
			// 将Tab按钮添加进Tab选项卡中
			mTabHost.addTab(tabSpec, fragmentArray[i], null);
		}
		// 设置Tab按钮的背景
		mTabHost.getTabWidget().getChildAt(2)
				.setBackgroundResource(R.drawable.tabbar_icon_pub_background);

		menu = new MenuView(this, this).initSlidingMenu();

		progressDialog = new MyProgressDialog(MainActivity.this);

	}
	
	public FragmentTabHost getTabHost(){
		return mTabHost;
	}

	/**
	 * 给Tab按钮设置图标和文字
	 */
	private View getTabItemView(int index) {
		View view = layoutInflater.inflate(R.layout.tabbar_item_view, null);

		ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
		imageView.setImageResource(mImageViewArray[index]);

		TextView textView = (TextView) view.findViewById(R.id.textview);
		textView.setText(mTextviewArray[index]);

		return view;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL) {
				currentBackPressedTime = System.currentTimeMillis();
				System.out.println(currentBackPressedTime);
				Toast.makeText(this, "再按一次返回键退出程序", Toast.LENGTH_SHORT).show();
				return true;
			} else {
				finish(); // 退出
			}
		}
		return super.dispatchKeyEvent(event);
	}

	public void cateonclick(View v) {
		switch (v.getId()) {
		case R.id.categotybt:
			if (menu.isMenuShowing()) {
				menu.showContent();
			} else {
				menu.showMenu();
			}
			break;

		default:
			break;
		}
	}

	// TODO 是否版本更新
	public void chekedVersionCode() {

		JsonObjectRequest request = new JsonObjectRequest(
				Constants.CHECK_UPDATE_URL, null, new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject result) {
						int jsonCode = vc;
						try {
							jsonCode = result.getInt("versioncode");
						} catch (JSONException e) {
							e.printStackTrace();
						}
						// 比较开源中国返回的code跟当前版本code是否一致
						if (jsonCode == vc) {
							return;
						} else if (jsonCode > vc) {

							CountDownTimer timer = new CountDownTimer(12 * 100,
									100) {

								@Override
								public void onTick(long millisUntilFinished) {
									long a = millisUntilFinished / 100;

									if (a == 1) {
										new SweetAlertDialog(MainActivity.this,
												SweetAlertDialog.WARNING_TYPE)
												.setTitleText("版本检测")
												.setContentText("发现新版本，是否更新？")
												.setCancelText("暂不更新")
												.setConfirmText("马上更新")
												.showCancelButton(true)
												.setCancelClickListener(
														new SweetAlertDialog.OnSweetClickListener() {
															@Override
															public void onClick(
																	SweetAlertDialog sDialog) {
																sDialog.dismiss();
															}
														})
												.setConfirmClickListener(
														new SweetAlertDialog.OnSweetClickListener() {
															@Override
															public void onClick(
																	SweetAlertDialog sDialog) {
																Intent updateIntent = new Intent(
																		MainActivity.this,
																		AppUpdateService.class);
																updateIntent
																		.putExtra(
																				"titleId",
																				R.string.app_name);
																startService(updateIntent);
																sDialog.dismiss();

															}
														}).show();
									} else {

									}
								}

								@Override
								public void onFinish() {

								}
							};
							timer.start();

						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						ToastUtil.showToast(MainActivity.this, getResources()
								.getString(R.string.request_fail_text));
					}
				});
		// 请求加上Tag,用于取消请求
		request.setTag(this);
		VolleyUtil.getQueue(MainActivity.this).add(request);
	}

	/**
	 * 获取版本号
	 * 
	 * @param context
	 * @return
	 */
	private int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			// 获取软件版本号
			versionCode = context.getPackageManager().getPackageInfo(
					"com.iecampus.activity", 1).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	public void chekedVersionCode2() {
		progressDialog.show();
		if (!NetWorkUtil.networkCanUse(this)) {
			new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
					.setTitleText("网络连接失败...").setContentText("请检查您的网络连接是否正常")
					.show();
			progressDialog.dismiss();
			return;
		}
		JsonObjectRequest request = new JsonObjectRequest(
				Constants.CHECK_UPDATE_URL, null, new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject result) {
						progressDialog.dismiss();
						int jsonCode = vc;
						try {
							jsonCode = result.getInt("versioncode");
						} catch (JSONException e) {
							e.printStackTrace();
						}
						// 比较开源中国返回的code跟当前版本code是否一致
						if (jsonCode == vc) {
							Log.e("11111", "版本号相同，不更新" + "jsonCode:" + jsonCode);
							new SweetAlertDialog(MainActivity.this,
									SweetAlertDialog.SUCCESS_TYPE)
									.setTitleText("当前版本已是最新")
									.setContentText("Version:" + getAppInfo())
									.show();

						} else if (jsonCode > vc) {
							new SweetAlertDialog(MainActivity.this,
									SweetAlertDialog.WARNING_TYPE)
									.setTitleText("版本检测")
									.setContentText("发现新版本，是否更新？")
									.setCancelText("暂不更新")
									.setConfirmText("马上更新")
									.showCancelButton(true)
									.setCancelClickListener(
											new SweetAlertDialog.OnSweetClickListener() {
												@Override
												public void onClick(
														SweetAlertDialog sDialog) {
													sDialog.dismiss();
												}
											})
									.setConfirmClickListener(
											new SweetAlertDialog.OnSweetClickListener() {
												@Override
												public void onClick(
														SweetAlertDialog sDialog) {
													Intent updateIntent = new Intent(
															MainActivity.this,
															AppUpdateService.class);
													updateIntent.putExtra(
															"titleId",
															R.string.app_name);
													startService(updateIntent);
													sDialog.dismiss();

												}
											}).show();
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						progressDialog.dismiss();
						ToastUtil.showToast(MainActivity.this, getResources()
								.getString(R.string.request_fail_text));
					}
				});
		// 请求加上Tag,用于取消请求
		request.setTag(this);
		VolleyUtil.getQueue(MainActivity.this).add(request);
	}

	private String getAppInfo() {
		try {
			String pkName = this.getPackageName();
			String versionName = this.getPackageManager().getPackageInfo(
					pkName, 0).versionName;
			return versionName;
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public void onUpdate() {
		chekedVersionCode2();
	}
}