package com.syhdoctor.webserver.controller.webadmin.groupsms;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.groupsms.GroupMessageService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description = "/Admin/groupsms 群发信息")
@RestController
@RequestMapping("/Admin/groupsms")
public class AdminGroupMessageController extends BaseController {

    @Autowired
    private GroupMessageService groupMessageService;


    @ApiOperation(value = "群发")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "type", value = "1用户，2医生", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "messagetext", value = "消息文本", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "typename", value = "typename", required = true, dataType = "String"),
    })
    @PostMapping("/addGroupMessageAll")
    public Map<String, Object> addGroupMessageAll(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int type = ModelUtil.getInt(params, "type");
        String messagetext = ModelUtil.getStr(params, "messagetext");
        String typename = ModelUtil.getStr(params,"typename");
        //if (type == 1 || type == 2) {
        result.put("data", groupMessageService.addGroupMessageAll(type, messagetext,typename));
        setOkResult(result, "新增成功");
        //} else {
        //  setErrorResult(result, "参数错误");
        // }
        return result;
    }


    @ApiOperation(value = "单发")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "type", value = "1用户，2医生", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "messagetext", value = "消息文本", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "id", value = "id集合", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "typename", value = "typename", required = true, dataType = "String"),
    })
    @PostMapping("/addGroupMessageId")
    public Map<String, Object> addGroupMessageId(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int type = ModelUtil.getInt(params, "type");
        String messagetext = ModelUtil.getStr(params, "messagetext");
        List<?> id = null;
        if(type == 1){
            id = ModelUtil.getList(params,"usertypename",new ArrayList<>());
        }else{
            id = ModelUtil.getList(params,"doctortypename",new ArrayList<>());
        }
        String typename = ModelUtil.getStr(params,"typename");
        //String byid = ModelUtil.getStr(params,"id");
        //String a []=byid.split(",");
        if (type == 1 || type == 2) {
        result.put("data", groupMessageService.addGroupMessageId(type, messagetext, id,typename));
        setOkResult(result, "新增成功");
         } else {
          setErrorResult(result, "参数错误");
         }
        return result;
    }

    @ApiOperation(value = "用户姓名下拉框")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "name", value = "姓名", required = true, dataType = "String"),
    })
    @PostMapping("/userName")
    public Map<String,Object> userName(@ApiParam(hidden = true) @RequestParam Map<String, Object> params){
        Map<String,Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params,"id");
        String name = ModelUtil.getStr(params,"name");
        int pageIndex = ModelUtil.getInt(params,"pageIndex",1);
        int pageSize = ModelUtil.getInt(params,"pageSize",20);
        result.put("data",groupMessageService.userName(id,name,pageIndex,pageSize));
        result.put("total",groupMessageService.userNameCount(id,name));
       setOkResult(result,"查询成功");
        return result;
    }

    @ApiOperation(value = "医生姓名下拉框")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "name", value = "姓名", required = true, dataType = "String"),
    })
    @PostMapping("/doctorName")
    public Map<String,Object> doctorName(@ApiParam(hidden = true) @RequestParam Map<String, Object> params){
        Map<String,Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params,"id");
        String name = ModelUtil.getStr(params,"name");
        int pageIndex = ModelUtil.getInt(params,"pageIndex",1);
        int pageSize = ModelUtil.getInt(params,"pageSize",20);
        result.put("data",groupMessageService.doctorName(id,name,pageIndex,pageSize));
        result.put("total",groupMessageService.doctorNameCount(id,name));
        setOkResult(result,"查询成功");
        return result;
    }

    @ApiOperation(value = "所有姓名下拉框")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "type", value = "1用户，2医生", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "name", value = "姓名", required = true, dataType = "String"),
    })
    @PostMapping("/nameAll")
    public Map<String,Object> nameAll(@ApiParam(hidden = true) @RequestParam Map<String, Object> params){
        Map<String,Object> result = new HashMap<>();
        int type = ModelUtil.getInt(params, "type");
        long id = ModelUtil.getLong(params,"id");
        String name = ModelUtil.getStr(params,"name");
        int pageIndex = ModelUtil.getInt(params,"pageIndex",1);
        int pageSize = ModelUtil.getInt(params,"pageSize",20);
        if(type == 0){
            setErrorResult(result,"参数错误");
        }else{
            result.put("data",groupMessageService.nameAll(type,id,name,pageIndex,pageSize));
//            if(type == 1){
//                result.put("total",groupMessageService.userNameCount(id,name));
//            }else if(type == 2){
//                result.put("total",groupMessageService.doctorNameCount(id,name));
//            }
            setOkResult(result,"查询成功");
        }
        return result;
    }





}
