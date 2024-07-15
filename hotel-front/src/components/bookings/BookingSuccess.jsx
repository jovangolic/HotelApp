import React from "react";
import Header from "../common/Header";
import { useLocation } from "react-router-dom";

const BookingSuccess = () => {

    const location = useLocation();
    //ovaj ? znak je za optional 
    const message = location.state?.message;
    const error = location.state?.error;



    return(
        <div className="container">
            <Header  title="Booking success"/>
            <div className="mt-5">
                {message ? (
                    <div>
                        <h3 className="text-success">Booking success!!</h3>
                        <p className="text-success">{message}</p>
                    </div>
                ) : (
                    <div>
                        <h3 className="text-danger">Error Booking room!!</h3>
                        <p className="text-danger">{error}</p>
                    </div>
                )}
            </div>
        </div>
    );
};

export default BookingSuccess;