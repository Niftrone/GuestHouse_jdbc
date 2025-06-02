package com.gh.vo;

public class GuestHouse {
	private String ghId; // 컬럼명 gh_id
	private String name; // 컬럼명 gh_name
	private String region; // 컬럼명 gh_region
	
	public GuestHouse() {
		
	}
	
	public GuestHouse(String ghId, String name, String region) {
		super();
		this.ghId = ghId;
		this.name = name;
		this.region = region;
	}

	public String getGhId() {
		return ghId;
	}

	public void setGhId(String ghId) {
		this.ghId = ghId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	@Override
	public String toString() {
		return "GuestHouse [ghId=" + ghId + ", name=" + name + ", region=" + region + "]";
	}
	
}
