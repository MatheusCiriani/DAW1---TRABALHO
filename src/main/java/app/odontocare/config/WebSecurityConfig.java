package app.odontocare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Permite acesso irrestrito a recursos estáticos (CSS, JS, imagens, etc.)
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                // Permite acesso irrestrito à página de login, cadastro de cliente E A PÁGINA INICIAL
                .requestMatchers("/", "/login", "/clientes/novo", "/clientes/cadastrar").permitAll() // ALTERADO: Adicionado "/"
                // Requer autenticação para qualquer outra requisição
                .anyRequest().authenticated()
            )
            // Configura o formulário de login
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            // Configura o logout
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            )
            // Desabilita o CSRF (ATENÇÃO: para produção, CSRF DEVE ser habilitado)
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}