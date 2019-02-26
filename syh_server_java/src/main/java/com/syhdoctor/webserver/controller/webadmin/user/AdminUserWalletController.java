package com.syhdoctor.webserver.controller.webadmin.user;

import com.syhdoctor.common.utils.*;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.service.user.UserAccountService;
import com.syhdoctor.webserver.service.user.UserBaseService;
import io.swagger.annotations.*;
import io.swagger.models.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Api(description = "/Admin/Wallet 用户账户管理")
@RestController
@RequestMapping("/Admin/Wallet")
public class AdminUserWalletController extends BaseController {

    @Autowired
    private UserAccountService userAccountService;


    @ApiOperation(value = "用户账户管理查询")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userno", value = "会员号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "name", value = "姓名", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "phone", value = "手机号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "begintime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "结束时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getUserAccountList")
    public Map<String, Object> getUserAccountList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String userno = ModelUtil.getStr(params, "userno");
        String name = ModelUtil.getStr(params, "name");
        String phone = ModelUtil.getStr(params, "phone");
        long begintime = ModelUtil.getLong(params, "begintime");
        long endtime = ModelUtil.getLong(params, "endtime");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", userAccountService.getUserAccountList(userno, name, phone, begintime, endtime, pageIndex, pageSize));
        result.put("total", userAccountService.getUserAccountCount(userno, name, phone, begintime, endtime));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "用户账户详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "编号", dataType = "String"),
    })
    @PostMapping("/getUserAccountId")
    public Map<String, Object> getUserAccountId(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id > 0) {
            result.put("data", userAccountService.getUserAccountId(id));
            setOkResult(result, "查询成功");
        } else {
            setErrorResult(result, "查询错误");
        }
        return result;

    }

    @ApiOperation(value = "用户账户详情列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id(编号)", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "moneyflag", value = "交易类型", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getUserAccountListId")
    public Map<String, Object> getUserAccountListId(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int userid = ModelUtil.getInt(params, "userid");
        int moneyflag = ModelUtil.getInt(params, "moneyflag");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        if (userid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userAccountService.getUserAccountListId(userid, moneyflag, pageIndex, pageSize));
            result.put("total", userAccountService.getUserAccountListCount(userid, moneyflag));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "导出")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userno", value = "会员号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "name", value = "姓名", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "phone", value = "手机号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "begintime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "结束时间", dataType = "String"),
    })
    @PostMapping("/getUserAccountexportListAll")
    public Map<String, Object> getUserAccountexportListAll(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String userno = ModelUtil.getStr(params, "userno");
        String name = ModelUtil.getStr(params, "name");
        String phone = ModelUtil.getStr(params, "phone");
        long begintime = ModelUtil.getLong(params, "begintime");
        long endtime = ModelUtil.getLong(params, "endtime");
        String customRandomString = UnixUtil.getCustomRandomString();
        List<Map<String, Object>> DoctorExportList = userAccountService.getUserAccountexportListAll(userno, name, phone, begintime, endtime);
        String fileName = customRandomString + "订单.xls";
        if (StrUtil.isEmpty(fileName)) {
            return null;
        }
        String fileStr = ConfigModel.BASEFILEPATH + FileUtil.setFileName(FileUtil.FILE_TEMP_PATH, fileName);
        FileSystemResource file = new FileSystemResource(fileStr);
        String[] strings = {"id", "userno", "name", "phone", "createtime", "walletbalance", "integral"};
        ExcelUtil.createExcel(strings, DoctorExportList, file.getPath());
        result.put("data", fileStr);
        setOkResult(result, "查询成功");
        return result;
    }

    /**
     * 导出用户账户列表Excel
     *
     * @param params
     * @return
     */
    @GetMapping(value = "/userExcelFile")
    public ResponseEntity<InputStreamResource> userExcelFile(@RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        log.info("DistributorSale>userExcelFile 参数 " + params);
        String fileStr = ModelUtil.getStr(params, "filestr");
        try {
            FileSystemResource file = new FileSystemResource(fileStr);
            return responseEntity(file);
        } catch (Exception ex) {
            log.error(" DistributorSale>DelCreditOrder   ", ex);
            setErrorResult(result, ex.getMessage());
        }
        return null;
    }

    @PostMapping("/userHealthRecords")
    public Map<String, Object> userHealthRecords(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userid = ModelUtil.getLong(params, "userid");
        result.put("data", userAccountService.userHealthRecords(userid));
        setOkResult(result, "成功");
        return result;
    }


}
