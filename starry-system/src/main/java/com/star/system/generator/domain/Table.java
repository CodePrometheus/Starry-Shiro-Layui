package com.star.system.generator.domain;

import lombok.Data;


/**
 * @Author: zzStar
 * @Date: 03-08-2021 20:33
 */
@Data
public class Table {
    /**
     * 名称
     */
    private String name;
    /**
     * 备注
     */
    private String remark;
    /**
     * 数据量（行）
     */
    private Long dataRows;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 修改时间
     */
    private String updateTime;
}
