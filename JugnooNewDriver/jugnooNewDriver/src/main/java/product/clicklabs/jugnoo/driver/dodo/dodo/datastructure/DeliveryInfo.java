package product.clicklabs.jugnoo.driver.dodo.dodo.datastructure;

/**
 * Created by aneeshbansal on 30/05/16.
 */
public class DeliveryInfo {

	public int id;
	public double distanceAway;
	public String customerName;
	public String deliverAddress;
	public String customerNo;


	public DeliveryInfo(int id, double distanceAway, String customerName, String deliverAddress,
						String customerNo) {
		this.id = id;
		this.distanceAway = distanceAway;
		this.customerName = customerName;
		this.deliverAddress = deliverAddress;
		this.customerNo = customerNo;
	}


	public int Integer() {
		return id;
	}

	public double getDistanceAway() {
		return distanceAway;
	}

	public void setDistanceAway(double distanceAway) {
		this.distanceAway = distanceAway;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getDeliverAddress() {
		return deliverAddress;
	}

	public void setDeliverAddress(String deliverAddress) {
		this.deliverAddress = deliverAddress;
	}

	public String getCustomerNo() {
		return customerNo;
	}

	public void setCustomerNo(String customerNo) {
		this.customerNo = customerNo;
	}
}
