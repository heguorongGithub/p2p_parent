package com.bjpowernode.p2p.service.user;

import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.ResultObject;

/**
 * ClassName:UserService
 * Package:com.bjpowernode.p2p.service.user
 * Description:
 *
 * @date:2018/11/6 10:40
 * @author:guoxin@bjpowernode.com
 */
public interface UserService {

    /**
     * 获取平台注册总人数
     * @return
     */
    Long queryAllUserCount();

    /**
     * 根据手机号码查询用户信息
     * @param phone
     * @return
     */
    User queryUserByPhone(String phone);

    /**
     * 用户注册
     * @param phone
     * @param loginPassword
     * @return
     */
    ResultObject register(String phone, String loginPassword);

    /**
     * 根据用户标识更新用户信息
     * @param user
     * @return
     */
    int modifyUserByUid(User user);

    /**
     * 用户登录
     * @param phone
     * @param loginPassword
     * @return
     */
    User login(String phone, String loginPassword);
}
