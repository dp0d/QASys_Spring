package com.exam.qa_robot.utils;

import com.exam.qa_robot.controller.dto.LableDto;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Component
public class fileToArray {
    public static void main(String[] args) {
        fileToArray file =new fileToArray();
        File filename = new File("D:/Code_DATE/QAData/processed/response - 副本.txt"); // 要读取以上路径的input。txt文件
        HashMap<String,String> map =new HashMap();
        map.put("1","v1");
        map.put("3","v3");
        map.put("2","v2");
        map.put("5","v5");
        map.put("6","v6");
        Set<String> keySet = map.keySet();
        if (keySet.contains("4")) System.out.println("yyyyyyyy");
        System.out.println(keySet);
//        file.fileWriter("D:/Code_DATE/QAData/processed/response - 副本.txt", map);
//        System.out.println(lableDtos);
    }
    public List<LableDto> fileRead(String pathname){
        List<LableDto> lableDtos= new ArrayList<LableDto>();
        try { // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw

            /* 读入TXT文件 */
            File filename = new File(pathname); // 要读取以上路径的input。txt文件
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(filename)); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言


            String line = "";
            int i=0;
            while ((line= br.readLine() )!= null) {
                i++;
                String[] array = line.split(" "); //仅分割一个空格
                LableDto lable = new LableDto(i,array[0],array[1],null,null);
                lableDtos.add(lable);
            }
//            System.out.println(line);


//            /* 写入Txt文件 */
//            File writename = new File(".\\result\\en\\output.txt"); // 相对路径，如果没有则要建立一个新的output。txt文件
//            writename.createNewFile(); // 创建新文件
//            BufferedWriter out = new BufferedWriter(new FileWriter(writename));
//            out.write("我会写入文件啦\r\n"); // \r\n即为换行
//            out.flush(); // 把缓存区内容压入文件
//            out.close(); // 最后记得关闭文件

        } catch (Exception e) {
            e.printStackTrace();
        }
        return lableDtos;
    }

    /**
     * 修改文件指定行的标签
     * @param path
     * @param keys
     * @param labels
     */
    public void fileWriter(String path, List<String>keys,List<String>labels) {
        File file = new File(path);
        StringBuilder sb = new StringBuilder();
        //记录修改的行数
        int cnt = 0;
        //记录替换所在的行
        int rowLine = 0;
        //换行符
        String enter = System.getProperty("line.separator");

        //printWriter原本也想放在 try-with 中，少写点代码，
        //但是一个文件不能同时读写，pw 和 br 对同一个文件操作的结果时，文件的内容被清空！！！
        //不妨试下，将 pw 申明在 try-with 中，看下运行结果。
        PrintWriter pw = null;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            for (line = br.readLine(); line != null; line = br.readLine()) {
                rowLine++;
                if (keys.contains( String.valueOf(rowLine))) {
                    line = line.replace(line.split(" ")[0],labels.get(rowLine));
                    cnt++;
                }
                //数据暂存在 StringBuilder 中
                if (rowLine == 1) {
                    sb.append(line);
                } else {
                    sb.append(enter).append(line);
                }
            }
            pw = new PrintWriter(new FileWriter(file));
            pw.print(sb);
        } catch (FileNotFoundException e) {
            System.exit(1);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }


}
