package com.syhdoctor.webserver.controller.webapp.webapi.answer;

import com.syhdoctor.common.pay.IPayService;
import com.syhdoctor.common.utils.EnumUtils.OrderTypeEnum;
import com.syhdoctor.common.utils.EnumUtils.QAContentTypeEnum;
import com.syhdoctor.common.utils.FileUtil;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.service.answer.AnswerService;
import com.syhdoctor.webserver.service.answer.PhoneService;
import com.syhdoctor.webserver.service.doctor.DoctorPhoneService;
import com.syhdoctor.webserver.service.vipcard.VipCardService;
import com.syhdoctor.webserver.service.wallet.RechargeableOrderService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description = "/Web/UserAnswer Web医生咨询用户端接口")
@RestController
@RequestMapping("/Web/UserAnswer")
public class WebUserAnswerController extends BaseController {

    @Autowired
    private AnswerService answerService;

    @Autowired
    private DoctorPhoneService doctorPhoneService;

    @Autowired
    private RechargeableOrderService rechargeableOrderService;

    @Autowired
    private VipCardService vipCardService;

    @Autowired
    private PhoneService phoneService;


    /**
     * todo 版本兼容
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "带层级常见病症状列表")
    @ApiImplicitParams({
    })
    @PostMapping("/getAppSymptomsTypeList")
    public Map<String, Object> getAppSymptomsTypeList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", answerService.getAppSymptomsTypeList());
        setOkResult(result, "查询成功");
        return result;
    }

    /**
     * 添加问诊订单
     */
    @ApiOperation(value = "下单")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "ordertype", value = "订单类型", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "diseaselist", value = "常见病列表", dataType = "List"),
            @ApiImplicitParam(paramType = "query", name = "schedulingid", value = "排班id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "disdescribe", value = "病情描述", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "picturelist", value = "图片列表", dataType = "List"),
            @ApiImplicitParam(paramType = "query", name = "diseasetimeid", value = "患病时长id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "gohospital", value = "是否去过医院", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "issuredis", value = "是否确诊", dataType = "String"),
    })
    @PostMapping("/addOrder")
    public Map<String, Object> addOrder(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userId = ModelUtil.getLong(params, "userid");
        long doctorId = ModelUtil.getLong(params, "doctorid");
        int ordertype = ModelUtil.getInt(params, "ordertype");
        List<?> diseaselist = ModelUtil.getList(params, "diseaselist", new ArrayList<>());
        long schedulingid = ModelUtil.getLong(params, "schedulingid");
        String disdescribe = ModelUtil.getStr(params, "disdescribe");
        List<?> picturelist = ModelUtil.getList(params, "picturelist", new ArrayList<>());
        long diseasetimeid = ModelUtil.getLong(params, "diseasetimeid");
        int gohospital = ModelUtil.getInt(params, "gohospital", -1);
        int issuredis = ModelUtil.getInt(params, "issuredis", -1);
        if (userId == 0 || ordertype == 0 || StrUtil.isEmpty(disdescribe) || diseasetimeid == 0 || gohospital == -1) {
            setErrorResult(result, "参数错误");
            return result;
        }
        if (gohospital > 0 && issuredis == -1) {
            setErrorResult(result, "参数错误");
            return result;
        }
        result.put("data", answerService.addOrder(userId, doctorId, ordertype, diseaselist, schedulingid, disdescribe, picturelist, diseasetimeid, gohospital, issuredis));
        setOkResult(result, "下单成功");
        return result;
    }


    /**
     * 添加问诊订单
     */
    @ApiOperation(value = "用户订单信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "ordertype", value = "订单类型", dataType = "String"),
    })
    @PostMapping("/getUserOrderInfo")
    public Map<String, Object> getUserOrderInfo(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userId = ModelUtil.getLong(params, "userid");
        long doctorId = ModelUtil.getLong(params, "doctorid");
        int ordertype = ModelUtil.getInt(params, "ordertype");
        if (userId == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", answerService.getUserOrderInfo(userId, doctorId, ordertype));
            setOkResult(result, "下单成功");
        }
        return result;
    }

    /**
     * 添加问诊订单
     */
    @ApiOperation(value = "下单显示")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "ordertype", value = "订单类型", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "typeid", value = "症状类型id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "symptomsid", value = "专病id", dataType = "String"),
    })
    @PostMapping("/getOrderView")
    public Map<String, Object> getOrderView(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userId = ModelUtil.getLong(params, "userid");
        long doctorId = ModelUtil.getLong(params, "doctorid");
        int ordertype = ModelUtil.getInt(params, "ordertype");
        long typeid = ModelUtil.getLong(params, "typeid");
        long symptomsid = ModelUtil.getLong(params, "symptomsid");
        if (userId == 0 || ordertype == 0) {
            setErrorResult(result, "参数错误");
            return result;
        }
        if (typeid > 0 && symptomsid > 0) {
            setErrorResult(result, "参数错误");
            return result;
        }
        result.put("data", answerService.getOrderView(userId, doctorId, ordertype, typeid, symptomsid));
        setOkResult(result, "下单成功");
        return result;
    }


    /**
     * todo 版本兼容
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "问诊下单")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "diseaselist", value = "常见病列表", required = true, dataType = "String"),
    })
    @PostMapping("/addAnswerOrder")
    public Map<String, Object> addAnswerOrder(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userId = ModelUtil.getLong(params, "userid");
        long doctorId = ModelUtil.getLong(params, "doctorid");
        List<?> diseaselist = ModelUtil.getList(params, "diseaselist", new ArrayList<>());
        if (userId == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", answerService.addAnswerOrder(userId, doctorId, diseaselist));
            setOkResult(result, "下单成功");
        }
        return result;
    }


    /**
     * todo 版本兼容
     * 添加问诊订单
     */
    @ApiOperation(value = "问诊下单(马良专用)")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "diseaselist", value = "常见病列表", required = true, dataType = "String"),
    })
    @PostMapping("/addAnswerOrderNew")
    public Map<String, Object> addAnswerOrderNew(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userId = ModelUtil.getLong(params, "userid");
        long doctorId = ModelUtil.getLong(params, "doctorid");
        List<?> diseaselist = ModelUtil.getList(params, "diseaselist", new ArrayList<>());
        if (userId == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", answerService.addAnswerOrder(userId, doctorId, diseaselist));
            setOkResult(result, "下单成功");
        }
        return result;
    }

    /**
     * 添加问诊订单
     */
    @ApiOperation(value = "急诊下单")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "diseaselist", value = "常见病列表", required = true, dataType = "String"),
    })
    @PostMapping("/addPhoneOrder")
    public Map<String, Object> addPhoneOrder(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userId = ModelUtil.getLong(params, "userid");
        List<?> diseaselist = ModelUtil.getList(params, "diseaselist", new ArrayList<>());
        if (userId == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", answerService.addPhoneOrder(userId, diseaselist));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    /**
     * todo 版本兼容
     * 添加问诊订单
     */
    @ApiOperation(value = "急诊（电话）下单")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "schedulingid", value = "排班id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "diseaselist", value = "症状列表", dataType = "List"),
    })
    @PostMapping("/addDoctorPhoneOrder")
    public Map<String, Object> addDoctorPhoneOrder(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userId = ModelUtil.getLong(params, "userid");
        long doctorId = ModelUtil.getLong(params, "doctorid");
        long schedulingid = ModelUtil.getLong(params, "schedulingid");
        List<?> diseaselist = ModelUtil.getList(params, "diseaselist", new ArrayList<>());
        if (userId == 0 || doctorId == 0 || schedulingid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", answerService.addPhoneOrder(userId, doctorId, diseaselist, schedulingid));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    /**
     * TODO 版本兼容
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "医生电话排班列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", required = true, dataType = "String"),
    })
    @PostMapping("/getDoctorPhoneSchedulingList")
    public Map<String, Object> getDoctorPhoneSchedulingList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        if (doctorid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", phoneService.getDoctorPhoneSchedulingList(doctorid));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    /**
     * 问诊订单微信支付状态查询
     */
    @ApiOperation(value = "订单支付状态查询")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "ordertype", value = "订单类型：1：问诊，2：急诊，3：充值,4：会员卡", required = true, dataType = "String"),
    })
    @PostMapping("/orderPayStatus")
    public Map<String, Object> answerWeChatPayStatus(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderId = ModelUtil.getLong(params, "orderid");
        int ordertype = ModelUtil.getInt(params, "ordertype");
        if (orderId == 0 && ordertype == 0) {
            setErrorResult(result, "参数错误");
        } else {
            Map<String, Integer> data = new HashMap<>();
            if (ordertype == OrderTypeEnum.Answer.getCode()) {
                data.put("paystatus", answerService.answerWeChatPayStatus(orderId));
            } else if (ordertype == OrderTypeEnum.Phone.getCode()) {
                data.put("paystatus", answerService.phoneWeChatPayStatus(orderId));
            } else if (ordertype == OrderTypeEnum.Rechargeable.getCode()) {
                data.put("paystatus", rechargeableOrderService.rechargeableOrderPayStatus(orderId));
            } else if (ordertype == OrderTypeEnum.Vip.getCode()) {
                data.put("paystatus", vipCardService.vipOrderPayStatus(orderId));
            }
            result.put("data", data);
            setOkResult(result, "支付成功");
        }
        return result;
    }

    /**
     * 下单详细
     */
    @ApiOperation(value = "下单详细")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "用户id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "ordertype", value = "1：问诊，2：急诊", dataType = "String"),
    })
    @PostMapping("/getOrder")
    public Map<String, Object> getOrder(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderId = ModelUtil.getLong(params, "orderid");
        int ordedType = ModelUtil.getInt(params, "ordertype");
        if (orderId == 0 || ordedType == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", answerService.getOrder(orderId, ordedType));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    /**
     * 下单详细
     */
    @ApiOperation(value = "下单详细")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "用户id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "ordertype", value = "1：问诊，2：电话：3：视频，4：会员卡", dataType = "String"),
    })
    @PostMapping("/getOrderDetailed")
    public Map<String, Object> getOrderDetailed(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderId = ModelUtil.getLong(params, "orderid");
        int ordedType = ModelUtil.getInt(params, "ordertype");
        if (orderId == 0 || ordedType == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", answerService.getOrderDetailed(orderId, ordedType));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    /**
     * 添加问诊订单支付宝支付
     */
    @ApiOperation(value = "问诊订单支付宝支付")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
    })
    @PostMapping("/answerAliPay")
    public Map<String, Object> answerAliPay(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderId = ModelUtil.getLong(params, "orderid");
        if (orderId == 0) {
            setErrorResult(result, "参数错误");
        } else {
            IPayService.PayBean pay = answerService.answerAliWebPay(orderId);
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
     * 添加问诊订单支付宝支付
     */
    @ApiOperation(value = "问诊订单钱包支付")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
    })
    @PostMapping("/answerWalletPay")
    public Map<String, Object> answerWalletPay(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderId = ModelUtil.getLong(params, "orderid");
        if (orderId == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", answerService.answerWalletPay(orderId));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    /**
     * 急诊订单支付宝支付
     */
    @ApiOperation(value = "急诊订单钱包支付")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
    })
    @PostMapping("/phoneWalletPay")
    public Map<String, Object> phoneWalletPay(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderId = ModelUtil.getLong(params, "orderid");
        if (orderId == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", answerService.phoneWalletPay(orderId));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    /**
     * 急诊订单支付宝支付
     */
    @ApiOperation(value = "急诊订单康养支付")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
    })
    @PostMapping("/phoneKangyangPay")
    public Map<String, Object> phoneKangyangPay(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderId = ModelUtil.getLong(params, "orderid");
        if (orderId == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", answerService.phoneKangyangPay(orderId));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    /**
     * 急诊订单支付宝支付
     */
    @ApiOperation(value = "图文订单康养支付")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
    })
    @PostMapping("/answerKangyangPay")
    public Map<String, Object> answerKangyangPay(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderId = ModelUtil.getLong(params, "orderid");
        if (orderId == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", answerService.answerKangyangPay(orderId));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    /**
     * 急诊订单支付宝支付
     */
    @ApiOperation(value = "急诊订单支付宝支付")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
    })
    @PostMapping("/phoneAliPay")
    public Map<String, Object> phoneAliPay(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderId = ModelUtil.getLong(params, "orderid");
        if (orderId == 0) {
            setErrorResult(result, "参数错误");
        } else {
            IPayService.PayBean pay = answerService.phoneAliWebPay(orderId);
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
    @ApiOperation(value = "添加问诊订单微信支付")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
    })
    @PostMapping("/answerWeChatPay")
    public Map<String, Object> answerWeChatPay(@ApiParam(hidden = true) @RequestParam Map<String, Object> params, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        long orderId = ModelUtil.getLong(params, "orderid");
        if (orderId == 0) {
            setErrorResult(result, "参数错误");
        } else {
            String ip = request.getRemoteAddr();
            IPayService.PayBean pay = answerService.answerWeChatWebPay(orderId, ip);
            if (pay.isState()) {
                result.put("data", getResult(pay));
                setOkResult(result, "支付成功");
            } else {
                setErrorResult(result, "支付失败");
            }
        }
        return result;
    }

    /**
     * 添加急诊订单微信支付
     */
    @ApiOperation(value = "添加急诊订单微信支付")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
    })
    @PostMapping("/phoneWeChatPay")
    public Map<String, Object> phoneWeChatPay(@ApiParam(hidden = true) @RequestParam Map<String, Object> params, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        long orderId = ModelUtil.getLong(params, "orderid");
        if (orderId == 0) {
            setErrorResult(result, "参数错误");
        } else {
            String ip = request.getRemoteAddr();
            IPayService.PayBean pay = answerService.phoneWeChatWebPay(orderId, ip);
            if (pay.isState()) {
                result.put("data", getResult(pay));
                setOkResult(result, "支付成功");
            } else {
                setErrorResult(result, "支付失败");
            }
        }
        return result;
    }

    @ApiOperation(value = "用户提交问题")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "templatelist", value = "问答列表", required = true, dataType = "String"),
    })
    @PostMapping("/submitUserAnser")
    public Map<String, Object> submitUserAnser(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        List<?> templatelist = ModelUtil.getList(params, "templatelist", new ArrayList<>());
        if (templatelist.size() == 0) {
            setErrorResult(result, "请回答问题");
        } else {
            int i = answerService.submitUserAnser(orderid, templatelist);
            if (i == 1) {
                setOkResult(result, "回答成功");
            } else if (i == -1) {
                setErrorResult(result, "不能重复回答");
            } else if (i == -2) {
                setErrorResult(result, "该问题只能有一个答案");
            } else if (i == -3) {
                setErrorResult(result, "当前状态不可回答");
            }
        }
        return result;
    }

    /**
     * @param params
     * @return
     */
    @ApiOperation(value = "用户回答问题")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "templateid", value = "模板id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "answeridlist", value = "答案id列表", defaultValue = "1", dataType = "String"),
    })
    @PostMapping("/addUserAnserNew")
    public Map<String, Object> addUserAnserNew(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        long templateid = ModelUtil.getLong(params, "templateid");
        List<?> answeridlist = ModelUtil.getList(params, "answeridlist", new ArrayList<>());
        int i = answerService.addUserAnser(orderid, templateid, answeridlist);
        if (i == 1) {
            setOkResult(result, "回答成功");
        } else if (i == -2) {
            setErrorResult(result, "该问题只能有一个答案");
        } else if (i == -3) {
            setErrorResult(result, "当前状态不可回答");
        }
        return result;
    }

    /**
     * @param params
     * @return
     */
    @ApiOperation(value = "用户回答问题")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "templateid", value = "模板id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "answerid", value = "答案id", defaultValue = "1", dataType = "String"),
    })
    @PostMapping("/addUserAnser")
    public Map<String, Object> addUserAnser(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        long templateid = ModelUtil.getLong(params, "templateid");
        long answerid = ModelUtil.getLong(params, "answerid");
        Map<String, Object> map = answerService.addUserAnser(orderid, templateid, answerid);
        int i = ModelUtil.getInt(map, "status");
        if (i == 1) {
            setOkResult(result, "回答成功");
            long id = ModelUtil.getLong(map, "answerid", 0);
            long id1 = ModelUtil.getLong(map, "answerid1", 0);
            if (id > 0) {
                answerService.sendSocket(id);
            }

            if (id1 > 0) {
                answerService.sendSocket(id1);
            }
        } else if (i == -1) {
            setErrorResult(result, "不能重复回答");
        } else if (i == -2) {
            setErrorResult(result, "该问题只能有一个答案");
        } else if (i == -3) {
            setErrorResult(result, "当前状态不可回答");
        }
        return result;
    }

    @ApiOperation(value = "用户取消订单")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String")
    })
    @PostMapping("/closeOrderUserAnswer")
    public Map<String, Object> closeOrderUserAnswer(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderId = ModelUtil.getLong(params, "orderid");//订单id
        if (orderId == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", answerService.closeOrderUserAnswer(orderId));
            setOkResult(result, "查询成功!");
        }
        return result;
    }


    private Map<String, Object> getResult(IPayService.PayBean payBean) {
        Map<String, Object> result = new HashMap<>();
        result.put("state", payBean.isState());
        result.put("timestamp", payBean.getTimeStamp());
        result.put("noncestr", payBean.getNonceStr());
        result.put("package", payBean.getPackageValue());
        result.put("signtype", payBean.getSignType());
        result.put("paysign", payBean.getPaysign());
        result.put("returnmsg", payBean.getReturnMsg());
        result.put("appid", payBean.getAppId());
        return result;
    }


    @ApiOperation(value = "H5发送语音")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "content", value = "发送内容", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "contenttime", value = "语音时长(秒)", required = true, dataType = "String"),
    })
    @PostMapping("/sendVoice")
    public Map<String, Object> sendVoice(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        String content = ModelUtil.getStr(params, "content");
        long contenttime = ModelUtil.getLong(params, "contenttime");
        if (orderid == 0) {
            setOkResult(result, "参数错误");
        } else {
            result.put("data", answerService.addH5Answer(orderid, content, contenttime, QAContentTypeEnum.Voice.getCode(), 1));
            setOkResult(result, "添加成功");
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
                        result.put("data", answerService.addAppAnswer(orderid, key, QAContentTypeEnum.Picture.getCode(), 0));
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
            result.put("data", answerService.addAppAnswer(orderid, content, QAContentTypeEnum.Text.getCode(), 0));
            setOkResult(result, "添加成功");
        }
        return result;
    }

    /**
     * 我的订单列表
     */
    @ApiOperation(value = "我的订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户编号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "ordertype", value = "订单类型（1:问诊，2:急诊）", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "当前页", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页数据量", dataType = "String"),
    })
    @PostMapping("/myAnswerOrderList")
    public Map<String, Object> myAnswerOrderList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userid = ModelUtil.getLong(params, "userid");//用户id
        int ordertype = ModelUtil.getInt(params, "ordertype");
        int pageindex = ModelUtil.getInt(params, "pageindex", 1);
        int pagesize = ModelUtil.getInt(params, "pagesize", 20);
        if (userid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            if (ordertype == OrderTypeEnum.Answer.getCode()) {
                result.put("data", answerService.appUserAnswerOrderList(userid, pageindex, pagesize));
            } else if (ordertype == OrderTypeEnum.Phone.getCode()) {
                result.put("data", answerService.appUserPhoneOrderList(userid, pageindex, pagesize));
            }
            setOkResult(result, "查询成功!");
        }
        return result;
    }

    //todo 版本兼容
    @ApiOperation(value = "急诊订单详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
    })
    @PostMapping("/getPhoneOrderById")
    public Map<String, Object> getPhoneOrderById(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        if (orderid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorPhoneService.getPhoneOrderById(orderid));
        }
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "订单详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单编号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "当前页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页数据量", defaultValue = "20", dataType = "String")
    })
    @PostMapping("/getUserAnswerList")
    public Map<String, Object> getUserAnswerList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        int pageindex = ModelUtil.getInt(params, "pageindex", 1);
        int pagesize = ModelUtil.getInt(params, "pagesize", 20);
        if (orderid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", answerService.getUserAnswerList(orderid, pageindex, pagesize));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "订单详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
    })
    @PostMapping("/getUserPhoneDetailed")
    public Map<String, Object> getUserPhoneDetailed(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        if (orderid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", answerService.getUserPhoneDetailed(orderid));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "订单详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
    })
    @PostMapping("/getUserAnswerDetailed")
    public Map<String, Object> getUserAnswerDetailed(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        if (orderid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", answerService.getUserAnswerDetailed(orderid));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "用户追加的消息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单编号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "id", value = "消息id", required = true, dataType = "String"),
    })
    @PostMapping("/getAppendUserAnswerList")
    public Map<String, Object> getAppendUserAnswerList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        long id = ModelUtil.getLong(params, "id");
        if (orderid == 0 && id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", answerService.getAppendUserAnswerList(orderid, id));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "用户是否同意结束图文问诊订单")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
    })
    @PostMapping("/userAgreedCloseOrder")
    public Map<String, Object> userAgreenCloseOrder(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        int agree = ModelUtil.getInt(params, "agree");
        if (orderid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", answerService.userAgreenCloseOrder(orderid, agree));
        }
        setOkResult(result, "查询成功");
        return result;
    }
}
