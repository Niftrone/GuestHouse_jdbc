package com.gh.dao;

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
	void insertCustomer(Customer cust) throws  DMLException, DuplicateIDException;
	void updateCustomer(Customer cust) throws  DMLException, IDNotFoundException;
	void deleteCustomer(String uId) throws  DMLException, IDNotFoundException;
	Customer getCustomer(String uId) throws  DMLException, IDNotFoundException;
	ArrayList<GuestHouse> getAllGH() throws DMLException;
	ArrayList<GuestHouse> getAllGH(String region) throws DMLException;
	ArrayList<Room> getAvailableRoom(LocalDate date, String gender) throws DMLException;
	void insertReservation(Customer cust, Room room, LocalDate sDate, LocalDate eDate) throws DMLException;
	Reservation getReservation(String uId) throws DMLException;
	void updateReservation(Reservation rv) throws DMLException, IDNotFoundException;
	void deleteReservation(String rvId) throws DMLException, IDNotFoundException;
	void insertWishList(String uId, String ghId) throws DMLException, IDNotFoundException;
	void deleteWishList(String uId, String ghId) throws DMLException, IDNotFoundException;
	
	//manager
	void insertGH(GuestHouse gh) throws DMLException, DuplicateIDException;
	void updateGH(GuestHouse gh) throws DMLException, IDNotFoundException;
	void deleteGH(String ghId) throws DMLException, IDNotFoundException;
	void repairRoom(String rmId, LocalDate sDate, LocalDate eDate) throws DMLException, IDNotFoundException;
	void setEventGH(String ghId, LocalDate sDate, LocalDate eDate, double rate) throws DMLException, IDNotFoundException;
	ArrayList<Reservation> getAllRV() throws DMLException;
	ArrayList<Reservation> getAllRV(LocalDate date) throws DMLException;
	ArrayList<Reservation> getAllRV(LocalDate date, String ghId) throws DMLException;
	ArrayList<Integer> getQuarterSale(String ghId, int year) throws DMLException;
	int getMonthSale(int year, int month) throws DMLException;
	String getSeasonalCount(int year) throws DMLException;
	ArrayList<GuestHouse> getPopularGH(String region) throws  DMLException;
	String getGenderRatio(String ghId, int year, int month) throws DMLException;
}
