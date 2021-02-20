package com.ticketingapp.controller;

import com.ticketingapp.annotation.DefaultExceptionMessage;
import com.ticketingapp.dto.MailDTO;
import com.ticketingapp.dto.UserDTO;
import com.ticketingapp.entity.ConfirmationToken;
import com.ticketingapp.entity.ResponseWrapper;
import com.ticketingapp.entity.User;
import com.ticketingapp.exception.TicketingProjectException;
import com.ticketingapp.util.MapperUtil;
import com.ticketingapp.service.ConfirmationTokenService;
import com.ticketingapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User Controller", description = "User API")
public class UserController {

    @Value("${app.local-url}")
    private String BASE_URL;

    private UserService userService;
    private MapperUtil mapperUtil;
    private ConfirmationTokenService confirmationTokenService;

    public UserController(UserService userService, MapperUtil mapperUtil, ConfirmationTokenService confirmationTokenService) {
        this.userService = userService;
        this.mapperUtil = mapperUtil;
        this.confirmationTokenService = confirmationTokenService;
    }

    @PostMapping("/create-user")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Create user")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<ResponseWrapper> doRegister(@RequestBody UserDTO userDTO) throws TicketingProjectException {
        UserDTO createdUser = userService.save(userDTO);
        sendEmail(createEmail(createdUser));
        return ResponseEntity.ok(new ResponseWrapper("User has been created!",createdUser));
    }

    @GetMapping
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Read All users")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<ResponseWrapper> readAll(){
        return ResponseEntity.ok(new ResponseWrapper("Successfully retrieved users", userService.listAllUsers()));
    }

    @GetMapping("/{username}")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Read by username")
    //TODO Only admin should see other profiles or current user can see his/her profile
    public ResponseEntity<ResponseWrapper> readByUsername(@PathVariable("username") String username){
        return ResponseEntity.ok(new ResponseWrapper("Successfully retrieved user", userService.findByUserName(username)));
    }

    @PutMapping
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Update user")
    public ResponseEntity<ResponseWrapper> updateUser(@RequestBody UserDTO user) {
        return ResponseEntity.ok(new ResponseWrapper("Successfully updated", userService.update(user)));
    }

    @DeleteMapping("/{username}")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Delete user")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<ResponseWrapper> deleteUser(@PathVariable("username") String username) throws TicketingProjectException {
        userService.delete(username);
        return ResponseEntity.ok(new ResponseWrapper("Successfully deleted"));
    }

    @GetMapping("/role")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Read by role")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public ResponseEntity<ResponseWrapper> readByRole(@RequestParam String role){
        return ResponseEntity.ok(new ResponseWrapper("Successfully read users by role", userService.listAllByRole(role)));
    }

    private MailDTO createEmail(UserDTO userDTO){
        User user = mapperUtil.convert(userDTO,new User());
        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        confirmationToken.setIsDeleted(false);
        ConfirmationToken createdConfirmationToken = confirmationTokenService.save(confirmationToken);
        return MailDTO
                .builder()
                .emailTo(user.getUsername())
                .token(createdConfirmationToken.getToken())
                .subject("Confirm Registration")
                .message("To confirm your account, please click here: ")
                .url(BASE_URL + "/confirmation?token=")
                .build();
    }

    private void sendEmail(MailDTO mailDTO){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(mailDTO.getEmailTo());
        mailMessage.setSubject(mailDTO.getSubject());
        mailMessage.setText(mailDTO.getMessage() + mailDTO.getUrl() + mailDTO.getToken() );
        confirmationTokenService.sendEmail(mailMessage);
    }

}
