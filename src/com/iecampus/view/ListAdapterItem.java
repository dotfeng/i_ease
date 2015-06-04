package com.iecampus.view;

import android.view.View;
import android.view.ViewGroup;

public interface ListAdapterItem {
	public View getView(int position, View convertView, ViewGroup parent);
}