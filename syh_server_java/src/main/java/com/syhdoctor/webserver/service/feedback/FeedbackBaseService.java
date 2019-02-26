package com.syhdoctor.webserver.service.feedback;

import com.syhdoctor.common.utils.EnumUtils.FeedbackStatusEnum;
import com.syhdoctor.common.utils.EnumUtils.MessageTypeEnum;
import com.syhdoctor.common.utils.EnumUtils.RegisterChannelEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.mapper.doctor.DoctorMapper;
import com.syhdoctor.webserver.mapper.feedback.FeedbackMapper;
import com.syhdoctor.webserver.mapper.user.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public abstract class FeedbackBaseService extends BaseService {

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 意见反馈
     *
     * @param userId   用户id
     * @param doctorId 医生id
     * @param type     1: android 2:ios
     * @param system   1: 用户端 2:医生端
     * @param content  反馈内容
     * @return
     */
    public boolean addFeedback(long userId, long doctorId, int type, int system, String content) {
        return feedbackMapper.addFeedback(userId, doctorId, type, system, content);
    }


    /**
     * 意见反馈列表
     *
     * @param name
     * @param system
     * @param status
     * @param phone
     * @param begintime
     * @param endtime
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> feedbackList(String name, int system, int status, String phone, long begintime, long endtime, int pageIndex, int pageSize) {
        return feedbackMapper.feedbackList(name, system, status, phone, begintime, endtime, pageIndex, pageSize);
    }

    /**
     * 意见反馈列表行数
     *
     * @param name
     * @param system
     * @param status
     * @param phone
     * @param begintime
     * @param endtime
     * @return
     */
    public long feedbackListCount(String name, int system, int status, String phone, long begintime, long endtime) {
        return feedbackMapper.feedbackListCount(name, system, status, phone, begintime, endtime);
    }


    /**
     * 导出
     *
     * @param name
     * @param system
     * @param status
     * @param phone
     * @param begintime
     * @param endtime
     * @return
     */
    public List<Map<String, Object>> feedbackListExport(String name, int system, int status, String phone, long begintime, long endtime) {
        List<Map<String, Object>> list = feedbackMapper.feedbackListExport(name, system, status, phone, begintime, endtime);
        Map<String, Object> map = new HashMap<>();
        map.put("id", "编号");
        map.put("name", "昵称");
        map.put("phone", "手机号");
        map.put("system", "类型");
        map.put("content", "意见内容");
        map.put("createtime", "时间");
        map.put("status", "处理状态");
        map.put("diagnosis", "结果反馈");
        list.add(0, map);
        return list;
    }


    /**
     * 删除反馈
     *
     * @param id
     * @return
     */
    public long feedbackDel(long id) {
        return feedbackMapper.feedbackDel(id);
    }

    /**
     * 点击编辑查询单个
     *
     * @param id
     * @return
     */
    public Map<String, Object> feedbackOneId(long id) {
        Map<String, Object> map = feedbackMapper.feedbackOneId(id);
        if (map != null) {
            //Map<String, Object> system = new HashMap<>();
            //system.put("id", ModelUtil.getInt(map, "system"));
            map.put("system", MessageTypeEnum.getValue(ModelUtil.getInt(map, "system")).getMessage());
            //map.put("system", system);

            map.put("createtime", UnixUtil.getDate(ModelUtil.getLong(map, "createtime"), "yyyy-MM-dd HH:mm:ss"));

            //Map<String, Object> status = new HashMap<>();
           // status.put("id", ModelUtil.getInt(map, "status"));
            map.put("status", FeedbackStatusEnum.getValue(ModelUtil.getInt(map, "status")).getMessage());
            //map.put("status", status);
        }
        return map;
    }

    public Map<String, Object> feedbackOneIdType(long id) {
        Map<String, Object> map = feedbackMapper.feedbackOneIdType(id);
        if (map != null) {
            Map<String, Object> system = new HashMap<>();
            system.put("id", ModelUtil.getInt(map, "system"));
            system.put("name", MessageTypeEnum.getValue(ModelUtil.getInt(map, "system")).getMessage());
            map.put("system", system);

            Map<String, Object> status = new HashMap<>();
            status.put("id", ModelUtil.getInt(map, "status"));
            status.put("name", FeedbackStatusEnum.getValue(ModelUtil.getInt(map, "status")).getMessage());
            map.put("status", status);

        }
        return map;
    }


    /**
     * 编辑反馈
     *
     * @param id
     * @param diagnosis
     * @return
     */
    public boolean feedbackUpdate(long id, String diagnosis) {
        feedbackMapper.feedbackUpdate(id, diagnosis);
        Map<String, Object> map = feedbackOneId(id);
        feedbackOperationRecordAdd(id, ModelUtil.getLong(map, "userid"));
        return true;
    }

    /**
     * 新增反馈操作记录
     *
     * @param feedbackid
     * @param createuser
     * @return
     */
    public boolean feedbackOperationRecordAdd(long feedbackid, long createuser) {
        return feedbackMapper.feedbackOperationRecordAdd(feedbackid, createuser);
    }


//    public boolean feebackUpdateorAdd(long id,String phone, int system, int status, String content, String diagnosis, long createtime, String name){
//
//        if(id == 0){
//            feedbackPhone(phone,system,status,content,diagnosis,createtime,name);
//        }else{
//            feedbackUpdate(id,diagnosis);
//        }
//
//        return true;
//    }


    /**
     * 新增反馈
     *
     * @param phone
     * @param system
     * @param status
     * @param content
     * @param diagnosis
     * @param createtime
     * @param name
     * @return
     */
    public boolean feedbackPhone(String phone, int system, int status, String content, String diagnosis, long createtime, String name) {

        long userId = 0;
        long doctorId = 0;
        long creataid = 0;
        Map<String, Object> createuserid = null;
        if (system == 1) {
            createuserid = userMapper.getUser(phone);
            if (createuserid == null) {
                String token = UnixUtil.generateString(32);
                userId = userMapper.addUser(phone, name, token, RegisterChannelEnum.Admin.getCode());
            } else {
                userId = ModelUtil.getLong(createuserid, "id");
            }
            creataid = userId;
        } else if (system == 2) {
            createuserid = feedbackMapper.feedbackPhone(phone);
            if (createuserid == null) {
                doctorId = doctorMapper.addDoctorRegister(phone, name, 1);
                doctorMapper.addDoctorExpand(doctorId);
            } else {
                doctorId = ModelUtil.getLong(createuserid, "doctorid");
            }
            creataid = doctorId;
        }
        long feedbackid = feedbackMapper.feedbackAdd(system, userId, doctorId, status, content, diagnosis, createtime);
        feedbackOperationRecordAdd(feedbackid, creataid);
        return true;
    }
}
