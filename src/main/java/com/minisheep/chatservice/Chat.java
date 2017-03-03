package com.minisheep.chatservice;


import com.minisheep.bean.BaseFlightInfo;
import com.minisheep.searchflight.SearchFlight;
import com.minisheep.searchflight.SearchFlightDetail;
import com.minisheep.searchflight.SearchIATACodeByCNName;
import com.minisheep.util.MySearch;
import com.minisheep.util.MysqlUtil;
import com.minisheep.util.SynonymUtil;
import com.minisheep.util.ToolsUtil;
import org.apache.lucene.queryparser.classic.ParseException;
import sun.jvm.hotspot.tools.Tool;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Created by minisheep on 16/12/28.
 */

public class Chat {
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public String ChatWithBot(String question, String openId) {
        Date now = new Date();
        String createTime = this.format.format(now);
        String answer = "";
        answer = Service.chat(openId, createTime, question);  //修改这个函数看能否是答案的唯一出口
        //System.out.println(answer);
        return answer;
    }


    //分词，并返回分词后的结果
    public static String[] cutWordsResult(String question) throws IOException {
        String result = Service.cutWords(question);  //分词结果

        String[] str = result.split("\\|");  //根据分词结果判断用户是否是要查询航班动态?
        return str;
    }


    public static String changeMulToOne(String originRequest) {  //把多种问法通过预处理变成统一的一种格式,再去查询
        String resultQuestion = ""; //最后统一的格式
        try {
            String indexPath = "/Users/minisheep/Documents/testindex";
            String result = SynonymUtil.displayTokens(SynonymUtil.convertSynonym(SynonymUtil.analyzerChinese(originRequest, true)));
            //FSystem.out.println("result:"+ result);
            List<String> docs = MySearch.searchIndex(result, indexPath);
            for (String string : docs) {
                //这个可以获取结果:比如厦门到沈阳的飞机何时起飞(所有跟起飞有关的同义词可归为此类,并且厦门到沈阳可以动态改变,其他不变)
                //System.out.println(string);
                resultQuestion = string;
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //System.out.println("问题处理后的结果为:" + resultQuestion);
        return resultQuestion;
    }

    public static List<BaseFlightInfo> detailFlightInfoByCode(String FlightCode) {  //根据航班号返回Bean所有信息
        SearchFlight search = new SearchFlight();
        List<BaseFlightInfo> flights = new ArrayList<BaseFlightInfo>();
        flights = search.searchFlightname(FlightCode);
        return flights;
    }

    public long changeToTimestamp(String time) {
        try {
            Date date = format.parse(time);
            return date.getTime();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //待完成
    public static List<BaseFlightInfo> detailFlightInfoByFlightId(String FlightId) {  //根据唯一的主键flightId返回所有信息
        return null;
    }

    public static List<BaseFlightInfo> detailFlightInfoByCity(List<String> cityName) {
        List<BaseFlightInfo> baseFlightInfos = new ArrayList<BaseFlightInfo>();

        String dep = "";
        String arr = "";
        String answer = "";
        if (cityName.size() == 1) {
            dep = cityName.get(0);
            //System.out.println("dep:" + dep + "," + "arr:" + arr);
        } else if (cityName.size() >= 2) {
            dep = cityName.get(0);
            arr = cityName.get(1);
            //System.out.println("dep:" + dep + "," + "arr:" + arr);
        }
        SearchFlightDetail searchFlightDetail = new SearchFlightDetail();
        baseFlightInfos = searchFlightDetail.flightDetail(dep, arr);
        return baseFlightInfos;
    }

    /*
        问题归一化后根据航班号数据库查询返回结果，考虑用户点击更多该航班动态的时候显示全部该航班的内容
        给出用户需要的部分信息

        重构 : 返回List数组,BaseFlightInfo
    */
    public static List<String> dealWithFlightCodeQuestion(String FlightCode, String questionCategory) {
        //SearchFlight search = new SearchFlight();
        List<String> flightInfoResult = new ArrayList<String>();
        String answer = "";
        List<BaseFlightInfo> flights = new ArrayList<BaseFlightInfo>();
        List<BaseFlightInfo> temp = new ArrayList<BaseFlightInfo>();
        //flights = search.searchFlightname(FlightCode);
        temp = detailFlightInfoByCode(FlightCode);
        for (BaseFlightInfo flight : temp) {
            if (flight.getOpdate().substring(0, 10).compareTo(ToolsUtil.getSystemDate().substring(0, 10)) >= 0) {
                flights.add(flight);
                //System.out.println("系统时间为:"+ToolsUtil.getSystemDate().substring(0,9));
                //System.out.println("数据库时间为:" + flight.getOpdate().substring(0,9));
            }
        }
        boolean AD = false;  //是问进港还是出港
        if (!FlightCode.equals("") && FlightCode != null) {  //问哪里到哪里的问题

//				//过滤比当前系统时间小的航班
//				if(flight.getScheduleTime().compareTo(ToolsUtil.getSystemDate()) < 0){
//					continue;
//				}
            //for循环修改到内层，更省时
            if (questionCategory.equals("实际起飞") || questionCategory.equals("预计起飞")) {
                for (BaseFlightInfo flight : flights) {   //这里要通过direction判断,是进港还是出港
                    if (flight.getDirection().equals("D")) {
                        String actualTime = "";
                        String estimateTime = "";
                        String scheduleTime = "";
                        if (flight.getActualTime() != null) {
                            actualTime = ToolsUtil.getHourAndMin(flight.getActualTime());
                            if(flight.getEstimateTime() == null){
                                estimateTime = "/";
                            }else{
                                estimateTime = ToolsUtil.getHourAndMin(flight.getEstimateTime());
                            }
                            if(flight.getScheduleTime() == null){
                                estimateTime = "/";
                            }else{
                                scheduleTime = ToolsUtil.getHourAndMin(flight.getScheduleTime());
                            }
                        } else if (flight.getEstimateTime() != null) {
                            actualTime = "/";
                            if(flight.getEstimateTime() == null){
                                estimateTime = "/";
                            }else{
                                estimateTime = ToolsUtil.getHourAndMin(flight.getEstimateTime());
                            }
                            if(flight.getScheduleTime() == null){
                                estimateTime = "/";
                            }else{
                                scheduleTime = ToolsUtil.getHourAndMin(flight.getScheduleTime());
                            }
                        } else if (flight.getScheduleTime() != null) {
                            actualTime = "/";
                            estimateTime = "/";
                            if(flight.getScheduleTime() == null){
                                estimateTime = "/";
                            }else{
                                scheduleTime = ToolsUtil.getHourAndMin(flight.getScheduleTime());
                            }
                        }else{
                            actualTime = "/";
                            estimateTime = "/";
                            scheduleTime = "/";
                        }
                        //System.out.println("航班号:" + flight.getCarrier()  + flight.getFlight() + " 实际起飞时间为:" + flight.getActualTime());
                        answer = "航班" + flight.getCarrier() + flight.getFlight() + "," + "日期:" + flight.getOpdate().substring(0, 10) + "，计划时间:" + scheduleTime + "，预计时间:" + estimateTime + "，实际时间:" + actualTime;
                        flightInfoResult.add(answer);
                    }
                }
                //answer += "找不到该航班起飞的相关信息,您是否需要查询的是查询该航班的抵达相关信息?";
            } else if (questionCategory.equals("实际抵达") || questionCategory.equals("预计抵达")) {
                for (BaseFlightInfo flight : flights) {   //这里要通过direction判断,是进港还是出港
                    if (flight.getDirection().equals("A")) {
                        String actualTime = "";
                        String estimateTime = "";
                        String scheduleTime = "";
                        if (flight.getActualTime() != null) {
                            actualTime = ToolsUtil.getHourAndMin(flight.getActualTime());
                            if(flight.getEstimateTime() == null){
                                estimateTime = "/";
                            }else{
                                estimateTime = ToolsUtil.getHourAndMin(flight.getEstimateTime());
                            }
                            if(flight.getScheduleTime() == null){
                                estimateTime = "/";
                            }else{
                                scheduleTime = ToolsUtil.getHourAndMin(flight.getScheduleTime());
                            }
                        } else if (flight.getEstimateTime() != null) {
                            actualTime = "/";
                            if(flight.getEstimateTime() == null){
                                estimateTime = "/";
                            }else{
                                estimateTime = ToolsUtil.getHourAndMin(flight.getEstimateTime());
                            }
                            if(flight.getScheduleTime() == null){
                                estimateTime = "/";
                            }else{
                                scheduleTime = ToolsUtil.getHourAndMin(flight.getScheduleTime());
                            }
                        } else if (flight.getScheduleTime() != null) {
                            actualTime = "/";
                            estimateTime = "/";
                            if(flight.getScheduleTime() == null){
                                estimateTime = "/";
                            }else{
                                scheduleTime = ToolsUtil.getHourAndMin(flight.getScheduleTime());
                            }
                        }else{
                            actualTime = "/";
                            estimateTime = "/";
                            scheduleTime = "/";
                        }
                        //System.out.println("航班号:" + flight.getCarrier()  + flight.getFlight() + " 实际起飞时间为:" + flight.getActualTime());
                        answer = "航班" + flight.getCarrier() + flight.getFlight() + "，" + "日期:" + flight.getOpdate().substring(0, 10) + "，计划时间:" + scheduleTime + "，预计时间:" + estimateTime + "，实际时间:" + actualTime;
                        flightInfoResult.add(answer);
                    }
                }
            } else if (questionCategory.equals("登机口")) {
                for (BaseFlightInfo flight : flights) {
                    String gateActualOpen = "";
                    String gateActualClose = "";
                    String gateEstimateOpen = "";
                    String gateEstimateClose = "";
                    String gateScheduleOpen = "";
                    String gateScheduleClose = "";
                    if (flight.getGateActualOpen() != null) {
                        gateActualOpen = ToolsUtil.getHourAndMin(flight.getGateActualOpen());
                        System.out.println("gateActualOpen:" + gateActualOpen);
                    } else {
                        gateActualOpen = "/";
                        System.out.println("gateActualOpen:" + "/");
                    }
                    if (flight.getGateActualClose() != null) {
                        gateActualClose = ToolsUtil.getHourAndMin(flight.getGateActualClose());
                        System.out.println("gateActualClose:" + gateActualClose);
                    } else {
                        gateActualClose = "/";
                        System.out.println("gateActualClose:" + "/");
                    }
                    if (flight.getGateEstimateOpen() != null) {
                        gateEstimateOpen = ToolsUtil.getHourAndMin(flight.getGateEstimateOpen());
                        System.out.println("gateEstimateOpen:" + gateEstimateOpen);
                    } else {
                        gateEstimateOpen = "/";
                        System.out.println("gateEstimateOpen:" + "/");
                    }
                    if (flight.getGateEstimateClose() != null) {
                        gateEstimateClose = ToolsUtil.getHourAndMin(flight.getGateEstimateClose());
                        System.out.println("gateEstimateClose:" + gateEstimateClose);
                    } else {
                        gateEstimateClose = "/";
                        System.out.println("gateEstimateClose:" + "/");
                    }
                    if (flight.getGateScheduleOpen() != null) {
                        gateScheduleOpen = ToolsUtil.getHourAndMin(flight.getGateScheduleOpen());
                        System.out.println("gateScheduleOpen:" + gateScheduleOpen);
                    } else {
                        gateScheduleOpen = "/";
                        System.out.println("gateScheduleOpen:" + "/");
                    }
                    if (flight.getGateScheduleClose() != null) {
                        gateScheduleClose = ToolsUtil.getHourAndMin(flight.getGateScheduleClose());
                        System.out.println("gateScheduleClose:" + gateScheduleClose);
                    } else {
                        gateScheduleClose = "/";
                        System.out.println("gateScheduleClose:" + "/");
                    }
                    if (flight.getGateActualOpen() != null) {
                        System.out.println("getGateActualOpen");
                        answer = "航班" + flight.getCarrier() + flight.getFlight() +  "，日期:" + flight.getOpdate().substring(0, 10) + "，当前登机口为:" + flight.getGate() +
                                "，计划登机时间:" + gateEstimateOpen + "~" + gateEstimateClose + "，变更登机时间:" + gateActualOpen + "~" + gateActualClose + "，请及时关注机场登机口和登机时间变化通知。";
                    } else if (flight.getGateEstimateOpen() != null) {
                        System.out.println("getGateEstimateOpen");
                        answer = "航班" + flight.getCarrier() + flight.getFlight() +  "，日期:" + flight.getOpdate().substring(0, 10) + "，当前登机口为:" + flight.getGate() +
                                "，计划登机时间:" + gateEstimateOpen + "~" + gateEstimateClose
                                + "，请及时关注机场登机口和登机时间变化通知。";
                    } else if (flight.getGateScheduleOpen() != null) {
                        System.out.println("getGateScheduleOpen");
                        answer = "航班" + flight.getCarrier() + flight.getFlight() + "，日期:" + flight.getOpdate().substring(0, 10) + "，当前登机口为:" + flight.getGate() +
                                "，计划登机时间:" + gateScheduleOpen + "~" + gateScheduleClose
                                + "，请及时关注机场登机口和登机时间变化通知。";
                    }
                    flightInfoResult.add(answer);
                }
            } else if (questionCategory.equals("候机楼")) {
                for (BaseFlightInfo flight : flights) {
                    answer = "航班:" + flight.getCarrier() + flight.getFlight() + "，日期:" + flight.getOpdate().substring(0, 10) + "，候机楼:" + flight.getTerminal();
                    flightInfoResult.add(answer);
                }
            } else if (questionCategory.equals("状态")) {
                for (BaseFlightInfo flight : flights) {
                    String state = "";
                    if(flight.getFlightStatus() == null){
                        state = "/";
                    }else{
                        state = MysqlUtil.codeTodescription(flight.getFlightStatus());
                    }

                    answer = "航班:" + flight.getCarrier() + flight.getFlight() + "，日期:" + flight.getOpdate().substring(0, 10) + "，状态:" + state;
                    flightInfoResult.add(answer);
                }
            } else if (questionCategory.equals("检票口")) {
                for (BaseFlightInfo flight : flights) {
                    String checkinCounter = "";
                    if (flight.getCheckinCounter() == null) {
                        checkinCounter = "/";
                    } else {
                        checkinCounter = flight.getCheckinCounter();
                    }
                    answer = "航班:" + flight.getCarrier() + flight.getFlight() + "，日期:" + flight.getOpdate().substring(0, 10) + "，值机柜台:" + checkinCounter;
                    flightInfoResult.add(answer);
                }
            } else if (questionCategory.equals("值机时间")) {
                for (BaseFlightInfo flight : flights) {
                    String actualCheckinOpen = "";
                    String scheduleCheckinOpen = "";
                    String actualCheckinClose = "";
                    String scheduleCheckinClose = "";
                    if (flight.getActualCheckinOpen() != null) {
                        actualCheckinOpen = ToolsUtil.getHourAndMin(flight.getActualCheckinOpen());
                    }else{
                        actualCheckinOpen = "/";
                    }
                    if(flight.getActualCheckinClose() != null){
                        actualCheckinClose = ToolsUtil.getHourAndMin(flight.getActualCheckinClose());
                    }else{
                        actualCheckinClose = "/";
                    }
                    if(flight.getScheduleCheckinClose() != null){
                        scheduleCheckinClose = ToolsUtil.getHourAndMin(flight.getScheduleCheckinClose());
                    }else{
                        scheduleCheckinClose = "/";
                    }
                    if(flight.getScheduleCheckinOpen() != null){
                        scheduleCheckinOpen = ToolsUtil.getHourAndMin(flight.getScheduleCheckinOpen());
                    }else{
                        scheduleCheckinOpen = "/";
                    }
                    String checkinCounter = "";
                    if (flight.getCheckinCounter() == null) {
                        checkinCounter = "/";
                    } else {
                        checkinCounter = flight.getCheckinCounter();
                    }
                    if(!actualCheckinOpen.equals("/")){
                        answer = "航班:" + flight.getCarrier() + flight.getFlight() + "，日期:"
                                + flight.getOpdate().substring(0, 10) + "，值机柜台:" + checkinCounter
                                + "，在机场柜台值机时间:" + actualCheckinOpen + "~" + actualCheckinClose;
                    }else{
                        answer = "航班:" + flight.getCarrier() + flight.getFlight() + "，日期:"
                                + flight.getOpdate().substring(0, 10) + "，值机柜台:" + checkinCounter
                                + "，在机场柜台值机时间:" + scheduleCheckinOpen + "~" + scheduleCheckinClose;
                    }

                    flightInfoResult.add(answer);
                }

            }  else if (questionCategory.equals("行李传送带")) {
                for (BaseFlightInfo flight : flights) {
                    String carouselActualOpen = "";
                    String carouselActualClose = "";
                    String carouselScheduleOpen = "";
                    String carouselScheduleClose = "";
                    String carouselCode = "";
                    if (flight.getCarouselActualOpen() != null) {
                        carouselActualOpen = ToolsUtil.getHourAndMin(flight.getCarouselActualOpen());
                    }else{
                        carouselActualOpen = "/";
                    }
                    if(flight.getCarouselActualClose() != null){
                        carouselActualClose = ToolsUtil.getHourAndMin(flight.getCarouselActualClose());
                    }else{
                        carouselActualClose = "/";
                    }
                    if(flight.getCarouselScheduleOpen() != null){
                        carouselScheduleOpen = ToolsUtil.getHourAndMin(flight.getCarouselScheduleOpen());
                    }else{
                        carouselScheduleOpen = "/";
                    }
                    if(flight.getCarouselScheduleClose() != null){
                        carouselScheduleClose = ToolsUtil.getHourAndMin(flight.getCarouselScheduleClose());
                    }else{
                        carouselScheduleClose = "/";
                    }
                    if (flight.getCarouselCode() == null) {
                        carouselCode = "/";
                    } else {
                        carouselCode = flight.getCarouselCode();
                    }
                    if(!carouselActualOpen.equals("/")){
                        answer = "航班:" + flight.getCarrier() + flight.getFlight() + "，日期:"
                                + flight.getOpdate().substring(0, 10) + "，转盘号:" + carouselCode
                                + "，提取行李时间:" + carouselActualOpen + "~" + carouselActualClose;
                    }else{
                        answer = "航班:" + flight.getCarrier() + flight.getFlight() + "，日期:"
                                + flight.getOpdate().substring(0, 10) + "，转盘号:" + carouselCode
                                + "，提取行李时间:" + carouselScheduleOpen + "~" + carouselScheduleClose;
                    }
                    flightInfoResult.add(answer);
                }
            } else if (questionCategory.equals("异常状态")) {
                for (BaseFlightInfo flight : flights) {
                    if (flight.getIrregularCode() != null) {
                        answer = "航班:" + flight.getCarrier() + flight.getFlight() + "，该航班异常原因为:" + MysqlUtil.irregularCode(flight.getIrregularCode());
                        flightInfoResult.add(answer);
                    }
                }
                if (answer.equals("")) {
                    answer = "该航班无异常原因!";
                    flightInfoResult.add(answer);
                }
            } else {  //默认
                for (BaseFlightInfo flight : flights) {  //全部信息给出?
                    String passby;
                    String scheduleTime = "";
                    String estimateTime = "";
                    String actualTime = "";
                    String state = "";
                    if(flight.getActualTime() != null){
                        actualTime = ToolsUtil.getHourAndMin(flight.getActualTime());
                    }else{
                        actualTime = "/";
                    }
                    if(flight.getEstimateTime() != null){
                        estimateTime = ToolsUtil.getHourAndMin(flight.getEstimateTime());
                    }else{
                        estimateTime = "/";
                    }
                    if(flight.getScheduleTime() != null){
                        scheduleTime = ToolsUtil.getHourAndMin(flight.getScheduleTime());
                    }else{
                        scheduleTime = "/";
                    }
                    if(flight.getFlightStatus() != null){
                        state = MysqlUtil.codeTodescription(flight.getFlightStatus());
                    }else{
                        state = "/";
                    }
                    if (flight.getPassby() != null) {
                        passby = MysqlUtil.CNNamebyIataCodeSearch(flight.getPassby());
                    } else {
                        passby = "无";
                    }
                    if(passby.equals("无")){
                        answer = "航班:" + flight.getCarrier() + flight.getFlight() + "，日期:" + flight.getOpdate().substring(0, 10) + "，航线:" + MysqlUtil.CNNamebyIataCodeSearch(flight.getOrigin())
                                + "-" + MysqlUtil.CNNamebyIataCodeSearch(flight.getDestination()) + "，计划:" + scheduleTime
                                + "，预计:" + estimateTime + "，实际:" + actualTime + "，当前进展:" + state;
                    }else{
                        answer = "航班:" + flight.getCarrier() + flight.getFlight() + "，日期:" + flight.getOpdate().substring(0, 10) + "，航线:" + MysqlUtil.CNNamebyIataCodeSearch(flight.getOrigin()) + "-" + MysqlUtil.CNNamebyIataCodeSearch(flight.getPassby())
                                + "-" + MysqlUtil.CNNamebyIataCodeSearch(flight.getDestination()) + "，计划:" + scheduleTime
                                + "，预计:" + estimateTime + "，实际:" + actualTime + "，当前进展:" + state;
                    }
                    flightInfoResult.add(answer);
                }
            }
        }
        System.out.println("----------------------------------------");

        return flightInfoResult;
    }

    /*
        问题归一化后根据出发城市和到达城市数据库查询返回结果
        给出部分用户需要的信息

        重构的时候返回一个BaseFlightInfo的List数组
     */
    public static List<String> delWithFlightCityQuestion(List<String> cityName, String questionCategory) {
        List<BaseFlightInfo> baseFlightInfos = new ArrayList<BaseFlightInfo>();

        List<BaseFlightInfo> temp = new ArrayList<BaseFlightInfo>();
        List<String> flightInfoResult = new ArrayList<String>();
        String answer = "";

        temp = detailFlightInfoByCity(cityName);
        for (BaseFlightInfo flight : temp) {
            if (flight.getOpdate().substring(0, 10).compareTo(ToolsUtil.getSystemDate().substring(0, 10)) >= 0) {
                baseFlightInfos.add(flight);
                //System.out.println("系统时间为:"+ToolsUtil.getSystemDate().substring(0,9));
                //System.out.println("数据库时间为:" + flight.getOpdate().substring(0,9));
            }
        }

        if (baseFlightInfos.size() == 0 && cityName.size() != 0) {
            System.out.println("没有此航班的动态信息!");
            //break;
            answer = "没有此航班的动态信息,只提供与本航站楼(厦门)相关数据的查询!";
            flightInfoResult.add(answer);
            return flightInfoResult;
        }
        boolean AD = false;  //是问进港还是出港

        if (questionCategory.equals("实际起飞") || questionCategory.equals("预计起飞")) {
            for (BaseFlightInfo flight : baseFlightInfos) {   //这里要通过direction判断,是进港还是出港
                if (flight.getDirection().equals("D")) {
                    String actualTime = "";
                    String estimateTime = "";
                    String scheduleTime = "";
                    if (flight.getActualTime() != null) {
                        actualTime = ToolsUtil.getHourAndMin(flight.getActualTime());
                        if(flight.getEstimateTime() == null){
                            estimateTime = "/";
                        }else{
                            estimateTime = ToolsUtil.getHourAndMin(flight.getEstimateTime());
                        }
                        if(flight.getScheduleTime() == null){
                            estimateTime = "/";
                        }else{
                            scheduleTime = ToolsUtil.getHourAndMin(flight.getScheduleTime());
                        }
                    } else if (flight.getEstimateTime() != null) {
                        actualTime = "/";
                        if(flight.getEstimateTime() == null){
                            estimateTime = "/";
                        }else{
                            estimateTime = ToolsUtil.getHourAndMin(flight.getEstimateTime());
                        }
                        if(flight.getScheduleTime() == null){
                            estimateTime = "/";
                        }else{
                            scheduleTime = ToolsUtil.getHourAndMin(flight.getScheduleTime());
                        }
                    } else if (flight.getScheduleTime() != null) {
                        actualTime = "/";
                        estimateTime = "/";
                        if(flight.getScheduleTime() == null){
                            estimateTime = "/";
                        }else{
                            scheduleTime = ToolsUtil.getHourAndMin(flight.getScheduleTime());
                        }
                    }else{
                        actualTime = "/";
                        estimateTime = "/";
                        scheduleTime = "/";
                    }
                    //System.out.println("航班号:" + flight.getCarrier()  + flight.getFlight() + " 实际起飞时间为:" + flight.getActualTime());
                    answer = "航班" + flight.getCarrier() + flight.getFlight() + "," + "日期:" + flight.getOpdate().substring(0, 10) + "，计划时间:" + scheduleTime + "，预计时间:" + estimateTime + "，实际时间:" + actualTime;
                    flightInfoResult.add(answer);
                }
            }
            //answer += "找不到该航班起飞的相关信息,您是否需要查询的是查询该航班的抵达相关信息?";
        } else if (questionCategory.equals("实际抵达") || questionCategory.equals("预计抵达")) {
            for (BaseFlightInfo flight : baseFlightInfos) {   //这里要通过direction判断,是进港还是出港
                if (flight.getDirection().equals("A")) {
                    String actualTime = "";
                    String estimateTime = "";
                    String scheduleTime = "";
                    if (flight.getActualTime() != null) {
                        actualTime = ToolsUtil.getHourAndMin(flight.getActualTime());
                        if(flight.getEstimateTime() == null){
                            estimateTime = "/";
                        }else{
                            estimateTime = ToolsUtil.getHourAndMin(flight.getEstimateTime());
                        }
                        if(flight.getScheduleTime() == null){
                            estimateTime = "/";
                        }else{
                            scheduleTime = ToolsUtil.getHourAndMin(flight.getScheduleTime());
                        }
                    } else if (flight.getEstimateTime() != null) {
                        actualTime = "/";
                        if(flight.getEstimateTime() == null){
                            estimateTime = "/";
                        }else{
                            estimateTime = ToolsUtil.getHourAndMin(flight.getEstimateTime());
                        }
                        if(flight.getScheduleTime() == null){
                            estimateTime = "/";
                        }else{
                            scheduleTime = ToolsUtil.getHourAndMin(flight.getScheduleTime());
                        }
                    } else if (flight.getScheduleTime() != null) {
                        actualTime = "/";
                        estimateTime = "/";
                        if(flight.getScheduleTime() == null){
                            estimateTime = "/";
                        }else{
                            scheduleTime = ToolsUtil.getHourAndMin(flight.getScheduleTime());
                        }
                    }else{
                        actualTime = "/";
                        estimateTime = "/";
                        scheduleTime = "/";
                    }
                    //System.out.println("航班号:" + flight.getCarrier()  + flight.getFlight() + " 实际起飞时间为:" + flight.getActualTime());
                    answer = "航班" + flight.getCarrier() + flight.getFlight() + "，" + "日期:" + flight.getOpdate().substring(0, 10) + "，计划时间:" + scheduleTime + "，预计时间:" + estimateTime + "，实际时间:" + actualTime;
                    flightInfoResult.add(answer);
                }
            }
        } else if (questionCategory.equals("登机口")) {
            for (BaseFlightInfo flight : baseFlightInfos) {
                String gateActualOpen = "";
                String gateActualClose = "";
                String gateEstimateOpen = "";
                String gateEstimateClose = "";
                String gateScheduleOpen = "";
                String gateScheduleClose = "";
                if (flight.getGateActualOpen() != null) {
                    gateActualOpen = ToolsUtil.getHourAndMin(flight.getGateActualOpen());
                    System.out.println("gateActualOpen:" + gateActualOpen);
                } else {
                    gateActualOpen = "/";
                    System.out.println("gateActualOpen:" + "/");
                }
                if (flight.getGateActualClose() != null) {
                    gateActualClose = ToolsUtil.getHourAndMin(flight.getGateActualClose());
                    System.out.println("gateActualClose:" + gateActualClose);
                } else {
                    gateActualClose = "/";
                    System.out.println("gateActualClose:" + "/");
                }
                if (flight.getGateEstimateOpen() != null) {
                    gateEstimateOpen = ToolsUtil.getHourAndMin(flight.getGateEstimateOpen());
                    System.out.println("gateEstimateOpen:" + gateEstimateOpen);
                } else {
                    gateEstimateOpen = "/";
                    System.out.println("gateEstimateOpen:" + "/");
                }
                if (flight.getGateEstimateClose() != null) {
                    gateEstimateClose = ToolsUtil.getHourAndMin(flight.getGateEstimateClose());
                    System.out.println("gateEstimateClose:" + gateEstimateClose);
                } else {
                    gateEstimateClose = "/";
                    System.out.println("gateEstimateClose:" + "/");
                }
                if (flight.getGateScheduleOpen() != null) {
                    gateScheduleOpen = ToolsUtil.getHourAndMin(flight.getGateScheduleOpen());
                    System.out.println("gateScheduleOpen:" + gateScheduleOpen);
                } else {
                    gateScheduleOpen = "/";
                    System.out.println("gateScheduleOpen:" + "/");
                }
                if (flight.getGateScheduleClose() != null) {
                    gateScheduleClose = ToolsUtil.getHourAndMin(flight.getGateScheduleClose());
                    System.out.println("gateScheduleClose:" + gateScheduleClose);
                } else {
                    gateScheduleClose = "/";
                    System.out.println("gateScheduleClose:" + "/");
                }
                if (flight.getGateActualOpen() != null) {
                    System.out.println("getGateActualOpen");
                    answer = "航班" + flight.getCarrier() + flight.getFlight() +  "，日期:" + flight.getOpdate().substring(0, 10) + "，当前登机口为:" + flight.getGate() +
                            "，计划登机时间:" + gateEstimateOpen + "~" + gateEstimateClose + "，变更登机时间:" + gateActualOpen + "~" + gateActualClose + "，请及时关注机场登机口和登机时间变化通知。";
                } else if (flight.getGateEstimateOpen() != null) {
                    System.out.println("getGateEstimateOpen");
                    answer = "航班" + flight.getCarrier() + flight.getFlight() +  "，日期:" + flight.getOpdate().substring(0, 10) + "，当前登机口为:" + flight.getGate() +
                            "，计划登机时间:" + gateEstimateOpen + "~" + gateEstimateClose
                            + "，请及时关注机场登机口和登机时间变化通知。";
                } else if (flight.getGateScheduleOpen() != null) {
                    System.out.println("getGateScheduleOpen");
                    answer = "航班" + flight.getCarrier() + flight.getFlight() + "，日期:" + flight.getOpdate().substring(0, 10) + "，当前登机口为:" + flight.getGate() +
                            "，计划登机时间:" + gateScheduleOpen + "~" + gateScheduleClose
                            + "，请及时关注机场登机口和登机时间变化通知。";
                }
                flightInfoResult.add(answer);
            }
        } else if (questionCategory.equals("候机楼")) {
            for (BaseFlightInfo flight : baseFlightInfos) {
                answer = "航班:" + flight.getCarrier() + flight.getFlight() + "，日期:" + flight.getOpdate().substring(0, 10) + "，候机楼:" + flight.getTerminal();
                flightInfoResult.add(answer);
            }
        } else if (questionCategory.equals("状态")) {
            for (BaseFlightInfo flight : baseFlightInfos) {
                String state = "";
                if(flight.getFlightStatus() == null){
                    state = "/";
                }else{
                    state = MysqlUtil.codeTodescription(flight.getFlightStatus());
                }
                answer = "航班:" + flight.getCarrier() + flight.getFlight() + "，日期:" + flight.getOpdate().substring(0, 10) + "，状态:" + state;
                flightInfoResult.add(answer);
            }
        } else if (questionCategory.equals("检票口")) {
            for (BaseFlightInfo flight : baseFlightInfos) {
                String checkinCounter = "";
                if (flight.getCheckinCounter() == null) {
                    checkinCounter = "/";
                } else {
                    checkinCounter = flight.getCheckinCounter();
                }
                answer = "航班:" + flight.getCarrier() + flight.getFlight() + "，日期:" + flight.getOpdate().substring(0, 10) + "，值机柜台:" + checkinCounter;
                flightInfoResult.add(answer);
            }
        } else if (questionCategory.equals("值机时间")) {
            for (BaseFlightInfo flight : baseFlightInfos) {
                String actualCheckinOpen = "";
                String scheduleCheckinOpen = "";
                String actualCheckinClose = "";
                String scheduleCheckinClose = "";
                if (flight.getActualCheckinOpen() != null) {
                    actualCheckinOpen = ToolsUtil.getHourAndMin(flight.getActualCheckinOpen());
                }else{
                    actualCheckinOpen = "/";
                }
                if(flight.getActualCheckinClose() != null){
                    actualCheckinClose = ToolsUtil.getHourAndMin(flight.getActualCheckinClose());
                }else{
                    actualCheckinClose = "/";
                }
                if(flight.getScheduleCheckinClose() != null){
                    scheduleCheckinClose = ToolsUtil.getHourAndMin(flight.getScheduleCheckinClose());
                }else{
                    scheduleCheckinClose = "/";
                }
                if(flight.getScheduleCheckinOpen() != null){
                    scheduleCheckinOpen = ToolsUtil.getHourAndMin(flight.getScheduleCheckinOpen());
                }else{
                    scheduleCheckinOpen = "/";
                }
                String checkinCounter = "";
                if (flight.getCheckinCounter() == null) {
                    checkinCounter = "/";
                } else {
                    checkinCounter = flight.getCheckinCounter();
                }
                if(!actualCheckinOpen.equals("/")){
                    answer = "航班:" + flight.getCarrier() + flight.getFlight() + "，日期:"
                            + flight.getOpdate().substring(0, 10) + "，值机柜台:" + checkinCounter
                            + "，在机场柜台值机时间:" + actualCheckinOpen + "~" + actualCheckinClose;
                }else{
                    answer = "航班:" + flight.getCarrier() + flight.getFlight() + "，日期:"
                            + flight.getOpdate().substring(0, 10) + "，值机柜台:" + checkinCounter
                            + "，在机场柜台值机时间:" + scheduleCheckinOpen + "~" + scheduleCheckinClose;
                }

                flightInfoResult.add(answer);
            }

        }  else if (questionCategory.equals("行李传送带")) {
            for (BaseFlightInfo flight : baseFlightInfos) {
                String carouselActualOpen = "";
                String carouselActualClose = "";
                String carouselScheduleOpen = "";
                String carouselScheduleClose = "";
                String carouselCode = "";
                if (flight.getCarouselActualOpen() != null) {
                    carouselActualOpen = ToolsUtil.getHourAndMin(flight.getCarouselActualOpen());
                }else{
                    carouselActualOpen = "/";
                }
                if(flight.getCarouselActualClose() != null){
                    carouselActualClose = ToolsUtil.getHourAndMin(flight.getCarouselActualClose());
                }else{
                    carouselActualClose = "/";
                }
                if(flight.getCarouselScheduleOpen() != null){
                    carouselScheduleOpen = ToolsUtil.getHourAndMin(flight.getCarouselScheduleOpen());
                }else{
                    carouselScheduleOpen = "/";
                }
                if(flight.getCarouselScheduleClose() != null){
                    carouselScheduleClose = ToolsUtil.getHourAndMin(flight.getCarouselScheduleClose());
                }else{
                    carouselScheduleClose = "/";
                }
                if (flight.getCarouselCode() == null) {
                    carouselCode = "/";
                } else {
                    carouselCode = flight.getCarouselCode();
                }
                if(!carouselActualOpen.equals("/")){
                    answer = "航班:" + flight.getCarrier() + flight.getFlight() + "，日期:"
                            + flight.getOpdate().substring(0, 10) + "，转盘号:" + carouselCode
                            + "，提取行李时间:" + carouselActualOpen + "~" + carouselActualClose;
                }else{
                    answer = "航班:" + flight.getCarrier() + flight.getFlight() + "，日期:"
                            + flight.getOpdate().substring(0, 10) + "，转盘号:" + carouselCode
                            + "，提取行李时间:" + carouselScheduleOpen + "~" + carouselScheduleClose;
                }
                flightInfoResult.add(answer);
            }
        } else if (questionCategory.equals("异常状态")) {
            for (BaseFlightInfo flight : baseFlightInfos) {
                if (flight.getIrregularCode() != null) {
                    answer = "航班:" + flight.getCarrier() + flight.getFlight() + "，该航班异常原因为:" + MysqlUtil.irregularCode(flight.getIrregularCode());
                    flightInfoResult.add(answer);
                }
            }
            if (answer.equals("")) {
                answer = "该航班无异常原因!";
                flightInfoResult.add(answer);
            }
        } else {  //默认
            for (BaseFlightInfo flight : baseFlightInfos) {  //全部信息给出?
                String passby;
                String scheduleTime = "";
                String estimateTime = "";
                String actualTime = "";
                String state = "";
                if(flight.getActualTime() != null){
                    actualTime = ToolsUtil.getHourAndMin(flight.getActualTime());
                }else{
                    actualTime = "/";
                }
                if(flight.getEstimateTime() != null){
                    estimateTime = ToolsUtil.getHourAndMin(flight.getEstimateTime());
                }else{
                    estimateTime = "/";
                }
                if(flight.getScheduleTime() != null){
                    scheduleTime = ToolsUtil.getHourAndMin(flight.getScheduleTime());
                }else{
                    scheduleTime = "/";
                }
                if(flight.getFlightStatus() != null){
                    state = MysqlUtil.codeTodescription(flight.getFlightStatus());
                }else{
                    state = "/";
                }
                if (flight.getPassby() != null) {
                    passby = MysqlUtil.CNNamebyIataCodeSearch(flight.getPassby());
                } else {
                    passby = "无";
                }

                /*
                判断航班状态
                 */
                String status = "";
                if(flight.getFlightStatus() == null){
                    status = "";
                }else{
                    status = flight.getFlightStatus();
                }
                //航班状态为ARR,DEP,CNL的时候直接过滤
                if(status.equals("ARR") || status.equals("DEP") || status.equals("CNL")){
                    continue;
                }
                if(passby.equals("无")){
                    answer = "航班:" + flight.getCarrier() + flight.getFlight() + "，日期:" + flight.getOpdate().substring(0, 10) + "，航线:" + MysqlUtil.CNNamebyIataCodeSearch(flight.getOrigin())
                            + "-" + MysqlUtil.CNNamebyIataCodeSearch(flight.getDestination()) + "，计划:" + scheduleTime
                            + "，预计:" + estimateTime + "，实际:" + actualTime + "，当前进展:" + state;
                }else{
                    answer = "航班:" + flight.getCarrier() + flight.getFlight() + "，日期:" + flight.getOpdate().substring(0, 10) + "，航线:" + MysqlUtil.CNNamebyIataCodeSearch(flight.getOrigin()) + "-" + MysqlUtil.CNNamebyIataCodeSearch(flight.getPassby())
                            + "-" + MysqlUtil.CNNamebyIataCodeSearch(flight.getDestination()) + "，计划:" + scheduleTime
                            + "，预计:" + estimateTime + "，实际:" + actualTime + "，当前进展:" + state;
                }
                flightInfoResult.add(answer);
            }
        }
        System.out.println("------------------------------------------------");
        return flightInfoResult;
    }

    public static String getFlightId(String question) throws IOException {  //问题返回FlightId
        String[] names = cutWordsResult(question);
        String flightIdName = "";
        for (int i = 0; i < names.length; i++) {
            if (ToolsUtil.RegexFlightId(names[i]) == true) {
                flightIdName = ToolsUtil.lowerToupper(names[i]);
                break;
            }
        }
        return flightIdName;
    }

    public static List<String> getDoubleCity(String question) throws IOException {
        List<String> cityname = new ArrayList<String>();
        String[] names = cutWordsResult(question);
        for (int i = 0; i < names.length; i++) {
            SearchIATACodeByCNName searchIATACodeByCNName = new SearchIATACodeByCNName();
            String result = searchIATACodeByCNName.searchIataCodebyCNname(names[i]);  //遍历
            if (!result.equals("")) {  //有城市英文简写
                cityname.add(result);
            }
        }
        return cityname;
    }

    public List<String> getAnswer(String question) throws IOException {
        List<String> answer = new ArrayList<String>();
        String afterDeal = changeMulToOne(question);  //同义词搜索,归一
        String openId = "guest";
        String response = "";
        List<String> cityname = new ArrayList<String>();
        String category = "";  //按类别查询不同的数据库

        String[] names = cutWordsResult(question);

        boolean isFlightSearch = false;
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals("飞") || names[i].equals("飞往") || names[i].equals("航班") || names[i].equals("航班动态") || names[i].equals("航班号")
                    || ToolsUtil.RegexFlightId(names[i]) == true) {  //航班查询的关键词
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
        String save = "";
        int size = 0;
        if (isFlightSearch == true) {  //如果没有结果就继续往下
            String flightIdName = "";
            for (int i = 0; i < names.length; i++) {
                if (ToolsUtil.RegexFlightId(names[i]) == true) {
                    flightIdName = ToolsUtil.lowerToupper(names[i]);
//                    if(flightIdName.length() < 4 && !afterDeal.equals("航空公司"))
//                        continue;
                    answer = dealWithFlightCodeQuestion(flightIdName, afterDeal);

                    //System.out.println("变成大写后的FlightId:" + flightIdName);
                    for (int j = 0; j < answer.size(); j++) {
                        save += answer.get(j) + " ";
                    }
                    MysqlUtil.saveChatLog("guest", ToolsUtil.getSystemDate(), question, save, 1);
                    //这里显示的是全部数据
                    //response = chat.responseFlightIdSearch(flightIdName,question);  //航班号做回答,如果没有数据就继续往下查找别的数据库等
                    return answer;
                }
            }
        }
        if (response.equals("")) {  //再次查询哪里飞哪里
            for (int i = 0; i < names.length; i++) {
                SearchIATACodeByCNName searchIATACodeByCNName = new SearchIATACodeByCNName();
                String result = searchIATACodeByCNName.searchIataCodebyCNname(names[i]);  //遍历
                if (!result.equals("")) {  //有城市英文简写
                    cityname.add(result);
                }
            }
            answer = delWithFlightCityQuestion(cityname, afterDeal);
            for (int j = 0; j < answer.size(); j++) {
                save += answer.get(j) + " ";
            }
            MysqlUtil.saveChatLog("guest", ToolsUtil.getSystemDate(), question, save, 2);
            //这里显示的是全部数据
            //response = chat.responseFlightByCityNameSearch(cityname,question);
        }
        if (answer.size() == 0) {   //普通静态的数据库v
            response = chat.ChatWithBot(question, openId);
            answer.add(response);
            for (int j = 0; j < answer.size(); j++) {
                save += answer.get(j) + " ";
            }
            MysqlUtil.saveChatLog("guest", ToolsUtil.getSystemDate(), question, save, 4);
        }
        return answer;
    }


    public static void main(String[] args) {
        //Service.createIndex();  //建立索引,建立一次就够了
        System.out.println("您好阿,我是智能机器人,请问有什么可以帮您?");
        Chat chat = new Chat();
        Scanner in = new Scanner(System.in);
        String text = "";
        while ((text = in.next()) != null) {
            List<String> result = new ArrayList<String>();
            try {
                result = chat.getAnswer(text);
                for (String res : result) {
                    System.out.println(res);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(result);
        }
    }
}
