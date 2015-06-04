package product.clicklabs.jugnoo.driver.datastructure;

public enum DriverScreenMode {
	D_INITIAL(0), 
	D_REQUEST_ACCEPT(1), 
	D_START_RIDE(2), 
	D_IN_RIDE(3), 
	D_RIDE_END(4);
	
	
	private int ordinal;
	
	private DriverScreenMode(int ordinal) {
		this.ordinal = ordinal;
	}
	
	public int getOrdinal() {
		return ordinal;
	}
}
