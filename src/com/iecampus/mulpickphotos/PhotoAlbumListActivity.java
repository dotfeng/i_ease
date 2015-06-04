package com.iecampus.mulpickphotos;

import java.io.Serializable;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.iecampus.activity.R;

public class PhotoAlbumListActivity extends Activity {
	// ArrayList<Entity> dataList;//����װ������Դ���б�
	List<PhotoAlbum> dataList;
	GridView gridView;
	PhotoAlbumListAdapter adapter;// �Զ����������
	PhotoAlbumHelper helper;
	public static final String EXTRA_IMAGE_LIST = "imagelist";
	public static Bitmap bimap;
	private TextView tv_cancle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_album_list);

		helper = PhotoAlbumHelper.getHelper();
		helper.init(getApplicationContext());

		initData();
		initView();
	}

	/**
	 * ��ʼ������
	 */
	private void initData() {
		// /**
		// * ������Ǽ����Ѿ���������߱��ؽ����������ݣ�����ֱ��������ģ����10��ʵ���ֱ࣬��װ���б���
		// */
		// dataList = new ArrayList<Entity>();
		// for(int i=-0;i<10;i++){
		// Entity entity = new Entity(R.drawable.picture, false);
		// dataList.add(entity);
		// }
		dataList = helper.getImagesBucketList(false);	
		bimap=BitmapFactory.decodeResource(
				getResources(),
				R.drawable.goods_photo_btn_add_click);
	}

	/**
	 * ��ʼ��view��ͼ
	 */
	private void initView() {
		gridView = (GridView) findViewById(R.id.gridview);
		adapter = new PhotoAlbumListAdapter(PhotoAlbumListActivity.this, dataList);
		gridView.setAdapter(adapter);
		
		tv_cancle = (TextView) findViewById(R.id.tv_cancle);
		tv_cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				/**
				 * ����position���������Ի�ø�GridView����View��󶨵�ʵ���࣬Ȼ���������isSelected״̬��
				 * ���ж��Ƿ���ʾѡ��Ч���� ����ѡ��Ч���Ĺ��������������Ĵ����л���˵��
				 */
				// if(dataList.get(position).isSelected()){
				// dataList.get(position).setSelected(false);
				// }else{
				// dataList.get(position).setSelected(true);
				// }
				/**
				 * ֪ͨ���������󶨵����ݷ����˸ı䣬Ӧ��ˢ����ͼ
				 */
				// adapter.notifyDataSetChanged();
				Intent intent = new Intent(PhotoAlbumListActivity.this,
						PhotoListActivity.class);
				intent.putExtra(PhotoAlbumListActivity.EXTRA_IMAGE_LIST,
						(Serializable) dataList.get(position).imageList);
				startActivity(intent);
				finish();
			}

		});
	}
}
