package com.syhdoctor.webserver.service.greenhospital;

import com.syhdoctor.common.utils.EnumUtils.GreenOrderStateEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.TextFixed;
import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.exception.ServiceException;
import com.syhdoctor.webserver.mapper.greenhospital.GreenHospitalMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public abstract class GreenHospitalBaseService extends BaseService {

    @Autowired
    private GreenHospitalMapper greenHospitalMapper;

    public List<Map<String, Object>> getGreenHospitalList(String hospitalname, String hospitalphone, String hospitallevel, Long categoryid, long departmentid, int pageindex, int pagesize) {
        List<Map<String, Object>> list = greenHospitalMapper.getGreenHospitalList(hospitalname, hospitalphone, hospitallevel, categoryid, departmentid, pageindex, pagesize);
        List<Long> ids = new ArrayList<>();
        List<String> areas = new ArrayList<>();
        String[] split = null;
        String areastr = "";
        if (list.size() > 0) {
            for (Map<String, Object> map : list) {
                long id = ModelUtil.getLong(map, "id");
                String area = ModelUtil.getStr(map, "area");
                split = area.split(",");
                areastr = split[split.length - 1];
                ids.add(id);
                areas.add(areastr);
            }
        }
        Map<String, Object> areamap = greenHospitalMapper.getArea(areas);
        Map<Long, Object> m = greenHospitalMapper.category(ids);
        if (list.size() > 0) {
            for (Map<String, Object> map : list) {
                long id = ModelUtil.getLong(map, "id");
                map.put("categoryname", m.get(id));
                String code = ModelUtil.getStr(map, "area");
                split = code.split(",");
                areastr = split[split.length - 1];
                map.put("area", areamap.get(areastr));
            }
        }
        return list;
    }


    public long getGreenHospitalListCount(String hospitalname, String hospitalphone, String hospitallevel, Long categoryid, long departmentid) {
        return greenHospitalMapper.getGreenHospitalListCount(hospitalname, hospitalphone, hospitallevel, categoryid, departmentid);
    }


    public boolean updateAddGreenHospital(long hospitalid, long categoryids, List<Long> departmentids, String hospitalname, String hospitaladdress, String hospitalphone, String hospitalintroduce, int hospitallevelid, List<?> areas, String hospitalpicturebig, String hospitalpicturesmall) {
        Map<String, Object> levelnamemap = greenHospitalMapper.hospitalLevelName(hospitallevelid);
        String hospitallevel = ModelUtil.getStr(levelnamemap, "name");   //医院等级名称
        List<String> areass = new ArrayList<>();
        if (areas.size() > 0) {
            for (Object value : areas) {
                areass.add(value.toString());
            }
        }
        String area = "";
        if (areass.size() > 0) {
            for (String areavalue : areass) {
                area += areavalue + ",";
            }
        }
        area = area.substring(0, area.length() - 1);
        if (hospitalid == 0) {
            long id = greenHospitalMapper.insertGreenHospital(hospitalname, hospitaladdress, hospitalphone, hospitalintroduce, hospitalpicturebig, hospitalpicturesmall, hospitallevel, hospitallevelid, area);
//            if (categoryids.size() > 0) {
//                for (long categoryid : categoryids) {
            greenHospitalMapper.insertMiddleHospitalCategory(id, categoryids);    //添加医院类别中间表
//                }
//            }
            if (departmentids.size() > 0) {
                for (long departmentid : departmentids) {
                    greenHospitalMapper.insertMiddleHospitalDepartment(id, departmentid); //添加科室中间表
                }
            }
        } else {
            greenHospitalMapper.updateGreenHospital(hospitalintroduce, hospitalpicturebig, hospitalpicturesmall, hospitalid, hospitalname, hospitaladdress, hospitalphone, hospitallevel, hospitallevelid, area);
            greenHospitalMapper.delMiddleHospitalCategory(hospitalid);//删除医院类别中间表
            greenHospitalMapper.delMiddleHospitalDepartment(hospitalid);//删除科室中间表
//            if (categoryids.size() > 0) {
//                for (long categoryid : categoryids) {
            greenHospitalMapper.insertMiddleHospitalCategory(hospitalid, categoryids);    //添加医院类别中间表
//                }
//            }
            if (departmentids.size() > 0) {
                for (long departmentid : departmentids) {
                    greenHospitalMapper.insertMiddleHospitalDepartment(hospitalid, departmentid); //添加科室中间表
                }
            }

        }
        return true;
    }

    /**
     * 删除绿通医院
     *
     * @param id
     * @return
     */
    public boolean delGreenHospital(long id) {
        return greenHospitalMapper.delGreenHospital(id);
    }


    /**
     * 查询医院科室
     *
     * @return
     */
    public List<Map<String, Object>> departmentGreen(long hospitalid) {
        return greenHospitalMapper.departmentGreen(hospitalid);
    }
    public List<Map<String, Object>> departmentGreens() {
        return greenHospitalMapper.departmentGreens();
    }

    /**
     * 根据pid查找地区
     * <p>
     * //     * @param code
     *
     * @return
     */
    public List<Map<String, Object>> getAreaByParentId(int code) {
        return greenHospitalMapper.getAreaByParentId(code);
    }

    /**
     * 查询医院类别
     *
     * @return
     */
    public List<Map<String, Object>> hospitalCategory(long hospitalid) {
        return greenHospitalMapper.hospitalCategory(hospitalid);
    }

    /**
     * 医院等级
     *
     * @return
     */
    public List<Map<String, Object>> hospitalLevel(long levelid) {
        return greenHospitalMapper.hospitalLevel(levelid);
    }


    /**
     * 详情
     *
     * @param hospitalid
     * @return
     */
    public Map<String, Object> getGreenHospitalId(long hospitalid) {
        Map<String, Object> map = greenHospitalMapper.getGreenHospitalId(hospitalid);
        List<Map<String, Object>> CategoryList = hospitalCategory(hospitalid);
        map.put("category", CategoryList);
        List<Map<String, Object>> departmentList = departmentGreen(hospitalid);
        map.put("department", departmentList);
        List<Map<String, Object>> hospitalLevelList = hospitalLevel(ModelUtil.getInt(map, "hospitallevelid"));
        map.put("hospitalLevel", hospitalLevelList);

        String strarea[] = ModelUtil.getStr(map, "area").split(",");
        List<String> stringB = Arrays.asList(strarea);
        map.put("areas", stringB);
        return map;
    }


    /*
    绿通订单
     */
    public List<Map<String, Object>> getGreenOrderList(String username, String phone, String patientname, int status, long begintime, long endtime, int pageIndex, int pageSize) {
        return greenHospitalMapper.getGreenOrderList(username, phone, patientname, status, begintime, endtime, pageIndex, pageSize);
    }

    public long getGreenOrderListCount(String username, String phone, String patientname, int status, long begintime, long endtime) {
        return greenHospitalMapper.getGreenOrderListCount(username, phone, patientname, status, begintime, endtime);
    }

    /**
     * 修改状态
     *
     * @param id
     * @param status
     * @return
     */
    public boolean updateStatus(long id, int status, String failreason) {
        if (status == GreenOrderStateEnum.OrderFail.getCode() || status == GreenOrderStateEnum.UnPaid.getCode()) {
            greenHospitalMapper.updateFailReason(id, failreason);    //添加失败原因
        }
        Map<String, Object> map = getGreenOrderId(id);
        long userid = ModelUtil.getLong(map, "userid");
        long doctorid = ModelUtil.getLong(map, "doctorid");
        String content = null;
        boolean a = true;
        int type = 2;
        if (status == 1 || status == 5) {
            status = 5;//交易失败
            content = TextFixed.green_order;
        } else if (status == 3) {//进行中
            a = false;
        } else if (status == 2) {//待接诊
            getGreenInformation(id);    //服务信息是否为空
            content = TextFixed.green_order_x;
            type = 5;
        } else if (status == 4) {//完成
            getGreenInformation(id);    //服务信息是否为空
            content = TextFixed.green_order_over;
        }
        if (a) {
            greenHospitalMapper.insertGreenOrderChat(userid, doctorid, id, content, type);
        }
        return greenHospitalMapper.updateStatus(id, status);
    }


    public void getGreenInformation(long id) {
        Map<String, Object> map = greenHospitalMapper.getGreenInformation(id);
        String greencontact = ModelUtil.getStr(map,"greencontact");
        String greenphone = ModelUtil.getStr(map,"greenphone");
        String subscribetime = ModelUtil.getStr(map,"subscribetime");
        String greenaddress = ModelUtil.getStr(map,"greenaddress");
        log.info("-------------------------------绿通服务信息"+greencontact+greenphone+subscribetime+greenaddress);
        if (StrUtil.isEmpty(greencontact) || StrUtil.isEmpty(greenphone) || StrUtil.isEmpty(subscribetime) || StrUtil.isEmpty(greenaddress)) {
            throw new ServiceException("绿通服务信息未完善");
        }
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
    public boolean updateGreenInformation(long id, String greencontact, String greenphone, String subscribetime, String greenaddress, String introduction, List<?> photos, List<?> pictures) {
        String frontphoto = "";
        String afterphoto = "";
        if (photos.size() > 0) {
            frontphoto = photos.get(0).toString();
            afterphoto = photos.get(1).toString();
        }
        if (pictures.size() > 0) {
            greenHospitalMapper.delMiddleGreenOrderPicture(id);
            for (Object ob : pictures) {
                greenHospitalMapper.addMiddleGreenOrderPicture(id, ob.toString());
            }
        }
        return greenHospitalMapper.updateGreenInformation(id, greencontact, greenphone, subscribetime, greenaddress, introduction, frontphoto, afterphoto);
    }


    /**
     * 订单详情
     *
     * @param id
     * @return
     */
    public Map<String, Object> getGreenOrderId(long id) {
        Map<String, Object> map = greenHospitalMapper.getGreenOrderId(id);
        Map<String, Object> diseasename = greenHospitalMapper.diseaseName(id);  //症状名
        String disease = ModelUtil.getStr(diseasename, "diseasename");
        if(StrUtil.isEmpty(disease)){
            disease="";
        }
        map.put("disease", disease);
        List<Map<String, Object>> picture = greenHospitalMapper.middleGreenOrderPicture(id);//病症照片
        map.put("pictures", picture);

        List<Map<String, Object>> idpicturelist = new ArrayList<>();

        String zhen = ModelUtil.getStr(map, "frontphoto");
        Map<String, Object> photomap = new HashMap<>();  //正
        photomap.put("id", ModelUtil.getStr(map, "id"));
        photomap.put("url", zhen);
        if (!StrUtil.isEmpty(zhen)) {
            idpicturelist.add(photomap);
        }
        String fan = ModelUtil.getStr(map, "afterphoto");
        Map<String, Object> photoma = new HashMap<>();   //反
        photoma.put("id", ModelUtil.getStr(map, "id"));
        photoma.put("url", fan);
        if (!StrUtil.isEmpty(fan)) {
            idpicturelist.add(photoma);
        }

        map.put("photos", idpicturelist);
        return map;
    }


    /*
    医院类别
     */
    public List<Map<String, Object>> getHospitalCategoryList(String categoryname, int pageindex, int pagesize) {
        return greenHospitalMapper.getHospitalCategoryList(categoryname, pageindex, pagesize);
    }

    public long getHospitalCategoryListCount(String categoryname) {
        return greenHospitalMapper.getHospitalCategoryListCount(categoryname);
    }

    public Map<String, Object> getHospitalCategoryId(long id) {
        return greenHospitalMapper.getHospitalCategoryId(id);
    }

    public boolean delHospitalCategory(long id) {
        return greenHospitalMapper.delHospitalCategory(id);
    }

    public boolean updateAddHospitalCategory(long id, String categoryname) {
        if (id == 0) {
            greenHospitalMapper.insertHospitalCategory(categoryname);
        } else {
            greenHospitalMapper.updateHospitalCategory(id, categoryname);
        }
        return true;
    }

    /*
    医院科室
     */
    public List<Map<String, Object>> getDepartmentGreenList(String departmentname, int pageindex, int pagesize) {
        return greenHospitalMapper.getDepartmentGreenList(departmentname, pageindex, pagesize);
    }

    public long getDepartmentGreenListCount(String departmentname) {
        return greenHospitalMapper.getDepartmentGreenListCount(departmentname);
    }

    public Map<String, Object> getDepartmentGreenId(long id) {
        return greenHospitalMapper.getDepartmentGreenId(id);
    }

    public boolean delDepartmentGreen(long id) {
        return greenHospitalMapper.delDepartmentGreen(id);
    }

    public boolean updateAddDepartmentGreen(long id, String departmentname) {
        if (id == 0) {
            greenHospitalMapper.insertDepartmentGreen(departmentname);
        } else {
            greenHospitalMapper.updateDepartmentGreen(id, departmentname);
        }
        return true;
    }


}
