package product.clicklabs.jugnoo.driver.utils;

/**
 * Created by aneeshbansal on 09/11/15.
 */
public class ProfileInfo {

	public String textViewDriverName;
	public int textViewDriverId;
	public int textViewRankCity;
	public int textViewRankOverall;
	public int textViewMonthlyValue;
	public int textViewRidesTakenValue;
	public int textViewRidesCancelledValue;
	public int textViewRidesMissedValue;
	public int textViewOnlineHoursValue;
	public String textViewTitleBarDEI;

	public ProfileInfo(String textViewDriverName, int textViewDriverId, int textViewRankCity, int textViewRankOverall,
					   int textViewMonthlyValue, int textViewRidesTakenValue, int textViewRidesCancelledValue,
					   int textViewRidesMissedValue, int textViewOnlineHoursValue, String textViewTitleBarDEI) {


		this.textViewDriverName = textViewDriverName;
		this.textViewDriverId = textViewDriverId;
		this.textViewRankCity = textViewRankCity;
		this.textViewRankOverall = textViewRankOverall;
		this.textViewMonthlyValue = textViewMonthlyValue;
		this.textViewRidesTakenValue = textViewRidesTakenValue;
		this.textViewRidesCancelledValue = textViewRidesCancelledValue;
		this.textViewRidesMissedValue = textViewRidesMissedValue;
		this.textViewOnlineHoursValue = textViewOnlineHoursValue;
		this.textViewTitleBarDEI = textViewTitleBarDEI;
	}
}