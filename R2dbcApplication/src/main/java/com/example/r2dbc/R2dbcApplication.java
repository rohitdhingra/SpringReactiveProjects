package com.example.r2dbc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.connectionfactory.R2dbcTransactionManager;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.util.Assert;

import io.r2dbc.spi.ConnectionFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class R2dbcApplication {

	public static void main(String[] args) {
		SpringApplication.run(R2dbcApplication.class, args);
	}

}
interface CustomerRepository extends ReactiveCrudRepository<Customer,Long>
{
}
@Data
@AllArgsConstructor
@NoArgsConstructor
class Customer
{
	private Long id;
	private String email;
}

@Service
@RequiredArgsConstructor
class CustomerService
{
	private final TransactionalOperator transactionalOperator;
	
	@Transactional
	Flux<Customer> saveAllWithTransactionalAnnotation(String... emails)
	{
		return this.validCustomersFromEmails(emails);
	}

	
	Flux<Customer> saveAllWithTransactionalOperator(String... emails)
	{
		return this.transactionalOperator.transactional(this.validCustomersFromEmails(emails));
	}
	
	private Flux<Customer> validCustomersFromEmails(String[] emails) {
		return Flux.fromArray(emails)
				.map(email -> new Customer(null,email))
				.doOnNext(c -> Assert.isTrue(c.getEmail().contains("@"),"the email must contain a @"))
				;
	}
}

@Configuration
@EnableTransactionManagement
class TransactionConfiguration
{
	@Bean
	ReactiveTransactionManager reactiveTransactionManager(ConnectionFactory cf)
	{
		return new R2dbcTransactionManager(cf);
	}
	@Bean
	TransactionalOperator transactionalOperator(ReactiveTransactionManager txm)
	{
		return TransactionalOperator.create(txm);
	}
}