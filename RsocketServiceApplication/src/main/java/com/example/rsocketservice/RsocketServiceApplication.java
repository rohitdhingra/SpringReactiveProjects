package com.example.rsocketservice;

import java.time.Duration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class RsocketServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RsocketServiceApplication.class, args);
	}

}


@Controller
class GreetingsController
{
	@MessageMapping("intervals")
	Flux<GreetingResponse> interval(GreetingRequest request)
	{
		return Flux
				.interval(Duration.ofMillis(1000))
				.map(interval -> new GreetingResponse("Hello (#"+interval+")"+request.getName()+"!"));
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