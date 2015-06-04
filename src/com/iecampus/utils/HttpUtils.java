package com.iecampus.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import android.util.Log;

public class HttpUtils {

	public HttpUtils() {
	}

	public static String getJsonContent(String url_path)
			throws SocketTimeoutException, IOException {
		URL url = new URL(url_path);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setConnectTimeout(3000);
		connection.setRequestMethod("GET");
		connection.setDoInput(true);
		int code = connection.getResponseCode();
		Log.i("test", "code===" + code);
		if (code == 200) {
			return changeInputStream(connection.getInputStream());
		}
		return "{}";
	}

	private static String changeInputStream(InputStream inputStream)
			throws IOException {
		String jsonString = "";
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		int len = 0;
		byte[] data = new byte[1024];
		while ((len = inputStream.read(data)) != -1) {
			outputStream.write(data, 0, len);
		}
		jsonString = new String(outputStream.toByteArray());
		return jsonString;
	}

}
