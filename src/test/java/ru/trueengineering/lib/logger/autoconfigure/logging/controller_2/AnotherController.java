package ru.trueengineering.lib.logger.autoconfigure.logging.controller_2;

import ru.trueengineering.lib.logger.autoconfigure.logging.model_1.CompositeClass;
import ru.trueengineering.lib.logger.autoconfigure.logging.model_2.Dto;

/**
 * @author m.yastrebov
 */
public class AnotherController {

    public CompositeClass publicMethodWitArgsAndResponse(int intArg, float floatArg, String name, CompositeClass compositeArg, Dto dto) {
        return new CompositeClass("response", 123);
    }
}
