package com.minisheep.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ToolsUtil {
	public static String getSystemDate(){
		String systemdate = "";
		SimpleDateFormat myFmt1=new SimpleDateFormat("yy/MM/dd"); 
		Date now=new Date();
		//System.out.println(myFmt1.format(now));
		systemdate = myFmt1.format(now);
		return systemdate;
	}
	
	public static boolean RegexFlightId(String str){  //例如MFxxx,判断这是否是个航班号
		//[a-zA-Z\d]+
		Pattern pattern = Pattern.compile("[a-zA-Z0-9]+");
		Matcher matcher = pattern.matcher(str);
		boolean b= matcher.matches();
		return b;
	}
	
	public static String lowerToupper(String str){
		String str2;
		StringBuffer str1=new StringBuffer();
		for(int j=0;j<str.length();j++){
			str2=str.substring(j,j+1);
			if(str2.toLowerCase().equals(str2)){
				//如果是小写，则转换成大写
				str2=str2.toUpperCase();
				str1.append(str2);
			}
		}
		return str1.toString();
	}
}
