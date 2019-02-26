package com.syhdoctor.webtask.doctor.mapper;

import com.syhdoctor.common.utils.EnumUtils.AnswerOrderStateEnum;
import com.syhdoctor.common.utils.EnumUtils.MoneyTypeEnum;
import com.syhdoctor.common.utils.EnumUtils.QAContentTypeEnum;
import com.syhdoctor.common.utils.EnumUtils.TransactionTypeStateEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.PriceUtil;
import com.syhdoctor.common.utils.TextFixed;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webtask.base.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class DoctorOrderMapper extends BaseMapper {
    public List<Map<String, Object>> getPhoneOrderList() {
        String sql = " select d.id,order_no orderno, doctor_phone doctorphone, user_phone userphone, status " +
                "from ( " +
                "       select min(mid.id) id " +
                "       from (select min(id) id " +
                "             from doctor_phone_order " +
                "             where status in (2, 3) " +
                "               and callnum < 3 " +
                "               and ifnull(subscribe_time,0)< unix_timestamp() * 1000 " +
                "             group by doctor_phone " +
                "             limit 10) mid " +
                "              left join doctor_phone_order d on mid.id = d.id " +
                "       group by user_phone " +
                "     ) mid " +
                "       left join doctor_phone_order d on mid.id = d.id " +
                "order by ifnull(subscribe_time,0) " +
                "limit 10 ";
        return queryForList(sql);
    }

    public List<Map<String, Object>> getPhoneOrderFile() {
        String sql = " select id,order_no orderno ,record_url recordurl from doctor_phone_order where status in (4,6) and ifnull(delflag,0)=0 and ifnull(ispull,0)=0 ";
        return queryForList(sql);
    }

    /**
     * 录音文件
     *
     * @param orderId
     * @return
     */
    public boolean updatePhoneOrder(long orderId, String filePath) {
        String sql = "update doctor_phone_order set record_url=?,ispull=1 where id=?";
        List<Object> params = new ArrayList<>();
        params.add(filePath);
        params.add(orderId);
        return update(sql, params) > 0;
    }

    public void updatePhoneOrder(long id) {
        String sql = " update doctor_phone_order set callnum=callnum+1,status=3 where id=? ";
        update(sql, id);
    }

    public void updatePhoneOrderDiagnosis(long id) {
        String sql = " update doctor_phone_order set diagnosis=? where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(TextFixed.doctor_phone_order_auto_diagnosis);
        params.add(id);
        update(sql, params);
    }

    /**
     * 查询等待自动关闭的订单
     */
    public List<Map<String, Object>> getWaitCloseAnswerOrder() {
        String sql = "SELECT id,orderno,states,userid,doctorid,paytype,visitcategory,actualmoney,originalprice  " +
                "FROM doctor_problem_order " +
                "WHERE UNIX_TIMESTAMP() * 1000 - create_time > ? " +
                "  AND states in(2,6) " +
                "  and paystatus = 1 " +
                "  and ifnull(delflag, 0) = 0";
        List<Object> params = new ArrayList<>();
        params.add(TextFixed.auto_problem_order_time);
        return query(sql, params, (ResultSet res, int num) -> {
            Map<String, Object> map = resultToMap(res);
            if (map != null) {
                map.put("actualmoney", PriceUtil.findPrice(ModelUtil.getLong(map, "actualmoney")));
                map.put("originalprice", PriceUtil.findPrice(ModelUtil.getLong(map, "originalprice")));
            }
            return map;
        });
    }

    /**
     * 查询等待自动关闭的订单
     */
    public List<Map<String, Object>> getWaitCloseVideoOrder() {
        String sql = "SELECT id,orderno,status,userid,doctorid,userinto,doctorinto,paytype,visitcategory,actualmoney,originalprice  " +
                "FROM doctor_video_order " +
                "WHERE subscribe_end_time <= UNIX_TIMESTAMP() * 1000 " +
                "  AND status in (2,3) " +
                "  and paystatus = 1 " +
                "  and ifnull(delflag, 0) = 0";
        List<Object> params = new ArrayList<>();
        return query(sql, params, (ResultSet res, int num) -> {
            Map<String, Object> map = resultToMap(res);
            if (map != null) {
                map.put("actualmoney", PriceUtil.findPrice(ModelUtil.getLong(map, "actualmoney")));
                map.put("originalprice", PriceUtil.findPrice(ModelUtil.getLong(map, "originalprice")));
            }
            return map;
        });
    }


    /**
     * 自动关闭问诊订单
     */
    public boolean autoCloseProblemOrder(long id, int states, String remark) {
        String sql = "update doctor_problem_order set states=?,remark=? where id=? and ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(states);
        params.add(remark);
        params.add(id);
        return update(sql, params) > 0;
    }


    /**
     * 自动关闭急诊订单
     */
    public boolean autoClosePhoneOrder(long id, int states, String remark) {
        String sql = "update doctor_phone_order set status=?,order_remark=? where id=? and ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(states);
        params.add(remark);
        params.add(id);
        return update(sql, params) > 0;
    }

    /**
     * 自动关闭急诊订单
     */
    public boolean autoCloseVideoOrder(long id, int states, String remark) {
        String sql = "update doctor_video_order set status=?,remark=? where id=? and ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(states);
        params.add(remark);
        params.add(id);
        return update(sql, params) > 0;
    }

    /**
     * 添加语音回答
     *
     * @return
     */
    public long addAnswer(long userId, long doctorId, long orderId, String content, long contenttime, int contentType, int questionaAnswerType) {
        String sql = "   insert into doctor_answer (content,contenttime, userid, doctorid, orderid, delflag, create_time, status, contenttype,questionanswertype) " +
                " values (   " +
                "   ?, ?, ?, ?, ?, ?, ?, ?, ? , ?" +
                " )  ";
        List<Object> params = new ArrayList<>();
        params.add(content);
        params.add(contenttime);
        params.add(userId);
        params.add(doctorId);
        params.add(orderId);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(1);
        params.add(contentType);
        params.add(questionaAnswerType);
        return insert(sql, params, "id");
    }


    /**
     * 查询订单失败的，需要进行退款
     *
     * @param
     */
    public List<Map<String, Object>> getAnswerRefundOrderList() {
        String sql = " SELECT id,orderno,doctorid,userid,paytype,actualmoney,visitcategory FROM doctor_problem_order WHERE states=? and paystatus=1 and ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(AnswerOrderStateEnum.WaitRefund.getCode());
        return query(sql, params, (ResultSet rs, int rowNum) -> {
            Map<String, Object> data = resultToMap(rs);
            if (data != null) {
                data.put("actualmoney", PriceUtil.findPrice(ModelUtil.getLong(data, "actualmoney")));
            }
            return data;
        });
    }


    /**
     * 查询订单失败的，需要进行退款
     *
     * @param
     */
    public List<Map<String, Object>> getPhoneRefundOrderList() {
        String sql = " SELECT id,order_no orderno,doctorid,userid,paytype,actualmoney,visitcategory FROM doctor_phone_order WHERE status=? and paystatus=1 and ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(AnswerOrderStateEnum.WaitRefund.getCode());
        return query(sql, params, (ResultSet rs, int rowNum) -> {
            Map<String, Object> data = resultToMap(rs);
            if (data != null) {
                data.put("actualmoney", PriceUtil.findPrice(ModelUtil.getLong(data, "actualmoney")));
            }
            return data;
        });
    }

    public boolean updateDoctorWallet(long doctorId, BigDecimal walletbalance) {
        String sql = " update doctor_extends set walletbalance=? where doctorid=? ";
        List<Object> params = new ArrayList<>();
        params.add(PriceUtil.addPrice(walletbalance));
        params.add(doctorId);
        return update(sql, params) > 0;
    }

    public boolean addDoctorTransactionRecord(String orderno, TransactionTypeStateEnum transactionTypeStateEnum, MoneyTypeEnum moneyTypeEnum, long doctorid, String account, BigDecimal transactionmoney, String cardnumber, BigDecimal walletbalance) {
        String sql = " insert into doctor_transaction_record (orderno, " +
                "                                transactiontype, " +
                "                                moneyflag, " +
                "                                doctorid, " +
                "                                account, " +
                "                                transactionmoney, " +
                "                                cardnumber, " +
                "                                delflag, " +
                "                                walletbalance, " +
                "                                create_time) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
        List<Object> params = new ArrayList<>();
        params.add(orderno);
        params.add(transactionTypeStateEnum.getCode());
        params.add(moneyTypeEnum.getCode());
        params.add(doctorid);
        params.add(account);
        params.add(PriceUtil.addPrice(transactionmoney));
        params.add(cardnumber);
        params.add(0);
        params.add(PriceUtil.addPrice(walletbalance));
        params.add(UnixUtil.getNowTimeStamp());
        return update(sql, params) > 0;
    }

    public Map<String, Object> getDoctorWallet(long doctorId) {
        String sql = " select di.doctorid, di.doo_tel phone, de.walletbalance " +
                "from doctor_info di " +
                "       left join doctor_extends de on di.doctorid = de.doctorid and ifnull(de.delflag, 0) = 0 " +
                "where di.doctorid = ? ";
        Map<String, Object> data = queryForMap(sql, doctorId);
        if (data != null) {
            data.put("walletbalance", PriceUtil.findPrice(ModelUtil.getLong(data, "walletbalance")));
        }
        return data;
    }

    public Map<String, Object> getUserWallet(long userId) {
        String sql = " select  id, name, userno, phone, walletbalance from user_account  where  id=? ";
        Map<String, Object> data = queryForMap(sql, userId);
        if (data != null) {
            data.put("walletbalance", PriceUtil.findPrice(ModelUtil.getLong(data, "walletbalance")));
        }
        return data;
    }

    public Map<String, Object> findByUserId(long id) {
        String sql = "select phone from user_account where id=?";
        return queryForMap(sql, id);
    }

    public Map<String, Object> findById(long doctorid) {
        String sql = "select doo_tel,doc_name from doctor_info where doctorid=?";
        return queryForMap(sql, doctorid);
    }

    public boolean updateUserWallet(long userId, BigDecimal walletbalance) {
        String sql = " update user_account set walletbalance=? where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(PriceUtil.addPrice(walletbalance));
        params.add(userId);
        return update(sql, params) > 0;
    }

    public boolean addUserTransactionRecord(String orderno, TransactionTypeStateEnum transactionTypeStateEnum, MoneyTypeEnum moneyTypeEnum, long userid, String account, BigDecimal transactionmoney, int refundflag, BigDecimal walletbalance) {
        String sql = " insert into user_transaction_record (orderno, " +
                "                                transactiontype, " +
                "                                moneyflag, " +
                "                                userid, " +
                "                                account, " +
                "                                transactionmoney, " +
                "                                refundflag, " +
                "                                delflag," +
                "                                walletbalance," +
                "                                create_time) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
        List<Object> params = new ArrayList<>();
        params.add(orderno);
        params.add(transactionTypeStateEnum.getCode());
        params.add(moneyTypeEnum.getCode());
        params.add(userid);
        params.add(account);
        params.add(PriceUtil.addPrice(transactionmoney));
        params.add(refundflag);
        params.add(0);
        params.add(PriceUtil.addPrice(walletbalance));
        params.add(UnixUtil.getNowTimeStamp());
        return update(sql, params) > 0;
    }

    public List<Map<String, Object>> addPushOrderOneHour() {
        String sql = "select dpo.id orderid, ua.id userid, ua.platform, ua.xg_token usertoken, de.platform, de.xg_token doctoken " +
                "from doctor_problem_order dpo " +
                "       left join user_account ua on dpo.userid = ua.id and ifnull(ua.delflag, 0) = 0 " +
                "       left join doctor_extends de on dpo.doctorid = de.doctorid and ifnull(de.delflag, 0) = 0 " +
                "where states in (2, 6) " +
                "  and ifnull(dpo.delflag, 0) = 0 " +
                "  and paystatus = 1 " +
                "  and from_unixtime(dpo.create_time / 1000 - 3600, '%Y-%m-%d %H-%i') = date_format(now(), '%Y-%m-%d %H-%i')";
        return queryForList(sql);
    }

    public boolean updateHealthConsultantCeefax(long userId) {
        String sql = " UPDATE user_member set health_consultant_ceefax=health_consultant_ceefax+1 where userid=? and ifnull(delflag,0)=0 ";
        return update(sql, userId) > 0;
    }

    public boolean updateMedicalExpertCeefax(long userId) {
        String sql = " UPDATE user_member set medical_expert_ceefax=medical_expert_ceefax+1 where userid=? and ifnull(delflag,0)=0 ";
        return update(sql, userId) > 0;
    }

    public boolean updateHealthConsultantPhone(long userId) {
        String sql = " UPDATE user_member set health_consultant_phone=health_consultant_phone+1 where userid=? and ifnull(delflag,0)=0 ";
        return update(sql, userId) > 0;
    }

    public boolean updateMedicalExpertPhone(long userId) {
        String sql = " UPDATE user_member set medical_expert_phone=medical_expert_phone+1 where userid=? and ifnull(delflag,0)=0 ";
        return update(sql, userId) > 0;
    }

    public Map<String, Object> getAnswerOrder(String orderno) {
        String sql = " select id,originalprice,visitcategory,paytype from doctor_problem_order where ifnull(delflag,0)=0 and orderno=? ";
        return queryForMap(sql, orderno);
    }

    public Map<String, Object> getPhoneOrder(String orderno) {
        String sql = " select id,originalprice,visitcategory,paytype from doctor_phone_order where ifnull(delflag,0)=0 and order_no=? ";
        return queryForMap(sql, orderno);
    }

    //体现待退款订单
    public List<Map<String, Object>> getExtractRefundOrderList() {
        String sql = " select id,orderno,doctorid,account,cardnumber,amountmoney from doctor_extract_order where ifnull(refundflag,0)=0 and ifnull(delflag,0)=0 and status in(2,4) ";
        return query(sql, new ArrayList<>(), (ResultSet res, int num) -> {
            Map<String, Object> map = resultToMap(res);
            if (map != null) {
                map.put("amountmoney", PriceUtil.findPrice(ModelUtil.getLong(map, "amountmoney")));
            }
            return map;
        });
    }

    //视频待退款订单
    public List<Map<String, Object>> getVideoRefundOrderList() {
        String sql = " select id,orderno,doctorid,userid,actualmoney,paytype,visitcategory from doctor_video_order where paystatus=1 and status=8 and ifnull(delflag,0)=0 ";
        return query(sql, new ArrayList<>(), (ResultSet res, int num) -> {
            Map<String, Object> map = resultToMap(res);
            if (map != null) {
                map.put("actualmoney", PriceUtil.findPrice(ModelUtil.getLong(map, "actualmoney")));
            }
            return map;
        });
    }

    public boolean updateExpertOrderRefund(long id) {
        String sql = "  update doctor_extract_order set refundflag=1 where id=? ";
        return update(sql, id) > 0;
    }

    public void updateOrderTemplateChoiceflag(long orderId) {
        String sql = " update doctor_problem_template set choiceflag=1 where orderid=? ";
        update(sql, orderId);
    }

    public boolean updateAnswers(long orderid) {
        String sql = "update doctor_answer set is_answer=1 where orderid=? and contenttype=?";
        List<Object> list = new ArrayList<>();
        list.add(orderid);
        list.add(QAContentTypeEnum.DoctorClose.getCode());
        return update(sql, list) > 0;
    }

    public Map<String, Object> getDoctorAnswer(long id) {
        String sql = " select ua.headpic     userheadpic,   " +
                "            ua.name,   " +
                "            ua.id,   " +
                "            ua.userno,   " +
                "            da.id,   " +
                "            da.orderid,   " +
                "            da.create_time createtime,   " +
                "            da.content,   " +
                "            da.contenttime,   " +
                "            da.contenttype,   " +
                "            da.questionanswertype,   " +
                "            di.doc_name    doctorname,   " +
                "            di.in_doc_code    doctorno,   " +
                "            di.doc_photo_url   doctorheadpic   " +
                "     from doctor_answer da   " +
                "            left join user_account ua on da.userid = ua.id   " +
                "            left join doctor_info di on di.doctorid = da.doctorid   " +
                "     where ifnull(da.delflag, 0) = 0  and da.id= ?";
        return queryForMap(sql, id);
    }

    public List<Map<String, Object>> getOrderState(long orderid) {
        String sql = "select states,diagnosis from doctor_problem_order where id=?";
        List<Object> params = new ArrayList<>();
        params.add(orderid);
        return queryForList(sql, params);
    }

    /**
     * 订单推荐回复列表(单个订单)
     *
     * @return
     */
    public List<Map<String, Object>> getAppendUserSocketAnswerList(long orderId, long id) {
        String sql = " select ua.headpic     userheadpic,   " +
                "            ua.name,   " +
                "            da.id,   " +
                "            da.create_time createtime,   " +
                "            da.content,   " +
                "            da.contenttime,   " +
                "            da.contenttype,   " +
                "            da.questionanswertype,   " +
                "            di.doc_name    doctorname,   " +
                "            di.doc_photo_url   doctorheadpic   " +
                "     from doctor_answer da   " +
                "            left join user_account ua on da.userid = ua.id   " +
                "            left join doctor_info di on di.doctorid = da.doctorid   " +
                "            left join doctor_problem_order dpo on dpo.id= da.orderid " +
                "     where ifnull(da.delflag, 0) = 0  and da.orderid=? and da.contenttype not in(6,7,10,13,15,17,18) and dpo.states in(2,4,6) and da.id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(orderId);
        params.add(id);
        return queryForList(sql, params);
    }

    /**
     * 追加的消息
     *
     * @return
     */
    public List<Map<String, Object>> getAppendDoctorSocketAnswerList(long orderid, long id) {
        String sql = " select ua.headpic     userheadpic, " +
                "       ua.name, " +
                "       da.id, " +
                "       da.create_time createtime, " +
                "       da.content, " +
                "       da.contenttime, " +
                "       da.contenttype, " +
                "       da.questionanswertype, " +
                "       di.doc_name    doctorname, " +
                "       di.doc_photo_url   doctorheadpic " +
                "from doctor_answer da " +
                "       left join user_account ua on da.userid = ua.id " +
                "       left join doctor_info di on di.doctorid = da.doctorid " +
                "       left join doctor_problem_order dpo on da.orderid=dpo.id " +
                "where ifnull(da.delflag, 0) = 0  and da.orderid= ? and dpo.states in(2,4,6) and da.contenttype not in(5,9,12,14,16) and da.id = ? order by da.create_time desc,da.id desc ";
        List<Object> params = new ArrayList<>();
        params.add(orderid);
        params.add(id);
        return queryForList(sql, params);
    }

    /**
     * 通话三十分钟还未结束订单
     *
     * @return
     */
    public List<Map<String, Object>> phoneCloseOrderList() {
        String sql = " select dpo.id, " +
                "       dpo.order_no orderno, " +
                "       dpo.userid, " +
                "       dpo.doctorid, " +
                "       dpo.visitcategory, " +
                "       dpo.paytype, " +
                "       dpo.actualmoney, " +
                "       mid.caeatetime, " +
                "       dpo.create_time as createtime, " +
                "       ua.platform     as uplatform, " +
                "       ua.xg_token     as utoken, " +
                "       de.xg_token     as dtoken, " +
                "       de.platform     as dplatform " +
                "from doctor_phone_order dpo " +
                "       left join (select orderid,max(create_time) caeatetime " +
                "                  from doctor_phone_order_record " +
                "                  where ifnull(delflag, 0) = 0 " +
                "                  group by orderid) mid on dpo.id = mid.orderid " +
                "       left join doctor_extends de on dpo.doctorid = de.doctorid " +
                "       left join user_account ua on dpo.userid = ua.id " +
                "where dpo.paystatus = 1 " +
                "  and dpo.status = 3 " +
                "  and UNIX_TIMESTAMP() * 1000 - mid.caeatetime >= 30 * 60 * 1000 " +
                "  and ifnull(dpo.delflag, 0) = 0 ";
        return queryForList(sql);
    }

    /**
     * 急诊状态和录音文件
     *
     * @param orderId
     * @return
     */
    public boolean updatePhoneOrder(long orderId, int status) {
        String sql = "update doctor_phone_order set status=? where id=?";
        List<Object> params = new ArrayList<>();
        params.add(status);
        params.add(orderId);
        return update(sql, params) > 0;
    }


}
