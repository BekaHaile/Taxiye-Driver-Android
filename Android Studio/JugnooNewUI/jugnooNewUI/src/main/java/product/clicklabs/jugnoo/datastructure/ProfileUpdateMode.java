package product.clicklabs.jugnoo.datastructure;

public enum ProfileUpdateMode {
	NAME(0), 
	EMAIL(1),
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
