package com.minisheep.chatservice;


import com.minisheep.bean.BaseFlightInfo;
import com.minisheep.bean.QAresponse;
import com.minisheep.searchflight.SearchFlight;
import com.minisheep.searchflight.SearchFlightDetail;
import com.minisheep.searchflight.SearchIATACodeByCNName;
import com.minisheep.util.MySearch;
import com.minisheep.util.MysqlUtil;
import com.minisheep.util.SynonymUtil;
import com.minisheep.util.ToolsUtil;
import com.sun.xml.internal.rngom.parse.host.Base;
import org.apache.lucene.queryparser.classic.ParseException;

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
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public String ChatWithBot(String question, String openId) {
        Date now = new Date();
        String createTime = this.format.format(now);
        String answer = "";
        answer = Service.chat(openId, createTime, question);  //修改这个函数看能否是答案的唯一出口
        //System.out.println(answer);
        return answer;
    }


    //分词，并返回分词后的结果
    public String[] cutWordsResult(String question) throws IOException {
        String result = Service.cutWords(question);  //分词结果

        String[] str = result.split("\\|");  //根据分词结果判断用户是否是要查询航班动态?
        return str;
    }


    public int FlightCategoryCode(String[] cutWordResult) throws IOException {  //根据问题回复航班分类，推荐相应答案
        for (int i = 0; i < cutWordResult.length; i++) {

        }
        return 0;
    }

    public String responseFlightIdSearch(String flightname, String req) {  //回复根据航班号查询,数据库相关信息都在javabean了，要什么取什么
        SearchFlight search = new SearchFlight();
        List<BaseFlightInfo> flights = new ArrayList<BaseFlightInfo>();
        flights = search.searchFlightname(flightname);
        String finalStr = "";
        String lastupdateTime = "";
        String answer = "";
        for (BaseFlightInfo flight : flights) {
            String status = "";
            //System.out.println("代号:" + flight.getFlightStatus());
            //System.out.println("状态为:" + MysqlUtil.codeTodescription(flight.getFlightStatus()));
            status = MysqlUtil.codeTodescription(flight.getFlightStatus());

            String passby = "";
            passby = MysqlUtil.CNNamebyIataCodeSearch(flight.getPassby());
            if (passby.equals("")) {
                passby = "无";
            }
            if (status.equals("")) {
                status = "无";
            }
            if (status.equals("到达")) {
                finalStr = "机型编号:" + flight.getCarrier() + flight.getFlight() + "\n" + "始发:" + MysqlUtil.CNNamebyIataCodeSearch(flight.getOrigin()) + ",终点:" + MysqlUtil.CNNamebyIataCodeSearch(flight.getDestination()) + ",经停:" + passby + "\n" + "计划到达时间:" + flight.getScheduleTime() + "\n" + "预计到达时间:" + flight.getEstimateTime() + "\n" + "实际到达时间:" + flight.getActualTime() + "\n" + "航班状态:" + status;
                lastupdateTime = "最后一次更新时间为:" + flight.getLastUpdated();
            } else if (status.equals("起飞")) {
                finalStr = "机型编号:" + flight.getCarrier() + flight.getFlight() + "\n" + flight.getFlight() + "\n" + "始发:" + MysqlUtil.CNNamebyIataCodeSearch(flight.getOrigin()) + ",终点:" + MysqlUtil.CNNamebyIataCodeSearch(flight.getDestination()) + ",经停:" + passby + "\n" + "计划起飞时间:" + flight.getScheduleTime() + "\n" + "预计起飞时间:" + flight.getEstimateTime() + "\n" + "实际起飞时间:" + flight.getActualTime() + "\n" + "航班状态:" + status;
                lastupdateTime = "最后一次更新时间为:" + flight.getLastUpdated();
            } else {  //不属于以上情况
                finalStr = "机型编号:" + flight.getCarrier() + flight.getFlight() + "\n" + flight.getFlight() + "\n" + "始发:" + MysqlUtil.CNNamebyIataCodeSearch(flight.getOrigin()) + ",终点:" + MysqlUtil.CNNamebyIataCodeSearch(flight.getDestination()) + ",经停:" + passby + "\n" + "计划时间:" + flight.getScheduleTime() + "\n" + "航班状态:" + status;
                lastupdateTime = "最后一次更新时间为:" + flight.getLastUpdated();
            }
//			System.out.println(finalStr);
//			System.out.println(lastupdateTime);
//			System.out.println();
            answer += finalStr + "\n" + lastupdateTime + "\n" + "\n";
            String openId = "guest";
            Date now = new Date();
            String createTime = this.format.format(now);
            int chatCategory = 6; //航班查询
            MysqlUtil.saveChatLog(openId, createTime, req, answer, chatCategory);
        }
        return answer;
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


    /*
        问题归一化后根据航班号数据库查询返回结果，考虑用户点击更多该航班动态的时候显示全部该航班的内容
        给出用户需要的部分信息

        重构 : 返回List数组,BaseFlightInfo
    */
    public String dealWithFlightCodeQuestion(String FlightCode, String questionCategory) {
        SearchFlight search = new SearchFlight();
        String answer = "";
        List<BaseFlightInfo> flights = new ArrayList<BaseFlightInfo>();
        flights = search.searchFlightname(FlightCode);
        boolean AD = false;  //是问进港还是出港
        if (!FlightCode.equals("") && FlightCode != null) {  //问哪里到哪里的问题

//				//过滤比当前系统时间小的航班
//				if(flight.getScheduleTime().compareTo(ToolsUtil.getSystemDate()) < 0){
//					continue;
//				}

            //for循环修改到内层，更省时
            if (questionCategory.equals("实际起飞") || questionCategory.equals("预计起飞")) {
                for (BaseFlightInfo flight : flights) {   //这里要通过direction判断,是进港还是出港
                    if (flight.getActualTime() != null) {
                        if (flight.getDirection().equals("D")) {
                            //System.out.println("航班号:" + flight.getCarrier()  + flight.getFlight() + " 实际起飞时间为:" + flight.getActualTime());
                            answer += "航班号:" + flight.getCarrier() + flight.getFlight() + " 实际起飞时间为:" + flight.getActualTime() + "\n";
                        }
                        continue;
                    }
                    if (flight.getEstimateTime() != null) {
                        if (flight.getDirection().equals("D")) {
                            //System.out.println("航班号:" + flight.getCarrier()  + flight.getFlight() + " 预计到达时间为:" + flight.getEstimateTime());
                            answer += "航班号:" + flight.getCarrier() + flight.getFlight() + " 预计起飞时间为:" + flight.getEstimateTime() + "\n";
                        }
                        continue;
                    }
                    if (flight.getScheduleTime() != null) {
                        if (flight.getDirection().equals("D")) {
                            //System.out.println("航班号:" + flight.getCarrier()  + flight.getFlight() + " 预计起飞时间为:" + flight.getScheduleTime());
                            answer += "航班号:" + flight.getCarrier() + flight.getFlight() + " 预计起飞时间为:" + flight.getScheduleTime() + "\n";
                        }
                        continue;
                    }else{
                        answer += "暂无该航班的起飞信息";
                    }
                }
                //answer += "找不到该航班起飞的相关信息,您是否需要查询的是查询该航班的抵达相关信息?";
            } else if (questionCategory.equals("实际抵达") || questionCategory.equals("预计抵达")) {
                for (BaseFlightInfo flight : flights) {   //这里要通过direction判断,是进港还是出港
                    if (flight.getActualTime() != null) {
                        if (flight.getDirection().equals("A")) {
                            //System.out.println("航班号:" + flight.getCarrier()  + flight.getFlight() + " 预计抵达时间为:" + flight.getScheduleTime());
                            answer += "航班号:" + flight.getCarrier() + flight.getFlight() + " 预计抵达时间为:" + flight.getActualTime() + "\n";
                        }
                        continue;
                    }
                    if (flight.getEstimateTime() != null) {
                        if (flight.getDirection().equals("A")) {
                            //System.out.println("航班号:" + flight.getCarrier()  + flight.getFlight() + " 预计到达时间为:" + flight.getEstimateTime());
                            answer += "航班号:" + flight.getCarrier() + flight.getFlight() + " 预计抵达时间为:" + flight.getEstimateTime() + "\n";
                        }
                        continue;
                    }
                    if (flight.getScheduleTime() != null) {
                        if (flight.getDirection().equals("A")) {
                            //System.out.println("航班号:" + flight.getCarrier()  + flight.getFlight() + " 预计起飞时间为:" + flight.getScheduleTime());
                            answer += "航班号:" + flight.getCarrier() + flight.getFlight() + " 预计抵达时间为:" + flight.getScheduleTime() + "\n";
                        }
                        continue;
                    }else{
                        answer += "暂无该航班的抵达信息";
                    }
                }
                //answer += "找不到该航班抵达的相关信息,您是否需要查询的是查询该航班的起飞相关信息?";
            }else if(questionCategory.equals("登机口")){
                for (BaseFlightInfo flight : flights) {
                    answer += "航班号:" + flight.getCarrier() + flight.getFlight() + " 登机口为:" + flight.getGate() + "  信息最后更新时间为:" + flight.getLastUpdated()  + "\n";
                }
            }else if(questionCategory.equals("候机楼")){
                for (BaseFlightInfo flight : flights) {
                    answer += "航班号:" + flight.getCarrier() + flight.getFlight() + " 您的所查询航班的航站楼为:" + flight.getTerminal() + "航站楼" + "  信息最后更新时间为:" + flight.getLastUpdated() + "\n";
                }
            }else if(questionCategory.equals("状态")){
                for(BaseFlightInfo flight : flights){
                    answer += "航班号:" + flight.getCarrier() + flight.getFlight() + " 您所查询航班的当前状态为:" + MysqlUtil.codeTodescription(flight.getFlightStatus()) + "  信息最后更新时间为:" + flight.getLastUpdated() + "\n";
                }
            }else if(questionCategory.equals("检票口")){
                for(BaseFlightInfo flight : flights){
                    String checkinCounter = "";
                    if(flight.getCheckinCounter() == null){
                        checkinCounter = "任意";
                    }else{
                        checkinCounter = flight.getCheckinCounter();
                    }
                    answer += "航班号:" + flight.getCarrier() + flight.getFlight() + " 该航班在 " + checkinCounter + " 值机柜台检票" + "  信息最后更新时间为:" + flight.getLastUpdated() + "\n";
                }
            }else if(questionCategory.equals("开始检票时间")){
                for(BaseFlightInfo flight : flights){
                    if(flight.getActualCheckinOpen() != null){
                        answer += "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班开始检票时间为:" +
                                flight.getActualCheckinOpen() + "\n";
                    }else if(flight.getScheduleCheckinOpen() != null){
                        answer += "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班开始检票时间为:" +
                                flight.getScheduleCheckinOpen() + "\n";
                    }else{
                        answer += "还没有该航班检票开始的时间,请稍后查询!" + "\n";
                    }
                }
            }else if(questionCategory.equals("停止检票时间")){
                for(BaseFlightInfo flight : flights){
                    if(flight.getActualCheckinClose() != null){
                        answer += "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班停止检票时间为:" +
                                flight.getActualCheckinClose() + "\n";
                    }else if(flight.getScheduleCheckinClose() != null){
                        answer += "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班停止检票时间为:" +
                                flight.getScheduleCheckinClose() + "\n";
                    }else{
                        answer += "还没有该航班检票停止的时间,请稍后查询!" + "\n";
                    }
                }
            }else if (questionCategory.equals("传送带开始")) {
                for(BaseFlightInfo flight : flights){
                    if(flight.getCarouselActualOpen() != null){
                        answer += "航班号:" + flight.getCarrier() + flight.getFlight() + "  行李传送带开始时间为:"
                                + flight.getCarouselActualOpen() + "\n";
                    }else if(flight.getCarouselScheduleOpen() != null){
                        answer += "航班号:" + flight.getCarrier() + flight.getFlight() + "  行李传送带开始时间为:"
                                + flight.getCarouselScheduleOpen() + "\n";
                    }else{
                        answer += "暂无该航班行李传送带开始的时间" + "\n";
                    }
                }
            }else if(questionCategory.equals("传送带结束")){
                for(BaseFlightInfo flight : flights){
                    if(flight.getCarouselActualClose() != null){
                        answer += "航班号:" + flight.getCarrier() + flight.getFlight() + "  行李传送带停止时间为:"
                                + flight.getCarouselActualClose() + "\n";
                    }else if(flight.getCarouselScheduleClose() != null){
                        answer += "航班号:" + flight.getCarrier() + flight.getFlight() + "  行李传送带停止时间为:"
                                + flight.getCarouselScheduleClose() + "\n";
                    }else{
                        answer += "暂无该航班行李传送带停止的时间" + "\n";
                    }
                }
            }else if(questionCategory.equals("登机门打开时间")){
                for(BaseFlightInfo flight : flights){
                    if(flight.getGateActualOpen() != null){
                        answer += "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班的登机门打开时间为:"
                                + flight.getGateActualOpen() + "\n";
                    }else if(flight.getGateEstimateOpen() != null){
                        answer += "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班的登机门打开时间为:"
                                + flight.getGateEstimateOpen() + "\n";
                    }else if(flight.getGateScheduleOpen() != null){
                        answer += "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班的登机门打开时间为:"
                                + flight.getGateScheduleOpen() + "\n";
                    }else{
                        answer += "暂无该航班登机门打开的时间" + "\n";
                    }
                }
            }else if(questionCategory.equals("登机门关闭时间")){
                for(BaseFlightInfo flight : flights){
                    if(flight.getGateActualClose() != null){
                        answer += "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班的登机门关闭时间为:"
                                + flight.getGateActualClose() + "\n";
                    }else if(flight.getGateEstimateClose() != null){
                        answer += "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班的登机门关闭时间为:"
                                + flight.getGateEstimateClose() + "\n";
                    }else if(flight.getGateScheduleClose() != null){
                        answer += "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班的登机门关闭时间为:"
                                + flight.getGateScheduleClose() + "\n";
                    }else{
                        answer += "暂无该航班登机门关闭的时间" + "\n";
                    }
                }
            }
            else if(questionCategory.equals("航班任务")){
                for(BaseFlightInfo flight : flights){
                    answer += "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班目前的航班任务状态为:"
                            + MysqlUtil.flightTaskCodeChangeCN(flight.getFlightTask()) + "\n";
                }
            }else if(questionCategory.equals("航空公司")){  //根据航班号可以查询航空公司
                String companyCode = "";
                companyCode += FlightCode.charAt(0);
                companyCode += FlightCode.charAt(1);
                if(MysqlUtil.flightCompany(companyCode) != "")
                    answer += "该航班所属的航空公司为:" + MysqlUtil.flightCompany(companyCode) + "\n";
                else
                    answer += "您输入的航空公司英文代号有误,没有相关信息!" + "\n";
            }else if(questionCategory.equals("异常状态")){
                for(BaseFlightInfo flight : flights){
                    if(flight.getIrregularCode() != null){
                        answer += "该航班异常原因为:" + MysqlUtil.irregularCode(flight.getIrregularCode()) + "\n";
                    }
                }
                if(answer.equals("")){
                    answer += "该航班无异常原因!";
                }
            }else{
                for(BaseFlightInfo flight : flights){  //全部信息给出?
                    String passby;
                    if(flight.getPassby() != null){
                        passby = MysqlUtil.CNNamebyIataCodeSearch(flight.getPassby());
                    }else{
                        passby = "无";
                    }
                    answer += "航班号:" + flight.getCarrier() + flight.getFlight() + " 该航班是从 " + MysqlUtil.CNNamebyIataCodeSearch(flight.getOrigin())
                            + " 飞往 " + MysqlUtil.CNNamebyIataCodeSearch(flight.getDestination()) + " 经停 " + passby + "\n";
                }
            }
        }
        System.out.println("----------------------------------------");
        return answer;
    }

    /*
        问题归一化后根据出发城市和到达城市数据库查询返回结果
        给出部分用户需要的信息

        重构的时候返回一个BaseFlightInfo的List数组
     */
    public static String delWithFlightCityQuestion(List<String> cityName, String questionCategory) {
        List<BaseFlightInfo> baseFlightInfos = new ArrayList<BaseFlightInfo>();
        String dep = "";
        String arr = "";
        String answer = "";
        if (cityName.size() == 1) {
            dep = cityName.get(0);
            //System.out.println("dep:" + dep + "," + "arr:" + arr);
        } else if (cityName.size() == 2) {
            dep = cityName.get(0);
            arr = cityName.get(1);
            //System.out.println("dep:" + dep + "," + "arr:" + arr);
        } else if (cityName.size() > 2) {  //大于两个城市名取前两个
            dep = cityName.get(0);
            arr = cityName.get(1);
        }
        SearchFlightDetail searchFlightDetail = new SearchFlightDetail();
        baseFlightInfos = searchFlightDetail.flightDetail(dep, arr);
        if (baseFlightInfos.size() == 0 && cityName.size() != 0) {
            System.out.println("没有此航班的动态信息!");
            //break;
            answer += "没有此航班的动态信息!";
            System.out.println("没有此航班的动态信息!");
        }
        boolean AD = false;  //是问进港还是出港

        if (questionCategory.equals("实际起飞") || questionCategory.equals("预计起飞")) {
            for (BaseFlightInfo flight : baseFlightInfos) {   //这里要通过direction判断,是进港还是出港
                if (flight.getActualTime() != null) {
                    if (flight.getDirection().equals("D")) {
                        //System.out.println("航班号:" + flight.getCarrier()  + flight.getFlight() + " 实际起飞时间为:" + flight.getActualTime());
                        answer += "航班号:" + flight.getCarrier() + flight.getFlight() + " 实际起飞时间为:" + flight.getActualTime() + "\n";
                    }
                    continue;
                }
                if (flight.getEstimateTime() != null) {
                    if (flight.getDirection().equals("D")) {
                        //System.out.println("航班号:" + flight.getCarrier()  + flight.getFlight() + " 预计到达时间为:" + flight.getEstimateTime());
                        answer += "航班号:" + flight.getCarrier() + flight.getFlight() + " 预计起飞时间为:" + flight.getEstimateTime() + "\n";
                    }
                    continue;
                }
                if (flight.getScheduleTime() != null) {
                    if (flight.getDirection().equals("D")) {
                        //System.out.println("航班号:" + flight.getCarrier()  + flight.getFlight() + " 预计起飞时间为:" + flight.getScheduleTime());
                        answer += "航班号:" + flight.getCarrier() + flight.getFlight() + " 预计起飞时间为:" + flight.getScheduleTime() + "\n";
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
                        answer += "航班号:" + flight.getCarrier() + flight.getFlight() + " 预计抵达时间为:" + flight.getActualTime() + "\n";
                    }
                    continue;
                }
                if (flight.getEstimateTime() != null) {
                    if (flight.getDirection().equals("A")) {
                        //System.out.println("航班号:" + flight.getCarrier()  + flight.getFlight() + " 预计到达时间为:" + flight.getEstimateTime());
                        answer += "航班号:" + flight.getCarrier() + flight.getFlight() + " 预计抵达时间为:" + flight.getEstimateTime() + "\n";
                    }
                    continue;
                }
                if (flight.getScheduleTime() != null) {
                    if (flight.getDirection().equals("A")) {
                        //System.out.println("航班号:" + flight.getCarrier()  + flight.getFlight() + " 预计起飞时间为:" + flight.getScheduleTime());
                        answer += "航班号:" + flight.getCarrier() + flight.getFlight() + " 预计抵达时间为:" + flight.getScheduleTime() + "\n";
                    }
                    continue;
                }
            }
            //answer += "找不到该航班抵达的相关信息,您是否需要查询的是查询该航班的起飞相关信息?";
        }else if(questionCategory.equals("登机口")){
            for (BaseFlightInfo flight : baseFlightInfos) {
                answer += "航班号:" + flight.getCarrier() + flight.getFlight() + " 登机口为:" + flight.getGate() + "  信息最后更新时间为:" + flight.getLastUpdated()  + "\n";
            }
        }else if(questionCategory.equals("候机楼")){
            for (BaseFlightInfo flight : baseFlightInfos) {
                answer += "航班号:" + flight.getCarrier() + flight.getFlight() + " 您的所查询航班的航站楼为:" + flight.getTerminal() + "航站楼" + "  信息最后更新时间为:" + flight.getLastUpdated() + "\n";
            }
        }else if(questionCategory.equals("状态")){
            for(BaseFlightInfo flight : baseFlightInfos){
                answer += "航班号:" + flight.getCarrier() + flight.getFlight() + " 您所查询航班的当前状态为:" + MysqlUtil.codeTodescription(flight.getFlightStatus()) + "  信息最后更新时间为:" + flight.getLastUpdated() + "\n";
            }
        }else if(questionCategory.equals("检票口")){
            for(BaseFlightInfo flight : baseFlightInfos){
                String checkinCounter = "";
                if(flight.getCheckinCounter() == null){
                    checkinCounter = "任意";
                }else{
                    checkinCounter = flight.getCheckinCounter();
                }
                answer += "航班号:" + flight.getCarrier() + flight.getFlight() + " 该航班在 " + checkinCounter + " 值机柜台检票" + "  信息最后更新时间为:" + flight.getLastUpdated() + "\n";
            }
        }else if(questionCategory.equals("开始检票时间")){
            for(BaseFlightInfo flight : baseFlightInfos){
                if(flight.getActualCheckinOpen() != null){
                    answer += "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班开始检票时间为:" +
                            flight.getActualCheckinOpen() + "\n";
                }else if(flight.getScheduleCheckinOpen() != null){
                    answer += "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班开始检票时间为:" +
                            flight.getScheduleCheckinOpen() + "\n";
                }else{
                    answer += "还没有该航班检票开始的时间,请稍后查询!" + "\n";
                }
            }
        }else if(questionCategory.equals("停止检票时间")){
            for(BaseFlightInfo flight : baseFlightInfos){
                if(flight.getActualCheckinClose() != null){
                    answer += "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班停止检票时间为:" +
                            flight.getActualCheckinClose() + "\n";
                }else if(flight.getScheduleCheckinClose() != null){
                    answer += "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班停止检票时间为:" +
                            flight.getScheduleCheckinClose() + "\n";
                }else{
                    answer += "还没有该航班检票停止的时间,请稍后查询!" + "\n";
                }
            }
        }else if (questionCategory.equals("传送带开始")) {
            for(BaseFlightInfo flight : baseFlightInfos){
                if(flight.getCarouselActualOpen() != null){
                    answer += "航班号:" + flight.getCarrier() + flight.getFlight() + "  行李传送带开始时间为:"
                            + flight.getCarouselActualOpen() + "\n";
                }else if(flight.getCarouselScheduleOpen() != null){
                    answer += "航班号:" + flight.getCarrier() + flight.getFlight() + "  行李传送带开始时间为:"
                            + flight.getCarouselScheduleOpen() + "\n";
                }else{
                    answer += "暂无该航班行李传送带开始的时间" + "\n";
                }
            }
        }else if(questionCategory.equals("传送带结束")){
            for(BaseFlightInfo flight : baseFlightInfos){
                if(flight.getCarouselActualClose() != null){
                    answer += "航班号:" + flight.getCarrier() + flight.getFlight() + "  行李传送带停止时间为:"
                            + flight.getCarouselActualClose() + "\n";
                }else if(flight.getCarouselScheduleClose() != null){
                    answer += "航班号:" + flight.getCarrier() + flight.getFlight() + "  行李传送带停止时间为:"
                            + flight.getCarouselScheduleClose() + "\n";
                }else{
                    answer += "暂无该航班行李传送带停止的时间" + "\n";
                }
            }
        }else if(questionCategory.equals("登机门打开时间")){
            for(BaseFlightInfo flight : baseFlightInfos){
                if(flight.getGateActualOpen() != null){
                    answer += "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班的登机门打开时间为:"
                            + flight.getGateActualOpen() + "\n";
                }else if(flight.getGateEstimateOpen() != null){
                    answer += "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班的登机门打开时间为:"
                            + flight.getGateEstimateOpen() + "\n";
                }else if(flight.getGateScheduleOpen() != null){
                    answer += "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班的登机门打开时间为:"
                            + flight.getGateScheduleOpen() + "\n";
                }else{
                    answer += "暂无该航班登机门打开的时间" + "\n";
                }
            }
        }else if(questionCategory.equals("登机门关闭时间")){
            for(BaseFlightInfo flight : baseFlightInfos){
                if(flight.getGateActualClose() != null){
                    answer += "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班的登机门关闭时间为:"
                            + flight.getGateActualClose() + "\n";
                }else if(flight.getGateEstimateClose() != null){
                    answer += "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班的登机门关闭时间为:"
                            + flight.getGateEstimateClose() + "\n";
                }else if(flight.getGateScheduleClose() != null){
                    answer += "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班的登机门关闭时间为:"
                            + flight.getGateScheduleClose() + "\n";
                }else{
                    answer += "暂无该航班登机门关闭的时间" + "\n";
                }
            }
        }else if(questionCategory.equals("航班任务")){
            for(BaseFlightInfo flight : baseFlightInfos){
                answer += "航班号:" + flight.getCarrier() + flight.getFlight() + "  该航班目前的航班任务状态为:"
                        + MysqlUtil.flightTaskCodeChangeCN(flight.getFlightTask()) + "\n";
            }
        }
        System.out.println("------------------------------------------------");
        return answer;
    }

    /*
        给出全部信息
     */
    public String responseFlightByCityNameSearch(List<String> cityName, String req) {
        List<BaseFlightInfo> baseFlightInfos = new ArrayList<BaseFlightInfo>();
        String dep = "";
        String arr = "";
        String answer = "";
        if (cityName.size() == 1) {
            dep = cityName.get(0);
            System.out.println("dep:" + dep + "," + "arr:" + arr);
        } else if (cityName.size() == 2) {
            dep = cityName.get(0);
            arr = cityName.get(1);
            System.out.println("dep:" + dep + "," + "arr:" + arr);
        } else if (cityName.size() > 2) {
            dep = cityName.get(0);
            arr = cityName.get(1);
        }
        SearchFlightDetail searchFlightDetail = new SearchFlightDetail();
        baseFlightInfos = searchFlightDetail.flightDetail(dep, arr);
        if (baseFlightInfos.size() == 0 && cityName.size() != 0) {
            System.out.println("没有此航班的动态信息!");
            //break;
            answer += "没有此航班的动态信息!";
        }
        for (BaseFlightInfo detail : baseFlightInfos) {

            String passby = "";
            passby = MysqlUtil.CNNamebyIataCodeSearch(detail.getPassby());
            if (passby.equals("")) {
                passby = "无";
            }

            String status = "";
            status = MysqlUtil.codeTodescription(detail.getFlightStatus());


            String finalstr = "航班号:" + detail.getCarrier() + detail.getFlight() + "\n" +
                    "始发:" + detail.getOrigin() + ",终点:" + detail.getDestination() + ",经停:" + passby + "\n"
                    + "预计起飞时间:" + detail.getScheduleTime() + "\n" + "评估起飞时间:" + detail.getEstimateTime()
                    + "\n" + "实际起飞时间:" + detail.getActualTime() + "\n" + "飞机状态:" + status +
                    "\n" + "最后刷新时间:" + detail.getLastUpdated();
//			System.out.println(finalstr);
//			System.out.println("------------------------------");
            answer += finalstr + "\n" + "------------------------------" + "\n";
        }
        String openId = "guest";
        Date now = new Date();
        String createTime = this.format.format(now);
        int chatCategory = 6; //航班查询
        MysqlUtil.saveChatLog(openId, createTime, req, answer, chatCategory);
        return answer;
    }

    public String getAnswer(String question) throws IOException {
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
        int size = 0;
        if (isFlightSearch == true) {  //如果没有结果就继续往下
            String flightIdName = "";
            for (int i = 0; i < names.length; i++) {
                if (ToolsUtil.RegexFlightId(names[i]) == true) {
                    flightIdName = ToolsUtil.lowerToupper(names[i]);
                    response = dealWithFlightCodeQuestion(flightIdName, afterDeal);
                    //System.out.println("变成大写后的FlightId:" + flightIdName);

                    //这里显示的是全部数据
                    //response = chat.responseFlightIdSearch(flightIdName,question);  //航班号做回答,如果没有数据就继续往下查找别的数据库等
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
            response = delWithFlightCityQuestion(cityname, afterDeal);

            //这里显示的是全部数据
            //response = chat.responseFlightByCityNameSearch(cityname,question);
        }
        if (response.equals("") && cityname.size() == 0) {   //普通静态的数据库v
            if (afterDeal.equals("进出港航班数量")){
                int flightArr = MysqlUtil.depAndarrCount("A");
                int flightDep = MysqlUtil.depAndarrCount("D");
                response = "当前进港航班数量为:" + flightArr + "," + "出港航班数量为:" + flightDep + "\n";
            }else {
                response = chat.ChatWithBot(question, openId);
            }
        }
        return response;
    }

    public static void main(String[] args) {
        //Service.createIndex();  //建立索引,建立一次就够了
        System.out.println("您好阿,我是智能机器人,请问有什么可以帮您?");
        Chat chat = new Chat();
        Scanner in = new Scanner(System.in);
        String text = "";
        while ((text = in.next()) != null) {
            String result = "";
            try {
                result = chat.getAnswer(text);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(result);
        }
    }
}
