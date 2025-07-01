package com.kosta.saladMan.config.kiosk;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
@Component
public class KioskAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		// 세션에 키오스크 로그인 플래그 세팅
		request.getSession().setAttribute("kioskLogin", true);

//		// 특정 URL로 리다이렉트
//		setDefaultTargetUrl("/kiosk/main");

		super.onAuthenticationSuccess(request, response, authentication);
	}
}
