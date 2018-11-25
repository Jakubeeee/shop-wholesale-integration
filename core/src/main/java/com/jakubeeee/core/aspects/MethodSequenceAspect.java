package com.jakubeeee.core.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * If activated, this aspect registers every entry and exit from every method in spring beans,
 * also informing about input parameters and return values
 */
@Slf4j
@Aspect
@Component
@ConditionalOnExpression("${enable.method.sequence.monitor:true}")
public class MethodSequenceAspect {

    @Around("execution( * com.jakubeeee..*.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        LOG.info("Starting method : \"" + joinPoint.getSignature().getName() +
                "\" in class: \"" + joinPoint.getSignature().getDeclaringTypeName() + "\"");
        try {
            for (var param : joinPoint.getArgs()) LOG.info("Parameter value in above method: " + param.toString());
        } catch (NullPointerException ignored) {
        }
        Object result = joinPoint.proceed();
        LOG.info("Exiting method :\"" + joinPoint.getSignature().getName() +
                "\" in class: \"" + joinPoint.getSignature().getDeclaringTypeName() + "\"" +
                " with return value: \"" + result + "\"");
        return result;
    }
}