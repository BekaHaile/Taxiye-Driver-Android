package product.clicklabs.jugnoo.datastructure;

public enum TransactionType {
	CREDIT(1), 
	DEBIT(2)
	;

	private int ordinal;

	private TransactionType(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
