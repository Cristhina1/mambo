package com.sistemaFacturacion.Mambo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableAsync
public class MamboApplication {
	//USUARIO: 99999999
	//CONTRA: admin123
	public static void main(String[] args) {
		SpringApplication.run(MamboApplication.class, args);
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    System.out.println(encoder.encode("admin12345"));
	}

}
