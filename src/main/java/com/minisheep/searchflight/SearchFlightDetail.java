package com.minisheep.searchflight;

import java.util.ArrayList;
import java.util.List;

import com.minisheep.bean.BaseFlightInfo;
import com.minisheep.util.MysqlUtil;

/**
 * Created by minisheep on 16/12/28.
 */

public class SearchFlightDetail {
	public List<BaseFlightInfo> flightDetail(String depCity, String arrCity){
		List<BaseFlightInfo> baseFlightInfos = new ArrayList<BaseFlightInfo>();
		baseFlightInfos = MysqlUtil.depCityAndarrCity(depCity, arrCity);
		return baseFlightInfos;
	}
}
