package com.jovan.Hotel_projekat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDTO {

	private String firstName;
    private String lastName;
    private String email;
    private String password;
	
}
