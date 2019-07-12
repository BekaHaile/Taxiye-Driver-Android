package product.clicklabs.jugnoo.driver.datastructure;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Database2;
import product.clicklabs.jugnoo.driver.MyApplication;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryInfo;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryInfoInRideDetails;
import product.clicklabs.jugnoo.driver.retrofit.model.TollData;
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
	private int isDeliveryPool, falseDeliveries, loadingStatus;
	private ArrayList<DeliveryInfo> deliveryInfos;
	private ArrayList<String> deliveryAddress;
	private int totalDeliveries, orderId;
	private double estimatedFare, dryDistance, estimatedDist;
	private String vendorMessage, estimatedDriverFare;

	private String color;
	private PoolFare poolFare;
	private ReverseBidFare reverseBidFare;
	private DeliveryInfoInRideDetails deliveryInfoInRideDetails;
	private String currencyUnit;
	private int reverseBid, bidPlaced;
	private double bidValue;
	private double initialBidValue;
	private double estimatedTripDistance;
	private ArrayList<TollData> tollData;
	private double tipAmount;
	private int luggageCount;
	private double waypointDistance;
	private String pickupTime;
	private String pickupAddressEng, dropAddressEng;
	private int isCorporateRide;
	private String customerNotes;
	private int tollApplicable;
	private String rentalInfo;
	private List<String> customerOrderImagesList;


	/**
	 * For accepted customers
	 */
	public CustomerInfo(Context context, int engagementId, int userId, int referenceId, String name, String phoneNumber, LatLng requestlLatLng, int cachedApiEnabled,
						String image, String rating, CouponInfo couponInfo, PromoInfo promoInfo, double jugnooBalance,
						int meterFareApplicable, int jugnooFareButton, int luggageChargesApplicable, int waitTimeApplicable,
						int status, int isPooled, int isDelivery, int isDeliveryPool, String address, int totalDeliveries, double estimatedFare,
						String vendorMessage, double cashOnDelivery, LatLng currentLatLng, int forceEndDelivery, String estimatedDriverFare,
						int falseDeliveries, int orderId, int loadingStatus, String currencyUnit, double tipAmount, int luggageCount, String pickupTime,
						int isCorporateRide, String customerNotes, int tollApplicable, final String rentalInfo,List<String> customerOrderImagesList){
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
		this.isDeliveryPool = isDeliveryPool;
		if (Prefs.with(context).getInt(Constants.KEY_DRIVER_CHECK_LOCALE_FOR_ADDRESS, 0) != 1
				|| context.getResources().getConfiguration().locale.getLanguage().contains("en")) {
			this.address = address;
		} else {
			this.address = "";
			this.pickupAddressEng = address;
		}

		if(this.isDelivery == 1){
			this.waitingChargesApplicable = 1;
		}
		this.totalDeliveries = totalDeliveries;
		this.estimatedFare = estimatedFare;
		this.vendorMessage = vendorMessage;
		this.cashOnDelivery = cashOnDelivery;
		this.forceEndDelivery = forceEndDelivery;
		this.estimatedDriverFare = estimatedDriverFare;
		this.falseDeliveries = falseDeliveries;
		this.orderId = orderId;
		this.loadingStatus = loadingStatus;
		this.currencyUnit = currencyUnit;
		this.tipAmount = tipAmount;
		this.luggageCount = luggageCount;
		this.pickupTime = pickupTime;
		this.isCorporateRide = isCorporateRide;
		this.customerNotes = customerNotes;
		this.tollApplicable = tollApplicable;
		this.rentalInfo = rentalInfo;
		this.customerOrderImagesList = customerOrderImagesList;


		setMapValue(engagementId, Constants.KEY_WAYPOINT_DISTANCE, "0");
//		setMapValue(engagementId, Constants.KEY_CSV_PATH, "");
//		setMapValue(engagementId, Constants.KEY_CSV_WAYPOINTS, "");
//		setMapValue(engagementId, Constants.KEY_NUM_WAYPOINTS, "0");
	}


	/**
	 * For finding customer from array
	 */
	public CustomerInfo(int engagementId){
		this.engagementId = engagementId;
	}

	/**
	 * For customer requests
	 */
	public CustomerInfo(int engagementId, int userId, LatLng requestlLatLng, String startTime, String address,
						int referenceId, double fareFactor, int status, int isPooled, int isDelivery, int isDeliveryPool,
						int totalDeliveries, double estimatedFare, String userName, double dryDistance, double cashOnDelivery,
						LatLng currentLatLng, String estimatedDriverFare, ArrayList<String> deliveryAddress, double estimatedDist,
						String currency, int reverseBid, int bidPlaced, double bidValue, double initialBidValue, double estimatedTripDistance,
						String pickupTime, String rentalInfo){
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
		this.isDeliveryPool = isDeliveryPool;
		this.totalDeliveries = totalDeliveries;
		this.estimatedFare = estimatedFare;
		this.name = userName;
		this.dryDistance =dryDistance;
		this.cashOnDelivery = cashOnDelivery;
		this.estimatedDriverFare = estimatedDriverFare;
		this.deliveryAddress = deliveryAddress;
		this.estimatedDist = estimatedDist;
		this.currencyUnit = currency;
		this.reverseBid = reverseBid;
		this.bidPlaced = bidPlaced;
		this.bidValue = bidValue;
		this.initialBidValue = initialBidValue;
		this.estimatedTripDistance = estimatedTripDistance;
		this.pickupTime = pickupTime;
		this.rentalInfo = rentalInfo;
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

	public void setDropAddress(Context context, String dropAddress, boolean forceSet) {
		if(forceSet
				|| Prefs.with(context).getInt(Constants.KEY_DRIVER_CHECK_LOCALE_FOR_ADDRESS, 0) != 1
				|| context.getResources().getConfiguration().locale.getLanguage().contains("en")){
			this.dropAddress = dropAddress;
		} else {
			this.dropAddress = "";
			this.dropAddressEng = dropAddress;
		}
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

	public int getIsDeliveryPool() {
		return isDeliveryPool;
	}

	public void setIsDeliveryPool(int isDeliveryPool) {
		this.isDeliveryPool = isDeliveryPool;
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
			return "0";
		}
	}

	public void setEstimatedFare(double estimatedFare) {
		this.estimatedFare = estimatedFare;
	}

	public String getCashOnDelivery() {
		if(cashOnDelivery > 0){
			return Utils.getDecimalFormatForMoney().format(cashOnDelivery);
		} else{
			return "0";
		}
	}

	public void setCashOnDelivery(double cashOnDelivery) {
		this.cashOnDelivery = cashOnDelivery;
	}

	public String getEstimatedDriverFare() {
		return estimatedDriverFare;
	}

	public void setEstimatedDriverFare(String estimatedDriverFare) {
		this.estimatedDriverFare = estimatedDriverFare;
	}

	public String getVendorMessage() {
		return vendorMessage;
	}

	public void setVendorMessage(String vendorMessage) {
		this.vendorMessage = vendorMessage;
	}

	private boolean distanceRecover = false;
	private double totalDistanceRecovered = 0;

	public void setTotalDistance(double distance){
		distanceRecover = true;
		totalDistanceRecovered = distance;
	}

	public double getTotalDistance(double distance, Context context){
		if(distanceRecover){return totalDistanceRecovered;}
//		if(getIsPooled() == 1 && getPoolFare() != null){
//			return getPoolFare().getDistance();
//		} else {
		if(waypointDistance > 0 && Prefs.with(context).getInt(Constants.KEY_USE_WAYPOINT_DISTANCE_FOR_FARE, 0) == 1){
			return waypointDistance;
		}
		return getSPSavedDistance(distance, context);
	}

	public double getSPSavedDistance(double distance, Context context) {
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
		return distance;
	}

	public double getWaypointDistance(){
		return waypointDistance;
	}

	public void setWaypointDistance(double waypointDistance){
		this.waypointDistance = waypointDistance;
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

	public ArrayList<String> getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(ArrayList<String> deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public double getEstimatedDist() {
		return estimatedDist;
	}

	public void setEstimatedDist(double estimatedDist) {
		this.estimatedDist = estimatedDist;
	}

	public int getFalseDeliveries() {
		return falseDeliveries;
	}

	public void setFalseDeliveries(int falseDeliveries) {
		this.falseDeliveries = falseDeliveries;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public DeliveryInfoInRideDetails getDeliveryInfoInRideDetails() {
		return deliveryInfoInRideDetails;
	}

	public void setDeliveryInfoInRideDetails(DeliveryInfoInRideDetails deliveryInfoInRideDetails) {
		this.deliveryInfoInRideDetails = deliveryInfoInRideDetails;
	}

	public int getLoadingStatus() {
		return loadingStatus;
	}

	public void setLoadingStatus(int loadingStatus) {
		this.loadingStatus = loadingStatus;
	}

	public String getCurrencyUnit() {
		return currencyUnit;
	}

	public void setCurrencyUnit(String currencyUnit) {
		this.currencyUnit = currencyUnit;
	}

	public boolean isReverseBid() {
		return reverseBid == 1;
	}

	public void setReverseBid(int reverseBid) {
		this.reverseBid = reverseBid;
	}

	public ReverseBidFare getReverseBidFare() {
		return reverseBidFare;
	}

	public void setReverseBidFare(ReverseBidFare reverseBidFare) {
		this.reverseBidFare = reverseBidFare;
	}

	public boolean isBidPlaced() {
		return bidPlaced == 1;
	}

	public void setBidPlaced(int bidPlaced) {
		this.bidPlaced = bidPlaced;
	}

	public double getBidValue() {
		return bidValue;
	}

	public void setBidValue(double bidValue) {
		this.bidValue = bidValue;
	}

	public double getInitialBidValue() {
		return initialBidValue;
	}

	public void setInitialBidValue(double initialBidValue) {
		this.initialBidValue = initialBidValue;
	}

	public double getEstimatedTripDistance() {
		return estimatedTripDistance;
	}

	public void setEstimatedTripDistance(double estimatedTripDistance) {
		this.estimatedTripDistance = estimatedTripDistance;
	}

	public double getTollFare() {
		double tollFare = 0;
		for(TollData td : getTollData()){
			if(!td.getEdited() || td.getToll() > 0) {
				tollFare += td.getToll();
			}
		}
		return tollFare;
	}


	public double getTipAmount() {
		return tipAmount;
	}

	public void setTipAmount(double tipAmount) {
		this.tipAmount = tipAmount;
	}

	public int getLuggageCount() {
		return luggageCount;
	}

	public void setLuggageCount(int luggageCount) {
		this.luggageCount = luggageCount;
	}

	public String getPickupTime() {
		return pickupTime;
	}

	public void setPickupTime(String pickupTime) {
		this.pickupTime = pickupTime;
	}

	public ArrayList<TollData> getTollData() {
		if(tollData == null){
			tollData = new ArrayList<>();
		}
		return tollData;
	}

	public void setTollData(ArrayList<TollData> tollData) {
		this.tollData = tollData;
	}

	public String getPickupAddressEng() {
		return pickupAddressEng;
	}

	public String getDropAddressEng() {
		return dropAddressEng;
	}

	public int getIsCorporateRide() {
		return isCorporateRide;
	}

	public void setIsCorporateRide(int isCorporateRide) {
		this.isCorporateRide = isCorporateRide;
	}

	public String getCustomerNotes() {
		if(customerNotes != null && customerNotes.equalsIgnoreCase("null")){
			customerNotes = "";
		}
		return customerNotes;
	}

	public int getTollApplicable() {
		return tollApplicable;
	}

	public void setTollApplicable(int tollApplicable) {
		this.tollApplicable = tollApplicable;
	}

	public String getRentalInfo() {
		return rentalInfo;
	}

	public void setRentalInfo(String rentalInfo) {
		this.rentalInfo = rentalInfo;
	}

	public List<String> getCustomerOrderImagesList() {
		return customerOrderImagesList!=null ? customerOrderImagesList : new ArrayList<String>();
	}

	public void setCustomerOrderImagesList(List<String> customerOrderImagesList) {
		this.customerOrderImagesList = customerOrderImagesList;
	}

	public static void setMapValue(int engagementId, String key, String value){
		Database2.getInstance(MyApplication.getInstance()).setKeyValue(key+engagementId, value);
	}

	public static String getMapValue(int engagementId, String key){
		return Database2.getInstance(MyApplication.getInstance()).getKeyValue(key+engagementId);
	}
}
