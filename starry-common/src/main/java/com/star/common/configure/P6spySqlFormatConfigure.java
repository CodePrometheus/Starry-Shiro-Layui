package com.star.common.configure;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import com.star.common.entity.Strings;
import com.star.common.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

/**
 * 自定义 p6spy sql输出格式
 *
 * @Author: zzStar
 * @Date: 03-07-2021 14:07
 */
public class P6spySqlFormatConfigure implements MessageFormattingStrategy {

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        return StringUtils.isNotBlank(sql) ? DateUtil.formatFullTime(LocalDateTime.now(), DateUtil.FULL_TIME_SPLIT_PATTERN)
                + " | 耗时 " + elapsed + " ms | SQL 语句：" + Strings.NEWLINE + sql.replaceAll("[\\s]+", StringUtils.SPACE) +
                Strings.SEMICOLON : Strings.EMPTY;
    }
}
