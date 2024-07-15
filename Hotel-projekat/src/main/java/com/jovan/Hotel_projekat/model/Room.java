package com.jovan.Hotel_projekat.model;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@AllArgsConstructor
@Entity
public class Room {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String roomType;
	
	private BigDecimal RoomPrice;
	
	private boolean isBooked = false;
	
	@Lob
	private Blob photo;
	
	@OneToMany(mappedBy = "room" ,fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<BookedRoom> bookings;

	public Room() {
		this.bookings = new ArrayList<>();
	}


	public void addBooking(BookedRoom booking) {
		if(bookings == null) {
			bookings = new ArrayList<>();
		}
		bookings.add(booking);
		booking.setRoom(this);
		//soba postaje rezervisana
		isBooked = true;
		//generisanje koda za potvrdu, od desest cifara
		String bookingCode = RandomStringUtils.randomNumeric(10);
		//setovanje koda za potvrdu
		booking.setBookingConfirmationCode(bookingCode);
	}
	
	
	
}
