package product.clicklabs.jugnoo.driver.datastructure;

public class RingData {

	public int engagement;
	public long time;

	public RingData(int engagement, long time){
		this.engagement = engagement;
		this.time = time;
	}
	
	@Override
	public String toString() {
		return engagement+","+time;
	}
	
}
