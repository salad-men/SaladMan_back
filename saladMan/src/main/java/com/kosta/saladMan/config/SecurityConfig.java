//package com.kosta.saladMan.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//public class SecurityConfig {
//	
//	//계정 생성 postman 테스트용
//	@Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf().disable()
//            .authorizeRequests()
//                .antMatchers("/hq/*").permitAll() // ✅ 여기 허용
//                .anyRequest().authenticated(); // 나머지는 인증 필요
//        return http.build();
//    }
//	
//	@Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}


package com.kosta.saladMan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

//@Configuration
//public class SecurityConfig {
//	
//	//계정 생성 postman 테스트용
//	@Bean
//  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//      http
//          .csrf().disable()
//          .authorizeRequests()
//              .antMatchers("/hq/*").permitAll() // ✅ 여기 허용
//              .anyRequest().authenticated(); // 나머지는 인증 필요
//      return http.build();
//  }
//	
//	@Bean
//  public BCryptPasswordEncoder bCryptPasswordEncoder() {
//      return new BCryptPasswordEncoder();
//  }
//}

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
          // ① WebMvcConfigurer/CorsConfigurationSource에 등록한 CORS 정책을 활성화
          .cors()    
        .and()
          .csrf().disable()
          .authorizeRequests()
            // ② 프리플라이트(OPTIONS) 요청도 풀어주고
            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            // ③ /hq/** 전체(하위 몇 단계든) 열어주고
            .antMatchers("/hq/**", "/api/**").permitAll()
            // 그 외는 인증 필요
            .anyRequest().authenticated();
        return http.build();
    }
    
}
