package com.kosta.saladMan.config.jwt;

public interface JwtProperties {
	String SECRET = "코스타";
	Integer ACCESS_EXPIRATION_TIME = 60000*60*4;
//	Integer ACCESS_EXPIRATION_TIME = 10000;
	Integer REFRESH_EXPIRATION_TIME = 60000*60*24;
//	Integer REFRESH_EXPIRATION_TIME = 60000;
	String TOKEN_PREFIX = "Bearer ";
	String HEADER_STRING = "Authorization";
}
