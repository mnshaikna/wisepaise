package com.appswella.wisepaise.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.PathContainer;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import com.appswella.wisepaise.util.JwtUtil;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.WebFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.logging.Logger;

@Configuration
@EnableWebFluxSecurity
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityConfig {

    Logger log = Logger.getLogger(SecurityConfig.class.getName());

    @Autowired
    private JwtUtil jwtUtil;

    private static final String[] AUTH_WHITELIST = {
            "/expense/auth/**",
            "/auth/**",
            "/actuator/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/webjars/**",
            "/expense/api/health",
            "/expense/users/reset-pin"
    };

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(ex -> ex
                        .pathMatchers(AUTH_WHITELIST).permitAll()
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public WebFilter jwtWebFilter() {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            log.info("Processing request for path: " + path);

            // Skip JWT validation for whitelisted paths
            for (String pattern : AUTH_WHITELIST) {
                log.info("Checking path: " + pattern);
                if (pathMatcher(pattern, path)) {
                    log.info("Skipping JWT validation for whitelisted path: " + path);
                    return chain.filter(exchange);
                }
            }

            String authHeader =
                    exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            log.info("***AuthHeader:::" + authHeader + "***");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);

            log.info("***Token: " + token + "***");
            if (!jwtUtil.validateToken(token)) {
                log.info("***I Reached this point and 401 is thrown***");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            log.info("all good till now");


            String userId = jwtUtil.getSubject(token); // or email, or username

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            List.of() // add roles if you have them
                    );

            SecurityContextImpl securityContext = new SecurityContextImpl(authentication);

            log.info("JWT valid, SecurityContext populated");

            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(
                            Mono.just(securityContext)
                    ));


            //return chain.filter(exchange);
        };
    }

    private final PathPatternParser pathPatternParser = new PathPatternParser();

    private boolean pathMatcher(String pattern, String path) {
        return pathPatternParser.parse(pattern).matches(PathContainer.parsePath(path));
    }
}