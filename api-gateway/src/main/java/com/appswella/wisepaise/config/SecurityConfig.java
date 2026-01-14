package com.appswella.wisepaise.config;

import com.appswella.wisepaise.security.JwtWebFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain publicSecurity(ServerHttpSecurity http) {
        return http.securityMatcher(ServerWebExchangeMatchers.pathMatchers("/auth/**", "/expense/auth/**", "/actuator/**")).csrf(ServerHttpSecurity.CsrfSpec::disable).authorizeExchange(ex -> ex.anyExchange().permitAll()).build();
    }

    // 2️⃣ Protected endpoints (JWT required)
    @Bean
    public SecurityWebFilterChain securedSecurity(ServerHttpSecurity http, JwtWebFilter jwtWebFilter) {
        return http.securityMatcher(ServerWebExchangeMatchers.anyExchange()).csrf(ServerHttpSecurity.CsrfSpec::disable).authorizeExchange(ex -> ex.anyExchange().authenticated()).addFilterAt(jwtWebFilter, SecurityWebFiltersOrder.AUTHORIZATION).httpBasic(ServerHttpSecurity.HttpBasicSpec::disable).formLogin(ServerHttpSecurity.FormLoginSpec::disable).build();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails user = User.builder().username("user").password(passwordEncoder().encode("password")).roles("USER").build();

        UserDetails admin = User.builder().username("admin").password(passwordEncoder().encode("password")).roles("ADMIN").build();

        return new MapReactiveUserDetailsService(user, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}