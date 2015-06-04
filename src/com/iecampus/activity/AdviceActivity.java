package com.iecampus.activity;

import java.io.UnsupportedEncodingException;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import com.iecampus.utils.Constants;
import com.iecampus.utils.PreferenceUtils;
import com.iecampus.utils.ToastUtil;
import com.iecampus.utils.UtilTools;
import com.iecampus.view.MyProgressDialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AdviceActivity extends Activity implements OnClickListener {
	private EditText connect, content;
	private Button publish;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.advice_feedback);
		initview();
	}

	private void initview() {
		this.connect = (EditText) findViewById(R.id.advice_connect);
		this.content = (EditText) findViewById(R.id.advice_content_input);
		this.publish = (Button) findViewById(R.id.advice_publish_bt);
		this.publish.setOnClickListener(this);
		progressDialog = new MyProgressDialog(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.advice_publish_bt:
			publishData(this.connect.getText().toString(), this.content
					.getText().toString());
			break;
		default:
			break;
		}
	}

	private void publishData(String connect, String content) {
		if (TextUtils.isEmpty(connect) || TextUtils.isEmpty(content)) {
			ToastUtil.showToast(this, "�㻹û�п��ŵĵط�Ŷ��");
			return;
		} else {
			progressDialog.show();
			String url = getString(R.string.hostAddress) + "advice_insert";
			AjaxParams params = new AjaxParams();
			params.put("advice.username", connect);
			params.put("advice.content", content);
			params.put("advice.date", UtilTools.getDate());
			FinalHttp fh = new FinalHttp();
			fh.post(url, params, new AjaxCallBack<String>() {
				@Override
				public void onSuccess(String state) {
					if (Boolean.parseBoolean(state)) {
						ToastUtil.showToast(AdviceActivity.this, "��л��ı��������");
						AdviceActivity.this.finish();
					} else {
						ToastUtil.showToast(AdviceActivity.this, "�ϴ�ʧ����");
					}
					progressDialog.dismiss();
				}

				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					ToastUtil.showToast(AdviceActivity.this, "�����쳣,����������磡");
					progressDialog.dismiss();
				}
			});
		}
	}
}
