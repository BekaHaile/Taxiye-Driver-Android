package product.clicklabs.jugnoo.driver.datastructure;

public enum DriverSubscriptionEnabled {
	DISABLED(0),
	ENABLED(1),
	MANDATORY(2)
	;

	private int ordinal;

	private DriverSubscriptionEnabled(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
