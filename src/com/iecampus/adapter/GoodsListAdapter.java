package com.iecampus.adapter;

import java.util.List;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.iecampus.activity.R;
import com.iecampus.moldel.Goods;
import com.iecampus.utils.LruImageCache;
import com.iecampus.utils.StringUtil;
import com.iecampus.utils.VolleyUtil;

public class GoodsListAdapter extends BaseAdapter {

	private List<Goods> list;
	private Activity activity;
	private ImageLoader imageLoader;

	public class ViewHolder {
		private ImageView img_left;
		private TextView price_left;
		private TextView tv_place;
		private TextView item_title_left;
		private TextView browsenumber;
		private ImageView icon_shou, icon_qiu;
	}

	public GoodsListAdapter(List<Goods> list, Activity activity) {
		this.list = list;
		this.activity = activity;
		this.imageLoader = new ImageLoader(VolleyUtil.getQueue(activity),
				new LruImageCache());
	}

	@Override
	public int getCount() {
		if (list == null) {
			return 0;
		} else {
			return list.size();
		}
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int positon, View convertView, ViewGroup parent) {
		ViewHolder viewholder;
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = LayoutInflater.from(activity);
			view = inflater.inflate(R.layout.listview_item, null);
			viewholder = new ViewHolder();
			viewholder.img_left = (ImageView) view
					.findViewById(R.id.goodsimage_left);
			viewholder.tv_place = (TextView) view.findViewById(R.id.tv_place);
			viewholder.price_left = (TextView) view
					.findViewById(R.id.price_left);
			viewholder.item_title_left = (TextView) view
					.findViewById(R.id.item_title_left);
			viewholder.icon_shou = (ImageView) view
					.findViewById(R.id.icon_shou);
			viewholder.icon_qiu = (ImageView) view.findViewById(R.id.icon_qiu);
			viewholder.browsenumber = (TextView) view
					.findViewById(R.id.browsenumber);
			view.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) view.getTag();
		}
		Goods goods = list.get(positon);
		Log.i("test",
				"positon===" + positon + "    imagepath==="
						+ goods.getGoods_imagepath());

		if (goods.getGoods_imagepath() != null) {
			this.setImage(viewholder.img_left,
					activity.getString(R.string.hostAddress)
							+ goods.getGoods_imagepath().split(",")[0]);
		} else {
			viewholder.img_left.setImageResource(R.drawable.ic_launcher);
		}

		if (goods.getState()) {
			viewholder.icon_qiu.setVisibility(View.VISIBLE);
			viewholder.icon_shou.setVisibility(View.GONE);
		} else {
			viewholder.icon_shou.setVisibility(View.VISIBLE);
			viewholder.icon_qiu.setVisibility(View.GONE);
		}
		viewholder.item_title_left.setText(goods.getGoods_name());
		viewholder.tv_place.setText(goods.getRequirements());
		viewholder.price_left.setText("￥" + goods.getPrice() + "");
		viewholder.browsenumber.setText(goods.getBrowsenumber() + "人浏览");
		return view;
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
