package com.syhdoctor.webserver.service.user;

import com.syhdoctor.common.utils.EnumUtils.JumpTypeEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.common.utils.encryption.AESEncrypt;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.mapper.user.UserBaseMapper;
import com.syhdoctor.webserver.service.mongo.MongoService;
import com.syhdoctor.webserver.thirdparty.mongodb.entity.Share;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService extends UserBaseService {

    @Autowired
    private UserBaseMapper userMapper;

    @Autowired
    private MongoService mongoService;

    /**
     * state  自定义参数  {1}-{2}-{3}-{4} 格式 1：跳转类型，2：跳转类型id，3：分享人id，4:是否分享
     *
     * @param share
     * @param openid
     * @param source
     * @param opentype
     * @return
     */
    public String getRedirectUrl(String share, String openid, int source, int opentype) {
        Map<String, Object> sharedata = sharedata(share);
        long sharetypeid = ModelUtil.getLong(sharedata, "sharetypeid");
        long shareuserid = ModelUtil.getLong(sharedata, "shareuserid");
        int sharetype = ModelUtil.getInt(sharedata, "sharetype");
        int shareId = ModelUtil.getInt(sharedata, "shareid");
        long openId = addUpdateUserOpen(openid, source, opentype, shareuserid, shareId);
        long userid = getUserAccount(openid, opentype);
        // mongdb 分享链接的点击记录
        if (shareId > 0) {
            Share shareBean = new Share();
            long id = mongoService.getId("mongodb_share");
            shareBean.setId(id);
            shareBean.setPid(shareId);
            shareBean.setShareType(sharetype);
            shareBean.setShareTypeId(sharetypeid);
            shareBean.setUsertype(2);
            shareBean.setShareUserId(userid);
            shareBean.setOpenId(openId);
            mongoService.saveShare(shareBean);
        }


        JumpTypeEnum value = JumpTypeEnum.getValue(sharetype);
        String url = "";
        boolean islogin = true;
        switch (value) {
            case CourseList:
                islogin = false;
                url = String.format("university?uid=%s", AESEncrypt.getInstance().encrypt(String.valueOf(userid))) + (shareId > 0 ? "&isshare=1" : "");
                break;
            case CourseDetail:
                islogin = false;
                url = String.format("undetail?uid=%s&courseid=%s", AESEncrypt.getInstance().encrypt(String.valueOf(userid)), sharetypeid) + (shareId > 0 ? "&isshare=1" : "");
                break;
            case ArticleList:
                islogin = false;
                url = String.format("headline?uid=%s", AESEncrypt.getInstance().encrypt(String.valueOf(userid))) + (shareId > 0 ? "&isshare=1" : "");
                break;
            case ArticleDetail:
                islogin = false;
                url = String.format("headlinedetail?uid=%s&id=%s", AESEncrypt.getInstance().encrypt(String.valueOf(userid)), sharetypeid) + (shareId > 0 ? "&isshare=1" : "");
                break;
            case DoctorList:
                url = String.format("expertlist?uid=%s", AESEncrypt.getInstance().encrypt(String.valueOf(userid))) + (shareId > 0 ? "&isshare=1" : "");
                break;
            case DoctorDetail:
                break;
            case AnswerOrder:
                url = String.format("inquirytreatment?uid=%s", AESEncrypt.getInstance().encrypt(String.valueOf(userid))) + (shareId > 0 ? "&isshare=1" : "");
                break;
            case PhoneOrder:
                url = String.format("phonetreatment?uid=%s", AESEncrypt.getInstance().encrypt(String.valueOf(userid))) + (shareId > 0 ? "&isshare=1" : "");
                break;
            case PersonalCenter:
                url = String.format("personal?uid=%s", AESEncrypt.getInstance().encrypt(String.valueOf(userid))) + (shareId > 0 ? "&isshare=1" : "");
                break;
            case SpecialtiesDetail:
                url = String.format("specialdepartment?uid=%s&id=%s", AESEncrypt.getInstance().encrypt(String.valueOf(userid)), sharetypeid) + (shareId > 0 ? "&isshare=1" : "");
                break;
            case SpecialtiesDetailList:
                url = String.format("speciallist?uid=%s", AESEncrypt.getInstance().encrypt(String.valueOf(userid))) + (shareId > 0 ? "&isshare=1" : "");
                break;
            case CounselingDetail:
                url = String.format("specialadvisory?uid=%s&id=%s", AESEncrypt.getInstance().encrypt(String.valueOf(userid)), sharetypeid) + (shareId > 0 ? "&isshare=1" : "");
                break;
            case MDTSpecialtiesDetail:
                url = String.format("features?uid=%s&id=%s", AESEncrypt.getInstance().encrypt(String.valueOf(userid)), sharetypeid) + (shareId > 0 ? "&isshare=1" : "");
                break;
            case GreenChannel:
                url = String.format("greenregistered?uid=%s&id=%s", AESEncrypt.getInstance().encrypt(String.valueOf(userid)), sharetypeid) + (shareId > 0 ? "&isshare=1" : "");
                break;
            default:
                url = String.format("login?openid=%s&redirecturl=%s", openId, StrUtil.encode(url)) + (shareId > 0 ? "&isshare=1" : "");
                break;
        }
        if (userid == 0 && islogin) {
            String replace = url.replace("?uid=853407e18129ec2093eef2541debc796", "?1=1");
            url = String.format("login?openid=%s&redirecturl=%s", openId, StrUtil.encode(replace)) + (shareId > 0 ? "&isshare=1" : "");
        } else if (userid == 0) {
            url = url.replace("?uid=853407e18129ec2093eef2541debc796", "?1=1");
        }
        url = ConfigModel.WEBLINKURL + "web/syhdoctor/#/" + url;
        return url;
    }

    public static void main(String[] args) {
        System.out.println(AESEncrypt.getInstance().encrypt(String.valueOf(0)));
    }

    public Map<String, Object> sharedata(String state) {
        String[] split = state.split("-");
        //分享类型
        int shareType = 0;
        //分享类型id
        long shareTypeId = 0;
        //分享人
        long shareUserId = 0;
        //是否分享进入
        long shareId = 0;

        switch (split.length) {
            case 1:
                shareType = ModelUtil.strToInt(split[0], 0);
                break;
            case 2:
                shareType = ModelUtil.strToInt(split[0], 0);
                shareTypeId = ModelUtil.strToLong(split[1], 0);
                break;
            case 3:
                shareType = ModelUtil.strToInt(split[0], 0);
                shareTypeId = ModelUtil.strToLong(split[1], 0);
                shareUserId = ModelUtil.strToLong(split[2], 0);
                break;
            case 4:
                shareType = ModelUtil.strToInt(split[0], 0);
                shareTypeId = ModelUtil.strToLong(split[1], 0);
                shareUserId = ModelUtil.strToLong(split[2], 0);
                shareId = ModelUtil.strToInt(split[3], 0);
                break;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("sharetype", shareType);
        result.put("sharetypeid", shareTypeId);
        result.put("shareuserid", shareUserId);
        result.put("shareid", shareId);
        return result;
    }


    /**
     * 插入用户 通过UnionID机制获取用户信息
     *
     * @param openid openid
     * @param source 渠道
     */
    public long addUpdateUserOpen(String openid, int source, int opentype) {
        return addUpdateUserOpen(openid, source, opentype, 0, 0);
    }

    /**
     * 插入用户 通过UnionID机制获取用户信息
     *
     * @param openid openid
     * @param source 渠道
     */
    public long addUpdateUserOpen(String openid, int source, int opentype, long shareUserId, long shareId) {
        long id;
        Map<String, Object> result = userMapper.getUserOpenId(openid, opentype);
        log.info("开始时间" + UnixUtil.getDate(UnixUtil.getNowTimeStamp(), "yyyy-MM-dd HH:mm:ss"));
        if (result != null) {
            id = ModelUtil.getLong(result, "id");
            if (ModelUtil.getLong(result, "userid") != 0 && shareId != 0) {
                //设置分享人
                userMapper.updateUserOpenShareUserId(shareUserId, id);
            }
        } else {
            id = userMapper.addUserOpenid(openid, source, opentype);
            //设置分享人
            if (shareId != 0) {
                userMapper.updateUserOpenShareUserId(shareUserId, id);
            }
        }

        log.info("结束时间" + UnixUtil.getDate(UnixUtil.getNowTimeStamp(), "yyyy-MM-dd HH:mm:ss"));
        return id;
    }

    /**
     * 插入用户 通过UnionID机制获取用户信息
     *
     * @param openid openid
     */
    public long getUserAccount(String openid, int opentype) {
        long userid = 0;
        Map<String, Object> result = userMapper.getUserAccount(openid, opentype);
        log.info("开始时间" + UnixUtil.getDate(UnixUtil.getNowTimeStamp(), "yyyy-MM-dd HH:mm:ss"));
        if (result != null) {
            userid = ModelUtil.getLong(result, "userid", 0);
        }
        log.info("结束时间" + UnixUtil.getDate(UnixUtil.getNowTimeStamp(), "yyyy-MM-dd HH:mm:ss"));
        return userid;
    }

    public boolean updateUserOpen(long id, int issubscribe, int opentype) {
        return userMapper.updateUserOpen(id, issubscribe, opentype);
    }

    public Map<String, Object> getOpenId(long userId, int opentype) {
        return userMapper.getOpenId(userId, opentype);
    }

}
