package com.bjpowernode.p2p.web;

import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.LoanInfoService;
import com.bjpowernode.p2p.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:IndexController
 * Package:com.bjpowernode.p2p.web
 * Description:
 *
 * @date:2018/11/5 16:11
 * @author:guoxin@bjpowernode.com
 */
@Controller
public class IndexController {

    @Autowired
    private LoanInfoService loanInfoService;

    @Autowired
    private UserService userService;

    @Autowired
    private BidInfoService bidInfoService;

    @RequestMapping(value = "/index")
    public String index(HttpServletRequest request, Model model) {

        //获取历史平均年化收益率
        Double historyAverageRate = loanInfoService.queryHistoryAverageRate();
        model.addAttribute(Constants.HISTORY_AVERAGE_RATE,historyAverageRate);

        //获取平台注册总人数
        Long allUserCount = userService.queryAllUserCount();
        model.addAttribute(Constants.ALL_USER_COUNT,allUserCount);


        //获取平台累计投资金额
        Double allBidMoney = bidInfoService.queryAllBidMoney();
        model.addAttribute(Constants.ALL_BID_MONEY,allBidMoney);

        //以下查询看成是一个分页，但实际不是一个分页查询功能
        //因为我们使用limit函数，limit 起始下标,截取长度
        //（页码-1）*pageSize
        //产品业务接口层提供一个方法：根据产品类型获取产品信息列表(页码，每页显示条数,产品类型) -> 返回List<产品>

        //准备查询的参数
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("currentPage",0);


        //获取新手宝产品，显示第1页，每页显示1个，产品类型0
        paramMap.put("pageSize",1);
        paramMap.put("productType",Constants.PRODUCT_TYPE_X);
        List<LoanInfo> xLoanInfoList = loanInfoService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute("xLoanInfoList",xLoanInfoList);


        //获取优选产品，显示第1页，每页显示4个，产品类型1
        paramMap.put("pageSize",4);
        paramMap.put("productType",Constants.PRODUCT_TYPE_U);
        List<LoanInfo> uLoanInfoList = loanInfoService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute("uLoanInfoList",uLoanInfoList);


        //获取散标产品，显示第1页，每页显示8个，产品类型2
        paramMap.put("pageSize",8);
        paramMap.put("productType",Constants.PRODUCT_TYPE_S);
        List<LoanInfo> sLoanInfoList = loanInfoService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute("sLoanInfoList",sLoanInfoList);


        return "index";
    }
}
