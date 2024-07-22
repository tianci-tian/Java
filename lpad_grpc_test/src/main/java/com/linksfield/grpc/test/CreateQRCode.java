package com.linksfield.grpc.test;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * Created by zhangxiao on 2018/11/23
 * Descr: Gen ActiveCode
 *
 */
public class CreateQRCode {

   public static void generateQR() throws IOException, WriterException {
	   final int width = 300;
       final int height = 300;
       final String format = "png";
       //final String content = "LPA:1$192.168.1.96$100343E73472FFA2EA701B3EA72B83600C3AEDC8E3F7B38109ACE4C391B6422E";
       final String content = "LPA:1$192.168.1.42$100343E73472FFA2EA701B3EA72B83600C3AEDC8E3F7B38109ACE4C391B6422E";
       //定义二维码的参数
       HashMap hints = new HashMap();
       hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
       //L级：约可纠错7%的数据码字,M级：约可纠错15%的数据码字,Q级：约可纠错25%的数据码字,H级：约可纠错30%的数据码字
       hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);
       hints.put(EncodeHintType.MARGIN, 2);

      //生成二维码
       BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
       //Path file = new File("D:/img.png").toPath();
       Path file = new File("F:/eclipse4.6win32x64Workspace/LPAassistoldAnother/DPplus42.png").toPath();
       MatrixToImageWriter.writeToPath(bitMatrix, format, file);
       System.out.println("生成成功,路径：" + file.toString());
       System.out.println("------------------------------");
       
       
   }
   
   public static String decodeQR(Path file) throws IOException, NotFoundException {
	   HashMap hints = new HashMap();
       hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
       //L级：约可纠错7%的数据码字,M级：约可纠错15%的数据码字,Q级：约可纠错25%的数据码字,H级：约可纠错30%的数据码字
       hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);
       hints.put(EncodeHintType.MARGIN, 2);
       
	   //解析二维码
       MultiFormatReader formatReader = new MultiFormatReader();
       BufferedImage image = ImageIO.read(file.toFile());
       BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
       Result result = formatReader.decode(binaryBitmap, hints);
       System.out.println("二维码解析结果：" + result.toString());
       System.out.println("二维码的格式：" + result.getBarcodeFormat());
       System.out.println("二维码的文本内容：" + result.getText());
       return result.getText();
   }

    public static void main(String[] args) throws Exception {
    	// Path file = new File("I:/Projects/ACTIVATION_CODE_1.png").toPath();
    	//decodeQR(file);
    	generateQR();
    	Path file = new File("F:/eclipse4.6win32x64Workspace/LPAassistoldAnother/DPplus42.png").toPath();
   	    decodeQR(file);
    }

}