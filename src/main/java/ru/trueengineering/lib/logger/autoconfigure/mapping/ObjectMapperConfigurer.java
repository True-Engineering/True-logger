package ru.trueengineering.lib.logger.autoconfigure.mapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.core.io.InputStreamResource;
import ru.trueengineering.lib.logger.autoconfigure.SecuredField;
import java.util.Optional;

/**
 * Конфигуратор для ObjectMapper'a см. {@link SecuredField}
 */
public class ObjectMapperConfigurer {
    public static ObjectMapper prepareObjectMapper(Optional<ObjectMapper> objectMapperOptional) {
        return objectMapperOptional.map(ObjectMapper::copy)
                .orElse(new ObjectMapper())
                .registerModule(new SimpleModule() {
                    @Override
                    public void setupModule(SetupContext context) {
                        super.setupModule(context);
                        context.addBeanSerializerModifier(new SecuredFieldSerializerModifier());
                    }
                })
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .addMixIn(InputStreamResource.class, InputStreamResourceMixIn.class);
    }
}
