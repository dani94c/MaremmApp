package com.maremmapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import org.springframework.context.annotation.Bean;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

import com.maremmapp.remoting.repository.EventRepository;
import com.maremmapp.remoting.repository.UserRepository;
import com.maremmapp.remoting.repository.ReservationRepository;


@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@ConfigurationPropertiesScan("com.maremmapp")
public class WebServerRmiApplication {
	@Autowired
	private Utils utils;
	
	// Creates a bean that has the same interface exposed by the service running on the server side and 
	// that will transparently route the invocations it will receive to the server
	@Bean 
	RmiProxyFactoryBean UserService() {
	    RmiProxyFactoryBean rmiProxyFactory = new RmiProxyFactoryBean();
	    rmiProxyFactory.setServiceUrl("rmi://"+utils.getRemoteServerIP()+":"+
	    		utils.getRemoteServerPort()+"/UserRepository");
	    rmiProxyFactory.setServiceInterface(UserRepository.class);
	    return rmiProxyFactory;
	}
	
	@Bean 
	RmiProxyFactoryBean EventService() {
	    RmiProxyFactoryBean rmiProxyFactory = new RmiProxyFactoryBean();
	    rmiProxyFactory.setServiceUrl("rmi://"+utils.getRemoteServerIP()+":"+
	    		utils.getRemoteServerPort()+"/EventRepository");
	    rmiProxyFactory.setServiceInterface(EventRepository.class);
	    return rmiProxyFactory;
	}
	
	@Bean 
	RmiProxyFactoryBean ReservationService() {
	    RmiProxyFactoryBean rmiProxyFactory = new RmiProxyFactoryBean();
	    rmiProxyFactory.setServiceUrl("rmi://"+utils.getRemoteServerIP()+":"+
	    		utils.getRemoteServerPort()+"/ReservationRepository");
	    rmiProxyFactory.setServiceInterface(ReservationRepository.class);
	    return rmiProxyFactory;
	}

	public static void main(String[] args) {
		SpringApplication.run(WebServerRmiApplication.class, args);
	}

}