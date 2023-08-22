package ru.trueengineering.lib.logger.autoconfigure.aspect;


import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.trueengineering.lib.logger.autoconfigure.RequestMappingLogger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public final class AspectLoggerUtils {

    //    MDC arguments name
    public static final String REQUEST_MILLIS = "requestMillis";
    public static final String API = "API";
    public static final String METHOD = "METHOD";
    public static final String CLASS = "CLASS";

    private static final RequestMappingLogger DEFAULT = new RequestMappingLogger() {
        @Override
        public Class<? extends Annotation> annotationType() {
            return RequestMappingLogger.class;
        }

        @Override
        public boolean value() {
            return true;
        }

        @Override
        public boolean args() {
            return true;
        }

        @Override
        public boolean output() {
            return true;
        }
    };

    public static RequestMappingLogger getAnnotationOrDefault(MethodInvocation pjp) {
        RequestMappingLogger annotation = getAnnotation(pjp);
        if (annotation == null) {
            return DEFAULT;
        }
        return annotation;
    }

    public static Logger getLogger(MethodInvocation pjp) {
        return LoggerFactory.getLogger(pjp.getThis().getClass());
    }

    private static RequestMappingLogger getAnnotation(MethodInvocation pjp) {
        RequestMappingLogger annotation = pjp.getThis().getClass().getAnnotation(RequestMappingLogger.class);
        if (annotation != null && !annotation.value()) { //logging off
            return annotation;
        }
        Method method = pjp.getMethod();
        final RequestMappingLogger annotation2 = method.getAnnotation(RequestMappingLogger.class);
        if (annotation2 != null) {
            return annotation2;
        }
        return annotation;
    }

}
