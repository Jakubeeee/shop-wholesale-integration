package com.jakubeeee.core.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import static com.jakubeeee.common.util.LangUtils.nvl;

/**
 * If activated, this aspect registers every entry and exit from every method in spring beans,
 * also informing about input parameters and return values.<br>
 * This aspect should be enabled only in development environment as a helpful tool in code debugging.<br>
 * To enable this aspect, the flag property in resources/core.properties file must be switched to true.
 */
@Slf4j
@Aspect
@Component
@ConditionalOnExpression("${enable.method.sequence.monitor:true}")
public final class MethodSequenceAspect {

    @Around("execution( * com.jakubeeee..*.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        LOG.debug("Starting method : \"" + joinPoint.getSignature().getName() +
                "\" in class: \"" + joinPoint.getSignature().getDeclaringTypeName() + "\"");
        for (var param : joinPoint.getArgs())
            LOG.debug("Parameter value in above method: " + nvl(param.toString(), param));
        Object result = joinPoint.proceed();
        LOG.debug("Exiting method :\"" + joinPoint.getSignature().getName() +
                "\" in class: \"" + joinPoint.getSignature().getDeclaringTypeName() + "\"" +
                " with return value: \"" + result + "\"");
        return result;
    }

}