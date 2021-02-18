package com.ticketingapp.service;

import com.ticketingapp.dto.UserDTO;
import com.ticketingapp.entity.User;
import com.ticketingapp.exception.TicketingProjectException;

import java.util.List;

public interface UserService {
    List<UserDTO> listAllUsers();
    UserDTO findByUserName(String username);
    UserDTO save(UserDTO dto) throws TicketingProjectException;
    UserDTO update(UserDTO dto);
    void delete(String username) throws TicketingProjectException;
    void deleteByUserName(String username);
    List<UserDTO> listAllByRole(String role);
    Boolean checkIfUserCanBeDeleted(User user);
    UserDTO confirm(User user);
}