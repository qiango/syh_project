package com.syhdoctor.webserver.mapper.greenhospital;

import com.syhdoctor.common.utils.EnumUtils.GreenOrderStateEnum;
import com.syhdoctor.common.utils.EnumUtils.OrderTypeEnum;
import com.syhdoctor.common.utils.EnumUtils.PayTypeEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GreenHospitalBaseMapper extends BaseMapper {


    /**
     * 绿通医院列表
     *
     * @param hospitalname
     * @param hospitalphone
     * @param hospitallevel
     * @param categoryid
     * @param departmentid
     * @param pageindex
     * @param pagesize
     * @return
     */
    public List<Map<String, Object>> getGreenHospitalList(String hospitalname, String hospitalphone, String hospitallevel, Long categoryid, long departmentid, int pageindex, int pagesize) {
        String sql = " SELECT hg.id, " +
                "       hospital_name    hospitalname, " +
                "       hospital_address hospitaladdress, " +
                "       hospital_phone   hospitalphone, " +
                "       hospital_level   hospitallevel, " +
                "       hospital_introduce   hospitalintroduce, " +
                "       hospital_picture_big   hospitalpicturebig, " +
                "       hospital_picture_small   hospitalpicturesmall, " +
                "       area " +
                " FROM hospital_green hg " +
//                "            left join code_area ca on ca.code = hg.area and ifnull(ca.delflag, 0) = 0 " +
                " WHERE ifnull(hg.delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(hospitalname)) {
            sql += " and hospital_name like ? ";
            params.add(String.format("%%%S%%", hospitalname));
        }
        if (!StrUtil.isEmpty(hospitalphone)) {
            sql += " and hospital_phone like ? ";
            params.add(String.format("%%%S%%", hospitalphone));
        }
        if (!StrUtil.isEmpty(hospitallevel)) {
            sql += " and hospital_level like ? ";
            params.add(String.format("%%%S%%", hospitallevel));
        }
        if (categoryid != 0) {
            sql += " and hg.id in ( " +
                    "    select distinct mhc.hospitalid from middle_hospital_category mhc " +
                    "       left join middle_hospital_department mhd on mhc.hospitalid=mhd.hospitalid and ifnull(mhd.delflag,0)=0 " +
                    "       where ifnull(mhc.delflag,0)=0 and mhc.categoryid = ? ";
            params.add(categoryid);
            if (departmentid != 0) {
                sql += "  and mhd.departmentid = ? ) ";
                params.add(departmentid);
            } else {
                sql += " ) ";
            }
        } else {
            if (departmentid != 0) {
                sql += " and hg.id in ( " +
                        "    select distinct mhc.hospitalid from middle_hospital_category mhc " +
                        "       left join middle_hospital_department mhd on mhc.hospitalid=mhd.hospitalid and ifnull(mhd.delflag,0)=0 " +
                        "       where ifnull(mhc.delflag,0)=0 and mhd.departmentid = ? ) ";
                params.add(departmentid);
            }
        }

        List<Map<String, Object>> list = queryForList(pageSql(sql, " order by hg.id desc "), pageParams(params, pageindex, pagesize));
//        List<Long> hospital = new ArrayList<>();
//        for (Map<String, Object> map1 : list) {
//            long hospitalid = ModelUtil.getLong(map1, "id");
//            hospital.add(hospitalid);
//        }
//        Map<Long, List<Map<String, Object>>> category = userVideoMapper.findCategory(hospital);
////        Map<Long,List<Map<String, Object>>> department = userVideoMapper.findDepartment(hospital);
//        for (Map<String, Object> map2 : list) {
//            long hospitalid = ModelUtil.getLong(map2, "id");
////            map2.put("department",department.get(hospitalid));
//            map2.put("category", category.get(hospitalid));
//        }
        return list;
    }
//
//    /**
//     * 绿通医院详情
//     * @param id
//     * @return
//     */
//    public Map<String,Object> getGreenHospitalId(long id){
//        String sql = " SELECT hg.id, " +
//                "       hospital_name    hospitalname, " +
//                "       hospital_address hospitaladdress, " +
//                "       hospital_phone   hospitalphone, " +
//                "       hospital_introduce   hospitalintroduce, " +
//                "       hospital_picture_big   hospitalpicturebig, " +
//                "       hospital_picture_small   hospitalpicturesmall, " +
//                "       area " +
//                " FROM hospital_green hg where ifnull(delflag,0)=0 and id=? ";
//        return queryForMap(sql,id);
//    }


    /**
     * 列表查询地区
     *
     * @param codes
     * @return
     */
    public Map<String, Object> getArea(List<String> codes) {
        if (codes == null) {
            return null;
        }
        String sql = " select code,value from code_area where ifnull(delflag,0)=0 and code in (:code) ";
        Map<String, Object> params = new HashMap<>();
        params.put("code", codes);
        List<Map<String, Object>> list = queryForList(sql, params);
        Map<String, Object> map = new HashMap<>();
        for (Map<String, Object> maps : list) {
            String code = ModelUtil.getStr(maps, "code");
            String value = ModelUtil.getStr(maps, "value");
            map.put(code, value);
        }
        return map;
    }

    /**
     * 列表中 类别
     *
     * @param hospitalid
     * @return
     */
    public Map<Long, Object> category(List<Long> hospitalid) {
        if (hospitalid == null) {
            return null;
        }
        String sql = " select mh.hospitalid,group_concat(hc.category_name) name " +
                " from middle_hospital_category mh " +
                "       left join hospital_category hc on mh.categoryid = hc.id " +
                " where mh.hospitalid in (:hospitalid) " +
                " group by mh.hospitalid ";
        Map<Long, Object> map = new HashMap<>();
        Map<String, Object> params = new HashMap<>();
        params.put("hospitalid", hospitalid);
        List<Map<String, Object>> list = queryForList(sql, params);
        for (Map<String, Object> map1 : list) {
            long hospitalids = ModelUtil.getLong(map1, "hospitalid");
            map.put(hospitalids, ModelUtil.getStr(map1, "name"));
        }
        return map;
    }


    public long getGreenHospitalListCount(String hospitalname, String hospitalphone, String hospitallevel, Long categoryid, long departmentid) {
        String sql = " select count(hg.id) count " +
                " from hospital_green hg " +
                " where ifnull(hg.delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(hospitalname)) {
            sql += " and hospital_name like ? ";
            params.add(String.format("%%%S%%", hospitalname));
        }
        if (!StrUtil.isEmpty(hospitalphone)) {
            sql += " and hospital_phone like ? ";
            params.add(String.format("%%%S%%", hospitalphone));
        }
        if (!StrUtil.isEmpty(hospitallevel)) {
            sql += " and hospital_level like ? ";
            params.add(String.format("%%%S%%", hospitallevel));
        }
        if (categoryid != 0) {
            sql += " and hg.id in ( " +
                    "    select distinct mhc.hospitalid from middle_hospital_category mhc " +
                    "       left join middle_hospital_department mhd on mhc.hospitalid=mhd.hospitalid and ifnull(mhd.delflag,0)=0  " +
                    "       where ifnull(mhc.delflag,0)=0 and  mhc.categoryid = ? ";
            Map<String, Object> map = new HashMap<>();
            params.add(categoryid);
            if (departmentid != 0) {
                sql += "  and mhd.departmentid = ? ) ";
                params.add(departmentid);
            } else {
                sql += " ) ";
            }
        } else {
            if (departmentid != 0) {
                sql += " and hg.id in ( " +
                        "    select distinct mhc.hospitalid from middle_hospital_category mhc " +
                        "       left join middle_hospital_department mhd on mhc.hospitalid=mhd.hospitalid and ifnull(mhd.delflag,0)=0  " +
                        "       where ifnull(mhc.delflag,0)=0 and  mhd.departmentid = ? ) ";
                params.add(departmentid);
            }
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    /**
     * 新增医院
     *
     * @param hospitalname
     * @param hospitaladdress
     * @param hospitalphone
     * @param hospitalintroduce
     * @param hospitalpicturebig
     * @param hospitalpicturesmall
     * @param hospitallevel
     * @return
     */
    public long insertGreenHospital(String hospitalname, String hospitaladdress, String hospitalphone, String hospitalintroduce, String hospitalpicturebig, String hospitalpicturesmall, String hospitallevel, int hospitallevelid, String area) {
        String sql = " INSERT INTO hospital_green ( hospital_name, hospital_address, hospital_phone, hospital_introduce, hospital_picture_big,hospital_picture_small, create_time, hospital_level,area,hospital_level_id ) " +
                " VALUES(?,?,?,?,?,?,?,?,?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(hospitalname);
        params.add(hospitaladdress);
        params.add(hospitalphone);
        params.add(hospitalintroduce);
        params.add(hospitalpicturebig);
        params.add(hospitalpicturesmall);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(hospitallevel);
        params.add(area);
        params.add(hospitallevelid);
        return insert(sql, params, "id");
    }


    /**
     * 修改绿通医院
     *
     * @param hospitalid
     * @param hospitalname
     * @param hospitaladdress
     * @param hospitalphone
     * @param hospitallevelid
     * @return
     */
    public boolean updateGreenHospital(String hospitalintroduce, String hospitalpicturebig, String hospitalpicturesmall, long hospitalid, String hospitalname, String hospitaladdress, String hospitalphone, String hospitallevel, int hospitallevelid, String area) {
        String sql = " UPDATE hospital_green  " +
                " SET hospital_name = ?, " +
                " hospital_address = ?, " +
                " hospital_phone = ?, " +
                " hospital_introduce = ?, " +
                " area = ?, " +
                " hospital_level = ?, " +
                " hospital_level_id = ?,  " +
                " hospital_picture_big = ?,  " +
                " hospital_picture_small = ?  " +
                " WHERE " +
                " id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(hospitalname);
        params.add(hospitaladdress);
        params.add(hospitalphone);
        params.add(hospitalintroduce);
        params.add(area);
        params.add(hospitallevel);
        params.add(hospitallevelid);
        params.add(hospitalpicturebig);
        params.add(hospitalpicturesmall);
        params.add(hospitalid);
        return update(sql, params) > 0;
    }


    /**
     * 新增医院类别中间表
     *
     * @param hospitalid
     * @param categoryid
     * @return
     */
    public boolean insertMiddleHospitalCategory(long hospitalid, long categoryid) {
        String sql = " insert into middle_hospital_category(hospitalid,categoryid,create_time) values(?,?,?)  ";
        List<Object> params = new ArrayList<>();
        params.add(hospitalid);
        params.add(categoryid);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params) > 0;
    }

    /**
     * 删除医院类别中间表
     *
     * @param hospitalid
     * @return
     */
    public boolean delMiddleHospitalCategory(long hospitalid) {
        String sql = " delete from middle_hospital_category where hospitalid = ? ";
        return update(sql, hospitalid) > 0;
    }


    /**
     * 新增科室中间表
     *
     * @param hospitalid
     * @param departmentid
     * @return
     */
    public boolean insertMiddleHospitalDepartment(long hospitalid, long departmentid) {
        String sql = " insert into middle_hospital_department(hospitalid,departmentid,create_time) values(?,?,?)  ";
        List<Object> params = new ArrayList<>();
        params.add(hospitalid);
        params.add(departmentid);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params) > 0;
    }

    /**
     * 删除科室中间表
     *
     * @param hospitalid
     * @return
     */
    public boolean delMiddleHospitalDepartment(long hospitalid) {
        String sql = " delete from middle_hospital_department where hospitalid = ? ";
        return update(sql, hospitalid) > 0;
    }


    /**
     * 查询医院科室
     *
     * @return
     */
    public List<Map<String, Object>> departmentGreen(long hospitalid) {
        String sql = " select distinct dg.id,department_name name " +
                " from department_green dg " +
                " left join middle_hospital_department mhd on mhd.departmentid = dg.id and IFNULL(mhd.delflag, 0) = 0 " +
                " where IFNULL(dg.delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        if (hospitalid != 0) {
            sql += " and hospitalid = ? ";
            params.add(hospitalid);
        }
        return queryForList(sql, params);
    }

    public List<Map<String, Object>> departmentGreens() {
        String sql = " select dg.id,department_name name " +
                " from department_green dg " +
                " where IFNULL(dg.delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        return queryForList(sql, params);
    }


    /**
     * 根据pid查找地区
     *
     * @param code
     * @return
     */
    public List<Map<String, Object>> getAreaByParentId(int code) {
        String sql = " select code id,value from code_area where ifnull(delflag,0)=0 and parentid=? ";
        return queryForList(sql, code);
    }


    /**
     * 查询医院类别
     *
     * @return
     */
    public List<Map<String, Object>> hospitalCategory(long hospitalid) {
        String sql = " select distinct hc.id, category_name name " +
                " from hospital_category hc " +
                "            left join middle_hospital_category mhc on mhc.categoryid = hc.id and IFNULL(mhc.delflag, 0) = 0 " +
                " where IFNULL(hc.delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (hospitalid != 0) {
            sql += " and hospitalid = ? ";
            params.add(hospitalid);
        }
        return queryForList(sql, params);
    }

    /**
     * 医院等级
     *
     * @return
     */
    public List<Map<String, Object>> hospitalLevel(long levelid) {
        String sql = " select customid id,name from basics where type=9 and ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        if (levelid != 0) {
            sql += " and customid = ? ";
            params.add(levelid);
        }
        return queryForList(sql, params);
    }

    public Map<String, Object> hospitalLevelName(long levelid) {
        String sql = " select customid id,name from basics where type=9 and ifnull(delflag,0)=0 and customid = ? ";
        return queryForMap(sql, levelid);
    }


    /**
     * 删除医院
     *
     * @param id
     * @return
     */
    public boolean delGreenHospital(long id) {
        String sql = " update hospital_green set delflag=1 where id=? ";
        return update(sql, id) > 0;
    }


    /**
     * 详情
     *
     * @param hospitalid
     * @return
     */
    public Map<String, Object> getGreenHospitalId(long hospitalid) {
        String sql = " SELECT hg.id, " +
                "       hospital_name    hospitalname, " +
                "       hospital_address hospitaladdress, " +
                "       hospital_phone   hospitalphone, " +
                "       hospital_level   hospitallevel, " +
                "       hospital_level_id   hospitallevelid, " +
                "       hospital_picture_big   hospitalpicturebig, " +
                "       hospital_picture_small   hospitalpicturesmall, " +
                "       hospital_introduce   hospitalintroduce, " +
                "       area " +
                " FROM hospital_green hg " +
                " WHERE ifnull(hg.delflag, 0) = 0 " +
                "  and hg.id = ? ";
        return queryForMap(sql, hospitalid);
    }




    /*
    绿通订单
     */

    public List<Map<String, Object>> getGreenOrderList(String username, String phone, String patientname, int status, long begintime, long endtime, int pageIndex, int pageSize) {
        String sql = " select go.id,orderno,visitcategory,ua.name,ua.phone,up.patient_name,actualmoney,go.create_time,status,failreason " +
                " from green_order go " +
                "            left join user_account ua on ua.id = go.userid and ifnull(ua.delflag,0)=0 " +
                "            left join user_patient up on up.id = go.patientid and ifnull(up.delflag,0)=0 " +
                " where ifnull(go.delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(username)) {
            sql += " and ua.name like ? ";
            params.add(String.format("%%%S%%", username));
        }
        if (!StrUtil.isEmpty(phone)) {
            sql += " and ua.phone like ? ";
            params.add(String.format("%%%S%%", phone));
        }
        if (!StrUtil.isEmpty(patientname)) {
            sql += " and up.patient_name like ? ";
            params.add(String.format("%%%S%%", patientname));
        }
        if (status != 0) {
            sql += " and status = ? ";
            params.add(status);
        }
        if (begintime != 0) {
            sql += " and go.create_time > ?  ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and go.create_time < ? ";
            params.add(endtime);
        }
//        return queryForList(pageSql(sql," order by go.id desc "),pageParams(params,pageIndex,pageSize));
        return query(pageSql(sql, " order by FIELD(status,1,6,2,3,4,5),go.id desc "), pageParams(params, pageIndex, pageSize), (ResultSet re, int num) -> {
            Map<String, Object> map = resultToMap(re);
            if (map != null) {
                map.put("statusname", GreenOrderStateEnum.getValue(ModelUtil.getInt(map, "status")).getMessage());
            }
            return map;
        });
    }

    public long getGreenOrderListCount(String username, String phone, String patientname, int status, long begintime, long endtime) {
        String sql = " select count(go.id) count " +
                " from green_order go " +
                "            left join user_account ua on ua.id = go.userid and ifnull(ua.delflag,0)=0 " +
                "            left join user_patient up on up.id = go.patientid and ifnull(up.delflag,0)=0 " +
                " where ifnull(go.delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(username)) {
            sql += " and ua.name like ? ";
            params.add(String.format("%%%S%%", username));
        }
        if (!StrUtil.isEmpty(phone)) {
            sql += " and ua.phone like ? ";
            params.add(String.format("%%%S%%", phone));
        }
        if (!StrUtil.isEmpty(patientname)) {
            sql += " and up.patient_name like ? ";
            params.add(String.format("%%%S%%", patientname));
        }
        if (status != 0) {
            sql += " and status = ? ";
            params.add(status);
        }
        if (begintime != 0) {
            sql += " and go.create_time > ?  ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and go.create_time < ? ";
            params.add(endtime);
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }


    /**
     * 修改状态
     *
     * @param id
     * @param status
     * @return
     */
    public boolean updateStatus(long id, int status) {
        String sql = " update green_order set status=? where id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(status);
        params.add(id);
        return update(sql, params) > 0;
    }

    /**
     * 添加交易失败原因
     *
     * @param id
     * @param failreason
     * @return
     */
    public boolean updateFailReason(long id, String failreason) {
        String sql = " update green_order set failreason=? where id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(failreason);
        params.add(id);
        return update(sql, params) > 0;
    }

    /**
     * 添加就诊信息
     *
     * @param id
     * @param greencontact
     * @param greenphone
     * @param subscribetime
     * @param greenaddress
     * @return
     */
    public boolean updateGreenInformation(long id, String greencontact, String greenphone, String subscribetime, String greenaddress, String introduction, String frontphoto, String afterphoto) {
        String sql = " update green_order  " +
                " set green_contact  = ?, " +
                "    green_phone    = ?, " +
                "    subscribe_time = ?, " +
                "    green_address  = ?, " +
                "    introduction  = ?, " +
                "    front_photo = ?, " +
                "    after_photo = ? " +
                "where id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(greencontact);
        params.add(greenphone);
        params.add(subscribetime);
        params.add(greenaddress);
        params.add(introduction);
        params.add(frontphoto);
        params.add(afterphoto);
        params.add(id);
        return update(sql, params) > 0;
    }

    public long insertGreenInformation(String greencontact, String greenphone, String subscribetime, String greenaddress, String introduction, String frontphoto, String afterphoto) {
        String sql = "  insert into green_order (green_contact, " +
                "                         green_phone, " +
                "                         subscribe_time, " +
                "                         green_address, " +
                "                         introduction, " +
                "                         front_photo, " +
                "                         after_photo) " +
                " values (?,?,?,?,?,?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(greencontact);
        params.add(greenphone);
        params.add(subscribetime);
        params.add(greenaddress);
        params.add(introduction);
        params.add(frontphoto);
        params.add(afterphoto);
        return insert(sql, params, "id");
    }


    /**
     * 推送信息
     * <p>
     * //     * @param content
     *
     * @param userid
     * @param doctorid
     * @param orderid
     * @return
     */
    public boolean insertGreenOrderChat(long userid, long doctorid, long orderid, String content, int type) {
        String sql = " insert into green_order_chat(userid,content,doctorid,orderid,contenttype,questionanswertype,delflag,create_time,status) " +
                " values (?,?,?,?,?,?,?,?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(userid);
        params.add(content);
        params.add(doctorid);
        params.add(orderid);
        params.add(type);
        params.add(1);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(1);
        return insert(sql, params) > 0;
    }

    /**
     * 是否存在就诊信息
     *
     * @param id
     * @return
     */
    public Map<String, Object> getGreenInformation(long id) {
        String sql = " select green_contact greencontact,green_phone greenphone,subscribe_time subscribetime,green_address greenaddress from green_order where ifnull(delflag,0)=0 and id =?  ";
        return queryForMap(sql, id);
    }


    /**
     * 订单详情
     *
     * @param id
     * @return
     */
    public Map<String, Object> getGreenOrderId(long id) {
        String sql = " select go.id, " +
                "       orderno, " +
                "       go.paytype, " +
                "       disease_time diseasetime, " +
                "       gohospital, " +
                "       diagnosis, " +
                "       green_address greenaddress, " +
                "       go.introduction, " +
                "       go.front_photo frontphoto, " +
                "       go.after_photo afterphoto, " +
                "       go.userid, " +
                "       go.doctorid, " +
                "       hg.hospital_name hospitalname, " +
                "       dg.department_name departmentname, " +
                "       appointment_type appointmenttype, " +
                "       go.create_time createtime, " +
                "       appointment_type appointmenttype, " +
                "       is_ordinary isordinary, " +
                "       is_expert isexpert, " +
                "       is_urgent isurgent, " +
                "       status, " +
                "       green_contact greencontact, " +
                "       green_phone greenphone, " +
                "       hg.hospital_address hospitaladdress, " +
                "       subscribe_time subscribetime, " +
                "       ua.name, " +
                "       ua.phone, " +
                "       ua.age, " +
                "       ua.gender, " +
                "       up.patient_name patientname, " +
                "       up.patient_phone patientphone, " +
                "       up.patient_age patientage, " +
                "       up.patient_gender patientgender " +
                " from green_order go " +
                "       left join hospital_green hg on hg.id = go.hospital_greenid and ifnull(hg.delflag, 0) = 0 " +
                "       left join department_green dg on dg.id = go.green_departmentid and ifnull(dg.delflag, 0) = 0 " +
                "       left join user_account ua on ua.id = go.userid and ifnull(ua.delflag, 0) = 0 " +
                "       left join user_patient up on up.id = go.patientid and ifnull(up.delflag, 0) = 0  " +
                "       where ifnull(go.delflag, 0) = 0 and go.id = ? ";
        Map<String, Object> map = queryForMap(sql, id);
        if (map != null) {
            map.put("paytype", PayTypeEnum.getValue(ModelUtil.getInt(map, "paytype")).getMessage());
        }
        return map;
    }

    /**
     * 病症照片
     *
     * @param id
     * @return
     */
    public List<Map<String, Object>> middleGreenOrderPicture(long id) {
        String sql = " select orderid id,disease_picture url from middle_order_picture where ifnull(delflag,0)=0 and orderid = ? and order_type=? ";
        return queryForList(sql, id, OrderTypeEnum.Green.getCode());
    }

    /**
     * 添加病症照片
     *
     * @param greenorderid
     * @param diseasepicture
     * @return
     */
    public boolean addMiddleGreenOrderPicture(long greenorderid, String diseasepicture) {
        String sql = " insert into middle_order_picture(orderid,disease_picture,create_time,order_type) values (?,?,?,?)   ";
        List<Object> params = new ArrayList<>();
        params.add(greenorderid);
        params.add(diseasepicture);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(OrderTypeEnum.Green.getCode());
        return insert(sql, params) > 0;
    }

    public boolean delMiddleGreenOrderPicture(long greenorderid) {
        String sql = " update middle_order_picture set delflag=1 where orderid=? and order_type=? ";
        return update(sql, greenorderid, OrderTypeEnum.Green.getCode()) > 0;
    }


    public Map<String, Object> diseaseName(long orderid) {
        String sql = " select group_concat(diseasename) diseasename from middle_green_disease where orderid = ? ";
        return queryForMap(sql, orderid);
    }


    /**
     * 订单详情导出
     *
     * @param id
     * @return
     */
    public Map<String, Object> getGreenOrderIdExport(long id) {
        String sql = " select go.id, " +
                "       orderno, " +
                "       hg.hospital_name hospitalname, " +
                "       dg.department_name departmentname, " +
                "       appointment_type appointmenttype, " +
                "       go.create_time createtime, " +
                "       appointment_type appointmenttype, " +
                "       is_ordinary isordinary, " +
                "       is_expert isexpert, " +
                "       is_urgent isurgent, " +
                "       status, " +
                "       green_contact greencontact, " +
                "       green_phone greenphone, " +
                "       hg.hospital_address hospitaladdress, " +
                "       subscribe_time subscribetime, " +
                "       ua.name, " +
                "       ua.phone, " +
                "       ua.age, " +
                "       ua.gender, " +
                "       up.patient_name patientname, " +
                "       up.patient_phone patientphone, " +
                "       up.patient_age patientage, " +
                "       up.patient_gender patientgender " +
                " from green_order go " +
                "       left join hospital_green hg on hg.id = go.hospital_greenid and ifnull(hg.delflag, 0) = 0 " +
                "       left join department_green dg on dg.id = go.green_departmentid and ifnull(dg.delflag, 0) = 0 " +
                "       left join user_account ua on ua.id = go.userid and ifnull(ua.delflag, 0) = 0 " +
                "       left join user_patient up on up.id = go.patientid and ifnull(up.delflag, 0) = 0  " +
                "       where ifnull(go.delflag, 0) = 0 and go.id = ? ";
        Map<String, Object> map = queryForMap(sql, id);
        if (map != null) {
            map.put("appointmenttype", GreenOrderStateEnum.getValue(ModelUtil.getInt(map, "appointmenttype")).getMessage());
            map.put("createtime", UnixUtil.getDate(ModelUtil.getLong(map, "createtime"), "yyyy-MM-dd HH:mm:ss"));

        }
        return map;
    }


    /*
    医院类别
     */

    public List<Map<String, Object>> getHospitalCategoryList(String categoryname, int pageindex, int pagesize) {
        String sql = " SELECT id,category_name categoryname,create_time FROM hospital_category where ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(categoryname)) {
            sql += " and category_name like ? ";
            params.add(String.format("%%%S%%", categoryname));
        }
        return queryForList(pageSql(sql, " order by id desc "), pageParams(params, pageindex, pagesize));
    }

    public long getHospitalCategoryListCount(String categoryname) {
        String sql = " SELECT count(id) FROM hospital_category where ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(categoryname)) {
            sql += " and category_name like ? ";
            params.add(String.format("%%%S%%", categoryname));
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    public Map<String, Object> getHospitalCategoryId(long id) {
        String sql = " SELECT id,category_name categoryname  FROM hospital_category where ifnull(delflag,0)=0 and id =? ";
        return queryForMap(sql, id);
    }

    public boolean delHospitalCategory(long id) {
        String sql = " update hospital_category set delflag=1 where id =? ";
        return update(sql, id) > 0;
    }

    public boolean updateHospitalCategory(long id, String categoryname) {
        String sql = " update hospital_category set category_name=? where id =? ";
        List<Object> params = new ArrayList<>();
        params.add(categoryname);
        params.add(id);
        return update(sql, params) > 0;
    }

    public boolean insertHospitalCategory(String categoryname) {
        String sql = " insert into hospital_category(category_name,create_time) values (?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(categoryname);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params) > 0;
    }




     /*
    医院科室
     */

    public List<Map<String, Object>> getDepartmentGreenList(String departmentname, int pageindex, int pagesize) {
        String sql = " SELECT id,department_name departmentname,create_time FROM department_green where ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(departmentname)) {
            sql += " and department_name like ? ";
            params.add(String.format("%%%S%%", departmentname));
        }
        return queryForList(pageSql(sql, " order by id desc "), pageParams(params, pageindex, pagesize));
    }

    public long getDepartmentGreenListCount(String departmentname) {
        String sql = " SELECT count(id) FROM department_green where ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(departmentname)) {
            sql += " and category_name like ? ";
            params.add(String.format("%%%S%%", departmentname));
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    public Map<String, Object> getDepartmentGreenId(long id) {
        String sql = " SELECT id,department_name departmentname  FROM department_green where ifnull(delflag,0)=0 and id =? ";
        return queryForMap(sql, id);
    }

    public boolean delDepartmentGreen(long id) {
        String sql = " update department_green set delflag=1 where id =? ";
        return update(sql, id) > 0;
    }

    public boolean updateDepartmentGreen(long id, String departmentname) {
        String sql = " update department_green set department_name=? where id =? ";
        List<Object> params = new ArrayList<>();
        params.add(departmentname);
        params.add(id);
        return update(sql, params) > 0;
    }

    public boolean insertDepartmentGreen(String departmentname) {
        String sql = " insert into department_green(department_name,create_time) values (?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(departmentname);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params) > 0;
    }


}
