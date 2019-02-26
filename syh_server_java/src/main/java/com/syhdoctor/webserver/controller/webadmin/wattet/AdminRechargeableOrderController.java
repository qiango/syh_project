package com.syhdoctor.webserver.controller.webadmin.wattet;

import com.syhdoctor.common.utils.EnumUtils.PayTypeEnum;
import com.syhdoctor.common.utils.*;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.service.wallet.RechargeableOrderService;
import com.syhdoctor.webserver.thirdparty.mongodb.DemoDao;
import com.syhdoctor.webserver.thirdparty.mongodb.entity.DemoEntity;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api(description = "/Admin/Wattat 充值订单管理")
@RequestMapping("/Admin/Wattat")
public class AdminRechargeableOrderController extends BaseController {


    @Autowired
    private RechargeableOrderService rechargeableOrderService;

    @Autowired
    @Qualifier("demoDaoImpl")
    private DemoDao demoDao;

    @ApiOperation(value = "充值记录查询")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "编号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "amountmoney", value = "面值", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "lotnumber", value = "批号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "name", value = "用户姓名", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "paytype", value = "充值方式", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "begintime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "结束时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getRechargeableOrderList")
    public Map<String, Object> getRechargeableOrderList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getInt(params, "id");
        BigDecimal amountmoney = ModelUtil.getDec(params, "amountmoney", BigDecimal.ZERO);
        String lotnumber = ModelUtil.getStr(params, "lotnumber");
        String name = ModelUtil.getStr(params, "name");
        int paytype = ModelUtil.getInt(params, "paytype");
        long begintime = ModelUtil.getLong(params, "begintime");
        long endtime = ModelUtil.getLong(params, "endtime");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", rechargeableOrderService.getRechargeableOrderList(id, amountmoney, lotnumber, name, paytype, begintime, endtime, pageIndex, pageSize));
        result.put("total", rechargeableOrderService.getRechargeableOrdersCount(id, amountmoney, lotnumber, name, paytype, begintime, endtime));
        setOkResult(result, "查询成功");
        return result;
    }


    @ApiOperation(value = "充值记录查询导出")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "编号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "amountmoney", value = "面值", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "lotnumber", value = "批号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "name", value = "用户姓名", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "paytype", value = "充值方式", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "begintime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "结束时间", dataType = "String"),
    })
    @PostMapping("/getRechargeableOrderExportListAll")
    public Map<String, Object> getRechargeableOrderExportListAll(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        BigDecimal amountmoney = ModelUtil.getDec(params, "amountmoney", BigDecimal.ZERO);
        String lotnumber = ModelUtil.getStr(params, "lotnumber");
        String name = ModelUtil.getStr(params, "name");
        int paytype = ModelUtil.getInt(params, "paytype");
        long begintime = ModelUtil.getLong(params, "begintime");
        Long endtime = ModelUtil.getLong(params, "endtime");
        String customRandomString = UnixUtil.getCustomRandomString();
        List<Map<String, Object>> DoctorExportList = rechargeableOrderService.getRechargeableOrderExportListAll(id, amountmoney, lotnumber, name, paytype, begintime, endtime);
        String fileName = customRandomString + "订单.xls";
        if (StrUtil.isEmpty(fileName)) {
            return null;
        }
        String fileStr = ConfigModel.BASEFILEPATH + FileUtil.setFileName(FileUtil.FILE_TEMP_PATH, fileName);
        FileSystemResource file = new FileSystemResource(fileStr);
        String[] strings = {"id", "paytype", "amountmoney", "lotnumber", "redeemcode", "begintime", "userid", "name"};
        ExcelUtil.createExcel(strings, DoctorExportList, file.getPath());
        result.put("data", fileStr);
        setOkResult(result, "查询成功");
        return result;
    }


    /**
     * 导出充值记录列表Excel
     *
     * @param params
     * @return
     */
    @GetMapping(value = "/rechargeableExcelFile")
    public ResponseEntity<InputStreamResource> rechargeableExcelFile(@RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        log.info("DistributorSale>rechargeableExcelFile 参数 " + params);
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

    @ApiOperation(value = "充值卡详情导出")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "编号", dataType = "String"),
    })
    @PostMapping("/getRechargeableOrderExportDetail")
    public Map<String, Object> getRechargeableOrderDetail(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        String customRandomString = UnixUtil.getCustomRandomString();
        List<Map<String, Object>> detailList = rechargeableOrderService.getDetailList(id);
        String fileName = customRandomString + "订单.xls";
        if (StrUtil.isEmpty(fileName)) {
            return null;
        }
        String fileStr = ConfigModel.BASEFILEPATH + FileUtil.setFileName(FileUtil.FILE_TEMP_PATH, fileName);
        FileSystemResource file = new FileSystemResource(fileStr);
        String[] strings = {"id", "redeemcode", "amountmoney", "lotnumber", "effectivetype", "begintime", "endtime", "principal"};
        ExcelUtil.createExcel(strings, detailList, file.getPath());
        result.put("data", fileStr);
        setOkResult(result, "查询成功");
        return result;
    }


    /**
     * 导出充值卡详情列表Excel
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "导出充值卡详情")
    @GetMapping(value = "/rechargeableExcelFileDetail")
    public ResponseEntity<InputStreamResource> rechargeableExcelFileDetail(@RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        log.info("DistributorSale>rechargeableExcelFile 参数 " + params);
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

    @ApiOperation(value = "批次列表导出")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "amountmoney", value = "面值", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "lotnumber", value = "批号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "begintime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "结束时间", dataType = "String"),
    })
    @PostMapping("/getRechargeableOrderExportLot")
    public Map<String, Object> getRechargeableOrderLot(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        BigDecimal amountmoney = ModelUtil.getDec(params, "amountmoney", BigDecimal.ZERO);
        String lotnumber = ModelUtil.getStr(params, "lotnumber");
        long begintime = ModelUtil.getLong(params, "begintime");
        Long endtime = ModelUtil.getLong(params, "endtime");
        String customRandomString = UnixUtil.getCustomRandomString();
        List<Map<String, Object>> DoctorExportList = rechargeableOrderService.getRechargeableOrderExportLot(amountmoney, lotnumber, begintime, endtime);
        String fileName = customRandomString + "订单.xls";
        if (StrUtil.isEmpty(fileName)) {
            return null;
        }
        String fileStr = ConfigModel.BASEFILEPATH + FileUtil.setFileName(FileUtil.FILE_TEMP_PATH, fileName);
        FileSystemResource file = new FileSystemResource(fileStr);
        String[] strings = {"id", "amountmoney", "lotnumber", "create_time", "effectivetype", "principal", "createcardstatus", "total", "notusedcount", "begintime", "endtime"};
        ExcelUtil.createExcel(strings, DoctorExportList, file.getPath());
        result.put("data", fileStr);
        setOkResult(result, "查询成功");
        return result;
    }


    /**
     * 导出批次列表Excel
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "导出批次列表Excel")
    @GetMapping(value = "/rechargeableExcelFileLot")
    public ResponseEntity<InputStreamResource> rechargeableExcelFileLot(@RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        log.info("DistributorSale>rechargeableExcelFile 参数 " + params);
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

    @ApiOperation(value = "批量生成充值卡")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "amountmoney", value = "面值", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "effectivetype", value = "有效期", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "begintime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "结束时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "total", value = "生成总数", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "principal", value = "负责人", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "channelprefix", value = "渠道前缀", dataType = "String"),
    })
    @PostMapping("/addRechargeaCard")
    public Map<String, Object> insertRechargea(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        BigDecimal amountmoney = ModelUtil.getDec(params, "amountmoney", BigDecimal.ZERO);
        int effectivetype = ModelUtil.getInt(params, "effectivetype");//类型(1:永久有效，2：近效期)
        long begintime = ModelUtil.getLong(params, "begintime");
        long endtime = ModelUtil.getLong(params, "endtime");
        long total = ModelUtil.getLong(params, "total");//生成总数
        String principal = ModelUtil.getStr(params, "principal");//负责人
        String channelprefix = ModelUtil.getStr(params, "channelprefix");// 渠道前缀
        long createUser = ModelUtil.getLong(params, "agentid");//用户
        if (!verfiy(channelprefix)) {
            setErrorResult(result, "渠道前缀参数不合法，请检查");
        } else {
            long id = rechargeableOrderService.addRechargeablecardlot(amountmoney, effectivetype, begintime, endtime, total, principal, channelprefix, createUser);
            rechargeableOrderService.createDetail(id);
            result.put("data", id);
            setOkResult(result, "保存成功!");
        }
        return result;
    }

    @ApiOperation(value = "充值卡查询列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "amountmoney", value = "面值", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "lotnumber", value = "批号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "begintime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "结束时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getRechargeableCardLotList")
    public Map<String, Object> getRechargeableCardLotList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        BigDecimal amountmoney = ModelUtil.getDec(params, "amountmoney", BigDecimal.ZERO);
        String lotnumber = ModelUtil.getStr(params, "lotnumber");
        long begintime = ModelUtil.getLong(params, "begintime");
        long endtime = ModelUtil.getLong(params, "endtime");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", rechargeableOrderService.getRechargeableOrderList(amountmoney, lotnumber, begintime, endtime, pageIndex, pageSize));
        result.put("total", rechargeableOrderService.getRechargeableOrderCount(amountmoney, lotnumber, begintime, endtime));
        setOkResult(result, "查询成功");
        return result;
    }


    @ApiOperation(value = "充值卡详情列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "充值卡id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getDetail")
    public Map<String, Object> getDetail(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", rechargeableOrderService.getDetailList(id, pageIndex, pageSize));
        result.put("total", rechargeableOrderService.getDetailListCount(id));
        setOkResult(result, "查询成功");
        return result;

    }

    @ApiOperation(value = "充值卡详情头")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "充值卡id", dataType = "String"),
    })
    @PostMapping("/getDetailHeader")
    public Map<String, Object> getDetailHeader(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        result.put("data", rechargeableOrderService.getDetailHeader(id));
        setOkResult(result, "查询成功");
        return result;

    }

    @ApiOperation(value = "面值列表")
    @PostMapping("/getAmount")
    public Map<String, Object> getAmount() {
        Map<String, Object> result = new HashMap<>();
        result.put("data", rechargeableOrderService.findValue());
        setOkResult(result, "查询成功");
        return result;

    }

    @ApiOperation(value = "充值类型列表")
    @PostMapping("/payTypeList")
    public Map<String, Object> payTypeList() {
        Map<String, Object> result = new HashMap<>();
        result.put("data", PayTypeEnum.getList());
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "获取用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userno", value = "用户编码", dataType = "String"),
    })
    @PostMapping("/getUser")
    public Map<String, Object> getUser(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();

        String userno = ModelUtil.getStr(params, "userno");
        if (!StrUtil.isEmpty(userno)) {
            result.put("data", rechargeableOrderService.getUser(userno));
        }
        setOkResult(result, "获取成功!");
        return result;
    }


    @ApiOperation(value = "用户账户充值")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "payType", value = "充值类型", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "applicableType", value = "适用类型", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "amount", value = "充值金额", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "cardCode", value = "充值卡兑换码", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "userno", value = "用户编码", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "remark", value = "备注", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "verificationCode", value = "验证码", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "operateMode", value = "操作平台,1、后台操作，2、前台操作", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "agentid", value = "创建人id", dataType = "String"),
    })
    @PostMapping("/addRechargeableOrderUser")
    public Map<String, Object> addRechargeableOrder(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();

        int payType = ModelUtil.getInt(params, "payType");
        int applicableType = ModelUtil.getInt(params, "applicableType");
        BigDecimal amount = ModelUtil.getDec(params, "amount", BigDecimal.ZERO);
        String cardCode = ModelUtil.getStr(params, "cardCode");
        String userno = ModelUtil.getStr(params, "userno");
        String remark = ModelUtil.getStr(params, "remark");
        String verificationCode = ModelUtil.getStr(params, "verificationCode");
        int operateMode = ModelUtil.getInt(params, "operateMode");
        long createuserid = ModelUtil.getLong(params, "agentid");
        int num = rechargeableOrderService.validCode(TextFixed.phone, verificationCode);
        if (num == -1) {
            setErrorResult(result, "请先获取验证码");
        } else if (num == -2) {
            setErrorResult(result, "验证码错误");
        } else if (num == 1) {
            rechargeableOrderService.addRechargeablecardByuser(userno, payType, applicableType, amount, cardCode, remark, verificationCode, createuserid, operateMode);
            setOkResult(result, "保存成功!");
        }
        return result;
    }

    @ApiOperation(value = "获取医生信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorno", value = "医生编码", dataType = "String"),
    })
    @PostMapping("/getDoctor")
    public Map<String, Object> getDoctor(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String doctorno = ModelUtil.getStr(params, "doctorno");
        if (!StrUtil.isEmpty(doctorno)) {
            result.put("data", rechargeableOrderService.getDoctor(doctorno));
        }
        setOkResult(result, "获取成功!");
        return result;
    }

    @ApiOperation(value = "获取医生信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorno", value = "医生编码", dataType = "String"),
    })
    @PostMapping("/getDoctorList")
    public Map<String, Object> getDoctorList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String doctorname = ModelUtil.getStr(params, "doctorname");
        result.put("data", rechargeableOrderService.getDoctorList(doctorname));
        setOkResult(result, "获取成功!");
        return result;
    }


    @ApiOperation(value = "医生账户充值")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "applicableType", value = "适用类型", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "amount", value = "积分", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "userno", value = "医生编码", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "remark", value = "备注", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "verificationCode", value = "验证码", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "operateMode", value = "操作平台,1、后台操作，2、前台操作", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "agentid", value = "创建人id", dataType = "String"),
    })
    @PostMapping("/addRechargeableOrderDoctor")
    public Map<String, Object> addRechargeableOrderDoctor(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int applicableType = ModelUtil.getInt(params, "applicableType");
//        BigDecimal amount = ModelUtil.getDec(params, "amount", BigDecimal.ZERO);
        List<?> doctorinfo = ModelUtil.getList(params, "doctoracount", new ArrayList<>());
        String remark = ModelUtil.getStr(params, "remark");
        String verificationCode = ModelUtil.getStr(params, "verificationCode");
        int operateMode = ModelUtil.getInt(params, "operateMode");
        long createuserid = ModelUtil.getLong(params, "agentid");
        int num = rechargeableOrderService.validCode(TextFixed.phone, verificationCode);
        if (num == -1) {
            setErrorResult(result, "请先获取验证码");
        } else if (num == -2) {
            setErrorResult(result, "验证码错误");
        } else if (num == 1) {
            rechargeableOrderService.addRechargeablecardbyDoctor(doctorinfo, applicableType, remark, createuserid, operateMode);
            setOkResult(result, "保存成功!");
        }
        return result;
    }


    @ApiOperation(value = "发送验证码")
    @PostMapping("/sendMessage")
    public Map<String, Object> sendMessage() {
        Map<String, Object> result = new HashMap<>();
        if (!RegexValidateUtil.checkCellphone(TextFixed.phone)) {
            setErrorResult(result, "手机号码格式错误");
        } else {
            boolean flag = rechargeableOrderService.sendMesg(TextFixed.phone);
            if (flag) {
                setOkResult(result, "发送成功");
            } else {
                setErrorResult(result, "发送失败");
            }
        }
        return result;
    }

    public boolean verfiy(String str) {
        boolean isDigit = true;//
        if (str.length() != 2) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            char a = str.charAt(i);
            if (!Character.isDigit(str.charAt(i))) {
                int j = (int) a;
                if ((j >= 65 && j <= 90) || (j >= 97 && j <= 122)) {
                } else {
                    isDigit = false;
                    break;
                }
            }
        }
        return isDigit;
    }

    @PostMapping("/testAdd")
    public Map<String, Object> test(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String title = ModelUtil.getStr(params, "title");
        String description = ModelUtil.getStr(params, "description");
        String url = ModelUtil.getStr(params, "url");
        long id = ModelUtil.getLong(params, "sql");
        DemoEntity demoEntity = new DemoEntity();
        demoEntity.setId(id);
        demoEntity.setTitle(title);
        demoEntity.setDescription(description);
        demoEntity.setBy("wwwwwwwwww");
        demoEntity.setUrl(url);
        demoDao.saveDemo(demoEntity);
        setOkResult(result, "ok");
        return result;
    }

    @PostMapping("/testRemove")
    public Map<String, Object> testRemove(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        demoDao.removeDemo(id);
        setOkResult(result, "ok");
        return result;
    }

    @PostMapping("/view")
    public Map<String, Object> view(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        setOkResult(result, "ok");
        DemoEntity demoById = demoDao.findDemoById(id);
        result.put("data", demoDao.findDemoById(id));
        result.put("allData", demoDao.findAll());
        return result;
    }

    @GetMapping(value = "/testPaths")
//    @RequestMapping(method = RequestMethod.GET, value = "/testPaths/{filePath:.+}")
    public void downLoad(HttpServletResponse response) throws IOException {
//        String filePath="D:\\home\\qwq\\file\\apk\\17411d4db5cbcd6a3209cb6415a5a6778de81cf2.apk";
        String filePath = "D:\\logs\\bzz_server_java.2018-09-14.log";
        File f = new File(filePath);
        if (!f.exists()) {
            response.sendError(404, "File not found!");
            return;
        }
        BufferedInputStream br = new BufferedInputStream(new FileInputStream(f));
        byte[] buf = new byte[1024];
        int len = 0;
        response.reset(); // 非常重要
        String suffix = filePath.substring(filePath.lastIndexOf(".") + 1);
        if ("jpg".equals(suffix) || "png".equals(suffix)) { // 在线打开方式
            URL u = new URL("file:///" + filePath);
            response.setContentType(u.openConnection().getContentType());
            response.setHeader("Content-Disposition", "inline; filename=" + f.getName());
            // 文件名应该编码成UTF-8
        } else { // 纯下载方式
            response.setContentType("application/x-msdownload");
            response.setHeader("Content-Disposition", "attachment; filename=" + f.getName());
        }
        OutputStream out = response.getOutputStream();
        while ((len = br.read(buf)) > 0)
            out.write(buf, 0, len);
        br.close();
        out.close();
    }


    @ApiOperation(value = "医生充值记录查询")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = "用户姓名", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "begintime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "结束时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/doctorRechargeableOrderList")
    public Map<String, Object> doctorRechargeableOrderList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String name = ModelUtil.getStr(params, "name");
        String phone = ModelUtil.getStr(params, "docphone");
        int applicabletype = ModelUtil.getInt(params, "applicabletype");
        long begintime = ModelUtil.getLong(params, "begintime");
        long endtime = ModelUtil.getLong(params, "endtime");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", rechargeableOrderService.doctorRechargeableOrderList(name, phone, applicabletype, begintime, endtime, pageIndex, pageSize));
        result.put("total", rechargeableOrderService.doctorRechargeableOrderListCount(name, phone, applicabletype, begintime, endtime));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "医生充值记录查询导出")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = "用户姓名", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "begintime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "结束时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/doctorRechargeableOrderListExport")
    public Map<String, Object> doctorRechargeableOrderListExport(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String name = ModelUtil.getStr(params, "name");
        String phone = ModelUtil.getStr(params, "docphone");
        int applicabletype = ModelUtil.getInt(params, "applicabletype");
        long begintime = ModelUtil.getLong(params, "begintime");
        long endtime = ModelUtil.getLong(params, "endtime");
        List<Map<String, Object>> DoctorExportList = rechargeableOrderService.doctorRechargeableOrderListExport(name, phone, applicabletype, begintime, endtime);
        String customRandomString = UnixUtil.getCustomRandomString();
        String fileName = customRandomString + "订单.xls";
        if (StrUtil.isEmpty(fileName)) {
            return null;
        }
        String fileStr = ConfigModel.BASEFILEPATH + FileUtil.setFileName(FileUtil.FILE_TEMP_PATH, fileName);
        FileSystemResource file = new FileSystemResource(fileStr);
        String[] strings = {"id", "name", "docphone", "amountmoney", "remark", "applicabletype", "rechargeabletime"};
        ExcelUtil.createExcel(strings, DoctorExportList, file.getPath());
        result.put("data", fileStr);
        setOkResult(result, "查询成功");
        return result;
    }

    /**
     * 导出医生充值记录账户列表Excel
     *
     * @param params
     * @return
     */
    @GetMapping(value = "/doctorRechargeableOrderExcelFile")
    public ResponseEntity<InputStreamResource> doctorRechargeableOrderExcelFile(@RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        log.info("DistributorSale>doctorRechargeableOrderExcelFile 参数 " + params);
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


}
