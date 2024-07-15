import React, { useEffect, useState } from "react";
import { bookRoom, getRoomById } from "../utils/ApiFunctions";
import { useNavigate, useParams } from "react-router-dom";
import moment from "moment";
import { Form } from "react-bootstrap";
import { FormControl } from "react-bootstrap";
import BookingSummary from "./BookingSummary";


const BookingForm = () => {

    const[isValidated, setIsValidated] = useState(false);
    const[isSubmitted, setIsSubmitted] = useState(false);
    const[errorMessage, setErrorMessage] = useState("");
    const[roomPrice, setRoomPrice] = useState(0);
    const[booking, setBooking] = useState({
        guestFullName: "",guestEmail: "",checkInDate:"",
        checkOutDate:"",numberOfAdults:"",numberOfChildren:""
    });
    const [payment, setPayment] = useState(0);

    const[roomInfo, setRoomInfo] = useState({
        photo:"",roomType:"", roomPrice:""
    }); 

    const {roomId} = useParams();
    const navigate = useNavigate();
    
    const handleInputChange = (e) => {
        const {name, value} = e.target;
        setBooking({...booking, [name] : value});
        setErrorMessage("");
    };

    //funkicja za racunanje cene sobe, koliko dana ce biti zauzeta
    const getRoomPriceById = async(roomId) => {
        try{
            const response = await getRoomById(roomId)
            setRoomPrice(response.roomPrice);
        }
        catch(error){
            throw new Error(error);
        }
    };

    useEffect(()=>{
        getRoomPriceById(roomId);

    },[roomId]);

    function calculatePayment () {
        console.log("dsad")
        const checkInDate = moment(booking.checkInDate);
        const checkOutDate = moment(booking.checkOutDate);
        const differenceInDays = checkOutDate.diff(checkInDate);
        const price = roomPrice ? roomPrice : 0;
        console.log(differenceInDays * price);
        return differenceInDays * price;
    };

    //provera da li je najmanje jedan adult, da bi forma bila validna
    const isGuestCountValid = () => {
        const adultCount = parseInt(booking.numberOfAdults);
        const childrenCount = parseInt(booking.numberOfChildren);
        const totalCount = adultCount + childrenCount;
        return totalCount >= 1 && adultCount >= 1;
    };

    const isCheckOutDateValid = () => {
        if(!moment(booking.checkOutDate).isSameOrAfter(moment(booking.checkInDate))){
            setErrorMessage("Check-out date must come before check-id date");
            return false;
        }
        else{
            setErrorMessage("");
            return true;
        }
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        const form = e.currentTarget;
        if(form.checkValidity() === false || !isGuestCountValid() || !isCheckOutDateValid()){
            e.stopPropagation();
        }
        else{
            setPayment(calculatePayment);
            setIsSubmitted(true);
        }
        setIsValidated(true);
    };

    const handleBooking = async()=>{
        try{
            const confirmationCode = await bookRoom(roomId, booking);
            setIsSubmitted(true);
            navigate("/" , {state:{message: confirmationCode}});
        }
        catch(error){
            setErrorMessage(error.message);
            navigate("/", {state:{error : errorMessage}});
        }
    };

    return(
        <>
        <div className="container mb-5">
            <div className="row">
                <div className="col-md-6">
                    <div className="card card-body mt-5">
                        <h4 className="card card-title">Reserve Room</h4>
                        <Form noValidate validated={isValidated} onSubmit={handleSubmit}>
                            <Form.Group>
                                <Form.Label htmlFor="guestFullName">Full Name: </Form.Label>
                                <FormControl required type="text"  id="guestFullName" name="guestFullName" value={booking.guestFullName} placeholder="Enter your full name"
                                    onChange={handleInputChange} />
                                <Form.Control.Feedback type="invalid">
                                    Please enter your full name
                                </Form.Control.Feedback>
                            </Form.Group>

                            <Form.Group>
                                <Form.Label htmlFor="guestEmal">Email: </Form.Label>
                                <FormControl required type="email"  id="guestEmail" name="guestEmail" value={booking.guestEmail} placeholder="Enter your email address"
                                    onChange={handleInputChange} />
                                <Form.Control.Feedback type="invalid">
                                    Please enter your email address
                                </Form.Control.Feedback>
                            </Form.Group>

                            <fieldset style={{ border: "2px" }}>
									<legend>Lodging Period</legend>
									<div className="row">
										<div className="col-6">
											<Form.Label htmlFor="checkInDate" className="hotel-color">
												Check-in date
											</Form.Label>
											<FormControl
												required
												type="date"
												id="checkInDate"
												name="checkInDate"
												value={booking.checkInDate}
												placeholder="check-in-date"
												min={moment().format("MMM Do, YYYY")}
												onChange={handleInputChange}
											/>
											<Form.Control.Feedback type="invalid">
												Please select a check in date.
											</Form.Control.Feedback>
										</div>

										<div className="col-6">
											<Form.Label htmlFor="checkOutDate" className="hotel-color">
												Check-out date
											</Form.Label>
											<FormControl
												required
												type="date"
												id="checkOutDate"
												name="checkOutDate"
												value={booking.checkOutDate}
												placeholder="check-out-date"
												min={moment().format("MMM Do, YYYY")}
												onChange={handleInputChange}
											/>
											<Form.Control.Feedback type="invalid">
												Please select a check out date.
											</Form.Control.Feedback>
										</div>
										{errorMessage && <p className="error-message text-danger">{errorMessage}</p>}
									</div>
								</fieldset>

								<fieldset style={{ border: "2px" }}>
									<legend>Number of Guest</legend>
									<div className="row">
										<div className="col-6">
											<Form.Label htmlFor="numberOfAdults" className="hotel-color">
												Adults
											</Form.Label>
											<FormControl
												required
												type="number"
												id="numberOfAdults"
												name="numberOfAdults"
												value={booking.numberOfAdults}
												min={1}
												placeholder="0"
												onChange={handleInputChange}
											/>
											<Form.Control.Feedback type="invalid">
												Please select at least 1 adult.
											</Form.Control.Feedback>
										</div>
										<div className="col-6">
											<Form.Label htmlFor="numberOfChildren" className="hotel-color">
												Children
											</Form.Label>
											<FormControl
												required
												type="number"
												id="numberOfChildren"
												name="numberOfChildren"
												value={booking.numberOfChildren}
												placeholder="0"
												min={0}
												onChange={handleInputChange}
											/>
											<Form.Control.Feedback type="invalid">
												Select 0 if no children
											</Form.Control.Feedback>
										</div>
									</div>
								</fieldset>
                                <div className="form-group mt-2 mb-2">
                                    <button type="submit" className="btn btn-hotel">Continue</button>
                                </div>
                        </Form>
                    </div>
                </div>
                <div className="col-md-4">
                    {isSubmitted && (
                        <BookingSummary booking={booking} payment={payment} isFormValid={isValidated} onConfirm={handleBooking} />
                    )}
                </div>
            </div>
        </div>
        </>
    );
};

export default BookingForm;