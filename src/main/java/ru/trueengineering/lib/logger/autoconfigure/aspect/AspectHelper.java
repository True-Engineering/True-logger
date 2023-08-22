package ru.trueengineering.lib.logger.autoconfigure.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.AnnotatedElement;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.util.DigestUtils;
import ru.trueengineering.lib.logger.autoconfigure.SecuredDataObject;

public class AspectHelper {

    private static final String MD_5 = "MD5";

    private static final String DEFAULT_MASKED_STRING = "[SecuredDataObject]****";

    private static final Logger log = LoggerFactory.getLogger(AspectHelper.class);

    /**
     * see {@link MethodInvocationProceedingJoinPoint}
     */
    private static final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    public static StringBuilder getArgs(MethodInvocation pjp,
                                        ObjectMapper objectMapper,
                                        Collection<String> basePackageForModels) {
        StringBuilder builder = new StringBuilder();
        final Parameter[] parameters = pjp.getMethod().getParameters();
        if (parameters != null) {
            final List<String> parameterNames = getParameterNames(pjp);
            int i = 0;
            for (Object arg : pjp.getArguments()) {
                if (isOwnOrPrimitiveOrStringOrMapOrCollection(arg, basePackageForModels)) {
                    try {
                        builder.append('\n');
                        builder.append(parameterNames.get(i));
                        builder.append(" : ");
                        builder.append(getSerializedObjectString(arg, parameters[i], objectMapper));
                    } catch (Exception e) {
                        log.error("Error occurred during building args", e);
                    }
                }
                i++;
            }
        }
        return builder;
    }

    public static Object getJsonResponse(Object output,
                                         AnnotatedElement annotatedElement,
                                         ObjectMapper objectMapper,
                                         List<String> basePackagesForModels) {
        try {
            if (isOwnOrPrimitiveOrStringOrMapOrCollection(output, basePackagesForModels)) {
                return getSerializedObjectString(output, annotatedElement, objectMapper);
            }
            return output == null ? "" : output.toString();
        } catch (JsonProcessingException e) {
            log.error("Error during build json {}", output, e);
            return "";
        }
    }

    /**
     * Returns serialized object string. Masked, if {@link SecuredDataObject} annotation is present.
     *
     * @param object - object to log
     * @param element - annotated element that corresponds to loggable object
     * @param objectMapper - Jackson object mapper
     *
     * @return serialized string, masked if needed
     * @throws JsonProcessingException if problems occurred while processing JSON content
     */
    protected static String getSerializedObjectString(Object object,
                                                      AnnotatedElement element,
                                                      ObjectMapper objectMapper)
        throws JsonProcessingException {
        String serializedObjectString = objectMapper.writeValueAsString(object);

        if (element.isAnnotationPresent(SecuredDataObject.class)) {
            return maskString(serializedObjectString);
        }

        return serializedObjectString;
    }

    private static String maskString(String value) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(MD_5);
        } catch (NoSuchAlgorithmException e) {
            log.error("Error during instantiating message digest", e);
            return DEFAULT_MASKED_STRING;
        }
        byte[] md5Bytes = messageDigest.digest(value.getBytes(StandardCharsets.UTF_8));

        return DigestUtils.md5DigestAsHex(md5Bytes).substring(0, 8) + "****";
    }

    private static boolean isOwnOrPrimitiveOrStringOrMapOrCollection(Object arg, Collection<String> basePackageForModels) {
        if (arg == null) {
            return false;
        }

        return arg.getClass().getPackage() != null
                && (basePackageForModels.stream().anyMatch(it -> arg.getClass().getPackage().getName().startsWith(it))
                || ClassUtils.isPrimitiveOrWrapper(arg.getClass())
                || arg.getClass() == String.class)
                || arg instanceof Collection
                || arg instanceof Map
                || arg.getClass() == byte[].class;
    }

    private static List<String> getParameterNames(MethodInvocation pjp) {
        final int length = pjp.getArguments().length;

        final String[] parameterNames = parameterNameDiscoverer.getParameterNames(pjp.getMethod());

        if (parameterNames != null && parameterNames.length < length) {
            final List<String> names = new ArrayList<>(length);
            int i = 0;
            for (String parameterName : parameterNames) {
                names.set(i, parameterName);
                i++;
            }
            return names;
        }
        return Optional.ofNullable(parameterNames)
                .map(it -> Stream.of(it).collect(Collectors.toList()))
                .orElse(new ArrayList<>());
    }
}
