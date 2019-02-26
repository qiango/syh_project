package com.syhdoctor.webserver.api.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class ProcIndexList {

    /**
     * 过程文件1(
     * 可以是mp4或pdf格式，分别对应视频和图文，
     * 内容举例：互联网医院编码+“/”+
     * 就诊时间（yyyyMMdd）+”/”+本次就诊号+“/”+
     * 过程文件名称（后缀为mp4或pdf）)
     */
    @JSONField(name = "proc_file")
    private String procFile;

    public String getProcFile() {
        return procFile;
    }

    public void setProcFile(String procFile) {
        this.procFile = procFile;
    }
}
