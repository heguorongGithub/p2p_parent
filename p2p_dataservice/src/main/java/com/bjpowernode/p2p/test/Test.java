package com.bjpowernode.p2p.test;

import com.bjpowernode.p2p.service.loan.BidInfoService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ClassName:Test
 * Package:com.bjpowernode.p2p.test
 * Description:
 *
 * @date:2018/11/10 10:57
 * @author:guoxin@bjpowernode.com
 */
public class Test {

    public static void main(String[] args) {

        //获取spring容器
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");

        //获取指定容器中的bean对象
        BidInfoService bidInfoService = (BidInfoService) context.getBean("bidInfoServiceImpl");

        //准备投资参数
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("uid",1);
        paramMap.put("loanId",3);
        paramMap.put("bidMoney",1.0);

        //创建一个固定的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(100);

        for (int i = 0; i < 1000 ; i++) {
            executorService.submit(new Callable<Object>(){
                @Override
                public Object call() throws Exception {
                    return bidInfoService.invest(paramMap);
                }
            });
        }


        executorService.shutdown();



    }
}
