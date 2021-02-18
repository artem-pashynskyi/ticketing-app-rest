package com.ticketingapp.service;

import com.ticketingapp.entity.ConfirmationToken;
import com.ticketingapp.exception.TicketingProjectException;
import org.springframework.mail.SimpleMailMessage;

public interface ConfirmationTokenService {
    ConfirmationToken save(ConfirmationToken confirmationToken);
    void sendEmail(SimpleMailMessage email);
    ConfirmationToken readByToken(String token) throws TicketingProjectException;
    void delete(ConfirmationToken confirmationToken);
}
