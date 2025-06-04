package com.gh.test;

import java.time.LocalDate;

import com.gh.dao.impl.ghDAOImpl;
import com.gh.vo.Customer;
import com.gh.vo.GuestHouse;
import com.gh.vo.Reservation;
import com.gh.vo.Room;

public class ghTest {

	public static void main(String[] args) {
		
		ghDAOImpl gh = ghDAOImpl.getInstance();
		//String uId, String name, String phNum, LocalDate birthday, String gender
		
		// INSERT CHECK
//		try {
//			gh.insertCustomer(new Customer(
//					"c777","Kingstone","010-1111-2222",LocalDate.of(1996, 01, 13),"M"));
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//			
//		}
		
		// Update CHECK
//		try {
//			gh.updateCustomer(new Customer(
//					"c777","Kingstone2","010-1111-3333",LocalDate.of(1996, 01, 13),"M"));
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//			
//		}
//		
//		try {
//			gh.insertGH(new GuestHouse("GH010", "윤슬하우스", "Busan"));
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//		try {
//			gh.insertGH(new GuestHouse("GH011", "윤슬하우스", "Busan"));
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//
//		try {
//			gh.updateGH(new GuestHouse("GH022", "윤슬하우스2", "Busan"));
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//		try {
//			gh.updateGH(new GuestHouse("GH011", "윤슬하우스", "Seoul"));
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
		
//		try {
//			gh.insertReservation(new Reservation(
//					"RV081", LocalDate.of(2025, 06, 12), LocalDate.of(2025, 06, 15), 2, 
//					new Room("RM001",
//							new GuestHouse("GH001", "소담하우스", "Seoul")
//							,"햇살방", "F", 55000, 2),
//					new Customer("C035", "임재현","01011110035",  LocalDate.of(1988,10,15), "M")
//					));
//		} catch (Exception e) {
//			System.out.println(e.getMessage());	
//		}

//		try {
//			gh.deleteGH("GH022");
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//		try {
//			gh.deleteGH("GH011");
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
		
		try {
			System.out.println(gh.getReservation("C010"));
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
		
}