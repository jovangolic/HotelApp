import React, { useState } from "react";
import { addRoom } from "../utils/ApiFunctions";
import RoomTypeSelector from "../common/RoomTypeSelector";
import { Link } from "react-router-dom";


const  AddRoom =() => {

    const[newRoom, setNewRoom] = useState({
        photo: null, roomType: "", roomPrice: ""
    });

    const[imagePreview, setImagePreview] = useState("")
    const[successMessage, setSuccessMessage] = useState("")
    const[errorMessage, setErrorMessage] = useState("")

    //funkcija za unos
    const handleRoomInputChange =(e) => {
        //ime polja
        const name = e.target.name
        //vrednost polja
		let value = e.target.value
		if (name === "roomPrice") {
            //ako je value  broj
			if (!isNaN(value)) {
				value = parseInt(value)
			} else {
				value = ""
			}
		}
		setNewRoom({ ...newRoom, [name]: value })
    }

    const handleImageChange = (e) => {
        const selectedImage = e.target.files[0]
        setNewRoom({...newRoom, photo : selectedImage});
        //kreiranje linka ka slici
        setImagePreview(URL.createObjectURL(selectedImage))
    };

    //metoda za rukovanje  submit-om
    const handleSubmit = async(e) => {
        e.preventDefault();
        try{
            //slanje podataka na backend
            const success = await addRoom(newRoom.photo, newRoom.roomType, newRoom.roomPrice);
            if(success !== undefined){
                setSuccessMessage("A new room was added to the database");
                setNewRoom({photo: null, roomType: "", roomPrice : ""});
                setImagePreview("");
                setErrorMessage("");
            }
            else{
                setErrorMessage("Error adding room");
            }
        }
        catch(error){
            setErrorMessage(error.message);
        }
        setTimeout(() => {
			setSuccessMessage("")
			setErrorMessage("")
		}, 3000)
    };

    //kreiranje UI za formu
    return(
        <>
        <section className="container mt-5 mb-5">
            <div className="row justify-content-center">
                <div className="col-md-8 col-lg-6">
                    <h2 className="mt-5 mb-2">Add a new Room</h2>
                        {successMessage && (
							<div className="alert alert-success fade show"> {successMessage}</div>
						)}

						{errorMessage && <div className="alert alert-danger fade show"> {errorMessage}</div>}
                    <form onSubmit={handleSubmit}>
                        <div className="mb-3">
                            <label htmlFor="roomType" className="form-label"> Room Type</label>
                            <div>
                                <RoomTypeSelector handleRoomInputChange={handleRoomInputChange} newRoom={newRoom} />
                            </div>
                        </div>

                        <div className="mb-3">
                            <label htmlFor="roomPrice" className="form-label"> Room Price</label>
                            <input className="form-control" required id="roomPrice" name="roomPrice" type="number" value={newRoom.roomPrice} onChange={handleRoomInputChange} />
                        </div>

                        <div className="mb-3">
                            <label htmlFor="photo" className="form-label"> Room Photo</label>
                            <input required id="photo" name="photo" type="file" className="form-control" onChange={handleImageChange} />
                            {imagePreview && (
                                <img src={imagePreview} alt="Preview Room photo"  style={{maxWidth: "400px", maxHeight:"400px"}} className="mb-3"></img>
                            )}
                        </div>
                        <div className="d-grid gap-2 d-md-flex mt-2">
                            <Link to={"/existing-rooms"} className="btn btn-outline-info">
                                Back
                            </Link>
                            <button type="submit" className="btn btn-outline-primary ml-5">Save Room</button>
                        </div>
                    </form>
                </div>
            </div>




        </section>
        
        
        </>
    );
};

export default AddRoom;