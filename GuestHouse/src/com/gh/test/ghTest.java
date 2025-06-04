package com.gh.test;

import java.time.LocalDate;

import com.gh.dao.impl.ghDAOImpl;
import com.gh.vo.Customer;

public class ghTest {

	public static void main(String[] args) {
		
		ghDAOImpl ghservice = ghDAOImpl.getInstance();
		//String uId, String name, String phNum, LocalDate birthday, String gender
		
		// INSERT CHECK
//		try {
//			ghservice.insertCustomer(new Customer(
//					"c777","Kingstone","010-1111-2222",LocalDate.of(1996, 01, 13),"M"));
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//			
//		}
		
		// Update CHECK
		try {
			ghservice.updateCustomer(new Customer(
					"c777","Kingstone2","010-1111-3333",LocalDate.of(1996, 01, 13),"M"));
		} catch (Exception e) {
			System.out.println(e.getMessage());
			
		}
		
		
		
		
	}

}
