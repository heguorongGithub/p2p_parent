package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.mapper.loan.BidInfoMapper;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.vo.BidUserTop;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.model.vo.ResultObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * ClassName:BidInfoServiceImpl
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 *
 * @date:2018/11/6 11:06
 * @author:guoxin@bjpowernode.com
 */
@Service("bidInfoServiceImpl")
public class BidInfoServiceImpl implements BidInfoService {

    @Autowired
    private BidInfoMapper bidInfoMapper;

    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public Double queryAllBidMoney() {
        //设置redis中key的序列化方式
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        //从redis缓存中获取平台累计投资金额
        Double allBidMoney = (Double) redisTemplate.opsForValue().get(Constants.ALL_BID_MONEY);

        //判断是否有值
        if (null == allBidMoney) {
            //去数据库查询
            allBidMoney = bidInfoMapper.selectAllBidMoney();

            //存放到redis缓存中，并设置失效时间
            redisTemplate.opsForValue().set(Constants.ALL_BID_MONEY,allBidMoney,15, TimeUnit.MINUTES);

        }


        return allBidMoney;
    }

    @Override
    public List<BidInfo> queryBidInfoListByLoanId(Integer loanId) {
        return bidInfoMapper.selectBidInfoListByLoanId(loanId);
    }

    @Override
    public List<BidInfo> queryBidInfoListByUid(Map<String, Object> paramMap) {
        return bidInfoMapper.selectBidInfoByPage(paramMap);
    }

    @Override
    public PaginationVO<BidInfo> queryBidInfoByPage(Map<String, Object> paramMap) {
        PaginationVO<BidInfo> paginationVO = new PaginationVO<>();

        paginationVO.setTotal(bidInfoMapper.selectTotal(paramMap));

        paginationVO.setDataList(bidInfoMapper.selectBidInfoByPage(paramMap));


        return paginationVO;
    }

    @Override
    public ResultObject invest(Map<String, Object> paramMap) {
        ResultObject resultObject = new ResultObject();
        resultObject.setErrorCode(Constants.SUCCESS);

        //超卖现象：实际销售的数量超过库存数量
        //解决方案：数据库乐观锁机制
        Integer uid = (Integer) paramMap.get("uid");
        Integer loanId = (Integer) paramMap.get("loanId");
        Double bidMoney = (Double) paramMap.get("bidMoney");
        String phone = (String) paramMap.get("phone");

        //查询到产品当前的版本号
        LoanInfo loanInfo = loanInfoMapper.selectByPrimaryKey(loanId);
        paramMap.put("version",loanInfo.getVersion());

        //更新产品剩余可投金额
        int updateLeftProductMoneyCount = loanInfoMapper.updateLeftProductMoneyByLoanId(paramMap);

        if (updateLeftProductMoneyCount > 0) {

            //更新帐户可用余额
            int updateFinanceCount = financeAccountMapper.updateFinanceAccountByBid(paramMap);

            if (updateFinanceCount > 0) {

                //新增投资记录信息
                BidInfo bidInfo = new BidInfo();
                bidInfo.setUid(uid);
                bidInfo.setLoanId(loanId);
                bidInfo.setBidMoney(bidMoney);
                bidInfo.setBidTime(new Date());
                bidInfo.setBidStatus(1);
                int insertBidInfoCount = bidInfoMapper.insertSelective(bidInfo);

                if (insertBidInfoCount > 0) {
                    LoanInfo loanInfoDetail = loanInfoMapper.selectByPrimaryKey(loanId);

                    //判断产品剩余可投金额
                    if (0 == loanInfoDetail.getLeftProductMoney()) {

                        //更新产品的状态及满标时间
                        LoanInfo updateLoanInfo = new LoanInfo();
                        updateLoanInfo.setId(loanInfoDetail.getId());
                        updateLoanInfo.setProductStatus(1);
                        updateLoanInfo.setProductFullTime(new Date());
                        int updateLoanInfoCount = loanInfoMapper.updateByPrimaryKeySelective(updateLoanInfo);

                        if (updateLoanInfoCount <= 0) {
                            resultObject.setErrorCode(Constants.FAIL);
                        }
                    }

                    redisTemplate.opsForZSet().incrementScore(Constants.INVEST_TOP,phone,bidMoney);



                } else {
                    resultObject.setErrorCode(Constants.FAIL);
                }


            } else {
                resultObject.setErrorCode(Constants.FAIL);
            }

        } else {
            resultObject.setErrorCode(Constants.FAIL);
        }



        return resultObject;
    }


    @Override
    public List<BidUserTop> queryBidUserTopList() {
        List<BidUserTop> bidUserTopList = new ArrayList<BidUserTop>();

        Set<ZSetOperations.TypedTuple<Object>> typedTuples = redisTemplate.opsForZSet().reverseRangeWithScores(Constants.INVEST_TOP, 0, 9);

        Iterator<ZSetOperations.TypedTuple<Object>> iterator = typedTuples.iterator();

        while (iterator.hasNext()) {
            ZSetOperations.TypedTuple<Object> next = iterator.next();
            String phone = (String) next.getValue();
            Double score = next.getScore();

            BidUserTop bidUserTop = new BidUserTop();
            bidUserTop.setPhone(phone);
            bidUserTop.setScore(score);

            bidUserTopList.add(bidUserTop);
        }


        return bidUserTopList;
    }
}



















