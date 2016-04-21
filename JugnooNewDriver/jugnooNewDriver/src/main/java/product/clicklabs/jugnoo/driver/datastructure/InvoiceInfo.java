package product.clicklabs.jugnoo.driver.datastructure;

public class InvoiceInfo {

	public int id;
	public double fare;
	public String fromTime;
	public String toTime;
	public String generatedTime;
	public String statusString;


	public InvoiceInfo(int id, double fare, String fromTime, String toTime,
					    String generatedTime, String statusString) {
		this.id = id;
		this.fare = fare;
		this.fromTime = fromTime;
		this.toTime = toTime;
		this.generatedTime = generatedTime;
		this.statusString = statusString;
	}


	public int Integer() {
		return id;
	}

}
