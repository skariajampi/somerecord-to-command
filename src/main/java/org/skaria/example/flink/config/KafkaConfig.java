package org.skaria.example.flink.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skaria.avro.model.Identifier;
import com.skaria.avro.model.aggregate.domain.CommandRecord;
import com.skaria.json.model.SomeRecord;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.sink.KafkaSinkBuilder;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.KafkaSourceBuilder;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.skaria.example.flink.serde.CustomJsonDeserializationSchema;
import org.skaria.example.flink.serde.FlinkKafkaAvroSerialization;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class KafkaConfig {
    private static final String DEV_PROFILE = "dev";
    private static final String PROD_PROFILE = "prod";
    private final KafkaConfigData kafkaConfigData;
    private final Environment env;

    public KafkaConfig(KafkaConfigData kafkaConfigData, Environment env) {
        this.kafkaConfigData = kafkaConfigData;
        this.env = env;
    }

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

    @Bean
    public KafkaSource<Tuple2<String, SomeRecord>> kafkaSourceSomeRecordJson(){
        KafkaSourceBuilder<Tuple2<String, SomeRecord>> kafkaSourceBuilder =
                defaultKafkaSourceBuilder(kafkaConfigData.getBootstrapServers(),
                                          kafkaConfigData.getSourceSomeRecordTopicName(),
                                          kafkaConfigData.getSourceSomeRecordGroupName(),
                                          kafkaConfigData.getSchemaRegistryUrl());
        //for msf set security properties
        //kafkaSourceBuilder.setProperties()
        return kafkaSourceBuilder.build();

    }

    @Bean
    public KafkaSink<Tuple2<Identifier, CommandRecord>> kafkaSinkSomeRecordUpdatedEventRecord(){
        KafkaSinkBuilder<Tuple2<Identifier, CommandRecord>> kafkaSinkBuilder =
                defaultKafkaSinkBuilder(kafkaConfigData.getBootstrapServers(),
                                        kafkaConfigData.getSinkProcessSomeRecordCommandTopicName(),
                                        kafkaConfigData.getSchemaRegistryUrl());
        //for msf set security properties
        //kafkaSinkBuilder.setProperties()
        return kafkaSinkBuilder.build();

    }

    private KafkaSourceBuilder<Tuple2<String, SomeRecord>> defaultKafkaSourceBuilder(String bootStrapServer,
                                                                                            String sourceTopicName,
                                                                                            String sourceGroupName,
                                                                                            String schemaRegistryUrl){
        return KafkaSource.<Tuple2<String, SomeRecord>>builder()
                .setBootstrapServers(bootStrapServer)
                .setTopics(sourceTopicName)
                .setGroupId(sourceGroupName)
                .setStartingOffsets(OffsetsInitializer.latest())
                .setProperty("partition.discovery.interval.ms", "-1")
                .setProperty("commit.offsets.on.checkpoint", "true")
                .setDeserializer(new CustomJsonDeserializationSchema(objectMapper()));
    }

    private KafkaSinkBuilder<Tuple2<Identifier, CommandRecord>> defaultKafkaSinkBuilder(String bootStrapServer,
                                                                                            String sourceTopicName,
                                                                                            String schemaRegistryUrl){
        return KafkaSink.<Tuple2<Identifier, CommandRecord>>builder()
                .setBootstrapServers(bootStrapServer)
                .setRecordSerializer(new FlinkKafkaAvroSerialization<>(Identifier.class, CommandRecord.class, schemaRegistryUrl,
                                                                       sourceTopicName));
    }

}
