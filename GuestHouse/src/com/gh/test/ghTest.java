package com.gh.test;

import java.sql.SQLException;
import java.time.LocalDate;

import com.gh.dao.impl.ghDAOImpl;
import com.gh.exception.DMLException;
import com.gh.exception.DuplicateIDException;
import com.gh.vo.Customer;
import com.gh.vo.GuestHouse;
import com.gh.vo.Reservation;
import com.gh.vo.Room;

public class ghTest {

	public static void main(String[] args) throws DMLException,DuplicateIDException {
		
		ghDAOImpl gh = ghDAOImpl.getInstance();
		
		try {
			gh.insertReservation(new Reservation(
					"RV081", LocalDate.of(2025, 06, 12), LocalDate.of(2025, 06, 15), 2, 
					new Room("RM001",
							new GuestHouse("GH001", "소담하우스", "Seoul")
							,"햇살방", "F", 55000, 2),
					new Customer("C035", "임재현","01011110035",  LocalDate.of(1988,10,15), "M")
					));
		} catch (SQLException e) {
			throw new DMLException(e.getMessage());
		} catch (DuplicateIDException e) {
			throw new DuplicateIDException(e.getMessage());
		}
	}

}
