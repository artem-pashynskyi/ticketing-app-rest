package com.ticketingapp.service;

import com.ticketingapp.dto.ProjectDTO;
import com.ticketingapp.dto.TaskDTO;
import com.ticketingapp.entity.Task;
import com.ticketingapp.entity.User;
import com.ticketingapp.enums.Status;
import com.ticketingapp.exception.TicketingProjectException;

import java.util.List;

public interface TaskService {
    TaskDTO findById(Long id) throws TicketingProjectException;
    List<TaskDTO> listAllTasks();
    Task save(TaskDTO dto);
    TaskDTO update(TaskDTO dto) throws TicketingProjectException;
    void delete(long id) throws TicketingProjectException;
    int totalNonCompletedTasks(String projectCode);
    int totalCompletedTasks(String projectCode);
    void deleteByProject(ProjectDTO project);
    List<TaskDTO> listAllByProject(ProjectDTO project);
    List<TaskDTO> listAllTasksByStatusIsNot(Status status) throws TicketingProjectException;
    List<TaskDTO> listAllTasksByProjectManager() throws TicketingProjectException;
    TaskDTO updateStatus(TaskDTO dto) throws TicketingProjectException;
    List<TaskDTO> listAllTasksByStatus(Status status);
    List<TaskDTO> readAllByEmployee(User assignedEmployee);
}
