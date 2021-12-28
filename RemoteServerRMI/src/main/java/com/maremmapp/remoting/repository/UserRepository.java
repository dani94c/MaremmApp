package com.maremmapp.remoting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.maremmapp.remoting.entity.User;

public interface UserRepository extends JpaRepository<User,Long>{
	// used by spring security when the user logged in
	User findByUsername(String username);
	boolean existsById(Long userID);
}