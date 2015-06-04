package com.iecampus.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.iecampus.activity.R;
import com.iecampus.adapter.FragmentAdapter;
import com.iecampus.utils.Constants;
import com.iecampus.utils.PreferenceUtils;

public class HomeFragment extends Fragment {
	private ArrayList<Fragment> list = null;
	private ViewPager mViewPager;
	private ImageView iv_bottom_line;
	private TextView title;
	private TextView zh;
	private TextView xw;
	private TextView ty;
	private View rootView;// 缓存Fragment view
	private String goodsCategory;
	private String type;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Bundle args = getArguments();
		goodsCategory = args != null ? args.getString("goodsCategory", "") : "";
		type = args != null ? args.getString("type", "") : "";
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("test", "HomeFragment----onCreateView");
		if (rootView == null) {
			rootView = LayoutInflater.from(getActivity()).inflate(
					R.layout.fragment_home, null);
			this.title = (TextView) rootView.findViewById(R.id.title);
			initView(rootView);
			initWidth();
			initViewPager();
		}

		// 缓存的rootView需要判断是否已经被加过parent，
		// 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		return rootView;
	}

	private void initViewPager() {
		GoodsFragment newest = null;
		GoodsFragment mostfire = null;
		if (goodsCategory.equals("")) {
			newest = new GoodsFragment(getActivity(), "newest?type=true");
			mostfire = new GoodsFragment(getActivity(), "mostpopular?type=true");
		} else {
			newest = new GoodsFragment(getActivity(),
					"newestbycategory?goods_category=" + goodsCategory
							+ "&type=" + type);
			mostfire = new GoodsFragment(getActivity(),
					"mostpopularbycategory?goods_category=" + goodsCategory
							+ "&type=" + type);
		}

		list = new ArrayList<Fragment>();
		list.add(newest);
		list.add(mostfire);

		mViewPager.setAdapter(new FragmentAdapter(getChildFragmentManager(),
				list));
		mViewPager.setCurrentItem(0);
		mViewPager.setOnPageChangeListener(new MyViewPagerChangedListener());

	}

	private void initView(View view) {
		mViewPager = (ViewPager) view.findViewById(R.id.myviewpager);
		iv_bottom_line = (ImageView) view.findViewById(R.id.iv_bottom_line);
		zh = (TextView) view.findViewById(R.id.zh);
		xw = (TextView) view.findViewById(R.id.xw);

		zh.setOnClickListener(new MyClickListener(0));
		xw.setOnClickListener(new MyClickListener(1));

	}

	private int first = 0;
	private int second = 0;

	private void initWidth() {
		int lineWidth = iv_bottom_line.getLayoutParams().width;
		Log.d("lineWidth ", lineWidth + "");
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		first = width / 2;
		second = first * 2;

	}

	private int currPosition = 0;
	TranslateAnimation ta = null;

	class MyViewPagerChangedListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int arg0) {
			Log.d("onchanged", "onchanged " + arg0);

			switch (arg0) {
			case 0:

				if (currPosition == 1) {
					ta = new TranslateAnimation(first, 0, 0, 0);
				}

				break;

			case 1:

				if (currPosition == 0) {
					ta = new TranslateAnimation(0, first, 0, 0);
				}

				break;

			}

			currPosition = arg0;
			if (ta == null)
				return;
			ta.setDuration(300);
			ta.setFillAfter(true);
			iv_bottom_line.startAnimation(ta);
		}

	}

	class MyClickListener implements OnClickListener {

		private int index = 0;

		public MyClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mViewPager.setCurrentItem(index);

		}

	}

	@Override
	public void onResume() {
		if (this.title != null&&getActivity()!=null)
			this.title.setText(PreferenceUtils.getPrefString(getActivity(),
					Constants.SCHOOL, ""));
		super.onResume();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.i("test", "HomeFragment----onDestroyView");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("test", "HomeFragment----onDestroy");
	}
}
