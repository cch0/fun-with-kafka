package com.example.kafka.connector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GithubSinkConnector extends SinkConnector {
    private SinkConnectorConfig config;

    @Override
    public String version() {
        return "1.0";
    }

    @Override
    public void start(Map<String, String> props) {
        config = new SinkConnectorConfig(props);
    }

    @Override
    public Class<? extends Task> taskClass() {
        return GithubSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        ArrayList<Map<String, String>> configs = new ArrayList<>(1);
        configs.add(config.originalsStrings());
        return configs;
    }

    @Override
    public void stop() {

    }

    @Override
    public ConfigDef config() {
        ConfigDef configDef = SinkConnectorConfig.conf();
        return configDef;
    }
}
