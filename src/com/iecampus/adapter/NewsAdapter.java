package com.iecampus.adapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.iecampus.activity.R;
import com.iecampus.moldel.NewsEntity;
import com.iecampus.utils.LruImageCache;
import com.iecampus.utils.StringUtil;
import com.iecampus.utils.VolleyUtil;
import com.iecampus.view.HeadListView;
import com.iecampus.view.HeadListView.HeaderAdapter;


/**
 * 新闻列表适配器
 * 
 * @author lfh
 * 
 */
public class NewsAdapter extends BaseAdapter implements SectionIndexer,
		HeaderAdapter, OnScrollListener {
	List<NewsEntity> newsList;
	Activity activity;
	LayoutInflater inflater = null;
	private ImageLoader imageLoader;
	private String uri = "http://iesunny.gotoip2.com/";

	public NewsAdapter(Activity activity, List<NewsEntity> newsList) {
		this.activity = activity;
		this.newsList = newsList;
		inflater = LayoutInflater.from(activity);
		this.imageLoader = new ImageLoader(VolleyUtil.getQueue(activity),
				new LruImageCache());
		initDateHead();
	}

	private List<Integer> mPositions;
	private List<String> mSections;

	private void initDateHead() {
		mSections = new ArrayList<String>();
		mPositions = new ArrayList<Integer>();
		for (int i = 0; newsList != null && i < newsList.size(); i++) {
			if (i == 0) {
				mSections.add(newsList.get(i).getPublishTime());
				mPositions.add(i);
				continue;
			}
			if (i != newsList.size()) {
				if (!newsList.get(i).getPublishTime()
						.equals(newsList.get(i - 1).getPublishTime())) {
					mSections.add(newsList.get(i).getPublishTime());
					mPositions.add(i);
				}
			}
		}
	}

	@Override
	public int getCount() {
		return newsList == null ? 0 : newsList.size();
	}

	@Override
	public NewsEntity getItem(int position) {
		if (newsList != null && newsList.size() != 0) {
			return newsList.get(position);
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
			view = inflater.inflate(R.layout.item_news,parent,false);
			mHolder = new ViewHolder();
			mHolder.item_title = (TextView) view.findViewById(R.id.item_title);
			mHolder.right_image = (ImageView) view
					.findViewById(R.id.right_image);
			mHolder.item_image_layout = (LinearLayout) view
					.findViewById(R.id.item_image_layout);
			mHolder.item_image_0 = (ImageView) view
					.findViewById(R.id.item_image_0);
			mHolder.item_image_1 = (ImageView) view
					.findViewById(R.id.item_image_1);
			mHolder.item_image_2 = (ImageView) view
					.findViewById(R.id.item_image_2);
			mHolder.large_image = (ImageView) view
					.findViewById(R.id.large_image);
			mHolder.item_contentpre = (TextView) view
					.findViewById(R.id.contentpre);
			mHolder.item_left_contentpre = (TextView) view
					.findViewById(R.id.left_contentpre);
			// 头部的日期部分
			mHolder.layout_list_section = (LinearLayout) view
					.findViewById(R.id.layout_list_section);
			mHolder.section_text = (TextView) view
					.findViewById(R.id.section_text);
			mHolder.section_day = (TextView) view
					.findViewById(R.id.section_day);
			view.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) view.getTag();
		}
		// 获取position对应的数据
		NewsEntity news = getItem(position);
		mHolder.item_title.setText(news.getTitle());
		
		String picList[]=news.getPicList().split(",");
		Log.i("test", "picList==="+picList.length);
		
		// 是否有图片
		if (!news.getPicList().equals("")) {
			// 是否有三张图
			if (picList.length <3) {
				mHolder.item_image_layout.setVisibility(View.GONE);
				// 是否是大图
				if (news.isLarge()==true) {
					mHolder.right_image.setVisibility(View.GONE);
					mHolder.item_left_contentpre.setVisibility(View.GONE);
					mHolder.large_image.setVisibility(View.VISIBLE);
					mHolder.item_contentpre.setVisibility(View.VISIBLE);
					this.setImage(mHolder.large_image, uri + picList[0]);
					mHolder.item_contentpre.setText(news.getContent());
				} else {
					mHolder.large_image.setVisibility(View.GONE);
					mHolder.item_contentpre.setVisibility(View.GONE);
					mHolder.right_image.setVisibility(View.VISIBLE);
					mHolder.item_left_contentpre.setVisibility(View.VISIBLE);
					this.setImage(mHolder.right_image, uri + picList[0]);
					mHolder.item_left_contentpre.setText(news.getContent());
				}
			} else {
				mHolder.large_image.setVisibility(View.GONE);
				mHolder.right_image.setVisibility(View.GONE);
				mHolder.item_left_contentpre.setVisibility(View.GONE);
				mHolder.item_contentpre.setVisibility(View.VISIBLE);
				mHolder.item_image_layout.setVisibility(View.VISIBLE);
				mHolder.item_contentpre.setText(news.getContent());
				this.setImage(mHolder.item_image_0, uri + picList[0]);
				this.setImage(mHolder.item_image_1, uri + picList[1]);
				this.setImage(mHolder.item_image_2, uri + picList[2]);
			}
		} else {
			mHolder.right_image.setVisibility(View.GONE);
			mHolder.item_left_contentpre.setVisibility(View.GONE);
			mHolder.large_image.setVisibility(View.GONE);
			mHolder.item_image_layout.setVisibility(View.GONE);
			mHolder.item_contentpre.setVisibility(View.VISIBLE);
			mHolder.item_contentpre.setText(news.getContent());
		}
		// 头部的日期部分
		int section = getSectionForPosition(position);
		if (getPositionForSection(section) == position) {
			mHolder.layout_list_section.setVisibility(View.VISIBLE);
			mHolder.section_text.setText(mSections.get(section));
			String day = judgeDay(mSections.get(section));
			mHolder.section_day.setText(day);
		} else {
			mHolder.layout_list_section.setVisibility(View.GONE);
		}
		news = null;
		picList=null;
		return view;
	}

	static class ViewHolder {
		/**新闻的标题*/
		TextView item_title;
		/**新闻的内容预览*/
		TextView item_contentpre;
		/**新闻的左侧内容预览*/
		TextView item_left_contentpre;
		/**新闻的右侧图片*/
		ImageView right_image;
		/**新闻列表的子项布局*/
		LinearLayout item_image_layout;
		/**新闻的图片一*/
		ImageView item_image_0;
		/**新闻的图片二*/
		ImageView item_image_1;
		/**新闻的图片三*/
		ImageView item_image_2;
		/**新闻的大图*/
		ImageView large_image;
		/**头部的时间条布局*/
		LinearLayout layout_list_section;
		/** 新闻的发布日期 */
		TextView section_text;
		/** 今天、昨天或者前天 */
		TextView section_day;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	// 滑动监听
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (view instanceof HeadListView) {
			Log.d("first", "first:" + view.getFirstVisiblePosition());
			((HeadListView) view).configureHeaderView(firstVisibleItem);
		}
	}

	@Override
	public int getHeaderState(int position) {
		int realPosition = position;
		if (realPosition < 0 || position >= getCount()) {
			return HEADER_GONE;
		}
		int section = getSectionForPosition(realPosition);
		int nextSectionPosition = getPositionForSection(section + 1);
		if (nextSectionPosition != -1
				&& realPosition == nextSectionPosition - 1) {
			return HEADER_PUSHED_UP;
		}
		return HEADER_VISIBLE;
	}

	@Override
	public void configureHeader(View header, int position, int alpha) {
		int realPosition = position;
		int section = getSectionForPosition(realPosition);
		String title = (String) getSections()[section];
		((TextView) header.findViewById(R.id.section_text)).setText(title);
		String day = judgeDay(title);
		((TextView) header.findViewById(R.id.section_day)).setText(day);
	}

	@Override
	public Object[] getSections() {
		return mSections.toArray();
	}

	@Override
	public int getPositionForSection(int sectionIndex) {
		if (sectionIndex < 0 || sectionIndex >= mPositions.size()) {
			return -1;
		}
		return mPositions.get(sectionIndex);
	}

	@Override
	public int getSectionForPosition(int position) {
		if (position < 0 || position >= getCount()) {
			return -1;
		}
		int index = Arrays.binarySearch(mPositions.toArray(), position);
		return index >= 0 ? index : -index - 2;
	}
	
	/**
	 * 判断是今天、昨天还是前天
	 * @param publishdate 发布日期
	 * @return
	 */
	public String judgeDay(String publishdate) {
		Date now = new Date();
		DateFormat df = DateFormat.getDateInstance();
		String nowDate = df.format(now);
		try {
			long between = df.parse(nowDate).getTime()
					- df.parse(publishdate).getTime();
			long day = between / 1000 / 60 / 60 / 24;
			if (day == 0) {
				return "今天";
			} else if (day == 1) {
				return "昨天";
			} else if (day == 2) {
				return "前天";
			}
		} catch (ParseException e) {
		}
		return "";
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
