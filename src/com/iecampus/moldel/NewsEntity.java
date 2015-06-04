package com.iecampus.moldel;

import java.io.Serializable;

/**
 * 新闻信息的实体类
 * 
 * @author lfh
 * 
 */
public class NewsEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 新闻编号 */
	private Integer id;
	/** 标题 */
	private String title;
	/** 内容预览 */
	private String content;
	/** 新闻源地址 URL */
	private String link;
	/** 发布时间 */
	private String publishTime;
	/** 图片 列表 */
	private String picList;
	/** 图片类型是否为大图 */
	private boolean isLarge;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}

	public String getPicList() {
		return picList;
	}

	public void setPicList(String picList) {
		this.picList = picList;
	}

	public boolean isLarge() {
		return isLarge;
	}

	public void setLarge(boolean isLarge) {
		this.isLarge = isLarge;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
