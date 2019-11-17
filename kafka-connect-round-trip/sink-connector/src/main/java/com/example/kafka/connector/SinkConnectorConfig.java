package com.example.kafka.connector;

import java.util.Map;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import com.example.kafka.Validators.BatchSizeValidator;

public class SinkConnectorConfig extends AbstractConfig {
    public static final String BATCH_SIZE_CONFIG = "batch.size";
    private static final String BATCH_SIZE_DOC = "Number of data points to retrieve at a time. Defaults to 100 (max value)";


    public SinkConnectorConfig(ConfigDef config, Map<String, String> parsedConfig) {
        super(config, parsedConfig);
    }

    public SinkConnectorConfig(Map<String, String> parsedConfig) {
        this(conf(), parsedConfig);
    }

    public static ConfigDef conf() {
        return new ConfigDef()
            .define(BATCH_SIZE_CONFIG, ConfigDef.Type.INT, 100, new BatchSizeValidator(),
                ConfigDef.Importance.LOW, BATCH_SIZE_DOC);
    }
}
