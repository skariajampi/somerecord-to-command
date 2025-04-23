package org.skaria.example.flink.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skaria.json.model.SomeRecord;
import org.apache.avro.specific.SpecificRecord;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.TupleTypeInfo;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.formats.avro.registry.confluent.ConfluentRegistryAvroDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CustomJsonDeserializationSchema implements KafkaRecordDeserializationSchema<Tuple2<String, SomeRecord>> {

    private final ObjectMapper objectMapper;

    public CustomJsonDeserializationSchema(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void deserialize(ConsumerRecord<byte[], byte[]> consumerRecord, Collector<Tuple2<String, SomeRecord>> collector) throws IOException {
        String key = new String(consumerRecord.key(), StandardCharsets.UTF_8);
        SomeRecord someRecord = objectMapper.readValue(consumerRecord.value(), SomeRecord.class);
        collector.collect(Tuple2.of(key, someRecord));
    }

    @Override
    public TypeInformation<Tuple2<String, SomeRecord>> getProducedType() {
        TypeInformation<Tuple2<String, SomeRecord>> type =
                TypeInformation.of(new TypeHint<Tuple2<String, SomeRecord>>() {
                });

        return type;
    }
}

