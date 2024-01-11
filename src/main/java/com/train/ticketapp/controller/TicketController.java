package com.train.ticketapp.controller;

import org.springframework.web.bind.annotation.*;

import com.train.ticketapp.model.Ticket;
import com.train.ticketapp.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TicketController {
	private List<Ticket> tickets = new ArrayList<>();

	// API to purchase a ticket
	@PostMapping("/purchase")
	public Ticket purchaseTicket(@RequestBody Ticket ticket) {
		User user = new User(ticket.getUser().getUserFirstName(), ticket.getUser().getUserLastName(), ticket.getUser().getUserEmail());
        Ticket newTicket = new Ticket(ticket.getFrom(), ticket.getTo(), user, ticket.getPrice(), "A");
        tickets.add(newTicket);
        return newTicket;
	}
	
	 // API to get receipt details for a user
	@GetMapping("/receipt/{seatNumber}")
	public Ticket getReceiptDetails(@PathVariable int seatNumber) {
		return tickets.stream().filter(ticket -> ticket.getSeatNumber() == seatNumber).findFirst().orElse(null);
	}

	// API to get users and seats for a section
	@GetMapping("/users/{section}")
	public List<Ticket> getUsersAndSeats(@PathVariable String section) {
		return tickets.stream().filter(ticket -> ticket.getSection().equalsIgnoreCase(section))
				.collect(Collectors.toList());
	}
	
	// API to remove a user from the train
	@DeleteMapping("/remove/{seatNumber}")
	public String removeUser(@PathVariable int seatNumber) {
		tickets.removeIf(ticket -> ticket.getSeatNumber() == seatNumber);
		return "User having seat number: " + seatNumber +" deleted successfully";
	}
	
	// API to modify a user's seat (Modifying section of user)
	@PutMapping("/modifySeat/{seatNumber}")
	public String modifyUserSeat(@PathVariable int seatNumber) {
		Ticket ticket = tickets.stream().filter(t -> t.getSeatNumber() == seatNumber).findFirst().orElse(null);

		if (ticket != null) {
			ticket.setSection("B");
		}

		return "User having seatNumber: " + seatNumber +" has been assigned to section B";
	}
}
