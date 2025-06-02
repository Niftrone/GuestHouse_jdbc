package com.gh.vo;

import java.util.ArrayList;

public class Customer extends User {
	private String birthday;
	private String gender;
	private ArrayList<GuestHouse> wishList;
	private ArrayList<Reservation> rvList;
	
	public Customer() {
		
	}
	
	public Customer(String uId, String name, String phNum , String birthday, String gender) {
		super(uId, name, phNum);
		this.birthday = birthday;
		this.gender = gender;
	}
	
	public Customer(String uId, String name, String phNum, String birthday, String gender, ArrayList<GuestHouse> wishList, ArrayList<Reservation> rvList) {
		this(uId, name, phNum, birthday, gender);
		this.wishList = wishList;
		this.rvList = rvList;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public ArrayList<GuestHouse> getWishList() {
		return wishList;
	}

	public void setWishList(ArrayList<GuestHouse> wishList) {
		this.wishList = wishList;
	}

	public ArrayList<Reservation> getRvList() {
		return rvList;
	}

	public void setRvList(ArrayList<Reservation> rvList) {
		this.rvList = rvList;
	}

	@Override
	public String toString() {
		return super.toString() + ", birthday=" + birthday + ", gender=" + gender + ", wishList=" + wishList + ", rvList=" + rvList
				+ "]";
	}
	
}
