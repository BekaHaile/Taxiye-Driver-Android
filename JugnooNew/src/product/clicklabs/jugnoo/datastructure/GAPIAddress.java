package product.clicklabs.jugnoo.datastructure;

import java.util.ArrayList;

public class GAPIAddress{
	
	public ArrayList<String> addressComponents;
	public String formattedAddress, postalCode;
	
	public GAPIAddress(ArrayList<String> addressComponents, String formattedAddress, String postalCode){
		this.addressComponents = addressComponents;
		this.postalCode = postalCode;
	}
	
	@Override
	public String toString() {
		return addressComponents.toString();
	}
}