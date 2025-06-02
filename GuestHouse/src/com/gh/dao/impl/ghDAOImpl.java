package com.gh.dao.impl;

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

public class ghDAOImpl implements ghDAO{

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
