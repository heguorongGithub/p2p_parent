package com.bjpowernode.p2p.service.user;

import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.mapper.user.UserMapper;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.ResultObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * ClassName:UserServiceImpl
 * Package:com.bjpowernode.p2p.service.user
 * Description:
 *
 * @date:2018/11/6 10:42
 * @author:guoxin@bjpowernode.com
 */
@Service("userServiceImpl")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public Long queryAllUserCount() {

        //首先去redis缓存中查询，有：直接使用，没有：去数据库查询并存放到Redis中
        //好处：减少对数据库的访问，提升系统的性能

        //获取redis中平台注册总人数
        BoundValueOperations<String, Object> boundValueOperations = redisTemplate.boundValueOps(Constants.ALL_USER_COUNT);
        Long allUserCount = (Long) boundValueOperations.get();

        //判断是否有值
        if (null == allUserCount) {
            //去数据库查询
            allUserCount = userMapper.selectAllUserCount();

            //将该值存放到redis缓存中
            boundValueOperations.set(allUserCount);

            //设置失效时间
            boundValueOperations.expire(15, TimeUnit.SECONDS);

        }



        return allUserCount;
    }

    @Override
    public User queryUserByPhone(String phone) {
        return userMapper.selectUserByPhone(phone);
    }

    @Override
    public ResultObject register(String phone, String loginPassword) {
        ResultObject resultObject = new ResultObject();
        resultObject.setErrorCode(Constants.SUCCESS);

        //创建User对象
        User user = new User();

        user.setPhone(phone);
        user.setLoginPassword(loginPassword);
        user.setAddTime(new Date());
        user.setLastLoginTime(new Date());

        //新增用户
        int insertUserCount = userMapper.insertSelective(user);

        if (insertUserCount > 0) {
            User userInfo = userMapper.selectUserByPhone(phone);

            FinanceAccount financeAccount = new FinanceAccount();
            financeAccount.setUid(userInfo.getId());
            financeAccount.setAvailableMoney(888.0);
            //新增帐户
            int insertFianceAccountCount = financeAccountMapper.insertSelective(financeAccount);

            if (insertFianceAccountCount <= 0) {
                resultObject.setErrorCode(Constants.FAIL);
            }

        } else {
            resultObject.setErrorCode(Constants.FAIL);
        }




        return resultObject;
    }


    @Override
    public int modifyUserByUid(User user) {
        return userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public User login(String phone, String loginPassword) {
        //根据手机号和密码查询用户信息
        User user = userMapper.selectUserByPhoneAndLoginPassword(phone,loginPassword);

        //判断用户是否存在
        if (null != user) {

            //根据用户标识更新最近登录时间
            User updateUser = new User();
            updateUser.setId(user.getId());
            updateUser.setLastLoginTime(new Date());
            userMapper.updateByPrimaryKeySelective(updateUser);

        }

        return user;
    }
}
