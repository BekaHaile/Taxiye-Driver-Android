package product.clicklabs.jugnoo.driver.datastructure;

import java.util.Calendar;

import product.clicklabs.jugnoo.driver.utils.DateOperations;

import com.google.android.gms.maps.model.LatLng;

public class StationData {

	public String stationId;
	public LatLng latLng;
	public String time, address, message;
	public boolean acknowledgeDone;
	public double toleranceDistance;
	public Calendar timeCalendar;
	
	public StationData(String stationId, double latitude, double longitude, String time, String address, String message, double toleranceDistance){
		this.stationId = stationId;
		this.latLng = new LatLng(latitude, longitude);
		this.time = time;
		this.address = address;
		this.message = message;
		this.toleranceDistance = toleranceDistance;
		this.acknowledgeDone = false;
		this.timeCalendar = DateOperations.getCalendarFromTimeStamp(time);
	}
	
}
