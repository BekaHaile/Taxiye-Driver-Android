package product.clicklabs.jugnoo.driver.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aneeshbansal on 19/09/16.
 */

public class DriverEarningsResponse {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("earnings")
	@Expose
	private List<Earning> earnings = new ArrayList<Earning>();
	@SerializedName("previous_invoice_id")
	@Expose
	private Integer previousInvoiceId;
	@SerializedName("next_invoice_id")
	@Expose
	private Integer nextInvoiceId;
	@SerializedName("current_invoice_id")
	@Expose
	private Integer currentInvoiceId;
	@SerializedName("jugnoo_balance")
	@Expose
	private Double jugnooBalance;
	@SerializedName("period")
	@Expose
	private String period;
	@SerializedName("recharge_options")
	@Expose
	private List<RechargeOption> rechargeOptions = null;

	public List<RechargeOption> getRechargeOptions() {
		return rechargeOptions;
	}

	public void setRechargeOptions(List<RechargeOption> rechargeOptions) {
		this.rechargeOptions = rechargeOptions;
	}

	/**
	 *
	 * @return
	 * The flag
	 */
	public Integer getFlag() {
		return flag;
	}

	/**
	 *
	 * @param flag
	 * The flag
	 */
	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	/**
	 *
	 * @return
	 * The earnings
	 */
	public List<Earning> getEarnings() {
		return earnings;
	}

	/**
	 *
	 * @param earnings
	 * The earnings
	 */
	public void setEarnings(List<Earning> earnings) {
		this.earnings = earnings;
	}

	/**
	 *
	 * @return
	 * The previousInvoiceId
	 */
	public Integer getPreviousInvoiceId() {
		return previousInvoiceId;
	}

	/**
	 *
	 * @param previousInvoiceId
	 * The previous_invoice_id
	 */
	public void setPreviousInvoiceId(Integer previousInvoiceId) {
		this.previousInvoiceId = previousInvoiceId;
	}

	/**
	 *
	 * @return
	 * The nextInvoiceId
	 */
	public Integer getNextInvoiceId() {
		return nextInvoiceId;
	}

	/**
	 *
	 * @param nextInvoiceId
	 * The next_invoice_id
	 */
	public void setNextInvoiceId(Integer nextInvoiceId) {
		this.nextInvoiceId = nextInvoiceId;
	}

	/**
	 *
	 * @return
	 * The currentInvoiceId
	 */
	public Integer getCurrentInvoiceId() {
		return currentInvoiceId;
	}

	/**
	 *
	 * @param currentInvoiceId
	 * The current_invoice_id
	 */
	public void setCurrentInvoiceId(Integer currentInvoiceId) {
		this.currentInvoiceId = currentInvoiceId;
	}


	/**
	 *
	 * @return
	 * The jugnooBalance
	 */
	public Double getJugnooBalance() {
		return jugnooBalance;
	}

	/**
	 *
	 * @param jugnooBalance
	 * The jugnoo_balance
	 */
	public void setJugnooBalance(Double jugnooBalance) {
		this.jugnooBalance = jugnooBalance;
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


	public class Earning {

		@SerializedName("earnings")
		@Expose
		private Integer earnings;
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
		public Integer getEarnings() {
			return earnings;
		}

		/**
		 *
		 * @param earnings
		 * The earnings
		 */
		public void setEarnings(Integer earnings) {
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

	public class RechargeOption {

		@SerializedName("name")
		@Expose
		private String name;
		@SerializedName("is_active")
		@Expose
		private Integer isActive;
		@SerializedName("tag")
		@Expose
		private String value;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getIsActive() {
			return isActive;
		}

		public void setIsActive(Integer isActive) {
			this.isActive = isActive;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}

}
