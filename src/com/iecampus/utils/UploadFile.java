package com.iecampus.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.iecampus.activity.R;

/**
 * 
 * �ϴ��ļ� ��������ָ����·����·���Զ��壩
 * 
 * @author yzj
 * 
 *         ���ܣ�����uploadFile(Context context,File file,String savepath)�����ϴ�
 */
public class UploadFile {

	private Context mcontext;// ���ô˷�����activity����

	/**
	 * ���ļ��ϴ����������Զ����Ŀ¼��
	 * 
	 * @param context
	 *            ���ô˷�����activity����
	 * @param file
	 *            Ҫ�ϴ����ļ�
	 * @param savepath
	 *            �������ļ��Ĵ��·�� ���磺
	 *            savepath="/chat/img"); Ŀ¼�밴ʾ����ʽҪ��д
	 * @throws Exception 
	 */
	public UploadFile(Context context, File file, String savepath,String url,int uid) throws Exception {
		this.mcontext = context;
		Log.i(mcontext.toString(), "upload file");
		try {
			String requestUrl = mcontext.getString(R.string.hostAddress)
					+ url;
			// //������ͨ��Ϣ
			Log.i("test", "�ϴ���ַ��"+requestUrl);
			Map<String, String> params = new HashMap<String, String>();
			params.put("savepath", savepath);
			params.put("fileName", file.getName());
            if(uid!=-1){
            	params.put("userid", String.valueOf(uid));
            }
			// System.out.println("�ļ�����"+file.getName());
			// �ϴ��ļ�
			FormFile formfile = new FormFile(file.getName(), file, "image",
					"application/octet-stream");

			SocketHttpRequester.post(requestUrl, params, formfile, "UTF-8");
			Log.i(mcontext.toString(), "upload success");

		} catch (Exception e) {
			//e.printStackTrace();
			Log.i(mcontext.toString(), "upload error");
			throw e;
		}
		Log.i(mcontext.toString(), "upload end");
	}
}