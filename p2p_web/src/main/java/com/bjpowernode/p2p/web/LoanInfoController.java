package com.bjpowernode.p2p.web;

import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.BidUserTop;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.LoanInfoService;
import com.bjpowernode.p2p.service.user.FinanceAccountService;
import org.apache.log4j.ConsoleAppender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:LoanInfoController
 * Package:com.bjpowernode.p2p.web
 * Description:
 *
 * @date:2018/11/6 15:04
 * @author:guoxin@bjpowernode.com
 */
@Controller
public class LoanInfoController {

    @Autowired
    private LoanInfoService loanInfoService;

    @Autowired
    private BidInfoService bidInfoService;

    @Autowired
    private FinanceAccountService financeAccountService;

    @RequestMapping(value = "/loan/loan")
    public String loan(HttpServletRequest request, Model model,
                       @RequestParam (value = "ptype",required = false) Integer ptype,
                       @RequestParam (value = "currentPage",required = false) Integer currentPage) {

        //判断是否为第1页
        if (null == currentPage) {
            currentPage = 1;
        }

        //准备分页查询条数
        Map<String,Object> paramMap = new HashMap<String,Object>();
        int pageSize = 9;
        paramMap.put("currentPage",(currentPage-1)*pageSize);
        paramMap.put("pageSize",pageSize);
        //判断类型是否为空
        if (null != ptype) {
            paramMap.put("productType",ptype);
        }


        //分页查询产品信息列表（产品类型，页码，每页显示条数） -> 返回数据（产品列表，条数）分页模型对象PaginationVO
        PaginationVO<LoanInfo> paginationVO = loanInfoService.queryLoanInfoByPage(paramMap);

        //计算总页数
        int totalPage = paginationVO.getTotal().intValue() / pageSize;
        //再次求余
        int mod = paginationVO.getTotal().intValue() % pageSize;
        if (mod > 0) {
            totalPage = totalPage + 1;
        }

        model.addAttribute("totalRows",paginationVO.getTotal());
        model.addAttribute("totalPage",totalPage);
        model.addAttribute("loanInfoList",paginationVO.getDataList());
        model.addAttribute("currentPage",currentPage);
        if (null != ptype) {
            model.addAttribute("ptype",ptype);
        }


        //用户投资排行榜
        List<BidUserTop> bidUserTopList = bidInfoService.queryBidUserTopList();
        model.addAttribute("bidUserTopList",bidUserTopList);



        return "loan";
    }


    @RequestMapping(value = "/loan/loanInfo")
    public String loanInfo(HttpServletRequest request,Model model,
                           @RequestParam (value = "id",required = true) Integer id) {

        //根据产品标识获取产品详情
        LoanInfo loanInfo = loanInfoService.queryLoanInfoById(id);

        //根据产品标识获取产品的所有投资记录 -> 返回List<投资记录>
        List<BidInfo> bidInfoList = bidInfoService.queryBidInfoListByLoanId(id);

        model.addAttribute("loanInfo",loanInfo);
        model.addAttribute("bidInfoList",bidInfoList);

        //从session中获取用户信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

        //判断用户是否登录
        if (null != sessionUser) {

            //根据用户标识获取帐户可用余额
            FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(sessionUser.getId());
            model.addAttribute("financeAccount",financeAccount);
        }




        return "loanInfo";
    }
}
















