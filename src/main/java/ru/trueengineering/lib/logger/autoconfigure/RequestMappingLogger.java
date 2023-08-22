package ru.trueengineering.lib.logger.autoconfigure;

import org.springframework.stereotype.Component;
import ru.trueengineering.lib.logger.autoconfigure.handler.RequestSpentTimeLogger;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@example
 *
 * @RequestMappingLogger //a1
 * class SomeClass {
 * @RequestMappingLogger //a2
 * public output method(Obj ... args)
 * }
 * <p>
 * if (a1.value == false) {
 *   ALL logging OFF;
 * } else {
 *   if (a2 == null) {
 *     a1.args -> on/off
 *     a1.output -> on/off
 *   } else {
 *     if (a2.value == false) {
 *       logging OFF;
 *     } else {
 *       a1.args -> on/off
 *       a1.output -> on/off
 *     }
 *   }
 * }
 * <p>
 * }
 * @see RequestSpentTimeLogger
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RequestMappingLogger {
    /**
     * off/on logging public method for target class
     */
    boolean value() default true;

    /**
     * off/on logging args in public method for target class
     */
    boolean args() default true;

    /**
     * off/on logging output in public method for target class
     */
    boolean output() default true;
}
