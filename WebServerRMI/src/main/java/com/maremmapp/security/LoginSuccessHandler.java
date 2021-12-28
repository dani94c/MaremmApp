package com.maremmapp.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
	
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		response.setStatus(HttpServletResponse.SC_OK);
		for(GrantedAuthority auth : authentication.getAuthorities()) {
			if(auth.getAuthority().equals("ADMIN")) {
				redirectStrategy.sendRedirect(request, response, "/admin");
			} else if(auth.getAuthority().equals("CUSTOMER")) {
				redirectStrategy.sendRedirect(request, response, "/customer");
			} else if(auth.getAuthority().equals("EVENTPLANNER")) {
				redirectStrategy.sendRedirect(request, response, "/eventPlanner");
			}
		}
	}
}
