package product.clicklabs.jugnoo.datastructure;

public enum ScheduleOperationMode {
	INSERT(0),
	MODIFY(1)
	;

	private int ordinal;

	private ScheduleOperationMode(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
	
}
