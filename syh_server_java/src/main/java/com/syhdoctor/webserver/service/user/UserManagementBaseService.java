package com.syhdoctor.webserver.service.user;

import com.syhdoctor.common.utils.EnumUtils.OrderTypeEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.mapper.user.UserManagementMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public abstract class UserManagementBaseService extends BaseService {

    @Autowired
    private UserManagementMapper userManagementMapper;


    public Map<String, Object> getUserId(long userid) {
        Map<String, Object> map = userManagementMapper.getUserId(userid);
        List<Map<String, Object>> list = userManagementMapper.isVip(userid);
        int isvip = 0;// 0 非会员，1 会员
        if (list != null && list.size() > 0) {
            isvip = 1;
        }
        map.put("isvip", isvip);
        return map;
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
        return userManagementMapper.getUserAccountId(id);
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
        return userManagementMapper.transactionRecordList(userid, pageIndex, pageSize);
    }

    public long transactionRecordListCount(long userid) {
        return userManagementMapper.transactionRecordListCount(userid);
    }

    /**
     * 积分记录
     *
     * @param userid
     * @return
     */
    public List<Map<String, Object>> userIntegralList(long userid, int pageIndex, int pageSize) {
        return userManagementMapper.userIntegralList(userid, pageIndex, pageSize);
    }



    /*
    会员信息
     */

    public Map<String, Object> getUserMember(long userid) {
        Map<String, Object> map = new HashMap<>();
        map.put("baseinfo", userManagementMapper.getUserMember(userid));
        map.put("vipinfo", userManagementMapper.getVipInfo(userid));
        return map;
    }

     /*
        会员信息
     */

    public Map<String, Object> getUserMemberWeb(long userid) {
        return userManagementMapper.getVipInfoWeb(userid);
    }

    public Long userIntegralListCount(long userid) {
        return userManagementMapper.userIntegralListCount(userid);
    }


    //修改加载
    public Map<String, Object> getBasicList(long userid) {
        Map<String, Object> map = new HashMap<>();
        map.put("isallergy", userManagementMapper.getBasicList(10));//过敏
        map.put("ischronicillness", userManagementMapper.getBasicList(11));//慢性病史
        map.put("isfamilyhistory", userManagementMapper.getBasicList(12));//有无家族史
        map.put("menstrualcycle", userManagementMapper.getBasicList(15));//月经周期
        map.put("menstrualday", userManagementMapper.getBasicList(16));//经期天数
        map.put("info", userManagementMapper.getUserAccount(userid));//其他信息
        map.put("haveselect", userManagementMapper.getDiseaseList(userid));//已选病例
        map.put("getAccount", userManagementMapper.getAccounts(userid));//基本信息
        return map;
    }

    //修改加载web
    public Map<String, Object> getBasicListWeb(long userid) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> isallergy = userManagementMapper.getBasicList(10);
        List<Map<String, Object>> ischronicillness = userManagementMapper.getBasicList(11);
        List<Map<String, Object>> isfamilyhistory = userManagementMapper.getBasicList(12);
        List<Map<String, Object>> menstrualcycle = userManagementMapper.getBasicList(15);
        List<Map<String, Object>> menstrualday = userManagementMapper.getBasicList(16);
        for (Map<String, Object> maps : isallergy) {
            long id = ModelUtil.getLong(maps, "id");
            boolean flag = userManagementMapper.getDiseaseListBoolean(userid, id);
            maps.put("selected", flag);
        }
        for (Map<String, Object> maps : ischronicillness) {
            long id = ModelUtil.getLong(maps, "id");
            boolean flag = userManagementMapper.getDiseaseListBoolean(userid, id);
            maps.put("selected", flag);
        }
        for (Map<String, Object> maps : isfamilyhistory) {
            long id = ModelUtil.getLong(maps, "id");
            boolean flag = userManagementMapper.getDiseaseListBoolean(userid, id);
            maps.put("selected", flag);
        }
        for (Map<String, Object> maps : menstrualcycle) {
            long id = ModelUtil.getLong(maps, "id");
            boolean flag = userManagementMapper.getDiseaseListBoolean(userid, id);
            maps.put("selected", flag);
        }
        for (Map<String, Object> maps : menstrualday) {
            long id = ModelUtil.getLong(maps, "id");
            boolean flag = userManagementMapper.getDiseaseListBoolean(userid, id);
            maps.put("selected", flag);
        }
        map.put("isallergy", isallergy);//过敏
        map.put("ischronicillness", ischronicillness);//慢性病史
        map.put("isfamilyhistory", isfamilyhistory);//有无家族史
        map.put("menstrualcycle", menstrualcycle);//月经周期
        map.put("menstrualday", menstrualday);//经期天数
        map.put("info", userManagementMapper.getUserAccount(userid));//其他信息
        map.put("getAccount", userManagementMapper.getAccounts(userid));//基本信息
        map.put("gender", ModelUtil.getInt(userManagementMapper.getUserGender(userid),"gender"));//基本信息
        return map;
    }

    //查看
    public Map<String, Object> getBasic(long userid) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> diseaseList = userManagementMapper.getDiseaseList(userid);
        map.put("info", userManagementMapper.getUserAccount(userid));//其他信息
        Map<String, Object> maps = new HashMap<>();
        for (Map<String, Object> m : diseaseList) {
            String name = ModelUtil.getStr(m, "name");
            int type = ModelUtil.getInt(m, "type");
            updateMap(maps, name, type);
        }
        map.put("haveselect", maps);//已选病例
        map.put("getAccount", userManagementMapper.getAccount(userid));//基本信息
        return map;
    }

    //APP查看
    public Map<String, Object> getBasicApp(long userid) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> diseaseList = userManagementMapper.getDiseaseList(userid);
        Map<String, Object> userAccount = userManagementMapper.getUserAccount(userid);
        Map<String, Object> userInfo = userManagementMapper.getUserInfo(userid);
        if (userAccount != null) {
            int pregnancy = ModelUtil.getInt(userAccount, "pregnancy");
            int childbirth = ModelUtil.getInt(userAccount, "childbirth");
            int abortion = ModelUtil.getInt(userAccount, "abortion");
            String childbearing = "";
            if (pregnancy != 0) {
                childbearing = "妊娠" + pregnancy + "次";
            }
            if (childbirth != 0) {
                if (StringUtils.isNotEmpty(childbearing)) {
                    childbearing += "、分娩" + childbirth + "次";
                } else {
                    childbearing += "分娩" + childbirth + "次";
                }
            }
            if (abortion != 0) {
                if (StringUtils.isNotEmpty(childbearing)) {
                    childbearing += "、流产" + abortion + "次";
                } else {
                    childbearing += "流产" + abortion + "次";
                }
            }
            userAccount.put("childbearing", childbearing);
        }
        map.put("info", userAccount);//其他信息
        map.put("userInfo", userInfo);
        Map<String, Object> maps = new HashMap<>();
        for (Map<String, Object> m : diseaseList) {
            String name = ModelUtil.getStr(m, "name");
            int type = ModelUtil.getInt(m, "type");
            updateMap(maps, name, type);
        }
        map.put("haveselect", maps);//已选病例
        map.put("getAccount", userManagementMapper.getAccountApp(userid));//基本信息
        return map;
    }

    public void updateMap(Map<String, Object> maps, String name, int type) {
        if (type == 10) {
            if (maps.containsKey("isallergyvalues")) {
                String finl = maps.get("isallergyvalues") + "、" + name;
                maps.put("isallergyvalues", finl);
            } else {
                maps.put("isallergyvalues", name);
            }
        }
        if (type == 11) {
            if (maps.containsKey("ischronicillness")) {
                String finl = maps.get("ischronicillness") + "、" + name;
                maps.put("ischronicillness", finl);
            } else {
                maps.put("ischronicillness", name);
            }
        }
        if (type == 12) {
            if (maps.containsKey("isfamilyhistory")) {
                String finl = maps.get("isfamilyhistory") + "、" + name;
                maps.put("isfamilyhistory", finl);
            } else {
                maps.put("isfamilyhistory", name);
            }
        }
        if (type == 15) {
            if (maps.containsKey("menstrualcycle")) {
                String finl = maps.get("menstrualcycle") + "、" + name;
                maps.put("menstrualcycle", finl);
            } else {
                maps.put("menstrualcycle", name);
            }
        }
        if (type == 16) {
            if (maps.containsKey("menstrualday")) {
                String finl = maps.get("menstrualday") + "、" + name;
                maps.put("menstrualday", finl);
            } else {
                maps.put("menstrualday", name);
            }
        }
    }

    public boolean updateAccount(long userid, int weight, int height, Object ismarry) {
        int b = height * height;
        float c = (float) b / 10000;
        double bmi = weight / c;
        List<Map<String, Object>> list = userManagementMapper.selectIsUser(userid); //查询健康表的用户
        if (list.size() > 0) {
            userManagementMapper.updateinfo(userid, weight, height, ismarry, bmi);
        } else {
            userManagementMapper.insertInfo(userid, weight, height, ismarry, bmi);
        }
        return true;
    }

    public boolean updateAccountWeb(long userid, Object weight, Object height, Object ismarry) {
        Object bmi = null;
        if (null != weight && null != height) {
            if (StrUtil.isNotEmpty(weight.toString(), height.toString())) {
                int h = Integer.parseInt(height.toString());
                int w = Integer.parseInt(weight.toString());
                int b = h * h;
                float c = (float) b / 10000;
                bmi = (double) w / c;
            }
        }
        List<Map<String, Object>> list = userManagementMapper.selectIsUser(userid); //查询健康表的用户
        if (list.size() > 0) {
            userManagementMapper.updateinfoWeb(userid, weight, height, ismarry, bmi);
        } else {
            userManagementMapper.insertInfoWeb(userid, weight, height, ismarry, bmi);
        }
        return true;
    }

    public boolean updateUserCase(long userid, Object isallergy, Object ischronicillness, String ischronOther, Object issurgery, String issurgeryOther,
                                  String isallergyOther, Object isfamilyhistory, String familyOther, Object issmoking, Object isdrinking, Object isfertility,
                                  Object pregnancy, Object childbirth, Object abortion, Object menarche_age, long final_menarche, Object ismenopause, List<?> values) {
        userManagementMapper.updateDieas(userid);
        userManagementMapper.updateMiddle(userid);
        userManagementMapper.insertDisease(userid, isallergy, ischronicillness, ischronOther, issurgery, issurgeryOther, isallergyOther, isfamilyhistory, familyOther, issmoking, isdrinking, isfertility, pregnancy, childbirth, abortion, menarche_age, final_menarche, ismenopause);
        for (Object b : values) {
            long val = Long.parseLong(b.toString());
            userManagementMapper.insertMiddleDisease(userid, val);
        }
        return true;
    }


    public boolean updateUserCaseApp(long userid, Object weight, Object height, Object ismarry, Object isallergy, Object ischronicillness, String ischronOther, Object issurgery, String issurgeryOther,
                                     String isallergyOther, Object isfamilyhistory, String familyOther, Object issmoking, Object isdrinking, Object isfertility,
                                     Object pregnancy, Object childbirth, Object abortion, Object menarche_age, long final_menarche, Object ismenopause, List<?> values) {
        updateAccountWeb(userid, weight, height, ismarry);
        userManagementMapper.updateDieas(userid);
        userManagementMapper.updateMiddle(userid);
        userManagementMapper.insertDisease(userid, isallergy, ischronicillness, ischronOther, issurgery, issurgeryOther, isallergyOther, isfamilyhistory, familyOther, issmoking, isdrinking, isfertility, pregnancy, childbirth, abortion, menarche_age, final_menarche, ismenopause);
        for (Object b : values) {
            long val = Long.parseLong(b.toString());
            userManagementMapper.insertMiddleDisease(userid, val);
        }
        return true;
    }


    /*
        就诊信息
     */

    public Map<String, Object> getMedicalInformationList(long userid, int pageIndex, int pageSize) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<Map<String, Object>> problemlist = userManagementMapper.getProblemOrderList(userid);    //图文
        for (Map<String, Object> map : problemlist) {
            map.put("ordertype", OrderTypeEnum.Answer.getCode());
            list.add(map);
        }
        List<Map<String, Object>> phonelist = userManagementMapper.getPhoneOrderList(userid);    //电话
        for (Map<String, Object> map : phonelist) {
            map.put("ordertype", OrderTypeEnum.Phone.getCode());
            list.add(map);
        }
        List<Map<String, Object>> videolist = userManagementMapper.getVideoOrderList(userid);    //视频
        for (Map<String, Object> map : videolist) {
            map.put("ordertype", OrderTypeEnum.Video.getCode());
            list.add(map);
        }

        Map<String, Object> ma = new HashMap<>();

        //排序
        if (list.size() > 0) {
            Collections.sort(list, new Comparator<Map>() {
                @Override
                public int compare(Map o1, Map o2) {
                    int ret = 0;
                    //比较两个对象的顺序，如果前者小于、等于或者大于后者，则分别返回-1/0/1
                    ret = o2.get("createtime").toString().compareTo(o1.get("createtime").toString());//逆序的话就用o2.compareTo(o1)即可
                    return ret;
                }
            });
        }

        //分页
        int offset = (pageIndex - 1) * pageSize;
        int total = list.size();
        int limit = total - offset;

        if (list != null && total > 0) {
            //查询当页无数据，需要显示最后一页的内容
            if (limit <= 0) {
                offset = total % pageSize == 0 ? (total / pageSize) - 1 * pageSize : total / pageSize * pageSize;
                ma.put("list", list.subList(offset, total));
                //正常查询，查询当页有数据
            } else {
                limit = pageSize > limit ? limit : pageSize;
                ma.put("list", list.subList(offset, offset + limit));
            }
        } else {
            ma.put("list", "");
        }
        ma.put("count", list.size());
        return ma;
    }

    /**
     * 修改就诊
     *
     * @param ordertype
     * @param orderid
     * @param complaints
     * @param diagnosis
     * @param remark
     * @return
     */
    public boolean updateOrder(int ordertype, long orderid, String complaints, String diagnosis, String remark) {
        boolean order = false;
        if (ordertype == OrderTypeEnum.Answer.getCode()) {    //图文
            order = userManagementMapper.updateProblem(orderid, complaints, diagnosis, remark);
        } else if (ordertype == OrderTypeEnum.Phone.getCode()) {   //电话
            order = userManagementMapper.updatePhone(orderid, complaints, diagnosis, remark);
        } else if (ordertype == OrderTypeEnum.Video.getCode()) {   //视频
            order = userManagementMapper.updateVideo(orderid, complaints, diagnosis, remark);
        }
        return order;
    }

    /**
     * 修改疾病信息
     *
     * @param ordertype
     * @param orderid
     * @param diseasetime
     * @param gohospital
     * @param issuredis
     * @param disdescribe
     * @param picturelist
     * @return
     */
    public boolean updateSickOrder(int ordertype, long orderid, String diseasetime, int gohospital, int issuredis, String disdescribe, List<?> picturelist) {
        boolean order = false;
        if (ordertype == OrderTypeEnum.Answer.getCode()) {    //图文
            order = userManagementMapper.updateSickProblem(orderid, diseasetime, gohospital, issuredis, disdescribe);
            updateOderPicture(orderid, OrderTypeEnum.Answer.getCode(), picturelist);
        } else if (ordertype == OrderTypeEnum.Phone.getCode()) {   //电话
            order = userManagementMapper.updateSickPhone(orderid, diseasetime, gohospital, issuredis, disdescribe);
            updateOderPicture(orderid, OrderTypeEnum.Phone.getCode(), picturelist);
        } else if (ordertype == OrderTypeEnum.Video.getCode()) {   //视频
            order = userManagementMapper.updateSickVideo(orderid, diseasetime, gohospital, issuredis, disdescribe);
            updateOderPicture(orderid, OrderTypeEnum.Video.getCode(), picturelist);
        }
        return order;
    }

    public void updateOderPicture(long orderid, int ordertype, List<?> picturelist) {
        if (picturelist.size() > 0) {
            userManagementMapper.delOderPicture(orderid, ordertype);
            for (Object picture : picturelist) {
                userManagementMapper.insertOderPicture(orderid, picture.toString(), ordertype);
            }
        }
    }


    /**
     * 就诊详情
     *
     * @param orderid
     * @return
     */
    public Map<String, Object> getOrderListId(long orderid, int ordertype) {
        Map<String, Object> ordermap = new HashMap<>();
        if (ordertype == OrderTypeEnum.Answer.getCode()) {    //图文
            ordermap = getProblemOrderListId(orderid);
        } else if (ordertype == OrderTypeEnum.Phone.getCode()) {   //电话
            ordermap = getPhoneOrderListId(orderid);
        } else if (ordertype == OrderTypeEnum.Video.getCode()) {   //视频
            ordermap = getVideoOrderListId(orderid);
        }
        return ordermap;
    }

    //字符串拼接
    public List<Map<String, Object>> problemOrderdrugList(long orderid) {
        List<Map<String, Object>> list = userManagementMapper.problemOrderdrugList(orderid);
        if (list.size() > 0) {
            for (Map<String, Object> map : list) {
                String drugname = ModelUtil.getStr(map, "drugname");//药名
                String standarddesc = ModelUtil.getStr(map, "standarddesc");//0.5g 规格
                String medicinefreqname = ModelUtil.getStr(map, "medicinefreqname"); //3次/天
                int dosage = ModelUtil.getInt(map, "dosage");//单数
                String dosageunit = ModelUtil.getStr(map, "dosageunit");//单数（单位）
                String methods = ModelUtil.getStr(map, "method");//说明
                int totaldosage = ModelUtil.getInt(map, "totaldosage"); //总数
                String totaldosageunit = ModelUtil.getStr(map, "totaldosageunit");//总数（单位）
                String title = drugname + " " + standarddesc;
                String method = medicinefreqname + "，" + "每次" + dosage + dosageunit + " " + methods;
                String num = totaldosage + totaldosageunit;
                map.put("title", title);
                map.put("method", method);
                map.put("num", num);
            }
        }
        return list;
    }


    /**
     * 图文详情
     *
     * @param orderid
     * @return
     */
    public Map<String, Object> getProblemOrderListId(long orderid) {
        Map<String, Object> ordermap = userManagementMapper.getProblemOrderListId(orderid);
        int customid = ModelUtil.getInt(ordermap, "diseasetime");
        Map<String, Object> sickmap = userManagementMapper.sickTime(customid);
        ordermap.put("diseasetime", sickmap);//患病时长
        List<Map<String, Object>> picturemap = userManagementMapper.orderProblemPicture(orderid, OrderTypeEnum.Answer.getCode());
        ordermap.put("picturelist", picturemap);//疾病图片
        Map<String, Object> diseasemap = userManagementMapper.problemOrderDisease(orderid);
        ordermap.put("diseasename", ModelUtil.getStr(diseasemap, "diseasename")); //症状
        Map<String, Object> prescriptionmap = userManagementMapper.problemOrderprescription(orderid);
        ordermap.put("presphotourl", ModelUtil.getStr(prescriptionmap, "presphotourl"));//处方图片
        List<Map<String, Object>> druglist = userManagementMapper.problemOrderdrug(orderid);
        ordermap.put("druglist", druglist);//西药处方
        Map<String, Object> evaluatemap = userManagementMapper.problemOrderevaluate(orderid);     //评价
        ordermap.put("evaluate", ModelUtil.getInt(evaluatemap, "evaluate"));
        ordermap.put("content", ModelUtil.getStr(evaluatemap, "content"));
        ordermap.put("ordertype", OrderTypeEnum.Answer.getCode());
        ordermap.put("ordertypename", OrderTypeEnum.Answer.getMessage());
        return ordermap;
    }

    /**
     * 电话详情
     *
     * @param orderid
     * @return
     */
    public Map<String, Object> getPhoneOrderListId(long orderid) {
        Map<String, Object> ordermap = userManagementMapper.getPhoneOrderListId(orderid);
        int customid = ModelUtil.getInt(ordermap, "diseasetime");
        Map<String, Object> sickmap = userManagementMapper.sickTime(customid);
        ordermap.put("diseasetime", sickmap);//患病时长
        List<Map<String, Object>> picturemap = userManagementMapper.orderProblemPicture(orderid, OrderTypeEnum.Phone.getCode());
        ordermap.put("picturelist", picturemap);//疾病图片
        Map<String, Object> diseasemap = userManagementMapper.problemOrderDisease(orderid);
        ordermap.put("diseasename", ModelUtil.getStr(diseasemap, "diseasename")); //症状
        Map<String, Object> prescriptionmap = userManagementMapper.problemOrderprescription(orderid);
        ordermap.put("presphotourl", ModelUtil.getStr(prescriptionmap, "presphotourl"));//处方图片
        List<Map<String, Object>> druglist = userManagementMapper.problemOrderdrug(orderid);
        ordermap.put("druglist", druglist);//西药处方
        Map<String, Object> evaluatemap = userManagementMapper.problemOrderevaluate(orderid);     //评价
        ordermap.put("evaluate", ModelUtil.getInt(evaluatemap, "evaluate"));
        ordermap.put("content", ModelUtil.getStr(evaluatemap, "content"));
        ordermap.put("ordertype", OrderTypeEnum.Phone.getCode());
        ordermap.put("ordertypename", OrderTypeEnum.Phone.getMessage());
        return ordermap;
    }

    /**
     * 视频详情
     *
     * @param orderid
     * @return
     */
    public Map<String, Object> getVideoOrderListId(long orderid) {
        Map<String, Object> ordermap = userManagementMapper.getVideoOrderListId(orderid);
        int customid = ModelUtil.getInt(ordermap, "diseasetime");
        Map<String, Object> sickmap = userManagementMapper.sickTime(customid);
        ordermap.put("diseasetime", sickmap);//患病时长
        List<Map<String, Object>> picturemap = userManagementMapper.orderProblemPicture(orderid, OrderTypeEnum.Phone.getCode());
        ordermap.put("picturelist", picturemap);//疾病图片
        Map<String, Object> diseasemap = userManagementMapper.problemOrderDisease(orderid);
        ordermap.put("diseasename", ModelUtil.getStr(diseasemap, "diseasename")); //症状
        Map<String, Object> prescriptionmap = userManagementMapper.problemOrderprescription(orderid);
        ordermap.put("presphotourl", ModelUtil.getStr(prescriptionmap, "presphotourl"));//处方图片
        List<Map<String, Object>> druglist = userManagementMapper.problemOrderdrug(orderid);
        ordermap.put("druglist", druglist);//西药处方
        Map<String, Object> evaluatemap = userManagementMapper.problemOrderevaluate(orderid);     //评价
        ordermap.put("evaluate", ModelUtil.getInt(evaluatemap, "evaluate"));
        ordermap.put("content", ModelUtil.getStr(evaluatemap, "content"));
        ordermap.put("ordertype", OrderTypeEnum.Video.getCode());
        ordermap.put("ordertypename", OrderTypeEnum.Video.getMessage());
        return ordermap;
    }

    public List<Map<String, Object>> getSickTimeList() {
        return userManagementMapper.getSickTimeList();
    }


}
