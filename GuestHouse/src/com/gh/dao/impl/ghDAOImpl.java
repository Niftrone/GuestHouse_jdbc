package com.gh.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gh.dao.ghDAO;
import com.gh.exception.DMLException;
import com.gh.exception.DuplicateIDException;
import com.gh.exception.IDNotFoundException;
import com.gh.vo.Customer;
import com.gh.vo.GuestHouse;
import com.gh.vo.Reservation;
import com.gh.vo.Room;

import config.ServerInfo;

public class ghDAOImpl implements ghDAO {

	/// 싱글톤 ///
	// Test 클래스의 static 안이 아니라 생성자 안에서 드라이버 로딩 시키기
	private static ghDAOImpl dao = new ghDAOImpl("127.0.0.1");
	private ghDAOImpl(String serverIp) {
		try {
			Class.forName(ServerInfo.DRIVER_NAME);
			System.out.println("드라이버 로딩 성공...");
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}
	public static ghDAOImpl getInstance() {
		return dao;
	}

	/// 공통 로직 (외부, 다른 클래스에서 호출할 일이 없으므로 private) ///

	private static Map<String, DiscountInfo> discountInfo = new HashMap<>();
	private static Map<String, RepairInfo> repairInfo = new HashMap<>();

	private static class DiscountInfo {
		LocalDate sDate;
		LocalDate eDate;
		Double rate;

		DiscountInfo(LocalDate sDate, LocalDate eDate, Double rate) {
			this.sDate = sDate;
			this.eDate = eDate;
			this.rate = rate;
		}
	}
	
	private static class RepairInfo {
		LocalDate sDate;
		LocalDate eDate;
		
		RepairInfo(LocalDate sDate, LocalDate eDate) {
			this.sDate = sDate;
			this.eDate = eDate;
		}
	}

	private int discountedPrice(Reservation rv) {
	    long days = ChronoUnit.DAYS.between(rv.getsDate(), rv.geteDate());
	    if (days <= 0) return 0;

	    double discount = 0.0;

	    String ghId = rv.getRoom().getGh().getGhId();
	    DiscountInfo info = discountInfo.get(ghId);

	    if (info != null) {
	        boolean over = !(rv.geteDate().isBefore(info.sDate) || rv.getsDate().isAfter(info.eDate));
	        if (over) {
	            discount = info.rate;
	        }
	    }

	    int unitPrice = rv.getRoom().getPrice();
	    return (int) (unitPrice * days * rv.getCount() * (1 - discount));
	}

	
	private boolean checkRepair(Reservation rv) {
	    RepairInfo info = repairInfo.get(rv.getRoom().getRmId());
	    if (info == null) return false;

	    // 날짜 겹침 판별
	    return !(rv.geteDate().isBefore(info.sDate) || rv.getsDate().isAfter(info.eDate));
	}

	private Connection getConnect() throws SQLException {
		Connection conn = DriverManager.getConnection(ServerInfo.URL, ServerInfo.USER, ServerInfo.PASS);
		return conn;
	}

	private void closeAll(PreparedStatement ps, Connection conn) throws SQLException {
		if (ps != null)
			ps.close();
		if (conn != null)
			conn.close();
	}

	private void closeAll(ResultSet rs, PreparedStatement ps, Connection conn) throws SQLException {
		if (rs != null)
			rs.close();
		closeAll(ps, conn);
	}
	
	private Reservation createRV(ResultSet rs) throws SQLException {
		Reservation rv =  new Reservation(
							rs.getString("rv_id"),
							rs.getDate("rv_sdate").toLocalDate(),
							rs.getDate("rv_edate").toLocalDate(),
							rs.getInt("rv_price"),
							rs.getInt("count"),
							rs.getString("rm_id"),
							rs.getString("u_id")
							);
		return rv;
	}
	
	private boolean isRoomAvailable(String rmId, LocalDate sDate, LocalDate eDate, int count, String gender, Connection conn) throws SQLException {
	    int p = Period.between(sDate, eDate).getDays();
	    for (int i = 0; i < p; i++) {
	        String query = """
	            SELECT CASE 
	                     WHEN SUM(rv.count) >= rm.capacity 
	                          AND SUM(rv.count) + ? > rm.capacity 
	                     THEN rv.rm_id 
	                     ELSE NULL 
	                   END fullrmId
	              FROM reservation rv, user u, room rm
	             WHERE rv.u_id = u.u_id AND rv.rm_id = rm.rm_id
	               AND u.u_gender = ?
	               AND ? >= rv_sdate AND ? < rv_edate
	               AND rv.rm_id = ?
	             GROUP BY rv.rm_id
	        """;
	        try (PreparedStatement ps = conn.prepareStatement(query)) {
	            ps.setInt(1, count);
	            ps.setString(2, gender);
	            ps.setDate(3, java.sql.Date.valueOf(sDate.plusDays(i)));
	            ps.setDate(4, java.sql.Date.valueOf(sDate.plusDays(i)));
	            ps.setString(5, rmId);

	            try (ResultSet rs = ps.executeQuery()) {
	                if (rs.next() && rs.getString("fullrmId") != null) {
	                    return false; // 하루라도 꽉 찬 경우 예약 불가
	                }
	            }
	        }
	    }
	    return true; // 모든 날짜에 예약 가능
	}

	/// 비즈니스 로직 ///
	// 자바의 date를 sql date로 변환하는 함수를 따로 만들어야 하는가?
	@Override
	public void insertCustomer(Customer cust) throws SQLException, DuplicateIDException {
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			String insertQuery = "INSERT INTO user (u_id, u_name, birthday, u_gender, phnum, u_region) VALUES (?, ?, ?, ?, ?, ?)";
			
			conn = getConnect();
			ps = conn.prepareStatement(insertQuery);
			ps.setString(1, cust.getuId());
			ps.setString(2, cust.getName());
			ps.setDate(3, java.sql.Date.valueOf(cust.getBirthday()));
            ps.setString(4, cust.getGender());      
            ps.setString(5, cust.getPhNum());       
            // 고객이 추가될 때, 지역은 삽입하지 않는다.
            ps.setNull(6, java.sql.Types.VARCHAR); 
				
            int row = ps.executeUpdate();
            if(row != 1) {
            	throw new DMLException(cust.getName() + "님의 고객 등록이 실패하였습니다.");
            }
            
            System.out.println(cust.getName() + "님의 등록이 완료 되었습니다.");
			
		} catch (SQLIntegrityConstraintViolationException e) {
			throw new DuplicateIDException(cust.getuId() + " 는 이미 등록된 고객입니다.");
			
		} catch(SQLException e) {
			throw new DMLException("Insert Error로 인하여 고객 등록 실패하였습니다.");
		} finally {
			closeAll(ps, conn);
		}
		
	}

	@Override
	public void updateCustomer(Customer cust) throws SQLException, IDNotFoundException {
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			String updateQuery = "UPDATE user SET u_name = ?, birthday = ?, u_gender = ?, phnum = ? WHERE u_id = ?";

			conn = getConnect();
			ps = conn.prepareStatement(updateQuery);

            // --- birthday 유효성 검사 추가 ---
//            if (cust.getBirthday() == null) {
//                // birthday가 null이면 에러 발생
//                throw new IllegalArgumentException("고객의 생년월일 정보는 필수 입력 사항입니다.");
//            }
			
			ps.setString(1, cust.getName());
			ps.setDate(2, java.sql.Date.valueOf(cust.getBirthday()));
			ps.setString(3, cust.getGender());
			ps.setString(4, cust.getPhNum());

			// user id 입력
			ps.setString(5, cust.getuId());
			
            int row = ps.executeUpdate();
            
            // Record가 추가 되지 않으면 IDNotFoundException
            if (row == 0) {
                throw new IDNotFoundException(cust.getuId() + " 고객 ID가 존재하지 않아 정보 수정이 실패했습니다.");
            } 
            
            System.out.println(cust.getName() + "님의 정보가 수정 완료 되었습니다.");
			
		} catch (SQLException e) {
			throw new DMLException(cust.getName() + "님 Update Error로 인하여 정보 수정 실패하였습니다.");
		} finally {
			closeAll(ps, conn);
		}
	}

	@Override
	public void deleteCustomer(String uId) throws SQLException, IDNotFoundException {

		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			String deleteQuery = "DELETE FROM user WHERE u_id = ?";
			
			conn = getConnect();
			ps = conn.prepareStatement(deleteQuery);
			ps.setString(1, uId);
			
			int row = ps.executeUpdate();
            if (row == 0) {
                throw new IDNotFoundException(uId + " 라는 ID를 찾을 수 없어, 삭제 실패했습니다.");
            } 
            
            System.out.println(uId + "님의 정보 삭제 완료하였습니다.");
			
		} catch (SQLException e) {
			throw new DMLException("Delete Error로 인하여 " + uId + "의 고객 삭제 실패하였습니다.");
			
		}finally {
			closeAll(ps, conn);
		}
	}
	
	@Override
	public Customer getCustomer(String uId) throws SQLException, IDNotFoundException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Customer customer = null;
		
		try {
			String selectQuery = "SELECT u_id, u_name, birthday, u_gender, phnum FROM user WHERE u_id = ?";
			
			conn = getConnect();
			ps = conn.prepareStatement(selectQuery);
			ps.setString(1, uId);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				customer = new Customer(uId, 
										rs.getString("u_name"), 
										rs.getString("phnum"),
										rs.getDate("birthday").toLocalDate(), 
										rs.getString("u_gender"));
			} else {
				throw new IDNotFoundException(uId + " 라는 ID를 찾을 수 없어 고객 정보 불러오기 실패하였습니다.");
			}
			
		} catch (SQLException e) {
			throw new DMLException("getCustomer Error로 인하여 " + uId + "로 등록된 고객 정보 불러오기 실패하였습니다.");
		} finally {
			closeAll(rs, ps, conn);
		}
		
		return customer;
	}
	
	@Override
	public Customer getCustomer2(String uId) throws SQLException, IDNotFoundException {
		Customer cust = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnect();
			String query = "SELECT u_id, u_name, birthday, u_gender, phnum FROM user WHERE u_id=?";
			ps = conn.prepareStatement(query);
			ps.setString(1, uId);
			rs = ps.executeQuery();
			if(rs.next()) {
				cust = new Customer(rs.getString("u_id"),
									rs.getString("u_name"),
									rs.getString("phnum"),
									rs.getDate("birthday").toLocalDate(),
									rs.getString("u_gender"));
				cust.setWishList(getWishList(uId));
				cust.setRvList(getReservation(uId));
			} else {
				throw new IDNotFoundException(uId + " 라는 ID를 찾을 수 없어 고객 정보 조회에 실패하였습니다.");
			}
		} finally {
			closeAll(rs, ps, conn);
		}
		return cust;
	}
	
	@Override
	public ArrayList<GuestHouse> getWishList(String uId) throws SQLException {
		ArrayList<GuestHouse> wish = new ArrayList<GuestHouse>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnect();
			String query = """
						SELECT *
						FROM wishlist JOIN guesthouse USING(gh_id)
						WHERE u_id=?
						ORDER BY gh_id
							""";
			ps = conn.prepareStatement(query);
			ps.setString(1, uId);
			rs = ps.executeQuery();
			while(rs.next()) {
				wish.add(new GuestHouse(rs.getString("gh_id"), rs.getString("gh_name"), rs.getString("gh_region")));
			}
		} finally {
			closeAll(rs, ps, conn);
		}
		return wish;
	}
	
	@Override
	public ArrayList<Customer> getAllCustomer() throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<Customer> allCustomers = new ArrayList<Customer>();

		try {
			String selectQuery = "SELECT u_id, u_name, birthday, u_gender, phnum FROM user";

			conn = getConnect();
			ps = conn.prepareStatement(selectQuery);
			rs = ps.executeQuery();

			while (rs.next()) {
				
				// DB에 저장된 user Table의 birthday 값이 null일 경우 Error를 발생
				// SQL문으로 IFNULL() 사용하여 처리하려 했지만 LocalDate에서 인식을 못하는 Error가 발생
				// java에서 처리하기로 결정, birthday == null -> null or birthday != null -> rs.getDate()로 해결
				LocalDate birthday = (rs.getDate("birthday") != null) ? rs.getDate("birthday").toLocalDate() : null;

				// gender null일 경우 N/A (없다는 뜻임)
				String gender = (rs.getString("u_gender") != null) ? rs.getString("u_gender") : "N/A";

				allCustomers.add(new Customer(rs.getString("u_id"), 
											  rs.getString("u_name"), 
											  rs.getString("phnum"),
											  birthday, 
											  gender));
			}

		} catch (SQLException e) {
			throw new DMLException("getAllCustomer Error로 인하여 등록된 전체 고객 정보 불러오기 실패하였습니다.");
		} finally {
			closeAll(rs, ps, conn);
		}
		return allCustomers;
	}

	@Override
	public ArrayList<GuestHouse> getAllGH() throws SQLException {
		ArrayList<GuestHouse> ghs = new ArrayList<GuestHouse>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnect();
			String query = "SELECT * FROM guesthouse";
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			while(rs.next()) {
				ghs.add(new GuestHouse(rs.getString("gh_id"), rs.getString("gh_name"), rs.getString("gh_region")));
			}
		} finally {
			closeAll(rs, ps, conn);
		}
		return ghs;
	}

	@Override
	public ArrayList<GuestHouse> getAllGH(String region) throws SQLException {
		ArrayList<GuestHouse> ghs = new ArrayList<GuestHouse>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnect();
			String query = "SELECT * FROM guesthouse WHERE gh_region=?";
			ps = conn.prepareStatement(query);
			ps.setString(1, region);
			rs = ps.executeQuery();
			while(rs.next()) {
				ghs.add(new GuestHouse(rs.getString("gh_id"), rs.getString("gh_name"), rs.getString("gh_region")));
			}
		} finally {
			closeAll(rs, ps, conn);
		}
		return ghs;
	}
	
	
	@Override
	public ArrayList<String> getAvailableRoom(LocalDate sDate, LocalDate eDate, String gender, int count) throws SQLException {
	    ArrayList<String> availableRooms = new ArrayList<>();
	    Connection conn = null;
	    PreparedStatement ps = null;
	    ResultSet rs = null;

	    try {
	        conn = getConnect();

	        // 해당 성별과 일치하는 모든 방 조회
	        String query = "SELECT rm_id FROM room WHERE rm_gender = ?";
	        ps = conn.prepareStatement(query);
	        ps.setString(1, gender);
	        rs = ps.executeQuery();

	        // 각 방이 예약 가능한지 확인
	        while (rs.next()) {
	            String rmId = rs.getString("rm_id");

	            // 현재 방이 해당 날짜에 예약 가능하면 리스트에 추가
	            if (isRoomAvailable(rmId, sDate, eDate, count, gender, conn)) {
	                availableRooms.add(rmId);
	            }
	        }

	    } finally {
	        closeAll(rs, ps, conn);
	    }

	    return availableRooms;
	}

//	@Override
//	public ArrayList<String> getAvailableRoom(LocalDate sDate, LocalDate eDate, String gender, int count) throws SQLException {
//		ArrayList<String> fullrms = new ArrayList<String>(); // 해당 일자에 예약이 다 찬 방을 담을 ArrayList
//		ArrayList<String> rooms = new ArrayList<String>(); // 성별이 같은 방 목록을 담을 ArrayList
//		Connection conn = null;
//		PreparedStatement ps1 = null;
//		PreparedStatement ps2 = null;
//		ResultSet rs1 = null;
//		ResultSet rs2 = null;
//		int p = Period.between(sDate, eDate).getDays();
//		try {
//			conn = getConnect();
//			for(int i=0; i<p; i++) {
//				String query1 = "SELECT CASE WHEN sum(rv.count) >= rm.capacity AND sum(rv.count)+? > rm.capacity THEN rv.rm_id ELSE NULL END fullrmId FROM reservation rv,  user u, room rm WHERE rv.u_id = u.u_id AND rv.rm_id = rm.rm_id AND u.u_gender=? AND (?>=rv_sdate AND ?<rv_edate) GROUP BY rv.rm_id";
//				ps1 = conn.prepareStatement(query1);
//				ps1.setInt(1, count);
//				ps1.setString(2, gender);
//				ps1.setDate(3, java.sql.Date.valueOf(sDate.plusDays(i)));
//				ps1.setDate(4, java.sql.Date.valueOf(sDate.plusDays(i)));
//				rs1 = ps1.executeQuery();
//				while(rs1.next()) {
//					if(rs1.getString("fullrmId") != null && !fullrms.contains(rs1.getString("fullrmId")))
//						fullrms.add(rs1.getString("fullrmId"));
//				}
//			}
//			String query2 = "SELECT rm_id FROM room WHERE rm_gender=?";
//			ps2 = conn.prepareStatement(query2);
//			ps2.setString(1, gender);
//			rs2 = ps2.executeQuery();
//			while(rs2.next()) {
//				rooms.add(rs2.getString("rm_id"));
//			}
//			if(fullrms.size() != 0) {
//				for(String r : fullrms) {
//					rooms.remove(r);
//				}
//			}
//		} finally {
//			closeAll(rs1, ps1, null);
//			closeAll(rs2, ps2, null);
//		}
//		return rooms;
//	}

	@Override
	public void insertReservation(Reservation rv) throws SQLException, DuplicateIDException  {
		Connection conn = null;
		PreparedStatement ps = null;
		Room room = rv.getRoom();
		
		if(rv.getCount() > room.getCapacity()) {
			System.out.println("방의 수용인원보다 많은 인원입니다.");
			return;
		}
		
		if(checkRepair(rv)) {
			System.out.println("해당 방은 공사 중입니다.");
			return;
		}
			
		try {
			conn = getConnect();
			
			if (!isRoomAvailable(rv.getRoom().getRmId(), rv.getsDate(), rv.geteDate(), rv.getCount(), rv.getCust().getGender(), conn)) {
			    System.out.println("예약을 진행할수 없습니다.");
			    return;
			}
			
			String query = "INSERT INTO reservation(rv_id, u_id, rm_id, rv_sdate, rv_edate, rv_price, count) VALUES (?,?,?,?,?,?,?) ";
			ps = conn.prepareStatement(query);
			ps.setString(1, rv.getRvId());
			ps.setString(2, rv.getCust().getuId());
			ps.setString(3, room.getRmId());
			ps.setDate(4, java.sql.Date.valueOf(rv.getsDate()));
			ps.setDate(5, java.sql.Date.valueOf(rv.geteDate()));
			ps.setInt(6, discountedPrice(rv));
			ps.setInt(7, rv.getCount());
			
			System.out.println(ps.executeUpdate() == 1 ? "예약 성공" : "예약 실패");
			
		} catch(SQLIntegrityConstraintViolationException e) {
			throw new DuplicateIDException("중복된 예약 번호입니다.");
		} catch(SQLException e) {
			throw new DMLException(e.getMessage());
		} finally {
			closeAll(ps, conn);
		}

	}

	@Override
	public ArrayList<Reservation> getReservation(String uId) throws SQLException {
		ArrayList<Reservation> rvs = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = getConnect();
			String query = "SELECT * FROM reservation WHERE u_id = ?";
			
			ps = conn.prepareStatement(query);
			ps.setString(1, uId);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				rvs.add(createRV(rs));
			}
			return rvs;
			
		}catch(SQLException e) {
			throw new DMLException(e.getMessage()); 
		} finally {
			closeAll(rs, ps, conn);
		}
		
	}

	@Override
	public void updateReservation(Reservation rv) throws SQLException, IDNotFoundException {
		Connection conn = null;
		PreparedStatement ps = null;
		
		if(checkRepair(rv)) {
			System.out.println("해당 방은 공사 중입니다.");
			return;
		}
		
		try {
			conn = getConnect();

			if (!isRoomAvailable(rv.getRoom().getRmId(), rv.getsDate(), rv.geteDate(), rv.getCount(), rv.getCust().getGender(), conn)) {
			    System.out.println("예약을 진행할수 없습니다.");
			    return;
			}

			String query = "UPDATE reservation SET u_id = ?, rm_id = ?, rv_sdate = ?, rv_edate = ?, rv_price = ?, count = ? WHERE rv_id = ?";
	        
	        ps = conn.prepareStatement(query);
	        ps.setString(1, rv.getCust().getuId());
	        ps.setString(2, rv.getRoom().getRmId());
	        ps.setDate(3, java.sql.Date.valueOf(rv.getsDate()));
	        ps.setDate(4, java.sql.Date.valueOf(rv.geteDate()));
	        ps.setInt(5, discountedPrice(rv));
	        ps.setInt(6, rv.getCount());
	        ps.setString(7, rv.getRvId());

	        System.out.println(ps.executeUpdate() == 1 ? "업데이트 성공" : "업데이트 실패");

		} catch(SQLException e) {
			throw new DMLException(e.getMessage());
		} finally {
			closeAll(ps, conn);
		}
	}

	@Override
	public void deleteReservation(String rvId) throws SQLException, IDNotFoundException {
		Connection conn = null;
		PreparedStatement ps = null;

		try{
			conn = getConnect();
			String query = "DELETE FROM reservation WHERE rv_id = ?";

			ps = conn.prepareStatement(query);
			ps.setString(1, rvId);

			int result = ps.executeUpdate();
			if(result == 1){
				System.out.println("예약 삭제 완료");
			}else{
				throw new IDNotFoundException("해당 예약 번호가 없습니다.");
			}

		} catch (SQLException e) {
			throw new DMLException(e.getMessage());
		} finally{
			closeAll(ps, conn);
		}

	}

	@Override
	public void insertWishList(String uId, String ghId) throws SQLException, IDNotFoundException, DuplicateIDException {
		Connection conn = null;
		
		// SELECT 담당 PreparedStatement
		PreparedStatement selectPs = null;
		// INSERT 담당 PreparedStatement
		PreparedStatement insertPs = null;
		
		ResultSet rs = null;
		
		try {
			String selectQuery = "SELECT COUNT(*) FROM wishlist WHERE u_id = ? AND gh_id = ?";
			
			conn = getConnect();
			selectPs = conn.prepareStatement(selectQuery);
			selectPs.setString(1, uId);
			selectPs.setString(2, ghId);
			rs = selectPs.executeQuery();
			
			// 만약 고객이 wishlist에 담으려는 gh_id가 이미 있다면 count = 1;
			if(rs.next() && rs.getInt(1) > 0) {
				// 그때 throw new DuplicateIDException
				throw new DuplicateIDException("WishList에 이미 추가된 Guest House입니다.");
			} else {
				String insertQuery = "INSERT INTO wishlist (u_id, gh_id) VALUES (?, ?)"; 
				insertPs = conn.prepareStatement(insertQuery); 
				insertPs.setString(1, uId);
				insertPs.setString(2, ghId);
				insertPs.executeUpdate();
                
                System.out.println("고객 " + uId + "의 위시리스트에 " + ghId + "가 추가되었습니다.");
			}
			
		} catch (SQLIntegrityConstraintViolationException  e) {
			throw new IDNotFoundException("존재하지 않는 사용자 ID(" + uId + ") 또는 게스트하우스 ID(" + ghId + ")로 WishList 등록 실패.");
		} catch (SQLException e) {
			throw new DMLException("데이터베이스 오류로 위시리스트 추가 실패 :  " + e.getMessage());
		} finally {
			// 내일 쌤께 여쭈어보자.
			closeAll(rs, insertPs, conn);
			closeAll(rs, selectPs, conn);
		}
	}

	@Override
	public void deleteWishList(String uId, String ghId) throws SQLException, IDNotFoundException {
        Connection conn = null;
        PreparedStatement ps = null;
		
        try {
			conn = getConnect();
			
			String deleteQuery = "DELETE FROM wishlist WHERE u_id = ? AND gh_id = ?";
			ps = conn.prepareStatement(deleteQuery);
			ps.setString(1, uId);
			ps.setString(2, ghId);
			ps.executeUpdate();
			
			// 내일 무조건 출력 안되게 하기
			System.out.println("고객 " + uId + "의 위시리스트에 " + ghId + "가 삭제되었습니다.");
			
			
		} catch (SQLIntegrityConstraintViolationException e) {
			throw new IDNotFoundException("존재하지 않는 사용자 ID(" + uId + ") 또는 게스트하우스 ID(" + ghId + ")로 WishList 삭제 실패.");
		} catch (SQLException e) {
			throw new DMLException("데이터베이스 오류로 위시리스트 삭제 실패 : " + e.getMessage());
		} finally {
			closeAll(ps, conn);
		}
        
	}

	@Override
	public void insertGH(GuestHouse gh) throws SQLException, DuplicateIDException {
		Connection conn = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		try {
			conn = getConnect();
			String selectQuery = "SELECT gh_id FROM guesthouse WHERE gh_id=?";
			ps1 = conn.prepareStatement(selectQuery);
			ps1.setString(1, gh.getGhId());
			rs = ps1.executeQuery();
			if(!rs.next()) {
				String insertQuery = "INSERT INTO guesthouse (gh_id, gh_name, gh_region) VALUES (?,?,?)";
				ps2 = conn.prepareStatement(insertQuery);
				ps2.setString(1, gh.getGhId());
				ps2.setString(2, gh.getName());
				ps2.setString(3, gh.getRegion());
				System.out.println(ps2.executeUpdate()+" 개 INSERT 성공...insertGH()");
			} else {
				throw new DuplicateIDException("추가하려는 게스트하우스의 아이디는 이미 등록되어 있어 추가할 수 없습니다.");
			}
		} catch (SQLException e) {
				throw new DMLException("게스트하우스 정보를 등록하는 중 문제가 발생했습니다.");
		}finally {
			closeAll(rs, ps1, null);
			closeAll(null, ps2, conn);
		}

	}

	@Override
	public void updateGH(GuestHouse gh) throws SQLException, IDNotFoundException {
		Connection conn = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		try {
			conn = getConnect();
			String selectQuery = "SELECT gh_id FROM guesthouse WHERE gh_id=?";
			ps1 = conn.prepareStatement(selectQuery);
			ps1.setString(1, gh.getGhId());
			rs = ps1.executeQuery();
			if(rs.next()) {
				String updateQuery = "UPDATE guesthouse SET gh_name=?, gh_region=? WHERE gh_id =?";
				ps2 = conn.prepareStatement(updateQuery);
				ps2.setString(1, gh.getName());
				ps2.setString(2, gh.getRegion());
				ps2.setString(3, gh.getGhId());
				System.out.println(ps2.executeUpdate()+" 개 UPDATE 성공...updateGH()");
			} else {
				throw new IDNotFoundException("수정하려는 게스트하우스는 없는 id 입니다.");
			}
		} catch (SQLException e) {
			throw new DMLException("게스트하우스 정보를 수정하던 중 문제가 발생했습니다.");
		} finally {
			closeAll(rs, ps1, null);
			closeAll(null, ps2, conn);
		}
	}

	@Override
	public void deleteGH(String ghId) throws SQLException, IDNotFoundException {
		Connection conn = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		try {
			conn = getConnect();
			String selectQuery = "SELECT gh_id FROM guesthouse WHERE gh_id=?";
			ps1 = conn.prepareStatement(selectQuery);
			ps1.setString(1, ghId);
			rs = ps1.executeQuery();
			if(rs.next()) {
				String deleteQuery = "DELETE FROM guesthouse WHERE gh_id =?";
				ps2 = conn.prepareStatement(deleteQuery);
				ps2.setString(1, ghId);
				System.out.println(ps2.executeUpdate()+" 개 DELETE 성공...deleteGH()");
			} else {
				throw new IDNotFoundException("삭제하려는 게스트하우스는 없는 id 입니다.");
			}
		} catch (SQLException e) {
			throw new DMLException("게스트하우스 정보를 삭제하던 중 문제가 발생했습니다."); 
		}finally {
			closeAll(rs, ps1, null);
			closeAll(null, ps2, conn);
		}
	}

	@Override
	public void repairRoom(String rmId, LocalDate sDate, LocalDate eDate){
		repairInfo.put(rmId,new RepairInfo(sDate, eDate));
	}

	@Override
	public void setEventGH(String ghId, LocalDate sDate, LocalDate eDate, double rate){
		discountInfo.put(ghId, new DiscountInfo(sDate, eDate, rate));
	}

	@Override
	public ArrayList<Reservation> getAllRV() throws SQLException {
		ArrayList<Reservation> rvs = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = getConnect();
			String query = "SELECT * FROM reservation";
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				rvs.add(createRV(rs));
			}
			return rvs;
		}catch(SQLException e) {
			throw new DMLException(e.getMessage());
		}
		
	}

	@Override
	public ArrayList<Reservation> getAllRV(LocalDate sDate, LocalDate eDate) throws SQLException {
		ArrayList<Reservation> rvs = new ArrayList<>();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try{
			conn = getConnect();
			String query = """
							SELECT * FROM reservation
							WHERE rv_sdate >= ? AND rv_edate <= ?
							""";
			ps = conn.prepareStatement(query);
			ps.setDate(1, java.sql.Date.valueOf(sDate));
			ps.setDate(2, java.sql.Date.valueOf(eDate));
			rs = ps.executeQuery();
			
			while (rs.next()) {
				rvs.add(createRV(rs));
			}
		} catch (SQLException e) {
			throw new DMLException(e.getMessage());
		} finally {
			closeAll(rs, ps, conn);
		}
		return rvs;
	}

	@Override
	public ArrayList<Reservation> getAllRV(LocalDate sDate, LocalDate eDate, String ghId) throws SQLException {
		ArrayList<Reservation> rvs = new ArrayList<>();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try{
			conn = getConnect();
			String query = """
							SELECT * FROM reservation r
							LEFT JOIN room rm ON r.rm_id = rm.rm_id
							WHERE rv_sdate >= ? 
							AND rv_edate <= ? 
							AND gh_id = ?
							""";

			ps = conn.prepareStatement(query);
			ps.setDate(1, java.sql.Date.valueOf(sDate));
			ps.setDate(2, java.sql.Date.valueOf(eDate));
			ps.setString(3, ghId);

			rs = ps.executeQuery();
			
			while (rs.next()) {
				rvs.add(createRV(rs));
			}

		} catch (SQLException e) {
			throw new DMLException(e.getMessage());
		} finally {
			closeAll(rs, ps, conn);
		}
		return rvs;
	}

	@Override
	public Map<String, Integer> getQuarterSale(String ghId, int year) throws SQLException {
		Map<String, Integer> sales = new HashMap<>();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = getConnect();
			String query = """
							SELECT CASE
									WHEN month(rv_sdate) BETWEEN 1 AND 3 THEN 'Q1'
									WHEN month(rv_sdate) BETWEEN 4 AND 6 THEN 'Q2'
									WHEN month(rv_sdate) BETWEEN 7 AND 9 THEN 'Q3'
									WHEN month(rv_sdate) BETWEEN 10 AND 12 THEN 'Q4'
							        END quarter,
							        SUM(rv_price) as total
							FROM reservation r
							LEFT JOIN room rm ON r.rm_id = rm.rm_id
							WHERE gh_id = ?
							AND year(rv_sdate) = ?
							GROUP BY 1;
						   """;
			ps = conn.prepareStatement(query);
			ps.setString(1, ghId);
			ps.setInt(2, year);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				sales.put(rs.getString("quarter"), rs.getInt("total"));
			}
			
			for(String q : List.of("Q1", "Q2", "Q3", "Q4")) {
				sales.putIfAbsent(q, 0);
			}
			
		}catch(SQLException e) {
			throw new DMLException(e.getMessage());
		}finally {
			closeAll(rs, ps, conn);
		}
		
		return sales;
	}

	@Override
	public int getMonthSale(int year, int month) throws SQLException {
		int total_price = 0;
		
		LocalDate start = LocalDate.of(year, month, 1);
		LocalDate end = start.plusMonths(1);
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = getConnect();
			String query = """
						   SELECT sum(rv_price) as total FROM reservation 
						   WHERE rv_sdate >= ? AND rv_sdate < ?
						   """;
			ps = conn.prepareStatement(query);
			ps.setDate(1, java.sql.Date.valueOf(start));
			ps.setDate(2, java.sql.Date.valueOf(end));
			rs = ps.executeQuery();
			
			if (rs.next()) {
			    total_price = rs.getObject("total") != null ? rs.getInt("total") : 0;
			}
			
		} catch(SQLException e) {
			throw new DMLException(e.getMessage());
		} finally {
			closeAll(rs, ps, conn);
		}
		
		return total_price;
	}

	@Override
	public String getSeasonalCount(int year) throws SQLException {
		String ans = "";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnect();
			String query = """
						SELECT
						SUM(CASE WHEN YEAR(rv_sdate) = ? AND MONTH(rv_sdate) IN (6, 7, 8)
							THEN count ELSE 0 END) AS summer,
						SUM(CASE WHEN (YEAR(rv_sdate) = ? AND MONTH(rv_sdate) = 12)
								OR (YEAR(rv_sdate) = ? AND MONTH(rv_sdate) IN (1, 2))
							THEN count ELSE 0 END) AS winter
						FROM reservation
							""";
			ps = conn.prepareStatement(query);
			ps.setInt(1, year);
			ps.setInt(2, year);
			ps.setInt(3, year+1);
			rs = ps.executeQuery();
			if(rs.next()) {
				ans = year%100+" Summer : "+String.valueOf(rs.getInt("summer"))+" / "+year%100+(year%100+1)+" Winter : "+String.valueOf(rs.getInt("winter"));
			}
		} finally {
			closeAll(rs, ps, conn);
		}
		return ans;
	}

	@Override
	public Map<Integer, GuestHouse> getPopularGH(String region) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		Map<Integer, GuestHouse> popularGHList = new HashMap<>();
		
		try {
			/*
			 *  각 게스트하우스별 예약 건수를 바탕으로 1~5위 까지 순위를 매긴다.
			 *  DENSE_RANK()을 통해서 순위가 공동이어도 1,1,2,3,4,5 이런식으로 순위가 매겨진다.
			 *  중복 값이 발생될 수 있기에, 2차적으로 게하별 매출을 비교하여 순위를 반드시 중복없이 매기도록 한다.
			 */
			String selectQuery = """
					SELECT gh_id, gh_name, gh_region, total_reservations, total_sales, ranking"
					FROM (SELECT gh.gh_id, gh.gh_name, gh.gh_region, COUNT(r.rv_id) AS total_reservations, SUM(r.rv_price) AS total_sales,
					 	  DENSE_RANK() OVER(PARTITION BY gh.gh_region ORDER BY COUNT(r.rv_id) DESC, SUM(r.rv_price) DESC) AS ranking
					 	  FROM guesthouse gh
					 	  LEFT JOIN room rm ON gh.gh_id = rm.gh_id
					      LEFT JOIN reservation r ON rm.rm_id = r.rm_id
	 			  		  WHERE gh.gh_region = ?
						  GROUP BY gh.gh_id, gh.gh_name, gh.gh_region) AS ranked_gh
	 			     ORDER BY ranking ASC, total_reservations DESC, gh_id ASC
	 			     LIMIT 5
					""";
			
			conn = getConnect();
			ps = conn.prepareStatement(selectQuery);
			ps.setString(1, region);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				popularGHList.put(
								  rs.getInt("ranking"),
								  new GuestHouse(
								  rs.getString("gh_id"),
								  rs.getString("gh_name"),
								  rs.getString("gh_region")));
			}
			
		} catch (SQLException e) {
			throw new DMLException("인기 게스트하우스 정보를 불러오는 데 실패하였습니다.");
		} finally {
			closeAll(rs, ps, conn);
		}
		return popularGHList;
	}

	// 연도 + 월 별 성별 통계
	@Override
	public String getGenderRatio(String ghId, int year, int month) throws SQLException {
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String genderRatio  = null;
		
		
		try {
			conn = getConnect();
			String selectQuery = """
						SELECT
					 		SUM(CASE WHEN u.u_gender = 'M' THEN 1 ELSE 0 END) AS male_count,\r\n
					     	SUM(CASE WHEN u.u_gender = 'F' THEN 1 ELSE 0 END) AS female_count,\r\n
					     	COUNT(r.rv_id) AS total_count
					 	FROM reservation r
					 	JOIN room rm ON r.rm_id = rm.rm_id
					 	JOIN user u ON r.u_id = u.u_id
					 	WHERE rm.gh_id = ?
					    AND YEAR(r.rv_sdate) = ?
					 	AND MONTH(r.rv_sdate) = ?
					""" ;
			
			ps = conn.prepareStatement(selectQuery);
			
			ps.setString(1, ghId);
			ps.setInt(2, year);
			ps.setInt(3, month);
			
			rs = ps.executeQuery();

			if (rs.next()) {
				// 남성 예약 건수
				int maleCount = rs.getInt("male_count");
				// 여성 예약 건수 가져오기
				int femaleCount = rs.getInt("female_count");
				// 총 예약 건수 가져오기
				int totalCount = rs.getInt("total_count");

                if (totalCount == 0) {
                    genderRatio = "해당 기간에 예약이 없습니다.";
                } else {
                    double maleRatio = (double) maleCount / totalCount * 100;
                    double femaleRatio = (double) femaleCount / totalCount * 100;

                    genderRatio = String.format(year + "년 " + month + "월 예약 남녀비율 통계 : 남성 : %.2f%%, 여성 : %.2f%%", maleRatio, femaleRatio);
                }
            } 
		} catch (SQLException e) {
			throw new DMLException("GenderRatio Error로 인하여 정보를 불러오지 못하였습니다.");
		} finally {
			closeAll(rs, ps, conn);
		}
		
		return genderRatio;
	}
	
	// 연도별 성별 통계
	@Override
	public String getGenderRatio(String ghId, int year) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String genderRatio  = null;
		
		
		try {
			conn = getConnect();
			String selectQuery = """ 
						SELECT
							SUM(CASE WHEN u.u_gender = 'M' THEN 1 ELSE 0 END) AS male_count,
					  		SUM(CASE WHEN u.u_gender = 'F' THEN 1 ELSE 0 END) AS female_count,
					  		COUNT(r.rv_id) AS total_count
						FROM reservation r
						JOIN room rm ON r.rm_id = rm.rm_id
						JOIN user u ON r.u_id = u.u_id
						WHERE rm.gh_id = ?
					    AND YEAR(r.rv_sdate) = ?
					""";
			
			ps = conn.prepareStatement(selectQuery);
			
			ps.setString(1, ghId);
			ps.setInt(2, year);
			
			rs = ps.executeQuery();

			if (rs.next()) {
				// 남성 예약 건수
				int maleCount = rs.getInt("male_count");
				// 여성 예약 건수 가져오기
				int femaleCount = rs.getInt("female_count");
				// 총 예약 건수 가져오기
				int totalCount = rs.getInt("total_count");

                if (totalCount == 0) {
                    genderRatio = "해당 기간에 예약이 없습니다.";
                } else {
                    double maleRatio = (double) maleCount / totalCount * 100;
                    double femaleRatio = (double) femaleCount / totalCount * 100;

                    genderRatio = String.format(year + "년 예약 남녀비율 통계  남성 : %.2f%%, 여성 : %.2f%%", maleRatio, femaleRatio);
                }
            } 
		} catch (SQLException e) {
			throw new DMLException("GenderRatio Error로 인하여 정보를 불러오지 못하였습니다.");
		} finally {
			closeAll(rs, ps, conn);
		}
		
		return genderRatio;
	}
}