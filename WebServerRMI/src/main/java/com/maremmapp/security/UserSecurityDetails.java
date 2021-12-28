package com.maremmapp.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.maremmapp.remoting.entity.User;

public class UserSecurityDetails implements UserDetails {

	private User user;
	private List<GrantedAuthority> authorities;

	public UserSecurityDetails(User user) {
		this.user = user;
		// In MySQL DB authority is saved as String. In UserSecurityDetails has to be a GrantedAutority, so the convertion is necessary
		this.authorities = Arrays.stream(user.getRole().split(","))
							.map(SimpleGrantedAuthority::new)
							.collect(Collectors.toList());
    }
	
	public UserSecurityDetails() {}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
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
		return true;
	}
	
	public User getUser() {
		return user;
	}
}
