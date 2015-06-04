package com.iecampus.adapter;

import java.util.List;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.iecampus.activity.R;
import com.iecampus.adapter.GoodsListAdapter.ViewHolder;
import com.iecampus.moldel.Goods;
import com.iecampus.utils.LruImageCache;
import com.iecampus.utils.StringUtil;
import com.iecampus.utils.VolleyUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PublishedAdapter extends BaseAdapter {
	private List<Goods> list;
	private Context context;
	private ImageLoader imageLoader;
	private String url = "http://iesunny.gotoip2.com/";
	private PublishAction publishaction;

	public PublishedAdapter(List<Goods> list, Context context,
			PublishAction publishaction) {
		this.list = list;
		this.context = context;
		this.publishaction = publishaction;
		this.imageLoader = new ImageLoader(VolleyUtil.getQueue(context),
				new LruImageCache());
	}

	public class ViewHolder {
		private ImageView image;
		private TextView title;
		private TextView price;
		private TextView browse;
		private TextView state;
		private Button update;
		private Button complete;
		private ImageView signed;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Goods getItem(int position) {

		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int positon, View convertView, ViewGroup parent) {
		final ViewHolder viewholder;
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.published_listitem, parent,
					false);
			viewholder = new ViewHolder();
			viewholder.image = (ImageView) convertView.findViewById(R.id.img);
			viewholder.title = (TextView) convertView.findViewById(R.id.title);
			viewholder.price = (TextView) convertView.findViewById(R.id.price);
			viewholder.state = (TextView) convertView.findViewById(R.id.state);
			viewholder.browse = (TextView) convertView
					.findViewById(R.id.browse);
			viewholder.update = (Button) convertView.findViewById(R.id.update);
			viewholder.complete = (Button) convertView
					.findViewById(R.id.complete);
			viewholder.signed = (ImageView) convertView
					.findViewById(R.id.signed);
			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}
		this.setImage(viewholder.image, url
				+ list.get(positon).getGoods_imagepath().split(",")[0]);
		viewholder.title.setText(list.get(positon).getGoods_name());
		viewholder.price.setText("￥" + list.get(positon).getPrice());
		viewholder.browse.setText(list.get(positon).getBrowsenumber() + "人浏览");
		if (list.get(positon).getState()) {
			viewholder.state.setText("求购");
		} else {
			viewholder.state.setText("出售");
		}
		final int temp = positon;
		if (list.get(positon).isIssale()) {
			viewholder.signed.setVisibility(View.VISIBLE);
			viewholder.complete.setClickable(false);
			viewholder.update.setClickable(false);
		} else {
			viewholder.signed.setVisibility(View.GONE);
			viewholder.complete.setClickable(true);
			viewholder.update.setClickable(true);
			viewholder.update.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					publishaction.OnDeleteClick(list.get(temp).getGid(),temp);
				}
			});
			viewholder.complete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					publishaction.OnCompleteClick(list.get(temp).getGid());
				}
			});
		}
		return convertView;
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

	public interface PublishAction {
		public void OnDeleteClick(int gid,int position);

		public void OnCompleteClick(int gid);
	}
}
