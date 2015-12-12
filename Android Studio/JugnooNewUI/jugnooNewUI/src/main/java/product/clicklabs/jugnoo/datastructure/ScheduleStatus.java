package product.clicklabs.jugnoo.datastructure;

public enum ScheduleStatus {
	IN_QUEUE(0, "Request in queue"), 
	IN_PROCESS(1, "Request in process"),
	PROCESSED(2, "Request processed")
	;

	private int ordinal;
	private String statusString;

	private ScheduleStatus(int ordinal, String statusString) {
		this.ordinal = ordinal;
		this.statusString = statusString;
	}

	public int getOrdinal() {
		return ordinal;
	}
	
	public String getStatusString() {
		return statusString;
	}
}
