package com.gh.dao.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.gh.dao.ghDAO;
import com.gh.exception.DMLException;
import com.gh.exception.DuplicateIDException;
import com.gh.exception.IDNotFoundException;
import com.gh.vo.Customer;
import com.gh.vo.GuestHouse;
import com.gh.vo.Reservation;
import com.gh.vo.Room;

import config.ServerInfo;

public class ghDAOImpl implements ghDAO {

	// 싱글톤
	// Test 클래스의 static 안이 아니라 생성자 안에서 드라이버 로딩 시키기
	private static ghDAOImpl dao = new ghDAOImpl("127.0.0.1");

	private ghDAOImpl(String serverIp) {
		try {
			Class.forName(ServerInfo.DRIVER_NAME);
			System.out.println("드라이버 로딩 성공...");
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public static ghDAOImpl getInstance() {
		return dao;
	}

	/// 공통 로직 (외부, 다른 클래스에서 호출할 일이 없으므로 private) ///

	private static Map<String, DiscountInfo> discountInfo = new HashMap<>();
	private static Map<String, RepairInfo> repairInfo = new HashMap<>();

	private static class DiscountInfo {
		LocalDate sDate;
		LocalDate eDate;
		Double rate;

		DiscountInfo(LocalDate sDate, LocalDate eDate, Double rate) {
			this.sDate = sDate;
			this.eDate = eDate;
			this.rate = rate;
		}
	}
	
	private static class RepairInfo {
		LocalDate sDate;
		LocalDate eDate;
		
		RepairInfo(LocalDate sDate, LocalDate eDate) {
			this.sDate = sDate;
			this.eDate = eDate;
		}
	}

	private int discountedPrice(Reservation rv) {
		int days = rv.geteDate().getDayOfMonth() - rv.getsDate().getDayOfMonth();
		double discount = 0.0;

		DiscountInfo info = discountInfo.get(rv.getRvId());

		if (info != null) {
			boolean over = !(rv.geteDate().isBefore(info.sDate) || rv.getsDate().isAfter(info.eDate));
			if (over) {
				discount = info.rate;
			}
		}

		return (int) (rv.getRoom().getPrice() * days * (1 - discount) * rv.getCount());

	}
	
	private boolean checkRepair(Reservation rv) {
		RepairInfo info = repairInfo.get(rv.getRoom().getRmId());
		if(rv.getsDate() == info.sDate || rv.geteDate() == info.eDate)
			return true;

		return !rv.getsDate().isBefore(info.sDate) && !rv.geteDate().isAfter(info.eDate);
	}

	private Connection getConnect() throws SQLException {
		Connection conn = DriverManager.getConnection(ServerInfo.URL, ServerInfo.USER, ServerInfo.PASS);
		return conn;
	}

	private void closeAll(PreparedStatement ps, Connection conn) throws SQLException {
		if (ps != null)
			ps.close();
		if (conn != null)
			conn.close();
	}

	private void closeAll(ResultSet rs, PreparedStatement ps, Connection conn) throws SQLException {
		if (rs != null)
			rs.close();
		closeAll(ps, conn);
	}

	/// 비즈니스 로직 ///
	@Override
	public void insertCustomer(Customer cust) throws SQLException, DuplicateIDException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateCustomer(Customer cust) throws SQLException, IDNotFoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteCustomer(String uId) throws SQLException, IDNotFoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public Customer getCustomer(String uId) throws SQLException, IDNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<GuestHouse> getAllGH() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<GuestHouse> getAllGH(String region) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Room> getAvailableRoom(LocalDate date, String gender) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insertReservation(Reservation rv) throws SQLException, DuplicateIDException  {
		Connection conn = null;
		PreparedStatement ps = null;
		Room room = rv.getRoom();
		
		if(rv.getCount() > room.getCapacity()) {
			System.out.println("방의 수용인원보다 많은 인원입니다.");
			return;
		}
			
		try {
			conn = getConnect();
			String query = "INSERT INTO reservation(rv_id, u_id, rm_id, rv_sdate, rv_edate, rv_price, count) VALUES (?,?,?,?,?,?,?) ";
			ps = conn.prepareStatement(query);
			ps.setString(1, rv.getRvId());
			ps.setString(2, rv.getCust().getuId());
			ps.setString(3, room.getRmId());
			ps.setDate(4, java.sql.Date.valueOf(rv.getsDate()));
			ps.setDate(5, java.sql.Date.valueOf(rv.geteDate()));
			ps.setInt(6, discountedPrice(rv));
			ps.setInt(7, rv.getCount());
			
			System.out.println(ps.executeUpdate() == 1 ? "예약 성공" : "예약 실패");
			
		} catch(SQLIntegrityConstraintViolationException e) {
			throw new DuplicateIDException(e.getMessage());
		} catch(SQLException e) {
			throw new DMLException(e.getMessage());
		} finally {
			closeAll(ps, conn);
		}

	}

	@Override
	public Reservation getReservation(String uId) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = getConnect();
			String query = "SELECT * FROM reservation ";
			
		}catch(SQLException e) {
			throw new DMLException(e.getMessage()); 
		}
		
		return null;
	}

	@Override
	public void updateReservation(Reservation rv) throws SQLException, IDNotFoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteReservation(String rvId) throws SQLException, IDNotFoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public void insertWishList(String uId, String ghId) throws SQLException, IDNotFoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteWishList(String uId, String ghId) throws SQLException, IDNotFoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public void insertGH(GuestHouse gh) throws SQLException, DuplicateIDException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateGH(GuestHouse gh) throws SQLException, IDNotFoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteGH(String ghId) throws SQLException, IDNotFoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public void repairRoom(String rmId, LocalDate sDate, LocalDate eDate){
		repairInfo.put(rmId,new RepairInfo(sDate, eDate));

	}

	@Override
	public void setEventGH(String ghId, LocalDate sDate, LocalDate eDate, double rate){
		discountInfo.put(ghId, new DiscountInfo(sDate, eDate, rate));

	}

	@Override
	public ArrayList<Reservation> getAllRV() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Reservation> getAllRV(LocalDate date) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Reservation> getAllRV(LocalDate date, String ghId) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Integer> getQuarterSale(String ghId, int year) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMonthSale(int year, int month) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getSeasonalCount(int year) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<GuestHouse> getPopularGH(String region) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getGenderRatio(String ghId, int year, int month) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}
