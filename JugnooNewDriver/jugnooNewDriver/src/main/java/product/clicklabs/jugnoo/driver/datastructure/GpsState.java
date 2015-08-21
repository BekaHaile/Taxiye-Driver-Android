package product.clicklabs.jugnoo.driver.datastructure;

public enum GpsState {
    ZERO_TWO(0),
    TWO_LESS_FOUR(1),
	GREATER_FOUR(2),
	GREATER_SIX(3)
	;

	private int ordinal;

	private GpsState(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}