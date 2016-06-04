package product.clicklabs.jugnoo;

import org.json.JSONObject;

public interface AppInterruptHandler {
	
	public void refreshDriverLocations();
	
	public void onChangeStatePushReceived();
	
	public void onCancelCompleted();
	
	public void rideRequestAcceptedInterrupt(JSONObject jObj);
	
	public void onNoDriversAvailablePushRecieved(String logMessage);
	
	public void startRideForCustomer(final int flag);
	
	public void customerEndRideInterrupt(String engagementId);
	
	public void onAfterRideFeedbackSubmitted(int givenRating);
	
	public void onJugnooCashAddedByDriver(double jugnooBalance, String message);
	
}
