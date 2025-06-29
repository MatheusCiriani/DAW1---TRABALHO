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
@EnableWebSecurity // Habilita a segurança web do Spring Security
public class WebSecurityConfig {

    // Define a cadeia de filtros de segurança (como as requisições HTTP serão protegidas)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Permite acesso irrestrito a recursos estáticos (CSS, JS, imagens, etc.)
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                // Permite acesso irrestrito à página de login e cadastro de cliente
                .requestMatchers("/login", "/clientes/novo", "/clientes/cadastrar").permitAll()
                // Requer autenticação para qualquer outra requisição
                .anyRequest().authenticated()
            )
            // Configura o formulário de login
            .formLogin(form -> form
                .loginPage("/login") // Define a URL da sua página de login customizada
                .defaultSuccessUrl("/", true) // Redireciona para a página inicial após login bem-sucedido
                .failureUrl("/login?error=true") // Redireciona para a página de login com erro em caso de falha
                .permitAll() // Permite que todos acessem a página de login
            )
            // Configura o logout
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // URL para logout
                .logoutSuccessUrl("/login?logout=true") // Redireciona para login após logout bem-sucedido
                .permitAll() // Permite que todos façam logout
            )
            // Desabilita o CSRF para permitir POSTs de formulários HTML sem token CSRF (PARA SIMPLIFICAR NO INÍCIO)
            // ATENÇÃO: Para aplicações em produção, CSRF DEVE ser habilitado e tratado.
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    // Define o encoder de senha a ser usado (BCrypt é o recomendado)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Você precisará de um UserDetailsService para carregar usuários do banco de dados.
    // Faremos isso no próximo passo.
}