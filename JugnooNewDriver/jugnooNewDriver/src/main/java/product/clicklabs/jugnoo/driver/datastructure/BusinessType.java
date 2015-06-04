package product.clicklabs.jugnoo.driver.datastructure;

public enum BusinessType {
	AUTOS(1), 
	MEALS(2),
	FATAFAT(3)
	;

	private int ordinal;

	private BusinessType(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
	
}
