package com.syhdoctor.webserver.controller.webapp.appapi.doctor.answer;

import com.syhdoctor.common.utils.EnumUtils.OrderTypeEnum;
import com.syhdoctor.common.utils.EnumUtils.QAContentTypeEnum;
import com.syhdoctor.common.utils.FileUtil;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.service.answer.AnswerService;
import com.syhdoctor.webserver.utils.QiniuUtils;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

@Api(description = "/App/doctorAnswer 医生咨询医生端接口")
@RestController
@RequestMapping("/App/doctorAnswer")
public class AppDoctorAnswerController extends BaseController {

    @Autowired
    private AnswerService answerService;

    @ApiOperation(value = "订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "intotype", value = "进入类型（1:全部，2:首页待接诊,进行中）", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "ordertype", value = "订单类型（1:问诊，2:电话）", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "state", value = "订单状态（2:带接诊，6:待回复，0：全部）", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "当前页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页数据量", defaultValue = "20", dataType = "String")
    })
    @PostMapping("/OrderList")
    public Map<String, Object> answerOrderList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        int intotype = ModelUtil.getInt(params, "intotype", 1);
        int ordertype = ModelUtil.getInt(params, "ordertype");
        int state = ModelUtil.getInt(params, "state");
        int pageindex = ModelUtil.getInt(params, "pageindex", 1);
        int pagesize = ModelUtil.getInt(params, "pagesize", 20);
        if (doctorid == 0 && ordertype == 0) {
            setErrorResult(result, "参数错误");
        } else {
            if (ordertype == OrderTypeEnum.Answer.getCode()) {
                result.put("data", answerService.appDoctorAnswerOrderList(doctorid, state, intotype, pageindex, pagesize));
            } else if (ordertype == OrderTypeEnum.Phone.getCode()) {
                result.put("data", answerService.appDoctorPhoneOrderList(doctorid, state, intotype, pageindex, pagesize));
            } else if (ordertype == OrderTypeEnum.Video.getCode()) {
                result.put("data", answerService.appDoctorVideoOrderList(doctorid, state, intotype, pageindex, pagesize));
            }
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
    @PostMapping("/getDoctorAnswerList")
    public Map<String, Object> getDoctorAnswerList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        int pageindex = ModelUtil.getInt(params, "pageindex", 1);
        int pagesize = ModelUtil.getInt(params, "pagesize", 20);
        if (orderid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", answerService.getDoctorAnswerList(orderid, pageindex, pagesize,1));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "追加的消息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单编号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "id", value = "消息id", required = true, dataType = "String"),
    })
    @PostMapping("/getAppendDoctorAnswerList")
    public Map<String, Object> getAppendDoctorAnswerList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        long id = ModelUtil.getLong(params, "id");
        if (orderid == 0 && id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", answerService.getAppendDoctorAnswerList(orderid, id));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "订单详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单编号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "当前页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页数据量", defaultValue = "20", dataType = "String")
    })
    @PostMapping("/getDoctorAnswerListNew")
    public Map<String, Object> getDoctorAnswerListNew(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        int pageindex = ModelUtil.getInt(params, "pageindex", 1);
        int pagesize = ModelUtil.getInt(params, "pagesize", 20);
        if (orderid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", answerService.getDoctorAnswerListNew(orderid, pageindex, pagesize));
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
                    result.put("data", answerService.addAppAnswer(orderid, key, QAContentTypeEnum.Voice.getCode(), contenttime, 1));
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
                        result.put("data", answerService.addAppAnswer(orderid, key, QAContentTypeEnum.Picture.getCode(), 1));
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
            result.put("data", answerService.addAppAnswer(orderid, content, QAContentTypeEnum.Text.getCode(), 1));
            setOkResult(result, "添加成功");
        }
        return result;
    }

    /*@ApiOperation(value = "发送处方")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "presno", value = "处方编号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "oftenprescriptionid", value = "常用处方id", required = true, dataType = "String"),
    })
    @PostMapping("/sendPrescription")
    public Map<String, Object> sendPrescription(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        String presNo = ModelUtil.getStr(params, "presno");
        long oftenPrescriptionId = ModelUtil.getLong(params, "oftenprescriptionid");
        if (orderid == 0 || StrUtil.isEmpty(presNo)) {
            setErrorResult(result, "参数错误");
        } else {
            Map<String, Object> map = answerService.sendPrescription(orderid, presNo, oftenPrescriptionId);
//            long id = ModelUtil.getLong(map, "id");
//            answerService.sendSocket(id);
            result.put("data", map);
            setOkResult(result, "添加成功");
        }
        return result;
    }*/


    @ApiOperation(value = "订单详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
    })
    @PostMapping("/getDoctorAnswerDetail")
    public Map<String, Object> getDoctorAnswerDetail(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        if (orderid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", answerService.getUserProblemDetailed(orderid));
        }
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "医生结束订单")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
    })
    @PostMapping("/confirmCloseOrder")
    public Map<String, Object> confirmCloseOrder(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        if (orderid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", answerService.confirmCloseOrder(orderid));
        }
        setOkResult(result, "查询成功");
        return result;
    }

}
