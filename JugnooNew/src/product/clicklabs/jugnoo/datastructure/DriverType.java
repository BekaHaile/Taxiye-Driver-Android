package product.clicklabs.jugnoo.datastructure;

public class DriverType {
	
	public String driverTypeId;
	public String driverTypeName;
	public boolean selected;
	
	public DriverType(String driverTypeId, String driverTypeName){
		this.driverTypeId = driverTypeId;
		this.driverTypeName = driverTypeName;
		this.selected = false;
	}

}
