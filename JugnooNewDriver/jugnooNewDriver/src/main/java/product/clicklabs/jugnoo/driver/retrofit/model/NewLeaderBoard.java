package product.clicklabs.jugnoo.driver.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aneeshbansal on 16/02/16.
 */

public class NewLeaderBoard {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("message")
	@Expose
	private String message;
	@SerializedName("driver_leader_board")
	@Expose
	private DriverLeaderBoard driverLeaderBoard;
	@SerializedName("dynamic_column_name")
	@Expose
	private String dynamicColumnName;

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
	 * The message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 *
	 * @param message
	 * The message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 *
	 * @return
	 * The driverLeaderBoard
	 */
	public DriverLeaderBoard getDriverLeaderBoard() {
		return driverLeaderBoard;
	}

	/**
	 *
	 * @param driverLeaderBoard
	 * The driver_leader_board
	 */
	public void setDriverLeaderBoard(DriverLeaderBoard driverLeaderBoard) {
		this.driverLeaderBoard = driverLeaderBoard;
	}

	/**
	 *
	 * @return
	 * The dynamicColumnName
	 */
	public String getDynamicColumnName() {
		return dynamicColumnName;
	}

	/**
	 *
	 * @param dynamicColumnName
	 * The dynamic_column_name
	 */
	public void setDynamicColumnName(String dynamicColumnName) {
		this.dynamicColumnName = dynamicColumnName;
	}

	public class DriverLeaderBoard {

		@SerializedName("overall_leader_board")
		@Expose
		private OverallLeaderBoard overallLeaderBoard;
		@SerializedName("overall_driver_rank")
		@Expose
		private OverallDriverRank overallDriverRank;
		@SerializedName("driver_city")
		@Expose
		private String driverCity;
		@SerializedName("city_leader_board")
		@Expose
		private CityLeaderBoard cityLeaderBoard;
		@SerializedName("city_driver_rank")
		@Expose
		private CityDriverRank cityDriverRank;
		@SerializedName("total_drivers_city")
		@Expose
		private TotalDriversCity totalDriversCity;
		@SerializedName("total_drivers_overall")
		@Expose
		private TotalDriversOverall totalDriversOverall;

		/**
		 *
		 * @return
		 * The overallLeaderBoard
		 */
		public OverallLeaderBoard getOverallLeaderBoard() {
			return overallLeaderBoard;
		}

		/**
		 *
		 * @param overallLeaderBoard
		 * The overall_leader_board
		 */
		public void setOverallLeaderBoard(OverallLeaderBoard overallLeaderBoard) {
			this.overallLeaderBoard = overallLeaderBoard;
		}

		/**
		 *
		 * @return
		 * The overallDriverRank
		 */
		public OverallDriverRank getOverallDriverRank() {
			return overallDriverRank;
		}

		/**
		 *
		 * @param overallDriverRank
		 * The overall_driver_rank
		 */
		public void setOverallDriverRank(OverallDriverRank overallDriverRank) {
			this.overallDriverRank = overallDriverRank;
		}

		/**
		 *
		 * @return
		 * The driverCity
		 */
		public String getDriverCity() {
			return driverCity;
		}

		/**
		 *
		 * @param driverCity
		 * The driver_city
		 */
		public void setDriverCity(String driverCity) {
			this.driverCity = driverCity;
		}

		/**
		 *
		 * @return
		 * The cityLeaderBoard
		 */
		public CityLeaderBoard getCityLeaderBoard() {
			return cityLeaderBoard;
		}

		/**
		 *
		 * @param cityLeaderBoard
		 * The city_leader_board
		 */
		public void setCityLeaderBoard(CityLeaderBoard cityLeaderBoard) {
			this.cityLeaderBoard = cityLeaderBoard;
		}

		/**
		 *
		 * @return
		 * The cityDriverRank
		 */
		public CityDriverRank getCityDriverRank() {
			return cityDriverRank;
		}

		/**
		 *
		 * @param cityDriverRank
		 * The city_driver_rank
		 */
		public void setCityDriverRank(CityDriverRank cityDriverRank) {
			this.cityDriverRank = cityDriverRank;
		}

		/**
		 *
		 * @return
		 * The totalDriversCity
		 */
		public TotalDriversCity getTotalDriversCity() {
			return totalDriversCity;
		}

		/**
		 *
		 * @param totalDriversCity
		 * The total_drivers_city
		 */
		public void setTotalDriversCity(TotalDriversCity totalDriversCity) {
			this.totalDriversCity = totalDriversCity;
		}

		/**
		 *
		 * @return
		 * The totalDriversOverall
		 */
		public TotalDriversOverall getTotalDriversOverall() {
			return totalDriversOverall;
		}

		/**
		 *
		 * @param totalDriversOverall
		 * The total_drivers_overall
		 */
		public void setTotalDriversOverall(TotalDriversOverall totalDriversOverall) {
			this.totalDriversOverall = totalDriversOverall;
		}

		public class OverallLeaderBoard {

			@SerializedName("day")
			@Expose
			private List<Item> day = new ArrayList<Item>();
			@SerializedName("week")
			@Expose
			private List<Item> week = new ArrayList<Item>();

			/**
			 *
			 * @return
			 * The day
			 */
			public List<Item> getDay() {
				return day;
			}

			/**
			 *
			 * @param day
			 * The day
			 */
			public void setDay(List<Item> day) {
				this.day = day;
			}

			/**
			 *
			 * @return
			 * The week
			 */
			public List<Item> getWeek() {
				return week;
			}

			/**
			 *
			 * @param week
			 * The week
			 */
			public void setWeek(List<Item> week) {
				this.week = week;
			}

			

		}

		public class OverallDriverRank {

			@SerializedName("day")
			@Expose
			private Integer day;
			@SerializedName("week")
			@Expose
			private Integer week;
			@SerializedName("dayScore")
			@Expose
			private Integer dayScore;
			@SerializedName("weekScore")
			@Expose
			private Integer weekScore;

			/**
			 *
			 * @return
			 * The day
			 */
			public Integer getDay() {
				return day;
			}

			/**
			 *
			 * @param day
			 * The day
			 */
			public void setDay(Integer day) {
				this.day = day;
			}

			/**
			 *
			 * @return
			 * The week
			 */
			public Integer getWeek() {
				return week;
			}

			/**
			 *
			 * @param week
			 * The week
			 */
			public void setWeek(Integer week) {
				this.week = week;
			}

			/**
			 *
			 * @return
			 * The dayScore
			 */
			public Integer getDayScore() {
				return dayScore;
			}

			/**
			 *
			 * @param dayScore
			 * The dayScore
			 */
			public void setDayScore(Integer dayScore) {
				this.dayScore = dayScore;
			}

			/**
			 *
			 * @return
			 * The weekScore
			 */
			public Integer getWeekScore() {
				return weekScore;
			}

			/**
			 *
			 * @param weekScore
			 * The weekScore
			 */
			public void setWeekScore(Integer weekScore) {
				this.weekScore = weekScore;
			}

		}

		public class CityLeaderBoard {

			@SerializedName("day")
			@Expose
			private List<Item> day = new ArrayList<Item>();
			@SerializedName("week")
			@Expose
			private List<Item> week = new ArrayList<Item>();

			/**
			 *
			 * @return
			 * The day
			 */
			public List<Item> getDay() {
				return day;
			}

			/**
			 *
			 * @param day
			 * The day
			 */
			public void setDay(List<Item> day) {
				this.day = day;
			}

			/**
			 *
			 * @return
			 * The week
			 */
			public List<Item> getWeek() {
				return week;
			}

			/**
			 *
			 * @param week
			 * The week
			 */
			public void setWeek(List<Item> week) {
				this.week = week;
			}

		}

		public class CityDriverRank {

			@SerializedName("day")
			@Expose
			private Integer day;
			@SerializedName("week")
			@Expose
			private Integer week;
			@SerializedName("dayScore")
			@Expose
			private Integer dayScore;
			@SerializedName("weekScore")
			@Expose
			private Integer weekScore;

			/**
			 *
			 * @return
			 * The day
			 */
			public Integer getDay() {
				return day;
			}

			/**
			 *
			 * @param day
			 * The day
			 */
			public void setDay(Integer day) {
				this.day = day;
			}

			/**
			 *
			 * @return
			 * The week
			 */
			public Integer getWeek() {
				return week;
			}

			/**
			 *
			 * @param week
			 * The week
			 */
			public void setWeek(Integer week) {
				this.week = week;
			}

			/**
			 *
			 * @return
			 * The dayScore
			 */
			public Integer getDayScore() {
				return dayScore;
			}

			/**
			 *
			 * @param dayScore
			 * The dayScore
			 */
			public void setDayScore(Integer dayScore) {
				this.dayScore = dayScore;
			}

			/**
			 *
			 * @return
			 * The weekScore
			 */
			public Integer getWeekScore() {
				return weekScore;
			}

			/**
			 *
			 * @param weekScore
			 * The weekScore
			 */
			public void setWeekScore(Integer weekScore) {
				this.weekScore = weekScore;
			}

		}

		public class TotalDriversCity {

			@SerializedName("day")
			@Expose
			private Integer day;
			@SerializedName("week")
			@Expose
			private Integer week;

			/**
			 *
			 * @return
			 * The day
			 */
			public Integer getDay() {
				return day;
			}

			/**
			 *
			 * @param day
			 * The day
			 */
			public void setDay(Integer day) {
				this.day = day;
			}

			/**
			 *
			 * @return
			 * The week
			 */
			public Integer getWeek() {
				return week;
			}

			/**
			 *
			 * @param week
			 * The week
			 */
			public void setWeek(Integer week) {
				this.week = week;
			}

		}

		public class TotalDriversOverall {

			@SerializedName("day")
			@Expose
			private Integer day;
			@SerializedName("week")
			@Expose
			private Integer week;

			/**
			 *
			 * @return
			 * The day
			 */
			public Integer getDay() {
				return day;
			}

			/**
			 *
			 * @param day
			 * The day
			 */
			public void setDay(Integer day) {
				this.day = day;
			}

			/**
			 *
			 * @return
			 * The week
			 */
			public Integer getWeek() {
				return week;
			}

			/**
			 *
			 * @param week
			 * The week
			 */
			public void setWeek(Integer week) {
				this.week = week;
			}

		}



	}

//	public class Item {
//
//		@SerializedName("driver_id")
//		@Expose
//		private Integer driverId;
//		@SerializedName("driver_name")
//		@Expose
//		private String driverName;
//		@SerializedName("city_name")
//		@Expose
//		private String cityName;
//		@SerializedName("time_interval")
//		@Expose
//		private String timeInterval;
//		@SerializedName("city_rank")
//		@Expose
//		private Integer cityRank;
//		@SerializedName("custom_column_value")
//		@Expose
//		private Integer customColumnValue;
//
//		/**
//		 *
//		 * @return
//		 * The driverId
//		 */
//		public Integer getDriverId() {
//			return driverId;
//		}
//
//		/**
//		 *
//		 * @param driverId
//		 * The driver_id
//		 */
//		public void setDriverId(Integer driverId) {
//			this.driverId = driverId;
//		}
//
//		/**
//		 *
//		 * @return
//		 * The driverName
//		 */
//		public String getDriverName() {
//			return driverName;
//		}
//
//		/**
//		 *
//		 * @param driverName
//		 * The driver_name
//		 */
//		public void setDriverName(String driverName) {
//			this.driverName = driverName;
//		}
//
//		/**
//		 *
//		 * @return
//		 * The cityName
//		 */
//		public String getCityName() {
//			return cityName;
//		}
//
//		/**
//		 *
//		 * @param cityName
//		 * The city_name
//		 */
//		public void setCityName(String cityName) {
//			this.cityName = cityName;
//		}
//
//		/**
//		 *
//		 * @return
//		 * The timeInterval
//		 */
//		public String getTimeInterval() {
//			return timeInterval;
//		}
//
//		/**
//		 *
//		 * @param timeInterval
//		 * The time_interval
//		 */
//		public void setTimeInterval(String timeInterval) {
//			this.timeInterval = timeInterval;
//		}
//
//		/**
//		 *
//		 * @return
//		 * The cityRank
//		 */
//		public Integer getCityRank() {
//			return cityRank;
//		}
//
//		/**
//		 *
//		 * @param cityRank
//		 * The city_rank
//		 */
//		public void setCityRank(Integer cityRank) {
//			this.cityRank = cityRank;
//		}
//
//		/**
//		 *
//		 * @return
//		 * The customColumnValue
//		 */
//		public Integer getCustomColumnValue() {
//			return customColumnValue;
//		}
//
//		/**
//		 *
//		 * @param customColumnValue
//		 * The custom_column_value
//		 */
//		public void setCustomColumnValue(Integer customColumnValue) {
//			this.customColumnValue = customColumnValue;
//		}
//
//	}

}












