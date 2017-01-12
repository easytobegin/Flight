package com.minisheep.chatservice;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import com.minisheep.bean.Flight;
import com.minisheep.bean.FlightDetail;

import com.minisheep.searchflight.SearchFlight;
import com.minisheep.searchflight.SearchFlightDetail;
import com.minisheep.searchflight.SearchIATACodeByCNName;
import com.minisheep.util.MysqlUtil;
import com.minisheep.util.ToolsUtil;

/**
 * Created by minisheep on 16/12/28.
 */

public class Chat {
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public String ChatWithBot(String question,String openId){
		Date now = new Date();
		String createTime = this.format.format(now);
		String answer = "";
		answer = Service.chat(openId, createTime, question);  //修改这个函数看能否是答案的唯一出口
		//System.out.println(answer);
		return answer;
	}
	
	
	public String responseFlightIdSearch(String flightname,String req){  //回复根据航班号查询,数据库相关信息都在javabean了，要什么取什么
		SearchFlight search = new SearchFlight();
		List<Flight> flights = new ArrayList<Flight>();
		flights = search.searchFlightname(flightname);
		String finalStr = "";
		String lastupdateTime = "";
		String answer = "";
		for(Flight flight : flights){
			String status = "";
			if(flight.getFlightStatus().equals("ARR")){
				status = "已经达到";
			}else if(flight.getFlightStatus().equals("DEP")){
				status = "已起飞";
			}else{
				status = flight.getFlightStatus();
			}
			finalStr = "航班号:" + flight.getCarrier()  + flight.getFlight() + "\n" + "预计起飞时间:" + flight.getScheduleTime() + "\n" + "实际起飞时间:" + flight.getActualTime() + "\n" +  "航班状态:" + status;
			lastupdateTime = "最后一次更新时间为:" + flight.getLastUpdated();
//			System.out.println(finalStr);
//			System.out.println(lastupdateTime);
//			System.out.println();
			answer += finalStr + "\n" + lastupdateTime + "\n" + "\n";
			String openId = "guest";
			Date now = new Date();
			String createTime = this.format.format(now);
			int chatCategory = 6; //航班查询
			MysqlUtil.saveChatLog(openId, createTime, req, finalStr+lastupdateTime, chatCategory);
		}
		return answer;
	}

	public String responseFlightByCityNameSearch(List<String> cityName,String req){
		List<FlightDetail> flightDetails = new ArrayList<FlightDetail>();
		String dep = "";
		String arr = "";
		String answer = "";
		if(cityName.size() == 1){
			dep = cityName.get(0);
			System.out.println("dep:" + dep + "," + "arr:" + arr);
		}else if(cityName.size() == 2){
			dep = cityName.get(0);
			arr = cityName.get(1);
			System.out.println("dep:" + dep + "," + "arr:" + arr);
		}
		SearchFlightDetail searchFlightDetail = new SearchFlightDetail();
		flightDetails = searchFlightDetail.flightDetail(dep, arr);
		if(flightDetails.size() == 0 && cityName.size() != 0){
			System.out.println("没有此航班的动态信息!");
			//break;
			answer += "没有此航班的动态信息!";
		}
		for(FlightDetail detail : flightDetails){
			String finalstr = "航班号:" + detail.getFlightId() + "\n" + "预计起飞时间:" + detail.getScheduleDepartureTime() + "\n" + "预计到达时间:"
					+ detail.getScheduleArrivalTime() + "\n" + "实际起飞时间:" + detail.getActualDepartureTime() + "\n" +
					"实际到达时间:" + detail.getActualArrivalTime() + "\n" + "最后刷新时间:" + detail.getLastUpdated();
//			System.out.println(finalstr);
//			System.out.println("------------------------------");
			answer += finalstr + "\n" + "------------------------------" + "\n";
		}
		return answer;
	}

	public String getAnswer(String question){
		String openId = "guest";
		String response = "";
		List<String> cityname = new ArrayList<String>();
		String category = "";  //按类别查询不同的数据库

		try {
			category = Service.cutWords(question);  //切词
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("分类为:" + category);
		//System.out.println();
		String[] names = category.split("\\|");  //根据分词结果判断用户是否是要查询航班动态?

		boolean isFlightSearch = false;
		for(int i=0;i<names.length;i++){
			if(names[i].equals("飞") || names[i].equals("飞往") || names[i].equals("航班") || names[i].equals("航班动态") || names[i].equals("航班号")
					|| ToolsUtil.RegexFlightId(names[i]) == true){  //航班查询的关键词
				isFlightSearch = true;
				//System.out.println("这个是航班动态的分类!");
				break;
			}
		}
			/*
			 * 可以设置优先级，先在哪个数据库找结果，没有再下一个数据库里面找，如下先在航班动态的数据库里面找,
			 * 也可以考虑先进去栏目，再搜索就在指定的栏目数据库里面查找
			 */
		Chat chat = new Chat();
		int size = 0;
		if(isFlightSearch == true){  //如果没有结果就继续往下
			String flightIdName = "";
			for(int i=0;i<names.length;i++){
				if(ToolsUtil.RegexFlightId(names[i]) == true){
					flightIdName = ToolsUtil.lowerToupper(names[i]);
					//System.out.println("变成大写后的FlightId:" + flightIdName);
					response = chat.responseFlightIdSearch(flightIdName,question);  //航班号做回答,如果没有数据就继续往下查找别的数据库等
				}
			}
		}
		if(response.equals("")){  //再次查询哪里飞哪里
			for(int i=0;i<names.length;i++){
				SearchIATACodeByCNName searchIATACodeByCNName = new SearchIATACodeByCNName();
				String result = searchIATACodeByCNName.searchIataCodebyCNname(names[i]);  //遍历
				if(!result.equals("")){  //有城市英文简写
					cityname.add(result);
				}
			}
			response = chat.responseFlightByCityNameSearch(cityname,question);
		}
		if(response.equals("") && cityname.size() == 0){   //普通静态的数据库
			response = chat.ChatWithBot(question, openId);
		}
		return response;
	}

	public static void main(String[] args){
		Service.createIndex();
		System.out.println("您好阿,我是智能机器人,请问有什么可以帮您?");
		Chat chat = new Chat();
		Scanner in=new Scanner(System.in);
		String text = "";
		while((text = in.next()) != null) {
			String result = chat.getAnswer(text);
			System.out.println(result);
		}
	}
}
