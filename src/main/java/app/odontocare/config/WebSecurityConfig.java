package app.odontocare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(requests -> requests
                // 1. Acesso Público
                .requestMatchers("/login", "/css/**", "/js/**", "/images/**", "/api/usuarios/verificar-identificador").permitAll()

                // 2. Regras para ADMIN (exclusivas)
                .requestMatchers("/dentistas/**", "/agendas/**").hasRole("ADMIN")
                
                // 3. Regras para DENTISTA (e ADMIN)
                .requestMatchers(HttpMethod.GET, "/clientes", "/clientes/novo").hasAnyRole("ADMIN", "DENTISTA")
                .requestMatchers(HttpMethod.POST, "/clientes/cadastrar").hasAnyRole("ADMIN", "DENTISTA")
                .requestMatchers("/relatorios/**").hasAnyRole("ADMIN", "DENTISTA")

                // 4. Regras para CLIENTE (e outros autenticados)
                .requestMatchers("/consultas").authenticated()
                .requestMatchers("/consultas/novo", "/consultas/cadastrar", "/consultas/horarios-disponiveis").authenticated()
                
                // ✅ CORREÇÃO: Permite que qualquer usuário autenticado acesse as páginas de edição.
                // A lógica de quem pode editar o quê será feita no Controller.
                .requestMatchers("/consultas/editar/**", "/consultas/atualizar/**").authenticated()
                
                // Apenas ADMIN e DENTISTA podem cancelar ou finalizar
                .requestMatchers("/consultas/cancelar/**", "/consultas/finalizar/**").hasAnyRole("ADMIN", "DENTISTA")

                // Apenas admin pode editar/deletar clientes
                .requestMatchers("/clientes/**").hasRole("ADMIN")

                // 6. Qualquer outra requisição precisa de autenticação
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}