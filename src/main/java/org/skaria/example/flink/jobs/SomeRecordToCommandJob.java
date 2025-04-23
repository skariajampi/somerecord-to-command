package org.skaria.example.flink.jobs;

import com.skaria.avro.model.Identifier;
import com.skaria.avro.model.aggregate.domain.CommandRecord;
import com.skaria.json.model.SomeRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.skaria.example.flink.config.KafkaConfigData;
import org.skaria.example.flink.transform.SomeRecordToCommandProcessor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SomeRecordToCommandJob {

    private final KafkaConfigData kafkaConfigData;
    private final KafkaSource<Tuple2<String, SomeRecord>> kafkaSourceSomeRecord;
    private final KafkaSink<Tuple2<Identifier, CommandRecord>> kafkaSinkCommand;
    private final SomeRecordToCommandProcessor someRecordToCommandProcessor;

    public SomeRecordToCommandJob(KafkaConfigData kafkaConfigData,
                                  KafkaSource<Tuple2<String, SomeRecord>> kafkaSourceSomeRecord,
                                  KafkaSink<Tuple2<Identifier, CommandRecord>> kafkaSinkCommand,
                                  SomeRecordToCommandProcessor someRecordToCommandProcessor) {
        this.kafkaConfigData = kafkaConfigData;
        this.kafkaSourceSomeRecord = kafkaSourceSomeRecord;
        this.kafkaSinkCommand = kafkaSinkCommand;
        this.someRecordToCommandProcessor = someRecordToCommandProcessor;
    }

    public void buildJobTopology(StreamExecutionEnvironment streamExecutionEnvironment){
        log.info("Bootstrap server: {}", kafkaConfigData.getBootstrapServers());
        DataStreamSource<Tuple2<String, SomeRecord>> sourceSomeRecordCommandStream =
                streamExecutionEnvironment.fromSource(kafkaSourceSomeRecord, WatermarkStrategy.noWatermarks(),
                                                      "Read SomeRecord Json payload");

        SingleOutputStreamOperator<Tuple2<Identifier, CommandRecord>> processCommand =
                sourceSomeRecordCommandStream
                .name("SomeRecordToCommandProcessor")
                .setUidHash("88d6da2170eca0ff4b16887d1d3d069a")
                .setDescription("SomeRecordToCommandProcessor Processor")
                .process(someRecordToCommandProcessor);

        processCommand
                .sinkTo(kafkaSinkCommand)
                .name("P")
                .setDescription("");
    }
}
