package com.kosta.saladMan.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.kosta.saladMan.config.jwt.JwtProperties;


@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * BCryptPasswordEncoder 빈 설정
     */
    
	@Bean
	public PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}

    @Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true); //프론트의 Access-Control-Allow-Credentials 요청에 대한 허용
		config.addAllowedOrigin("https://www.saladman.net");
		config.addAllowedOrigin("http://www.saladman.net");
		config.addAllowedOrigin("http://localhost:5173");
		config.addAllowedOrigin("http://saladman-web.s3-website.ap-northeast-2.amazonaws.com");
        config.addAllowedOrigin("https://api.saladman.com");
        config.addAllowedOrigin("http://api.saladman.com");
        config.addAllowedOrigin("http://localhost:8081");
        config.addAllowedOrigin("http://localhost:8080");
        config.addAllowedOrigin("http://192.168.0.15:8080");
        config.addAllowedOrigin("http://192.168.0.15:5173");
        config.addAllowedOrigin("http://192.168.0.15:8090");
        //config.addAllowedOriginPattern("*");


		config.addAllowedHeader("*"); //프론트의 Access-Control-Allow-Headers 요청에 대한 허용
		config.addAllowedMethod("*"); //프론트의 Access-Control-Allow-Method 요청에 대한 허용
		config.addAllowedOriginPattern("*");
		config.addExposedHeader(JwtProperties.HEADER_STRING); //클라이언트(리액트 등)가 응답에 접간할 수 있는 Header 추가
//		source.registerCorsConfiguration("/*", config);
//		source.registerCorsConfiguration("/*/*", config);
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}
    
    @Bean
    public CorsFilter kioskCorsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
            "http://localhost:5173",
            "http://192.168.0.23:5173",
            "http://localhost:8081",
            "http://saladman-web.s3-website.ap-northeast-2.amazonaws.com",
            "https://api.saladman.com",
            "http://192.168.0.15:8080",
            "http://192.168.0.15:8090",
            "http://192.168.0.15:5173"

        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        source.registerCorsConfiguration("/kiosk/**", config);
        return new CorsFilter(source);
    }
}
