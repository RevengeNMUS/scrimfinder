package com.scrimfinder.scrimfinder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SillySecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity htp) throws Exception {
        htp.authorizeHttpRequests((requests) -> requests
                .requestMatchers("/dashboard").authenticated()
                .anyRequest().permitAll())
        .oauth2Login(l -> l
                .defaultSuccessUrl("/dashboard"))
        .logout(l -> l
                .logoutSuccessUrl("/homepage").permitAll()
        );

        return htp.build();
    }
}
