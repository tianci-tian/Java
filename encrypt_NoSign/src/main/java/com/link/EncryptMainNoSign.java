package com.link;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;

import java.io.IOException;
import java.security.NoSuchProviderException;
import java.security.Security;

public class EncryptMainNoSign {

    public static void main(String[] args) throws PGPException, IOException, NoSuchProviderException {
        // 读取输入数据
//        Scanner scanner = new Scanner(System.in);
//        System.out.print("明文数据地址: ");
//        String inputPath = scanner.nextLine();
//
//
//        System.out.print("公钥地址: ");
//        String publicKeys = scanner.nextLine();
//
//        System.out.print("自己签名用的私钥地址：");
//        String privateKeys = scanner.nextLine();
//
//        System.out.print("自己选择签名后输入的密码：");
//        String password = scanner.nextLine();
//
//        System.out.print("输出加密文件地址: ");
//        String outPath = scanner.nextLine();
//        scanner.close();


        Security.addProvider(new BouncyCastleProvider());
        String outPath = "D:\\桌面\\excel加密\\111.txt";
        String inputPath = "D:\\桌面\\excel加密\\profile_test_1130.inp";
        String publicKeys = "D:\\桌面\\excel加密\\jindongliang_PublicKeys.asc"; // 公钥地址
        KeyBasedFileProcessor.encryptFile(outPath, inputPath, publicKeys, true, true);
        System.out.println("密文已经成功输出到文件");
        //输出加密后的密文
//        System.out.println("Encrypted data: " + new String(encryptedData, StandardCharsets.UTF_8));
    }
}
