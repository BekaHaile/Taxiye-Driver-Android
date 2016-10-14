package product.clicklabs.jugnoo.driver.datastructure;

public class UsageData {


	public String time, receive, sent;

	public UsageData(String time, String receive, String sent){
		this.time = time;
		this.receive = receive;
		this.sent = sent;
	}
	
	@Override
	public String toString() {
		return time+","+receive+","+sent;
	}
	
}
