package com.iecampus.activity;

import java.util.ArrayList;
import java.util.List;

import com.iecampus.adapter.FragmentAdapter;
import com.iecampus.fragment.CollectionGoodsFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class CollectionActivity extends FragmentActivity implements
		OnPageChangeListener, OnClickListener {
	private ViewPager viewpager;
	private TextView goods, service, txback;
	private ArrayList<Fragment> list = null;
	private List<TextView> txlist;
	private int oldposition = 0;
	private ImageView back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.fragment_collection);
		initview();
		initviewpager();
	}

	private void initview() {
		this.viewpager = (ViewPager) findViewById(R.id.myviewpager);
		this.goods = (TextView) findViewById(R.id.goods);
		this.service = (TextView) findViewById(R.id.service);
		this.back = (ImageView) findViewById(R.id.back);
		this.txback = (TextView) findViewById(R.id.tx_back);
		this.goods.setOnClickListener(this);
		this.service.setOnClickListener(this);
		this.back.setOnClickListener(this);
		this.txback.setOnClickListener(this);
		txlist = new ArrayList<TextView>();
		txlist.add(goods);
		txlist.add(service);
	}

	private void initviewpager() {
		CollectionGoodsFragment goodsfragment = new CollectionGoodsFragment(
				this, "true");
		CollectionGoodsFragment servicefragment = new CollectionGoodsFragment(
				this, "false");
		list = new ArrayList<Fragment>();
		list.add(goodsfragment);
		list.add(servicefragment);
		viewpager.setAdapter(new FragmentAdapter(getSupportFragmentManager(),
				list));
		viewpager.setCurrentItem(0);
		viewpager.setOnPageChangeListener(this);
		this.txlist.get(oldposition).setBackgroundResource(
				R.drawable.textview_backgroud_normal);
		this.txlist.get(oldposition).setTextColor(R.color.white);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.tx_back:
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int position) {
		this.txlist.get(oldposition).setBackgroundResource(
				R.drawable.textview_backgroud_click);
		this.txlist.get(oldposition).setTextColor(R.color.green);
		this.txlist.get(position).setBackgroundResource(
				R.drawable.textview_backgroud_normal);
		this.txlist.get(position).setTextColor(R.color.white);
		oldposition = position;
	}
}
