package product.clicklabs.jugnoo.driver.datastructure;

public enum HelpSection {
	MAIL_US(-2, "Send Us an Email"),
	CALL_US(-1, "Call Us"),
	
	ABOUT(0, "About Jugnoo"), 
	FAQ(1, "FAQs"),
	PRIVACY(2, "Privacy Policy"),
	TERMS(3, "Terms of Use"),
	FARE_DETAILS(4, "Fare Details"),
	SCHEDULES_TNC(5, "Terms of Schedule")
	;

	private int ordinal;
	private String name;

	private HelpSection(int ordinal, String name) {
		this.ordinal = ordinal;
		this.name = name;
	}

	public int getOrdinal() {
		return ordinal;
	}
	
	public String getName() {
		return name;
	}
}
