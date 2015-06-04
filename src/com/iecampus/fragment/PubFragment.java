package com.iecampus.fragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iecampus.activity.LoginActivity;
import com.iecampus.activity.R;
import com.iecampus.adapter.PubCategoryListViewAdapter;
import com.iecampus.mulpickphotos.Bimp;
import com.iecampus.mulpickphotos.FileUtils;
import com.iecampus.mulpickphotos.PhotoAlbumListActivity;
import com.iecampus.mulpickphotos.PhotoPreviewActivity;
import com.iecampus.utils.Constants;
import com.iecampus.utils.PreferenceUtils;
import com.iecampus.utils.ToastUtil;
import com.iecampus.utils.UploadFile;
import com.iecampus.utils.UtilTools;
import com.iecampus.view.MyProgressDialog;

public class PubFragment extends Fragment implements OnClickListener {
	private View rootView;// 缓存Fragment view
	private GridView noScrollgridview;
	private GridAdapter adapter;
	private PopupWindow popupWindow;
	private PopupWindow CategorypopupWindow;// 选择分类选择的对话框
	private TextView btn_pub;
	private List<String> pathList;
	/** 选择求购或者出售的单选按钮组 */
	private RadioGroup radiogroup_buyorsell;
	/** 发布的商品类型的下拉菜单 */
	private TextView sp_goods_pub_type;
	/** 输入标题的文本框 */
	private EditText edt_goods_pub_title;
	/** 输入交易地点的文本框 */
	private EditText edt_goods_pub_place;
	/** 输入交易价格文本框 */
	private EditText edt_goods_pub_price;
	/** 输入附加内容的文本框 */
	private EditText edt_goods_pub_content;
	/** 交易地点的标签 */
	private TextView tv_goods_pub_place;
	/** 加载数据的进度条 */
	private ProgressDialog progressDialog;
	private String pubstate = "true";
	private List<String> maincategory;
	private List<String> goodscategory;
	private List<String> servicecategory;
	private PubCategoryListViewAdapter goodsadaper, serviceadapter;
	private String type = null, category = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("test", "PubFragment----onCreateView");
		if (rootView != null) {
			ViewGroup parent = (ViewGroup) rootView.getParent();
			if (parent != null) {
				parent.removeView(rootView);
			}
			return rootView;
		}
		/** 分类菜单初始化 **/
		maincategory = new ArrayList<String>();
		goodscategory = new ArrayList<String>();
		servicecategory = new ArrayList<String>();
		maincategory.add("二手物品");
		maincategory.add("校园服务");

		goodscategory.add("手机电脑");
		goodscategory.add("交通工具");
		goodscategory.add("电子数码");
		goodscategory.add("图书文具");
		goodscategory.add("运动用品");
		goodscategory.add("衣饰鞋帽");
		goodscategory.add("其他物品");

		servicecategory.add("学习类");
		servicecategory.add("生活类");
		/** 分类菜单初始化 **/

		rootView = LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_pub, null);

		radiogroup_buyorsell = (RadioGroup) rootView
				.findViewById(R.id.rg_buyorsell);
		sp_goods_pub_type = (TextView) rootView
				.findViewById(R.id.sp_goods_pub_type);
		edt_goods_pub_title = (EditText) rootView
				.findViewById(R.id.edt_goods_pub_title);
		edt_goods_pub_place = (EditText) rootView
				.findViewById(R.id.edt_goods_pub_place);
		edt_goods_pub_price = (EditText) rootView
				.findViewById(R.id.edt_goods_pub_price);
		edt_goods_pub_content = (EditText) rootView
				.findViewById(R.id.edt_goods_pub_content);
		tv_goods_pub_place = (TextView) rootView
				.findViewById(R.id.tv_goods_pub_place);

		progressDialog = new MyProgressDialog(getActivity());

		noScrollgridview = (GridView) rootView
				.findViewById(R.id.noScrollgridview);
		btn_pub = (TextView) rootView.findViewById(R.id.btn_pub);
		btn_pub.setOnClickListener(this);
		sp_goods_pub_type.setOnClickListener(this);
		noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new GridAdapter(getActivity());
		// adapter.update();
		noScrollgridview.setAdapter(adapter);
		noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 == Bimp.bmp.size()) {
					showPopupWindow();
					// new PopupWindows(getActivity(), noScrollgridview);
				} else {
					Intent intent = new Intent(getActivity(),
							PhotoPreviewActivity.class);
					intent.putExtra("ID", arg2);
					startActivity(intent);
				}
			}
		});

		radiogroup_buyorsell
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup arg0, int arg1) {
						if (arg1 == R.id.rbtn_buy) {
							pubstate = "true";
						} else {
							pubstate = "false";
						}

					}
				});
		return rootView;
	}

	@SuppressLint("HandlerLeak")
	public class GridAdapter extends BaseAdapter {
		private LayoutInflater inflater; // 视图容器
		private int selectedPosition = -1;// 选中的位置
		private boolean shape;

		public boolean isShape() {
			return shape;
		}

		public void setShape(boolean shape) {
			this.shape = shape;
		}

		public GridAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		public void update() {
			loading();
		}

		public int getCount() {
			return (Bimp.bmp.size() + 1);
		}

		public Object getItem(int arg0) {

			return null;
		}

		public long getItemId(int arg0) {

			return 0;
		}

		public void setSelectedPosition(int position) {
			selectedPosition = position;
		}

		public int getSelectedPosition() {
			return selectedPosition;
		}

		/**
		 * ListView Item设置
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			final int coord = position;
			ViewHolder holder = null;
			if (convertView == null) {

				convertView = inflater.inflate(
						R.layout.item_published_gridview, parent, false);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView
						.findViewById(R.id.item_grida_image);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position == Bimp.bmp.size()) {
				holder.image.setImageBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.goods_photo_btn_add_click));
				if (position == 9) {
					holder.image.setVisibility(View.GONE);
				}
			} else {
				holder.image.setImageBitmap(Bimp.bmp.get(position));
			}

			return convertView;
		}

		public class ViewHolder {
			public ImageView image;
		}

		Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					adapter.notifyDataSetChanged();
					break;
				}
				super.handleMessage(msg);
			}
		};

		public void loading() {
			new Thread(new Runnable() {
				public void run() {
					while (true) {
						if (Bimp.max == Bimp.drr.size()) {
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
							break;
						} else {
							try {
								String path = Bimp.drr.get(Bimp.max);
								System.out.println(path);
								Bitmap bm = Bimp.revitionImageSize(path);
								Bimp.bmp.add(bm);
								String newStr = path.substring(
										path.lastIndexOf("/") + 1,
										path.lastIndexOf("."));
								FileUtils.saveBitmap(bm, "" + newStr);
								Bimp.max += 1;
								Message message = new Message();
								message.what = 1;
								handler.sendMessage(message);
							} catch (IOException e) {

								e.printStackTrace();
							}
						}
					}
				}
			}).start();
		}
	}

	public String getString(String s) {
		String path = null;
		if (s == null)
			return "";
		for (int i = s.length() - 1; i > 0; i++) {
			s.charAt(i);
		}
		return path;
	}

	@Override
	public void onResume() {
		Log.i("test", "onResume");
		adapter.update();
		super.onResume();
	}

	/**
	 * 弹出选择图片来源的对话框
	 */
	private void showPopupWindow() {
		View view = (RelativeLayout) LayoutInflater.from(getActivity())
				.inflate(R.layout.layout_select_photos_popmenu, null);
		TextView camera = (TextView) view
				.findViewById(R.id.message_popmenu_camera);
		TextView photos = (TextView) view
				.findViewById(R.id.message_popmenu_photos);
		TextView cancel = (TextView) view
				.findViewById(R.id.message_popmenu_cancel);
		camera.setText("拍照");
		photos.setText("相册");
		camera.setOnClickListener(this);
		photos.setOnClickListener(this);
		cancel.setOnClickListener(this);
		if (popupWindow == null) {
			popupWindow = new PopupWindow(getActivity());
		}
		popupWindow.setFocusable(true); // 设置PopupWindow可获得焦点
		popupWindow.setTouchable(true); // 设置PopupWindow可触摸
		popupWindow.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
		popupWindow.setContentView(view);
		popupWindow.setWidth(LayoutParams.MATCH_PARENT); // 设置SelectPicPopupWindow弹出窗体的宽
		popupWindow.setHeight(450); // 设置SelectPicPopupWindow弹出窗体的高
		popupWindow.setAnimationStyle(R.style.AnimBottom); // 设置SelectPicPopupWindow弹出窗体动画效果

		ColorDrawable dw = new ColorDrawable(0xb0000000); // 实例化一个ColorDrawable颜色为半透明
		popupWindow.setBackgroundDrawable(dw); // 设置SelectPicPopupWindow弹出窗体的背景
		popupWindow.showAtLocation(
				getActivity().findViewById(R.id.content_layout), Gravity.BOTTOM
						| Gravity.CENTER_HORIZONTAL, 0, 0);
		popupWindow.update();
	}

	private void showCategoryPopupWindow() {
		View view = (LinearLayout) LayoutInflater.from(getActivity()).inflate(
				R.layout.pub_category_menu, null);
		ListView categoty = (ListView) view.findViewById(R.id.category);
		final ListView catedetail = (ListView) view
				.findViewById(R.id.categorydetail);
		goodsadaper = new PubCategoryListViewAdapter(maincategory,
				view.getContext());
		categoty.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {

				case 0:
					serviceadapter = new PubCategoryListViewAdapter(
							goodscategory, view.getContext());
					catedetail.setAdapter(serviceadapter);
					type = goodsadaper.getItem(position);
					serviceadapter.notifyDataSetChanged();
					break;
				case 1:
					serviceadapter = new PubCategoryListViewAdapter(
							servicecategory, view.getContext());
					catedetail.setAdapter(serviceadapter);
					type = goodsadaper.getItem(position);
					serviceadapter.notifyDataSetChanged();
					break;
				default:
					break;
				}

			}
		});
		catedetail.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				category = serviceadapter.getItem(position);
				sp_goods_pub_type.setText(type + " -> " + category);
				CategorypopupWindow.dismiss();

			}
		});
		categoty.setAdapter(goodsadaper);
		if (CategorypopupWindow == null) {
			CategorypopupWindow = new PopupWindow(getActivity());
		}

		CategorypopupWindow.setFocusable(true); // 设置PopupWindow可获得焦点
		CategorypopupWindow.setTouchable(true); // 设置PopupWindow可触摸
		CategorypopupWindow.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
		CategorypopupWindow.setContentView(view);
		CategorypopupWindow.setWidth(LayoutParams.WRAP_CONTENT); // 设置SelectPicPopupWindow弹出窗体的宽
		CategorypopupWindow.setHeight(550); // 设置SelectPicPopupWindow弹出窗体的高
		// popupWindow.setAnimationStyle(R.style.AnimBottom); //
		// 设置SelectPicPopupWindow弹出窗体动画效果

		ColorDrawable dw = new ColorDrawable(0xb0C0C0C0); // 实例化一个ColorDrawable颜色为半透明
		CategorypopupWindow.setBackgroundDrawable(dw); // 设置SelectPicPopupWindow弹出窗体的背景
		CategorypopupWindow.showAtLocation(
				getActivity().findViewById(R.id.content_layout), Gravity.CENTER
						| Gravity.CENTER_HORIZONTAL, 0, 0);
		CategorypopupWindow.update();

	}

	private static final int TAKE_PICTURE = 0x000000;
	private String path = "";

	public void takePhoto() {
		File lcoalfile = new File(Environment.getExternalStorageDirectory(),
				"/myimage/");// 外存根目录下的文件夹
		File dir = new File(lcoalfile.getPath());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		// 打开照相机
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		File file = new File(Environment.getExternalStorageDirectory()
				+ "/myimage/", String.valueOf(System.currentTimeMillis())
				+ ".jpg");
		path = file.getPath();
		// 保存图片时的路径和文件名
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case TAKE_PICTURE:
			Log.i("test", "Bimp.drr.size()======" + Bimp.drr.size());
			Log.i("test", "path=====" + path);
			if (Bimp.drr.size() < 9 && resultCode == -1) {
				Bimp.drr.add(path);
				Log.i("test", "dddddd=====" + Bimp.drr.get(0));
			}
			Log.i("test", "eeeeeeeeeeeeeeeeee");
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.message_popmenu_camera:
			popupWindow.dismiss();
			takePhoto();
			break;
		case R.id.message_popmenu_photos:
			popupWindow.dismiss();
			Intent intent = new Intent(getActivity(),
					PhotoAlbumListActivity.class);
			startActivity(intent);
			break;
		case R.id.message_popmenu_cancel:
			popupWindow.dismiss();
			break;
		case R.id.btn_pub:
			if (checkISEmpty()) {
				progressDialog.show();
				uploadPhotos();
			}
			break;
		case R.id.sp_goods_pub_type:
			showCategoryPopupWindow();
			break;
		default:
			break;
		}

	}

	private boolean checkISEmpty() {
		if (!CheckSign()) {
			ShowLoginDialog();
		} else if (TextUtils.isEmpty(edt_goods_pub_title.getText().toString()
				.trim())) {
			ToastUtil.showToast(getActivity(), "亲，你忘记填标题了哦");
		} else if (TextUtils.isEmpty(edt_goods_pub_place.getText().toString()
				.trim())) {
			ToastUtil.showToast(getActivity(), "亲，你忘记填交易地点了哦");
		} else if (TextUtils.isEmpty(edt_goods_pub_price.getText().toString()
				.trim())) {
			ToastUtil.showToast(getActivity(), "亲，你忘记填交易价格了哦");
		} else if (TextUtils.isEmpty(type) || TextUtils.isEmpty(category)) {
			ToastUtil.showToast(getActivity(), "亲，你忘记选择分类了哦");
		} else {
			return true;
		}
		return false;
	}

	private void submitData() {
		String url = getString(R.string.hostAddress) + "goods_publish";
		String imagepath = "";
		// /storage/sdcard0/formats/-1.952e_09.JPEG
		for (String path : pathList) {
			imagepath = imagepath + "Image" + path.split("formats")[1] + ",";
		}
		Log.i("test", imagepath);
		AjaxParams params = new AjaxParams();
		int uid = PreferenceUtils
				.getPrefInt(getActivity(), Constants.USERID, 0);
		params.put("goods.uid", String.valueOf(uid));
		params.put("goods.isgoods", type.equals("二手物品") ? "true" : "false");
		params.put("goods.state", pubstate);
		params.put("goods.goods_name", edt_goods_pub_title.getText().toString()
				.trim());
		params.put("goods.price", edt_goods_pub_price.getText().toString()
				.trim());
		params.put("goods.goods_school", PreferenceUtils.getPrefString(
				getActivity(), Constants.SCHOOL, ""));
		params.put("goods.requirements", edt_goods_pub_place.getText()
				.toString().trim());
		params.put("goods.category", category);
		String detail = edt_goods_pub_content.getText().toString().trim();
		if (TextUtils.isEmpty(detail)) {
			detail = "亲，联系我时就说在I易校园上看到的。";
		}
		params.put("goods.detail", detail);
		params.put("goods.date", UtilTools.getDate());
		params.put("goods.goods_imagepath", imagepath);
		FinalHttp fb = new FinalHttp();
		fb.post(url, params, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String t) {
				if (t.toString().equals("true")) {
					Log.i("test", "发表成功");
					int number_published = PreferenceUtils.getPrefInt(
							getActivity(), Constants.PUBLISHNUMBER, 0);
					PreferenceUtils.setPrefInt(getActivity(),
							Constants.PUBLISHNUMBER, ++number_published);
					ToastUtil.showToast(getActivity(), "发表成功");
					progressDialog.dismiss();
					clearText();
				}
				super.onSuccess(t);
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				ToastUtil.showToast(getActivity(), "发表失败");
				progressDialog.dismiss();
			}
		});

	}

	private void uploadPhotos() {
		pathList = new ArrayList<String>();
		for (int i = 0; i < Bimp.drr.size(); i++) {
			String Str = Bimp.drr.get(i).substring(
					Bimp.drr.get(i).lastIndexOf("/") + 1,
					Bimp.drr.get(i).lastIndexOf("."));
			pathList.add(FileUtils.SDPATH + Str + ".JPEG");
		}
		for (String string : pathList) {
			Log.i("test", "list===" + string);
		}

		AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				for (String filePath : pathList) {
					try {
						new UploadFile(getActivity(), new File(filePath),
								"/Goods/Image", "goods_uploadimage", -1);
					} catch (Exception e) {
						// e.printStackTrace();
						return "error";
					}
					Log.i("test", filePath + "图片上传成功！");
				}
				return "success";
			}

			@Override
			protected void onPostExecute(String result) {
				if (result == "success") {
					submitData();
					// 高清的压缩图片全部就在pathList 路径里面了
					// 高清的压缩过的 bmp 对象 都在 Bimp.bmp里面
					// 完成上传服务器后 .........
					FileUtils.deleteDir();
				} else {
					progressDialog.dismiss();
					ToastUtil.showToast(getActivity(), "图片上传失败");
				}

			}

		};
		task.execute();

	}

	private void clearText() {
		edt_goods_pub_title.setText("");
		edt_goods_pub_place.setText("");
		edt_goods_pub_content.setText("");
		edt_goods_pub_price.setText("");
		sp_goods_pub_type.setText("");
		type = null;
		category = null;
		Bimp.bmp.clear();
		Bimp.drr.clear();
		Bimp.max = 0;
		adapter.update();
	}

	public boolean CheckSign() {
		String username = PreferenceUtils.getPrefString(getActivity(),
				Constants.USERNAME, "");
		if (username.equals("") || username == null) {
			return false;
		}
		return true;

	}

	public void ShowLoginDialog() {
		AlertDialog dialog = new AlertDialog.Builder(getActivity())
				.setMessage("登录后才可以发布哦，现在登录？")
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(getActivity(),
								LoginActivity.class);
						startActivity(intent);
						getActivity().finish();
					}
				}).create();
		dialog.show();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.i("test", "PubFragment----onDestroyView");
	}

}
