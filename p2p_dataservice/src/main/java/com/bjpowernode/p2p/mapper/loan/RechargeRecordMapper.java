package com.bjpowernode.p2p.mapper.loan;

import com.bjpowernode.p2p.model.loan.RechargeRecord;

public interface RechargeRecordMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_recharge_record
     *
     * @mbggenerated Sat Nov 03 16:24:05 CST 2018
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_recharge_record
     *
     * @mbggenerated Sat Nov 03 16:24:05 CST 2018
     */
    int insert(RechargeRecord record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_recharge_record
     *
     * @mbggenerated Sat Nov 03 16:24:05 CST 2018
     */
    int insertSelective(RechargeRecord record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_recharge_record
     *
     * @mbggenerated Sat Nov 03 16:24:05 CST 2018
     */
    RechargeRecord selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_recharge_record
     *
     * @mbggenerated Sat Nov 03 16:24:05 CST 2018
     */
    int updateByPrimaryKeySelective(RechargeRecord record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_recharge_record
     *
     * @mbggenerated Sat Nov 03 16:24:05 CST 2018
     */
    int updateByPrimaryKey(RechargeRecord record);

    /**
     * 根据充值订单号更新充值记录
     * @param rechargeRecord
     * @return
     */
    int updateRechargeRecordByRechargeNo(RechargeRecord rechargeRecord);
}