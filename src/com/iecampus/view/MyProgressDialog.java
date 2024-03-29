package com.iecampus.view;



import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.iecampus.activity.R;

/**
 * ProgressDialog 自定义效进度条
 * 
 * @author qjc
 *
 */
public class MyProgressDialog extends ProgressDialog {

	public MyProgressDialog(Context context, int theme) {
		super(context, theme);
	}

	public MyProgressDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.progress_dialog);

		setScreenBrightness();
		this.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {
				ImageView image = (ImageView) MyProgressDialog.this
						.findViewById(R.id.loading_img);
				Animation anim = new RotateAnimation(0, 360,
						Animation.RELATIVE_TO_SELF, 0.5f,
						Animation.RELATIVE_TO_SELF, 0.5f);
				anim.setRepeatCount(Animation.INFINITE); // 设置INFINITE，对应值-1，代表重复次数为无穷次
				anim.setDuration(1000); // 设置该动画的持续时间，毫秒单位
				anim.setInterpolator(new LinearInterpolator()); // 设置一个插入器，或叫补间器，用于完成从动画的一个起始到结束中间的补间部分
				image.startAnimation(anim);
			}
		});
	}

	private void setScreenBrightness() {
		Window window = getWindow();
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		/**
		 * 此处设置亮度值。dimAmount代表黑暗数量，也就是昏暗的多少，设置为0则代表完全明亮。 范围是0.0到1.0
		 */
		lp.dimAmount = 0;
		window.setAttributes(lp);
	}

}