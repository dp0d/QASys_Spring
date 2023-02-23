package com.exam.qa_robot.utils;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
@Component
public class LuceneUtils {
    @Value("${files.upload.Lucenepath}")
    private String Lucenepath;

//    public static void main(String[] args) throws Exception {
//        HashMap<String, String> result=sendQuery("法律书@新冠疫情传播情况");
//        for(int i=0;i<5;i++){
//            System.out.println(result.get(i));
//        }
//    }

    public  HashMap<String, String> sendQuery(String query) throws IOException {
        //1.创建
        System.out.println(Lucenepath);
        Socket socket = new Socket(Lucenepath,9001);
        //2.获取输入流输出流
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"utf-8"));
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
        //3.1处理数据(发送)
        bw.write(query);
        bw.newLine();
        bw.flush();
        //3.2处理数据(接收)
        String[] result=new String[5];
        //
        HashMap<String,String> luceneMap =new HashMap();
        for(int i=0;i<5;i++){
            String str = br.readLine();
            System.out.println(str+"+++++++++++++++++++++++++++++++++++++");
            luceneMap.put(String.valueOf(i),str);
            result[i]=str;
        }
        System.out.println("服务端传输完成-------------------------------------------------------------------------------");
        //4.关闭资源
        bw.close();
        socket.close();
        return luceneMap;
    }
}