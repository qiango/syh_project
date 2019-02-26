package com.syhdoctor.webserver.controller.webadmin.sys;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.sys.SysService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description = "/sys 权限接口")
@RestController
@RequestMapping(value = "/sys")
public class SysController extends BaseController {

    @Autowired
    private SysService sysService;

    /**
     * 后台登陆
     *
     * @param params 请求的参数
     * @return Map
     */
    @ApiOperation(value = "登录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "account", value = "账号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "password", value = "密码", required = true, dataType = "String")

    })
    @PostMapping("/login")
    @ResponseBody
    public Map<String, Object> login(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String account = ModelUtil.getStr(params, "account");
        String password = ModelUtil.getStr(params, "password");
        if (StrUtil.isEmpty(account, password)) {
            setErrorResult(result, "请检查参数!");
        } else {
            Map<String, Object> temp = sysService.login(account, password);
            if (temp == null) {
                setErrorResult(result, "账号密码错误");
            } else if (ModelUtil.getInt(ModelUtil.getMap(temp, "user"), "islock", 1) == 1) {
                setErrorResult(result, "用户处于锁定状态，不可登录，请联系管理员进行解锁。");
            } else {
                result.put("data", temp);
                setOkResult(result);
            }
        }
        return result;
    }

    /**
     * 权限列表
     *
     * @param params 请求的参数
     * @return java.com.syhdoctor.webserver.api.util.Map
     */
    @ApiOperation(value = "权限列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = "权限名称", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pname", value = "父类权限名称", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "type", value = "权限类型", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "当前页", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页数据量", dataType = "String")
    })
    @PostMapping("/permission/list")
    @ResponseBody
    public Map<String, Object> permissionList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String name = ModelUtil.getStr(params, "name");
        String pname = ModelUtil.getStr(params, "pname");
        int type = ModelUtil.getInt(params, "type");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);

        result.put("total", sysService.permissionCount(name));
        result.put("data", sysService.permissionList(name, pname, type, pageIndex, pageSize));
        setOkResult(result);
        return result;
    }


    /**
     * 权限详情
     *
     * @param params 请求的参数
     * @return java.com.syhdoctor.webserver.api.util.Map
     */
    @ApiOperation(value = "权限详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "权限id不能为空", required = true, dataType = "String")
    })
    @PostMapping("/permission/getPermissionDetail")
    @ResponseBody
    public Map<String, Object> getPermissionDetail(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();

        long id = ModelUtil.getLong(params, "id", 0);

        if (id == 0) {
            setErrorResult(result, "权限id不能为空");
        } else {
            result.put("data", sysService.getPermissionDetail(id));
            setOkResult(result);
        }
        return result;
    }

    /**
     * 权限列表供Select选择
     *
     * @param params 请求的参数
     * @return java.com.syhdoctor.webserver.api.util.Map
     */
    @ApiOperation(value = "权限列表供Select选择")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = "权限名称", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "type", value = "权限类型", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "当前页", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页数据量", dataType = "String")

    })
    @PostMapping("/permission/getPermissions")
    @ResponseBody
    public Map<String, Object> getPermissions(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String name = ModelUtil.getStr(params, "name");
        int type = ModelUtil.getInt(params, "type", 1);
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", sysService.getPermissions(name, type, pageIndex, pageSize));
        setOkResult(result);
        return result;
    }

    /**
     * 权限新增或者更新
     *
     * @param params 请求的参数
     * @return java.com.syhdoctor.webserver.api.util.Map
     */
    @ApiOperation(value = "权限新增或者更新")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "权限id存在就是编辑，否则就是新增", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "name", value = "权限名称", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pid", value = "父类权限", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "imagename", value = "图片名称", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "type", value = "权限类型", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "url", value = "权限值", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "descritpion", value = "权限描述", dataType = "String")
    })
    @PostMapping("/permission/addOrUpdate")
    @ResponseBody
    public Map<String, Object> addUpdatePermission(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id", 0);
        String name = ModelUtil.getStr(params, "name");
        String imagename = ModelUtil.getStr(params, "imagename");
        long pid = ModelUtil.getLong(params, "pid", 0);
        String descritpion = ModelUtil.getStr(params, "descritpion");
        String url = ModelUtil.getStr(params, "url");
        int type = ModelUtil.getInt(params, "type", 1);

        if (!StrUtil.isEmpty(name)) {
            result.put("data", sysService.addUpdatePermission(id, name, pid, descritpion, url, type,imagename));
            setOkResult(result);
        } else {
            setErrorResult(result, "权限名不能为空！");
        }
        return result;
    }

    /**
     * 权限删除
     *
     * @param params 请求的参数
     * @return java.com.syhdoctor.webserver.api.util.Map
     */
    @ApiOperation(value = "权限删除")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "权限id不能为空", required = true, dataType = "String")
    })
    @PostMapping("/permission/del")
    @ResponseBody
    public Map<String, Object> delPermission(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id", 0);

        if (id == 0) {
            setErrorResult(result, "权限id不能为空！");
        } else {
            result.put("data", sysService.delPermission(id));
            setOkResult(result);
        }

        return result;
    }

    /**
     * 角色列表
     *
     * @param params 请求的参数
     * @return java.com.syhdoctor.webserver.api.util.Map
     */
    @ApiOperation(value = "角色列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = "角色名称", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "当前页", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页数据量", dataType = "String")

    })
    @PostMapping("/role/getRoleList")
    @ResponseBody
    public Map<String, Object> getRoleList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String name = ModelUtil.getStr(params, "name");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("total", sysService.getRoleListCount(name));
        result.put("data", sysService.getRoleList(name, pageIndex, pageSize));
        setOkResult(result);
        return result;
    }

    /**
     * 角色详情
     *
     * @param params 请求的参数
     * @return java.com.syhdoctor.webserver.api.util.Map
     */
    @ApiOperation(value = "角色详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "角色ID", required = true, dataType = "String")
    })
    @PostMapping("/role/getRole")
    @ResponseBody
    public Map<String, Object> getRole(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id", 0);

        if (id == 0) {
            setErrorResult(result, "角色id不能为空");
        } else {

            result.put("data", sysService.getRole(id));

            setOkResult(result);
        }

        return result;
    }

    /**
     * 角色新增或者更新
     *
     * @param params 请求的参数
     * @return java.com.syhdoctor.webserver.api.util.Map
     */
    @ApiOperation(value = "角色新增或者更新")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "账户id存在就是编辑，否则就是新增", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "name", value = "角色名称", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagePermission", value = "角色所属页面权限id列表", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "featuresPermission", value = "角色所属功能权限id列表", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "interfacePermission", value = "角色所属接口权限id列表", dataType = "String")
    })
    @PostMapping("/role/addOrUpdate")
    @ResponseBody
    public Map<String, Object> addOrUpdate(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id", 0);
        String name = ModelUtil.getStr(params, "name");
        List<?> pagePermission = ModelUtil.getList(params, "pagePermission", new ArrayList<>());
        List<?> featuresPermission = ModelUtil.getList(params, "featuresPermission", new ArrayList<>());
        List<?> interfacePermission = ModelUtil.getList(params, "interfacePermission", new ArrayList<>());

        if (StrUtil.isEmpty(name)) {
            setErrorResult(result, "角色名称不能为空！");
        } else {
            result.put("data", sysService.addOrUpdataRole(id, name, pagePermission, featuresPermission, interfacePermission));
            setOkResult(result);
        }

        return result;
    }

    /**
     * 角色删除
     *
     * @param params 请求的参数
     * @return java.com.syhdoctor.webserver.api.util.Map
     */
    @ApiOperation(value = "角色删除")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "角色ID", required = true, dataType = "String")
    })
    @PostMapping("/role/delRole")
    @ResponseBody
    public Map<String, Object> delRole(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id", 0);

        if (id == 0) {
            setErrorResult(result, "角色id不能为空！");
        } else {
            result.put("data", sysService.delRole(id));
            setOkResult(result);
        }

        return result;
    }


    /**
     * 获取角色的权限
     *
     * @param params 请求的参数
     * @return java.com.syhdoctor.webserver.api.util.Map
     */
    @ApiOperation(value = "获取角色的权限")
    @ApiImplicitParams({
    })
    @PostMapping("/role/rolePermissinon")
    @ResponseBody
    public Map<String, Object> getRolePermissinon(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", sysService.getRolePermissinon(0l));
        setOkResult(result);
        return result;
    }

    /**
     * 账户列表
     *
     * @param params 请求的参数
     * @return java.com.syhdoctor.webserver.api.util.Map
     */
    @ApiOperation(value = "账户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "account", value = "账号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "name", value = "用户名", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "页码", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页数量", defaultValue = "20", dataType = "String")
    })
    @PostMapping("/user/list")
    @ResponseBody
    public Map<String, Object> userList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String account = ModelUtil.getStr(params, "account");
        String name = ModelUtil.getStr(params, "name");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);

        result.put("total", sysService.userCount(account, name));
        result.put("data", sysService.userList(account, name, pageIndex, pageSize));
        setOkResult(result);
        return result;
    }

    /**
     * 账户详情
     *
     * @param params 请求的参数
     * @return java.com.syhdoctor.webserver.api.util.Map
     */
    @ApiOperation(value = "账户详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "账号ID", required = true, dataType = "String")
    })
    @PostMapping("/user/detail")
    @ResponseBody
    public Map<String, Object> getUserDetail(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();

        long id = ModelUtil.getLong(params, "id", 0);

        if (id == 0) {
            setErrorResult(result, "用户id不能为空");
        } else {
            result.put("data", sysService.getUserDetail(id));
            setOkResult(result);
        }
        return result;
    }

    /**
     * 账户的角色列表（供Select选择）
     *
     * @param params 请求的参数
     * @return java.com.syhdoctor.webserver.api.util.Map
     */
    @ApiOperation(value = "账户的角色列表（供Select选择）")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = "角色名称", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "当前页", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页数据量", dataType = "String")

    })
    @PostMapping("/user/getRoles")
    @ResponseBody
    public Map<String, Object> getRoles(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String name = ModelUtil.getStr(params, "name");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", sysService.getRoles(name, pageIndex, pageSize));
        setOkResult(result);
        return result;
    }

    /**
     * 账户新增或者更新
     *
     * @param params 请求的参数
     * @return java.com.syhdoctor.webserver.api.util.Map
     */
    @ApiOperation(value = "账户新增或者更新")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "账户id存在就是编辑，否则就是新增", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "account", value = "账户账号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "username", value = "账户名称", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "imagename", value = "图片名称", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "password", value = "账户密码", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "lock", value = "账户是否禁用", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "roles", value = "账户所属角色id列表", dataType = "String")
    })
    @PostMapping("/user/addOrUpdate")
    @ResponseBody
    public Map<String, Object> addOrUpdateUser(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id", 0);
        String account = ModelUtil.getStr(params, "account");
        String username = ModelUtil.getStr(params, "username");
        String imagename = ModelUtil.getStr(params, "imagename");
        String pwd = ModelUtil.getStr(params, "password");
        int islock = ModelUtil.getInt(params, "lock", 0);
        List<?> roles = ModelUtil.getList(params, "roles", new ArrayList<>());

        if (StrUtil.isEmpty(account)) {
            setErrorResult(result, "用户账号不能为空！");
        } else if (id == 0 && StrUtil.isEmpty(pwd)) {
            setErrorResult(result, "用户密码不能为空！");
        } else {
            result.put("data", sysService.addUpdateUser(id, account, username, pwd, islock, roles,imagename));
            setOkResult(result);
        }
        return result;
    }

    /**
     * 账户更新密码
     *
     * @param params 请求的参数
     * @return java.com.syhdoctor.webserver.api.util.Map
     */
    @ApiOperation(value = "账户更新密码")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "账户id不能为空", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "password", value = "账户密码", required = true, dataType = "String")
    })
    @PostMapping("/user/updatePwd")
    @ResponseBody
    public Map<String, Object> updateUserPwd(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id", 0);
        String pwd = ModelUtil.getStr(params, "password");

        if (id == 0) {
            setErrorResult(result, "账户id必传！");
        } else if (StrUtil.isEmpty(pwd)) {
            setErrorResult(result, "用户密码不能为空！");
        } else {
            result.put("data", sysService.updateUserPwd(id, pwd));
            setOkResult(result);
        }
        return result;
    }

    /**
     * 账户删除
     *
     * @param params 请求的参数
     * @return java.com.syhdoctor.webserver.api.util.Map
     */
    @ApiOperation(value = "账户删除")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "账户id不能为空", required = true, dataType = "String")
    })
    @PostMapping("/user/del")
    @ResponseBody
    public Map<String, Object> delUser(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id", 0);

        if (id == 0) {
            setErrorResult(result, "用户id不能为空！");
        } else {
            result.put("data", sysService.delUser(id));
            setOkResult(result);
        }

        return result;
    }
}
