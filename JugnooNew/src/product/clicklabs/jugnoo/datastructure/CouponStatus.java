package product.clicklabs.jugnoo.datastructure;

public enum CouponStatus {
	EXPIRED(0), 
	ACTIVE(1),
	REDEEMED(2)
	;

	private int ordinal;

	private CouponStatus(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
