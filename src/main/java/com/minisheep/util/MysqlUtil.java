package com.minisheep.util;

import com.minisheep.Bean.Flight;
import com.minisheep.Bean.FlightDetail;
import com.minisheep.Bean.Knowledge;
import com.mysql.jdbc.PreparedStatement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by minisheep on 16/12/22.
 */
public class MysqlUtil {
	private Connection getConnection(){
		String url = "jdbc:mysql://localhost:3306/LuceneTestDemo?characterEncoding=utf8";
		String username = "root";
		String password = "220015";
		Connection connection = null;
		try{
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(url,username,password);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return connection;
	}
	private void closeConnection(Connection connection,PreparedStatement ps,ResultSet rs){
		try {
			if(null != rs)
				rs.close();
			if(null != ps)
				ps.close();
			if(null != connection){
				connection.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static List<Knowledge> findAllKownLedge(){
		List<Knowledge> knowledges = new ArrayList<Knowledge>();
		String sql = "select * from knowledge";
		MysqlUtil mysqlUtil = new MysqlUtil();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			conn = mysqlUtil.getConnection();
			ps = (PreparedStatement) conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				Knowledge knowledge = new Knowledge();
				knowledge.setId(rs.getInt("id"));
				knowledge.setQuestion(rs.getString("question"));
				knowledge.setAnswer(rs.getString("answer"));
				knowledge.setCategory(rs.getInt("category"));
				knowledges.add(knowledge);
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			mysqlUtil.closeConnection(conn, ps, rs);
		}
		return knowledges;
	}
	
	public static int getLastCategory(String openId){
		int charCategory = -1;
		String sql = "select chat_category from chat_log where open_id=? order by id desc limit 0,1"; //0偏移1
		
		MysqlUtil mysqlUtil = new MysqlUtil();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			conn = mysqlUtil.getConnection();
			ps = (PreparedStatement) conn.prepareStatement(sql);
			ps.setString(1, openId);
			rs = ps.executeQuery();
			if(rs.next()){
				charCategory = rs.getInt("chat_category");
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			mysqlUtil.closeConnection(conn, ps, rs);
		}
		return charCategory;
	}
	
	public static String getKnowledSub(int knowledgeId){
		String knowledgeAnswer = "";
		String sql = "select answer from knowledge_sub where pid=? order by rand() limit 0,1";
		
		MysqlUtil mysqlUtil = new MysqlUtil();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			conn = mysqlUtil.getConnection();
			ps = (PreparedStatement) conn.prepareStatement(sql);
			ps.setInt(1, knowledgeId);
			rs = ps.executeQuery();
			if(rs.next()){
				knowledgeAnswer = rs.getString("answer");
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			mysqlUtil.closeConnection(conn, ps, rs);
		}
		return knowledgeAnswer;
	}
	
	public static String getJoke(){
		String jokeContent = "";
		String sql = "select joke_content from joke order by rand() limit 0,1";
		
		MysqlUtil mysqlUtil = new MysqlUtil();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			conn = mysqlUtil.getConnection();
			ps = (PreparedStatement) conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				jokeContent = rs.getString("joke_content");
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			mysqlUtil.closeConnection(conn, ps, rs);
		}
		return jokeContent;
	}
	
	public static void saveChatLog(String openId,String createTime,String reqMsg,String respMsg,int chatCategory){
		String sql = "insert into chat_log(open_id,create_time,req_msg,resp_msg,chat_category) value(?,?,?,?,?)";
		
		MysqlUtil mysqlUtil = new MysqlUtil();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			conn = mysqlUtil.getConnection();
			ps = (PreparedStatement) conn.prepareStatement(sql);
			ps.setString(1, openId);
			ps.setString(2, createTime);
			ps.setString(3, reqMsg);
			ps.setString(4, respMsg);
			ps.setInt(5, chatCategory);
			ps.executeUpdate();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			mysqlUtil.closeConnection(conn, ps, rs);
		}
	}
	
	public static void addKnowledge(Knowledge item){
		String sql = "insert into knowledge(question,answer,category) values(?,?,?)";
		
		MysqlUtil mysqlUtil = new MysqlUtil();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			conn = mysqlUtil.getConnection();
			ps = (PreparedStatement) conn.prepareStatement(sql);
			ps.setString(1, item.getQuestion());
			ps.setString(2, item.getAnswer());
			ps.setInt(3, item.getCategory());
			ps.executeUpdate();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			mysqlUtil.closeConnection(conn, ps, rs);
		}
	}
	
	/*
	 * 根据飞机航班编号返回列表
	 */
	public static List<Flight> flightSearch(String carrier,String flightname){   //根据飞机航班编号返回列表
		String systemdate = ToolsUtil.getSystemDate();  //系统日期
		System.out.println("当前系统时间为:" + systemdate);
		//systemdate = "2016/12/28";  //移植的时候改成当日日期
		//String scheauleTimesql = "select * from Flight where CARRIER = ? and FLIGHT = ?"+" and OPDATE = ?"; //计划起飞时间
		String scheauleTimesql = "select * from Flight where CARRIER = ? and FLIGHT = ?";
		//System.out.println("sql语句为:" + scheauleTimesql);
		
		List<Flight> flights = new ArrayList<Flight>();
		MysqlUtil mysqlUtil = new MysqlUtil();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try{
			conn = mysqlUtil.getConnection();
			ps = (PreparedStatement) conn.prepareStatement(scheauleTimesql);
			ps.setString(1, carrier);
			ps.setString(2, flightname);
			//ps.setString(3, systemdate);
			System.out.println(carrier);
			System.out.println(flightname);
			rs = ps.executeQuery();
			while(rs.next()){
				//System.out.println("进来了!");
				Flight flight = new Flight();
				flight.setCarrier(rs.getString("CARRIER"));
				flight.setFlight(rs.getString("FLIGHT"));
				/*
				 * 
				 * flight.getScheduleTime() + ",实际起飞时间:" + flight.getActualTime() + ",航班状态:" + status;
				 */
				
				flight.setScheduleTime(rs.getString("SCHEDULETIME"));
				flight.setActualTime(rs.getString("ACTUALTIME"));
				flight.setFlightStatus(rs.getString("FLIGHTSTATUS"));
				flight.setLastUpdated(rs.getString("LASTUPDATED"));
				flights.add(flight);
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			mysqlUtil.closeConnection(conn, ps, rs);
		}
		return flights;
	}
	
	/*
	 * 根据中文名返回英文简写
	 */
	public static String IataCodebyCNnameSearch(String cityName){
		String searchsql = "select IATACODE from CityMap where DISPLAYCNNAME = ?";
		MysqlUtil mysqlUtil = new MysqlUtil();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		conn = mysqlUtil.getConnection();
		try {
			ps = (PreparedStatement) conn.prepareStatement(searchsql);
			ps.setString(1, cityName);
			rs = ps.executeQuery();
			if(rs.next()){
				return rs.getString("IATACODE");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			mysqlUtil.closeConnection(conn, ps, rs);
		}
		return "";
	}
	
	/*
	 * 出发城市名和抵达城市名
	 */
	public static List<FlightDetail> depCityAndarrCity(String dep,String arr){
		String searchsql = "select * from FlightDetail where ORIGIN = ? and DESTINATION = ?";
		MysqlUtil mysqlUtil = new MysqlUtil();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<FlightDetail> flightDetails = new ArrayList<FlightDetail>();
		
		conn = mysqlUtil.getConnection();
		try{
			ps = (PreparedStatement) conn.prepareStatement(searchsql);
			ps.setString(1, dep);
			ps.setString(2, arr);
			rs = ps.executeQuery();
			while(rs.next()){
				FlightDetail flightDetail = new FlightDetail();
				flightDetail.setFlightId(rs.getInt("FLIGHTID"));  //FLIGHTID
				flightDetail.setScheduleDepartureTime(rs.getString("SCHEDULEDEPARTURETIME"));  //计划起飞时间
				flightDetail.setScheduleArrivalTime(rs.getString("SCHEDULEARRIVALTIME")); //计划到达时间
				flightDetail.setActualDepartureTime(rs.getString("ACTUALDEPARTURETIME")); //实际起飞时间
				flightDetail.setActualArrivalTime(rs.getString("ACTUALARRIVALTIME")); //实际到达时间
				flightDetail.setLastUpdated(rs.getString("LASTUPDATED"));  //最后更新时间
				flightDetails.add(flightDetail);
			}
		}catch (Exception e) {
			// TODO: handle exception
		}finally{
			mysqlUtil.closeConnection(conn, ps, rs);
		}
		return flightDetails;
	}
}
