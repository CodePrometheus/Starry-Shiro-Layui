package com.star.system.framework.controller;


import com.star.common.annotation.OperationLog;
import com.star.common.entity.AjaxResult;
import com.star.common.exception.StarryException;
import com.star.system.framework.entity.Menu;
import com.star.system.framework.service.IMenuService;
import com.wuwenze.poi.ExcelKit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;


/**
 * @Author: zzStar
 * @Date: 03-05-2021 20:31
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("menu")
public class MenuController extends BaseController {

    private final IMenuService menuService;

    @GetMapping("{username}")
    public AjaxResult getUserMenus(@NotBlank(message = "{required}") @PathVariable String username) throws StarryException {
        if (!StringUtils.equalsIgnoreCase(username, getCurrentUser().getUsername())) {
            throw new StarryException("您无权获取别人的菜单");
        }
        return new AjaxResult().data(menuService.findUserMenus(username));
    }

    @GetMapping("tree")
    @OperationLog(exceptionMessage = "获取菜单树失败")
    public AjaxResult getMenuTree(Menu menu) {
        return new AjaxResult().success()
                .data(menuService.findMenus(menu).getChilds());
    }

    @PostMapping
    @RequiresPermissions("menu:add")
    @OperationLog(operation = "新增菜单/按钮", exceptionMessage = "新增菜单/按钮失败")
    public AjaxResult addMenu(@Valid Menu menu) {
        menuService.createMenu(menu);
        return new AjaxResult().success();
    }

    @GetMapping("delete/{menuIds}")
    @RequiresPermissions("menu:delete")
    @OperationLog(operation = "删除菜单/按钮", exceptionMessage = "删除菜单/按钮失败")
    public AjaxResult deleteMenus(@NotBlank(message = "{required}") @PathVariable String menuIds) {
        menuService.deleteMenus(menuIds);
        return new AjaxResult().success();
    }

    @PostMapping("update")
    @RequiresPermissions("menu:update")
    @OperationLog(operation = "修改菜单/按钮", exceptionMessage = "修改菜单/按钮失败")
    public AjaxResult updateMenu(@Valid Menu menu) {
        menuService.updateMenu(menu);
        return new AjaxResult().success();
    }

    @GetMapping("excel")
    @RequiresPermissions("menu:export")
    @OperationLog(exceptionMessage = "导出Excel失败")
    public void export(Menu menu, HttpServletResponse response) {
        ExcelKit.$Export(Menu.class, response).downXlsx(menuService.findMenuList(menu), false);
    }
}
