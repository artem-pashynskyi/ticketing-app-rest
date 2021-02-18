package com.ticketingapp.controller;

import com.ticketingapp.annotation.DefaultExceptionMessage;
import com.ticketingapp.dto.MailDTO;
import com.ticketingapp.dto.UserDTO;
import com.ticketingapp.entity.ConfirmationToken;
import com.ticketingapp.entity.ResponseWrapper;
import com.ticketingapp.entity.User;
import com.ticketingapp.entity.common.AuthenticationRequest;
import com.ticketingapp.exception.TicketingProjectException;
import com.ticketingapp.mapper.MapperUtil;
import com.ticketingapp.service.ConfirmationTokenService;
import com.ticketingapp.service.UserService;
import com.ticketingapp.util.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Authentication Controller",description = "Authenticate API")
public class LoginController {

	private AuthenticationManager authenticationManager;
	private UserService userService;
	private MapperUtil mapperUtil;
	private JWTUtil jwtUtil;
	private ConfirmationTokenService confirmationTokenService;

	public LoginController(AuthenticationManager authenticationManager, UserService userService, MapperUtil mapperUtil, JWTUtil jwtUtil, ConfirmationTokenService confirmationTokenService) {
		this.authenticationManager = authenticationManager;
		this.userService = userService;
		this.mapperUtil = mapperUtil;
		this.jwtUtil = jwtUtil;
		this.confirmationTokenService = confirmationTokenService;
	}

	@PostMapping("/authenticate")
	@DefaultExceptionMessage(defaultMessage = "Bad Credentials")
	@Operation(summary = "Login to application")
	public ResponseEntity<ResponseWrapper> doLogin(@RequestBody AuthenticationRequest authenticationRequest) throws TicketingProjectException {
		String password = authenticationRequest.getPassword();
		String username = authenticationRequest.getUsername();
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username,password);
		authenticationManager.authenticate(authentication);
		UserDTO foundUser = userService.findByUserName(username);
		User convertedUser = mapperUtil.convert(foundUser,new User());
		if(!foundUser.isEnabled()) throw new TicketingProjectException("Please verify your user");
		String jwtToken = jwtUtil.generateToken(convertedUser);
		return ResponseEntity.ok(new ResponseWrapper("Login Successful", jwtToken));
	}

	@DefaultExceptionMessage(defaultMessage = "Failed to confirm email, please try again!")
	@GetMapping("/confirmation")
	@Operation(summary = "Confirm account")
	public ResponseEntity<ResponseWrapper> confirmEmail(@RequestParam("token") String token) throws TicketingProjectException {
		ConfirmationToken confirmationToken = confirmationTokenService.readByToken(token);
		UserDTO confirmUser = userService.confirm(confirmationToken.getUser());
		confirmationTokenService.delete(confirmationToken);
		return ResponseEntity.ok(new ResponseWrapper("User has been confirmed!", confirmUser));
	}

}
