package com.minisheep.com.minisheep.SearchFlight;

import java.util.ArrayList;
import java.util.List;

import com.minisheep.Bean.FlightDetail;
import com.minisheep.util.MysqlUtil;

public class SearchFlightDetail {
	public List<FlightDetail> flightDetail(String depCity,String arrCity){
		List<FlightDetail> flightDetails = new ArrayList<FlightDetail>();
		flightDetails = MysqlUtil.depCityAndarrCity(depCity, arrCity);
		return flightDetails;
	}
}
