package com.iecampus.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	public static void showToast(Context context, CharSequence text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

}
