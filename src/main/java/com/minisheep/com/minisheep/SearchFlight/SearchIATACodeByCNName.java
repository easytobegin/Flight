package com.minisheep.com.minisheep.SearchFlight;

import com.minisheep.util.MysqlUtil;

public class SearchIATACodeByCNName {
	public String searchIataCodebyCNname(String CityName){
		String cityCode = MysqlUtil.IataCodebyCNnameSearch(CityName);
		return cityCode;
	}
}
