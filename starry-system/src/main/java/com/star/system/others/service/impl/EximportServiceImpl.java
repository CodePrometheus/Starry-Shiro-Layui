package com.star.system.others.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.common.entity.QueryRequest;
import com.star.system.utils.StarryProperties;
import com.star.system.others.entity.Eximport;
import com.star.system.others.mapper.EximportMapper;
import com.star.system.others.service.IEximportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: zzStar
 * @Date: 03-12-2021 10:16
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class EximportServiceImpl extends ServiceImpl<EximportMapper, Eximport> implements IEximportService {

    private final StarryProperties properties;

    @Override
    public IPage<Eximport> findEximports(QueryRequest request, Eximport eximport) {
        Page<Eximport> page = new Page<>(request.getPageNum(), request.getPageSize());
        return page(page, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchInsert(List<Eximport> list) {
        saveBatch(list, properties.getMaxBatchInsertNum());
    }

}
