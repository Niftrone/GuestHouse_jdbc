package com.gh.dao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

import com.gh.exception.DMLException;
import com.gh.exception.DuplicateIDException;
import com.gh.exception.IDNotFoundException;
import com.gh.vo.Customer;
import com.gh.vo.GuestHouse;
import com.gh.vo.Reservation;

public interface ghDAO {
	//client
	void insertCustomer(Customer cust) throws  SQLException, DuplicateIDException;
	void updateCustomer(Customer cust) throws  SQLException, IDNotFoundException;
	void deleteCustomer(String uId) throws  SQLException, IDNotFoundException;
	Customer getCustomer(String uId) throws  SQLException, IDNotFoundException;
	Customer getCustomer2(String uId) throws  SQLException, IDNotFoundException;
	ArrayList<Customer> getAllCustomer() throws  SQLException;
	ArrayList<GuestHouse> getAllGH() throws SQLException;
	ArrayList<GuestHouse> getAllGH(String region) throws SQLException;
	ArrayList<String> getAvailableRoom(LocalDate sDate, LocalDate eDate, String gender, int count) throws SQLException;
	void insertReservation(Reservation rs) throws SQLException, DuplicateIDException;
	ArrayList<Reservation> getReservation(String uId) throws SQLException;
	void updateReservation(Reservation rv) throws SQLException, IDNotFoundException;
	void deleteReservation(String rvId) throws SQLException, IDNotFoundException;
	void insertWishList(String uId, String ghId) throws SQLException, IDNotFoundException, DuplicateIDException;
	void deleteWishList(String uId, String ghId) throws SQLException, IDNotFoundException;
	ArrayList<GuestHouse> getWishList(String uId) throws SQLException;
	
	//manager
	void insertGH(GuestHouse gh) throws SQLException, DuplicateIDException;
	void updateGH(GuestHouse gh) throws SQLException, IDNotFoundException;
	void deleteGH(String ghId) throws SQLException, IDNotFoundException;
	void repairRoom(String rmId, LocalDate sDate, LocalDate eDate);
	void setEventGH(String ghId, LocalDate sDate, LocalDate eDate, double rate);
	ArrayList<Reservation> getAllRV() throws SQLException;
	ArrayList<Reservation>  getAllRV(LocalDate sDate, LocalDate eDate) throws SQLException;
	ArrayList<Reservation>  getAllRV(LocalDate sDate, LocalDate eDate, String ghId) throws SQLException;
	Map<String, Integer> getQuarterSale(String ghId, int year) throws SQLException;
	int getMonthSale(int year, int month) throws SQLException;
	String getSeasonalCount(int year) throws SQLException;
	Map<Integer, GuestHouse> getPopularGH(String region) throws  SQLException;
	String getGenderRatio(String ghId, int year, int month) throws SQLException;
	String getGenderRatio(String ghId, int year) throws SQLException;
}