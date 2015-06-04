package com.iecampus.activity;

import com.iecampus.utils.Constants;
import com.iecampus.utils.PreferenceUtils;
import com.iecampus.utils.ToastUtil;
import com.iecampus.utils.UtilTools;
import com.iecampus.view.MyProgressDialog;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity implements OnClickListener,
		OnCheckedChangeListener{
	private EditText username, password, email, confirm_pass, tel, qq;
	private RadioGroup sex;
	private MyProgressDialog dialog;
	private Button registers;
	private String str = "男";
	private String school = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.setContentView(R.layout.register);
		super.onCreate(savedInstanceState);
		initview();
	}

	private void initview() {
		this.username = (EditText) findViewById(R.id.username_regesiter);
		this.password = (EditText) findViewById(R.id.passwd_regesiter);
		this.confirm_pass = (EditText) findViewById(R.id.confirm_pass);
		// this.city = (Spinner) findViewById(R.id.city);
		this.dialog = new MyProgressDialog(this);
		this.email = (EditText) findViewById(R.id.email);
		this.sex = (RadioGroup) findViewById(R.id.sex);
		this.tel = (EditText) findViewById(R.id.tel);
		this.tel.setText(PreferenceUtils.getPrefString(this, Constants.TEL, ""));
		this.qq = (EditText) this.findViewById(R.id.QQ);
		this.registers = (Button) findViewById(R.id.regesiters);
		this.registers.setOnClickListener(this);
		this.sex.setOnCheckedChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.regesiters:
			dialog.show();
			String uri = getString(R.string.hostAddress) + "/user_register";
			AjaxParams ajax = new AjaxParams();
			final String username = this.username.getText().toString().trim();
			final String password = this.password.getText().toString().trim();
			String confirmpass = this.confirm_pass.getText().toString().trim();
			String email = this.email.getText().toString().trim();
			String telephone = PreferenceUtils.getPrefString(this,
					Constants.TEL, "");
			String openid = getIntent().getStringExtra("openid");
			this.tel.setText(telephone);
			String sex = this.str;
			if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)
					|| TextUtils.isEmpty(confirmpass)|| TextUtils.isEmpty(sex)
					|| TextUtils.isEmpty(email)) {
				ToastUtil.showToast(RegisterActivity.this, "你还有地方空着哦！");
				dialog.dismiss();
				return;
			} else if (!password.equals(confirmpass)) {
				ToastUtil.showToast(RegisterActivity.this, "两次密码输入不一致!");
				dialog.dismiss();
				return;
			}else if(password.length()<6){
				ToastUtil.showToast(RegisterActivity.this, "密码长度小于6!");
				dialog.dismiss();
				return;
			}else if(!UtilTools.isEmail(email)){
				ToastUtil.showToast(RegisterActivity.this, "邮箱格式错误！");
				dialog.dismiss();
				return;
			}else if(TextUtils.isEmpty(telephone)&&TextUtils.isEmpty(this.qq.getText())){
				ToastUtil.showToast(RegisterActivity.this, "手机号和QQ必需填写一个！");
				dialog.dismiss();
				return;
			}
			try{
			ajax.put("user.username", username);
			ajax.put("user.password", password);
			ajax.put("user.tel", this.tel.getText().toString());
			ajax.put("user.email", this.email.getText().toString());
			ajax.put("user.sex", this.str);
			ajax.put("user.tel", telephone);
			ajax.put("user.openid", openid);
			ajax.put("user.qq", this.qq.getText().toString());
			FinalHttp fb = new FinalHttp();
			fb.post(uri, ajax, new AjaxCallBack<String>() {
				@Override
				public void onSuccess(String t) {

					if (Boolean.parseBoolean(t.toString())) {
						dialog.dismiss();
						Toast.makeText(RegisterActivity.this, "你已注册成功,自动登录中！",
								Toast.LENGTH_LONG).show();
						Intent intent = new Intent();
						intent.putExtra("username", username);
						intent.putExtra("password", password);
						setResult(1002, intent);
						finish();

					} else {
						Toast.makeText(RegisterActivity.this, "注册失败,请重试~！",
								Toast.LENGTH_LONG).show();
					}
					super.onSuccess(t);
				}

				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					Toast.makeText(RegisterActivity.this, "网络真不给力，让我们再试试！",
							Toast.LENGTH_LONG).show();
					super.onFailure(t, errorNo, strMsg);
				}
			});}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				Toast.makeText(this, "用户名已被使用", Toast.LENGTH_LONG).show();
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkid) {
		switch (checkid) {
		case R.id.male:
			RegisterActivity.this.str = "男";
			break;
		case R.id.female:
			RegisterActivity.this.str = "女";
			break;
		default:
			break;
		}

	}
}
