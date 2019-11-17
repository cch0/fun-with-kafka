
package com.example.kafka.model;

import java.util.Map;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import static com.example.kafka.schema.GitHubSchemas.PR_HTML_URL_FIELD;
import static com.example.kafka.schema.GitHubSchemas.PR_URL_FIELD;

@Value.Immutable
@JsonSerialize(as = ImmutablePullRequest.class)
@JsonDeserialize(as = ImmutablePullRequest.class)
public interface PullRequest {
    @Nullable
    String url();

    @Nullable
    @JsonProperty("html_url")
    String htmlUrl();

    @Nullable
    String diffUrl();

    @Nullable
    String patchUrl();

    Map<String, Object> additionalProperties();

    static PullRequest fromJson(JSONObject jsonObject) {
        final ImmutablePullRequest.Builder builder = ImmutablePullRequest.builder();
        builder.url(jsonObject.getString(PR_URL_FIELD));
        builder.htmlUrl(jsonObject.getString(PR_HTML_URL_FIELD));
        return builder.build();
    }
}
