package ru.trueengineering.lib.logger.autoconfigure.logging;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import ru.trueengineering.lib.logger.autoconfigure.LoggingConfiguration;
import ru.trueengineering.lib.logger.autoconfigure.logging.controller_1.ControllerForTestingLoggingAspect;
import ru.trueengineering.lib.logger.autoconfigure.logging.controller_2.AnotherController;
import ru.trueengineering.lib.logger.autoconfigure.logging.model_1.CompositeClass;
import ru.trueengineering.lib.logger.autoconfigure.logging.model_2.Dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author m.yastrebov
 */
@SpringBootTest(classes = {ControllerConfig.class, LoggingConfiguration.class},
        properties = {
        "trueengineering.logging.enabled=true",
        "trueengineering.logging.packages=ru.trueengineering.lib.logger.autoconfigure.logging.controller_1," +
                "ru.trueengineering.*.controller_2",
        "trueengineering.logging.base.package.for.models=ru.trueengineering.lib.logger.autoconfigure.logging.model_1," +
                "ru.trueengineering.lib.logger.autoconfigure.logging.model_2"
})
class TestLoggingAspect {

    @Autowired
    private ControllerForTestingLoggingAspect testController;
    @Autowired
    private AnotherController anotherController;

    @Test
    void shouldProcessMethodWithoutArgs() throws Exception {
        final String response = testController.publicMethodWithoutArgs();
        assertEquals("result", response);
    }

    @Test
    void shouldProcessVoidMethodWithoutArgs() throws Exception {
        testController.publicVoidMethodWithoutArgs();
    }

    @Test
    void shouldProcessMethodWitArgsAndResponse() throws Exception {
        final CompositeClass response =
                testController.publicMethodWitArgsAndResponse(
                1, 1.0f, "arg", new CompositeClass("qwe", 33), new Dto("dto_id")
        );

        assertNotNull(response);
    }

    @Test
    void shouldNotLoggingBecauseUsedAnnotation() throws Exception {
        final CompositeClass response = testController.methodWithoutLogging(
                1, 1.0f, "arg", new CompositeClass("qwe", 33)
        );

        assertNotNull(response);
    }

    @Test
    void shouldLogForControllerFromAnotherPackage() throws Exception {
        final CompositeClass response = anotherController.publicMethodWitArgsAndResponse(
                1, 1.0f, "arg", new CompositeClass("qwe", 33), new Dto("dto_id")
        );

        assertNotNull(response);
    }

    @Test
    void shouldLogForMethodWithInputStream() {
        final ResponseEntity<InputStreamResource> response = testController.methodWithInputStream();
        assertNotNull(response);
    }
}