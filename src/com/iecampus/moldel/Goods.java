package com.iecampus.moldel;

/**
 * 
 * ������Ʒ����
 * 
 */
public class Goods {
	private int gid;// ��Ʒid
	private int uid;// ��Ʒ�������û�id
	private String goods_name;// ��Ʒ����
	private String school;// �������ڵ�ѧУ
	private String goods_imagepath;// ͼƬ·��
	private String detail;// ��Ʒ����
	private float price;// ��Ʒ�۸�
	private String category;// ��Ʒ����
	private String requirements;// ��Ʒ����Ҫ��
	private String date;// ��Ʒ����ʱ��
	private boolean state;// �Ƿ��������� true�����Ƿ��� false��������
	private boolean issale; // ����Ƿ����
	private int browsenumber; // �����
	private boolean isgoods; // ����Ʒ���Ƕ��ַ���

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
