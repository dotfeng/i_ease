package com.iecampus.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.iecampus.activity.AboutusActivity;
import com.iecampus.activity.AdviceActivity;
import com.iecampus.activity.ConnectWithUsActivity;
import com.iecampus.activity.FunIntroduceActivity;
import com.iecampus.activity.MainActivity;
import com.iecampus.activity.R;
import com.iecampus.adapter.GoodsCategoryListAdapter;
import com.iecampus.fragment.HomeFragment;
import com.iecampus.moldel.GoodsCategory;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

/**
 * @ClassName: SlidingPaneMenuFragment
 * @Description: TODO
 * @author: raot 719055805@qq.com
 * @date: 2014年9月5日 上午10:42:07
 */
public class MenuView implements OnClickListener {
	private FragmentActivity activity;
	SlidingMenu menu;

	private ListView goodsCategoryRecyclerView;
	private ListView serviceCategoryRecyclerView;
	private GoodsCategoryListAdapter goodsCategoryListAdapter;
	private GoodsCategoryListAdapter serviceCategoryListAdapter;
	private List<GoodsCategory> goodsCategoryList;
	private List<GoodsCategory> serviceCategoryList;
	private ViewFlipper vf_menu;
	private Button btn_market;
	private Button btn_service;
	private String type = "true";
	private TextView tv_update, tv_aboutus, tv_connect, tv_detail,myregument;

	private updateListener updateListener;

	public MenuView(FragmentActivity activity, updateListener updateListener) {
		this.activity = activity;
		this.updateListener = updateListener;
	}

	public SlidingMenu initSlidingMenu() {
		menu = new SlidingMenu(activity);
		menu.setMode(SlidingMenu.LEFT_RIGHT);// 设置左右滑菜单
		menu.setTouchModeAbove(SlidingMenu.SLIDING_WINDOW);// 设置要使菜单滑动，触碰屏幕的范围
		// localSlidingMenu.setTouchModeBehind(SlidingMenu.SLIDING_CONTENT);//设置了这个会获取不到菜单里面的焦点，所以先注释掉
		// menu.setShadowWidthRes(R.dimen.shadow_width);//设置阴影图片的宽度
		// localSlidingMenu.setShadowDrawable(R.drawable.shadow);//设置阴影图片
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);// SlidingMenu划出时主页面显示的剩余宽度
		menu.setFadeDegree(0.35F);// SlidingMenu滑动时的渐变程度
		menu.attachToActivity(activity, SlidingMenu.RIGHT);// 使SlidingMenu附加在Activity右边
		// localSlidingMenu.setBehindWidthRes(R.dimen.left_drawer_avatar_size);//设置SlidingMenu菜单的宽度
		menu.setMenu(R.layout.layout_menu);// 设置menu的布局文件
		// menu.toggle();//动态判断自动关闭或开启SlidingMenu
		menu.setSecondaryMenu(R.layout.menu_item);
		menu.setSecondaryMenuOffsetRes(R.dimen.munuwith);
		// menu.setSecondaryShadowDrawable(R.drawable.shadowright);
		menu.setFadeEnabled(false);
		menu.setBehindScrollScale(0.25f);
		menu.setFadeDegree(0.25f);
		menu.setBackgroundImage(R.drawable.img_frame_background);
		menu.setBehindCanvasTransformer(new SlidingMenu.CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (percentOpen * 0.25 + 0.75);
				canvas.scale(scale, scale, -canvas.getWidth() / 2,
						canvas.getHeight() / 2);
			}
		});

		menu.setAboveCanvasTransformer(new SlidingMenu.CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (1 - percentOpen * 0.25);
				canvas.scale(scale, scale, 0, canvas.getHeight() / 2);
			}
		});

		initData();
		initView();
		return menu;
	}

	private void initView() {
		vf_menu = (ViewFlipper) menu.findViewById(R.id.vf);
		btn_market = (Button) menu.findViewById(R.id.btn_market);
		btn_service = (Button) menu.findViewById(R.id.btn_service);
		tv_update = (TextView) menu.findViewById(R.id.tv_update);
		tv_aboutus = (TextView) menu.findViewById(R.id.tv_aboutus);
		tv_connect = (TextView) menu.findViewById(R.id.tv_connect);
		tv_detail = (TextView) menu.findViewById(R.id.tv_detail);
		myregument  =(TextView) menu.findViewById(R.id.myregument);
		tv_aboutus.setOnClickListener(this);
		tv_connect.setOnClickListener(this);
		tv_detail.setOnClickListener(this);
		tv_update.setOnClickListener(this);
		btn_market.setOnClickListener(this);
		btn_service.setOnClickListener(this);
		myregument.setOnClickListener(this);
		btn_market.performClick();

		goodsCategoryRecyclerView = (ListView) menu
				.findViewById(R.id.goods_category_recyclerview);
		goodsCategoryListAdapter = new GoodsCategoryListAdapter(
				goodsCategoryList);
		goodsCategoryRecyclerView.setAdapter(goodsCategoryListAdapter);
		goodsCategoryRecyclerView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Bundle data = new Bundle();
						data.putString("goodsCategory",
								goodsCategoryList.get(position).getName());
						data.putString("type", type);
						((MainActivity) activity).getTabHost().setCurrentTab(0);
						HomeFragment homefragment = new HomeFragment();
						homefragment.setArguments(data);
						activity.getSupportFragmentManager().beginTransaction()
								.replace(R.id.realtabcontent, homefragment)
								.commit();
						menu.showContent();
					}
				});

		serviceCategoryRecyclerView = (ListView) menu
				.findViewById(R.id.service_category_recyclerview);
		serviceCategoryListAdapter = new GoodsCategoryListAdapter(
				serviceCategoryList);
		serviceCategoryRecyclerView.setAdapter(serviceCategoryListAdapter);
		serviceCategoryRecyclerView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Bundle data = new Bundle();
						data.putString("goodsCategory", serviceCategoryList
								.get(position).getName());
						data.putString("type", type);
						((MainActivity) activity).getTabHost().setCurrentTab(0);
						HomeFragment homefragment = new HomeFragment();
						homefragment.setArguments(data);
						activity.getSupportFragmentManager().beginTransaction()
								.replace(R.id.realtabcontent, homefragment)
								.commit();
						menu.showContent();
					}
				});

	}

	private void initData() {
		goodsCategoryList = new ArrayList<GoodsCategory>();
		String goodsCategoryname[] = { "交通工具", "手机电脑", "电子数码", "图书文具", "运动用品",
				"衣饰鞋帽", "其他物品" };
		String goodsCategorydesc[] = { "自行车/电动车", "苹果/三星/联想", "U盘/相机/电脑椅",
				"书籍/考研资料", "球/健身器材", "首饰/衣服/箱包", "" };
		int goodsCategoryimgId[] = { R.drawable.biker, R.drawable.phone,
				R.drawable.camera, R.drawable.book, R.drawable.sport,
				R.drawable.shirt, R.drawable.other };
		for (int i = 0; i < goodsCategoryimgId.length; i++) {
			GoodsCategory goodsCategory = new GoodsCategory();
			goodsCategory.setName(goodsCategoryname[i]);
			goodsCategory.setDesc(goodsCategorydesc[i]);
			goodsCategory.setImgId(goodsCategoryimgId[i]);
			goodsCategoryList.add(goodsCategory);
		}

		serviceCategoryList = new ArrayList<GoodsCategory>();
		String serviceCategoryname[] = { "学习类", "生活类" };
		String serviceCategorydesc[] = { "代写/海报设计/ppt制作", "摄影/驴友/约饭/约电影" };
		int serviceCategoryimgId[] = { R.drawable.study, R.drawable.life };
		for (int i = 0; i < serviceCategoryimgId.length; i++) {
			GoodsCategory goodsCategory = new GoodsCategory();
			goodsCategory.setName(serviceCategoryname[i]);
			goodsCategory.setDesc(serviceCategorydesc[i]);
			goodsCategory.setImgId(serviceCategoryimgId[i]);
			serviceCategoryList.add(goodsCategory);
		}
	}

	@Override
	public void onClick(View v) {
		// ((MainActivity) getActivity()).getSlidingPaneLayout().closePane();
		switch (v.getId()) {
		case R.id.btn_market:
			Log.i("test", "00000");
			btn_market.setSelected(true);
			btn_service.setSelected(false);
			vf_menu.setDisplayedChild(0);
			type = "true";
			break;
		case R.id.btn_service:
			Log.i("test", "1111");
			btn_market.setSelected(false);
			btn_service.setSelected(true);
			vf_menu.setDisplayedChild(1);
			type = "false";
			break;
		case R.id.tv_update:
			// if (menu.isMenuShowing()) {
			// menu.showContent();
			// } else {
			// menu.showMenu();
			// }
			if (updateListener != null) {
				updateListener.onUpdate();
			}
			break;
		case R.id.tv_aboutus:
			activity.startActivity(new Intent(activity, AboutusActivity.class));
			break;
		case R.id.tv_connect:
			activity.startActivity(new Intent(activity,
					ConnectWithUsActivity.class));
			break;
		case R.id.tv_detail:
			activity.startActivity(new Intent(activity,
					FunIntroduceActivity.class));
			break;
		case R.id.myregument:
			activity.startActivity(new Intent(activity,
					AdviceActivity.class));
			break;
		default:
			break;
		}
	}

	public static interface updateListener {
		public void onUpdate();

	}

}
