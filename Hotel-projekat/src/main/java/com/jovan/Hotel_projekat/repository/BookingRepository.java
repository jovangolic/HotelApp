package com.jovan.Hotel_projekat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jovan.Hotel_projekat.model.BookedRoom;

public interface BookingRepository extends JpaRepository<BookedRoom, Long> {
	
	
	Optional<BookedRoom> findByBookingConfirmationCode(String confirmationCode);

	List<BookedRoom> findByRoomId(Long roomId);
	
	List<BookedRoom> findByGuestEmail(String email);

}
