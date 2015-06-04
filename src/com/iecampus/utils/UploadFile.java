package com.iecampus.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.iecampus.activity.R;

/**
 * 
 * 上传文件 到服务器指定的路径（路径自定义）
 * 
 * @author yzj
 * 
 *         介绍：调用uploadFile(Context context,File file,String savepath)进行上传
 */
public class UploadFile {

	private Context mcontext;// 调用此方法的activity名称

	/**
	 * 将文件上传到服务器自定义的目录下
	 * 
	 * @param context
	 *            调用此方法的activity名称
	 * @param file
	 *            要上传的文件
	 * @param savepath
	 *            服务器文件的存放路径 例如：
	 *            savepath="/chat/img"); 目录请按示例格式要求写
	 * @throws Exception 
	 */
	public UploadFile(Context context, File file, String savepath,String url,int uid) throws Exception {
		this.mcontext = context;
		Log.i(mcontext.toString(), "upload file");
		try {
			String requestUrl = mcontext.getString(R.string.hostAddress)
					+ url;
			// //请求普通信息
			Log.i("test", "上传地址："+requestUrl);
			Map<String, String> params = new HashMap<String, String>();
			params.put("savepath", savepath);
			params.put("fileName", file.getName());
            if(uid!=-1){
            	params.put("userid", String.valueOf(uid));
            }
			// System.out.println("文件名："+file.getName());
			// 上传文件
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