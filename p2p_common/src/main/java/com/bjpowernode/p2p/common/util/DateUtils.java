package com.bjpowernode.p2p.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * ClassName:DateUtils
 * Package:com.bjpowernode.p2p.common.util
 * Description:
 *
 * @date:2018/11/10 15:03
 * @author:guoxin@bjpowernode.com
 */
public class DateUtils {


    /**
     * 通过添加天数返回日期值
     * @param date
     * @param days
     * @return
     */
    public static Date getDateByAddDays(Date date, Integer days) {

        //创建日期处理对象
        Calendar calendar = Calendar.getInstance();

        //设置calendar对象的日期值
        calendar.setTime(date);

        //添加天数
        calendar.add(Calendar.DAY_OF_MONTH,days);

        return calendar.getTime();
    }

    public static Date getDateByAddMonths(Date date, Integer months) {
        //创建日期处理对象
        Calendar calendar = Calendar.getInstance();

        //设置calendar对象的日期值
        calendar.setTime(date);

        //添加月份
        calendar.add(Calendar.MONTH,months);

        return calendar.getTime();
    }



    public static void main(String[] args) throws ParseException {
        System.out.println(getDateByAddMonths(new SimpleDateFormat("yyyy-MM-dd").parse("2008-08-08"),1));

    }

    /**
     * 获取时间戳
     * @return 格式：yyyyMMddHHmmssSSS
     */
    public static String getTimeStamp() {
        return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    }
}
