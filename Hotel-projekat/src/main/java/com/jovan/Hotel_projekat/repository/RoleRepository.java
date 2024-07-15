package com.jovan.Hotel_projekat.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jovan.Hotel_projekat.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

	
	
	Optional<Role> findByName(String role);

	boolean existsByName(Role role);

}