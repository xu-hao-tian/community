package com.nowcoder.community;

import org.omg.SendingContext.RunTime;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: 许昊天
 * @Date: 2021/07/12/9:01
 * @Description:
 */
public class WkTests {
    
    public static void main(String[] args){  
        String cmd = "F:/JAVA/development_kit/wkhtmltox/wkhtmltopdf/bin/wkhtmltoimage --quality 75 https://www.nowcoder.com F:/JAVA/许昊天/java-workspace/项目实战/牛客网/wk-images/3.png";
        try {
            Runtime.getRuntime().exec(cmd);
            System.out.println("ok.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
