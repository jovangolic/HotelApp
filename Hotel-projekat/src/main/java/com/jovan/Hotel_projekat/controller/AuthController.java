package com.jovan.Hotel_projekat.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jovan.Hotel_projekat.dto.UserDTO;
import com.jovan.Hotel_projekat.exceptions.UserAlreadyExistsException;
import com.jovan.Hotel_projekat.model.User;
import com.jovan.Hotel_projekat.request.LoginRequest;
import com.jovan.Hotel_projekat.response.JwtResponse;
import com.jovan.Hotel_projekat.security.jwt.JwtUtils;
import com.jovan.Hotel_projekat.security.user.HotelUserDetails;
import com.jovan.Hotel_projekat.service.IUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	
	
	private final IUserService userService;
	
	private final AuthenticationManager authManager;
	
	private final JwtUtils jwtUtils;
	
	/*@PostMapping("/register-user")
	public ResponseEntity<?> registerUser(@RequestBody User user){
		try {
			userService.registerUser(user);
			return ResponseEntity.ok("Registration successful!");
		}
		catch(UserAlreadyExistsException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}*/
	
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	//metoda za registraciju novog korisnika
	@PostMapping("/register-user")
	public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO){
		logger.info("Registering user with email: {}", userDTO.getEmail());
		try {
            User user = new User();
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setEmail(userDTO.getEmail());
            user.setPassword(userDTO.getPassword());

			userService.registerUser(user);
			logger.info("User with email {} successfully registered", userDTO.getEmail());
			return ResponseEntity.ok("Registration successful!");
		}
		catch(UserAlreadyExistsException e) {
			logger.error("Error registering user: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}
	
	//metoda za logovanje postojeceg korisnika
	@PostMapping("/login")
	public ResponseEntity<?> authenticateUsers(@Valid @RequestBody LoginRequest request){
		Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		//generisanje tokena
		String jwt = jwtUtils.generateJwtTokenForUser(authentication);
		HotelUserDetails userDetails = (HotelUserDetails)authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream().map(
				GrantedAuthority::getAuthority).toList();
		return ResponseEntity.ok(new JwtResponse(
				userDetails.getId(),
				userDetails.getEmail(),
				jwt,
				roles));
	}

}
