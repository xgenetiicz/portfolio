package com.example.genetiicz.Config;

import com.example.genetiicz.Enum.Role;
import com.example.genetiicz.Filter.JwtAuthFilter;
import com.example.genetiicz.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserService userService;

    @Autowired
    private final JwtAuthFilter jwtAuthFilter;

    @Autowired
    private PasswordConfig passwordConfig;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, UserService userService, PasswordConfig passwordConfig) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userService = userService;
        this.passwordConfig = passwordConfig;

    }


    /*
    Later on i need to change the security filter, since i don't want spring to permit after i generate the token
    so i want to get authenticated and check for it. The only thing that it should actually permit is
    the login page and possibility to register
    so it should be then "api/auth/login" and "api/auth/register"
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                .csrf(csrf -> csrf.disable())
                        .cors(cors ->cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        //Making changes to add ADMIN VERIFICATION if it is present so just I can add projects
                        .requestMatchers("/api/projects/addproject", "/api/projects/delete/**", "/api/content/upload/files/**",
                                "/api/content/upload/image/**", "/api/content/delete/**", "/api/projects/update/**")
                        .hasAuthority(String.valueOf(Role.ADMIN))
                       // .requestMatchers("/api/projects/upload/image/**").authenticated() // this project is no longer multi-users-authentication.

                        //Testing here for javacontact, but i think i will check this with authority as user have the role of user, where this is an authenticated user and not a bot spamming the email.
                        .requestMatchers("/api/mail/contact","/api/auth/forgot/password").permitAll()
                        //Adding permitall on requestmatch for ("/api/projects/fetchProjects") this is so all can view the projects - It should not have any auth on it.
                        .requestMatchers("/api/projects/fetchProjects").permitAll()
                        //same here, there should be only to see the content provided to the project.
                        .requestMatchers("/api/content/project/{projectId}").permitAll()
                        //for serving content
                        .requestMatchers("/uploads/**").permitAll()
                        //for downloading content, and with whole URI so everyone can download pdf or zip files.
                        .requestMatchers("/api/content/download/**").permitAll()
                        //health actuactor checks own health on container.
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                    .sessionManagement(session -> session
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // every request needs to be treated as a new one.
                    .authenticationProvider(authenticationProvider())
                    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                    .exceptionHandling(exception -> exception
                            .authenticationEntryPoint(((request, response, authException) ->
                                    response.sendError(401))));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("https://gentirudi.buildshubs.net", "http://localhost:5173")); //changed to react localhost
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    //  based on: TODO: https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/dao-authentication-provider.html
    //           TODO: https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/index.html


    @Bean
    public AuthenticationManager authenticationManager (AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider  authenticationProvider = new DaoAuthenticationProvider(userService);
        authenticationProvider.setPasswordEncoder(passwordConfig.passwordEncoder());
        return authenticationProvider;
    }
}
