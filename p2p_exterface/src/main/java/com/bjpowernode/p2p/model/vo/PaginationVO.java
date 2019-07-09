package com.bjpowernode.p2p.model.vo;

import java.io.Serializable;
import java.util.List;

/**
 * ClassName:PaginationVO
 * Package:com.bjpowernode.p2p.model.vo
 * Description:
 *
 * @date:2018/11/6 15:15
 * @author:guoxin@bjpowernode.com
 */
public class PaginationVO<T> implements Serializable {

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 数据
     */
    private List<T> dataList;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }
}
