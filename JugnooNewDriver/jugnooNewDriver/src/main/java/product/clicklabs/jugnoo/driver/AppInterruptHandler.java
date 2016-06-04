package product.clicklabs.jugnoo.driver;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.datastructure.CurrentPathItem;

public interface AppInterruptHandler {
	
	public void onNewRideRequest(int perfectRide, int isPooled);
	
	public void onCancelRideRequest(String engagementId, boolean acceptedByOtherDriver);
	
	public void onRideRequestTimeout(String engagementId);
	
	public void onManualDispatchPushReceived();

	public void onChangeStatePushReceived(int flag, String engagementId);
	
	public void onCashAddedToWalletByCustomer(int userId, double balance);

	void onDropLocationUpdated(String engagementId, LatLng dropLatLng);
	
	public void updateMeteringUI(double distance, long elapsedTime, long waitTime, Location lastGPSLocation,
								 Location lastFusedLocation, double totalHaversineDistance);
	
	public void drawOldPath();
	
	public void addPathToMap(PolylineOptions polylineOptions);

    public void addPathNew(ArrayList<CurrentPathItem> currentPathItems);

	void handleCancelRideSuccess();
	void handleCancelRideFailure();

	void markArrivedInterrupt(LatLng latLng);

	void driverTimeoutDialogPopup(long timeoutInterwal);

	void fetchHeatMapDataCall(Context context);

	void updateCustomers();

}
