package com.linksfield.grpc.test.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class demo2 {
    public static void main(String[] args) throws IOException
    {
        test();
    }

    public static void test(){
        String command = "cmd /c D:/lpad_client/bin/lpad-client.bat";
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line = null;
        while (true) {
            try {
                if (!((line = reader.readLine()) != null)) break;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // 处理每行输出结果
            System.out.println(line);
        }

        try {
            process.waitFor();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
