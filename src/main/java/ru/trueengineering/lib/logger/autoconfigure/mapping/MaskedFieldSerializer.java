package ru.trueengineering.lib.logger.autoconfigure.mapping;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ru.trueengineering.lib.logger.autoconfigure.SecuredField;

import java.io.IOException;

/**
 * @author m.yastrebov
 *
 * Выполняет операцию маскировки полей, помеченных аннотацией {@link SecuredField}
 */
class MaskedFieldSerializer extends JsonSerializer<Object> {

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeObject("*****");
    }
}
