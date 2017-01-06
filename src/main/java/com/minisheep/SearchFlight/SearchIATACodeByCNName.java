package com.minisheep.SearchFlight;

import com.minisheep.util.MysqlUtil;

/**
 * Created by minisheep on 16/12/28.
 */

public class SearchIATACodeByCNName {
	public String searchIataCodebyCNname(String CityName){
		String cityCode = MysqlUtil.IataCodebyCNnameSearch(CityName);
		return cityCode;
	}
}
