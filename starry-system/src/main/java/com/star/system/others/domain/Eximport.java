package com.star.system.others.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wuwenze.poi.annotation.Excel;
import com.wuwenze.poi.annotation.ExcelField;
import com.wuwenze.poi.validator.EmailValidator;
import lombok.Data;

import java.util.Date;

/**
 * @Author: zzStar
 * @Date: 03-12-2021 10:09
 */
@Data
@TableName("t_eximport")
@Excel("测试导入导出数据")
public class Eximport {

    /**
     * 字段1
     */
    @ExcelField(value = "字段1", required = true, maxLength = 20,
            comment = "提示：必填，长度不能超过20个字符")
    private String field1;

    /**
     * 字段2
     */
    @ExcelField(value = "字段2", required = true, maxLength = 11, regularExp = "[0-9]+",
            regularExpMessage = "必须是数字", comment = "提示: 必填，只能填写数字，并且长度不能超过11位")
    private Integer field2;

    /**
     * 字段3
     */
    @ExcelField(value = "字段3", required = true, maxLength = 50,
            comment = "提示：必填，只能填写邮箱，长度不能超过50个字符", validator = EmailValidator.class)
    private String field3;

    /**
     * 创建时间
     */
    private Date createTime;

}
