package com.linksfield.grpc.test.feishu;

import cn.hutool.http.HttpRequest;

import com.alibaba.fastjson.JSON;
import com.linksfield.grpc.test.Constant;

import java.util.HashMap;
import java.util.Map;


public class SendMessage {

    //这里就是刚才拿到的Webhook的值
    //public static final String WebHookUrl = "https://www.feishu.cn/flow/api/trigger-webhook/451da1817d0edacfef4f7feb271bb636";

    public static void sendMessage(String msg){
        String WebHookUrl = "";

        if(Constant.TEST_TYPE == 0){
            WebHookUrl = "https://www.feishu.cn/flow/api/trigger-webhook/db6d9ba9bd2940771ad4f6d1ef9c10ac";
        }
        else{
            WebHookUrl = "https://www.feishu.cn/flow/api/trigger-webhook/451da1817d0edacfef4f7feb271bb636";
        }

        //请求的JSON数据，这里用map在工具类里转成json格式
        Map<String,Object> json=new HashMap();
        Map<String,Object> text=new HashMap();
        json.put("msg_type", "text");
        text.put("text", "项目告警通知：" + msg);
        json.put("content", text);
        //发送post请求
        String result = HttpRequest.post(WebHookUrl).body(JSON.toJSONString(json), "application/json;charset=UTF-8").execute().body();
        System.out.println(result);
    }

    public static void sendMessageForFeishu(){
        String message = "";
        if(Constant.TEST_TYPE == 0){
            message = "123456";
        }
        else{
            message = "123";
        }
        SendMessage.sendMessage(message);
    }

//    public static void main(String[] args) {
//        SendMessage.sendMessage("123");
//    }

}