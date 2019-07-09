package com.bjpowernode.p2p.model.vo;

import java.io.Serializable;

/**
 * ClassName:ResultObject
 * Package:com.bjpowernode.p2p.model.vo
 * Description:
 *
 * @date:2018/11/8 12:04
 * @author:guoxin@bjpowernode.com
 */
public class ResultObject implements Serializable {

    /**
     * SUCCESS|FAIL
     */
    private String errorCode;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
