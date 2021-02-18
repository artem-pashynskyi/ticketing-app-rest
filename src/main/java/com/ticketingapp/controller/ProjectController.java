package com.ticketingapp.controller;

import com.ticketingapp.annotation.DefaultExceptionMessage;
import com.ticketingapp.dto.ProjectDTO;
import com.ticketingapp.entity.ResponseWrapper;
import com.ticketingapp.exception.TicketingProjectException;
import com.ticketingapp.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/project")
@Tag(name = "Project Controller", description = "Project API")
public class ProjectController {

    private ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Read all projects")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public ResponseEntity<ResponseWrapper> readAll() {
        return ResponseEntity
                .ok(new ResponseWrapper("Projects have been successfully retrieved", projectService.listAllProjects()));
    }

    @GetMapping("/{projectcode}")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Read by project code")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public ResponseEntity<ResponseWrapper> readByProjectCode(@PathVariable("projectcode") String projectcode) {
        return ResponseEntity
                .ok(new ResponseWrapper("Project has been successfully retrieved", projectService.getByProjectCode(projectcode)));
    }

    @PostMapping
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Create project")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public ResponseEntity<ResponseWrapper> createProject(@RequestBody ProjectDTO projectDTO) throws TicketingProjectException {
        return ResponseEntity
                .ok(new ResponseWrapper("Project has been successfully created", projectService.save(projectDTO)));
    }

    @PutMapping
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Update project")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public ResponseEntity<ResponseWrapper> updateProject(@RequestBody ProjectDTO projectDTO) throws TicketingProjectException {
        return ResponseEntity
                .ok(new ResponseWrapper("Project has been successfully updated", projectService.update(projectDTO)));
    }

    @DeleteMapping("/{projectcode}")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Delete project")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public ResponseEntity<ResponseWrapper> deleteProject(@PathVariable("projectcode") String projectcode) throws TicketingProjectException {
        projectService.delete(projectcode);
        return ResponseEntity
                .ok(new ResponseWrapper("Project has been successfully deleted"));
    }

    @PutMapping("/complete/{projectcode}")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Complete project")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public ResponseEntity<ResponseWrapper> updateProject(@PathVariable("projectcode") String projectcode) throws TicketingProjectException {
        return ResponseEntity
                .ok(new ResponseWrapper("Project has been successfully completed", projectService.complete(projectcode)));
    }

    @GetMapping("/details")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Read all project details")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> readAllProjectDetails() throws TicketingProjectException {
        return ResponseEntity
                .ok(new ResponseWrapper("Projects have been successfully retrieved", projectService.listAllProjectDetails()));
    }

}





















