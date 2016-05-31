package product.clicklabs.jugnoo.driver.dodo.datastructure;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by aneeshbansal on 30/05/16.
 */
public class DeliveryInfo {

	private int id;
	private LatLng latLng;
	private String customerName;
	private String deliveryAddress;
	private String customerNo;
	private double amount;
	private int status;


	public DeliveryInfo(int id, LatLng latLng, String customerName, String deliveryAddress,
						String customerNo, double amount, int status) {
		this.id = id;
		this.latLng = latLng;
		this.customerName = customerName;
		this.deliveryAddress = deliveryAddress;
		this.customerNo = customerNo;
		this.amount = amount;
		this.status = status;
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
}
