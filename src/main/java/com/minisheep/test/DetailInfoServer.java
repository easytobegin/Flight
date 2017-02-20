package com.minisheep.test;

import com.minisheep.bean.BaseFlightInfo;
import com.minisheep.bean.QAresponse;
import com.minisheep.chatservice.Chat;
import com.minisheep.chatservice.Service;
import org.json.JSONStringer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by minisheep on 17/1/6.
 */

//根据航班号查询的详细的JSON列表
public class DetailInfoServer extends HttpServlet {
    public DetailInfoServer() {
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
        request.setCharacterEncoding("utf-8");
        //客户端 HttpUtils并没有写request方法是post ,但服务器端可自动识别
        String method = request.getMethod();
        System.out.println("request method:" + method);

        //PrintWriter out = response.getWriter();
        String question = request.getParameter("detailbycode");  //这里传递的是航班号

        Chat chat = new Chat();
        //out.print(question);
        String flightCode = Chat.getFlightId(question);  //先获取航班号
        List<BaseFlightInfo> result = Chat.detailFlightInfoByCode(flightCode);

        JSONStringer stringer = new JSONStringer();
        int cnt = 1;
        try{
            stringer.array();
            for(BaseFlightInfo res : result)
            {
                stringer.object().key("id").value(cnt++)
                        .key("flightcode").value(res.getCarrier() + res.getFlight())
                        .key("terminal").value(res.getTerminal())
                        .key("flightstatus").value(res.getFlightStatus()).
                        endObject();
            }
            stringer.endArray();
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
