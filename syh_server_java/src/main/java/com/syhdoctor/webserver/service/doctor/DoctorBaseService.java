package com.syhdoctor.webserver.service.doctor;

import com.aliyuncs.exceptions.ClientException;
import com.syhdoctor.common.utils.EnumUtils.*;
import com.syhdoctor.common.utils.*;
import com.syhdoctor.common.utils.alidayu.SendShortMsgUtil;
import com.syhdoctor.common.utils.encryption.BASE64;
import com.syhdoctor.common.utils.http.HttpParamModel;
import com.syhdoctor.common.utils.http.HttpUtil;
import com.syhdoctor.webserver.api.BaseException;
import com.syhdoctor.webserver.api.Client;
import com.syhdoctor.webserver.api.bean.*;
import com.syhdoctor.webserver.api.request.impl.DoctorBasicInfoRequest;
import com.syhdoctor.webserver.api.response.BaseResponse;
import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.controller.webapp.appapi.doctor.QRCodeUtil;
import com.syhdoctor.webserver.exception.ServiceException;
import com.syhdoctor.webserver.mapper.doctor.DoctorMapper;
import com.syhdoctor.webserver.mapper.user.UserMapper;
import com.syhdoctor.webserver.mapper.video.DoctorVideoMapper;
import com.syhdoctor.webserver.service.answer.AnswerService;
import com.syhdoctor.webserver.service.code.CodeService;
import com.syhdoctor.webserver.service.focusfigure.FocusfigureService;
import com.syhdoctor.webserver.service.hospital.HospitalService;
import com.syhdoctor.webserver.service.lecturer.LecturerService;
import com.syhdoctor.webserver.service.prescription.PrescriptionService;
import com.syhdoctor.webserver.service.system.SystemService;
import com.syhdoctor.webserver.service.user.UserService;
import com.syhdoctor.webserver.utils.QiniuUtils;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public abstract class DoctorBaseService extends BaseService {
    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private FocusfigureService focusfigureService;

    @Autowired
    private CodeService codeService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private SystemService systemService;


    @Autowired
    private LecturerService lecturerService;

    @Autowired
    private QRCodeUtil qrCodeUtil;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DoctorVideoMapper doctorVideoMapper;

    @Autowired
    private UserMapper userMapper;


    /**
     * 后台设置医生排班
     *
     * @param doctorId 医生ID
     * @param timeList 排班时间
     * @return
     */
    public boolean setUpScheduling(long doctorId, List<?> timeList) {
        for (Object obj : timeList) {
            Map<String, Object> value = (Map<String, Object>) obj;
            int id = ModelUtil.getInt(value, "id");
            int shift = ModelUtil.getInt(value, "shift");
            long visitingStartTime = ModelUtil.getLong(value, "visitingstarttime");
            long visitingEndTime = ModelUtil.getLong(value, "visitingendtime");
            boolean b = doctorMapper.findByDoctor(doctorId, visitingStartTime, visitingEndTime, shift);
            if (id == 0 && !b) {
                doctorMapper.addOnduty(doctorId, visitingStartTime, visitingEndTime, shift);
            } else {
                throw new ServiceException("该医生当天排班已存在，不可再新增");
            }
        }
        doctorMapper.updateWhetherOpen(doctorId, 1, VisitCategoryEnum.phone.getCode());
        return true;
    }

    public List<Map<String, Object>> getWaitSchedulingDoctorList() {
        return doctorMapper.getWaitSchedulingDoctorList();
    }

    public List<Map<String, Object>> getWaitSittingDoctorList() {
        return doctorMapper.getWaitSittingDoctorList();
    }

    public boolean examineState(int examineState, int id) {
        return doctorMapper.examineState(examineState, id);
    }

    public List<Map<String, Object>> getDoctorSchedulingInfo(long time) {
        return doctorMapper.getDoctorSchedulingInfo(time);
    }

    public List<Map<String, Object>> getDoctorSchedulingInfos(long time) {
        return doctorMapper.getDoctorSchedulingInfos(time);
    }

    public List<Map<String, Object>> getDoctorInfoCalendar() {
        List<Map<String, Object>> listCalendar = doctorMapper.getSchedulingCalendar();
        List<Map<String, Object>> listDoctorScheduling = doctorMapper.getDoctorSchedulingInfo(0);
        List<Map<String, Object>> listMap = new ArrayList<>();
        for (Map<String, Object> mp : listCalendar) {
            long calendar = ModelUtil.getLong(mp, "calendar");
            List<Map<String, Object>> ls = new ArrayList<>();
            for (Map<String, Object> listMp : listDoctorScheduling) {
                long value = ModelUtil.getLong(listMp, "time");
                if (calendar == value) {
                    ls.add(listMp);
                }
            }
            mp.put("child", ls);
            listMap.add(mp);
        }
        return listMap;
    }

    public List<Map<String, Object>> getSittingDoctorInfoCalendar() {
        List<Map<String, Object>> listCalendar = doctorMapper.getSchedulingCalendars();
        List<Map<String, Object>> listDoctorScheduling = doctorMapper.getDoctorSchedulingInfos(0);
        List<Map<String, Object>> listMap = new ArrayList<>();
        for (Map<String, Object> mp : listCalendar) {
            long calendar = ModelUtil.getLong(mp, "calendar");
            List<Map<String, Object>> ls = new ArrayList<>();
            for (Map<String, Object> listMp : listDoctorScheduling) {
                long value = ModelUtil.getLong(listMp, "time");
                if (calendar == value) {
                    ls.add(listMp);
                }
            }
            mp.put("child", ls);
            listMap.add(mp);
        }
        return listMap;
    }

    public List<Map<String, Object>> getDocMedPriceList(long doctorId) {
        return doctorMapper.getMedPriceList(doctorId);
    }

    public Map<String, Object> getDocMedPriceListById(int id) {
        Map<String, Object> map = doctorMapper.getDocMedPriceListById(id);
        Map<String, Object> value = new HashMap<>();
        value.put("medclassid", ModelUtil.getInt(map, "medclassid"));
        value.put("medclassname", ModelUtil.getStr(map, "medclassname"));
        map.put("value", value);
        return map;
    }

    public void addUpdateDocMedPrice(long doctorId, int id, int whetheropen, BigDecimal price, int medclassid, String medclassname) {
        if (id > 0) {
            doctorMapper.UpdateDocMedPrice(id, whetheropen, price);
        } else {
            Map<String, Object> map = doctorMapper.getOnDutyStatus(doctorId, medclassid);
            if (map != null) {
                throw new ServiceException(-1, "已经添加过" + medclassname + "价格,不能重复添加");
            } else {
                doctorMapper.addDocMedPrice(doctorId, whetheropen, price, medclassid, medclassname);
            }
        }
    }


    /**
     * 医生审核
     * 0:未认证 1:认证中 2:认证成功 3:认证失败
     *
     * @param examine
     * @return
     */
    public boolean
    examineDoctor(long doctorId, int examine, long agentId, String reason, int doctype) {
        //认证成功需要上传到数据中心
        boolean flag = false;
        Map<String, Object> doctorMp = doctorMapper.getDoctorNotBlobById(doctorId);
        if (examine == DoctorExamineEnum.certificationSuccess.getCode()) {
            DoctorBasicInfoRequest request = new DoctorBasicInfoRequest();
            if (doctorMp != null) {
                Map<String, Object> hospital = doctorMapper.findHospitals(ModelUtil.getInt(doctorMp, "hospitalid"));
                request.setDocName(ModelUtil.getStr(doctorMp, "docname"));
                int doctortype = ModelUtil.getInt(doctorMp, "doctortype");
                String docTypeName = "";
                if (doctortype == DoctorTypeEnum.ReviewDoctor.getCode() || doctortype == DoctorTypeEnum.trialDoctor.getCode()) {
                    docTypeName = "审方医生";
                } else {
                    docTypeName = "诊疗医师";
                }
                request.setDocType(docTypeName);
                request.setTitleCode(ModelUtil.getStr(doctorMp, "titlecode"));
                request.setTitleName(ModelUtil.getStr(doctorMp, "titlename"));
                request.setInDocCode(ModelUtil.getStr(doctorMp, "indoccode"));
//                request.setSignTime(UnixUtil.getDate(ModelUtil.getLong(doctorMp, "signtime"), "yyyy-MM-dd HH:mm:ss"));
//                request.setSignLife(ModelUtil.getStr(doctorMp, "signlife"));
                request.setCreditLevel(ModelUtil.getStr(doctorMp, "creditlevel"));
                request.setOccuLevel(ModelUtil.getStr(doctorMp, "occulevel"));
                request.setWorkInstCode(ModelUtil.getStr(hospital, "code"));
                request.setWorkInstName(ModelUtil.getStr(hospital, "value"));
                request.setDooTel(ModelUtil.getStr(doctorMp, "dootel"));
                request.setIdCard(ModelUtil.getStr(doctorMp, "idcard"));
                request.setPracNo(ModelUtil.getStr(doctorMp, "pracno"));
                request.setPracRecDate(UnixUtil.getDate(ModelUtil.getLong(doctorMp, "pracrecdate"), "yyyy-MM-dd"));
                request.setCertNo(ModelUtil.getStr(doctorMp, "certno"));
                request.setCertRecDate(UnixUtil.getDate(ModelUtil.getLong(doctorMp, "certrecdate"), "yyyy-MM-dd"));
                request.setTitleNo(ModelUtil.getStr(doctorMp, "titleno"));
                request.setTitleRecDate(UnixUtil.getDate(ModelUtil.getLong(doctorMp, "titlerecdate"), "yyyy-MM-dd"));
                request.setPracType(ModelUtil.getStr(doctorMp, "practype"));
                request.setQualifyOrNot("1".equals(ModelUtil.getStr(doctorMp, "qualifyornot")) ? "是" : "否");
                request.setProfessional(ModelUtil.getStr(doctorMp, "professional"));
                List<MedPriceList> medPriceLists = new ArrayList<>();
                /*
                //医生设置价格
                List<Map<String, Object>> medPriceMpList = doctorMapper.getMedPriceList(doctorId);
                if (medPriceMpList.size() < 1) {
                    throw new ServiceException("请设置医生的图文价格!");
                }
                for (Map<String, Object> mp : medPriceMpList) {
                    MedPriceList medPriceList = new MedPriceList();
                    medPriceList.setMedClassCode(ModelUtil.getStr(mp, "medclassid"));
                    medPriceList.setMedClassName(ModelUtil.getStr(mp, "medclassname"));
                    medPriceList.setPrice(ModelUtil.getStr(mp, "price"));
                    medPriceLists.add(medPriceList);
                }*/


                //默认价格0
                MedPriceList medPriceList1 = new MedPriceList();
                medPriceList1.setMedClassCode("1");
                medPriceList1.setMedClassName("图文问诊");
                medPriceList1.setPrice("0");
                medPriceLists.add(medPriceList1);

                MedPriceList medPriceList2 = new MedPriceList();
                medPriceList2.setMedClassCode("2");
                medPriceList2.setMedClassName("语音问诊");
                medPriceList2.setPrice("0");
                medPriceLists.add(medPriceList2);

                MedPriceList medPriceList3 = new MedPriceList();
                medPriceList3.setMedClassCode("3");
                medPriceList3.setMedClassName("电话问诊");
                medPriceList3.setPrice("0");
                medPriceLists.add(medPriceList3);

                MedPriceList medPriceList4 = new MedPriceList();
                medPriceList4.setMedClassCode("4");
                medPriceList4.setMedClassName("视频问诊");
                medPriceList4.setPrice("0");
                medPriceLists.add(medPriceList4);

                request.setMedPriceList(medPriceLists);


                String digitalSign = BASE64.base64Encoding(PicUtils.compressPicForScale(BASE64.readFile(Paths.get(ConfigModel.BASEFILEPATH + FileUtil.getFileName(ModelUtil.getStr(doctorMp, "digitalsignurl")))), 200));

                String digitalsignurlthumbnailKey = request.getInDocCode() + "/thumbnail/" + UnixUtil.getCustomRandomString() + ".png";
                String fileName = FileUtil.setFileName(FileUtil.FILE_DOCTOR_PATH, digitalsignurlthumbnailKey);
                String digitalsignurlthumbnail = ConfigModel.BASEFILEPATH + fileName;
                FileUtil.createFile(digitalsignurlthumbnail);

                log.info(ConfigModel.BASEFILEPATH + FileUtil.getFileName(ModelUtil.getStr(doctorMp, "digitalsignurl")));
                log.info(digitalsignurlthumbnail);

                PicUtils.getThumbnail(ConfigModel.BASEFILEPATH + FileUtil.getFileName(ModelUtil.getStr(doctorMp, "digitalsignurl")), digitalsignurlthumbnail);

                request.setDigitalSign(digitalSign);
                String mathnum = ModelUtil.getStr(doctorMp, "docpenaltypoints");
                int finalMath = 100 - Integer.parseInt(mathnum == null ? "100" : mathnum);
                request.setDocPenaltyPoints(String.valueOf(finalMath));
//                request.setYcRecordFlag(ModelUtil.getStr(doctorMp, "ycrecordflag"));
//                request.setHosConfirmFlag(ModelUtil.getStr(doctorMp, "hosconfirmflag"));
//                request.setYcPresRecordFlag(ModelUtil.getStr(doctorMp, "ycpresrecordflag"));
                List<DocMultiSitedLicRecordList> docMultiSitedLicRecordLists = new ArrayList<>();
                try {
                    List<Map<String, Object>> docMultSiteLicRecordList = doctorMapper.getDocMultiSitedLicRecord(doctorId);
                    for (Map<String, Object> mp : docMultSiteLicRecordList) {
                        int id = ModelUtil.getInt(mp, "id");
                        DocMultiSitedLicRecordList docMultiSitedLicRecordList = new DocMultiSitedLicRecordList();
                        String docmultisitedlicrecord = BASE64.base64Encoding(PicUtils.compressPicForScale(BASE64.readFile(Paths.get(ConfigModel.BASEFILEPATH + FileUtil.getFileName(ModelUtil.getStr(mp, "url")))), 200));
                        docMultiSitedLicRecordList.setDocMultiSitedLicRecord(docmultisitedlicrecord);
                        docMultiSitedLicRecordLists.add(docMultiSitedLicRecordList);
                        log.info("docmultisitedlicrecord>>>" + ConfigModel.BASEFILEPATH + FileUtil.getFileName(ModelUtil.getStr(mp, "url")));
                        if (!StrUtil.isEmpty(docmultisitedlicrecord)) {
                            doctorMapper.updateDocMultiSitedLicRecord(id, docmultisitedlicrecord);
                        } else {
                            throw new ServiceException("证件文件上传不全");
                        }
                    }
                    request.setDocMultiSitedLicRecordList(docMultiSitedLicRecordLists);

                    List<IdCardList> idCardLists = new ArrayList<>();
                    List<Map<String, Object>> IdCardList = doctorMapper.getDocIdCardList(doctorId);
                    for (Map<String, Object> mp : IdCardList) {
                        IdCardList idCardList = new IdCardList();
                        int id = ModelUtil.getInt(mp, "id");
                        String idCard = BASE64.base64Encoding(PicUtils.compressPicForScale(BASE64.readFile(Paths.get(ConfigModel.BASEFILEPATH + FileUtil.getFileName(ModelUtil.getStr(mp, "url")))), 200));
                        idCardList.setIdCard(idCard);
                        idCardLists.add(idCardList);
                        log.info("idCard>>>" + ConfigModel.BASEFILEPATH + FileUtil.getFileName(ModelUtil.getStr(mp, "url")));
                        doctorMapper.updateDoctorCard(id, idCard);
                    }
                    request.setIdCardList(idCardLists);

                    List<Map<String, Object>> certDocPracMpList = doctorMapper.getDocCertDocPracList(doctorId);
                    List<CertDocPracList> certDocPracLists = new ArrayList<>();
                    for (Map<String, Object> mp : certDocPracMpList) {
                        CertDocPracList certDocPracList = new CertDocPracList();
                        int id = ModelUtil.getInt(mp, "id");
                        String certDocPrac = BASE64.base64Encoding(PicUtils.compressPicForScale(BASE64.readFile(Paths.get(ConfigModel.BASEFILEPATH + FileUtil.getFileName(ModelUtil.getStr(mp, "url")))), 200));
                        certDocPracList.setCertDocPrac(certDocPrac);
                        certDocPracLists.add(certDocPracList);
                        doctorMapper.updateDocIdCard(id, certDocPrac);
                    }
                    request.setCertDocPracList(certDocPracLists);

                    List<Map<String, Object>> titleCertMpList = doctorMapper.getDocTitleCertList(doctorId);
                    List<TitleCertList> titleCertLists = new ArrayList<>();
                    for (Map<String, Object> mp : titleCertMpList) {
                        int id = ModelUtil.getInt(mp, "id");
                        TitleCertList titleCertList = new TitleCertList();
                        String titleCert = BASE64.base64Encoding(PicUtils.compressPicForScale(BASE64.readFile(Paths.get(ConfigModel.BASEFILEPATH + FileUtil.getFileName(ModelUtil.getStr(mp, "url")))), 200));
                        titleCertList.setTitleCert(titleCert);
                        titleCertLists.add(titleCertList);
                        doctorMapper.updateTitleCert(id, titleCert);
                    }
                    request.setTitleCertList(titleCertLists);

                    List<Map<String, Object>> docCertMpList = doctorMapper.getDocCertList(doctorId);
                    List<DocCertList> docCertLists = new ArrayList<>();
                    for (Map<String, Object> mp : docCertMpList) {
                        DocCertList docCertList = new DocCertList();
                        int id = ModelUtil.getInt(mp, "id");
                        String docCert = BASE64.base64Encoding(PicUtils.compressPicForScale(BASE64.readFile(Paths.get(ConfigModel.BASEFILEPATH + FileUtil.getFileName(ModelUtil.getStr(mp, "url")))), 200));
                        docCertList.setDocCert(docCert);
                        docCertLists.add(docCertList);
                        doctorMapper.updateDocCert(id, docCert);
                    }
                    request.setDocCertList(docCertLists);
                } catch (ServiceException e) {
                    throw new ServiceException(e.getMessage());
                } catch (Exception e) {
                    log.info(e.getMessage());
                    throw new ServiceException("认证失败，填写信息不全");
                }
                String docPhoto = BASE64.base64Encoding(PicUtils.compressPicForScale(BASE64.readFile(Paths.get(ConfigModel.BASEFILEPATH + FileUtil.getFileName(ModelUtil.getStr(doctorMp, "docphotourl")))), 200));
                request.setDocPhoto(docPhoto);

                String employfile = BASE64.base64Encoding(PicUtils.compressPicForScale(BASE64.readFile(Paths.get(ConfigModel.BASEFILEPATH + FileUtil.getFileName(ModelUtil.getStr(doctorMp, "employfileurl")))), 200));
                request.setEmployFile(employfile);

                doctorMapper.updateDoctorImageBackup(doctorId, digitalSign, employfile, docPhoto, fileName); //医生签名 合同 头像 转为流保存不在变更

                request.setAgreeTerms(ModelUtil.getInt(doctorMp, "agreeterms") == 1 ? "是" : "否");
                request.setPracScope(ModelUtil.getStr(doctorMp, "pracscope"));
                request.setPracScopeApproval(ModelUtil.getStr(doctorMp, "pracscopeapproval"));
                request.setDocMultiSitedDateStart(UnixUtil.getDate(ModelUtil.getLong(doctorMp, "docmultisiteddatestart"), "yyyy-MM-dd"));
                request.setDocMultiSitedDateEnd(UnixUtil.getDate(ModelUtil.getLong(doctorMp, "docmultisiteddateend"), "yyyy-MM-dd"));
                request.setHosOpinion(ModelUtil.getStr(doctorMp, "hosopinion"));
                request.setHosOpinionDate(UnixUtil.getDate(ModelUtil.getLong(doctorMp, "hosopiniondate"), "yyyy-MM-dd"));
                request.setDocMultiSitedDatePromise(UnixUtil.getDate(ModelUtil.getLong(doctorMp, "docmultisiteddatepromise"), "yyyy-MM-dd"));
                request.setHosDigitalSign(ModelUtil.getStr(doctorMp, "hosdigitalsign"));

                Client client = new Client(ConfigModel.BEIAN);
                try {
                    //银川服务器异常，暂时跳过上传
                    /*BaseResponse response = client.excute(request);
                    if (response.getStatus().equals("1")) {
                        if (doctype == DoctorTypeEnum.trialDoctor.getCode()) {
                            doctype = DoctorTypeEnum.trialDoctor.getCode();
                        } else if (doctype == DoctorTypeEnum.ReviewDoctor.getCode()) {
                            doctype = DoctorTypeEnum.ReviewDoctor.getCode();
                        } else {
                            doctype = DoctorTypeEnum.DoctorExpert.getCode();
                        }
                        doctorMapper.examineDoctor(doctorId, examine, agentId, reason, doctype);
                        addDoctorIntegral(doctorId, IntegralTypeEnum.Info.getCode());
                        flag = true;
                    } else {
                        Map<String, Object> map = new HashMap<>();
                        map.put("doctor", ModelUtil.getStr(doctorMp, "docname"));
                        if (doctype == DoctorTypeEnum.DoctorExpert.getCode()) {
                            SendShortMsgUtil.sendSms(ConfigModel.ISONLINE, ModelUtil.getStr(doctorMp, "dootel"), com.syhdoctor.common.config.ConfigModel.SMS.doctor_examine_fail, map);
                        }
                        throw new ServiceException("认证失败，填写信息不全!!");
                    }
                    log.info("医生认证>>>>" + response.getErrorType());*/

                    //临时方案
                    try {
                        request.Validate();
                        if (doctype == DoctorTypeEnum.trialDoctor.getCode()) {
                            doctype = DoctorTypeEnum.trialDoctor.getCode();
                        } else if (doctype == DoctorTypeEnum.ReviewDoctor.getCode()) {
                            doctype = DoctorTypeEnum.ReviewDoctor.getCode();
                        } else {
                            doctype = DoctorTypeEnum.DoctorExpert.getCode();
                        }
                        doctorMapper.examineDoctor(doctorId, examine, agentId, reason, doctype);
                        addDoctorIntegral(doctorId, IntegralTypeEnum.Info.getCode());
                        flag = true;
                    } catch (BaseException e) {
                        throw new ServiceException("认证失败，填写信息不全!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("医生认证失败>>>>" + e.getMessage());
                    throw new ServiceException("认证失败，填写信息不全!");
                }
            }
        } else {
            flag = true;
            if (doctype == DoctorTypeEnum.trialDoctor.getCode()) {
                doctype = DoctorTypeEnum.trialDoctor.getCode();
            } else if (doctype == DoctorTypeEnum.ReviewDoctor.getCode()) {
                doctype = DoctorTypeEnum.ReviewDoctor.getCode();
            } else {
                doctype = DoctorTypeEnum.DctorDiagnosis.getCode();
            }
            doctorMapper.examineDoctor(doctorId, examine, agentId, reason, doctype);
            if (examine == DoctorExamineEnum.authenticationFailed.getCode()) {
                Map<String, Object> map = new HashMap<>();
                map.put("doctor", ModelUtil.getStr(doctorMp, "docname"));
                try {
                    SendShortMsgUtil.sendSms(ConfigModel.ISONLINE, ModelUtil.getStr(doctorMp, "dootel"), com.syhdoctor.common.config.ConfigModel.SMS.doctor_examine_fail, map);
                } catch (ClientException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    /**
     * 删除医生
     *
     * @param doctorId
     * @param agentId
     * @return
     */
    public boolean deleteDoctor(long doctorId, long agentId) {
//        doctorMapper.delDoctorExtends(doctorId,agentId);//删除扩展表医生
        return doctorMapper.deleteDoctor(doctorId, agentId);
    }

    /**
     * 删除审方医生
     *
     * @param doctorId
     * @param agentId
     * @return
     */
    public boolean deleteExamineDoctor(long doctorId, long agentId) {
//        doctorMapper.delDoctorExtends(doctorId,agentId);//删除扩展表医生
        return doctorMapper.deleteDoctor(doctorId, agentId);
    }


    /**
     * 审方成功的行数
     *
     * @return
     */
    public long trialPartyOk() {
        return doctorMapper.trialPartyOk();
    }

    /**
     * 审核成功的行数
     *
     * @return
     */
    public long examineOk() {
        return doctorMapper.examineOk();
    }


    /**
     * 完善信息
     *
     * @param doctorId
     * @param
     * @return
     */
    public boolean updateDoctorInfoNew(long doctorId, String idCardpositive, String idCardside, String certpositive, String certside, String certprac, String titlecert) {
        doctorMapper.deleteDocCard(doctorId);
        doctorMapper.deleteDocCertList(doctorId);
        doctorMapper.deleteCertDocPracList(doctorId); //
        doctorMapper.deleteDocTitleCertList(doctorId);//删除职称

        List<String> cardList = new ArrayList<>();
        cardList.add(idCardpositive);
        cardList.add(idCardside);
        //身份证文件
        for (Object value : cardList) {
            String cardId = String.valueOf(value);
            doctorMapper.addDocCardList("", cardId, doctorId, doctorId);
        }
        //资格证文件
        doctorMapper.addDocCertList("", certpositive, doctorId, doctorId);
        doctorMapper.addDocCertList("", certside, doctorId, doctorId);

        //保存执业证文件
        doctorMapper.addCertDocPracList("", certprac, doctorId, doctorId);

        //保存职称文件列表
        doctorMapper.addTitleCertList("", titlecert, doctorId, doctorId);


        return doctorMapper.updateDoctorInfoNew(doctorId, "");

    }

    public boolean updateDoctorInfoFinal(long doctorId, String idCardpositive, String idCardside, String certpositive, String certside, String certprac, String titlecert, String titlecertTwo) {
        doctorMapper.deleteDocCard(doctorId);
        doctorMapper.deleteDocCertList(doctorId);
        doctorMapper.deleteCertDocPracList(doctorId); //
        doctorMapper.deleteDocTitleCertList(doctorId);//删除职称

        List<String> cardList = new ArrayList<>();
        cardList.add(idCardpositive);
        cardList.add(idCardside);
        //身份证文件
        for (Object value : cardList) {
            String cardId = String.valueOf(value);
            doctorMapper.addDocCardList("", cardId, doctorId, doctorId);
        }
        //资格证文件
        doctorMapper.addDocCertList("", certpositive, doctorId, doctorId);
        doctorMapper.addDocCertList("", certside, doctorId, doctorId);

        //保存执业证文件
        doctorMapper.addCertDocPracList("", certprac, doctorId, doctorId);

        //保存职称文件列表
        doctorMapper.addTitleCertList("", titlecert, doctorId, doctorId);
        if (StringUtils.isNotBlank(titlecertTwo)) {
            doctorMapper.addTitleCertList("", titlecertTwo, doctorId, doctorId);
        }
        return doctorMapper.updateDoctorInfoNew(doctorId, "");

    }

    public boolean updateSign(long doctorid, String sign) {
        return doctorMapper.updateDoctorInfoNewSign(doctorid, sign, "");
    }

    public boolean updateDoctorInfo(long doctorId, String docName, String docPhotoUrl, String idCard, int gender,
                                    String workInstName, int departmentId, int titleId,
                                    String pracNo, String digitalSignUrl, String professional, String introduction, int agreeTerms,
                                    List<?> cardList, List<?> certList,
                                    List<?> certPracList, List<?> titleCertList, List<?> multiSitedLicRecordList) {
        doctorMapper.deleteDocCard(doctorId);
        doctorMapper.deleteDocCertList(doctorId);
        doctorMapper.deleteCertDocPracList(doctorId); //
        doctorMapper.deleteDocTitleCertList(doctorId);//删除职称
        doctorMapper.deleteMultiSitedLicRecord(doctorId);//删除多点执业


        //身份证文件
        if (cardList.size() > 0) {
            for (Object value : cardList) {
                String cardId = String.valueOf(value);
                doctorMapper.addDocCardList("", cardId, doctorId, doctorId);
            }
        }
        //资格证文件
        if (certList.size() > 0) {
            for (Object value : certList) {
                String docCert = String.valueOf(value);
                doctorMapper.addDocCertList("", docCert, doctorId, doctorId);
            }
        }
        //保存执业证文件
        if (certPracList.size() > 0) {
            for (Object value : certPracList) {
                String certDocPrac = String.valueOf(value);
                doctorMapper.addCertDocPracList("", certDocPrac, doctorId, doctorId);
            }
        }
        //保存职称文件列表
        if (titleCertList.size() > 0) {
            for (Object value : titleCertList) {
                String titleCert = String.valueOf(value);
                doctorMapper.addTitleCertList("", titleCert, doctorId, doctorId);
            }
        }
        //保存多点执业文件列表
        if (multiSitedLicRecordList.size() > 0) {
            for (Object value : multiSitedLicRecordList) {
                String docMultiSitedLicRecord = String.valueOf(value);
                doctorMapper.addMultiSitedLicRecord("", docMultiSitedLicRecord, doctorId);
            }
        }


        return doctorMapper.updateDoctorInfo(doctorId, docName, "", docPhotoUrl, idCard, gender, workInstName, departmentId, titleId,
                pracNo, "", digitalSignUrl, professional, introduction, agreeTerms);

    }

    /**
     * 查询科室
     *
     * @return
     */
    public List<Map<String, Object>> getDepartment(String value) {
        return doctorMapper.getDepartment(value);
    }

    /**
     * 查询科室
     *
     * @return
     */
    public List<Map<String, Object>> getLastDepartment(String value) {
        return doctorMapper.getLastDepartment(value);
    }

    /*
     *功能描述 查询梯形科室
     * @author qian.wang
     * @date 2018/11/13
     * @param  * @param
     * @return java.lang.Object
     */

    public Object getDeparent(String name) {
        if (null == name) {
            List<Map<String, Object>> list = doctorMapper.getDepartmentOne();
            for (Map<String, Object> map : list) {
                long value = ModelUtil.getLong(map, "value");
                List<Map<String, Object>> departmentTwo = doctorMapper.getDepartmentTwo(value);
                if (departmentTwo.size() > 0) {
                    map.put("children", departmentTwo);
                }
            }
            return list;
        } else {
            return doctorMapper.getAdminDepartmen(name);
        }
    }

    public List<Map<String, Object>> getAllDepartmentListNew(String name) {
        if (StrUtil.isEmpty(name)) {
            List<Map<String, Object>> allDepartmentList = codeService.getAllDepartmentLists();
            Map<Long, List<Map<String, Object>>> tempMap = new HashMap<>();
            for (Map<String, Object> temp : allDepartmentList) {
                Long pid = ModelUtil.getLong(temp, "pid", 0);
                departmentTree(temp, pid, tempMap);
            }
            for (Map<String, Object> temp : allDepartmentList) {
                Long pid = ModelUtil.getLong(temp, "id", 0);
                List<Map<String, Object>> list = tempMap.get(pid);
                if (ModelUtil.getLong(temp, "pid") == 0) {
                    Map<String, Object> map = new HashMap<>();
                    if (list == null) {
                        list = new ArrayList<>();
                        map.put("id", temp.get("id"));
                        map.put("value", temp.get("value"));
                    } else if (list.size() == 0) {
                        map.put("id", temp.get("id"));
                        map.put("value", temp.get("value"));
                    } else {
                        map.put("id", temp.get("id"));
                        map.put("value", temp.get("value"));
                    }
                    list.add(map);
                }
                temp.put("child", list);
            }
            return tempMap.get(0L);
        } else {
            return doctorMapper.getAppDepartmen(name);
        }
    }

    private void departmentTree(Map<String, Object> temp, long pid, Map<Long, List<Map<String, Object>>> tempMap) {
        if (tempMap.containsKey(pid)) {
            tempMap.get(pid).add(temp);
        } else {
            List<Map<String, Object>> list = new ArrayList<>();
            list.add(temp);
            tempMap.put(pid, list);
        }
    }

    public Object getDeparents(long id) {
        if (id == 0) {
            List<Map<String, Object>> list = doctorMapper.getDepartmentOne();
            for (Map<String, Object> map : list) {
                long value = ModelUtil.getLong(map, "value");
                List<Map<String, Object>> departmentTwo = doctorMapper.getDepartmentTwo(value);
                if (departmentTwo.size() > 0) {
                    map.put("children", departmentTwo);
                    for (Map<String, Object> mapTwo : departmentTwo) {
                        long valueTwo = ModelUtil.getLong(mapTwo, "value");
                        List<Map<String, Object>> departmentThree = doctorMapper.getDepartmentTwo(valueTwo);
                        if (departmentThree.size() > 0) {
                            mapTwo.put("children", departmentThree);
                        }
                    }
                }
            }
            return list;
        } else {
            return doctorMapper.getDepartmens(id);
        }
    }

    public List<Map<String, Object>> getLastDepartments(String value) {
        return doctorMapper.getLastDepartments(value);
    }


    /**
     * 查询职称
     *
     * @return
     */
    public List<Map<String, Object>> getDoctorTitle(String value) {
        return doctorMapper.getDoctorTitle(value);
    }

    public List<Map<String, Object>> getDoctorList(String docName, String dooTel, String workInstName, String titleId, String departmentId, long doctorStart, long doctorEnd, int examine, int graphicStatus, int pageIndex, int pageSize) {
        return doctorMapper.getDoctorList(docName, dooTel, workInstName, titleId, departmentId, doctorStart, doctorEnd, examine, graphicStatus, pageIndex, pageSize);
    }

    public long getdoctorTotal(String docName, String dooTel, String workInstName, String titleId, String departmentId, long doctorStart, long doctorEnd, int examine, int graphicStatus) {
        return doctorMapper.getDoctorTotal(docName, dooTel, workInstName, titleId, departmentId, doctorStart, doctorEnd, examine, graphicStatus);
    }

    /**
     * 后台加载医生详情
     *
     * @return
     */
    public Map<String, Object> getDoctorById(long doctorId) {
        Map<String, Object> result = doctorMapper.getDoctorNotBlobById(doctorId);

        Map<String, Object> departmentfumap = doctorMapper.getCodeDepartmentfu(ModelUtil.getLong(result, "departmentid"));
        String departmentnamefu = ModelUtil.getStr(departmentfumap, "value");
        if (!StrUtil.isEmpty(departmentnamefu)) {
            String departmentname = String.format("%S/%S", departmentnamefu, ModelUtil.getStr(result, "departmentname"));
            result.put("departmentname", departmentname);
        }
        result.put("invitationcode", doctorMapper.getSalesId(doctorId)); //邀请人
        result.put("docdepartment", doctorMapper.getDocDepartment(ModelUtil.getInt(result, "departmentid"))); //科室
        result.put("departTree", doctorMapper.getDocDepartmentTree(ModelUtil.getInt(result, "departmentid")));
        result.put("titleid", doctorMapper.getCodeDoctorTitle(ModelUtil.getInt(result, "titleid")));   //职称名称
        result.put("cardlist", doctorMapper.getNoBlobDocIdCardList(doctorId));
        result.put("certpraclist", doctorMapper.getNoBlobDocCertDocPracList(doctorId));
        result.put("certlist", doctorMapper.getNoBlobDocCertList(doctorId));
        result.put("titlecertlist", doctorMapper.getNoBlobDocTitleCertList(doctorId));
        result.put("multisitedlicrecordlist", doctorMapper.getNoBlobMultiSitedLicRecordList(doctorId));
        result.put("hospitalid", doctorMapper.findHospitals(ModelUtil.getInt(result, "hospitalid"))); //医院名称
        return result;
    }

    /**
     * 后台加载专家医生认证详情
     *
     * @return
     */
    public Map<String, Object> getDoctorexpertId(long doctorId) {
        Map<String, Object> result = doctorMapper.getDoctorexpertId(doctorId);
//        result.put("docdepartment", doctorMapper.getDocDepartment(ModelUtil.getInt(result, "departmentid"))); //科室
        result.put("departTree", doctorMapper.getDocDepartmentTree(ModelUtil.getInt(result, "departmentid")));
//        result.put("titleid", doctorMapper.getCodeDoctorTitle(ModelUtil.getInt(result, "titleid")));   //职称名称
        result.put("cardlist", doctorMapper.getNoBlobDocIdCardList(doctorId));
        result.put("certpraclist", doctorMapper.getNoBlobDocCertDocPracList(doctorId));
        result.put("certlist", doctorMapper.getNoBlobDocCertList(doctorId));
        result.put("titlecertlist", doctorMapper.getNoBlobDocTitleCertList(doctorId));
        result.put("multisitedlicrecordlist", doctorMapper.getNoBlobMultiSitedLicRecordList(doctorId));
//        result.put("hospitalid", doctorMapper.findHospitals(ModelUtil.getInt(result, "hospitalid"))); //医院名称
        return result;
    }


    /**
     * @param docName   医生姓名
     * @param userName  用户姓名
     * @param dooTel    医生手机号
     * @param states    订单状态
     * @param paystatus 支付状态
     * @param orderNo   订单号
     * @param pageIndex 分页
     * @param pageSize  分页
     * @return
     */
    public List<Map<String, Object>> getDoctorProblemOrder(String docName, String userName, String dooTel, int states, int paystatus, String orderNo, int pageIndex, int pageSize) {
        return doctorMapper.getDoctorProblemOrder(docName, userName, dooTel, states, paystatus, orderNo, pageIndex, pageSize);
    }

    /**
     * 返回数量
     *
     * @param docName   医生姓名
     * @param userName  用户姓名
     * @param dooTel    医生手机号
     * @param states    订单状态
     * @param paystatus 支付状态
     * @param orderNo   订单号
     * @return
     */
    public long getDoctorProblemOrderTotal(String docName, String userName, String dooTel, int states, int paystatus, String orderNo) {
        return doctorMapper.getDoctorProblemOrderTotal(docName, userName, dooTel, states, paystatus, orderNo);
    }

    /**
     * 获取医生code
     *
     * @return
     */
    public Map<String, Object> getDoctorCode() {
        return doctorMapper.getDoctorCode();
    }

    /**
     * 后台完善医生信息
     *
     * @param docName        医生姓名
     * @param docPhotoUrl    医生头像
     * @param docType        医生类型
     * @param titleId        职称中文名
     * @param idCard         医生身份证号
     * @param pracNo         医师执业号
     * @param pracRecDate    执业证取得时间（YYYY-MM-DD）
     * @param certNo         医师资格证号
     * @param certRecDate    资格证取得时间
     * @param titleNo        医师职称号
     * @param titleRecDate   职称证取得时间
     * @param pracType       医师执业类别
     *                       //     * @param qualifyOrNot     考核是否合格 是 | 否
     * @param professional   医师擅长专业
     * @param signTime       签约时间
     * @param signLife       签约年限
     * @param employFileUrl  聘任合同
     *                       //     * @param creditLevel      信用评级
     *                       //     * @param occuLevel        职业评级
     * @param digitalSignUrl 数字签名留样
     *                       //     * @param docPenaltyPoints 医师评分
     *                       //     * @param ycRecordFlag     银川是否备案
     *                       //     * @param hosConfirmFlag   医院是否备案
     *                       //     * @param ycPresRecordFlag 是否有开处方的权限
     * @param doctorId
     * @return
     */
    public void addUpdateDoctorInfo(int examine, String docName, String docPhotoUrl, int docType, int titleId, long hospitalid, String dooTel, String idCard, String pracNo, long pracRecDate, String certNo, long certRecDate, String titleNo,
                                    long titleRecDate, String pracType, String professional,
                                    long signTime, String signLife, String employFileUrl,
                                    String digitalSignUrl,
                                    String pracScope, String pracScopeApproval,
                                    long docMultiSitedDateStart, long docMultiSitedDateEnd,
                                    long docMultiSitedDatePromise, long agentid,
                                    String introduction, int gender, int departmentId, long doctorId, String inDocCode, long tempDoctorId,
                                    List<?> ardList, List<?> certList, List<?> certPracList, List<?> titleCertList, List<?> multiSitedLicRecordList) {

        String workInstName = ModelUtil.getStr(findDoctor(hospitalid), "hospital_name");
        String workInstCode = ModelUtil.getStr(findDoctor(hospitalid), "hospital_code");
        String docPhoto = BASE64.base64Encoding(PicUtils.compressPicForScale(BASE64.readFile(Paths.get(ConfigModel.BASEFILEPATH + FileUtil.getFileName(docPhotoUrl))), 200));
        String employFile = BASE64.base64Encoding(PicUtils.compressPicForScale(BASE64.readFile(Paths.get(ConfigModel.BASEFILEPATH + FileUtil.getFileName(employFileUrl))), 200));
        String digitalSign = BASE64.base64Encoding(PicUtils.compressPicForScale(BASE64.readFile(Paths.get(ConfigModel.BASEFILEPATH + FileUtil.getFileName(digitalSignUrl))), 200));
        String qualifyOrNot = "1";
        String creditLevel = "优秀";
        String occuLevel = "1级";
        String docPenaltyPoints = "100";
        String hosOpinion = "同意";
        long hosOpinionDate = docMultiSitedDateStart;
        int agreeTerms = 1;
        if (doctorId > 0) {
            long doctorCount = doctorMapper.getDoctorCount(doctorId, dooTel);
            if (doctorCount > 0) {
                throw new ServiceException("手机号码已被使用");
            }
            doctorMapper.deleteDocCard(doctorId);//删除身份证
            doctorMapper.deleteDocCertList(doctorId);//删除资格证文件
            doctorMapper.deleteCertDocPracList(doctorId);//删除执业证文件
            doctorMapper.deleteDocTitleCertList(doctorId);//删除职称
            doctorMapper.deleteMultiSitedLicRecord(doctorId);//删除多点执业
            if ((docType == DoctorTypeEnum.trialDoctor.getCode() || docType == DoctorTypeEnum.ReviewDoctor.getCode()) && examine == DoctorExamineEnum.authenticationFailed.getCode()) {
                examine = DoctorExamineEnum.Certification.getCode();
            }
            doctorMapper.updateDoctorInfos(pracNo, examine, docPhoto, docType, hospitalid,
                    pracRecDate, certNo, certRecDate, titleNo,
                    titleRecDate, pracType,
                    signTime, signLife, employFile, employFileUrl,
                    digitalSign, digitalSignUrl, pracScope, pracScopeApproval,
                    docMultiSitedDateStart, docMultiSitedDateEnd,
                    docMultiSitedDatePromise, agentid, doctorId);
            Map<String, Object> lecturer = lecturerService.getLecturerByDoctor(doctorId);
            if (lecturer != null) {
                Map<String, Object> department = codeService.getDepartment(departmentId);
                Map<String, Object> title = codeService.getTitle(titleId);
                lecturerService.updateLecturer(docName, docPhotoUrl, dooTel, ModelUtil.getStr(title, "name"), workInstName, ModelUtil.getStr(department, "name"), professional, introduction, agentid, doctorId);
            }
        } else {
            long doctorCount = doctorMapper.getDoctorCount(dooTel);
            if (doctorCount > 0) {
                throw new ServiceException("手机号码已被使用");
            }
            doctorId = doctorMapper.addDoctorInfo(docName, docPhoto, docPhotoUrl, docType, titleId, workInstCode,
                    workInstName, hospitalid, dooTel, idCard, pracNo, pracRecDate, certNo, certRecDate, titleNo,
                    titleRecDate, pracType, qualifyOrNot, professional,
                    signTime, signLife, employFile, employFileUrl, creditLevel, occuLevel,
                    digitalSign, digitalSignUrl, docPenaltyPoints, pracScope, pracScopeApproval, agreeTerms,
                    docMultiSitedDateStart, docMultiSitedDateEnd, hosOpinion,
                    hosOpinionDate, docMultiSitedDatePromise, agentid,
                    introduction, gender, departmentId, inDocCode, tempDoctorId);
            doctorMapper.addDoctorExpand(doctorId);
        }
        //身份证文件
        if (ardList.size() > 0) {
            for (Object value : ardList) {
                String cardId = String.valueOf(value);
                //String blobCardId = BASE64.imageToBase64Str( ConfigModel.BASEFILEPATH + FileUtil.getFileName(FileUtil.FILE_DOCTOR_PATH, cardId));
                doctorMapper.addDocCardList("", cardId, doctorId, doctorId);
            }
        }
        //资格证文件
        if (certList.size() > 0) {
            for (Object value : certList) {
                String docCert = String.valueOf(value);
                //String blobDocCert = BASE64.imageToBase64Str( ConfigModel.BASEFILEPATH + FileUtil.getFileName(FileUtil.FILE_DOCTOR_PATH,docCert));
                doctorMapper.addDocCertList("", docCert, doctorId, agentid);
            }

        }
        //保存执业证文件
        if (certPracList.size() > 0) {
            for (Object value : certPracList) {
                String certDocPrac = String.valueOf(value);
                //String blobCertDocPrac = BASE64.imageToBase64Str( ConfigModel.BASEFILEPATH + FileUtil.getFileName(FileUtil.FILE_DOCTOR_PATH, ) + certDocPrac);
                doctorMapper.addCertDocPracList("", certDocPrac, doctorId, agentid);
            }
        }
        //保存职称文件列表
        if (titleCertList.size() > 0) {
            for (Object value : titleCertList) {
                String titleCert = String.valueOf(value);
                //String blobTitleCert = BASE64.imageToBase64Str( ConfigModel.BASEFILEPATH + FileUtil.getFileName(FileUtil.FILE_DOCTOR_PATH, ) + titleCert);
                doctorMapper.addTitleCertList("", titleCert, doctorId, doctorId);
            }
        }
        //保存多点执业文件列表
        if (multiSitedLicRecordList.size() > 0) {
            for (Object value : multiSitedLicRecordList) {
                String docMultiSitedLicRecord = String.valueOf(value);
                doctorMapper.addMultiSitedLicRecord("", docMultiSitedLicRecord, doctorId);
            }
        }
    }

    public Map<String, Object> findDoctor(long id) {
        return doctorMapper.findHospital(id);
    }

    /**
     * 医生登录和注册
     *
     * @param phone
     * @param code
     * @return
     */
    public boolean doctotLoginRegisterNew(String phone, String code, int codeType, int agreePlatform) {
        boolean flag = false;
        if (TextFixed.def_code.equals(code)) {
            flag = true;
        }

        if (!flag) {
            Object object = this.getRedisCode(phone, codeType);
            if (object instanceof Map) {
                Map<String, Object> value = (Map<String, Object>) object;
                if (code.equals(ModelUtil.getStr(value, "code"))) {
                    deleteRedisCode(phone, codeType);
                    flag = true;
                } else if ((System.currentTimeMillis() - ModelUtil.getLong(value, "timestamp")) / (1000 * 60) > 15) {
                    throw new ServiceException("验证码过期!");
                } else {
                    throw new ServiceException("验证码错误!");
                }
            } else {
                throw new ServiceException("请先获取验证码");
            }
        }
        if (codeType == 2) {
            Map<String, Object> doctorLogin = getDoctorLogin(phone);
            if (doctorLogin == null) {
                long doctorId = doctorMapper.addDoctorRegisterNew(phone, agreePlatform);
                doctorMapper.addDoctorExpand(doctorId);
            } else {
                long examine = ModelUtil.getLong(doctorLogin, "examine");
                if (examine == 0 || examine == 1) {
                } else {
                    throw new ServiceException("用户已存在");
                }
            }
        }
        return flag;
    }

    //注册第二步
    public boolean doctotLoginRegisterNewTwo(String docPhotoUrl, String pra_no, String idcard, long hospitalid, long doctorid, int gender, String name, int departmentid, int titleId) {
        Map<String, Object> hospitalById = hospitalService.findHospitalById(hospitalid);
        String hoscode = ModelUtil.getStr(hospitalById, "code");
        String hosname = ModelUtil.getStr(hospitalById, "name");
        boolean flag = doctorMapper.addDoctorRegisterNewUpdate(docPhotoUrl, pra_no, hoscode, hosname, idcard, hospitalid, doctorid, gender, name, departmentid, titleId);
        return flag;
    }

    //注册第二步--邀请码
    public boolean doctotLoginRegisterNewTwoCode(String docPhotoUrl, String code, String idcard, long hospitalid, long doctorid, int gender, String name, int departmentid, int titleId) {
        Map<String, Object> hospitalById = hospitalService.findHospitalById(hospitalid);
        String hoscode = ModelUtil.getStr(hospitalById, "code");
        String hosname = ModelUtil.getStr(hospitalById, "name");
        boolean flag = doctorMapper.addDoctorRegisterNewUpdates(docPhotoUrl, hoscode, hosname, idcard, hospitalid, doctorid, gender, name, departmentid, titleId);
        Map<String, Object> salesPerson = doctorMapper.findSalesPerson(code);
        if (salesPerson != null) {
            long saleid = ModelUtil.getLong(salesPerson, "id");
            doctorMapper.insertSalesPerson(saleid, code, doctorid);
        }
        return flag;
    }

    //注册第三步
    public boolean doctotLoginRegisterNewThree(long doctorid, String professional, String introduction) {
        boolean flag = doctorMapper.addDoctorRegisterNewUpdateThree(doctorid, professional, introduction);
        return flag;
    }

    public boolean doctotLoginRegister(String phone, String code, int codeType, int agreePlatform) {
        boolean flag = false;
        if (TextFixed.def_code.equals(code)) {
            flag = true;
        }

        if (!flag) {
            Object object = this.getRedisCode(phone, codeType);
            if (object instanceof Map) {
                Map<String, Object> value = (Map<String, Object>) object;
                if (code.equals(ModelUtil.getStr(value, "code"))) {
                    deleteRedisCode(phone, codeType);
                    flag = true;
                } else if ((System.currentTimeMillis() - ModelUtil.getLong(value, "timestamp")) / (1000 * 60) > 15) {
                    throw new ServiceException("验证码过期!");
                } else {
                    throw new ServiceException("验证码错误!");
                }
            } else {
                throw new ServiceException("请先获取验证码");
            }
        }
        if (codeType == 2) {
            Map<String, Object> doctorLogin = getDoctorLogin(phone);
            if (doctorLogin == null) {
                long doctorId = doctorMapper.addDoctorRegister(phone, agreePlatform);
                doctorMapper.addDoctorExpand(doctorId);
            } else {
                throw new ServiceException("用户已存在");
            }
        }
        return flag;
    }

    /**
     * 查询登录成功后的医生信息
     *
     * @param phone
     * @return
     */
    public Map<String, Object> getDoctorLogin(String phone) {
        return doctorMapper.getDoctorLogin(phone);
    }

    /*
     *注册查询医生信息
     * @author qian.wang
     * @date 2018/11/22
     * @param  * @param phone
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    public Map<String, Object> getDoctorLoginInformation(long doctorid) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> info = doctorMapper.getDoctorLogins(doctorid);
        if (null != info) {
            info.put("code", ModelUtil.getStr(doctorMapper.getCode(doctorid), "code"));
        }
        map.put("info", info);
        map.put("title", doctorMapper.getDoctorTitles(doctorid));
        map.put("hospital", doctorMapper.getDoctorHospital(doctorid));
        map.put("departtype", doctorMapper.getDoctorType(doctorid));
        List<Map<String, Object>> list = doctorMapper.getSigns(doctorid);
        String idcardpositive = null;
        String idcardside = null;
        String certpositive = null;
        String certside = null;
        String titlecert = null;
        String titlecertTwo = null;
        if (list.size() == 1) {
            idcardpositive = ModelUtil.getStr(list.get(0), "url");
        } else if (list.size() > 1) {
            idcardpositive = ModelUtil.getStr(list.get(0), "url");
            idcardside = ModelUtil.getStr(list.get(1), "url");
        }
        List<Map<String, Object>> lists = doctorMapper.getSignt(doctorid);
        if (lists.size() == 1) {
            certpositive = ModelUtil.getStr(lists.get(0), "url");
        } else if (lists.size() > 1) {
            certpositive = ModelUtil.getStr(lists.get(0), "url");
            certside = ModelUtil.getStr(lists.get(1), "url");
        }
        List<Map<String, Object>> listss = doctorMapper.getSignw(doctorid);
        if (listss.size() == 1) {
            titlecert = ModelUtil.getStr(listss.get(0), "url");
        } else if (listss.size() > 1) {
            titlecert = ModelUtil.getStr(listss.get(0), "url");
            titlecertTwo = ModelUtil.getStr(listss.get(1), "url");
        }
        Map<String, Object> ms = new HashMap();
        ms.put("idcardpositive", idcardpositive);//身份证
        ms.put("idcardside", idcardside);
        ms.put("certpositive", certpositive);//资格证
        ms.put("certside", certside);
        ms.put("titlecert", titlecert);//职称
        ms.put("titlecertTwo", titlecertTwo);
        ms.put("certprac", ModelUtil.getStr(doctorMapper.getSigno(doctorid), "url"));//执业证文件
        map.put("certificate", ms);
        return map;
    }

    /**
     * redis 获取验证码
     *
     * @param phone
     * @return
     */
    private Object getRedisCode(String phone, int codeType) {
        Object redisCode = this.redisTemplate.opsForValue().get(codeType + "-" + phone);
        return redisCode;
    }

    /**
     * 清除验证码
     *
     * @param phone
     * @param codeType
     */
    public void deleteRedisCode(String phone, int codeType) {
        redisTemplate.delete(String.format("%s-%s", codeType, phone));
    }

    /**
     * 发送验证码
     *
     * @param phone    手机号
     * @param codetype 1 登录  2 注册
     */
    public boolean sendCode(String phone, int codetype) throws ClientException {
        String code = UnixUtil.getCode();
        long register = doctorMapper.getDoctorIsRegister(phone);
        if (register == 0 && codetype == 1) {
            throw new ServiceException("请先注册!");
        } else if (register > 0 && codetype == 2) {
            throw new ServiceException("已注册,请直接登录!");
        } else {
            //有效时间
            Map<String, Object> value = new HashMap<>();
            value.put("code", code);
            value.put("timestamp", UnixUtil.getNowTimeStamp());
            //将验证码存入redis
            this.redisTemplate.opsForValue().set(codetype + "-" + phone, value);
            this.redisTemplate.expire(codetype + "-" + phone, com.syhdoctor.common.config.ConfigModel.SMS.timeout, TimeUnit.SECONDS);
            Map<String, Object> map = new HashMap<>();
            map.put("code", code);
            SendShortMsgUtil.sendSms(ConfigModel.ISONLINE, phone, com.syhdoctor.common.config.ConfigModel.SMS.Login_sms_template, map);//发送短信
        }
        return true;
    }


    /**
     * 医生详细
     *
     * @param doctorid
     * @return
     */
    public Map<String, Object> getSimpleDoctor(long doctorid) {
        Map<String, Object> doctor = doctorMapper.getSimpleDoctor(doctorid);
        if (doctor != null) {
            doctor.put("whetheropen", doctorMapper.isOpen(doctorid));
            doctor.put("headpic", ModelUtil.getStr(doctor, "headpic"));
            doctor.put("messagecount", doctorMapper.getDoctorMessageCount(doctorid));
        }
        return doctor;
    }

    /**
     * 修改医生擅长和简介
     *
     * @param doctorid     医生id
     * @param professional 擅长
     * @param introduction 简介
     * @return
     */
    public boolean updateDoctorProfessionalAndIntroduction(long doctorid, String professional, String introduction) {
        return doctorMapper.updateDoctorProfessionalAndIntroduction(doctorid, professional, introduction);
    }

    /**
     * 医生常用药品列表
     *
     * @param doctorid  医生id
     * @param pageIndex 分页
     * @param pageSize  分页
     * @return
     */
    public List<Map<String, Object>> getDoctorDrugsList(long doctorid, int pageIndex, int pageSize) {
        return doctorMapper.getDoctorDrugsList(doctorid, pageIndex, pageSize);
    }

    /**
     * 药品包列表
     *
     * @return
     */
    public List<Map<String, Object>> getDrugsPackageList() {
        return doctorMapper.getDrugsPackageList();
    }

    /**
     * 药品字典列表判断医生是否已经选择
     *
     * @param doctorId  医生id
     * @param packageid 药品分类id
     * @param pageIndex 分页
     * @param pageSize  分页
     * @return
     */
    public List<Map<String, Object>> selectDrugsList(long doctorId, long packageid, int pageIndex, int pageSize) {
        return doctorMapper.selectDrugsList(doctorId, packageid, pageIndex, pageSize);
    }

    /**
     * 搜索药品
     *
     * @param name      药品名或者首字母
     * @param pageIndex 分页
     * @param pageSize  分页
     * @return
     */
    public List<Map<String, Object>> searchDrugsList(long doctorId, String name, int pageIndex, int pageSize) {
        return doctorMapper.searchDrugsList(doctorId, name, pageIndex, pageSize);
    }

    /**
     * 添加常用药品
     *
     * @param doctorid 医生id
     * @param drugsid  药品id
     * @return
     */
    public boolean addOftenDrugs(long doctorid, long drugsid) {
        long oftenDrugsCount = doctorMapper.getOftenDrugsCount(doctorid, drugsid);
        if (oftenDrugsCount == 0) {
            return doctorMapper.addOftenDrugs(doctorid, drugsid);
        } else {
            return false;
        }
    }

    /**
     * 删除常用药品
     *
     * @param doctorid 医生id
     * @param drugsid  药品id
     * @return
     */
    public boolean delOftenDrugs(long doctorid, long drugsid) {
        return doctorMapper.delOftenDrugs(doctorid, drugsid);
    }

    /**
     * 根据pid查找地区
     *
     * @param code
     * @return
     */
    public List<Map<String, Object>> getAreaByParentId(int code) {
        return codeService.getAreaByParentId(code);
    }

    /**
     * 添加银行卡
     *
     * @param doctorid 医生id
     * @param name     开户行
     * @param number   卡号
     * @return
     */
    public boolean addBankCard(long doctorid, String address, String name, String bankName, String number, String issInsId) {
        long count = doctorMapper.getBankCardCount(doctorid);
        if (count > 0) {
            throw new ServiceException("不能添加多张银行卡");
        }
        return doctorMapper.addBankCard(doctorid, address, name, bankName, number, issInsId);
    }

    /**
     * @param cardNo 卡号
     * @return
     */
    public Map<String, Object> getCardInfo(String cardNo) {
        long ts = UnixUtil.getNowTimeStamp();
        String json = "{\"cardNo\":\"" + cardNo + "\"}";
        String sign = UnionpayUtil.sign(json, String.valueOf(ts), ConfigModel.UNIONPAYSIGNATURE);
        String url = String.format(JumpLink.UNIONPAY_CARDINFO, getToken(), sign, String.valueOf(ts));
        Map<String, Object> post = HttpUtil.getInstance().post(url, json);
        return post;
    }

    private String getToken() {
        Object codeObject = redisTemplate.opsForValue().get(ConfigModel.UNIONPAYAPPID);
        if (codeObject != null) {
            return codeObject.toString();
        } else {
            Map<String, Object> takenMap = HttpUtil.getInstance().get(String.format(JumpLink.UNIONPAY_TOKEN, ConfigModel.UNIONPAYAPPID, ConfigModel.UNIONPAYSECRET));
            //有效时间
            long timeout = 7200;
            String token = ModelUtil.getStr(takenMap, "token");
            this.redisTemplate.opsForValue().set(ConfigModel.UNIONPAYAPPID, ModelUtil.getStr(takenMap, "token"));
            this.redisTemplate.expire(ConfigModel.UNIONPAYAPPID, timeout, TimeUnit.SECONDS);
            return token;
        }
    }

    /**
     * 获取银行卡列表
     *
     * @param doctorid 医生ID
     * @return
     */
    public List<Map<String, Object>> getBankCardList(long doctorid) {
        return doctorMapper.getBankCardList(doctorid);
    }

    /**
     * 获取银行卡详情
     *
     * @param id id
     * @return
     */
    public Map<String, Object> getBankCard(long id) {
        Map<String, Object> bankCard = doctorMapper.getBankCard(id);
        String areas = ModelUtil.getStr(bankCard, "address");
        systemService.getAres(bankCard, areas);
        return bankCard;
    }

    /**
     * 解除银行卡绑定
     *
     * @param id id
     * @return
     */
    public boolean delBankCard(long id) {
        return doctorMapper.delBankCard(id);
    }

    /**
     * 签到
     *
     * @param doctorid 医生id
     */
    public int doctorSignIn(long doctorid) {
        int integral = 0;
        //是否签到
        long count = doctorMapper.signFlag(doctorid);
        if (count == 0) {
            //签到
            doctorMapper.signIn(doctorid);
            //添加积分
            integral = addDoctorIntegral(doctorid, IntegralTypeEnum.SignIn.getCode());

            /*systemService.addMessage("", TextFixed.signTitle,
                    MessageTypeEnum.doctor.getCode(), "",
                    TypeNameAppPushEnum.doctorUserSignIn.getCode(), doctorid,
                    String.format(TextFixed.doctorsignText, 1),
                    "");//app 医生 内推送*/
        }
        return integral;
    }

    //添加积分
    public int addDoctorIntegral(long doctorId, int type) {
        if (type != 1) {
            long count = doctorMapper.getIntegralDetailed(doctorId, type);
            if (count > 0) {
                //不能重复添加积分
                return 0;
            }
        }
        int integral = TextFixed.doctor_sign_integral;
        //添加积分
        doctorMapper.updateIntegral(doctorId, integral);
        //添加积分明细
        doctorMapper.addIntegralDetailed(doctorId, type, integral);
        return integral;
    }


    /**
     * 患者列表
     *
     * @param doctorId
     * @return
     */
    public List<Map<String, Object>> doctorUserList(long doctorId) {
        //数据源
        List<Map<String, Object>> maps = doctorMapper.doctorUserList(doctorId);
        //字母集合
        List<Map<String, Object>> letterList = new ArrayList<>();

        //用户集合
        List<Map<String, Object>> userList = new ArrayList<>();

        if (maps.size() > 0) {
            //用户对象
            String key = null;
            for (Map map : maps) {
                Map<String, Object> userMap = new HashMap<>();
                String initials = ModelUtil.getStr(map, "initials");
                if (!initials.equals(key)) {
                    key = initials;
                    //字母对象
                    Map<String, Object> letterMap = new HashMap<>();
                    userList = new ArrayList<>();
                    letterMap.put("key", key);
                    letterMap.put("userlist", userList);
                    userMap.put("userid", ModelUtil.getLong(map, "id"));
                    userMap.put("name", ModelUtil.getStr(map, "name"));
                    userMap.put("headpic", ModelUtil.getStr(map, "headpic"));
                    userList.add(userMap);
                    letterList.add(letterMap);
                } else {
                    userMap.put("userid", ModelUtil.getLong(map, "id"));
                    userMap.put("name", ModelUtil.getStr(map, "name"));
                    userMap.put("headpic", ModelUtil.getStr(map, "headpic"));
                    userList.add(userMap);
                }
            }
        }
        return letterList;
    }

    /**
     * 患者列表查询
     *
     * @param doctorid
     * @param name
     * @return
     */
    public List<Map<String, Object>> findDoctorUserList(long doctorid, String name) {
        return doctorMapper.fingDoctorUser(doctorid, name);
    }

    /**
     * 患者详细
     *
     * @param doctorid
     * @param userid
     * @return
     */
    public Map<String, Object> getUser(long doctorid, long userid) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> user = userService.getUser(userid);
        result.put("user", user);
        Map<String, Object> health = new HashMap<>();
        if (user != null) {
            health = userService.getHealth(ModelUtil.getLong(user, "id"));
        }
        result.put("health", health);
        result.put("prescriptionlist", prescriptionService.getDoctorUserPrescriptionList(doctorid, userid));
        result.put("orderlist", answerService.doctorUserOrderList(doctorid, userid));
        return result;
    }

    public List<Map<String, Object>> getPrescripList(long doctorid, long userid) {
        return prescriptionService.getDoctorUserPrescriptionList(doctorid, userid);
    }

    public Map<String, Object> getAppPrescription(long prescriptionid) {
        return prescriptionService.getAppPrescription(prescriptionid);
    }


    public Map<String, Object> doctorHomePage(long doctorid) {
        Map<String, Object> simpleDoctor = getSimpleDoctor(doctorid);
        long signCount = doctorMapper.signFlag(doctorid);
        if (simpleDoctor != null) {
            //banner图
            simpleDoctor.put("signflag", signCount > 0 ? 1 : 0);
            simpleDoctor.put("todayproblemcount", doctorMapper.getTodayProblemCount(doctorid));//今日问诊数量
            simpleDoctor.put("historyreplycount", doctorMapper.getHistoryProblemcount(doctorid));//历史问诊数量
            simpleDoctor.put("waitvisitcount", doctorMapper.getWaitVisitCount(doctorid));//等待接诊数量
            simpleDoctor.put("waitreplycount", doctorMapper.getWaitReplyCount(doctorid));//等待回复数量
            simpleDoctor.put("answerCount", doctorMapper.getAnswerCount(doctorid));//图文数量
            simpleDoctor.put("phoneCount", doctorMapper.getPhoneCount(doctorid));//电话数量
            simpleDoctor.put("videoCount", doctorMapper.getVideoCount(doctorid));//视频数量
            simpleDoctor.put("videoorder", doctorVideoMapper.getBeingVideoOrder(doctorid));//最近的一笔视频订单
            simpleDoctor.put("bannerlist", focusfigureService.bannerList(DisplaypositionEnum.DoctorTop.getCode(), 6));//广告图
            simpleDoctor.put("failpresion", doctorMapper.getPresionFail(doctorid));//失败的处方
            simpleDoctor.put("chatlist", getList(doctorid));//消息列表
        }
        return simpleDoctor;
    }

    //未读的消息列表
    public List<Map<String, Object>> getList(long doctorid) {
        List<Map<String, Object>> chat = doctorMapper.getChat(doctorid);
        chat.forEach(map -> {
            long orderid = ModelUtil.getLong(map, "id");
            Map<String, Object> answer = doctorMapper.getAnswer(doctorid, orderid);
            map.put("contenttype", ModelUtil.getInt(answer, "contenttype"));
            map.put("content", ModelUtil.getStr(answer, "content"));
            map.put("createtime", ModelUtil.getLong(answer, "createtime"));
        });
        return chat;
    }

    public Map<String, Object> getOutpatient(long doctorId) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> phoneMp = doctorMapper.getPhonePrice(doctorId, VisitCategoryEnum.graphic.getCode());
        if (phoneMp != null) {
            result.put("price", ModelUtil.getStr(phoneMp, "price"));
        } else {
            result.put("price", "");
        }
        result.put("phonetime", doctorMapper.getAnswerTime(doctorId));
        Map<String, Object> weeks = doctorMapper.getWeeks(doctorId);
        List<Integer> list = new ArrayList<>();
        if (weeks != null) {
            for (Object value : weeks.values()) {
                list.add((Boolean) value ? 1 : 0);
            }
        } else {
            for (int i = 0; i < 7; i++) {
                list.add(0);
            }
        }
        result.put("weeks", list);
        result.put("tips", TextFixed.doctorAnswercClinicTips);
        result.put("recommendprice", getRecommendPrice(OrderTypeEnum.Answer.getCode()));
        return result;
    }

    /**
     * 查询医生诊所状态
     *
     * @param doctorId
     * @return
     */
    public Map<String, Object> getDoctorClinic(long doctorId) {
        Map<String, Object> result = new HashMap<>();
        //Map<String, Object> phoneMp = doctorMapper.getOnDutyStatus(doctorId, VisitCategoryEnum.phone.getCode());
        Map<String, Object> departmentMp = doctorMapper.getDepartmentStatus(doctorId);
        if (departmentMp != null) {
            result.put("phonewhetheropen", ModelUtil.getInt(departmentMp, "whetheropendepartment"));
        } else {
            result.put("phonewhetheropen", 0);
        }
        Map<String, Object> graphicMp = doctorMapper.getOnDutyStatus(doctorId, VisitCategoryEnum.graphic.getCode());
        if (graphicMp != null) {
            result.put("graphicwhetheropen", ModelUtil.getInt(graphicMp, "whetheropen"));
        } else {
            result.put("graphicwhetheropen", 0);
        }

        Map<String, Object> videoMp = doctorMapper.getVideoStatus(doctorId);    //视频
        if (videoMp != null) {
            result.put("videowhetheropen", ModelUtil.getInt(videoMp, "whetheropenvideo"));
        } else {
            result.put("videowhetheropen", 0);
        }


        result.put("time", UnixUtil.getNowTimeStamp());
        return result;
    }


    /**
     * 添加or修改诊所
     *
     * @param
     * @param medclassid
     * @param price
     * @param doctorid
     * @param whetheropen
     * @return
     */
    public boolean updateAddDoctorClinic(int medclassid, BigDecimal price, long doctorid, int whetheropen, List<?> visiting_start_time, int isupdate) {
        if (whetheropen == 0) {//关闭服务
            boolean a = doctorMapper.getOtherScheduling(doctorid);
            if (a) {
                throw new ServiceException("您还有预约视频订单，不可关闭，请检查");
            } else {
                doctorMapper.delOtherScheduling(doctorid);
            }
        }
        String medclassname = VisitCategoryEnum.getName(medclassid);
        Map<String, Object> map = doctorMapper.findDoctorClinics(doctorid);
        if (map == null) {
            doctorMapper.insertDoctorClinic(medclassid, medclassname, price, doctorid, whetheropen);
        } else {
            doctorMapper.updateDoctorClinic(price, whetheropen, doctorid);
        }
        if (isupdate == 1) {//进去排班页面
            doctorMapper.delOtherScheduling(doctorid);
            Map<String, Object> setingTime = doctorVideoMapper.getSetingTime(OrderTypeEnum.Video.getCode());
            for (Object o : visiting_start_time) {
                long startTime = ModelUtil.strToLong(o.toString(), 0);
                if (startTime != 0) {
                    long visiting_end_time = startTime + ModelUtil.getLong(setingTime, "interval_time") * 60 * 1000;//结束时间为20分钟后
                    doctorMapper.insertScheduling(doctorid, startTime, visiting_end_time);
                }
            }
        }
        return true;
    }

    public Map<String, Object> findDetailClass(long doctorid) {
        Map<String, Object> map = doctorMapper.findDoctorClinic(doctorid);
        if (map != null) {
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> listmap = getHaveSelectTime(doctorid);
            Map<Long, List<Long>> mapal = new LinkedHashMap<>();//保证顺序
            for (Map<String, Object> map1 : listmap) {
                long startTime = ModelUtil.getLong(map1, "visiting_start_time");
                boolean a = false;
                for (Long l : mapal.keySet()) {
                    if (compareDate(startTime, l)) {//如果在同一天
                        a = true;
                        mapal.get(l).add(startTime);
                    }
                }
                if (!a) {
                    List<Long> longs = new ArrayList<>();
                    longs.add(startTime);
                    mapal.put(startTime, longs);
                }
            }
            for (long l : mapal.keySet()) {
                Map<String, Object> map1 = new HashMap<>();
                map1.put("currenttime", l);
                map1.put("timelist", mapal.get(l));
                list.add(map1);
            }
            map.put("time", list);
            map.put("tips", TextFixed.doctorVideoClinicTips);
        }
        map.put("recommendprice", getRecommendPrice(OrderTypeEnum.Video.getCode()));
        return map;
    }

    public Map<String, Object> getTimeList(List<?> visiting_start_time) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        List<Object> listmap = (List<Object>) visiting_start_time;
        Map<Long, List<Long>> mapal = new LinkedHashMap<>();//保证顺序
        for (Object startTime : listmap) {
            long t = Long.parseLong(startTime.toString());
            boolean a = false;
            for (Long l : mapal.keySet()) {
                if (compareDate(t, l)) {//如果在同一天
                    a = true;
                    mapal.get(l).add(t);
                }
            }
            if (!a) {
                List<Long> longs = new ArrayList<>();
                longs.add(t);
                mapal.put(t, longs);
            }
        }
        for (long l : mapal.keySet()) {
            Map<String, Object> map1 = new HashMap<>();
            map1.put("currenttime", l);
            map1.put("timelist", mapal.get(l));
            list.add(map1);
        }
        map.put("time", list);
        return map;
    }


    public boolean compareDate(long a, long b) {
        Date date0 = new Date(a);   //假设给的毫秒t0,t1，就从这句开始
        Date date1 = new Date(b);
        GregorianCalendar ca0 = new GregorianCalendar();  //如果给的Date对象date，就忽略上句
        GregorianCalendar ca1 = new GregorianCalendar();
        ca0.setTime(date0);
        ca1.setTime(date1);
        //获取ca0和ca1的年，月，日，对比是否相同
        if (ca0.get(GregorianCalendar.YEAR) == ca1.get(GregorianCalendar.YEAR) &&
                ca0.get(GregorianCalendar.MONTH) == ca1.get(GregorianCalendar.MONTH) &&
                ca0.get(GregorianCalendar.DAY_OF_MONTH) == ca1.get(GregorianCalendar.DAY_OF_MONTH)) {
            return true;
        } else {
            return false;
        }

    }

    public List<Map<String, Object>> getHaveSelectTime(long doctorid) {
        return doctorMapper.getHaveSelectTime(doctorid);
    }

    public List<Map<String, Object>> getHaveSelectTimePhone(long doctorid) {
        return doctorMapper.getHaveSelectTimePhone(doctorid);
    }


    public Map<String, Object> findDetailClassDuty(long doctorid) {
        Map<String, Object> map = doctorMapper.findDoctorClinicDuty(doctorid);
        if (map != null) {
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> listmap = getHaveSelectTimePhone(doctorid);
            Map<Long, List<Long>> mapal = new LinkedHashMap<>();
            for (Map<String, Object> map1 : listmap) {
                long startTime = ModelUtil.getLong(map1, "visiting_start_time");
                boolean a = false;
                for (Long l : mapal.keySet()) {
                    if (compareDate(startTime, l)) {//如果在同一天
                        a = true;
                        mapal.get(l).add(startTime);
                    }
                }
                if (!a) {
                    List<Long> longs = new ArrayList<>();
                    longs.add(startTime);
                    mapal.put(startTime, longs);
                }
            }
            for (long l : mapal.keySet()) {
                Map<String, Object> map1 = new HashMap<>();
                map1.put("currenttime", l);
                map1.put("timelist", mapal.get(l));
                list.add(map1);
            }
            map.put("time", list);
            map.put("tips", TextFixed.doctorPhoneClinicTips);
        }
        map.put("recommendprice", getRecommendPrice(OrderTypeEnum.Phone.getCode()));
        return map;
    }

    /**
     * 门诊医生值班新增
     *
     * @param doctorId 医生id
     * @param timeList 值班时间
     * @return
     */
    public void addDoctorInquiry(long doctorId, BigDecimal graphicPrice, int whetherOpen, List<?> timeList, String startTime, String endTime) {
        long count = doctorMapper.getDoctorPrice(VisitCategoryEnum.graphic.getCode(), doctorId);
        if (count > 0) {
            doctorMapper.updateDoctorPirce(VisitCategoryEnum.graphic.getCode(), graphicPrice, doctorId, whetherOpen);
        } else {
            doctorMapper.addDoctorPrice(VisitCategoryEnum.graphic.getCode(), VisitCategoryEnum.graphic.getMessage(), graphicPrice, doctorId, whetherOpen);
        }
        doctorMapper.deleteDoctorDutyRule(doctorId);
        doctorMapper.deleteDoctorInquiry(doctorId);
        Map<Integer, Map<String, Long>> mapMap = UnixUtil.weekDays(new Date(), startTime, endTime);
        int bound = timeList.size();
        for (int i = 0; i < bound; i++) {
            boolean value = ModelUtil.strToInt(timeList.get(i).toString(), 0) != 0;
            long todaystart = UnixUtil.dateTimeStamp(UnixUtil.getDate(UnixUtil.getNowTimeStamp(), "yyyy-MM-dd"), "yyyy-MM-dd");
            Map<String, Long> map = mapMap.get(i);
            long visitingStartTime = ModelUtil.getLong(map, "starttime");
            if (value && visitingStartTime >= todaystart) {//选择 并且大于等于今天
                long visitingEndTime = ModelUtil.getLong(map, "endtime");
                doctorMapper.addDoctorInquiry(doctorId, visitingStartTime, visitingEndTime);
            }
        }
        doctorMapper.addDoctorDutyRule(doctorId,
                ModelUtil.strToInt(timeList.get(1).toString(), 0) != 0,
                ModelUtil.strToInt(timeList.get(2).toString(), 0) != 0,
                ModelUtil.strToInt(timeList.get(3).toString(), 0) != 0,
                ModelUtil.strToInt(timeList.get(4).toString(), 0) != 0,
                ModelUtil.strToInt(timeList.get(5).toString(), 0) != 0,
                ModelUtil.strToInt(timeList.get(6).toString(), 0) != 0,
                ModelUtil.strToInt(timeList.get(0).toString(), 0) != 0, startTime, endTime);
    }

    public static void main(String[] args) {
        Map<Integer, Map<String, Long>> integerMapMap = UnixUtil.weekDays(new Date(), "00:00:00", "23:00:00");
        UnixUtil.dateTimeStamp(UnixUtil.getDate(UnixUtil.getNowTimeStamp(), "yyyy-MM-dd"), "yyyy-MM-dd");
    }


    /**
     * 查询医生排班信息
     *
     * @param doctorId
     * @return
     */
    public Map<String, Object> getOnduty(long doctorId) {
        Map<String, Object> result = new HashMap<>();
        result.put("ondutylist", doctorMapper.getOndutyList(doctorId));
        //Map<String, Object> mp = doctorMapper.getOnDutyStatus(doctorId, VisitCategoryEnum.phone.getCode());
        Map<String, Object> departmentMp = doctorMapper.getDepartmentStatus(doctorId);
        if (departmentMp != null) {
            result.put("phonewhetheropen", ModelUtil.getInt(departmentMp, "whetheropendepartment"));
        } else {
            result.put("phonewhetheropen", 0);
        }

        Map<String, Object> phonePrice = doctorMapper.getPhonePrice(doctorId, VisitCategoryEnum.phone.getCode());
        if (phonePrice != null) {
            result.put("price", ModelUtil.getStr(phonePrice, "price"));
        } else {
            result.put("price", 0);
        }

        return result;
    }


    /**
     * 医生排班 添加
     *
     * @param doctorId 医生ID
     * @param timeList 排班开始时间,
     * @return
     */
    public void addUpdaetOnduty(long doctorId, BigDecimal phonePrice, List<?> timeList, int whetherOpen) {
        long count = doctorMapper.getDoctorPrice(VisitCategoryEnum.phone.getCode(), doctorId);
        if (count > 0) {
            doctorMapper.updateDoctorPirce(VisitCategoryEnum.phone.getCode(), phonePrice, doctorId, whetherOpen);
        } else {
            doctorMapper.addDoctorPrice(VisitCategoryEnum.phone.getCode(), VisitCategoryEnum.phone.getMessage(), phonePrice, doctorId, whetherOpen);
        }
        //doctorMapper.updateWhetherOpen(doctorId, whetherOpen, VisitCategoryEnum.phone.getCode());
        doctorMapper.updateDepartmentWhetherOpen(doctorId, whetherOpen);
        for (Object obj : timeList) {
            Map<String, Object> value = (Map<String, Object>) obj;
            int id = ModelUtil.getInt(value, "id");
            int shift = ModelUtil.getInt(value, "shift");
            long visitingStartTime = ModelUtil.getLong(value, "visitingstarttime");
            long visitingEndTime = ModelUtil.getLong(value, "visitingendtime");
            if (visitingStartTime > visitingEndTime) {
                throw new ServiceException("开始时间不能大于结束时间");
            }
            if (id == 0) {
                doctorMapper.addOnduty(doctorId, visitingStartTime, visitingEndTime, shift);
                //doctorMapper.updateDoctorOnduty(id, doctorId, visitingStartTime, visitingEndTime, shift);
            }
        }
    }

    /**
     * 医生排班 添加
     *
     * @param doctorId 医生ID
     * @param timeList 排班开始时间,
     * @return
     */
    public void addUpdaetOndutyNew(long doctorId, BigDecimal phonePrice, List<?> timeList, int whetherOpen, int isupdate) {
        long count = doctorMapper.getDoctorPrice(VisitCategoryEnum.phone.getCode(), doctorId);
        if (count > 0) {
            doctorMapper.updateDoctorPirce(VisitCategoryEnum.phone.getCode(), phonePrice, doctorId, whetherOpen);
        } else {
            doctorMapper.addDoctorPrice(VisitCategoryEnum.phone.getCode(), VisitCategoryEnum.phone.getMessage(), phonePrice, doctorId, whetherOpen);
        }
        //doctorMapper.updateWhetherOpen(doctorId, whetherOpen, VisitCategoryEnum.phone.getCode());
        doctorMapper.updateDepartmentWhetherOpen(doctorId, whetherOpen);

        if (isupdate == 1) {
            doctorMapper.delOtherDuty(doctorId);
            Map<String, Object> setingTime = doctorVideoMapper.getSetingTime(OrderTypeEnum.Phone.getCode());
            for (Object o : timeList) {
                long startTime = ModelUtil.strToLong(o.toString(), 0);
                if (startTime != 0) {
                    long visiting_end_time = startTime + ModelUtil.getLong(setingTime, "interval_time") * 60 * 1000;//结束时间为20分钟后
                    doctorMapper.insertDuty(doctorId, startTime, visiting_end_time);
                }
            }
        }
    }


    public List<Map<String, Object>> orderProfitList(long doctorid, int pageIndex, int pageSize) {
        return doctorMapper.orderProfitList(doctorid, pageIndex, pageSize);
    }

    public Map<String, Object> doctorIntegralList(long doctorid, int pageIndex, int pageSize) {
        Map<String, Object> result = new HashMap<>();
        result.put("methodcount", doctorMapper.thisMonthDoctorIntegralCount(doctorid));
        result.put("totalcount", doctorMapper.totalDoctorIntegralCount(doctorid));
        result.put("integrallist", doctorMapper.doctorIntegralList(doctorid, pageIndex, pageSize, (Map<String, Object> value) -> {
            value.put("typename", getType(ModelUtil.getInt(value, "type")));
            return value;
        }));
        return result;
    }

    private String getType(int type) {
        if (type == IntegralTypeEnum.SignIn.getCode()) {
            return IntegralTypeEnum.SignIn.getMessage();
        } else if (type == IntegralTypeEnum.Info.getCode()) {
            return IntegralTypeEnum.Info.getMessage();
        } else if (type == IntegralTypeEnum.QA.getCode()) {
            return IntegralTypeEnum.QA.getMessage();
        } else if (type == IntegralTypeEnum.Phone.getCode()) {
            return IntegralTypeEnum.Phone.getMessage();
        } else {
            return "";
        }
    }

    public String qrcode(String sceneId, String iconName, String filename) {
        try {
            //二维码原图
            String qrcode = FileUtil.newFile(ConfigModel.BASEFILEPATH + FileUtil.setFileName(FileUtil.FILE_TEMP_PATH, "qrcode/" + filename));

            HttpParamModel httpParamModel = new HttpParamModel();
            httpParamModel.add("sceneId", sceneId);
            //获取微信二维码
            String qrcodeurl = this.qrCodeUtil.qrcode(sceneId);

            HttpUtil.getInstance().getFile(qrcodeurl, qrcode);

            BufferedImage newPic = new BufferedImage(ImageIO.read(new File(qrcode)).getWidth(), ImageIO.read(new File(qrcode)).getHeight(),
                    BufferedImage.TYPE_3BYTE_BGR);

            ColorConvertOp cco = new ColorConvertOp(ColorSpace
                    .getInstance(ColorSpace.CS_sRGB), null);
            cco.filter(ImageIO.read(new File(qrcode)), newPic);

            //将灰度二维码转换成彩色
            ImageIO.write(newPic, "png", new File(qrcode));
            //加logo
            Thumbnails
                    .of(qrcode)
                    .size(430, 430)
                    .watermark(Positions.CENTER, ImageIO.read(new File(ConfigModel.BASEFILEPATH + FileUtil.setFileName(FileUtil.FILE_STATIC_PATH, iconName))), 1f)
                    .outputQuality(1f).toFile(qrcode);

            String key = "qcode_" + UnixUtil.getCustomRandomString() + ".png";
            QiniuUtils.putFile(ConfigModel.QINIU.BUCKET, key, new FileInputStream(new File(qrcode)));
            return key;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public Map<String, Object> getDoctorCard(long doctorId) {
        Map<String, Object> simpleDoctor = getSimpleDoctor(doctorId);
        if (simpleDoctor != null) {
            simpleDoctor.put("qrcode", qrcode(String.valueOf(doctorId), TextFixed.qrcode_logo, String.format("qrcode_%s.png", doctorId)));
        }
        return simpleDoctor;
    }

    public List<Map<String, Object>> getDoctorMessageList(long doctorId, int pageIndex, int pageSize) {
        return doctorMapper.getDoctorMessageList(doctorId, pageIndex, pageSize);
    }

    public boolean updateMessageReadStatu(long id) {
        Map<String, Object> messageType = doctorMapper.getMessageType(id);
        return doctorMapper.updateMessageReadStatu(ModelUtil.getInt(messageType, "messagetype"));
    }

    public Map<String, Object> newUser(long doctorid, int pageIndex, int pageSize) {
        Map<String, Object> result = new HashMap<>();
        result.put("newuserlist", doctorMapper.newUserList(doctorid, pageIndex, pageSize));
        result.put("total", doctorMapper.newUserCount(doctorid));
        return result;
    }

    public long getDoctorUserCount(long doctorid, long userid) {
        return doctorMapper.getDoctorUserCount(doctorid, userid);
    }

    public boolean addDoctorUser(long doctorid, long userid) {
        return doctorMapper.addDoctorUser(doctorid, userid);
    }

    public boolean updateDoctorUser(long doctorid, long userid) {
        return doctorMapper.updateDoctorUser(doctorid, userid);
    }

    //添加医生图文问诊次数
    public boolean updateDoctorExtendAnswerCount(long doctorId) {
        return doctorMapper.updateDoctorExtendAnswerCount(doctorId);
    }

    //添加医生电话问诊次数
    public boolean updateDoctorExtendPhoneCount(long doctorId) {
        return doctorMapper.updateDoctorExtendPhoneCount(doctorId);
    }

    //添加医生电话问诊次数
    public boolean updateDoctorExtendVideoCount(long doctorId) {
        return doctorMapper.updateDoctorExtendVideoCount(doctorId);
    }

    public Map<String, Object> getPhoneDoctor() {
        return doctorMapper.getPhoneDoctor();
    }

    public Map<String, Object> getAnserDoctor() {
        return doctorMapper.getAnserDoctor();
    }


    /**
     * 设置坐班医生
     *
     * @param doctorids
     * @param agentid
     * @return
     */
    public boolean setSittingDoctor(List<?> doctorids, long agentid, long startTime, long endTime) {
        for (Object value : doctorids) {
            long doctorid = ((Integer) value).longValue();
            Map<String, Object> doc = doctorMapper.findDoctorName(doctorid);
            if (doctorMapper.findByDoctor(doctorid, startTime, endTime)) {
                throw new ServiceException("医生为" + ModelUtil.getStr(doc, "doc_name") + "的在该时间段已经排班了,请检查！");
            }
            long todayStart = UnixUtil.getStart();
            long todayEnd = UnixUtil.getEndTime();
            long a = todayEnd - todayStart;
            long b = endTime - startTime;
            System.out.println(a);
            if (b > a) {
                throw new ServiceException("设置的时间超出当天，请检查!");
            }
            if (doctorid > 0) {
                doctorMapper.setSittingDoctor(doctorid, agentid, startTime, endTime);
            }
        }
        return true;
    }

    /**
     * 获取电话坐班医生
     *
     * @return
     */
    public Map<String, Object> getPhoneSittingDoctor() {
        Map<String, Object> phoneSittingDoctor = doctorMapper.getPhoneSittingDoctor();
        if (phoneSittingDoctor == null) {
            phoneSittingDoctor = doctorMapper.getSittingDoctor();
        }
        if (phoneSittingDoctor == null) {
            phoneSittingDoctor = doctorMapper.getHealthyDoctor();
        }
        return phoneSittingDoctor;
    }

    /**
     * 获取问诊坐班医生
     *
     * @return
     */
    public Map<String, Object> getAnswerSittingDoctor() {
        Map<String, Object> answerSittingDoctor = doctorMapper.getAnswerSittingDoctor();
        if (answerSittingDoctor == null) {
            answerSittingDoctor = doctorMapper.getSittingDoctor();
        }
        if (answerSittingDoctor == null) {
            answerSittingDoctor = doctorMapper.getHealthyDoctor();
        }
        return answerSittingDoctor;
    }

    /**
     * 获取问诊坐班医生
     *
     * @return
     */
    public Map<String, Object> getSittingDoctor() {
        return doctorMapper.getSittingDoctor();
    }

    public Map<String, Object> getQuestion() {
        return doctorMapper.getQuestionDoc();
    }

    public Object delSittingDoctor(long id) {
        return doctorMapper.delSittingDoctor(id);
    }

    public List<Map<String, Object>> getSittingDoctorList(String name, String phone, int pageIndex, int pageSize) {
        return doctorMapper.getSittingDoctorList(name, phone, pageIndex, pageSize);
    }

    public long getSittingDoctorCount(String name, String phone) {
        return doctorMapper.getSittingDoctorCount(name, phone);
    }

    /**
     * 急诊，门诊价格管理
     *
     * @return
     */
    public List<Map<String, Object>> getEmergencyClinicPriceList() {
        return doctorMapper.getEmergencyClinicPriceList();
    }

    /**
     * 急诊，门诊价格管理
     *
     * @return
     */
    public boolean updateEmergencyClinicPrice(long id, BigDecimal emergencyprice, BigDecimal outpatientprice, long userid) {
        return doctorMapper.updateEmergencyClinicPrice(id, emergencyprice, outpatientprice, userid);
    }

    /**
     * 功能描述 坐班价格查询
     *
     * @param * @param id
     * @return
     * @author qian.wang
     * @date 2018/10/29
     */
    public Map<String, Object> findById() {
        return doctorMapper.findById();
    }


    public Map<String, Object> discountPrice() {
        Map<String, Object> discount = discount();
        Map<String, Object> money = findById();
        BigDecimal emergencyprice = ModelUtil.getDec(money, "emergencyprice", BigDecimal.ZERO);
        BigDecimal outpatientprice = ModelUtil.getDec(money, "outpatientprice", BigDecimal.ZERO);
        BigDecimal videoprice = ModelUtil.getDec(money, "videoprice", BigDecimal.ZERO);
        BigDecimal dis = ModelUtil.getDec(discount, "health_consultant_discount", BigDecimal.ZERO);
        BigDecimal em = emergencyprice.divide(dis, 2, RoundingMode.HALF_UP);
        BigDecimal ou = outpatientprice.divide(dis, 2, RoundingMode.HALF_UP);

        Map<String, Object> params = new HashMap<>();
        params.put("type", 0);//图文问诊
        params.put("outpatientpriceDiscount", ou); //计算后
        params.put("outpatientprice", outpatientprice); //原价

        //params.put("phonetype", 2);//电话问诊
        params.put("emergencypriceDiscount", em); //计算后
        params.put("emergencyprice", emergencyprice); //原价

        params.put("videoprice", videoprice);//视频原价
        params.put("discount", dis);
        return params;
    }

    /**
     * 折扣
     *
     * @return
     */
    public Map<String, Object> discount() {
        return doctorMapper.discount();
    }

    /**
     * 修改金额
     *
     * @param price
     * @param type
     * @return
     */
    public boolean updatePrice(BigDecimal price, int type) {
        boolean update = false;
        if (type == 1) {
            //图文
            update = doctorMapper.updateOutpatientprice(price);
        } else if (type == 2) {
            //电话
            update = doctorMapper.updateEmergencyprice(price);
        } else if (type == 3) {
            //视频
            update = doctorMapper.updateVideoprice(price);
        }
        return update;
    }


    /**
     * 诊疗医师
     *
     * @param docName
     * @param dooTel
     * @param workInstName
     * @param titleId
     * @param departmentId
     * @param doctorStart
     * @param doctorEnd
     * @param examine
     * @param graphicStatus
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getClinicsList(String docName, String dooTel, String workInstName, String titleId, String departmentId, long doctorStart, long doctorEnd, int examine, int graphicStatus, int pageIndex, int pageSize) {
        return doctorMapper.getClinicsList(docName, dooTel, workInstName, titleId, departmentId, doctorStart, doctorEnd, examine, graphicStatus, pageIndex, pageSize);
    }

    public long getClinicsListCount(String docName, String dooTel, String workInstName, String titleId, String departmentId, long doctorStart, long doctorEnd, int examine, int graphicStatus) {
        return doctorMapper.getClinicsListCount(docName, dooTel, workInstName, titleId, departmentId, doctorStart, doctorEnd, examine, graphicStatus);
    }

    /**
     * 诊疗医师详情
     *
     * @param doctorId
     * @return
     */
    public Map<String, Object> getClinicsById(long doctorId) {
        Map<String, Object> result = doctorMapper.getClinicsById(doctorId);
        result.put("invitationcode", doctorMapper.getSalesId(doctorId)); //邀请人
        result.put("docdepartment", doctorMapper.getDocDepartment(ModelUtil.getInt(result, "departmentid"))); //科室
        result.put("departTree", doctorMapper.getDocDepartmentTree(ModelUtil.getInt(result, "departmentid"))); //科室二级
        result.put("titleid", doctorMapper.getCodeDoctorTitle(ModelUtil.getInt(result, "titleid")));   //职称名称
        result.put("hospitalid", doctorMapper.findHospitals(ModelUtil.getInt(result, "hospitalid"))); //医院名称
        result.put("expertid", doctorMapper.bindingExpert(doctorId));
        return result;
    }


    /**
     * 修改审核状态,失败则添加失败原因
     *
     * @param doctorid
     * @param examine
     * @return
     */
    public boolean updataExamine(long doctorid, int examine, String reason) {
//        if (examine != DoctorExamineEnum.successfulCertified.getCode()) {
//            throw new ServiceException("该状态下不能审核");
//        }
        if (examine == DoctorExamineEnum.failCertified.getCode()) {
            Map<String, Object> doctorMp = doctorMapper.getDoctorNotBlobById(doctorid);
            Map<String, Object> map = new HashMap<>();
            map.put("doctor", ModelUtil.getStr(doctorMp, "docname"));
            try {
                SendShortMsgUtil.sendSms(ConfigModel.ISONLINE, ModelUtil.getStr(doctorMp, "dootel"), com.syhdoctor.common.config.ConfigModel.SMS.doctor_examine_fail, map);
            } catch (ClientException e) {
                e.printStackTrace();
            }
            doctorMapper.updateReason(doctorid, reason);
        }

        return doctorMapper.updataExamine(doctorid, examine);
    }

    /**
     * 修改顾问审核状态,失败则添加失败原因
     *
     * @param doctorid
     * @param examine
     * @param reason
     * @return
     */
    public boolean updataAdviserExamine(long doctorid, int examine, String reason) {
        if (examine == DoctorExamineEnum.failCertified.getCode()) {
            doctorMapper.updateReason(doctorid, reason);
        }
        return doctorMapper.updataExamine(doctorid, examine);
    }


    /**
     * 专家医师
     *
     * @param docName
     * @param dooTel
     * @param workInstName
     * @param titleId
     * @param departmentId
     * @param doctorStart
     * @param doctorEnd
     * @param examine
     * @param graphicStatus
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getExpertPhysicianList(String docName, String dooTel, String workInstName, String titleId, String departmentId, long doctorStart, long doctorEnd, int examine, int graphicStatus, int pageIndex, int pageSize) {
        return doctorMapper.getExpertPhysicianList(docName, dooTel, workInstName, titleId, departmentId, doctorStart, doctorEnd, examine, graphicStatus, pageIndex, pageSize);
    }

    public long getExpertPhysicianListCount(String docName, String dooTel, String workInstName, String titleId, String departmentId, long doctorStart, long doctorEnd, int examine, int graphicStatus) {
        return doctorMapper.getExpertPhysicianListCount(docName, dooTel, workInstName, titleId, departmentId, doctorStart, doctorEnd, examine, graphicStatus);
    }


    public List<Map<String, Object>> salespersonAll() {
        return doctorMapper.salespersonAll();
    }


    /**
     * 新增或修改诊疗医师
     *
     * @param invitationcode 邀请码
     * @param docName        医生姓名
     * @param docPhotoUrl    医生头像
     * @param titleId        职称中文名
     * @param idCard         医生身份证号
     * @param professional   医师擅长专业
     * @param doctorId
     * @return
     */
    public void addUpdateClinics(String invitationcode, String docName, String docPhotoUrl, int titleId, long hospitalid, String dooTel, String idCard,
                                 String professional, String introduction, int gender, int departmentId, long doctorId) {
        if (doctorId > 0) {
            long doctorCount = doctorMapper.getDoctorCount(doctorId, dooTel);
            if (doctorCount > 0) {
                throw new ServiceException("手机号码已被使用");
            }
            doctorMapper.updateClinics(docName, docPhotoUrl, titleId, hospitalid, dooTel, idCard, professional, introduction, gender, departmentId, doctorId);
            addHospitalCodeName(hospitalid, doctorId);//添加医院code医院name
//            doctorMapper.updateSalesPerson(doctorId, invitationcode);//修改邀请码
            doctorMapper.delSalesPerson(doctorId);  //删除邀请码中间表
            Map<String, Object> salesPerson = doctorMapper.findSalesPerson(invitationcode);
            if (salesPerson != null) {
                long saleid = ModelUtil.getLong(salesPerson, "id");
                doctorMapper.insertSalesPerson(saleid, invitationcode, doctorId);//添加邀请码
            }
        } else {
            long doctorCount = doctorMapper.getDoctorCount(dooTel);
            if (doctorCount > 0) {
                throw new ServiceException("手机号码已被使用");
            }
            long docid = doctorMapper.addClinics(docName, docPhotoUrl, titleId, hospitalid, dooTel, idCard, professional, introduction, gender, departmentId);
            doctorMapper.addDoctorExpand(docid); //新增拓展表
            addHospitalCodeName(hospitalid, docid);//添加医院code医院name
            Map<String, Object> salesPerson = doctorMapper.findSalesPerson(invitationcode); //添加邀请码
            if (salesPerson != null) {
                long saleid = ModelUtil.getLong(salesPerson, "id");
                doctorMapper.insertSalesPerson(saleid, invitationcode, docid);
            }
        }
    }

    /**
     * 新增或修改审方医师基本信息
     *
     * @param docName      医生姓名
     * @param docPhotoUrl  医生头像
     * @param titleId      职称中文名
     * @param idCard       医生身份证号
     * @param professional 医师擅长专业
     * @param doctorId
     * @return
     */
    public Map<String, Object> updateAddTrialParty(String invitationcode, int examine, int doctype, String docName, String docPhotoUrl, int titleId, long hospitalid, String dooTel, String idCard,
                                                   String professional, String introduction, int gender, int departmentId, long doctorId) {
        Map<String, Object> map = new HashMap<>();
        if (doctorId > 0) {
            long doctorCount = doctorMapper.getDoctorCount(doctorId, dooTel);
            if (doctorCount > 0) {
                throw new ServiceException("手机号码已被使用");
            }
            if (examine == DoctorExamineEnum.authenticationFailed.getCode()) {
                examine = DoctorExamineEnum.Certification.getCode();
            }
            doctorMapper.updateTrialParty(examine, doctype, docName, docPhotoUrl, titleId, hospitalid, dooTel, idCard, professional, introduction, gender, departmentId, doctorId);
            addHospitalCodeName(hospitalid, doctorId);//添加医院code医院name
//            doctorMapper.updateSalesPerson(doctorId, invitationcode);//修改邀请码
            doctorMapper.delSalesPerson(doctorId);  //删除邀请码中间表
            Map<String, Object> salesPerson = doctorMapper.findSalesPerson(invitationcode);
            if (salesPerson != null) {
                long saleid = ModelUtil.getLong(salesPerson, "id");
                doctorMapper.insertSalesPerson(saleid, invitationcode, doctorId);//添加邀请码
            }
        } else {
            long doctorCount = doctorMapper.getDoctorCount(dooTel);
            if (doctorCount > 0) {
                throw new ServiceException("手机号码已被使用");
            }
            long doctorid = doctorMapper.addTrialPartys(doctype, docName, docPhotoUrl, titleId, hospitalid, dooTel, idCard, professional, introduction, gender, departmentId);
            map.put("doctorid", doctorid);
            map.put("doctype", doctype);
            map.put("examine", DoctorExamineEnum.Certification.getCode());
            addHospitalCodeName(hospitalid, doctorid);//添加医院code医院name
            Map<String, Object> salesPerson = doctorMapper.findSalesPerson(invitationcode); //添加邀请码
            if (salesPerson != null) {
                long saleid = ModelUtil.getLong(salesPerson, "id");
                doctorMapper.insertSalesPerson(saleid, invitationcode, doctorid);
            }
//            else{
//                throw new ServiceException("请填写正确的邀请码");
//            }
        }
        return map;
    }

    /**
     * 新增或修改顾问医师基本信息
     *
     * @param docName      医生姓名
     * @param docPhotoUrl  医生头像
     * @param titleId      职称中文名
     * @param idCard       医生身份证号
     * @param professional 医师擅长专业
     * @param doctorId
     * @return
     */
    public Map<String, Object> addUpdateExpertPhysician(String invitationcode, String docName, String docPhotoUrl, int titleId, long hospitalid, String dooTel, String idCard,
                                                        String professional, String introduction, int gender, int departmentId, long doctorId) {
        Map<String, Object> map = new HashMap<>();
        if (doctorId > 0) {
            long doctorCount = doctorMapper.getDoctorCount(doctorId, dooTel);
            if (doctorCount > 0) {
                throw new ServiceException("手机号码已被使用");
            }
            doctorMapper.updateClinics(docName, docPhotoUrl, titleId, hospitalid, dooTel, idCard, professional, introduction, gender, departmentId, doctorId);
            addHospitalCodeName(hospitalid, doctorId);//添加医院code医院name
//            doctorMapper.updateSalesPerson(doctorId, invitationcode);//修改邀请码
            doctorMapper.delSalesPerson(doctorId);  //删除邀请码中间表
            Map<String, Object> salesPerson = doctorMapper.findSalesPerson(invitationcode);
            if (salesPerson != null) {
                long saleid = ModelUtil.getLong(salesPerson, "id");
                doctorMapper.insertSalesPerson(saleid, invitationcode, doctorId);//添加邀请码
            }
        } else {
            long doctorCount = doctorMapper.getDoctorCount(dooTel);
            if (doctorCount > 0) {
                throw new ServiceException("手机号码已被使用");
            }
            long doctorid = doctorMapper.addExpertPhysician(docName, docPhotoUrl, titleId, hospitalid, dooTel, idCard, professional, introduction, gender, departmentId);
            map.put("doctorid", doctorid);
            addHospitalCodeName(hospitalid, doctorid);//添加医院code医院name
            Map<String, Object> salesPerson = doctorMapper.findSalesPerson(invitationcode); //添加邀请码
            if (salesPerson != null) {
                long saleid = ModelUtil.getLong(salesPerson, "id");
                doctorMapper.insertSalesPerson(saleid, invitationcode, doctorid);
            }
        }
        return map;
    }


    /**
     * 顾问列表
     *
     * @param docName
     * @param dooTel
     * @param workInstName
     * @param titleId
     * @param departmentId
     * @param doctorStart
     * @param doctorEnd
     * @param examine
     * @param graphicStatus
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getAdviserList(String docName, String dooTel, String workInstName, String titleId, String departmentId, long doctorStart, long doctorEnd, int examine, int graphicStatus, int pageIndex, int pageSize) {
        return doctorMapper.getAdviserList(docName, dooTel, workInstName, titleId, departmentId, doctorStart, doctorEnd, examine, graphicStatus, pageIndex, pageSize);
    }


    public long getAdviserListCount(String docName, String dooTel, String workInstName, String titleId, String departmentId, long doctorStart, long doctorEnd, int examine, int graphicStatus) {
        return doctorMapper.getAdviserListCount(docName, dooTel, workInstName, titleId, departmentId, doctorStart, doctorEnd, examine, graphicStatus);
    }


    /**
     * 新增或修改顾问
     *
     * @param docName      医生姓名
     * @param docPhotoUrl  医生头像
     * @param titleId      职称中文名
     * @param idCard       医生身份证号
     * @param professional 医师擅长专业
     * @param doctorId
     * @return
     */
    public void addUpdateAdviser(String invitationcode, int examine, String docName, String docPhotoUrl, int titleId, long hospitalid, String dooTel, String idCard,
                                 String professional, String introduction, int gender, int departmentId, long doctorId, long expertid) {
        if (doctorId > 0) {
            long doctorCount = doctorMapper.getDoctorCount(doctorId, dooTel);
            if (doctorCount > 0) {
                throw new ServiceException("手机号码已被使用");
            }
            if (examine == DoctorExamineEnum.failCertified.getCode()) {
                examine = DoctorExamineEnum.successfulCertified.getCode();
            }
            doctorMapper.updateAdviser(examine, docName, docPhotoUrl, titleId, hospitalid, dooTel, idCard, professional, introduction, gender, departmentId, doctorId);
            doctorMapper.updateAdviserDoctorExtends(doctorId, expertid); //修改时绑定认证成功专家医师
            addHospitalCodeName(hospitalid, doctorId);//添加医院code医院name
//            doctorMapper.updateSalesPerson(doctorId, invitationcode);//修改邀请码
            doctorMapper.delSalesPerson(doctorId);  //删除邀请码中间表
            Map<String, Object> salesPerson = doctorMapper.findSalesPerson(invitationcode);
            if (salesPerson != null) {
                long saleid = ModelUtil.getLong(salesPerson, "id");
                doctorMapper.insertSalesPerson(saleid, invitationcode, doctorId);//添加邀请码
            }
        } else {
            long doctorCount = doctorMapper.getDoctorCount(dooTel);
            if (doctorCount > 0) {
                throw new ServiceException("手机号码已被使用");
            }
            long docid = doctorMapper.addAdviser(docName, docPhotoUrl, titleId, hospitalid, dooTel, idCard, professional, introduction, gender, departmentId);
            doctorMapper.insertAdviserDoctorExtends(docid, expertid); //新增时绑定认证成功专家医师
            addHospitalCodeName(hospitalid, docid);//添加医院code医院name
            Map<String, Object> salesPerson = doctorMapper.findSalesPerson(invitationcode); //添加邀请码
            if (salesPerson != null) {
                long saleid = ModelUtil.getLong(salesPerson, "id");
                doctorMapper.insertSalesPerson(saleid, invitationcode, docid);
            }
//            else{
//                throw new ServiceException("请填写正确的邀请码");
//            }
        }

    }

    /**
     * 审方医师
     *
     * @param docName
     * @param dooTel
     * @param workInstName
     * @param titleId
     * @param departmentId
     * @param doctorStart
     * @param doctorEnd
     * @param examine
     * @param graphicStatus
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getTrialPartyList(String docName, String dooTel, String workInstName, String titleId, String departmentId, long doctorStart, long doctorEnd, int examine, int graphicStatus, int pageIndex, int pageSize) {
        return doctorMapper.getTrialPartyList(docName, dooTel, workInstName, titleId, departmentId, doctorStart, doctorEnd, examine, graphicStatus, pageIndex, pageSize);
    }

    public long getTrialPartyListCount(String docName, String dooTel, String workInstName, String titleId, String departmentId, long doctorStart, long doctorEnd, int examine, int graphicStatus) {
        return doctorMapper.getTrialPartyListCount(docName, dooTel, workInstName, titleId, departmentId, doctorStart, doctorEnd, examine, graphicStatus);
    }


    /**
     * 后台完善审方医生信息
     *
     * @param docName        医生姓名
     * @param docPhotoUrl    医生头像
     * @param docType        医生类型
     * @param titleId        职称中文名
     * @param idCard         医生身份证号
     * @param pracNo         医师执业号
     * @param pracRecDate    执业证取得时间（YYYY-MM-DD）
     * @param certNo         医师资格证号
     * @param certRecDate    资格证取得时间
     * @param titleNo        医师职称号
     * @param titleRecDate   职称证取得时间
     * @param pracType       医师执业类别
     *                       //     * @param qualifyOrNot     考核是否合格 是 | 否
     * @param professional   医师擅长专业
     * @param signTime       签约时间
     * @param signLife       签约年限
     * @param employFileUrl  聘任合同
     *                       //     * @param creditLevel      信用评级
     *                       //     * @param occuLevel        职业评级
     * @param digitalSignUrl 数字签名留样
     *                       //     * @param docPenaltyPoints 医师评分
     *                       //     * @param ycRecordFlag     银川是否备案
     *                       //     * @param hosConfirmFlag   医院是否备案
     *                       //     * @param ycPresRecordFlag 是否有开处方的权限
     * @param doctorId
     * @return
     */
    public void addUpdateTrialParty(int examine, String docName, String docPhotoUrl, int docType, int titleId, long hospitalid, String dooTel, String idCard, String pracNo, long pracRecDate, String certNo, long certRecDate, String titleNo,
                                    long titleRecDate, String pracType, String professional,
                                    long signTime, String signLife, String employFileUrl,
                                    String digitalSignUrl, String pracScope, String pracScopeApproval,
                                    long docMultiSitedDateStart, long docMultiSitedDateEnd,
                                    long docMultiSitedDatePromise, long agentid,
                                    String introduction, int gender, int departmentId, long doctorId, String inDocCode, long tempDoctorId,
                                    List<?> ardList, List<?> certList, List<?> certPracList, List<?> titleCertList, List<?> multiSitedLicRecordList) {

        String workInstName = ModelUtil.getStr(findDoctor(hospitalid), "hospital_name");
        String workInstCode = ModelUtil.getStr(findDoctor(hospitalid), "hospital_code");
        String docPhoto = BASE64.base64Encoding(PicUtils.compressPicForScale(BASE64.readFile(Paths.get(ConfigModel.BASEFILEPATH + FileUtil.getFileName(docPhotoUrl))), 200));
        String employFile = BASE64.base64Encoding(PicUtils.compressPicForScale(BASE64.readFile(Paths.get(ConfigModel.BASEFILEPATH + FileUtil.getFileName(employFileUrl))), 200));
        String digitalSign = BASE64.base64Encoding(PicUtils.compressPicForScale(BASE64.readFile(Paths.get(ConfigModel.BASEFILEPATH + FileUtil.getFileName(digitalSignUrl))), 200));
        String qualifyOrNot = "1";
        String creditLevel = "优秀";
        String occuLevel = "1级";
        String docPenaltyPoints = "100";
        String hosOpinion = "同意";
        long hosOpinionDate = docMultiSitedDateStart;
        int agreeTerms = 1;
        if (doctorId > 0) {
            long doctorCount = doctorMapper.getDoctorCount(doctorId, dooTel);
            if (doctorCount > 0) {
                throw new ServiceException("手机号码已被使用");
            }
            doctorMapper.deleteDocCard(doctorId);//删除身份证
            doctorMapper.deleteDocCertList(doctorId);//删除资格证文件
            doctorMapper.deleteCertDocPracList(doctorId);//删除执业证文件
            doctorMapper.deleteDocTitleCertList(doctorId);//删除职称
            doctorMapper.deleteMultiSitedLicRecord(doctorId);//删除多点执业
            if ((docType == DoctorTypeEnum.trialDoctor.getCode() || docType == DoctorTypeEnum.ReviewDoctor.getCode()) && examine == DoctorExamineEnum.authenticationFailed.getCode()) {
                examine = DoctorExamineEnum.Certification.getCode();
            }
            doctorMapper.updateDoctorInfos(pracNo, examine, docPhoto, docType, hospitalid,
                    pracRecDate, certNo, certRecDate, titleNo,
                    titleRecDate, pracType,
                    signTime, signLife, employFile, employFileUrl,
                    digitalSign, digitalSignUrl, pracScope, pracScopeApproval,
                    docMultiSitedDateStart, docMultiSitedDateEnd,
                    docMultiSitedDatePromise, agentid, doctorId);
            Map<String, Object> lecturer = lecturerService.getLecturerByDoctor(doctorId);
            if (lecturer != null) {
                Map<String, Object> department = codeService.getDepartment(departmentId);
                Map<String, Object> title = codeService.getTitle(titleId);
                lecturerService.updateLecturer(docName, docPhotoUrl, dooTel, ModelUtil.getStr(title, "name"), workInstName, ModelUtil.getStr(department, "name"), professional, introduction, agentid, doctorId);
            }
        } else {
            long doctorCount = doctorMapper.getDoctorCount(dooTel);
            if (doctorCount > 0) {
                throw new ServiceException("手机号码已被使用");
            }
            doctorId = doctorMapper.addTrialParty(docName, docPhoto, docPhotoUrl, docType, titleId, workInstCode,
                    workInstName, hospitalid, dooTel, idCard, pracNo, pracRecDate, certNo, certRecDate, titleNo,
                    titleRecDate, pracType, qualifyOrNot, professional,
                    signTime, signLife, employFile, employFileUrl, creditLevel, occuLevel,
                    digitalSign, digitalSignUrl, docPenaltyPoints, pracScope, pracScopeApproval, agreeTerms,
                    docMultiSitedDateStart, docMultiSitedDateEnd, hosOpinion,
                    hosOpinionDate, docMultiSitedDatePromise, agentid,
                    introduction, gender, departmentId, inDocCode, tempDoctorId);
            doctorMapper.addDoctorExpand(doctorId);
        }
        //身份证文件
        if (ardList.size() > 0) {
            for (Object value : ardList) {
                String cardId = String.valueOf(value);
                //String blobCardId = BASE64.imageToBase64Str( ConfigModel.BASEFILEPATH + FileUtil.getFileName(FileUtil.FILE_DOCTOR_PATH, cardId));
                doctorMapper.addDocCardList("", cardId, doctorId, doctorId);
            }
        }
        //资格证文件
        if (certList.size() > 0) {
            for (Object value : certList) {
                String docCert = String.valueOf(value);
                //String blobDocCert = BASE64.imageToBase64Str( ConfigModel.BASEFILEPATH + FileUtil.getFileName(FileUtil.FILE_DOCTOR_PATH,docCert));
                doctorMapper.addDocCertList("", docCert, doctorId, agentid);
            }

        }
        //保存执业证文件
        if (certPracList.size() > 0) {
            for (Object value : certPracList) {
                String certDocPrac = String.valueOf(value);
                //String blobCertDocPrac = BASE64.imageToBase64Str( ConfigModel.BASEFILEPATH + FileUtil.getFileName(FileUtil.FILE_DOCTOR_PATH, ) + certDocPrac);
                doctorMapper.addCertDocPracList("", certDocPrac, doctorId, agentid);
            }
        }
        //保存职称文件列表
        if (titleCertList.size() > 0) {
            for (Object value : titleCertList) {
                String titleCert = String.valueOf(value);
                //String blobTitleCert = BASE64.imageToBase64Str( ConfigModel.BASEFILEPATH + FileUtil.getFileName(FileUtil.FILE_DOCTOR_PATH, ) + titleCert);
                doctorMapper.addTitleCertList("", titleCert, doctorId, doctorId);
            }
        }
        //保存多点执业文件列表
        if (multiSitedLicRecordList.size() > 0) {
            for (Object value : multiSitedLicRecordList) {
                String docMultiSitedLicRecord = String.valueOf(value);
                doctorMapper.addMultiSitedLicRecord("", docMultiSitedLicRecord, doctorId);
            }
        }
    }


    /**
     * 顾问详情的医生下拉框（专家认证成功的）
     *
     * @return
     */
    public List<Map<String, Object>> doctorSelect(String phoneName) {
        return doctorMapper.doctorSelect(phoneName);
    }


    /**
     * 添加or修改医生，医院code医院name数据冗余到医生
     *
     * @param hospitalid
     * @param doctorid
     */
    public void addHospitalCodeName(long hospitalid, long doctorid) {
        Map<String, Object> map = doctorMapper.getHospitalById(hospitalid);
        String hospitalcode = ModelUtil.getStr(map, "hospitalcode");
        String hospitalname = ModelUtil.getStr(map, "hospitalname");
        doctorMapper.updateDoctorCodeName(doctorid, hospitalcode, hospitalname);
    }


    public Map<String, Object> getDoctorExamine(long doctorid) {
        return doctorMapper.getDoctorExamine(doctorid);
    }

    public Map<String, Object> findScheudList(long doctorid) {
        Map<String, Object> settTime = doctorVideoMapper.getSetingTime(OrderTypeEnum.Phone.getCode());
        int start = ModelUtil.getInt(settTime, "start_time");
        int end = ModelUtil.getInt(settTime, "end_time");
        int interval_time = ModelUtil.getInt(settTime, "interval_time");//间隔时间
        int fina = 0;
        try {
            int interval = (end - start) * 60;
            fina = interval / interval_time;
            if (fina < 0 || fina == 0) {
                throw new ServiceException("配的开始时间结束时间不合法,请检查");
            }
        } catch (Exception e) {
            throw new ServiceException("配的开始时间结束时间不合法,请检查");
        }
        Map<String, Object> map = new HashMap<>();
        long currentTimes = UnixUtil.getNowTimeStamp();
        List<Map<String, Object>> result = doctorMapper.findResults(doctorid);
        Map<Long, Object> mapResult = new HashMap<>();
        for (Map<String, Object> m : result) {
            long time = ModelUtil.getLong(m, "visiting_start_time");
            mapResult.put(time, ModelUtil.getInt(m, "issubscribe"));
        }

        List<Map<String, Object>> doctorLdleTimeR = new ArrayList<>();
        long currentTime = UnixUtil.getStart();//当天开始时间
        Date time = UnixUtil.getStartDate();
        for (int i = 0; i < 14; i++) {
            Map<String, Object> map2 = new HashMap<>();
            long tomorrow;
            if (i == 0) {
                tomorrow = currentTime;
            } else {
                tomorrow = UnixUtil.getBeginDayOfTomorrow(time);//每天的日期
            }
            time = new Date(tomorrow);
            List<Map<String, Object>> doctorLdleR = new ArrayList<>();
            long startTimeCurrent = tomorrow + start * 60 * 60 * 1000 - interval_time * 60 * 1000;//每天每段开始时间
            for (int j = 0; j < fina + 1; j++) {
                startTimeCurrent += interval_time * 60 * 1000;
                if (i == 0) {
                    if (currentTimes > startTimeCurrent) {
                        continue;
                    }
                }
                Map<String, Object> map1 = new HashMap<>();
                map1.put("ldlestime", startTimeCurrent);
                if (mapResult.keySet().contains(startTimeCurrent)) {
                    map1.put("isopen", 1);//是否开放
                    map1.put("issubscribe", mapResult.get(startTimeCurrent));//未被预约
                } else {
                    map1.put("isopen", 0);//是否开放
                    map1.put("issubscribe", 0);
                }
                doctorLdleR.add(map1);
            }
            map2.put("doctorldler", doctorLdleR);
            map2.put("date", tomorrow);
            doctorLdleTimeR.add(map2);
        }

        map.put("currenttime", currentTime);
        map.put("doctorldletimer", doctorLdleTimeR);
        return map;
    }


    public boolean getSql() {
        return doctorMapper.udate();
    }

    public boolean getUp() {
        List<Map<String, Object>> list = doctorMapper.get();
        try {
            for (Map<String, Object> map : list) {
                String cardno = ModelUtil.getStr(map, "cardno");
                long id = ModelUtil.getLong(map, "id");
                if (cardno == null) {
                    continue;
                }
                int leh = cardno.length();
                String dates = "";
                int u = 0;
                if (leh == 18) {
                    dates = cardno.substring(6, 10);
                    SimpleDateFormat df = new SimpleDateFormat("yyyy");
                    String year = df.format(new Date());
                    u = Integer.parseInt(year) - Integer.parseInt(dates);
                } else {
                    dates = cardno.substring(6, 8);
                    u = Integer.parseInt(dates);
                }
                doctorMapper.updateSql(id, u);

            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.getMessage());
        }
        return true;
    }

    public List<Map<String, Object>> getUserOrderList(long userid, long doctorid, int pageIndex, int pageSize) {
        List<Map<String, Object>> userOrderList = doctorMapper.getUserOrderList(userid, doctorid, pageIndex, pageSize);
        List<Long> answerIds = new ArrayList<>();
        List<Long> phoneIds = new ArrayList<>();
        List<Long> videoIds = new ArrayList<>();
        for (Map<String, Object> map : userOrderList) {
            long orderid = ModelUtil.getLong(map, "id");
            switch (OrderTypeEnum.getValue(ModelUtil.getInt(map, "ordertype"))) {
                case Answer:
                    answerIds.add(orderid);
                    break;
                case Phone:
                    phoneIds.add(orderid);
                    break;
                case Video:
                    videoIds.add(orderid);
                    break;
                default:
                    break;
            }
        }

        if (answerIds.size() > 0) {
            List<Map<String, Object>> orderDiseaseList = userMapper.orderAnswerDiseaseList(answerIds);
            initList(userOrderList, orderDiseaseList, OrderTypeEnum.Answer.getCode());
        }

        if (phoneIds.size() > 0) {
            List<Map<String, Object>> orderDiseaseList = userMapper.orderPhoneDiseaseList(phoneIds);
            initList(userOrderList, orderDiseaseList, OrderTypeEnum.Phone.getCode());
        }

        if (videoIds.size() > 0) {
            List<Map<String, Object>> orderDiseaseList = userMapper.orderVideoDiseaseList(videoIds);
            initList(userOrderList, orderDiseaseList, OrderTypeEnum.Video.getCode());
        }

        return userOrderList;
    }

    private void initList(List<Map<String, Object>> orderList, List<Map<String, Object>> diseaseList, int ordertype) {
        List<Map<String, Object>> tempList = new ArrayList<>();
        Map<Long, Object> tempProblem = new HashMap<>();
        long tempId = 0;

        for (Map<String, Object> obj : diseaseList) {
            Long orderid = ModelUtil.getLong(obj, "orderid");
            if (orderid > 0) {
                if (orderid != tempId) {
                    tempId = orderid;
                    tempList = new ArrayList<>();
                    Map<String, Object> contentObj = new HashMap<>();
                    contentObj.put("value", ModelUtil.getStr(obj, "value"));
                    tempList.add(contentObj);

                    tempProblem.put(orderid, tempList);
                } else {
                    Map<String, Object> contentObj = new HashMap<>();
                    contentObj.put("value", ModelUtil.getStr(obj, "value"));
                    tempList.add(contentObj);
                }
            }
        }

        for (Map<String, Object> map : orderList) {
            if (ModelUtil.getInt(map, "ordertype") == ordertype) {
                map.put("diseaselist", tempProblem.get(ModelUtil.getLong(map, "id")));
            }
        }
    }

    public Map<String, Object> doctorRecommendPriceList() {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> Answerlist = doctorMapper.doctorRecommendPriceList(OrderTypeEnum.Answer.getCode());//医生图文推荐价格
        List<Map<String, Object>> Phonelist = doctorMapper.doctorRecommendPriceList(OrderTypeEnum.Phone.getCode());//医生电话推荐价格
        List<Map<String, Object>> Videolist = doctorMapper.doctorRecommendPriceList(OrderTypeEnum.Video.getCode());//医生视频推荐价格
        map.put("answer", Answerlist);
        map.put("phone", Phonelist);
        map.put("video", Videolist);
        return map;
    }


    public boolean insertDoctorRecommendPrice(List<?> answerlist, List<?> phonelist, List<?> videolist) {
        if (answerlist == null && phonelist == null && videolist == null) {
            return false;
        }
        doctorMapper.delDoctorRecommendPrice();//删除医生推荐价格
        List<Map<String, Object>> list1 = (List<Map<String, Object>>) answerlist;
        List<Map<String, Object>> list2 = (List<Map<String, Object>>) phonelist;
        List<Map<String, Object>> list3 = (List<Map<String, Object>>) videolist;
        for (Map<String, Object> map : list1) {
            int sort = ModelUtil.getInt(map, "sort");
            BigDecimal price = ModelUtil.getDec(map, "price", BigDecimal.ZERO);
            doctorMapper.insertDoctorRecommendPrice(sort, price, OrderTypeEnum.Answer.getCode());
        }
        for (Map<String, Object> map : list2) {
            int sort = ModelUtil.getInt(map, "sort");
            BigDecimal price = ModelUtil.getDec(map, "price", BigDecimal.ZERO);
            doctorMapper.insertDoctorRecommendPrice(sort, price, OrderTypeEnum.Phone.getCode());
        }
        for (Map<String, Object> map : list3) {
            int sort = ModelUtil.getInt(map, "sort");
            BigDecimal price = ModelUtil.getDec(map, "price", BigDecimal.ZERO);
            doctorMapper.insertDoctorRecommendPrice(sort, price, OrderTypeEnum.Video.getCode());
        }
        return true;
    }


    public List<Object> getRecommendPrice(int type) {
        List<Map<String, Object>> list = doctorMapper.getRecommendPrice(type);
        for (Map<String, Object> map : list) {
            map.put("price", ModelUtil.getDec(map, "price", BigDecimal.ZERO));
        }
        List<Object> params = new ArrayList<>();
        for (Map<String, Object> map : list) {
            params.add(ModelUtil.getDec(map, "price", BigDecimal.ZERO));
        }
        return params;
    }

    public boolean updatecheck(int id, int onecheck, int twocheck) {
        return doctorMapper.updatecheck(id, onecheck, twocheck);
    }

    public List<Map<String, Object>> getCheck() {
        return doctorMapper.getCheck();
    }

    public Map<String, Object> getCheckDetail(long id) {
        return doctorMapper.getCheckDetail(id);
    }


}
