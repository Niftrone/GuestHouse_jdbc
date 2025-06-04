package com.gh.vo;

import java.time.LocalDate;

public class Reservation {
	private String rvId; // 컬럼명 rv_id
	private LocalDate sDate; // 컬럼명 rv_sdate
	private LocalDate eDate; // 컬럼명 rv_edate
	private int price; // 컬럼명 rv_price
	private int count;
	private Room room;

	public Reservation() {

	}

	public Reservation(String rvId, LocalDate sDate, LocalDate eDate, int price, int count, Room room) {
		super();
		this.rvId = rvId;
		this.sDate = sDate;
		this.eDate = eDate;
		this.price = price;
		this.count = count;
		this.room = room;
	}

	public String getRvId() {
		return rvId;
	}

	public void setRvId(String rvId) {
		this.rvId = rvId;
	}

	public LocalDate getsDate() {
		return sDate;
	}

	public void setsDate(LocalDate sDate) {
		this.sDate = sDate;
	}

	public LocalDate geteDate() {
		return eDate;
	}

	public void seteDate(LocalDate eDate) {
		this.eDate = eDate;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	@Override
	public String toString() {
		return "Reservation [rvId=" + rvId + ", sDate=" + sDate + ", eDate=" + eDate + ", count =" + count + ", price=" + price + ", room=" + room + "]";
	}

}
