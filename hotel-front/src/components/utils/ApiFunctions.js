//svrha je kreirati funkcije koje ce imati interakciju sa backend serverom
import axios from "axios";

export const api = axios.create({
    baseURL:"http://localhost:8080"
});

//funkcija za dodavanje nove sobe u nasu database:
export async function addRoom(photo, roomType, roomPrice){
    //kreiranje novog formData objekta
    const formData = new FormData();
    formData.append("photo",photo)
    formData.append("roomType",roomType)
    formData.append("roomPrice",roomPrice)

    //slanje podataka na backend
    const response = await api.post("/rooms/add/new-room", formData);
    //provera da li je kreirano
    if(response.status === 201){
        return true;
    }
    else{
        return false;
    }

}

/*kreiranje funkcije za dobavljanje svih soba odredjenog tipa */
export async function getRoomTypes() {
	try {
		const response = await api.get("/rooms/room/types")
		return response.data
	} catch (error) {
        console.error("Error fetching room types:", error);
		throw new Error("Error fetching room types")
	}
}

/*Ova funkcija dobavlja sve sobe iz baze podataka. */
export async function getAllRooms() {
	try {
		const result = await api.get("/rooms/all-rooms")
		return result.data
	} catch (error) {
        console.error("Error fetching rooms:", error);
		throw new Error("Error fetching rooms")
	}
}

/*ova funkcija brise sobu po id-ju */
export async function deleteRoom(roomId){
    try{
        const result = await api.delete(`/rooms/delete/room/${roomId}`);
        return result.data;
    }
    catch(error){
        throw new Error(`Error deleting room ${error.message}`);
    }
}


/*funkcija za azuriranje sobe */
export async function updateRoom(roomId, roomData){
    const formData = new FormData();
    formData.append("roomType", roomData.roomType);
    formData.append("roomPrice", roomData.roomPrice);
    formData.append("photo", roomData.photo);
    const response = await api.put(`/rooms/update/${roomId}`, formData);
    return response
}

/*funkcija koja dobavlja sobe po id-ju */
export async function getRoomById(roomId){
    try{
        const result = await api.get(`/rooms/room/${roomId}`);
        return result.data;
    }
    catch(error){
        throw new Error(`Error fetching room ${error.message}`);
    }
}

/*funkcija koja sluzi za rezervisanje sobe. Ovaj booking parametar predstavlja objekat*/
export async function bookRoom(roomId, booking){
    try{
        const response = await api.post(`/bookings/room/${roomId}/booking`, booking);
        return response.data;
    }
    catch(error){
        if(error.response && error.response.data){
            throw new Error(error.response.data)
        }
        else{
            throw new Error(`Error booking room : ${error.message}`);
        }
    }
}

/* funkcija za dobavljanje svih bukingsa iz baze */
export async function getAllBookings(){
    try{
        const result = await api.get("/bookings/all-bookings")
        return result.data;
    }
    catch(error){
        throw new Error(`Error fetching bookings : ${error.message}`);
    }
}

/*funkcija za trazenje rezervacije prema confirmation code-u */
export async function getBookingByConfirmationCode(confirmationCode){
    try{
        const result = await api.get(`/bookings/confirmation/${confirmationCode}`);
        return result.data;
    }
    catch(error){
        if(error.response && error.response.data){
            throw new Error(error.response.data);
        }
        else{
            throw new Error(`Error finding booking : ${error.message}`);
        }
    }
}

/*funkcija za otkazivanje rezervacije*/ 
export async function cancelBooking(bookingId){
    try{
        const result = await api.delete(`/bookings/booking/${bookingId}/delete`);
        return result.data;
    }
    catch(error){
        throw new Error(`Error canceling booking : ${error.message}`);
    }
}

//ova funkcija pravi gresku
/*export async function getAvailableRooms(checkInDate, checkOutDate, roomType){
    const results = await api.get(`/rooms/available-rooms?checkInDate=${checkInDate}
        &checkOutDate=${checkOutDate}&roomType=${roomType}`);
    return results;    
}*/

/*ova funkcija dobavlja sve slobodne sobe iz baze sa datim datumom i tipom sobe */
export async function getAvailableRooms(checkInDate, checkOutDate, roomType) {
    try {
        const response = await api.get("/rooms/available-rooms", {
            params: {
                checkInDate: checkInDate.trim(),
                checkOutDate: checkOutDate.trim(),
                roomType: roomType.trim()
            }
        });
        return response;
    } catch (error) {
        throw new Error("Error fetching available rooms");
    }
}