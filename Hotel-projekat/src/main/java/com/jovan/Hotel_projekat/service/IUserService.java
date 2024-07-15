package com.jovan.Hotel_projekat.service;

import java.util.List;

import com.jovan.Hotel_projekat.model.User;

public interface IUserService {

	
	User registerUser(User user);
	List<User> getUsers();
	void deleteUser(String email);
	User getUser(String email);
}
