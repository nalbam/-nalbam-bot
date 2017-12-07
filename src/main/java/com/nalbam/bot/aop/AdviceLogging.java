package com.nalbam.bot.aop;

import com.nalbam.common.util.PackageUtil;
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
import java.util.Map;

@Slf4j
@Aspect
@Component
public class AdviceLogging {

    @Value("${app.product}")
    private String product;

    @Value("${app.profile}")
    private String profile;

    @Before(value = "execution(* com..*Controller.*(..))")
    public void loggingAdvice(final JoinPoint joinPoint) {
        MDC.put("product", this.product);
        MDC.put("profile", this.profile);

        final Map<String, String> data = PackageUtil.getData(this.getClass());
        MDC.put("version", data.get("version"));

        final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        MDC.put("x-forwarded-for", request.getHeader("X-Forwarded-For"));
        MDC.put("remote", request.getRemoteAddr());
        MDC.put("request", request.getRequestURI());
        MDC.put("method", request.getMethod());
    }

}
