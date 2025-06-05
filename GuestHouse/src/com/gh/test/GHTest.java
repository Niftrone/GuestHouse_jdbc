package com.gh.test;

import java.sql.SQLException;
import java.time.LocalDate;

import com.gh.dao.impl.GHDAOImpl;
import com.gh.vo.Customer;
import com.gh.vo.GuestHouse;
import com.gh.vo.Reservation;
import com.gh.vo.Room;

//public class GHTest implements Runnable {
public class GHTest{	
	GHDAOImpl gh;
	
//	public GHTest() throws Exception {
//		try {
//			gh = GHDAOImpl.getInstance();
//		} catch(Exception cnfe) {
//			System.out.println("GeHa Constructor : " + cnfe);
//		}
//	}
	
	public static void main(String[] args) throws Exception {
		
		GHDAOImpl gh = GHDAOImpl.getInstance();
//		GHTest geha = new GHTest();
//		Thread t = new Thread(geha);
//		t.start();
		
		System.out.println("@@@@@@@@@@@@@@@@insertCustomer 실행@@@@@@@@@@@@@@@@@@");
		
		try {
			gh.insertCustomer(new Customer(
							"c777",
							"Kingstone",
							"010-1111-2222",
							LocalDate.of(1996, 01, 13),
							"M"));
			gh.insertCustomer(new Customer(
					"c777",
					"Kingstone",
					"010-1111-2222",
					LocalDate.of(1996, 01, 13),
					"M"));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		System.out.println("@@@@@@@@@@@@@@@@updateCustomer 실행@@@@@@@@@@@@@@@@@@");
		
		// Update CHECK
		try {
			gh.updateCustomer(new Customer(
					"c777","Kingstone2","010-1111-3333",LocalDate.of(1996, 01, 13),"M"));
			gh.updateCustomer(new Customer(
					"sda456","Kingstone2","010-1111-3333",LocalDate.of(1996, 01, 13),"M"));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		// Delete CHECK

		System.out.println("@@@@@@@@@@@@@@@@deleteCustomer 실행@@@@@@@@@@@@@@@@@@");
		try {
			gh.deleteCustomer("c777");
			gh.updateCustomer(new Customer(
					"c777","Kingstone2","010-1111-3333",LocalDate.of(1996, 01, 13),"M"));

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		// getCustomer
		
		System.out.println("@@@@@@@@@@@@@@@@getCustomer 실행@@@@@@@@@@@@@@@@@@");
		
		try {
			System.out.println("=====C001 고객의 정보 조회1(예약 내역과 찜 목록 포함X)=====");
			System.out.println(gh.getCustomer("C001"));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		// getCustomer2
		try {
			System.out.println("=====C001 고객의 정보 조회2(예약 내역과 찜 목록 포함)=====");
			System.out.println(gh.getCustomer2("C001"));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
//		 getCustomer
		
		System.out.println("@@@@@@@@@@@@@@@@getCustomer 실행@@@@@@@@@@@@@@@@@@");
		
		try {
			System.out.println(gh.getAllCustomer());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		System.out.println("@@@@@@@@@@@@@@@@getAllGH 실행@@@@@@@@@@@@@@@@@@");
		
 		try {
			System.out.println("=====전체 게스트하우스 목록 조회=====");
			gh.getAllGH().stream().forEach(g->System.out.println(g));
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
 		try {
			System.out.println("=====서울 지역 전체 게스트하우스 목록 조회=====");
			gh.getAllGH("Seoul").stream().forEach(g->System.out.println(g));
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
 			
		try {
			System.out.println("=====부산 지역 전체 게스트하우스 목록 조회=====");
			gh.getAllGH("Busan").stream().forEach(g->System.out.println(g));
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		System.out.println("@@@@@@@@@@@@@@@@getAvailableRoom 실행@@@@@@@@@@@@@@@@@@");

		try {
			System.out.println("=====2023-08-24 ~ 2023-08-26 사이에 예약 가능한 F 방 목록 조회=====");
			// reservation 테이블에 ('RV101', 'C001', 'RM057', '2023-08-24', '2023-08-26', 124000, 1) 데이터 추가
			gh.getAvailableRoom(LocalDate.of(2023, 8, 24) , LocalDate.of(2023, 8, 26), "F", 1).stream().forEach(r-> System.out.print(r+"\t"));
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		System.out.println("\n@@@@@@@@@@@@@@@@insertReservation 실행@@@@@@@@@@@@@@@@@@");

		try {
	        gh.insertReservation(new Reservation(
	            "RV083", LocalDate.of(2025, 06, 20), LocalDate.of(2025, 06, 21), 1, 
	            new Room("RM073",
	                new GuestHouse("GH010", "별빛하우스", "Seoul")
	                ,"별헤는방", "F", 63000, 1),
	            new Customer("C035", "임재현","01011110035",  LocalDate.of(1988,10,15), "F")
	            ));
	    } catch(Exception e) {
	        System.out.println(e.getMessage());
	    }
		
		System.out.println("@@@@@@@@@@@@@@@@getReservation 실행@@@@@@@@@@@@@@@@@@");

 		try {
			System.out.println(gh.getReservation("C010"));
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		System.out.println("@@@@@@@@@@@@@@@@updateReservation 실행@@@@@@@@@@@@@@@@@@");

		try {
			gh.updateReservation(new Reservation(
					"RV083", LocalDate.of(2023, 03, 11), LocalDate.of(2023, 03, 16), 2, 
					new Room("RM040",
							new GuestHouse("GH001", "소담하우스", "Seoul")
							,"햇살방", "F", 55000, 2),
					new Customer("C035", "임재현","01011110035",  LocalDate.of(1988,10,15), "F")
					));
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		System.out.println("@@@@@@@@@@@@@@@@deleteReservation 실행@@@@@@@@@@@@@@@@@@");

		try {
			gh.deleteReservation("RV083");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		System.out.println("@@@@@@@@@@@@@@@@insertWishList 실행@@@@@@@@@@@@@@@@@@");

		try {
			gh.insertWishList("C002", "GH010");
			gh.insertWishList("C002", "GH001");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		System.out.println("@@@@@@@@@@@@@@@@deleteWishList 실행@@@@@@@@@@@@@@@@@@");

		try {
			gh.deleteWishList("C002", "GH010");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		System.out.println("@@@@@@@@@@@@@@@@getWishList 실행@@@@@@@@@@@@@@@@@@");
		
		// getWishList
		try {
			System.out.println("=====C002 고객의 찜 목록 조회=====");
			gh.getWishList("C002").stream().forEach(g->System.out.println(g));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		System.out.println("@@@@@@@@@@@@@@@@insertGH 실행@@@@@@@@@@@@@@@@@@");

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
		
		System.out.println("@@@@@@@@@@@@@@@@updateGH 실행@@@@@@@@@@@@@@@@@@");

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
		
		System.out.println("@@@@@@@@@@@@@@@@deleteGH 실행@@@@@@@@@@@@@@@@@@");

		try {
			gh.deleteGH("GH022");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			gh.deleteGH("GH011");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		System.out.println("@@@@@@@@@@@@@@@@repairRoom, setEventGH 실행@@@@@@@@@@@@@@@@@@");
		
		gh.repairRoom("RM067", LocalDate.of(2025,1,1), LocalDate.of(2025,12,31));
		gh.setEventGH("GH005", LocalDate.of(2024,1,1), LocalDate.of(2025,12,31), 0.5);
		
		try {
	        gh.insertReservation(new Reservation(
	            "RV084", LocalDate.of(2025, 06, 20), LocalDate.of(2025, 06, 21), 1, 
	            new Room("RM067",
	                new GuestHouse("GH009", "하늘하우스", "Seoul")
	                ,"창공방", "F", 51000, 1),
	            new Customer("C035", "임재현","01011110035",  LocalDate.of(1988,10,15), "F")
	            ));
	    } catch(Exception e) {
	        System.out.println(e.getMessage());
	    }
		
		try {
	        gh.insertReservation(new Reservation(
	            "RV084", LocalDate.of(2025, 06, 20), LocalDate.of(2025, 06, 21), 1, 
	            new Room("RM044",
	                new GuestHouse("GH005", "산들하우스", "Busan")
	                ,"숲길방", "M", 70000, 3),
	            new Customer("C035", "임재현","01011110035",  LocalDate.of(1988,10,15), "F")
	            ));
	    } catch(Exception e) {
	        System.out.println(e.getMessage());
	    }
		
		System.out.println("@@@@@@@@@@@@@@@@결과 실행@@@@@@@@@@@@@@@@@@");

 		try {
			System.out.println(gh.getReservation("C035"));
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		System.out.println("@@@@@@@@@@@@@@@@getAllRV 실행@@@@@@@@@@@@@@@@@@");

		try {
			System.out.println(gh.getAllRV());
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		try {
			System.out.println(gh.getAllRV(LocalDate.of(2023, 8, 24) , LocalDate.of(2023, 9, 30)));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			System.out.println(gh.getAllRV(LocalDate.of(2023, 8, 24) , LocalDate.of(2023, 9, 30), "GH010"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		System.out.println("@@@@@@@@@@@@@@@@getQuarterSale 실행@@@@@@@@@@@@@@@@@@");

		try {
			System.out.println(gh.getQuarterSale("GH010", 2023));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		System.out.println("@@@@@@@@@@@@@@@@getMonthSale 실행@@@@@@@@@@@@@@@@@@");

		try {
			System.out.println(gh.getMonthSale(2023, 1));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("@@@@@@@@@@@@@@@@getSeasonalCount 실행@@@@@@@@@@@@@@@@@@");

	 	// getSeasonalCount
	 	try {
	 		System.out.println("=====2023.06~2023.08 하계 기간과 2023.12~2024.02 동계 기간의 예약 손님 수 분석=====");
	 		System.out.println(gh.getSeasonalCount(2023));
	 	} catch (SQLException e) {
	 		System.out.println(e.getMessage());
	 	}
		
		System.out.println("@@@@@@@@@@@@@@@@getPopularGH 실행@@@@@@@@@@@@@@@@@@");

		try {
			System.out.println(gh.getPopularGH("Busan"));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		System.out.println("@@@@@@@@@@@@@@@@getGenderRatio 실행@@@@@@@@@@@@@@@@@@");

		try {
			System.out.println(gh.getGenderRatio("GH001", 2024));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		try {
			System.out.println(gh.getGenderRatio("GH001", 2024, 2));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

//	@Override
//	public void run() {
//		// 쓰레드가 작업하는 코드를 작성
//		// 실시간으로 reservation 테이블의 전체 정보를 가져와서 출력
//		while(true) {
//			try {
//				gh.getAllRV().stream().forEach(r->System.out.println(r));
//				System.out.println("@@@@@ 실시간 예약 정보 가져옴 @@@@@");
//				Thread.sleep(5000);
//			} catch (Exception e) {
//				System.out.println(e.getMessage());
//			}
//		}
//	}
}