package com.minisheep.test;

import com.minisheep.chatService.Chat;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by minisheep on 17/1/6.
 */
public class JsonServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        this.doPost(request, response);
    }
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");      //解决中文乱码问题

        PrintWriter out = response.getWriter();
        Map map = new HashMap();

        /*
        键值对参数列表
         */
        Map<String,String[]> params = request.getParameterMap();
        String queryString = "";
        for(String key : params.keySet()){
            String[] values = params.get(key);
            for(int i=0;i<values.length;i++){
                String value = values[i];
                map.put(key,value);
            }
        }

        JSONObject json = new JSONObject(map);
        //out.println("GET " + request.getRequestURL() + " " +queryString);

        //out.println("GET " + request.getRequestURL() + " " + request.getQueryString());
        out.write(json.toString());
        out.flush();
        out.close();
    }
}
