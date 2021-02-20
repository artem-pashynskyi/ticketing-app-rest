package com.ticketingapp.service;

import com.ticketingapp.dto.RoleDTO;
import com.ticketingapp.exception.TicketingProjectException;

import java.util.List;

public interface RoleService {
    List<RoleDTO> listAllRoles();
    RoleDTO findById(Long id) throws TicketingProjectException;
}
