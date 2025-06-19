package com.kosta.saladMan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 전역 CORS 설정
     */
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                // Vite 개발서버와 S3 호스팅 도메인 허용
//	        
//                .allowedOriginPatterns(
//                    "http://localhost:5173",
//                    "http://saladman-web.s3-website.ap-northeast-2.amazonaws.com"
//                )
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                .allowedHeaders("*")
//                .allowCredentials(true);
//    }
    
    @Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true); //프론트의 Access-Control-Allow-Credentials 요청에 대한 허용
		config.addAllowedOrigin("http://localhost:5173");
		config.addAllowedOrigin("http://saladman-web.s3-website.ap-northeast-2.amazonaws.com");
		config.addAllowedOriginPattern("*");
		config.addAllowedHeader("*"); //프론트의 Access-Control-Allow-Headers 요청에 대한 허용
		config.addAllowedMethod("*"); //프론트의 Access-Control-Allow-Method 요청에 대한 허용
		config.addExposedHeader(JwtProperties.HEADER_STRING); //클라이언트(리액트 등)가 응답에 접간할 수 있는 Header 추가
//		source.registerCorsConfiguration("/*", config);
//		source.registerCorsConfiguration("/*/*", config);
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}
}
