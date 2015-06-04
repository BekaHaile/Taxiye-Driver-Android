package product.clicklabs.jugnoo.driver.datastructure;

public class RideData {

	public int i;
	public double lat, lng;
	public long t;
	
	public RideData(int i, double lat, double lng, long t){
		this.i = i;
		this.lat = lat;
		this.lng = lng;
		this.t = t;
	}
	
	@Override
	public String toString() {
		return i+","+lat+","+lng+","+t;
	}
	
}
