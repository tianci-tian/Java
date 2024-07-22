package com.linksfield.grpc.test;


import javax.smartcardio.CardException;
import java.io.IOException;
import java.util.Timer;

public class autoTest {
    public static void main(String[] args) throws InterruptedException, IOException, CardException {
		Timer timer = new Timer();
		long delay = 0;   // 初始化延迟为0ms
		long period = 1 * 60 * 1000;   // 间隔为60分钟（单位为毫秒）
		timer.scheduleAtFixedRate(new MyTask(), delay, period);
    }
}
