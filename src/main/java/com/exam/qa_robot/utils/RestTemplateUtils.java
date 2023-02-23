package com.exam.qa_robot.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class RestTemplateUtils {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${files.upload.QApath}")
    private String QApath;
    @Value("${files.upload.Hashpath}")
    private String Hashpath;
    @Value("${files.upload.QGpath}")
    private String QGpath;
    @Value("${files.upload.LPpath}")
    private String LPpath;

    public void RestTemplateTestGet(Map<String, String> request) {
        /**
         * getForObject
         * 参数1 要请求的地址的url  必填项
         * 参数2 响应数据的类型 是String 还是 Map等 必填项
         * 参数3 请求携带参数 选填
         * getForObject 方法的返回值就是 被调用接口响应的数据
         */
//        String url = "http://121.250.212.43:5000//sim?query1={query1}&query2={query2}";
//        String url = "https://510173f1-ccf7-47ae-964a-1c5c965bfd18.mock.pstmn.io/test?query1={query1}&query2={query2}";
//        String url = "https://localhost:9090/user/test2";
        String url = "https://510173f1-ccf7-47ae-964a-1c5c965bfd18.mock.pstmn.io/test";
        //map的定义
//        Map<String,String> request = new HashMap<>();
//        request.put("query1","111");
//        request.put("query2","222");

        //1. getForObject()
        //先获取返回的字符串，若想获取属性，可以使用gson转化为实体后get方法获取
        String result = restTemplate.getForObject(url, String.class, request);


        //2. getForEntity()
        //获取实体ResponseEntity，可以用get函数获取相关信息
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class, request);
        System.out.println("responseEntity.getStatusCode() = " + responseEntity.getStatusCode());

        System.out.println("responseEntity.getStatusCodeValue() = " + responseEntity.getStatusCodeValue()); //responseEntity.getStatusCodeValue() = 200

        System.out.println("responseEntity.getBody() = " + responseEntity.getBody());   //responseEntity.getBody() = {"code":"0","data":{"address":"北京市海淀区","id":1,"password":"123456","role":0,"sex":0,"telephone":"10086","username":"小明"},"msg":"操作成功"}

        System.out.println("responseEntity.getHeaders() = " + responseEntity.getHeaders());//responseEntity.getHeaders() = [Content-Type:"application/json", Content-Length:"158", Server:"Werkzeug/0.14.1 Python/3.7.0", Date:"Sat, 16 Oct 2021 06:01:26 GMT"]

        System.out.println("responseEntity.getClass() = " + responseEntity.getClass());//responseEntity.getClass() = class org.springframework.http.ResponseEntity

    }

    public void RestTemplateTestPost() {
        //String url = "http://127.0.0.1:5000/register";
//        String url = "http://127.0.0.1:5000/login";
        String url = "http://127.0.0.1:5000/query/aa";
//        String url="https://510173f1-ccf7-47ae-964a-1c5c965bfd18.mock.pstmn.io/test";

        //LinkedMultiValueMap一个键对应多个值，对应format-data的传入类型
        LinkedMultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        Map map = new HashMap<String, String>();
        map.put("question", "泥鳅应该怎么吃");
        //入参
        request.set("question", "泥鳅应该怎么吃");
        //请求
        String result = restTemplate.postForObject(url, map, String.class);
        System.out.println(result);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, request, String.class);
        System.out.println("responseEntity.getBody() = " + responseEntity.getBody());
    }

    public void RestTemplateTestUpload() throws IOException {
        String httpMethod = "http://127.0.0.1:5555/photo";
        String args = "可以添加其他属性参数";

        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("args", args);

        //打开图片并写入流
        File file = new File("D:\\WorkProject\\static\\output\\04.png");
        byte[] bytesArray = new byte[(int) file.length()];

        FileInputStream fis = new FileInputStream(file);
        fis.read(bytesArray); //read file into bytes[]
        fis.close();

        ByteArrayResource contentsAsResource = new ByteArrayResource(bytesArray) {
            //重载设置文件名
            @Override
            public String getFilename() {
                return "04.png";
            }
        };
        paramMap.add("file", contentsAsResource);
        String result = restTemplate.postForObject(httpMethod, paramMap, String.class);
//        System.out.println("result = " + result);
    }

    /**
     * 向QA发送请求
     *
     * @param url_tail
     * @param question
     * @param context
     */
    public String QAPost(String url_tail, String question, String context) {
//        Map answerMap = new HashMap();

        //设置url
        String url = QApath + url_tail;
        //设置要发送的数据
        String message = question;
        if (context != null) {//需要拼接时
            message = question + "|" + context;
        }
//        System.out.println(message);
//        System.out.println(url);
        //请求

        //RestTemplate设置编码
//        restTemplate.getMessageConverters().set(1,new StringHttpMessageConverter(StandardCharsets.US_ASCII));
//        List result = restTemplate.postForObject(url,map,List.class);

        ResponseEntity<String> result = restTemplate.postForEntity(url, message.getBytes(StandardCharsets.UTF_8), String.class);
        String answer = unicodeDecode(result.getBody());
        return answer;
    }

    /**
     * 向QG发送请求
     * @param message
     * @return
     */
    public List<HashMap<String, String>> QGPost(String message) {

//        Map answerMap = new HashMap();
        //设置url
        String url = QGpath + "/gen";

        //请求
        //RestTemplate设置编码
//        restTemplate.getMessageConverters().set(1,new StringHttpMessageConverter(StandardCharsets.US_ASCII));
//        List result = restTemplate.postForObject(url,message,List.class);
//        String result = restTemplate.postForObject(url, message, String.class);
        Map map = new HashMap<String, String>();
        map.put("context", message);
        HashMap<String,List<HashMap<String,String>>> result = restTemplate.postForObject(url, map, HashMap.class);
        return result.get("result");
    }


    public void getLabel(String fileUrl,String fid) {

        String url = "http://10.27.138.51:5000/pro";
        //map的定义
        Map<String,String> request = new HashMap<>();
        request.put("fileUrl",fileUrl);
        request.put("fid",fid);

        String result = restTemplate.postForObject(url, request,String.class);

    }

    public void sendLabelUrl(String fileUrl) {

        String url = LPpath+"/pro";
        //map的定义
        Map<String,String> request = new HashMap<>();
        request.put("fileUrl",fileUrl);

        String result = restTemplate.postForObject(url,  request,String.class);

    }
    public String SSIM(String query1, String query2) {

        String url = Hashpath+"/sim?query1={query1}&query2={query2}";
        //map的定义
        Map<String,String> request = new HashMap<>();
        request.put("query1",query1);
        request.put("query2",query2);

        String result = restTemplate.getForObject(url, String.class, request);

        return mapStringToMap(result).get("similarity");

    }

    /**
     * hash
     * @param query
     * @param book
     * @param num
     * @return
     */
    public Map<String, String> HashGet(String query, String book, String num) {

        String url = Hashpath+"/query?query={query}&book={book}&num={num}";
        //map的定义
        Map<String, String> request = new HashMap<>();
        request.put("query", query);
        request.put("book", book);
        request.put("num", num);

        //先获取返回的字符串，若想获取属性，可以使用json转化为实体后get方法获取
        String result = restTemplate.getForObject(url, String.class, request);
        return mapStringToMap(unicodeDecode(result));
    }

    /**
     * @param string
     * @return 转换之后的内容
     * @Title: unicodeDecode
     * @Description: unicode解码 将Unicode的编码转换为中文
     */
    public String unicodeDecode(String string) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(string);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            string = string.replace(matcher.group(1), ch + "");
        }
        return string;
    }


    /**
     * 将Map字符串转换为Map
     *
     * @param str Map字符串
     * @return Map
     */
    public static Map<String,String> mapStringToMap(String str){
        str = str.substring(1, str.length()-1);
        String[] strs = str.split(",");
        Map<String,String> map = new HashMap<String, String>();
        for (String string : strs) {
            String key = string.split(":")[0];
            String value = string.split(":")[1];
            // 去掉头部空格
            String key1 = key.trim();
            String c1 = String.valueOf(key1.charAt(key1.length() - 1));
            if ("\"".equals(c1)){
                key1=key1.substring(1,key1.length()-1);
            }
            String value1 = value.trim();
            String c2 = String.valueOf(value1.charAt(value1.length() - 1));
            if ("\"".equals(c2)){
                value1=value1.substring(1,value1.length()-1);
            }
            map.put(key1, value1);
        }
        return map;
    }
//
//    public static void main(String[] args) {
//        Map<String, String> map = new HashMap<String, String>();
//        map.put("key1", "value1");
//        map.put("key2", "value2");
//        map.put("key3", "value3");
////        System.out.println("asdfg".substring(0,4));
//
//        String mapStr = map.toString();
//        Map<String, String> newMap = mapStringToMap("{\"similarity\": 1}");
//        System.out.println("！"+newMap);
//        System.out.println(newMap.get("similarity"));
//    }



}

