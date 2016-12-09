package product.clicklabs.jugnoo.driver.datastructure;

/**
 * Created by aneeshbansal on 05/11/16.
 */
public enum State {
	SPLASH_INIT(0),
	SPLASH_LS(1),
	SPLASH_NO_NET(2),
	LOGIN(3),
	SIGNUP(4);

	private int ordinal;

	State(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}