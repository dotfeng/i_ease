package com.iecampus.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.iecampus.utils.Constants;
import com.iecampus.utils.PreferenceUtils;

public class SplashActivity extends Activity implements AnimationListener {
	private Animation alphaAnimation = null;
	private ImageView imageview;
	private TextView logo_text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.welcome);
		this.imageview = (ImageView) findViewById(R.id.imgview);
		this.logo_text = (TextView) findViewById(R.id.logo_text);
		try {
			String str = getVersionName();
			logo_text.setText("��ǰ�汾�ţ�" + str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.welcome_in);
		alphaAnimation.setFillEnabled(true); // ����Fill����
		alphaAnimation.setFillAfter(true); // ���ö��������һ֡�Ǳ�����View����
		imageview.setAnimation(alphaAnimation);
		alphaAnimation.setAnimationListener(this); // Ϊ�������ü���
	}

	@Override
	public void onAnimationEnd(Animation arg0) {
		if (PreferenceUtils.getPrefBoolean(this, Constants.ISFIRSTlOGIN, true)) {
			Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
			startActivity(intent);
			finish();
		} else {
			String school = PreferenceUtils.getPrefString(this,
					Constants.SCHOOL, "");
			if ("".equals(school)) {
				Intent intent = new Intent(SplashActivity.this,
						SerchActivity.class);
				startActivity(intent);
			} else {
				Intent intent = new Intent(SplashActivity.this,
						MainActivity.class);
				startActivity(intent);
			}
			finish();
		}

	}

	@Override
	public void onAnimationRepeat(Animation animation) {

	}

	@Override
	public void onAnimationStart(Animation animation) {

	}

	private String getVersionName() throws Exception {
		// ��ȡpackagemanager��ʵ��
		PackageManager packageManager = getPackageManager();
		// getPackageName()���㵱ǰ��İ�����0�����ǻ�ȡ�汾��Ϣ
		PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),
				0);
		String version = packInfo.versionName;
		return version;
	}
}
