package product.clicklabs.jugnoo.driver.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aneeshbansal on 20/09/16.
 */
public class DailyEarningResponse {

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
	@SerializedName("time_online")
	@Expose
	private String timeOnline;
	@SerializedName("total_trips")
	@Expose
	private Integer totalTrips;
	@SerializedName("trips")
	@Expose
	private List<Trip> trips = new ArrayList<Trip>();

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

	public class DailyParam {

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


	public class Trip {

		@SerializedName("time")
		@Expose
		private String time;
		@SerializedName("earning")
		@Expose
		private Double earning;
		@SerializedName("extras")
		@Expose
		private Extras extras;

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
			@SerializedName("ride_param")
			@Expose
			private List<RideParam> rideParam = new ArrayList<RideParam>();
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
			 * The rideParam
			 */
			public List<RideParam> getRideParam() {
				return rideParam;
			}

			/**
			 *
			 * @param rideParam
			 * The rideParam
			 */
			public void setRideParam(List<RideParam> rideParam) {
				this.rideParam = rideParam;
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

			public class RideParam {

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

		}

	}

}


