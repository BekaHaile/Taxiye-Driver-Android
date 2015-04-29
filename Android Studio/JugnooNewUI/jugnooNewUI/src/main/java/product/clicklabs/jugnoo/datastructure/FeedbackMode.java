package product.clicklabs.jugnoo.datastructure;

public enum FeedbackMode {
	AFTER_RIDE(0), 
	SUPPORT(1)
	;

	private int ordinal;

	private FeedbackMode(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
