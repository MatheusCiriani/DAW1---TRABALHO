package app.odontocare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan; // Importe esta!
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@ComponentScan(basePackages = {"app.odontocare.controller", "app.odontocare.service", "app.odontocare.repository", "app.odontocare.config"}) // Adicione esta anotação!
public class OdontoCareApplication {

    public static void main(String[] args) {
        SpringApplication.run(OdontoCareApplication.class, args);
        // BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        // String rawPassword = "admin"; // A senha que você quer criptografar
        // String encodedPassword = encoder.encode(rawPassword);
        // System.out.println("Senha '" + rawPassword + "' criptografada: " + encodedPassword);
    
    }
}