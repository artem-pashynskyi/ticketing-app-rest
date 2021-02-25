package com.ticketingapp.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Aspect
@Configuration
public class LoggingAspect {

    Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* com.ticketingapp.controller.ProjectController.*(..)) || execution(* com.ticketingapp.controller.TaskController.*(..))")
    private void anyControllerOperation() {}

    @Before("anyControllerOperation()")
    public void anyBeforeOperationAdvice(JoinPoint joinPoint) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Before(User : {} Method : {} - Parameters : {})", auth.getName(), joinPoint.getSignature().toShortString(), joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "anyControllerOperation()", returning = "results")
    public void anyAfterReturningControllerOperationAdvice(JoinPoint joinPoint, Object results) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.info("AfterReturning(User : {} Method : {} - Results : {})", auth.getName(), joinPoint.getSignature().toShortString(), results.toString());
    }

    @AfterThrowing(pointcut = "anyControllerOperation()", throwing = "exception")
    public void anyAfterThrowingControllerOperationAdvice(JoinPoint joinPoint, RuntimeException exception) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.info("AfterThrowing(User : {} Method : {} - Exception : {})", auth.getName(), joinPoint.getSignature().toShortString(), exception.getLocalizedMessage());
    }

}