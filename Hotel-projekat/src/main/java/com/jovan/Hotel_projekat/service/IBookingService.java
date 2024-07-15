package com.jovan.Hotel_projekat.service;

import java.util.List;

import com.jovan.Hotel_projekat.model.BookedRoom;

public interface IBookingService {

	void cancelBooking(Long bookingId);

	String saveBooking(Long roomId, BookedRoom bookingRequest);

	BookedRoom findByBookingConfirmationCode(String confirmationCode);

	List<BookedRoom> getAllBookings();

	List<BookedRoom> getBookingsByUserEmail(String email);
	
	List<BookedRoom> getAllBookingsByRoomId(Long roomId);

}
