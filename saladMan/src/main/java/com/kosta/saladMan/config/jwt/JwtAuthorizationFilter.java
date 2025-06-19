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
 		System.out.println(uri);
 		
 		if (uri.startsWith("/user")) {
 		    chain.doFilter(request, response); // 인증 없이 통과시킴
 		    return;
 		}
 		
 		
 	

 		
 		//로그인(인증)이 필요없는 요청은 그대로 진행
 		if(!(uri.contains("/store") || uri.contains("/hq"))) {
 			chain.doFilter(request, response);
 			return;
 		}
 		
		String authentication = request.getHeader(JwtProperties.HEADER_STRING);
		if(authentication==null) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"로그인 필요1");
			return;
		}
		
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String,String> token = objectMapper.readValue(authentication,Map.class);
		System.out.println(token);
		
		//access_token : header로부터 accessToken가져와 bear check
		String accessToken = token.get("access_token");
		if(!accessToken.startsWith(JwtProperties.TOKEN_PREFIX)) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인 필요2");
			return;
		}
		
		accessToken = accessToken.replace(JwtProperties.TOKEN_PREFIX, "");
		try {
			//1. access token check
			//1-1. 보안키, 만료시간 체크
			String username = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET))
						.build()
						.verify(accessToken)
						.getClaim("sub")
						.asString();
			System.out.println(username);
			if(username==null || username.equals("")) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인 필요3");
				return;
			}
			
			//1-2. username check		
			Optional<Store> ostore = storeRepository.findByUsername(username);
			if(ostore.isEmpty()) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인 필요4");
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
			try {
				//2. Refresh Token check : Access Token invalidate 일 경우
				String refreshToken = token.get("refresh_token");
				if(!refreshToken.startsWith(JwtProperties.TOKEN_PREFIX)) {
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인 필요5");
					return;
				}
				
				refreshToken = refreshToken.replace(JwtProperties.TOKEN_PREFIX, "");
				//2-1. 보안키, 만료시간 check
				String username = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET))
					.build()
					.verify(refreshToken)
					.getClaim("sub")
					.asString();
				
				//2-2. username check
				Optional<Store> ostore = storeRepository.findByUsername(username);
				if(ostore.isEmpty()) {
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인 필요6");
					return;
				}
				
				//3-3. refresh token이 invalidate일 경우 accessToken, refreshToken 새로 만들어서 보낸다.
				String reAccessToken = jwtToken.makeAccessToken(username);
				String reRefreshToken = jwtToken.makeRefreshToken(username);
				
				Map<String,String> map = new HashMap<>();
				map.put("access_token", JwtProperties.TOKEN_PREFIX+reAccessToken);
				map.put("refresh_token", JwtProperties.TOKEN_PREFIX+reRefreshToken);

				PrincipalDetails principalDetails = new PrincipalDetails(ostore.get());
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principalDetails, null,
						principalDetails.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(auth);
				
				String reToken = objectMapper.writeValueAsString(map);				
				response.addHeader(JwtProperties.HEADER_STRING, reToken);				
			} catch(Exception e2) {
				e2.printStackTrace();
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인 필요7");
			}
		}
		
		super.doFilterInternal(request, response, chain);
	}
}
