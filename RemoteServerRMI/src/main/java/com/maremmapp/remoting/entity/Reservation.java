package com.maremmapp.remoting.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Reservation implements Serializable{
	
	private static final long serialVersionUID = 6016154528677657782L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@ManyToOne(
			fetch = FetchType.LAZY,
			cascade = {}
		)
	private User user;
	
	@ManyToOne(
			fetch = FetchType.EAGER,
			cascade = {}
		)
	private Event event;
	
}
