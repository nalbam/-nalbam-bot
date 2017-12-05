package com.nalbam.bot.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Aspect
@Component
public class AdviceLogging {

    @Value("${spring.application.name}")
    private String name;

    @Value("${spring.profiles.active}")
    private String profile;

    @Before(value = "execution(* com..*Controller.*(..))")
    public void loggingAdvice(final JoinPoint joinPoint) {
        MDC.put("product", this.name);
        MDC.put("profile", this.profile);

        final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        MDC.put("x-forwarded-for", request.getHeader("X-Forwarded-For"));
        MDC.put("remote", request.getRemoteAddr());
        MDC.put("request", request.getRequestURI());
        MDC.put("method", request.getMethod());
    }

}
