package com.minisheep.util;

import com.minisheep.bean.BaseFlightInfo;
import com.minisheep.bean.Knowledge;
import com.mysql.jdbc.PreparedStatement;
import sun.jvm.hotspot.tools.Tool;

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
	private Connection getConnection(){  //自己建的数据库
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

	private Connection getConnectionFlight(){  //航班数据库
		String url = "jdbc:mysql://10.1.16.20:3306/aiidb?characterEncoding=utf8";
		String username = "aii";
		String password = "_Aii123,";
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
	 * 根据飞机航班编号返回列表,需要在此处set要get的信息，否则会为null
	 */
	public static List<BaseFlightInfo> flightSearch(String carrier,String flightname){   //根据飞机航班编号返回列表
		String systemdate = ToolsUtil.getSystemDate();  //系统日期
		System.out.println("当前系统时间为:" + systemdate);
		//systemdate = "2016/12/28";  //移植的时候改成当日日期
		//String scheauleTimesql = "select * from Flight where CARRIER = ? and FLIGHT = ?"+" and OPDATE = ?"; //计划起飞时间
		String scheauleTimesql = "select * from t_flightinfo where carrier = ? and flight = ?";
		//System.out.println("sql语句为:" + scheauleTimesql);

		List<BaseFlightInfo> flights = new ArrayList<BaseFlightInfo>();
		MysqlUtil mysqlUtil = new MysqlUtil();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try{
			conn = mysqlUtil.getConnectionFlight();
			ps = (PreparedStatement) conn.prepareStatement(scheauleTimesql);
			ps.setString(1, carrier);
			ps.setString(2, flightname);
			//ps.setString(3, systemdate);
			System.out.println(carrier);
			System.out.println(flightname);
			rs = ps.executeQuery();
			while(rs.next()){
				//System.out.println("进来了!");
				BaseFlightInfo flight = new BaseFlightInfo();
				flight.setCarrier(rs.getString("carrier"));
				flight.setFlight(rs.getString("flight"));
				/*
				 * 
				 * flight.getScheduleTime() + ",实际起飞时间:" + flight.getActualTime() + ",航班状态:" + status;
				 */
				flight.setFlightId(rs.getInt("flightid"));
				if(rs.getString("actualtime") != null){
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("actualtime"));
					flight.setActualTime(afterDeal); //实际起飞时间
				}
				if(rs.getString("estimatetime") != null){
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("estimatetime"));
					flight.setEstimateTime(afterDeal);
				}
				if(rs.getString("scheduletime") != null){
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("scheduletime"));
					flight.setScheduleTime(afterDeal);
				}
				if(rs.getString("lastupdated") != null){
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("lastupdated"));
					flight.setLastUpdated(afterDeal); //实际起飞时间
				}
				//baseFlightInfo.setActualArrivalTime(rs.getString("ACTUALARRIVALTIME")); //实际到达时间
				flight.setFlightStatus(rs.getString("flightstatus"));
				flight.setOrigin(rs.getString("origin"));
				flight.setPassby(rs.getString("passby"));
				flight.setDestination(rs.getString("destination"));
				flight.setDirection(rs.getString("direction"));
				flight.setGate(rs.getString("gate")); //登机口
				flight.setTerminal(rs.getString("terminal")); //候机楼
				flight.setFlightStatus(rs.getString("flightstatus"));  //航班状态
				flight.setCheckinCounter(rs.getString("checkincounter")); //检票口
				flight.setIrregularCode(rs.getString("irregularcode"));

				if(rs.getString("schedulecheckinopen") != null){
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("schedulecheckinopen"));
					flight.setScheduleCheckinOpen(afterDeal);
				}
				if(rs.getString("schedulecheckinclose") != null) {
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("schedulecheckinclose"));
					flight.setScheduleCheckinClose(afterDeal);
				}
				if(rs.getString("actualcheckinopen") != null){
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("actualcheckinopen"));
					flight.setActualCheckinOpen(afterDeal);
				}
				if(rs.getString("actualcheckinclose") != null){
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("actualcheckinclose"));
					flight.setActualCheckinClose(afterDeal);
				}
				if (rs.getString("carouselscheduleopen") != null) {
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("carouselscheduleopen"));
					flight.setCarouselScheduleOpen(afterDeal);
				}
				if(rs.getString("carouselscheduleclose") != null){
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("carouselscheduleclose"));
					flight.setCarouselScheduleClose(afterDeal);
				}
				if(rs.getString("carouselactualopen") != null){
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("carouselactualopen"));
					flight.setCarouselActualOpen(afterDeal);
				}
				if(rs.getString("carouselactualclose") != null){
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("carouselactualclose"));
					flight.setCarouselActualClose(afterDeal);
				}

				if(rs.getString("gatescheduleopen") != null){
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("gatescheduleopen"));
					flight.setGateScheduleOpen(afterDeal);
				}
				if(rs.getString("gatescheduleclose") != null){
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("gatescheduleclose"));
					flight.setGateScheduleClose(afterDeal);
				}
				if(rs.getString("gateactualopen") != null){
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("gateactualopen"));
					flight.setGateActualOpen(afterDeal);
				}
				if(rs.getString("gateactualclose") != null){
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("gateactualclose"));
					flight.setGateActualClose(afterDeal);
				}
				if(rs.getString("gateestimateopen") != null){
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("gateestimateopen"));
					flight.setGateEstimateOpen(afterDeal);
				}
				if(rs.getString("gateestimateclose") != null){
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("gateestimateclose"));
					flight.setGateEstimateClose(afterDeal);
				}
				flight.setFlightTask(rs.getString("flighttask")); //航班任务状态
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
		String searchsql = "select iatacode from base_airport where displaycnname = ?";
		MysqlUtil mysqlUtil = new MysqlUtil();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		conn = mysqlUtil.getConnectionFlight();
		try {
			ps = (PreparedStatement) conn.prepareStatement(searchsql);
			ps.setString(1, cityName);
			rs = ps.executeQuery();
			if(rs.next()){
				return rs.getString("iatacode");
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
		根据英文简写返回中文机场名
	 */
	public static String CNNamebyIataCodeSearch(String iataCode){
		String searchsql = "select displaycnname from base_airport where iatacode = ?";
		MysqlUtil mysqlUtil = new MysqlUtil();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		conn = mysqlUtil.getConnectionFlight();
		try {
			ps = (PreparedStatement) conn.prepareStatement(searchsql);
			ps.setString(1, iataCode);
			rs = ps.executeQuery();
			if(rs.next()){
				return rs.getString("displaycnname");
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
	public static List<BaseFlightInfo> depCityAndarrCity(String dep, String arr){
		String searchsql = "select * from t_flightinfo where origin = ? and destination = ?";
		MysqlUtil mysqlUtil = new MysqlUtil();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<BaseFlightInfo> baseFlightInfos = new ArrayList<BaseFlightInfo>();

		conn = mysqlUtil.getConnectionFlight();
		try{
			ps = (PreparedStatement) conn.prepareStatement(searchsql);
			ps.setString(1, dep);
			ps.setString(2, arr);
			rs = ps.executeQuery();
			while(rs.next()){
				BaseFlightInfo baseFlightInfo = new BaseFlightInfo();

				if(rs.getString("actualtime") != null){
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("actualtime"));
					baseFlightInfo.setActualTime(afterDeal); //实际起飞时间
				}
				if(rs.getString("estimatetime") != null){
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("estimatetime"));
					baseFlightInfo.setEstimateTime(afterDeal);
				}
				if(rs.getString("scheduletime") != null){
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("scheduletime"));
					baseFlightInfo.setScheduleTime(afterDeal);
				}
				if(rs.getString("lastupdated") != null){
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("lastupdated"));
					baseFlightInfo.setLastUpdated(afterDeal);
				}
				if(rs.getString("schedulecheckinopen") != null){
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("schedulecheckinopen"));
					baseFlightInfo.setScheduleCheckinOpen(afterDeal);
				}
				if(rs.getString("schedulecheckinclose") != null){
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("schedulecheckinclose"));
					baseFlightInfo.setScheduleCheckinClose(afterDeal);
				}
				if(rs.getString("actualcheckinopen") != null){
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("actualcheckinopen"));
					baseFlightInfo.setActualCheckinOpen(afterDeal);
				}
				if(rs.getString("actualcheckinclose") != null){
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("actualcheckinclose"));
					baseFlightInfo.setActualCheckinClose(afterDeal);
				}
				if (rs.getString("carouselscheduleopen") != null) {
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("carouselscheduleopen"));
					baseFlightInfo.setCarouselScheduleOpen(afterDeal);
				}
				if(rs.getString("carouselscheduleclose") != null){
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("carouselscheduleclose"));
					baseFlightInfo.setCarouselScheduleClose(afterDeal);
				}
				if(rs.getString("carouselactualopen") != null){
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("carouselactualopen"));
					baseFlightInfo.setCarouselActualOpen(afterDeal);
				}
				if(rs.getString("carouselactualclose") != null){
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("carouselactualclose"));
					baseFlightInfo.setCarouselActualClose(afterDeal);
				}

				if(rs.getString("gatescheduleopen") != null){
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("gatescheduleopen"));
					baseFlightInfo.setGateScheduleOpen(afterDeal);
				}
				if(rs.getString("gatescheduleclose") != null){
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("gatescheduleclose"));
					baseFlightInfo.setGateScheduleClose(afterDeal);
				}
				if(rs.getString("gateactualopen") != null){
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("gateactualopen"));
					baseFlightInfo.setGateActualOpen(afterDeal);
				}
				if(rs.getString("gateactualclose") != null){
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("gateactualclose"));
					baseFlightInfo.setGateActualClose(afterDeal);
				}
				if(rs.getString("gateestimateopen") != null){
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("gateestimateopen"));
					baseFlightInfo.setGateEstimateOpen(afterDeal);
				}
				if(rs.getString("gateestimateclose") != null){
					String afterDeal = ToolsUtil.removeDotZero(rs.getString("gateestimateclose"));
					baseFlightInfo.setGateEstimateClose(afterDeal);
				}


				baseFlightInfo.setFlightId(rs.getInt("flightid"));  //FLIGHTID
				baseFlightInfo.setCarrier(rs.getString("carrier"));
				baseFlightInfo.setFlight(rs.getString("flight"));
				baseFlightInfo.setOrigin(rs.getString("origin"));
				baseFlightInfo.setDestination(rs.getString("destination"));

				baseFlightInfo.setFlightStatus(rs.getString("flightstatus"));
				baseFlightInfo.setDirection(rs.getString("direction"));
				baseFlightInfo.setGate(rs.getString("gate")); //登机口
				baseFlightInfo.setTerminal(rs.getString("terminal")); //候机楼
				baseFlightInfo.setFlightStatus(rs.getString("flightstatus"));  //航班状态
				baseFlightInfo.setCheckinCounter(rs.getString("checkincounter")); //检票口
				baseFlightInfo.setFlightTask(rs.getString("flighttask")); //航班任务状态
				baseFlightInfo.setIrregularCode(rs.getString("irregularcode"));
				baseFlightInfos.add(baseFlightInfo);
			}
		}catch (Exception e) {
			// TODO: handle exception
		}finally{
			mysqlUtil.closeConnection(conn, ps, rs);
		}
		return baseFlightInfos;
	}

	public static String codeTodescription(String statusCode){
		String searchsql = "select * from base_flightstatus where statuscode = ?";
		MysqlUtil mysqlUtil = new MysqlUtil();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		conn = mysqlUtil.getConnectionFlight();
		String result = "";
		try {
			ps = (PreparedStatement) conn.prepareStatement(searchsql);
			ps.setString(1, statusCode);
			rs = ps.executeQuery();
			while(rs.next()){
				result = rs.getString("description");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//System.out.println("飞机目前的状态为:" + result);
		return result;
	}

	public static int depAndarrCount(String direction){
		String searchsql = "select COUNT(*) from t_flightinfo where direction = ?";
		MysqlUtil mysqlUtil = new MysqlUtil();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		conn = mysqlUtil.getConnectionFlight();
		String result = "";
		int cnt = 0;
		try {
			ps = (PreparedStatement) conn.prepareStatement(searchsql);
			ps.setString(1, direction);
			rs = ps.executeQuery();
			while(rs.next()){
				result = rs.getString("COUNT(*)");
			}
			cnt = Integer.parseInt(result);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return cnt;
	}

	public static String flightTaskCodeChangeCN(String flightTaskCode){  //任务英文代码转中文
		String searchsql = "select description from `base_flighttask` where flighttaskcode = ?";
		MysqlUtil mysqlUtil = new MysqlUtil();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		conn = mysqlUtil.getConnectionFlight();
		String result = "";

		try {
			ps = (PreparedStatement) conn.prepareStatement(searchsql);
			ps.setString(1, flightTaskCode);
			rs = ps.executeQuery();
			while(rs.next()){
				result = rs.getString("description");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String flightCompany(String companyCode){  //查询航空公司
		String searchsql = "select cnname from base_airlines where iatacode = ?";

		MysqlUtil mysqlUtil = new MysqlUtil();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		conn = mysqlUtil.getConnectionFlight();
		String result = "";
		try {
			ps = (PreparedStatement) conn.prepareStatement(searchsql);
			ps.setString(1, companyCode);
			rs = ps.executeQuery();
			while(rs.next()){
				result = rs.getString("cnname");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String irregularCode(String irrCode){
		String searchsql = "select description from base_irregularcode where irregularcode = ?";

		MysqlUtil mysqlUtil = new MysqlUtil();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		conn = mysqlUtil.getConnectionFlight();
		String result = "";

		try {
			ps = (PreparedStatement) conn.prepareStatement(searchsql);
			ps.setString(1, irrCode);
			rs = ps.executeQuery();
			while(rs.next()){
				result = rs.getString("description");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
		//select description from base_irregularcode where irregularcode = "100010";
	}

	//根据航班号FlightId查询数据(待完成)

}
