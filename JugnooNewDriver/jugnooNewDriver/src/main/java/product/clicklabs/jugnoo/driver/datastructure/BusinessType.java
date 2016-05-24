package product.clicklabs.jugnoo.driver.datastructure;

public enum BusinessType {
	AUTOS(1)
	;

	private int ordinal;

	private BusinessType(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
	
}
