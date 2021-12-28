package com.maremmapp;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ConstructorBinding
@ConfigurationProperties(prefix="utils")
public class Utils {
	//RemoteServer IP and Port
	public String remoteServerIP;
	public String remoteServerPort;
	//parameters for Erlang Client
	public String cookie;
	public String serverNodeName;
    public String serverRegisteredName;
    public String clientNodeName; 
    public String mBoxName;
    // parameters for WebServer Controller
    public int numEventsPerPage;
    
    public Utils(String remoteServerIP, String remoteServerPort, String cookie, String serverNodeName, String serverRegisteredName, 
    		String clientNodeName, String mBoxName, int numEventsPerPage) {
    	this.remoteServerIP=remoteServerIP;
    	this.remoteServerPort=remoteServerPort;
    	this.cookie=cookie;
    	this.serverNodeName=serverNodeName;
    	this.serverRegisteredName=serverRegisteredName;
    	this.clientNodeName=clientNodeName;
    	this.mBoxName=mBoxName;
    	this.numEventsPerPage=numEventsPerPage;
	}
    	
}
