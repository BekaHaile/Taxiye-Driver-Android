package product.clicklabs.jugnoo.driver.datastructure;

import java.util.HashMap;

public class PendingAPICall {
	
	public int id;
	public String url;
	public HashMap<String, String> nameValuePairs;
	
	public PendingAPICall(int id, String url, HashMap<String, String> nameValuePairs){
		this.id = id;
		this.url = url;
		this.nameValuePairs = nameValuePairs;
	}

	@Override
	public String toString() {
		return id + " " + url + " " + nameValuePairs.toString();
	}
}
