package com.gh.vo;

public class User {
	private String uId; // 컬럼명 u_id
	private String name; // 컬럼명 u_name
	private String phNum;
	
	public User() {}
	public User(String uId, String name, String phNum) {
		this.uId = uId;
		this.name = name;
		this.phNum = phNum;
	}
	
	public String getuId() {
		return uId;
	}
	public void setuId(String uId) {
		this.uId = uId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhNum() {
		return phNum;
	}
	public void setPhNum(String phNum) {
		this.phNum = phNum;
	}
	
	@Override
	public String toString() {
		return "User [uId=" + uId + ", name=" + name + ", phNum=" + phNum;
	}
}