package com.syhdoctor.webserver.mapper.focusfigure;

import com.syhdoctor.common.utils.EnumUtils.TypeNameBannerEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;
import com.syhdoctor.webserver.config.ConfigModel;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class FocusfigureBaseMapper extends BaseMapper {
    /**
     * 获取下拉显示位置下拉列表
     *
     * @return
     */
    public List<Map<String, Object>> getAlbumDropList(long albumid) {
        String sql = "select albumid as id,albumintro as title,img_height imgheight,img_width imgwidth,pid, case when albumid = ? then 1 else 0 end as selected from album where ifnull(delfalg,0)=0";
        List<Object> params = new ArrayList<>();
        params.add(albumid);
        return queryForList(sql, params);
    }

    public Map<String, Object> getAlbumDropListId(long albumid) {
        String sql = "select albumid as id,albumintro as title,img_height imgheight,img_width imgwidth,pid, case when albumid = ? then 1 else 0 end as selected from album where ifnull(delfalg,0)=0 and albumid=?";
        List<Object> params = new ArrayList<>();
        params.add(albumid);
        params.add(albumid);
        return queryForMap(sql, params);
    }

    /**
     * 查询广告列表
     *
     * @param albumid
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getFocusfigureList(int albumid, int pageIndex, int pageSize) {
        String sql = "select focusfigureid,bigimg,smallimg,instructions,type,typename,sort,sharepic,sharetitle,sharecontent,starttime,endtime from focusfigure where albumid=? and ifnull(delfalg,0)=0";
        List<Object> params = new ArrayList<>();
        params.add(albumid);
        sql = pageSql(sql, " order by focusfigureid desc ");
        params = pageParams(params, pageIndex, pageSize);
        return query(sql, params, (ResultSet res, int num) -> {
            Map<String, Object> map = resultToMap(res);
            if (map != null) {
                map.put("typename", TypeNameBannerEnum.getValue(ModelUtil.getInt(map, "type")).getMessage());
            }
            return map;
        });
    }

    public String getType(long albumid){
        String sql="select albumIntro from album where albumid=(select pid from album where albumid=?)";
        Map<String,Object> map=queryForMap(sql,albumid);
        String type=ModelUtil.getStr(map,"albumIntro");
        return type;
    }

    public Map<String, Object> getImageSize(long albumid) {
        String sql = "select big_size_length as bigsizelength,big_size_width as bigsizewidth,small_size_length as smallsizelength,small_size_width as smallsizewidth from album_size where album_id=?";
        return queryForMap(sql, albumid);
    }

    /**
     * banner图列表
     *
     * @param displayposition banner图位置
     * @param count           数量
     * @return
     */
    public List<Map<String, Object>> bannerList(int displayposition, int count) {
        String sql = " select f.focusfigureid, " +
                "       f.bigimg, " +
                "       f.smallimg, " +
                "       f.type, " +
                "       f.cycle_time cycletime, " +
                "       f.typename, " +
                "       f.sharepic, " +
                "       f.sharetitle, " +
                "       f.create_time " +
                "from focusfigure f " +
                "       LEFT JOIN album a on f.albumid = a.albumid " +
                "where a.displayposition = ? " +
                "  AND IFNULL(a.delfalg, 0) = 0 " +
                "  AND IFNULL(f.delfalg, 0) = 0 " +
                "  AND ((unix_timestamp(now()) * 1000 >= f.starttime AND unix_timestamp(now()) * 1000 <= f.endtime) " +
                "         OR (unix_timestamp(now()) * 1000 >= f.starttime AND IFNULL(f.endtime, '') = '') " +
                "         OR (unix_timestamp(now()) * 1000 <= f.endtime AND IFNULL(f.starttime, '') = '') " +
                "         OR (IFNULL(f.starttime, '') = '' AND IFNULL(f.endtime, '') = '')) " +
                "order by f.sort desc " +
                "LIMIT ? ";
        List<Object> params = new ArrayList<>();
        params.add(displayposition);
        params.add(count);
        List<Map<String, Object>> list = queryForList(sql, params);
        for (Map<String, Object> map : list) {
            int type = ModelUtil.getInt(map, "type");
            String typename = ModelUtil.getStr(map, "typename");
            if (type == 4) {
                String BANNER_URL = ConfigModel.WEBLINKURL + "web/syhdoctor/#/undetail?uid=$$&courseid=%s";
                type = 5;
                typename = String.format(BANNER_URL, typename);
                map.put("type", type);
                map.put("typename", typename);
            }
            if(type==6){
                String BANNER_URL = ConfigModel.WEBLINKURL + "web/syhdoctor/#/greenregistered?uid=$$";
                type = 5;
                typename = BANNER_URL;
                map.put("type", type);
                map.put("typename", typename);
            }
        }
        return list;
    }

    /**
     * 总条数
     *
     * @param albumid
     * @return
     */
    public long getFocusfigureListTotal(int albumid) {
        String sql = "select count(focusfigureid) total from focusfigure where albumid=? and ifnull(delfalg,0)=0";
        List<Object> params = new ArrayList<>();
        params.add(albumid);
        return jdbcTemplate.queryForObject(sql, params.toArray(), Long.class);
    }

    /**
     * 查询单条数据
     *
     * @param focusfigureId
     * @return
     */
    public Map<String, Object> getFocusfigureById(int focusfigureId) {
        String sql = "select focusfigureid,cycle_time cycle,albumid, bigimg, smallimg, instructions, type, typename, sort, sharepic, sharetitle, sharecontent,starttime,endtime from focusfigure where focusfigureid=?";
        List<Object> params = new ArrayList<>();
        params.add(focusfigureId);
        Map<String, Object> map = queryForMap(sql, params);
        if (null != map) {
            long day = ModelUtil.getLong(map, "cycle");
            long a = 60 * 60 * 1000;
            map.put("cycle", day / a);
        }
        return map;
    }

    /**
     * 删除广告图
     *
     * @param fousfigureId 广告图ID
     * @param agentId      登录人ID
     * @return
     */
    public boolean deleteFousfigure(int fousfigureId, int agentId) {
        String sql = "update focusfigure set delfalg=1,modify_user=?,modify_time=? where focusfigureid=?";
        List<Object> params = new ArrayList<>();
        params.add(agentId);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(fousfigureId);
        return update(sql, params) > 0;
    }

    /**
     * 添加banner图
     *
     * @param albumid      albumid
     * @param bigimg       大图
     * @param smallimg     小图
     * @param instructions 说明
     * @param type         类型
     * @param typename     跳转内容
     * @param sort         排序
     * @param sharepic     分享图片
     * @param sharetitle   分享标题
     * @param sharecontent 分享内容
     */
    public boolean addFocusfigure(long cycleTime, int albumid, String bigimg, String smallimg, String instructions, int type,
                                  String typename, int sort, String sharepic, String sharetitle, String sharecontent, int agentId, Long starttime, Long endtime) {
        String sql = "INSERT INTO focusfigure (cycle_time,albumid, bigimg, smallimg, instructions, type, typename, sort, sharepic, sharetitle, sharecontent, create_time, create_user,starttime,endtime) " +
                "VALUES (?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        List<Object> params = new ArrayList<>();
        params.add(cycleTime);
        params.add(albumid);
        params.add(bigimg);
        params.add(smallimg);
        params.add(instructions);
        params.add(type);
        params.add(typename);
        params.add(sort);
        params.add(sharepic);
        params.add(sharetitle);
        params.add(sharecontent);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(agentId);
        params.add(starttime);
        params.add(endtime);
        return insert(sql, params) > 0;
    }

    public boolean findFocus(long albumid, long focusfigureid) {
        String sql = "select count(focusfigureid) count " +
                "from focusfigure f " +
                "       left join album a on f.albumid = a.albumid and ifnull(a.delfalg, 0) = 0 " +
                "where f.albumid = ? and a.displayposition in(2,3,4,6) " +
                "  and ifnull(f.delfalg, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        params.add(albumid);
        if (focusfigureid != 0) {
            sql += " and f.focusfigureid != ?  ";
            params.add(focusfigureid);
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class) > 0;
    }

    /**
     * 修改广告图
     *
     * @param albumid       albumid
     * @param bigimg        大图
     * @param smallimg      小图
     * @param instructions  说明
     * @param type          类型
     * @param typename      跳转内容
     * @param sort          排序
     * @param sharepic      分享图片
     * @param sharetitle    分享标题
     * @param agentId       登录人ID
     * @param focusfigureid 广告ID
     * @return
     */
    public boolean updateFocusfigure(long cycleTime, int albumid, String bigimg, String smallimg, String instructions, int type,
                                     String typename, int sort, String sharepic, String sharetitle, String sharecontent, int agentId, int focusfigureid, Long starttime, Long endtime) {
        String sql = "update focusfigure set " +
                " cycle_time=?, albumid=?, bigimg=?, smallimg=?, instructions=?, type=?, typename=?, sort=?, sharepic=?, sharetitle=?, sharecontent=?, modify_time=?, modify_user=?, starttime=?, endtime=? where focusfigureid=? ";
        List<Object> params = new ArrayList<>();
        params.add(cycleTime);
        params.add(albumid);
        params.add(bigimg);
        params.add(smallimg);
        params.add(instructions);
        params.add(type);
        params.add(typename);
        params.add(sort);
        params.add(sharepic);
        params.add(sharetitle);
        params.add(sharecontent);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(agentId);
        params.add(starttime);
        params.add(endtime);
        params.add(focusfigureid);
        return update(sql, params) > 0;
    }
}
