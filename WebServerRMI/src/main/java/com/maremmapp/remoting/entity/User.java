package com.maremmapp.remoting.entity;


import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

//Lombok is used to autogenerate getter and setter methods of the Entity and ToString methods
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User implements Serializable {

	private static final long serialVersionUID = 5922718757264894571L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userID;
    
    private String password;
    
    private String role;

    @Column(nullable = false, unique = true)
    private String username;
    
    // used by the customer
    @OneToMany(
			mappedBy = "user",
			fetch = FetchType.LAZY,
			cascade = {}
		)
    private List<Reservation> reservation;
    
    // used by the event planner
    @OneToMany(
    		mappedBy = "user",
			fetch = FetchType.LAZY,
			cascade = {}
		)
    private List<Event> event;
}
