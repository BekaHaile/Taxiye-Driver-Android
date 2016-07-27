package product.clicklabs.jugnoo.driver.datastructure;

public class RideData {

	public int i;
	public double lat, lng, accDistance;
	public long t;
	
	public RideData(int i, double lat, double lng, long t, double accDistance){
		this.i = i;
		this.lat = lat;
		this.lng = lng;
		this.t = t;
		this.accDistance = accDistance;
	}
	
	@Override
	public String toString() {
		return i+","+lat+","+lng+","+t+","+accDistance;
	}
	
}
