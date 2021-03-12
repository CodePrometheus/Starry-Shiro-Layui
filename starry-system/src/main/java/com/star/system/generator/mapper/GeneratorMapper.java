package com.star.system.generator.mapper;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.star.system.generator.entity.Column;
import com.star.system.generator.entity.Table;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: zzStar
 * @Date: 03-09-2021 09:53
 */
public interface GeneratorMapper {

    /**
     * 获取数据库列表
     *
     * @param databaseType
     * @return
     */
    List<String> getDatabases(@Param("databaseType") String databaseType);

    /**
     * 获取数据表
     *
     * @param page
     * @param tableName
     * @param databaseType
     * @param schemaName
     * @param <T>
     * @return
     */
    <T> IPage<Table> getTables(Page<T> page, @Param("tableName") String tableName, @Param("databaseType") String databaseType, @Param("schemaName") String schemaName);

    /**
     * 获取数据表列属性
     *
     * @param databaseType
     * @param schemaName
     * @param tableName
     * @return
     */
    List<Column> getColumns(@Param("databaseType") String databaseType, @Param("schemaName") String schemaName, @Param("tableName") String tableName);
}
