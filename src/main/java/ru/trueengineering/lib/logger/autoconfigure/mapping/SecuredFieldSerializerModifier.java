package ru.trueengineering.lib.logger.autoconfigure.mapping;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import ru.trueengineering.lib.logger.autoconfigure.SecuredField;
import java.util.List;

/**
 * @author m.yastrebov
 *
 * Добавляет {@link MaskedFieldSerializer} для полей, помеченных аннотацией {@link SecuredField}
 */
public class SecuredFieldSerializerModifier extends BeanSerializerModifier {

    private final MaskedFieldSerializer maskedFieldSerializer = new MaskedFieldSerializer();

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc,
                                                     List<BeanPropertyWriter> beanProperties) {
        beanProperties.stream()
                .filter(it -> it.getAnnotation(SecuredField.class) != null)
                .forEach(this::assignSerializers);
        return beanProperties;
    }

    private void assignSerializers(BeanPropertyWriter beanPropertyWriter) {
        beanPropertyWriter.assignSerializer(maskedFieldSerializer);
        beanPropertyWriter.assignNullSerializer(maskedFieldSerializer);
    }
}
