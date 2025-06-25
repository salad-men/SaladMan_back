package com.kosta.saladMan.config.jwt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosta.saladMan.auth.PrincipalDetails;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.repository.StoreRepository;


public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	private StoreRepository storeRepository;
	
	public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
		setFilterProcessesUrl("/login");
	}
	private  AuthenticationManager authenticationManager;
	private JwtToken jwtToken = new JwtToken();
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
	    try {
	        ObjectMapper objectMapper = new ObjectMapper();
	        Map<String, String> loginRequest = objectMapper.readValue(request.getInputStream(), Map.class);

	        if (loginRequest == null) {
	            throw new RuntimeException("요청 본문이 비어 있습니다.");
	        }

	        String username = loginRequest.get("username");
	        String password = loginRequest.get("password");

	        if (username == null || password == null) {
	            throw new RuntimeException("아이디 또는 비밀번호가 없습니다.");
	        }

	        UsernamePasswordAuthenticationToken authToken =
	            new UsernamePasswordAuthenticationToken(username, password);

	        return getAuthenticationManager().authenticate(authToken);

	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("로그인 시도 실패", e);
	    }
	}


	//super의 attemptAuthentication 메소드가 실행되고 성공하면 successfulAuthentication가 호출된다.
	//attemptAuthentication 메소드가 리턴해준 Authentication을 파라미터로 받아옴
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		PrincipalDetails principalDetails = (PrincipalDetails)authResult.getPrincipal();
		String username = principalDetails.getUsername();
		
		String accessToken = jwtToken.makeAccessToken(username);
		String refreshToken = jwtToken.makeRefreshToken(username);
		//토큰저장 
		String fcmToken = request.getParameter("fcmToken");
		storeRepository.updateFcmToken(username, fcmToken);
		
		Map<String,String> map = new HashMap<>();
		map.put("access_token", JwtProperties.TOKEN_PREFIX+accessToken);
		map.put("refresh_token", JwtProperties.TOKEN_PREFIX+refreshToken);
		
		ObjectMapper objectMapper = new ObjectMapper();
		String token = objectMapper.writeValueAsString(map);
		System.out.println(token);
		
		response.addHeader(JwtProperties.HEADER_STRING, token);
		response.setContentType("application/json; charset=utf-8");
		
		Store store = principalDetails.getStore();
		Map<String, Object> userInfo = new HashMap<>();
		userInfo.put("id", store.getId());
		userInfo.put("name", store.getName());
		userInfo.put("role", store.getRole());
		userInfo.put("username", store.getUsername());
		response.getWriter().write(objectMapper.writeValueAsString(userInfo));
	}
}
