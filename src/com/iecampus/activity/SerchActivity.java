package com.iecampus.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.iecampus.utils.Constants;
import com.iecampus.utils.PreferenceUtils;

public class SerchActivity extends Activity {
	private EditText serch_content;
	private ListView list;
	private Button cancle;
	private ArrayList<String> data = new ArrayList<String>();
	private ArrayList<String> temp = null;
	private String status = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.setContentView(R.layout.scholl_select);
		super.onCreate(savedInstanceState);
		initview();
		initdata();
		this.cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new Thread() {
					@Override
					public void run() {
						Instrumentation inst = new Instrumentation();
						inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
					}
				}.start();

			}
		});
		this.list.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
				R.layout.simplelistitem, data));
		this.list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				TextView tx = (TextView) view.findViewById(R.id.item);
				Log.i("test", tx.getText().toString());
				PreferenceUtils.setPrefString(getApplicationContext(),
						Constants.SCHOOL, tx.getText().toString());
				if ("".equals(status)) {
					Intent intent = new Intent(SerchActivity.this,
							MainActivity.class);
					startActivity(intent);
					finish();
				} else {
					finish();
				}

			}
		});
		this.serch_content.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Log.i("text", "onTextChanged:" + s + "-" + "-" + start + "-"
						+ before + "-" + count);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

				Log.i("text", "beforeTextChanged:" + s + "-" + "-" + start
						+ "-" + after + "-" + count);
			}

			@Override
			public void afterTextChanged(Editable s) {
				String text = s.toString();
				getitem(s.toString());
				if (temp == null) {
					list.setVisibility(View.GONE);
				} else {
					SerchActivity.this.list
							.setAdapter(new ArrayAdapter<String>(
									getApplicationContext(),
									R.layout.simplelistitem, temp));
				}

				Log.i("text", "afterTextChanged:" + s + "-");
			}
		});
	}

	private ArrayList<String> getitem(String key) {
		temp = new ArrayList<String>();
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).contains(key)) {
				temp.add(data.get(i));
			}
		}
		return temp;

	}

	private void initdata() {
		data.add("武汉大学 ");
		data.add("华中科技大学");
		data.add("武汉理工大学");
		data.add("中国地质大学");
		data.add("华中农业大学");
		data.add("华中师范大学");
		data.add("中南财经政法大学");
		data.add("中南民族大学");
		data.add("湖北大学");
		data.add("武汉科技大学");
		data.add("湖北中医学院");
		data.add("湖北工业大学");
		data.add("武汉体育学院");
		data.add("武汉工程大学");
		data.add("武汉科技学院");
		data.add("武汉工业学院");
		data.add("武汉音乐学院");
		data.add("湖北美术学院");
		data.add("武汉生物工程学院");

	}

	private void initview() {
		this.serch_content = (EditText) findViewById(R.id.serch_content);
		this.list = (ListView) findViewById(R.id.scholl_list);
		this.cancle = (Button) findViewById(R.id.cancle);
		try {
			status = getIntent().getExtras().getString("status");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
