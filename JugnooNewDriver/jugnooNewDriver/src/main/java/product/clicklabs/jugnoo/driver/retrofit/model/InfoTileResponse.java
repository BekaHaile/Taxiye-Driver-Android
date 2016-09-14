package product.clicklabs.jugnoo.driver.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aneeshbansal on 14/09/16.
 */


public class InfoTileResponse {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("tiles")
	@Expose
	private List<Tile> tiles = new ArrayList<Tile>();

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
	 * The tiles
	 */
	public List<Tile> getTiles() {
		return tiles;
	}

	/**
	 *
	 * @param tiles
	 * The tiles
	 */
	public void setTiles(List<Tile> tiles) {
		this.tiles = tiles;
	}

	public class Tile {

		@SerializedName("title")
		@Expose
		private String title;
		@SerializedName("text_view_1")
		@Expose
		private Object textView1;
		@SerializedName("text_view_2")
		@Expose
		private Object textView2;
		@SerializedName("text_view_sub")
		@Expose
		private Object textViewSub;
		@SerializedName("deep_index")
		@Expose
		private Integer deepIndex;
		@SerializedName("text_value_1")
		@Expose
		private Object textValue1;
		@SerializedName("text_value_2")
		@Expose
		private Object textValue2;
		@SerializedName("value")
		@Expose
		private Object value;
		@SerializedName("extras")
		@Expose
		private Extras extras;

		/**
		 *
		 * @return
		 * The title
		 */
		public String getTitle() {
			return title;
		}

		/**
		 *
		 * @param title
		 * The title
		 */
		public void setTitle(String title) {
			this.title = title;
		}

		/**
		 *
		 * @return
		 * The textView1
		 */
		public Object getTextView1() {
			return textView1;
		}

		/**
		 *
		 * @param textView1
		 * The text_view_1
		 */
		public void setTextView1(Object textView1) {
			this.textView1 = textView1;
		}

		/**
		 *
		 * @return
		 * The textView2
		 */
		public Object getTextView2() {
			return textView2;
		}

		/**
		 *
		 * @param textView2
		 * The text_view_2
		 */
		public void setTextView2(Object textView2) {
			this.textView2 = textView2;
		}

		/**
		 *
		 * @return
		 * The textViewSub
		 */
		public Object getTextViewSub() {
			return textViewSub;
		}

		/**
		 *
		 * @param textViewSub
		 * The text_view_sub
		 */
		public void setTextViewSub(Object textViewSub) {
			this.textViewSub = textViewSub;
		}

		/**
		 *
		 * @return
		 * The deepIndex
		 */
		public Integer getDeepIndex() {
			return deepIndex;
		}

		/**
		 *
		 * @param deepIndex
		 * The deep_index
		 */
		public void setDeepIndex(Integer deepIndex) {
			this.deepIndex = deepIndex;
		}

		/**
		 *
		 * @return
		 * The textValue1
		 */
		public Object getTextValue1() {
			return textValue1;
		}

		/**
		 *
		 * @param textValue1
		 * The text_value_1
		 */
		public void setTextValue1(Object textValue1) {
			this.textValue1 = textValue1;
		}

		/**
		 *
		 * @return
		 * The textValue2
		 */
		public Object getTextValue2() {
			return textValue2;
		}

		/**
		 *
		 * @param textValue2
		 * The text_value_2
		 */
		public void setTextValue2(Object textValue2) {
			this.textValue2 = textValue2;
		}

		/**
		 *
		 * @return
		 * The value
		 */
		public Object getValue() {
			return value;
		}

		/**
		 *
		 * @param value
		 * The value
		 */
		public void setValue(Object value) {
			this.value = value;
		}

		/**
		 *
		 * @return
		 * The extras
		 */
		public Extras getExtras() {
			return extras;
		}

		/**
		 *
		 * @param extras
		 * The extras
		 */
		public void setExtras(Extras extras) {
			this.extras = extras;
		}

		public class Extras {

			@SerializedName("type")
			@Expose
			private String type;
			@SerializedName("engagement_id")
			@Expose
			private Integer engagementId;
			@SerializedName("date")
			@Expose
			private String date;
			@SerializedName("ride_fare")
			@Expose
			private Double rideFare;
			@SerializedName("driver_fare_factor")
			@Expose
			private Double driverFareFactor;
			@SerializedName("surge_amount")
			@Expose
			private Double surgeAmount;
			@SerializedName("accept_subsidy")
			@Expose
			private Double acceptSubsidy;
			@SerializedName("cancel_subsidy")
			@Expose
			private Double cancelSubsidy;
			@SerializedName("jugnoo_commission")
			@Expose
			private Double jugnooCommission;
			@SerializedName("earning")
			@Expose
			private Double earning;
			@SerializedName("paid_using_cash")
			@Expose
			private Double paidUsingCash;
			@SerializedName("account")
			@Expose
			private Double account;
			@SerializedName("from")
			@Expose
			private String from;
			@SerializedName("to")
			@Expose
			private String to;
			@SerializedName("ride_time")
			@Expose
			private Integer rideTime;
			@SerializedName("distance")
			@Expose
			private Integer distance;
			@SerializedName("redirect_url")
			@Expose
			private String redirectUrl;

			/**
			 *
			 * @return
			 * The type
			 */
			public String getType() {
				return type;
			}

			/**
			 *
			 * @param type
			 * The type
			 */
			public void setType(String type) {
				this.type = type;
			}

			/**
			 *
			 * @return
			 * The engagementId
			 */
			public Integer getEngagementId() {
				return engagementId;
			}

			/**
			 *
			 * @param engagementId
			 * The engagement_id
			 */
			public void setEngagementId(Integer engagementId) {
				this.engagementId = engagementId;
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
			 * The rideFare
			 */
			public Double getRideFare() {
				return rideFare;
			}

			/**
			 *
			 * @param rideFare
			 * The ride_fare
			 */
			public void setRideFare(Double rideFare) {
				this.rideFare = rideFare;
			}

			/**
			 *
			 * @return
			 * The driverFareFactor
			 */
			public Double getDriverFareFactor() {
				return driverFareFactor;
			}

			/**
			 *
			 * @param driverFareFactor
			 * The driver_fare_factor
			 */
			public void setDriverFareFactor(Double driverFareFactor) {
				this.driverFareFactor = driverFareFactor;
			}

			/**
			 *
			 * @return
			 * The surgeAmount
			 */
			public Double getSurgeAmount() {
				return surgeAmount;
			}

			/**
			 *
			 * @param surgeAmount
			 * The surge_amount
			 */
			public void setSurgeAmount(Double surgeAmount) {
				this.surgeAmount = surgeAmount;
			}

			/**
			 *
			 * @return
			 * The acceptSubsidy
			 */
			public Double getAcceptSubsidy() {
				return acceptSubsidy;
			}

			/**
			 *
			 * @param acceptSubsidy
			 * The accept_subsidy
			 */
			public void setAcceptSubsidy(Double acceptSubsidy) {
				this.acceptSubsidy = acceptSubsidy;
			}

			/**
			 *
			 * @return
			 * The cancelSubsidy
			 */
			public Double getCancelSubsidy() {
				return cancelSubsidy;
			}

			/**
			 *
			 * @param cancelSubsidy
			 * The cancel_subsidy
			 */
			public void setCancelSubsidy(Double cancelSubsidy) {
				this.cancelSubsidy = cancelSubsidy;
			}

			/**
			 *
			 * @return
			 * The jugnooCommission
			 */
			public Double getJugnooCommission() {
				return jugnooCommission;
			}

			/**
			 *
			 * @param jugnooCommission
			 * The jugnoo_commission
			 */
			public void setJugnooCommission(Double jugnooCommission) {
				this.jugnooCommission = jugnooCommission;
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
			 * The from
			 */
			public String getFrom() {
				return from;
			}

			/**
			 *
			 * @param from
			 * The from
			 */
			public void setFrom(String from) {
				this.from = from;
			}

			/**
			 *
			 * @return
			 * The to
			 */
			public String getTo() {
				return to;
			}

			/**
			 *
			 * @param to
			 * The to
			 */
			public void setTo(String to) {
				this.to = to;
			}

			/**
			 *
			 * @return
			 * The rideTime
			 */
			public Integer getRideTime() {
				return rideTime;
			}

			/**
			 *
			 * @param rideTime
			 * The ride_time
			 */
			public void setRideTime(Integer rideTime) {
				this.rideTime = rideTime;
			}

			/**
			 *
			 * @return
			 * The distance
			 */
			public Integer getDistance() {
				return distance;
			}

			/**
			 *
			 * @param distance
			 * The distance
			 */
			public void setDistance(Integer distance) {
				this.distance = distance;
			}

			/**
			 *
			 * @return
			 * The redirectUrl
			 */
			public String getRedirectUrl() {
				return redirectUrl;
			}

			/**
			 *
			 * @param redirectUrl
			 * The redirect_url
			 */
			public void setRedirectUrl(String redirectUrl) {
				this.redirectUrl = redirectUrl;
			}

		}

	}

}


