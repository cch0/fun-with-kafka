package com.example.kafka.connector;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Map;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;

import com.example.kafka.Validators.BatchSizeValidator;
import com.example.kafka.Validators.TimestampValidator;

public class GitHubSourceConnectorConfig extends AbstractConfig {

    public static final String TOPIC_CONFIG = "topic";
    private static final String TOPIC_DOC = "Topic to write to";

    public static final String OWNER_CONFIG = "github.owner";
    private static final String OWNER_DOC = "Owner of the repository you'd like to follow";

    public static final String REPO_CONFIG = "github.repo";
    private static final String REPO_DOC = "Repository you'd like to follow";

    public static final String SINCE_CONFIG = "since.timestamp";
    private static final String SINCE_DOC =
        "Only issues updated at or after this time are returned.\n"
            + "This is a timestamp in ISO 8601 format: YYYY-MM-DDTHH:MM:SSZ.\n"
            + "Defaults to a year from first launch.";

    public static final String BATCH_SIZE_CONFIG = "batch.size";
    private static final String BATCH_SIZE_DOC = "Number of data points to retrieve at a time. Defaults to 100 (max value)";

    public GitHubSourceConnectorConfig(ConfigDef config, Map<String, String> parsedConfig) {
        super(config, parsedConfig);
    }

    public GitHubSourceConnectorConfig(Map<String, String> parsedConfig) {
        this(conf(), parsedConfig);
    }

    public static ConfigDef conf() {
        return new ConfigDef()
            .define(TOPIC_CONFIG, Type.STRING, Importance.HIGH, TOPIC_DOC)
            .define(OWNER_CONFIG, Type.STRING, Importance.HIGH, OWNER_DOC)
            .define(REPO_CONFIG, Type.STRING, Importance.HIGH, REPO_DOC)
            .define(BATCH_SIZE_CONFIG, Type.INT, 100, new BatchSizeValidator(), Importance.LOW, BATCH_SIZE_DOC)
            .define(SINCE_CONFIG, Type.STRING, ZonedDateTime.now().minusYears(1).toInstant().toString(),
                new TimestampValidator(), Importance.HIGH, SINCE_DOC)
            ;
    }

    public String getOwnerConfig() {
        return this.getString(OWNER_CONFIG);
    }

    public String getRepoConfig() {
        return this.getString(REPO_CONFIG);
    }

    public Integer getBatchSize() {
        return this.getInt(BATCH_SIZE_CONFIG);
    }

    public Instant getSince() {
        return Instant.parse(this.getString(SINCE_CONFIG));
    }

    public String getTopic() {
        return this.getString(TOPIC_CONFIG);
    }
}
