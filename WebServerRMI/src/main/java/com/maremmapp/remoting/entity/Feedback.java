package com.maremmapp.remoting.entity;

import java.time.LocalDate;
//Lombok is used to autogenerate getter and setter methods of the Entity and ToString methods
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Feedback {

	private Long id;
	private String owner;
	private String message;
	private LocalDate date;
	
}
