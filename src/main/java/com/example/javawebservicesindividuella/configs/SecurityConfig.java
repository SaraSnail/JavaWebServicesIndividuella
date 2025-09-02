package com.example.javawebservicesindividuella.configs;

import com.example.javawebservicesindividuella.converters.JwtAuthConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private JwtAuthConverter jwtAuthConverter;

    @Autowired
    public void setJwtAuthConverter(JwtAuthConverter jwtAuthConverter) {
        this.jwtAuthConverter = jwtAuthConverter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf->csrf.disable())
                .oauth2ResourceServer(oauth->
                        oauth.jwt(jwt->
                                jwt.jwtAuthenticationConverter(jwtAuthConverter)))
                .authorizeHttpRequests(auth->
                        auth
                                .requestMatchers("/api/v2/newpost").hasRole("user")
                                .requestMatchers("/api/v2/updatepost").hasRole("user")
                                .requestMatchers("/api/v2/deletepost/**").hasAnyRole("user","admin")
                                .requestMatchers("/api/v2/count").hasRole("admin")
                                .anyRequest().authenticated()
                );

        return http.build();
    }
}
