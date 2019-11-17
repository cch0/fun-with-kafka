package com.example.kafka.connector;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.json.JsonConverter;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;

import com.example.kafka.model.Issue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GithubSinkTask extends SinkTask {
    public ObjectMapper objectMapper = new ObjectMapper();
    public JsonConverter converter = new JsonConverter();
    Map<String, String> props;

    @Override
    public String version() {
        return "1.0";
    }

    @Override
    public void start(Map<String, String> props) {
        log.info("sink-task, {}", props);
        this.props = props;

        objectMapper.registerModule(new JavaTimeModule());

        // converter to be used in the case when schema is not provided in
        // the SinkRecord
        HashMap<String, String> m = new HashMap<>();
        m.put("schemas.enable", "false");
        m.put("converter.type", "value");

        converter.configure(m);
    }

    @Override
    public void put(Collection<SinkRecord> records) {
        log.info("record size:{}", records.size());

        records.forEach(record -> {
            try {
                log.debug("kye-schema:{}", record.keySchema());
                log.debug("kye-value:{}", record.key());
                log.debug("value-schema:{}", record.valueSchema());
                log.debug("value:{}", record.value());

                Issue issue;

                if (record.valueSchema() == null) {
                    // schema-less
                    final byte[] bytes = converter.fromConnectData("issues",
                        record.valueSchema(), record.value());

                    issue = objectMapper.readValue(bytes, Issue.class);
                } else {
                    // schema is available
                    Struct struct = (Struct) record.value();
                    issue = Issue.fromStruct(struct);
                }

                log.info("issue:{}", issue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void stop() {

    }
}
