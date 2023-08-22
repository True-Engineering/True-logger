package ru.trueengineering.lib.logger.autoconfigure.handler;

import org.aopalliance.intercept.MethodInvocation;

/**
 * @author m.yastrebov
 */
public interface AfterExecutionProceed {
    void after(MethodInvocation pjp, Object output);
}

