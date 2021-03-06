package product.clicklabs.jugnoo.driver.datastructure;

public enum BenefitType {
    DISCOUNTS(1),
    CAPPED_FARE(2),
    CASHBACKS(3)
	;

	private int ordinal;

	private BenefitType(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}

//      promo_type` int(11) NOT NULL COMMENT '1 for location insensitive, 2 for pickup based,\n3 for drop based',
//      benefit_type` int(11) NOT NULL COMMENT '1 for discounts, 2 for capped fare, 3 for\ncashbacks',