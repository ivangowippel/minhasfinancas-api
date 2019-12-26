package com.ognavi.minhasfinancas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class MinhasfinancasApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		// Sobe a aplicacao
		SpringApplication.run(MinhasfinancasApplication.class, args);
	}

}
