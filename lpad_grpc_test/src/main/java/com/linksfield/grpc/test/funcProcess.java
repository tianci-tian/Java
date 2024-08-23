package com.linksfield.grpc.test;

import com.google.zxing.NotFoundException;
import com.linksfield.grpc.test.feishu.SendMessage;
import com.linksfield.grpc.test.service.APDUCmdService;

import javax.smartcardio.CardException;
import java.io.*;
import java.net.InetAddress;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class funcProcess {

    APDUCmdService apduCmdService = new APDUCmdService();

    public String readActiveCodeFormtxt() {
        String activecode = "";
        try {
            //
            String pathname = "C:\\Users\\zsq\\Desktop\\test\\grpc_test_jar_dp\\" + Constant.txt_string; //
            File filename = new File(pathname); //
            InputStreamReader reader = new InputStreamReader(new FileInputStream(filename),"gbk"); //
            BufferedReader br = new BufferedReader(reader); //
            String line = "";

            while((line = br.readLine())!= null) {
                activecode = line;
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return activecode;
    }

    public String readActiveCodeFormPic() {
        String activecode = "";
        String fileName = "./"+Constant.pic_string;
        Path file = new File(fileName).toPath();
        try {
            activecode = CreateQRCode.decodeQR(file);
        } catch (NotFoundException | IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        return activecode;
    }

    public String downProfileByActiveCode(String activeCode) {
        String res = "";
        res = Constant.get_Download_string + " " + activeCode;
        return res;
    }

    public String disableProfile(String iccid) {
        String res = "";
        res = Constant.get_Disable_string + " " + iccid;
        return res;
    }

    public String deleteProfile(String iccid) {
        String res = "";
        res = Constant.get_Delete_string + " " + iccid;
        return res;
    }

    public String getProfileList(){
        String res = "";
        res = Constant.get_List_string;
        return res;
    }

    public String getEID(){
        String res = "";
        res = Constant.get_EID_string;
        return res;
    }

    public void iccReset() throws CardException {
        apduCmdService.reset();
    }

    public void iccdisConnect() throws CardException {
        apduCmdService.disConnectCard();
    }

    public String xchByte(String hexString) {
        String value = "";
        int len = hexString.length();
        String temp;
        for (int i=0; i<len; i=i+2) {
            temp = hexString.substring(i+1, i+2) + hexString.substring(i, i+1);
            value = value + temp;
        }
        return value;
    }

    public void createBatScript() throws IOException, InterruptedException {
        String tBatFilePath = "";
        if(Constant.TEST_TYPE == 0){
            tBatFilePath = Constant.FilePath_dp;
        }
        else{
            tBatFilePath = Constant.FilePath_eim;
        }
        // 执行批处理文件
        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", tBatFilePath);
        builder.redirectErrorStream(true);

        // 执行bat文件
        Process process = builder.start();

        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            // 处理每行输出结果
            System.out.println(line);
        }

        process.waitFor();

        // 获取脚本的执行结果
        int exitValue = process.exitValue();
        if (exitValue == 0) {
            System.out.println("脚本执行成功");
        } else {
            System.out.println("脚本执行失败");
        }
    }

    public String executeBatScript(String batCmd) throws IOException, InterruptedException {
        String line;
        String lastLine = "";
        String parameter = batCmd;

        String tBatFilePath = "";
        if(Constant.TEST_TYPE == 0){
            tBatFilePath = Constant.FilePath_dp;
        }
        else{
            tBatFilePath = Constant.FilePath_eim;
        }

        String command = "cmd /c " + tBatFilePath;

        StringBuilder commandBuilder = new StringBuilder();

        commandBuilder.append(command);
        commandBuilder.append(" ");
        commandBuilder.append(parameter);

        // 获取运行时对象
        Runtime runtime = Runtime.getRuntime();

        // 执行.bat文件
        Process process = runtime.exec(commandBuilder.toString());

        // 获取命令输出结果
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "GBK")); // 设置编码为GBK
        while ((line = reader.readLine()) != null) {
            lastLine = line;
            //System.out.println(line);
            wirtelogOutputFile(line, true, true);
        }

        // 等待命令执行完成
        process.waitFor();

        // 获取脚本的执行结果
        int exitValue = process.exitValue();
        if (exitValue == 0) {
            //System.out.println("脚本执行成功");
        } else {
            System.out.println("脚本执行失败");
        }

        return lastLine;
    }

    boolean check_result(String resdata){
        boolean flag = false;
        if(resdata.equalsIgnoreCase("Success")){
            flag = true;
        }
        return flag;
    }

    public String get_res_eid(String resdata){
        String data = resdata;
        return data;
    }

    public String get_res_iccid(String resdata) {
        if (resdata == null || resdata.equalsIgnoreCase("None")) {
            return "None";
        }

        int index = resdata.indexOf("iccid");
        if (index == -1) {
            return "None";  // 如果没有找到 "iccid"，返回 "None"
        }

        // 找到 "iccid" 后，获取后续的部分内容
        int startIndex = index + 6;  // 6 是 "iccid:" 或 "iccid=" 后的偏移量
        if (startIndex >= resdata.length()) {
            return "None";  // 如果 startIndex 超出了 resdata 的长度，返回 "None"
        }

        // 尝试找到 ICCID 的结束位置，假设 ICCID 是 20 位数字
        int endIndex = Math.min(startIndex + 20, resdata.length());

        String iccid = resdata.substring(startIndex, endIndex).trim();

        // 检查提取的字符串是否符合 ICCID 的预期格式
        if (iccid.length() == 20 && iccid.matches("\\d+")) {
            return iccid;
        }

        return "None";  // 如果提取的 ICCID 无效，返回 "None"
    }

    public boolean get_res_profile_status(String resdata){
        boolean flag = false;
        int index;
        String data = "";
        index = resdata.indexOf("state");
        data = resdata.substring(index+6, index+7);
        if(data.equalsIgnoreCase("1")){
            flag = true;
        }
        return flag;
    }

    //覆盖或追加写入log文件
    public void wirteOutputFile(String content, boolean append, boolean flag) {
        try {
            //构造函数中的第二个参数true表示以追加形式写文件
            FileWriter PersoData_wr = new FileWriter("./err.txt",append);
            if(flag == true)
            {
                PersoData_wr.write(content+"\r\n");
            }
            else
            {
                PersoData_wr.write(content);
            }
            PersoData_wr.close();
        } catch (Exception e) {
            System.out.println("Output file write failed！!!" + e);
            e.printStackTrace();
        }
    }

    //覆盖或追加写入log文件
    public void wirtelogOutputFile(String content, boolean append, boolean flag) {
        try {
            //构造函数中的第二个参数true表示以追加形式写文件
            FileWriter PersoData_wr = new FileWriter("./log.txt",append);
            if(flag == true)
            {
                PersoData_wr.write(content+"\r\n");
            }
            else
            {
                PersoData_wr.write(content);
            }
            PersoData_wr.close();
        } catch (Exception e) {
            System.out.println("Output file write failed！!!" + e);
            e.printStackTrace();
        }
    }

    public String get_current_time(){
        String time;
        Date date = new Date();
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //System.out.println(dateFormat.format(date));
        time = dateFormat.format(date);
        return time;
    }

    public void sendMessage_ForFeishu(){
        SendMessage.sendMessageForFeishu();
    }

    boolean checkNetWork() throws IOException {
        boolean flag = false;
        String host = "www.baidu.com"; // 需要检测的主机名或IP地址
        try {
            InetAddress address = InetAddress.getByName(host);
            boolean reachable = address.isReachable(5000); // 设置超时时间为5秒钟

            if (reachable) {
                flag = true;
            } else {
                flag = false;
                System.out.println("net is disconect");
            }
        }
        catch (Exception e){
            System.out.println("net is disconect");
            e.printStackTrace();
        }

        return flag;
    }

    private static final int MAX_RETRIES = 3;

    public int test_func() throws CardException, IOException, InterruptedException {
        String curent_time = get_current_time();
        System.out.println(curent_time);

        if (!checkNetWork()) {
            return 0;
        }

        iccReset();

        String batCmd = getEID();
        String reslastCmd = executeBatScript(batCmd);
        String res_data = get_res_eid(reslastCmd);
        curent_time = get_current_time();
        System.out.println(curent_time + "   " + "device eid: " + res_data);

        if (!handleProfiles()) {
            return 1;
        }

        String activeCode = readActiveCodeFormtxt();
        if (!downloadProfile(activeCode)) {
            return 1;
        }

        String iccid = getLatestIccid();
        if (iccid.equalsIgnoreCase("None")) {
            logAndWriteOutput("device no have profile!!!");
            return 1;
        }

        if (get_res_profile_status(reslastCmd) && !disableProfileByIccid(iccid)) {
            return 1;
        }

        if (!deleteProfileByIccid(iccid)) {
            return 1;
        }

        iccdisConnect();
        return 0;
    }

    private boolean handleProfiles() throws IOException, InterruptedException {
        int num = 0;

        while (num < 4) {
            try {
                String batCmd = getProfileList();
                String reslastCmd = executeBatScript(batCmd);
                String iccid = get_res_iccid(reslastCmd);

                if (iccid.equalsIgnoreCase("None")) {
                    break;
                } else if (num == MAX_RETRIES) {
                    logAndWriteOutput("try 3 times, device disable/delete profile failed!!!");
                    return false;
                } else if (get_res_profile_status(reslastCmd) && !disableProfileByIccid(iccid)) {
                    num++;
                    continue;
                }

                if (!deleteProfileByIccid(iccid)) {
                    num++;
                } else {
                    num++;
                }
            } catch (IOException | InterruptedException e) {
                logAndWriteOutput("Exception occurred: " + e.getMessage());
                num++;
            }
        }

        return true;
    }

    private boolean disableProfileByIccid(String iccid) throws IOException, InterruptedException {
        String batCmd = disableProfile(iccid);
        String reslastCmd = executeBatScript(batCmd);
        boolean flag = check_result(reslastCmd);

        if (!flag) {
            logAndWriteOutput("device disable profile failed!!!");
        } else {
            logAndWriteOutput("device disable profile success");
        }

        return flag;
    }

    private boolean deleteProfileByIccid(String iccid) throws IOException, InterruptedException {
        String batCmd = deleteProfile(iccid);
        String reslastCmd = executeBatScript(batCmd);
        boolean flag = check_result(reslastCmd);

        if (!flag) {
            logAndWriteOutput("device delete profile failed!!!");
            if (MAX_RETRIES > 0) {
                Thread.sleep(2000); // 2秒延迟
            }
        } else {
            logAndWriteOutput("device delete profile success");
        }

        return flag;
    }

    private boolean downloadProfile(String activeCode) throws IOException, InterruptedException {
        int maxRetries = 3;
        int attempts = 0;
        boolean flag = false;

        while (attempts < maxRetries) {
            String batCmd = downProfileByActiveCode(activeCode);
            String reslastCmd = executeBatScript(batCmd);
            flag = check_result(reslastCmd);

            if (flag) {
                logAndWriteOutput("device download profile success");
                return true;
            } else {
                logAndWriteOutput("device download profile failed!!!");
                attempts++;
                if (attempts < maxRetries) {
                    Thread.sleep(2000); // 等待2秒再尝试
                }
            }
        }

        return false;
    }
    private String getLatestIccid() throws IOException, InterruptedException {
        String batCmd = getProfileList();
        String reslastCmd = executeBatScript(batCmd);
        return get_res_iccid(reslastCmd);
    }

    private void logAndWriteOutput(String message) throws IOException {
        String curent_time = get_current_time();
        System.out.println(curent_time + "   " + message);
        String res_data = curent_time + "   " + message;
        wirteOutputFile(res_data, true, true);
    }

}
