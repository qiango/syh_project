package com.syhdoctor.webserver.controller.webadmin.greenhospital;

import com.syhdoctor.common.utils.EnumUtils.GreenOrderStateEnum;
import com.syhdoctor.common.utils.EnumUtils.QAContentTypeEnum;
import com.syhdoctor.common.utils.FileUtil;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.service.greenhospital.GreenHospitalService;
import com.syhdoctor.webserver.service.video.UserVideoService;
import com.syhdoctor.webserver.utils.QiniuUtils;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.jws.WebParam;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description = "/Admin/greenhospital 绿通医院")
@RestController
@RequestMapping("/Admin/greenhospital")
public class AdminGreenHospitalController extends BaseController {

    @Autowired
    private GreenHospitalService greenHospitalService;

    @Autowired
    private UserVideoService userVideoService;


    @ApiOperation(value = "绿通医院列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "hospitalname", value = "医院名称", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "hospitalphone", value = "医院电话", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "hospitallevel", value = "医院等级", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "categoryid", value = "医院类别id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "departmentid", value = "科室id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getGreenHospitalList")
    public Map<String, Object> getGreenHospitalList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String hospitalname = ModelUtil.getStr(params, "hospitalname");
        String hospitalphone = ModelUtil.getStr(params, "hospitalphone");
        String hospitallevel = ModelUtil.getStr(params, "hospitallevel");
        long categoryid = ModelUtil.getLong(params, "categoryid");
        long departmentid = ModelUtil.getLong(params, "departmentid");
        int pageindex = ModelUtil.getInt(params, "pageindex", 1);
        int pagesize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", greenHospitalService.getGreenHospitalList(hospitalname, hospitalphone, hospitallevel, categoryid, departmentid, pageindex, pagesize));
        result.put("total", greenHospitalService.getGreenHospitalListCount(hospitalname, hospitalphone, hospitallevel, categoryid, departmentid));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "添加or修改绿通医院")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "hospitalid", value = "医院id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "hospitalname", value = "医院名称", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "hospitaladdress", value = "医院地址", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "hospitalphone", value = "医院电话", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "hospitalLevel", value = "医院等级", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "categoryids", value = "医院类别id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "departmentids", value = "科室id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "hospitalintroduce", value = "医院简介", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "hospitalpicture", value = "图片", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "area", value = "地区", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "hospitalpicturebig", value = "大图", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "hospitalpicturesmall", value = "小图", required = true, dataType = "String"),
    })
    @PostMapping("/updateAddGreenHospital")
    public Map<String, Object> updateAddGreenHospital(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long hospitalid = ModelUtil.getLong(params, "hospitalid");
        String hospitalname = ModelUtil.getStr(params, "hospitalname");
        String hospitaladdress = ModelUtil.getStr(params, "hospitaladdress");
        String hospitalphone = ModelUtil.getStr(params, "hospitalphone");
        String hospitalintroduce = ModelUtil.getStr(params, "hospitalintroduce");
        int hospitallevel = ModelUtil.getInt(params, "hospitalLevel");
        String hospitalpicturebig = ModelUtil.getStr(params, "hospitalpicturebig");
        String hospitalpicturesmall = ModelUtil.getStr(params, "hospitalpicturesmall");
        long categoryid = ModelUtil.getLong(params, "category");
        List<?> departmentids = ModelUtil.getList(params, "department", new ArrayList<>());
        List<?> areas = ModelUtil.getList(params, "areas", new ArrayList<>());
//        List<Long> categoryidss = new ArrayList<>();
//        if (categoryids.size() > 0) {
//            for (Object value : categoryids) {
//                categoryidss.add(Long.parseLong(value.toString()));
//            }
//        }
        List<Long> departmentidss = new ArrayList<>();
        if (departmentids.size() > 0) {
            for (Object value : departmentids) {
                departmentidss.add(Long.parseLong(value.toString()));
            }
        }
        if (areas.size() <= 0) {
            setErrorResult(result, "地区为必填字段");
        }
        result.put("data", greenHospitalService.updateAddGreenHospital(hospitalid, categoryid, departmentidss, hospitalname, hospitaladdress, hospitalphone, hospitalintroduce, hospitallevel, areas, hospitalpicturebig, hospitalpicturesmall));
        setOkResult(result, "操作成功");
        return result;
    }

    @ApiOperation(value = "删除绿通医院")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "hospitalid", value = "医院id", dataType = "String"),
    })
    @PostMapping("/delGreenHospital")
    public Map<String, Object> delGreenHospital(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int id = ModelUtil.getInt(params, "hospitalid");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", greenHospitalService.delGreenHospital(id));
            setOkResult(result, "查询成功");
        }
        return result;
    }


    @ApiOperation(value = "查看地区")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "地区id,查询省份id=0", dataType = "String"),
    })
    @PostMapping("/getAreas")
    public Map<String, Object> getAreaByParentId(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int code = ModelUtil.getInt(params, "id", 0);
        result.put("data", greenHospitalService.getAreaByParentId(code));
        setOkResult(result, "查询成功");
        return result;
    }


    @ApiOperation(value = "查询医院类别")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "hospitalid", value = "医院id", required = true, dataType = "String"),
    })
    @PostMapping("/hospitalCategory")
    public Map<String, Object> hospitalCategory(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long hospitalid = ModelUtil.getLong(params, "hospitalid");
        result.put("data", greenHospitalService.hospitalCategory(hospitalid));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "查询医院科室")
    @PostMapping("/departmentGreen")
    public Map<String, Object> departmentGreen(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", greenHospitalService.departmentGreens());
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "查询医院等级")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "hospitalid", value = "医院id", required = true, dataType = "String"),
    })
    @PostMapping("/hospitalLevel")
    public Map<String, Object> hospitalLevel(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
//        long hospitalid = ModelUtil.getLong(params, "hospitalid");
        result.put("data", greenHospitalService.hospitalLevel(0));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "医院详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "hospitalid", value = "医院id", required = true, dataType = "String"),
    })
    @PostMapping("/getGreenHospitalId")
    public Map<String, Object> getGreenHospitalId(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long hospitalid = ModelUtil.getLong(params, "hospitalid");
        if (hospitalid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", greenHospitalService.getGreenHospitalId(hospitalid));
            setOkResult(result, "查询成功");
        }
        return result;
    }


    /*
    绿通订单
     */
    @ApiOperation(value = "绿通订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "username", value = "用户姓名", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "phone", value = "电话", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "patientname", value = "就诊人姓名", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "status", value = "状态", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "begintime", value = "开始时间", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "结束时间", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getGreenOrderList")
    public Map<String, Object> getGreenOrderList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String username = ModelUtil.getStr(params, "username");
        String phone = ModelUtil.getStr(params, "phone");
        String patientname = ModelUtil.getStr(params, "patientname");
        int status = ModelUtil.getInt(params, "status");
        long begintime = ModelUtil.getLong(params, "begintime");
        long endtime = ModelUtil.getLong(params, "endtime");
        int pageindex = ModelUtil.getInt(params, "pageindex", 1);
        int pagesize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", greenHospitalService.getGreenOrderList(username, phone, patientname, status, begintime, endtime, pageindex, pagesize));
        result.put("total", greenHospitalService.getGreenOrderListCount(username, phone, patientname, status, begintime, endtime));
        setOkResult(result, "查询成功");
        return result;
    }


    @ApiOperation(value = "修改状态")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "编号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "status", value = "状态", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "failreason", value = "失败原因", required = true, dataType = "String"),
    })
    @PostMapping("/updateStatus")
    public Map<String, Object> updateStatus(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        int status = ModelUtil.getInt(params, "status");
        String failreason = ModelUtil.getStr(params, "failreason");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else if (status == GreenOrderStateEnum.OrderFail.getCode() && failreason == null) {
            setErrorResult(result, "失败原因不能为空");
        } else if (status == GreenOrderStateEnum.UnPaid.getCode() && failreason == null) {
            setErrorResult(result, "取消原因不能为空");
        } else {
            result.put("data", greenHospitalService.updateStatus(id, status, failreason));
            setOkResult(result, "修改成功");
        }
        return result;
    }

    @ApiOperation(value = "添加就诊信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "编号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "greencontact", value = "姓名", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "greenphone", value = "电话", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "subscribetime", value = "预约时间", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "greenaddress", value = "预约地址", required = true, dataType = "String"),
    })
    @PostMapping("/updateGreenInformation")
    public Map<String, Object> updateGreenInformation(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        String greencontact = ModelUtil.getStr(params, "greencontact");
        String greenphone = ModelUtil.getStr(params, "greenphone");
        String subscribetime = ModelUtil.getStr(params, "subscribetime");
        String greenaddress = ModelUtil.getStr(params, "greenaddress");
        String introduction = ModelUtil.getStr(params, "introduction");
        List<?> photos = ModelUtil.getList(params, "photos", new ArrayList<>()); //身份证
        List<?> pictures = ModelUtil.getList(params, "pictures", new ArrayList<>());//照片
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else if (photos.size() > 2) {
            setErrorResult(result, "身份证必须传入正反两面！");
        } else {
            result.put("data", greenHospitalService.updateGreenInformation(id, greencontact, greenphone, subscribetime, greenaddress, introduction, photos, pictures));
            setOkResult(result, "修改成功");
        }
        return result;
    }


    @ApiOperation(value = "绿通订单详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "编号", required = true, dataType = "String"),
    })
    @PostMapping("/getGreenOrderId")

    public Map<String, Object> getGreenOrderId(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", greenHospitalService.getGreenOrderId(id));
            setOkResult(result, "修改成功");
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


    @ApiOperation(value = "绿通医院类别列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "categoryname", value = "医院类别名", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getHospitalCategoryList")
    public Map<String, Object> getHospitalCategoryList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String categoryname = ModelUtil.getStr(params, "categoryname");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", greenHospitalService.getHospitalCategoryList(categoryname, pageIndex, pageSize));
        result.put("total", greenHospitalService.getHospitalCategoryListCount(categoryname));
        setOkResult(result, "添加成功");
        return result;
    }

    @ApiOperation(value = "绿通医院类别详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", required = true, dataType = "String"),
    })
    @PostMapping("/getHospitalCategoryId")
    public Map<String, Object> getHospitalCategoryId(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setOkResult(result, "参数错误");
        } else {
            result.put("data", greenHospitalService.getHospitalCategoryId(id));
            setOkResult(result, "添加成功");
        }
        return result;
    }

    @ApiOperation(value = "删除绿通医院类别")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", required = true, dataType = "String"),
    })
    @PostMapping("/delHospitalCategory")
    public Map<String, Object> delHospitalCategory(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setOkResult(result, "参数错误");
        } else {
            result.put("data", greenHospitalService.delHospitalCategory(id));
            setOkResult(result, "添加成功");
        }
        return result;
    }


    @ApiOperation(value = "修改or新增绿通医院类别")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "categoryname", value = "医院类别名", required = true, dataType = "String"),
    })
    @PostMapping("/updateAddHospitalCategory")
    public Map<String, Object> updateAddHospitalCategory(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        String categoryname = ModelUtil.getStr(params, "categoryname");
        result.put("data", greenHospitalService.updateAddHospitalCategory(id, categoryname));
        setOkResult(result, "操作成功");
        return result;
    }


    /*
    科室
     */
    @ApiOperation(value = "绿通科室列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "departmentname", value = "科室名", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getDepartmentGreenList")
    public Map<String, Object> getDepartmentGreenList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String departmentname = ModelUtil.getStr(params, "departmentname");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", greenHospitalService.getDepartmentGreenList(departmentname, pageIndex, pageSize));
        result.put("total", greenHospitalService.getDepartmentGreenListCount(departmentname));
        setOkResult(result, "添加成功");
        return result;
    }

    @ApiOperation(value = "绿通科室详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", required = true, dataType = "String"),
    })
    @PostMapping("/getDepartmentGreenId")
    public Map<String, Object> getDepartmentGreenId(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setOkResult(result, "参数错误");
        } else {
            result.put("data", greenHospitalService.getDepartmentGreenId(id));
            setOkResult(result, "添加成功");
        }
        return result;
    }

    @ApiOperation(value = "删除绿通科室")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", required = true, dataType = "String"),
    })
    @PostMapping("/delDepartmentGreen")
    public Map<String, Object> delDepartmentGreen(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setOkResult(result, "参数错误");
        } else {
            result.put("data", greenHospitalService.delDepartmentGreen(id));
            setOkResult(result, "添加成功");
        }
        return result;
    }


    @ApiOperation(value = "修改or新增绿通科室")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "departmentname", value = "科室名", required = true, dataType = "String"),
    })
    @PostMapping("/updateAddDepartmentGreen")
    public Map<String, Object> updateAddDepartmentGreen(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        String departmentname = ModelUtil.getStr(params, "departmentname");
        result.put("data", greenHospitalService.updateAddDepartmentGreen(id, departmentname));
        setOkResult(result, "操作成功");
        return result;
    }


}
