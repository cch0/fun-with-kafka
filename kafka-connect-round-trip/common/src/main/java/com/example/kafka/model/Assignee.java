
package com.example.kafka.model;

import java.util.Map;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImmutableAssignee.class)
@JsonDeserialize(as = ImmutableAssignee.class)
public interface Assignee {
    String login();

    Integer id();

    String avatarUrl();

    String gravatarId();

    String url();

    @JsonProperty("html_url")
    String htmlUrl();

    String followersUrl();

    String followingUrl();

    String gistsUrl();

    String starredUrl();

    String subscriptionsUrl();

    String organizationsUrl();

    String reposUrl();

    String eventsUrl();

    String receivedEventsUrl();

    String type();

    Boolean siteAdmin();

    Map<String, Object> additionalProperties();
}
