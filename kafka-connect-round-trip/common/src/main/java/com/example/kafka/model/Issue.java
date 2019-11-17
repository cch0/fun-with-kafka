
package com.example.kafka.model;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.kafka.connect.data.Struct;
import org.immutables.value.Value;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import static com.example.kafka.schema.GitHubSchemas.CREATED_AT_FIELD;
import static com.example.kafka.schema.GitHubSchemas.HTML_URL_FIELD;
import static com.example.kafka.schema.GitHubSchemas.NUMBER_FIELD;
import static com.example.kafka.schema.GitHubSchemas.PR_FIELD;
import static com.example.kafka.schema.GitHubSchemas.PR_HTML_URL_FIELD;
import static com.example.kafka.schema.GitHubSchemas.PR_URL_FIELD;
import static com.example.kafka.schema.GitHubSchemas.STATE_FIELD;
import static com.example.kafka.schema.GitHubSchemas.TITLE_FIELD;
import static com.example.kafka.schema.GitHubSchemas.UPDATED_AT_FIELD;
import static com.example.kafka.schema.GitHubSchemas.URL_FIELD;
import static com.example.kafka.schema.GitHubSchemas.USER_FIELD;
import static com.example.kafka.schema.GitHubSchemas.USER_HTML_URL_FIELD;
import static com.example.kafka.schema.GitHubSchemas.USER_ID_FIELD;
import static com.example.kafka.schema.GitHubSchemas.USER_LOGIN_FIELD;
import static com.example.kafka.schema.GitHubSchemas.USER_URL_FIELD;

@JsonSerialize(as = ImmutableIssue.class)
@JsonDeserialize(as = ImmutableIssue.class)
@Value.Immutable
public interface Issue {
    @Nullable
    Integer id();

    String url();

    @Nullable
    String repositoryUrl();

    @Nullable
    String labelsUrl();

    @Nullable
    String commentsUrl();

    @Nullable
    String eventsUrl();

    @Nullable
    @JsonProperty("html_url")
    String htmlUrl();

    Integer number();

    String state();

    String title();

    @Nullable
    String body();

    User user();

    List<Label> labels();

    @Nullable
    Assignee assignee();

    @Nullable
    Milestone milestone();

    @Nullable
    Boolean locked();

    @Nullable
    Integer comments();

    @Nullable
    @JsonProperty("pull_request")
    PullRequest pullRequest();

    @Nullable
    Object closedAt();

    @JsonProperty("created_at")
    Instant createdAt();

    @JsonProperty("updated_at")
    Instant updatedAt();

    List<Assignee> assignees();

    Map<String, Object> additionalProperties();

    static Issue fromJson(JSONObject jsonObject) {
        ImmutableIssue.Builder builder = ImmutableIssue.builder();
        builder.url(jsonObject.getString(URL_FIELD));
        builder.htmlUrl(jsonObject.getString(HTML_URL_FIELD));
        builder.title(jsonObject.getString(TITLE_FIELD));
        builder.createdAt(Instant.parse(jsonObject.getString(CREATED_AT_FIELD)));
        builder.updatedAt(Instant.parse(jsonObject.getString(UPDATED_AT_FIELD)));
        builder.number(jsonObject.getInt(NUMBER_FIELD));
        builder.state(jsonObject.getString(STATE_FIELD));

        // user is mandatory
        User user = User.fromJson(jsonObject.getJSONObject(USER_FIELD));
        builder.user(user);

        // pull request is an optional field
        if (jsonObject.has(PR_FIELD)) {
            PullRequest pullRequest = PullRequest.fromJson(jsonObject.getJSONObject(PR_FIELD));
            builder.pullRequest(pullRequest);
        }

        return builder.build();
    }

    static Issue fromStruct(Struct struct) {
        ImmutableIssue.Builder builder = ImmutableIssue.builder()
            .url(struct.getString(URL_FIELD))
            .title(struct.getString(TITLE_FIELD))
            .createdAt(((Date) struct.get(CREATED_AT_FIELD)).toInstant())
            .updatedAt(((Date) struct.get(UPDATED_AT_FIELD)).toInstant())
            .number(struct.getInt32(NUMBER_FIELD))
            .state(struct.getString(STATE_FIELD));


        if (hasField(struct, HTML_URL_FIELD)) {
            builder.htmlUrl(struct.getString(HTML_URL_FIELD));
        }

        // user
        Struct userStruct = struct.getStruct(USER_FIELD);

        ImmutableUser.Builder userBuilder = ImmutableUser.builder();
        userBuilder.url(userStruct.getString(USER_URL_FIELD));
        userBuilder.id(userStruct.getInt32(USER_ID_FIELD));
        userBuilder.login(userStruct.getString(USER_LOGIN_FIELD));

        if (hasField(userStruct, USER_HTML_URL_FIELD)) {
            userBuilder.htmlUrl(userStruct.getString(USER_HTML_URL_FIELD));
        }

        builder.user(userBuilder.build());

        // PR
        if (hasField(struct, PR_FIELD)) {
            Struct prStruct = struct.getStruct(PR_FIELD);

            ImmutablePullRequest.Builder prBuilder = ImmutablePullRequest.builder();

            if (hasField(prStruct, PR_HTML_URL_FIELD)) {
                prBuilder.htmlUrl(prStruct.getString(PR_HTML_URL_FIELD));
            }

            if (hasField(prStruct, PR_URL_FIELD)) {
                prBuilder.url(prStruct.getString(PR_URL_FIELD));
            }

            builder.pullRequest(prBuilder.build());
        }

        return builder.build();
    }

    static boolean hasField(Struct struct, String fieldName) {
        return struct != null && struct.schema() != null && struct.schema().field(fieldName) != null;
    }
}
