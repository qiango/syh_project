package com.syhdoctor.webserver.service.focusfigure;

import com.syhdoctor.common.utils.EnumUtils.TypeNameBannerEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.exception.ServiceException;
import com.syhdoctor.webserver.mapper.focusfigure.FocusfigureMapper;
import com.syhdoctor.webserver.mapper.microclass.MicroClassMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FocusfigureBaseService extends BaseService {

    @Autowired
    private FocusfigureMapper focusfigureMapper;

    @Autowired
    private MicroClassMapper microClassMapper;

    /**
     * 查询显示位置下拉列表
     *
     * @return
     */

    public List<Map<String, Object>> getAlbumDropList(long albumid) {
        List<Map<String, Object>> albumDropList = focusfigureMapper.getAlbumDropList(albumid);
        Map<Long, List<Map<String, Object>>> tempMap = new HashMap<>();
        for (Map<String, Object> temp : albumDropList) {
            temp.put("expand", 1);
            Long pid = ModelUtil.getLong(temp, "pid", 0);
            departmentTree(temp, pid, tempMap);
        }
        for (Map<String, Object> temp : albumDropList) {
            Long id = ModelUtil.getLong(temp, "id", 0);
            List<Map<String, Object>> datas = tempMap.get(id);
            if (datas != null && datas.size() > 0) {
                temp.put("disabled", 1);
            }
            temp.put("child", datas);
        }

        return tempMap.get(0L);
    }

    /**
     * 宽高
     * @param albumid
     * @return
     */
    public Map<String, Object> getAlbumDropListId(long albumid) {
        return focusfigureMapper.getAlbumDropListId(albumid);
    }


    private void departmentTree(Map<String, Object> temp, long pid, Map<Long, List<Map<String, Object>>> tempMap) {
        if (tempMap.containsKey(pid)) {
            tempMap.get(pid).add(temp);
        } else {
            List<Map<String, Object>> list = new ArrayList<>();
            list.add(temp);
            tempMap.put(pid, list);
        }
    }

    public Map<String, Object> getImageSize(long albumid) {
        return focusfigureMapper.getImageSize(albumid);
    }

    /**
     * 查询单条数据
     *
     * @param focusfigureId
     * @return
     */
    public Map<String, Object> getFocusfigureById(int focusfigureId) {
        Map<String, Object> data = focusfigureMapper.getFocusfigureById(focusfigureId);
        data.put("albumid", getAlbumDropList(ModelUtil.getLong(data, "albumid")));
        if (ModelUtil.getInt(data, "type") == TypeNameBannerEnum.microclassDetail.getCode()) {
            data.put("typename", microClassMapper.getAdminCourseBySelect(ModelUtil.getLong(data, "typename")));
        }
        return data;
    }

    /**
     * 删除广告图
     *
     * @param fousfigureId 广告图ID
     * @param agentId      登录人ID
     * @return
     */
    public boolean deleteFousfigure(int fousfigureId, int agentId) {
        return focusfigureMapper.deleteFousfigure(fousfigureId, agentId);
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
        return focusfigureMapper.getFocusfigureList(albumid, pageIndex, pageSize);
    }

    public int getTypes(long albumid){
        int a=0;
        String type=focusfigureMapper.getType(albumid);
        if("用户端".equals(type)){
            a=1;
        }else if("医生端".equals(type)){
            a=2;
        }
        return a;
    }

    /**
     * 查询广告列表
     *
     * @param count
     * @return
     */
    public List<Map<String, Object>> bannerList(int displayposition, int count) {
        return focusfigureMapper.bannerList(displayposition, count);
    }

    /**
     * 总条数
     *
     * @param albumid
     * @return
     */
    public long getFocusfigureListTotal(int albumid) {
        return focusfigureMapper.getFocusfigureListTotal(albumid);
    }


    /**
     * 添加或修改广告图
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
    public boolean addUpdateFocusfigure(int cycle, int albumid, String bigimg, String smallimg, String instructions, int type,
                                        String typename, int sort, String sharepic, String sharetitle, String sharecontent, int agentId, int focusfigureid, Long starttime, Long endtime) {
        if (focusfigureMapper.findFocus(albumid, focusfigureid)) {
            throw new ServiceException("该闪屏图或开屏已存在，请检查");
        }
        long cycleTime = cycle * 60 * 60*1000;
//        if (type == 4) {
//            String BANNER_URL = ConfigModel.WEBLINKURL + "web/syhdoctor/#/undetail?courseid=%s";
//            type = 5;
//            typename = String.format(BANNER_URL, typename);
//        }
        if (focusfigureid > 0) {
            return focusfigureMapper.updateFocusfigure(cycleTime, albumid, bigimg, smallimg, instructions, type, typename, sort, sharepic, sharetitle, sharecontent, agentId, focusfigureid, starttime, endtime);
        } else {
            return focusfigureMapper.addFocusfigure(cycleTime, albumid, bigimg, smallimg, instructions, type, typename, sort, sharepic, sharetitle, sharecontent, agentId, starttime, endtime);
        }
    }
}
