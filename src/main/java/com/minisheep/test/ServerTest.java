package com.minisheep.test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

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

    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        //客户端 HttpUtils并没有写request方法是post ,但服务器端可自动识别
        String method = request.getMethod();
        System.out.println("request method:" + method);

        PrintWriter out = response.getWriter();
        String question = request.getParameter("question");

        if(question.equals("厦门到北京的航班信息")){  //问题
            out.print("test success!");
        }else{
            out.print("test fail!");
        }
        out.flush();
        out.close();
    }

    public void init() throws ServletException{

    }
}
