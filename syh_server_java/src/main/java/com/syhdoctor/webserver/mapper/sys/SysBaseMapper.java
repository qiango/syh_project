package com.syhdoctor.webserver.mapper.sys;

import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class SysBaseMapper extends BaseMapper {

    /**
     * 根据账号密码获取用户信息
     *
     * @param account 账号
     * @param pwd     MD5的密码
     * @return java.com.syhdoctor.webserver.api.util.Map
     */
    public Map<String, Object> getUser(String account, String pwd) {
        String sql = "SELECT id,account,username,islock,imagename FROM sys_user WHERE delflag = 0 AND account = ? AND password = ?";
        List<Object> params = new ArrayList<>();
        params.add(account);
        params.add(pwd);
        return queryForMap(sql, params.toArray());
    }

    /**
     * 获取对应账户的菜单权限
     *
     * @param id 账户id
     * @return java.com.syhdoctor.webserver.api.util.List
     */
    public List<Map<String, Object>> getMenus(long id) {
        String sql;
        List<Object> params = new ArrayList<>();
        if (id == 0) {
            sql = "SELECT " +
                    "  p.name, " +
                    "  p.id, " +
                    "  p.url, " +
                    "  p.pid, " +
                    "  p.type, " +
                    "  p.imagename " +
                    "FROM sys_permission p " +
                    "WHERE p.delflag = 0 " +
                    "ORDER BY p.create_time ASC ";
        } else {
            sql = "SELECT p.name,p.id,p.url,p.pid,p.type,p.imagename FROM sys_user u,sys_role r,sys_role_user ru,sys_permission p,sys_permission_role pr " +
                    " WHERE u.id = ru.sys_user_id AND r.id = ru.sys_role_id AND r.id = pr.role_id AND p.id = pr.permission_id " +
                    " AND u.delflag=0 AND r.delflag=0 AND p.delflag=0 AND u.id = ? " +
                    " GROUP BY p.name,p.id,p.url,p.pid,p.create_time,p.type ORDER BY p.create_time ASC";
            params.add(id);
        }
        return queryForList(sql, params);
    }

    /**
     * 判断账户是否存在
     *
     * @param account 账号
     * @param pwd     MD5的密码
     * @return boolean
     */
    public boolean isUser(String account, String pwd) {
        String sql = "SELECT count(id) FROM sys_user WHERE delflag = 0 AND account = ? AND password = ?";
        List<Object> params = new ArrayList<>();
        params.add(account);
        params.add(pwd);
        return jdbcTemplate.queryForObject(sql, params.toArray(), Long.class) > 0;
    }

    /**
     * 权限列表的总条数
     *
     * @param name 权限名
     * @return long
     */
    public long permissionCount(String name) {
        String sql = "SELECT count(pe.id) FROM sys_permission AS pe" +
                "  LEFT JOIN sys_permission AS pe2 ON pe.pid = pe2.id WHERE pe.delflag=0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " AND pe.name LIKE ? ";
            params.add(String.format("%%%s%%", name));
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), Long.class);
    }

    /**
     * 权限列表
     *
     * @param name      权限名
     * @param pageindex 页码
     * @param pagesize  条数
     * @return java.com.syhdoctor.webserver.api.util.List
     */
    public List<Map<String, Object>> permissionList(String name, String pname, int type, int pageindex, int pagesize) {
        String sql = "SELECT pe.id, pe.name, pe.pid, pe2.name AS pname, pe.descritpion, pe.url , pe.type,pe.create_time as createTime FROM sys_permission AS pe" +
                "  LEFT JOIN sys_permission AS pe2 ON pe.pid = pe2.id WHERE pe.delflag=0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " AND pe.name LIKE ? ";
            params.add(String.format("%%%s%%", name));
        }
        if (type > 0) {
            sql += " AND pe.type = ? ";
            params.add(type);
        }
        if (!StrUtil.isEmpty(pname)) {
            sql += " AND pe2.name LIKE ? ";
            params.add(String.format("%%%s%%", pname));
        }
        sql = pageSql(sql, " ORDER BY pe.create_time DESC ");
        params = pageParams(params, pageindex, pagesize);
        return queryForList(sql, params);
    }

    /**
     * 获取所有的权限
     *
     * @return java.com.syhdoctor.webserver.api.util.List
     */
    public List<Map<String, Object>> getPermissions(String name, int type, int pageindex, int pagesize) {
        String sql = "SELECT id,name FROM sys_permission WHERE delflag = 0 AND ifnull(url,'')='' ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " AND name LIKE ? ";
            params.add(String.format("%%%s%%", name));
        }
        if (type > 0) {
            sql += " AND type = ? ";
            params.add(type);
        }
        sql = pageSql(sql, " ORDER BY create_time DESC ");
        params = pageParams(params, pageindex, pagesize);
        return queryForList(sql, params);
    }

    /**
     * 获取权限详情
     *
     * @param id 权限id
     * @return java.com.syhdoctor.webserver.api.util.Map
     */
    public Map<String, Object> getPermissionDetail(long id) {
        List<Object> params = new ArrayList<>();
        String sql = "SELECT id,name, pid, descritpion, url, type,imagename FROM sys_permission WHERE id=?";
        params.add(id);
        return queryForMap(sql, params);
    }

    /**
     * 获取父类权限详情(Select框需要)
     *
     * @param id 权限id
     */
    public Map<String, Object> getPermission(long id) {
        List<Object> params = new ArrayList<>();
        String sql = "SELECT id,name FROM sys_permission WHERE id=?";
        params.add(id);
        return queryForMap(sql, params);
    }

    /**
     * 添加权限
     *
     * @param name        权限名
     * @param pid         权限父级
     * @param descritpion 权限描述
     * @param url         权限的值
     * @param type        权限类型
     * @return long
     */
    public long addPermissinon(String name, long pid, String descritpion, String url, int type, String imagename) {

        String sql = "INSERT INTO sys_permission(id, name, pid, descritpion, url, create_time, type,imagename) VALUES (?,?,?,?,?,?,?,?)";

        long id = getId("sys_permission");
        List<Object> params = new ArrayList<>();
        params.add(id);
        params.add(name);
        params.add(pid);
        params.add(descritpion);
        params.add(url);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(type);
        params.add(imagename);
        insert(sql, params);
        return id;

    }

    /**
     * 修改权限
     *
     * @param id          权限id
     * @param name        权限名
     * @param pid         权限父级
     * @param descritpion 权限描述
     * @param url         权限的值
     * @param type        权限类型
     * @return long
     */
    public long updatePermissinon(long id, String name, long pid, String descritpion, String url, int type, String imagename) {
        String sql = "UPDATE sys_permission set name=?, pid=?, descritpion=?, url=?, update_time=?, type=? ,imagename=? WHERE id=?";

        List<Object> params = new ArrayList<>();
        params.add(name);
        params.add(pid);
        params.add(descritpion);
        params.add(url);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(type);
        params.add(imagename);
        params.add(id);

        return update(sql, params);

    }

    /**
     * 删除权限
     *
     * @param id 权限id
     * @return long
     */
    public long delPermission(long id) {
        List<Object> params = new ArrayList<>();
        String sql = "UPDATE sys_permission set delflag=1 WHERE id=?";
        params.add(id);
        return update(sql, params);
    }

    /**
     * 删除权限对应的角色
     *
     * @param id 权限id
     * @return long
     */
    public long delPermissionRole(long id) {
        List<Object> params = new ArrayList<>();
        String sql = "DELETE FROM sys_permission_role WHERE permission_id = ?";
        params.add(id);
        return update(sql, params);
    }


    /**
     * 角色聊表的总数
     *
     * @param name 角色名
     * @return long
     */
    public long getRoleListCount(String name) {

        String sql = " SELECT count(id) FROM sys_role WHERE 1=1 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " AND name LIKE ? ";
            params.add(String.format("%%%s%%", name));
        }
        Long temp = jdbcTemplate.queryForObject(sql, params.toArray(), Long.class);
        return temp == null ? 0 : temp;
    }

    /**
     * 角色列表
     *
     * @param name      角色名
     * @param pageindex 页码
     * @param pagesize  条数
     * @return java.com.syhdoctor.webserver.api.util.List
     */
    public List<Map<String, Object>> getRoleList(String name, int pageindex, int pagesize) {

        String sql = "SELECT id,name,create_time as createTime FROM sys_role WHERE delflag = 0 ";

        List<Object> params = new ArrayList<>();

        if (!StrUtil.isEmpty(name)) {
            sql += " AND name LIKE ?";
            params.add(String.format("%%%s%%", name));
        }

        sql = pageSql(sql, " ORDER BY create_time DESC ");
        params = pageParams(params, pageindex, pagesize);
        return queryForList(sql, params);
    }

    /**
     * 获取角色详情
     *
     * @param id 角色id
     * @return java.com.syhdoctor.webserver.api.util.Map
     */
    public Map<String, Object> getRole(long id) {
        String sql = "SELECT id,name,create_time as createTime FROM sys_role WHERE id=?";
        List<Object> params = new ArrayList<>();
        params.add(id);

        return queryForMap(sql, params);
    }


    /**
     * 获取角色的所有权限
     *
     * @param rid 角色id
     * @return java.com.syhdoctor.webserver.api.util.List
     */

    public List<Map<String, Object>> getRolePermissinon(long rid) {
        String sql = "SELECT p.id, " +
                "       p.id    AS value, " +
                "       p.name  AS title, " +
                "       p.url, " +
                "       p.pid, " +
                "       p.type, " +
                "       case when pr.role_id = ? and ifnull(cp.num, 0) = 0 then 1 else 0 end as checked " +
                " FROM sys_permission p " +
                "       LEFT JOIN sys_permission_role pr ON p.id = pr.permission_id AND pr.role_id = ? " +
                "       left join (select pid, count(id) as num " +
                "                  from sys_permission " +
                "                  where ifnull(pid, 0) != 0 " +
                "                    and ifnull(delflag, 0) = 0 " +
                "                  group by pid) as cp on p.id = cp.pid " +
                " WHERE ifnull(p.delflag, 0) = 0 " +
                " ORDER BY p.create_time ASC ";

        List<Object> params = new ArrayList<>();
        params.add(rid);
        params.add(rid);
        return queryForList(sql, params);
    }

    /**
     * 添加角色
     *
     * @param name 角色名
     * @return long
     */
    public long addRole(String name) {

        String sql = "INSERT INTO sys_role(id, name, create_time) VALUES (?,?,?)";

        long id = getId("sys_role");
        List<Object> params = new ArrayList<>();
        params.add(id);
        params.add(name);
        params.add(UnixUtil.getNowTimeStamp());

        insert(sql, params);

        return id;
    }

    /**
     * 修改角色
     *
     * @param rid  角色id
     * @param name 角色名
     * @return long
     */

    public long upDataRole(long rid, String name) {

        String sql = "UPDATE sys_role set name=?, update_time=? WHERE id = ?";

        List<Object> params = new ArrayList<>();

        params.add(name);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(rid);

        return update(sql, params);

    }

    /**
     * 删除角色权限关系
     *
     * @param rid 角色id
     * @return long
     */

    public long delRolePermissinonFooting(long rid) {

        String sql = "DELETE FROM sys_permission_role WHERE role_id= ?";

        List<Object> params = new ArrayList<>();

        params.add(rid);

        return update(sql, params);

    }

    /**
     * 添加角色权限关系
     *
     * @param rid 角色id
     * @param pid 权限id
     * @return long
     */
    public long addRolePermissinonFooting(long rid, long pid) {
        String sql = "INSERT INTO sys_permission_role(role_id,permission_id) VALUES (?,?)";
        List<Object> params = new ArrayList<>();

        params.add(rid);
        params.add(pid);

        return insert(sql, params);
    }

    /**
     * 删除角色
     *
     * @param rid 角色id
     * @return long
     */
    public long delRole(long rid) {
        String sql = "UPDATE sys_role set delflag=1 WHERE id = ?";
        List<Object> params = new ArrayList<>();
        params.add(rid);

        return update(sql, params);
    }

    /**
     * 删除角色与用户的关系
     *
     * @param rid 角色id
     * @return long
     */
    public long delRoleUserFooting(long rid) {
        String sql = "DELETE FROM sys_role_user WHERE sys_role_id = ?";
        List<Object> params = new ArrayList<>();
        params.add(rid);

        return update(sql, params);
    }


    /**
     * 账户列表的总条数
     *
     * @param account 账户号
     * @param name    账户名
     * @return long
     */
    public long userCount(String account, String name) {
        String sql = "SELECT count(id) FROM sys_user WHERE delflag = 0 AND id!=0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(account)) {
            sql += " AND account LIKE ? ";
            params.add(String.format("%%%s%%", account));
        }
        if (!StrUtil.isEmpty(name)) {
            sql += " AND username LIKE ? ";
            params.add(String.format("%%%s%%", name));
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), Long.class);
    }

    /**
     * 账户列表
     *
     * @param account   账户号
     * @param name      账户名
     * @param pageindex 页码
     * @param pagesize  条数
     * @return java.com.syhdoctor.webserver.api.util.List
     */
    public List<Map<String, Object>> userList(String account, String name, int pageindex, int pagesize) {
        String sql = "SELECT id,account,username,create_time as createTime,islock FROM sys_user WHERE delflag = 0 AND id!=0";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(account)) {
            sql += " AND account LIKE ? ";
            params.add(String.format("%%%s%%", account));
        }
        if (!StrUtil.isEmpty(name)) {
            sql += " AND username LIKE ? ";
            params.add(String.format("%%%s%%", name));
        }
        sql = pageSql(sql, " ORDER BY create_time DESC ");
        params = pageParams(params, pageindex, pagesize);
        return queryForList(sql, params);
    }

    public List<Map<String,Object>> roleName(){
        String sql = " select sys_user_id sysuserid,group_concat(name) rolename from sys_role_user sru " +
                " left join sys_role sr on sr.id = sru.sys_role_id and ifnull(sr.delflag,0)=0 " +
                " GROUP BY sys_user_id";
        return queryForList(sql);
    }



    /**
     * 删除账户
     *
     * @param id 账户id
     * @return long
     */
    public long delUser(long id) {
        List<Object> params = new ArrayList<>();
        String sql = "UPDATE sys_user set delflag=1 WHERE id=?";
        params.add(id);
        return update(sql, params);
    }

    /**
     * 删除账户关联的角色
     *
     * @param id 账户id
     * @return long
     */
    public long delUserRole(long id) {
        List<Object> params = new ArrayList<>();
        String sql = "DELETE FROM sys_role_user WHERE sys_user_id = ?";
        params.add(id);
        return update(sql, params);
    }

    /**
     * 添加账户
     *
     * @param account  账户号
     * @param username 账户名
     * @param password MD5的密码
     * @return long
     */
    public long addUser(String account, String username, String password, int islock, String imagename) {
        String sql = "INSERT INTO sys_user(id, account, username, password,islock, create_time,imagename) VALUES (?,?,?,?,?,?,?)";

        long id = getId("sys_user");
        List<Object> params = new ArrayList<>();
        params.add(id);
        params.add(account);
        params.add(username);
        params.add(password);
        params.add(islock);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(imagename);
        insert(sql, params);
        return id;

    }

    /**
     * 修改账户密码
     *
     * @param id       账户id
     * @param password MD5的密码
     * @return long
     */
    public long updateUserPwd(long id, String password) {
        String sql = "UPDATE sys_user set password=?, update_time=? WHERE id=?";

        List<Object> params = new ArrayList<>();
        params.add(password);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(id);

        return update(sql, params);
    }

    /**
     * 修改账户信息
     *
     * @param id       账户id
     * @param account  账户号
     * @param username 账户名
     * @return long
     */
    public long updateUser(long id, String account, String username, int islock, String imagename) {
        String sql = "UPDATE sys_user set account=?, username=?, update_time=?,islock=? ,imagename=? WHERE id=?";

        List<Object> params = new ArrayList<>();
        params.add(account);
        params.add(username);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(islock);
        params.add(imagename);
        params.add(id);

        return update(sql, params);
    }

    /**
     * 获取账户详情
     *
     * @param id 账户id
     * @return java.com.syhdoctor.webserver.api.util.Map
     */
    public Map<String, Object> getUserDetail(long id) {
        List<Object> params = new ArrayList<>();
        String sql = "SELECT id,account,username,islock as `lock` FROM sys_user WHERE id=?";
        params.add(id);
        return queryForMap(sql, params);
    }

    /**
     * 获取账户对应的所有角色
     *
     * @param userid 账户id
     * @return java.com.syhdoctor.webserver.api.util.List
     */
    public List<Map<String, Object>> getUserRoles(long userid) {
        List<Object> params = new ArrayList<>();
        String sql = " SELECT DISTINCT r.id,r.name FROM sys_role_user ru LEFT JOIN sys_role r ON ru.sys_role_id = r.id " +
                " WHERE ru.sys_user_id = ? ";
        params.add(userid);
        return queryForList(sql, params);
    }

    /**
     * 角色总数（供select选择使用）
     *
     * @param name 角色名
     * @return long
     */
//    public long roleCount(String name) {
//        String sql = "SELECT count(id) FROM sys_role WHERE delflag = 0 ";
//        List<Object> params = new ArrayList<>();
//        if (!StrUtil.isEmpty(name)) {
//            sql += " AND name LIKE ? ";
//            params.add(String.format("%%%s%%", name));
//        }
//        return jdbcTemplate.queryForObject(sql, params.toArray(), Long.class);
//    }

    /**
     * 角色列表（供select选择使用）
     *
     * @param name      角色名
     * @param pageindex 页码
     * @param pagesize  条数
     * @return java.com.syhdoctor.webserver.api.util.List
     */
    public List<Map<String, Object>> getRoles(String name, int pageindex, int pagesize) {
        String sql = "SELECT id,name,create_time as createTime FROM sys_role WHERE delflag = 0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " AND name LIKE ? ";
            params.add(String.format("%%%s%%", name));
        }
        sql = pageSql(sql, " ORDER BY create_time ASC ");
        params = pageParams(params, pageindex, pagesize);
        return queryForList(sql, params);
    }

    /**
     * 添加账户的角色
     *
     * @param userid 账户id
     * @param roleid 角色id
     * @return boolean
     */
    public boolean addUserRoles(long userid, long roleid) {
        String sql = "INSERT INTO sys_role_user (sys_user_id,sys_role_id) VALUES (?,?)";
        List<Object> params = new ArrayList<>();
        params.add(userid);
        params.add(roleid);
        return update(sql, params) > 0;
    }
}
