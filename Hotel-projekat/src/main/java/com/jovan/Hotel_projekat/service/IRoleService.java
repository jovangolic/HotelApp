package com.jovan.Hotel_projekat.service;

import java.util.List;

import com.jovan.Hotel_projekat.model.Role;
import com.jovan.Hotel_projekat.model.User;



public interface IRoleService {

	
	List<Role> getRoles();
	Role createRole(Role theRole);
	void deleteRole(Long id);
	Role findByName(String name);
	User removeUserFromRole(Long userId, Long roleId);
	
	//dodeljivanje role korisniku
	User assignRoleToUser(Long userId, Long roleId);
	
	Role removeAllUsersFromRole(Long roleId);
	
}
