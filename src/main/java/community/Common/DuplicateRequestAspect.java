package community.Common;

import community.Exception.JwtException.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Aspect
@Component
@Slf4j
public class DuplicateRequestAspect {
    private Set<String> requestSet = Collections.synchronizedSet(new HashSet<>());

    @Pointcut("within(*..*Controller)")
    public void onRequest() {
    }

    @Around("onRequest()")
    public Object duplicateRequestCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String httpMethod = request.getMethod();

        if ("GET".equalsIgnoreCase(httpMethod)) {
            return joinPoint.proceed();
        }

        String requestId = joinPoint.getSignature().toLongString();
        if (requestSet.contains(requestId)) {
            log.error("중복된 요청입니다. " + requestId);
            throw new JwtException.DuplicateRequestException("중복된 요청입니다.");
        }

        requestSet.add(requestId);
        try {
            return joinPoint.proceed();
        } finally {
            requestSet.remove(requestId);
        }

    }


}
