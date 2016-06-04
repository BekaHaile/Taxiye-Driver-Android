package product.clicklabs.jugnoo.driver.datastructure;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryInfo;

/**
 * Super class for customer info
 * @author shankar
 *
 */
public class CustomerInfo {
	
	public int engagementId;
	public int userId, referenceId;
	public String name, phoneNumber;
	public LatLng requestlLatLng;
	private int cachedApiEnabled;

	public String image, rating;
	public CouponInfo couponInfo;
	public PromoInfo promoInfo;
	public double jugnooBalance;
	public LatLng dropLatLng;
	public int meterFareApplicable;
	public int getJugnooFareEnabled;
	public int luggageChargesApplicable;
	public int waitingChargesApplicable;
	public double poolFare;
	public long poolTime;
	public double poolDistance;

	private int status;
	private String address, startTime;
	private double fareFactor;
	private int isPooled;

	private CustomerRideData customerRideData = new CustomerRideData();

	private int isDelivery;
	private ArrayList<DeliveryInfo> deliveryInfos;
	private int totalDeliveries;
	private double deliveryFare;



	public CustomerInfo(int engagementId, int userId, int referenceId, String name, String phoneNumber, LatLng requestlLatLng, int cachedApiEnabled,
						String image, String rating, CouponInfo couponInfo, PromoInfo promoInfo, double jugnooBalance,
						int meterFareApplicable, int jugnooFareButton, int luggageChargesApplicable, int waitTimeApplicable,
						int status, int isPooled, int isDelivery, String address, int totalDeliveries, double deliveryFare){
		this.engagementId = engagementId;
		this.userId = userId;
		this.referenceId = referenceId;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.requestlLatLng = requestlLatLng;
		this.cachedApiEnabled = cachedApiEnabled;

		this.image = image;
		this.rating = rating;
		this.couponInfo = couponInfo;
		this.promoInfo = promoInfo;
		this.jugnooBalance = jugnooBalance;

		this.image = this.image.replace("http://graph.facebook", "https://graph.facebook");
		this.dropLatLng = null;

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
		this.deliveryFare = deliveryFare;
	}


	public CustomerInfo(int engagementId){
		this.engagementId = engagementId;
	}

	public CustomerInfo(int engagementId, int userId, LatLng requestlLatLng, String startTime, String address,
						int referenceId, double fareFactor, int status, int isPooled, int isDelivery,
						int totalDeliveries, double deliveryFare){
		this.engagementId = engagementId;
		this.userId = userId;
		this.requestlLatLng = requestlLatLng;
		this.startTime = startTime;
		this.address = address;
		this.referenceId = referenceId;
		this.fareFactor = fareFactor;
		this.status = status;
		this.isPooled = isPooled;
		this.isDelivery = isDelivery;
		this.totalDeliveries = totalDeliveries;
		this.deliveryFare = deliveryFare;
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

	public CustomerRideData getCustomerRideData() {
		return customerRideData;
	}

	public double getPoolFare() {
		return poolFare;
	}

	public void setPoolFare(double poolFare) {
		this.poolFare = poolFare;
	}

	public long getPoolTime() {
		return poolTime;
	}

	public void setPoolTime(long poolTime) {
		this.poolTime = poolTime;
	}

	public double getPoolDistance() {
		return poolDistance;
	}

	public void setPoolDistance(double poolDistance) {
		this.poolDistance = poolDistance;
	}


	public void setCustomerFareValues(double poolFare, long poolTime, double poolDistance){
		this.poolFare = poolFare;
		this.poolTime = poolTime;
		this.poolDistance = poolDistance;
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

	public double getDeliveryFare() {
		return deliveryFare;
	}

	public void setDeliveryFare(double deliveryFare) {
		this.deliveryFare = deliveryFare;
	}
}
