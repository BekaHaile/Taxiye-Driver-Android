package product.clicklabs.jugnoo.driver.datastructure;

public enum ProfileUpdateMode {
	NAME(0), 
	PHONE(2),
	PASSWORD(3)
	;

	private int ordinal;

	private ProfileUpdateMode(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
