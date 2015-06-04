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
	// ����FragmentTabHost����
	private FragmentTabHost mTabHost;

	// ����һ������
	private LayoutInflater layoutInflater;

	// �������������Fragment����
	private Class fragmentArray[] = { HomeFragment.class, HiFragment.class,
			PubFragment.class, NewsFragment.class, MeFragment.class };

	// ������������Ű�ťͼƬ
	private int mImageViewArray[] = { R.drawable.tabbar_icon_home_select,
			R.drawable.tabbar_icon_hi_select,
			R.drawable.tabbar_icon_pub_select,
			R.drawable.tabbar_icon_news_select,
			R.drawable.tabbar_icon_me_select };

	// Tabѡ�������
	private String mTextviewArray[] = { "��ҳ", "��һ��", "", "����", "��" };

	private SlidingMenu menu;// �໬�˵�
	// �˳�ʱ��
	private long currentBackPressedTime = 0;
	// �˳����
	private static final int BACK_PRESSED_INTERVAL = 2000;

	private int vc;// ��ȡ��ǰ�汾��

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
	 * ��ʼ�����
	 */
	private void initView() {
		// ʵ�������ֶ���
		layoutInflater = LayoutInflater.from(this);

		// ʵ����TabHost���󣬵õ�TabHost
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		// mMenu = (SlidingMenu) findViewById(R.id.layout_main);
		// �õ�fragment�ĸ���
		int count = fragmentArray.length;

		for (int i = 0; i < count; i++) {
			// Ϊÿһ��Tab��ť����ͼ�ꡢ���ֺ�����
			TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i])
					.setIndicator(getTabItemView(i));
			// ��Tab��ť��ӽ�Tabѡ���
			mTabHost.addTab(tabSpec, fragmentArray[i], null);
		}
		// ����Tab��ť�ı���
		mTabHost.getTabWidget().getChildAt(2)
				.setBackgroundResource(R.drawable.tabbar_icon_pub_background);

		menu = new MenuView(this, this).initSlidingMenu();

		progressDialog = new MyProgressDialog(MainActivity.this);

	}
	
	public FragmentTabHost getTabHost(){
		return mTabHost;
	}

	/**
	 * ��Tab��ť����ͼ�������
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
				Toast.makeText(this, "�ٰ�һ�η��ؼ��˳�����", Toast.LENGTH_SHORT).show();
				return true;
			} else {
				finish(); // �˳�
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

	// TODO �Ƿ�汾����
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
						// �ȽϿ�Դ�й����ص�code����ǰ�汾code�Ƿ�һ��
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
												.setTitleText("�汾���")
												.setContentText("�����°汾���Ƿ���£�")
												.setCancelText("�ݲ�����")
												.setConfirmText("���ϸ���")
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
		// �������Tag,����ȡ������
		request.setTag(this);
		VolleyUtil.getQueue(MainActivity.this).add(request);
	}

	/**
	 * ��ȡ�汾��
	 * 
	 * @param context
	 * @return
	 */
	private int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			// ��ȡ����汾��
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
					.setTitleText("��������ʧ��...").setContentText("�����������������Ƿ�����")
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
						// �ȽϿ�Դ�й����ص�code����ǰ�汾code�Ƿ�һ��
						if (jsonCode == vc) {
							Log.e("11111", "�汾����ͬ��������" + "jsonCode:" + jsonCode);
							new SweetAlertDialog(MainActivity.this,
									SweetAlertDialog.SUCCESS_TYPE)
									.setTitleText("��ǰ�汾��������")
									.setContentText("Version:" + getAppInfo())
									.show();

						} else if (jsonCode > vc) {
							new SweetAlertDialog(MainActivity.this,
									SweetAlertDialog.WARNING_TYPE)
									.setTitleText("�汾���")
									.setContentText("�����°汾���Ƿ���£�")
									.setCancelText("�ݲ�����")
									.setConfirmText("���ϸ���")
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
		// �������Tag,����ȡ������
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