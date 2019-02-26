package com.syhdoctor.webserver.controller.webadmin.microclass;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.microclass.MicroClassService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(description = "/Admin/MicroClass 微课后台")
@RestController
@RequestMapping("/Admin/MicroClass")
public class AdminMicroClassController extends BaseController {

    @Autowired
    private MicroClassService service;


    @ApiOperation(value = "课程列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = "医生名字", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "id", value = "分类id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/courseList")
    public Map<String, Object> courseList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String name = ModelUtil.getStr(params, "name");
        long typeId = ModelUtil.getLong(params, "id");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", service.getAdminCourseList(name, typeId, pageIndex, pageSize));
        result.put("total", service.courseCount(name, typeId));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "课程列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = "课程名字", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/courseListBySelect")
    public Map<String, Object> courseListBySelect(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String name = ModelUtil.getStr(params, "name");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", service.getAdminCourseList(name, pageIndex, pageSize));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "课程详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", required = true, dataType = "String"),
    })
    @PostMapping("/getCourse")
    public Map<String, Object> getCourse(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", service.getAdminCourse(id));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "医生列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = "医生名字", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/doctorList")
    public Map<String, Object> doctorList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String nameOrPhone = ModelUtil.getStr(params, "name", "");
        result.put("data", service.doctorList(nameOrPhone));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "添加修改课程")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "课程id（空为添加，不空修改）", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "name", value = "课程名字", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctor", value = "医生号码", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "type", value = "分类id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "details", value = "详细信息（富文本）", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "coursewarevoice", value = "课件（语音）", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "coursewarevideo", value = "课件（视频）", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "coursewareposter", value = "封面图", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "coursewaretime", value = "课件时长", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "coursewaretype", value = "课件类型1：语音，2：视频", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "img", value = "大图", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "smallimg", value = "小图", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "test", value = "是否测试：0是，1不是", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "sort", value = "排序", dataType = "String"),
    })
    @PostMapping("/addUpdateCourse")
    public Map<String, Object> addUpdateCourse(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        String name = ModelUtil.getStr(params, "name");
        String phone = ModelUtil.getStr(params, "doctor");
        long typeId = ModelUtil.getLong(params, "type");
        String details = ModelUtil.getStr(params, "details");
        String coursewarevoice = ModelUtil.getStr(params, "coursewarevoice");
        String coursewarevideo = ModelUtil.getStr(params, "coursewarevideo");
        String coursewareposter = ModelUtil.getStr(params, "coursewareposter");
        String img = ModelUtil.getStr(params, "img");
        String smallimg = ModelUtil.getStr(params, "smallimg");
        int coursewaretime = ModelUtil.getInt(params, "coursewaretime", 0);
        int coursewaretype = ModelUtil.getInt(params, "coursewaretype", 0);
        int test = ModelUtil.getInt(params, "test", 1);
        int sort = ModelUtil.getInt(params, "sort", 0);
        long createUser = ModelUtil.getLong(params, "agentid");
        if (StrUtil.isEmpty(name, phone, smallimg) || typeId == 0 || coursewaretype == 0 || (StrUtil.isEmpty(coursewarevoice) && StrUtil.isEmpty(coursewarevideo))) {
            setErrorResult(result, "参数错误");
        } else {
            String courseware = null;
            if (coursewaretype == 1) {
                courseware = coursewarevoice;
            } else if (coursewaretype == 2) {
                courseware = coursewarevideo;
            }
            boolean flag = service.addUpdateCourse(id, name, phone, typeId, details, courseware, coursewareposter, img, smallimg, coursewaretime, coursewaretype, test, sort, createUser);
            if (flag) {
                setOkResult(result, "添加成功");
            } else {
                setErrorResult(result, "添加失败");
            }
        }
        return result;
    }

    @ApiOperation(value = "课程大纲列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "title", value = "标题", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "courseid", value = "课程id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/outlineList")
    public Map<String, Object> ourlineList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long courseid = ModelUtil.getLong(params, "courseid");
        String title = ModelUtil.getStr(params, "title");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", service.ourlineList(title, courseid, pageIndex, pageSize));
        result.put("total", service.ourlineCount(title, courseid));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "课程大纲详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", dataType = "String"),
    })
    @PostMapping("/getOutline")
    public Map<String, Object> getOutline(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", service.getOutline(id));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "添加修改课程大纲")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "课程大纲id（空为添加，不空修改）", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "courseid", value = "课程id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "title", value = "标题", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "content", value = "内容", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "contenttime", value = "内容时长", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "sort", value = "排序", dataType = "String"),
    })
    @PostMapping("/addUpdateOutline")
    public Map<String, Object> addUpdateOutline(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        long courseid = ModelUtil.getLong(params, "courseid");
        String title = ModelUtil.getStr(params, "title");
        String content = ModelUtil.getStr(params, "content");
        int contenttime = ModelUtil.getInt(params, "contenttime", 0);
        int sort = ModelUtil.getInt(params, "sort", 0);
        long createUser = ModelUtil.getLong(params, "agentid");
        if (StrUtil.isEmpty(title) || StrUtil.isEmpty(content)) {
            setErrorResult(result, "参数错误");
        } else {
            boolean flag = service.addUpdateOutline(id, courseid, title, content, contenttime, sort, createUser);
            if (flag) {
                setOkResult(result, "添加成功");
            } else {
                setErrorResult(result, "添加失败");
            }
        }
        return result;
    }

    @ApiOperation(value = "删除课程大纲")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "课程大纲id", required = true, dataType = "String"),
    })
    @PostMapping("/delOutline")
    public Map<String, Object> delOutline(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            boolean flag = service.delOutline(id);
            if (flag) {
                setOkResult(result, "删除成功");
            } else {
                setErrorResult(result, "删除失败");
            }
        }
        return result;
    }


    @ApiOperation(value = "删除课程")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "课程id", required = true, dataType = "String"),
    })
    @PostMapping("/delCourse")
    public Map<String, Object> delCourse(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            boolean flag = service.delCourse(id);
            if (flag) {
                setOkResult(result, "删除成功");
            } else {
                setErrorResult(result, "删除失败");
            }
        }
        return result;
    }

    @ApiOperation(value = "课程添加编辑时显示的课程分类列表")
    @PostMapping("/courseTypeList")
    public Map<String, Object> typeList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", service.getCourseTypeList());
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "课程分类列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = "分类名字", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/courseTypePageList")
    public Map<String, Object> typePageList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String name = ModelUtil.getStr(params, "name");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", service.getCourseTypeList(name, pageIndex, pageSize));
        result.put("total", service.courseTypeCount(name));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "课程分类详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "分类id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getCourseType")
    public Map<String, Object> getCourseType(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        result.put("data", service.getCourseType(id));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "添加修改课程分类")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "课程分类id（空为添加，不空修改）", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "name", value = "课程分类名字", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "sort", value = "排序", dataType = "String"),
    })
    @PostMapping("/addUpdateCourseType")
    public Map<String, Object> addUpdateCourseType(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        String name = ModelUtil.getStr(params, "name");
        int sort = ModelUtil.getInt(params, "sort", 0);
        long createUser = ModelUtil.getLong(params, "agentid");
        if (StrUtil.isEmpty(name)) {
            setErrorResult(result, "参数错误");
        } else {
            boolean flag = service.addUpdateCourseType(id, name, sort, createUser);
            if (flag) {
                setOkResult(result, "添加成功");
            } else {
                setErrorResult(result, "添加失败");
            }
        }
        return result;
    }

    @ApiOperation(value = "删除课程分类")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "课程分类id", required = true, dataType = "String"),
    })
    @PostMapping("/delCourseType")
    public Map<String, Object> delCourseType(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            boolean flag = service.delCourseType(id);
            if (flag) {
                setOkResult(result, "删除成功");
            } else {
                setErrorResult(result, "该分类下有课程不能删除");
            }
        }
        return result;
    }

    @ApiOperation(value = "课程答疑列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "questioncontent", value = "问题", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "courseid", value = "课程id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/questionAnswerList")
    public Map<String, Object> questionAnswerList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String question = ModelUtil.getStr(params, "questioncontent");
        long courseid = ModelUtil.getLong(params, "courseid");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", service.questionAnswerList(question, courseid, pageIndex, pageSize));
        result.put("total", service.questionAnswerCount(question, courseid));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "课程答疑详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "课程id", required = true, dataType = "String"),
    })
    @PostMapping("/getQuestionAnswer")
    public Map<String, Object> getQuestionAnswer(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        result.put("data", service.getQuestionAnswer(id));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "添加修改答疑")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "答疑id（空为添加，不空修改）", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "courseid", value = "课程id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "questioncontent", value = "问题", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "answer", value = "答疑", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "answertime", value = "时长", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "answertype", value = "答疑类型1：语音,2:文字,3:图片", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "sort", value = "排序", dataType = "String"),
    })
    @PostMapping("/addUpdateQuestionAnswer")
    public Map<String, Object> addUpdatequestionAnswer(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        long courseid = ModelUtil.getLong(params, "courseid");
        String question = ModelUtil.getStr(params, "questioncontent");
        String answer = ModelUtil.getStr(params, "answer");
        int answertime = ModelUtil.getInt(params, "answertime", 0);
        int type = ModelUtil.getInt(params, "answertype", 0);
        int sort = ModelUtil.getInt(params, "sort", 0);
        long createUser = ModelUtil.getLong(params, "agentid");
        if (StrUtil.isEmpty(question) || courseid == 0 || StrUtil.isEmpty(answer) || type == 0) {
            setErrorResult(result, "参数错误");
        } else {
            boolean flag = service.addUpdateQuestionAnswer(id, courseid, question, answer, answertime, type, sort, createUser);
            if (flag) {
                setOkResult(result, "添加成功");
            } else {
                setErrorResult(result, "添加失败");
            }
        }
        return result;
    }

    @ApiOperation(value = "删除答疑")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "答疑id", required = true, dataType = "String"),
    })
    @PostMapping("/delquestionAnswer")
    public Map<String, Object> delquestionAnswer(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            boolean flag = service.delQuestionAnswer(id);
            if (flag) {
                setOkResult(result, "删除成功");
            } else {
                setErrorResult(result, "删除失败");
            }
        }
        return result;
    }
}
