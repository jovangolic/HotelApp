package com.jovan.Hotel_projekat.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import java.util.Base64;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jovan.Hotel_projekat.exceptions.PhotoRetrievalException;
import com.jovan.Hotel_projekat.exceptions.ResourceNotFoundException;
import com.jovan.Hotel_projekat.model.BookedRoom;
import com.jovan.Hotel_projekat.model.Room;
import com.jovan.Hotel_projekat.response.BookingResponse;
import com.jovan.Hotel_projekat.response.RoomResponse;
import com.jovan.Hotel_projekat.service.BookingService;
import com.jovan.Hotel_projekat.service.IRoomService;

import lombok.RequiredArgsConstructor;

@CrossOrigin("http://localhost:5173")
@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

	
	private final IRoomService roomService;
	private final BookingService bookingService;
	
	@PostMapping("/add/new-room")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<RoomResponse> addNewRoom(@RequestParam("photo") MultipartFile photo, 
			@RequestParam("roomType") String roomType, 
			@RequestParam("roomPrice") BigDecimal roomPrice) throws SQLException, IOException{
		
		Room savedRoom = roomService.addNewRoom(photo, roomType, roomPrice);
		//pretvaranje u dto objekat
		RoomResponse response = new RoomResponse(savedRoom.getId(), savedRoom.getRoomType(), savedRoom.getRoomPrice());
		//vracamo dto objekat
		return ResponseEntity.ok(response);
				
	}
	
	@GetMapping("/room/types")
	public List<String> getRoomTypes(){
		return roomService.getAllRoomTypes();
	}
	
	@GetMapping("/all-rooms")
	public ResponseEntity<List<RoomResponse>> getAllRooms() throws SQLException{
		List<Room> rooms = roomService.getAllRooms();
		//prazan dto
		List<RoomResponse> roomResponses = new ArrayList<>();
		for(Room r : rooms){
			//za svaku sobu prikazujemo sliku
			byte[] photoBytes = roomService.getRoomPhotoByRoomId(r.getId());
			if(photoBytes != null && photoBytes.length > 0 ) {
				String base64Photo = Base64.getEncoder().encodeToString(photoBytes);
				RoomResponse roomResponse = getRoomResponse(r);
				roomResponse.setPhoto(base64Photo);
				roomResponses.add(roomResponse);
			}
		}
		return ResponseEntity.ok(roomResponses);
	}
	
	
	@DeleteMapping("/delete/room/{roomId}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<Void> deleteRoom(@PathVariable Long roomId){
		roomService.deleteRoom(roomId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@PutMapping("/update/{roomId}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<RoomResponse> updateRoom(@PathVariable Long roomId,@RequestParam(required = false) String roomType,
			@RequestParam(required = false) BigDecimal roomPrice, @RequestParam(required = false) MultipartFile photo)
					throws SQLException, IOException{
		//provera, da li soba sadrzi sliku
		byte[] photoBytes = photo != null && !photo.isEmpty() ? photo.getBytes() : roomService.getRoomPhotoByRoomId(roomId);
		//konvertovanje photoBytes u Blob
		Blob photoBlob = photoBytes != null && photoBytes.length > 0 ? new SerialBlob(photoBytes) : null;
		Room theRoom = roomService.updateRoom(roomId, roomType, roomPrice, photoBytes);
		theRoom.setPhoto(photoBlob);
		RoomResponse roomResponse = getRoomResponse(theRoom);
		return ResponseEntity.ok(roomResponse);
	}
	
	@GetMapping("/room/{roomId}")
	public ResponseEntity<Optional<RoomResponse>> getRoomById(@PathVariable Long roomId){
		Optional<Room> theRoom = roomService.getRoomById(roomId);
		//pretvaranje theRoom optional u dto(RoomResponse)
		return theRoom.map(room -> {
            RoomResponse roomResponse = getRoomResponse(room);
            return  ResponseEntity.ok(Optional.of(roomResponse));
        }).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
	}
	
	
	@GetMapping("/available-rooms")
	public ResponseEntity<List<RoomResponse>> getAvailableRooms(
			@RequestParam("checkInDate") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate checkInDate, 
			@RequestParam("checkOutDate") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
			@RequestParam("roomType") String roomType) throws SQLException{
		List<Room> availableRooms = roomService.getAvailableRooms(checkInDate, checkOutDate, roomType);
		List<RoomResponse> roomResponses = new ArrayList<>();
		for(Room room : availableRooms) {
			byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
			if(photoBytes != null && photoBytes.length > 0) {
				String photoBase64 = Base64.getEncoder().encodeToString(photoBytes);
				RoomResponse roomResponse = getRoomResponse(room);
				roomResponse.setPhoto(photoBase64);
				roomResponses.add(roomResponse);
			}
		}
		if(roomResponses.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		else {
			return ResponseEntity.ok(roomResponses);
		}
	}
	

	private RoomResponse getRoomResponse(Room r) {
		List<BookedRoom> bookings = getAllBookingsByRoomId(r.getId());
		/*List<BookingResponse> bookingInfo = bookings.stream()
				.map(booking -> new BookingResponse(booking.getBookingId(),
						booking.getCheckedInDate(), booking.getCheckedOutDate(), booking.getBookingConfirmationCode())).toList();*/
		byte[] photoBytes = null;
		Blob photoBlob = r.getPhoto();
		if(photoBlob != null) {
			try {
				photoBytes = photoBlob.getBytes(1, (int)photoBlob.length());
			}
			catch (SQLException e) {
				throw new PhotoRetrievalException("Error retrieving photo");
			}
		}
		return new RoomResponse(r.getId(), r.getRoomType(), r.getRoomPrice(), r.isBooked(),photoBytes);
	}

	private List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
		return bookingService.getAllBookingsByRoomId(roomId);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
