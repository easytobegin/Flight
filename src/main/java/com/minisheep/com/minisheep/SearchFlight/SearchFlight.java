package com.minisheep.com.minisheep.SearchFlight;

import java.util.ArrayList;
import java.util.List;

import com.minisheep.Bean.Flight;
import com.minisheep.Bean.FlightDetail;
import com.minisheep.util.MysqlUtil;

public class SearchFlight {
	public List<Flight> searchFlightname(String flightname){  //比如SC4770
		int length = flightname.length();
		String carrier = flightname.substring(0,2);  //SC
		String flight = flightname.substring(2,length);  //4770
	
		//select SCHEDULETIME,ESTIMATETIME from Flight where CARRIER = "MF" and FLIGHT = "892" and DIRECTION = "A" and OPDATE = "日期";
		List<Flight> flights = new ArrayList<Flight>();
		flights = MysqlUtil.flightSearch(carrier, flight);
		return flights;
	}
	
}
