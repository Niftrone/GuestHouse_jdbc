package com.gh.dao.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import com.gh.dao.ghDAO;
import com.gh.exception.DMLException;
import com.gh.exception.DuplicateIDException;
import com.gh.exception.IDNotFoundException;
import com.gh.vo.Customer;
import com.gh.vo.GuestHouse;
import com.gh.vo.Reservation;
import com.gh.vo.Room;

import config.ServerInfo;

public class ghDAOImpl implements ghDAO{
	
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
	private Connection getConnect() throws SQLException { 
		Connection conn = DriverManager.getConnection(ServerInfo.URL, ServerInfo.USER, ServerInfo.PASS);
		return conn;
	}
	private void closeAll(PreparedStatement ps, Connection conn) throws SQLException { 
		if(ps != null) ps.close();
		if(conn != null) conn.close();
	}
	private void closeAll(ResultSet rs, PreparedStatement ps, Connection conn) throws SQLException {
		if(rs != null) rs.close();
		closeAll(ps, conn);
	}
	
	/// 비즈니스 로직 ///
	@Override
	public void insertCustomer(Customer cust) throws DMLException, DuplicateIDException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateCustomer(Customer cust) throws DMLException, IDNotFoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteCustomer(String uId) throws DMLException, IDNotFoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Customer getCustomer(String uId) throws DMLException, IDNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<GuestHouse> getAllGH() throws DMLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<GuestHouse> getAllGH(String region) throws DMLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Room> getAvailableRoom(LocalDate date, String gender) throws DMLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insertReservation(Customer cust, Room room, LocalDate sDate, LocalDate eDate) throws DMLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Reservation getReservation(String uId) throws DMLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateReservation(Reservation rv) throws DMLException, IDNotFoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteReservation(String rvId) throws DMLException, IDNotFoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertWishList(String uId, String ghId) throws DMLException, IDNotFoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteWishList(String uId, String ghId) throws DMLException, IDNotFoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertGH(GuestHouse gh) throws DMLException, DuplicateIDException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateGH(GuestHouse gh) throws DMLException, IDNotFoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteGH(String ghId) throws DMLException, IDNotFoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void repairRoom(String rmId, LocalDate sDate, LocalDate eDate) throws DMLException, IDNotFoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setEventGH(String ghId, LocalDate sDate, LocalDate eDate, double rate)
			throws DMLException, IDNotFoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<Reservation> getAllRV() throws DMLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Reservation> getAllRV(LocalDate date) throws DMLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Reservation> getAllRV(LocalDate date, String ghId) throws DMLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Integer> getQuarterSale(String ghId, int year) throws DMLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMonthSale(int year, int month) throws DMLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getSeasonalCount(int year) throws DMLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<GuestHouse> getPopularGH(String region) throws DMLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getGenderRatio(String ghId, int year, int month) throws DMLException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
