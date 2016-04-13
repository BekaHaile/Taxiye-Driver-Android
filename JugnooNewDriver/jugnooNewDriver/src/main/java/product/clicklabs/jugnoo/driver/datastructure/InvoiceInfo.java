package product.clicklabs.jugnoo.driver.datastructure;

public class InvoiceInfo {

	public int id;
	public double fare;
	public String toTime;
	public String fromTime;
	public String generatedTime;
	public String statusString;


	public InvoiceInfo(int id, double fare, String toTime,
					   String fromTime, String generatedTime, String statusString) {
		this.id = id;
		this.fare = fare;
		this.toTime = toTime;
		this.fromTime = fromTime;
		this.generatedTime = generatedTime;
		this.statusString = statusString;
	}


	public int Integer() {
		return id;
	}

}
