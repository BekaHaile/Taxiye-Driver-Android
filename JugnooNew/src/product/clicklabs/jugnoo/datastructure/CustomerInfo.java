package product.clicklabs.jugnoo.datastructure;

public class CustomerInfo {
	public String userId, name, image, phoneNumber, rating, schedulePickupTime;
	
	public CustomerInfo(String userId, String name, String image, String phoneNumber, String rating){
		this.userId = userId;
		this.name = name;
		this.image = image;
		this.phoneNumber = phoneNumber;
		this.rating = rating;
		this.schedulePickupTime = "";
	}
}
