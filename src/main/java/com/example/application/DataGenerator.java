package com.example.application;

import com.vaadin.exampledata.DataType;
import com.vaadin.exampledata.ExampleDataGenerator;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringComponent
public class DataGenerator {
    @Bean
    public CommandLineRunner loadData(@Autowired PersonRepository personRepo) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());

            logger.info("Generating demo data");

            logger.info("... generating 100 Sample Person entities...");
            ExampleDataGenerator<Person> samplePersonGenerator = new ExampleDataGenerator<>(
                    Person.class, LocalDateTime.now());
            samplePersonGenerator.setData(Person::setFirstName, DataType.FIRST_NAME);
            samplePersonGenerator.setData(Person::setLastName, DataType.LAST_NAME);
            samplePersonGenerator.setData(Person::setEmail, DataType.EMAIL);
            personRepo.saveAll(samplePersonGenerator.create(100, 123));

            logger.info("Generated demo data");
        };
    }
}
