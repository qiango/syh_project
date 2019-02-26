package com.syhdoctor.webserver.mapper.system;

import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class SystemBaseMapper extends BaseMapper {


    public boolean addPushSendRule(long orderId) {
        String sql = "insert into sending_order_rules (orderid, halfhour, onehour, threehour, sixhour, twelvehour, eighteenhour, twentyhour) " +
                "    value (?, ?, ?, ?, ?, ?, ?, ?)";
        List<Object> params = new ArrayList<>();
        params.add(orderId);
        params.add(false);
        params.add(true);
        params.add(true);
        params.add(true);
        params.add(true);
        params.add(true);
        params.add(true);
        return update(sql, params) > 0;
    }

    /**
     * 添加push消息表
     *
     * @return
     */
    public boolean addPushApp(String title, String content, int type, String typeName, long receiveId, int receiveType, int platform,
                              String xgtoken) {
        String sql = "insert into push_app(title, content, type, typename, receiveid, receivetype, delflag, create_time,ispush,platform,xgtoken)" +
                "values (?,?,?,?,?,?,?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(title);
        params.add(content);
        params.add(type);
        params.add(typeName);
        params.add(receiveId);
        params.add(receiveType);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(0);
        params.add(platform);
        params.add(xgtoken);
        return update(sql, params) > 0;
    }

    /**
     * 更新用户的信鸽的token
     *
     * @param xgToken
     * @param platform
     * @param userId
     * @return
     */
    public boolean updateUserXgToken(String xgToken, int platform, int userId) {
        String sql = "update user_account set xg_token=?,platform=? where id=?";
        List<Object> params = new ArrayList<>();
        params.add(xgToken);
        params.add(platform);
        params.add(userId);
        return update(sql, params) > 0;
    }

    /**
     * 更新医生的信鸽的token
     *
     * @param xgToken
     * @param platform
     * @param doctorId
     * @return
     */
    public boolean updateDoctorXgToken(String xgToken, int platform, int doctorId) {
        String sql = "update doctor_extends set xg_token=?,platform=? where doctorid=?";
        List<Object> params = new ArrayList<>();
        params.add(xgToken);
        params.add(platform);
        params.add(doctorId);
        return update(sql, params) > 0;
    }


    /**
     * 查询字典表
     *
     * @param type
     * @return
     */
    public List<Map<String, Object>> getBasicsList(int type) {
        String sql = "select id,name as value from basics where ifnull(delflag,0)=0 and type=?";
        List<Object> params = new ArrayList<>();
        params.add(type);
        return queryForList(sql, params);
    }

    /**
     * 支付成功页面文案
     *
     * @return
     */
    public Map<String, Object> getHomePage(int type) {
        String sql = " select id,name tips from basics where ifnull(delflag,0)=0 and type = ? ";
        return queryForMap(sql, type);
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
        String sql = "insert into message(url,name, type, type_name, message_type, sendid, message_text, message_subtext, delflag, create_time) " +
                "values (?,?,?,?,?,?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(url);
        params.add(name);
        params.add(type);
        params.add(typeName);
        params.add(messageType);
        params.add(sendId);
        params.add(messageText);
        params.add(messageSubtext);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        return update(sql, params) > 0;
    }

    /**
     * 更改是否已读
     *
     * @param id
     * @return
     */
    public boolean updateMessageRead(int id) {
        String sql = " update message set `read`=1 where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        return update(sql, params) > 0;
    }

    public Map<String, Object> getUser(long userId) {
        String sql = " select kangyang_userid userid,register_channel from user_account where ifnull(delflag,0)=0 and id=? ";
        return queryForMap(sql, userId);
    }

}
