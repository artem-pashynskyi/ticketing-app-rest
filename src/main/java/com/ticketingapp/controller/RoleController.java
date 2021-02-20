package com.ticketingapp.controller;

import com.ticketingapp.annotation.DefaultExceptionMessage;
import com.ticketingapp.entity.ResponseWrapper;
import com.ticketingapp.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/role")
@Tag(name = "Role Controller", description = "Role API")
public class RoleController {

    private RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again")
    @Operation(summary = "Read all roles")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<ResponseWrapper> readAll() {
        return ResponseEntity
                .ok(new ResponseWrapper("Roles have been successfully retrieved", roleService.listAllRoles()));
    }
}
