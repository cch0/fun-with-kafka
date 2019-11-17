
package com.example.kafka.model;

import java.util.Map;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImmutableMilestone.class)
@JsonDeserialize(as = ImmutableMilestone.class)
public interface Milestone {
    String url();

    String htmlUrl();

    String labelsUrl();

    Integer id();

    Integer number();

    String state();

    String title();

    String description();

    Creator creator();

    Integer openIssues();

    Integer closedIssues();

    String createdAt();

    String updatedAt();

    String closedAt();

    String dueOn();

    Map<String, Object> additionalProperties();
}
