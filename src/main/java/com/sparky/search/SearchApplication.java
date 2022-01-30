package com.sparky.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class SearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchApplication.class, args);
    }

}

interface RecordRepository extends ReactiveCrudRepository<Customer, Integer> {
    Flux<Customer> findByName(String name);
}

record Customer(@JsonProperty("id") @Id Integer id, @JsonProperty("name") String name) {

}

record Order(Integer id, Integer customerId, Integer quantity){}

@Controller
class CustomerGraphqlController {

    private final RecordRepository recordRepository;

    public CustomerGraphqlController(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @SchemaMapping(typeName = "Query", field = "customers")
    public Flux<Customer> getAllCustomer() {
        return recordRepository.findAll()
                .map(customer -> new Customer(customer.id(), "Humain : " + customer.name().toUpperCase()));
    }

    @SchemaMapping(typeName = "Customer")
    public Flux<Order> orders(Customer customer) {
        final List orders = new ArrayList<Order>();
        for (var orderId = 1; orderId < (Math.random() * 100); orderId++) {
            orders.add(new Order(orderId, customer.id(), orderId*100));
        }
        return Flux.fromIterable(orders);
    }

    @QueryMapping
    public Flux<Customer> customersByName(@Argument String name) {
        return this.recordRepository.findByName(name);
    }

    @MutationMapping
    public Mono<Customer> addCustomer(@Argument String name){
        return recordRepository.save(new Customer(null, name));
    }

}