package com.minisheep.test;

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

        map.put("name", "Dana、Li");
        map.put("age", new Integer(22));
        map.put("Provinces", new String("广东省"));
        map.put("citiy", new String("珠海市"));
        map.put("Master", new String("C、C++、Linux、Java"));
        JSONObject json = new JSONObject(map);

        out.write(json.toString());
        out.flush();
        out.close();
    }
}
