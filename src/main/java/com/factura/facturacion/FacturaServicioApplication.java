package com.factura.facturacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FacturaServicioApplication {

	public static void main(String[] args) {
		SpringApplication.run(FacturaServicioApplication.class, args);
	}

	@org.springframework.context.annotation.Bean
	public org.springframework.boot.CommandLineRunner initData(
			com.factura.facturacion.repositorios.seguridad.UsuarioRepositorio usuarioRepositorio,
			org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
		return args -> {
			if (usuarioRepositorio.findByUsername("vendedor").isEmpty()) {
				com.factura.facturacion.entidades.seguridad.Usuario vendedor = new com.factura.facturacion.entidades.seguridad.Usuario();
				vendedor.setUsername("vendedor");
				vendedor.setPassword(passwordEncoder.encode("vendedor"));
				vendedor.setRol("VENDEDOR");
				vendedor.setActivo(true);
				usuarioRepositorio.save(vendedor);
				System.out.println("Usuario vendedor creado con éxito");
			}

			if (usuarioRepositorio.findByUsername("contador").isEmpty()) {
				com.factura.facturacion.entidades.seguridad.Usuario contador = new com.factura.facturacion.entidades.seguridad.Usuario();
				contador.setUsername("contador");
				contador.setPassword(passwordEncoder.encode("contador"));
				contador.setRol("CONTADOR");
				contador.setActivo(true);
				usuarioRepositorio.save(contador);
				System.out.println("Usuario contador creado con éxito");
			}
		};
	}
}
