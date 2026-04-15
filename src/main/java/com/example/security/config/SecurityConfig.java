package com.example.security.config;

import com.example.security.Filter.JwtAuthenticationFilter;
import com.example.security.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // manager把任务委派给 provider，每个 provider自己指定认证方法
    // dao provider 比对待认证的 authentication的 name和 password 与 数据库的 name和 password
    // 调用 userDetailsService 的 loadUserByUsername方法获得 name和 password
    // 调用 passwordEncoder 比对密码
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(); // 创建 provider
        provider.setUserDetailsService(userDetailsService);     // 绑定查库逻辑
        provider.setPasswordEncoder(passwordEncoder());         // 绑定密码比对逻辑
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // JWT 模式下通常关闭 CSRF
                .csrf(AbstractHttpConfigurer::disable)
                // 无状态
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 放行注册和登录 jwt发放由login实现
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register").permitAll()
                        .anyRequest().authenticated())
                // 注册provider
                .authenticationProvider(authenticationProvider());

        // 在 UsernamePasswordAuthenticationFilter 之前插入 JWT 过滤器
        // 如果 JWT 校验成功，SecurityContextHolder 就有了用户信息，
        // 后续的 UsernamePasswordAuthenticationFilter 就会发现已认证而直接跳过。
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
