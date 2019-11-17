
package com.example.kafka.model;

import java.util.Map;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import static com.example.kafka.schema.GitHubSchemas.USER_HTML_URL_FIELD;
import static com.example.kafka.schema.GitHubSchemas.USER_ID_FIELD;
import static com.example.kafka.schema.GitHubSchemas.USER_LOGIN_FIELD;
import static com.example.kafka.schema.GitHubSchemas.USER_URL_FIELD;

@Value.Immutable
@JsonSerialize(as = ImmutableUser.class)
@JsonDeserialize(as = ImmutableUser.class)
public interface User {
    String login();

    Integer id();

    @Nullable
    String avatarUrl();

    @Nullable
    String gravatarId();

    String url();

    @Nullable
    @JsonProperty("html_url")
    String htmlUrl();

    @Nullable
    String followersUrl();

    @Nullable
    String followingUrl();

    @Nullable
    String gistsUrl();

    @Nullable
    String starredUrl();

    @Nullable
    String subscriptionsUrl();

    @Nullable
    String organizationsUrl();

    @Nullable
    String reposUrl();

    @Nullable
    String eventsUrl();

    @Nullable
    String receivedEventsUrl();

    @Nullable
    String type();

    @Nullable
    Boolean siteAdmin();

    Map<String, Object> additionalProperties();

    static User fromJson(JSONObject jsonObject) {
        final ImmutableUser.Builder builder = ImmutableUser.builder();
        builder.url(jsonObject.getString(USER_URL_FIELD));
        builder.htmlUrl(jsonObject.getString(USER_HTML_URL_FIELD));
        builder.id(jsonObject.getInt(USER_ID_FIELD));
        builder.login(jsonObject.getString(USER_LOGIN_FIELD));
        return builder.build();
    }
}
