package com.example.HospitalManagement.Interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod; // <--- WEB METHOD PACKAGE (CORRECT)
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class PerformanceLoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME = "startTime";

    // 1. Controller Execute hone SE PEHLE (Timer Start)
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,Object handler){

        log.info("Interceptor Active : Incoming request: {}", request.getRequestURI());

        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME,startTime);
        return true;   // Request ko aage jaane do

    }


    @Override // <--- @Override Lagaya, ab Spelling & Order Check Hoga
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        log.info("Interceptor Active : AfterCompletion with ClientID : {}", request.getRemoteAddr());

        Long startTime = (Long) request.getAttribute(START_TIME);
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;

            if (handler instanceof HandlerMethod handlerMethod) {
                String controllerName = handlerMethod.getBeanType().getSimpleName();
                String methodName = handlerMethod.getMethod().getName();
                String path = request.getRequestURI();
                String httpMethod = request.getMethod();

                if (duration > 350) {
                    log.warn("⚠️ SLOW API RESPONSE | Path: {} [{}] | Controller: {}#{} | Execution Time: {} ms",
                            path, httpMethod, controllerName, methodName, duration);
                } else {
                    log.info("✅ FAST API RESPONSE | Path: {} [{}] | Controller: {}#{} | Execution Time: {} ms",
                            path, httpMethod, controllerName, methodName, duration);
                }
            }
        }
    }
}
