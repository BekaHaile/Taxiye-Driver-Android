package product.clicklabs.jugnoo.driver.datastructure;

public enum FlagRideStatus {
	END_RIDE_ADDED_DISPLACEMENT(0),
	END_RIDE_ADDED_DISTANCE(1),
	END_RIDE_ADDED_COMP_DIST(2)
	;

	private int ordinal;

	private FlagRideStatus(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
