package com.gh.vo;

public class Room {
	private String rmId; // 컬럼명 rm_id
	private String name; // 컬럼명 rm_name
	private String gender; // 컬럼명 rm_gender
	private int price; // 컬럼명 rm_price
	private int capacity;
	
	public Room() {
		
	}
	
	public Room(String rmId, String name, String gender, int price, int capacity) {
		super();
		this.rmId = rmId;
		this.name = name;
		this.gender = gender;
		this.price = price;
		this.capacity = capacity;
	}

	public String getRmId() {
		return rmId;
	}

	public void setRmId(String rmId) {
		this.rmId = rmId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	@Override
	public String toString() {
		return "Room [rmId=" + rmId + ", name=" + name + ", gender=" + gender + ", price=" + price + ", capacity="
				+ capacity + "]";
	}
	
	
}
