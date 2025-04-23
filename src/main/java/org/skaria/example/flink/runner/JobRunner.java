package org.skaria.example.flink.runner;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.skaria.example.flink.jobs.SomeRecordToCommandJob;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@Slf4j
public class JobRunner implements ApplicationRunner {

    private final SomeRecordToCommandJob someRecordToCommandJob;
    private final StreamExecutionEnvironment streamExecutionEnvironment;

    public JobRunner(SomeRecordToCommandJob someRecordToCommandJob, StreamExecutionEnvironment streamExecutionEnvironment) {
        this.someRecordToCommandJob = someRecordToCommandJob;
        this.streamExecutionEnvironment = streamExecutionEnvironment;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        someRecordToCommandJob.buildJobTopology(streamExecutionEnvironment);
        log.info("IdentifierStreamingJob topology created...");
        streamExecutionEnvironment.execute("Identifier Streaming Job");
    }
}
