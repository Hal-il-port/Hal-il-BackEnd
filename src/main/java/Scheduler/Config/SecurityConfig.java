package Scheduler.Config;

import Scheduler.Security.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors() // ✅ CORS 허용 추가
                .and()
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/api/auth/**", "/api/signup",  "/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/swagger-ui/index.html",
                        "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userDetailsService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // JWT 필터 정의
    public static class JwtAuthenticationFilter extends OncePerRequestFilter {

        private final JwtUtil jwtUtil;
        private final UserDetailsService userDetailsService;

        public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
            this.jwtUtil = jwtUtil;
            this.userDetailsService = userDetailsService;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {

            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    String email = jwtUtil.validateTokenAndGetEmail(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("Authentication success for user: " + email);
                } catch (Exception e) {
                    System.out.println("JWT validation failed: " + e.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            } else {
                System.out.println("No Authorization header found or does not start with Bearer");
            }

            System.out.println("Authentication in context: " + SecurityContextHolder.getContext().getAuthentication());
            filterChain.doFilter(request, response);
        }
    }
}
