package com.minisheep.test;

import com.minisheep.bean.BaseFlightInfo;
import com.minisheep.bean.QAresponse;
import com.minisheep.chatservice.Chat;
import com.minisheep.chatservice.Service;
import com.minisheep.util.MysqlUtil;
import org.json.JSONStringer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by minisheep on 17/1/6.
 */
public class ServerTest extends HttpServlet {
    public ServerTest() {
        super();
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request,response);
    }


    //根据获得的List列表,进行JSON的返回
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException {
        //response.setContentType("text/html;charset=utf-8");
        request.setCharacterEncoding("utf-8");
        //response.setCharacterEncoding("utf-8");
        //客户端 HttpUtils并没有写request方法是post ,但服务器端可自动识别
        String method = request.getMethod();
        System.out.println("request method:" + method);

        //PrintWriter out = response.getWriter();
        String question = request.getParameter("question");

        Chat chat = new Chat();
        //out.print(question);
        String flightCode = Chat.getFlightId(question);  //先获取航班号
        List<String> city = Chat.getDoubleCity(question); //获取两个城市
        List<BaseFlightInfo> flights = new ArrayList<BaseFlightInfo>();
        List<String> flightId = new ArrayList<String>();
        List<Integer> direction = new ArrayList<Integer>();
        if(!flightCode.equals("")){ //如果有航班号
            flights = Chat.detailFlightInfoByCode(flightCode);
            for(BaseFlightInfo flight : flights){
                flightId.add(flight.getCarrier() + flight.getFlight());
                if(flight.getDirection().equals("A")){
                    direction.add(1);  //进港
                }else{
                    direction.add(2);  //出港
                }
            }
        }else if(city.size() != 0){  //没有航班号，有城市
            flights = Chat.detailFlightInfoByCity(city);
            if(flights.size() != 0){
                for(BaseFlightInfo flight : flights){
                    flightId.add(flight.getCarrier() + flight.getFlight());
                    if(flight.getDirection().equals("A")){
                        direction.add(1);  //进港
                    }else{
                        direction.add(2);  //出港
                    }
                }
            }
        }

        List<String> result = chat.getAnswer(question);

        JSONStringer stringer = new JSONStringer();
        int cnt = 0;
        try{
            if(!flightCode.equals("")){   //获取航班号
                if(result.size() != 0){
                    stringer.array();
                    for(String res : result)
                    {
                        stringer.object().key("id").value(flightId.get(cnt))
                                .key("question").value(question)
                                .key("answer").value(res)
                                .key("direction").value(direction.get(cnt++))
                                .endObject();
                    }
                    stringer.endArray();
                }else{
                    String defaultAnswer = Service.getDefaultAnswer();
                    System.out.println("defaultAnswer:"+defaultAnswer);
                    stringer.array();
                    stringer.object().key("id").value(0)
                            .key("question").value(question)
                            .key("answer").value("您输入的航班号有误,请重新输入!")
                            .endObject();
                    stringer.endArray();
                }
            }else if(city.size() != 0 && flights.size() != 0){
                if(result.size() != 0){
                    stringer.array();
                    for(String res : result)
                    {
                        stringer.object().key("id").value(cnt)
                                .key("question").value(question)
                                .key("answer").value(res)
                                .key("direction").value(direction.get(cnt++))
                                .endObject();
                    }
                    stringer.endArray();
                }else{
                    String defaultAnswer = Service.getDefaultAnswer();
                    stringer.array();
                    stringer.object().key("id").value(0)
                            .key("question").value(question)
                            .key("answer").value(defaultAnswer)
                            .endObject();
                    stringer.endArray();
                }
            }else{
                stringer.array();
                for(String res : result)
                {
                    stringer.object().key("id").value(cnt++)
                            .key("question").value(question)
                            .key("answer").value(res)
                            .endObject();
                }
                stringer.endArray();
            }
        }catch (Exception e){}
        response.getOutputStream().write(stringer.toString().getBytes("UTF-8"));
        response.setContentType("text/json; charset=UTF-8");  //JSON的类型为text/json

        //out.print(result);
        //out.flush();
        //out.close();
    }

    public void init() throws ServletException{

    }
}
