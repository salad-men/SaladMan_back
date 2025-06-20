package com.kosta.saladMan.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.kosta.saladMan.entity.store.*;

import lombok.Data;
// security가 /loginProc 주소를 낚아서 로그인 한다
// 로그인 진행이 완료가 되면 security session을 만들어준다(Security ContextHolder)
// security session에 들어가는 타입은 Authentication 타입의 객체여야 한다.
// 그래서, Authentication안에 User 정보를 넣어야 한다.
// 그 User 오브젝트 타입은 UserDetails 타입이어야 한다.
// 즉, (Security ContextHolder (new Authentication(new UserDetails(new User)))

@Data
public class PrincipalDetails implements UserDetails {
	
	private Store store;
	
	private Map<String, Object> attributes;
	
	public PrincipalDetails(Store store) {
		this.store = store;
	}

	public PrincipalDetails(Store store, Map<String, Object> attributes) {
		this.store = store;
		this.attributes = attributes;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collect = new ArrayList<>();
		collect.add(()->store.getRole());
		System.out.println("권한 체크: " + store.getRole());
		return collect;
	}

	@Override
	public String getPassword() {
		return store.getPassword();
	}

	@Override
	public String getUsername() {
		return store.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		//우리 사이트에서 1년동안 로그인을 안하면 휴면 계정으로 변하리고 했다면
		//현재시간 - 마지막 로그인 시간을 계산하여 1년 초과하면 return false
		return true;
	}
}
