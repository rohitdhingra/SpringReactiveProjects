package com.example.rsocketclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

@SpringBootApplication
@RequiredArgsConstructor
public class RsocketClientApplication {

	@SneakyThrows
	public static void main(String[] args) {
		SpringApplication.run(RsocketClientApplication.class, args);
		System.in.read();
	}
	
	@Bean
	RSocketRequester rSocketRequester(RSocketRequester.Builder builder)
	{
		return builder.connectTcp("localhost", 7777).block();
	}
}

@Log4j2
@Component
@RequiredArgsConstructor
class Client 
{
	private final RSocketRequester rSocketRequester;
	
	@EventListener(ApplicationReadyEvent.class)
	public void ready()
	{
		this.rSocketRequester
		.route("intervals")
		.data(new GreetingRequest("World"))
		.retrieveFlux(GreetingResponse.class)
		.subscribe(im -> log.info("Consuming"+im.getMessage()+"."));
	}
}
@Data
@AllArgsConstructor
@NoArgsConstructor
class GreetingRequest
{
	private String name;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class GreetingResponse
{
	private String message;
}