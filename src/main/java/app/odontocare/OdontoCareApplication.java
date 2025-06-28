package app.odontocare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan; // Importe esta!

@SpringBootApplication
@ComponentScan(basePackages = {"app.odontocare.controller", "app.odontocare.service", "app.odontocare.repository", "app.odontocare.config"}) // Adicione esta anotação!
public class OdontoCareApplication {

    public static void main(String[] args) {
        SpringApplication.run(OdontoCareApplication.class, args);
    }
}