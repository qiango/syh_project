package com.syhdoctor.webserver.controller.webapp.appapi.doctor;

import com.aliyuncs.exceptions.ClientException;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.RegexValidateUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.service.doctor.DoctorService;
import com.syhdoctor.webserver.service.hospital.HospitalService;
import com.syhdoctor.webserver.service.user.UserManagementService;
import com.syhdoctor.webserver.utils.IdCardVerCheckUtil;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description = "/App/Doctor APP医生相关接口")
@RestController
@RequestMapping("/App/Doctor")
public class AppDoctorController extends BaseController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private UserManagementService userManagementService;

    @ApiOperation(value = "新增门诊医生值班")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生编号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "graphicprice", value = "图文价格", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "whetheropen", value = "是否开启 1 开启 0 关闭  状态", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "timelist", value = "时间", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "starttime", value = "开始时间", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "结束时间", required = true, dataType = "String")
    })
    @PostMapping("/addDoctorInquiry")
    public Map<String, Object> addDoctorInquiry(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        BigDecimal graphicPrice = ModelUtil.getDec(params, "graphicprice", BigDecimal.ZERO);
        int whetherOpen = ModelUtil.getInt(params, "whetheropen");
        List<?> timeList = ModelUtil.getList(params, "timelist", new ArrayList<>());
        String startTime = ModelUtil.getStr(params, "starttime");
        String endTime = ModelUtil.getStr(params, "endtime");
        if (doctorid == 0 || StrUtil.isEmpty(startTime, endTime)) {
            setErrorResult(result, "参数错误");
        } else {
            doctorService.addDoctorInquiry(doctorid, graphicPrice, whetherOpen, timeList, startTime, endTime);
            setOkResult(result, "保存成功!");
        }
        return result;
    }

    @ApiOperation(value = "图文排班管理页面")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生编号", required = true, dataType = "String")
    })
    @PostMapping("/getOutpatient")
    public Map<String, Object> getOutpatient(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        if (doctorid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorService.getOutpatient(doctorid));
            setOkResult(result, "保存成功!");
        }
        return result;
    }


    /**
     * 医生诊所
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "查询医生诊所主页")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生编号", required = true, dataType = "String")
    })
    @PostMapping("/getDoctorClinic")
    public Map<String, Object> getDoctorClinic(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        if (doctorid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorService.getDoctorClinic(doctorid));
            setOkResult(result, "查询成功!");
        }
        return result;
    }


    @ApiOperation(value = "新增修改视频医生诊所")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生ID", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "whetheropen", value = "是否开启 1开启 0 未开启", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "price", value = "价格", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "medclassid", value = "咨询类型", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "isupdate", value = "是否修改排班", required = true, dataType = "String")
    })
    @PostMapping("/UpdateAddDoctorClinic")
    public Map<String, Object> UpdateAddDoctorClinic(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorId = ModelUtil.getLong(params, "doctorid");
        int whetheropen = ModelUtil.getInt(params, "whetheropen");
        BigDecimal price = ModelUtil.getDec(params, "price", BigDecimal.ZERO);
        List<?> visiting_start_time = ModelUtil.getList(params, "visitingstarttime", new ArrayList<>());
        int isupdate = ModelUtil.getInt(params, "isupdate");
        int medclassid = 4;
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            setErrorResult(result, "请检查参数!");
        } else {
            doctorService.updateAddDoctorClinic(medclassid, price, doctorId, whetheropen, visiting_start_time, isupdate);
            setOkResult(result, "保存成功!");
        }
        return result;
    }


    @PostMapping("/getHaveSelectTimeVideo")
    public Map<String, Object> getHaveSelectTimeVideo(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorId = ModelUtil.getLong(params, "doctorid");
        if (doctorId == 0) {
            setErrorResult(result, "请检查参数!");
        } else {
            result.put("data", doctorService.getHaveSelectTime(doctorId));
            setOkResult(result, "查询成功!");
        }
        return result;

    }

    @PostMapping("/getHaveSelectTimePhone")
    public Map<String, Object> getHaveSelectTimePhone(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorId = ModelUtil.getLong(params, "doctorid");
        if (doctorId == 0) {
            setErrorResult(result, "请检查参数!");
        } else {
            result.put("data", doctorService.getHaveSelectTimePhone(doctorId));
            setOkResult(result, "查询成功!");
        }
        return result;

    }

    @PostMapping("/findDetailClass")
    public Map<String, Object> findDetailClass(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorId = ModelUtil.getLong(params, "doctorid");
        if (doctorId == 0) {
            setErrorResult(result, "请检查参数!");
        } else {
            result.put("data", doctorService.findDetailClass(doctorId));
            setOkResult(result, "查询成功!");
        }
        return result;
    }

    @PostMapping("/getTimeList")
    public Map<String, Object> getTimeList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        List<?> visiting_start_time = ModelUtil.getList(params, "visitingstarttime", new ArrayList<>());
        result.put("data", doctorService.getTimeList(visiting_start_time));
        setOkResult(result, "查询成功!");
        return result;
    }

    //todo 版本兼容
    @ApiOperation(value = "查询急诊医生排班信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生编号", required = true, dataType = "String")
    })
    @PostMapping("/getOnduty")
    public Map<String, Object> getOnduty(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        if (doctorid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorService.getOnduty(doctorid));
            setOkResult(result, "查询成功!");
        }
        return result;
    }

    @ApiOperation(value = "电话 查询急诊医生排班信息-New")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生编号", required = true, dataType = "String")
    })
    @PostMapping("/getOndutyNew")
    public Map<String, Object> getOndutyNew(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorId = ModelUtil.getLong(params, "doctorid");
        if (doctorId == 0) {
            setErrorResult(result, "请检查参数!");
        } else {
            result.put("data", doctorService.findDetailClassDuty(doctorId));
            setOkResult(result, "查询成功!");
        }
        return result;
    }


    @ApiOperation(value = "添加急诊值班时间---新接口")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生编号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "phoneprice", value = "电话价格", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "timelist", value = "时间列表", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "whetheropen", value = "是否开启", required = true, dataType = "String")
    })
    @PostMapping("/addOndutyNew")
    public Map<String, Object> addOndutyNew(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        List<?> timeList = ModelUtil.getList(params, "timelist", new ArrayList<>());
        int whetheropen = ModelUtil.getInt(params, "whetheropen");
        BigDecimal phonePrice = ModelUtil.getDec(params, "phoneprice", BigDecimal.ZERO);
        int isUpdate = ModelUtil.getInt(params, "isupdate");
        if (phonePrice.compareTo(BigDecimal.ZERO) < 0) {
            setErrorResult(result, "请检查参数!");
        } else {
            doctorService.addUpdaetOndutyNew(doctorid, phonePrice, timeList, whetheropen, isUpdate);
            setOkResult(result, "保存成功!");
        }
        return result;
    }


    /**
     * @param params
     * @return
     */
    @ApiOperation(value = "完善医生信息-新接口")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生ID", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "cardlist", value = "身份证", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "certlist", value = "资格证文件", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "certpraclist", value = "执业证文件", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "titlecertlist", value = "职称证文件", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "digitalsignurl", value = "签名", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "multisitedlicrecordlist", value = "多点执业文件", required = true, dataType = "String")
    })
    @PostMapping("/updateDoctorInfoNew")
    public Map<String, Object> updateDoctorInfoNew(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorId = ModelUtil.getLong(params, "doctorid");
        String idCardpositive = ModelUtil.getStr(params, "idcardpositive");//身份证正面
        String idCardside = ModelUtil.getStr(params, "idcardside");//身份证反面
        String certpositive = ModelUtil.getStr(params, "certpositive");//资格证文件1
        String certside = ModelUtil.getStr(params, "certside");//资格证文件2
        String certprac = ModelUtil.getStr(params, "certprac");//执业证文件
        String titlecert = ModelUtil.getStr(params, "titlecert");//职称证文件
        String titlecertTwo = ModelUtil.getStr(params, "titlecertTwo");//职称证文件2   //todo 2019.1.2新增
        if (doctorId == 0 || StrUtil.isEmpty(idCardpositive, idCardside, certpositive, certside, certprac, titlecert)) {
            setErrorResult(result, "请检查参数!");
        } else {
            result.put("data", doctorService.updateDoctorInfoFinal(doctorId, idCardpositive, idCardside, certpositive, certside, certprac, titlecert, titlecertTwo));
            setOkResult(result, "保存成功!");
        }
        return result;
    }

    @ApiOperation(value = "完善医生信息-新接口2")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生ID", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "digitalsignurl", value = "签名", required = true, dataType = "String"),

    })
    @PostMapping("/updateDoctorInfoNewSign")
    public Map<String, Object> updateDoctorInfoNewSign(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorId = ModelUtil.getLong(params, "doctorid");
        String digitalSignUrl = ModelUtil.getStr(params, "digitalsignurl");//签名
        if (doctorId == 0 || StrUtil.isEmpty(digitalSignUrl)) {
            setErrorResult(result, "请检查参数!");
        } else {
            result.put("data", doctorService.updateSign(doctorId, digitalSignUrl));
            setOkResult(result, "认证需要1-7个工作日，请您耐心等待!");
        }
        return result;
    }

    @ApiOperation(value = "医院列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = "医院名称", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getHospitalList")
    public Map<String, Object> getHospitalList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String hospitalname = ModelUtil.getStr(params, "name");
        long begintime = ModelUtil.getLong(params, "begintime");
        long endtime = ModelUtil.getLong(params, "endtime");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        //分词器
        result.put("data", hospitalService.findHospitalList(hospitalname, begintime, endtime, pageIndex, pageSize));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "查询梯形科室")
    @ApiImplicitParams({
    })
    @PostMapping("/getDeparent")
    public Map<String, Object> getDeparent(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String name = ModelUtil.getStr(params, "name");
        result.put("data", doctorService.getAllDepartmentListNew(name));
        setOkResult(result, "查询成功");
        return result;
    }

    /**
     * @param params
     * @return
     */
    @ApiOperation(value = "完善医生信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生ID", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorname", value = "医生姓名", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "docphotourl", value = "医生头像", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "idcard", value = "身份证号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "gender", value = "性别", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "workinstname", value = "医院名称", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "departmentid", value = "科室departmentid", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "titleid", value = "职称code", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pracno", value = "医师执业证号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "cardlist", value = "身份证", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "certlist", value = "资格证文件", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "certpraclist", value = "执业证文件", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "titlecertlist", value = "职称证文件", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "digitalsignurl", value = "签名", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "professional", value = "擅长领域", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "introduction", value = "个人简介", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "agreeterms", value = "使用协议 1 是 0 否", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "multisitedlicrecordlist", value = "多点执业文件", required = true, dataType = "String")
    })
    @PostMapping("/updateDoctorInfo")
    public Map<String, Object> updateDoctorInfo(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorId = ModelUtil.getLong(params, "doctorid");
        String docName = ModelUtil.getStr(params, "doctorname");
        String docPhotoUrl = ModelUtil.getStr(params, "docphotourl");
        String idCard = ModelUtil.getStr(params, "idcard");
        int gender = ModelUtil.getInt(params, "gender");
        //String workInstCode = ModelUtil.getStr(params, "workinstcode");
        String workInstName = ModelUtil.getStr(params, "workinstname");//医院名称
        int departmentId = ModelUtil.getInt(params, "departmentid");//科室Id
        int titleId = ModelUtil.getInt(params, "titleid");//职称名称
        String pracNo = ModelUtil.getStr(params, "pracno");//医师执业证号
        List<?> cardList = ModelUtil.getList(params, "cardlist", new ArrayList<>());//身份证文件
        List<?> certList = ModelUtil.getList(params, "certlist", new ArrayList<>());//资格证文件
        List<?> certPracList = ModelUtil.getList(params, "certpraclist", new ArrayList<>());//执业证文件
        List<?> titleCertList = ModelUtil.getList(params, "titlecertlist", new ArrayList<>());//职称证文件

        List<?> multiSitedLicRecordList = ModelUtil.getList(params, "multisitedlicrecordlist", new ArrayList<>());//多点执业文件

        String digitalSignUrl = ModelUtil.getStr(params, "digitalsignurl");//签名
        String professional = ModelUtil.getStr(params, "professional");//擅长领域
        String introduction = ModelUtil.getStr(params, "introduction");//个人简介
        int agreeTerms = ModelUtil.getInt(params, "agreeterms");//是否同意互联网医院使用协议

        if (doctorId == 0 || agreeTerms == 0 || gender == 0 || departmentId == 0 || titleId == 0 ||
                StrUtil.isEmpty(docName, docPhotoUrl,
                        idCard, workInstName, pracNo,
                        digitalSignUrl, professional, introduction)
                || cardList.size() == 0 || certList.size() == 0 || certPracList.size() == 0
                || titleCertList.size() == 0) {
            setErrorResult(result, "请检查参数!");
        } else {
            doctorService.updateDoctorInfo(doctorId, docName, docPhotoUrl, idCard, gender,
                    workInstName, departmentId, titleId, pracNo, digitalSignUrl, professional,
                    introduction, agreeTerms, cardList, certList, certPracList, titleCertList, multiSitedLicRecordList);
            setOkResult(result, "保存成功!");
        }
        return result;
    }

    /**
     * 查询科室
     *
     * @return
     */
    @ApiOperation(value = "查询科室")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "value", value = "科室名称", dataType = "String")
    })
    @PostMapping("/getDepartment")
    public Map<String, Object> getDepartment(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String value = ModelUtil.getStr(params, "value");
        result.put("data", doctorService.getLastDepartment(value));
        setOkResult(result, "查询成功");
        return result;
    }

    /**
     * 查询职称
     *
     * @return
     */
    @ApiOperation(value = "查询职称")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "value", value = "职称名称", dataType = "String")
    })
    @PostMapping("/getDoctorTitle")
    public Map<String, Object> getDoctorTitle(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String value = ModelUtil.getStr(params, "value");
        result.put("data", doctorService.getDoctorTitle(value));
        setOkResult(result, "查询成功");
        return result;
    }

    /**
     * 用户登录/注册
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "用户登录/注册-新接口1")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "phone", value = "医生手机号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "code", value = "验证码", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "codetype", value = "类型 1 登录 2 注册", dataType = "String"),
    })
    @PostMapping("/doctorLongRegisterNewOne")
    public Map<String, Object> doctorLongRegisterNew(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        log.info("params>>>" + params);
        String phone = ModelUtil.getStr(params, "phone");
        String code = ModelUtil.getStr(params, "code");
        log.info(">>>>" + code);
        int codeType = ModelUtil.getInt(params, "codetype");
        int agreePlatform = 1;
        if (StrUtil.isEmpty(phone, code)) {
            setErrorResult(result, "请输入手机号和验证码!");

        } else if (agreePlatform == 0 && codeType == 2) {
            setErrorResult(result, "请先同意协议!");
        } else {
            if (!RegexValidateUtil.checkCellphone(phone)) {
                setErrorResult(result, "请填入正确的手机号!");
            } else {
                boolean flag = doctorService.doctotLoginRegisterNew(phone, code, codeType, agreePlatform);
                if (flag) {
                    if (codeType == 1) {
                        Map<String, Object> doctorLogin = doctorService.getDoctorLogin(phone);
                        if (doctorLogin == null) {
                            setErrorResult(result, "请先注册");
                        } else {
                            result.put("data", doctorLogin);
                            setOkResult(result, "登录成功!");
                        }
                    } else if (codeType == 2) {
                        setOkResult(result, "注册成功!");
                        result.put("data", doctorService.getDoctorLogin(phone));
                    }
                }
            }
        }
        log.info(">>>>" + result);
        return result;
    }

    @ApiOperation(value = "用户登录/注册-新接口2")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "gender", value = "性别", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "departmentid", value = "科室departmentid", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "titleid", value = "职称code", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "professional", value = "擅长领域", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "introduction", value = "个人简介", required = true, dataType = "String"),
    })
    @PostMapping("/doctorLongRegisterNewTwo")
    public Map<String, Object> doctorLongRegisterNewTwo(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        log.info("params>>>" + params);
        long doctorid = ModelUtil.getLong(params, "doctorid");
        int gender = ModelUtil.getInt(params, "gender");//性别
        String name = ModelUtil.getStr(params, "doctorname");
        String idcard = ModelUtil.getStr(params, "idcard");//身份证号
        int departmentid = ModelUtil.getInt(params, "departmentid");//科室id
        int titleId = ModelUtil.getInt(params, "titleid");//职称id
        String prac_no = ModelUtil.getStr(params, "pracno");//
        String docPhotoUrl = ModelUtil.getStr(params, "docphotourl");//医生头像
        long hospitalid = ModelUtil.getLong(params, "hospitalid");//医院id
        if (StrUtil.isEmpty(name, idcard, docPhotoUrl, prac_no) || departmentid == 0 || titleId == 0 || hospitalid == 0) {
            setErrorResult(result, "请检查参数!");
            return result;
        }
        boolean validate = IdCardVerCheckUtil.validate(idcard);
        if (!validate) {
            setErrorResult(result, "身份证号不合法，请检查");
            return result;
        }
        boolean flag = doctorService.doctotLoginRegisterNewTwo(docPhotoUrl, prac_no, idcard, hospitalid, doctorid, gender, name, departmentid, titleId);
        result.put("data", flag);
        setOkResult(result, "成功");
        return result;
    }

    @ApiOperation(value = "用户登录/注册-新接口2--邀请码")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "gender", value = "性别", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "departmentid", value = "科室departmentid", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "titleid", value = "职称code", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "professional", value = "擅长领域", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "introduction", value = "个人简介", required = true, dataType = "String"),
    })
    @PostMapping("/doctorLongRegisterNewTwoCode")
    public Map<String, Object> doctorLongRegisterNewTwoCode(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        log.info("params>>>" + params);
        long doctorid = ModelUtil.getLong(params, "doctorid");
        int gender = ModelUtil.getInt(params, "gender");//性别
        String name = ModelUtil.getStr(params, "doctorname");
        String idcard = ModelUtil.getStr(params, "idcard");//身份证号
        int departmentid = ModelUtil.getInt(params, "departmentid");//科室id
        int titleId = ModelUtil.getInt(params, "titleid");//职称id
        String code = ModelUtil.getStr(params, "code");//邀请码
        String docPhotoUrl = ModelUtil.getStr(params, "docphotourl");//医生头像
        long hospitalid = ModelUtil.getLong(params, "hospitalid");//医院id
        if (StrUtil.isEmpty(name, idcard, docPhotoUrl) || departmentid == 0 || titleId == 0 || hospitalid == 0) {
            setErrorResult(result, "请检查参数!");
            return result;
        }
        boolean validate = IdCardVerCheckUtil.validate(idcard);
        if (!validate) {
            setErrorResult(result, "身份证号不合法，请检查");
            return result;
        }
        boolean flag = doctorService.doctotLoginRegisterNewTwoCode(docPhotoUrl, code, idcard, hospitalid, doctorid, gender, name, departmentid, titleId);
        result.put("data", flag);
        setOkResult(result, "成功");
        return result;
    }

    @ApiOperation(value = "用户登录/注册-新接口3")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "gender", value = "性别", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "professional", value = "擅长领域", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "introduction", value = "个人简介", required = true, dataType = "String"),
    })
    @PostMapping("/doctorLongRegisterNewThree")
    public Map<String, Object> doctorLongRegisterNewThree(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        log.info("params>>>" + params);
        long doctorid = ModelUtil.getLong(params, "doctorid");
        String professional = ModelUtil.getStr(params, "professional");//擅长领域
        String introduction = ModelUtil.getStr(params, "introduction");//个人简介
        boolean flag = doctorService.doctotLoginRegisterNewThree(doctorid, professional, introduction);
        result.put("data", flag);
        setOkResult(result, "快去进行处方权认证，认证审核通过后即可开具处方");
        return result;
    }

    @ApiOperation(value = "用户登录/注册查询")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "phone", value = "手机号", required = true, dataType = "String"),
    })
    @PostMapping("/findDoctorInformation")
    public Map<String, Object> findDoctorInformation(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        result.put("data", doctorService.getDoctorLoginInformation(doctorid));
        setOkResult(result, "查询成功");
        return result;
    }


    @ApiOperation(value = "用户登录/注册")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "phone", value = "医生手机号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "code", value = "验证码", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "codetype", value = "类型 1 登录 2 注册", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "agreeplatform", value = "是否同意协议 1是 0 否", dataType = "String")
    })
    @PostMapping("/doctorLongRegister")
    public Map<String, Object> doctorLongRegister(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        log.info("params>>>" + params);
        String phone = ModelUtil.getStr(params, "phone");
        String code = ModelUtil.getStr(params, "code");
        log.info(">>>>" + code);
        int codeType = ModelUtil.getInt(params, "codetype");
        int agreePlatform = ModelUtil.getInt(params, "agreeplatform");//是否同意平台协议
        if (StrUtil.isEmpty(phone, code)) {
            setErrorResult(result, "请输入手机号和验证码!");
        } else if (agreePlatform == 0 && codeType == 2) {
            setErrorResult(result, "请先同意协议!");
        } else {
            if (!RegexValidateUtil.checkCellphone(phone)) {
                setErrorResult(result, "请填入正确的手机号!");
            } else {
                boolean flag = doctorService.doctotLoginRegister(phone, code, codeType, agreePlatform);
                if (flag) {
                    if (codeType == 1) {
                        Map<String, Object> doctorLogin = doctorService.getDoctorLogin(phone);
                        if (doctorLogin == null) {
                            setErrorResult(result, "请先注册");
                        } else {
                            result.put("data", doctorLogin);
                            setOkResult(result, "登录成功!");
                        }
                    } else if (codeType == 2) {
                        setOkResult(result, "注册成功!");
                        result.put("data", doctorService.getDoctorLogin(phone));
                    }
                }
            }
        }
        log.info(">>>>" + result);
        return result;
    }


    /**
     * 发送验证码
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "发送验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "phone", value = "医生手机号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "codetype", value = "类型 1 登录 2 注册", dataType = "String")
    })
    @PostMapping("/doctorSendCode")
    public Map<String, Object> doctorSendCode(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String phone = ModelUtil.getStr(params, "phone");
        int codetype = ModelUtil.getInt(params, "codetype");//登录1 注册2
        if (StrUtil.isEmpty(phone) && codetype == 0) {
            setErrorResult(result, "请检查手机号和验证码不能为空!");
        } else {
            if (!RegexValidateUtil.checkCellphone(phone)) {
                setErrorResult(result, "请填入正确的手机号!");
            } else {
                boolean flag = false;
                try {
                    flag = doctorService.sendCode(phone, codetype);
                } catch (ClientException e) {
                    e.printStackTrace();
                }
                if (flag) {
                    setOkResult(result, "验证码已发送!");
                }
            }
        }

        return result;
    }


    @ApiOperation(value = "患者列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "医生id", value = "doctorid", required = true, dataType = "String"),
    })
    @PostMapping("/userList")
    public Map<String, Object> userList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        if (doctorid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorService.doctorUserList(doctorid));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "患者列表查询")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "name", value = "用户名字或者首字母", dataType = "String"),
    })
    @PostMapping("/findDoctorUserList")
    public Map<String, Object> findDoctorUserList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        String name = ModelUtil.getStr(params, "name");
        if (doctorid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorService.findDoctorUserList(doctorid, name));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "患者详细")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "userid", value = "患者id", required = true, dataType = "String"),
    })
    @PostMapping("/getDoctorUser")
    public Map<String, Object> doctorUser(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        long userid = ModelUtil.getLong(params, "userid");
        if (userid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorService.getUser(doctorid, userid));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "患者处方列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "userid", value = "患者id", required = true, dataType = "String"),
    })
    @PostMapping("/getPrescripList")
    public Map<String, Object> getPrescripList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        long userid = ModelUtil.getLong(params, "userid");
        if (userid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorService.getPrescripList(doctorid, userid));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "处方详细")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "prescriptionid", value = "处方id", required = true, dataType = "String"),
    })
    @PostMapping("/getPrescription")
    public Map<String, Object> getPrescription(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long prescriptionid = ModelUtil.getLong(params, "prescriptionid");
        if (prescriptionid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorService.getAppPrescription(prescriptionid));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "医生详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", required = true, dataType = "String"),
    })
    @PostMapping("/getDoctor")
    public Map<String, Object> getDoctor(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        if (doctorid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorService.getSimpleDoctor(doctorid));
            setOkResult(result, "查询成功");
            result.put("picdomain", ConfigModel.DOCTORPICDOMAIN);
        }
        return result;
    }

    @ApiOperation(value = "修改医生擅长和简介")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "professional", value = "擅长", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "introduction", value = "简介", required = true, dataType = "String"),
    })
    @PostMapping("/updateDoctorProfessionalAndIntroduction")
    public Map<String, Object> updateDoctorProfessionalAndIntroduction(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        String professional = ModelUtil.getStr(params, "professional");
        String introduction = ModelUtil.getStr(params, "introduction");
        if (doctorid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorService.updateDoctorProfessionalAndIntroduction(doctorid, professional, introduction));
            setOkResult(result, "修改成功");
        }
        return result;
    }


    @ApiOperation(value = "医生常用药列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "当前页", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "当前页条数", dataType = "String")
    })
    @PostMapping("/getDoctorDrugsList")
    public Map<String, Object> getDoctorDrugsList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        if (doctorid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorService.getDoctorDrugsList(doctorid, pageIndex, pageSize));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "药品分类")
    @ApiImplicitParams({
    })
    @PostMapping("/getDrugsPackageList")
    public Map<String, Object> getDrugsPackageList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", doctorService.getDrugsPackageList());
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "选择药品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "packageid", value = "药品分类id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "当前页", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "当前页条数", dataType = "String")
    })
    @PostMapping("/selectDrugsList")
    public Map<String, Object> selectDrugsList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        long packageid = ModelUtil.getLong(params, "packageid");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", doctorService.selectDrugsList(doctorid, packageid, pageIndex, pageSize));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "搜索药品")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "name", value = "药品名或者首字母", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "当前页", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "当前页条数", dataType = "String")
    })
    @PostMapping("/searchDrugsList")
    public Map<String, Object> searchDrugsList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        String name = ModelUtil.getStr(params, "name");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", doctorService.searchDrugsList(doctorid, name, pageIndex, pageSize));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "添加常用药品")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "drugsid", value = "药品id", required = true, dataType = "String"),
    })
    @PostMapping("/addDrugs")
    public Map<String, Object> addDrugs(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        long drugsid = ModelUtil.getLong(params, "drugsid");
        if (doctorid == 0 || drugsid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            boolean flag = doctorService.addOftenDrugs(doctorid, drugsid);
            if (flag) {
                setOkResult(result, "添加成功");
            } else {
                setErrorResult(result, "不能重复添加");
            }
        }
        return result;
    }

    @ApiOperation(value = "删除常用药品")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "drugsid", value = "药品id", required = true, dataType = "String"),
    })
    @PostMapping("/delDrugs")
    public Map<String, Object> delDrugs(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        long drugsid = ModelUtil.getLong(params, "drugsid");
        if (doctorid == 0 || drugsid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorService.delOftenDrugs(doctorid, drugsid));
            setOkResult(result, "删除成功");
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
        int code = ModelUtil.getInt(params, "id");
        result.put("data", doctorService.getAreaByParentId(code));
        setOkResult(result, "查询成功");
        return result;
    }


    @ApiOperation(value = "添加银行卡")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "address", value = "开户地区", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "name", value = "持卡人姓名", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "bankname", value = "开户行", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "number", value = "卡号", required = true, dataType = "String"),
    })
    @PostMapping("/addBankCard")
    public Map<String, Object> addBankCard(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        String address = ModelUtil.getStr(params, "address");
        String name = ModelUtil.getStr(params, "name");
        String number = ModelUtil.getStr(params, "number");
        number = number.replaceAll("\\s*", "");//替换空格
        if (StrUtil.isEmpty(address, name) || doctorid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            Map<String, Object> cardInfo = doctorService.getCardInfo(number);
            if ("0000".equals(ModelUtil.getStr(cardInfo, "respCd"))) {
                //银行编码
                String issInsId = ModelUtil.getStr(ModelUtil.getMap(cardInfo, "data"), "issInsId");
                String bankName = ModelUtil.getStr(ModelUtil.getMap(cardInfo, "data"), "issNm");
                result.put("data", doctorService.addBankCard(doctorid, address, name, bankName, number, issInsId));
                setOkResult(result, "添加成功");
            } else {
                setErrorResult(result, ModelUtil.getStr(cardInfo, "respMsg"));
            }
        }
        return result;
    }

    @ApiOperation(value = "获取银行卡信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "cardno", value = "卡号", required = true, dataType = "String"),
    })
    @PostMapping("/getCardInfo")
    public Map<String, Object> getCardInfo(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String cardno = ModelUtil.getStr(params, "cardno");
        cardno = cardno.replaceAll("\\s*", "");//替换空格
        if (StrUtil.isEmpty(cardno)) {
            setErrorResult(result, "参数错误");
        } else {
            Map<String, Object> cardInfo = doctorService.getCardInfo(cardno);
            if ("0000".equals(ModelUtil.getStr(cardInfo, "respCd"))) {
                result.put("data", ModelUtil.getMap(cardInfo, "data"));
                setOkResult(result, "查询成功");
            } else {
                setErrorResult(result, ModelUtil.getStr(cardInfo, "respMsg"));
            }
        }
        return result;
    }


    @ApiOperation(value = "银行卡列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", required = true, dataType = "String"),
    })
    @PostMapping("/getBankCardList")
    public Map<String, Object> getBankCardList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        if (doctorid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorService.getBankCardList(doctorid));
            setOkResult(result, "添加成功");
        }
        return result;
    }

    @ApiOperation(value = "银行卡详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "银行卡id", required = true, dataType = "String"),
    })
    @PostMapping("/getBankCard")
    public Map<String, Object> getBankCard(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorService.getBankCard(id));
            setOkResult(result, "添加成功");
        }
        return result;
    }

    @ApiOperation(value = "解除银行卡绑定")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "银行卡id", required = true, dataType = "String"),
    })
    @PostMapping("/delBankCard")
    public Map<String, Object> delBankCard(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorService.delBankCard(id));
            setOkResult(result, "添加成功");
        }
        return result;
    }

    @ApiOperation(value = "医生签到")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", required = true, dataType = "String"),
    })
    @PostMapping("/doctorSignIn")
    public Map<String, Object> doctorSignIn(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        if (doctorid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            int integral = doctorService.doctorSignIn(doctorid);
            if (integral > 0) {
                Map<String, Object> map = new HashMap<>();
                map.put("integral", integral);
                result.put("data", map);
                setOkResult(result, "签到成功");
            } else {
                setErrorResult(result, "不能重复签到");
            }
        }
        return result;
    }

    @ApiOperation(value = "医生首页")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", required = true, dataType = "String"),
    })
    @PostMapping("/doctorHomePage")
    public Map<String, Object> doctorHomePage(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        if (doctorid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            Map<String, Object> doctorHomePage = doctorService.doctorHomePage(doctorid);
            if (doctorHomePage == null) {
                setResult(result, "登录失效", -100);
            } else {
                result.put("data", doctorHomePage);
                setOkResult(result, "查询成功");
            }
        }
        return result;
    }

    @ApiOperation(value = "订单收益")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/orderProfitList")
    public Map<String, Object> orderProfitList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        if (doctorid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorService.orderProfitList(doctorid, pageIndex, pageSize));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "积分列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/integralList")
    public Map<String, Object> integralList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        if (doctorid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorService.doctorIntegralList(doctorid, pageIndex, pageSize));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "医生名片")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", required = true, dataType = "String"),
    })
    @PostMapping("/getDoctorCard")
    public Map<String, Object> getDoctorCard(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        if (doctorid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorService.getDoctorCard(doctorid));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "新患者列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/newUserList")
    public Map<String, Object> newUserList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        if (doctorid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorService.newUser(doctorid, pageIndex, pageSize));
            setOkResult(result, "查询成功");
        }
        return result;
    }


    @ApiOperation(value = "个人消息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生ID", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "分页", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "分页", dataType = "String"),
    })
    @PostMapping("/getDoctorMessageList")
    public Map<String, Object> getDoctorMessageList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorId = ModelUtil.getLong(params, "doctorid");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", doctorService.getDoctorMessageList(doctorId, pageIndex, pageSize));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "更新消息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", dataType = "String"),
    })
    @PostMapping("/updateMessageReadStatus")
    public Map<String, Object> updateMessageReadStatu(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorService.updateMessageReadStatu(id));
            setOkResult(result, "已阅读");
        }
        return result;
    }

    @ApiOperation(value = "查询医生状态")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", required = true, dataType = "String"),
    })
    @PostMapping("/getDoctorExamine")
    public Map<String, Object> getDoctorExamine(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        if (doctorid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorService.getDoctorExamine(doctorid));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "电话问诊时间列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "doctorid", dataType = "String"),
    })
    @PostMapping("/phoneScheudTime")
    public Map<String, Object> phoneScheudTime(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        result.put("data", doctorService.findScheudList(doctorid));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "兼容改排班数据")
    @ApiImplicitParams({
    })
    @PostMapping("/getSql")
    public Map<String, Object> getSql(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", doctorService.getSql());
        setOkResult(result, "成功");
        return result;
    }

    @ApiOperation(value = "手动修改年龄")
    @ApiImplicitParams({
    })
    @PostMapping("/updateAge")
    public Map<String, Object> updateAge(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", doctorService.getUp());
        setOkResult(result, "成功");
        return result;
    }

    @ApiOperation(value = "医生查看用户就诊记录")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getUserOrderList")
    public Map<String, Object> getUserOrderList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        long userid = ModelUtil.getLong(params, "userid");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", doctorService.getUserOrderList(userid, doctorid, pageIndex, pageSize));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "用户健康档案信息查询")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
    })
    @PostMapping("/getBasic")
    public Map<String, Object> getBasic(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userid = ModelUtil.getLong(params, "userid");
        if (userid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userManagementService.getBasicApp(userid));
            setOkResult(result, "成功");
        }
        return result;
    }

}
