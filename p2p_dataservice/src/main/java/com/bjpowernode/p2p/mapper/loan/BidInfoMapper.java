package com.bjpowernode.p2p.mapper.loan;

import com.bjpowernode.p2p.model.loan.BidInfo;

import java.util.List;
import java.util.Map;

public interface BidInfoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_bid_info
     *
     * @mbggenerated Sat Nov 03 16:24:05 CST 2018
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_bid_info
     *
     * @mbggenerated Sat Nov 03 16:24:05 CST 2018
     */
    int insert(BidInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_bid_info
     *
     * @mbggenerated Sat Nov 03 16:24:05 CST 2018
     */
    int insertSelective(BidInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_bid_info
     *
     * @mbggenerated Sat Nov 03 16:24:05 CST 2018
     */
    BidInfo selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_bid_info
     *
     * @mbggenerated Sat Nov 03 16:24:05 CST 2018
     */
    int updateByPrimaryKeySelective(BidInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_bid_info
     *
     * @mbggenerated Sat Nov 03 16:24:05 CST 2018
     */
    int updateByPrimaryKey(BidInfo record);

    /**
     * 获取平台累计投资金额
     * @return
     */
    Double selectAllBidMoney();

    /**
     * 根据产品标识获取投资记录（包含用户信息）
     * @param loanId
     * @return
     */
    List<BidInfo> selectBidInfoListByLoanId(Integer loanId);

    /**
     * 根据用户标识分页查询投资记录
     * @param paramMap
     * @return
     */
    List<BidInfo> selectBidInfoByPage(Map<String, Object> paramMap);

    /**
     * 根据用户标识查询投资记录总数
     * @param paramMap
     * @return
     */
    Long selectTotal(Map<String, Object> paramMap);

    /**
     * 根据产品标识获取投资记录
     * @param loanId
     * @return
     */
    List<BidInfo> selectBidInfoesByLoanId(Integer loanId);
}