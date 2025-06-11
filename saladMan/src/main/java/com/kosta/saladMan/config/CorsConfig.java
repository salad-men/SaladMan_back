package com.kosta.saladMan.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


@Configuration
public class CorsConfig {
	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true); //프론트의 Access-Control-Allow-Credentials 요청에 대한 허용
		//config.addAllowedOrigin("http://localhost:5173");
		config.addAllowedOriginPattern("*");
		config.addAllowedHeader("*"); //프론트의 Access-Control-Allow-Headers 요청에 대한 허용
		config.addAllowedMethod("*"); //프론트의 Access-Control-Allow-Method 요청에 대한 허용
		//config.addExposedHeader(JwtProperties.HEADER_STRING); //클라이언트(리액트 등)가 응답에 접간할 수 있는 Header 추가
		source.registerCorsConfiguration("/*", config);
		source.registerCorsConfiguration("/*/**", config);
		return new CorsFilter(source);
	}
}
