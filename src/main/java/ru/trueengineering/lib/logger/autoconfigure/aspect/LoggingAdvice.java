package ru.trueengineering.lib.logger.autoconfigure.aspect;


import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.trueengineering.lib.logger.autoconfigure.handler.AfterExecutionProceed;
import ru.trueengineering.lib.logger.autoconfigure.handler.AfterThrowingHandler;
import ru.trueengineering.lib.logger.autoconfigure.handler.BeforeExecutionHandler;

import java.util.List;

/**
 * @author m.yastrebov
 */
public class LoggingAdvice implements MethodInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoggingAdvice.class);

    private final List<BeforeExecutionHandler> beforeExecutionHandlers;
    private final List<AfterThrowingHandler> afterThrowingHandler;
    private final List<AfterExecutionProceed> afterExecutionProceeds;

    public LoggingAdvice(List<BeforeExecutionHandler> beforeExecutionHandlers,
                         List<AfterThrowingHandler> afterThrowingHandler,
                         List<AfterExecutionProceed> afterExecutionProceeds) {
        this.beforeExecutionHandlers = beforeExecutionHandlers;
        this.afterThrowingHandler = afterThrowingHandler;
        this.afterExecutionProceeds = afterExecutionProceeds;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        for (BeforeExecutionHandler beforeExecutionHandler : beforeExecutionHandlers) {
            try {
                beforeExecutionHandler.before(invocation);
            } catch (Throwable throwable) {
                log.error("Failed to execute a beforeExecutionHandler", throwable);
            }
        }

        Object output;
        try {
            output = invocation.proceed();
        } catch (Throwable throwable) {
            for (AfterThrowingHandler afterThrowingHandler : this.afterThrowingHandler) {
                try {
                    afterThrowingHandler.afterThrowing(invocation, throwable);
                } catch (Throwable handlerThrowable) {
                    log.error("Failed to execute an afterThrowingHandler", handlerThrowable);
                }
            }
            throw throwable;
        }

        for (AfterExecutionProceed afterExecutionProceed : afterExecutionProceeds) {
            try {
                afterExecutionProceed.after(invocation, output);
            } catch (Throwable throwable) {
                log.error("Failed to execute an afterExecutionProceed", throwable);
            }
        }

        return output;
    }
}
