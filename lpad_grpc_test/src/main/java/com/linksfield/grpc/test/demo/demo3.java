package com.linksfield.grpc.test.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class demo3 {
    public static void main(String[] args) throws IOException, InterruptedException {
        test();
    }

    public static void test() throws InterruptedException, IOException {
        String[] command = {"cmd.exe", "/c", "D:/lpad_client/bin/lpad-client.bat"};
        Process process = Runtime.getRuntime().exec(command);

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            // 处理每行输出结果
            System.out.println(line);
        }

        process.waitFor();
    }
}
