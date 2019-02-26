package com.syhdoctor.webserver.service.prescription;

import com.syhdoctor.common.utils.EnumUtils.*;
import com.syhdoctor.common.utils.*;
import com.syhdoctor.common.utils.encryption.BASE64;
import com.syhdoctor.webserver.api.bean.Drug;
import com.syhdoctor.webserver.api.bean.PresPhotosList;
import com.syhdoctor.webserver.api.request.impl.PrescriptionRequest;
import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.exception.ServiceException;
import com.syhdoctor.webserver.mapper.answer.AnswerMapper;
import com.syhdoctor.webserver.mapper.doctor.DoctorMapper;
import com.syhdoctor.webserver.mapper.prescription.PrescriptionMapper;
import com.syhdoctor.webserver.service.answer.AnswerService;
import com.syhdoctor.webserver.service.code.CodeService;
import com.syhdoctor.webserver.service.user.UserService;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.text.AttributedString;
import java.util.*;
import java.util.List;


@Transactional
public abstract class PrescriptionBaseService extends BaseService {

    @Autowired
    private PrescriptionMapper prescriptionMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private CodeService codeService;

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private AnswerMapper answerMapper;

    @Autowired
    private AnswerService answerService;


    /**
     * 添加常用处方
     *
     * @param doctorId       医生id
     * @param diseasesTypeId 疾病分类
     * @param druglist       药品列表
     * @return
     */
    public boolean addOftenPrescription(long oftenprescriptionid, long doctorId, String title, String diagnosis, long diseasesTypeId, List<?> druglist) {

        //todo 处方类别编码 pres_class_code 字典 pres_class_code
        String presClassCode = "01";
        //todo 处方类别名称 pres_class_name pres_class_name  西医
        String presClassName = "西药";
        // 就诊类别编码 med_class_code 1
        String medClassCode = "1";
        // 就诊类别名称 med_class_name 图文问诊
        String medClassName = "图文问诊";
        //  医疗机构编码 org_code WEBH019
        String orgCode = "WEBH019";
        //  医疗机构名称 org_name 银川山屿海互联网医院
        String orgName = "银川山屿海互联网医院";

        //todo 诊断编码类型 diag_code_type 表示疾病编码使用的标准，取值为：ICD10、GB-95、“-1”，-1表示其它标准情况
        String diagCodeType = "ICD10";
        //todo 疾病编码 diseases_code 参见疾病字典,西医使用ICD10，中医使用GB-95
        String diseasesCode = "A01.0";
        //todo 疾病名称 diseases_name
        String diseasesName = "伤寒";
        //  疾病分类 diseases_type 取字典 0：普通
        String diseasesType = "0";
        // 行动不便标志 mobility_flag 用来判断药量是否超标，取值如下：0：否，1：是，3：未知
        String mobilityFlag = "3";
        // 病情稳定需长期服药标志 long_medical_flag 用来判断药量是否超标，取值如下：0：否，1：是，3：未知
        String longMedicalFlag = "3";
        // 处方有效期（单位天）pres_effec_days
        int presEffecDays = 3;
        // 总金额 total_price
        BigDecimal totalPrice = new BigDecimal(0);
        // 互联网医院处方图片(适合单张处方照片的情形) pres_photo

        // 互联网医院处方图片列表(适合多张处方照片的情形) pres_photos_list 不是必填
        // 互联网医院处方图片(适合多张处方照片的情形) pres_photos 不是必填
        if (oftenprescriptionid == 0) {
            oftenprescriptionid = prescriptionMapper.addOftenPrescription(doctorId, title, diagnosis, presClassCode, presClassName, medClassCode, medClassName, orgCode, orgName,
                    diagCodeType, diseasesCode, diseasesName, diseasesType, mobilityFlag, longMedicalFlag, presEffecDays, totalPrice);
        } else {
            prescriptionMapper.updateOftenPrescription(oftenprescriptionid, title, diagnosis, presClassCode, presClassName, medClassCode, medClassName, orgCode, orgName,
                    diagCodeType, diseasesCode, diseasesName, diseasesType, mobilityFlag, longMedicalFlag, presEffecDays, totalPrice);
            prescriptionMapper.delOftenPrescriptionDrug(oftenprescriptionid);
        }
        Set<Long> set = new HashSet<>();
        // 药品列表 drug_list
        for (Object object : druglist) {
            Map<?, ?> map = (Map<?, ?>) object;
            long drugId = ModelUtil.getLong(map, "id");
            boolean flag = set.add(drugId);
            if (!flag) {
                throw new ServiceException("药品不能重复添加");
            }
            Map<String, Object> drugs = codeService.getDrugs(drugId);
            // 药品通用名称 appr_drug_name 药品列表的子节点，药品通用名称参考药品字典
            String apprDrugName = ModelUtil.getStr(drugs, "appdrugname");

            //todo 药品商品编码 drug_code 药品列表的子节点,填写国药准字号编码
            String drugCode = "国药准字号编码";
            //todo 药品商品名称 drug_name 药品列表的子节点，填写国药准字号对应名称
            String drugName = "国药准字号对应名称";

            // todo 药品剂型 drug_form
            String drugForm = "1";//ModelUtil.getStr(map, "drugform");
            //  用药剂量-单次 dosage
            String dosage = ModelUtil.getStr(map, "dosage");
            //  用药剂量单位-单次 dosage_unit
            String dosageUnit = ModelUtil.getStr(map, "dosageunit");
            //  用药剂量-总量 total_dosage
            String totalDosage = ModelUtil.getStr(map, "totaldosage");
            //  用药剂量单位-总量 total_dosage_unit
            String totalDosageUnit = ModelUtil.getStr(drugs, "totaldosageunit");
            long frequencyid = ModelUtil.getLong(map, "frequencyid");
            Map<String, Object> frequency = codeService.getFrequency(frequencyid);
            //  用药频率编码 medicine_freq
            String medicineFreq = ModelUtil.getStr(frequency, "code");
            //  用药频率 medicine_freq_name
            String medicineFreqName = ModelUtil.getStr(frequency, "name");
            //用法
            String method = ModelUtil.getStr(map, "method");
            //周期
            String cycle = ModelUtil.getStr(map, "cycle");

            //  规格 standard_desc
            String standardDesc = ModelUtil.getStr(drugs, "standarddesc");
            //  单价 single_price
            BigDecimal singlePrice = new BigDecimal(0);
            //  金额 drug_total_price
            BigDecimal drugTotalPrice = new BigDecimal(0);
            // 嘱托 comments
            String comments = "按计量使用，如服药后没有好转请停用并到医院复查";

            // 抗菌药说明 anti_comments
            String antiComments = "无";
            // 中药煎煮法名称 dec_meth_name 不是必填
            String decMethName = "";
            // 药量(单位为天) total_charge
            String totalCharge = "7";

            String remark = ModelUtil.getStr(map, "remark");
            if (StrUtil.isEmpty(dosage, dosageUnit, totalDosage, method) || frequencyid == 0 || drugId == 0 || frequency == null) {
                throw new ServiceException("参数错误");
            }
            prescriptionMapper.addDocOftenDrug(oftenprescriptionid, drugId, apprDrugName, drugCode, drugName, drugForm, dosage, dosageUnit, totalDosage, totalDosageUnit, frequencyid, medicineFreq, medicineFreqName, standardDesc, singlePrice, drugTotalPrice, comments, antiComments, decMethName, totalCharge, method, cycle, remark);
        }
        return true;
    }

    /**
     * 常用处方列表
     *
     * @param doctorId
     * @return
     */
    public List<Map<String, Object>> getOftenPrescriptionList(long doctorId, int pageindex, int pagesize) {
        return prescriptionMapper.getOftenPrescriptionList(doctorId, pageindex, pagesize);
    }

    /**
     * 将常用处方添加成处方
     *
     * @param oftenPrescriptionId
     * @param orderId
     * @return
     */
    public long addPrescriptionByOften(long oftenPrescriptionId, String presNo, long orderId, int orderType) {
        Map<String, Object> problem = answerMapper.getProblem(orderId);
        // 就诊类别编码 med_class_code 1
        String medClassCode = "1";
        // 就诊类别名称 med_class_name 图文问诊
        String medClassName = "图文问诊";

        if (orderType == OrderTypeEnum.Phone.getCode()) {
            problem = answerMapper.getPhoneOrderDetail(orderId);
            medClassCode = "3";
            medClassName = "电话问诊";
        } else if (orderType == OrderTypeEnum.Video.getCode()) {
            problem = answerMapper.getVideoOrderDetail(orderId);
            medClassCode = "4";
            medClassName = "视频问诊";
        }

        long userId = ModelUtil.getLong(problem, "userid");
        Map<String, Object> oftenPrescription = prescriptionMapper.getOftenPrescription(oftenPrescriptionId);
        // 处方号 pres_no
        //临床诊断
        String diagnosis = ModelUtil.getStr(oftenPrescription, "diagnosis");
        // 处方类别编码 pres_class_code 字典 prescriptionTypeIds
        String presClassCode = ModelUtil.getStr(oftenPrescription, "presclasscode");
        // 处方类别名称 pres_class_name prescriptionTypeNames
        String presClassName = ModelUtil.getStr(oftenPrescription, "presclassname");
        // 患者id pt_id userid
        // 就诊号 med_rd_no
        String medRdNo = ModelUtil.getStr(problem, "orderno");

        Map<String, Object> user = userService.getUser(userId);
        //  患者姓名 pt_no
        String ptNo = ModelUtil.getStr(user, "name");
        //  性别编码 0 1 2 9
        String geCode = ModelUtil.getStr(user, "gender");
        //  性别名称 ge_name
        String geName = ModelUtil.getStr(codeService.getGender(geCode), "name");
        //  出生日期 birthday
        long birthday = ModelUtil.getLong(user, "birthday");
        //  患者年龄 pt_age
        long ptAge = (UnixUtil.getNowTimeStamp() - birthday) / 365 / 24 / 60 / 60 / 1000;
        //  身份证号 id_no
        String idNo = ModelUtil.getStr(user, "cardno");
        //  患者手机号 pt_tel
        String ptTel = ModelUtil.getStr(user, "phone");
        //  患者所在地区 pt_district
        String ptDistrict = ModelUtil.getStr(user, "areas");
        if (!StrUtil.isEmpty(ptDistrict)) {
            ptDistrict = ptDistrict.substring(ptDistrict.lastIndexOf(".") + 1);
        }
        // 保险类别编码 ins_class_code 01:社会基本医疗保险,02:商业医疗保险,03:大病统筹,04:新型农村合作医疗,05:城镇居民基本医疗保险,06:公费医疗,99:其他
        String insClassCode = "99";
        // 保险类别名称 ins_class_name
        String insClassName = "其他";

        long doctorId = ModelUtil.getLong(oftenPrescription, "doctorid");
        Map<String, Object> doctor = doctorMapper.getDoctorById(doctorId);
        //  医疗机构编码 org_code WEBH019
        String orgCode = ModelUtil.getStr(oftenPrescription, "orgcode");
        //  医疗机构名称 org_name 银川山屿海互联网医院
        String orgName = ModelUtil.getStr(oftenPrescription, "orgname");


        //  就诊科室编码 visit_dept_code 关联医生科室
        //  开方科室编码 pres_dept_code 关联医生科室
        long departmentId = ModelUtil.getLong(doctor, "department_id");
        //  就诊科室名称 visit_dept_name 关联医生科室
        //  开方科室名称 pres_dept_name 关联医生科室
        String departmentName = ModelUtil.getStr(doctor, "departmentname");
        String departmentcode = ModelUtil.getStr(doctor, "departmentcode");
        //  开方时间 pres_time 形式如“YYYY-MM-DD”+空格+“ hh:mm:ss”
        long presTime = UnixUtil.getNowTimeStamp();
        //  开方医生编码 pres_doc_code
        String presDocCode = ModelUtil.getStr(doctor, "indoccode");
        //  开方医生姓名 pres_doc_name
        String presDocName = ModelUtil.getStr(doctor, "docname");
        //  开方医师照片数据 pres_doc_phote_data
        String presDocPhoteData = ModelUtil.getStr(doctor, "docphoto");

        // 审核时间 review_time review_trial
        long reviewTime = 0;
        // 审核医生编码 review_doc_code
        String reviewDocCode = null;
        // 审核医生姓名 review_doc_name
        String reviewDocName = null;
        // 审方时间 trial_time
        long trialTime = 0;
        // 审方医生编码 trial_doc_code
        String trialDocCode = null;
        // 审方医生姓名 trial_doc_name
        String trialDocName = null;

        String reviewDoctorUrl = null;
        String trialDoctorUrl = null;

        int examine = 1;

        Map<String, Object> check = doctorMapper.getCheck().get(0);
        int onecheck = ModelUtil.getInt(check, "onecheck");//审核是否自动审核
        int twocheck = ModelUtil.getInt(check, "twocheck");//审方是否自动审核
        if (onecheck == 1) {
            examine = 4;
            //审核医生
            Map<String, Object> reviewDoctor = doctorMapper.getReviewTrialDoctor(DoctorTypeEnum.ReviewDoctor.getCode());
            // 审核时间 review_time review_trial
            reviewTime = UnixUtil.getNowTimeStamp();
            // 审核医生编码 review_doc_code
            reviewDocCode = ModelUtil.getStr(reviewDoctor, "indoccode");
            // 审核医生姓名 review_doc_name
            reviewDocName = ModelUtil.getStr(reviewDoctor, "docname");

            reviewDoctorUrl = ConfigModel.BASEFILEPATH + FileUtil.getFileName(ModelUtil.getStr(reviewDoctor, "digitalsignurl"));

        }

        if (onecheck == 1 && twocheck == 1) {
            examine = 2;
            //审方医生
            Map<String, Object> trialDoctor = doctorMapper.getReviewTrialDoctor(DoctorTypeEnum.trialDoctor.getCode());
            // 审方时间 trial_time
            trialTime = UnixUtil.getNowTimeStamp();
            // 审方医生编码 trial_doc_code
            trialDocCode = ModelUtil.getStr(trialDoctor, "indoccode");
            // 审方医生姓名 trial_doc_name
            trialDocName = ModelUtil.getStr(trialDoctor, "docname");

            trialDoctorUrl = ConfigModel.BASEFILEPATH + FileUtil.getFileName(ModelUtil.getStr(trialDoctor, "digitalsignurl"));
        }

        // 诊断编码类型 diag_code_type 表示疾病编码使用的标准，取值为：ICD10、GB-95、“-1”，-1表示其它标准情况
        String diagCodeType = ModelUtil.getStr(oftenPrescription, "diagcodetype");
        // 疾病编码 diseases_code 参见疾病字典,西医使用ICD10，中医使用GB-95
        String diseasesCode = ModelUtil.getStr(oftenPrescription, "diseasescode");
        // 疾病名称 diseases_name
        String diseasesName = ModelUtil.getStr(oftenPrescription, "diseasesname");


        //  疾病分类 diseases_type 取字典
        String diseasesType = ModelUtil.getStr(oftenPrescription, "diseasestype");
        // 行动不便标志 mobility_flag 用来判断药量是否超标，取值如下：0：否，1：是，3：未知
        String mobilityFlag = ModelUtil.getStr(oftenPrescription, "mobilityflag");
        // 病情稳定需长期服药标志 long_medical_flag 用来判断药量是否超标，取值如下：0：否，1：是，3：未知
        String longMedicalFlag = ModelUtil.getStr(oftenPrescription, "longmedicalflag");
        // 处方有效期（单位天）pres_effec_days
        int presEffecDays = ModelUtil.getInt(oftenPrescription, "preseffecdays");
        // 总金额 total_price
        BigDecimal totalPrice = ModelUtil.getDec(oftenPrescription, "totalprice", BigDecimal.ZERO);
        // 互联网医院处方图片(适合单张处方照片的情形) pres_photo

        // 互联网医院处方图片列表(适合多张处方照片的情形) pres_photos_list 不是必填
        // 互联网医院处方图片(适合多张处方照片的情形) pres_photos 不是必填
        if (StrUtil.isEmpty(presNo)) {
            presNo = IdGenerator.INSTANCE.nextId();
        }
        long prescriptionid = prescriptionMapper.addPrescription(presNo, diagnosis, userId, presClassCode, presClassName, medRdNo, medClassCode, medClassName, ptNo, geCode, geName,
                birthday, ptAge, idNo, ptTel, ptDistrict, insClassCode, insClassName, orgCode, orgName, departmentcode, departmentName, presTime, doctorId, presDocCode, presDocName,
                presDocPhoteData, reviewTime, reviewDocCode, reviewDocName, trialTime, trialDocCode, trialDocName, diagCodeType, diseasesCode, diseasesName, diseasesType,
                mobilityFlag, longMedicalFlag, presEffecDays, totalPrice, orderId, orderType, examine, onecheck, twocheck);
        // 药品列表 drug_list
        List<Map<String, Object>> druglist = prescriptionMapper.getOftenPrescriptionDrugList(oftenPrescriptionId);
        if (druglist.size() == 0) {
            throw new ServiceException("药品不能为空");
        }
        for (Object object : druglist) {
            Map<?, ?> map = (Map<?, ?>) object;
            long drugId = ModelUtil.getLong(map, "drugid");
            Map<String, Object> drugs = codeService.getDrugs(drugId);
            // 药品通用名称 appr_drug_name 药品列表的子节点，药品通用名称参考药品字典
            String apprDrugName = ModelUtil.getStr(drugs, "appdrugname");

            // 药品商品编码 drug_code 药品列表的子节点,填写国药准字号编码
            String drugCode = apprDrugName;// ModelUtil.getStr(map, "drugcode");
            // 药品商品名称 drug_name 药品列表的子节点，填写国药准字号对应名称
            String durgName = apprDrugName;//ModelUtil.getStr(map, "drugname");

            //  药品剂型 drug_form
            String drugForm = ModelUtil.getStr(map, "drugform");
            //  用药剂量-单次 dosage
            String dosage = ModelUtil.getStr(map, "dosage");
            //  用药剂量单位-单次 dosage_unit
            String dosageUnit = ModelUtil.getStr(map, "dosageunit");
            //  用药剂量-总量 total_dosage
            String totalDosage = ModelUtil.getStr(map, "totaldosage");
            //  用药剂量单位-总量 total_dosage_unit
            String totalDosageUnit = ModelUtil.getStr(drugs, "totaldosageunit");

            //  用药频率编码 medicine_freq
            String medicineFreq = ModelUtil.getStr(map, "medicinefreq");
            //  用药频率 medicine_freq_name
            String medicineFreqName = ModelUtil.getStr(map, "medicinefreqname");

            //  规格 standard_desc
            String standardDesc = ModelUtil.getStr(drugs, "standarddesc");
            //  单价 single_price
            BigDecimal singlePrice = ModelUtil.getDec(map, "singleprice", BigDecimal.ZERO);
            //  金额 drug_total_price
            BigDecimal drugTotalPrice = ModelUtil.getDec(map, "drugtotalprice", BigDecimal.ZERO);
            // 嘱托 comments
            String comments = ModelUtil.getStr(map, "comments");

            //抗菌药说明 anti_comments
            String antiComments = ModelUtil.getStr(map, "anticomments");
            //中药煎煮法名称 dec_meth_name 不是必填
            String decMethName = "";
            //药量(单位为天) total_charge
            String totalCharge = ModelUtil.getStr(map, "totalcharge");
            //用法
            String method = ModelUtil.getStr(map, "method");
            //周期
            String cycle = ModelUtil.getStr(map, "cycle");
            //备注
            String remark = ModelUtil.getStr(map, "remark");
            if (StrUtil.isEmpty(dosage, dosageUnit, totalDosage, method) || drugId == 0) {
                throw new ServiceException("参数错误");
            }
            prescriptionMapper.addDocDrug(prescriptionid, drugId, apprDrugName, drugCode, durgName, drugForm, dosage, dosageUnit, totalDosage, totalDosageUnit, medicineFreq, medicineFreqName, standardDesc, singlePrice, drugTotalPrice, comments, antiComments, decMethName, totalCharge, method, cycle, remark);
        }

        //处方图片路径
        String key = String.format("%s/%s/%s_%s.jpg", ModelUtil.getStr(doctor, "indoccode"), orderId, prescriptionid, UnixUtil.getCustomRandomString());
        String fileName = FileUtil.setFileName(FileUtil.FILE_PRESCRIPTION_PATH, key);
        String presPhotoUrl = ConfigModel.BASEFILEPATH + fileName;
        FileUtil.createFile(presPhotoUrl);
        try {
            String doctorUrl = ConfigModel.BASEFILEPATH + FileUtil.getFileName(ModelUtil.getStr(doctor, "digitalsignurl"));
            createImage(presPhotoUrl, doctorUrl, reviewDoctorUrl, trialDoctorUrl, prescriptionid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //图片转成流
        String presPhoto = BASE64.base64Encoding(PicUtils.compressPicForScale(BASE64.readFile(Paths.get(presPhotoUrl)), 200));
        //设置处方图片和图片流
        prescriptionMapper.setPrescriptionDocPhoto(presPhoto, ModelUtil.setLocalUrl(fileName), prescriptionid);
        //上传数据中心
        if (examine == 2) {
            uploadPrescriptionToDataCenter(prescriptionid);
        }
        return prescriptionid;
    }

    /**
     * 常用处方详情
     *
     * @param prescriptionId
     * @return
     */
    public Map<String, Object> getOftenPrescription(long prescriptionId) {
        Map<String, Object> oftenPrescription = prescriptionMapper.getSimpleOftenPrescription(prescriptionId);
        if (oftenPrescription != null) {
            oftenPrescription.put("druglist", prescriptionMapper.getSimpleOftenPrescriptionDrugList(prescriptionId));
        }
        return oftenPrescription;
    }

    /**
     * 处方发送页面详情
     *
     * @param prescriptionId
     * @param userId
     * @return
     */
    public Map<String, Object> getSendPrescription(long userId, long prescriptionId) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> oftenPrescription = getOftenPrescription(prescriptionId);
        Map<String, Object> health = userService.getUserHealth(userId);
        String utl = ModelUtil.setLocalUrl(FileUtil.FILE_STATIC_PATH + "hos_digital_sign.png");
        result.put("userinfo", health);
        result.put("hosdigitalsign", utl);
        result.put("oftenPrescription", oftenPrescription);
        result.put("presno", IdGenerator.INSTANCE.nextId());
        result.put("createtime", UnixUtil.getNowTimeStamp());
        return result;
    }

    /**
     * 处方详细
     *
     * @param id
     * @return
     */
    public Map<String, Object> getAppPrescription(long id) {
        Map<String, Object> prescription = prescriptionMapper.getAppSimplePrescription(id);
        Map<String, Object> userinfo = userService.getHealth(ModelUtil.getLong(prescription, "ptid"));
        String utl = ModelUtil.setLocalUrl(FileUtil.FILE_STATIC_PATH + "hos_digital_sign.png");
        if (prescription != null) {
            List<Map<String, Object>> drugList = prescriptionMapper.getAppPrescriptionDrugList(id);
            prescription.put("druglist", drugList);
            prescription.put("userinfo", userinfo);
            prescription.put("hosdigitalsign", utl);
            long starttime = ModelUtil.getLong(prescription, "createtime");
            long endtime = ModelUtil.getLong(prescription, "createtime") + 3 * 24 * 60 * 60 * 1000;
            prescription.put("starttime", starttime);
            prescription.put("endtime", endtime);
        }
        return prescription;
    }

    /**
     * 删除常用处方
     *
     * @param prescriptionId
     * @return
     */
    public boolean delOftenPrescription(long prescriptionId) {
        boolean flag = false;
        flag = prescriptionMapper.delOftenPrescription(prescriptionId);
        if (flag) {
            flag = prescriptionMapper.delOftenPrescriptionDrug(prescriptionId);
        }
        return flag;
    }


    private static AttributedString getUnLineText(String name, Font font) {
        AttributedString as = new AttributedString(name);
        as.addAttribute(TextAttribute.FONT, font);
        as.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        return as;
    }


    /**
     * 生成处方图片
     *
     * @param keyPath         处方图片
     * @param doctorUrl       医生签名
     * @param reviewDoctorUrl 审核医生签名
     * @param trialDoctorUrl  审方医生签名
     * @param prescriptionId  处方id
     * @throws Exception
     */
    public void createImage(String keyPath, String doctorUrl, String reviewDoctorUrl, String trialDoctorUrl, long prescriptionId) throws Exception {
        log.info("createimage========================" + doctorUrl);
        Map<String, Object> prescription = prescriptionMapper.getPrescription(prescriptionId);
        Map<String, Object> user = userService.getHealth(ModelUtil.getLong(prescription, "ptid"));
        Font font = new Font("宋体", Font.BOLD, 28);
        Font font1 = new Font("宋体", Font.BOLD, 56);
        Font font2 = new Font("宋体", Font.BOLD, 42);
        File outFile = new File(keyPath);
        // 创建图片
        int width = 1500;
        int height = 1754;
        int index = 50;
        BufferedImage bufImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bufImg.createGraphics();
        g.setClip(0, 0, width, height);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);// 先用黑色填充整张图片,也就是背景
        g.setColor(Color.black);// 在换成黑色
        g.setFont(font);// 设置画笔字体
        FontMetrics fm = g.getFontMetrics(font);
        long createTime = UnixUtil.getNowTimeStamp();
        int webh019 = fm.stringWidth(String.format("医疗机构编码：%s", "WEBH019"));
        int no = fm.stringWidth(String.format("处方编号：%s", ModelUtil.getStr(prescription, "presno")));
//        int time = fm.stringWidth(String.format("有效时间：%s至%s", UnixUtil.getDate(createTime, "yyyy-MM-dd"), UnixUtil.getDate(createTime, "yyyy-MM-dd") + 3 * 24 * 60 * 60 * 1000, "yyyy-MM-dd"));
        int time = fm.stringWidth(String.format("有效时间：%s至%s", UnixUtil.getDate(createTime, "yyyy-MM-dd"), UnixUtil.getDate(createTime + 3 * 24 * 60 * 60 * 1000, "yyyy-MM-dd")));
        int a = width - time - 50;
        int b = webh019 + 50;
        int c = (a - b) / 2;
        int d = (b + c) - no / 2;

        g.drawString(String.format("医疗机构编码：%s", "WEBH019"), 50, index);
        g.drawString(String.format("处方编号：%s", ModelUtil.getStr(prescription, "presno")), d, index);
        g.drawString(String.format("有效时间%s至%s", UnixUtil.getDate(createTime, "yyyy-MM-dd"), UnixUtil.getDate(createTime + 3 * 24 * 60 * 60 * 1000, "yyyy-MM-dd")), width - fm.stringWidth(String.format("有效时间%s至%s", UnixUtil.getDate(createTime, "yyyy-MM-dd"), UnixUtil.getDate(createTime + 3 * 24 * 60 * 60 * 1000, "yyyy-MM-dd"))) - 50, index);
        g.setFont(font1);
        fm = g.getFontMetrics(font1);
        index = index + 100;
        g.drawString("山屿海互联网医院电子处方", (width - fm.stringWidth("山屿海互联网医院电子处方")) / 2, index);
        g.setFont(font);
        fm = g.getFontMetrics(font);
        index = index + 70;

        g.drawString("姓名：", 100, index);
        g.drawString(getUnLineText(ModelUtil.getStr(prescription, "ptno", " "), font).getIterator(), fm.stringWidth("姓名：") + 100, index);

        g.drawString("性别：", 500, index);
        g.drawString(getUnLineText(ModelUtil.getStr(prescription, "gename", " "), font).getIterator(), fm.stringWidth("性别：") + 500, index);

        g.drawString("年龄：", 900, index);
        g.drawString(getUnLineText(String.format("%s岁", ModelUtil.getStr(prescription, "ptage", " ")), font).getIterator(), fm.stringWidth("年龄：") + 900, index);

        index = index + 50;
        g.drawString("体重：", 100, index);
        g.drawString(getUnLineText(ModelUtil.getInt(user, "weight", 0) == 0 ? "暂无" : String.format("%skg", ModelUtil.getInt(user, "weight", 0)), font).getIterator(), fm.stringWidth("体重：") + 100, index);

        g.drawString("身高：", 500, index);

        g.drawString(getUnLineText(ModelUtil.getInt(user, "height", 0) == 0 ? "暂无" : String.format("%scm", ModelUtil.getInt(user, "height", 0)), font).getIterator(), fm.stringWidth("身高：") + 500, index);

        g.drawString("身份证号：", 900, index);
        g.drawString(getUnLineText(ModelUtil.getStr(prescription, "idno", " "), font).getIterator(), fm.stringWidth("身份证号：") + 900, index);

        index = index + 50;
        g.drawString("诊断结果：", 100, index);

        String result = ModelUtil.getStr(prescription, "diagnosis", " ");
        StringBuilder resultLine = new StringBuilder("诊断结果：");
        for (int i = 0; i < result.length(); i++) {
            char fir = result.charAt(i);
            StringBuilder line = new StringBuilder(resultLine);
            line.append(fir);
            if (fm.stringWidth(line.toString()) > width - 200) {
                g.drawString(getUnLineText(resultLine.toString(), font).getIterator(), 100, index);
                index = index + 50;
                resultLine = new StringBuilder();
            }
            resultLine.append(fir);
            if (i == result.length() - 1) {
                g.drawString(getUnLineText(resultLine.toString(), font).getIterator(), 100, index);
                index = index + 50;
            }
        }

        int leng = (width - 200) / fm.stringWidth("-");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < leng; i++) {
            stringBuilder.append("-");
        }
        g.drawString(stringBuilder.toString(), 100, index);

        index = index + 50;
        g.setFont(font2);
        g.drawString("Rp", 100, index);

        g.setFont(font);
        List<Map<String, Object>> drugList = prescriptionMapper.getPrescriptionDrugList(prescriptionId);
        for (int i = 1; i < drugList.size() + 1; i++) {
            String format = String.format("%s、(%s 规格：%s)", i, ModelUtil.getStr(drugList.get(i - 1), "apprdrugname"), ModelUtil.getStr(drugList.get(i - 1), "standarddesc"));
            g.drawString(format, 100, index + 50);// 画出字符串
            g.drawString(String.format("*%s", ModelUtil.getInt(drugList.get(i - 1), "totaldosage")), fm.stringWidth(format) + 200, index + 50);// 画出字符串
            String medicineFreqName = ModelUtil.getStr(drugList.get(i - 1), "medicinefreqname");
            int dosage = ModelUtil.getInt(drugList.get(i - 1), "dosage");
            String dosageUnit = ModelUtil.getStr(drugList.get(i - 1), "dosageunit");
            String method = ModelUtil.getStr(drugList.get(i - 1), "method");
            method = StrUtil.isEmpty(method) ? "" : "," + method;
            String usagemethod = String.format("%s,每次%s%s%s", medicineFreqName, dosage, dosageUnit, method);
            g.drawString(String.format("使用方法：%s", usagemethod), 120, index + 100);// 画出字符串
            index = index + 150;
        }

        index = index + 50;
        g.drawString("（以下为空白，修改无效）", (width - fm.stringWidth("（以下为空白，修改无效）")) / 2, index);
        g.drawString("医师：", 100, height - fm.getHeight() - 100);
        g.drawString("药师审方：", 500, height - fm.getHeight() - 100);
        g.drawString("药师复核：", 900 + fm.stringWidth("药师"), height - fm.getHeight() - 100);
        g.dispose();
        ImageIO.write(bufImg, "jpg", outFile);// 输出png图片

        try {
            //医院电子章
            String hos = ConfigModel.BASEFILEPATH + FileUtil.FILE_STATIC_PATH + "hos_digital_sign.png";
            String temphos = ConfigModel.BASEFILEPATH + FileUtil.FILE_TEMP_PATH + "hos_digital_sign.png";

            //诊疗医师
            String smallDoctorUrl = ConfigModel.BASEFILEPATH + FileUtil.FILE_TEMP_PATH + "small_doctor_url.png";
            Thumbnails.of(doctorUrl)
                    .size(293, 176)
                    .toFile(smallDoctorUrl);


            FontMetrics finalFm = fm;
            Thumbnails.of(outFile)
                    .size(width, height)
                    .watermark((int enclosingWidth, int enclosingHeight, int width1, int height1, int insetLeft, int insetRight, int insetTop, int insetBottom) -> new Point(finalFm.stringWidth("医生：") + 100, height - 240), ImageIO.read(new File(smallDoctorUrl)), 1f)
                    .outputQuality(1f).toFile(keyPath);

            if (!StrUtil.isEmpty(reviewDoctorUrl)) {
                //审核医师
                String smallReviewDoctorUrl = ConfigModel.BASEFILEPATH + FileUtil.FILE_TEMP_PATH + "small_review_doctor_url.png";
                Thumbnails.of(reviewDoctorUrl)
                        .size(293, 176)
                        .toFile(smallReviewDoctorUrl);

                Thumbnails.of(outFile)
                        .size(width, height)
                        .watermark((int enclosingWidth, int enclosingHeight, int width1, int height1, int insetLeft, int insetRight, int insetTop, int insetBottom) -> new Point(finalFm.stringWidth("药师审方：") + 500, height - 240), ImageIO.read(new File(smallReviewDoctorUrl)), 1f)
                        .outputQuality(1f).toFile(keyPath);
            }

            if (!StrUtil.isEmpty(trialDoctorUrl)) {
                //审方医师
                String smallRrialDoctorUrl = ConfigModel.BASEFILEPATH + FileUtil.FILE_TEMP_PATH + "small_trial_doctor_url.png";
                Thumbnails.of(trialDoctorUrl)
                        .size(293, 176)
                        .toFile(smallRrialDoctorUrl);

                Thumbnails.of(outFile)
                        .size(width, height)
                        .watermark((int enclosingWidth, int enclosingHeight, int width1, int height1, int insetLeft, int insetRight, int insetTop, int insetBottom) -> new Point(finalFm.stringWidth("药师复核：") + 900 + finalFm.stringWidth("药师"), height - 240), ImageIO.read(new File(smallRrialDoctorUrl)), 1f)
                        .outputQuality(1f).toFile(keyPath);

                Thumbnails.of(hos)
                        .size(240, 240)
                        .toFile(temphos);

                Thumbnails.of(outFile)
                        .size(width, height)
                        .watermark((int enclosingWidth, int enclosingHeight, int width1, int height1, int insetLeft, int insetRight, int insetTop, int insetBottom) -> new Point(width - 430, height - 600), ImageIO.read(new File(hos)), 1f)
                        .outputQuality(1f).toFile(keyPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createImageTwo(String keyPath, String doctorUrl, String reviewDoctorUrl, String trialDoctorUrl, long orderid, String result, List<?> drugList, String pardno) throws Exception {
        Map<String, Object> orderDetail = prescriptionMapper.getOrder(orderid);

        Map<String, Object> user = userService.getUserHeath(ModelUtil.getLong(orderDetail, "userid"));
        Font font = new Font("宋体", Font.BOLD, 28);
        Font font1 = new Font("宋体", Font.BOLD, 56);
        Font font2 = new Font("宋体", Font.BOLD, 42);
        File outFile = new File(keyPath);
        // 创建图片
        int width = 1500;
        int height = 1754;
        int index = 50;
        BufferedImage bufImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bufImg.createGraphics();
        g.setClip(0, 0, width, height);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);// 先用黑色填充整张图片,也就是背景
        g.setColor(Color.black);// 在换成黑色
        g.setFont(font);// 设置画笔字体
        FontMetrics fm = g.getFontMetrics(font);

        int webh019 = fm.stringWidth(String.format("医疗机构编码：%s", "WEBH019"));
        int no = fm.stringWidth(String.format("处方编号：%s", pardno));
        long createTime = UnixUtil.getNowTimeStamp();
        int time = fm.stringWidth(String.format("有效时间：%s至%s", UnixUtil.getDate(createTime, "yyyy-MM-dd"), UnixUtil.getDate(createTime + 3 * 24 * 60 * 60 * 1000, "yyyy-MM-dd")));
        int a = width - time - 50;
        int b = webh019 + 50;
        int c = (a - b) / 2;
        int d = (b + c) - no / 2;

        g.drawString(String.format("医疗机构编码：%s", "WEBH019"), 50, index);
        g.drawString(String.format("处方编号：%s", pardno), d, index);
        g.drawString(String.format("有效时间%s至%s", UnixUtil.getDate(createTime, "yyyy-MM-dd"), UnixUtil.getDate(createTime + 3 * 24 * 60 * 60 * 1000, "yyyy-MM-dd")), width - fm.stringWidth(String.format("有效时间%s至%s", UnixUtil.getDate(createTime, "yyyy-MM-dd"), UnixUtil.getDate(createTime + 3 * 24 * 60 * 60 * 1000, "yyyy-MM-dd"))) - 50, index);
        g.setFont(font1);
        fm = g.getFontMetrics(font1);
        index = index + 100;
        g.drawString("山屿海互联网医院电子处方", (width - fm.stringWidth("山屿海互联网医院电子处方")) / 2, index);
        g.setFont(font);
        fm = g.getFontMetrics(font);
        index = index + 70;

        g.drawString("姓名：", 100, index);
        g.drawString(getUnLineText(ModelUtil.getStr(user, "name", " "), font).getIterator(), fm.stringWidth("姓名：") + 100, index);

        g.drawString("性别：", 500, index);
        g.drawString(getUnLineText(ModelUtil.getStr(user, "gender", " "), font).getIterator(), fm.stringWidth("性别：") + 500, index);

        g.drawString("年龄：", 900, index);
        g.drawString(getUnLineText(String.format("%s岁", ModelUtil.getStr(user, "age", " ")), font).getIterator(), fm.stringWidth("年龄：") + 900, index);

        index = index + 50;
        g.drawString("体重：", 100, index);
        g.drawString(getUnLineText(ModelUtil.getInt(user, "weight", 0) == 0 ? "暂无" : String.format("%skg", ModelUtil.getInt(user, "weight", 0)), font).getIterator(), fm.stringWidth("体重：") + 100, index);

        g.drawString("身高：", 500, index);

        g.drawString(getUnLineText(ModelUtil.getInt(user, "height", 0) == 0 ? "暂无" : String.format("%scm", ModelUtil.getInt(user, "height", 0)), font).getIterator(), fm.stringWidth("身高：") + 500, index);

        g.drawString("身份证号：", 900, index);
        g.drawString(getUnLineText(ModelUtil.getStr(user, "cardno", " "), font).getIterator(), fm.stringWidth("身份证号：") + 900, index);

        index = index + 50;
        g.drawString("诊断结果：", 100, index);

        StringBuilder resultLine = new StringBuilder("诊断结果：");
        for (int i = 0; i < result.length(); i++) {
            char fir = result.charAt(i);
            StringBuilder line = new StringBuilder(resultLine);
            line.append(fir);
            if (fm.stringWidth(line.toString()) > width - 200) {
                g.drawString(getUnLineText(resultLine.toString(), font).getIterator(), 100, index);
                index = index + 50;
                resultLine = new StringBuilder();
            }
            resultLine.append(fir);
            if (i == result.length() - 1) {
                g.drawString(getUnLineText(resultLine.toString(), font).getIterator(), 100, index);
                index = index + 50;
            }
        }

        /*String res = String.format("%s%s", "诊断结果：", result);
        int linenum = (width - 200) / fm.stringWidth("诊");//一行多少字
        int resnum = (int) Math.ceil((double) res.length() / linenum);//多少行
        int resStartIndex = 0;
        for (int i = 0; i < resnum; i++) {
            int endIndex = resStartIndex + linenum;
            if (endIndex > res.length()) {
                endIndex = res.length();
            }
            if (i == 0) {
                g.drawString(getUnLineText(res.substring(resStartIndex, endIndex).replaceFirst("诊断结果：", ""), font).getIterator(), fm.stringWidth("诊断结果：") + 100, index);
            } else {
                index = index + 50;
                g.drawString(getUnLineText(res.substring(resStartIndex, endIndex), font).getIterator(), 100, index);
            }
            resStartIndex = endIndex;
        }
*/
        int leng = (width - 200) / fm.stringWidth("-");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < leng; i++) {
            stringBuilder.append("-");
        }
        g.drawString(stringBuilder.toString(), 100, index);

        index = index + 50;
        g.setFont(font2);
        g.drawString("Rp", 100, index);

        g.setFont(font);
//        List<Map<String, Object>> drugList = prescriptionMapper.getPrescriptionDrugList(prescriptionId);

        for (int i = 1; i < drugList.size() + 1; i++) {
            Map<?, ?> map = (Map<?, ?>) drugList.get(i - 1);
            String format = String.format("%s、(%s 规格：%s)", i, ModelUtil.getStr(map, "apprdrugname"), ModelUtil.getStr(map, "standarddesc"));
            g.drawString(format, 100, index + 50);// 画出字符串
            g.drawString(String.format("*%s", ModelUtil.getInt(map, "totaldosage")), fm.stringWidth(format) + 200, index + 50);// 画出字符串
            long frequencyid = ModelUtil.getLong(map, "frequencyid");
            Map<String, Object> frequency = codeService.getFrequency(frequencyid);
            log.info("用法id-----------" + frequencyid);
            //  用药频率编码 medicine_freq
            String medicineFreq = ModelUtil.getStr(frequency, "code");
            //  用药频率 medicine_freq_name
            String medicineFreqName = ModelUtil.getStr(frequency, "name");
            log.info("用药频率是-----------" + medicineFreqName);
//            String medicineFreqName = ModelUtil.getStr(map, "medicinefreqname");
            int dosage = ModelUtil.getInt(map, "dosage");
            String dosageUnit = ModelUtil.getStr(map, "dosageunit");
            String method = ModelUtil.getStr(map, "method");
            method = StrUtil.isEmpty(method) ? "" : "," + method;
            String usagemethod = String.format("%s,每次%s%s%s", medicineFreqName == null ? "" : medicineFreqName, dosage, dosageUnit, method);
            g.drawString(String.format("使用方法：%s", usagemethod), 120, index + 100);// 画出字符串
            index = index + 150;
        }

        index = index + 50;
        g.drawString("（以下为空白，修改无效）", (width - fm.stringWidth("（以下为空白，修改无效）")) / 2, index);
        g.drawString("医师：", 100, height - fm.getHeight() - 100);
        g.drawString("药师审方：", 500, height - fm.getHeight() - 100);
        g.drawString("药师复核：", 900 + fm.stringWidth("药师"), height - fm.getHeight() - 100);
        g.dispose();
        ImageIO.write(bufImg, "jpg", outFile);// 输出png图片

        try {
            //医院电子章
            String hos = ConfigModel.BASEFILEPATH + FileUtil.FILE_STATIC_PATH + "hos_digital_sign.png";
            String temphos = ConfigModel.BASEFILEPATH + FileUtil.FILE_TEMP_PATH + "hos_digital_sign.png";

            //诊疗医师
            String smallDoctorUrl = ConfigModel.BASEFILEPATH + FileUtil.FILE_TEMP_PATH + "small_doctor_url.png";
            Thumbnails.of(doctorUrl)
                    .size(293, 176)
                    .toFile(smallDoctorUrl);


            FontMetrics finalFm = fm;
            Thumbnails.of(outFile)
                    .size(width, height)
                    .watermark((int enclosingWidth, int enclosingHeight, int width1, int height1, int insetLeft, int insetRight, int insetTop, int insetBottom) -> new Point(finalFm.stringWidth("医生：") + 100, height - 240), ImageIO.read(new File(smallDoctorUrl)), 1f)
                    .outputQuality(1f).toFile(keyPath);

            if (!StrUtil.isEmpty(reviewDoctorUrl)) {
                //审核医师
                String smallReviewDoctorUrl = ConfigModel.BASEFILEPATH + FileUtil.FILE_TEMP_PATH + "small_review_doctor_url.png";
                Thumbnails.of(reviewDoctorUrl)
                        .size(293, 176)
                        .toFile(smallReviewDoctorUrl);

                Thumbnails.of(outFile)
                        .size(width, height)
                        .watermark((int enclosingWidth, int enclosingHeight, int width1, int height1, int insetLeft, int insetRight, int insetTop, int insetBottom) -> new Point(finalFm.stringWidth("药师审方：") + 500, height - 240), ImageIO.read(new File(smallReviewDoctorUrl)), 1f)
                        .outputQuality(1f).toFile(keyPath);
            }

            if (!StrUtil.isEmpty(trialDoctorUrl)) {
                //审方医师
                String smallRrialDoctorUrl = ConfigModel.BASEFILEPATH + FileUtil.FILE_TEMP_PATH + "small_trial_doctor_url.png";
                Thumbnails.of(trialDoctorUrl)
                        .size(293, 176)
                        .toFile(smallRrialDoctorUrl);

                Thumbnails.of(outFile)
                        .size(width, height)
                        .watermark((int enclosingWidth, int enclosingHeight, int width1, int height1, int insetLeft, int insetRight, int insetTop, int insetBottom) -> new Point(finalFm.stringWidth("药师复核：") + 900 + finalFm.stringWidth("药师"), height - 240), ImageIO.read(new File(smallRrialDoctorUrl)), 1f)
                        .outputQuality(1f).toFile(keyPath);

                Thumbnails.of(hos)
                        .size(240, 240)
                        .toFile(temphos);

                Thumbnails.of(outFile)
                        .size(width, height)
                        .watermark((int enclosingWidth, int enclosingHeight, int width1, int height1, int insetLeft, int insetRight, int insetTop, int insetBottom) -> new Point(width - 430, height - 600), ImageIO.read(new File(hos)), 1f)
                        .outputQuality(1f).toFile(keyPath);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void createImage3(String keyPath, String reviewDoctorUrl, String trialDoctorUrl) {
        Font font = new Font("宋体", Font.BOLD, 28);
        File outFile = new File(keyPath);
        // 创建图片
        int width = 1500;
        int height = 1754;
        int index = 50;
        BufferedImage bufImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bufImg.createGraphics();
        g.setClip(0, 0, width, height);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);// 先用黑色填充整张图片,也就是背景
        g.setColor(Color.black);// 在换成黑色
        g.setFont(font);// 设置画笔字体
        FontMetrics fm = g.getFontMetrics(font);

        try {

            //医院电子章
            String hos = ConfigModel.BASEFILEPATH + FileUtil.FILE_STATIC_PATH + "hos_digital_sign.png";
            String temphos = ConfigModel.BASEFILEPATH + FileUtil.FILE_TEMP_PATH + "hos_digital_sign.png";

            if (!StrUtil.isEmpty(reviewDoctorUrl)) {
                //审核医师
                String smallReviewDoctorUrl = ConfigModel.BASEFILEPATH + FileUtil.FILE_TEMP_PATH + "small_review_doctor_url.png";
                Thumbnails.of(reviewDoctorUrl)
                        .size(293, 176)
                        .toFile(smallReviewDoctorUrl);

                Thumbnails.of(outFile)
                        .size(width, height)
                        .watermark((int enclosingWidth, int enclosingHeight, int width1, int height1, int insetLeft, int insetRight, int insetTop, int insetBottom) -> new Point(fm.stringWidth("药师审方：") + 500, height - 240), ImageIO.read(new File(smallReviewDoctorUrl)), 1f)
                        .outputQuality(1f).toFile(keyPath);
            }

            if (!StrUtil.isEmpty(trialDoctorUrl)) {
                //审方医师
                String smallRrialDoctorUrl = ConfigModel.BASEFILEPATH + FileUtil.FILE_TEMP_PATH + "small_trial_doctor_url.png";
                Thumbnails.of(trialDoctorUrl)
                        .size(293, 176)
                        .toFile(smallRrialDoctorUrl);

                Thumbnails.of(outFile)
                        .size(width, height)
                        .watermark((int enclosingWidth, int enclosingHeight, int width1, int height1, int insetLeft, int insetRight, int insetTop, int insetBottom) -> new Point(fm.stringWidth("药师复核：") + 900 + fm.stringWidth("药师"), height - 240), ImageIO.read(new File(smallRrialDoctorUrl)), 1f)
                        .outputQuality(1f).toFile(keyPath);

                Thumbnails.of(hos)
                        .size(240, 240)
                        .toFile(temphos);

                Thumbnails.of(outFile)
                        .size(width, height)
                        .watermark((int enclosingWidth, int enclosingHeight, int width1, int height1, int insetLeft, int insetRight, int insetTop, int insetBottom) -> new Point(width - 430, height - 600), ImageIO.read(new File(hos)), 1f)
                        .outputQuality(1f).toFile(keyPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传到数据中心
     *
     * @param prescriptionId
     * @return
     */
    public void uploadPrescriptionToDataCenter(long prescriptionId) {
        Map<String, Object> prescription = prescriptionMapper.getPrescription(prescriptionId);
        PrescriptionRequest request = new PrescriptionRequest();
        if (prescription != null) {
            request.setPresNo(ModelUtil.getStr(prescription, "presno"));//处方号
            request.setPresClassCode(ModelUtil.getStr(prescription, "presclasscode"));//处方类别编码
            request.setPresClassName(ModelUtil.getStr(prescription, "presclassname"));//处方类别名称
            request.setPtId(ModelUtil.getStr(prescription, "ptid"));//患者ID
            request.setMedRdNo(ModelUtil.getStr(prescription, "medrdno"));//就诊号
            request.setMedClassCode(ModelUtil.getStr(prescription, "medclasscode"));//就诊类别编码
            request.setMedClassName(ModelUtil.getStr(prescription, "medclassname"));//就诊类别名称
            request.setPtNo(ModelUtil.getStr(prescription, "ptno"));//患者姓名
            request.setGeCode(ModelUtil.getStr(prescription, "gecode"));//性别编码
            request.setGeName(ModelUtil.getStr(prescription, "gename"));//性别名称
            request.setPtAge(ModelUtil.getStr(prescription, "ptage"));//患者年龄
            request.setBirthday(ModelUtil.getStr(prescription, "birthday"));//出生日期
            request.setIdNo(ModelUtil.getStr(prescription, "idno"));//身份证号
            request.setPtTel(ModelUtil.getStr(prescription, "pttel"));//患者手机号
            request.setPtDistrict(ModelUtil.getStr(prescription, "ptdistrict"));//患者所在地区
            request.setInsClassCode(ModelUtil.getStr(prescription, "insclasscode"));//保险类别编码
            request.setInsClassName(ModelUtil.getStr(prescription, "insclassname"));//保险类别名称
            request.setVisitDeptCode(ModelUtil.getStr(prescription, "visitdeptcode"));//就诊科室编码
            request.setVisitDeptName(ModelUtil.getStr(prescription, "visitdeptname"));//就诊科室名称
            request.setPresDeptCode(ModelUtil.getStr(prescription, "presdeptcode"));//开方科室编码
            request.setPresDeptName(ModelUtil.getStr(prescription, "presdeptname"));//开方科室名称
            request.setPresTime(UnixUtil.getDate(ModelUtil.getLong(prescription, "prestime"), "yyyy-MM-dd HH:mm:ss"));//开方时间
            request.setPresDocCode(ModelUtil.getStr(prescription, "presdoccode"));//开方医生编码
            request.setPresDocName(ModelUtil.getStr(prescription, "presdocname"));//开方医生姓名
            request.setPresDocPhoteData(ModelUtil.getStr(prescription, "presdocphotedata"));//开方医师照片数据
            request.setReviewTime(UnixUtil.getDate(ModelUtil.getLong(prescription, "reviewtime"), "yyyy-MM-dd HH:mm:ss"));//审核时间
            request.setReviewDocCode(ModelUtil.getStr(prescription, "reviewdoccode"));//审核医生编码
            request.setReviewDocName(ModelUtil.getStr(prescription, "reviewdocname"));//审核医生姓名
            request.setTrialTime(UnixUtil.getDate(ModelUtil.getLong(prescription, "trialtime"), "yyyy-MM-dd HH:mm:ss"));//审方时间
            request.setTrialDocCode(ModelUtil.getStr(prescription, "trialdoccode"));//审方医生编码
            request.setTrialDocName(ModelUtil.getStr(prescription, "trialdocname"));//审方医生姓名
            request.setDiagCodeType(ModelUtil.getStr(prescription, "diagcodetype"));//诊断编码类型
            request.setDiseasesCode(ModelUtil.getStr(prescription, "diseasescode"));//疾病编码
            request.setDiseasesName(ModelUtil.getStr(prescription, "diseasesname"));//疾病名称
            request.setDiseasesType(ModelUtil.getStr(prescription, "diseasestype"));//疾病分类
            request.setMobilityFlag(ModelUtil.getStr(prescription, "mobilityflag"));//行动不便标志
            request.setLongMedicalFlag(ModelUtil.getStr(prescription, "longmedicalflag"));//病情稳定需长期服药标志
            request.setPresEffecDays(ModelUtil.getStr(prescription, "preseffecdays"));//处方有效期（单位天）
            request.setTotalPrice(ModelUtil.getStr(prescription, "totalprice"));//总金额
            request.setPresPhoto(ModelUtil.getStr(prescription, "presphoto"));//互联网医院处方图片(适合单张处方照片的情形)
            List<Map<String, Object>> drugList = prescriptionMapper.getPrescriptionDrugList(prescriptionId);
            List<Drug> drugs = new ArrayList<>();
            for (Map<String, Object> map : drugList) {
                Drug drug = new Drug();
                drug.setApprDrugName(ModelUtil.getStr(map, "apprdrugname"));//药品通用名称
                drug.setDrugCode(ModelUtil.getStr(map, "drugcode"));//药品商品编码
                drug.setDrugName(ModelUtil.getStr(map, "drugname"));//药品商品名称
                drug.setDrugForm(ModelUtil.getStr(map, "drugform"));//药品剂型
                drug.setDosAge(ModelUtil.getStr(map, "dosage"));//用药剂量-单次
                drug.setDosageUnit(ModelUtil.getStr(map, "dosageunit"));//用药剂量单位-单次
                drug.setTotalDosage(ModelUtil.getStr(map, "totaldosage"));//用药剂量-总量
                drug.setTotalDosageUnit(ModelUtil.getStr(map, "totaldosageunit"));//用药剂量单位-总量
                drug.setMedicineFreq(ModelUtil.getStr(map, "medicinefreq"));//用药频率编码
                drug.setMedicineFreqName(ModelUtil.getStr(map, "medicinefreqname"));//用药频率
                drug.setStandardDesc(ModelUtil.getStr(map, "standarddesc"));//规格
                drug.setSinglePrice(ModelUtil.getStr(map, "singleprice"));//单价
                drug.setDrugTotalPrice(ModelUtil.getStr(map, "drugtotalprice"));//金额
                drug.setComments(ModelUtil.getStr(map, "comments"));//嘱托
                drug.setAntiComments(ModelUtil.getStr(map, "anticomments"));//抗菌药说明
                drug.setDecMethName(ModelUtil.getStr(map, "decmethname"));//中药煎煮法名称
                drug.setTotalCharge(ModelUtil.getStr(map, "totalcharge"));//药量(单位为天)
                drugs.add(drug);
            }
            request.setDrugList(drugs);
            List<PresPhotosList> photosLists = new ArrayList<>();
            PresPhotosList presPhotosList = new PresPhotosList();
            presPhotosList.setPresPhotos(ModelUtil.getStr(prescription, "presphoto"));
            photosLists.add(presPhotosList);
            request.setPresPhotosList(photosLists);

//            Client client = new Client(ConfigModel.JIANGUAN);
//            BaseResponse response = client.excute(request);
//            log.info("prescriptionid：" + ModelUtil.getLong(prescription, "prescriptionid"));
//            if (response.getStatus().equals("1")) {
//                log.info("上传处方成功>>>>" + response.getErrorType());
//            } else {
//                throw new ServiceException("上传失败");
//            }
        } else {
            throw new ServiceException("处方不存在");
        }
    }

    /**
     * 药品使用详情
     *
     * @param id
     * @return
     */
    public Map<String, Object> getDrugUseDetail(long id) {
        Map<String, Object> drugs = codeService.getDrugs(id);
        if (drugs != null) {
            drugs.put("frequencylist", codeService.getFrequencyList());
            drugs.put("dosageunitlist", prescriptionMapper.getBasicsList(1));
            drugs.put("usagemethodlist", prescriptionMapper.getBasicsList(2));
            drugs.put("cyclelist", prescriptionMapper.getBasicsList(3));
        }
        return drugs;
    }

    /**
     * 医生用户的处方列表
     *
     * @param doctorId
     * @param userId
     * @return
     */
    public List<Map<String, Object>> getDoctorUserPrescriptionList(long doctorId, long userId) {
        return prescriptionMapper.getDoctorUserPrescriptionList(doctorId, userId);
    }

    /**
     * 用户的处方列表
     *
     * @param userId
     * @return
     */
    public List<Map<String, Object>> getUserPrescriptionList(long userId, int pageIndex, int pageSize) {
        return prescriptionMapper.getUserPrescriptionList(userId, pageIndex, pageSize);
    }

    /**
     * 用户的处方数量
     *
     * @param userId
     * @return
     */
    public long getUserPrescriptionCount(long userId) {
        return prescriptionMapper.getUserPrescriptionCount(userId);
    }

    /**
     * 处方列表
     *
     * @param userName
     * @param doctorName
     * @param diagnosis
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getPrescriptionList(int examine, String userName, String doctorName, String diagnosis, int pageIndex, int pageSize) {
        return prescriptionMapper.getPrescriptionList(examine, userName, doctorName, diagnosis, pageIndex, pageSize);
    }

    public List<Map<String, Object>> getPrescriptionListOneCheck(int pageIndex, int pageSize) {
        return prescriptionMapper.getPrescriptionListOne(pageIndex, pageSize);
    }

    public List<Map<String, Object>> getPrescriptionListTwoCheck(int pageIndex, int pageSize) {
        return prescriptionMapper.getPrescriptionListTwo(pageIndex, pageSize);
    }

    public long getPrescriptionListSum(int examine) {
        return prescriptionMapper.getPrescriptionListSum(examine);
    }

    /**
     * 处方详细
     *
     * @param id
     * @return
     */
    public Map<String, Object> getAdminPrescription(long id) {
        Map<String, Object> prescription = prescriptionMapper.getAdminSimplePrescription(id);
        if (prescription != null) {
            List<Map<String, Object>> drugList = prescriptionMapper.getPrescriptionDrugList(id);
            prescription.put("druglist", drugList);
        }
        return prescription;
    }

    /**
     * 预览图片
     *
     * @param doctorId 医生id
     * @param
     * @param druglist 药品列表
     * @return
     */
    public Map<String, Object> preview(long orderId, long doctorId, String diagnosis, List<?> druglist) {
        Map<String, Object> types = doctorMapper.getDoctorTypes(doctorId);
        if (ModelUtil.getInt(types, "doctype") == 4) {
            Map<String, Object> pid = doctorMapper.getPid(doctorId);
            if (pid == null) {
                throw new ServiceException("该顾问未绑定专家,不可开处方");
            } else {
                doctorId = ModelUtil.getLong(pid, "pid");
            }
        }
        Map<String, Object> results = new HashMap<>();
        String presNo = UnixUtil.getCustomRandomString();
        Map<String, Object> doctor = doctorMapper.getDoctorById(doctorId);

        //审核医生
//        Map<String, Object> reviewDoctor = doctorMapper.getReviewTrialDoctor(DoctorTypeEnum.ReviewDoctor.getCode());
//        //审方医生
//        Map<String, Object> trialDoctor = doctorMapper.getReviewTrialDoctor(DoctorTypeEnum.trialDoctor.getCode());
        //处方图片路径
        String key = String.format("%s/%s/%s.jpg", ModelUtil.getStr(doctor, "indoccode"), orderId, presNo);
        String fileName = FileUtil.setFileName(FileUtil.FILE_PRESCRIPTION_PATH, key);
        String presPhotoUrl = ConfigModel.BASEFILEPATH + fileName;
        FileUtil.createFile(presPhotoUrl);
        try {
            String doctorUrl = ConfigModel.BASEFILEPATH + FileUtil.getFileName(ModelUtil.getStr(doctor, "digitalsignurl"));
//            String reviewDoctorUrl = ConfigModel.BASEFILEPATH + FileUtil.getFileName(ModelUtil.getStr(reviewDoctor, "digitalsignurl"));
//            String trialDoctorUrl = ConfigModel.BASEFILEPATH + FileUtil.getFileName(ModelUtil.getStr(trialDoctor, "digitalsignurl"));
            createImageTwo(presPhotoUrl, doctorUrl, null, null, orderId, diagnosis, druglist, presNo);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //设置处方图片和图片流
        String photoUrl = ModelUtil.setLocalUrl(fileName);
        results.put("photourl", photoUrl);
        return results;
    }

    /**
     * 预览图片 需要审核
     *
     * @param doctorId 医生id
     * @param
     * @param druglist 药品列表
     * @return
     */
    public Map<String, Object> previewExamine(long orderId, long doctorId, String diagnosis, List<?> druglist) {
        Map<String, Object> types = doctorMapper.getDoctorTypes(doctorId);
        if (ModelUtil.getInt(types, "doctype") == 4) {
            Map<String, Object> pid = doctorMapper.getPid(doctorId);
            if (pid == null) {
                throw new ServiceException("该顾问未绑定专家,不可开处方");
            } else {
                doctorId = ModelUtil.getLong(pid, "pid");
            }
        }
        Map<String, Object> results = new HashMap<>();
        String presNo = UnixUtil.getCustomRandomString();
        Map<String, Object> doctor = doctorMapper.getDoctorById(doctorId);

        //处方图片路径
        String key = String.format("%s/%s/%s.jpg", ModelUtil.getStr(doctor, "indoccode"), orderId, presNo);
        String fileName = FileUtil.setFileName(FileUtil.FILE_PRESCRIPTION_PATH, key);
        String presPhotoUrl = ConfigModel.BASEFILEPATH + fileName;
        FileUtil.createFile(presPhotoUrl);
        try {
            String doctorUrl = ConfigModel.BASEFILEPATH + FileUtil.getFileName(ModelUtil.getStr(doctor, "digitalsignurl"));
            createImageTwo(presPhotoUrl, doctorUrl, null, null, orderId, diagnosis, druglist, presNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //设置处方图片和图片流
        String photoUrl = ModelUtil.setLocalUrl(fileName);
        results.put("photourl", photoUrl);
        return results;
    }

    /**
     * 预览图片
     *
     * @param
     * @param druglist 药品列表
     * @return
     */
    public Map<String, Object> appDoctorPreview(long orderId, String diagnosis, List<?> druglist) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> orderDetail = prescriptionMapper.getOrder(orderId);
        Map<String, Object> user = userService.getUserHeaths(ModelUtil.getLong(orderDetail, "userid"));
        String utl = ModelUtil.setLocalUrl(FileUtil.FILE_STATIC_PATH + "hos_digital_sign.png");
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> getDetail = prescriptionMapper.getDetail(ModelUtil.getLong(orderDetail, "doctorid"));
        map.put("diagnosis", diagnosis);
        map.put("signimg", ModelUtil.getStr(getDetail, "signimg"));
        map.put("druglist", druglist);
        result.put("userinfo", user);
        result.put("hosdigitalsign", utl);
        result.put("oftenPrescription", map);
        result.put("presno", UnixUtil.getCustomRandomString());
        result.put("createtime", UnixUtil.getNowTimeStamp());
        return result;
    }

    public Map<String, Object> getAnswerOrderDoctorId(long orderid) {
        return doctorMapper.getAnswerOrderDoctorId(orderid);
    }

    public Map<String, Object> getPhoneOrderDoctorId(long orderid) {
        return doctorMapper.getPhoneOrderDoctorId(orderid);
    }

    public Map<String, Object> getVideoOrderDoctorId(long orderid) {
        return doctorMapper.getVideoOrderDoctorId(orderid);
    }

    //处方详情
    public Map<String, Object> getPrescription(long prescriptionId) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> prescription = prescriptionMapper.getPrescriptionSimple(prescriptionId);
        if (prescription != null) {
            Map<String, Object> userHeath = userService.getUserHealth(ModelUtil.getLong(prescription, "userid"));
            Map<String, Object> doctor = doctorMapper.getDoctordSignUrl(ModelUtil.getLong(prescription, "doctorid"));
            List<Map<String, Object>> drugList = prescriptionMapper.getSimplePrescriptionDrugList(prescriptionId);
            String utl = ModelUtil.setLocalUrl(FileUtil.FILE_STATIC_PATH + "hos_digital_sign.png");
            prescription.put("signimg", ModelUtil.getStr(doctor, "digitalsignurl"));
            prescription.put("druglist", drugList);
            result.put("userinfo", userHeath);
            result.put("hosdigitalsign", utl);
            result.put("oftenPrescription", prescription);
            result.put("presno", ModelUtil.getStr(prescription, "presno"));
            result.put("createtime", ModelUtil.getLong(prescription, "createtime"));
        } else {
            throw new ServiceException("处方不存在");
        }
        return result;
    }

    //需要后台审核
    public long sendPrescriptionExamine(long orderid, long doctorid, String diagnosis, List<?> druglist, int orderType) {
        Map<String, Object> map = previewSaveExamine(orderid, doctorid, diagnosis, druglist);
        String presNo = ModelUtil.getStr(map, "preno");
        String photoUrl = ModelUtil.getStr(map, "photourl");
        Long prescriptionId = savePrestionExamine(orderid, diagnosis, druglist, presNo, photoUrl, orderType);
        return prescriptionId;
    }

    //需要后台审核
    public long sendUpdatePrescriptionExamine(long orderid, long prescriptionId, String diagnosis, List<?> druglist, int orderType) {
        Map<String, Object> map = previewUpdateExamine(orderid, prescriptionId, diagnosis, druglist);
        String photoUrl = ModelUtil.getStr(map, "photourl");
        updatePrestionExamine(prescriptionId, diagnosis, druglist, photoUrl);
        return prescriptionId;
    }

    //后台审核药师审核
    public long prescriptionReviewExamine(long prescriptionId, int examine, String remark, long agentid) {
        Map<String, Object> prescription = prescriptionMapper.getPrescriptionSimple(prescriptionId);
        int examine1 = ModelUtil.getInt(prescription, "examine");
        if (examine1 != 1) {
            throw new ServiceException("该状态下不能审核");
        }

        long time = 0;
        String reviewIndoccode = null;
        String reviewDocname = null;
        String presPhoto = null;
        String url = ModelUtil.getStr(prescription, "presphotourl");
        String presphotourl = ConfigModel.BASEFILEPATH + FileUtil.getFileName(url);
        //审核医生
        Map<String, Object> reviewDoctor = doctorMapper.getReviewTrialDoctor(DoctorTypeEnum.ReviewDoctor.getCode());

        reviewIndoccode = ModelUtil.getStr(reviewDoctor, "indoccode");
        reviewDocname = ModelUtil.getStr(reviewDoctor, "docname");
        //一审成功
        if (examine == 1) {
            examine = 4;
            String reviewDoctorUrl = ConfigModel.BASEFILEPATH + FileUtil.getFileName(ModelUtil.getStr(reviewDoctor, "digitalsignurl"));
            time = UnixUtil.getNowTimeStamp();
            createImage3(presphotourl, reviewDoctorUrl, null);
        } else if (examine == 0) {
            examine = 3;
            long orderid = ModelUtil.getLong(prescription, "orderid");
            long doctorid = ModelUtil.getLong(prescription, "doctorid");
            long userid = ModelUtil.getLong(prescription, "userid");
            Map<String, Object> problem = answerMapper.getProblem(orderid);
            int ordertype = ModelUtil.getInt(prescription, "ordertype");
            int states = ModelUtil.getInt(problem, "states");
            //图文正在聊天需要发送到聊天页面
            if (ordertype == OrderTypeEnum.Answer.getCode() && (states == AnswerOrderStateEnum.Paid.getCode() || states == AnswerOrderStateEnum.WaitReply.getCode())) {
                long id = answerMapper.addAnswer(userid, doctorid, orderid, TextFixed.prescriptionFailDoctorTips, 0, QAContentTypeEnum.DoctorTips.getCode(), 2);
                answerMapper.updateAnswer(orderid);
                //websocket
                answerService.sendSocket(id);

                long id1 = answerMapper.addAnswer(userid, doctorid, orderid, String.valueOf(prescriptionId), 0, QAContentTypeEnum.PrescriptionFail.getCode(), 1);
                answerMapper.updateAnswer(orderid);
                //websocket
                answerService.sendSocket(id1);
            }
        } else {
            throw new ServiceException("审核状态错误");
        }
        presPhoto = BASE64.base64Encoding(PicUtils.compressPicForScale(BASE64.readFile(Paths.get(presphotourl)), 200));
        prescriptionMapper.updateReviewPrescriptionExamine(prescriptionId, presPhoto, examine, remark, time, reviewIndoccode, reviewDocname, agentid);

        Map<String, Object> check = doctorMapper.getCheck().get(0);
        int twocheck = ModelUtil.getInt(check, "twocheck");//审方是否自动审核
        if (examine == 4 && twocheck == 1) {
            prescriptionTrialExamine(prescriptionId, 1, remark, 1, agentid);
        }
        return prescriptionId;
    }

    //后台审方审核
    public long prescriptionTrialExamine(long prescriptionId, int examine, String remark, int twocheck, long agentid) {
        Map<String, Object> prescription = prescriptionMapper.getPrescriptionSimple(prescriptionId);
        int examine1 = ModelUtil.getInt(prescription, "examine");
        if (examine1 != 4) {
            throw new ServiceException("该状态下不能审核");
        }

        long time = 0;
        //审方医生
        Map<String, Object> trialDoctor = doctorMapper.getReviewTrialDoctor(DoctorTypeEnum.trialDoctor.getCode());


        String trialIndoccode = ModelUtil.getStr(trialDoctor, "indoccode");
        String trialDocname = ModelUtil.getStr(trialDoctor, "docname");
        String url = ModelUtil.getStr(prescription, "presphotourl");
        String presphotourl = ConfigModel.BASEFILEPATH + FileUtil.getFileName(url);
        String presPhoto = BASE64.base64Encoding(PicUtils.compressPicForScale(BASE64.readFile(Paths.get(presphotourl)), 200));
        long userid = ModelUtil.getLong(prescription, "userid");
        long doctorid = ModelUtil.getLong(prescription, "doctorid");
        long orderid = ModelUtil.getLong(prescription, "orderid");
        int ordertype = ModelUtil.getInt(prescription, "ordertype");
        Map<String, Object> problem = answerMapper.getProblem(orderid);
        int states = ModelUtil.getInt(problem, "states");
        if (examine == 1) {
            examine = 2;
            String trialDoctorUrl = ConfigModel.BASEFILEPATH + FileUtil.getFileName(ModelUtil.getStr(trialDoctor, "digitalsignurl"));

            time = UnixUtil.getNowTimeStamp();

            createImage3(presphotourl, null, trialDoctorUrl);

            //图片转成流
            presPhoto = BASE64.base64Encoding(PicUtils.compressPicForScale(BASE64.readFile(Paths.get(presphotourl)), 200));

            try {
                sendCenter(prescriptionId);
            } catch (Exception e) {
                throw new ServiceException("处方上传到数据中心超时，请检查");
            }

            //图文正在聊天需要发送到聊天页面
            if (ordertype == OrderTypeEnum.Answer.getCode() && (states == AnswerOrderStateEnum.Paid.getCode() || states == AnswerOrderStateEnum.WaitReply.getCode())) {
                long id = answerMapper.addAnswer(userid, doctorid, orderid, String.valueOf(prescriptionId), 0, QAContentTypeEnum.Prescription.getCode(), 1);
                answerMapper.updateAnswer(orderid);
                //websocket
                answerService.sendSocket(id);
            }
        } else if (examine == 0) {
            examine = 3;

            //图文正在聊天需要发送到聊天页面
            if (ordertype == OrderTypeEnum.Answer.getCode() && (states == AnswerOrderStateEnum.Paid.getCode() || states == AnswerOrderStateEnum.WaitReply.getCode())) {
                long id = answerMapper.addAnswer(userid, doctorid, orderid, TextFixed.prescriptionFailDoctorTips, 0, QAContentTypeEnum.DoctorTips.getCode(), 2);
                answerMapper.updateAnswer(orderid);
                //websocket
                answerService.sendSocket(id);

                long id1 = answerMapper.addAnswer(userid, doctorid, orderid, String.valueOf(prescriptionId), 0, QAContentTypeEnum.PrescriptionFail.getCode(), 1);
                answerMapper.updateAnswer(orderid);
                //websocket
                answerService.sendSocket(id1);
            }
        } else {
            throw new ServiceException("审核状态错误");
        }
        prescriptionMapper.updateTrialPrescriptionExamine(prescriptionId, presPhoto, examine, remark, time, trialIndoccode, trialDocname, twocheck, agentid);

        return prescriptionId;
    }

    /**
     * 保存图片
     *
     * @param doctorId 医生id
     * @param
     * @param druglist 药品列表
     * @return
     */
    public Map<String, Object> previewSave(long orderId, long doctorId, String diagnosis, List<?> druglist) {
        Map<String, Object> types = doctorMapper.getDoctorTypes(doctorId);
        if (ModelUtil.getInt(types, "doctype") == 4) {
            Map<String, Object> pid = doctorMapper.getPid(doctorId);
            if (pid == null) {
                throw new ServiceException("该顾问未绑定专家,不可开处方");
            } else {
                doctorId = ModelUtil.getLong(pid, "pid");
            }
        }
        Map<String, Object> results = new HashMap<>();
        String presNo = UnixUtil.getCustomRandomString();
        Map<String, Object> doctor = doctorMapper.getDoctorById(doctorId);

        //审核医生
        Map<String, Object> reviewDoctor = doctorMapper.getReviewTrialDoctor(DoctorTypeEnum.ReviewDoctor.getCode());
        //审方医生
        Map<String, Object> trialDoctor = doctorMapper.getReviewTrialDoctor(DoctorTypeEnum.trialDoctor.getCode());

        //处方图片路径
        String key = String.format("%s/%s/%s.jpg", ModelUtil.getStr(doctor, "indoccode"), orderId, presNo);
        String fileName = FileUtil.setFileName(FileUtil.FILE_PRESCRIPTION_PATH, key);
        String presPhotoUrl = ConfigModel.BASEFILEPATH + fileName;
        FileUtil.createFile(presPhotoUrl);
        try {
            String doctorUrl = ConfigModel.BASEFILEPATH + FileUtil.getFileName(ModelUtil.getStr(doctor, "digitalsignurl"));
            String reviewDoctorUrl = ConfigModel.BASEFILEPATH + FileUtil.getFileName(ModelUtil.getStr(reviewDoctor, "digitalsignurl"));
            String trialDoctorUrl = ConfigModel.BASEFILEPATH + FileUtil.getFileName(ModelUtil.getStr(trialDoctor, "digitalsignurl"));
            createImageTwo(presPhotoUrl, doctorUrl, reviewDoctorUrl, trialDoctorUrl, orderId, diagnosis, druglist, presNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //图片转成流
        String presPhoto = BASE64.base64Encoding(PicUtils.compressPicForScale(BASE64.readFile(Paths.get(presPhotoUrl)), 200));
        //设置处方图片和图片流
        String photoUrl = ModelUtil.setLocalUrl(fileName);
        results.put("photourl", photoUrl);
        results.put("preno", presNo);
        results.put("presphoto", presPhoto);
        results.put("key", key);
        return results;
    }

    /**
     * 保存图片
     *
     * @param doctorId 医生id
     * @param
     * @param druglist 药品列表
     * @return
     */
    public Map<String, Object> previewSaveExamine(long orderId, long doctorId, String diagnosis, List<?> druglist) {
        Map<String, Object> types = doctorMapper.getDoctorTypes(doctorId);
        if (ModelUtil.getInt(types, "doctype") == 4) {
            Map<String, Object> pid = doctorMapper.getPid(doctorId);
            if (pid == null) {
                throw new ServiceException("该顾问未绑定专家,不可开处方");
            } else {
                doctorId = ModelUtil.getLong(pid, "pid");
            }
        }
        Map<String, Object> results = new HashMap<>();
        String presNo = UnixUtil.getCustomRandomString();
        Map<String, Object> doctor = doctorMapper.getDoctorById(doctorId);
        Map<String, Object> check = doctorMapper.getCheck().get(0);
        int onecheck = ModelUtil.getInt(check, "onecheck");//审核是否自动审核
        int twocheck = ModelUtil.getInt(check, "twocheck");//审核是否自动审核
        String reviewDoctorUrl = null;
        String trialDoctorUrl = null;
        if (onecheck == 1) {
            //审核医生
            Map<String, Object> reviewDoctor = doctorMapper.getReviewTrialDoctor(DoctorTypeEnum.ReviewDoctor.getCode());
            reviewDoctorUrl = ConfigModel.BASEFILEPATH + FileUtil.getFileName(ModelUtil.getStr(reviewDoctor, "digitalsignurl"));
        }

        if (onecheck == 1 && twocheck == 1) {
            //审方医生
            Map<String, Object> trialDoctor = doctorMapper.getReviewTrialDoctor(DoctorTypeEnum.trialDoctor.getCode());
            trialDoctorUrl = ConfigModel.BASEFILEPATH + FileUtil.getFileName(ModelUtil.getStr(trialDoctor, "digitalsignurl"));
        }


        //处方图片路径
        String key = String.format("%s/%s/%s.jpg", ModelUtil.getStr(doctor, "indoccode"), orderId, presNo);
        String fileName = FileUtil.setFileName(FileUtil.FILE_PRESCRIPTION_PATH, key);
        String presPhotoUrl = ConfigModel.BASEFILEPATH + fileName;
        FileUtil.createFile(presPhotoUrl);
        try {
            String doctorUrl = ConfigModel.BASEFILEPATH + FileUtil.getFileName(ModelUtil.getStr(doctor, "digitalsignurl"));
            createImageTwo(presPhotoUrl, doctorUrl, reviewDoctorUrl, trialDoctorUrl, orderId, diagnosis, druglist, presNo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //设置处方图片和图片流
        String photoUrl = ModelUtil.setLocalUrl(fileName);
        results.put("photourl", photoUrl);
        results.put("preno", presNo);
        results.put("key", key);
        return results;
    }

    /**
     * 保存图片
     *
     * @param
     * @param druglist 药品列表
     * @return
     */
    public Map<String, Object> previewUpdateExamine(long orderId, long prescriptionId, String diagnosis, List<?> druglist) {
        Map<String, Object> results = new HashMap<>();
        Map<String, Object> prescription = prescriptionMapper.getAppSimplePrescription(prescriptionId);
        String presNo = ModelUtil.getStr(prescription, "presno");
        //处方图片路径
        String fileName = FileUtil.getFileName(ModelUtil.getStr(prescription, "presphotourl"));
        String presPhotoUrl = ConfigModel.BASEFILEPATH + fileName;
        try {
            String doctorUrl = ConfigModel.BASEFILEPATH + FileUtil.getFileName(ModelUtil.getStr(prescription, "docsignimg"));
            createImageTwo(presPhotoUrl, doctorUrl, null, null, orderId, diagnosis, druglist, presNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //设置处方图片和图片流
        String photoUrl = ModelUtil.setLocalUrl(fileName);
        results.put("photourl", photoUrl);
        results.put("preno", presNo);
        return results;
    }


    //发送处方需要审核
    public Long savePrestionExamine(long orderId, String diagnosis, List<?> druglist, String presNo, String photoUrl, int orderType) {
        Map<String, Object> problem = answerMapper.getProblem(orderId);

        // 就诊类别编码 med_class_code 1
        String medClassCode = "1";
        // 就诊类别名称 med_class_name 图文问诊
        String medClassName = "图文问诊";
        if (orderType == OrderTypeEnum.Answer.getCode()) {
            medClassCode = "1";
            medClassName = "图文问诊";
        } else if (orderType == OrderTypeEnum.Phone.getCode()) {
            problem = answerMapper.getPhoneOrderDetail(orderId);
            medClassCode = "3";
            medClassName = "电话问诊";
        } else if (orderType == OrderTypeEnum.Video.getCode()) {
            problem = answerMapper.getVideoOrderDetail(orderId);
            medClassCode = "4";
            medClassName = "视频问诊";
        }

        long doctorId = ModelUtil.getLong(problem, "doctorid");
        long userId = ModelUtil.getLong(problem, "userid");
        // 就诊号 med_rd_no
        String medRdNo = ModelUtil.getStr(problem, "orderno");
        // 就诊类别编码 med_class_code 1
        //todo 处方类别编码 pres_class_code 字典 pres_class_code
        String presClassCode = "01";
        //todo 处方类别名称 pres_class_name pres_class_name  西医
        String presClassName = "西药";
        //  医疗机构编码 org_code WEBH019
        String orgCode = "WEBH019";
        //  医疗机构名称 org_name 银川山屿海互联网医院
        String orgName = "银川山屿海互联网医院";
        //todo 诊断编码类型 diag_code_type 表示疾病编码使用的标准，取值为：ICD10、GB-95、“-1”，-1表示其它标准情况
        String diagCodeType = "ICD10";
        //todo 疾病编码 diseases_code 参见疾病字典,西医使用ICD10，中医使用GB-95
        String diseasesCode = "A01.0";
        //todo 疾病名称 diseases_name
        String diseasesName = "伤寒";
        //  疾病分类 diseases_type 取字典 0：普通
        String diseasesType = "0";
        // 行动不便标志 mobility_flag 用来判断药量是否超标，取值如下：0：否，1：是，3：未知
        String mobilityFlag = "3";
        // 病情稳定需长期服药标志 long_medical_flag 用来判断药量是否超标，取值如下：0：否，1：是，3：未知
        String longMedicalFlag = "3";
        // 处方有效期（单位天）pres_effec_days
        int presEffecDays = 3;
        // 总金额 total_price
        BigDecimal totalPrice = new BigDecimal(0);
        Map<String, Object> user = userService.getUser(userId);
        //  患者姓名 pt_no
        String ptNo = ModelUtil.getStr(user, "name");
        //  性别编码 0 1 2 9
        String geCode = ModelUtil.getStr(user, "gender");
        //  性别名称 ge_name
        String geName = ModelUtil.getStr(codeService.getGender(geCode), "name");
        //  出生日期 birthday
        long birthday = ModelUtil.getLong(user, "birthday");
        //  患者年龄 pt_age
        long ptAge = (UnixUtil.getNowTimeStamp() - birthday) / 365 / 24 / 60 / 60 / 1000;
        //  身份证号 id_no
        String idNo = ModelUtil.getStr(user, "cardno");
        //  患者手机号 pt_tel
        String ptTel = ModelUtil.getStr(user, "phone");
        //  患者所在地区 pt_district
        String ptDistrict = ModelUtil.getStr(user, "areas");
        if (!StrUtil.isEmpty(ptDistrict)) {
            ptDistrict = ptDistrict.substring(ptDistrict.lastIndexOf(".") + 1);
        }
        // 保险类别编码 ins_class_code 01:社会基本医疗保险,02:商业医疗保险,03:大病统筹,04:新型农村合作医疗,05:城镇居民基本医疗保险,06:公费医疗,99:其他
        String insClassCode = "99";
        // 保险类别名称 ins_class_name
        String insClassName = "其他";

        Map<String, Object> doctor = doctorMapper.getDoctorById(doctorId);
        //  就诊科室编码 visit_dept_code 关联医生科室
        //  开方科室编码 pres_dept_code 关联医生科室
        long departmentId = ModelUtil.getLong(doctor, "department_id");
        //  就诊科室名称 visit_dept_name 关联医生科室
        //  开方科室名称 pres_dept_name 关联医生科室
        String departmentName = ModelUtil.getStr(doctor, "departmentname");
        String departmentcode = ModelUtil.getStr(doctor, "departmentcode");
        //  开方时间 pres_time 形式如“YYYY-MM-DD”+空格+“ hh:mm:ss”
        long presTime = UnixUtil.getNowTimeStamp();
        //  开方医生编码 pres_doc_code
        String presDocCode = ModelUtil.getStr(doctor, "indoccode");
        //  开方医生姓名 pres_doc_name
        String presDocName = ModelUtil.getStr(doctor, "docname");
        //  开方医师照片数据 pres_doc_phote_data
        String presDocPhoteData = ModelUtil.getStr(doctor, "docphoto");

        // 审核时间 review_time review_trial
        long reviewTime = 0;
        // 审核医生编码 review_doc_code
        String reviewDocCode = null;
        // 审核医生姓名 review_doc_name
        String reviewDocName = null;
        // 审方时间 trial_time
        long trialTime = 0;
        // 审方医生编码 trial_doc_code
        String trialDocCode = null;
        // 审方医生姓名 trial_doc_name
        String trialDocName = null;

        int examine = 1;

        Map<String, Object> check = doctorMapper.getCheck().get(0);
        int onecheck = ModelUtil.getInt(check, "onecheck");//审核是否自动审核
        int twocheck = ModelUtil.getInt(check, "twocheck");//审核是否自动审核
        if (onecheck == 1) {
            examine = 4;
            //审核医生
            Map<String, Object> reviewDoctor = doctorMapper.getReviewTrialDoctor(DoctorTypeEnum.ReviewDoctor.getCode());
            // 审核时间 review_time review_trial
            reviewTime = UnixUtil.getNowTimeStamp();
            // 审核医生编码 review_doc_code
            reviewDocCode = ModelUtil.getStr(reviewDoctor, "indoccode");
            // 审核医生姓名 review_doc_name
            reviewDocName = ModelUtil.getStr(reviewDoctor, "docname");
        }

        if (onecheck == 1 && twocheck == 1) {
            examine = 2;
            //审方医生
            Map<String, Object> trialDoctor = doctorMapper.getReviewTrialDoctor(DoctorTypeEnum.trialDoctor.getCode());
            // 审方时间 trial_time
            trialTime = UnixUtil.getNowTimeStamp();
            // 审方医生编码 trial_doc_code
            trialDocCode = ModelUtil.getStr(trialDoctor, "indoccode");
            // 审方医生姓名 trial_doc_name
            trialDocName = ModelUtil.getStr(trialDoctor, "docname");
        }

        // 互联网医院处方图片列表(适合多张处方照片的情形) pres_photos_list 不是必填
        // 互联网医院处方图片(适合多张处方照片的情形) pres_photos 不是必填
        long prescriptionId = prescriptionMapper.addPrescription(presNo, diagnosis, userId, presClassCode, presClassName, medRdNo, medClassCode, medClassName, ptNo, geCode, geName,
                birthday, ptAge, idNo, ptTel, ptDistrict, insClassCode, insClassName, orgCode, orgName, departmentcode, departmentName, presTime, doctorId, presDocCode, presDocName,
                presDocPhoteData, reviewTime, reviewDocCode, reviewDocName, trialTime, trialDocCode, trialDocName, diagCodeType, diseasesCode, diseasesName, diseasesType,
                mobilityFlag, longMedicalFlag, presEffecDays, totalPrice, orderId, orderType, examine, onecheck, twocheck);
        // 药品列表 drug_list
        if (druglist.size() == 0) {
            throw new ServiceException("药品不能为空");
        }
        for (Object object : druglist) {
            Map<?, ?> map = (Map<?, ?>) object;
            long drugId = ModelUtil.getLong(map, "id");
            Map<String, Object> drugs = codeService.getDrugs(drugId);
            // 药品通用名称 appr_drug_name 药品列表的子节点，药品通用名称参考药品字典
            String apprDrugName = ModelUtil.getStr(drugs, "appdrugname");

            // 药品商品编码 drug_code 药品列表的子节点,填写国药准字号编码
            String drugCode = apprDrugName;// ModelUtil.getStr(map, "drugcode");
            // 药品商品名称 drug_name 药品列表的子节点，填写国药准字号对应名称
            String durgName = apprDrugName;//ModelUtil.getStr(map, "drugname");

//            //  药品剂型 drug_form
//            String drugForm = ModelUtil.getStr(map, "drugform");
            // todo 药品剂型 drug_form
            String drugForm = "1";//ModelUtil.getStr(map, "drugform");
            //  用药剂量-单次 dosage
            String dosage = ModelUtil.getStr(map, "dosage");
            //  用药剂量单位-单次 dosage_unit
            String dosageUnit = ModelUtil.getStr(map, "dosageunit");
            //  用药剂量-总量 total_dosage
            String totalDosage = ModelUtil.getStr(map, "totaldosage");
            //  用药剂量单位-总量 total_dosage_unit
            String totalDosageUnit = ModelUtil.getStr(drugs, "totaldosageunit");
            String medicineFreq = null;
            String medicineFreqName = null;
            long frequencyid = ModelUtil.getLong(map, "frequencyid");
            if (frequencyid == 0) {
                medicineFreqName = ModelUtil.getStr(map, "medicinefreqname");
                medicineFreq = ModelUtil.getStr(map, "medicinefreq");
            } else {
                Map<String, Object> frequency = codeService.getFrequency(frequencyid);
                //  用药频率编码 medicine_freq
                medicineFreq = ModelUtil.getStr(frequency, "code");
                //  用药频率 medicine_freq_name
                medicineFreqName = ModelUtil.getStr(frequency, "name");
            }

            //  规格 standard_desc
            String standardDesc = ModelUtil.getStr(drugs, "standarddesc");
            //  单价 single_price
            BigDecimal singlePrice = ModelUtil.getDec(map, "singleprice", BigDecimal.ZERO);
            //  金额 drug_total_price
            BigDecimal drugTotalPrice = ModelUtil.getDec(map, "drugtotalprice", BigDecimal.ZERO);
            // 嘱托 comments
            String comments = "按计量使用，如服药后没有好转请停用并到医院复查";
            // 抗菌药说明 anti_comments
            String antiComments = "无";
            // 中药煎煮法名称 dec_meth_name 不是必填
            String decMethName = "";
            // 药量(单位为天) total_charge
            String totalCharge = "7";
            //药量(单位为天) total_charge
//            String totalCharge = ModelUtil.getStr(map, "totalcharge");
            //用法s
            String method = ModelUtil.getStr(map, "method");
            //周期
            String cycle = ModelUtil.getStr(map, "cycle");
            //备注
            String remark = ModelUtil.getStr(map, "remark");
            if (StrUtil.isEmpty(dosage, dosageUnit, totalDosage, method) || drugId == 0) {
                throw new ServiceException("参数错误");
            }
            prescriptionMapper.addDocDrug(prescriptionId, drugId, apprDrugName, drugCode, durgName, drugForm, dosage, dosageUnit, totalDosage, totalDosageUnit, medicineFreq, medicineFreqName, standardDesc, singlePrice, drugTotalPrice, comments, antiComments, decMethName, totalCharge, method, cycle, remark);
        }
        //图片转成流
        String presPhoto = BASE64.base64Encoding(PicUtils.compressPicForScale(BASE64.readFile(Paths.get(FileUtil.getFileName(ConfigModel.BASEFILEPATH + photoUrl))), 200));

        prescriptionMapper.setPrescriptionDocPhoto(presPhoto, photoUrl, prescriptionId);
        return prescriptionId;
    }

    //发送处方需要审核
    public Long updatePrestionExamine(long prescriptionId, String diagnosis, List<?> druglist, String photoUrl) {
        // 审核时间 review_time review_trial
        long reviewTime = 0;
        // 审核医生编码 review_doc_code
        String reviewDocCode = null;
        // 审核医生姓名 review_doc_name
        String reviewDocName = null;
        // 审方时间 trial_time
        long trialTime = 0;
        // 审方医生编码 trial_doc_code
        String trialDocCode = null;
        // 审方医生姓名 trial_doc_name
        String trialDocName = null;

        Map<String, Object> check = doctorMapper.getCheck().get(0);
        int onecheck = ModelUtil.getInt(check, "onecheck");//审核是否自动审核
        int twocheck = ModelUtil.getInt(check, "twocheck");//审核是否自动审核
        int examine = 1;
        if (onecheck == 1) {
            examine = 4;
            //审核医生
            Map<String, Object> reviewDoctor = doctorMapper.getReviewTrialDoctor(DoctorTypeEnum.ReviewDoctor.getCode());
            // 审核时间 review_time review_trial
            reviewTime = UnixUtil.getNowTimeStamp();
            // 审核医生编码 review_doc_code
            reviewDocCode = ModelUtil.getStr(reviewDoctor, "indoccode");
            // 审核医生姓名 review_doc_name
            reviewDocName = ModelUtil.getStr(reviewDoctor, "docname");
        }

        if (onecheck == 1 && twocheck == 1) {
            examine = 2;
            //审方医生
            Map<String, Object> trialDoctor = doctorMapper.getReviewTrialDoctor(DoctorTypeEnum.trialDoctor.getCode());
            // 审方时间 trial_time
            trialTime = UnixUtil.getNowTimeStamp();
            // 审方医生编码 trial_doc_code
            trialDocCode = ModelUtil.getStr(trialDoctor, "indoccode");
            // 审方医生姓名 trial_doc_name
            trialDocName = ModelUtil.getStr(trialDoctor, "docname");
        }

        prescriptionMapper.updatePrescription(prescriptionId, diagnosis, examine, reviewTime, reviewDocCode, reviewDocName, trialTime, trialDocCode, trialDocName, onecheck, twocheck);
        // 药品列表 drug_list
        if (druglist.size() == 0) {
            throw new ServiceException("药品不能为空");
        }

        prescriptionMapper.delDocDrug(prescriptionId);
        for (Object object : druglist) {
            Map<?, ?> map = (Map<?, ?>) object;
            long drugId = ModelUtil.getLong(map, "id");
            Map<String, Object> drugs = codeService.getDrugs(drugId);
            // 药品通用名称 appr_drug_name 药品列表的子节点，药品通用名称参考药品字典
            String apprDrugName = ModelUtil.getStr(drugs, "appdrugname");

            // 药品商品编码 drug_code 药品列表的子节点,填写国药准字号编码
            String drugCode = apprDrugName;// ModelUtil.getStr(map, "drugcode");
            // 药品商品名称 drug_name 药品列表的子节点，填写国药准字号对应名称
            String durgName = apprDrugName;//ModelUtil.getStr(map, "drugname");

//            //  药品剂型 drug_form
//            String drugForm = ModelUtil.getStr(map, "drugform");
            // todo 药品剂型 drug_form
            String drugForm = "1";//ModelUtil.getStr(map, "drugform");
            //  用药剂量-单次 dosage
            String dosage = ModelUtil.getStr(map, "dosage");
            //  用药剂量单位-单次 dosage_unit
            String dosageUnit = ModelUtil.getStr(map, "dosageunit");
            //  用药剂量-总量 total_dosage
            String totalDosage = ModelUtil.getStr(map, "totaldosage");
            //  用药剂量单位-总量 total_dosage_unit
            String totalDosageUnit = ModelUtil.getStr(drugs, "totaldosageunit");

            long frequencyid = ModelUtil.getLong(map, "frequencyid");
            Map<String, Object> frequency = codeService.getFrequency(frequencyid);
            //  用药频率编码 medicine_freq
            String medicineFreq = ModelUtil.getStr(frequency, "code");
            //  用药频率 medicine_freq_name
            String medicineFreqName = ModelUtil.getStr(frequency, "name");

            //  用药频率编码 medicine_freq
//            String medicineFreq = ModelUtil.getStr(map, "medicinefreq");
//            //  用药频率 medicine_freq_name
//            String medicineFreqName = ModelUtil.getStr(map, "medicinefreqname");

            //  规格 standard_desc
            String standardDesc = ModelUtil.getStr(drugs, "standarddesc");
            //  单价 single_price
            BigDecimal singlePrice = ModelUtil.getDec(map, "singleprice", BigDecimal.ZERO);
            //  金额 drug_total_price
            BigDecimal drugTotalPrice = ModelUtil.getDec(map, "drugtotalprice", BigDecimal.ZERO);
            // 嘱托 comments
            String comments = "按计量使用，如服药后没有好转请停用并到医院复查";
            // 抗菌药说明 anti_comments
            String antiComments = "无";
            // 中药煎煮法名称 dec_meth_name 不是必填
            String decMethName = "";
            // 药量(单位为天) total_charge
            String totalCharge = "7";
            //药量(单位为天) total_charge
//            String totalCharge = ModelUtil.getStr(map, "totalcharge");
            //用法s
            String method = ModelUtil.getStr(map, "method");
            //周期
            String cycle = ModelUtil.getStr(map, "cycle");
            //备注
            String remark = ModelUtil.getStr(map, "remark");
            if (StrUtil.isEmpty(dosage, dosageUnit, totalDosage, method) || drugId == 0) {
                throw new ServiceException("参数错误");
            }
            prescriptionMapper.addDocDrug(prescriptionId, drugId, apprDrugName, drugCode, durgName, drugForm, dosage, dosageUnit, totalDosage, totalDosageUnit, medicineFreq, medicineFreqName, standardDesc, singlePrice, drugTotalPrice, comments, antiComments, decMethName, totalCharge, method, cycle, remark);
        }

        //图片转成流
        String presPhoto = BASE64.base64Encoding(PicUtils.compressPicForScale(BASE64.readFile(Paths.get(FileUtil.getFileName(ConfigModel.BASEFILEPATH + photoUrl))), 200));

        prescriptionMapper.setPrescriptionDocPhoto(presPhoto, photoUrl, prescriptionId);
        return prescriptionId;
    }

    public void sendCenter(long prescriptionId) {
        uploadPrescriptionToDataCenter(prescriptionId);
    }

    public List<Map<String, Object>> getDrugsPackageList() {
        List<Map<String, Object>> packagelist = prescriptionMapper.getDrugsPackageList();
        if (packagelist.size() > 0) {
            List<Long> pidlist = new ArrayList<>();
            for (Map<String, Object> map : packagelist) {
                pidlist.add(ModelUtil.getLong(map, "value"));
            }
            Map<Long, List<Map<String, Object>>> mp = prescriptionMapper.getMiddleDrugsPackageList(pidlist);
            for (Map<String, Object> map : packagelist) {
                long id = ModelUtil.getLong(map, "value");
                map.put("children", mp.get(id));
            }
        }
        return packagelist;
    }


    /**
     * 父级药
     *
     * @return
     */
    public List<Map<String, Object>> getDrugsList() {
        return prescriptionMapper.getDrugsPackageList();
    }

    /**
     * 子级药
     *
     * @param pid
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getMiddleDrugsList(String name, long pid, int pageIndex, int pageSize) {
        return prescriptionMapper.getMiddleDrugsList(name, pid, pageIndex, pageSize);
    }

    public long getMiddleDrugsListCount(String name, long pid) {
        return prescriptionMapper.getMiddleDrugsListCount(name, pid);
    }

    public Map<String, Object> getPreDetail(long preid) {
        return prescriptionMapper.getPreDetail(preid);
    }
}
