package com.gh.test;

import java.sql.SQLException;
import java.time.LocalDate;

import com.gh.dao.impl.ghDAOImpl;
import com.gh.exception.IDNotFoundException;
import com.gh.vo.Customer;
import com.gh.vo.GuestHouse;
import com.gh.vo.Reservation;
import com.gh.vo.Room;

public class ghTest {

	public static void main(String[] args) {
		
		ghDAOImpl gh = ghDAOImpl.getInstance();
		//String uId, String name, String phNum, LocalDate birthday, String gender
		
		try {
			System.out.println(gh.getPopularGH("BUSAN"));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		// INSERT CHECK
//		try {
//			gh.insertCustomer(new Customer(
//					"c777","Kingstone","010-1111-2222",LocalDate.of(1996, 01, 13),"M"));
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//		
//		// Update CHECK
//		try {
//			gh.updateCustomer(new Customer(
//					"c777","Kingstone2","010-1111-3333",LocalDate.of(1996, 01, 13),"M"));
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//		
//		// Delete CHECK
//
//		try {
//			gh.deleteCustomer("c777");
//			gh.updateCustomer(new Customer(
//					"c777","Kingstone2","010-1111-3333",LocalDate.of(1996, 01, 13),"M"));
//
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}

//		
//		// getCustomer
//		try {
//			System.out.println(gh.getAllCustomer());
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}

//		try {

//			gh.deleteCustomer("c777");
//			gh.updateCustomer(new Customer(
//					"c777","Kingstone2","010-1111-3333",LocalDate.of(1996, 01, 13),"M"));
//
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//			
//		}
		
		

// 		try {

// 			gh.deleteCustomer("c777");
// 			gh.updateCustomer(new Customer(
// 					"c777","Kingstone2","010-1111-3333",LocalDate.of(1996, 01, 13),"M"));

// 		} catch (Exception e) {
// 			System.out.println(e.getMessage());	
// 		}
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
		gh.setEventGH("GH010", LocalDate.of(2022, 8, 24) , LocalDate.of(2025, 9, 30), 0.1);
//		
		try {
			gh.deleteReservation("RV083");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IDNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
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
		
// 		try {
// 			System.out.println(gh.getReservation("C010"));
// 		}catch (Exception e) {
// 			System.out.println(e.getMessage());
// 		}
// 		try {
// 			System.out.println("=====전체 게스트하우스 목록 조회=====");
// 			gh.getAllGH().stream().forEach(g->System.out.println(g));
// 		} catch (SQLException e) {
// 			System.out.println(e.getMessage());
// 		}
// 		try {
// 			System.out.println("=====서울 지역 전체 게스트하우스 목록 조회=====");
// 			gh.getAllGH("Seoul").stream().forEach(g->System.out.println(g));
// 		} catch (SQLException e) {
// 			System.out.println(e.getMessage());
// 		}
// 		try {
// 			System.out.println("=====부산 지역 전체 게스트하우스 목록 조회=====");
// 			gh.getAllGH("Busan").stream().forEach(g->System.out.println(g));
// 		} catch (SQLException e) {
// 			System.out.println(e.getMessage());
// 		}
//		try {
//			System.out.println(gh.getReservation("C010"));
//		}catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//		try {
//			System.out.println("=====전체 게스트하우스 목록 조회=====");
//			gh.getAllGH().stream().forEach(g->System.out.println(g));
//		} catch (SQLException e) {
//			System.out.println(e.getMessage());
//		}
//		try {
//			System.out.println("=====서울 지역 전체 게스트하우스 목록 조회=====");
//			gh.getAllGH("Seoul").stream().forEach(g->System.out.println(g));
//		} catch (SQLException e) {
//			System.out.println(e.getMessage());
//		}
//		try {
//			System.out.println("=====부산 지역 전체 게스트하우스 목록 조회=====");
//			gh.getAllGH("Busan").stream().forEach(g->System.out.println(g));
//		} catch (SQLException e) {
//			System.out.println(e.getMessage());
//		}
		

//		try {
//			gh.updateReservation(new Reservation(
//					"RV081", LocalDate.of(2023, 03, 11), LocalDate.of(2023, 03, 16), 2, 
//					new Room("RM040",
//							new GuestHouse("GH001", "소담하우스", "Seoul")
//							,"햇살방", "F", 55000, 2),
//					new Customer("C035", "임재현","01011110035",  LocalDate.of(1988,10,15), "F")
//					));
//			
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//		
//		try {
//			System.out.println(gh.getAllRV());
//		} catch (SQLException e) {
//			System.out.println(e.getMessage());
//		}
		
//      try {
//        System.out.println(gh.getReservation("C010"));
//      }catch (Exception e) {
//        System.out.println(e.getMessage());
//      }
//		try {
//			System.out.println("=====2023-08-24 ~ 2023-08-26 사이에 예약 가능한 F 방 목록 조회=====");
//			// reservation 테이블에 ('RV101', 'C001', 'RM057', '2023-08-24', '2023-08-26', 124000, 1) 데이터 추가
//			gh.getAvailableRoom(LocalDate.of(2023, 8, 24) , LocalDate.of(2023, 8, 26), "F", 1).stream().forEach(r-> System.out.print(r+"\t"));
//		} catch (SQLException e) {
//			System.out.println(e.getMessage());
//		}

    //   try {
    //     System.out.println(gh.getReservation("C010"));
    //   }catch (Exception e) {
    //     System.out.println(e.getMessage());
    //   }
      
    //   /////////////////// YJ Method ///////////////////
    //   // insertGH
    //   System.out.println("=====게스트하우스 등록=====");
    //   try {
	// 		gh.insertGH(new GuestHouse("GH010", "윤슬하우스", "Busan"));
	// 	} catch (Exception e) {
	// 		System.out.println(e.getMessage());
	// 	}
	// 	try {
	// 		gh.insertGH(new GuestHouse("GH011", "윤슬하우스", "Busan"));
	// 	} catch (Exception e) {
	// 		System.out.println(e.getMessage());
	// 	}
	// 	// updateGH
	// 	System.out.println("=====게스트하우스 수정=====");
	// 	try {
	// 		gh.updateGH(new GuestHouse("GH022", "윤슬하우스2", "Busan"));
	// 	} catch (Exception e) {
	// 		System.out.println(e.getMessage());
	// 	}
	// 	try {
	// 		gh.updateGH(new GuestHouse("GH011", "윤슬하우스", "Seoul"));
	// 	} catch (Exception e) {
	// 		System.out.println(e.getMessage());
	// 	}
	// 	// deleteGH
	// 	System.out.println("=====게스트하우스 삭제=====");
	// 	try {
	// 		gh.deleteGH("GH022");
	// 	} catch (Exception e) {
	// 		System.out.println(e.getMessage());
	// 	}
	// 	try {
	// 		gh.deleteGH("GH011");
	// 	} catch (Exception e) {
	// 		System.out.println(e.getMessage());
	// 	}
	// 	// getAllGH()
	// 	try {
	// 		System.out.println("=====전체 게스트하우스 목록 조회=====");
	// 		gh.getAllGH().stream().forEach(g->System.out.println(g));
	// 	} catch (SQLException e) {
	// 		System.out.println(e.getMessage());
	// 	}
	// 	// getAllGH(String)
	// 	try {
	// 		System.out.println("=====서울 지역 전체 게스트하우스 목록 조회=====");
	// 		gh.getAllGH("Seoul").stream().forEach(g->System.out.println(g));
	// 	} catch (SQLException e) {
	// 		System.out.println(e.getMessage());
	// 	}
	// 	try {
	// 		System.out.println("=====부산 지역 전체 게스트하우스 목록 조회=====");
	// 		gh.getAllGH("Busan").stream().forEach(g->System.out.println(g));
	// 	} catch (SQLException e) {
	// 		System.out.println(e.getMessage());
	// 	}
	// 	// getAvailableRoom
	// 	try {
	// 		System.out.println("=====2023-08-24 ~ 2023-08-26 사이에 예약 가능한 F 방 목록 조회=====");
	// 		// reservation 테이블에 ('RV101', 'C001', 'RM057', '2023-08-24', '2023-08-26', 124000, 1) 데이터 추가
	// 		gh.getAvailableRoom(LocalDate.of(2023, 8, 24) , LocalDate.of(2023, 8, 26), "F", 1).stream().forEach(r-> System.out.print(r+"\t"));
	// 		System.out.println();
	// 	} catch (SQLException e) {
	// 		System.out.println(e.getMessage());
	// 	}
	// 	// getSeasonalCount
	// 	try {
	// 		System.out.println("=====2023.06~2023.08 하계 기간과 2023.12~2024.02 동계 기간의 예약 손님 수 분석=====");
	// 		System.out.println(gh.getSeasonalCount(2023));
	// 	} catch (SQLException e) {
	// 		System.out.println(e.getMessage());
	// 	}
		
//		try {
//			System.out.println(gh.getAllRV(LocalDate.of(2023, 8, 24) , LocalDate.of(2023, 9, 30)));
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		try {
//			System.out.println(gh.getAllRV(LocalDate.of(2023, 8, 24) , LocalDate.of(2023, 9, 30), "GH010"));
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}

		
		try {
			System.out.println(gh.getQuarterSale("GH010", 2023));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// getCustomer
		try {
			System.out.println("=====C001 고객의 정보 조회1(예약 내역과 찜 목록 포함)=====");
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
		// getWishList
		try {
			System.out.println("=====C001 고객의 찜 목록 조회=====");
			gh.getWishList("C001").stream().forEach(g->System.out.println(g));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
//		try {
//			System.out.println(gh.getMonthSale(2023, 1));
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}
}