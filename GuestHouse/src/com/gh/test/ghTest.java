package com.gh.test;

import java.time.LocalDate;

import com.gh.dao.impl.ghDAOImpl;
import com.gh.vo.Customer;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;

import com.gh.dao.impl.ghDAOImpl;
import com.gh.exception.DuplicateIDException;
import com.gh.exception.IDNotFoundException;
import com.gh.vo.GuestHouse;

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
		
		// Delete CHECK
		try {
			gh.deleteCustomer("c777");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			
		}
		
		try {
			gh.insertGH(new GuestHouse("GH010", "윤슬하우스", "Busan"));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			gh.insertGH(new GuestHouse("GH011", "윤슬하우스", "Busan"));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			gh.updateGH(new GuestHouse("GH022", "윤슬하우스2", "Busan"));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			gh.updateGH(new GuestHouse("GH011", "윤슬하우스", "Seoul"));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}
}