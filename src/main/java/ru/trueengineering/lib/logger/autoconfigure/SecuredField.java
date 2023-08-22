package ru.trueengineering.lib.logger.autoconfigure;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Данной аннотацией помечаются поля в контрактных DTO, которые необходимо замаскировать при логировании
 * см. пример использования в {@link SecuredFieldTest}
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SecuredField {
}
