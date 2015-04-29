package product.clicklabs.jugnoo.datastructure;

public enum PromotionApplyMode {
	BEFORE_RIDE(0), 
	AFTER_SCHEDULE(1)
	;

	private int ordinal;

	private PromotionApplyMode(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
