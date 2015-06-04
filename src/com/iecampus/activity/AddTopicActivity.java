package com.iecampus.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import com.iecampus.utils.Constants;
import com.iecampus.utils.PreferenceUtils;
import com.iecampus.utils.ToastUtil;
import com.iecampus.view.MyProgressDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AddTopicActivity extends Activity implements OnClickListener {
	private EditText content;
	private Button publish;
	/** 请求网络的进度条 */
	private MyProgressDialog progressDialog;
	private LinearLayout back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.topic_publish);
		initview();
	}

	private void initview() {
		content = (EditText) this.findViewById(R.id.topic_content_input);
		publish = (Button) this.findViewById(R.id.topic_publish_bt);
		back = (LinearLayout) findViewById(R.id.backlayout);
		progressDialog = new MyProgressDialog(this);
		publish.setOnClickListener(this);
		back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.topic_publish_bt:
			if(!CheckSign()){
				ShowLoginDialog();
			}
			publish.setClickable(false);
			progressDialog.show();
			publishtopic();
			break;
		case R.id.backlayout:
			Intent intent = new Intent("com.ie.update");
			AddTopicActivity.this.sendBroadcast(intent);
			finish();
			break;
		default:
			break;
		}
	}

	private void publishtopic() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = content.getText().toString();
		String date = format.format(new Date());
		String username = PreferenceUtils.getPrefString(this,
				Constants.USERNAME, "");
		int uid = PreferenceUtils.getPrefInt(this, Constants.USERID, 0);
		AjaxParams params = new AjaxParams();
		params.put("topic.username", username);
		params.put("topic.uid", uid + "");
		params.put("topic.title", str);
		params.put("topic.date", date);
		FinalHttp fb = new FinalHttp();
		fb.post(getString(R.string.hostAddress) + "topic_addtopic", params,
				new AjaxCallBack<String>() {
					@Override
					public void onSuccess(String t) {
						if (t.toString().equals("true")) {
							Toast.makeText(AddTopicActivity.this, "发表成功",
									Toast.LENGTH_SHORT).show();
							progressDialog.dismiss();
							Intent intent = new Intent("com.ie.update");
							AddTopicActivity.this.sendBroadcast(intent);
							finish();
						}
					}
				});
	}
	public boolean CheckSign() {
		String username = PreferenceUtils.getPrefString(this,
				Constants.USERNAME, "");
		if (username.equals("") || username == null) {
			return false;
		}
		return true;

	}

	public void ShowLoginDialog() {
		AlertDialog dialog = new AlertDialog.Builder(AddTopicActivity.this)
				.setMessage("登录后才可以评论哦，现在登录？")
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(AddTopicActivity.this,
								LoginActivity.class);
						startActivity(intent);
						finish();
					}
				}).create();
		dialog.show();
	}
}
