package com.linksfield.grpc.test.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class demo1 {
    public static void main(String[] args) throws IOException {
        test();
    }

    public static void test(){
        String batFilePath = "D:/lpad_client/bin/lpad-client.bat"; // 这里需要指定.bat文件的完整路径

        try {
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "D:/lpad_client/bin/lpad-client.bat");
            builder.redirectErrorStream(true);
            Process process = builder.start();

            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // 处理每行输出结果
                System.out.println(line);
            }

            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


}
