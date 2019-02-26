package com.syhdoctor.webserver.mapper.microclass;

import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class MicroClassBaseMapper extends BaseMapper {

    /**
     * 课程详情
     *
     * @param id 课程id
     * @return
     */
    public Map<String, Object> getAdminCourse(long id) {
        String sql = " SELECT " +
                "  mc.id, " +
                "  mc.name, " +
                "  mc.doctorid, " +
                "  mc.typeid, " +
                "  mc.test, " +
                "  mc.sort, " +
                "  mc.details, " +
                "  mc.img, " +
                "  mc.smallimg, " +
                "  mc.courseware, " +
                "  mc.coursewareposter, " +
                "  mc.courseware_type coursewaretype, " +
                "  mc.courseware_time coursewaretime, " +
                "  mc.create_time, " +
                "  li.id doctorid, " +
                "  li.name as doctorname, " +
                "  li.phone, " +
                "  li.hospital, " +
                "  li.department, " +
                "  li.title_name " +
                "FROM microclass_course mc " +
                "  left join lecturer_info li on mc.doctorid = li.id " +
                "WHERE mc.id = ? ";
        return queryForMap(sql, id);
    }

    /**
     * 课程详情
     *
     * @param id 课程id
     * @return
     */
    public Map<String, Object> getAdminCourseBySelect(long id) {
        String sql = " SELECT id,name FROM microclass_course WHERE id = ? ";
        return queryForMap(sql, id);
    }


    /**
     * 课程详情
     *
     * @param id 课程id
     * @return
     */
    public Map<String, Object> getAppCourse(long id) {
        String sql = " SELECT " +
                "  id, " +
                "  name, " +
                "  doctorid, " +
                "  details, " +
                "  img, " +
                "  smallimg, " +
                "  courseware," +
                "  coursewareposter, " +
                "  courseware_type coursewaretype, " +
                "  courseware_time coursewaretime, " +
                "  create_time " +
                "FROM microclass_course  " +
                "WHERE id = ? ";
        return queryForMap(sql, id);
    }

    /**
     * 课程详情
     *
     * @param id 课程id
     * @return
     */
    public Map<String, Object> getCourseDoctor(long id) {
        String sql = " SELECT " +
                "  mc.id, " +
                "  mc.name, " +
                "  mc.doctorid, " +
                "  li.name as doctorname, " +
                "  li.hospital, " +
                "  li.department, " +
                "  li.photo doctorheadpic, " +
                "  li.title_name titlename  " +
                "FROM microclass_course mc " +
                "  left join lecturer_info li on mc.doctorid = li.id " +
                "WHERE mc.id = ? ";
        return queryForMap(sql, id);
    }

    /**
     * 课程大纲
     *
     * @param id 课程id
     * @return
     */
    public List<Map<String, Object>> outlineList(long id) {
        String sql = "SELECT id,title,content,contenttime FROM microclass_outline WHERE courseid = ? and ifnull(delflag,0) ORDER BY sort DESC,create_time DESC  ";
        return queryForList(sql, id);
    }

    /**
     * 大纲列表
     *
     * @param title
     * @param courseid
     * @param pageIndex
     * @param pageSize
     * @param callback
     * @return
     */
    public List<Map<String, Object>> outlineList(String title, long courseid, int pageIndex, int pageSize, IMapperResult callback) {
        String sql = " SELECT id,courseid,title,content,contenttime,sort,create_time createtime FROM microclass_outline WHERE courseid=? AND IFNULL(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(courseid);
        if (!StrUtil.isEmpty(title)) {
            sql += " AND title like ? ";
            params.add(String.format("%%%s%%", title));
        }
        return query(pageSql(sql, " ORDER BY sort DESC,create_time DESC "), pageParams(params, pageIndex, pageSize), (ResultSet res, int num) -> {
            Map<String, Object> result = resultToMap(res);
            if (callback != null) {
                result = callback.result(result);
            }
            return result;
        });
    }

    /**
     * 大纲详细
     *
     * @param id
     * @return
     */
    public Map<String, Object> getOutline(long id) {
        String sql = " SELECT id,title,content,contenttime,type contenttype,sort FROM microclass_outline WHERE id=? ";
        return queryForMap(sql, id);
    }

    /**
     * 大纲数量
     *
     * @param title
     * @param courseid
     * @return
     */
    public long outlineCount(String title, long courseid) {
        String sql = " SELECT count(id) count FROM microclass_outline WHERE courseid=? AND IFNULL(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(courseid);
        if (!StrUtil.isEmpty(title)) {
            sql += " AND title like ? ";
            params.add(String.format("%%%s%%", title));
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    /**
     * 课程疑答列表
     *
     * @param id 课程id
     * @return
     */
    public List<Map<String, Object>> questionAnswerList(long id) {
        String sql = " SELECT id,courseid,question_content,answer,answertime,answertype FROM microclass_question_answer WHERE courseid = ? and ifnull(delflag,0)=0 ORDER BY sort DESC,create_time DESC ";
        return queryForList(sql, id);
    }

    /**
     * 课程疑答列表
     *
     * @param question  问题
     * @param courseid  课程id
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> questionAnswerList(String question, long courseid, int pageIndex, int pageSize, IMapperResult callback) {
        String sql = " SELECT id,courseid,question_content as questioncontent,answer,answertime,answertype,sort,create_time as createtime FROM microclass_question_answer WHERE courseid = ? AND IFNULL(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(courseid);
        if (!StrUtil.isEmpty(question)) {
            sql += " AND question_content LIKE ? ";
            params.add(String.format("%%%s%%", question));
        }
        return query(pageSql(sql, " ORDER BY sort DESC,create_time DESC  "), pageParams(params, pageIndex, pageSize), (ResultSet res, int num) -> {
            Map<String, Object> result = resultToMap(res);
            if (callback != null) {
                result = callback.result(result);
            }
            return result;
        });
    }

    /**
     * 课程疑答数量
     *
     * @param courseid 课程id
     * @return
     */
    public long questionAnswerCount(String question, long courseid) {
        String sql = " SELECT count(id) count FROM microclass_question_answer WHERE courseid = ? ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(question)) {
            sql += " AND question_content LIKE ? ";
            params.add(String.format("%%%s%%", question));
        }
        params.add(courseid);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }


    /**
     * 课程列表
     *
     * @param name      课程名字
     * @param typeId    课程类型id
     * @param pageIndex 开始条数
     * @param pageSize  当页总条数
     * @return
     */
    public List<Map<String, Object>> getAdminCourseList(String name, long typeId, int pageIndex, int pageSize) {
        String sql = " SELECT " +
                "  mc.id, " +
                "  mc.name, " +
                "  mc.doctorid, " +
                "  mc.smallimg, " +
                "  mc.courseware, " +
                "  mc.courseware_type, " +
                "  mc.courseware_time, " +
                "  mc.sort, " +
                "  mc.create_time, " +
                "  li.name as doctorname, " +
                "  li.phone, " +
                "  li.hospital, " +
                "  li.title_name " +
                "FROM microclass_course mc " +
                "  left join lecturer_info li on mc.doctorid = li.id " +
                "WHERE IFNULL(mc.delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " AND mc.name like ? ";
            params.add(String.format("%%%s%%", name));
        }
        if (typeId != 0) {
            sql += " AND mc.typeid =? ";
            params.add(typeId);
        }
        return queryForList(pageSql(sql, " ORDER BY mc.sort DESC,mc.create_time DESC "), pageParams(params, pageIndex, pageSize));
    }

    /**
     * 课程列表
     *
     * @param name      课程名字
     * @param pageIndex 开始条数
     * @param pageSize  当页总条数
     * @return
     */
    public List<Map<String, Object>> getAdminCourseList(String name, int pageIndex, int pageSize) {
        String sql = " SELECT id, name FROM microclass_course WHERE IFNULL(delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " AND name like ? ";
            params.add(String.format("%%%s%%", name));
        }
        return queryForList(pageSql(sql, " ORDER BY sort DESC,create_time DESC "), pageParams(params, pageIndex, pageSize));
    }

    /**
     * 课程列表
     *
     * @param count 显示数量
     * @return
     */
    public List<Map<String, Object>> getCourseList(int count) {
        String sql = " SELECT " +
                "  mc.id, " +
                "  mc.img, " +
                "  mc.smallimg, " +
                "  mc.courseware_type coursewaretype, " +
                "  mc.courseware_time coursewaretime, " +
                "  li.name as doctorname, " +
                "  li.title_name as  title, " +
                "  li.hospital " +
                "FROM microclass_course mc " +
                "  left join lecturer_info li on mc.doctorid = li.id " +
                "WHERE IFNULL(mc.delflag, 0)=0 and IFNULL(mc.test,0) = 0 ORDER BY mc.sort DESC,mc.create_time DESC limit ? ";
        List<Object> params = new ArrayList<>();
        params.add(count);
        return queryForList(sql, params);
    }

    /**
     * 该课程类型下的其他课程列表
     *
     * @return
     */
    public List<Map<String, Object>> getTypeOrtherCourseList(long courseId, long typeId) {
        String sql = " SELECT " +
                "  mc.id, " +
                "  mc.img, " +
                "  mc.smallimg, " +
                "  mc.courseware_type coursewaretype, " +
                "  mc.courseware_time coursewaretime, " +
                "  li.name as doctorname " +
                "FROM microclass_course mc " +
                "  left join lecturer_info li on mc.doctorid = li.id " +
                "WHERE IFNULL(mc.delflag, 0)=0 and IFNULL(mc.test,0) = 0 and mc.typeid=? and mc.id!=? ORDER BY mc.browsenum ,mc.create_time DESC limit 4 ";
        return queryForList(sql, typeId, courseId);
    }

    /**
     * 该医生的其他课程列表
     *
     * @return
     */
    public List<Map<String, Object>> getDoctorOrtherCourseList(long courseId, long typeId) {
        String sql = " SELECT " +
                "  mc.id, " +
                "  mc.img, " +
                "  mc.smallimg, " +
                "  mc.courseware_type coursewaretype, " +
                "  mc.courseware_time coursewaretime, " +
                "  li.name as doctorname " +
                "FROM microclass_course mc " +
                "  left join lecturer_info li on mc.doctorid = li.id " +
                "WHERE IFNULL(mc.delflag, 0)=0 and IFNULL(mc.test,0) = 0 and mc.doctorid=? and mc.id!=? ORDER BY mc.browsenum,mc.create_time DESC limit 4 ";
        return queryForList(sql, typeId, courseId);
    }

    /**
     * 其他课程流浪数正序列表
     *
     * @return
     */
    public List<Map<String, Object>> getBrowsenumCourseList(long courseId, long typeId) {
        String sql = " SELECT " +
                "  mc.id, " +
                "  mc.img, " +
                "  mc.smallimg, " +
                "  mc.courseware_type coursewaretype, " +
                "  mc.courseware_time coursewaretime, " +
                "  li.name as doctorname " +
                "FROM microclass_course mc " +
                "  left join lecturer_info li on mc.doctorid = li.id " +
                "WHERE IFNULL(mc.delflag, 0)=0 and IFNULL(mc.test,0) = 0 and mc.typeid= ? and mc.id!=?  ORDER BY mc.browsenum,mc.create_time DESC limit 4 ";
        return queryForList(sql, typeId, courseId);
    }


    /**
     * 课程列表
     *
     * @param typeId    课程类型id
     * @param pageIndex 开始条数
     * @param pageSize  当页总条数
     * @return
     */
    public List<Map<String, Object>> getAppCourseList(long typeId, int pageIndex, int pageSize) {
        String sql = " SELECT " +
                "  mc.id, " +
                "  mc.name coursetitle, " +
                "  mc.smallimg, " +
                "  mc.courseware_type coursewaretype, " +
                "  mc.courseware_time coursewaretime, " +
                "  li.title_name as titlename, " +
                "  li.name as doctorname " +
                "FROM microclass_course mc " +
                "  left join lecturer_info li on mc.doctorid = li.id " +
                "WHERE IFNULL(mc.delflag, 0) = 0 AND ifnull(mc.test,0)=0 ";
        List<Object> params = new ArrayList<>();
        if (typeId != 0) {
            sql += " AND mc.typeid =? ";
            params.add(typeId);
        }
        return queryForList(pageSql(sql, " ORDER BY mc.sort DESC,mc.create_time DESC "), pageParams(params, pageIndex, pageSize));
    }

    /**
     * 课程数量
     *
     * @param name   课程名字
     * @param typeId 课程类型id
     * @return
     */
    public long getCourseCount(String name, long typeId) {
        String sql = " SELECT " +
                "  count(mc.id) count " +
                "FROM microclass_course mc " +
                "  left join doctor_info di on mc.doctorid = di.doctorid " +
                "WHERE IFNULL(di.delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " AND mc.name like ? ";
            params.add(String.format("%%%s%%", name));
        }
        if (typeId != 0) {
            sql += " AND mc.typeid =? ";
            params.add(typeId);
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }


    /**
     * 首页课程类型列表（取全部，先sort倒序，后时间倒序）
     *
     * @return
     */
    public List<Map<String, Object>> getCourseTypeList() {
        String sql = " SELECT id,name,sort,create_time createtime FROM microclass_type WHERE IFNULL(delflag,0)=0 ORDER BY sort DESC ,create_time DESC ";
        return queryForList(sql);
    }

    /**
     * 首页课程类型列表带
     *
     * @param name      课程名
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getCourseTypeList(String name, int pageIndex, int pageSize) {
        String sql = " SELECT id,name,sort,create_time createtime FROM microclass_type WHERE IFNULL(delflag,0)=0  ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " AND name like ? ";
            params.add(String.format("%%%s%%", name));
        }
        return queryForList(pageSql(sql, " ORDER BY sort DESC ,create_time DESC "), pageParams(params, pageIndex, pageSize));
    }

    /**
     * 首页课程类型数量
     *
     * @return
     */
    public long getCourseTypeCount(String name) {
        String sql = " SELECT count(id) count FROM microclass_type WHERE IFNULL(delflag,0)=0  ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " AND name like ? ";
            params.add(String.format("%%%s%%", name));
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    /**
     * 课程标签列表
     *
     * @param id 课程id
     * @return
     */
    public List<Map<String, Object>> tagList(long id) {
        String sql = " SELECT t.name " +
                "FROM tag t LEFT JOIN middle_course_tag ct ON t.id = ct.tagid " +
                "  LEFT JOIN microclass_course mc ON ct.courseid = mc.id " +
                "WHERE mc.id = ? AND IFNULL(t.delflag, 0) = 0 ORDER BY t.sort DESC,t.create_time DESC ";
        return queryForList(sql, id);
    }

    /**
     * 添加课程
     *
     * @param name           课程名字
     * @param doctorId       医生id
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
    public long addCourse(String name, long doctorId, long typeId, String details, String courseware, String coursewareposter, String img, String smallimg, int coursewaretime, int coursewaretype, int test, int sort, long createUser) {
        String sql = "INSERT INTO microclass_course (name, doctorid, typeid, details, courseware, coursewareposter, courseware_type, courseware_time, img, smallimg, test, sort, delflag, create_time, create_user) " +
                "VALUES ( " +
                "  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? , ? " +
                ")";
        List<Object> params = new ArrayList<>();
        params.add(name);
        params.add(doctorId);
        params.add(typeId);
        params.add(details);
        params.add(courseware);
        params.add(coursewareposter);
        params.add(coursewaretype);
        params.add(coursewaretime);
        params.add(img);
        params.add(smallimg);
        params.add(test);
        params.add(sort);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(createUser);
        return insert(sql, params, "id");
    }


    /**
     * 修改课程
     *
     * @param id             课程id
     * @param name           课程名字
     * @param doctorId       医生id
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
    public boolean updateCourse(long id, String name, long doctorId, long typeId, String details, String courseware, String coursewareposter, String img, String smallimg, int coursewaretime, int coursewaretype, int test, int sort, long createUser) {
        String sql = " UPDATE microclass_course " +
                " SET name = ?, doctorid = ?, typeid = ?, details = ?, courseware = ?,coursewareposter=? , courseware_type = ?, courseware_time = ?, " +
                "  img    = ?, smallimg = ?, test = ?, sort = ?, modify_time = ?, modify_user = ? " +
                " where id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(name);
        params.add(doctorId);
        params.add(typeId);
        params.add(details);
        params.add(courseware);
        params.add(coursewareposter);
        params.add(coursewaretype);
        params.add(coursewaretime);
        params.add(img);
        params.add(smallimg);
        params.add(test);
        params.add(sort);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(createUser);
        params.add(id);
        return update(sql, params) > 0;
    }

    /**
     * 添加课程标签关系
     *
     * @param courseId 课程id
     * @param tagId    标签id
     * @return
     */
    public long addCourseTag(long courseId, long tagId) {
        String sql = " INSERT INTO middle_course_tag(courseid,tagid)VALUES (?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(courseId);
        params.add(tagId);
        return insert(sql, params);
    }


    /**
     * 删除课程标签关系
     *
     * @param courseId 课程id
     * @return
     */
    public boolean delCourseTag(long courseId) {
        String sql = " DELETE FROM middle_course_tag WHERE courseid=? ";
        List<Object> params = new ArrayList<>();
        params.add(courseId);
        return insert(sql, params) > 0;
    }


    /**
     * 删除课程
     *
     * @param id 课程id
     * @return
     */
    public boolean delCourse(long id) {
        String sql = " UPDATE microclass_course SET delflag=1 where id=? ";
        return update(sql, id) > 0;
    }

    /**
     * 添加课程
     *
     * @param name       课程名
     * @param sort       排序
     * @param createUser 添加人
     * @return
     */
    public boolean addCourseType(String name, int sort, long createUser) {
        String sql = " INSERT INTO microclass_type (name, sort, delflag, create_time, create_user) VALUES (?, ?, ?, ?, ?) ";
        List<Object> params = new ArrayList<>();
        params.add(name);
        params.add(sort);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(createUser);
        return insert(sql, params) > 0;
    }

    /**
     * 修改课程
     *
     * @param id         课程id
     * @param name       课程名
     * @param sort       排序
     * @param createUser 修改人
     * @return
     */
    public boolean updateCourseType(long id, String name, int sort, long createUser) {
        String sql = " UPDATE microclass_type SET name=?, sort=?, modify_time=?, modify_user=? where id= ? ";
        List<Object> params = new ArrayList<>();
        params.add(name);
        params.add(sort);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(createUser);
        params.add(id);
        return insert(sql, params) > 0;
    }

    /**
     * 删除课程分类
     *
     * @param id 课程分类id
     * @return
     */
    public boolean delCourseType(long id) {
        String sql = " UPDATE microclass_type SET delflag=1 where id=? ";
        return update(sql, id) > 0;
    }

    /**
     * 添加大纲
     *
     * @param courseid    课程id
     * @param title       标题
     * @param content     内容
     * @param contenttime 内容时长
     * @param sort        排序
     * @param createUser  操作人
     * @return
     */
    public boolean addOutline(long courseid, String title, String content, int contenttime, int sort, long createUser) {
        String sql = " INSERT INTO microclass_outline (title, courseid, content, contenttime, sort, delflag, create_time, create_user) " +
                "VALUES ( " +
                "  ?, ?, ?, ?, ?, ?, ?, ?" +
                ")  ";
        List<Object> params = new ArrayList<>();
        params.add(title);
        params.add(courseid);
        params.add(content);
        params.add(contenttime);
        params.add(sort);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(createUser);
        return insert(sql, params) > 0;
    }

    /**
     * 修改大纲
     *
     * @param id          大纲id
     * @param title       标题
     * @param content     内容
     * @param contenttime 内容时长
     * @param sort        排序
     * @param createUser  操作人
     * @return
     */
    public boolean updateOutline(long id, String title, String content, int contenttime, int sort, long createUser) {
        String sql = " UPDATE microclass_outline SET title=?, content=?, contenttime=?, sort=?, modify_time=?, modify_user=? WHERE id =? ";
        List<Object> params = new ArrayList<>();
        params.add(title);
        params.add(content);
        params.add(contenttime);
        params.add(sort);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(createUser);
        params.add(id);
        return insert(sql, params) > 0;
    }


    /**
     * 删除大纲
     *
     * @param id 大纲id
     * @return
     */
    public boolean delOutline(long id) {
        String sql = " UPDATE microclass_outline SET delflag=1 WHERE id=? ";
        return update(sql, id) > 0;
    }

    /**
     * 添加答疑
     *
     * @param courseid   课程id
     * @param question   问题
     * @param answer     答案
     * @param answertime 答案时长
     * @param type       答案类型1：语音，2：文字，3：图片
     * @param sort       排序
     * @param createUser 操作人
     * @return
     */
    public boolean addQuestionAnswer(long courseid, String question, String answer, int answertime, int type, int sort, long createUser) {
        String sql = " INSERT INTO microclass_question_answer (courseid, question_content, answer, answertime, answertype, sort, delflag, create_time, create_user) " +
                "VALUES ( " +
                "  ?, ?, ?, ?, ?, ?, ?, ?, ? " +
                ") ";
        List<Object> params = new ArrayList<>();
        params.add(courseid);
        params.add(question);
        params.add(answer);
        params.add(answertime);
        params.add(type);
        params.add(sort);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(createUser);
        return insert(sql, params) > 0;
    }

    /**
     * 修改答疑
     *
     * @param id         答疑id
     * @param courseid   课程id
     * @param question   问题
     * @param answer     答案
     * @param answertime 答案时长
     * @param type       答案类型1：语音，2：文字，3：图片
     * @param sort       排序
     * @param createUser 操作人
     * @return
     */
    public boolean updateQuestionAnswer(long id, long courseid, String question, String answer, int answertime, int type, int sort, long createUser) {
        String sql = " UPDATE microclass_question_answer " +
                "SET courseid  = ?, question_content = ?, answer = ?, answertime = ?, answertype = ?, sort = ?, delflag = ?, " +
                "  modify_time = ?, modify_user = ? " +
                "WHERE id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(courseid);
        params.add(question);
        params.add(answer);
        params.add(answertime);
        params.add(type);
        params.add(sort);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(createUser);
        params.add(id);
        return update(sql, params) > 0;
    }

    /**
     * 删除答疑
     *
     * @param id
     * @return
     */
    public boolean delQuestionAnswer(long id) {
        String sql = " UPDATE microclass_question_answer SET delflag=1 WHERE id=? ";
        return update(sql, id) > 0;
    }

    /**
     * 医生讲师列表,医生讲师一一对应,优先取讲师,讲师存在则对应的医生不取
     *
     * @param nameOrPhone 名字或者手机号码
     * @return
     */
    public List<Map<String, Object>> doctorList(String nameOrPhone) {
        String sql = "  " +
                "select id, concat(name, '_', phone) name, phone phone  " +
                "from lecturer_info  " +
                "where length(ifnull(phone, '')) > 6  and ( name like ? or phone like ? ) " +
                "union  all " +
                "select di.doctorid as id, concat(doc_name, '_', doo_tel) as name, doo_tel as phone  " +
                "from doctor_info di  " +
                "       left join lecturer_info li on di.doo_tel = li.phone  " +
                "where li.id is null  " +
                "  and length(ifnull(di.doo_tel, '')) > 6 and ( di.doc_name like ? or di.doo_tel like ? ) and ifnull(di.delflag,0)=0 and di.examine=2 ";
        List<Object> params = new ArrayList<>();
        params.add(String.format("%%%s%%", nameOrPhone));
        params.add(String.format("%%%s%%", nameOrPhone));
        params.add(String.format("%%%s%%", nameOrPhone));
        params.add(String.format("%%%s%%", nameOrPhone));
        return queryForList(sql, params);
    }

    /**
     * 将医生数据复制到讲师
     *
     * @param createUser 创建人
     * @param phone      手机号码
     * @return
     */
    public long copyDoctor(long createUser, String phone, String docPhotoUrl) {
        String sql = "   insert into lecturer_info (doctorid,name, photo, phone, title_name, hospital, department,expertise, abstract, create_time, create_user)   " +
                " select doctorid," +
                "        doc_name,   " +
                "        ?,   " +
                "        doo_tel,   " +
                "        cdt.value, " +
                "        work_inst_name, " +
                "        cd.value, " +
                "        professional, " +
                "        introduction, " +
                "        ?,   " +
                "        ?   " +
                " from doctor_info  di left join code_doctor_title cdt on di.title_id=cdt.id left join code_department cd on di.department_id=cd.id " +
                " where doo_tel = ? and ifnull(di.delflag,0)=0 limit 1 ";
        List<Object> params = new ArrayList<>();
        params.add(docPhotoUrl);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(createUser);
        params.add(phone);
        return insert(sql, params, "id");
    }

    /**
     * 课程分类详情
     *
     * @param id
     * @return
     */
    public Map<String, Object> getCourseType(long id) {
        String sql = " select id,name,sort from microclass_type where id=? and ifnull(delflag,0)=0 order by create_time desc";
        return queryForMap(sql, id);
    }

    /**
     * 课程答疑详情
     *
     * @param id 答疑id
     * @return
     */
    public Map<String, Object> getQuestionAnswer(long id) {
        String sql = " SELECT id,courseid,question_content as questioncontent,answer,answertime,answertype,sort,create_time as createtime FROM microclass_question_answer WHERE id= ? ";
        return queryForMap(sql, id);
    }

    /**
     * 将课程重复排序的清空
     *
     * @param sort
     */
    public void updateCourseSort(int sort) {
        String sql = " update microclass_course set sort =0 where sort=? ";
        update(sql, sort);
    }


    /**
     * 将大纲重复排序的清空
     *
     * @param sort
     */
    public void updateOutlineSort(int sort) {
        String sql = " update microclass_outline set sort =0 where sort=? ";
        update(sql, sort);
    }

    /**
     * 将课程分类重复排序的清空
     *
     * @param sort
     */
    public void updateCourseTypeSort(int sort) {
        String sql = " update microclass_type set sort =0 where sort=? ";
        update(sql, sort);
    }

    /**
     * 将答疑重复排序的清空
     *
     * @param sort
     */
    public void updateQuestionAnswerSort(int sort) {
        String sql = " update microclass_question_answer set sort =0 where sort=? ";
        update(sql, sort);
    }

    public boolean updateCourseBrowsenum(long id) {
        String sql = " update microclass_course set browsenum=browsenum+1 where id=? ";
        return update(sql, id) > 0;
    }
}
