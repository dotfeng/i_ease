package com.iecampus.moldel;

/**
 * 
 * 二手商品对象
 * 
 */
public class Goods {
	private int gid;// 商品id
	private int uid;// 商品所属的用户id
	private String goods_name;// 商品名字
	private String school;// 发布所在的学校
	private String goods_imagepath;// 图片路径
	private String detail;// 商品详情
	private float price;// 商品价格
	private String category;// 商品分类
	private String requirements;// 商品特殊要求
	private String date;// 商品发布时间
	private boolean state;// 是发布还是求购 true代表是发布 false代表是求购
	private boolean issale; // 标记是否出售
	private int browsenumber; // 浏览量
	private boolean isgoods; // 是商品还是二手服务

	public int getGid() {
		return gid;
	}

	public void setGid(int gid) {
		this.gid = gid;
	}

	public String getGoods_name() {
		return goods_name;
	}

	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getGoods_imagepath() {
		return goods_imagepath;
	}

	public void setGoods_imagepath(String goods_imagepath) {
		this.goods_imagepath = goods_imagepath;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getRequirements() {
		return requirements;
	}

	public void setRequirements(String requirements) {
		this.requirements = requirements;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public boolean getState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public boolean isIssale() {
		return issale;
	}

	public void setIssale(boolean issale) {
		this.issale = issale;
	}

	public int getBrowsenumber() {
		return browsenumber;
	}

	public void setBrowsenumber(int browsenumber) {
		this.browsenumber = browsenumber;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public boolean getIsgoods() {
		return isgoods;
	}

	public void setIsgoods(boolean isgoods) {
		this.isgoods = isgoods;
	}



}
