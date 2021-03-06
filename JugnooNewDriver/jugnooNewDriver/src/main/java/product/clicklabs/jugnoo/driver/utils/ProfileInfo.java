package product.clicklabs.jugnoo.driver.utils;

/**
 * Created by aneeshbansal on 09/11/15.
 */
public class ProfileInfo{

	public String textViewDriverName;
	public int textViewDriverId;
	public int textViewRankCity;
	public int textViewRankOverall;
	public double textViewMonthlyValue;
	public int textViewRidesTakenValue;
	public int textViewRidesCancelledValue;
	public int textViewRidesMissedValue;
	public int textViewOnlineHoursValue;
	public String textViewTitleBarDEI, accNo, ifscCode, bankName, bankLoc,currency;

	public ProfileInfo(String textViewDriverName, int textViewDriverId, int textViewRankCity, int textViewRankOverall,
					   double textViewMonthlyValue, int textViewRidesTakenValue, int textViewRidesMissedValue,
					   int textViewRidesCancelledValue, int textViewOnlineHoursValue, String textViewTitleBarDEI,
					   String accNo, String ifscCode, String bankName, String bankLoc,String currency) {


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
		this.accNo= accNo;
		this.ifscCode = ifscCode;
		this.bankName = bankName;
		this.bankLoc =bankLoc;
		this.currency =currency;

	}
}
