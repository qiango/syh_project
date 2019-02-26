package com.syhdoctor.webserver.thirdparty.baidu;

public class PublicMsg {

    public final static String UEDITOR_CONFIG =/* 上传图片配置项 */
            "{\n" +
                    "\"imageActionName\": \"uploadimage\", \n" +/* 执行上传图片的action名称 */
                    "\"imageFieldName\": \"upfile\", \n" +/* 提交的图片表单名称 */
                    "\"imageMaxSize\": 2048000, \n" +/* 上传大小限制，单位B */
                    "\"imageAllowFiles\": [\".png\", \".jpg\", \".jpeg\", \".gif\", \".bmp\"], \n" +/* 上传图片格式显示 */
                    "\"imageCompressEnable\": true, \n" +/* 是否压缩图片,默认是true */
                    "\"imageCompressBorder\": 1600,\n" + /* 图片压缩最长边限制 */
                    "\"imageInsertAlign\": \"none\", \n" +/* 插入的图片浮动方式 */
                    "\"imageUrlPrefix\": \"\", \n" +/* 图片访问路径前缀 */
                    "\"imagePathFormat\": \"\", \n" +/* 上传保存路径,可以自定义保存路径和文件名格式 */
                    /* {filename} 会替换成原文件名,配置这项需要注意中文乱码问题 */
                    /* {rand:6} 会替换成随机数,后面的数字是随机数的位数 */
                    /* {time} 会替换成时间戳 */
                    /* {yyyy} 会替换成四位年份 */
                    /* {yy} 会替换成两位年份 */
                    /* {mm} 会替换成两位月份 */
                    /* {dd} 会替换成两位日期 */
                    /* {hh} 会替换成两位小时 */
                    /* {ii} 会替换成两位分钟 */
                    /* {ss} 会替换成两位秒 */
                    /* 非法字符 \\ : * ? \" < > | */
                    /* 具请体看线上文档: fex.baidu.com/ueditor/#use-format_upload_filename */
                    /* 涂鸦图片上传配置项 */
                    "\"scrawlActionName\": \"uploadscrawl\",\n" + /* 执行上传涂鸦的action名称 */
                    "\"scrawlFieldName\": \"upfile\", \n" +/* 提交的图片表单名称 */
                    "\"scrawlPathFormat\": \"\", \n" +/* 上传保存路径,可以自定义保存路径和文件名格式 */
                    "\"scrawlMaxSize\": 2048000, \n" +/* 上传大小限制，单位B */
                    "\"scrawlUrlPrefix\": \"\",\n" + /* 图片访问路径前缀 */
                    "\"scrawlInsertAlign\": \"none\",\n" +
                    /* 截图工具上传 */
                    "\"snapscreenActionName\": \"uploadimage\", \n" +/* 执行上传截图的action名称 */
                    "\"snapscreenPathFormat\": \"\", \n" +/* 上传保存路径,可以自定义保存路径和文件名格式 */
                    "\"snapscreenUrlPrefix\": \"\", \n" +/* 图片访问路径前缀 */
                    "\"snapscreenInsertAlign\": \"none\", \n" +/* 插入的图片浮动方式 */
                    /* 抓取远程图片配置 */
                    "\"catcherLocalDomain\": [\"resource.syhdoctor.com\",\"www.syhdoctor.com\"],\n" +
                    "\"catcherActionName\": \"catchimage\",\n" + /* 执行抓取远程图片的action名称 */
                    "\"catcherFieldName\": \"source\", \n" +/* 提交的图片列表表单名称 */
                    "\"catcherPathFormat\": \"\", \n" +/* 上传保存路径,可以自定义保存路径和文件名格式 */
                    "\"catcherUrlPrefix\": \"\", \n" +/* 图片访问路径前缀 */
                    "\"catcherMaxSize\": 2048000, \n" +/* 上传大小限制，单位B */
                    "\"catcherAllowFiles\": [\".png\", \".jpg\", \".jpeg\", \".gif\", \".bmp\"], \n" +/* 抓取图片格式显示 */
                    /* 上传视频配置 */
                    "\"videoActionName\": \"uploadvideo\", \n" +/* 执行上传视频的action名称 */
                    "\"videoFieldName\": \"upfile\", \n" +/* 提交的视频表单名称 */
                    "\"videoPathFormat\": \"\", \n" +/* 上传保存路径,可以自定义保存路径和文件名格式 */
                    "\"videoUrlPrefix\": \"\", \n" +/* 视频访问路径前缀 */
                    "\"videoMaxSize\": 102400000, \n" +/* 上传大小限制，单位B，默认100MB */
                    "\"videoAllowFiles\": [\n" +
                    "\".flv\", \".swf\", \".mkv\", \".avi\", \".rm\", \".rmvb\", \".mpeg\", \".mpg\",\n" +
                    "\".ogg\", \".ogv\", \".mov\", \".wmv\", \".mp4\", \".webm\", \".mp3\", \".wav\", \".mid\"], \n" +/* 上传视频格式显示 */
                    /* 上传文件配置 */
                    "\"fileActionName\": \"uploadfile\", \n" +/* controller里,执行上传视频的action名称 */
                    "\"fileFieldName\": \"upfile\", \n" +/* 提交的文件表单名称 */
                    "\"filePathFormat\": \"\", \n" +/* 上传保存路径,可以自定义保存路径和文件名格式 */
                    "\"fileUrlPrefix\": \"\", \n" +/* 文件访问路径前缀 */
                    "\"fileMaxSize\": 51200000, \n" +/* 上传大小限制，单位B，默认50MB */
                    "\"fileAllowFiles\": [\n" +
                    "\".png\", \".jpg\", \".jpeg\", \".gif\", \".bmp\",\n" +
                    "\".flv\", \".swf\", \".mkv\", \".avi\", \".rm\", \".rmvb\", \".mpeg\", \".mpg\",\n" +
                    "\".ogg\", \".ogv\", \".mov\", \".wmv\", \".mp4\", \".webm\", \".mp3\", \".wav\", \".mid\",\n" +
                    "\".rar\", \".zip\", \".tar\", \".gz\", \".7z\", \".bz2\", \".cab\", \".iso\",\n" +
                    "\".doc\", \".docx\", \".xls\", \".xlsx\", \".ppt\", \".pptx\", \".pdf\", \".txt\", \".md\", \".xml\"\n" +
                    "],\n" + /* 上传文件格式显示 */
                    /* 列出指定目录下的图片 */
                    "\"imageManagerActionName\": \"listimage\",\n" + /* 执行图片管理的action名称 */
                    "\"imageManagerListPath\": \"\",\n" + /* 指定要列出图片的目录 */
                    "\"imageManagerListSize\": 20, \n" +/* 每次列出文件数量 */
                    "\"imageManagerUrlPrefix\": \"\", \n" +/* 图片访问路径前缀 */
                    "\"imageManagerInsertAlign\": \"none\", \n" +/* 插入的图片浮动方式 */
                    "\"imageManagerAllowFiles\": [\".png\", \".jpg\", \".jpeg\", \".gif\", \".bmp\"], \n" +/* 列出的文件类型 */
                    /* 列出指定目录下的文件 */
                    "\"fileManagerActionName\": \"listfile\", \n" +/* 执行文件管理的action名称 */
                    "\"fileManagerListPath\": \"\", \n" +/* 指定要列出文件的目录 */
                    "\"fileManagerUrlPrefix\": \"\", \n" +/* 文件访问路径前缀 */
                    "\"fileManagerListSize\": 20, \n" +/* 每次列出文件数量 */
                    "\"fileManagerAllowFiles\": [\n" +
                    "\".png\", \".jpg\", \".jpeg\", \".gif\", \".bmp\",\n" +
                    "\".flv\", \".swf\", \".mkv\", \".avi\", \".rm\", \".rmvb\", \".mpeg\", \".mpg\",\n" +
                    "\".ogg\", \".ogv\", \".mov\", \".wmv\", \".mp4\", \".webm\", \".mp3\", \".wav\", \".mid\",\n" +
                    "\".rar\", \".zip\", \".tar\", \".gz\", \".7z\", \".bz2\", \".cab\", \".iso\",\n" +
                    "\".doc\", \".docx\", \".xls\", \".xlsx\", \".ppt\", \".pptx\", \".pdf\", \".txt\", \".md\", \".xml\"\n" +
                    "] \n" +/* 列出的文件类型 */
                    "}";


    /**
     * Ueditor的返回状态类型
     */
    public enum UeditorMsg {
        SUCCESS("SUCCESS"), ERROR("上传失败");
        private String v;

        UeditorMsg(String v) {
            this.v = v;
        }

        public String get() {
            return this.v;
        }
    }
}
