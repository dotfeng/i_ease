package com.iecampus.adapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iecampus.activity.R;
import com.iecampus.moldel.GoodsCategory;

public class GoodsCategoryListAdapter extends BaseAdapter {
	private List<GoodsCategory> goodsCategoryList;

	public GoodsCategoryListAdapter(List<GoodsCategory> goodsCategoryList) {
		this.goodsCategoryList = goodsCategoryList;
	}

	public class ViewHolder {
		public TextView tv_categoryname;
		public TextView tv_categorydesc;
		public ImageView img_category;
	}

	@Override
	public int getCount() {
		return goodsCategoryList.size();
	}

	@Override
	public GoodsCategory getItem(int position) {
		if (goodsCategoryList != null && goodsCategoryList.size() != 0) {
			return goodsCategoryList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder;
		View view = convertView;
		if (view == null) {
			view = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.item_goods_category, parent, false);
			mHolder = new ViewHolder();
			mHolder.tv_categoryname = (TextView) view
					.findViewById(R.id.tv_categoryname);
			mHolder.tv_categorydesc = (TextView) view
					.findViewById(R.id.tv_categorydesc);
			mHolder.img_category = (ImageView) view
					.findViewById(R.id.img_category);
			view.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) view.getTag();
		}
		GoodsCategory goodsCategory = getItem(position);
		mHolder.tv_categoryname.setText(goodsCategory.getName());
		mHolder.tv_categorydesc.setText(goodsCategory.getDesc());
		mHolder.img_category.setBackgroundResource(goodsCategory.getImgId());
		return view;
	}

}
