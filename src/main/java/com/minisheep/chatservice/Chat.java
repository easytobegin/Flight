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
            } else if (questionCategory.equals("登机口")) {   //bug MF896登机口
                for (BaseFlightInfo flight : flights) {
                    String gateActualOpen = "";
                    String gateActualClose = "";
                    String gateEstimateOpen = "";
                    String gateEstimateClose = "";
                    String gateScheduleOpen = "";
                    String gateScheduleClose = "";
                    if (flight.getGateActualOpen() != null) {
                        gateActualOpen = ToolsUtil.getHourAndMin(flight.getGateActualOpen());
                    } else {
                        gateActualOpen = "/";
                    }
                    if (flight.getGateActualClose() != null) {
                        gateActualClose = ToolsUtil.getHourAndMin(flight.getGateActualOpen());
                    } else {
                        gateActualClose = "/";
                    }
                    if (flight.getGateEstimateOpen() != null) {
                        gateEstimateOpen = ToolsUtil.getHourAndMin(flight.getGateEstimateOpen());
                    } else {
                        gateEstimateOpen = "/";
                    }
                    if (flight.getGateEstimateClose() != null) {
                        gateEstimateClose = ToolsUtil.getHourAndMin(flight.getGateEstimateClose());
                    } else {
                        gateEstimateClose = "/";
                    }
                    if (flight.getGateScheduleOpen() != null) {
                        gateScheduleOpen = ToolsUtil.getHourAndMin(flight.getGateActualOpen());
                    } else {
                        gateScheduleOpen = "/";
                    }
                    if (flight.getGateScheduleClose() != null) {
                        gateScheduleClose = ToolsUtil.getHourAndMin(flight.getGateActualOpen());
                    } else {
                        gateScheduleClose = "/";
                    }
                    if (flight.getGateActualOpen() != null) {
                        answer = "航班" + flight.getCarrier() + flight.getFlight() + "，" + "，日期:" + flight.getOpdate().substring(0, 10) + "，当前登机口为:" + flight.getGate() +
                                "，计划登机时间:" + gateEstimateOpen + "~" + gateEstimateClose + "，变更登机时间:" + gateActualOpen + "~" + gateActualClose + "，请及时关注机场登机口和登机时间变化通知。";
                        continue;
                    }
                    if (flight.getGateEstimateOpen() != null) {
                        answer = "航班" + flight.getCarrier() + flight.getFlight() + "，" + "，日期:" + flight.getOpdate().substring(0, 10) + "，当前登机口为:" + flight.getGate() +
                                "，计划登机时间:" + gateEstimateOpen + "~" + gateEstimateClose
                                + "，请及时关注机场登机口和登机时间变化通知。";
                        continue;
                    } else if (flight.getGateScheduleOpen() != null) {
                        answer = "航班" + flight.getCarrier() + flight.getFlight() + "，" + "，日期:" + flight.getOpdate().substring(0, 10) + "，当前登机口为:" + flight.getGate() +
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
                    answer = "航班:" + flight.getCarrier() + flight.getFlight() + "，日期:" + flight.getOpdate().substring(0, 10) + "，状态:" + MysqlUtil.codeTodescription(flight.getFlightStatus());
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
            } else if (questionCategory.equals("开始检票时间")) {
                for (BaseFlightInfo flight : flights) {
                    if (flight.getActualCheckinOpen() != null) {
                        answer = "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班开始办理值机时间为:" +
                                flight.getActualCheckinOpen();
                        flightInfoResult.add(answer);
                    } else if (flight.getScheduleCheckinOpen() != null) {
                        answer = "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班开始办理值机时间为:" +
                                flight.getScheduleCheckinOpen();
                        flightInfoResult.add(answer);
                    } else {
                        answer = "暂无该航班值机开始的时间,请稍后查询!";
                        flightInfoResult.add(answer);
                    }
                }
            } else if (questionCategory.equals("停止检票时间")) {
                for (BaseFlightInfo flight : flights) {
                    if (flight.getActualCheckinClose() != null) {
                        answer = "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班停止办理值机时间为:" +
                                flight.getActualCheckinClose();
                        flightInfoResult.add(answer);
                    } else if (flight.getScheduleCheckinClose() != null) {
                        answer = "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班停止办理值机时间为:" +
                                flight.getScheduleCheckinClose();
                        flightInfoResult.add(answer);
                    } else {
                        answer = "暂无该航班值机结束的时间,请稍后查询!";
                        flightInfoResult.add(answer);
                    }
                }
            } else if (questionCategory.equals("传送带开始")) {
                for (BaseFlightInfo flight : flights) {
                    if (flight.getCarouselActualOpen() != null) {
                        answer = "航班号:" + flight.getCarrier() + flight.getFlight() + "  行李传送带开始时间为:"
                                + flight.getCarouselActualOpen();
                        flightInfoResult.add(answer);
                    } else if (flight.getCarouselScheduleOpen() != null) {
                        answer = "航班号:" + flight.getCarrier() + flight.getFlight() + "  行李传送带开始时间为:"
                                + flight.getCarouselScheduleOpen();
                        flightInfoResult.add(answer);
                    } else {
                        answer = "暂无该航班行李传送带开始的时间";
                        flightInfoResult.add(answer);
                    }
                }
            } else if (questionCategory.equals("传送带结束")) {
                for (BaseFlightInfo flight : flights) {
                    if (flight.getCarouselActualClose() != null) {
                        answer = "航班号:" + flight.getCarrier() + flight.getFlight() + "  行李传送带停止时间为:"
                                + flight.getCarouselActualClose();
                        flightInfoResult.add(answer);
                    } else if (flight.getCarouselScheduleClose() != null) {
                        answer = "航班号:" + flight.getCarrier() + flight.getFlight() + "  行李传送带停止时间为:"
                                + flight.getCarouselScheduleClose();
                        flightInfoResult.add(answer);
                    } else {
                        answer = "暂无该航班行李传送带停止的时间";
                        flightInfoResult.add(answer);
                    }
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
                        state = flight.getFlightStatus();
                    }else{
                        state = "/";
                    }
                    if (flight.getPassby() != null) {
                        passby = MysqlUtil.CNNamebyIataCodeSearch(flight.getPassby());
                    } else {
                        passby = "无";
                    }
                    if(passby.equals("无")){
                        answer = "航班:" + flight.getCarrier() + flight.getFlight() + "，日期:" + flight.getOpdate().substring(0, 10) + "航线:" + MysqlUtil.CNNamebyIataCodeSearch(flight.getOrigin())
                                + "-" + MysqlUtil.CNNamebyIataCodeSearch(flight.getDestination()) + "，计划:" + scheduleTime
                                + "，预计:" + estimateTime + "，实际:" + actualTime + "，当前进展:" + MysqlUtil.codeTodescription(state);
                    }else{
                        answer = "航班:" + flight.getCarrier() + flight.getFlight() + "，日期:" + flight.getOpdate().substring(0, 10) + "航线:" + MysqlUtil.CNNamebyIataCodeSearch(flight.getOrigin()) + "-" + MysqlUtil.CNNamebyIataCodeSearch(flight.getPassby())
                                + "-" + MysqlUtil.CNNamebyIataCodeSearch(flight.getDestination()) + "，计划:" + scheduleTime
                                + "，预计:" + estimateTime + "，实际:" + actualTime + "，当前进展:" + MysqlUtil.codeTodescription(state);
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
            System.out.println("没有此航班的动态信息,只提供与本航站楼(厦门)相关数据的查询!");
            return flightInfoResult;
        }
        boolean AD = false;  //是问进港还是出港

        if (questionCategory.equals("实际起飞") || questionCategory.equals("预计起飞")) {
            for (BaseFlightInfo flight : baseFlightInfos) {   //这里要通过direction判断,是进港还是出港
                if (flight.getActualTime() != null) {
                    if (flight.getDirection().equals("D")) {
                        //System.out.println("航班号:" + flight.getCarrier()  + flight.getFlight() + " 实际起飞时间为:" + flight.getActualTime());
                        answer = "航班号:" + flight.getCarrier() + flight.getFlight() + " 实际起飞时间为:" + flight.getActualTime();
                        flightInfoResult.add(answer);
                    }
                    continue;
                }
                if (flight.getEstimateTime() != null) {
                    if (flight.getDirection().equals("D")) {
                        //System.out.println("航班号:" + flight.getCarrier()  + flight.getFlight() + " 预计到达时间为:" + flight.getEstimateTime());
                        answer = "航班号:" + flight.getCarrier() + flight.getFlight() + " 预计起飞时间为:" + flight.getEstimateTime();
                        flightInfoResult.add(answer);
                    }
                    continue;
                }
                if (flight.getScheduleTime() != null) {
                    if (flight.getDirection().equals("D")) {
                        //System.out.println("航班号:" + flight.getCarrier()  + flight.getFlight() + " 预计起飞时间为:" + flight.getScheduleTime());
                        answer = "航班号:" + flight.getCarrier() + flight.getFlight() + " 预计起飞时间为:" + flight.getScheduleTime();
                        flightInfoResult.add(answer);
                    }
                    continue;
                }
                //没有相关信息,用户查询的是否是错的(比如降落变成起飞,起飞变成降落)

            }
        } else if (questionCategory.equals("实际抵达") || questionCategory.equals("预计抵达")) {
            for (BaseFlightInfo flight : baseFlightInfos) {   //这里要通过direction判断,是进港还是出港
                if (flight.getActualTime() != null) {
                    if (flight.getDirection().equals("A")) {
                        //System.out.println("航班号:" + flight.getCarrier()  + flight.getFlight() + " 预计抵达时间为:" + flight.getScheduleTime());
                        answer = "航班号:" + flight.getCarrier() + flight.getFlight() + " 实际抵达时间为:" + flight.getActualTime();
                        flightInfoResult.add(answer);
                    }
                    continue;
                }
                if (flight.getEstimateTime() != null) {
                    if (flight.getDirection().equals("A")) {
                        //System.out.println("航班号:" + flight.getCarrier()  + flight.getFlight() + " 预计到达时间为:" + flight.getEstimateTime());
                        answer = "航班号:" + flight.getCarrier() + flight.getFlight() + " 预计抵达时间为:" + flight.getEstimateTime();
                        flightInfoResult.add(answer);
                    }
                    continue;
                }
                if (flight.getScheduleTime() != null) {
                    if (flight.getDirection().equals("A")) {
                        //System.out.println("航班号:" + flight.getCarrier()  + flight.getFlight() + " 预计起飞时间为:" + flight.getScheduleTime());
                        answer = "航班号:" + flight.getCarrier() + flight.getFlight() + " 预计抵达时间为:" + flight.getScheduleTime();
                        flightInfoResult.add(answer);
                    }
                    continue;
                }
            }
            //answer += "找不到该航班抵达的相关信息,您是否需要查询的是查询该航班的起飞相关信息?";
        } else if (questionCategory.equals("登机口")) {
            for (BaseFlightInfo flight : baseFlightInfos) {
                answer = "航班号:" + flight.getCarrier() + flight.getFlight() + " 登机口为:" + flight.getGate();
                flightInfoResult.add(answer);
            }
        } else if (questionCategory.equals("候机楼")) {
            for (BaseFlightInfo flight : baseFlightInfos) {
                answer = "航班号:" + flight.getCarrier() + flight.getFlight() + " 您的所查询航班的航站楼为:" + flight.getTerminal() + "航站楼";
                flightInfoResult.add(answer);
            }
        } else if (questionCategory.equals("状态")) {
            for (BaseFlightInfo flight : baseFlightInfos) {
                answer = "航班号:" + flight.getCarrier() + flight.getFlight() + " 您所查询航班的当前状态为:" + MysqlUtil.codeTodescription(flight.getFlightStatus());
                flightInfoResult.add(answer);
            }
        } else if (questionCategory.equals("检票口")) {
            for (BaseFlightInfo flight : baseFlightInfos) {
                String checkinCounter = "";
                if (flight.getCheckinCounter() == null) {
                    checkinCounter = "任意";
                } else {
                    checkinCounter = flight.getCheckinCounter();
                }
                answer = "航班号:" + flight.getCarrier() + flight.getFlight() + " 该航班在 " + checkinCounter + " 值机柜台办理" + "  信息最后更新时间为:" + flight.getLastUpdated();
                flightInfoResult.add(answer);
            }
        } else if (questionCategory.equals("开始检票时间")) {
            for (BaseFlightInfo flight : baseFlightInfos) {
                if (flight.getActualCheckinOpen() != null) {
                    answer = "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班开始办理值机时间为:" +
                            flight.getActualCheckinOpen();
                    flightInfoResult.add(answer);
                } else if (flight.getScheduleCheckinOpen() != null) {
                    answer = "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班开始办理值机时间为:" +
                            flight.getScheduleCheckinOpen();
                    flightInfoResult.add(answer);
                } else {
                    answer = "暂无该航班值机开始的时间,请稍后查询!(只提供从本航站楼(厦门)出发航班的值机时间)";
                    flightInfoResult.add(answer);
                }
            }
        } else if (questionCategory.equals("停止检票时间")) {
            for (BaseFlightInfo flight : baseFlightInfos) {
                if (flight.getActualCheckinClose() != null) {
                    answer = "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班停止办理值机时间为:" +
                            flight.getActualCheckinClose();
                    flightInfoResult.add(answer);
                } else if (flight.getScheduleCheckinClose() != null) {
                    answer = "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班停止办理值机时间为:" +
                            flight.getScheduleCheckinClose();
                    flightInfoResult.add(answer);
                } else {
                    answer = "暂无该航班值机结束的时间,请稍后查询!(只提供从本航站楼(厦门)出发航班的值机时间)";
                    flightInfoResult.add(answer);
                }
            }
        } else if (questionCategory.equals("传送带开始")) {
            for (BaseFlightInfo flight : baseFlightInfos) {
                if (flight.getCarouselActualOpen() != null) {
                    answer = "航班号:" + flight.getCarrier() + flight.getFlight() + "  行李传送带开始时间为:"
                            + flight.getCarouselActualOpen();
                    flightInfoResult.add(answer);
                } else if (flight.getCarouselScheduleOpen() != null) {
                    answer = "航班号:" + flight.getCarrier() + flight.getFlight() + "  行李传送带开始时间为:"
                            + flight.getCarouselScheduleOpen();
                    flightInfoResult.add(answer);
                } else {
                    answer = "暂无该航班行李传送带开始的时间(只提供本航站楼(厦门)行李传送带相关信息)";
                    flightInfoResult.add(answer);
                }
            }
        } else if (questionCategory.equals("传送带结束")) {
            for (BaseFlightInfo flight : baseFlightInfos) {
                if (flight.getCarouselActualClose() != null) {
                    answer = "航班号:" + flight.getCarrier() + flight.getFlight() + "  行李传送带停止时间为:"
                            + flight.getCarouselActualClose();
                    flightInfoResult.add(answer);
                } else if (flight.getCarouselScheduleClose() != null) {
                    answer = "航班号:" + flight.getCarrier() + flight.getFlight() + "  行李传送带停止时间为:"
                            + flight.getCarouselScheduleClose();
                    flightInfoResult.add(answer);
                } else {
                    answer = "暂无该航班行李传送带停止的时间(只提供本航站楼(厦门)行李传送带相关信息)";
                    flightInfoResult.add(answer);
                }
            }
        } else if (questionCategory.equals("登机门打开时间")) {
            for (BaseFlightInfo flight : baseFlightInfos) {
                if (flight.getGateActualOpen() != null) {
                    answer = "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班的登机门打开时间为:"
                            + flight.getGateActualOpen();
                    flightInfoResult.add(answer);
                } else if (flight.getGateEstimateOpen() != null) {
                    answer = "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班的登机门打开时间为:"
                            + flight.getGateEstimateOpen();
                    flightInfoResult.add(answer);
                } else if (flight.getGateScheduleOpen() != null) {
                    answer = "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班的登机门打开时间为:"
                            + flight.getGateScheduleOpen();
                    flightInfoResult.add(answer);
                } else {
                    answer = "暂无该航班登机门打开的时间";
                    flightInfoResult.add(answer);
                }
            }
        } else if (questionCategory.equals("登机门关闭时间")) {
            for (BaseFlightInfo flight : baseFlightInfos) {
                if (flight.getGateActualClose() != null) {
                    answer = "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班的登机门关闭时间为:"
                            + flight.getGateActualClose();
                    flightInfoResult.add(answer);
                } else if (flight.getGateEstimateClose() != null) {
                    answer = "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班的登机门关闭时间为:"
                            + flight.getGateEstimateClose();
                    flightInfoResult.add(answer);
                } else if (flight.getGateScheduleClose() != null) {
                    answer = "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班的登机门关闭时间为:"
                            + flight.getGateScheduleClose();
                    flightInfoResult.add(answer);
                } else {
                    answer = "暂无该航班登机门关闭的时间";
                    flightInfoResult.add(answer);
                }
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
