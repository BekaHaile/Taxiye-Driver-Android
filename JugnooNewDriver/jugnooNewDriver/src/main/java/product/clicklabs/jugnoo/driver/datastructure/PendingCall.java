package product.clicklabs.jugnoo.driver.datastructure;

/**
 * Created by shankar on 2/2/16.
 */
public enum PendingCall {

	END_RIDE("end_ride"), MARK_DELIVERED("mark_delivered"), UPLOAD_RIDE_DATA("upload_ride_data"),
	END_DELIVERY("end_delivery");

	private String path;

	PendingCall(String path){
		this.path = path;
	}

	public String getPath() {
		return path;
	}
}
