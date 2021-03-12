package com.star.system.others.rest;

import com.star.system.framework.rest.BaseController;
import com.star.common.entity.AjaxResult;
import com.star.common.entity.QueryRequest;
import com.star.system.others.domain.DataPermissionTest;
import com.star.system.others.service.IDataPermissionTestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: zzStar
 * @Date: 03-12-2021 10:18
 */
@Slf4j
@RestController
@RequestMapping("datapermission/test")
@RequiredArgsConstructor
public class DataPermissionTestController extends BaseController {

    private final IDataPermissionTestService dataPermissionTestService;

    @GetMapping("list")
    @RequiresPermissions("others:datapermission")
    public AjaxResult dataPermissionTestList(QueryRequest request, DataPermissionTest dataPermissionTest) {
        return new AjaxResult().success()
                .data(getDataTable(dataPermissionTestService.findDataPermissionTests(request, dataPermissionTest)));
    }
}
