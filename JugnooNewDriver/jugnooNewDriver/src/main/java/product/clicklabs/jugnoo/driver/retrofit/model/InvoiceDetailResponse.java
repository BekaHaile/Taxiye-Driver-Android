package product.clicklabs.jugnoo.driver.retrofit.model;

/**
 * Created by aneeshbansal on 13/04/16.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InvoiceDetailResponse {

	@SerializedName("invoiceDetails")
	@Expose
	private InvoiceDetails invoiceDetails;
	@SerializedName("flag")
	@Expose
	private Integer flag;

	/**
	 *
	 * @return
	 * The invoiceDetails
	 */
	public InvoiceDetails getInvoiceDetails() {
		return invoiceDetails;
	}

	/**
	 *
	 * @param invoiceDetails
	 * The invoiceDetails
	 */
	public void setInvoiceDetails(InvoiceDetails invoiceDetails) {
		this.invoiceDetails = invoiceDetails;
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

	public class InvoiceDetails {

		@SerializedName("invoice_id")
		@Expose
		private Integer invoiceId;
		@SerializedName("status")
		@Expose
		private String status;
		@SerializedName("amount_to_be_paid")
		@Expose
		private Double amountToBePaid;
		@SerializedName("invoicing_from_date")
		@Expose
		private String invoicingFromDate;
		@SerializedName("invoicing_to_date")
		@Expose
		private String invoicingToDate;
		@SerializedName("cancel_distance_subsidy")
		@Expose
		private Double cancelDistanceSubsidy;
		@SerializedName("manual_charges")
		@Expose
		private Double manualCharges;
		@SerializedName("manual_charges_reason")
		@Expose
		private String manualChargesReason;
		@SerializedName("phone_deductions")
		@Expose
		private Double phoneDeductions;
		@SerializedName("referral_amount")
		@Expose
		private Double referralAmount;
		@SerializedName("money_transacted")
		@Expose
		private Double moneyTransacted;
		@SerializedName("outstanding_amount")
		@Expose
		private Double outstandingAmount;
		@SerializedName("actual_fare")
		@Expose
		private Double actualFare;
		@SerializedName("paid_by_customer")
		@Expose
		private Double paidByCustomer;
		@SerializedName("paid_by_jugnoo")
		@Expose
		private Double paidByJugnoo;
		@SerializedName("paid_using_wallet")
		@Expose
		private Double paidUsingWallet;
		@SerializedName("jugnoo_commision")
		@Expose
		private Double jugnooCommision;
		@SerializedName("total_driver_earnings")
		@Expose
		private Double totalDriverEarnings;
		@SerializedName("money_transacted_to_driver")
		@Expose
		private Double moneyTransactedToDriver;
		@SerializedName("invoicing_date")
		@Expose
		private String invoiceDate;
		@SerializedName("paytm_driver_transfer")
		@Expose
		private Double paytmCash;
		@SerializedName("ride_money")
		@Expose
		private Double rideMoney;
		@SerializedName("total_amount")
		@Expose
		private Double totalAmount;


		public Double getRideMoney() {
			return rideMoney;
		}

		public void setRideMoney(Double rideMoney) {
			this.rideMoney = rideMoney;
		}

		public Double getTotalAmount() {
			return totalAmount;
		}

		public void setTotalAmount(Double totalAmount) {
			this.totalAmount = totalAmount;
		}

		/**
		 *
		 * @return
		 * The invoiceId
		 */
		public Integer getInvoiceId() {
			return invoiceId;
		}

		/**
		 *
		 * @param invoiceId
		 * The invoice_id
		 */
		public void setInvoiceId(Integer invoiceId) {
			this.invoiceId = invoiceId;
		}

		/**
		 *
		 * @return
		 * The status
		 */
		public String getStatus() {
			return status;
		}

		/**
		 *
		 * @param status
		 * The status
		 */
		public void setStatus(String status) {
			this.status = status;
		}

		/**
		 *
		 * @return
		 * The amountToBePaid
		 */
		public Double getAmountToBePaid() {
			return amountToBePaid;
		}

		/**
		 *
		 * @param amountToBePaid
		 * The amount_to_be_paid
		 */
		public void setAmountToBePaid(Double amountToBePaid) {
			this.amountToBePaid = amountToBePaid;
		}

		/**
		 *
		 * @return
		 * The invoicingFromDate
		 */
		public String getInvoicingFromDate() {
			return invoicingFromDate;
		}

		/**
		 *
		 * @param invoicingFromDate
		 * The invoicing_from_date
		 */
		public void setInvoicingFromDate(String invoicingFromDate) {
			this.invoicingFromDate = invoicingFromDate;
		}

		/**
		 *
		 * @return
		 * The invoicingToDate
		 */
		public String getInvoicingToDate() {
			return invoicingToDate;
		}

		/**
		 *
		 * @param invoicingToDate
		 * The invoicing_to_date
		 */
		public void setInvoicingToDate(String invoicingToDate) {
			this.invoicingToDate = invoicingToDate;
		}

		/**
		 *
		 * @return
		 * The cancelDistanceSubsidy
		 */
		public Double getCancelDistanceSubsidy() {
			return cancelDistanceSubsidy;
		}

		/**
		 *
		 * @param cancelDistanceSubsidy
		 * The cancel_distance_subsidy
		 */
		public void setCancelDistanceSubsidy(Double cancelDistanceSubsidy) {
			this.cancelDistanceSubsidy = cancelDistanceSubsidy;
		}

		/**
		 *
		 * @return
		 * The manualCharges
		 */
		public Double getManualCharges() {
			return manualCharges;
		}

		/**
		 *
		 * @param manualCharges
		 * The manual_charges
		 */
		public void setManualCharges(Double manualCharges) {
			this.manualCharges = manualCharges;
		}

		/**
		 *
		 * @return
		 * The manualChargesReason
		 */
		public String getManualChargesReason() {
			return manualChargesReason;
		}

		/**
		 *
		 * @param manualChargesReason
		 * The manual_charges_reason
		 */
		public void setManualChargesReason(String manualChargesReason) {
			this.manualChargesReason = manualChargesReason;
		}

		/**
		 *
		 * @return
		 * The phoneDeductions
		 */
		public Double getPhoneDeductions() {
			return phoneDeductions;
		}

		/**
		 *
		 * @param phoneDeductions
		 * The phone_deductions
		 */
		public void setPhoneDeductions(Double phoneDeductions) {
			this.phoneDeductions = phoneDeductions;
		}

		/**
		 *
		 * @return
		 * The referralAmount
		 */
		public Double getReferralAmount() {
			return referralAmount;
		}

		/**
		 *
		 * @param referralAmount
		 * The referral_amount
		 */
		public void setReferralAmount(Double referralAmount) {
			this.referralAmount = referralAmount;
		}

		/**
		 *
		 * @return
		 * The moneyTransacted
		 */
		public Double getMoneyTransacted() {
			return moneyTransacted;
		}

		/**
		 *
		 * @param moneyTransacted
		 * The money_transacted
		 */
		public void setMoneyTransacted(Double moneyTransacted) {
			this.moneyTransacted = moneyTransacted;
		}

		/**
		 *
		 * @return
		 * The outstandingAmount
		 */
		public Double getOutstandingAmount() {
			return outstandingAmount;
		}

		/**
		 *
		 * @param outstandingAmount
		 * The outstanding_amount
		 */
		public void setOutstandingAmount(Double outstandingAmount) {
			this.outstandingAmount = outstandingAmount;
		}

		/**
		 *
		 * @return
		 * The actualFare
		 */
		public Double getActualFare() {
			return actualFare;
		}

		/**
		 *
		 * @param actualFare
		 * The actual_fare
		 */
		public void setActualFare(Double actualFare) {
			this.actualFare = actualFare;
		}

		/**
		 *
		 * @return
		 * The paidByCustomer
		 */
		public Double getPaidByCustomer() {
			return paidByCustomer;
		}

		/**
		 *
		 * @param paidByCustomer
		 * The paid_by_customer
		 */
		public void setPaidByCustomer(Double paidByCustomer) {
			this.paidByCustomer = paidByCustomer;
		}

		/**
		 *
		 * @return
		 * The paidByJugnoo
		 */
		public Double getPaidByJugnoo() {
			return paidByJugnoo;
		}

		/**
		 *
		 * @param paidByJugnoo
		 * The paid_by_jugnoo
		 */
		public void setPaidByJugnoo(Double paidByJugnoo) {
			this.paidByJugnoo = paidByJugnoo;
		}

		/**
		 *
		 * @return
		 * The paidUsingWallet
		 */
		public Double getPaidUsingWallet() {
			return paidUsingWallet;
		}

		/**
		 *
		 * @param paidUsingWallet
		 * The paid_using_wallet
		 */
		public void setPaidUsingWallet(Double paidUsingWallet) {
			this.paidUsingWallet = paidUsingWallet;
		}

		/**
		 *
		 * @return
		 * The jugnooCommision
		 */
		public Double getJugnooCommision() {
			return jugnooCommision;
		}

		/**
		 *
		 * @param jugnooCommision
		 * The jugnoo_commision
		 */
		public void setJugnooCommision(Double jugnooCommision) {
			this.jugnooCommision = jugnooCommision;
		}

		/**
		 *
		 * @return
		 * The totalDriverEarnings
		 */
		public Double getTotalDriverEarnings() {
			return totalDriverEarnings;
		}

		/**
		 *
		 * @param totalDriverEarnings
		 * The total_driver_earnings
		 */
		public void setTotalDriverEarnings(Double totalDriverEarnings) {
			this.totalDriverEarnings = totalDriverEarnings;
		}

		/**
		 *
		 * @return
		 * The moneyTransactedToDriver
		 */
		public Double getMoneyTransactedToDriver() {
			return moneyTransactedToDriver;
		}

		/**
		 *
		 * @param moneyTransactedToDriver
		 * The money_transacted_to_driver
		 */
		public void setMoneyTransactedToDriver(Double moneyTransactedToDriver) {
			this.moneyTransactedToDriver = moneyTransactedToDriver;
		}

		/**
		 *
		 * @return
		 * The paytmCash
		 */
		public Double getPaytmCash() {
			return paytmCash;
		}

		/**
		 *
		 * @param paytmCash
		 * The paytm_cash
		 */
		public void setPaytmCash(Double paytmCash) {
			this.paytmCash = paytmCash;
		}

		/**
		 *
		 * @return
		 * The invoiceDate
		 */
		public String getInvoiceDate() {
			return invoiceDate;
		}

		/**
		 *
		 * @param invoiceDate
		 * The invoice_date
		 */
		public void setInvoiceDate(String invoiceDate) {
			this.invoiceDate = invoiceDate;
		}

	}

}


