package com.iecampus.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ie.university.guide.ParallaxContainer;
import com.iecampus.activity.R;
import com.iecampus.utils.Constants;
import com.iecampus.utils.PreferenceUtils;

/**
 * @author zhongdaxia 2014-12-15
 */

public class GuideActivity extends Activity{

	ImageView iv_man;
	ImageView rl_weibo;
	ParallaxContainer parallaxContainer;
	TextView tx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		PreferenceUtils.setPrefBoolean(GuideActivity.this,
				Constants.ISFIRSTlOGIN, false);
		// login = (Button) findViewById(R.id.login_now);
		// visit = (Button) findViewById(R.id.vistor_now);
		// login.setOnClickListener(this);
		// visit.setOnClickListener(this);
		/**
		 * 动画支持11以上sdk,11以下默认不显示动画
		 * 若需要支持11以下动画,也可导入https://github.com/JakeWharton/NineOldAndroids
		 */
		if (android.os.Build.VERSION.SDK_INT > 10) {
			iv_man = (ImageView) findViewById(R.id.iv_man);
			parallaxContainer = (ParallaxContainer) findViewById(R.id.parallax_container);

			if (parallaxContainer != null) {
				parallaxContainer.setImage(iv_man);
				parallaxContainer.setLooping(false);

				iv_man.setVisibility(View.VISIBLE);
				parallaxContainer.setupChildren(getLayoutInflater(),
						R.layout.view_intro_1, R.layout.view_intro_2,
						R.layout.view_intro_3, R.layout.view_intro_4,
						R.layout.view_intro_5, R.layout.login_select);
			}
		}
	}

	public void login_click(View v) {
		startActivity(new Intent(GuideActivity.this, LoginActivity.class));
		finish();
	}

	public void vistor_click(View v) {
		startActivity(new Intent(GuideActivity.this, SerchActivity.class));
		finish();
	}

}
