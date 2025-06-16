package com.kosta.saladMan.config;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.modelmapper.ModelMapper;


@Configuration
public class QuerydslConfig {
	
	@Autowired
	EntityManager entityManager;
	
	@Bean
	public JPAQueryFactory jpaQueryFactory() {
		return new JPAQueryFactory(entityManager);
	}
	
	@Bean
	public ModelMapper modleMapper() {
		return new ModelMapper();
	}
	
}
