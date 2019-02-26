package com.syhdoctor.webserver.mapper.prescription;

import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class PrescriptionBaseMapper extends BaseMapper {

    /**
     * 添加常用处方
     *
     * @param doctorId        医生id
     * @param title           标题
     * @param diagnosis       临时诊断
     * @param presClassCode   处方类别编码
     * @param presClassName   处方类别名称
     * @param medClassCode    就诊类别编码
     * @param medClassName    就诊类别名称
     * @param orgCode         医疗机构编码
     * @param orgName         医疗机构名称
     * @param diagCodeType    诊断编码类型
     * @param diseasesCode    疾病编码
     * @param diseasesName    疾病名称
     * @param diseasesType    疾病分类
     * @param mobilityFlag    行动不便标志 mobility_flag 用来判断药量是否超标，取值如下：0：否，1：是，3：未知
     * @param longMedicalFlag 病情稳定需长期服药标志 long_medical_flag 用来判断药量是否超标，取值如下：0：否，1：是，3：未知
     * @param presEffecDays   处方有效期（单位天）
     * @param totalPrice      总金额
     * @return
     */
    public long addOftenPrescription(long doctorId, String title, String diagnosis, String presClassCode, String presClassName, String medClassCode, String medClassName,
                                     String orgCode, String orgName, String diagCodeType, String diseasesCode, String diseasesName, String diseasesType, String mobilityFlag, String longMedicalFlag, int presEffecDays, BigDecimal totalPrice) {
        String sql = " insert into doc_often_prescription (doctorid, " +
                "                                    title, " +
                "                                    diagnosis, " +
                "                                    pres_class_code, " +
                "                                    pres_class_name, " +
                "                                    med_class_code, " +
                "                                    med_class_name, " +
                "                                    org_code, " +
                "                                    org_name, " +
                "                                    diag_code_type, " +
                "                                    diseases_code, " +
                "                                    diseases_name, " +
                "                                    diseases_type, " +
                "                                    mobility_flag, " +
                "                                    long_medical_flag, " +
                "                                    pres_effec_days, " +
                "                                    total_price, " +
                "                                    delflag, " +
                "                                    create_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(title);
        params.add(diagnosis);
        params.add(presClassCode);
        params.add(presClassName);
        params.add(medClassCode);
        params.add(medClassName);
        params.add(orgCode);
        params.add(orgName);
        params.add(diagCodeType);
        params.add(diseasesCode);
        params.add(diseasesName);
        params.add(diseasesType);
        params.add(mobilityFlag);
        params.add(longMedicalFlag);
        params.add(presEffecDays);
        params.add(totalPrice);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params, "id");
    }

    /**
     * 修改常用处方
     *
     * @param oftenprescriptionid 常用处方id
     * @param title               标题
     * @param diagnosis           临床诊断
     * @param presClassCode       处方类别编码
     * @param presClassName       处方类别名称
     * @param medClassCode        就诊类别编码
     * @param medClassName        就诊类别名称
     * @param orgCode             医疗机构编码
     * @param orgName             医疗机构名称
     * @param diagCodeType        诊断编码类型
     * @param diseasesCode        疾病编码
     * @param diseasesName        疾病名称
     * @param diseasesType        疾病分类
     * @param mobilityFlag        行动不便标志 mobility_flag 用来判断药量是否超标，取值如下：0：否，1：是，3：未知
     * @param longMedicalFlag     病情稳定需长期服药标志 long_medical_flag 用来判断药量是否超标，取值如下：0：否，1：是，3：未知
     * @param presEffecDays       处方有效期（单位天）
     * @param totalPrice          总金额
     * @return
     */
    public boolean updateOftenPrescription(long oftenprescriptionid, String title, String diagnosis, String presClassCode, String presClassName, String medClassCode, String medClassName,
                                           String orgCode, String orgName, String diagCodeType, String diseasesCode, String diseasesName, String diseasesType, String mobilityFlag, String longMedicalFlag, int presEffecDays, BigDecimal totalPrice) {
        String sql = " update doc_often_prescription set title=?, " +
                "                                    diagnosis=?, " +
                "                                    pres_class_code=?, " +
                "                                    pres_class_name=?, " +
                "                                    med_class_code=?, " +
                "                                    med_class_name=?, " +
                "                                    org_code=?, " +
                "                                    org_name=?, " +
                "                                    diag_code_type=?, " +
                "                                    diseases_code=?, " +
                "                                    diseases_name=?, " +
                "                                    diseases_type=?, " +
                "                                    mobility_flag=?, " +
                "                                    long_medical_flag=?, " +
                "                                    pres_effec_days=?, " +
                "                                    total_price=?, " +
                "                                    modify_time=? " +
                "                                    where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(title);
        params.add(diagnosis);
        params.add(presClassCode);
        params.add(presClassName);
        params.add(medClassCode);
        params.add(medClassName);
        params.add(orgCode);
        params.add(orgName);
        params.add(diagCodeType);
        params.add(diseasesCode);
        params.add(diseasesName);
        params.add(diseasesType);
        params.add(mobilityFlag);
        params.add(longMedicalFlag);
        params.add(presEffecDays);
        params.add(totalPrice);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(oftenprescriptionid);
        return update(sql, params) > 0;
    }

    /**
     * 添加常用处方药品
     *
     * @param apprDrugName     药品通用名称 appr_drug_name 药品列表的子节点，药品通用名称参考药品字典
     * @param drugCode         药品商品编码 drug_code 药品列表的子节点,填写国药准字号编码
     * @param drugName         药品商品名称 drug_name 药品列表的子节点，填写国药准字号对应名称
     * @param drugForm         药品剂型
     * @param dosage           用药剂量-单次
     * @param dosageUnit       用药剂量单位-单次
     * @param totalDosage      用药剂量-总量
     * @param totalDosageUnit  用药剂量单位-总量
     * @param medicineFreq     用药频率编码
     * @param medicineFreqName 用药频率
     * @param standardDesc     规格
     * @param singlePrice      单价
     * @param drugTotalPrice   金额
     * @param comments         嘱托
     * @param antiComments     抗菌药说明
     * @param decMethName      中药煎煮法名称 dec_meth_name 不是必填
     * @param totalCharge      药量(单位为天)
     * @param method           用法
     * @param cycle            周期
     * @param remark           备注
     * @return
     */
    public boolean addDocOftenDrug(long prescriptionId, long drugId, String apprDrugName, String drugCode, String drugName, String drugForm, String dosage, String dosageUnit, String totalDosage, String totalDosageUnit,
                                   long frequencyid, String medicineFreq, String medicineFreqName, String standardDesc, BigDecimal singlePrice, BigDecimal drugTotalPrice, String comments,
                                   String antiComments, String decMethName, String totalCharge, String method, String cycle, String remark) {
        String sql = " insert into doc_often_prescription_drugs (prescriptionid, " +
                "                                          drugid," +
                "                                          appr_drug_name, " +
                "                                          drug_code, " +
                "                                          drug_name, " +
                "                                          drug_form, " +
                "                                          dosage, " +
                "                                          dosage_unit, " +
                "                                          total_dosage, " +
                "                                          total_dosage_unit, " +
                "                                          frequencyid, " +
                "                                          medicine_freq, " +
                "                                          medicine_freq_name, " +
                "                                          standard_desc, " +
                "                                          single_price, " +
                "                                          drug_total_price, " +
                "                                          comments, " +
                "                                          anti_comments, " +
                "                                          dec_meth_name, " +
                "                                          total_charge, " +
                "                                          method, " +
                "                                          cycle, " +
                "                                          remark, " +
                "                                          delflag, " +
                "                                          create_time) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
        List<Object> param = new ArrayList<>();
        param.add(prescriptionId);
        param.add(drugId);
        param.add(apprDrugName);
        param.add(drugCode);
        param.add(drugName);
        param.add(drugForm);
        param.add(dosage);
        param.add(dosageUnit);
        param.add(totalDosage);
        param.add(totalDosageUnit);
        param.add(frequencyid);
        param.add(medicineFreq);
        param.add(medicineFreqName);
        param.add(standardDesc);
        param.add(singlePrice);
        param.add(drugTotalPrice);
        param.add(comments);
        param.add(antiComments);
        param.add(decMethName);
        param.add(totalCharge);
        param.add(method);
        param.add(cycle);
        param.add(remark);
        param.add(0);
        param.add(UnixUtil.getNowTimeStamp());
        return insert(sql, param) > 0;
    }

    /**
     * 添加处方
     *
     * @param presNo                处方号
     * @param diagnosis             临床诊断
     * @param userId                患者id
     * @param prescriptionTypeIds   处方类别编码
     * @param prescriptionTypeNames 处方类别名称
     * @param medRdNo               就诊号
     * @param medClassCode          就诊类别编码
     * @param medClassName          med_class_name
     * @param ptNo                  姓名
     * @param geCode                性别编码
     * @param geName                性别名称
     * @param birthday              出生日期
     * @param ptAge                 患者年龄
     * @param idNo                  身份证号
     * @param ptTel                 患者手机号
     * @param ptDistrict            患者所在地区
     * @param insClassCode          保险类别编码
     * @param insClassName          保险类别名称
     * @param orgCode               医疗机构编码
     * @param orgName               医疗机构名称
     * @param departmentName        就诊科室名称 开方科室名称
     * @param presTime              开方时间
     * @param doctorId              医生id
     * @param presDocCode           开方医生编码
     * @param presDocName           开方医生姓名
     * @param presDocPhoteData      开方医师照片数据
     * @param diagCodeType          诊断编码类型
     * @param diseasesCode          疾病编码
     * @param diseasesName          疾病名称
     * @param diseasesType          疾病分类
     * @param mobilityFlag          行动不便标志 mobility_flag 用来判断药量是否超标，取值如下：0：否，1：是，3：未知
     * @param longMedicalFlag       病情稳定需长期服药标志 long_medical_flag 用来判断药量是否超标，取值如下：0：否，1：是，3：未知
     * @param presEffecDays         处方有效期（单位天）
     * @param totalPrice            总金额
     * @param orderId               订单id
     * @return
     */
    public long addPrescription(String presNo, String diagnosis, long userId, String prescriptionTypeIds, String prescriptionTypeNames, String medRdNo, String medClassCode, String medClassName,
                                String ptNo, String geCode, String geName, long birthday, long ptAge, String idNo, String ptTel, String ptDistrict, String insClassCode, String insClassName,
                                String orgCode, String orgName, String departmentcode, String departmentName, long presTime, long doctorId, String presDocCode, String presDocName, String presDocPhoteData,
                                long reviewTime, String reviewDocCode, String reviewDocName, long trialTime, String trialDocCode, String trialDocName, String diagCodeType,
                                String diseasesCode, String diseasesName, String diseasesType, String mobilityFlag, String longMedicalFlag, int presEffecDays, BigDecimal totalPrice, long orderId, int orderType, int examine, int onecheck, int twocheck) {

        String sql = " insert into doc_prescription (pres_no, " +
                "                              diagnosis, " +
                "                              pres_class_code, " +
                "                              pres_class_name, " +
                "                              pt_id, " +
                "                              med_rd_no, " +
                "                              med_class_code, " +
                "                              med_class_name, " +
                "                              pt_no, " +
                "                              ge_code, " +
                "                              ge_name, " +
                "                              pt_age, " +
                "                              birthday, " +
                "                              id_no, " +
                "                              pt_tel, " +
                "                              pt_district, " +
                "                              ins_class_code, " +
                "                              ins_class_name, " +
                "                              org_code, " +
                "                              org_name, " +
                "                              visit_dept_code, " +
                "                              visit_dept_name, " +
                "                              pres_dept_code, " +
                "                              pres_dept_name, " +
                "                              pres_time, " +
                "                              doctorid, " +
                "                              pres_doc_code, " +
                "                              pres_doc_name, " +
                "                              pres_doc_phote_data, " +
                "                              review_time, " +
                "                              review_doc_code, " +
                "                              review_doc_name, " +
                "                              trial_time, " +
                "                              trial_doc_code, " +
                "                              trial_doc_name, " +
                "                              diag_code_type, " +
                "                              diseases_code, " +
                "                              diseases_name, " +
                "                              diseases_type, " +
                "                              mobility_flag, " +
                "                              long_medical_flag, " +
                "                              pres_effec_days, " +
                "                              total_price, " +
                "                              create_time," +
                "                              order_type," +
                "                              examine," +
                "                              onecheck," +
                "                              twocheck," +
                "                              orderid) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(presNo);
        params.add(diagnosis);
        params.add(prescriptionTypeIds);
        params.add(prescriptionTypeNames);
        params.add(userId);
        params.add(medRdNo);
        params.add(medClassCode);
        params.add(medClassName);
        params.add(ptNo);
        params.add(geCode);
        params.add(geName);
        params.add(ptAge);
        params.add(birthday);
        params.add(idNo);
        params.add(ptTel);
        params.add(ptDistrict);
        params.add(insClassCode);
        params.add(insClassName);
        params.add(orgCode);
        params.add(orgName);
        params.add(departmentcode);
        params.add(departmentName);
        params.add(departmentcode);
        params.add(departmentName);
        params.add(presTime);
        params.add(doctorId);
        params.add(presDocCode);
        params.add(presDocName);
        params.add(presDocPhoteData);
        params.add(reviewTime);
        params.add(reviewDocCode);
        params.add(reviewDocName);
        params.add(trialTime);
        params.add(trialDocCode);
        params.add(trialDocName);
        params.add(diagCodeType);
        params.add(diseasesCode);
        params.add(diseasesName);
        params.add(diseasesType);
        params.add(mobilityFlag);
        params.add(longMedicalFlag);
        params.add(presEffecDays);
        params.add(totalPrice);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(orderType);
        params.add(examine);
        params.add(onecheck);
        params.add(twocheck);
        params.add(orderId);
        return insert(sql, params, "prescriptionid");
    }

    /**
     * 修改处方
     *
     * @param prescriptionId 处方id
     * @param diagnosis      临床诊断
     * @param examine        审核状态
     * @return
     */
    public boolean updatePrescription(long prescriptionId, String diagnosis, int examine, long reviewTime, String reviewDocCode, String reviewDocName, long trialTime, String trialDocCode, String trialDocName, int onecheck, int twocheck) {
        String sql = " update doc_prescription set diagnosis=? , pres_time =? , examine=?, " +
                " review_time=?," +
                " review_doc_code=?," +
                " review_doc_name=?," +
                " trial_time=?," +
                " trial_doc_code=?," +
                " trial_doc_name=?," +
                " onecheck=?," +
                " twocheck=?" +
                " where prescriptionid=?";
        List<Object> params = new ArrayList<>();
        params.add(diagnosis);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(examine);
        params.add(reviewTime);
        params.add(reviewDocCode);
        params.add(reviewDocName);
        params.add(trialTime);
        params.add(trialDocCode);
        params.add(trialDocName);
        params.add(onecheck);
        params.add(twocheck);
        params.add(prescriptionId);
        return update(sql, params) > 0;
    }

    /**
     * 添加处方药品
     *
     * @param apprDrugName     药品通用名称 appr_drug_name 药品列表的子节点，药品通用名称参考药品字典
     * @param drugCode         药品商品编码 drug_code 药品列表的子节点,填写国药准字号编码
     * @param drugName         药品商品名称 drug_name 药品列表的子节点，填写国药准字号对应名称
     * @param drugForm         药品剂型
     * @param dosage           用药剂量-单次
     * @param dosageUnit       用药剂量单位-单次
     * @param totalDosage      用药剂量-总量
     * @param totalDosageUnit  用药剂量单位-总量
     * @param medicineFreq     用药频率编码
     * @param medicineFreqName 用药频率
     * @param standardDesc     规格
     * @param singlePrice      单价
     * @param drugTotalPrice   金额
     * @param comments         嘱托
     * @param antiComments     抗菌药说明
     * @param decMethName      中药煎煮法名称 dec_meth_name 不是必填
     * @param totalCharge      药量(单位为天)
     * @param method           用法
     * @param cycle            周期
     * @param remark           备注
     * @return
     */
    public boolean addDocDrug(long prescriptionid, long drugId, String apprDrugName, String drugCode, String drugName, String drugForm, String dosage, String dosageUnit, String totalDosage, String totalDosageUnit, String medicineFreq,
                              String medicineFreqName, String standardDesc, BigDecimal singlePrice, BigDecimal drugTotalPrice, String comments, String antiComments, String decMethName, String totalCharge, String method, String cycle, String remark) {
        String sql = "insert into doc_drug_list (prescriptionid, " +
                "                           drug_id, " +
                "                           appr_drug_name, " +
                "                           drug_code, " +
                "                           drug_name, " +
                "                           drug_form, " +
                "                           dosage, " +
                "                           dosage_unit, " +
                "                           total_dosage, " +
                "                           total_dosage_unit, " +
                "                           medicine_freq, " +
                "                           medicine_freq_name, " +
                "                           standard_desc, " +
                "                           single_price, " +
                "                           drug_total_price, " +
                "                           comments, " +
                "                           anti_comments, " +
                "                           dec_meth_name, " +
                "                           total_charge, " +
                "                           method, " +
                "                           cycle, " +
                "                           remark) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
        List<Object> param = new ArrayList<>();
        param.add(prescriptionid);
        param.add(drugId);
        param.add(apprDrugName);
        param.add(drugCode);
        param.add(drugName);
        param.add(drugForm);
        param.add(dosage);
        param.add(dosageUnit);
        param.add(totalDosage);
        param.add(totalDosageUnit);
        param.add(medicineFreq);
        param.add(medicineFreqName);
        param.add(standardDesc);
        param.add(singlePrice);
        param.add(drugTotalPrice);
        param.add(comments);
        param.add(antiComments);
        param.add(decMethName);
        param.add(totalCharge);
        param.add(method);
        param.add(cycle);
        param.add(remark);
        return insert(sql, param) > 0;
    }

    /**
     * 删除处方药品
     *
     * @param prescriptionid 处方id
     * @return
     */
    public boolean delDocDrug(long prescriptionid) {
        String sql = "update into doc_drug_list set delflag=1 whrere prescriptionid =? ";
        List<Object> param = new ArrayList<>();
        param.add(prescriptionid);
        return update(sql, param) > 0;
    }

    /**
     * 常用处方列表
     *
     * @param doctorId
     * @param pageindex
     * @param pagesize
     * @return
     */
    public List<Map<String, Object>> getOftenPrescriptionList(long doctorId, int pageindex, int pagesize) {
        String sql = " select middle.id, " +
                "       middle.diagnosis, " +
                "       dofd.appr_drug_name apprdrugname, " +
                "       dofd.total_dosage totaldosage, " +
                "       dofd.standard_desc standarddesc, " +
                "       CONCAT(dofd.medicine_freq_name,',每次',dofd.dosage,',',ifnull(dofd.dosage_unit,'')) usagemethod " +
                " from (select of.id, of.diagnosis, min(ofd.id) drugsid " +
                "      from doc_often_prescription of " +
                "             left join doc_often_prescription_drugs ofd on of.id = ofd.prescriptionid " +
                "      where of.doctorid = ? " +
                "        and ifnull(of.delflag, 0) = 0 and ifnull(ofd.delflag,0)=0 " +
                "      group by of.id) middle " +
                "       left join doc_often_prescription_drugs dofd on middle.drugsid = dofd.id ";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        return queryForList(pageSql(sql, " order by middle.id desc "), pageParams(params, pageindex, pagesize));
    }

    /**
     * 简单常用处方详情
     *
     * @param id
     * @return
     */
    public Map<String, Object> getSimpleOftenPrescription(long id) {
        String sql = " select dop.id, " +
                "       dop.doctorid, " +
                "       di.doc_name           doctorname, " +
                "       di.digital_sign_url signimg, " +
                "       cd.value              departmentname, " +
                "       cdt.value             titlename, " +
                "       dop.diagnosis, " +
                "       dop.create_time       createtime " +
                "from doc_often_prescription dop " +
                "       left join doctor_info di on dop.doctorid = di.doctorid " +
                "       left join code_department cd on di.department_id = cd.id " +
                "       left join code_doctor_title cdt on di.title_id = cdt.id " +
                "where dop.id = ? ";
        return queryForMap(sql, id);
    }

    /**
     * 简单常用处方详情
     *
     * @param id
     * @return
     */
    public Map<String, Object> getSimplePrescription(long id) {
        String sql = " select dop.id, " +
                "       dop.doctorid, " +
                "       di.doc_name           doctorname, " +
                "       di.digital_sign_url signimg, " +
                "       cd.value              departmentname, " +
                "       cdt.value             titlename, " +
                "       dop.diagnosis, " +
                "       dop.create_time       createtime " +
                "from doc_often_prescription dop " +
                "       left join doctor_info di on dop.doctorid = di.doctorid " +
                "       left join code_department cd on di.department_id = cd.id " +
                "       left join code_doctor_title cdt on di.title_id = cdt.id " +
                "where dop.id = ? ";
        return queryForMap(sql, id);
    }

    public Map<String, Object> getDetail(long doctorid) {
        String sql = "select digital_sign_url signimg from doctor_info where doctorid=?";
        return queryForMap(sql, doctorid);
    }

    /**
     * 常用处方详情
     *
     * @param id
     * @return
     */
    public Map<String, Object> getOftenPrescription(long id) {
        String sql = " select dop.doctorid, " +
                "       di.doc_name           doctorname, " +
                "       cd.value              departmentname, " +
                "       cdt.value             titlename, " +
                "       dop.diagnosis, " +
                "       dop.pres_class_code   presclasscode, " +
                "       dop.pres_class_name   presclassname, " +
                "       dop.med_class_code    medclasscode, " +
                "       dop.med_class_name    medclassname, " +
                "       dop.org_code          orgcode, " +
                "       dop.org_name          orgname, " +
                "       dop.diag_code_type    diagcodetype, " +
                "       dop.diseases_code     diseasescode, " +
                "       dop.diseases_name     diseasesname, " +
                "       dop.diseases_type     diseasestype, " +
                "       dop.mobility_flag     mobilityflag, " +
                "       dop.long_medical_flag longmedicalflag, " +
                "       dop.pres_effec_days   preseffecdays, " +
                "       dop.total_price       totalprice, " +
                "       dop.delflag, " +
                "       dop.create_time       createtime " +
                "from doc_often_prescription dop " +
                "       left join doctor_info di on dop.doctorid = di.doctorid " +
                "       left join code_department cd on di.department_id = cd.id " +
                "       left join code_doctor_title cdt on di.title_id = cdt.id " +
                "where dop.id = ? ";
        return queryForMap(sql, id);
    }

    /**
     * 常用处方药品简单列表
     *
     * @param prescriptionId
     * @return
     */
    public List<Map<String, Object>> getSimpleOftenPrescriptionDrugList(long prescriptionId) {
        String sql = " select id, " +
                "       prescriptionid, " +
                "       drugid, " +
                "       standard_desc standarddesc, " +
                "       appr_drug_name apprdrugname, " +
                "       drug_form drugform, " +
                "       dosage, " +
                "       dosage_unit dosageunit, " +
                "       total_dosage totaldosage, " +
                "       total_dosage_unit totaldosageunit, " +
                "       frequencyid, " +
                "       method, " +
                "       cycle, " +
                "       remark, " +
                "       CONCAT(medicine_freq_name,',每次',dosage,dosage_unit,',',ifnull(method,'')) usagemethod, " +
                "       create_time  createtime" +
                " from doc_often_prescription_drugs " +
                " where prescriptionid = ?  and ifnull(delflag,0)=0 ";
        return queryForList(sql, prescriptionId);
    }

    /**
     * 常用处方药品简单列表
     *
     * @param prescriptionId
     * @return
     */
    public List<Map<String, Object>> getSimplePrescriptionDrugList(long prescriptionId) {
        String sql = " select id, " +
                "       prescriptionid, " +
                "       standard_desc                                                                   standarddesc, " +
                "       appr_drug_name                                                                  apprdrugname, " +
                "       drug_form                                                                       drugform, " +
                "       dosage, " +
                "       dosage_unit                                                                     dosageunit, " +
                "       total_dosage                                                                    totaldosage, " +
                "       total_dosage_unit                                                               totaldosageunit, " +
                "       method, " +
                "       cycle, " +
                "       medicine_freq                                                                   medicinefreq, " +
                "       medicine_freq_name                                                              medicinefreqname, " +
                "       remark, " +
                "       CONCAT(medicine_freq_name, ',每次', dosage, dosage_unit, ',', ifnull(method, '')) usagemethod " +
                "from doc_drug_list " +
                "where prescriptionid = ? " +
                "  and ifnull(delflag, 0) = 0 ";
        return queryForList(sql, prescriptionId);
    }

    /**
     * 常用处方药品列表
     *
     * @param prescriptionId
     * @return
     */
    public List<Map<String, Object>> getOftenPrescriptionDrugList(long prescriptionId) {
        String sql = " select id, " +
                "       prescriptionid, " +
                "       drugid, " +
                "       appr_drug_name apprdrugname, " +
                "       drug_code drugcode, " +
                "       drug_name drugname, " +
                "       drug_form drugform, " +
                "       dosage, " +
                "       dosage_unit dosageunit, " +
                "       total_dosage totaldosage, " +
                "       total_dosage_unit totaldosageunit, " +
                "       medicine_freq medicinefreq, " +
                "       medicine_freq_name medicinefreqname, " +
                "       standard_desc standarddesc, " +
                "       single_price singleprice,  " +
                "       drug_total_price drugtotalprice, " +
                "       comments, " +
                "       anti_comments anticomments, " +
                "       dec_meth_name decmethname, " +
                "       total_charge totalcharge, " +
                "       delflag, " +
                "       method, " +
                "       cycle, " +
                "       remark, " +
                "       CONCAT(medicine_freq_name,',每次',dosage,dosage_unit,',',ifnull(method,'')) usagemethod, " +
                "       create_time  createtime" +
                " from doc_often_prescription_drugs " +
                " where prescriptionid = ?  and ifnull(delflag,0)=0 ";
        return queryForList(sql, prescriptionId);
    }

    /**
     * 处方列表
     *
     * @param userName
     * @param doctorName
     * @param diagnosis  诊断
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getPrescriptionList(int examine, String userName, String doctorName, String diagnosis, int pageIndex, int pageSize) {
        String sql = " select prescriptionid, " +
                "       pres_no             presno, " +
                "       pres_class_code     presclasscode, " +
                "       pres_class_name     presclassname, " +
                "       pt_id               ptid, " +
                "       med_rd_no           medrdno, " +
                "       med_class_code      medclasscode, " +
                "       med_class_name      medclassname, " +
                "       pt_no               ptno, " +
                "       ge_code             gecode, " +
                "       ge_name             gename, " +
                "       pt_age              ptage, " +
                "       birthday, " +
                "       id_no               idno, " +
                "       pt_tel              pttel, " +
                "       visit_dept_code     visitdeptcode, " +
                "       visit_dept_name     visitdeptname, " +
                "       pres_dept_code      presdeptcode, " +
                "       pres_dept_name      presdeptname, " +
                "       pres_time           prestime, " +
                "       pres_doc_code       presdoccode, " +
                "       pres_doc_name       presdocname, " +
                "       diag_code_type      diagcodetype, " +
                "       diseases_code       diseasescode, " +
                "       diseases_name       diseasesname, " +
                "       pres_photo_url presphotourl," +
                "       diseases_type       diseasestype, " +
                "       diagnosis, " +
                "       examine, " +
                "       remark, " +
                "       onecheck, " +
                "       twocheck, " +
                "       create_time         createtime " +
                " from doc_prescription" +
                " where ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(userName)) {
            sql += " and pt_no like ? ";
            params.add(String.format("%%%s%%", userName));
        }
        if (!StrUtil.isEmpty(doctorName)) {
            sql += " and pres_doc_name like ? ";
            params.add(String.format("%%%s%%", doctorName));
        }
        if (!StrUtil.isEmpty(diagnosis)) {
            sql += " and diagnosis like ? ";
            params.add(String.format("%%%s%%", diagnosis));
        }
        if (examine != 0) {
            sql += " and examine = ? ";
            params.add(examine);
        }
        return queryForList(pageSql(sql, " order by createtime desc "), pageParams(params, pageIndex, pageSize));
    }


    public List<Map<String, Object>> getPrescriptionListOne(int pageIndex, int pageSize) {

        String sql = " select prescriptionid, " +
                "       pres_photo_url     presphotourl " +
                " from doc_prescription" +
                " where ifnull(delflag,0)=0 and examine=1 ";
        List<Object> params = new ArrayList<>();
        return queryForList(pageSql(sql, " order by create_time asc "), pageParams(params, pageIndex, pageSize));
    }

    public long getPrescriptionListSum(int examine) {
        String sql = "  select count(prescriptionid) count from doc_prescription where ifnull(delflag,0)=0 and examine=? ";
        List<Object> params = new ArrayList<>();
        params.add(examine);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }


    public List<Map<String, Object>> getPrescriptionListTwo(int pageIndex, int pageSize) {
        String sql = " select prescriptionid, " +
                "       pres_photo_url     presphotourl " +
                " from doc_prescription" +
                " where ifnull(delflag,0)=0 and examine=4 ";
        List<Object> params = new ArrayList<>();
        return queryForList(pageSql(sql, " order by create_time asc "), pageParams(params, pageIndex, pageSize));
    }

    /**
     * 处方详情
     *
     * @param prescriptionId
     * @return
     */
    public Map<String, Object> getAdminSimplePrescription(long prescriptionId) {
        String sql = " select dp.prescriptionid, " +
                "       dp.pres_no             presno, " +
                "       dp.pres_class_code     presclasscode, " +
                "       dp.pres_class_name     presclassname, " +
                "       dp.pres_doc_name docsignimg, " +
                "       dp.review_doc_name reviewsignimg, " +
                "       dp.trial_doc_name trialsignimg, " +
                "       dp.pt_id               ptid, " +
                "       dp.med_rd_no           medrdno, " +
                "       dp.med_class_code      medclasscode, " +
                "       dp.med_class_name      medclassname, " +
                "       dp.pt_no               ptno, " +
                "       dp.ge_code             gecode, " +
                "       dp.ge_name             gename, " +
                "       dp.pt_age              ptage, " +
                "       dp.birthday, " +
                "       dp.id_no               idno, " +
                "       dp.pt_tel              pttel, " +
                "       dp.pt_district         ptdistrict, " +
                "       dp.ins_class_code      insclasscode, " +
                "       dp.ins_class_name      insclassname, " +
                "       dp.org_code            orgcode, " +
                "       dp.org_name            orgname, " +
                "       dp.visit_dept_code     visitdeptcode, " +
                "       dp.visit_dept_name     visitdeptname, " +
                "       dp.pres_dept_code      presdeptcode, " +
                "       dp.pres_dept_name      presdeptname, " +
                "       dp.pres_time           prestime, " +
                "       dp.pres_doc_code       presdoccode, " +
                "       dp.pres_doc_name       presdocname, " +
                "       dp.pres_doc_phote_data presdocphotedata, " +
                "       dp.review_time         reviewtime, " +
                "       dp.review_doc_code     reviewdoccode, " +
                "       dp.review_doc_name     reviewdocname, " +
                "       dp.trial_time          trialtime, " +
                "       dp.trial_doc_code      trialdoccode, " +
                "       dp.trial_doc_name      trialdocname, " +
                "       dp.diag_code_type      diagcodetype, " +
                "       dp.diseases_code       diseasescode, " +
                "       dp.diseases_name       diseasesname, " +
                "       dp.diseases_type       diseasestype, " +
                "       dp.mobility_flag       mobilityflag, " +
                "       dp.long_medical_flag   longmedicalflag, " +
                "       dp.pres_effec_days     preseffecdays, " +
                "       dp.total_price         totalprice, " +
                "       dp.pres_photo_url presphotourl," +
                "       dp.diagnosis , " +
                "       dp.create_time        createtime " +
                "from doc_prescription dp" +
                " where prescriptionid = ? ";
        return queryForMap(sql, prescriptionId);
    }

    /**
     * 处方详情
     *
     * @param prescriptionId
     * @return
     */
    public Map<String, Object> getAppSimplePrescription(long prescriptionId) {
        String sql = " select prescriptionid, " +
                "       pres_no                       presno, " +
                "       pt_no                         name, " +
                "       pt_age                        age, " +
                "       pt_id                         ptid, " +
                "       ge_name                         gename, " +
                "       di1.digital_sign_url docsignimg, " +
                "       di2.digital_sign_url reviewsignimg, " +
                "       di3.digital_sign_url trialsignimg, " +
                "       dp.diagnosis, " +
                "       pres_photo_url presphotourl, " +
                "       dpo.states, " +
                "       dpo.userid, " +
                "       dpo.doctorid, " +
                "       dpo.id orderid, " +
                "       dp.create_time                createtime " +
                "from doc_prescription dp " +
                "       left join doctor_info di1 on dp.pres_doc_code = di1.in_doc_code " +
                "       left join doctor_info di2 on dp.review_doc_code = di2.in_doc_code " +
                "       left join doctor_info di3 on dp.trial_doc_code = di3.in_doc_code " +
                "       left join doctor_problem_order dpo on dp.orderid = dpo.id " +
                "where prescriptionid = ? ";
        return queryForMap(sql, prescriptionId);
    }

    /**
     * 处方详情
     *
     * @param prescriptionId
     * @return
     */
    public Map<String, Object> getPrescriptionSimple(long prescriptionId) {
        String sql = " select prescriptionid, " +
                "       pt_id               userid, " +
                "       pres_no               presno, " +
                "       review_doc_code              reviewdoccode, " +
                "       trial_doc_code               trialdoccode, " +
                "       pres_photo_url               presphotourl, " +
                "       diagnosis, " +
                "       remark, " +
                "       orderid, " +
                "       examine, " +
                "       create_time createtime, " +
                "       order_type ordertype, " +
                "       doctorid " +
                "from doc_prescription where prescriptionid=? ";
        return queryForMap(sql, prescriptionId);
    }

    /**
     * 处方详情
     *
     * @param prescriptionId
     * @return
     */
    public Map<String, Object> getPreDetail(long prescriptionId) {
        String sql = " select prescriptionid, " +
                "       pres_photo_url   presphotourl " +
                "from doc_prescription where prescriptionid=? ";
        return queryForMap(sql, prescriptionId);
    }

    /**
     * 处方详情
     *
     * @param prescriptionId
     * @return
     */
    public Map<String, Object> getPrescription(long prescriptionId) {
        String sql = " select prescriptionid, " +
                "       pres_no             presno, " +
                "       pres_class_code     presclasscode, " +
                "       pres_class_name     presclassname, " +
                "       pt_id               ptid, " +
                "       med_rd_no           medrdno, " +
                "       med_class_code      medclasscode, " +
                "       med_class_name      medclassname, " +
                "       pt_no               ptno, " +
                "       ge_code             gecode, " +
                "       ge_name             gename, " +
                "       pt_age              ptage, " +
                "       birthday, " +
                "       id_no               idno, " +
                "       pt_tel              pttel, " +
                "       pt_district         ptdistrict, " +
                "       ins_class_code      insclasscode, " +
                "       ins_class_name      insclassname, " +
                "       org_code            orgcode, " +
                "       org_name            orgname, " +
                "       visit_dept_code     visitdeptcode, " +
                "       visit_dept_name     visitdeptname, " +
                "       pres_dept_code      presdeptcode, " +
                "       pres_dept_name      presdeptname, " +
                "       pres_time           prestime, " +
                "       orderid, " +
                "       examine, " +
                "       order_type ordertype, " +
                "       doctorid, " +
                "       pres_doc_code       presdoccode, " +
                "       pres_doc_name       presdocname, " +
                "       pres_doc_phote_data presdocphotedata, " +
                "       review_time         reviewtime, " +
                "       review_doc_code     reviewdoccode, " +
                "       review_doc_name     reviewdocname, " +
                "       trial_time          trialtime, " +
                "       trial_doc_code      trialdoccode, " +
                "       trial_doc_name      trialdocname, " +
                "       diag_code_type      diagcodetype, " +
                "       diseases_code       diseasescode, " +
                "       diseases_name       diseasesname, " +
                "       diseases_type       diseasestype, " +
                "       mobility_flag       mobilityflag, " +
                "       long_medical_flag   longmedicalflag, " +
                "       pres_effec_days     preseffecdays, " +
                "       total_price         totalprice, " +
                "       pres_photo          presphoto, " +
                "       diagnosis, " +
                "       create_time         createtime " +
                "from doc_prescription where prescriptionid=? ";
        return queryForMap(sql, prescriptionId);
    }

    public Map<String, Object> getOrder(long orderid) {
        String sql = "select userid,doctorid from doctor_problem_order where id=?";
        return queryForMap(sql, orderid);
    }

    /**
     * 处方药品列表
     *
     * @param prescriptionId
     * @return
     */
    public List<Map<String, Object>> getPrescriptionDrugList(long prescriptionId) {
        String sql = " select prescriptionid, " +
                "       appr_drug_name     apprdrugname, " +
                "       drug_code          drugcode, " +
                "       drug_name          drugname, " +
                "       drug_form          drugform, " +
                "       dosage, " +
                "       dosage_unit        dosageunit, " +
                "       total_dosage       totaldosage, " +
                "       total_dosage_unit  totaldosageunit, " +
                "       medicine_freq      medicinefreq, " +
                "       medicine_freq_name medicinefreqname, " +
                "       standard_desc      standarddesc, " +
                "       single_price       singleprice, " +
                "       drug_total_price   drugtotalprice, " +
                "       comments, " +
                "       anti_comments      anticomments, " +
                "       dec_meth_name      decmethname, " +
                "       method, " +
                "       total_charge       totalcharge " +
                "from doc_drug_list where prescriptionid=? and ifnull(delflag,0)=0 ";
        return queryForList(sql, prescriptionId);
    }

    /**
     * 处方药品列表
     *
     * @param prescriptionId
     * @return
     */
    public List<Map<String, Object>> getAppPrescriptionDrugList(long prescriptionId) {
        String sql = " select prescriptionid, " +
                "       appr_drug_name     apprdrugname, " +
                "       total_dosage       totaldosage, " +
                "       standard_desc      standarddesc, " +
                "       CONCAT(medicine_freq_name,',每次',dosage,dosage_unit,',',ifnull(method,'')) usagemethod " +
                "from doc_drug_list where prescriptionid=? and ifnull(delflag,0)=0 ";
        return queryForList(sql, prescriptionId);
    }

    /**
     * 获取配置
     *
     * @param type
     * @return
     */
    public List<Map<String, Object>> getBasicsList(int type) {
        String sql = " select customid id,name value from basics where type=? order by id ";
        return queryForList(sql, type);
    }

    /**
     * 删除常用处方
     *
     * @param prescriptionId 常用处方id
     * @return
     */
    public boolean delOftenPrescription(long prescriptionId) {
        String sql = " update doc_often_prescription set delflag=1 where id=? ";
        return update(sql, prescriptionId) > 0;
    }

    /**
     * 删除常用处方的药品
     *
     * @param prescriptionId 常用处方id
     * @return
     */
    public boolean delOftenPrescriptionDrug(long prescriptionId) {
        String sql = " update doc_often_prescription_drugs set delflag=1 where prescriptionid=? ";
        return update(sql, prescriptionId) > 0;
    }

    /**
     * 删除处方的药品
     *
     * @param prescriptionId 常用处方id
     * @return
     */
    public boolean delPrescriptionDrug(long prescriptionId) {
        String sql = " update doc_drug_list set delflag=1 where prescriptionid=? ";
        return update(sql, prescriptionId) > 0;
    }

    public boolean delPrescription(long prescriptionId) {
        String sql = " update doc_prescription set delflag=1 where prescriptionid=? ";
        return update(sql, prescriptionId) > 0;
    }


    /**
     * 医生患者处方列表
     *
     * @param doctorId
     * @param userId
     * @return
     */
    public List<Map<String, Object>> getDoctorUserPrescriptionList(long doctorId, long userId) {
        String sql = " select middle.prescriptionid, " +
                "       middle.diagnosis, " +
                "       dofd.appr_drug_name                                                                                 apprdrugname, " +
                "       dofd.total_charge                                                                                   totalcharge, " +
                "       dofd.standard_desc                                                                                  standarddesc, " +
                "       CONCAT(dofd.medicine_freq_name, ',每次', dofd.dosage, dofd.dosage_unit, ',', ifnull(dofd.method, '')) usagemethod, " +
                "       createtime " +
                "from (select dp.prescriptionid, dp.diagnosis, min(ofd.id) drugsid, dp.create_time as createtime " +
                "      from doc_prescription dp " +
                "             join doc_drug_list ofd on dp.prescriptionid = ofd.prescriptionid and ifnull(ofd.delflag,0)=0 " +
                "      where dp.doctorid = ? " +
                "        and pt_id = ? " +
                "      group by dp.prescriptionid) middle " +
                "       join doc_drug_list dofd on middle.drugsid = dofd.id and ifnull(dofd.delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(userId);
        return queryForList(sql, params);
    }

    /**
     * 用户的处方列表
     *
     * @param userId
     * @return
     */
    public List<Map<String, Object>> getUserPrescriptionList(long userId, int pageIndex, int pageSize) {
        String sql = "  select middle.prescriptionid, " +
                "       middle.diagnosis,  " +
                "       middle.create_time createtime,  " +
                "       middle.pres_photo_url presphotourl,  " +
                "       dofd.appr_drug_name apprdrugname,  " +
                "       dofd.total_charge totalcharge,  " +
                "       dofd.standard_desc standarddesc,  " +
                "       dofd.standard_desc standarddesc,  " +
                "       CONCAT(dofd.medicine_freq_name,',每次',dofd.dosage,dofd.dosage_unit,',',ifnull(dofd.method,'')) usagemethod  " +
                " from (select dp.prescriptionid, dp.diagnosis,dp.create_time, min(ofd.id) drugsid ,dp.pres_photo_url" +
                "      from doc_prescription dp " +
                "             left join doc_drug_list ofd on dp.prescriptionid = ofd.prescriptionid and ifnull(ofd.delflag,0)=0 " +
                "      where pt_id=?  " +
                "      group by dp.prescriptionid) middle " +
                "       left join doc_drug_list dofd on middle.drugsid = dofd.id and ifnull(dofd.delflag,0)=0  ";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        return queryForList(pageSql(sql, " order by middle.prescriptionid desc "), pageParams(params, pageIndex, pageSize));
    }

    /**
     * 用户的处方数量
     *
     * @param userId
     * @return
     */
    public long getUserPrescriptionCount(long userId) {
        String sql = "  select count(prescriptionid) count from doc_prescription where pt_id=? ";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    /**
     * 设置处方图片
     *
     * @param presPhoto      图片流
     * @param presPhotoUrl   图片路径
     * @param prescriptionId
     * @return
     */
    public boolean setPrescriptionDocPhoto(String presPhoto, String presPhotoUrl, long prescriptionId) {
        String sql = " update doc_prescription set pres_photo =?,pres_photo_url=? where prescriptionid=? ";
        List<Object> params = new ArrayList<>();
        params.add(presPhoto);
        params.add(presPhotoUrl);
        params.add(prescriptionId);
        return update(sql, params) > 0;
    }

    //审核医生审核
    public boolean updateReviewPrescriptionExamine(long prescriptionId, String presPhoto, int examine, String remark, long reviewTime, String reviewDocCode, String reviewDocName, long agentid) {
        String sql = " update doc_prescription set pres_photo=?, examine=?, remark=?,review_time=?,review_doc_code=?,review_doc_name=?,modify_user=?,modify_time=? where prescriptionid=? ";
        List<Object> params = new ArrayList<>();
        params.add(presPhoto);
        params.add(examine);
        params.add(remark);
        params.add(reviewTime);
        params.add(reviewDocCode);
        params.add(reviewDocName);
        params.add(agentid);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(prescriptionId);
        return update(sql, params) > 0;
    }

    //审方医生审核
    public boolean updateTrialPrescriptionExamine(long prescriptionId, String presPhoto, int examine, String remark, long trialTime, String trialDocCode, String trialDocName, int twocheck, long agentid) {
        String sql = " update doc_prescription set pres_photo=?, examine=?, remark=?,trial_time=?,trial_doc_code=?,trial_doc_name=?,twocheck=?,modify_user=?,modify_time=? where prescriptionid=? ";
        List<Object> params = new ArrayList<>();
        params.add(presPhoto);
        params.add(examine);
        params.add(remark);
        params.add(trialTime);
        params.add(trialDocCode);
        params.add(trialDocName);
        params.add(twocheck);
        params.add(agentid);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(prescriptionId);
        return update(sql, params) > 0;
    }
}
