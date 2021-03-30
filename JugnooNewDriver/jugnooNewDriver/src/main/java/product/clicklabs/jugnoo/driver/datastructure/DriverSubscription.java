package product.clicklabs.jugnoo.driver.datastructure;

public enum DriverSubscription {
	UNSUBSCRIBED(0),
	SUBSCRIBED(1),
	;

	private int ordinal;

	private DriverSubscription(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
