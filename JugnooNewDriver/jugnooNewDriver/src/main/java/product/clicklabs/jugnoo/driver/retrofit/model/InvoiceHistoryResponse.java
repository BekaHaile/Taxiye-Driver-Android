package product.clicklabs.jugnoo.driver.retrofit.model;

/**
 * Created by aneeshbansal on 12/04/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class InvoiceHistoryResponse {

	@SerializedName("data")
	@Expose
	private List<Datum> data = new ArrayList<Datum>();
	@SerializedName("flag")
	@Expose
	private Integer flag;

	/**
	 *
	 * @return
	 * The data
	 */
	public List<Datum> getData() {
		return data;
	}

	/**
	 *
	 * @param data
	 * The data
	 */
	public void setData(List<Datum> data) {
		this.data = data;
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

	public class Datum {

		@SerializedName("invoice_id")
		@Expose
		private Integer invoiceId;
		@SerializedName("amount_to_be_paid")
		@Expose
		private Double amountToBePaid;
		@SerializedName("from_date")
		@Expose
		private String fromDate;
		@SerializedName("to_date")
		@Expose
		private String toDate;
		@SerializedName("invoice_status")
		@Expose
		private String invoiceStatus;
		@SerializedName("invoicing_date")
		@Expose
		private String invoiceDate;


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
		 * The fromDate
		 */
		public String getFromDate() {
			return fromDate;
		}

		/**
		 *
		 * @param fromDate
		 * The from_date
		 */
		public void setFromDate(String fromDate) {
			this.fromDate = fromDate;
		}

		/**
		 *
		 * @return
		 * The toDate
		 */
		public String getToDate() {
			return toDate;
		}

		/**
		 *
		 * @param toDate
		 * The to_date
		 */
		public void setToDate(String toDate) {
			this.toDate = toDate;
		}

		/**
		 *
		 * @return
		 * The invoiceStatus
		 */
		public String getInvoiceStatus() {
			return invoiceStatus;
		}

		/**
		 *
		 * @param invoiceStatus
		 * The invoice_status
		 */
		public void setInvoiceStatus(String invoiceStatus) {
			this.invoiceStatus = invoiceStatus;
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