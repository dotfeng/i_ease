package com.iecampus.utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class JsonUtil {

	public static <T, clazz> List<T> json2List(String json, Type t) {
		if (json.startsWith("<html>"))
			return null;
		List<T> list = new ArrayList<T>();
		Gson gson = new Gson();
		list = gson.fromJson(json, t);
		return list;
	}

	public static <T> T json2Entity(String json, Class c) {
		T t = null;
		Gson gson = new Gson();
		t = (T) gson.fromJson(json, c);
		return t;
	}

}
