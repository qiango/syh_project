package com.syhdoctor.webserver.mapper.department;


import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class DepartmentBaseMapper extends BaseMapper {

    /**
     * 新增科室包
     *
     * @param name   科室名字
     * @param sort   排序
     * @param userId 创建人
     * @return
     */
    public long addDepartmentPackage(String name, int sort, long userId) {
        String sql = " INSERT INTO department_package (name, sort, delflag, create_time,create_user)VALUES (?, ?, ?, ?, ?) ";
        List<Object> params = new ArrayList<>();
        params.add(name);
        params.add(sort);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(userId);
        return insert(sql, params, "id");
    }

    /**
     * 修改科室包
     *
     * @param id     科室id
     * @param name   科室名字
     * @param sort   排序
     * @param userId 创建人
     * @return
     */
    public boolean updateDepartmentPackage(long id, String name, int sort, long userId) {
        String sql = " UPDATE department_package SET name=?, sort=? ,modify_time=? ,modify_user=? WHERE id=? ";
        List<Object> params = new ArrayList<>();
        params.add(name);
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
    public boolean delMiddleDepartmentPackage(long id) {
        String sql = " delete from middle_department_package where packageid=? ";
        return update(sql, id) > 0;
    }

    /**
     * 添加中间表数据
     *
     * @param packageid
     * @param departmentId
     * @return
     */
    public boolean addMiddleDepartmentPackage(long packageid, long departmentId) {
        String sql = " INSERT INTO middle_department_package(packageid,departmentid)VALUES(?, ?) ";
        List<Object> params = new ArrayList<>();
        params.add(packageid);
        params.add(departmentId);
        return insert(sql, params) > 0;
    }

    /**
     * 科室包列表
     *
     * @param name      名字
     * @param pageIndex 分页
     * @param pageSize  分页
     * @return
     */
    public List<Map<String, Object>> getDepartmentPackageList(String name, int pageIndex, int pageSize) {
        String sql = " SELECT dp.id,dp.name,dp.sort,code.value FROM department_package dp " +
                "left join ( " +
                "          select dp.id,group_concat(cd.value) as value " +
                "          from department_package dp " +
                "                 left join middle_department_package mdp on dp.id = mdp.packageid " +
                "                 left join code_department cd on mdp.departmentid = cd.id " +
                "          where ifnull(dp.delflag,0)=0 group by dp.id " +
                "    ) code on dp.id=code.id " +
                "WHERE IFNULL(delflag,0)=0";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " AND dp.name like ? ";
            params.add(String.format("%%%s%%", name));
        }
        return queryForList(pageSql(sql, " order by dp.sort desc, dp.create_time desc "), pageParams(params, pageIndex, pageSize));
    }

    /**
     * 科室包数量
     *
     * @param name
     * @return
     */
    public long getDepartmentPackageCount(String name) {
        String sql = " SELECT count(id) count FROM department_package WHERE IFNULL(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " AND name like ? ";
            params.add(String.format("%%%s%%", name));
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    /**
     * 科室包详情
     *
     * @param id
     * @return
     */
    public Map<String, Object> getDepartmentPackage(long id) {
        String sql = " SELECT id,name,sort FROM department_package WHERE id=? ";
        return queryForMap(sql, id);
    }

    /**
     * 删除科室包
     *
     * @param id
     * @return
     */
    public boolean delDepartmentPackage(long id) {
        String sql = " UPDATE department_package SET delflag=1 WHERE id= ? ";
        return update(sql, id) > 0;
    }

    /**
     * 科室包下面的科室字典
     *
     * @param packageId
     * @return
     */
    public List<?> getMiddleDepartmentPackageList(long packageId) {
        String sql = " select cd.id, cd.value as name " +
                "from middle_department_package as mdp " +
                "       left join code_department as cd on mdp.departmentid = cd.id " +
                "where packageid = ? ";
        return queryForList(sql, packageId);
    }

    /**
     * 清空重复排序
     *
     * @param sort 排序
     */
    public void updateDepartmentSort(int sort) {
        String sql = " update department_package set sort =0 where sort=? ";
        update(sql, sort);
    }
}
