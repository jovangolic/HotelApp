import React, { useEffect, useState } from "react";
import { cancelBooking, getAllBookings } from "../utils/ApiFunctions";
import Header from "../common/Header";
import BookingsTable from "./BookingsTable";


//prikazuje sve rezervacije
const Bookings = () => {

    const[bookingInfo, setBookingInfo] = useState([]);
    const[isLoading, setIsLoading] = useState(true);
    const[errorMessage ,setErrorMessage] = useState(""); 
    
    useEffect(()=>{
        setTimeout(()=>{
            getAllBookings().then((data) => {
                setBookingInfo(data);
                setIsLoading(false);
            })
            .catch((error) => {
                setErrorMessage(error.message)
                setIsLoading(false)
            })
        }, 1000)
    },[]);

    //funkcija za otkazivanje rezervacije
    const handleBookingCancellation = async(bookingId) => {
        try{
            await cancelBooking(bookingId)
            const data = await getAllBookings()
            setBookingInfo(data)
        }
        catch(error){
            setErrorMessage(error.message)
        }
    };

    return(
        <section style={{backgroundColor:"whitesmoke"}}>
            <Header title={"Existing Bookings"} />
            {errorMessage && (<div className="text-danger">{errorMessage}</div>)}
            {isLoading ? (
                <div>Loading existing bookings</div>
            ) : (
                <BookingsTable bookingInfo={bookingInfo} handleBookingCancellation={handleBookingCancellation} />
            )}
        </section>
    );
};

export default Bookings;