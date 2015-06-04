package com.iecampus.moldel;

public class TopicReply {
	private int rid;
	private int tid;
	private int uid;
	private String username;
	private String content;
	private String date;

	public int getRid() {
		return rid;
	}

	public void setRid(int rid) {
		this.rid = rid;
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "TopicReply [rid=" + rid + ", tid=" + tid + ", uid=" + uid
				+ ", username=" + username + ", content=" + content + ", date="
				+ date + "]";
	}

}
