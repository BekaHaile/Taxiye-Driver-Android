package product.clicklabs.jugnoo.driver.datastructure;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryInfo;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;

/**
 * Super class for customer info
 * @author shankar
 *
 */
public class CustomerInfo {
	
	public int engagementId;
	public int userId, referenceId;
	public String phoneNumber;
	private String name;
	public LatLng requestlLatLng;
	public LatLng currentLatLng;
	private int cachedApiEnabled;

	public String image, rating;
	public CouponInfo couponInfo;
	public PromoInfo promoInfo;
	public double jugnooBalance;
	public LatLng dropLatLng;
	public String dropAddress;
	public int meterFareApplicable;
	public int getJugnooFareEnabled;
	public int luggageChargesApplicable;
	public int waitingChargesApplicable, forceEndDelivery;

	private int status;
	private String address, startTime;
	private double fareFactor, cashOnDelivery;
	private int isPooled;

	private int isDelivery;
	private ArrayList<DeliveryInfo> deliveryInfos;
	private int totalDeliveries;
	private double estimatedFare, dryDistance;
	private String vendorMessage;

	private String color;
	private PoolFare poolFare;



	public CustomerInfo(int engagementId, int userId, int referenceId, String name, String phoneNumber, LatLng requestlLatLng, int cachedApiEnabled,
						String image, String rating, CouponInfo couponInfo, PromoInfo promoInfo, double jugnooBalance,
						int meterFareApplicable, int jugnooFareButton, int luggageChargesApplicable, int waitTimeApplicable,
						int status, int isPooled, int isDelivery, String address, int totalDeliveries, double estimatedFare,
						String vendorMessage, double cashOnDelivery, LatLng currentLatLng, int forceEndDelivery){
		this.engagementId = engagementId;
		this.userId = userId;
		this.referenceId = referenceId;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.requestlLatLng = requestlLatLng;
		this.currentLatLng = currentLatLng;
		this.cachedApiEnabled = cachedApiEnabled;

		this.image = image;
		this.rating = rating;
		this.couponInfo = couponInfo;
		this.promoInfo = promoInfo;
		this.jugnooBalance = jugnooBalance;

		this.image = this.image.replace("http://graph.facebook", "https://graph.facebook");
		this.dropLatLng = null;
		this.dropAddress = null;

		this.meterFareApplicable = meterFareApplicable;
		this.getJugnooFareEnabled = jugnooFareButton;
		this.luggageChargesApplicable = luggageChargesApplicable;
		this.waitingChargesApplicable = waitTimeApplicable;
		this.setStatus(status);
		this.isPooled = isPooled;
		this.isDelivery = isDelivery;
		this.address = address;

		if(this.isDelivery == 1){
			this.waitingChargesApplicable = 1;
		}
		this.totalDeliveries = totalDeliveries;
		this.estimatedFare = estimatedFare;
		this.vendorMessage = vendorMessage;
		this.cashOnDelivery = cashOnDelivery;
		this.forceEndDelivery = forceEndDelivery;
	}


	public CustomerInfo(int engagementId){
		this.engagementId = engagementId;
	}

	public CustomerInfo(int engagementId, int userId, LatLng requestlLatLng, String startTime, String address,
						int referenceId, double fareFactor, int status, int isPooled, int isDelivery,
						int totalDeliveries, double estimatedFare, String userName, double dryDistance, double cashOnDelivery,
						LatLng currentLatLng){
		this.engagementId = engagementId;
		this.userId = userId;
		this.requestlLatLng = requestlLatLng;
		this.currentLatLng = currentLatLng;
		this.startTime = startTime;
		this.address = address;
		this.referenceId = referenceId;
		this.fareFactor = fareFactor;
		this.status = status;
		this.isPooled = isPooled;
		this.isDelivery = isDelivery;
		this.totalDeliveries = totalDeliveries;
		this.estimatedFare = estimatedFare;
		this.name = userName;
		this.dryDistance =dryDistance;
		this.cashOnDelivery = cashOnDelivery;
	}

	public double getDryDistance() {
		return dryDistance;
	}

	public void setDryDistance(double dryDistance) {
		this.dryDistance = dryDistance;
	}

	@Override
	public String toString() {
		return "engagementId = "+engagementId+" userId = "+userId+" referenceId = "+referenceId+
				" name = "+name+" requestlLatLng = "+requestlLatLng+
				", cachedApiEnabled="+cachedApiEnabled;
	}

	@Override
	public boolean equals(Object o) {
		try{
			if(((CustomerInfo)o).engagementId == engagementId){
				return true;
			} else{
				return false;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return super.equals(o);
	}

	public int getCachedApiEnabled() {
		return cachedApiEnabled;
	}

	public void setCachedApiEnabled(int cachedApiEnabled) {
		this.cachedApiEnabled = cachedApiEnabled;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}


	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public double getFareFactor() {
		return fareFactor;
	}

	public void setFareFactor(double fareFactor) {
		this.fareFactor = fareFactor;
	}

	public LatLng getRequestlLatLng() {
		return requestlLatLng;
	}

	public void setRequestlLatLng(LatLng requestlLatLng) {
		this.requestlLatLng = requestlLatLng;
	}

	public LatLng getCurrentLatLng() {
		return currentLatLng;
	}

	public void setCurrentLatLng(LatLng currentLatLng) {
		this.currentLatLng = currentLatLng;
	}

	public int getEngagementId() {
		return engagementId;
	}

	public void setEngagementId(int engagementId) {
		this.engagementId = engagementId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(int referenceId) {
		this.referenceId = referenceId;
	}

	public String getName() {
		if(name == null){
			return "Customer";
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public CouponInfo getCouponInfo() {
		return couponInfo;
	}

	public void setCouponInfo(CouponInfo couponInfo) {
		this.couponInfo = couponInfo;
	}

	public PromoInfo getPromoInfo() {
		return promoInfo;
	}

	public void setPromoInfo(PromoInfo promoInfo) {
		this.promoInfo = promoInfo;
	}

	public double getJugnooBalance() {
		return jugnooBalance;
	}

	public void setJugnooBalance(double jugnooBalance) {
		this.jugnooBalance = jugnooBalance;
	}

	public LatLng getDropLatLng() {
		return dropLatLng;
	}

	public void setDropLatLng(LatLng dropLatLng) {
		this.dropLatLng = dropLatLng;
	}

	public String getDropAddress() {
		return dropAddress;
	}

	public void setDropAddress(String dropAddress) {
		this.dropAddress = dropAddress;
	}

	public int getMeterFareApplicable() {
		return meterFareApplicable;
	}

	public void setMeterFareApplicable(int meterFareApplicable) {
		this.meterFareApplicable = meterFareApplicable;
	}

	public int getGetJugnooFareEnabled() {
		return getJugnooFareEnabled;
	}

	public void setGetJugnooFareEnabled(int getJugnooFareEnabled) {
		this.getJugnooFareEnabled = getJugnooFareEnabled;
	}

	public int getLuggageChargesApplicable() {
		return luggageChargesApplicable;
	}

	public void setLuggageChargesApplicable(int luggageChargesApplicable) {
		this.luggageChargesApplicable = luggageChargesApplicable;
	}

	public int getWaitingChargesApplicable() {
		return waitingChargesApplicable;
	}

	public void setWaitingChargesApplicable(int waitingChargesApplicable) {
		this.waitingChargesApplicable = waitingChargesApplicable;
	}

	public int getIsPooled() {
		return isPooled;
	}

	public void setIsPooled(int isPooled) {
		this.isPooled = isPooled;
	}

	public int getIsDelivery() {
		return isDelivery;
	}

	public void setIsDelivery(int isDelivery) {
		this.isDelivery = isDelivery;
	}

	public ArrayList<DeliveryInfo> getDeliveryInfos() {
		return deliveryInfos;
	}

	public void setDeliveryInfos(ArrayList<DeliveryInfo> deliveryInfos) {
		this.deliveryInfos = deliveryInfos;
	}

	public int getTotalDeliveries() {
		return totalDeliveries;
	}

	public void setTotalDeliveries(int totalDeliveries) {
		this.totalDeliveries = totalDeliveries;
	}

	public String getEstimatedFare() {
		if(estimatedFare > 0){
			return Utils.getDecimalFormatForMoney().format(estimatedFare);
		} else{
			return "-";
		}
	}

	public void setEstimatedFare(double estimatedFare) {
		this.estimatedFare = estimatedFare;
	}

	public String getCashOnDelivery() {
		if(cashOnDelivery > 0){
			return Utils.getDecimalFormatForMoney().format(cashOnDelivery);
		} else{
			return "-";
		}
	}

	public void setCashOnDelivery(double cashOnDelivery) {
		this.cashOnDelivery = cashOnDelivery;
	}

	public String getVendorMessage() {
		return vendorMessage;
	}

	public void setVendorMessage(String vendorMessage) {
		this.vendorMessage = vendorMessage;
	}

	public double getTotalDistance(double distance, Context context){
//		if(getIsPooled() == 1 && getPoolFare() != null){
//			return getPoolFare().getDistance();
//		} else {
			try {
				JSONObject jObj = new JSONObject(Prefs.with(context).getString(Constants.SP_CUSTOMER_RIDE_DATAS_OBJECT, Constants.EMPTY_OBJECT));
				if (jObj.has(String.valueOf(getEngagementId()))) {
					JSONObject jc = jObj.getJSONObject(String.valueOf(getEngagementId()));
					double startDistance = Double.parseDouble(jc.optString(Constants.KEY_DISTANCE, "0"));
					if(distance < startDistance){
						return 0;
					} else{
						return distance - startDistance;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
//		}
		return distance;
	}

	public double getTotalHaversineDistance(double haversineDistance, Context context){
		try {
			JSONObject jObj = new JSONObject(Prefs.with(context).getString(Constants.SP_CUSTOMER_RIDE_DATAS_OBJECT, Constants.EMPTY_OBJECT));
			if(jObj.has(String.valueOf(getEngagementId()))) {
				JSONObject jc = jObj.getJSONObject(String.valueOf(getEngagementId()));
				return haversineDistance - Double.parseDouble(jc.optString(Constants.KEY_HAVERSINE_DISTANCE, "0"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return haversineDistance;
	}

	public long getElapsedRideTime(Context context){
		long startTime = System.currentTimeMillis();
//		if(getIsPooled() == 1 && getPoolFare() != null){
//			return getPoolFare().getRideTime();
//		} else {
			try {
				JSONObject jObj = new JSONObject(Prefs.with(context).getString(Constants.SP_CUSTOMER_RIDE_DATAS_OBJECT, Constants.EMPTY_OBJECT));
				if (jObj.has(String.valueOf(getEngagementId()))) {
					JSONObject jc = jObj.getJSONObject(String.valueOf(getEngagementId()));
					startTime = Long.parseLong(jc.optString(Constants.KEY_RIDE_TIME, String.valueOf(System.currentTimeMillis())));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
//		}
		return System.currentTimeMillis() - startTime;
	}

//	public void resetStartRideTime(Context context) {
//		try {
//			JSONObject jObj = new JSONObject(Prefs.with(context).getString(Constants.SP_CUSTOMER_RIDE_DATAS_OBJECT, Constants.EMPTY_OBJECT));
//			if (jObj.has(String.valueOf(getEngagementId()))) {
//				JSONObject jc = jObj.getJSONObject(String.valueOf(getEngagementId()));
//				jc.put(Constants.KEY_RIDE_TIME, System.currentTimeMillis());
//				jObj.put(String.valueOf(getEngagementId()), jc);
//				Prefs.with(context).save(Constants.SP_CUSTOMER_RIDE_DATAS_OBJECT, jObj.toString());
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}


	public long getTotalWaitTime(long waitTime, Context context) {
		try {
			JSONObject jObj = new JSONObject(Prefs.with(context).getString(Constants.SP_CUSTOMER_RIDE_DATAS_OBJECT, Constants.EMPTY_OBJECT));
			if (jObj.has(String.valueOf(getEngagementId()))) {
				JSONObject jc = jObj.getJSONObject(String.valueOf(getEngagementId()));
				return waitTime - Long.parseLong(jc.optString(Constants.KEY_WAIT_TIME, "0"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return waitTime;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public PoolFare getPoolFare() {
		return poolFare;
	}

	public void setPoolFare(PoolFare poolFare) {
		this.poolFare = poolFare;
	}


}
