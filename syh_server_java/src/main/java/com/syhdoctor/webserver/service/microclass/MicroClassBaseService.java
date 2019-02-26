package com.syhdoctor.webserver.service.microclass;

import com.syhdoctor.common.utils.FileUtil;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.QiniuUtils;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.mapper.microclass.MicroClassMapper;
import com.syhdoctor.webserver.service.doctor.DoctorService;
import com.syhdoctor.webserver.service.lecturer.LecturerService;
import com.syhdoctor.webserver.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public abstract class MicroClassBaseService extends BaseService {

    @Autowired
    private MicroClassMapper microClassMapper;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private UserService userService;

    @Autowired
    private LecturerService lecturerService;

    /**
     * 课程详情
     *
     * @param id 课程id
     * @return
     */
    public Map<String, Object> getAdminCourse(long id) {
        Map<String, Object> course = microClassMapper.getAdminCourse(id);
        if (course != null) {
            int coursewaretype = ModelUtil.getInt(course, "coursewaretype");
            String courseware = ModelUtil.getStr(course, "courseware");
            if (coursewaretype == 1) {
                course.put("coursewarevoice", courseware);
            } else if (coursewaretype == 2) {
                course.put("coursewarevideo", courseware);
            }
            Map<String, Object> doctor = new HashMap<>();
            long typeid = ModelUtil.getLong(course, "typeid");
            long doctorid = ModelUtil.getLong(course, "doctorid");
            String phone = ModelUtil.getStr(course, "phone");
            String doctorname = ModelUtil.getStr(course, "doctorname");
            if (doctorid > 0) {
                doctor.put("id", doctorid);
                doctor.put("name", String.format("%s_%s", doctorname, phone));
                doctor.put("phone", phone);
            }
            course.put("doctor", doctor);
            course.put("type", microClassMapper.getCourseType(typeid));
        }
        return course;
    }


    public Map<String, Object> getCourseDoctor(long id) {
        return microClassMapper.getCourseDoctor(id);
    }

    /**
     * 课程详情
     *
     * @param id 课程id
     * @return
     */
    @Cacheable(value = "MicroClass", key = "#root.methodName+#root.args[0]")
    public Map<String, Object> getAppCourse(long id) {
        Map<String, Object> course = microClassMapper.getAppCourse(id);
        if (course != null) {
            long doctorid = ModelUtil.getLong(course, "doctorid");
            Map<String, Object> doctor = lecturerService.getLecturer(doctorid);
            course.put("doctor", doctor);
            int coursewaretype = ModelUtil.getInt(course, "coursewaretype");
            String courseware = ModelUtil.getStr(course, "courseware");
            int courseware_time = ModelUtil.getInt(course, "courseware_time");
            if (coursewaretype == 1 || coursewaretype == 2) {
                Map<String, Object> ware = new HashMap<>();
                ware.put("coursewaretype", coursewaretype);
                ware.put("courseware", courseware);
                ware.put("coursewaretime", courseware_time);
                course.put("ware", ware);
            }
            long typeid = ModelUtil.getLong(course, "typeid");
            course.put("taglist", microClassMapper.tagList(id));
            course.put("outlinelist", microClassMapper.outlineList(id));
            course.put("questionanswerlist", microClassMapper.questionAnswerList(id));
            course.put("type", microClassMapper.getCourseType(typeid));
            course.put("relevantList", relevantList(id, typeid, doctorid));
        }
        return course;
    }

    public boolean updateCourseBrowsenum(long id) {
        return microClassMapper.updateCourseBrowsenum(id);
    }

    /**
     * 相关课程
     *
     * @return
     */
    private List<Map<String, Object>> relevantList(long courseId, long typeId, long doctorId) {
        //显示数量
        int num = 4;
        //相关课程
        List<Map<String, Object>> relevantCourseList = new ArrayList<>();
        //该课程分类下的其他课程
        List<Map<String, Object>> typeOrtherCourseList = microClassMapper.getTypeOrtherCourseList(courseId, typeId);

        //全部放进相关课程中
        if (typeOrtherCourseList.size() > 0) {
            relevantCourseList.addAll(typeOrtherCourseList);
        }

        if (relevantCourseList.size() < 4) {
            //该医生下的其他课程
            List<Map<String, Object>> doctorOrtherCourseList = microClassMapper.getDoctorOrtherCourseList(courseId, doctorId);
            if (doctorOrtherCourseList.size() > 0) {
                int size = doctorOrtherCourseList.size();
                int i = num - size > size ? size : num - size;
                relevantCourseList.addAll(relevantCourseList.size(), doctorOrtherCourseList.subList(0, i));
            }
        }
        if (relevantCourseList.size() < 4) {
            //其他课程浏览数正序
            List<Map<String, Object>> browsenumCourseList = microClassMapper.getBrowsenumCourseList(courseId, doctorId);
            if (browsenumCourseList.size() > 0) {
                int size = browsenumCourseList.size();
                int i = num - size > size ? size : num - size;
                relevantCourseList.addAll(relevantCourseList.size(), browsenumCourseList.subList(0, i));
            }
        }

        return relevantCourseList;
    }


    public List<Map<String, Object>> getCourseTypeList() {
        return microClassMapper.getCourseTypeList();
    }

    /**
     * 课程数量
     *
     * @param typeId 课程类型id
     * @param name   课程类型id
     * @return
     */
    public long courseCount(String name, long typeId) {
        return microClassMapper.getCourseCount(name, typeId);
    }

    /**
     * 课程数量
     *
     * @param typeId 课程类型id
     * @return
     */
    public long courseCount(long typeId) {
        return courseCount(null, typeId);
    }

    /**
     * 课程列表
     *
     * @param name      课程名字
     * @param typeId    课程类型id
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getAdminCourseList(String name, long typeId, int pageIndex, int pageSize) {
        return microClassMapper.getAdminCourseList(name, typeId, pageIndex, pageSize);
    }

    /**
     * 课程列表
     *
     * @param name      课程名字
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getAdminCourseList(String name, int pageIndex, int pageSize) {
        return microClassMapper.getAdminCourseList(name, pageIndex, pageSize);
    }

    /**
     * 课程列表
     *
     * @return
     */
    public List<Map<String, Object>> courseList(int count) {
        return microClassMapper.getCourseList(count);
    }

    /**
     * 课程列表
     *
     * @param typeId    课程类型id
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getAppCourseList(long typeId, int pageIndex, int pageSize) {
        return microClassMapper.getAppCourseList(typeId, pageIndex, pageSize);
    }


    /**
     * 添加或者修改课程
     *
     * @param id             课程id
     * @param name           课程名字
     * @param phone          医生电话
     * @param typeId         课程类型
     * @param details        课程详情
     * @param courseware     课件
     * @param img            大图
     * @param smallimg       小图
     * @param coursewaretime 课件时间
     * @param coursewaretype 课件类型
     * @param test           是否测试
     * @param sort           排序
     * @param createUser     创建人
     * @return
     */
    @Transactional
    @CacheEvict(value = "MicroClass",key = "", allEntries = true)
    public boolean addUpdateCourse(long id, String name, String phone, long typeId, String details, String courseware, String coursewareposter, String img, String smallimg, int coursewaretime, int coursewaretype, int test, int sort, long createUser) {
        long doctorId = 0;
        //讲师
        Map<String, Object> lecturer = lecturerService.getLecturer(phone);
        Map<String, Object> doctorMp = lecturerService.getDoctorByPhone(phone);
        String key = "syh" + UnixUtil.getCustomRandomString() + ".png";
        if (doctorMp != null) {
            try {
                String docPhotoUrl = ModelUtil.getStr(doctorMp, "docphotourl");
                String s = ConfigModel.BASEFILEPATH + FileUtil.getFileName(docPhotoUrl);
                log.info(">>>>>>------------------------------>>>" + s);
                QiniuUtils.putFile(ConfigModel.QINIU.BUCKET, key, new FileInputStream(s));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (lecturer == null) {
            //不存在讲师将医生复制一份到讲师
            doctorId = microClassMapper.copyDoctor(createUser, phone, key);
        } else {
            doctorId = ModelUtil.getLong(lecturer, "id");
        }

        //将重复排序的清空
        microClassMapper.updateCourseSort(sort);

        if (id == 0) {
            microClassMapper.addCourse(name, doctorId, typeId, details, courseware, coursewareposter, img, smallimg, coursewaretime, coursewaretype, test, sort, createUser);
        } else {
            microClassMapper.updateCourse(id, name, doctorId, typeId, details, courseware, coursewareposter, img, smallimg, coursewaretime, coursewaretype, test, sort, createUser);
        }

        return true;
    }

    /**
     * 删除课程
     *
     * @param id 课程id
     * @return
     */
    @Transactional
    public boolean delCourse(long id) {
        //删除课程
        boolean flag = microClassMapper.delCourse(id);
        if (flag) {
            //删除大纲
            microClassMapper.delOutline(id);
            //删除课程标签关系
            microClassMapper.delCourseTag(id);
        }
        return flag;
    }


    /**
     * 课程分类列表
     *
     * @param name      课程名
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getCourseTypeList(String name, int pageIndex, int pageSize) {
        return microClassMapper.getCourseTypeList(name, pageIndex, pageSize);
    }

    /**
     * 课程分类数量
     *
     * @param name
     * @return
     */
    public long courseTypeCount(String name) {
        return microClassMapper.getCourseTypeCount(name);
    }

    /**
     * 添加修改课程分类
     *
     * @param id
     * @param name
     * @param sort
     * @param createUser
     * @return
     */
    public boolean addUpdateCourseType(long id, String name, int sort, long createUser) {
        //清空重复排序
        microClassMapper.updateCourseTypeSort(sort);
        if (id == 0) {
            return microClassMapper.addCourseType(name, sort, createUser);
        } else {
            return microClassMapper.updateCourseType(id, name, sort, createUser);
        }
    }

    /**
     * 删除课程分类
     *
     * @param id 课程分类id
     * @return
     */
    public boolean delCourseType(long id) {
        long count = courseCount(id);
        if (count > 0) {
            return false;
        } else {
            return microClassMapper.delCourseType(id);
        }
    }

    /**
     * 课程答疑列表
     *
     * @param question  问题
     * @param courseid  课程id
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> questionAnswerList(String question, long courseid, int pageIndex, int pageSize) {
        return microClassMapper.questionAnswerList(question, courseid, pageIndex, pageSize, (Map<String, Object> value) -> {
            value.put("answertype", questionAnswerTypeName(ModelUtil.getInt(value, "answertype", 0)));
            return value;
        });
    }

    /**
     * 答疑类型转文字
     *
     * @param type
     * @return
     */
    private String questionAnswerTypeName(int type) {
        String typeName = "";
        switch (type) {
            case 1:
                typeName = "语音";
                break;
            case 2:
                typeName = "文字";
                break;
        }
        return typeName;
    }

    /**
     * 课程答疑数量
     *
     * @param question 问题
     * @param courseid 课程id
     * @return
     */
    public long questionAnswerCount(String question, long courseid) {
        return microClassMapper.questionAnswerCount(question, courseid);
    }

    /**
     * 添加课程答疑
     *
     * @param id         答疑id
     * @param courseid   课程id
     * @param question   问题
     * @param answer     答案
     * @param answertime 时长
     * @param type       类型答案类型，1：语音,2：文本，3：图片
     * @param sort       排序
     * @param createUser 操作人
     * @return
     */
    public boolean addUpdateQuestionAnswer(long id, long courseid, String question, String answer, int answertime, int type, int sort, long createUser) {

        microClassMapper.updateQuestionAnswerSort(sort);
        if (id == 0) {
            return microClassMapper.addQuestionAnswer(courseid, question, answer, answertime, type, sort, createUser);
        } else {
            return microClassMapper.updateQuestionAnswer(id, courseid, question, answer, answertime, type, sort, createUser);
        }
    }

    /**
     * 删除答疑
     *
     * @param id
     * @return
     */
    public boolean delQuestionAnswer(long id) {
        return microClassMapper.delQuestionAnswer(id);
    }

    /**
     * 医生讲师列表,医生讲师一一对应,优先取讲师,讲师存在则对应的医生不取
     *
     * @param nameOrPhone 名字或者手机号码
     * @return
     */
    public List<Map<String, Object>> doctorList(String nameOrPhone) {
        return microClassMapper.doctorList(nameOrPhone);
    }

    /**
     * 课程分类详情
     *
     * @param id
     * @return
     */
    public Map<String, Object> getCourseType(long id) {
        return microClassMapper.getCourseType(id);
    }

    /**
     * 课程答疑详情
     *
     * @param id 答疑id
     * @return
     */
    public Map<String, Object> getQuestionAnswer(long id) {
        return microClassMapper.getQuestionAnswer(id);
    }

    /**
     * 添加课程大纲
     *
     * @param id          大纲id
     * @param courseid    课程id
     * @param title       标题
     * @param content     内容
     * @param contenttime 内容时长
     * @param sort        排序
     * @param createUser  创建人
     * @return
     */
    @Transactional
    public boolean addUpdateOutline(long id, long courseid, String title, String content, int contenttime, int sort, long createUser) {
        //清空重复排序
        microClassMapper.updateOutlineSort(sort);
        if (id == 0) {
            return microClassMapper.addOutline(courseid, title, content, contenttime, sort, createUser);
        } else {
            return microClassMapper.updateOutline(id, title, content, contenttime, sort, createUser);
        }
    }

    /**
     * 删除大纲
     *
     * @param id 大纲id
     * @return
     */
    public boolean delOutline(long id) {
        return microClassMapper.delOutline(id);
    }

    /**
     * 大纲列表
     *
     * @param title     标题
     * @param courseid  课程id
     * @param pageIndex 分页
     * @param pageSize  分页
     * @return
     */
    public List<Map<String, Object>> ourlineList(String title, long courseid, int pageIndex, int pageSize) {
        return microClassMapper.outlineList(title, courseid, pageIndex, pageSize, (Map<String, Object> value) -> {
            return value;
        });
    }

    /**
     * 大纲类型转文字
     *
     * @param type
     * @return
     */
    private String outlineTypeName(int type) {
        String typeName = "";
        switch (type) {
            case 1:
                typeName = "语音";
                break;
            case 2:
                typeName = "文字";
                break;
        }
        return typeName;
    }

    /**
     * 大纲数量
     *
     * @param title
     * @param courseid
     * @return
     */
    public long ourlineCount(String title, long courseid) {
        return microClassMapper.outlineCount(title, courseid);
    }

    /**
     * 大纲详情
     *
     * @param id
     * @return
     */
    public Map<String, Object> getOutline(long id) {
        return microClassMapper.getOutline(id);
    }

    public Map<String, Object> getInvitingCard(long courseId, long userId) {
        Map<String, Object> course = microClassMapper.getCourseDoctor(courseId);
        if (userId > 0) {
            Map<String, Object> user = userService.getUser(userId);
            course.put("userid", ModelUtil.getLong(user, "id"));
            course.put("userheadpic", ModelUtil.getStr(user, "headpic"));
            course.put("username", ModelUtil.getStr(user, "name"));
        }
        if (course != null) {
            course.put("qrcode", doctorService.qrcode(String.valueOf(courseId), "doctor_logo.png", String.format("qrcode_%s.png", courseId)));
        }
        return course;
    }

}
