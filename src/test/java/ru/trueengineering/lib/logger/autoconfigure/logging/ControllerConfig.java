package ru.trueengineering.lib.logger.autoconfigure.logging;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.trueengineering.lib.logger.autoconfigure.logging.controller_1.ControllerForTestingLoggingAspect;
import ru.trueengineering.lib.logger.autoconfigure.logging.controller_2.AnotherController;

/**
 * @author m.yastrebov
 */
@TestConfiguration
public class ControllerConfig {
    @Bean
    public ControllerForTestingLoggingAspect controller() {
        return new ControllerForTestingLoggingAspect();
    }

    @Bean
    public AnotherController anotherController() {
        return new AnotherController();
    }
}
