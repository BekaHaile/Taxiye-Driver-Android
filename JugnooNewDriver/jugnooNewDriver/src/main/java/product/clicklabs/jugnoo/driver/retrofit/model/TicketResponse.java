package product.clicklabs.jugnoo.driver.retrofit.model;

/**
 * Created by aneeshbansal on 16/03/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class TicketResponse {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("ticket_data")
	@Expose
	private List<TicketDatum> ticketData = null;

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	public List<TicketDatum> getTicketData() {
		return ticketData;
	}

	public void setTicketData(List<TicketDatum> ticketData) {
		this.ticketData = ticketData;
	}

	public class TicketDatum {

		@SerializedName("ticket_id")
		@Expose
		private Integer ticketId;
		@SerializedName("eng_id")
		@Expose
		private Integer engId;
		@SerializedName("status")
		@Expose
		private String status;
		@SerializedName("opening_date")
		@Expose
		private String openingDate;
		@SerializedName("closing_date")
		@Expose
		private String closingDate;
		@SerializedName("last_updated_on")
		@Expose
		private String lastUpdated;
		@SerializedName("ride_type")
		@Expose
		private String rideType;
		@SerializedName("issue_type")
		@Expose
		private String issueType;
		@SerializedName("amount")
		@Expose
		private Double manualAdjustment;

		public Integer getTicketId() {
			return ticketId;
		}

		public void setTicketId(Integer ticketId) {
			this.ticketId = ticketId;
		}

		public Integer getEngId() {
			return engId;
		}

		public void setEngId(Integer engId) {
			this.engId = engId;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getOpeningDate() {
			return openingDate;
		}

		public void setOpeningDate(String openingDate) {
			this.openingDate = openingDate;
		}

		public String getClosingDate() {
			return closingDate;
		}

		public void setClosingDate(String closingDate) {
			this.closingDate = closingDate;
		}

		public String getLastUpdated() {
			return lastUpdated;
		}

		public void setLastUpdated(String lastUpdated) {
			this.lastUpdated = lastUpdated;
		}

		public String getRideType() {
			return rideType;
		}

		public void setRideType(String rideType) {
			this.rideType = rideType;
		}

		public String getIssueType() {
			return issueType;
		}

		public void setIssueType(String issueType) {
			this.issueType = issueType;
		}

		public Double getManualAdjustment() {
			return manualAdjustment;
		}

		public void setManualAdjustment(Double manualAdjustment) {
			this.manualAdjustment = manualAdjustment;
		}
	}

}
