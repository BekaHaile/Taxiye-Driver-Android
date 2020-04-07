package product.clicklabs.jugnoo.driver.dodo.datastructure;

import com.google.android.gms.maps.model.LatLng;

import product.clicklabs.jugnoo.driver.Data;

/**
 * Created by aneeshbansal on 30/05/16.
 */
public class DeliveryInfo {

	private int id, index, falseDelivery;
	private LatLng latLng;
	private String customerName;
	private String deliveryAddress;
	private String customerNo;
	private double amount;
	private int status, totalDelivery, delSuccess, delFail, loadUnload;

	private double distance = 0;
	private long deliveryTime = System.currentTimeMillis();
	private long waitTime = 0;
	private boolean sate = false;
	private String cancelReason;
	private String currency;


	public DeliveryInfo(int id, LatLng latLng, String customerName, String deliveryAddress,
						String customerNo, double amount, int status, double distance, long deliveryTime, long waitTime,
						String cancelReason, int index, boolean sate, int falseDelivery, int loadUnload,String currency) {
		this.id = id;
		this.latLng = latLng;
		this.customerName = customerName;
		this.deliveryAddress = deliveryAddress;
		this.customerNo = customerNo;
		this.amount = amount;
		this.status = status;
		this.distance = distance;
		this.deliveryTime = deliveryTime * 1000l;
		this.waitTime = waitTime * 1000l;
		this.cancelReason = cancelReason;
		this.index = index;
		this.sate = sate;
		this.falseDelivery = falseDelivery;
		this.loadUnload = loadUnload;
		this.currency = currency;
	}

	public void setReturnData(int totalDelivery, int delSuccess, int delFail){
		this.totalDelivery = totalDelivery;
		this.delSuccess= delSuccess;
		this.delFail = delFail;
	}

	public DeliveryInfo(int id) {
		this.id = id;
	}


	public String getCurrency() {
		return Data.getCurrencyNullSafety(currency);
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LatLng getLatLng() {
		return latLng;
	}

	public void setlatLng(LatLng latLng) {
		this.latLng = latLng;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public String getCustomerNo() {
		return customerNo;
	}

	public void setCustomerNo(String customerNo) {
		this.customerNo = customerNo;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	@Override
	public boolean equals(Object o) {
		try{
			return ((DeliveryInfo)o).getId() == getId();
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public long getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(long deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	public long getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(long waitTime) {
		this.waitTime = waitTime;
	}

	public void setDeliveryValues(double distance, long deliveryTime, long waitTime){
		setDistance(distance);
		setDeliveryTime(deliveryTime);
		setWaitTime(waitTime);
	}

	public String getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getDelFail() {
		return delFail;
	}

	public void setDelFail(int delFail) {
		this.delFail = delFail;
	}

	public int getDelSuccess() {
		return delSuccess;
	}

	public void setDelSuccess(int delSuccess) {
		this.delSuccess = delSuccess;
	}

	public int getTotalDelivery() {
		return totalDelivery;
	}

	public void setTotalDelivery(int totalDelivery) {
		this.totalDelivery = totalDelivery;
	}

	public boolean isSate() {
		return sate;
	}

	public void setSate(boolean sate) {
		this.sate = sate;
	}

	public int getFalseDelivery() {
		return falseDelivery;
	}

	public void setFalseDelivery(int falseDelivery) {
		this.falseDelivery = falseDelivery;
	}

	public int getLoadUnload() {
		return loadUnload;
	}

	public void setLoadUnload(int loadUnload) {
		this.loadUnload = loadUnload;
	}
}
