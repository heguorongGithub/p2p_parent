package com.bjpowernode.qrcode;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:QRCodeTest
 * Package:com.bjpowernode.qrcode
 * Description:
 *
 * @date:2018/11/13 11:14
 * @author:guoxin@bjpowernode.com
 */
public class QRCodeTest {

    @Test
    public void generateQRCode() throws WriterException, IOException {

        //{"Country":"China","province":"河北省","city":"唐山","address":{"area":"幸福社区","number":"520"}}
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Country","China");
        jsonObject.put("province","河北省");
        jsonObject.put("city","唐山");
        JSONObject addressJson = new JSONObject();
        addressJson.put("area","幸福社区");
        addressJson.put("number","520");
        jsonObject.put("address",addressJson);

        String jsonString = jsonObject.toString();



        int width = 200;
        int height = 200;

        Map<EncodeHintType,Object> hintTypeObjectMap = new HashMap<EncodeHintType, Object>();
        hintTypeObjectMap.put(EncodeHintType.CHARACTER_SET,"UTF-8");

        //创建一个矩阵对象
        BitMatrix bitMatrix = new MultiFormatWriter().encode("weixin://wxpay/bizpayurl?pr=Qmn2Li3", BarcodeFormat.QR_CODE,width,height,hintTypeObjectMap);

        String filePath = "D://";
        String fileName = "qrCode.jpg";

        Path path = FileSystems.getDefault().getPath(filePath, fileName);

        //将矩阵对象转换为图片
        MatrixToImageWriter.writeToPath(bitMatrix,"jpg",path);

        System.out.println("生成图片成功");


    }
}
