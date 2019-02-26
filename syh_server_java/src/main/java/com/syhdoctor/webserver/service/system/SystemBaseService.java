package com.syhdoctor.webserver.service.system;

import com.syhdoctor.common.utils.EnumUtils.BasicsTypeEnum;
import com.syhdoctor.common.utils.EnumUtils.RegisterChannelEnum;
import com.syhdoctor.common.utils.EnumUtils.TypeNameAppPushEnum;
import com.syhdoctor.common.utils.*;
import com.syhdoctor.common.utils.http.HttpUtil;
import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.mapper.system.SystemMapper;
import com.syhdoctor.webserver.service.code.CodeBaseService;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.jms.Destination;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public abstract class SystemBaseService extends BaseService {

    @Autowired
    private SystemMapper systemMapper;

    @Autowired
    private CodeBaseService codeBaseService;

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;


    public boolean addPushSendRule(long orderId) {
        return systemMapper.addPushSendRule(orderId);
    }

    /**
     * 添加app push消息数据
     *
     * @param title       标题
     * @param content     内容
     * @param type        类型
     * @param typeName
     * @param receiveId   接收人ID
     * @param receiveType 接收人类型
     * @return
     */
    public boolean addPushApp(String title, String content, int type, String typeName, long receiveId, int receiveType, int platform,
                              String xgtoken) {
        //todo 康养云推送
        if (receiveType == 1) {
            if (type == TypeNameAppPushEnum.phoneOrderDetail.getCode() || type == TypeNameAppPushEnum.departmentCallFailUserOrder.getCode() || type == TypeNameAppPushEnum.departmentCallSuccessUserOrder.getCode()) {
                String url = "";
                Map<String, Object> user = systemMapper.getUser(receiveId);
                //订单支付成功	你于2018-10-13 10:14:42的急诊订单支付成功,请保持通讯畅通,等待电话呼叫	2	436	73	1
                if (RegisterChannelEnum.Kangyang.getCode() == ModelUtil.getInt(user, "register_channel")) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("userid", ModelUtil.getStr(user, "userid"));
                    params.put("title", title);
                    params.put("content", content);
                    params.put("type", String.valueOf(type));
                    params.put("typename", ConfigModel.WEBLINKURL + String.format("web/syhdoctor/#/inquirydetail/%s", typeName));
                    String s = JsonUtil.getInstance().gsonToJson(params);
                    HttpUtil.getInstance().post(JumpLink.KANGYANG_XG_URL, s);
                }
            }
        }
        return systemMapper.addPushApp(title, content, type, typeName, receiveId, receiveType, platform, xgtoken);
    }


//    public static void main(String[] args) {
//        Map<String, Object> params = new HashMap<>();
//        params.put("userid", "ae558b11443f4cbf815091c29bb22fd0");
//        params.put("title", "订单支付成功");
//        params.put("content", "你于2018-10-13 10:14:42的急诊订单支付成功,请保持通讯畅通,等待电话呼叫");
//        params.put("type", "2");
//        params.put("typename", "https://www.syhdoctor.com/web/syhdoctor/#/inquirydetail/884");
//        String s = JsonUtil.getInstance().gsonToJson(params);
//        Map<String, Object> map = HttpUtil.getInstance().post("http://112.124.70.173:5015/kangyang/api/pushData", s);
//        System.out.println(map);
//    }

    /**
     * 添加app push消息数据
     *
     * @param title       标题
     * @param content     内容
     * @param type        类型
     * @param typeName
     * @param receiveId   接收人ID
     * @param receiveType 接收人类型
     * @return
     */
    public void addPushApp1(String title, String content, int type, String typeName, long receiveId, int receiveType, int platform,
                            String xgtoken) {
        Destination destination = new ActiveMQQueue("AppPush");
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("content", content);
        map.put("type", type);
        map.put("typename", typeName);
        map.put("receiveid", receiveId);
        map.put("receivetype", receiveType);
        map.put("platform", platform);
        map.put("xgtoken", xgtoken);
        String message = JsonUtil.getInstance().gsonToJson(map);
        jmsMessagingTemplate.convertAndSend(destination, message);
    }

    public boolean updateUserXgToken(String xgToken, int platform, int userId) {
        return systemMapper.updateUserXgToken(xgToken, platform, userId);
    }

    public boolean updateDoctorXgToken(String xgToken, int platform, int doctorId) {
        return systemMapper.updateDoctorXgToken(xgToken, platform, doctorId);
    }

    /**
     * 查询字典表
     *
     * @param type drugPackage(1, "药品包装单位"),
     *             drugUsage(2, "药品用法"),
     *             drugCycle(3, "药品周期"),
     *             graphicOrderStatus(4, "医生咨询(图文)订单状态"),
     *             phoneOrderStatus(5, "急诊(电话)订单状态"),
     *             phoneAnswerStatus(6, "急诊(电话)接听状态"),
     *             messageType(7, "消息中心消息类型"),
     * @return
     */
    public List<Map<String, Object>> getBasicsList(int type) {
        return systemMapper.getBasicsList(type);
    }

    /**
     * HomePage(17,"首页-图文资讯-支付详情页面"),
     * HomePhone(18,"首页-电话咨询-支付详情页面"),
     * ExpertPage(19,"专家详情-图文咨询-支付详情页面"),
     * ExpertPhone(20,"专家详情-电话咨询-支付详情页面"),
     * ExpertVideo(21,"专家详情-视频咨询-支付详情页面"),
     *
     * @return
     */
    public Map<String, Object> getHomePage(int type) {
        Map<String, Object> map = systemMapper.getHomePage(type);
        if (map != null) {
            String tips = ModelUtil.getStr(map, "tips").replaceAll("&&", "\n");
            map.put("tips", tips);
        }
        return map;
    }

    /**
     * 提现文案
     * <p>
     * ExtractOne(22,"提现文案（前）"),
//     * ExtractTwo(23,"提现文案（后）"),
     * ExtractMoney(24,"医生提现最低金额800"),
     *
     * @return
     */
    public Map<String, Object> getExtractPage() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> qianmap = systemMapper.getHomePage(BasicsTypeEnum.ExtractOne.getCode());
        Map<String, Object> moneymap = systemMapper.getHomePage(BasicsTypeEnum.ExtractMoney.getCode());
        BigDecimal money = PriceUtil.findPrice(ModelUtil.getLong(moneymap, "tips"));
        String txt = String.format(ModelUtil.getStr(qianmap, "tips"),money);
        String tips = txt.replaceAll("&&", "\n");
        map.put("money", money);
        map.put("text", tips);
        return map;
    }


    public Map<String, Object> getAres(Map<String, Object> map, String areas) {
        if (!StrUtil.isEmpty(areas)) {
            List<Long> areaList = ModelUtil.splitStrToList(areas);
            if (areaList.size() > 0) {
                Long provinceId = areaList.get(0);
                map.put("province", codeBaseService.getArea(provinceId.intValue()));
            } else {
                map.put("province", new HashMap<>());
            }
            if (areaList.size() > 1) {
                Long cityId = areaList.get(1);
                map.put("city", codeBaseService.getArea(cityId.intValue()));
            } else {
                map.put("city", new HashMap<>());
            }
            if (areaList.size() > 2) {
                Long areaId = areaList.get(2);
                map.put("area", codeBaseService.getArea(areaId.intValue()));
            } else {
                map.put("area", new HashMap<>());
            }
        }
        return map;
    }

    public String getAres(String areas) {
        return codeBaseService.getArea(areas);
    }

    /**
     * 添加消息
     *
     * @param url            医生、用户头像
     * @param name           展示标题
     * @param type           1 用户 2 医生
     * @param typeName       根据不同类型填写不同数据
     * @param messageType    消息类型
     * @param sendId         发送给谁
     * @param messageText    消息文本
     * @param messageSubtext 消息副文本
     * @return
     */
    public boolean addMessage(String url, String name, int type, String typeName, int messageType, long sendId, String messageText, String messageSubtext) {
        return systemMapper.addMessage(url, name, type, typeName, messageType, sendId, messageText, messageSubtext);
    }

    /**
     * 更改是否已读
     *
     * @param id
     * @return
     */
    public boolean updateMessageRead(int id) {
        return systemMapper.updateMessageRead(id);
    }
}
