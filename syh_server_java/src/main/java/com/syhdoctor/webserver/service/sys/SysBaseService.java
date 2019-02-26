package com.syhdoctor.webserver.service.sys;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.mapper.sys.SysMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public abstract class SysBaseService extends BaseService {

    @Autowired
    private SysMapper sysMapper;

    public Map<String, Object> login(String name, String pwd) {
        Map<String, Object> result = null;
        if (sysMapper.isUser(name, pwd)) {
            result = new HashMap<>();
            Map<String, Object> user = sysMapper.getUser(name, pwd);
            result.put("user", user);
            List<Map<String, Object>> menus = sysMapper.getMenus(ModelUtil.getLong(user, "id"));
            Map<Long, List<Map<String, Object>>> tempMap = new HashMap<>();
            List<Map<String, Object>> tempPurview = new ArrayList<>();

            for (Map<String, Object> temp : menus) {
                int type = ModelUtil.getInt(temp, "type", 1);
                if (type == 1) {
                    Long pid = ModelUtil.getLong(temp, "pid", 0);
                    permissinonTree(temp, pid, tempMap);
                } else if (type == 2) {
                    tempPurview.add(temp);
                }
            }
            for (Map<String, Object> temp : menus) {
                Long pid = ModelUtil.getLong(temp, "id", 0);
                temp.put("child", tempMap.get(pid));
            }
            result.put("admin_menu", tempMap.get(0L));
            result.put("admin_purview", tempPurview);
        }
        return result;
    }


    public List<Map<String, Object>> permissionList(String name, String pname, int type, int pageindex, int pagesize) {
        return sysMapper.permissionList(name, pname, type, pageindex, pagesize);
    }

    public List<Map<String, Object>> getPermissions(String name, int type, int pageindex, int pagesize) {
        return sysMapper.getPermissions(name, type, pageindex, pagesize);
    }

    public Map<String, Object> getPermissionDetail(long id) {
        Map<String, Object> data = sysMapper.getPermissionDetail(id);
        long pid = ModelUtil.getLong(data, "pid");
        if (pid > 0) {
            data.put("pid", sysMapper.getPermission(pid));
        }
        return data;
    }

    public long permissionCount(String name) {
        return sysMapper.permissionCount(name);
    }

    public long addUpdatePermission(long id, String name, long pid, String descritpion, String url, int type,String imagename) {

        if (id == 0) {
            return sysMapper.addPermissinon(name, pid, descritpion, url, type,imagename);
        } else {
            sysMapper.updatePermissinon(id, name, pid, descritpion, url, type,imagename);
            return id;
        }
    }

    @Transactional
    public boolean delPermission(long id) {
        sysMapper.delPermission(id);
        sysMapper.delPermissionRole(id);

        return true;
    }

    public List<Map<String, Object>> getRoleList(String name, int pageindex, int pagesize) {

        return sysMapper.getRoleList(name, pageindex, pagesize);
    }

    public long getRoleListCount(String name) {
        return sysMapper.getRoleListCount(name);
    }


    public Map<String, Object> getRole(long id) {
        Map<String, Object> result = sysMapper.getRole(id);
        result.put("premissinon", getRolePermissinon(id));

        return result;
    }


    public Map<String, List<Map<String, Object>>> getRolePermissinon(long rid) {
        Map<String, List<Map<String, Object>>> result = new HashMap<>();

        List<Map<String, Object>> allList = sysMapper.getRolePermissinon(rid);


        Map<Long, List<Map<String, Object>>> tempMap1 = new HashMap<>();
        Map<Long, List<Map<String, Object>>> tempMap2 = new HashMap<>();
        Map<Long, List<Map<String, Object>>> tempMap3 = new HashMap<>();

        for (Map<String, Object> temp : allList) {

            int type = ModelUtil.getInt(temp, "type", 1);
            long pid = ModelUtil.getLong(temp, "pid", 0);

            temp.put("expand", 1);

            Map<Long, List<Map<String, Object>>> tempMap = new HashMap<>();
            if (type == 1) {
                tempMap = tempMap1;
            } else if (type == 2) {
                tempMap = tempMap2;
            } else if (type == 3) {
                tempMap = tempMap3;
            }

            permissinonTree(temp, pid, tempMap);

        }

        for (Map<String, Object> temp : allList) {
            int type = ModelUtil.getInt(temp, "type", 0);
            long id = ModelUtil.getLong(temp, "id", 0);
            if (type == 1) {
                temp.put("data", tempMap1.get(id) != null ? tempMap1.get(id) : new ArrayList<>());
            } else if (type == 2) {
                temp.put("data", tempMap2.get(id) != null ? tempMap2.get(id) : new ArrayList<>());
            } else if (type == 3) {
                temp.put("data", tempMap3.get(id) != null ? tempMap3.get(id) : new ArrayList<>());
            }

        }


        result.put("pagePermission", tempMap1.get(0L) == null ? new ArrayList<>() : tempMap1.get(0L));
        result.put("featuresPermission", tempMap2.get(0L) == null ? new ArrayList<>() : tempMap2.get(0L));
        result.put("interfacePermission", tempMap3.get(0L) == null ? new ArrayList<>() : tempMap3.get(0L));

        return result;
    }

    private void permissinonTree(Map<String, Object> temp, long pid, Map<Long, List<Map<String, Object>>> tempMap) {
        if (tempMap.containsKey(pid)) {
            tempMap.get(pid).add(temp);
        } else {
            List<Map<String, Object>> list = new ArrayList<>();
            list.add(temp);
            tempMap.put(pid, list);
        }
    }

    @Transactional
    public long addOrUpdataRole(long rid, String name, List<?> pagePermission, List<?> featuresPermission, List<?> interfacePermission) {
        long newRid = rid;
        if (rid == 0) {
            newRid = sysMapper.addRole(name);
        } else {
            sysMapper.upDataRole(rid, name);
            sysMapper.delRolePermissinonFooting(rid);
        }

        for (Object key : pagePermission) {
            long pid = ModelUtil.strToLong(String.valueOf(key), 0);
            if (pid > 0) {
                sysMapper.addRolePermissinonFooting(newRid, pid);
            }
        }

        for (Object key : featuresPermission) {
            long pid = ModelUtil.strToLong(String.valueOf(key), 0);
            if (pid > 0) {
                sysMapper.addRolePermissinonFooting(newRid, pid);
            }
        }

        for (Object key : interfacePermission) {
            long pid = ModelUtil.strToLong(String.valueOf(key), 0);
            if (pid > 0) {
                sysMapper.addRolePermissinonFooting(newRid, pid);
            }
        }
        return newRid;
    }

    @Transactional
    public boolean delRole(long rid) {

        sysMapper.delRole(rid);
        sysMapper.delRolePermissinonFooting(rid);
        sysMapper.delRoleUserFooting(rid);

        return true;
    }

    public long userCount(String account, String name) {
        return sysMapper.userCount(account, name);
    }

    public List<Map<String, Object>> userList(String account, String name, int pageindex, int pagesize) {
        List<Map<String, Object>> list = sysMapper.userList(account, name, pageindex, pagesize);
        List<Map<String, Object>> roleNamelist = sysMapper.roleName();
        Map<Long,Object> roleNamemap = new HashMap<>();
        for(Map<String,Object> map :roleNamelist){
            Long id = ModelUtil.getLong(map,"sysuserid");
            String rolename = ModelUtil.getStr(map,"rolename");
            roleNamemap.put(id,rolename);
        }
        for(Map<String,Object> map :list){
            long id= ModelUtil.getLong(map,"id");
            map.put("rolename", roleNamemap.get(id));
        }
        return list;
    }

//    public long roleCount(String name) {
//        return sysMapper.roleCount(name);
//    }

    public List<Map<String, Object>> getRoles(String name, int pageindex, int pagesize) {
        return sysMapper.getRoles(name, pageindex, pagesize);
    }

    public Map<String, Object> getUserDetail(long id) {
        Map<String, Object> temp = sysMapper.getUserDetail(id);
        if (temp != null) {
            temp.put("roles", sysMapper.getUserRoles(id));
        }
        return temp;
    }

    @Transactional
    public long addUpdateUser(long id, String account, String username, String password, int islock, List<?> roles,String imagename) {
        long userid = id;
        if (id == 0) {
            userid = sysMapper.addUser(account, username, password, islock,imagename);
        } else {
            sysMapper.updateUser(id, account, username, islock,imagename);
            sysMapper.delUserRole(userid);
        }
        for (Object key : roles) {
            long roleid = ModelUtil.strToLong(String.valueOf(key), 0);
            if (roleid > 0) {
                sysMapper.addUserRoles(userid, roleid);
            }
        }
        return userid;
    }

    public long updateUserPwd(long id, String password) {
        sysMapper.updateUserPwd(id, password);
        return id;
    }

    @Transactional
    public boolean delUser(long id) {
        sysMapper.delUser(id);
        sysMapper.delUserRole(id);
        return true;
    }
}
