package com.tienda.tiendaropa;

import com.tienda.tiendaropa.config.FirebaseAuthFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TiendaropaApplication {

	public static void main(String[] args) {
		SpringApplication.run(TiendaropaApplication.class, args);
	}

	@Bean
	public FilterRegistrationBean<FirebaseAuthFilter> jwtFilter() {
	    FilterRegistrationBean<FirebaseAuthFilter> reg = new FilterRegistrationBean<>();
	    reg.setFilter(new FirebaseAuthFilter());
	    reg.addUrlPatterns("/boletas/*", "/clientes/*", "/proveedores/*"); // Rutas protegidas
	    return reg;
	}
}
