package com.bjpowernode.p2p.service.user;

import com.bjpowernode.p2p.model.user.FinanceAccount;

/**
 * ClassName:FinanceAccountService
 * Package:com.bjpowernode.p2p.service.user
 * Description:
 *
 * @date:2018/11/8 12:24
 * @author:guoxin@bjpowernode.com
 */
public interface FinanceAccountService {

    /**
     * 根据用户标识获取帐户信息
     * @param uid
     * @return
     */
    FinanceAccount queryFinanceAccountByUid(Integer uid);
}
