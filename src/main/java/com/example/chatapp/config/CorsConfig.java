// package com.example.chatapp.config;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
// import org.springframework.web.servlet.config.annotation.CorsRegistry;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
// import org.springframework.web.cors.CorsConfigurationSource;

// import java.util.List;

// @Configuration
// public class CorsConfig {

//     @Bean
//     public CorsConfigurationSource corsConfigurationSource() {
//         CorsConfiguration configuration = new CorsConfiguration();
//         // 모든 Origin 허용 (개발환경에서 사용, 운영 시에는 제한하는 것이 좋음)
//         configuration.setAllowedOrigins(List.of("*"));
//         configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
//         configuration.setAllowedHeaders(List.of("*"));
//         // 필요 시 allowCredentials 설정
//         configuration.setAllowCredentials(true);  
        
//         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//         source.registerCorsConfiguration("/**", configuration);
//         return source;
//     }
// }

