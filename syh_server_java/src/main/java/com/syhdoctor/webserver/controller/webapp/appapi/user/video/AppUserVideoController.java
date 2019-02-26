package com.syhdoctor.webserver.controller.webapp.appapi.user.video;

import com.syhdoctor.common.pay.IPayService;
import com.syhdoctor.common.utils.EnumUtils.QAContentTypeEnum;
import com.syhdoctor.common.utils.FileUtil;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.exception.ServiceException;
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

@Api(description = "/App/UserVideo 用户视频问诊")
@RestController
@RequestMapping("/App/UserVideo")
public class AppUserVideoController extends BaseController {

    @Autowired
    private UserVideoService userVideoService;

    @Autowired
    private UserWalletService userWalletService;


    @ApiOperation(value = "基础信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", required = true, dataType = "String"),
    })
    @PostMapping("getBasicInformation")
    public Map<String, Object> getBasicInformation(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userid = ModelUtil.getLong(params, "userid");
        if (userid == 0) {
            setErrorResult(result, "参数错误");
            return result;
        }
        result.put("data", userVideoService.getBasicInformation(userid));
        setOkResult(result, "查询成功");
        return result;
    }


    /**
     * todo 版本兼容
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "视频下单")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "diseaselist", value = "病症列表", required = true, dataType = "List"),
            @ApiImplicitParam(paramType = "query", name = "schedulingid", value = "排班id", required = true, dataType = "String"),
    })
    @PostMapping("addVideoOrder")
    public Map<String, Object> addVideoOrder(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userId = ModelUtil.getLong(params, "userid");
        long doctorid = ModelUtil.getLong(params, "doctorid");
        List<?> diseaselist = ModelUtil.getList(params, "diseaselist", new ArrayList<>());
        long schedulingid = ModelUtil.getLong(params, "schedulingid");
        if (userId == 0 || doctorid == 0 || schedulingid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            if (diseaselist.size() == 0) {
                throw new ServiceException("请选择症状");
            } else {
                result.put("data", userVideoService.addVideoOrder(userId, doctorid, diseaselist, schedulingid));
                setOkResult(result, "下单成功");
            }
        }
        return result;
    }


    /**
     * todo 版本兼容
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "医生排班列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", required = true, dataType = "String"),
    })
    @PostMapping("getDoctorSchedulingList")
    public Map<String, Object> getDoctorSchedulingList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        if (doctorid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userVideoService.getDoctorSchedulingList(doctorid));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "订单详细")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderId", value = "订单id", required = true, dataType = "String"),
    })
    @PostMapping("getVideoOrder")
    public Map<String, Object> getVideoOrder(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderId = ModelUtil.getLong(params, "orderid");
        if (orderId == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userVideoService.getVideoOrder(orderId));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "进入视频通话")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderId", value = "订单id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "userdevicecode", value = "用户设备码,只能当前设备码能视频通话", required = true, dataType = "String"),
    })
    @PostMapping("/userIntoVideo")
    public Map<String, Object> intoVideo(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderId = ModelUtil.getLong(params, "orderid");
        String userdevicecode = ModelUtil.getStr(params, "userdevicecode");
        if (orderId == 0 || StrUtil.isEmpty(userdevicecode)) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userVideoService.userIntoVideo(orderId, userdevicecode));
            setOkResult(result, "查询成功");
        }

        return result;
    }


    @ApiOperation(value = "用户视频预约数据")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", required = true, dataType = "String"),
    })
    @PostMapping("getUserSubscribe")
    public Map<String, Object> getUserSubscribe(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userid = ModelUtil.getLong(params, "userid");
        if (userid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userVideoService.getUserSubscribe(userid));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "用户视频预约数量")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", required = true, dataType = "String"),
    })
    @PostMapping("getUserSubscribeCount")
    public Map<String, Object> getUserSubscribeCount(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userid = ModelUtil.getLong(params, "userid");
        if (userid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userVideoService.getUserSubscribeCount(userid));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "查看评价")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "ordertype", value = "订单类型", required = true, dataType = "String"),
    })
    @PostMapping("getOrderEvaluate")
    public Map<String, Object> getOrderEvaluate(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        int ordertype = ModelUtil.getInt(params, "ordertype");
        if (orderid == 0 || ordertype == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userVideoService.getOrderEvaluate(orderid, ordertype));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "添加评价")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "ordertype", value = "订单类型", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "isanonymous", value = "是否匿名", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "evaluate", value = "评星", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "content", value = "评价", required = true, dataType = "String"),
    })
    @PostMapping("addOrderEvaluate")
    public Map<String, Object> addOrderEvaluate(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        int ordertype = ModelUtil.getInt(params, "ordertype");
        int isanonymous = ModelUtil.getInt(params, "isanonymous");
        int evaluate = ModelUtil.getInt(params, "evaluate");
        String content = ModelUtil.getStr(params, "content");
        if (orderid == 0 || ordertype == 0 || evaluate == 0 || StrUtil.isEmpty(content)) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userVideoService.addOrderEvaluate(orderid, ordertype, isanonymous, evaluate, content));
            setOkResult(result, "评价成功");
        }
        return result;
    }

    @ApiOperation(value = "查看诊断")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "ordertype", value = "订单类型", required = true, dataType = "String"),
    })
    @PostMapping("getOrderGuidance")
    public Map<String, Object> getOrderGuidance(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        int ordertype = ModelUtil.getInt(params, "ordertype");
        if (orderid == 0 || ordertype == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userVideoService.getOrderGuidance(orderid, ordertype));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    /**
     * 急诊订单支付宝支付
     */
    @ApiOperation(value = "视频订单钱包支付")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
    })
    @PostMapping("/videoWalletPay")
    public Map<String, Object> videoWalletPay(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderId = ModelUtil.getLong(params, "orderid");
        if (orderId == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userVideoService.videoWalletPay(orderId));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    /**
     * 添加问诊订单支付宝支付
     */
    @ApiOperation(value = "视频订单支付宝支付")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
    })
    @PostMapping("/videoAliPay")
    public Map<String, Object> videoAliPay(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderId = ModelUtil.getLong(params, "orderid");
        if (orderId == 0) {
            setErrorResult(result, "参数错误");
        } else {
            IPayService.PayBean pay = userVideoService.videoAliAppPay(orderId);
            if (pay.isState()) {
                result.put("data", pay);
                setOkResult(result, "支付成功");
            } else {
                setErrorResult(result, "支付失败");
            }
        }
        return result;
    }


    /**
     * 添加问诊订单微信支付
     */
    @ApiOperation(value = "视频订单微信支付")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
    })
    @PostMapping("/videoWeChatPay")
    public Map<String, Object> videoWeChatPay(@ApiParam(hidden = true) @RequestParam Map<String, Object> params, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        long orderId = ModelUtil.getLong(params, "orderid");
        if (orderId == 0) {
            setErrorResult(result, "参数错误");
        } else {
            String ip = request.getRemoteAddr();
            IPayService.PayBean pay = userVideoService.videoWeChatAppPay(orderId, ip);
            if (pay.isState()) {
                result.put("data", getResultByApp(pay));
                setOkResult(result, "支付成功");
            } else {
                setErrorResult(result, "支付失败");
            }
        }
        return result;
    }

    /**
     * 问诊订单微信支付状态查询
     */
    @ApiOperation(value = "问诊订单支付状态查询")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
    })
    @PostMapping("/videoPayStatus")
    public Map<String, Object> videoPayStatus(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderId = ModelUtil.getLong(params, "orderid");
        if (orderId == 0) {
            setErrorResult(result, "参数错误");
        } else {
            Map<String, Integer> data = new HashMap<>();
            data.put("paystatus", userVideoService.videoPayStatus(orderId));
            result.put("data", data);
            setOkResult(result, "支付成功");
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

    @ApiOperation(value = "医生聊天订单详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单编号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "当前页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页数据量", defaultValue = "20", dataType = "String")
    })
    @PostMapping("/getDoctorGreenList")
    public Map<String, Object> getDoctorGreenList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        int pageindex = ModelUtil.getInt(params, "pageindex", 1);
        int pagesize = ModelUtil.getInt(params, "pagesize", 20);
        if (orderid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userVideoService.getDoctorGreenList(orderid, pageindex, pagesize));
            setOkResult(result, "查询成功");
        }
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
    @PostMapping("/sendVoice")
    public Map<String, Object> sendVoice(@ApiParam(hidden = true) @RequestParam Map<String, Object> params, @RequestParam("file") MultipartFile file) {
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
                    result.put("data", userVideoService.addAppGreen(orderid, key, QAContentTypeEnum.Voice.getCode(), contenttime, 1));
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
    @PostMapping("/sendImg")
    public Map<String, Object> sendImg(@ApiParam(hidden = true) @RequestParam Map<String, Object> params, @RequestParam("file") MultipartFile file) {
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
                        result.put("data", userVideoService.addAppGreen(orderid, key, QAContentTypeEnum.Picture.getCode(), 1));
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
    @PostMapping("/sendText")
    public Map<String, Object> sendText(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        String content = ModelUtil.getStr(params, "content");
        if (orderid == 0) {
            setOkResult(result, "参数错误");
        } else {
            result.put("data", userVideoService.addAppGreen(orderid, content, QAContentTypeEnum.Text.getCode(), 1));
            setOkResult(result, "添加成功");
        }
        return result;
    }

    @ApiOperation(value = "确认关闭")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "isclose", value = "是否确认关闭", required = true, dataType = "String"),
    })
    @PostMapping("/confirmClose")
    public Map<String, Object> confirmClose(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        int isclose = ModelUtil.getInt(params, "isclose");
        if (orderid == 0) {
            setOkResult(result, "参数错误");
        } else {
            result.put("data", userVideoService.confirmClose(orderid, isclose));
            setOkResult(result, "添加成功");
        }
        return result;
    }

    @ApiOperation(value = "主动关闭")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
    })
    @PostMapping("/userCloseOrder")
    public Map<String, Object> userCloseOrder(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        if (orderid == 0) {
            setOkResult(result, "参数错误");
        } else {
            result.put("data", userVideoService.userCloseOrder(orderid));
            setOkResult(result, "添加成功");
        }
        return result;
    }
}
