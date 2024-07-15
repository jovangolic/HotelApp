package com.jovan.Hotel_projekat.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jovan.Hotel_projekat.exceptions.InvalidBookingRequestException;
import com.jovan.Hotel_projekat.exceptions.ResourceNotFoundException;
import com.jovan.Hotel_projekat.model.BookedRoom;
import com.jovan.Hotel_projekat.model.Room;
import com.jovan.Hotel_projekat.response.BookingResponse;
import com.jovan.Hotel_projekat.response.RoomResponse;
import com.jovan.Hotel_projekat.service.IBookingService;
import com.jovan.Hotel_projekat.service.IRoomService;

import lombok.RequiredArgsConstructor;

@CrossOrigin("http://localhost:5173")
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {
	
	
	private final IBookingService bookingService;
	private final IRoomService roomService;
	
	
	
	@GetMapping("/all-bookings")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<List<BookingResponse>> getAllBookings(){
		List<BookedRoom> bookings = bookingService.getAllBookings();
		List<BookingResponse> bookingResponses = new ArrayList<>();
		for(BookedRoom booking : bookings) {
			BookingResponse bookingResponse = getBookingResponse(booking);
			bookingResponses.add(bookingResponse);
		}
		return ResponseEntity.ok(bookingResponses);
	}
	
	//vraca bilo sta. To je ovaj znak ?
	@GetMapping("/confirmation/{confirmationCode}")
	public ResponseEntity<?> getBookingByConfirmationCode(@PathVariable String confirmationCode){
		try {
			BookedRoom booking = bookingService.findByBookingConfirmationCode(confirmationCode);
			BookingResponse bookingResponse = getBookingResponse(booking);
			return ResponseEntity.ok(bookingResponse);
		}
		catch(ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}
	
	

	//metoda za rezervaciju sobe
	@PostMapping("/room/{roomId}/booking")
	public ResponseEntity<?> saveBooking(@PathVariable Long roomId,
			@RequestBody BookedRoom bookingRequest){
		try {
			//ako je soba usesno bukirana
			String confirmationCode = bookingService.saveBooking(roomId, bookingRequest);
			return ResponseEntity.ok("Room booked successfully!! Your booking confirmation code is: "
					+ confirmationCode);
		}
		catch(InvalidBookingRequestException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	
	@DeleteMapping("/booking/{bookingId}/delete")
	//@PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') and #email == principal.username)")
	public void cancelBooking(@PathVariable Long bookingId) {
		bookingService.cancelBooking(bookingId);
	}
	
	
	@GetMapping("/user/{email}/bookings")
	public ResponseEntity<List<BookingResponse>> getBookingsByUserEmail(@PathVariable String email){
		List<BookedRoom> bookings = bookingService.getBookingsByUserEmail(email);
		List<BookingResponse> bookingResponses = new ArrayList<>();
		for(BookedRoom booking : bookings) {
			BookingResponse bookingResponse = getBookingResponse(booking);
			bookingResponses.add(bookingResponse);
		}
		return ResponseEntity.ok(bookingResponses);
	}
	
	//metoda koja vraca DTO objekat
	private BookingResponse getBookingResponse(BookedRoom booking) {
		//trazi sobu i vraca optional
		Room theRoom = roomService.getRoomById(booking.getRoom().getId()).get();
		//kreira se dto objekat
		RoomResponse room = new RoomResponse(theRoom.getId(), theRoom.getRoomType(), theRoom.getRoomPrice());
		//vraca dto objekat
		return new BookingResponse(booking.getBookingId(), booking.getCheckInDate(), 
				booking.getCheckOutDate(), booking.getGuestFullName(), booking.getGuestEmail(),
				booking.getNumOfAdults(), booking.getNumOfChildren(), booking.getTotalNumOfGuests(),
				booking.getBookingConfirmationCode(), room);
	}

}
