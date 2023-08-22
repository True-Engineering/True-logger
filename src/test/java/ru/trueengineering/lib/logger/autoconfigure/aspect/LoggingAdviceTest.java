package ru.trueengineering.lib.logger.autoconfigure.aspect;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.trueengineering.lib.logger.autoconfigure.handler.AfterExecutionProceed;
import ru.trueengineering.lib.logger.autoconfigure.handler.AfterThrowingHandler;
import ru.trueengineering.lib.logger.autoconfigure.handler.BeforeExecutionHandler;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author a.chirikhin
 */
class LoggingAdviceTest {

    private LoggingAdvice uut;

    @Mock
    private BeforeExecutionHandler beforeExecutionHandler;

    @Mock
    private AfterExecutionProceed afterExecutionProceed;

    @Mock
    private AfterThrowingHandler afterThrowingHandler;

    @Mock
    private MethodInvocation methodInvocation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        uut = new LoggingAdvice(Collections.singletonList(beforeExecutionHandler),
                Collections.singletonList(afterThrowingHandler), Collections.singletonList(afterExecutionProceed));
    }

    @Test
    void invoke_methodIsProceed_ifThrowableIsThrownDuringBeforeHandleExecution() throws Throwable {
        doThrow(new AssertionError()).when(beforeExecutionHandler).before(eq(methodInvocation));

        uut.invoke(methodInvocation);

        verify(methodInvocation).proceed();
    }

    @Test
    void invoke_methodInvocationExceptionIsThrown_ifNewExceptionIsThrownDuringAfterThrowingHandlerExecution()
            throws Throwable {
        AssertionError methodInvocationError = new AssertionError("Method invocation error");
        doThrow(methodInvocationError).when(methodInvocation).proceed();
        RuntimeException afterThrowingHandlerException = new RuntimeException("After throwing handler exception");
        doThrow(afterThrowingHandlerException).when(afterThrowingHandler).afterThrowing(eq(methodInvocation),
                eq(methodInvocationError));

        assertThrows(methodInvocationError.getClass(), () -> uut.invoke(methodInvocation));
    }

    @Test
    void invoke_methodInvocationResultIsReturned_ifExceptionIsThrownDuringAfterExecutionProceed() throws Throwable {
        Object methodInvocationResult = new Object();
        doThrow(new AssertionError()).when(afterExecutionProceed).after(eq(methodInvocation),
                eq(methodInvocationResult));
        when(methodInvocation.proceed()).thenReturn(methodInvocationResult);

        Object actualMethodInvocationResult = uut.invoke(methodInvocation);

        assertEquals(methodInvocationResult, actualMethodInvocationResult);
    }
}