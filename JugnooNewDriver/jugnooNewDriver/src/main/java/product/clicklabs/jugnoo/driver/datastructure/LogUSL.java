package product.clicklabs.jugnoo.driver.datastructure;

/**
 * Created by aneeshbansal on 05/10/16.
 */
public class LogUSL {

	public String time, event;

	public LogUSL(String time, String event){
		this.time = time;
		this.event = event;
	}

	@Override
	public String toString() {
		return time+","+event;
	}

}
