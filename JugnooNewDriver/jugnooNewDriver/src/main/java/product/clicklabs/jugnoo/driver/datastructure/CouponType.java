package product.clicklabs.jugnoo.driver.datastructure;

public enum CouponType {
    LOCATION_INSENSITIVE(1),
    PICKUP_BASED(2),
    DROP_BASED(3)
	;

	private int ordinal;

	private CouponType(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}

//promo_type` int(11) NOT NULL COMMENT '1 for location insensitive, 2 for pickup based,\n3 for drop based',
//    `benefit_type` int(11) NOT NULL COMMENT '1 for discounts, 2 for capped fare, 3 for\ncashbacks',