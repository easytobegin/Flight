package com.minisheep.searchflight;

import java.util.ArrayList;
import java.util.List;

import com.minisheep.bean.BaseFlightInfo;
import com.minisheep.util.MysqlUtil;

/**
 * Created by minisheep on 16/12/28.
 */

public class SearchFlight {
	public List<BaseFlightInfo> searchFlightname(String flightname){  //比如SC4770
		int length = flightname.length();
		String carrier = flightname.substring(0,2);  //SC
		String flight = flightname.substring(2,length);  //4770

		//select SCHEDULETIME,ESTIMATETIME from Flight where CARRIER = "MF" and FLIGHT = "892" and DIRECTION = "A" and OPDATE = "日期";
		List<BaseFlightInfo> flights = new ArrayList<BaseFlightInfo>();
		flights = MysqlUtil.flightSearch(carrier, flight);
		return flights;
	}
	
}
