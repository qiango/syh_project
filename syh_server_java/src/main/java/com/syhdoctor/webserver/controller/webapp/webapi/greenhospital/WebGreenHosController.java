package com.syhdoctor.webserver.controller.webapp.webapi.greenhospital;

import com.syhdoctor.common.pay.IPayService;
import com.syhdoctor.common.utils.EnumUtils.QAContentTypeEnum;
import com.syhdoctor.common.utils.FileUtil;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.service.video.UserVideoService;
import com.syhdoctor.webserver.service.wallet.UserWalletService;
import com.syhdoctor.webserver.utils.QiniuUtils;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qian.wang
 * @description
 * @date 2018/12/6
 */

@Api(description = "/Web/GreenHospital 绿通医院")
@RestController
@RequestMapping("/Web/GreenHospital")
public class WebGreenHosController extends BaseController {

    @Autowired
    private UserVideoService userVideoService;

    @Autowired
    private UserWalletService userWalletService;

    @ApiOperation(value = "绿通医院列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "categoryids", value = "分类id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "departmentid", value = "科室id", defaultValue = "20", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/findHospitalList")
    public Map<String, Object> findHospitalList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        List<?> categoryids = ModelUtil.getList(params, "categoryids", new ArrayList<>());//分类id
        List<?> departmentids = ModelUtil.getList(params, "departmentids", new ArrayList<>());//分类id
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", userVideoService.getHospitalList(categoryids, departmentids, pageIndex, pageSize));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "绿通医院详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "hospitalid", value = "医院id", dataType = "String"),
    })
    @PostMapping("/findHospitalDetail")
    public Map<String, Object> findHospitalDetail(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long hospitalid = ModelUtil.getLong(params, "hospitalid");
        if (hospitalid == 0) {
            setErrorResult(result, "参数错误");
            return result;
        }
        result.put("data", userVideoService.findDetail(hospitalid));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "绿通医院分类列表")
    @ApiImplicitParams({
    })
    @PostMapping("/findCategory")
    public Map<String, Object> findCategory(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", userVideoService.findCategory());
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "绿通医院科室tree")
    @ApiImplicitParams({
    })
    @PostMapping("/findDepartmentList")
    public Map<String, Object> findDeparmentList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", userVideoService.getAllDepartmentList());
        setOkResult(result, "查询成功");
        return result;
    }


    @ApiOperation(value = "绿通下单")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "diseaselist", value = "症状", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "hospitalid", value = "医院id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "departmentid", value = "科室id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "appointmenttype", value = "服务类型(0:门诊，1：住院)", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "isordinary", value = "是否普通", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "isexpert", value = "是否专家", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "isurgent", value = "是否加急", dataType = "String"),
    })
    @PostMapping("/insertOrderGreen")
    public Map<String, Object> insertOrderGreen(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        List<?> diseaselist = ModelUtil.getList(params, "diseaselist", new ArrayList<>());//症状
        long userid = ModelUtil.getLong(params, "userid");
        long hospitalid = ModelUtil.getLong(params, "hospitalid"); //医院id
        long departmentid = ModelUtil.getLong(params, "departmentid");//科室id
        int appointmentType = ModelUtil.getInt(params, "appointmenttype");//服务类型(0:门诊，1：住院)
//        long userPatient = ModelUtil.getLong(params, "userpatient");//患者id
        int isOrdinary = ModelUtil.getInt(params, "isordinary");//是否普通
        int isExpert = ModelUtil.getInt(params, "isexpert");//是否专家
        int isUrgent = ModelUtil.getInt(params, "isurgent");//是否加急
        result.put("data", userVideoService.insertOrderGreen(diseaselist, userid, hospitalid, departmentid, appointmentType, isOrdinary, isExpert, isUrgent));
        setOkResult(result, "查询成功");
        return result;
    }


    @ApiOperation(value = "绿通预约")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "hospitalid", value = "医院id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "departmentid", value = "科室id", dataType = "String"),
    })
    @PostMapping("/insertOrderGreenSimple")
    public Map<String, Object> insertOrderGreenSimple(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userid = ModelUtil.getLong(params, "userid");
        long hospitalid = ModelUtil.getLong(params, "hospitalid"); //医院id
        long departmentid = ModelUtil.getLong(params, "departmentid");//科室id
        result.put("data", userVideoService.insertOrderGreenSimple(userid, hospitalid, departmentid));
        setOkResult(result, "查询成功");
        return result;
    }


    @ApiOperation(value = "绿通订单列表")
    @ApiImplicitParams({
    })
    @PostMapping("/greenOrderList")
    public Map<String, Object> greenOrderList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userid = ModelUtil.getLong(params, "userid");
        if (userid == 0) {
            setErrorResult(result, "参数错误");
            return result;
        }
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", userVideoService.greenOrderList(userid, pageSize, pageIndex));
        setOkResult(result, "查询成功");
        return result;
    }


    @ApiOperation(value = "绿通订单详情")
    @ApiImplicitParams({
    })
    @PostMapping("/getGreenDetail")
    public Map<String, Object> getGreenDetail(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        result.put("data", userVideoService.getGreenDetail(orderid));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "绿通微信app支付")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单号id", dataType = "String"),
    })
    @PostMapping("/addWechatAppOrder")
    public Map<String, Object> addWechatAppOrder(@ApiParam(hidden = true) @RequestParam Map<String, Object> params, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        long userId = ModelUtil.getLong(params, "userid");
        long orderid = ModelUtil.getLong(params, "orderid");
        Map<String, Object> map = userVideoService.findOrderByorderId(orderid);
        String orderno = ModelUtil.getStr(map, "orderno");
        BigDecimal amountmoney = ModelUtil.getDec(map, "actualmoney", BigDecimal.ZERO);
//        String ip="127.0.0.1";
        String ip = request.getRemoteAddr();
        if (userId == 0 || amountmoney.compareTo(BigDecimal.ZERO) == 0) {
            setErrorResult(result, "参数错误");
        } else {
            IPayService.PayBean payBean = userVideoService.weChatAppPay(amountmoney, ip, orderno, userId);
            payBean.setOrderid(orderid);
            result.put("data", getResultByApp(payBean));
            setOkResult(result, "充值成功");
        }
        return result;
    }

    @ApiOperation(value = "绿通支付宝app充值")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "amountmoney", value = "金额", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单号id", dataType = "String"),
    })
    @PostMapping("/addAliAppOrder")
    public Map<String, Object> addAliAppOrder(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userId = ModelUtil.getLong(params, "userid");
        long orderid = ModelUtil.getLong(params, "orderid");
        Map<String, Object> map = userVideoService.findOrderByorderId(orderid);
        String orderno = ModelUtil.getStr(map, "orderno");
        BigDecimal amountmoney = ModelUtil.getDec(map, "actualmoney", BigDecimal.ZERO);
        IPayService.PayBean payBean = userVideoService.aliAppPay(amountmoney, orderno, userId);
        payBean.setOrderid(orderid);
        result.put("data", payBean);
        setOkResult(result, "充值成功");
        return result;
    }

    @ApiOperation(value = "vip卡钱包充值")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单号id", dataType = "String"),
    })
    @PostMapping("/addWalletAppOrder")
    public Map<String, Object> addWalletOrder(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> suf = new HashMap<>();
        long userId = ModelUtil.getLong(params, "userid");
        long orderid = ModelUtil.getLong(params, "orderid");
        Map<String, Object> maps = userVideoService.findOrderByorderId(orderid);
        BigDecimal amountmoney = ModelUtil.getDec(maps, "actualmoney", BigDecimal.ZERO);
        if (userId == 0 || amountmoney.compareTo(BigDecimal.ZERO) == 0) {
            setErrorResult(result, "参数错误");
        } else {
            Map<String, Object> map = userWalletService.getUserWallet(userId);
            BigDecimal bigDecimal = ModelUtil.getDec(map, "walletbalance", BigDecimal.ZERO);//钱包金额
            if (bigDecimal.compareTo(amountmoney) < 1) {
                suf.put("sufficient", 0);
                result.put("sufficient", 0);
                setOkResult(result, "钱包余额不足");
            } else {
                suf.put("sufficient", 1);
                userVideoService.wallet(bigDecimal, amountmoney, orderid, userId);
                setOkResult(result, "充值成功");
            }
            result.put("data", suf);
            return result;
        }
        return result;
    }

    private Map<String, Object> getResultByApp(IPayService.PayBean payBean) {
        Map<String, Object> result = new HashMap<>();
        result.put("state", payBean.isState());
        result.put("paysign", payBean.getPaysign());
        result.put("timestamp", payBean.getTimeStamp());
        result.put("noncestr", payBean.getNonceStr());
        result.put("package", payBean.getPackageValue());
        result.put("partnerid", payBean.getPartnerId());
        result.put("prepayid", payBean.getPrepayId());
        result.put("appid", payBean.getAppId());
        return result;
    }


    @ApiOperation(value = "用户聊天订单详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单编号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "当前页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页数据量", defaultValue = "20", dataType = "String")
    })
    @PostMapping("/getUserGreenList")
    public Map<String, Object> getUserGreenList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        int pageindex = ModelUtil.getInt(params, "pageindex", 1);
        int pagesize = ModelUtil.getInt(params, "pagesize", 20);
        if (orderid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userVideoService.getUserGreenList(orderid, pageindex, pagesize));
            setOkResult(result, "查询成功");
        }
        return result;
    }




    @ApiOperation(value = "app发送语音")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "contenttime", value = "语音时长(秒)", required = true, dataType = "String"),
    })
    @PostMapping("/sendVoiceUser")
    public Map<String, Object> sendVoiceUser(@ApiParam(hidden = true) @RequestParam Map<String, Object> params, @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        long contenttime = ModelUtil.getLong(params, "contenttime");
        try {
            if (orderid == 0 || contenttime == 0) {
                setOkResult(result, "参数错误");
            } else {
                if (file != null) {
                    String key = "syh" + UnixUtil.getCustomRandomString() + ".mp3";
                    QiniuUtils.putFile(ConfigModel.QINIU.BUCKET, key, file.getInputStream());
                    String filePath = ConfigModel.BASEFILEPATH + FileUtil.setFileName(FileUtil.FILE_LONG_PATH, key);
                    if (FileUtil.validateFile(filePath)) {
                        FileUtil.delFile(filePath);
                    }
                    FileUtil.saveFile(file.getBytes(), filePath);
                    result.put("data", userVideoService.addAppGreen(orderid, key, QAContentTypeEnum.Voice.getCode(), contenttime, 0));
                    setOkResult(result, "添加成功");
                } else {
                    setErrorResult(result, "请上传文件");
                }
            }
        } catch (Exception ex) {
            log.error("home>uploadImage error", ex);
            setErrorResult(result, ex.getMessage());
        }
        return result;
    }

    @ApiOperation(value = "发送图片回答")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
    })
    @PostMapping("/sendImgUser")
    public Map<String, Object> sendImgUser(@ApiParam(hidden = true) @RequestParam Map<String, Object> params, @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        try {
            if (orderid == 0) {
                setOkResult(result, "参数错误");
            } else {
                if (file != null) {
                    BufferedImage image = ImageIO.read(file.getInputStream());
                    if (image != null) {
                        String filename = file.getOriginalFilename();
                        String type = filename.substring(filename.lastIndexOf("."));
                        String key = "syh" + UnixUtil.getCustomRandomString() + type;
                        QiniuUtils.putFile(ConfigModel.QINIU.BUCKET, key, file.getInputStream());
                        String filePath = ConfigModel.BASEFILEPATH + FileUtil.setFileName(FileUtil.FILE_LONG_PATH, key);
                        if (FileUtil.validateFile(filePath)) {
                            FileUtil.delFile(filePath);
                        }
                        FileUtil.saveFile(file.getBytes(), filePath);
                        result.put("data", userVideoService.addAppGreen(orderid, key, QAContentTypeEnum.Picture.getCode(), 0));
                        setOkResult(result, "添加成功");
                    } else {
                        setErrorResult(result, "请选择正确的图片");
                    }
                } else {
                    setErrorResult(result, "请上传文件");
                }
            }
        } catch (Exception ex) {
            log.error("home>uploadImage error", ex);
            setErrorResult(result, ex.getMessage());
        }
        return result;
    }

    @ApiOperation(value = "发送文字回答")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "content", value = "发送内容", required = true, dataType = "String"),
    })
    @PostMapping("/sendTextUser")
    public Map<String, Object> sendTextUser(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        String content = ModelUtil.getStr(params, "content");
        if (orderid == 0) {
            setOkResult(result, "参数错误");
        } else {
            result.put("data", userVideoService.addAppGreen(orderid, content, QAContentTypeEnum.Text.getCode(), 0));
            setOkResult(result, "添加成功");
        }
        return result;
    }



}
