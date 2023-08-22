package ru.trueengineering.lib.logger.autoconfigure;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables masking for logging
 *
 * {@example
 *  *
 *  * @PostMapping
 *  * public @SecuredDataObject Response foo(@SecuredDataObject @RequestBody Request request) {
 *  *    // request and response are masked
 *  * }
 *  }
 */
@Target({ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SecuredDataObject {
}
