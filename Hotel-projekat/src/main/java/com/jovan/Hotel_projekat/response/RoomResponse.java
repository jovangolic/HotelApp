package com.jovan.Hotel_projekat.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Base64;



//ovo je DTO objekat

@Data
@NoArgsConstructor
public class RoomResponse {

	private Long id;
	
	private String roomType; 
	
	private BigDecimal roomPrice;
	
	private boolean isBooked;
	
	private String photo;
	
	private List<BookingResponse> bookings;

	public RoomResponse(Long id, String roomType, BigDecimal roomPrice) {
		this.id = id;
		this.roomType = roomType;
		this.roomPrice = roomPrice;
	}

	public RoomResponse(Long id, String roomType, BigDecimal roomPrice, boolean isBooked, byte[] photoBytes
			) {
		this.id = id;
		this.roomType = roomType;
		this.roomPrice = roomPrice;
		this.isBooked = isBooked;
		this.photo = photoBytes != null ? Base64.getEncoder().encodeToString(photoBytes) : null;
		//this.bookings = bookings;
	}
}
