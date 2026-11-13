package com.sena.springecommerce.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitar CSRF porque las APIs REST con JWT son stateless
            .csrf(csrf -> csrf.disable())

            // Manejo de excepciones personalizadas para 401 y 403 con JSON
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler)
            )

            // Sesión sin estado (Stateless)
            .sessionManagement(sess -> sess
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Reglas de autorización de endpoints (RBAC)
            .authorizeHttpRequests(auth -> auth
                // 1. Recursos estáticos y frontend web públicos
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/favicon.ico",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/static/**",
                    "/index",
                    "/tienda",
                    "/presentacion_tema7.html",
                    "/error"
                ).permitAll()

                // 2. Endpoint de autenticación (Login y Registro) son públicos
                .requestMatchers("/api/auth/**", "/auth/**").permitAll()

                // 3. Consultar productos (GET) es público para que cualquiera pueda verlos
                .requestMatchers(HttpMethod.GET, "/apiproductos/**").permitAll()

                // 4. SOLO el rol ADMIN puede crear, actualizar o borrar productos
                .requestMatchers(HttpMethod.POST, "/apiproductos/create").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/apiproductos/update/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/apiproductos/delete/**").hasRole("ADMIN")

                // 5. Cualquier otra petición debe estar autenticada
                .anyRequest().authenticated()
            )

            // Proveedor de autenticación con BCrypt
            .authenticationProvider(authenticationProvider())

            // Agregar el filtro JWT antes de UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
