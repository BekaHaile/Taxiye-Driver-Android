package product.clicklabs.jugnoo.driver.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aneeshbansal on 22/09/16.
 */


public class InvoiceDetailResponseNew {

	@SerializedName("period")
	@Expose
	private String period;
	@SerializedName("earnings")
	@Expose
	private Double earnings;
	@SerializedName("invoice_status")
	@Expose
	private String invoiceStatus;
	@SerializedName("earning_params")
	@Expose
	private List<EarningParam> earningParams = new ArrayList<EarningParam>();
	@SerializedName("paid_using_cash")
	@Expose
	private Double paidUsingCash;
	@SerializedName("account")
	@Expose
	private Double account;
	@SerializedName("total_distance_travelled")
	@Expose
	private String totalDistanceTravelled;
	@SerializedName("total_trips")
	@Expose
	private Integer totalTrips;
	@SerializedName("delivery_trips")
	@Expose
	private Integer totalDelivery;
	@SerializedName("daily_breakup")
	@Expose
	private List<DailyBreakup> dailyBreakup = new ArrayList<DailyBreakup>();
	@SerializedName("fare_per_km")
	private Double farePerKm;

	public Double getFarePerKm() {
		return farePerKm;
	}

	/**
	 *
	 * @return
	 * The period
	 */
	public String getPeriod() {
		return period;
	}

	/**
	 *
	 * @param period
	 * The period
	 */
	public void setPeriod(String period) {
		this.period = period;
	}

	/**
	 *
	 * @return
	 * The earnings
	 */
	public Double getEarnings() {
		return earnings;
	}

	/**
	 *
	 * @param earnings
	 * The earnings
	 */
	public void setEarnings(Double earnings) {
		this.earnings = earnings;
	}

	public String getInvoiceStatus() {
		return invoiceStatus;
	}

	public void setInvoiceStatus(String invoiceStatus) {
		this.invoiceStatus = invoiceStatus;
	}

	/**
	 *
	 * @return
	 * The earningParams
	 */
	public List<EarningParam> getEarningParams() {
		return earningParams;
	}

	/**
	 *
	 * @param earningParams
	 * The earning_params
	 */
	public void setEarningParams(List<EarningParam> earningParams) {
		this.earningParams = earningParams;
	}

	/**
	 *
	 * @return
	 * The paidUsingCash
	 */
	public Double getPaidUsingCash() {
		return paidUsingCash;
	}

	/**
	 *
	 * @param paidUsingCash
	 * The paid_using_cash
	 */
	public void setPaidUsingCash(Double paidUsingCash) {
		this.paidUsingCash = paidUsingCash;
	}

	/**
	 *
	 * @return
	 * The account
	 */
	public Double getAccount() {
		return account;
	}

	/**
	 *
	 * @param account
	 * The account
	 */
	public void setAccount(Double account) {
		this.account = account;
	}

	/**
	 *
	 * @return
	 * The totalDistanceTravelled
	 */
	public String getTotalDistanceTravelled() {
		return totalDistanceTravelled;
	}

	/**
	 *
	 * @param totalDistanceTravelled
	 * The total_distance_travelled
	 */
	public void setTotalDistanceTravelled(String totalDistanceTravelled) {
		this.totalDistanceTravelled = totalDistanceTravelled;
	}

	/**
	 *
	 * @return
	 * The totalTrips
	 */
	public Integer getTotalTrips() {
		return totalTrips;
	}

	/**
	 *
	 * @param totalTrips
	 * The total_trips
	 */
	public void setTotalTrips(Integer totalTrips) {
		this.totalTrips = totalTrips;
	}

	public Integer getTotalDelivery() {
		return totalDelivery;
	}

	public void setTotalDelivery(Integer totalDelivery) {
		this.totalDelivery = totalDelivery;
	}

	/**
	 *
	 * @return
	 * The dailyBreakup
	 */
	public List<DailyBreakup> getDailyBreakup() {
		return dailyBreakup;
	}

	/**
	 *
	 * @param dailyBreakup
	 * The daily_breakup
	 */
	public void setDailyBreakup(List<DailyBreakup> dailyBreakup) {
		this.dailyBreakup = dailyBreakup;
	}

	public class EarningParam {

		@SerializedName("text")
		@Expose
		private String text;
		@SerializedName("value")
		@Expose
		private Double value;

		/**
		 *
		 * @return
		 * The text
		 */
		public String getText() {
			return text;
		}

		/**
		 *
		 * @param text
		 * The text
		 */
		public void setText(String text) {
			this.text = text;
		}

		/**
		 *
		 * @return
		 * The value
		 */
		public Double getValue() {
			return value;
		}

		/**
		 *
		 * @param value
		 * The value
		 */
		public void setValue(Double value) {
			this.value = value;
		}

	}

	public class DailyBreakup {

		@SerializedName("earnings")
		@Expose
		private Double earnings;
		@SerializedName("date")
		@Expose
		private String date;
		@SerializedName("day")
		@Expose
		private String day;

		/**
		 *
		 * @return
		 * The earnings
		 */
		public Double getEarnings() {
			return earnings;
		}

		/**
		 *
		 * @param earnings
		 * The earnings
		 */
		public void setEarnings(Double earnings) {
			this.earnings = earnings;
		}

		/**
		 *
		 * @return
		 * The date
		 */
		public String getDate() {
			return date;
		}

		/**
		 *
		 * @param date
		 * The date
		 */
		public void setDate(String date) {
			this.date = date;
		}

		/**
		 *
		 * @return
		 * The day
		 */
		public String getDay() {
			return day;
		}

		/**
		 *
		 * @param day
		 * The day
		 */
		public void setDay(String day) {
			this.day = day;
		}

	}


}
