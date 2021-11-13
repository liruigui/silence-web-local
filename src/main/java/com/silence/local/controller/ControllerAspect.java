package com.silence.local.controller;

import com.alibaba.fastjson.JSON;
import com.silence.module.common.model.DataResult;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * @author silence.2021-01-27
 * Aspect 该标签把ControllerAspect类声明为一个切面
 * Order(1) 设置切面的优先级：如果有多个切面，可通过设置优先级控制切面的执行顺序（数值越小，优先级越高）
 * Component 该标签把ControllerAspect类放到IOC容器中
 */
@Aspect
@Component
public class ControllerAspect {

    private Logger logger = LoggerFactory.getLogger(ControllerAspect.class);

    /**
     * 最大错误信息长度
     */
    private final int MAX_ERROR_MESSAGE_LENGTH = 500;

    /**
     * 定义一个方法，用于声明切入点表达式，方法中一般不需要添加其他代码
     * 使用@Pointcut声明切入点表达式
     * 后面的通知直接使用方法名来引用当前的切点表达式；如果是其他类使用，加上包名即可
     */
    @Pointcut("execution(public * com.silence.local.controller..*Controller.*(..))")
    public void declareJoinPointExpression() {

    }


    /**
     * 环绕通知(需要携带类型为ProceedingJoinPoint类型的参数)
     * 环绕通知包含前置、后置、返回、异常通知；ProceedingJoinPoin 类型的参数可以决定是否执行目标方法
     * 且环绕通知必须有返回值，返回值即目标方法的返回值
     *
     * @param joinPoint 参数
     */
    @Around(value = "declareJoinPointExpression()")
    public Object aroundMethod(ProceedingJoinPoint joinPoint) {

        Object result = null;
        String methodName = joinPoint.getSignature().getName();

        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();

        long startTime = System.currentTimeMillis();
        try {

            //执行目标方法
            result = joinPoint.proceed();

            //后置通知
            if (result instanceof DataResult) {
                return ((DataResult) result).calculateUseMillisecond(startTime).toMap();
            }

            return result;

        } catch (Throwable e) {

            //异常通知
            logger.error("this method " + methodName + " end.Throwable ", e);

            if (request.getMethod().equals("GET")) {
                ModelAndView mav = new ModelAndView();
                mav.setViewName("template/requestGetError");
                mav.addObject("errorTitle", e.getMessage());
                mav.addObject("errorDetail", JSON.toJSONString(e));
                return mav;
            }

            return new DataResult<>(e).calculateUseMillisecond(startTime).toMap();
        }
    }
}
