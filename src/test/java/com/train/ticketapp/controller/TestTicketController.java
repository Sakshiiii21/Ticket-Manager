package com.train.ticketapp.controller;

import com.train.ticketapp.model.Ticket;
import com.train.ticketapp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TicketControllerTest {

    private TicketController ticketController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ticketController = new TicketController();
        mockMvc = MockMvcBuilders.standaloneSetup(ticketController).build();
    }

    @Test
    void purchaseTicket() throws Exception {
        mockMvc.perform(post("/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"from\":\"Pune\",\"to\":\"Banglore\",\"user\":{\"userFirstName\":\"Sakshi\",\"userLastName\":\"Selokar\",\"userEmail\":\"sakshi@gmail.com\"},\"price\":50.0,\"section\":\"A\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.from", is("Pune")))
                .andExpect(jsonPath("$.to", is("Banglore")))
                .andExpect(jsonPath("$.user.userFirstName", is("Sakshi")))
                .andExpect(jsonPath("$.user.userLastName", is("Selokar")))
                .andExpect(jsonPath("$.user.userEmail", is("sakshi@gmail.com")))
                .andExpect(jsonPath("$.price", is(50.0)))
                .andExpect(jsonPath("$.section", is("A")));
    }

    @Test
    void getReceiptDetails() throws Exception {
       Ticket ticket = new Ticket("Pune", "Banglore", new User("Sakshi", "Selokar", "sakshi@gmail.com"), 50.0, "A");
       Ticket.setNextSeatNumber(1);

        ticketController.purchaseTicket(ticket);
        
        mockMvc.perform(get("/receipt/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.from", is("Pune")))
                .andExpect(jsonPath("$.to", is("Banglore")))
                .andExpect(jsonPath("$.user.userFirstName", is("Sakshi")))
                .andExpect(jsonPath("$.user.userLastName", is("Selokar")))
                .andExpect(jsonPath("$.user.userEmail", is("sakshi@gmail.com")))
                .andExpect(jsonPath("$.price", is(50.0)))
                .andExpect(jsonPath("$.section", is("A")))
                .andExpect(jsonPath("$.seatNumber", is(1)));

    }

    @Test
    void getUsersAndSeats() throws Exception {
        Ticket ticket1 = new Ticket("From", "To", new User("John", "Doe", "john.doe@example.com"), 50.0, "A");
        Ticket ticket2 = new Ticket("From", "To", new User("Jane", "Doe", "jane.doe@example.com"), 50.0, "A");
        ticketController.purchaseTicket(ticket1);
        ticketController.purchaseTicket(ticket2);

        mockMvc.perform(get("/users/A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user.userFirstName", is("John")))
                .andExpect(jsonPath("$[1].user.userFirstName", is("Jane")));
    }

    @Test
    void removeUser() throws Exception {
        Ticket ticket = new Ticket("From", "To", new User("John", "Doe", "john.doe@example.com"), 50.0, "A");
        ticket.setSeatNumber(1);
        ticketController.purchaseTicket(ticket);

        mockMvc.perform(delete("/remove/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("User having seat number: 1 deleted successfully"));

        // Verify that the user has been removed
        mockMvc.perform(get("/receipt/{seatNumber}", 1))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void modifyUserSeat() throws Exception {
        Ticket ticket = new Ticket("From", "To", new User("John", "Doe", "john.doe@example.com"), 50.0, "A");
        ticket.setSeatNumber(1);
        ticketController.purchaseTicket(ticket);

        mockMvc.perform(put("/modifySeat/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("User having seatNumber: 1 has been assigned to section B"));
    }

}
