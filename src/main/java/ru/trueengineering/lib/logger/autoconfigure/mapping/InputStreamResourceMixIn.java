package ru.trueengineering.lib.logger.autoconfigure.mapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = {"inputStream", "file", "uri", "url"})
public abstract class InputStreamResourceMixIn {
}
