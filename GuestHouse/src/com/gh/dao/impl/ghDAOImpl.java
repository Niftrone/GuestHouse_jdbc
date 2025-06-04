package com.gh.dao.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
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

	// 싱글톤
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
	//dasdasd

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
		int days = rv.geteDate().getDayOfMonth() - rv.getsDate().getDayOfMonth();
		double discount = 0.0;

		DiscountInfo info = discountInfo.get(rv.getRvId());

		if (info != null) {
			boolean over = !(rv.geteDate().isBefore(info.sDate) || rv.getsDate().isAfter(info.eDate));
			if (over) {
				discount = info.rate;
			}
		}

		return (int) (rv.getRoom().getPrice() * days * (1 - discount) * rv.getCount());

	}
	
	private boolean checkRepair(Reservation rv) {
		RepairInfo info = repairInfo.get(rv.getRoom().getRmId());
		if(rv.getsDate() == info.sDate || rv.geteDate() == info.eDate)
			return true;

		return !rv.getsDate().isBefore(info.sDate) && !rv.geteDate().isAfter(info.eDate);
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
			throw new DMLException("고객 등록 중 Error가 발생하였습니다. | insertCustomer Error");
		} finally {
			closeAll(ps, conn);
		}
		
	}

	@Override
	public void updateCustomer(Customer cust) throws SQLException, IDNotFoundException {
		Connection conn = null;
		PreparedStatement ps = null;
		// TODO Auto-generated method stub

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
			throw new DMLException(cust.getName() + " 오류로 인하여 정보 수정 실패하였습니다.");
		} finally {
				closeAll(ps, conn);
		}
	}

	@Override
	public void deleteCustomer(String uId) throws SQLException, IDNotFoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public Customer getCustomer(String uId) throws SQLException, IDNotFoundException {
		// TODO Auto-generated method stub
		return null;
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

	public ArrayList<Room> getAvailableRoom(LocalDate sDate, LocalDate eDate, String gender) throws SQLException {
		ArrayList<Room> rooms = new ArrayList<Room>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
//		LocalDate sDate = LocalDate.of(2025, 6, 1);
//		LocalDate eDate = LocalDate.of(2025, 6, 3);
		Period period = Period.between(sDate, eDate);
		int p = period.getDays();
		try {
			for(int i=0; i<p; i++) {
				
			}
//			conn = getConnect();
//			// 서브쿼리에서 예약 시작일부터 종료일까지 예약 내역에서 rm_id 의 SUM(예약 count) 과 rm_id의 capacity를 비교해야 함
//			String query = "SELECT * FROM room WHERE rm_gender=? AND rm_id IN (SELECT rm_id FROM reservation GROUP BY id_rm, rv_sdate)";
//			ps = conn.prepareStatement(query);
//			ps.setString(1, gender);
//			rs = ps.executeQuery();
//			while(rs.next()) {
//				rooms.add(new Room(rs.getString("rm_id"), rs.getString("rm_name"), rs.getString("rm_gender"), rs.getInt("rm_price"), rs.getInt("capacity")));
//			}
		} finally {
//			closeAll(rs, ps, conn);
		}
		return rooms;
	}

	@Override
	public void insertReservation(Reservation rv) throws SQLException, DuplicateIDException  {
		Connection conn = null;
		PreparedStatement ps = null;
		Room room = rv.getRoom();
		
		if(rv.getCount() > room.getCapacity()) {
			System.out.println("방의 수용인원보다 많은 인원입니다.");
			return;
		}
			
		try {
			conn = getConnect();
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
			throw new DuplicateIDException(e.getMessage());
		} catch(SQLException e) {
			throw new DMLException(e.getMessage());
		} finally {
			closeAll(ps, conn);
		}

	}

	@Override
	public Reservation getReservation(String uId) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = getConnect();
			String query = "SELECT * FROM reservation ";
			
		}catch(SQLException e) {
			throw new DMLException(e.getMessage()); 
		}
		
		return null;
	}

	@Override
	public void updateReservation(Reservation rv) throws SQLException, IDNotFoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteReservation(String rvId) throws SQLException, IDNotFoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public void insertWishList(String uId, String ghId) throws SQLException, IDNotFoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteWishList(String uId, String ghId) throws SQLException, IDNotFoundException {
		// TODO Auto-generated method stub

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
		} finally {
			closeAll(rs, ps1, conn);
			closeAll(rs, ps2, conn);
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
		} finally {
			closeAll(rs, ps1, conn);
			closeAll(rs, ps2, conn);
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
		} finally {
			closeAll(rs, ps1, conn);
			closeAll(rs, ps2, conn);
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Reservation> getAllRV(LocalDate date) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Reservation> getAllRV(LocalDate date, String ghId) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Integer> getQuarterSale(String ghId, int year) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMonthSale(int year, int month) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getSeasonalCount(int year) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<GuestHouse> getPopularGH(String region) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getGenderRatio(String ghId, int year, int month) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}
