package com.iecampus.mulpickphotos;

import java.io.Serializable;

/**
 * һ��ͼƬ����
 * 
 * @author Administrator
 * 
 */
public class Photo implements Serializable {
	public String imageId;
	public String thumbnailPath;
	public String imagePath;
	public boolean isSelected = false;
}
