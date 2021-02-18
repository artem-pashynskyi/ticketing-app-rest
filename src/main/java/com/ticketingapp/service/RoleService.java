package com.ticketingapp.service;

import com.ticketingapp.dto.RoleDTO;

import java.util.List;

public interface RoleService {
    List<RoleDTO> listAllRoles();
    RoleDTO findById(Long id);
}
