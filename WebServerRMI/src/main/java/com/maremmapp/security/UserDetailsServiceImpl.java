package com.maremmapp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.maremmapp.remoting.entity.User;
import com.maremmapp.remoting.repository.UserRepository;

/*
 * Service used by Spring security
 * The UserDetailsService interface is used to retrieve user-related data. It has one method named loadUserByUsername() 
 * which can be overridden to customize the process of finding the user.
 */

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;  
    
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	User user = userRepository.findByUsername(username);
		if(user == null)
			throw new UsernameNotFoundException("User "+username+" not found");
		UserSecurityDetails tmp = new UserSecurityDetails(user);
    	return tmp;
    }

}
