package com.bjpowernode.p2p.web;

import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.common.util.DateUtils;
import com.bjpowernode.p2p.common.util.HttpClientUtils;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.service.loan.OnlyNumberService;
import com.bjpowernode.p2p.service.loan.RechargeRecordService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:RechargeController
 * Package:com.bjpowernode.p2p.web
 * Description:
 *
 * @date:2018/11/12 12:16
 * @author:guoxin@bjpowernode.com
 */
@Controller
public class RechargeController {

    @Autowired
    private OnlyNumberService onlyNumberService;

    @Autowired
    private RechargeRecordService rechargeRecordService;

    @RequestMapping (value = "/loan/toAlipayRecharge")
    public String toAlipayRecharge(HttpServletRequest request, Model model,
                                 @RequestParam (value = "rechargeMoney",required = true) Double rechargeMoney) {

        System.out.println("-----------------toAlipayRecharge------------");

        //从session中获取用户信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

        //生成一个全局唯一充值订单号 = 时间戳 + redis全局唯一数字;
        String rechargeNo = DateUtils.getTimeStamp() + onlyNumberService.getOnlyNumber();

        //生成充值记录
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setUid(sessionUser.getId());
        rechargeRecord.setRechargeNo(rechargeNo);
        rechargeRecord.setRechargeMoney(rechargeMoney);
        rechargeRecord.setRechargeTime(new Date());
        rechargeRecord.setRechargeStatus("0");
        rechargeRecord.setRechargeDesc("支付宝充值");

        int addRechargeCount = rechargeRecordService.addRechargeRecord(rechargeRecord);

        if (addRechargeCount > 0) {

            //向pay工程传递参数
            model.addAttribute("p2p_pay_alipay_url","http://localhost:9090/pay/api/alipay");
            model.addAttribute("out_trade_no",rechargeNo);
            model.addAttribute("rechargeMoney",rechargeMoney);
            model.addAttribute("subject","支付宝充值");
            model.addAttribute("body","支付宝充值");

        } else {
            model.addAttribute("trade_msg","充值失败");
            return "toRechargeBack";
        }

        return "toAlipay";

    }


    @RequestMapping(value = "/loan/alipayBack")
    public String alipayBack(HttpServletRequest request,Model model,
                             @RequestParam (value = "out_trade_no",required = true) String out_trade_no,
                             @RequestParam (value = "total_amount",required = true) Double total_amount,
                             @RequestParam (value = "signVerified",required = true) String signVerified) {

        System.out.println("------------out_trade_no:" + out_trade_no + "---------total_amount=" + total_amount);

        //判断签名是否正确
        if (StringUtils.equals(Constants.SUCCESS,signVerified)) {


            //调用pay工程的订单查询接口
            Map<String,Object> paramMap = new HashMap<String,Object>();
            paramMap.put("out_trade_no",out_trade_no);
            String jsonString = HttpClientUtils.doPost("http://localhost:9090/pay/api/alipayQuery",paramMap);

            //解析json格式的字符串
            //将json格式的字符串转换为json对象
            JSONObject jsonObject = JSONObject.parseObject(jsonString);

            JSONObject tradeQueryResponse = jsonObject.getJSONObject("alipay_trade_query_response");

            //获取通信标识code
            String code = tradeQueryResponse.getString("code");

            //判断通信是否成功
            if (StringUtils.equals("10000",code)) {

                //获取业务处理结果trade_status
                String tradeStatus = tradeQueryResponse.getString("trade_status");

               /* 交易状态：
                WAIT_BUYER_PAY（交易创建，等待买家付款）
                TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）
                TRADE_SUCCESS（交易支付成功）
                TRADE_FINISHED（交易结束，不可退款）*/

               if ("TRADE_CLOSED".equals(tradeStatus)) {
                   //更新充值记录的状态为2
                   RechargeRecord updateRechargeRecord = new RechargeRecord();
                   updateRechargeRecord.setRechargeNo(out_trade_no);
                   updateRechargeRecord.setRechargeStatus("2");
                   int modifyRechargeCount = rechargeRecordService.modifyRechargeRecordByRechargeNo(updateRechargeRecord);

                   if (modifyRechargeCount <= 0) {
                       model.addAttribute("trade_msg","充值失败");
                       return "toRechargeBack";
                   }
               }


               if ("TRADE_SUCCESS".equals(tradeStatus)) {
                   //从session中获取用户信息
                   User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

                   //给用户充值【1.更新帐户可用余额 2.更新充值记录的状态】(用户标识，充值金额，充值订单号)
                   paramMap.put("uid",sessionUser.getId());
                   paramMap.put("rechargeMoney",total_amount);
                   paramMap.put("rechargeNo",out_trade_no);
                   paramMap.put("rechargeStatus","1");
                   int rechargeCount = rechargeRecordService.recharge(paramMap);

                   if (rechargeCount <= 0) {
                       model.addAttribute("trade_msg","充值失败");
                       return "toRechargeBack";
                   }
               }



            } else {
                model.addAttribute("trade_msg","充值失败");
                return "toRechargeBack";
            }


        } else {
            model.addAttribute("trade_msg","充值失败");
            return "toRechargeBack";
        }





        return "redirect:/loan/myCenter";
    }






    @RequestMapping (value = "/loan/toWxpayRecharge")
    public String toWxpayRecharge(HttpServletRequest request,Model model,
                                @RequestParam (value = "rechargeMoney",required = true) Double rechargeMoney) {

        System.out.println("-------------------toWxpayRecharge---------------");

        //从session中获取用户信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

        //生成一个全局唯一充值订单号 = 时间戳 + redis全局唯一数字;
        String rechargeNo = DateUtils.getTimeStamp() + onlyNumberService.getOnlyNumber();

        //生成充值记录
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setUid(sessionUser.getId());
        rechargeRecord.setRechargeNo(rechargeNo);
        rechargeRecord.setRechargeMoney(rechargeMoney);
        rechargeRecord.setRechargeTime(new Date());
        rechargeRecord.setRechargeStatus("0");
        rechargeRecord.setRechargeDesc("微信充值");

        int addRechargeCount = rechargeRecordService.addRechargeRecord(rechargeRecord);

        if (addRechargeCount > 0){
            model.addAttribute("rechargeNo",rechargeNo);
            model.addAttribute("rechargeMoney",rechargeMoney);
            model.addAttribute("rechargeTime",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        } else{
            model.addAttribute("trade_msg","充值失败");
            return "toRechargeBack";
        }

        return "showQRCode";
    }

    @RequestMapping(value = "/loan/generateQRCode")
    public void generateQRCode(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam (value = "rechargeNo",required = true) String rechargeNo,
                               @RequestParam (value = "rechargeMoney",required = true) Double rechargeMoney) throws IOException, WriterException {

        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("out_trade_no",rechargeNo);
        paramMap.put("total_fee",rechargeMoney);
        paramMap.put("body","微信支付");

        //调用pay工程的微信统一下单API接口，返回一个code_url
        String jsonString = HttpClientUtils.doPost("http://localhost:9090/pay/api/wxpay", paramMap);

        //解析json格式的字符串
        JSONObject jsonObject = JSONObject.parseObject(jsonString);

        //获取通信标识return_code
        String returnCode = jsonObject.getString("return_code");

        //判断通信是否成功
        if (StringUtils.equals(Constants.SUCCESS,returnCode)) {

            //获取业务处理结果
            String resultCode = jsonObject.getString("result_code");

            //判断业务处理结果
            if (StringUtils.equals(Constants.SUCCESS,resultCode)) {

                //获取code_url链接
                String codeUrl = jsonObject.getString("code_url");

                //将code_url生成一个二维码
                int width = 200;
                int height = 200;

                Map<EncodeHintType,Object> hintTypeObjectMap = new HashMap<EncodeHintType, Object>();
                hintTypeObjectMap.put(EncodeHintType.CHARACTER_SET,"UTF-8");

                //创建一个矩阵对象
                BitMatrix bitMatrix = new MultiFormatWriter().encode(codeUrl, BarcodeFormat.QR_CODE,width,height,hintTypeObjectMap);

                OutputStream outputStream = response.getOutputStream();

                MatrixToImageWriter.writeToStream(bitMatrix,"png",outputStream);

                outputStream.flush();
                outputStream.close();
            } else {
                response.sendRedirect(request.getContextPath() + "toRechargeBack.jsp");
            }

        } else {
            response.sendRedirect(request.getContextPath() + "toRechargeBack.jsp");
        }





    }
}
