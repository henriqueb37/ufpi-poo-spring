package ufpi.poo.spring.bar.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ufpi.poo.spring.bar.dao.UsuarioRepository;
import ufpi.poo.spring.bar.misc.CargoTipos;
import ufpi.poo.spring.bar.model.Usuario;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/login", "/css/**", "/js/**").permitAll()
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/mesas", true)
                        .permitAll()
                )
                .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login"));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository repository, PasswordEncoder passwordEncoder, UsuarioRepository usuarioRepository) {
        return args -> {
            if ((usuarioRepository.countByCargo(CargoTipos.ADMIN) < 1)) {
                Usuario newAdmin = new Usuario(
                        "admin",
                        "admin",
                        passwordEncoder.encode("admin123"),
                        CargoTipos.ADMIN
                );
                repository.save(newAdmin);
                log.warn("⚠ ADMIN PADRÃO CRIADO: Login: admin / Senha: admin123");
            }
            if (usuarioRepository.existsByEmail("admin")) {
                log.warn("⚠ É RECOMENDADO A REMOÇÃO DO USUÁRIO PADRÃO admin!");
            }
        };
    }
}
