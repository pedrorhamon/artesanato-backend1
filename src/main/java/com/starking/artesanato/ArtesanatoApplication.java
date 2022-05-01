package com.starking.artesanato;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class ArtesanatoApplication implements WebMvcConfigurer {
	
	public static void main(String[] args) {
		SpringApplication.run(ArtesanatoApplication.class, args);
	}

}
