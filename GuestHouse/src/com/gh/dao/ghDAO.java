package com.gh.dao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import com.gh.exception.DMLException;
import com.gh.exception.DuplicateIDException;
import com.gh.exception.IDNotFoundException;
import com.gh.vo.Customer;
import com.gh.vo.GuestHouse;
import com.gh.vo.Reservation;
import com.gh.vo.Room;

public interface ghDAO {
	//client
	void insertCustomer(Customer cust) throws  SQLException, DuplicateIDException;
	void updateCustomer(Customer cust) throws  SQLException, IDNotFoundException;
	void deleteCustomer(String uId) throws  SQLException, IDNotFoundException;
	Customer getCustomer(String uId) throws  DMLException, IDNotFoundException;
	ArrayList<GuestHouse> getAllGH() throws DMLException;
	ArrayList<GuestHouse> getAllGH(String region) throws DMLException;
	ArrayList<Room> getAvailableRoom(LocalDate sDate, LocalDate eDate, String gender) throws DMLException;
	void insertReservation(Customer cust, Room room, LocalDate sDate, LocalDate eDate) throws DMLException;
	Reservation getReservation(String uId) throws DMLException;
	void updateReservation(Reservation rv) throws DMLException, IDNotFoundException;
	void deleteReservation(String rvId) throws DMLException, IDNotFoundException;
	void insertWishList(String uId, String ghId) throws DMLException, IDNotFoundException;
	void deleteWishList(String uId, String ghId) throws DMLException, IDNotFoundException;
	
	//manager
	void insertGH(GuestHouse gh) throws SQLException, DuplicateIDException;
	void updateGH(GuestHouse gh) throws SQLException, IDNotFoundException;
	void deleteGH(String ghId) throws SQLException, IDNotFoundException;
	void repairRoom(String rmId, LocalDate sDate, LocalDate eDate);
	void setEventGH(String ghId, LocalDate sDate, LocalDate eDate, double rate);
	ArrayList<Reservation> getAllRV() throws DMLException;
	ArrayList<Reservation> getAllRV(LocalDate date) throws DMLException;
	ArrayList<Reservation> getAllRV(LocalDate date, String ghId) throws DMLException;
	ArrayList<Integer> getQuarterSale(String ghId, int year) throws DMLException;
	int getMonthSale(int year, int month) throws DMLException;
	String getSeasonalCount(int year) throws DMLException;
	ArrayList<GuestHouse> getPopularGH(String region) throws  DMLException;
	String getGenderRatio(String ghId, int year, int month) throws DMLException;
}
