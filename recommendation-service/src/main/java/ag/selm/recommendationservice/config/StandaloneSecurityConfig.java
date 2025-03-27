//package ag.selm.recommendationservice.config;
//
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;
//
//
//@Configuration
//public class StandaloneSecurityConfig {
//
//    @Bean
//    @ConditionalOnMissingBean(SecurityFilterChain.class)
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(antMatcher("/**")).permitAll() // Разрешаем все запросы
//                        .anyRequest().permitAll()) // Или так, если хотите явное правило для всех остальных запросов
//                .csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable()) // Отключаем CSRF (необязательно для API)
//                .build();
//        return http.build();
//    }
//}