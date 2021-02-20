package com.ticketingapp.controller;

import com.ticketingapp.annotation.DefaultExceptionMessage;
import com.ticketingapp.dto.TaskDTO;
import com.ticketingapp.entity.ResponseWrapper;
import com.ticketingapp.entity.User;
import com.ticketingapp.enums.Status;
import com.ticketingapp.exception.TicketingProjectException;
import com.ticketingapp.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/task")
@Tag(name = "Task Controller", description = "Task API")
public class TaskController {

    private TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again")
    @Operation(summary = "Read all tasks")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> readAll() {
        return ResponseEntity
                .ok(new ResponseWrapper("Tasks have been successfully retrieved", taskService.listAllTasks()));
    }

    @GetMapping("/project-manager")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Read all tasks by project manager")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> readAllByProjectManager(User user) throws TicketingProjectException {
        return ResponseEntity
                .ok(new ResponseWrapper("Tasks have been successfully retrieved", taskService.listAllTasksByProjectManager()));
    }

    @GetMapping("/{id}")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Read task by id")
    @PreAuthorize("hasAnyAuthority('Manager', 'Employee')")
    public ResponseEntity<ResponseWrapper> readById(@PathVariable("id") Long id) throws TicketingProjectException {
        return ResponseEntity
                .ok(new ResponseWrapper("Task has been successfully retrieved", taskService.findById(id)));
    }

    @PostMapping
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Create task")
    @PreAuthorize("hasAuthority('Employee')")
    public ResponseEntity<ResponseWrapper> create(@RequestBody TaskDTO task) {
        return ResponseEntity
                .ok(new ResponseWrapper("Task has been successfully created", taskService.save(task)));
    }

    @DeleteMapping("/{id}")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Delete task")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> delete(@PathVariable("id") Long id) throws TicketingProjectException {
        return ResponseEntity
                .ok(new ResponseWrapper("Task has been successfully created"));
    }

    @PutMapping
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Update task")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> updateTask(@RequestBody TaskDTO task) throws TicketingProjectException {
        return ResponseEntity
                .ok(new ResponseWrapper("Task has been successfully updated", taskService.update(task)));
    }

    @GetMapping("/employee")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Read all non complete tasks")
    @PreAuthorize("hasAuthority('Employee')")
    public ResponseEntity<ResponseWrapper> employeeReadAllNotCompleteTask(@RequestBody TaskDTO task) throws TicketingProjectException {
        return ResponseEntity
                .ok(new ResponseWrapper("Tasks have been successfully retrieved", taskService.listAllTasksByStatusIsNot(Status.COMPLETE)));
    }

    @PutMapping("/{id}")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Update status")
    @PreAuthorize("hasAuthority('Employee')")
    public ResponseEntity<ResponseWrapper> updateStatus(@RequestBody TaskDTO task) throws TicketingProjectException {
        return ResponseEntity
                .ok(new ResponseWrapper("Status has been successfully updated", taskService.updateStatus(task)));
    }

}
