package com.minisheep.searchflight;

import java.util.ArrayList;
import java.util.List;

import com.minisheep.bean.FlightDetail;
import com.minisheep.util.MysqlUtil;

/**
 * Created by minisheep on 16/12/28.
 */

public class SearchFlightDetail {
	public List<FlightDetail> flightDetail(String depCity,String arrCity){
		List<FlightDetail> flightDetails = new ArrayList<FlightDetail>();
		flightDetails = MysqlUtil.depCityAndarrCity(depCity, arrCity);
		return flightDetails;
	}
}
