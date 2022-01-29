package com.sparky.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class SearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchApplication.class, args);
    }

}

interface RecordRepository extends ReactiveCrudRepository<Customer, Integer> {

}

record Customer(@JsonProperty("id") @Id Integer id, @JsonProperty("name") String name) {

}

@Controller
class CustomerGraphqlController {

    private final RecordRepository recordRepository;

    public CustomerGraphqlController(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @SchemaMapping(typeName = "Query", field = "customers")
    public Flux<Customer> getAllCustomer() {
        return recordRepository.findAll();
    }
}