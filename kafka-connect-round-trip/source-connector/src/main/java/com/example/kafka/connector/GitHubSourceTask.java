package com.example.kafka.connector;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.kafka.model.Issue;
import com.example.kafka.model.PullRequest;
import com.example.kafka.model.User;
import com.example.kafka.utils.DateUtils;

import static com.example.kafka.schema.GitHubSchemas.CREATED_AT_FIELD;
import static com.example.kafka.schema.GitHubSchemas.KEY_SCHEMA;
import static com.example.kafka.schema.GitHubSchemas.NEXT_PAGE_FIELD;
import static com.example.kafka.schema.GitHubSchemas.NUMBER_FIELD;
import static com.example.kafka.schema.GitHubSchemas.OWNER_FIELD;
import static com.example.kafka.schema.GitHubSchemas.PR_FIELD;
import static com.example.kafka.schema.GitHubSchemas.PR_HTML_URL_FIELD;
import static com.example.kafka.schema.GitHubSchemas.PR_SCHEMA;
import static com.example.kafka.schema.GitHubSchemas.PR_URL_FIELD;
import static com.example.kafka.schema.GitHubSchemas.REPOSITORY_FIELD;
import static com.example.kafka.schema.GitHubSchemas.STATE_FIELD;
import static com.example.kafka.schema.GitHubSchemas.TITLE_FIELD;
import static com.example.kafka.schema.GitHubSchemas.UPDATED_AT_FIELD;
import static com.example.kafka.schema.GitHubSchemas.URL_FIELD;
import static com.example.kafka.schema.GitHubSchemas.USER_FIELD;
import static com.example.kafka.schema.GitHubSchemas.USER_ID_FIELD;
import static com.example.kafka.schema.GitHubSchemas.USER_LOGIN_FIELD;
import static com.example.kafka.schema.GitHubSchemas.USER_SCHEMA;
import static com.example.kafka.schema.GitHubSchemas.USER_URL_FIELD;
import static com.example.kafka.schema.GitHubSchemas.VALUE_SCHEMA;

public class GitHubSourceTask extends SourceTask {
    private static final Logger log = LoggerFactory.getLogger(GitHubSourceTask.class);
    public GitHubSourceConnectorConfig config;

    protected Instant nextQuerySince;
    protected Integer lastIssueNumber;
    protected Integer nextPageToVisit = 1;
    protected Instant lastUpdatedAt;

    GitHubAPIHttpClient gitHubHttpAPIClient;

    @Override
    public String version() {
        return "1.0";
    }

    @Override
    public void start(Map<String, String> map) {
        //Do things here that are required to start your task. This could be open a connection to a database, etc.
        config = new GitHubSourceConnectorConfig(map);
        initializeLastVariables();
        gitHubHttpAPIClient = new GitHubAPIHttpClient(config);
    }

    private void initializeLastVariables() {
        Map<String, Object> lastSourceOffset = null;
        lastSourceOffset = context.offsetStorageReader().offset(sourcePartition());
        if (lastSourceOffset == null) {
            nextQuerySince = config.getSince();
            lastIssueNumber = -1;
        } else {
            Object updatedAt = lastSourceOffset.get(UPDATED_AT_FIELD);
            Object issueNumber = lastSourceOffset.get(NUMBER_FIELD);
            Object nextPage = lastSourceOffset.get(NEXT_PAGE_FIELD);

            if (updatedAt != null && (updatedAt instanceof String)) {
                nextQuerySince = Instant.parse((String) updatedAt);
            }

            if (issueNumber != null && (issueNumber instanceof String)) {
                lastIssueNumber = Integer.valueOf((String) issueNumber);
            }

            if (nextPage != null && (nextPage instanceof String)) {
                nextPageToVisit = Integer.valueOf((String) nextPage);
            }
        }
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        gitHubHttpAPIClient.sleepIfNeed();

        // fetch data
        final ArrayList<SourceRecord> records = new ArrayList<>();
        JSONArray issues = gitHubHttpAPIClient.getNextIssues(nextPageToVisit, nextQuerySince);
        int i = 0;

        for (Object obj : issues) {
            Issue issue = Issue.fromJson((JSONObject) obj);
            SourceRecord sourceRecord = generateSourceRecord(issue);

            records.add(sourceRecord);
            i += 1;
            lastUpdatedAt = issue.updatedAt();
        }
        if (i > 0) log.info(String.format("Fetched %s record(s)", i));
        if (i == 100) {
            nextPageToVisit += 1;
        } else {
            if (lastUpdatedAt != null) {
                nextQuerySince = lastUpdatedAt.plusSeconds(1);
            }
            nextPageToVisit = 1;
            gitHubHttpAPIClient.sleep();
        }

        log.info("return source record size:{}", records.size());

        return records;
    }

    private SourceRecord generateSourceRecord(Issue issue) {
        return new SourceRecord(
            sourcePartition(),
            sourceOffset(issue.updatedAt()),
            config.getTopic(),
            null, // partition will be inferred by the framework
            KEY_SCHEMA,
            buildRecordKey(issue),
            VALUE_SCHEMA,
            buildRecordValue(issue),
            issue.updatedAt().toEpochMilli());
    }

    @Override
    public void stop() {
        // Do whatever is required to stop your task.
    }

    private Map<String, String> sourcePartition() {
        Map<String, String> map = new HashMap<>();
        map.put(OWNER_FIELD, config.getOwnerConfig());
        map.put(REPOSITORY_FIELD, config.getRepoConfig());
        return map;
    }

    private Map<String, String> sourceOffset(Instant updatedAt) {
        Map<String, String> map = new HashMap<>();
        map.put(UPDATED_AT_FIELD, DateUtils.MaxInstant(updatedAt, nextQuerySince).toString());
        map.put(NEXT_PAGE_FIELD, nextPageToVisit.toString());
        return map;
    }

    private Struct buildRecordKey(Issue issue) {
        // Key Schema
        Struct key = new Struct(KEY_SCHEMA)
            .put(OWNER_FIELD, config.getOwnerConfig())
            .put(REPOSITORY_FIELD, config.getRepoConfig())
            .put(NUMBER_FIELD, issue.number());

        return key;
    }

    public Struct buildRecordValue(Issue issue) {

        // Issue top level fields
        Struct valueStruct = new Struct(VALUE_SCHEMA)
            .put(URL_FIELD, issue.url())
            .put(TITLE_FIELD, issue.title())
            .put(CREATED_AT_FIELD, Date.from(issue.createdAt()))
            .put(UPDATED_AT_FIELD, Date.from(issue.updatedAt()))
            .put(NUMBER_FIELD, issue.number())
            .put(STATE_FIELD, issue.state());

        // User is mandatory
        User user = issue.user();
        Struct userStruct = new Struct(USER_SCHEMA)
            .put(USER_URL_FIELD, user.url())
            .put(USER_ID_FIELD, user.id())
            .put(USER_LOGIN_FIELD, user.login());
        valueStruct.put(USER_FIELD, userStruct);

        // Pull request is optional
        PullRequest pullRequest = issue.pullRequest();
        if (pullRequest != null) {
            Struct prStruct = new Struct(PR_SCHEMA)
                .put(PR_URL_FIELD, pullRequest.url())
                .put(PR_HTML_URL_FIELD, pullRequest.htmlUrl());
            valueStruct.put(PR_FIELD, prStruct);
        }

        return valueStruct;
    }
}