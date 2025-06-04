package com.gh.test;

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