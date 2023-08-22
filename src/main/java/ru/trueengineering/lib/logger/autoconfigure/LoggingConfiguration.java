package ru.trueengineering.lib.logger.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import ru.trueengineering.lib.logger.autoconfigure.aspect.LoggingAdvice;
import ru.trueengineering.lib.logger.autoconfigure.aspect.PackageBasedMethodMatcherPointcut;
import ru.trueengineering.lib.logger.autoconfigure.handler.AfterExecutionProceed;
import ru.trueengineering.lib.logger.autoconfigure.handler.AfterThrowingHandler;
import ru.trueengineering.lib.logger.autoconfigure.handler.BeforeExecutionHandler;
import ru.trueengineering.lib.logger.autoconfigure.handler.RequestSpentTimeLogger;
import ru.trueengineering.lib.logger.autoconfigure.mapping.ObjectMapperConfigurer;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author m.yastrebov
 */
@ConditionalOnProperty(value = "trueengineering.logging.enabled", havingValue = "true", matchIfMissing = true)
@Configuration
@EnableAspectJAutoProxy
public class LoggingConfiguration {

    @Bean
    public PackageBasedMethodMatcherPointcut pointcut(
            @Value("#{'${trueengineering.logging.packages}'.split(',')}") List<String> packageNamePatterns) {
        final List<Pattern> patterns = packageNamePatterns.stream()
                .map(Pattern::compile)
                .collect(Collectors.toList());
        return new PackageBasedMethodMatcherPointcut(patterns);
    }

    @Bean
    public RequestSpentTimeLogger requestSpentTimeLogger(
            @Autowired(required = false) Optional<ObjectMapper> objectMapperOptional,
            @Value("#{'${trueengineering.logging.base.package.for.models}'.split(',')}")
                    List<String> basePackageForModels,
            @Autowired(required = false) List<RequestSpentTimeLogger.AfterExecutionAdditionalLoggingDataProducer> loggingDataProducers
    ) {
        ObjectMapper objectMapper = ObjectMapperConfigurer.prepareObjectMapper(objectMapperOptional);
        return new RequestSpentTimeLogger(objectMapper, basePackageForModels, loggingDataProducers);
    }

    @Bean
    public LoggingAdvice loggingAdvice(
            List<BeforeExecutionHandler> beforeExecutionHandlers,
            List<AfterThrowingHandler> afterThrowingHandler,
            List<AfterExecutionProceed> afterExecutionProceeds
    ) {
        return new LoggingAdvice(beforeExecutionHandlers, afterThrowingHandler, afterExecutionProceeds);
    }

    @Bean
    public DefaultPointcutAdvisor advisor(PackageBasedMethodMatcherPointcut pointcut, LoggingAdvice loggingAdvice) {
        return new DefaultPointcutAdvisor(pointcut, loggingAdvice);
    }

}
