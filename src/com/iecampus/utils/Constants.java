package com.iecampus.utils;

public class Constants {

	public static final String GET_USERINFO_REQUEST_URL = "http://iesunny.gotoip2.com/user_oneinfo?";
	public static final String GET_ISCOLLECT_REQUEST_URL = "http://iesunny.gotoip2.com/collection_isCollected?";
	public static final String SET_COLLECT_REQUEST_URL = "http://iesunny.gotoip2.com/collection_add";
	public static final String CANCLE_COLLECT_REQUEST_URL = "http://iesunny.gotoip2.com/collection_cancle";

	public static final String DEFAULT_STRING_REQUEST_URL = "www.baidu.com";
	public static final String DEFAULT_JSON_REQUEST_URL = "http://app.api.autohome.com.cn/autov4.3/cars/seriesprice-a2-pm2-v4.3.0-b42-t1.html";
	public static final String DEFAULT_XML_REQUEST_URL = "http://flash.weather.com.cn/wmaps/xml/china.xml";
	public static final String DEFAULT_POST_REQUEST_URL = "webservice.webxml.com.cn/WebServices/MobileCodeWS.asmx/getMobileCodeInfo";
	/** 更新请求的URL */
	public static final String CHECK_UPDATE_URL = "http://iesunny.gotoip2.com/version_CheckVersion";
	/** 下载APK的URL */
	public static final String DOWNLOAD_APK_URL = "http://iesunny.gotoip2.com/file/IECampus.apk";
	/** 下载包安装路径 */
	public static final String savePath = "/sdcard/IECampus/";
	public static final String saveFileName = savePath + "IECampus.apk";

	private Constants() {
	}

	public static class Extra {
		public static final String FRAGMENT_INDEX = "com.grumoon.volleydemo.FRAGMENT_INDEX";
	}

	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String USERID = "uid";
	public static final String USERIMAGEPATH = "imagepath";
	public static final String ISAUTOLOGIN = "islogin";
	public static final String TEL = "tel";
	public static final String SCHOOL = "scholl";
	public static final String SEX = "sex";
	public static final String EMAIL = "email";
	public static final String CITY = "city";
	public static final String CLASS = "class";
	public static final String COLLECTIONNUBER = "collection";
	public static final String ISFIRSTlOGIN = "isloginfirst";
	public static final String PUBLISHNUMBER = "publish";
	public static final String QQ  = "qq";
}
