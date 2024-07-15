import moment from "moment";
import React, { useState } from "react";
import { getAvailableRooms } from "../utils/ApiFunctions";
import { Col, Container, Row, Form, Button } from "react-bootstrap";
import RoomTypeSelector from "./RoomTypeSelector";
import RoomSearchResult from "./RoomSearchResult";


const RoomSearch = () => {

    const[searchQuery, setSearchQuery] = useState({
        checkInDate:"", checkOutDate:"", roomType:""
    });

    const[errorMessage, setErrorMessage] = useState("");
    const[availableRooms, setAvailableRooms] = useState([]);
    const[isLoading, setIsLoading] = useState(false);

    //fukcija koja vrsi pretragu
    const handleSearch = (e) => {
        e.preventDefault();
        const checkIn = moment(searchQuery.checkInDate);
        const checkOut = moment(searchQuery.checkOutDate);
        if(!checkIn.isValid() || !checkOut.isValid()){
            setErrorMessage("Please enter valid date range.");
            return
        }
        if(!checkOut.isSameOrAfter(checkIn)){
            setErrorMessage("Check-In Date must come before Check-Out date.")
            return
        }
        setIsLoading(true);
        getAvailableRooms(searchQuery.checkInDate, searchQuery.checkOutDate, searchQuery.roomType).then((response)=> {
            setAvailableRooms(response.data);
            setTimeout(() =>{
                setIsLoading(false)
            }, 3000)
        }).catch((error) => {
            console.error(error)
        }).finally(() => {
            setIsLoading(false)
        });
    };

    const handleInputChange = (e) => {
        const {name, value} = e.target;
        setSearchQuery({...searchQuery, [name] : value});
        const checkIn = moment(searchQuery.checkInDate);
        const checkOut = moment(searchQuery.checkOutDate);
        //provera da li je datum validan
        if(checkIn.isValid() && checkOut.isValid()){
            setErrorMessage("")
        }
    };

    //ciscenje pretrage
    const clearSearch = () => {
        //nakon pretrage, parametri se postavljaju na prazne stringove
        setSearchQuery({
            checkInDate:"", checkOutDate:"", roomType:""
        });
    };

    return(
        <>
        <Container className="mt-5  mb-5 py-5 shadow">
            <Form onSubmit={handleSearch}>
				<Row className="justify-content-center">
					<Col xs={12} md={3}>
						<Form.Group controlId="checkInDate">
							<Form.Label>Check-in Date</Form.Label>
							<Form.Control
									type="date"
									name="checkInDate"
									value={searchQuery.checkInDate}
									onChange={handleInputChange}
									min={moment().format("YYYY-MM-DD")}
								/>
						</Form.Group>
					</Col>
					<Col xs={12} md={3}>
						<Form.Group controlId="checkOutDate">
							<Form.Label>Check-out Date</Form.Label>
							<Form.Control
									type="date"
									name="checkOutDate"
									value={searchQuery.checkOutDate}
									onChange={handleInputChange}
									min={moment().format("YYYY-MM-DD")}
								/>
						</Form.Group>
					</Col>
					<Col xs={12} md={3}>
						<Form.Group controlId="roomType">
							<Form.Label>Room Type</Form.Label>
							<div className="d-flex">
								<RoomTypeSelector
										handleRoomInputChange={handleInputChange}
										newRoom={searchQuery}
								/>
								<Button variant="secondary" type="submit" className="ml-2">
										Search
								</Button>
							</div>
						</Form.Group>
					</Col>
				</Row>
			</Form>
            {isLoading ? (
                <p>Finding available rooms...</p>
            ) : availableRooms ? (
                <RoomSearchResult results={availableRooms} onClearSearch={clearSearch} />
            ) : (
                <p>No rooms available for the selected date and type.</p>
            )}
            {errorMessage && <p className="text-danger">{errorMessage}</p>}
        </Container>
        </>
    );
};

export default RoomSearch;