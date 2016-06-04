package product.clicklabs.jugnoo.datastructure;

import com.google.android.gms.maps.model.LatLng;

public class DriverInfo {
	
	public String userId, name, image, carImage, phoneNumber, rating, carNumber;
	public LatLng latLng;
	public int freeRide;
	
	public String distanceToReach = "0", durationToReach = "";
	
	public DriverInfo(String userId){
		this.userId = userId;
	}
	
	public DriverInfo(String userId, double latitude, double longitude, String name, String image, String carImage, String phoneNumber, String rating, String carNumber, int freeRide){
		this.userId = userId;
		this.latLng = new LatLng(latitude, longitude);
		this.name = name;
		this.image = image;
		this.carImage = carImage;
		this.phoneNumber = phoneNumber;
		this.rating = rating;
		this.carNumber = carNumber;
		this.freeRide = freeRide;
	}
	
	public DriverInfo(String userId, String name, String image){
		this.userId = userId;
		this.latLng = new LatLng(0, 0);
		this.name = name;
		this.image = image;
		this.carImage = "";
		this.phoneNumber = "";
		this.rating = "4";
		this.carNumber = "";
		this.freeRide = 0;
	}
	
	@Override
	public String toString() {
		return  "Name: " + name + "\n" +
				"Phone Number: "+ phoneNumber;
	}
	
	@Override
	public boolean equals(Object o) {
		try{
			if(((DriverInfo)o).userId.equalsIgnoreCase(this.userId)){
				return true;
			}
			else{
				return false;
			}
		} catch(Exception e){
			return false;
		}
	}
	
	
}
