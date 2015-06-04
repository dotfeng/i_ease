package com.iecampus.activity;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

import com.google.gson.reflect.TypeToken;
import com.iecampus.dialog.SweetAlertDialog;
import com.iecampus.dialog.SweetAlertDialog.OnSweetClickListener;
import com.iecampus.moldel.User;
import com.iecampus.utils.Constants;
import com.iecampus.utils.PreferenceUtils;
import com.iecampus.utils.ToastUtil;
import com.iecampus.view.MyProgressDialog;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class LoginActivity extends Activity implements OnClickListener {
	private Button login, register;
	private static MyProgressDialog dialog;
	private EditText input_username, input_password;
	private Type UserType = new TypeToken<List<User>>() {
	}.getType();
	// 填写从短信SDK应用后台注册得到的APPKEY
	private static String APPKEY = "7d174ce26d68";
	// 填写从短信SDK应用后台注册得到的APPSECRET
	private static String APPSECRET = "f18267f9bbea94b9e8d70e82f663c8e4";
	private static int REGISTER = 1001;
	private static int REGISTERBACK = 1002;
	private ImageView QQ;
	private String Appid = "1031724787";
	public static Tencent mTencent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.login);
		super.onCreate(savedInstanceState);
		this.login = (Button) findViewById(R.id.login);
		this.register = (Button) findViewById(R.id.register);
		this.input_username = (EditText) findViewById(R.id.input_username);
		this.input_password = (EditText) findViewById(R.id.input_password);
		this.QQ = (ImageView) findViewById(R.id.QQ);
		this.dialog = new MyProgressDialog(LoginActivity.this);
		this.login.setOnClickListener(this);
		this.register.setOnClickListener(this);
		this.QQ.setOnClickListener(this);
		initSDK();
		initQQ();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login:

			String username = LoginActivity.this.input_username.getText()
					.toString().trim();
			String password = LoginActivity.this.input_password.getText()
					.toString().trim();
			Log.i("test", "pass  " + password + "  username  " + username);
			if (username.equals("") || password.equals("")) {
				ToastUtil.showToast(this, "用户名或者密码不能为空！");
				return;
			}
			loginNetwork(username, password);
			break;
		case R.id.register:

			// 打开注册页面
			RegisterPage registerPage = new RegisterPage();
			registerPage.setRegisterCallback(new EventHandler() {
				public void afterEvent(int event, int result, Object data) {
					// 解析注册结果
					if (result == SMSSDK.RESULT_COMPLETE) {
						@SuppressWarnings("unchecked")
						HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
						String country = (String) phoneMap.get("country");
						String phone = (String) phoneMap.get("phone");
						// 提交用户信息
						registerUser(country, phone);
					}
				}
			});
			registerPage.show(LoginActivity.this);
			break;
		case R.id.QQ:
			if (!mTencent.isSessionValid()) {
				mTencent.login(this, "all", loginListener);
			} else {
				mTencent.logout(this);
			}
			break;
		default:
			break;
		}

	}

	private void initSDK() {
		// 初始化短信SDK
		SMSSDK.initSDK(this, APPKEY, APPSECRET);
		EventHandler eventHandler = new EventHandler() {
			public void afterEvent(int event, int result, Object data) {
				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler.sendMessage(msg);
				// this.h
			}
		};
		// 注册回调监听接口
		SMSSDK.registerEventHandler(eventHandler);

	}

	private void initQQ() {
		if (mTencent == null) {
			mTencent = Tencent.createInstance(Appid, this);
		}

	}

	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int event = msg.arg1;
			int result = msg.arg2;
			Object data = msg.obj;
			Log.i("Android", "succeess  " + event + "  " + result + "   "
					+ data);
			if (event == SMSSDK.EVENT_SUBMIT_USER_INFO) {
				// 短信注册成功后，返回MainActivity,然后提示新好友
				// if (result == SMSSDK.RESULT_COMPLETE) {
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivityForResult(intent, REGISTER);
			}
		}
	};

	// 提交用户信息
	private void registerUser(String country, String phone) {
		Random rnd = new Random();
		int id = Math.abs(rnd.nextInt());
		String uid = String.valueOf(id);
		String nickName = "SmsSDK_User_" + uid;
		Log.i("test", "login:" + phone);
		PreferenceUtils.setPrefString(this, Constants.TEL, phone);
		SMSSDK.submitUserInfo(uid, nickName, null, country, phone);

	}

	private void loginNetwork(String username, String password) {
		dialog.show();
		String url = getString(R.string.hostAddress) + "/user_login?username="
				+ username + "&password=" + password;
		FinalHttp fh = new FinalHttp();
		fh.get(url, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String json) {
				Log.i("test", json);
				if (!json.equals("false")) {
					try {
						setPrefrence(json);
					} catch (JSONException e) {
						e.printStackTrace();
					}

					Intent intent = new Intent(LoginActivity.this,
							SerchActivity.class);
					startActivity(intent);
					Toast.makeText(LoginActivity.this, "登录成功", 1000).show();
					finish();

				} else {
					Toast.makeText(LoginActivity.this, "登录失败，用户名或者是密码错误！！",
							1000).show();
				}
				dialog.dismiss();
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				Toast.makeText(getApplicationContext(),
						"网络连接失败，请检查网络设置~！" + strMsg, 1000).show();
				dialog.dismiss();
			}
		});
	}

	private void setPrefrence(String json) throws JSONException {
		JSONObject data;
		data = new JSONObject(json);
		PreferenceUtils.setPrefString(LoginActivity.this, Constants.USERNAME,
				data.getString("username"));
		PreferenceUtils.setPrefString(LoginActivity.this, Constants.PASSWORD,
				data.getString("password"));
		PreferenceUtils.setPrefInt(LoginActivity.this, Constants.USERID,
				Integer.parseInt(data.getString("uid")));
		PreferenceUtils.setPrefString(LoginActivity.this,
				Constants.USERIMAGEPATH, data.getString("user_imagepath"));
		PreferenceUtils.setPrefString(LoginActivity.this, Constants.TEL,
				data.getString("tel"));
		PreferenceUtils.setPrefString(LoginActivity.this, Constants.QQ,
				data.getString("qq"));
		PreferenceUtils.setPrefString(LoginActivity.this, Constants.SEX,
				data.getString("sex"));
		PreferenceUtils.setPrefString(LoginActivity.this, Constants.EMAIL,
				data.getString("email"));
		PreferenceUtils.setPrefInt(LoginActivity.this,
				Constants.COLLECTIONNUBER, data.getInt("collection"));
		PreferenceUtils.setPrefInt(LoginActivity.this, Constants.PUBLISHNUMBER,
				data.getInt("publish"));
	}

	IUiListener loginListener = new BaseUiListener() {
		@Override
		protected void doComplete(JSONObject values) {
			Log.d("SDKQQAgentPref",
					"AuthorSwitch_SDK:" + SystemClock.elapsedRealtime());
			initOpenidAndToken(values);
			ToastUtil.showToast(LoginActivity.this, "授权成功！");
		}
	};

	private class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
			if (null == response) {
				ToastUtil.showToast(LoginActivity.this, "登录失败");
				return;
			}
			JSONObject jsonResponse = (JSONObject) response;
			if (null != jsonResponse && jsonResponse.length() == 0) {
				ToastUtil.showToast(LoginActivity.this, "登录失败");
				return;
			}
			Log.i("Android", response.toString());
			doComplete((JSONObject) response);
		}

		protected void doComplete(JSONObject values) {

		}

		@Override
		public void onError(UiError e) {
			ToastUtil.showToast(LoginActivity.this, e.toString());
		}

		@Override
		public void onCancel() {
		}
	}

	public void initOpenidAndToken(JSONObject jsonObject) {
		try {
			String token = jsonObject
					.getString(com.tencent.connect.common.Constants.PARAM_ACCESS_TOKEN);
			String expires = jsonObject
					.getString(com.tencent.connect.common.Constants.PARAM_EXPIRES_IN);
			String openId = jsonObject
					.getString(com.tencent.connect.common.Constants.PARAM_OPEN_ID);
			if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
					&& !TextUtils.isEmpty(openId)) {
				mTencent.setAccessToken(token, expires);
				mTencent.setOpenId(openId);
				isRegister(openId);
			}
		} catch (Exception e) {
		}
	}

	private void isRegister(String openid) {

		dialog.show();
		final String temp = openid;
		String url = "http://iesunny.gotoip2.com/user_loginByQQ";
		AjaxParams params = new AjaxParams();
		params.put("user_openid", openid);
		FinalHttp fh = new FinalHttp();
		fh.get(url, params, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String state) {
				Log.i("Android", state);
				if ("false".equals(state)) {
					showDialogs(temp);
				} else {
					ToastUtil.showToast(LoginActivity.this, "即将登陆！");
					try {
						setPrefrence(state);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					Intent intent = new Intent(LoginActivity.this,
							SerchActivity.class);
					startActivity(intent);
					finish();
				}
				dialog.dismiss();
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				ToastUtil.showToast(LoginActivity.this, "网络异常！");
			}
		});

	}

	private void showDialogs(String openid) {
		final String temp = openid;
		new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
				.setTitleText("警告")
				.setContentText("该QQ号未注册")
				.setConfirmText("绑定账号")
				.setConfirmClickListener(
						new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {
								Intent intent = new Intent(LoginActivity.this,
										BindingAccountActivity.class);
								intent.putExtra("openid", temp);
								startActivityForResult(intent, REGISTER);
								sDialog.dismiss();
							}
						}).showCancelButton(true).setCancelText("注册新用户")
				.setCancelClickListener(new OnSweetClickListener() {

					@Override
					public void onClick(SweetAlertDialog sweetAlertDialog) {
						Intent intent = new Intent(LoginActivity.this,
								RegisterActivity.class);
						intent.putExtra("openid", temp);
						startActivityForResult(intent, REGISTER);
						sweetAlertDialog.dismiss();
					}
				}).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1001:
			if (resultCode == 1002) {
				String username = data.getExtras().getString("username");
				String password = data.getExtras().getString("password");
				if (username != null & password != null) {
					loginNetwork(username, password);
				}
			}
			break;

		default:
			break;
		}
	}
}
