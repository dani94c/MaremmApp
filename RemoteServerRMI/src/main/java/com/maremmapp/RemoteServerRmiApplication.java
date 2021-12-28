package com.maremmapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.remoting.rmi.RmiServiceExporter;

import com.maremmapp.remoting.repository.EventRepository;
import com.maremmapp.remoting.repository.ReservationRepository;
import com.maremmapp.remoting.repository.UserRepository;


@SpringBootApplication
public class RemoteServerRmiApplication {
	
	// Exporter makes the service available to clients
	// Expose a service via RMI. Remote object URL is rmi://HOST:1099/UserRepository
	@Bean 
	RmiServiceExporter UserExporter(UserRepository implementation) {
	    Class<UserRepository> serviceInterface = UserRepository.class;
	    RmiServiceExporter exporter = new RmiServiceExporter();
		// Provides a reference to the interface that will be made remotely callable.
	    exporter.setServiceInterface(serviceInterface);
		// Provides a reference to the object actually executing the method 
	    exporter.setService(implementation);
		//  Set a service name, that allows identifying the exposed service in the RMI registry. 
	    //  In this case it will be UserRepository because takes the class name (UserRepository.class) 
	    exporter.setServiceName(serviceInterface.getSimpleName()); 
	    return exporter;
	}
	
	@Bean 
	RmiServiceExporter EventExporter(EventRepository implementation) {
	    Class<EventRepository> serviceInterface = EventRepository.class;
	    RmiServiceExporter exporter = new RmiServiceExporter();
		// Provides a reference to the interface that will be made remotely callable.
	    exporter.setServiceInterface(serviceInterface);
		// Provides a reference to the object actually executing the method 
	    exporter.setService(implementation);
		//  Set a service name, that allows identifying the exposed service in the RMI registry. 
	    //  In this case it will be EventRepository because takes the class name (EventRepository.class) 
	    exporter.setServiceName(serviceInterface.getSimpleName());
	    
	    return exporter;
	}
	
	@Bean 
	RmiServiceExporter ReservationExporter(ReservationRepository implementation) {
	    Class<ReservationRepository> serviceInterface = ReservationRepository.class;
	    RmiServiceExporter exporter = new RmiServiceExporter();
		// Provides a reference to the interface that will be made remotely callable.
	    exporter.setServiceInterface(serviceInterface);
		// Provides a reference to the object actually executing the method 
	    exporter.setService(implementation);
		//  Set a service name, that allows identifying the exposed service in the RMI registry. 
	    //  In this case it will be ReservationRepository because takes the class name (ReservationRepository.class) 
	    exporter.setServiceName(serviceInterface.getSimpleName());
	    return exporter;
	}

	public static void main(String[] args) {
		
		// RMI's class loader will download classes from remote locations
        // only if a security manager has been set.
		if(System.getSecurityManager()==null) {
            System.setSecurityManager(new SecurityManager());
            System.out.println("New security manager created ");
		}
		System.out.println("Remote Server IP :"+args[0]);
		System.setProperty("java.rmi.server.hostname", args[0]);
		
		SpringApplication.run(RemoteServerRmiApplication.class, args);
	}
}