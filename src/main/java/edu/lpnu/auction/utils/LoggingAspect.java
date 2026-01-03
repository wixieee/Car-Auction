package edu.lpnu.auction.utils;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    private static final Set<String> SENSITIVE_KEYWORDS = Set.of(
            "password", "passwd", "pwd", "secret", "token", "auth", "credential", "cvv", "card"
    );

    @Pointcut("within(@org.springframework.stereotype.Repository *)" +
            " || within(@org.springframework.stereotype.Service *)" +
            " || within(@org.springframework.web.bind.annotation.RestController *)")
    public void springBeanPointcut() {}

    @Pointcut("within(edu.lpnu.auction..*)")
    public void applicationPackagePointcut() {}

    @Around("applicationPackagePointcut() && springBeanPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (log.isDebugEnabled()) {
            log.debug("Вхід: {}.{}() з аргументами = {}",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    getSafeArgs(joinPoint));
        }

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long timeTaken = System.currentTimeMillis() - startTime;

        if (log.isDebugEnabled()) {
            log.debug("Вихід: {}.{}() з результатом = {} (Час: {} мс)",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    result,
                    timeTaken);
        }
        return result;
    }

    private Map<String, Object> getSafeArgs(ProceedingJoinPoint joinPoint) {
        CodeSignature signature = (CodeSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] paramValues = joinPoint.getArgs();

        Map<String, Object> safeArgs = new HashMap<>();

        for (int i = 0; i < paramNames.length; i++) {
            String name = paramNames[i];
            Object value = paramValues[i];

            if (isSensitive(name)) {
                safeArgs.put(name, "*** MASKED ***");
            } else {
                safeArgs.put(name, value);
            }
        }
        return safeArgs;
    }

    private boolean isSensitive(String name) {
        String lowerName = name.toLowerCase();
        return SENSITIVE_KEYWORDS.stream().anyMatch(lowerName::contains);
    }
}