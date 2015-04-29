package product.clicklabs.jugnoo.datastructure;

public enum EmailVerificationStatus {
	WRONG_EMAIL(-1), 
	EMAIL_UNVERIFIED(0),
	EMAIL_VERIFIED(1)
	;

	private int ordinal;

	private EmailVerificationStatus(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
