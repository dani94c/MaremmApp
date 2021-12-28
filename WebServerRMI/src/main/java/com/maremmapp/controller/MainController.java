package com.maremmapp.controller;

import java.security.Principal;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.maremmapp.remoting.entity.Event;
import com.maremmapp.remoting.entity.Feedback;
import com.maremmapp.remoting.entity.Reservation;
import com.maremmapp.Utils;
import com.maremmapp.remoting.entity.User;
import com.maremmapp.remoting.erlang.ErlangClient;
import com.maremmapp.remoting.repository.EventRepository;
import com.maremmapp.remoting.repository.ReservationRepository;
import com.maremmapp.remoting.repository.UserRepository;
import com.maremmapp.security.UserSecurityDetails;

@Controller
public class MainController {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private ReservationRepository reservationRepository;
	@Autowired
	private ErlangClient erlClient;
	@Autowired
	private Utils utils;
	
	@GetMapping(value={"/", "/login"})
	public String login() {
		return ("login");
	}
	
	@GetMapping("/registration")
	public String registration(Model model) {
		// userForm is used in order to take the user information in the html page and return it
		// in the post method
		model.addAttribute("userForm", new User());
		return ("registration");
	}
	
	@PostMapping("/registration")
	public String registration(@ModelAttribute("userForm") User userForm) {
		// Only customer can register
		userForm.setRole("CUSTOMER");
		try {
			userRepository.save(userForm);
		}catch(DataIntegrityViolationException ex) {
			System.err.println(userForm.getUsername()+" already exists");
			// redirect is used when we want to show another element in the current page. We don't use it when we want to change page.
			// with ?error I pass to the registraion page the paramater error. It is used in order to show an error message
			return "redirect:/registration?error";
		}
		return "login";
	}
	
	//The customer page has a parameter "table type" that will determine which table will be visualized:
	//if the value is "myReservation" the table will include all customer's reservations;
	//if the value is "futureEvents" the table will include all future events
	@GetMapping("/customer")
	public String customer(Model model, Principal principal, @RequestParam(defaultValue = "myReservation") String tableType) {
		// name is used in order to visualize the logged in user in the customer page
		model.addAttribute("name", principal.getName());
		model.addAttribute("tableType", tableType);
		if(tableType.equals("myReservation")) {
			User user = ((UserSecurityDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
			model.addAttribute("data", reservationRepository.retrieveByCustomer(user.getUserID()));
		} else {
			model.addAttribute("data", eventRepository.retrieveByDate());
			
		}
		
		return ("customer");
	}
	
	@GetMapping("/eventPlanner")
	public String eventPlanner(Authentication authentication, Model model,@RequestParam(defaultValue="1") int page) {
		// name is used in order to visualize the logged in user in the eventPlanner page
		model.addAttribute("name", authentication.getName());
		// Pagination is often helpful when we have a large dataset and we want to present it to the user in smaller chunks
		Pageable pageWithFourElements;
		// in the eventPlanner page index page starts from 1, in the PageRequest has to start from 0 so I have to decrement it
		if(page !=0)
			pageWithFourElements = PageRequest.of(page-1,utils.getNumEventsPerPage(),Sort.by("user"));
		else
			pageWithFourElements = PageRequest.of(page,utils.getNumEventsPerPage(),Sort.by("user"));
		// get the logged in user and finds the events created by him
		User user = ((UserSecurityDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
		model.addAttribute("eventsData",eventRepository.findByUser_UserID(user.getUserID(), pageWithFourElements));
		model.addAttribute("currentPage",page);
		return "eventPlanner";
	}
	
	@GetMapping("/admin")
	public String admin(Model model,Principal principal) {
		// name is used in order to visualize the logged in user in the eventPlanner page
		model.addAttribute("name", principal.getName());
		// redirecPage is used in order to redirect to the correct page when an event is added or modified
		model.addAttribute("redirectPage","/admin");
		model.addAttribute("eventsData",eventRepository.retrieveAll());
		return "admin";
	}
	
	//Books the currently logged user to the selected event if there are any available seats 
	//and decreases the available seats number of the selected event
	@GetMapping("/saveReservation")
	public String saveReservation(@ModelAttribute("id") Long id){
		try {
			Event event = eventRepository.getById(id);
			int seats = event.getAvailSeats();
			if(seats == 0)
				return("redirect:/customer?noseats");
			seats-=1;
			event.setAvailSeats(seats);
			event = eventRepository.save(event);
			User user = ((UserSecurityDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
			Reservation reservation = new Reservation();
			reservation.setUser(user);
			reservation.setEvent(event);
			Reservation result = reservationRepository.save(reservation);
			if(result != null && event != null)
				return("redirect:/customer?success");
			return("redirect:/customer?error");
		}catch(ObjectOptimisticLockingFailureException ex) {
			System.err.println("Optimistic locking failed. Row was updated or deleted by another transaction");
			return("redirect:/customer?retryBook");
		}
	}
	
	//Remove the reservation and increases available seats' number
	@GetMapping("/deleteReservation")
	public String deleteReservation(@ModelAttribute("id") Long id){
		try {
			Reservation reservation = reservationRepository.getById(id);
			Event event = reservation.getEvent();
			//Event event = eventRepository.getById(reservation.getEvent().getId());
			int seats = event.getAvailSeats();
			seats+=1;
			event.setAvailSeats(seats);
			eventRepository.save(event);
			reservationRepository.deleteById(id);
			return("redirect:/customer?success");
		}catch(ObjectOptimisticLockingFailureException ex) {
			System.err.println("Optimistic locking failed. Row was updated or deleted by another transaction");
			return("redirect:/customer?retryCancel");
		}
	}
	
	// called from eventPlanner.html or from admin.html when they add a new event
	@PostMapping("saveEvent")
	public String saveEvent(Authentication authentication, Event event) {
		Event eventSaved;
		//saveEvent is called from the admin
		if(authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
			// check if available seats and seats have valid values
			if(event.getAvailSeats() > event.getSeats() || event.getAvailSeats() < 0 || 
					event.getSeats() < 0 || event.getAvailSeats() < 0)
				return "redirect:/admin?badseats";
			// check if the user inserted from the admin exists
			if(userRepository.existsById(event.getUser().getUserID())) {
				// check if the action required is a new event or an update
				if(event.getId() != null) {
					// update
					Event tmp = eventRepository.getById(event.getId());
					tmp.setUser(event.getUser());
					tmp.setAvailSeats(event.getAvailSeats());
					tmp.setDate(event.getDate());
					tmp.setDescription(event.getDescription());
					tmp.setSeats(event.getSeats());
					tmp.setTitle(event.getTitle());
					eventSaved = eventRepository.save(tmp);
				} else {
					// new event
					eventSaved = eventRepository.save(event);
				}
				if(eventSaved != null) 
					return "redirect:/admin?success";
				else
					return "redirect:/admin?error";
			}
			return "redirect:/admin?badid";
		}
		//saveEvent is called from the eventPlanner
		else if(authentication.getAuthorities().contains(new SimpleGrantedAuthority("EVENTPLANNER"))){
			if(event.getAvailSeats() > event.getSeats() || event.getAvailSeats() < 0|| event.getSeats() < 0)
				return "redirect:/eventPlanner?badseats";
			User us = ((UserSecurityDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
			// check if the action required is a new event or an update
			if(event.getId() != null) {
				// update
				Event tmp = eventRepository.getById(event.getId());
				tmp.setUser(us);
				tmp.setAvailSeats(event.getAvailSeats());
				tmp.setDate(event.getDate());
				tmp.setDescription(event.getDescription());
				tmp.setSeats(event.getSeats());
				tmp.setTitle(event.getTitle());
				eventSaved = eventRepository.save(tmp);
			} else {
				// new event
				event.setUser(us);
				eventSaved = eventRepository.save(event);
			}
			if(eventSaved == null) 
				return "redirect:/eventPlanner?error";
			return "redirect:/eventPlanner?success";
		}
		return "error";
	}
	
	// called from eventPlanner or admin when an event is deleted
	@GetMapping("deleteEvent")
	public String deleteEvent(Authentication authentication, @ModelAttribute("id") Long id) {
		eventRepository.deleteById(id);
		// redirect to the admin or eventplanner page according to the logged in user's authority
		if(authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
			return "redirect:/admin?success";
		}
		else if(authentication.getAuthorities().contains(new SimpleGrantedAuthority("EVENTPLANNER"))){
			// saveEvent called from eventPlanner
			return "redirect:/eventPlanner?success";
		}
		return "redirect:/eventPlanner?error";
	}
	
	//Called from customer.html, eventPlanner.html or admin.html when they add a new feedback
	@PostMapping("/insertFeedback")
	// Principal is the currently logged in user. We retrieved it through the security context which is bound to the current thread 
	// and as such it's also bound to the current request and its session
	public String insertFeedback(Principal principal, Feedback feedbackForm) {
		System.out.println("Feedback retrieved from "+principal.getName() + " page: "+feedbackForm.getMessage());
		feedbackForm.setOwner(principal.getName());
		if(erlClient.insertFeedback(feedbackForm))
			return "redirect:/feedback?success";
		return "redirect:/feedback?error";
	}
	
	//Called from customer.html, eventPlanner.html or admin.html in order to show the feedbacks page
	@GetMapping("/feedback")
	public String feedback(Authentication auth, Model model, Principal principal, @RequestParam(required = false) String tableType) {
		// name is used in order to visualize the logged in user in the Feedbacks page
		model.addAttribute("name", principal.getName());
		// tableType is used to distinguish between all feedbacks or personal feedbacks requests
		model.addAttribute("tableType", tableType);
		// get the logged in user and retrieves his role
		User user = ((UserSecurityDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
		model.addAttribute("role", user.getRole());
		if(tableType != null)
			//feedbacksList is used to visualize all the retrieved feedbacks written by the logged user
			model.addAttribute("feedbacksList", erlClient.getUserFeedbacks(user.getUsername()));
		else
			//feedbacksList is used to visualize all the retrieved feedbacks stored in the db
			model.addAttribute("feedbacksList", erlClient.getAllFeedbacks());
		return "feedback";
	}
	
	// called by admin or from the personal feedback page when a feedback is deleted
	@GetMapping("deleteFeedback")
	public String deleteFeedback(Authentication authentication, @ModelAttribute("tableType") String tableType, @ModelAttribute("id") Long id) {
		// check if the delete operation is allowed (it's possible only for an admin of from the personal feedback page)
		if(authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN")) || 
				(tableType!=null && tableType.equals("personal"))) {
			//check the correctness of the operation and redirect with the proper parameter
			if(erlClient.deleteFeedback(id)) {
				System.out.println("Ok");
				return "redirect:/feedback?success";
			}
			else {
				System.out.println("Error");
				return "redirect:/feedback?error";
			}
		}
		//If user is not allowed to delete a feedback (not an admin), redirect to the homepage
		return "error";
	}
	
	
	//CONCURRENCY TEST
	
	//Remove the reservation and decreases available seats' number
	@GetMapping("/saveReservationConc")
	public ResponseEntity saveReservationConc(@RequestParam Long eventId,@RequestParam String userName, int threadId){
		try {
			System.out.println("Thread "+threadId+" is trying to save reservation for user: "+ userName
					+ " on the event: "+eventId);
			Event event = eventRepository.getById(eventId);
			System.out.println("[Thread "+threadId+" read]: [Event "+event.getId()+ "] Available seats before save reservation " +event.getAvailSeats());
			int seats = event.getAvailSeats();
			if(seats == 0) {
				System.out.println("Seats = 0");
				return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			seats-=1;
			event.setAvailSeats(seats);
			event = eventRepository.save(event);
			Reservation reservation = new Reservation();
			User user = userRepository.findByUsername(userName);
			reservation.setUser(user);
			reservation.setEvent(event);
			Reservation result = reservationRepository.save(reservation);
			if(result != null && event != null) {
				System.out.println("[Thread " +threadId+ " write]: [Event " + eventId
						+" ] Available seats after save reservation " +event.getAvailSeats());
				return new ResponseEntity(HttpStatus.OK);
			}
			System.out.println("oops something went wrong. Avail seats="+seats);
			//Return Response Code 400
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}catch(ObjectOptimisticLockingFailureException ex) {
			System.err.println("[Thread " +threadId+ " write]: Optimistic locking failed. Row was updated or deleted by another transaction");
			//Return Response Code 500
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//Remove the reservation and increases available seats' number
	@GetMapping("/deleteReservationConc")
	public ResponseEntity deleteReservationConc(@RequestParam Long reservId){
		try {	
			System.out.println("Thread "+reservId+" is trying to delete Reservation "+reservId);
			Reservation reservation = reservationRepository.getById(reservId);
			Event event = reservation.getEvent();
			System.out.println("[Thread "+reservId+" read]: [Event "+event.getId()+ "] Before delete reservation " +event.getAvailSeats());
			int seats = event.getAvailSeats();
			seats+=1;
			event.setAvailSeats(seats);
			Event tmp = eventRepository.save(event);
			System.out.println("[Thread "+reservId+" write]: [Event "+event.getId()+"] after delete Reservation "+tmp.getAvailSeats());
			reservationRepository.deleteById(reservId);
			
			return new ResponseEntity(HttpStatus.OK);
		}catch(ObjectOptimisticLockingFailureException ex) {
			System.err.println("[Thread " +reservId+ " write]: Optimistic locking failed. Row was updated or deleted by another transaction");
			//Return Response Code 500
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
