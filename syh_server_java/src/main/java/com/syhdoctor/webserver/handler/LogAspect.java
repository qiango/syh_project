package com.syhdoctor.webserver.handler;

import com.syhdoctor.webserver.service.system.SystemService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qian.wang
 * @description
 * @date 2018/11/2
 * 匹配所有类public方法  execution(public * *(..))
 * 匹配指定包下所有类方法 execution(* com.baidu.dao.*(..)) 不包含子包
 * execution(* com.baidu.dao..*(..))  ..*表示包、子孙包下所有类
 * 匹配指定类所有方法 execution(* com.baidu.service.UserService.*(..))
 * 匹配实现特定接口所有类方法
 *     execution(* com.baidu.dao.GenericDAO+.*(..))
 * 匹配所有save开头的方法 execution(* save*(..))
 * ---------------------
 */

@Component
@Order(-99) // 控制多个Aspect的执行顺序，越小越先执行
@Aspect
public class LogAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);
    private final String POINT_CUT = "execution(* com.syhdoctor.webserver.controller..*(..))  ..*";
    @Autowired
    private SystemService systemService;

    @Pointcut(POINT_CUT)
    public void pointCut() {
    }

    @Before(value = "pointCut()")
    public void before(JoinPoint joinPoint) {
    }


    /**
     * 后置返回
     * 如果第一个参数为JoinPoint，则第二个参数为返回值的信息
     * 如果第一个参数不为JoinPoint，则第一个参数为returning中对应的参数
     * returning：限定了只有目标方法返回值与通知方法参数类型匹配时才能执行后置返回通知，否则不执行，
     * 参数为Object类型将匹配任何目标返回值
     */
    @AfterReturning(value = POINT_CUT, returning = "result")
    public void doAfterReturningAdvice1(JoinPoint joinPoint, Object result) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        logger.info(request.getRequestURI() + " ：params >" + StringUtils.join(joinPoint.getArgs()));

        logger.info(request.getRequestURI() + " ：data >" + result);
    }


    /**
     * 环绕通知：
     * 注意:Spring AOP的环绕通知会影响到AfterThrowing通知的运行,不要同时使用
     * <p>
     * 环绕通知非常强大，可以决定目标方法是否执行，什么时候执行，执行时是否需要替换方法参数，执行完毕是否需要替换返回值。
     * 环绕通知第一个参数必须是org.aspectj.lang.ProceedingJoinPoint类型
     */
    @Around(value = POINT_CUT)
    public Object doAroundAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return proceedingJoinPoint.proceed(); //可以加参数
    }

}
