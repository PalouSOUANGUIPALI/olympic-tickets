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

}
