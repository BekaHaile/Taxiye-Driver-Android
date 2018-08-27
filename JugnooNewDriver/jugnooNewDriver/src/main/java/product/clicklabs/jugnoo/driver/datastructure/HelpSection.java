package product.clicklabs.jugnoo.driver.datastructure;

import android.content.Context;

import product.clicklabs.jugnoo.driver.R;

public enum HelpSection {
	MAIL_US(-2, R.string.send_us_a_mail),
	CALL_US(-1, R.string.call_us),
	
	ABOUT(0, R.string.about),
	FAQ(1, R.string.faqs),
	PRIVACY(2, R.string.privacy_policy),
	TERMS(3, R.string.terms_of_use),
	FARE_DETAILS(4, R.string.fare_details),
	SCHEDULES_TNC(5, R.string.terms_of_schedule),
	DRIVER_AGREEMENT(9, R.string.driver_agreement)
	;

	private int ordinal;
	private int nameRes;

	private HelpSection(int ordinal, int nameRes) {
		this.ordinal = ordinal;
		this.nameRes = nameRes;
	}

	public int getOrdinal() {
		return ordinal;
	}
	
	public String getName(Context context) {
		return context.getString(nameRes);
	}
}
