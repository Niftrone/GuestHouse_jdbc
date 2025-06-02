package com.gh.vo;

public class Reservation {
	private String rvId; // 컬럼명 rv_id
	private String sDate; // 컬럼명 rv_sdate
	private String eDate; // 컬럼명 rv_edate
	private int price; // 컬럼명 rv_price
	
	public Reservation() {
		
	}
	
	public Reservation(String rvId, String sDate, String eDate, int price) {
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

	public String getsDate() {
		return sDate;
	}

	public void setsDate(String sDate) {
		this.sDate = sDate;
	}

	public String geteDate() {
		return eDate;
	}

	public void seteDate(String eDate) {
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
