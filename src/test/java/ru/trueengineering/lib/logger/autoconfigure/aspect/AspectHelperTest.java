package ru.trueengineering.lib.logger.autoconfigure.aspect;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import ru.trueengineering.lib.logger.autoconfigure.SecuredDataObject;

class AspectHelperTest {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private ListAppender<ILoggingEvent> logWatcher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        logWatcher = new ListAppender<>();
        logWatcher.start();
        ((Logger) LoggerFactory.getLogger(AspectLoggerUtils.class)).addAppender(logWatcher);
    }

    @AfterEach
    void after() {
        ((Logger) LoggerFactory.getLogger(AspectLoggerUtils.class)).detachAndStopAllAppenders();
    }

    @Test
    void testSerializeObjectMasked() {
        MockRequest mockRequest = new MockRequest();
        mockRequest.setValue("value");
        AnnotatedElement annotatedElement = Mockito.mock(AnnotatedElement.class);
        Mockito.when(annotatedElement.isAnnotationPresent(SecuredDataObject.class)).thenReturn(true);
        String maskedSerializedObject = "";
        try {
            maskedSerializedObject = AspectHelper.getSerializedObjectString(mockRequest,
                annotatedElement,
                OBJECT_MAPPER);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Assertions.assertTrue(maskedSerializedObject.endsWith("****"));
    }

    @Test
    void testSerializeObjectNotMasked() {
        MockRequest mockRequest = new MockRequest();
        mockRequest.setValue("value");
        AnnotatedElement annotatedElement = Mockito.mock(AnnotatedElement.class);
        Mockito.when(annotatedElement.isAnnotationPresent(SecuredDataObject.class)).thenReturn(false);
        String maskedSerializedObject = "";
        try {
            maskedSerializedObject = AspectHelper.getSerializedObjectString(mockRequest,
                annotatedElement,
                OBJECT_MAPPER);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Assertions.assertFalse(maskedSerializedObject.endsWith("****"));
    }

    @ParameterizedTest
    @MethodSource(value = "provideGetJsonResponseArgs")
    void getJsonResponse(Object output, String representation) {
        Object jsonResponse = AspectHelper.getJsonResponse(output,
            output != null ? output.getClass() : null,
            OBJECT_MAPPER,
            Collections.singletonList(AspectHelperTest.class.getPackage().getName()));
        assertThat(jsonResponse).isEqualTo(representation);
        assertThat(logWatcher.list).isEmpty();
    }

    private static Stream<Arguments> provideGetJsonResponseArgs() throws JsonProcessingException {
        MockMultipartFile multipartFile = new MockMultipartFile("test.jpg", new byte[10]);
        ByteArrayResource resource = new ByteArrayResource(new byte[10]);
        ResponseEntity<ByteArrayResource> responseEntity = new ResponseEntity<>(resource, HttpStatus.OK);
        MockRequest mockRequest = new MockRequest("value");

        return Stream.of(
            Arguments.of(null, ""),
            Arguments.of(multipartFile, multipartFile.toString()),
            Arguments.of(resource, resource.toString()),
            Arguments.of(responseEntity, responseEntity.toString()),
            Arguments.of(mockRequest, OBJECT_MAPPER.writeValueAsString(mockRequest))
        );
    }

    static class MockRequest {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public MockRequest() {
        }

        public MockRequest(String value) {
            this.value = value;
        }
    }

}
