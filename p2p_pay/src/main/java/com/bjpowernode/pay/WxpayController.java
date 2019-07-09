package com.bjpowernode.pay;

import com.bjpowernode.p2p.common.util.HttpClientUtils;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:WxpayController
 * Package:com.bjpowernode.pay
 * Description:
 *
 * @date:2018/11/13 12:07
 * @author:guoxin@bjpowernode.com
 */
@Controller
public class WxpayController {

    @RequestMapping(value = "/api/wxpay")
    public @ResponseBody Object wxpay(HttpServletRequest request,
                                      @RequestParam (value = "body",required = true) String body,
                                      @RequestParam (value = "out_trade_no",required = true) String out_trade_no,
                                      @RequestParam (value = "total_fee",required = true) Double total_fee) throws Exception {

        Map<String,String> requestDataMap = new HashMap<String,String>();
        requestDataMap.put("appid","wx8a3fcf509313fd74");
        requestDataMap.put("mch_id","1361137902");
        requestDataMap.put("nonce_str",WXPayUtil.generateNonceStr());
        requestDataMap.put("body",body);
        requestDataMap.put("out_trade_no",out_trade_no);
        BigDecimal bigDecimal = new BigDecimal(total_fee);
        BigDecimal multiply = bigDecimal.multiply(new BigDecimal(100));
        requestDataMap.put("total_fee",multiply.toString());
        requestDataMap.put("spbill_create_ip","127.0.0.1");
        requestDataMap.put("notify_url","http://localhost:9090/pay/api/wxpayNotify");
        requestDataMap.put("trade_type","NATIVE");
        requestDataMap.put("product_id",out_trade_no);
        String sign = WXPayUtil.generateSignature(requestDataMap,"367151c5fd0d50f1e34a68a802d6bbca");
        requestDataMap.put("sign",sign);

        //将map集合的请求参数转换为xml
        String requestDataXml = WXPayUtil.mapToXml(requestDataMap);

        //调用统一下单API接口,响应的是xml格式的字符串
        String responseDataXml = HttpClientUtils.doPostByXml("https://api.mch.weixin.qq.com/pay/unifiedorder", requestDataXml);

        //将xml格式的字符串转换为map集合
        Map<String, String> responseDataMap = WXPayUtil.xmlToMap(responseDataXml);

        return responseDataMap;
    }
}
