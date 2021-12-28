package com.maremmapp.remoting.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Event implements Serializable{

	private static final long serialVersionUID = -659853535588856597L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	private String title; 
	private String description;
	// By default Spring doesen't recognize the date format. This tag is used in order to allow it to 
	// recognize the date when it has to be converted in the html page from string to date
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;
	private Integer seats;
	private Integer availSeats;
	@Column(nullable = false)
	@Version
	private Long version;
	
	@ManyToOne (
		fetch = FetchType.LAZY,
				cascade= {}
	)
	private User user;
	
	@OneToMany(
			mappedBy = "event",
			fetch = FetchType.LAZY,
			cascade = CascadeType.REMOVE
		)
    private List<Reservation> reservation;
}