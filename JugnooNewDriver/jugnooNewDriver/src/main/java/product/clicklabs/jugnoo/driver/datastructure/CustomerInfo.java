package product.clicklabs.jugnoo.driver.datastructure;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.Database2;
import product.clicklabs.jugnoo.driver.MyApplication;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryInfo;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryInfoInRideDetails;
import product.clicklabs.jugnoo.driver.retrofit.model.TollData;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.Log;
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
	private int canAcceptRequest;

	public String image, rating;
	public CouponInfo couponInfo;
	public PromoInfo promoInfo;
	public double jugnooBalance;
	public LatLng dropLatLng;
	public String dropAddress;
	public String pickupAddress;
	public int meterFareApplicable;
	public int getJugnooFareEnabled;
	public int luggageChargesApplicable;
	public int waitingChargesApplicable, forceEndDelivery;

	private int status;
	private String address, startTime, requestTime;
	private double fareFactor, cashOnDelivery;
	private int isPooled;

	private int stopId;
	private int multiDestCount;
	private int isDelivery;
	private int isDeliveryPool, falseDeliveries, loadingStatus;
	private ArrayList<DeliveryInfo> deliveryInfos;
	private ArrayList<String> deliveryAddress;
	private int totalDeliveries, orderId;
	private double estimatedFare, dryDistance, estimatedDist,distance;
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
	private String timeDiff;
	private String pickupAddressEng, dropAddressEng;
	private int isCorporateRide;
	private String customerNotes;
	private int tollApplicable;
	private String rentalInfo;
	private List<String> customerOrderImagesList;

	private double incrementPercent;
	private int stepSize;
	private String bidCreatedAt;


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
	}


	/**
	 * For finding customer from array
	 */
	public CustomerInfo(int engagementId){
		this.engagementId = engagementId;
	}

	/**
	 * For finding offline requests from array
	 */
	public CustomerInfo(String name,String address,String dropAddress,String time,String driverFare,double distance,String image,int canAcceptRequest,int userId,int engagementId,int reverseBid){
		this.name = name;
		this.pickupAddressEng =address;
		this.dropAddress=dropAddress;
		this.timeDiff = time;
		this.estimatedDriverFare=driverFare;
		this.distance =distance;
		this.image = image;
		this.canAcceptRequest = canAcceptRequest;
		this.userId=userId;
		this.engagementId=engagementId;
		this.reverseBid=reverseBid;


//		@SerializedName("request_latitude")
//		@Expose
//		private Double requestLatitude;
//		@SerializedName("request_longitude")
//		@Expose
//		private Double requestLongitude;
//		@SerializedName("ride_type")
//		@Expose
//		private String rideType;
	}


	/**
	 * For customer requests
	 */
	public CustomerInfo(int engagementId, int userId, LatLng requestlLatLng, String startTime, String address,
						int referenceId, double fareFactor, int status, int isPooled, int isDelivery, int isDeliveryPool,
						int totalDeliveries, double estimatedFare, String userName, double dryDistance, double cashOnDelivery,
						LatLng currentLatLng, String estimatedDriverFare, ArrayList<String> deliveryAddress, double estimatedDist,
						String currency, int reverseBid, int bidPlaced, double bidValue, double initialBidValue, double estimatedTripDistance,
						String pickupTime, String rentalInfo, double incrementPercent, int stepSize, String pickUpAddress, String dropAddress,
						String requestTime, String bidCreatedAt, int multiDestCount){
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
		this.incrementPercent = incrementPercent;
		this.stepSize = stepSize;
		this.dropAddress = dropAddress;
		this.pickupAddress = pickUpAddress;
		this.requestTime = requestTime;
		this.bidCreatedAt = bidCreatedAt;
        this.multiDestCount = multiDestCount;
	}


	public double getDryDistance() {
		return dryDistance;
	}

	public void setDryDistance(double dryDistance) {
		this.dryDistance = dryDistance;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	@Override
	public String toString() {
		return "id="+engagementId;
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

	public int getCanAcceptRequest() {
		return canAcceptRequest;
	}

	public void setCanAcceptRequest(int canAcceptRequest) {
		this.canAcceptRequest = canAcceptRequest;
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
			return "";
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

	public double getTotalDistance(Context context, boolean onEndRide){
		double distance = CustomerRideDataGlobal.getDistance(context);
		if(distanceRecover){return totalDistanceRecovered;}

		double meteringDistance = getSPSavedDistance(distance);

		//if Waypoint estimation logic is enabled then waypoint_distance would be in customer map values db
		if(Data.userData != null && Data.userData.getDriverTag().equalsIgnoreCase(DriverTagValues.WAYPOINT_DISTANCE.getType())){
			try {
				String wpDistance = getMapValue(getEngagementId(), Constants.KEY_WAYPOINT_DISTANCE, "0");
				Log.e("CustomerInfo getTotalDistance", "wpDistance = "+wpDistance);

				//check if waypoint distance is in specified range of metering distance
				// if yes then use waypoint distance else use metering distance for fare calculation
				double wpDist = Double.parseDouble(wpDistance);
				String range = Prefs.with(context).getString(Constants.KEY_DRIVER_WAYPOINT_DISTANCE_RANGE, "");
				Log.e("CustomerInfo getTotalDistance", "range = "+range+", meteringDistance="+meteringDistance);
				if(onEndRide && !TextUtils.isEmpty(range)){
					try {
						double rangeVal = Double.parseDouble(range) * meteringDistance / 100D;
						double lowerBound = meteringDistance - rangeVal;
						double upperBound = meteringDistance + rangeVal;
						Log.e("CustomerInfo getTotalDistance", "lowerBound="+lowerBound+", upperBound="+upperBound+", wpDist="+wpDist);
						if(lowerBound <= wpDist && wpDist <= upperBound){
							return wpDist;
						} else {
							return meteringDistance;
						}
					} catch (Exception ignored) {}
				}

				return wpDist;
			} catch (Exception ignored) {}
		}
		if(waypointDistance > 0 && Prefs.with(context).getInt(Constants.KEY_USE_WAYPOINT_DISTANCE_FOR_FARE, 0) == 1){
			return waypointDistance;
		}
		return meteringDistance;
	}

	public double getSPSavedDistance(double distance) {
		try {
			double startDistance = Double.parseDouble(getMapValue(engagementId, Constants.KEY_DISTANCE, "0"));
			if (distance < startDistance) {
				return 0;
			} else {
				return distance - startDistance;
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

	public double getTotalHaversineDistance(double haversineDistance){
		try {
			double haversineDistanceMark = Double.parseDouble(getMapValue(engagementId, Constants.KEY_HAVERSINE_DISTANCE, "0"));
			return haversineDistance - haversineDistanceMark;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return haversineDistance;
	}

	public long getElapsedRideTime(){
		try {
			return SystemClock.elapsedRealtime() - Long.parseLong(getMapValue(engagementId, Constants.KEY_RIDE_START_TIME, String.valueOf(SystemClock.elapsedRealtime())));
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}



	public long getTotalWaitTime(long waitTime) {
		try {
			return waitTime - Long.parseLong(getMapValue(engagementId, Constants.KEY_WAIT_TIME, "0"));
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
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

	public String getTimeDiff() {
		return timeDiff;
	}

	public void setTimeDiff(String timeDiff) {
		this.timeDiff = timeDiff;
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

	public static String getMapValue(int engagementId, String key, String defaultVal){
		return Database2.getInstance(MyApplication.getInstance()).getKeyValue(key+engagementId, defaultVal);
	}

	public static void clearMapValues(int engagementId){
		Database2.getInstance(MyApplication.getInstance()).removeKey(Constants.KEY_WAYPOINT_DISTANCE + engagementId);

		Database2.getInstance(MyApplication.getInstance()).removeKey(Constants.KEY_DISTANCE + engagementId);
		Database2.getInstance(MyApplication.getInstance()).removeKey(Constants.KEY_HAVERSINE_DISTANCE + engagementId);
		Database2.getInstance(MyApplication.getInstance()).removeKey(Constants.KEY_RIDE_START_TIME + engagementId);
		Database2.getInstance(MyApplication.getInstance()).removeKey(Constants.KEY_WAIT_TIME + engagementId);

		ArrayList<String> keys = Database2.getInstance(MyApplication.getInstance()).getAllKeys();
		Log.e("CustomerInfo:clearMapValues", "keys left="+keys);
	}

	public static void clearAllMapValues(){
		Database2.getInstance(MyApplication.getInstance()).removeKeyLike(Constants.KEY_WAYPOINT_DISTANCE);

		Database2.getInstance(MyApplication.getInstance()).removeKeyLike(Constants.KEY_DISTANCE);
		Database2.getInstance(MyApplication.getInstance()).removeKeyLike(Constants.KEY_HAVERSINE_DISTANCE);
		Database2.getInstance(MyApplication.getInstance()).removeKeyLike(Constants.KEY_RIDE_START_TIME);
		Database2.getInstance(MyApplication.getInstance()).removeKeyLike(Constants.KEY_WAIT_TIME);

		ArrayList<String> keys = Database2.getInstance(MyApplication.getInstance()).getAllKeys();
		Log.e("CustomerInfo:clearAllMapValues", "keys left="+keys);
	}



	public double getIncrementPercent() {
		return incrementPercent;
	}

	public void setIncrementPercent(double incrementPercent) {
		this.incrementPercent = incrementPercent;
	}

	public int getStepSize() {
		return stepSize;
	}

	public void setStepSize(int stepSize) {
		this.stepSize = stepSize;
	}

	public String getPickupAddress() {
		return pickupAddress;
	}

	public void setPickupAddress(String pickupAddress) {
		this.pickupAddress = pickupAddress;
	}
	public int getProgressValue(Context context){
		if(TextUtils.isEmpty(bidCreatedAt)){
			return 0;
		}
		double currentDiff = DateOperations.getTimeDifference(DateOperations.getCurrentTime(),bidCreatedAt);
		double total = Prefs.with(context).getLong(Constants.KEY_BID_TIMEOUT, 30000L);
		return (int) (100D - (currentDiff/total*100D));
	}

	public int getMultiDestCount() {
		return multiDestCount;
	}

	public void setMultiDestCount(int multiDestCount) {
		this.multiDestCount = multiDestCount;
	}

	public int getStopId(){
		return stopId;
	}

	public void setStopId(int stopId){
		this.stopId = stopId;
	}

}
