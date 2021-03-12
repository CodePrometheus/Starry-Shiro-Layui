package com.star.system.others.controller;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.star.common.annotation.OperationLog;
import com.star.system.framework.controller.BaseController;
import com.star.common.entity.AjaxResult;
import com.star.common.entity.QueryRequest;
import com.star.common.exception.StarryException;
import com.star.system.others.entity.Eximport;
import com.star.system.others.service.IEximportService;
import com.wuwenze.poi.ExcelKit;
import com.wuwenze.poi.handler.ExcelReadHandler;
import com.wuwenze.poi.pojo.ExcelErrorField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * @Author: zzStar
 * @Date: 03-12-2021 10:19
 */
@Slf4j
@RestController
@RequestMapping("eximport")
@RequiredArgsConstructor
public class EximportController extends BaseController {

    private final IEximportService eximportService;

    @GetMapping
    @RequiresPermissions("others:eximport:view")
    public AjaxResult findEximports(QueryRequest request) {
        return new AjaxResult().success()
                .data(getDataTable(eximportService.findEximports(request, null)));
    }

    /**
     * 生成 Excel导入模板
     */
    @GetMapping("template")
    @RequiresPermissions("eximport:template")
    public void generateImportTemplate(HttpServletResponse response) {
        // 构建数据
        List<Eximport> list = new ArrayList<>();
        IntStream.range(0, 20).forEach(i -> {
            Eximport eximport = new Eximport();
            eximport.setField1("字段1");
            eximport.setField2(i + 1);
            eximport.setField3("star" + i + "@gmail.com");
            list.add(eximport);
        });
        // 构建模板
        ExcelKit.$Export(Eximport.class, response).downXlsx(list, true);
    }

    /**
     * 导入Excel数据，并批量插入 T_EXIMPORT表
     */
    @PostMapping("import")
    @RequiresPermissions("eximport:import")
    @OperationLog(exceptionMessage = "导入Excel数据失败")
    public AjaxResult importExcels(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new StarryException("导入数据为空");
        }
        String filename = file.getOriginalFilename();
        if (!StringUtils.endsWith(filename, ".xlsx")) {
            throw new StarryException("只支持.xlsx类型文件导入");
        }
        // 开始导入操作
        Stopwatch stopwatch = Stopwatch.createStarted();
        final List<Eximport> data = Lists.newArrayList();
        final List<Map<String, Object>> error = Lists.newArrayList();
        ExcelKit.$Import(Eximport.class).readXlsx(file.getInputStream(), new ExcelReadHandler<Eximport>() {
            @Override
            public void onSuccess(int sheet, int row, Eximport eximport) {
                // 数据校验成功时，加入集合
                eximport.setCreateTime(new Date());
                data.add(eximport);
            }

            @Override
            public void onError(int sheet, int row, List<ExcelErrorField> errorFields) {
                // 数据校验失败时，记录到 error集合
                error.add(ImmutableMap.of("row", row, "errorFields", errorFields));
            }
        });
        if (CollectionUtils.isNotEmpty(data)) {
            // 将合法的记录批量入库
            eximportService.batchInsert(data);
        }
        ImmutableMap<String, Object> result = ImmutableMap.of(
                "time", stopwatch.stop().toString(),
                "data", data,
                "error", error
        );
        return new AjaxResult().success().data(result);
    }

    @GetMapping("excel")
    @RequiresPermissions("eximport:export")
    @OperationLog(exceptionMessage = "导出Excel失败")
    public void export(QueryRequest queryRequest, Eximport eximport, HttpServletResponse response) {
        ExcelKit.$Export(Eximport.class, response)
                .downXlsx(eximportService.findEximports(queryRequest, eximport).getRecords(), false);
    }
}
