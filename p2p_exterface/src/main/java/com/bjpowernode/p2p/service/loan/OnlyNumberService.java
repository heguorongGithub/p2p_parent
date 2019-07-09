package com.bjpowernode.p2p.service.loan;

/**
 * ClassName:OnlyNumberService
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 *
 * @date:2018/11/12 12:27
 * @author:guoxin@bjpowernode.com
 */
public interface OnlyNumberService {

    /**
     * 获取redis中全局唯一数字
     * @return
     */
    Long getOnlyNumber();
}
