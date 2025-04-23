package org.skaria.example.flink.transform;

import com.skaria.avro.model.Id;
import com.skaria.avro.model.Identifier;
import com.skaria.avro.model.SomeRecord;
import com.skaria.avro.model.aggregate.domain.CommandRecord;
import com.skaria.avro.model.aggregate.domain.CommandType;
import com.skaria.avro.model.aggregate.domain.ProcessSomeRecordCommandRecord;
import com.skaria.utils.DateUtils;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SomeRecordToCommandProcessor extends ProcessFunction<Tuple2<String, com.skaria.json.model.SomeRecord>,
        Tuple2<Identifier, CommandRecord>> {

    @Override
    public void processElement(Tuple2<String, com.skaria.json.model.SomeRecord> stringSomeRecordTuple2,
                               ProcessFunction<Tuple2<String, com.skaria.json.model.SomeRecord>, Tuple2<Identifier, CommandRecord>>.Context context,
                               Collector<Tuple2<Identifier, CommandRecord>> collector) throws Exception {

        String identifier = stringSomeRecordTuple2.f1.getIdentifier();
        CommandRecord commandRecord = CommandRecord.newBuilder()
                .setCommandType(CommandType.PROCESS_SOME_RECORD_COMMAND)
                .setCreationTimestamp(DateUtils.nowStandardUtc())
                .setIdentifier(Identifier.newBuilder().setIdentifier(identifier).build())
                .setCommand(getCommand(stringSomeRecordTuple2.f1))
                .build();
        Tuple2<Identifier, CommandRecord> commandRecordTuple2 = new Tuple2<>(Identifier.newBuilder().setIdentifier(identifier).build(),
                                                                             commandRecord);
        collector.collect(commandRecordTuple2);
    }

    private ProcessSomeRecordCommandRecord getCommand(com.skaria.json.model.SomeRecord someRecord) {
        SomeRecord someRecordAvro = SomeRecord.newBuilder()
                .setId(Id.newBuilder().setId(someRecord.getId()).build())
                .setIdentifier(Identifier.newBuilder().setIdentifier(someRecord.getIdentifier()).build())
                .setTIMESTAMP(someRecord.getTimestamp())
                .setMEASURE(someRecord.getMeasure())
                .build();

        return ProcessSomeRecordCommandRecord.newBuilder()
                .setCreationTimestamp(DateUtils.nowStandardUtc())
                .setEventId(UUID.randomUUID())
                .setSomeRecord(someRecordAvro)
                .build();
    }
}
