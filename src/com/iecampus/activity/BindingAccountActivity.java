package com.iecampus.activity;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import com.iecampus.utils.ToastUtil;
import com.iecampus.view.MyProgressDialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class BindingAccountActivity extends Activity implements OnClickListener {

	private EditText accout, password;
	private Button binding;
	private MyProgressDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_binding);
		initview();
	}

	private void initview() {
		this.accout = (EditText) findViewById(R.id.binding_accout);
		this.password = (EditText) findViewById(R.id.binding_password);
		this.binding = (Button) findViewById(R.id.binding);
		this.dialog = new MyProgressDialog(this);
		this.binding.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.binding:
			String accont_text = BindingAccountActivity.this.accout.getText().toString();
			String password_text = BindingAccountActivity.this.password.getText().toString();
             if(TextUtils.isEmpty(accont_text)||TextUtils.isEmpty(password_text)){
            	 ToastUtil.showToast(BindingAccountActivity.this, "你还有地方空着呢");
            	 return;
             }
             bindingNetwork(accont_text,password_text);
			break;

		default:
			break;
		}
	}

	private void bindingNetwork(final String accout,final String password){
		String openid=null;
		String url = this.getString(R.string.hostAddress)+"user_bindingAccount";
		try {
			openid = this.getIntent().getStringExtra("openid");
		} catch (Exception e) {
			e.printStackTrace();
		}
		AjaxParams ajax = new AjaxParams();
		ajax.put("user.user_username", accout);
		ajax.put("user.user_password", password);
		ajax.put("user.user_openid", openid);
		FinalHttp fb = new FinalHttp();
		fb.post(url, ajax, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String t) {

				if (Boolean.parseBoolean(t.toString())) {
					dialog.dismiss();
					Toast.makeText(BindingAccountActivity.this, "绑定成功,自动登录中！",
							Toast.LENGTH_LONG).show();
					Intent intent = new Intent();
					intent.putExtra("username", accout);
					intent.putExtra("password", password);
					setResult(1002, intent);
					finish();
				} else {
					Toast.makeText(BindingAccountActivity.this, "绑定失败,账号或者是密码错误！",
							Toast.LENGTH_LONG).show();
				}
				super.onSuccess(t);
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				Toast.makeText(BindingAccountActivity.this, "网络真不给力，让我们再试试！",
						Toast.LENGTH_LONG).show();
				super.onFailure(t, errorNo, strMsg);
			}
		});
	}
}
