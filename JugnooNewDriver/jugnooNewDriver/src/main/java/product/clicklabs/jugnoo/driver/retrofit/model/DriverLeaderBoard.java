package product.clicklabs.jugnoo.driver.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aneeshbansal on 08/09/15.
 */
public class DriverLeaderBoard {

	@Expose
	private Integer flag;
	@Expose
	private String message;
	@SerializedName("driver_leader_board")
	@Expose
	private DriverBackLeaderBoard driverBackLeaderBoard;

	/**
	 * @return The flag
	 */
	public Integer getFlag() {
		return flag;
	}

	/**
	 * @param flag The flag
	 */
	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	/**
	 * @return The message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message The message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return The driverLeaderBoard
	 */
	public DriverBackLeaderBoard getDriverBackLeaderBoard() {
		return driverBackLeaderBoard;
	}

	/**
	 * @param driverBackLeaderBoard The driver_leader_board
	 */
	public void setDriverBackLeaderBoard(DriverBackLeaderBoard driverBackLeaderBoard) {
		this.driverBackLeaderBoard = driverBackLeaderBoard;
	}


	public class CityDriverRank {

		@Expose
		private Integer day;
		@Expose
		private Integer week;
		@Expose
		private Integer month;

		/**
		 * @return The day
		 */
		public Integer getDay() {
			return day;
		}

		/**
		 * @param day The day
		 */
		public void setDay(Integer day) {
			this.day = day;
		}

		/**
		 * @return The week
		 */
		public Integer getWeek() {
			return week;
		}

		/**
		 * @param week The week
		 */
		public void setWeek(Integer week) {
			this.week = week;
		}

		/**
		 * @return The month
		 */
		public Integer getMonth() {
			return month;
		}

		/**
		 * @param month The month
		 */
		public void setMonth(Integer month) {
			this.month = month;
		}

	}

	public class CityLeaderBoard {

		@Expose
		private List<Day> day = new ArrayList<Day>();
		@Expose
		private List<Week> week = new ArrayList<Week>();
		@Expose
		private List<Month> month = new ArrayList<Month>();

		/**
		 * @return The day
		 */
		public List<Day> getDay() {
			return day;
		}

		/**
		 * @param day The day
		 */
		public void setDay(List<Day> day) {
			this.day = day;
		}

		/**
		 * @return The week
		 */
		public List<Week> getWeek() {
			return week;
		}

		/**
		 * @param week The week
		 */
		public void setWeek(List<Week> week) {
			this.week = week;
		}

		/**
		 * @return The month
		 */
		public List<Month> getMonth() {
			return month;
		}

		/**
		 * @param month The month
		 */
		public void setMonth(List<Month> month) {
			this.month = month;
		}

	}

	public class Day {

		@SerializedName("driver_id")
		@Expose
		private Integer driverId;
		@SerializedName("driver_name")
		@Expose
		private String driverName;
		@SerializedName("city_name")
		@Expose
		private String cityName;
		@SerializedName("overall_rank")
		@Expose
		private Integer overallRank;
		@SerializedName("city_rank")
		@Expose
		private Integer cityRank;
		@SerializedName("time_interval")
		@Expose
		private String timeInterval;
		@SerializedName("number_of_rides_in_city")
		@Expose
		private Integer numberOfRidesInCity;
		@SerializedName("number_of_rides_overall")
		@Expose
		private Integer numberOfRidesOverall;

		/**
		 * @return The driverId
		 */
		public Integer getDriverId() {
			return driverId;
		}

		/**
		 * @param driverId The driver_id
		 */
		public void setDriverId(Integer driverId) {
			this.driverId = driverId;
		}

		/**
		 * @return The driverName
		 */
		public String getDriverName() {
			return driverName;
		}

		/**
		 * @param driverName The driver_name
		 */
		public void setDriverName(String driverName) {
			this.driverName = driverName;
		}

		/**
		 * @return The cityName
		 */
		public String getCityName() {
			return cityName;
		}

		/**
		 * @param cityName The city_name
		 */
		public void setCityName(String cityName) {
			this.cityName = cityName;
		}

		/**
		 * @return The overallRank
		 */
		public Integer getOverallRank() {
			return overallRank;
		}

		/**
		 * @param overallRank The overall_rank
		 */
		public void setOverallRank(Integer overallRank) {
			this.overallRank = overallRank;
		}

		/**
		 * @return The cityRank
		 */
		public Integer getCityRank() {
			return cityRank;
		}

		/**
		 * @param cityRank The city_rank
		 */
		public void setCityRank(Integer cityRank) {
			this.cityRank = cityRank;
		}

		/**
		 * @return The timeInterval
		 */
		public String getTimeInterval() {
			return timeInterval;
		}

		/**
		 * @param timeInterval The time_interval
		 */
		public void setTimeInterval(String timeInterval) {
			this.timeInterval = timeInterval;
		}

		/**
		 * @return The numberOfRidesInCity
		 */
		public Integer getNumberOfRidesInCity() {
			return numberOfRidesInCity;
		}

		/**
		 * @param numberOfRidesInCity The number_of_rides_in_city
		 */
		public void setNumberOfRidesInCity(Integer numberOfRidesInCity) {
			this.numberOfRidesInCity = numberOfRidesInCity;
		}

		/**
		 * @return The numberOfRidesOverall
		 */
		public Integer getNumberOfRidesOverall() {
			return numberOfRidesOverall;
		}

		/**
		 * @param numberOfRidesOverall The number_of_rides_overall
		 */
		public void setNumberOfRidesOverall(Integer numberOfRidesOverall) {
			this.numberOfRidesOverall = numberOfRidesOverall;
		}

	}


	public class DriverBackLeaderBoard {

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
		 * @return The overallLeaderBoard
		 */
		public OverallLeaderBoard getOverallLeaderBoard() {
			return overallLeaderBoard;
		}

		/**
		 * @param overallLeaderBoard The overall_leader_board
		 */
		public void setOverallLeaderBoard(OverallLeaderBoard overallLeaderBoard) {
			this.overallLeaderBoard = overallLeaderBoard;
		}

		/**
		 * @return The overallDriverRank
		 */
		public OverallDriverRank getOverallDriverRank() {
			return overallDriverRank;
		}

		/**
		 * @param overallDriverRank The overall_driver_rank
		 */
		public void setOverallDriverRank(OverallDriverRank overallDriverRank) {
			this.overallDriverRank = overallDriverRank;
		}

		/**
		 * @return The driverCity
		 */
		public String getDriverCity() {
			return driverCity;
		}

		/**
		 * @param driverCity The driver_city
		 */
		public void setDriverCity(String driverCity) {
			this.driverCity = driverCity;
		}

		/**
		 * @return The cityLeaderBoard
		 */
		public CityLeaderBoard getCityLeaderBoard() {
			return cityLeaderBoard;
		}

		/**
		 * @param cityLeaderBoard The city_leader_board
		 */
		public void setCityLeaderBoard(CityLeaderBoard cityLeaderBoard) {
			this.cityLeaderBoard = cityLeaderBoard;
		}

		/**
		 * @return The cityDriverRank
		 */
		public CityDriverRank getCityDriverRank() {
			return cityDriverRank;
		}

		/**
		 * @param cityDriverRank The city_driver_rank
		 */
		public void setCityDriverRank(CityDriverRank cityDriverRank) {
			this.cityDriverRank = cityDriverRank;
		}

		/**
		 * @return The totalDriversCity
		 */
		public TotalDriversCity getTotalDriversCity() {
			return totalDriversCity;
		}

		/**
		 * @param totalDriversCity The total_drivers_city
		 */
		public void setTotalDriversCity(TotalDriversCity totalDriversCity) {
			this.totalDriversCity = totalDriversCity;
		}

		/**
		 * @return The totalDriversOverall
		 */
		public TotalDriversOverall getTotalDriversOverall() {
			return totalDriversOverall;
		}

		/**
		 * @param totalDriversOverall The total_drivers_overall
		 */
		public void setTotalDriversOverall(TotalDriversOverall totalDriversOverall) {
			this.totalDriversOverall = totalDriversOverall;
		}

	}


	public class Month {

		@SerializedName("driver_id")
		@Expose
		private Integer driverId;
		@SerializedName("driver_name")
		@Expose
		private String driverName;
		@SerializedName("city_name")
		@Expose
		private String cityName;
		@SerializedName("overall_rank")
		@Expose
		private Integer overallRank;
		@SerializedName("city_rank")
		@Expose
		private Integer cityRank;
		@SerializedName("time_interval")
		@Expose
		private String timeInterval;
		@SerializedName("number_of_rides_in_city")
		@Expose
		private Integer numberOfRidesInCity;
		@SerializedName("number_of_rides_overall")
		@Expose
		private Integer numberOfRidesOverall;

		/**
		 * @return The driverId
		 */
		public Integer getDriverId() {
			return driverId;
		}

		/**
		 * @param driverId The driver_id
		 */
		public void setDriverId(Integer driverId) {
			this.driverId = driverId;
		}

		/**
		 * @return The driverName
		 */
		public String getDriverName() {
			return driverName;
		}

		/**
		 * @param driverName The driver_name
		 */
		public void setDriverName(String driverName) {
			this.driverName = driverName;
		}

		/**
		 * @return The cityName
		 */
		public String getCityName() {
			return cityName;
		}

		/**
		 * @param cityName The city_name
		 */
		public void setCityName(String cityName) {
			this.cityName = cityName;
		}

		/**
		 * @return The overallRank
		 */
		public Integer getOverallRank() {
			return overallRank;
		}

		/**
		 * @param overallRank The overall_rank
		 */
		public void setOverallRank(Integer overallRank) {
			this.overallRank = overallRank;
		}

		/**
		 * @return The cityRank
		 */
		public Integer getCityRank() {
			return cityRank;
		}

		/**
		 * @param cityRank The city_rank
		 */
		public void setCityRank(Integer cityRank) {
			this.cityRank = cityRank;
		}

		/**
		 * @return The timeInterval
		 */
		public String getTimeInterval() {
			return timeInterval;
		}

		/**
		 * @param timeInterval The time_interval
		 */
		public void setTimeInterval(String timeInterval) {
			this.timeInterval = timeInterval;
		}

		/**
		 * @return The numberOfRidesInCity
		 */
		public Integer getNumberOfRidesInCity() {
			return numberOfRidesInCity;
		}

		/**
		 * @param numberOfRidesInCity The number_of_rides_in_city
		 */
		public void setNumberOfRidesInCity(Integer numberOfRidesInCity) {
			this.numberOfRidesInCity = numberOfRidesInCity;
		}

		/**
		 * @return The numberOfRidesOverall
		 */
		public Integer getNumberOfRidesOverall() {
			return numberOfRidesOverall;
		}

		/**
		 * @param numberOfRidesOverall The number_of_rides_overall
		 */
		public void setNumberOfRidesOverall(Integer numberOfRidesOverall) {
			this.numberOfRidesOverall = numberOfRidesOverall;
		}

	}

	public class OverallDriverRank {

		@Expose
		private Integer day;
		@Expose
		private Integer week;
		@Expose
		private Integer month;

		/**
		 * @return The day
		 */
		public Integer getDay() {
			return day;
		}

		/**
		 * @param day The day
		 */
		public void setDay(Integer day) {
			this.day = day;
		}

		/**
		 * @return The week
		 */
		public Integer getWeek() {
			return week;
		}

		/**
		 * @param week The week
		 */
		public void setWeek(Integer week) {
			this.week = week;
		}

		/**
		 * @return The month
		 */
		public Integer getMonth() {
			return month;
		}

		/**
		 * @param month The month
		 */
		public void setMonth(Integer month) {
			this.month = month;
		}

	}

	public class OverallLeaderBoard {

		@Expose
		private List<Day> day = new ArrayList<Day>();
		@Expose
		private List<Week> week = new ArrayList<Week>();
		@Expose
		private List<Month> month = new ArrayList<Month>();

		/**
		 * @return The day
		 */
		public List<Day> getDay() {
			return day;
		}

		/**
		 * @param day The day
		 */
		public void setDay(List<Day> day) {
			this.day = day;
		}

		/**
		 * @return The week
		 */
		public List<Week> getWeek() {
			return week;
		}

		/**
		 * @param week The week
		 */
		public void setWeek(List<Week> week) {
			this.week = week;
		}

		/**
		 * @return The month
		 */
		public List<Month> getMonth() {
			return month;
		}

		/**
		 * @param month The month
		 */
		public void setMonth(List<Month> month) {
			this.month = month;
		}

	}

	public class TotalDriversCity {

		@Expose
		private Integer day;
		@Expose
		private Integer week;
		@Expose
		private Integer month;

		/**
		 * @return The day
		 */
		public Integer getDay() {
			return day;
		}

		/**
		 * @param day The day
		 */
		public void setDay(Integer day) {
			this.day = day;
		}

		/**
		 * @return The week
		 */
		public Integer getWeek() {
			return week;
		}

		/**
		 * @param week The week
		 */
		public void setWeek(Integer week) {
			this.week = week;
		}

		/**
		 * @return The month
		 */
		public Integer getMonth() {
			return month;
		}

		/**
		 * @param month The month
		 */
		public void setMonth(Integer month) {
			this.month = month;
		}

	}

	public class TotalDriversOverall {

		@Expose
		private Integer day;
		@Expose
		private Integer week;
		@Expose
		private Integer month;

		/**
		 * @return The day
		 */
		public Integer getDay() {
			return day;
		}

		/**
		 * @param day The day
		 */
		public void setDay(Integer day) {
			this.day = day;
		}

		/**
		 * @return The week
		 */
		public Integer getWeek() {
			return week;
		}

		/**
		 * @param week The week
		 */
		public void setWeek(Integer week) {
			this.week = week;
		}

		/**
		 * @return The month
		 */
		public Integer getMonth() {
			return month;
		}

		/**
		 * @param month The month
		 */
		public void setMonth(Integer month) {
			this.month = month;
		}

	}

	public class Week {

		@SerializedName("driver_id")
		@Expose
		private Integer driverId;
		@SerializedName("driver_name")
		@Expose
		private String driverName;
		@SerializedName("city_name")
		@Expose
		private String cityName;
		@SerializedName("overall_rank")
		@Expose
		private Integer overallRank;
		@SerializedName("city_rank")
		@Expose
		private Integer cityRank;
		@SerializedName("time_interval")
		@Expose
		private String timeInterval;
		@SerializedName("number_of_rides_in_city")
		@Expose
		private Integer numberOfRidesInCity;
		@SerializedName("number_of_rides_overall")
		@Expose
		private Integer numberOfRidesOverall;

		/**
		 * @return The driverId
		 */
		public Integer getDriverId() {
			return driverId;
		}

		/**
		 * @param driverId The driver_id
		 */
		public void setDriverId(Integer driverId) {
			this.driverId = driverId;
		}

		/**
		 * @return The driverName
		 */
		public String getDriverName() {
			return driverName;
		}

		/**
		 * @param driverName The driver_name
		 */
		public void setDriverName(String driverName) {
			this.driverName = driverName;
		}

		/**
		 * @return The cityName
		 */
		public String getCityName() {
			return cityName;
		}

		/**
		 * @param cityName The city_name
		 */
		public void setCityName(String cityName) {
			this.cityName = cityName;
		}

		/**
		 * @return The overallRank
		 */
		public Integer getOverallRank() {
			return overallRank;
		}

		/**
		 * @param overallRank The overall_rank
		 */
		public void setOverallRank(Integer overallRank) {
			this.overallRank = overallRank;
		}

		/**
		 * @return The cityRank
		 */
		public Integer getCityRank() {
			return cityRank;
		}

		/**
		 * @param cityRank The city_rank
		 */
		public void setCityRank(Integer cityRank) {
			this.cityRank = cityRank;
		}

		/**
		 * @return The timeInterval
		 */
		public String getTimeInterval() {
			return timeInterval;
		}

		/**
		 * @param timeInterval The time_interval
		 */
		public void setTimeInterval(String timeInterval) {
			this.timeInterval = timeInterval;
		}

		/**
		 * @return The numberOfRidesInCity
		 */
		public Integer getNumberOfRidesInCity() {
			return numberOfRidesInCity;
		}

		/**
		 * @param numberOfRidesInCity The number_of_rides_in_city
		 */
		public void setNumberOfRidesInCity(Integer numberOfRidesInCity) {
			this.numberOfRidesInCity = numberOfRidesInCity;
		}

		/**
		 * @return The numberOfRidesOverall
		 */
		public Integer getNumberOfRidesOverall() {
			return numberOfRidesOverall;
		}

		/**
		 * @param numberOfRidesOverall The number_of_rides_overall
		 */
		public void setNumberOfRidesOverall(Integer numberOfRidesOverall) {
			this.numberOfRidesOverall = numberOfRidesOverall;
		}

	}
}