package com.kosta.saladMan.config.jwt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosta.saladMan.auth.PrincipalDetails;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.repository.StoreRepository;

//인가 : 로그인 처리가 되어야만 하는 처리가 들어왔을때 실행
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
	
	private StoreRepository storeRepository;
	
	private JwtToken jwtToken = new JwtToken();
	
	public JwtAuthorizationFilter(AuthenticationManager authenticationManager,StoreRepository storeRepository) {
		super(authenticationManager);
		this.storeRepository = storeRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
 		String uri = request.getRequestURI();
 	    System.out.println("▶ Authorization 헤더: " + request.getHeader("Authorization"));
 		
 	    if ("/actuator/health".equals(uri)) {
 	        chain.doFilter(request, response);
 	        return;
 	    }
 		
// 		//로그인(인증)이 필요없는 요청은 그대로 진행
// 		if(!(uri.contains("/store") || uri.contains("/hq"))) {
// 			chain.doFilter(request, response);
// 			return;
// 		}
 	    
 	   if (uri.startsWith("/connect")) {
 		    chain.doFilter(request, response);
 		    return;
 		}

 		
 		String accessTokenHeader = request.getHeader(JwtProperties.HEADER_STRING);
	    String refreshTokenHeader = request.getHeader("X-Refresh-Token");
	   
		if(accessTokenHeader==null || !accessTokenHeader.startsWith(JwtProperties.TOKEN_PREFIX)) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"로그인 필요1");
			return;
		}		
		
		String accessToken = accessTokenHeader.replace(JwtProperties.TOKEN_PREFIX, "").trim();
		
		try {
			//1. access token check
			//1-1. 보안키, 만료시간 체크
			String username = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET))
						.build()
						.verify(accessToken)
						.getClaim("sub")
						.asString();
			System.out.println("username from accessToken: " +username);
			if(username==null || username.equals("")) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인 필요2");
				return;
			}
			
			//1-2. username check		
			Optional<Store> ostore = storeRepository.findByUsername(username);
			if(ostore.isEmpty()) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인 필요3");
				System.out.println("role from DB: " + ostore.get().getRole());
				return;
			}
			
			//1-3. User를 Authentcation으로 생성해서 Security session(Context Holder)에 넣어준다.(그러면, Controller에서 사용할 수 있다.)
			PrincipalDetails principalDetails = new PrincipalDetails(ostore.get());
			UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principalDetails, null,
					principalDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(auth);
			chain.doFilter(request, response);
			return;
			
			
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("Access Token 만료됨 → refresh 로직 진입 여부 확인");
			//2. Refresh Token check : Access Token invalidate 일 경우
			if(refreshTokenHeader == null || !refreshTokenHeader.startsWith(JwtProperties.TOKEN_PREFIX)) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인 필요4");
				return;
			}
				
			try {
				//2-1. 보안키, 만료시간 check
				String refreshToken = refreshTokenHeader.replace(JwtProperties.TOKEN_PREFIX, "").trim();
				String username = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET))
						.build()
						.verify(refreshToken)
						.getClaim("sub")
						.asString();
					
				//2-2. username check
				Optional<Store> ostore = storeRepository.findByUsername(username);
				if(ostore.isEmpty()) {
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인 필요5");
					return;
				}
				
				//3-3. refresh token이 invalidate일 경우 accessToken, refreshToken 새로 만들어서 보낸다.
				String reAccessToken = jwtToken.makeAccessToken(username);
				String reRefreshToken = jwtToken.makeRefreshToken(username);
	
				PrincipalDetails principalDetails = new PrincipalDetails(ostore.get());
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principalDetails, null,
						principalDetails.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(auth);
					
				// 새 토큰 헤더에 담아 응답
	            response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + reAccessToken);
	            response.addHeader("X-Refresh-Token", JwtProperties.TOKEN_PREFIX + reRefreshToken);
	
	            chain.doFilter(request, response);
	            return;
            
			} catch(Exception e2) {
				e2.printStackTrace();
				System.out.println("Refresh Token 만료 → 로그인 필요");
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인 필요6");
				return;
			}
		}
		
	}
}