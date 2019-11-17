
package com.example.kafka.model;

import java.util.Map;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImmutableLabel.class)
@JsonDeserialize(as = ImmutableLabel.class)
public interface Label {
    Integer id();

    String url();

    String name();

    String color();

    Boolean _default();

    Map<String, Object> additionalProperties();
}
