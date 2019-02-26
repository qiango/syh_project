package com.syhdoctor.webserver.mapper.drugs;

import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class DrugsBaseMapper extends BaseMapper {
    /**
     * 新增药品包
     *
     * @param name   药品名字
     * @param sort   排序
     * @param userId 创建人
     * @return
     */
    public long addDrugsPackage(String name, String img, int sort, long userId) {
        String sql = " INSERT INTO drugs_package (name, img, sort, delflag, create_time,create_user)VALUES (?, ?, ?, ?, ?, ?) ";
        List<Object> params = new ArrayList<>();
        params.add(name);
        params.add(img);
        params.add(sort);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(userId);
        return insert(sql, params, "id");
    }

    /**
     * 修改药品包
     *
     * @param id     药品id
     * @param name   药品名字
     * @param sort   排序
     * @param userId 创建人
     * @return
     */
    public boolean updateDrugsPackage(long id, String name, String img, int sort, long userId) {
        String sql = " UPDATE drugs_package SET name=?, img =?, sort=? ,modify_time=? ,modify_user=? WHERE id=? ";
        List<Object> params = new ArrayList<>();
        params.add(name);
        params.add(img);
        params.add(sort);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(userId);
        params.add(id);
        return update(sql, params) > 0;
    }

    /**
     * 删除中间表数据
     *
     * @param id
     * @return
     */
    public boolean delMiddleDrugsPackage(long id) {
        String sql = " delete from middle_drugs_package where packageid=? ";
        return update(sql, id) > 0;
    }

    /**
     * 添加中间表数据
     *
     * @param packageid 药品包id
     * @param drugsid   药品id
     * @return
     */
    public boolean addMiddleDrugsPackage(long packageid, long drugsid) {
        String sql = " INSERT INTO middle_drugs_package(packageid,drugsid)VALUES(?, ?) ";
        List<Object> params = new ArrayList<>();
        params.add(packageid);
        params.add(drugsid);
        return insert(sql, params) > 0;
    }

    /**
     * 药品包列表
     *
     * @param name      名字
     * @param pageIndex 分页
     * @param pageSize  分页
     * @return
     */
    public List<Map<String, Object>> getDrugsPackageList(String name, int pageIndex, int pageSize, IMapperResult callback) {
        String sql = " SELECT dp.id,dp.name,dp.img,dp.sort,code.value FROM drugs_package dp " +
                "left join ( " +
                "          select dp.id,group_concat(cd.value) as value " +
                "          from drugs_package dp " +
                "                 left join middle_drugs_package mdp on dp.id = mdp.packageid " +
                "                 left join code_drugs_bak cd on mdp.drugsid = cd.id" +
                "          where ifnull(dp.delflag,0)=0 group by dp.id " +
                "    ) code on dp.id=code.id " +
                "WHERE IFNULL(delflag,0)=0";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " AND dp.name like ? ";
            params.add(String.format("%%%s%%", name));
        }
        return query(pageSql(sql, " order by dp.sort desc, dp.create_time desc "), pageParams(params, pageIndex, pageSize), (ResultSet res, int row) -> {
            Map<String, Object> value = resultToMap(res);
            if (callback != null) {
                callback.result(value);
            }
            return value;
        });
    }

    /**
     * 药品包数量
     *
     * @param name
     * @return
     */
    public long getDrugsPackageCount(String name) {
        String sql = " SELECT count(id) count FROM drugs_package WHERE IFNULL(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " AND name like ? ";
            params.add(String.format("%%%s%%", name));
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    /**
     * 药品包详情
     *
     * @param id
     * @return
     */
    public Map<String, Object> getDrugsPackage(long id) {
        String sql = " SELECT id,name,img,sort FROM drugs_package WHERE id=? ";
        return queryForMap(sql, id);
    }

    /**
     * 删除药品包
     *
     * @param id
     * @return
     */
    public boolean delDrugsPackage(long id) {
        String sql = " UPDATE drugs_package SET delflag=1 WHERE id= ? ";
        return update(sql, id) > 0;
    }

    /**
     * 药品包下面的药品字典
     *
     * @param packageId
     * @return
     */
    public List<?> getMiddleDrugsPackageList(long packageId) {
        String sql = " select cd.id, cd.value as name " +
                "from middle_drugs_package as mdp " +
                "       left join code_drugs_bak as cd on mdp.drugsid = cd.id " +
                "where packageid = ? ";
        return queryForList(sql, packageId);
    }
}
