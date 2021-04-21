package com.plawande.ipldashboard.data;

import com.plawande.ipldashboard.model.Match;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@EnableBatchProcessing
@Configuration
public class BatchConfig {

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory,
                   Step step,
                   JobCompletionNotificationListener listener) {
        return jobBuilderFactory.get("ipl-load")
                .incrementer(new RunIdIncrementer())
                .start(step)
                .listener(listener)
                .build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory,
                     ItemReader<MatchInput> itemReader,
                     ItemProcessor<MatchInput, Match> itemProcessor,
                     ItemWriter<Match> itemWriter) {
        return stepBuilderFactory.get("ipl-file-load")
                .<MatchInput, Match>chunk(100)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();
    }

    @Bean
    public FlatFileItemReader<MatchInput> itemReader(@Value("${input}") Resource resource) {
        FlatFileItemReader<MatchInput> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(resource);
        flatFileItemReader.setName("ipl-csv-reader");
        flatFileItemReader.setLinesToSkip(1);
        flatFileItemReader.setLineMapper(lineMapper());
        return flatFileItemReader;
    }

    @Bean
    public LineMapper<MatchInput> lineMapper() {
        DefaultLineMapper<MatchInput> defaultLineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(new String[] { "id", "city", "date", "player_of_match", "venue",
                "neutral_venue", "team1", "team2", "toss_winner", "toss_decision", "winner", "result", "result_margin",
                "eliminator", "method", "umpire1", "umpire2" });
        defaultLineMapper.setLineTokenizer(lineTokenizer);

        BeanWrapperFieldSetMapper<MatchInput> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(MatchInput.class);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);

        return defaultLineMapper;
    }
}
