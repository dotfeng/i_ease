package com.iecampus.moldel;



import java.util.HashSet;
import java.util.Set;

/**
 * 
 * �û�����
 * 
 */
public class User {

	private int uid;// �û�id
	private String password;// �û�����
	private String username;// �û���¼��
	private String person_name;// �û���ʵ��
	private String sex;// �Ա�
	private String email;// ����
	private String qq;// qq
	private String tel;// �绰
	private String city;// ����
	private String school;// ѧУ
	private String department;// Ժϵ
	private String classname;// �༶
	private String user_imagepath;// �û�ͷ���ַ
	private int collection; // �ղ���
	private int publish; // ������

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public String getUser_imagepath() {
		return user_imagepath;
	}

	public void setUser_imagepath(String user_imagepath) {
		this.user_imagepath = user_imagepath;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getPerson_name() {
		return person_name;
	}

	public void setPerson_name(String person_name) {
		this.person_name = person_name;
	}

	public int getCollection() {
		return collection;
	}

	public void setCollection(int collection) {
		this.collection = collection;
	}

	public int getPublish() {
		return publish;
	}

	public void setPublish(int publish) {
		this.publish = publish;
	}

	@Override
	public String toString() {
		return "User [uid=" + uid + ", password=" + password + ", username="
				+ username + ", person_name=" + person_name + ", sex=" + sex
				+ ", email=" + email + ", qq=" + qq + ", tel=" + tel
				+ ", city=" + city + ", school=" + school + ", department="
				+ department + ", classname=" + classname + ", user_imagepath="
				+ user_imagepath + ", collection=" + collection + ", publish="
				+ publish + "]";
	}

}
