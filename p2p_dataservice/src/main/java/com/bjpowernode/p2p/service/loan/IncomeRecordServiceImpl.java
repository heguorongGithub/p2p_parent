package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.common.util.DateUtils;
import com.bjpowernode.p2p.mapper.loan.BidInfoMapper;
import com.bjpowernode.p2p.mapper.loan.IncomeRecordMapper;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.IncomeRecord;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.mysql.jdbc.UpdatableResultSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:IncomeRecordServiceImpl
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 *
 * @date:2018/11/10 14:47
 * @author:guoxin@bjpowernode.com
 */
@Service("incomeRecordServiceImpl")
public class IncomeRecordServiceImpl implements IncomeRecordService {

    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Autowired
    private BidInfoMapper bidInfoMapper;

    @Autowired
    private IncomeRecordMapper incomeRecordMapper;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Override
    public void generateIncomePlan() {

        //获取已满标的产品 -> 返回List<已满标产品>
        List<LoanInfo> loanInfoList = loanInfoMapper.selectLoanInfoByProductStatus(1);

        //循环遍历
        for (LoanInfo loanInfo:loanInfoList) {

            //获取到每一个产品，获取当前产品的所有投资记录 -> 返回List<投资记录>
            List<BidInfo> bidInfoList = bidInfoMapper.selectBidInfoesByLoanId(loanInfo.getId());

            //循环遍历List<投资记录>
            for (BidInfo bidInfo:bidInfoList) {

                //获取到每一条投资记录，将当前的投资记录生成对应的收益记录
                IncomeRecord incomeRecord = new IncomeRecord();
                incomeRecord.setUid(bidInfo.getUid());
                incomeRecord.setLoanId(loanInfo.getId());
                incomeRecord.setBidId(bidInfo.getId());
                incomeRecord.setBidMoney(bidInfo.getBidMoney());
                incomeRecord.setIncomeStatus(0);

                //收益时间(Date) = 满标时间(Date) + 产品周期(int)
                Date incomeDate = null;

                //收益金额 = 投资金额 * 天利率 * 投资天数
                double incomeMoney = 0;


                if (Constants.PRODUCT_TYPE_X == loanInfo.getProductType()) {
                    //新手宝
                    incomeDate = DateUtils.getDateByAddDays(loanInfo.getProductFullTime(),loanInfo.getCycle());
                    incomeMoney = bidInfo.getBidMoney() * (loanInfo.getRate() / 100 / 365) * loanInfo.getCycle();
                } else {
                    //优选和散标
                    incomeDate = DateUtils.getDateByAddMonths(loanInfo.getProductFullTime(),loanInfo.getCycle());
                    incomeMoney = bidInfo.getBidMoney() * (loanInfo.getRate() / 100 / 365) * loanInfo.getCycle() * 30;
                }

                incomeMoney = Math.round(incomeMoney * Math.pow(10,2))/Math.pow(10,2);

                incomeRecord.setIncomeDate(incomeDate);
                incomeRecord.setIncomeMoney(incomeMoney);


                int insertIncomeCount = incomeRecordMapper.insertSelective(incomeRecord);
            }


            //将当前产品的状态更新为2满标且生成收益计划
            LoanInfo updateLoanInfo = new LoanInfo();
            updateLoanInfo.setId(loanInfo.getId());
            updateLoanInfo.setProductStatus(2);
            loanInfoMapper.updateByPrimaryKeySelective(updateLoanInfo);
        }
    }


    @Override
    public void generateIncomeBack() {

        //查询收益时间与当前时间相等且收益状态为0的收益记录 -> 返回List<收益记录>
        List<IncomeRecord> incomeRecordList = incomeRecordMapper.selectIncomeRecordByIncomeDateAndIncomeStatus(0);

        //循环遍历
        for (IncomeRecord incomeRecord:incomeRecordList) {

            //准备参数
            Map<String,Object> paramMap = new HashMap<String,Object>();
            paramMap.put("uid",incomeRecord.getUid());
            paramMap.put("incomeMoney",incomeRecord.getIncomeMoney());
            paramMap.put("bidMoney",incomeRecord.getBidMoney());

            //将收益及本金返还对应用户的帐户
            int udpateFinanceCount = financeAccountMapper.updateFinanceAccountByIncomeBack(paramMap);

            if (udpateFinanceCount > 0){

                //将当前收益记录的状态更新为1收益已返还
                IncomeRecord updateIncomeRecord = new IncomeRecord();
                updateIncomeRecord.setId(incomeRecord.getId());
                updateIncomeRecord.setIncomeStatus(1);

                incomeRecordMapper.updateByPrimaryKeySelective(updateIncomeRecord);
            }

        }



    }
}





















