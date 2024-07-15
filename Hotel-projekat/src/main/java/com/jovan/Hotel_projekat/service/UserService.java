package com.jovan.Hotel_projekat.service;

import java.util.Collections;
import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jovan.Hotel_projekat.exceptions.NoSuchElementException;
import com.jovan.Hotel_projekat.exceptions.UserAlreadyExistsException;
import com.jovan.Hotel_projekat.model.Role;
import com.jovan.Hotel_projekat.model.User;
import com.jovan.Hotel_projekat.repository.RoleRepository;
import com.jovan.Hotel_projekat.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
	
	
	private final UserRepository userRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	private final RoleRepository roleRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	/*@Override
	public User registerUser(User user) {
		//da li postoji korisnik
		if (userRepository.existsByEmail(user.getEmail())){
            throw new UserAlreadyExistsException(user.getEmail() + " already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        System.out.println(user.getPassword());
        Role userRole = roleRepository.findByName("ROLE_USER").get();
        user.setRoles(Collections.singletonList(userRole));
        return userRepository.save(user);
	}*/
	
	@Override
	public User registerUser(User user) {
		logger.info("Registering user with email: {}", user.getEmail());
		// Da li postoji korisnik
		if (userRepository.existsByEmail(user.getEmail())){
            logger.warn("User with email {} already exists", user.getEmail());
            throw new UserAlreadyExistsException(user.getEmail() + " already exists");
        }
        
        // Pronađi rolu ili baci izuzetak ako ne postoji
        Role userRole = roleRepository.findByName("ROLE_USER")
            .orElseThrow(() -> {
                logger.error("Role ROLE_USER not found");
                return new NoSuchElementException("Role not found");
            });
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        logger.debug("Encoded password for user with email: {}", user.getEmail());
        user.setRoles(Collections.singletonList(userRole));
        
        User savedUser = userRepository.save(user);
        logger.info("User with email {} successfully registered", user.getEmail());
        return savedUser;
	}

	@Override
	public List<User> getUsers() {
		return userRepository.findAll();
	}

	@Transactional
	@Override
	public void deleteUser(String email) {
		User theUser = getUser(email);
		if(theUser != null) {
			userRepository.deleteByEmail(email);
		}
		
	}

	@Override
	public User getUser(String email) {
		return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}

}
