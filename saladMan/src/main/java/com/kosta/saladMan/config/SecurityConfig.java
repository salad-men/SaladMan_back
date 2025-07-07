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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import com.kosta.saladMan.auth.PrincipalDetailsService;
import com.kosta.saladMan.repository.StoreRepository;
import com.kosta.saladMan.config.jwt.JwtAuthorizationFilter;
import com.kosta.saladMan.config.kiosk.KioskAuthenticationSuccessHandler;
import com.kosta.saladMan.config.kiosk.KioskAuthorizationFilter;
import com.kosta.saladMan.config.kiosk.KioskJwtAuthenticationFilter;
import com.kosta.saladMan.config.jwt.JwtAuthenticationFilter;
import com.kosta.saladMan.config.jwt.JwtAuthorizationFilter;
import com.kosta.saladMan.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // user페이지 hq만 수정할 수 있도록 권한 설정하는 코드
@RequiredArgsConstructor
public class SecurityConfig {

	@Autowired
	private CorsFilter corsFilter;

	private final StoreRepository storeRepository;

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	@Order(1)
	public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager)
			throws Exception {
		http.addFilter(corsFilter) // 다른 도메인 접근 허용
				.csrf().disable() // csrf 공격 비활성화
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // session비활성화

		http.formLogin().disable() // 로그인 폼 비활성화
				.httpBasic().disable() // httpBasic은 header에 username,password를 암호화하지 않은 상태로 주고받는다. 이를 사용하지 않겠다는 것.
				.addFilterAt(new JwtAuthenticationFilter(authenticationManager, storeRepository),
						UsernamePasswordAuthenticationFilter.class);
		
		http.addFilter(new JwtAuthorizationFilter(authenticationManager, storeRepository))
				.authorizeRequests()
				.antMatchers("/connect/**", "/connect", "/connect/info/**", "/connect/info").permitAll()
				.antMatchers("/hq/**").access("hasRole('ROLE_HQ')")
				.antMatchers("/store/**").access("hasRole('ROLE_STORE')")
				.antMatchers("/actuator/health").permitAll()
				.antMatchers("/user/**").permitAll()
				.antMatchers("/chat/sse").permitAll()
				.antMatchers("/chat/**").authenticated()
				.anyRequest().permitAll();
		return http.build();
	}
	
	@Bean
	@Order(0)
	public SecurityFilterChain kioskSecurityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
	    KioskJwtAuthenticationFilter kioskFilter = new KioskJwtAuthenticationFilter(authenticationManager, storeRepository);
	    KioskAuthorizationFilter kioskAuthorizationFilter = new KioskAuthorizationFilter(authenticationManager, storeRepository);


	    http
	        .antMatcher("/kiosk/**")
	        .cors().and()
	        .csrf().disable()
	        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	        .and()
	        .addFilter(corsFilter)
	        .formLogin().disable()
	        .httpBasic().disable()
	        .addFilterAt(kioskFilter, UsernamePasswordAuthenticationFilter.class)
	        .addFilter(kioskAuthorizationFilter)
	        .authorizeRequests()
	        	.antMatchers("/kiosk/confirm").permitAll()
	            .antMatchers("/kiosk/login").permitAll()
	            .anyRequest().hasRole("STORE");
	    

	    return http.build();
	}
}