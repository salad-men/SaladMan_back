package com.kosta.saladMan.config.kiosk;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosta.saladMan.auth.PrincipalDetails;
import com.kosta.saladMan.config.jwt.JwtProperties;
import com.kosta.saladMan.config.jwt.JwtToken;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.repository.StoreRepository;

public class KioskJwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private StoreRepository storeRepository;
    private final JwtToken jwtToken = new JwtToken();
    private final ThreadLocal<Map<String, String>> loginCache = new ThreadLocal<>();

    public KioskJwtAuthenticationFilter(AuthenticationManager authenticationManager, StoreRepository storeRepository) {
        super(authenticationManager);
    	this.storeRepository = storeRepository;
        setFilterProcessesUrl("/kiosk/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        try {
	        String uri = request.getRequestURI();
	        System.out.println("KioskJwtAuthenticationFilter 요청 URI: " + uri);
        	
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> loginRequest = objectMapper.readValue(request.getInputStream(), Map.class);
            loginCache.set(loginRequest);

            System.out.println("읽은 요청 데이터: " + loginRequest);

            
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");

            if (username == null || password == null) {
                throw new RuntimeException("아이디 또는 비밀번호가 없습니다.");
            }

            UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, password);

            return this.getAuthenticationManager().authenticate(authToken);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("로그인 시도 실패", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();
        String username = principalDetails.getUsername();

        Map<String, String> loginRequest = loginCache.get();
        loginCache.remove();

        String accessToken = jwtToken.makeAccessToken(username);
        String refreshToken = jwtToken.makeRefreshToken(username);

        Map<String, String> map = new HashMap<>();
        map.put("access_token", JwtProperties.TOKEN_PREFIX + accessToken);
        map.put("refresh_token", JwtProperties.TOKEN_PREFIX + refreshToken);

        ObjectMapper objectMapper = new ObjectMapper();
        String tokenJson = objectMapper.writeValueAsString(map);

        response.addHeader(JwtProperties.HEADER_STRING, tokenJson);
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
