package com.minisheep.test;

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
        String result = chat.getAnswer(question);

        JSONStringer stringer = new JSONStringer();
        try{
            stringer.array();
                stringer.object().key("id").value(1)
                        .key("question").value(question)
                        .key("answer").value(result).endObject();
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
