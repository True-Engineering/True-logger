package ru.trueengineering.lib.logger.autoconfigure.logging.controller_1;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.trueengineering.lib.logger.autoconfigure.RequestMappingLogger;
import ru.trueengineering.lib.logger.autoconfigure.logging.model_1.CompositeClass;
import ru.trueengineering.lib.logger.autoconfigure.logging.model_2.Dto;

/**
 * @author m.yastrebov
 */
public class ControllerForTestingLoggingAspect {

    String packagePrivateMethod() {
        return "result";
    }

    public String publicMethodWithoutArgs() {
        return "result";
    }

    public void publicVoidMethodWithoutArgs() {

    }

    public CompositeClass publicMethodWitArgsAndResponse(int intArg, float floatArg, String name, CompositeClass compositeArg, Dto dto) {
        return new CompositeClass("response", 123);
    }

    @RequestMappingLogger(false)
    public CompositeClass methodWithoutLogging(int intArg, float floatArg, String name, CompositeClass compositeArg) {
        return new CompositeClass("response", 123);
    }

    public ResponseEntity<InputStreamResource> methodWithInputStream(){
        InputStream targetStream = new ByteArrayInputStream("some bytes".getBytes());
        final ResponseEntity<InputStreamResource> entity = new ResponseEntity<>(new InputStreamResource(targetStream), null, HttpStatus.OK);
        return entity;
    }
}
