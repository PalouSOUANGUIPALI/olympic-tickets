package com.infoevent.olympictickets.controller;

import com.infoevent.olympictickets.dto.TicketDto;
import com.infoevent.olympictickets.service.TicketService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@AllArgsConstructor
@RestController
@RequestMapping(value = "/tickets")
public class TicketController {

    private TicketService ticketService;


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/reserve")
    public ResponseEntity<TicketDto> reserveTicket(@RequestBody TicketDto ticketDto) {
        TicketDto savedTicketDto = ticketService.reserveTicket(ticketDto);
        return ResponseEntity.ok(savedTicketDto);
    }

    /*@GetMapping("/{id}")
    public ResponseEntity<TicketDto> getTicketById(@PathVariable Long id) {
        TicketDto ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TicketDto>> getUserTickets(@PathVariable Integer userId) {
        List<TicketDto> tickets = ticketService.getUserTickets(userId);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping
    public ResponseEntity<List<TicketDto>> getAllTickets() {
        List<TicketDto> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelTicket(@PathVariable Long id) {
        ticketService.cancelTicket(id);
        return ResponseEntity.noContent().build();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/confirm/{ticketId}")
    public ResponseEntity<TicketDto> confirmTicketPurchase(@PathVariable Long ticketId) {
        TicketDto confirmedTicket = ticketService.confirmTicketPurchase(ticketId);
        return ResponseEntity.ok(confirmedTicket);
    }

     */
}
