package product.clicklabs.jugnoo.driver.datastructure;

public enum DriverScreenMode {
	D_OFFLINE(-1),
	D_INITIAL(0), 
	D_REQUEST_ACCEPT(1),
    D_ARRIVED(2),
    D_START_RIDE(3),
	D_IN_RIDE(4),
	D_RIDE_END(5),
    D_BEFORE_END_OPTIONS(6);
	
	
	private int ordinal;
	
	DriverScreenMode(int ordinal) {
		this.ordinal = ordinal;
	}
	
	public int getOrdinal() {
		return ordinal;
	}
}
