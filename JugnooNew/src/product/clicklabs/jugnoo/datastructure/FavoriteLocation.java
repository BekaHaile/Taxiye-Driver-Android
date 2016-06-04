package product.clicklabs.jugnoo.datastructure;

import com.google.android.gms.maps.model.LatLng;

public class FavoriteLocation{
	
	public int sNo;
	public String name;
	public LatLng latLng;
	
	public FavoriteLocation(int sNo, String name, LatLng latLng){
		this.sNo = sNo;
		this.name = name;
		this.latLng = latLng;
	}
	
}