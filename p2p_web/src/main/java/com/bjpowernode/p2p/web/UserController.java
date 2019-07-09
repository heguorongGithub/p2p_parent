package com.bjpowernode.p2p.web;

import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.common.util.HttpClientUtils;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.ResultObject;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.LoanInfoService;
import com.bjpowernode.p2p.service.user.FinanceAccountService;
import com.bjpowernode.p2p.service.user.UserService;
import javafx.scene.shape.VLineTo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * ClassName:UserController
 * Package:com.bjpowernode.p2p.web
 * Description:
 *
 * @date:2018/11/8 9:57
 * @author:guoxin@bjpowernode.com
 */
@Controller
//@RestController//该类中所有的方法返回的都json对象    //等同于 @Controller + 每个方法中的@ResponseBody
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FinanceAccountService financeAccountService;

    @Autowired
    private LoanInfoService loanInfoService;

    @Autowired
    private BidInfoService bidInfoService;

//    @RequestMapping(value = "/loan/checkPhone",method = RequestMethod.GET) //方法请求的路径为/loan/checkPhone,只可以接收get
//    @GetMapping(value = "/loan/checkPhone") //等同于  @RequestMapping(value = "/loan/checkPhone",method = RequestMethod.GET)
    @PostMapping(value = "/loan/checkPhone") //等同于 @RequestMapping(value = "/loan/checkPhone",method = RequestMethod.POST)
    @ResponseBody
    public Object checkPhone(HttpServletRequest request,
                                           @RequestParam (value = "phone",required = true) String phone) {
        Map<String,Object> retMap = new HashMap<String,Object>();

        //验证手机号码是否重试（手机号码是否存在）【标识】，（根据手机号码查询用户信息）【用户】
        User user = userService.queryUserByPhone(phone);

        //判断用户是否存在
        if (null != user) {
            retMap.put(Constants.ERROR_MESSAGE,"该手机号码已存在，请更换手机号码");
            return retMap;
        }

        retMap.put(Constants.ERROR_MESSAGE,Constants.OK);

        return retMap;
    }

    @GetMapping(value = "/loan/checkCaptcha")
    @ResponseBody
    public Map<String,Object> checkCaptcha(HttpServletRequest request,
                                           @RequestParam (value = "captcha",required = true) String captcha) {
        Map<String,Object> retMap = new HashMap<String,Object>();

        //从session中获取captcha
        String sessionCaptcha = (String) request.getSession().getAttribute(Constants.CAPTCHA);

        ///用户输入captcha与session中的captcha比较
        if (!StringUtils.equalsIgnoreCase(sessionCaptcha,captcha)) {
            retMap.put(Constants.ERROR_MESSAGE,"请输入正确的图形验证码");
            return retMap;
        }

        retMap.put(Constants.ERROR_MESSAGE,Constants.OK);

        return retMap;
    }


    @RequestMapping(value = "/loan/register")
    @ResponseBody
    public Object register(HttpServletRequest request,
                           @RequestParam (value = "phone",required = true) String phone,
                           @RequestParam (value = "loginPassword",required = true) String loginPassword,
                           @RequestParam (value = "replayLoginPassword",required = true) String replayLoginPassword) {

        Map<String,Object> retMap = new HashMap<String,Object>();


        //验证参数
        if (!Pattern.matches("^1[1-9]\\d{9}$",phone)) {
            retMap.put(Constants.ERROR_MESSAGE,"请输入正确的手机号码");
            return retMap;
        }

        if (!StringUtils.equals(loginPassword,replayLoginPassword)) {
            retMap.put(Constants.ERROR_MESSAGE,"两次密码输入不一致");
            return retMap;
        }


        //用户注册【1.新增用户 2.开立帐户】(手机号，密码) -> 返回Boolean|int|结果对象ResultObject
        ResultObject resultObject = userService.register(phone,loginPassword);

        //判断是否注册成功
        if (!StringUtils.equals(Constants.SUCCESS,resultObject.getErrorCode())) {
            retMap.put(Constants.ERROR_MESSAGE,"用户注册失败，请重试...");
            return retMap;
        }

        //将用户的信息存放到session中
        request.getSession().setAttribute(Constants.SESSION_USER,userService.queryUserByPhone(phone));

        retMap.put(Constants.ERROR_MESSAGE,Constants.OK);


        return retMap;
    }

    @RequestMapping(value = "/loan/myFinanceAccount")
    @ResponseBody
    public FinanceAccount myFinanceAccount(HttpServletRequest request) {
        //从session中获取用户信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

        //根据用户标识获取帐户信息
        FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(sessionUser.getId());

        return financeAccount;
    }


    @RequestMapping(value = "/loan/verifyRealName")
    @ResponseBody
    public Object verifyRealName(HttpServletRequest request,
                                 @RequestParam (value = "realName",required = true) String realName,
                                 @RequestParam (value = "idCard",required = true) String idCard,
                                 @RequestParam (value = "replayIdCard",required = true) String replayIdCard) {

        Map<String,Object> retMap = new HashMap<String,Object>();

        //验证参数
        if (!Pattern.matches("[\\u4e00-\\u9fa5]{0,}",realName)) {
            retMap.put(Constants.ERROR_MESSAGE,"真实姓名只支持中文");
            return retMap;
        }

        if (!Pattern.matches("(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)",idCard)) {
            retMap.put(Constants.ERROR_MESSAGE,"请输入正确的身份证号码");
            return retMap;
        }

        if (!StringUtils.equals(idCard,replayIdCard)) {
            retMap.put(Constants.ERROR_MESSAGE,"两次输入身份证号码不一致");
            return retMap;
        }


        //实名认证,调用互联网接口 -> 返回json格式的字符串

        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("appkey","48af9aad0820cf50e96854d5ef4e20d1");
        paramMap.put("cardNo",idCard);
        paramMap.put("realName",realName);
        String jsonStr = HttpClientUtils.doPost("https://way.jd.com/youhuoBeijing/test",paramMap);
//        String jsonStr = "{\"code\":\"10000\",\"charge\":false,\"remain\":1305,\"msg\":\"查询成功\",\"result\":{\"error_code\":0,\"reason\":\"成功\",\"result\":{\"realname\":\"乐天磊\",\"idcard\":\"350721197702134399\",\"isok\":true}}}";

        //解析json格式的字符串，使用fastjson
        //将json格式的字符串转换为json对象
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);

        //获取通信标签code
        String code = jsonObject.getString("code");

        //判断通信是否成功
        if (StringUtils.equals("10000",code)) {
            //成功
                //获取业务处理结果
            Boolean isok = jsonObject.getJSONObject("result").getJSONObject("result").getBoolean("isok");

            //判断业务处理结果
            if (isok) {
                //从session中获取用户信息
                User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);
                //更新用户的信息
                User updateUser = new User();
                updateUser.setId(sessionUser.getId());
                updateUser.setName(realName);
                updateUser.setIdCard(idCard);
                int modifyUserCount = userService.modifyUserByUid(updateUser);

                if (modifyUserCount > 0) {

                    //将用户的信息存放到session中
                    request.getSession().setAttribute(Constants.SESSION_USER,userService.queryUserByPhone(sessionUser.getPhone()));

                    retMap.put(Constants.ERROR_MESSAGE,Constants.OK);
                } else {
                    retMap.put(Constants.ERROR_MESSAGE,"实名认证人数过多，请稍后重试...");
                    return retMap;
                }


            } else {
                retMap.put(Constants.ERROR_MESSAGE,"实名认证人数过多，请稍后重试...");
                return retMap;
            }


        } else {
            //失败
            retMap.put(Constants.ERROR_MESSAGE,"通信异常，请稍后重试...");
            return retMap;
        }



        return retMap;
    }


    @RequestMapping(value = "/loan/loadStat")
    @ResponseBody
    public Object loadState(HttpServletRequest request) {
        Map<String,Object> retMap = new HashMap<String,Object>();

        //历史平均年化收益率
        Double historyAverageRate = loanInfoService.queryHistoryAverageRate();

        //平台注册总人数
        Long allUserCount = userService.queryAllUserCount();

        //平台累计投资金额
        Double allBidMoney = bidInfoService.queryAllBidMoney();

        retMap.put(Constants.HISTORY_AVERAGE_RATE,historyAverageRate);
        retMap.put(Constants.ALL_USER_COUNT,allUserCount);
        retMap.put(Constants.ALL_BID_MONEY,allBidMoney);


        return retMap;
    }

    @RequestMapping(value = "/loan/login")
    @ResponseBody
    public Object login(HttpServletRequest request,
                        @RequestParam (value = "phone",required = true) String phone,
                        @RequestParam (value = "loginPassword",required = true) String loginPassword) {

        Map<String,Object> retMap = new HashMap<String,Object>();

        if (!Pattern.matches("^1[1-9]\\d{9}$",phone)) {
            retMap.put(Constants.ERROR_MESSAGE,"请输入正确的手机号码");
            return retMap;
        }

        //用户登录【1.根据手机号和密码查询用户 2.更新最近登录时间】(手机号，登录密码) -> 返回User
        User user = userService.login(phone,loginPassword);

        //判断用户是否存在
        if (null == user) {
            retMap.put(Constants.ERROR_MESSAGE,"用户名或密码有误，请重新输入...");
            return retMap;
        }

        //将用户的信息存放到session中
        request.getSession().setAttribute(Constants.SESSION_USER,user);

        retMap.put(Constants.ERROR_MESSAGE,Constants.OK);


        return retMap;
    }

    @RequestMapping(value = "/loan/logout")
    public String logout(HttpServletRequest request) {

        //删除session中的key值
//        request.getSession().removeAttribute(Constants.SESSION_USER);

        //让session失效
        request.getSession().invalidate();

        return "redirect:/index";
    }

    @RequestMapping(value = "/loan/myCenter")
    public String myCenter(HttpServletRequest request, Model model) {

        //从session中获取用户的信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

        //根据用户标识获取帐户资金信息
        FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(sessionUser.getId());

        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("uid",sessionUser.getId());
        paramMap.put("currentPage",0);
        paramMap.put("pageSize",5);

        //获取最近投资记录
        List<BidInfo> bidInfoList = bidInfoService.queryBidInfoListByUid(paramMap);


        //获取最近充值记录


        //获取最近收益记录


        model.addAttribute("financeAccount",financeAccount);
        model.addAttribute("bidInfoList",bidInfoList);


        return "myCenter";
    }

}























