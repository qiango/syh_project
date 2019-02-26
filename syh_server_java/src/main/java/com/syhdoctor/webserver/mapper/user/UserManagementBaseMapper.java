package com.syhdoctor.webserver.mapper.user;

import com.syhdoctor.common.utils.EnumUtils.*;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.PriceUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;

import java.sql.ResultSet;
import java.util.*;


public abstract class UserManagementBaseMapper extends BaseMapper {

    /*
    会员信息
     */

    public Map<String, Object> getUserMember(long userid) {
        String sql = "select v.level,u.userno,v.create_time createtime,v.vip_expiry_time expirytime from user_member v left join user_account u on v.userid=u.id where v.is_enabled=1 and ifnull(v.delflag,0)=0 and v.userid=?";
        return queryForMap(sql, userid);
    }

    public Map<String, Object> getVipInfo(long userid) {
        String sql = "select v.price,v.vipcardname,v.effective_time effectivetime,u.health_consultant_discount healthconsultantdiscount,u.medical_expert_discount medicalexpertdiscount,u.health_consultant_ceefax healthconsultantceefax,u.health_consultant_phone healthconsultantphone,u.medical_expert_ceefax medicalexpertceefax ," +
                " u.medical_expert_phone medicalexpertphone, u.medical_expert_video medicalexpertvideo  from user_member u left join vip_card v on u.vipcardid=v.id where u.is_enabled=1 and ifnull(u.delflag,0)=0 and u.userid=?";
        Map<String, Object> map = queryForMap(sql, userid);
        if (map != null) {
            map.put("price", PriceUtil.findPrice(ModelUtil.getLong(map, "price")));
        }
        return map;
    }

    public Map<String, Object> getVipInfoWeb(long userid) {
        String sql = "select ua.userno,v.health_consultant_ceefax allceefax,v.health_consultant_phone allphone,v.medical_expert_ceefax allmedicalceefax,v.medical_expert_phone allmedicalphone,v.medical_expert_video allmedicalvideo," +
                " u.health_consultant_discount healthconsultantdiscount,u.medical_expert_discount medicalexpertdiscount,u.health_consultant_ceefax healthconsultantceefax,u.health_consultant_phone healthconsultantphone,u.medical_expert_ceefax medicalexpertceefax ," +
                " u.medical_expert_phone medicalexpertphone, u.medical_expert_video medicalexpertvideo  from user_member u left join vip_card v on u.vipcardid=v.id left join user_account ua on u.userid=ua.id where u.is_enabled=1 and ifnull(u.delflag,0)=0 and u.userid=?";
        return queryForMap(sql, userid);
    }

    /*
    基本信息
     */
    public Map<String, Object> getUserAccount(long userid) {
        String sql = "select pregnancy,childbirth,abortion,isallergy,isfamilyhistory,issmoking,ischronicillness,issurgery,isdrinking,isfertility,ismenopause ,mencharage ,final_menarche finalmenarche,isallergy_other isallergyother,family_other familyother,issurgery_other issurgeryother,ischronicillness_other ischronicillnessother from user_case where userid=? and ifnull(delflag,0)=0 ";
        return queryForMap(sql, userid);
    }


    //基本信息
    public Map<String, Object> getAccount(long userid) {
        String sql = "select height,weight,bmi_index bmiindex,is_marry ismarry from user_health_records where userid=? and ifnull(delflag,0)=0";
        Map<String, Object> map = queryForMap(sql, userid);
        if (null != map) {
            map.put("ismarry", MarryTypeEnum.getValue(ModelUtil.getInt(map, "ismarry")).getMessage());
        }
        return map;
    }

    //基本信息
    public Map<String, Object> getAccountApp(long userid) {
        String sql = "select height,weight,bmi_index bmiindex,is_marry ismarry from user_health_records where userid=? and ifnull(delflag,0)=0";
        Map<String, Object> map = queryForMap(sql, userid);
        return map;
    }

    /*
       用户信息
    */
    public Map<String, Object> getUserInfo(long userid) {
        String sql = "select name,age,gender,headpic,isinformation from user_account where id=?";
        return queryForMap(sql, userid);
    }

    //基本信息
    public Map<String, Object> getAccounts(long userid) {
        String sql = "select height,weight,bmi_index bmiindex,is_marry id,is_marry ismarry,ua.gender from user_health_records uh left join user_account ua on uh.userid=ua.id where userid=? and ifnull(uh.delflag,0)=0";
        Map<String, Object> map = queryForMap(sql, userid);
        return map;
    }

    public Map<String,Object> getUserGender(long userid){
        String sql="select gender from user_account where id=? and ifnull(delflag,0)=0";
        return queryForMap(sql,userid);
    }

    //病例列表
    public List<Map<String, Object>> getBasicList(int type) {
        String sql = "select customid id,name from basics where type=? and ifnull(delflag,0)=0 ";
        return queryForList(sql, type);
    }

    //病例
    public List<Map<String, Object>> getDiseaseList(long userid) {
        String sql = "select b.name,b.customid id,b.type from middle_user_case mu left join basics b on mu.basicid=b.customid and ifnull(b.delflag,0)=0  where mu.userid=? and ifnull(mu.delflag,0)=0";
        return queryForList(sql, userid);
    }

    public boolean getDiseaseListBoolean(long userid,long customid) {
        String sql = "select id from middle_user_case where userid=? and ifnull(delflag,0)=0 and basicid=? ";
        List<Object> list=new ArrayList<>();
        list.add(userid);
        list.add(customid);
        return queryForMap(sql,list)==null?false:true;
    }

    public boolean updateinfo(long userid, int weight, int height, Object ismarry, double bmi) {
        String sql = "update user_health_records set bmi_index=?,weight=?,height=?,is_marry=?,modify_time=? where userid=?";
        List<Object> list = new ArrayList<>();
        list.add(bmi);
        list.add(weight);
        list.add(height);
        list.add(ismarry);
        list.add(UnixUtil.getNowTimeStamp());
        list.add(userid);
        return update(sql, list) > 0;
    }

    public boolean updateinfoWeb(long userid, Object weight,Object height, Object ismarry, Object bmi) {
        String sql = "update user_health_records set bmi_index=?,weight=?,height=?,is_marry=?,modify_time=? where userid=?";
        List<Object> list = new ArrayList<>();
        list.add(bmi);
        list.add(weight);
        list.add(height);
        list.add(ismarry);
        list.add(UnixUtil.getNowTimeStamp());
        list.add(userid);
        return update(sql, list) > 0;
    }

    public boolean insertInfo(long userid, int weight, int height, Object ismarry, double bmi) {
        String sql = "insert into user_health_records(userid,height,weight,bmi_index,is_marry,create_time) values(?,?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(userid);
        params.add(height);
        params.add(weight);
        params.add(bmi);
        params.add(ismarry);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params) > 0;
    }

    public boolean insertInfoWeb(long userid, Object weight, Object height, Object ismarry, Object bmi) {
        String sql = "insert into user_health_records(userid,height,weight,bmi_index,is_marry,create_time) values(?,?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(userid);
        params.add(height);
        params.add(weight);
        params.add(bmi);
        params.add(ismarry);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params) > 0;
    }

    public List<Map<String, Object>> selectIsUser(long userid) {
        String sql = " select id  from user_health_records where ifnull(delflag,0)=0 and userid =? ";
        return queryForList(sql, userid);
    }


    public boolean insertDisease(long userid, Object isallergy, Object ischronicillness, String ischronOther, Object issurgery, String issurgeryOther, String isallergyOther,
                                 Object isfamilyhistory, String familyOther, Object issmoking, Object isdrinking, Object isfertility,
                                 Object pregnancy, Object childbirth, Object abortion, Object menarche_age, long final_menarche, Object ismenopause) {
        String sql = "insert into user_case (userid,create_time,isallergy,ischronicillness,isfamilyhistory,issurgery,issmoking,isdrinking,isfertility,pregnancy,childbirth, " +
                "abortion,mencharage,final_menarche,ismenopause,ischronicillness_other,issurgery_other,family_other,isallergy_other) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        List<Object> list = new ArrayList<>();
        list.add(userid);
        list.add(UnixUtil.getNowTimeStamp());
        list.add(isallergy);
        list.add(ischronicillness);
        list.add(isfamilyhistory);
        list.add(issurgery);
        list.add(issmoking);
        list.add(isdrinking);
        list.add(isfertility);
        list.add(pregnancy);
        list.add(childbirth);
        list.add(abortion);
        list.add(menarche_age);
        list.add(final_menarche);
        list.add(ismenopause);
        list.add(ischronOther);
        list.add(issurgeryOther);
        list.add(familyOther);
        list.add(isallergyOther);
        return insert(sql, list) > 0;
    }

    public void insertMiddleDisease(long userid, long valueid) {
        String sql = "insert into middle_user_case (userid,basicid,create_time) values (?,?,?)";
        List<Object> list = new ArrayList<>();
        list.add(userid);
        list.add(valueid);
        list.add(UnixUtil.getNowTimeStamp());
        insert(sql, list);
    }

    public void updateMiddle(long userid) {
        String sql = "update middle_user_case set delflag=1 where userid=? and ifnull(delflag,0)=0";
        update(sql, userid);
    }

    public boolean updateDieas(long userid) {
        String sql = "update user_case set delflag=1 where userid=? and ifnull(delflag,0)=0";
        return update(sql, userid) > 0;
    }


    public boolean updateDisease(long userid, int isallergy, List<Long> isallergylist, int ischronicillness, List<Long> ischronicillnesslist, String ischronOther, int issurgery, String issurgeryOther,
                                 int isfamilyhistory, List<Long> familyList, String familyOther, int issmoking, int isdrinking, int isfertility,
                                 int pregnancy, int childbirth, int abortion, int menarche_age, int menid, int mendayid, int final_menarche, int ismenopause) {
        String sql = "insert into user_case (userid,create_time,isallergy,ischronicillness，isfamilyhistory,issurgery,issmoking,isdrinking,isfertility,pregnancy,childbirth " +
                "abortion,menarche_age,final_menarche,ismenopause,ischronicillness_other,issurgery_other,family_other) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        List<Object> list = new ArrayList<>();
        list.add(userid);
        list.add(UnixUtil.getNowTimeStamp());
        list.add(isallergy);
        list.add(ischronicillness);
        list.add(isfamilyhistory);
        list.add(issurgery);
        list.add(issmoking);
        list.add(isdrinking);
        list.add(isfertility);
        list.add(pregnancy);
        list.add(childbirth);
        list.add(abortion);
        list.add(menarche_age);
        list.add(final_menarche);
        list.add(ismenopause);
        list.add(ischronOther);
        list.add(issurgeryOther);
        list.add(familyOther);
        return insert(sql, list) > 0;
    }


    /*
    通用
     */

    public Map<String, Object> getUserId(long userid) {
        String sql = " select id,headpic,name,gender,age,phone,userno,cardno,birthday from user_account where ifnull(delflag,0)=0 and id = ? ";
        return queryForMap(sql, userid);
    }

    public List<Map<String, Object>> isVip(long userid) {
        String sql = " select id from user_member where ifnull(delflag,0)=0 and is_enabled=1 and is_expire = 1 and userid = ? ";
        return queryForList(sql, userid);
    }






    /*
    账户信息
     */

    /**
     * 基本信息
     *
     * @param id
     * @return
     */
    public Map<String, Object> getUserAccountId(long id) {
        String sql = " SELECT " +
                " ua.id, " +
                " userno, " +
                " ua.create_time createtime, " +
                " walletbalance, " +
                " integral, " +
                " um.level, " +
                " um.vip_expiry_time vipexpirytime " +
                " FROM user_account ua " +
                " LEFT JOIN user_member um ON ifnull(um.delflag,0) = 0 AND um.userid = ua.id  " +
                " WHERE ifnull( ua.delflag, 0 ) = 0 AND ua.id = ? ";
        Map<String, Object> map = queryForMap(sql, id);
        if (map != null) {
            map.put("walletbalance", PriceUtil.findPrice(ModelUtil.getLong(map, "walletbalance")));
        }
        return map;
    }

    /**
     * 钱包的交易记录
     *
     * @param userid
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> transactionRecordList(long userid, int pageIndex, int pageSize) {
        String sql = " select utr.id,transactiontype,transactionmoney,moneyflag,utr.create_time createtime,utr.walletbalance from user_transaction_record utr " +
                "                 left join user_account ua on ifnull(ua.delflag,0)=0 and ua.id=utr.userid  " +
                "                 where utr.userid=? and ifnull(utr.delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(userid);
        return query(pageSql(sql, " order by utr.id desc "), pageParams(params, pageIndex, pageSize), (ResultSet rs, int num) -> {
            Map<String, Object> data = resultToMap(rs);
            if (data != null) {
                data.put("transactionmoney", PriceUtil.findPrice(ModelUtil.getLong(data, "transactionmoney")));
                data.put("transactiontypename", TransactionTypeStateEnum.getValue(ModelUtil.getInt(data, "transactiontype")).getMessage());
                data.put("walletbalance", PriceUtil.findPrice(ModelUtil.getLong(data, "walletbalance")));
            }
            return data;
        });
    }


    public long transactionRecordListCount(long userid) {
        String sql = " select count(utr.id) count from user_transaction_record utr " +
                "                 left join user_account ua on ifnull(ua.delflag,0)=0 and ua.id=utr.userid  " +
                "                 where utr.userid=? and ifnull(utr.delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(userid);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }


    /**
     * 积分记录
     *
     * @param userid
     * @return
     */
    public List<Map<String, Object>> userIntegralList(long userid, int pageIndex, int pageSize) {
        String sql = " select integral,type,create_time createtime from user_integral_detailed where userid=? ";
        List<Object> params = new ArrayList<>();
        params.add(userid);
        return query(pageSql(sql, " order by id desc "), pageParams(params, pageIndex, pageSize), (ResultSet res, int row) -> {
            Map<String, Object> data = resultToMap(res);
            if (data != null) {
                data.put("typename", IntegralTypeEnum.getValue(ModelUtil.getInt(data, "type")).getMessage());
            }
            return data;
        });
    }

    public long userIntegralListCount(long userid) {
        String sql = " select count(id) count from user_integral_detailed where userid=? ";
        List<Object> params = new ArrayList<>();
        params.add(userid);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }




    /*
        就诊信息
     */

    /**
     * 图文
     *
     * @param userid
     * @return
     */
    public List<Map<String, Object>> getProblemOrderList(long userid) {
        String sql = " SELECT dpo.id,di.doc_name docname, cd.value, dpo.visitcategory, dpo.create_time createtime, dpo.states status, dpo.doctorid, dpo.userid " +
                " FROM doctor_problem_order dpo " +
                "       left join doctor_info di ON ifnull(di.delflag, 0) = 0 AND di.doctorid = dpo.doctorid " +
                "       left join code_department cd ON ifnull(cd.delflag, 0) = 0 AND cd.id = di.department_id " +
                " WHERE ifnull(dpo.delflag, 0) = 0  and dpo.states = 4 " +
                "  and dpo.userid = ? ";
        List<Map<String, Object>> list = queryForList(sql, userid);
        for (Map<String, Object> map : list) {
            map.put("statusname", AnswerOrderStateEnum.getValue(ModelUtil.getInt(map, "status")).getMessage());
            map.put("visitcategoryname", VisitCategoryEnum.getValue(ModelUtil.getInt(map, "visitcategory")).getMessage());
//            map.put("time",UnixUtil.timeStampDate(ModelUtil.getLong(map,"createtime"),""));
        }
        return list;
    }

    /**
     * 图文详情
     *
     * @param orderid
     * @return
     */
    //dpo.disease_time diseasetime,dpo.gohospital,dpo.issuredis,dpo.dis_describe disdescribe,mop.disease_picture diseasepicture
    public Map<String, Object> getProblemOrderListId(long orderid) {
        String sql = " select dpo.id,dpo.create_time                                  createtime, " +
                "       concat_ws(' ', di.doc_name, cd.value, cdt.value) attendingdoctor, " +
                "       complaints, " +
                "       diagnosis, " +
                "       dpo.disease_time diseasetime, " +
                "       dpo.gohospital, " +
                "       dpo.issuredis, " +
                "       dpo.dis_describe disdescribe, " +
                "       remark " +
                " from doctor_problem_order dpo " +
                "       left join doctor_info di on ifnull(di.delflag, 0) = 0 AND di.doctorid = dpo.doctorid " +
                "       left join code_department cd on ifnull(cd.delflag, 0) = 0 and cd.id = di.department_id " +
                "       left join code_doctor_title cdt on ifnull(cdt.delflag, 0) = 0 and cdt.id = di.title_id " +
                " where ifnull(dpo.delflag, 0) = 0 and dpo.id=? ";
        return queryForMap(sql, orderid);
    }

    public List<Map<String, Object>> orderProblemPicture(long orderid, int ordertype) {
        String sql = " select orderid id,disease_picture url from  middle_order_picture where ifnull(delflag,0)=0 and orderid=? and order_type=? ";
        List<Object> params = new ArrayList<>();
        params.add(orderid);
        params.add(ordertype);
        return queryForList(sql, params);
    }

    public Map<String, Object> sickTime(long customid) {
        String sql = " select customid id,name  from  basics where ifnull(delflag,0)=0 and type=25 and customid=? ";
        return queryForMap(sql, customid);
    }


    /**
     * 电话
     *
     * @param userid
     * @return
     */
    public List<Map<String, Object>> getPhoneOrderList(long userid) {
        String sql = " SELECT dpo.id,di.doc_name docname, cd.value, dpo.visitcategory, dpo.create_time createtime, dpo.status, dpo.doctorid, dpo.userid " +
                " FROM doctor_phone_order dpo " +
                "       left join doctor_info di on ifnull(di.delflag, 0) = 0 and di.doctorid = dpo.doctorid " +
                "       left join code_department cd on ifnull(cd.delflag, 0) = 0 and cd.id = di.department_id " +
                " where ifnull(dpo.delflag, 0) = 0  and dpo.status = 4  " +
                "  and dpo.userid = ? ";
        List<Map<String, Object>> list = queryForList(sql, userid);
        for (Map<String, Object> map : list) {
            map.put("statusname", PhoneOrderStateEnum.getValue(ModelUtil.getInt(map, "status")).getMessage());
            map.put("visitcategoryname", VisitCategoryEnum.getValue(ModelUtil.getInt(map, "visitcategory")).getMessage());
        }
        return list;
    }

    /**
     * 电话详情
     *
     * @param orderid
     * @return
     */
    public Map<String, Object> getPhoneOrderListId(long orderid) {
        String sql = " select dpo.id, " +
                "       dpo.create_time                                  createtime, " +
                "       concat_ws(' ', di.doc_name, cd.value, cdt.value) attendingdoctor, " +
                "       complaints, " +
                "       diagnosis, " +
                "       dpo.disease_time                                 diseasetime, " +
                "       dpo.gohospital, " +
                "       dpo.issuredis, " +
                "       dpo.dis_describe                                 disdescribe, " +
                "       remark " +
                " from doctor_phone_order dpo " +
                "       left join doctor_info di on ifnull(di.delflag, 0) = 0 AND di.doctorid = dpo.doctorid " +
                "       left join code_department cd on ifnull(cd.delflag, 0) = 0 and cd.id = di.department_id " +
                "       left join code_doctor_title cdt on ifnull(cdt.delflag, 0) = 0 and cdt.id = di.title_id " +
                " where ifnull(dpo.delflag, 0) = 0 and dpo.id = ? ";
        return queryForMap(sql, orderid);
    }


    /**
     * 视频
     *
     * @param userid
     * @return
     */
    public List<Map<String, Object>> getVideoOrderList(long userid) {
        String sql = " SELECT dvo.id,di.doc_name docname, cd.value, dvo.visitcategory, dvo.create_time createtime, dvo.status, dvo.doctorid, dvo.userid " +
                " FROM doctor_video_order dvo " +
                "       left join doctor_info di on ifnull(di.delflag, 0) = 0 and di.doctorid = dvo.doctorid " +
                "       left join code_department cd on ifnull(cd.delflag, 0) = 0 and cd.id = di.department_id " +
                " where ifnull(dvo.delflag, 0) = 0  and dvo.status =4  " +
                "  and dvo.userid = ? ";
        List<Map<String, Object>> list = queryForList(sql, userid);
        for (Map<String, Object> map : list) {
            map.put("statusname", VideoOrderStateEnum.getValue(ModelUtil.getInt(map, "status")).getMessage());
            map.put("visitcategoryname", VisitCategoryEnum.getValue(ModelUtil.getInt(map, "visitcategory")).getMessage());
        }
        return list;
    }

    /**
     * 视频详情
     *
     * @param orderid
     * @return
     */
    public Map<String, Object> getVideoOrderListId(long orderid) {
        String sql = " select dpo.id, " +
                "       dpo.create_time                                  createtime, " +
                "       concat_ws(' ', di.doc_name, cd.value, cdt.value) attendingdoctor, " +
                "       complaints, " +
                "       guidance as                                      diagnosis, " +
                "       dpo.disease_time                                 diseasetime, " +
                "       dpo.gohospital, " +
                "       dpo.issuredis, " +
                "       dpo.dis_describe                                 disdescribe, " +
                "       remark " +
                " from doctor_video_order dpo " +
                "       left join doctor_info di on ifnull(di.delflag, 0) = 0 AND di.doctorid = dpo.doctorid " +
                "       left join code_department cd on ifnull(cd.delflag, 0) = 0 and cd.id = di.department_id " +
                "       left join code_doctor_title cdt on ifnull(cdt.delflag, 0) = 0 and cdt.id = di.title_id " +
                " where ifnull(dpo.delflag, 0) = 0 and dpo.id = ? ";
        return queryForMap(sql, orderid);
    }

    /**
     * 症状
     *
     * @param orderid
     * @return
     */
    public Map<String, Object> problemOrderDisease(long orderid) {
        String sql = " select orderid, GROUP_CONCAT(diseasename) diseasename " +
                " from middle_answer_disease " +
                " where orderid = ? " +
                " GROUP BY orderid  ";
        return queryForMap(sql, orderid);
    }

    /**
     * 处方
     *
     * @param orderid
     * @return
     */
    public Map<String, Object> problemOrderprescription(long orderid) {
        String sql = " select orderid,pres_photo_url presphotourl " +
                " from doc_prescription " +
                " where orderid = ? ";
        return queryForMap(sql, orderid);
    }

    /**
     * 西药处方   sql拼接
     *
     * @param orderid
     * @return
     */
    public List<Map<String, Object>> problemOrderdrug(long orderid) {
        String sql = " select ddl.id, " +
                "       ddl.prescriptionid, " +
                "       dp.orderid, " +
                "       concat_ws(' ', drug_name,standard_desc) as title, " +       // concat_ws('*', standard_desc, concat(total_dosage, dosage_unit))
                "       concat_ws('，', medicine_freq_name, concat(dosage, dosage_unit, method))               as method, " +
                "       concat(total_dosage, total_dosage_unit)                                               as num " +
                " from doc_drug_list ddl " +
                "       left join doc_prescription dp on dp.prescriptionid = ddl.prescriptionid " +
                " where dp.orderid = ? and ifnull(ddl.delflag,0)=0 ";
        return queryForList(sql, orderid);
    }

    //字段拼接
    public List<Map<String, Object>> problemOrderdrugList(long orderid) {
        String sql = " select ddl.id, " +
                "       ddl.prescriptionid, " +
                "       dp.orderid, " +
                "       drug_name drugname, " +              //药名
                "       standard_desc standarddesc, " +              //0.5g 规格
                "       medicine_freq_name medicinefreqname, " +              //3次/天
                "       dosage, " +              //单数
                "       dosage_unit dosageunit, " +              //单数（单位）
                "       method, " +              //说明
                "       total_dosage totaldosage, " +              //总数
                "       total_dosage_unit totaldosageunit " +              //总数（单位）
                " from doc_drug_list ddl " +
                "       left join doc_prescription dp on dp.prescriptionid = ddl.prescriptionid " +
                " where dp.orderid = ? and ifnull(ddl.delflag,0)=0 ";
        return queryForList(sql, orderid);
    }

    /**
     * 评价
     *
     * @param orderid
     * @return
     */
    public Map<String, Object> problemOrderevaluate(long orderid) {
        String sql = " select id,order_number orderid,evaluate,content from user_evaluate where ifnull(delflag,0)=0 and order_number=? ";
        return queryForMap(sql, orderid);
    }

    /**
     * 修改图文
     *
     * @param orderid
     * @param complaints
     * @param diagnosis
     * @param remark
     * @return
     */
    public boolean updateProblem(long orderid, String complaints, String diagnosis, String remark) {
        String sql = " update doctor_problem_order set complaints=?,diagnosis=?,remark=? where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(complaints);
        params.add(diagnosis);
        params.add(remark);
        params.add(orderid);
        return update(sql, params) > 0;
    }

    /**
     * 修改电话
     *
     * @param orderid
     * @param complaints
     * @param diagnosis
     * @param remark
     * @return
     */
    public boolean updatePhone(long orderid, String complaints, String diagnosis, String remark) {
        String sql = " update doctor_phone_order set complaints=?,diagnosis=?,remark=? where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(complaints);
        params.add(diagnosis);
        params.add(remark);
        params.add(orderid);
        return update(sql, params) > 0;
    }

    /**
     * 修改视频
     *
     * @param orderid
     * @param complaints
     * @param diagnosis
     * @param remark
     * @return
     */
    public boolean updateVideo(long orderid, String complaints, String diagnosis, String remark) {
        String sql = " update doctor_video_order set complaints=?,guidance=?,remark=? where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(complaints);
        params.add(diagnosis);
        params.add(remark);
        params.add(orderid);
        return update(sql, params) > 0;
    }

    /**
     * 修改疾病图文
     *
     * @param orderid
     * @param diseasetime
     * @param gohospital
     * @param issuredis
     * @param disdescribe
     * @return
     */
    public boolean updateSickProblem(long orderid, String diseasetime, int gohospital, int issuredis, String disdescribe) {
        String sql = " update doctor_problem_order set disease_time=?,gohospital=?,issuredis=?,dis_describe=? where id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(diseasetime);
        params.add(gohospital);
        params.add(issuredis);
        params.add(disdescribe);
        params.add(orderid);
        return update(sql, params) > 0;
    }

    /**
     * 修改疾病电话
     *
     * @param orderid
     * @param diseasetime
     * @param gohospital
     * @param issuredis
     * @param disdescribe
     * @return
     */
    public boolean updateSickPhone(long orderid, String diseasetime, int gohospital, int issuredis, String disdescribe) {
        String sql = " update doctor_phone_order set disease_time=?,gohospital=?,issuredis=?,dis_describe=? where id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(diseasetime);
        params.add(gohospital);
        params.add(issuredis);
        params.add(disdescribe);
        params.add(orderid);
        return update(sql, params) > 0;
    }

    /**
     * 修改疾病视频
     *
     * @param orderid
     * @param diseasetime
     * @param gohospital
     * @param issuredis
     * @param disdescribe
     * @return
     */
    public boolean updateSickVideo(long orderid, String diseasetime, int gohospital, int issuredis, String disdescribe) {
        String sql = " update doctor_video_order set disease_time=?,gohospital=?,issuredis=?,dis_describe=? where id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(diseasetime);
        params.add(gohospital);
        params.add(issuredis);
        params.add(disdescribe);
        params.add(orderid);
        return update(sql, params) > 0;
    }

    /**
     * 添加疾病图片
     *
     * @return
     */
    public boolean insertOderPicture(long orderid, String diseasepicture, int ordertype) {
        String sql = " insert into middle_order_picture(orderid,disease_picture,create_time,delflag,order_type) values(?,?,?,?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(orderid);
        params.add(diseasepicture);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(0);
        params.add(ordertype);
        return insert(sql, params) > 0;
    }

    /**
     * 删除疾病图片
     *
     * @param orderid
     * @return
     */
    public boolean delOderPicture(long orderid, int ordertype) {
        String sql = " update middle_order_picture set delflag=1 where orderid=? and order_type=? ";
        List<Object> params = new ArrayList<>();
        params.add(orderid);
        params.add(ordertype);
        return update(sql, params) > 0;
    }

    public List<Map<String, Object>> getSickTimeList() {
        String sql = " SELECT customid id,name FROM basics where ifnull(delflag,0)=0 and type = 25  ";
        return queryForList(sql);
    }


}
