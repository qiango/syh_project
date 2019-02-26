package com.syhdoctor.webserver.thirdparty.kangyang;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.kangyang.KangyangService;
import com.syhdoctor.webserver.utils.IdCardVerCheckUtil;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Api(description = "乐养云")
@Controller
@RequestMapping("/Kangyang")
public class KangyangController extends BaseController {

    @Autowired
    private KangyangService kangyangService;

    @ApiOperation(value = "导入用户数据")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "filepath", value = "文件全路径", dataType = "String"),
    })
    @RequestMapping("importKangyangUser")
    public void importKangyangUser(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        String filepath = ModelUtil.getStr(params, "filepath");
        kangyangService.importKangyangUser(filepath, 0, 0, 9, 1);
    }

    @ApiOperation(value = "外部跳转")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "phone", value = "手机号码", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "jumptype", value = "跳转类型", dataType = "String"),
    })
    @RequestMapping("jump")
    public String jump(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        String phone = ModelUtil.getStr(params, "phone");
        int jumptype = ModelUtil.getInt(params, "jumptype");
        return "redirect:" + kangyangService.jump(phone, jumptype);
    }


    @ApiOperation(value = "康养云用户注册")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "phone", value = "手机号码", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "name", value = "姓名", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "headpic", value = "头像", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "user_id", value = "用户id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "birthday", value = "出生日期", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "age", value = "年龄", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "vip_grade", value = "会员等级", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "gender", value = "性别", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "cardno", value = "身份证号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "address", value = "详细地址", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "jumptype", value = "跳转类型", required = true, dataType = "String"),
    })

    @PostMapping("userRegister")
    @ResponseBody
    public Map<String, Object> userRegister(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String userid = ModelUtil.getStr(params, "user_id");
        String phone = ModelUtil.getStr(params, "phone");
        String name = ModelUtil.getStr(params, "name");
        String headpic = ModelUtil.getStr(params, "headpic");
        String birthday = ModelUtil.getStr(params, "birthday");
        int age = ModelUtil.getInt(params, "age");
        String vip_grade = ModelUtil.getStr(params, "vip_grade");
        String gender = ModelUtil.getStr(params, "gender");
        String address = ModelUtil.getStr(params, "address");
        String cardno = ModelUtil.getStr(params, "cardno");
        int jumptype = ModelUtil.getInt(params, "jumptype");
        if (StrUtil.isEmpty(userid)) {
            result.put("result", -1);
            result.put("message", "user_id is null");
            return result;
        }
        if (StrUtil.isEmpty(phone)) {
            result.put("result", -1);
            result.put("message", "phone is null");
            return result;
        }
        if (StrUtil.isEmpty(name)) {
            result.put("result", -1);
            result.put("message", "name is null");
            return result;
        }
        if (age == 0) {
            result.put("result", -1);
            result.put("message", "age is null");
            return result;
        }
        if (StrUtil.isEmpty(birthday)) {
            result.put("result", -1);
            result.put("message", "birthday is null");
            return result;
        }
        if (StrUtil.isEmpty(gender)) {
            result.put("result", -1);
            result.put("message", "gender is null");
            return result;
        }

        if (StrUtil.isEmpty(address)) {
            result.put("result", -1);
            result.put("message", "address is null");
            return result;
        }

        if (!IdCardVerCheckUtil.validate(cardno)) {
            result.put("result", -1);
            result.put("message", "cardno is null");
            return result;
        }

        if (jumptype == 0) {
            result.put("result", -1);
            result.put("jumptype", "jumptype is null");
            return result;
        }

        Map<String, Object> map = kangyangService.useInternetHospital(name, headpic, vip_grade, birthday, gender, cardno, address, phone, userid, age);
        if (ModelUtil.getInt(map, "flag") == 1) {
            result.put("result", 1);
            result.put("message", "注册成功");
            result.put("userid", ModelUtil.getStr(map, "userid"));
            result.put("jumpurl", kangyangService.jump(phone, jumptype));
        } else {
            result.put("result", 1);
            result.put("message", "修改成功");
            result.put("userid", ModelUtil.getStr(map, "userid"));
            result.put("jumpurl", kangyangService.jump(phone, jumptype));
        }
        return result;
    }
}
