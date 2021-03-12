package com.star.common.entity;

import lombok.Data;
import lombok.ToString;

/**
 * 查询条件
 *
 * @Author: zzStar
 * @Date: 03-03-2021 13:56
 */
@Data
@ToString
public class QueryRequest {

    /**
     * 当前页面数据量
     */
    private int pageSize = 10;

    /**
     * 当前页码
     */
    private int pageNum = 1;

    /**
     * 排序字段
     */
    private String field;

    /**
     * 排序规则，asc升序，desc降序
     */
    private String order;

}
