package com.pruthviraj.hydra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/login.html",
                    "/register.html",
                    "/dashboard.html",
                    "/profile.html",
                    "/lab.html",
                    "/api/login",
                    "/api/register",
                    "/api/status"
                ).permitAll()

                .requestMatchers("/admin.html", "/api/admin/**")
                .hasRole("ADMIN")

                .anyRequest().authenticated()
            )

            .httpBasic(basic -> basic.realmName("Hydra Admin"))

            .formLogin(form -> form.disable());

        return http.build();
    }
}