package com.syhdoctor.webserver.filter;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.webserver.service.system.SystemService;
import com.syhdoctor.webserver.service.verupdate.VerupdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebFilter(filterName = "appFilter", urlPatterns = {"/App/*"})
public class BAppFilter implements Filter {

    private final static String UPDATE_ERROR = "{\"result\": -101,\"message\": \"检测到新版本\",\"url\": \"%s\",\"Url\": \"%s\"}";

    private static Logger log = LoggerFactory.getLogger(BAppFilter.class);


    private VerupdateService verupdateService;

    private SystemService systemService;

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        ServletContext context = request.getServletContext();
        WebApplicationContext cxt = WebApplicationContextUtils.getWebApplicationContext(context);
        if (cxt != null && cxt.getBean(VerupdateService.class) != null && verupdateService == null) {
            verupdateService = cxt.getBean(VerupdateService.class);
        }

        if (cxt != null && cxt.getBean(SystemService.class) != null && systemService == null) {
            systemService = cxt.getBean(SystemService.class);
        }
        log.info("VerupdateFilter >> 版本验证开始");
        String phoneTypeStr = request.getParameter("phonetype");
        String systemStr = request.getParameter("system");
        if (!StrUtil.isEmpty(phoneTypeStr) && !StrUtil.isEmpty(systemStr)) {
            String version = request.getParameter("version");
            //1.是安卓 2.IOS
            int phonetype = ModelUtil.strToInt(phoneTypeStr, 0);
            //信鸽tonken
            String xingeTonken = request.getParameter("xingetoken");
            int userId = ModelUtil.strToInt(request.getParameter("userid"), 0);
            int doctorId = ModelUtil.strToInt(request.getParameter("doctorid"), 0);
            //1.用户端 2.医生端
            int system = ModelUtil.strToInt(systemStr, 0);

            if (system == 1 && userId > 0 && phonetype > 0 && !StrUtil.isEmpty(xingeTonken)) {
                systemService.updateUserXgToken(xingeTonken, phonetype, userId);
            }
            if (system == 2 && doctorId > 0 && phonetype > 0 && !StrUtil.isEmpty(xingeTonken)) {
                systemService.updateDoctorXgToken(xingeTonken, phonetype, doctorId);
            }

            Map<String, Object> checkUpdate = verupdateService.checkUpdate(phonetype, system);
            String vernumber = ModelUtil.getStr(checkUpdate, "vernumber");
            int identification = ModelUtil.getInt(checkUpdate, "identification");
            String url = ModelUtil.getStr(checkUpdate, "url");
            if (identification == 1 && !compareVersion(version, vernumber)) {
                httpServletResponse.setContentType("text/html;charset=utf-8");
                httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
                httpServletResponse.getWriter().print(String.format(UPDATE_ERROR, url, url));
                httpServletResponse.getWriter().flush();
                httpServletResponse.getWriter().close();
            } else {
                chain.doFilter(httpServletRequest, httpServletResponse);
            }
        } else {
            chain.doFilter(httpServletRequest, httpServletResponse);
            log.info("phonetype or system isnull  phonetype = " + phoneTypeStr + "system = " + systemStr);
        }
        log.info("VerupdateFilter >> 版本验证结束");
    }

    @Override
    public void destroy() {

    }

    /**
     * 版本号比较
     *
     * @param currentVersion 1代表currentVersion大于serverVersion
     * @param serverVersion  -1代表currentVersion小于serverVersion
     * @return 0代表相等
     */
    public static boolean compareVersion(String currentVersion, String serverVersion) {
        if (currentVersion.equals(serverVersion)) {
            return true;
        }
        String[] version1Array = currentVersion.split("\\.");
        String[] version2Array = serverVersion.split("\\.");
        int index = 0;
        // 获取最小长度值
        int minLen = Math.min(version1Array.length, version2Array.length);
        int diff = 0;
        // 循环判断每位的大小
        while (index < minLen
                && (diff = Integer.parseInt(version1Array[index])
                - Integer.parseInt(version2Array[index])) == 0) {
            index++;
        }
        if (diff == 0) {
            // 如果位数不一致，比较多余位数
            for (int i = index; i < version1Array.length; i++) {
                if (Integer.parseInt(version1Array[i]) > 0) {
                    return true;
                }
            }

            for (int i = index; i < version2Array.length; i++) {
                if (Integer.parseInt(version2Array[i]) > 0) {
                    return false;
                }
            }
            return true;
        } else {
            return diff > 0;
        }
    }
}
