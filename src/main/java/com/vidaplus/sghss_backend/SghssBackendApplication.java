package com.vidaplus.sghss_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SghssBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SghssBackendApplication.class, args);
        System.out.println("\n" +
                "Seja bem-vindo ao Sistema de Gestão Hospitalar e de Serviços de Saúde VidaPlus! " +
                "Nossos servidores estão ativos e operantes... 🩺☕\n"
        );
	}

}
