package com.iecampus.utils;



public class StringUtil {
	/**
	 * 判断字符串是否为�?
	 * @param str 要判断的字符�?
	 * @return
	 */
	public static boolean isEmpty(String str){
		if(str==null||str.trim().length()==0){
			return true;
		}
		else {
			return false;
		}
		
	}
	
	
	/**
	 * 处理url
	 * 如果不是以http://或�?�https://�?头，就添加http://
	 * @param url 被处理的url
	 * @return
	 */
	public static String preUrl(String url){
		if(url==null){
			return null;
		}
		if(url.startsWith("http://")||url.startsWith("https://")){
			return url;
		}
		else{
			return "http://"+url;
		}
	}

}
