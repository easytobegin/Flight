package com.minisheep.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by minisheep on 17/1/6.
 */
public class GetJsonInterfaceInfo {
    private static String urlPath="http://localhost:8080/demo";
    public static void main(String[] args) throws IOException {
        //ServerFactory.getServer(8080).start();
        //列出原始数据
        StringBuilder json = new StringBuilder();


        URL oracle = new URL(GetJsonInterfaceInfo.urlPath);
        URLConnection yc = oracle.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream(),"UTF-8"));
        String inputLine = null;
        while ( (inputLine = in.readLine()) != null){
            json.append(inputLine);
        }
        in.close();
        String Strjson=json.toString();
        System.out.println("原始数据:");
        System.out.println(Strjson.toString());

        //Servlet里面用request.getParameter("参数名");取到

    }
}
