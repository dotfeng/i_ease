package com.iecampus.fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.iecampus.activity.CollectionActivity;
import com.iecampus.activity.LoginActivity;
import com.iecampus.activity.PublishedActivity;
import com.iecampus.activity.R;
import com.iecampus.activity.SerchActivity;
import com.iecampus.clipheadphoto.CutRoundHeadActivity;
import com.iecampus.dialog.SweetAlertDialog;
import com.iecampus.dialog.SweetAlertDialog.OnSweetClickListener;
import com.iecampus.utils.Constants;
import com.iecampus.utils.LruImageCache;
import com.iecampus.utils.PreferenceUtils;
import com.iecampus.utils.StringUtil;
import com.iecampus.utils.ToastUtil;
import com.iecampus.utils.UploadFile;
import com.iecampus.utils.VolleyUtil;
import com.iecampus.view.MyProgressDialog;

public class MeFragment extends Fragment implements OnClickListener {
	private ImageView userimg;// 用户头像
	private Button change_userimg, change_school;
	private View rootView;// 缓存Fragment view
	private RelativeLayout rl_collections, rl_published;
	private LinearLayout noresult, userinfo;
	private TextView username, tel, email, tv_number_shoucang, number_publish;
	private Button login, loginout, change_password;
	private PopupWindow popupWindow;
	public static final int PHOTOZOOM = 0; // 相册/拍照
	public static final int PHOTOTAKE = 1; // 相册/拍照
	public static final int IMAGE_COMPLETE = 2; // 结果
	public static final int CROPREQCODE = 3; // 截取
	private String photoSavePath;// 保存路径
	private String photoSaveName;// 图pian名
	private String path;// 图片全路径
	private ProgressDialog progressDialog;
	private ImageLoader imageLoader;
	private boolean isDetached = true;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		isDetached = false;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		isDetached = true;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = LayoutInflater.from(getActivity()).inflate(
					R.layout.fragment_me, null);
			this.imageLoader = new ImageLoader(
					VolleyUtil.getQueue(getActivity()), new LruImageCache());
			progressDialog = new MyProgressDialog(getActivity());

			File file = new File(Environment.getExternalStorageDirectory(),
					"ClipHeadPhoto/cache");
			if (!file.exists())
				file.mkdirs();
			photoSavePath = Environment.getExternalStorageDirectory()
					+ "/ClipHeadPhoto/cache/";
			photoSaveName = System.currentTimeMillis() + ".png";

			this.userimg = (ImageView) rootView.findViewById(R.id.userimg);
			this.change_userimg = (Button) rootView
					.findViewById(R.id.change_userimg);
			this.change_school = (Button) rootView
					.findViewById(R.id.change_school);
			this.tv_number_shoucang = (TextView) rootView
					.findViewById(R.id.number_shoucang);
			rl_collections = (RelativeLayout) rootView
					.findViewById(R.id.rl_collections);
			rl_published = (RelativeLayout) rootView
					.findViewById(R.id.rl_published);
			username = (TextView) rootView.findViewById(R.id.username);
			tel = (TextView) rootView.findViewById(R.id.tel);
			email = (TextView) rootView.findViewById(R.id.email);
			noresult = (LinearLayout) rootView.findViewById(R.id.noresult);
			userinfo = (LinearLayout) rootView.findViewById(R.id.userinfo);
			login = (Button) rootView.findViewById(R.id.login);
			loginout = (Button) rootView.findViewById(R.id.loginout);
			change_password = (Button) rootView
					.findViewById(R.id.change_password);
			number_publish = (TextView) rootView
					.findViewById(R.id.number_publish);
			String name = PreferenceUtils.getPrefString(getActivity(),
					Constants.USERNAME, "");
			String phone = PreferenceUtils.getPrefString(getActivity(),
					Constants.TEL, "");
			String emailtext = PreferenceUtils.getPrefString(getActivity(),
					Constants.EMAIL, "");
			if (!name.equals("")) {
				this.username.setText(name);
				this.tel.setText("手机:" + phone);
				this.email.setText("邮箱：" + emailtext);
				rl_collections.setOnClickListener(this);
				rl_published.setOnClickListener(this);
				this.loginout.setOnClickListener(this);
				this.change_userimg.setOnClickListener(this);
				this.userimg.setOnClickListener(this);
				this.change_password.setOnClickListener(this);
			} else {
				userinfo.setVisibility(View.GONE);
				noresult.setVisibility(View.VISIBLE);
			}
			this.change_school.setOnClickListener(this);
			this.login.setOnClickListener(this);

		}
		// 缓存的rootView需要判断是否已经被加过parent，
		// 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		return rootView;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.userimg:
			showPopupWindow();
			break;
		// case R.id.seting:
		// getActivity().menu.toggle();
		// break;
		case R.id.change_userimg:
			showPopupWindow();
			break;
		case R.id.change_school:
			Intent intent2 = new Intent(getActivity(), SerchActivity.class);
			intent2.putExtra("status", "change_scholl");
			startActivity(intent2);
			break;
		case R.id.rl_collections:
			startActivity(new Intent(getActivity(), CollectionActivity.class));
			break;
		case R.id.rl_published:
			startActivity(new Intent(getActivity(), PublishedActivity.class));
			break;
		case R.id.login:
			startActivity(new Intent(getActivity(), LoginActivity.class));
			this.getActivity().finish();
			break;
		case R.id.loginout:
			PreferenceUtils.clearPreference(this.getActivity(),
					PreferenceManager.getDefaultSharedPreferences(this
							.getActivity()));
			PreferenceUtils.setPrefBoolean(getActivity(),
					Constants.ISFIRSTlOGIN, false);
			startActivity(new Intent(getActivity(), LoginActivity.class));
			this.getActivity().finish();
			break;
		case R.id.change_password:
			showDialog();

			break;
		case R.id.message_popmenu_camera:
			popupWindow.dismiss();
			photoSaveName = String.valueOf(System.currentTimeMillis()) + ".png";
			Uri imageUri = null;
			Intent openCameraIntent = new Intent(
					MediaStore.ACTION_IMAGE_CAPTURE);
			imageUri = Uri.fromFile(new File(photoSavePath, photoSaveName));
			openCameraIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
			openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			startActivityForResult(openCameraIntent, PHOTOTAKE);
			break;
		case R.id.message_popmenu_photos:
			popupWindow.dismiss();
			Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
			openAlbumIntent.setDataAndType(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
			startActivityForResult(openAlbumIntent, PHOTOZOOM);
			break;
		case R.id.message_popmenu_cancel:
			popupWindow.dismiss();
			break;
		default:
			break;
		}

	}

	private void showDialog() {
		LayoutInflater factory = LayoutInflater.from(getActivity());
		View view = factory.inflate(R.layout.fragment_me_changepwd, null);
		final TextView pwd;
		final TextView confirm;
		pwd = (TextView) view.findViewById(R.id.pwd);
		confirm = (TextView) view.findViewById(R.id.confirmPwd);		
		Dialog dialog = new AlertDialog.Builder(getActivity()).setView(view)
				.setNeutralButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							Field field = dialog.getClass().getSuperclass()
									.getDeclaredField("mShowing");
							field.setAccessible(true);
							field.set(dialog, false);
						} catch (Exception e) {
							e.printStackTrace();
						}
						changePwdNetwork(pwd.getText().toString(), confirm
								.getText().toString(), dialog);
					}
				}).setNegativeButton("取消", null).create();
		dialog.show();
	}

	protected void changePwdNetwork(String pwd, String confrim,
			DialogInterface dialog) {
		if ("".equals(pwd) || "".equals(confrim)) {
			ToastUtil.showToast(getActivity(), "你还有没输入！");
		} else if (!pwd.equals(confrim)) {
			ToastUtil.showToast(getActivity(), "两次输入不一致");
			return;
		} else if (pwd.length() < 6) {
			ToastUtil.showToast(getActivity(), "密码长度小于6");
			return;
		} else {
			changepwd(pwd);
			try {
				Field field = dialog.getClass().getSuperclass()
						.getDeclaredField("mShowing");
				field.setAccessible(true);
				field.set(dialog, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void changepwd(String pwd) {
		progressDialog.show();
		String url = getString(R.string.hostAddress) + "user_updatePassword";
		AjaxParams params = new AjaxParams();
		params.put("userid",
				PreferenceUtils.getPrefInt(getActivity(), Constants.USERID, 0)
						+ "");
		params.put("pwd", pwd);

		FinalHttp fh = new FinalHttp();
		fh.get(url, params, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String state) {
				if (Boolean.parseBoolean(state)) {
					ToastUtil.showToast(getActivity(), "密码修改成功！");
				} else {
					ToastUtil.showToast(getActivity(), "密码修改失败！");
				}
				progressDialog.dismiss();
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				ToastUtil.showToast(getActivity(), "网络异常！");
				progressDialog.dismiss();
			}
		});

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

	/**
	 * 图片选择及拍照结果
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != getActivity().RESULT_OK) {
			return;
		}
		Uri uri = null;
		switch (requestCode) {
		case PHOTOZOOM:// 相册
			if (data == null) {
				return;
			}
			uri = data.getData();
			String[] proj = { MediaStore.Images.Media.DATA };
			Cursor cursor = getActivity().managedQuery(uri, proj, null, null,
					null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			path = cursor.getString(column_index);// 图片在的路径
			Intent intent3 = new Intent(getActivity(),
					CutRoundHeadActivity.class);
			intent3.putExtra("path", path);
			startActivityForResult(intent3, IMAGE_COMPLETE);
			break;
		case PHOTOTAKE:// 拍照
			path = photoSavePath + photoSaveName;
			uri = Uri.fromFile(new File(path));
			Intent intent2 = new Intent(getActivity(),
					CutRoundHeadActivity.class);
			intent2.putExtra("path", path);
			startActivityForResult(intent2, IMAGE_COMPLETE);
			break;
		case IMAGE_COMPLETE:
			progressDialog.show();
			final String temppath = data.getStringExtra("path");
			Log.i("test", "temppath=======" + temppath);
			userimg.setImageBitmap(getLoacalBitmap(temppath));
			uploadPhotos(temppath);
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * @param url
	 * @return
	 */
	public static Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void uploadPhotos(final String filePath) {

		AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				try {
					new UploadFile(getActivity(), new File(filePath),
							"/User/Image", "user_uploadimage",
							PreferenceUtils.getPrefInt(getActivity(),
									Constants.USERID, 1));
				} catch (Exception e) {
					return "error";
				}
				Log.i("test", filePath + "图片上传成功！");
				return "success";
			}

			@Override
			protected void onPostExecute(String result) {
				if (isDetached) {
					return;
				}
				if (result == "success") {
					ToastUtil.showToast(getActivity(), "图片上传成功");
				} else {
					ToastUtil.showToast(getActivity(), "图片上传失败");
				}
				progressDialog.dismiss();
			}

		};
		task.execute();

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
				R.drawable.home_icon_userlogin, R.drawable.home_icon_userlogin);

		container = imageLoader.get(StringUtil.preUrl(url), listener);

		// 在ImageView上存储当前请求的Container，用于取消请求
		imageView.setTag(container);
	}

	@Override
	public void onResume() {
		if (getActivity() != null) {
			int number_shoucang = PreferenceUtils.getPrefInt(getActivity(),
					Constants.COLLECTIONNUBER, 0);
			int number_published = PreferenceUtils.getPrefInt(getActivity(),
					Constants.PUBLISHNUMBER, 0);
			setImage(
					userimg,
					getString(R.string.hostAddress)
							+ PreferenceUtils.getPrefString(getActivity(),
									Constants.USERIMAGEPATH, ""));
			this.number_publish.setText(String.valueOf(number_published));
			this.tv_number_shoucang.setText(String.valueOf(number_shoucang));
		}
		super.onResume();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.i("test", "MeFragment----onDestroyView");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("test", "MeFragment----onDestroy");
	}
}
