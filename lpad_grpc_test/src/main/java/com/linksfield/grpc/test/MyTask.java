package com.linksfield.grpc.test;

import lombok.SneakyThrows;

import java.util.TimerTask;

public class MyTask extends TimerTask {
    static int err_num = 0;

    @SneakyThrows
    @Override
    public void run() {
        int res = 0;
        // 在此编写需要重复执行的代码
        funcProcess func_obj = new funcProcess();
        res = func_obj.test_func();
        if(res == 0){
            err_num = 0;
        }
        else{
            err_num++;
            if(err_num == 5){
                func_obj.sendMessage_ForFeishu();
                err_num = 0;
            }
        }

    }


}
