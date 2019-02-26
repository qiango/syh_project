package com.syhdoctor.webserver.controller.webadmin.vipcard;


import com.syhdoctor.common.utils.EnumUtils.PayTypeEnum;
import com.syhdoctor.common.utils.*;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.service.vipcard.VipCardService;
import com.syhdoctor.webserver.service.wallet.RechargeableOrderService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description = "/Admin/vipcard 会员卡")
@RestController
@RequestMapping("/Admin/vipcard")
public class AdminVipCardController extends BaseController {

    @Autowired
    private VipCardService vipCardService;
    @Autowired
    private RechargeableOrderService rechargeableOrderService;


    @ApiOperation(value = "会员列表")
    @ApiImplicitParams({

            @ApiImplicitParam(paramType = "query", name = "id", value = "id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "vipcardno", value = "会员卡号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "vipcardname", value = "会员卡名称", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "begintime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "结束时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getVipCardList")
    public Map<String, Object> getVipCardList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        String vipcardno = ModelUtil.getStr(params, "vipcardno");
        String vipcardname = ModelUtil.getStr(params, "vipcardname");
        long begintime = ModelUtil.getLong(params, "begintime");
        long endtime = ModelUtil.getLong(params, "endtime");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", vipCardService.vipCardList(id, vipcardno, vipcardname, begintime, endtime, pageIndex, pageSize));
        result.put("total", vipCardService.vipCardListCount(id, vipcardno, vipcardname, begintime, endtime));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "会员卡详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", dataType = "String"),
    })
    @PostMapping("/getVipCardDetails")
    public Map<String, Object> getVipCardDetails(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", vipCardService.vipCardDetails(id));
            setOkResult(result, "查询成功");
        }
        return result;
    }


    @ApiOperation(value = "会员导出查询")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "vipcardno", value = "会员卡号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "vipcardname", value = "会员卡名称", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "begintime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "结束时间", dataType = "String"),
    })
    @PostMapping("/getVipCardExportList")
    public Map<String, Object> getVipCardExportList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        String vipcardno = ModelUtil.getStr(params, "vipcardno");
        String vipcardname = ModelUtil.getStr(params, "vipcardname");
        long begintime = ModelUtil.getLong(params, "begintime");
        long endtime = ModelUtil.getLong(params, "endtime");
        String customRandomString = UnixUtil.getCustomRandomString();
        List<Map<String, Object>> DoctorExportList = vipCardService.vipCardExportList(id, vipcardno, vipcardname, begintime, endtime);
        String fileName = customRandomString + "订单.xls";
        if (StrUtil.isEmpty(fileName)) {
            return null;
        }
        String fileStr = ConfigModel.BASEFILEPATH + FileUtil.setFileName(FileUtil.FILE_TEMP_PATH, fileName);
        FileSystemResource file = new FileSystemResource(fileStr);
        String[] strings = {"id", "vipcardno", "vipcardname", "price", "healthconsultant", "medicalexpert", "medicalgreen", "healthconsultantceefax", "healthconsultantphone", "medicalexpertceefax", "medicalexpertphone", "medicalexpertvideo", "medicalgreennum", "healthconsultantdiscount", "medicalexpertdiscount", "effectivetime", "createtime"};
        ExcelUtil.createExcel(strings, DoctorExportList, file.getPath());
        result.put("data", fileStr);
        setOkResult(result, "查询成功");
        return result;
    }

    /**
     * 导出会员卡列表Excel
     *
     * @param params
     * @return
     */
    @GetMapping(value = "/vipCardExcelFile")
    public ResponseEntity<InputStreamResource> vipCardExcelFile(@RequestParam Map<String, Object> params) {
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


    @ApiOperation(value = "会员卡删除")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", dataType = "String"),
    })
    @PostMapping("/delVipCard")
    public Map<String, Object> delVipCard(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", vipCardService.delVipCard(id));
            setOkResult(result, "删除成功");
        }
        return result;
    }


    @ApiOperation(value = "会员卡新增或修改")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "vipcardno", value = "会员卡号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "vipcardname", value = "会员卡名称", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "price", value = "现价价格", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "renewalfee", value = "续费价格", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "originalprice", value = "原价价格", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "healthconsultant", value = "尊享权益-健康顾问", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "medicalexpert", value = "尊享权益-医学专家", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "medicalgreen", value = "尊享权益-医疗绿通", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "ceefax", value = "电话次数", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "video", value = "视频次数", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "discount", value = "折扣", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "effectivetime", value = "可用时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "sort", value = "等级", dataType = "String"),
    })
    @PostMapping("/updateAddVipCard")
    public Map<String, Object> updateAddVipCard(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        String vipcardno = ModelUtil.getStr(params, "vipcardno");
        String vipcardname = ModelUtil.getStr(params, "vipcardname");
        BigDecimal price = ModelUtil.getDec(params, "price", BigDecimal.ZERO);
        BigDecimal renewal_fee = ModelUtil.getDec(params, "renewal_fee", BigDecimal.ZERO);
        BigDecimal original_price = ModelUtil.getDec(params, "original_price", BigDecimal.ZERO);
        String healthconsultant = ModelUtil.getStr(params, "healthconsultant");
        String medicalexpert = ModelUtil.getStr(params, "medicalexpert");
        String medicalgreen = ModelUtil.getStr(params, "medicalgreen");
        long health_consultant_ceefax = ModelUtil.getLong(params, "healthconsultantceefax");
        long health_consultant_phone = ModelUtil.getLong(params, "healthconsultantphone");
        long medical_expert_ceefax = ModelUtil.getLong(params, "medicalexpertceefax");
        long medical_expert_phone = ModelUtil.getLong(params, "medicalexpertphone");
        long medical_expert_video = ModelUtil.getLong(params, "medicalexpertvideo");

        Double healthconsultantdiscount = ModelUtil.getDouble(params, "healthconsultantdiscount", 0);
        Double medicalexpertdiscount = ModelUtil.getDouble(params, "medicalexpertdiscount", 0);
        int ceefax = ModelUtil.getInt(params, "ceefax");
        int video = ModelUtil.getInt(params, "video");
        Double discount = ModelUtil.getDouble(params, "discount", 0);
        int sort = ModelUtil.getInt(params, "sort");
        if (0 == sort || healthconsultantdiscount == null || medicalexpertdiscount == null) {
            setErrorResult(result, "参数错误");
            return result;
        }
        long effectivetime = ModelUtil.getLong(params, "effectivetime");
        boolean a = vipCardService.updateAddVipCard(healthconsultantdiscount, medicalexpertdiscount, id, vipcardno, vipcardname, price, renewal_fee, original_price, healthconsultant, medicalexpert, medicalgreen, ceefax, video, discount, effectivetime, sort, health_consultant_ceefax, health_consultant_phone, medical_expert_ceefax, medical_expert_phone, medical_expert_video);
        if (a) {
            result.put("data", a);
            setOkResult(result, "操作成功");
        } else {
            setErrorResult(result, "操作错误");
        }
        return result;
    }

    @ApiOperation(value = "后台首冲vip")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "用户id", value = "userid", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "会员卡id", value = "vipcardid", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "操作平台1、后台操作，2：前台操作", value = "operateMode", dataType = "String"),
    })
    @PostMapping("/createVipByback")
    public Map<String, Object> createVipByback(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userId = ModelUtil.getLong(params, "userid");
        long vipcardid = ModelUtil.getLong(params, "vipcardid");
        if (userId == 0 || vipcardid == 0) {
            setErrorResult(result, "参数错误");
            return result;
        }
        String verificationCode = ModelUtil.getStr(params, "verificationCode");
        int num = vipCardService.validCode(TextFixed.phone, verificationCode);
        if (num == -1) {
            setErrorResult(result, "请先获取验证码");
        } else if (num == -2) {
            setErrorResult(result, "验证码错误");
        } else if (num == 1) {
            Map<String, Object> map = vipCardService.createOrderAndSys(userId, vipcardid);
            long orderid = ModelUtil.getLong(map, "orderid");
            int operateMode = ModelUtil.getInt(params, "operateMode");
            result.put("data",vipCardService.updateStatusBack(orderid, userId, PayTypeEnum.Wallet.getCode(), operateMode));
            setOkResult(result, "充值成功");
        }
        return result;
    }

    @ApiOperation(value = "后台续费vip")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "用户id", value = "userid", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "会员卡id", value = "vipcardid", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "操作平台1、后台操作，2：前台操作", value = "operateMode", dataType = "String"),
    })
    @PostMapping("/createVipBybackto")
    public Map<String, Object> createVipBybackto(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userId = ModelUtil.getLong(params, "userid");
        long vipcardid = ModelUtil.getLong(params, "vipcardid");
        int operateMode = ModelUtil.getInt(params, "operateMode");
        if (userId == 0 || vipcardid == 0 || operateMode == 0) {
            setErrorResult(result, "参数错误");
        }
        String verificationCode = ModelUtil.getStr(params, "verificationCode");
        int num = vipCardService.validCode(TextFixed.phone, verificationCode);
        if (num == -1) {
            setErrorResult(result, "请先获取验证码");
        } else if (num == -2) {
            setErrorResult(result, "验证码错误");
        } else if (num == 1) {
            Map<String, Object> map = vipCardService.renewalOrder(userId, vipcardid);
            long orderid = ModelUtil.getLong(map, "orderid");
            String orderno = ModelUtil.getStr(map, "orderno");
            result.put("data",vipCardService.updateStatusReneByback(orderid, userId, PayTypeEnum.Wallet.getCode(), orderno, operateMode,vipcardid));
            setOkResult(result, "续费成功");
        }
        return result;
    }

    @ApiOperation(value = "未首值的用户 下拉框")
    @PostMapping("/userlistDropdownBox")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "h用户id", value = "userid", dataType = "String"),
    })
    public Map<String,Object> userlistDropdownBox(@ApiParam(hidden = true) @RequestParam Map<String, Object> params){
        Map<String,Object> result = new HashMap<>();
        result.put("data",vipCardService.userlistDropdownBox());
        setOkResult(result,"查询成功");
        return result;
    }
    @ApiOperation(value = "会员卡列表 下拉框")
    @PostMapping("/viplistDropdownBox")
    public Map<String,Object> viplistDropdownBox(@ApiParam(hidden = true) @RequestParam Map<String, Object> params){
        Map<String,Object> result = new HashMap<>();
        result.put("data",vipCardService.viplistDropdownBox());
        setOkResult(result,"查询成功");
        return result;
    }


    @ApiOperation(value = "发送验证码")
    @PostMapping("/sendMessage")
    public Map<String, Object> sendMessage() {
        Map<String, Object> result = new HashMap<>();
        if (!RegexValidateUtil.checkCellphone(TextFixed.phone)) {
            setErrorResult(result, "手机号码格式错误");
        } else {
            boolean flag = vipCardService.sendMesg(TextFixed.phone);
            if (flag) {
                setOkResult(result, "发送成功");
            } else {
                setErrorResult(result, "发送失败");
            }
        }
        return result;
    }


    @ApiOperation(value = "获取用户")
    @PostMapping("/getUserMember")
    public Map<String, Object> getUserMember(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        result.put("data",vipCardService.getUsermember(id));
        setOkResult(result,"查询成功");
        return result;
    }

}
