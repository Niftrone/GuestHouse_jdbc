package com.gh.vo;

import java.time.LocalDate;

public class Reservation {
	private String rvId; // 컬럼명 rv_id
	private LocalDate sDate; // 컬럼명 rv_sdate
	private LocalDate eDate; // 컬럼명 rv_edate
	private int price; // 컬럼명 rv_price

	public Reservation() {

	}

	public Reservation(String rvId, LocalDate sDate, LocalDate eDate, int price) {
		super();
		this.rvId = rvId;
		this.sDate = sDate;
		this.eDate = eDate;
		this.price = price;
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

	@Override
	public String toString() {
		return "Reservation [rvId=" + rvId + ", sDate=" + sDate + ", eDate=" + eDate + ", price=" + price + "]";
	}

}
