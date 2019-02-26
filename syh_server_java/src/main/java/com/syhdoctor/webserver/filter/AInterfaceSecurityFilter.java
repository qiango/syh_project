package com.syhdoctor.webserver.filter;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.encryption.AESEncrypt;
import com.syhdoctor.common.utils.encryption.MD5Encrypt;
import com.syhdoctor.webserver.config.ConfigModel;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

//@WebFilter(filterName = "interfaceSecurityFilter", urlPatterns = "/*")
public class AInterfaceSecurityFilter implements Filter {


    private final static String AUTHORITY_ERROR = "{\"result\": -102,\"message\": \"没有权限访问\"}";


    @Override
    public void init(FilterConfig filterConfig) {

    }

    public static final String secretKey = "syh123456";


    public static void main(String[] args) {
        BigDecimal bigDecimal = ModelUtil.strToDec2(null, BigDecimal.ZERO);
        String format = String.format("%s|%s|%s|%s|%s", secretKey, 88, 0, "1533878838000", bigDecimal);
        System.out.println(format);
        String cipherMd5 = MD5Encrypt.getInstance().encrypt("111111");
        String cipherText = AESEncrypt.getInstance().encrypt(cipherMd5);
        String originalCipherText = MD5Encrypt.getInstance().encrypt(cipherText);
        System.out.println(originalCipherText);
    }


    /**
     * secretkey（syh123456） 秘钥
     * sign 签名  放header中
     * 参数： userid 用户id, doctorid 医生id, amountmoney 金额,timespan 时间戳；   没有默认为0
     * sign生成方式： 格式 secretkey|userid|doctorid|timespan|amountmoney 先MD5  然后AES 然后 MD5
     *
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        boolean isOk = false;
        if ("1".equals(ConfigModel.ISONLINE)) {
            try {
                if ("OPTIONS".equals(((HttpServletRequest) request).getMethod())) {
                    isOk = true;
                } else {
                    String sign = ((HttpServletRequest) request).getHeader("sign");
                    long userid = ModelUtil.strToLong(request.getParameter("userid"), 0);
                    long doctorid = ModelUtil.strToLong(request.getParameter("doctorid"), 0);
                    BigDecimal amountmoney = ModelUtil.strToDec2(request.getParameter("amountmoney"), BigDecimal.ZERO);
                    String timespan = request.getParameter("timespan");
                    if (!StrUtil.isEmpty(timespan, sign)) {
                        System.out.println(String.format("%s|%s|%s|%s|%s", secretKey, userid, doctorid, timespan, amountmoney));
                        String cipherMd5 = MD5Encrypt.getInstance().encrypt(String.format("%s|%s|%s|%s|%s", secretKey, userid, doctorid, timespan, amountmoney));
                        String cipherText = AESEncrypt.getInstance().encrypt(cipherMd5);
                        String originalCipherText = MD5Encrypt.getInstance().encrypt(cipherText);
                        System.out.println(sign);
                        System.out.println(originalCipherText);
                        if (sign.equals(originalCipherText)) {
                            isOk = true;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                isOk = false;
            }
        } else {
            isOk = true;
        }

        if (isOk) {
            chain.doFilter(httpServletRequest, httpServletResponse);
        } else {
            httpServletResponse.setContentType("text/html;charset=utf-8");
            httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
            httpServletResponse.getWriter().print(AUTHORITY_ERROR);
            httpServletResponse.getWriter().flush();
            httpServletResponse.getWriter().close();
        }
    }

    @Override
    public void destroy() {
    }
}
