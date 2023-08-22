package ru.trueengineering.lib.logger.autoconfigure.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.AnnotatedType;
import net.logstash.logback.argument.StructuredArgument;
import net.logstash.logback.argument.StructuredArguments;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.trueengineering.lib.logger.autoconfigure.RequestMappingLogger;
import ru.trueengineering.lib.logger.autoconfigure.aspect.AspectHelper;
import ru.trueengineering.lib.logger.autoconfigure.aspect.AspectLoggerUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.trueengineering.lib.logger.autoconfigure.aspect.AspectLoggerUtils.getLogger;

/**
 * @author m.yastrebov
 */
public class RequestSpentTimeLogger implements BeforeExecutionHandler, AfterExecutionProceed, AfterThrowingHandler {

    private static final Logger log = LoggerFactory.getLogger(RequestSpentTimeLogger.class);
    //templates
    private static final String METHOD_ARGS_TEMPLATE = "Method {}.{} {}";
    private static final String METHOD_SPENT_TIME_TEMPLATE = "Method {}.{} {}, spent millis = {}";
    private static final String FAILED_METHOD_SPENT_TIME_TEMPLATE = "Failed method {}.{} {}, spent millis = {}";

    private final ObjectMapper objectMapper;

    private final List<String> basePackagesForModels;

    private final List<AfterExecutionAdditionalLoggingDataProducer> loggingDataProducers;

    private final ThreadLocal<StopWatch> stopwatch = ThreadLocal.withInitial(StopWatch::new);

    public RequestSpentTimeLogger(ObjectMapper objectMapper, List<String> basePackagesForModels,
                                  List<AfterExecutionAdditionalLoggingDataProducer> loggingDataProducers) {
        this.objectMapper = objectMapper;
        this.basePackagesForModels = basePackagesForModels;
        this.loggingDataProducers = loggingDataProducers != null ? loggingDataProducers : new ArrayList<>();
    }

    @Override
    public void before(MethodInvocation pjp) {
        RequestMappingLogger annotation = getAnnotationOrDefault(pjp);
        if (annotation.value()) {
            stopwatch.get().start();
            logRequest(pjp, annotation);
        }
    }

    private void logRequest(MethodInvocation pjp, RequestMappingLogger annotation) {
        final Class<?> aClass = pjp.getThis().getClass();
        Method method = pjp.getMethod();
        final String name = method.getName();
        final String api = getRequestPath(aClass, method);
        if (annotation.value() && annotation.args()) {
            StringBuilder argsBuilder = AspectHelper.getArgs(pjp, objectMapper, basePackagesForModels);
            getLogger(pjp).info(METHOD_ARGS_TEMPLATE,
                    aClass.getSimpleName(), name,
                    argsBuilder,
                    StructuredArguments.value(AspectLoggerUtils.API, api),
                    StructuredArguments.value(AspectLoggerUtils.METHOD, name),
                    StructuredArguments.value(AspectLoggerUtils.CLASS, aClass.getSimpleName())
            );
        } else if (annotation.value()) {
            getLogger(pjp).info("Method {}.{}", aClass.getSimpleName(), name);
        }

    }

    private RequestMappingLogger getAnnotationOrDefault(MethodInvocation pjp) {
        return AspectLoggerUtils.getAnnotationOrDefault(pjp);
    }

    @Override
    public void after(MethodInvocation pjp, Object output) {
        final RequestMappingLogger annotation = getAnnotationOrDefault(pjp);
        if (!annotation.value()) {
            return;
        }
        stopwatch.get().stop();

        if (annotation.value() && getLogger(pjp).isInfoEnabled()) {
            final Class<?> aClass = pjp.getThis().getClass();
            Method method = pjp.getMethod();
            final String api = getRequestPath(aClass, method);
            final String name = method.getName();
            final long durationMilliseconds = stopwatch.get().getLastTaskTimeMillis();
            final Object[] baseLoggerArguments = {
                    aClass.getSimpleName(),
                    name,
                    getJsonResponse(annotation, output, method.getAnnotatedReturnType(), objectMapper),
                    buildDurationSummary(durationMilliseconds),
                    StructuredArguments.value(AspectLoggerUtils.REQUEST_MILLIS, durationMilliseconds),
                    StructuredArguments.value(AspectLoggerUtils.API, api),
                    StructuredArguments.value(AspectLoggerUtils.METHOD, name),
                    StructuredArguments.value(AspectLoggerUtils.CLASS, aClass.getSimpleName())
            };
            final Object[] loggerArguments = addAdditionalLoggerArguments(baseLoggerArguments);
            getLogger(pjp).info(METHOD_SPENT_TIME_TEMPLATE, loggerArguments);
        }
        stopwatch.remove();
    }

    private Object getJsonResponse(RequestMappingLogger annotation,
                                   Object output,
                                   AnnotatedType annotatedReturnType,
                                   ObjectMapper objectMapper) {
        return annotation.output()
            ? AspectHelper.getJsonResponse(output, annotatedReturnType, objectMapper, basePackagesForModels)
            : "";
    }

    @Override
    public void afterThrowing(MethodInvocation pjp, Throwable throwable) {
        final RequestMappingLogger annotation = getAnnotationOrDefault(pjp);
        if (!annotation.value()) {
            return;
        }
        stopwatch.get().stop();
        if (annotation.value() && getLogger(pjp).isErrorEnabled()) {
            final Class<?> aClass = pjp.getThis().getClass();
            Method method = pjp.getMethod();
            final String api = getRequestPath(aClass, method);
            final String name = method.getName();
            final long durationMilliseconds = stopwatch.get().getLastTaskTimeMillis();
            Object[] baseLoggerArguments = {
                    aClass.getSimpleName(),
                    name,
                    buildDurationSummary(durationMilliseconds),
                    stopwatch.get().getLastTaskTimeMillis(),
                    StructuredArguments.value(AspectLoggerUtils.REQUEST_MILLIS, durationMilliseconds),
                    StructuredArguments.value(AspectLoggerUtils.API, api),
                    StructuredArguments.value(AspectLoggerUtils.METHOD, name),
                    StructuredArguments.value(AspectLoggerUtils.CLASS, aClass.getSimpleName()),
                    throwable
            };
            final Object[] loggerArguments = addAdditionalLoggerArguments(baseLoggerArguments);
            getLogger(pjp).error(FAILED_METHOD_SPENT_TIME_TEMPLATE, loggerArguments);
        }
        stopwatch.remove();
    }

    private Object[] addAdditionalLoggerArguments(Object[] baseLoggerArguments) {
        final StructuredArgument[] additionalLoggerArguments = buildLoggerArguments();
        final Object[] loggerArguments;
        if (additionalLoggerArguments != null && additionalLoggerArguments.length != 0) {
            loggerArguments = Arrays.copyOf(baseLoggerArguments,
                    baseLoggerArguments.length + additionalLoggerArguments.length);
            System.arraycopy(
                    additionalLoggerArguments, 0,
                    loggerArguments,
                    baseLoggerArguments.length,
                    additionalLoggerArguments.length
            );
        } else {
            loggerArguments = baseLoggerArguments;
        }
        return loggerArguments;
    }

    private StructuredArgument[] buildLoggerArguments() {
        List<StructuredArgument> structuredArguments = new ArrayList<>();
        loggingDataProducers.stream()
                .map(AfterExecutionAdditionalLoggingDataProducer::buildLoggerArguments)
                .map(Arrays::asList)
                .forEach(structuredArguments::addAll);
        return structuredArguments.toArray(new StructuredArgument[0]);
    }

    private String buildDurationSummary(long durationMilliseconds) {
        return "StopWatch '': running time (millis) = " + durationMilliseconds;
    }

    public interface AfterExecutionAdditionalLoggingDataProducer {
        StructuredArgument[] buildLoggerArguments();
    }

    private String getRequestPath(Class<?> aClass, Method method) {
        String classRequestPath = getClassRequestPath(aClass);
        String methodRequestPath = getMethodRequestPath(method);
        return classRequestPath + methodRequestPath;
    }

    private String getClassRequestPath(Class<?> aClass) {
        String[] value = null;
        RequestMapping requestMapping = aClass.getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            value = requestMapping.value();
        }
        if (value != null && value.length > 0) {
            return value[0];
        }
        return "";
    }

    private String getMethodRequestPath(Method method) {
        String[] value = null;
        if (method.isAnnotationPresent(GetMapping.class)) {
            value = method.getAnnotation(GetMapping.class).value();
        } else if (method.isAnnotationPresent(PostMapping.class)) {
            value = method.getAnnotation(PostMapping.class).value();
        } else if (method.isAnnotationPresent(PatchMapping.class)) {
            value = method.getAnnotation(PatchMapping.class).value();
        } else if (method.isAnnotationPresent(PutMapping.class)) {
            value = method.getAnnotation(PutMapping.class).value();
        } else if (method.isAnnotationPresent(DeleteMapping.class)) {
            value = method.getAnnotation(DeleteMapping.class).value();
        }
        if (value != null && value.length > 0) {
            return value[0];
        }
        return "";
    }

}
