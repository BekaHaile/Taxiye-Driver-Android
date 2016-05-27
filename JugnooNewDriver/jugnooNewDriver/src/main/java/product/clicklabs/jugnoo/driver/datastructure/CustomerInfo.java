package product.clicklabs.jugnoo.driver.datastructure;

import com.google.android.gms.maps.model.LatLng;

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

	public String image, rating, schedulePickupTime;
	public int freeRide;
	public CouponInfo couponInfo;
	public PromoInfo promoInfo;
	public double jugnooBalance;
	public LatLng dropLatLng;
	public int meterFareApplicable;
	public int getJugnooFareEnabled;
	public int luggageChargesApplicable;
	public int waitingChargesApplicable;

	private int status;
	private String address, startTime;
	private double fareFactor;
	private int isPooled;

	public CustomerInfo(int engagementId, int userId, int referenceId, String name, String phoneNumber, LatLng requestlLatLng, int cachedApiEnabled,
						String image, String rating, String schedulePickupTime, int freeRide,
						CouponInfo couponInfo, PromoInfo promoInfo, double jugnooBalance, int meterFareApplicable,
						int jugnooFareButton, int luggageChargesApplicable, int waitTimeApplicable,
						int status){
		this.engagementId = engagementId;
		this.userId = userId;
		this.referenceId = referenceId;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.requestlLatLng = requestlLatLng;
		this.cachedApiEnabled = cachedApiEnabled;

		this.image = image;
		this.rating = rating;
		this.schedulePickupTime = schedulePickupTime;
		this.freeRide = freeRide;
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
	}

	public CustomerInfo(int engagementId, int userId, int referenceId, String name, String phoneNumber, LatLng requestlLatLng, int cachedApiEnabled,
							String image, CouponInfo couponInfo, PromoInfo promoInfo, int status){
		this.engagementId = engagementId;
		this.userId = userId;
		this.referenceId = referenceId;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.requestlLatLng = requestlLatLng;
		this.cachedApiEnabled = cachedApiEnabled;

		this.image = image;
		this.rating = "4";
		this.schedulePickupTime = "";
		this.freeRide = 0;
		this.couponInfo = couponInfo;
		this.promoInfo = promoInfo;
		this.jugnooBalance = 0;
		this.dropLatLng = null;
		this.luggageChargesApplicable = 0;
		this.meterFareApplicable = 0;
		this.getJugnooFareEnabled = 1;
		this.waitingChargesApplicable = 0;
		this.setStatus(status);
	}


	public CustomerInfo(int engagementId){
		this.engagementId = engagementId;
	}

	public CustomerInfo(int engagementId, int userId, LatLng requestlLatLng, String startTime, String address,
						int referenceId, double fareFactor, int status, int isPooled){
		this.engagementId = engagementId;
		this.userId = userId;
		this.requestlLatLng = requestlLatLng;
		this.startTime = startTime;
		this.address = address;
		this.referenceId = referenceId;
		this.fareFactor = fareFactor;
		this.status = status;
		this.isPooled = isPooled;
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

	public String getSchedulePickupTime() {
		return schedulePickupTime;
	}

	public void setSchedulePickupTime(String schedulePickupTime) {
		this.schedulePickupTime = schedulePickupTime;
	}

	public int getFreeRide() {
		return freeRide;
	}

	public void setFreeRide(int freeRide) {
		this.freeRide = freeRide;
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
}
