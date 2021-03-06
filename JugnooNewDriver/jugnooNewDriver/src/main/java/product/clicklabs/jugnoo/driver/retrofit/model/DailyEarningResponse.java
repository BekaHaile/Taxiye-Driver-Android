package product.clicklabs.jugnoo.driver.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.driver.retrofit.CurrencyModel;

/**
 * Created by aneeshbansal on 20/09/16.
 */
public class DailyEarningResponse extends CurrencyModel {

	@SerializedName("date")
	@Expose
	private String date;
	@SerializedName("day")
	@Expose
	private String day;
	@SerializedName("earnings")
	@Expose
	private Double earnings;
	@SerializedName("daily_param")
	@Expose
	private List<DailyParam> dailyParam = new ArrayList<DailyParam>();
	@SerializedName("paid_by_customer")
	@Expose
	private Double paidByCustomer;
	@SerializedName("account")
	@Expose
	private Double account;
	@SerializedName("total_distance_travelled")
	@Expose
	private String timeOnline;
	@SerializedName("total_trips")
	@Expose
	private Integer totalTrips;
	@SerializedName("delivery_trips")
	@Expose
	private Integer totalDelivery;
	/*@SerializedName("total_distance_covered")
	@Expose
	private String totalDistanceTravelled;*/
	@SerializedName("trips")
	@Expose
	private List<Trip> trips = new ArrayList<Trip>();
	@SerializedName("extras")
	@Expose
	private ExtrasData extrasData;
	@SerializedName("fare_per_km")
	private Double farePerKm;

	public Double getFarePerKm() {
		return farePerKm;
	}

	public String getTotalDistanceTravelled() {
		return "abstract";
	}


	public ExtrasData getExtrasData() {
		return extrasData;
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
	 * The dailyParam
	 */
	public List<DailyParam> getDailyParam() {
		return dailyParam;
	}

	/**
	 *
	 * @param dailyParam
	 * The dailyParam
	 */
	public void setDailyParam(List<DailyParam> dailyParam) {
		this.dailyParam = dailyParam;
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
	 * The timeOnline
	 */
	public String getTimeOnline() {
		return timeOnline;
	}

	/**
	 *
	 * @param timeOnline
	 * The time_online
	 */
	public void setTimeOnline(String timeOnline) {
		this.timeOnline = timeOnline;
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
	 * The trips
	 */
	public List<Trip> getTrips() {
		return trips;
	}

	/**
	 *
	 * @param trips
	 * The trips
	 */
	public void setTrips(List<Trip> trips) {
		this.trips = trips;
	}

	public class DailyParam extends CurrencyModel {

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


	public class Trip extends CurrencyModel {

		@SerializedName("date")
		@Expose
		private String date;
		@SerializedName("time")
		@Expose
		private String time;
		@SerializedName("earning")
		@Expose
		private Double earning;
		@SerializedName("collect_cash")
		@Expose
		private Double collectCash;
		@SerializedName("type")
		@Expose
		private Integer type;
		@SerializedName("status")
		@Expose
		private String status;
		@SerializedName("extras")
		@Expose
		private Tile.Extras extras;
		@SerializedName("distance")
		@Expose
		private double distance;

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		/**
		 *
		 * @return
		 * The time
		 */
		public String getTime() {
			return time;
		}

		/**
		 *
		 * @param time
		 * The time
		 */
		public void setTime(String time) {
			this.time = time;
		}

		/**
		 *
		 * @return
		 * The earning
		 */
		public Double getEarning() {
			return earning;
		}

		/**
		 *
		 * @param earning
		 * The earning
		 */
		public void setEarning(Double earning) {
			this.earning = earning;
		}

		/**
		 *
		 * @return
		 * The type
		 */
		public Integer getType() {
			return type;
		}

		/**
		 *
		 * @param type
		 * The type
		 */
		public void setType(Integer type) {
			this.type = type;
		}


		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		/**
		 *
		 * @return
		 * The extras
		 */
		public Tile.Extras getExtras() {
			return extras;
		}

		/**
		 *
		 * @param extras
		 * The extras
		 */
		public void setExtras(Tile.Extras extras) {
			this.extras = extras;
		}


		public void getDistance(){

		}

		public Double getCollectCash() {
			return collectCash;
		}
	}

	public class ExtrasData {
		@SerializedName("slots")
		private ArrayList<CaptiveSlots> slots;

		public ArrayList<CaptiveSlots> getCaptiveSlots() {
			return slots;
		}
	}
}


