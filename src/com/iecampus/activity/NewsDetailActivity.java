package com.iecampus.activity;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.iecampus.utils.LruImageCache;
import com.iecampus.utils.StringUtil;
import com.iecampus.utils.VolleyUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsDetailActivity extends Activity {
	private TextView title, date, content;
	private ImageView first, sencond, third;
	private ImageLoader imageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newsdetail);
		this.imageLoader = new ImageLoader(
				VolleyUtil.getQueue(NewsDetailActivity.this),
				new LruImageCache());
		initView();
		initData();
	}

	private void initData() {
		Intent intent = this.getIntent();
		String imglist = intent.getStringExtra("imglist");
		String title_str = intent.getStringExtra("title");
		String content_str = intent.getStringExtra("content");
		String time = intent.getStringExtra("time");
		String[] img = imglist.split(",");
		this.title.setText(title_str);
		this.date.setText("发布日期："+time);
		this.content.setText(content_str);
		switch (img.length) {
		case 0:
			first.setVisibility(View.GONE);
			break;
		case 1:
			first.setVisibility(View.VISIBLE);
			setImage(first, getString(R.string.hostAddress) + img[0]);
			break;
		case 2:
			first.setVisibility(View.VISIBLE);
			sencond.setVisibility(View.VISIBLE);
			setImage(first, getString(R.string.hostAddress) + img[0]);
			setImage(sencond, getString(R.string.hostAddress) + img[1]);
			break;
		case 3:
			first.setVisibility(View.VISIBLE);
			sencond.setVisibility(View.VISIBLE);
			third.setVisibility(View.VISIBLE);
			setImage(first, getString(R.string.hostAddress) + img[0]);
			setImage(sencond, getString(R.string.hostAddress) + img[1]);
			setImage(third, getString(R.string.hostAddress) + img[2]);
			break;
		default:
			break;
		}

	}

	private void initView() {
		this.title = (TextView) findViewById(R.id.news_title);
		this.date = (TextView) findViewById(R.id.news_publishtime);
		this.content = (TextView) findViewById(R.id.news_content);
		this.first = (ImageView) findViewById(R.id.newsimg_first);
		this.sencond = (ImageView) findViewById(R.id.newsimg_sencond);
		this.third = (ImageView) findViewById(R.id.newsimg_third);
	}

	void setImage(ImageView imageView, String url) {
		ImageContainer container;

		try {
			// 如果当前ImageView上存在请求，先取消
			if (imageView.getTag() != null) {
				container = (ImageContainer) imageView.getTag();
				container.cancelRequest();
			}
		} catch (Exception e) {

		}

		ImageListener listener = ImageLoader.getImageListener(imageView,
				R.drawable.ic_launcher, R.drawable.ic_launcher);

		container = imageLoader.get(StringUtil.preUrl(url), listener);

		// 在ImageView上存储当前请求的Container，用于取消请求
		imageView.setTag(container);
	}
}
