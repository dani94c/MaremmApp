package com.maremmapp.remoting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.maremmapp.remoting.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation,Long>{

	//Retrieves a reservation by the given ID
	Reservation getById(Long id);
	// Retrieves all the reservations made by a specific customer
	@Query("SELECT r FROM Reservation r JOIN FETCH r.event e JOIN FETCH e.user WHERE r.user.userID = :userid")
	List<Reservation> retrieveByCustomer(@Param("userid") Long userid);
	// Insert a new reservation 
	Reservation save(Reservation reservation);
	// Delete a reservation
	void deleteById(Reservation id);
}