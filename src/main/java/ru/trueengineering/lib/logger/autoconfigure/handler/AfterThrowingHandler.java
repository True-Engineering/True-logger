package ru.trueengineering.lib.logger.autoconfigure.handler;

import org.aopalliance.intercept.MethodInvocation;

/**
 * @author m.yastrebov
 */
public interface AfterThrowingHandler {
    void afterThrowing(MethodInvocation pjp, Throwable throwable);
}

