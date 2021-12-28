package com.maremmapp.remoting.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.maremmapp.remoting.entity.Event;

public interface EventRepository extends JpaRepository<Event,Long>{
	
	// Retrieves an event by the given ID
	Event getById(Long id);
	// Retrieves all the events made by a specific event planner 
	Page<Event> findByUser_UserID(Long userID, Pageable pageable);
	// Retrieve all the events and the related eventPlanner. Join Fetch is needed because user is lazy
	@Query("SELECT DISTINCT e FROM Event e JOIN FETCH e.user")
	List<Event> retrieveAll();
	// Insert a new event or update one if it already exists (same id)
	Event save(Event event);
	//Retrieve all the events which date does is not prior to today (customer)
	@Query("SELECT e FROM Event e JOIN FETCH e.user WHERE e.date >= CURRENT_DATE")
	List<Event> retrieveByDate();
	//Delete an Event by the given id
	void deleteById(Event id);
}