package com.maremmapp.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

/*
 * The SececurityConfig class is annotated with @EnableWebSecurity to enable 
 * Spring Security’s web security support and provide the Spring MVC integration. 
 * It also extends WebSecurityConfigurerAdapter and overrides a couple of its methods
 *  to set some specifics of the web security configuration.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
	
	@Autowired
	UserDetailsService userDetailsService;
	
	@Autowired
	private LoginSuccessHandler successHandler;
	
	/*
	 * Vengono usati due configure: il primo serve per indicare a spring security come autenticare l'utente.
	 * In particolare,l'authentication manager interrogherà lo UserDetailService ridefinito da noi per andare a cercare l'utente che ha inserito
	 * i dati nel form di login e questo service interagisce col db restituendo un oggetto UserDetails con l'oggetto User trovato (se esiste). 
	 * Il secondo gestisce le autorizzazioni per accedere alle varie pagine html
	 */
	//Configure for the authentication of the user from the db
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		//userDetailsService = myService();
		//For simplicity we are not using password encryption. The NoOpPasswordEncoder means that no
        //password encoder will be used therefore password will be stored in plaintext
		auth.userDetailsService(userDetailsService).passwordEncoder(NoOpPasswordEncoder.getInstance());
	}
	
	/*
	 * Defines which URL paths should be secured and which should not. Specifically, the / and /home 
	 * paths are configured to not require any authentication ( permitAll() ). All other paths must be authenticated.
	 *
	 * When a user successfully logs in, they are redirected to the previously requested page that required 
	 * authentication. There is a custom /login page (which is specified by loginPage()), and everyone is allowed to view it.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		log.debug("Sono dentro configure");
		http
			.authorizeRequests()
				.antMatchers("/admin").hasAuthority("ADMIN")
				.antMatchers("/eventplanner").hasAnyAuthority("ADMIN", "EVENTPLANNER")
				.antMatchers("/customer").hasAnyAuthority("ADMIN", "CUSTOMER")
				.antMatchers("/", "/bootstrapStyle.css", "/style.css","/registration").permitAll()
				.anyRequest().authenticated()
				.and()
			.formLogin()
				.loginPage("/login")
				.and().formLogin().successHandler(successHandler)
				.permitAll()
				.and()
			.logout()
				.permitAll();
	}
	
}
