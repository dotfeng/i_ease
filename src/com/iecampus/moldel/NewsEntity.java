package com.iecampus.moldel;

import java.io.Serializable;

/**
 * ������Ϣ��ʵ����
 * 
 * @author lfh
 * 
 */
public class NewsEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** ���ű�� */
	private Integer id;
	/** ���� */
	private String title;
	/** ����Ԥ�� */
	private String content;
	/** ����Դ��ַ URL */
	private String link;
	/** ����ʱ�� */
	private String publishTime;
	/** ͼƬ �б� */
	private String picList;
	/** ͼƬ�����Ƿ�Ϊ��ͼ */
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
