package product.clicklabs.jugnoo.driver.dodo.datastructure;

public enum EndDeliveryStatus {
	NEXT(0),
	RETURN(1),
	END(2)
	;

	private int ordinal;

	private EndDeliveryStatus(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
