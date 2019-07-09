package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.vo.BidUserTop;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.model.vo.ResultObject;

import java.util.List;
import java.util.Map;

/**
 * ClassName:BidInfoService
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 *
 * @date:2018/11/6 11:05
 * @author:guoxin@bjpowernode.com
 */
public interface BidInfoService {

    /**
     * 获取平台累计投资金额
     * @return
     */
    Double queryAllBidMoney();

    /**
     * 根据产品标识获取投资记录（包含用户信息）
     * @param loanId
     * @return
     */
    List<BidInfo> queryBidInfoListByLoanId(Integer loanId);

    /**
     * 根据用户标识获取最近投资记录（包含产品信息）
     * @param paramMap
     * @return
     */
    List<BidInfo> queryBidInfoListByUid(Map<String, Object> paramMap);

    /**
     * 根据用户标识分页查询投资列表
     * @param paramMap
     * @return
     */
    PaginationVO<BidInfo> queryBidInfoByPage(Map<String, Object> paramMap);


    /**
     * 用户投资
     * @param paramMap
     * @return
     */
    ResultObject invest(Map<String, Object> paramMap);

    /**
     * 获取用户投资排行榜
     * @return
     */
    List<BidUserTop> queryBidUserTopList();
}
