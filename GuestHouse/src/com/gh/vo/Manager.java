package com.gh.vo;

public class Manager extends User{
	private String region; // 컬럼명 u_region
	
	public Manager() { }
	public Manager(String uId, String name, String phNum, String region) {
		super(uId, name, phNum);
		this.region = region;
	}

	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}

	@Override
	public String toString() {
		return  super.toString() + ", region="  + region + "]";
	}
}