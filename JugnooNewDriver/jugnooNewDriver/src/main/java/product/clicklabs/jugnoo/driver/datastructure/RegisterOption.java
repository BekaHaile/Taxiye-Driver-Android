package product.clicklabs.jugnoo.driver.datastructure;

/**
 * Created by aneeshbansal on 11/08/16.
 */
public enum RegisterOption {
	NO_OPTION(0),
	ONLY_TOOKAN(1),
	BOTH_TOOKAN_SELF_REGISTER(2),
	ONLY_SELF_REGISTER(3)
	;

	private int ordinal;

	RegisterOption(int ordinal){
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}
}
