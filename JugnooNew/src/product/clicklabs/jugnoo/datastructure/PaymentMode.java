package product.clicklabs.jugnoo.datastructure;

public enum PaymentMode {
	CASH(1), 
	WALLET(2)
	;

	private int ordinal;

	private PaymentMode(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
