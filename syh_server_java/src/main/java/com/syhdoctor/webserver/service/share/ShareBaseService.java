package com.syhdoctor.webserver.service.share;

import com.syhdoctor.common.utils.EnumUtils.JumpTypeEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.TextFixed;
import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.exception.ServiceException;
import com.syhdoctor.webserver.service.article.ArticleService;
import com.syhdoctor.webserver.service.microclass.MicroClassService;
import com.syhdoctor.webserver.service.mongo.MongoService;
import com.syhdoctor.webserver.service.specialistcounseling.SpecialistCounselingService;
import com.syhdoctor.webserver.service.specialspecialties.SpecialSpecialtiesService;
import com.syhdoctor.webserver.thirdparty.mongodb.entity.Share;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public abstract class ShareBaseService extends BaseService {

    @Autowired
    private MicroClassService microClassService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private MongoService mongoService;

    @Autowired
    private SpecialSpecialtiesService specialSpecialtiesService;

    @Autowired
    private SpecialistCounselingService specialistCounselingService;

    public Map<String, Object> getShareContent(long userid, int sharetype, long sharetypeid) {
        long shareid = mongoService.getId("mongodb_share");
        JumpTypeEnum value = JumpTypeEnum.getValue(sharetype);
        String title = "";
        String desc = "";
        String imgurl = "";
        String link = ConfigModel.APILINKURL + String.format("Menu/GetCode?target=%s-%s-%s-%s", sharetype, sharetypeid, userid, shareid);
        switch (value) {
            case CourseList:
                title = TextFixed.Share.course_list_tile;
                desc = TextFixed.Share.course_list_desc;
                imgurl = TextFixed.Share.course_list_imgurl;
                break;
            case CourseDetail:
                if (sharetypeid == 0) {
                    throw new ServiceException("大学id为空");
                }
                Map<String, Object> course = microClassService.getCourseDoctor(sharetypeid);
                title = ModelUtil.getStr(course, "name");
                String format = String.format("%s %s", ModelUtil.getStr(course, "doctorname"), ModelUtil.getStr(course, "titlename"));
                desc = StrUtil.isEmpty(format) ? "山屿海健康" : format;
                imgurl = ConfigModel.QINIULINK + ModelUtil.getStr(course, "doctorheadpic");
                break;
            case ArticleList:
                title = TextFixed.Share.article_list_tile;
                desc = TextFixed.Share.article_list_desc;
                imgurl = TextFixed.Share.article_list_imgurl;
                break;
            case ArticleDetail:
                if (sharetypeid == 0) {
                    throw new ServiceException("头条id为空");
                }
                Map<String, Object> article = articleService.getArticle(sharetypeid);
                title = ModelUtil.getStr(article, "title");
                desc = StrUtil.isEmpty(ModelUtil.getStr(article, "subtitle")) ? "山屿海健康" : ModelUtil.getStr(article, "subtitle");
                imgurl = ConfigModel.QINIULINK + ModelUtil.getStr(article, "articlepic");
                break;
            case DoctorList:
                break;
            case DoctorDetail:
                break;
            case AnswerOrder:
                break;
            case PhoneOrder:
                if (userid != 0) {
                    title = TextFixed.Share.phone_title;
                    desc = TextFixed.Share.phone_desc;
                    imgurl = TextFixed.Share.phone_imgurl;
                    link = ConfigModel.APILINKURL + String.format("Menu/GetCode?target=%s-%s-%s-%s", sharetype, sharetypeid, userid, shareid);
                } else {
                    throw new ServiceException("请先登录");
                }
                break;
            case PersonalCenter:
                title = TextFixed.Share.personalcenter_title;
                desc = TextFixed.Share.personalcenter_desc;
                imgurl = TextFixed.Share.personalcenter_imgurl;
                link = ConfigModel.APILINKURL + String.format("Menu/GetCode?target=%s-%s-%s-%s", sharetype, sharetypeid, userid, shareid);
                break;
            case SpecialtiesDetail:
                if (sharetypeid == 0) {
                    throw new ServiceException("专科id为空");
                }
                Map<String, Object> special = specialSpecialtiesService.getShareSpecial(sharetypeid);
                title = String.format(TextFixed.Share.specialties_detail_tile, ModelUtil.getStr(special, "typename"));
                String name = String.format(TextFixed.Share.specialties_detail_desc, ModelUtil.getStr(special, "name"));
                desc = StrUtil.isEmpty(name) ? "山屿海健康" : name;
                imgurl = TextFixed.Share.specialties_detail_imgurl;
                link = ConfigModel.APILINKURL + String.format("Menu/GetCode?target=%s-%s-%s-%s", sharetype, sharetypeid, userid, shareid);
                break;
            case CounselingDetail:
                if (sharetypeid == 0) {
                    throw new ServiceException("专病id为空");
                }
                Map<String, Object> specialCountDetail = specialistCounselingService.getSpecialCountDetail(sharetypeid);
                title = ModelUtil.getStr(specialCountDetail, "title");
                desc = "山屿海健康";
                imgurl = ConfigModel.QINIULINK + ModelUtil.getStr(specialCountDetail, "picture");
                link = ConfigModel.APILINKURL + String.format("Menu/GetCode?target=%s-%s-%s-%s", sharetype, sharetypeid, userid, shareid);
                break;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("desc", desc);
        result.put("imgurl", imgurl);
        result.put("link", link);
        result.put("shareid", shareid);
        return result;
    }

    public void addShareContent(long userid, int sharetype, long sharetypeid, long shareid) {
        //mongdb 分享记录
        Share share = new Share();
        share.setId(shareid);
        share.setPid(0);
        share.setShareType(sharetype);
        share.setShareTypeId(sharetypeid);
        share.setUsertype(1);
        share.setShareUserId(userid);
        mongoService.saveShare(share);
    }

    public long getId() {
        return mongoService.getId("mongodb_share");
    }
}
