package org.skaria.example.flink.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-config")
public class KafkaConfigData {
    private String sourceSomeRecordTopicName;
    private String sourceSomeRecordGroupName;
    private String sinkProcessSomeRecordCommandTopicName;
    private String bootstrapServers;
    private String schemaRegistryUrl;
}
