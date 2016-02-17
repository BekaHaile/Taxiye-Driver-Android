package product.clicklabs.jugnoo.driver.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by aneeshbansal on 16/02/16.
 */
public class Item {


	@SerializedName("driver_id")
	@Expose
	private Integer driverId;
	@SerializedName("driver_name")
	@Expose
	private String driverName;
	@SerializedName("city_name")
	@Expose
	private String cityName;
	@SerializedName("time_interval")
	@Expose
	private String timeInterval;
	@SerializedName("rank")
	@Expose
	private Integer cityRank;
	@SerializedName("custom_column_value")
	@Expose
	private Integer customColumnValue;

	private boolean isUser = false;

	public Item(Integer driverId, String driverName, String cityName, String timeInterval, Integer cityRank, Integer customColumnValue, Boolean isUser){
		this.driverId = driverId;
		this.driverName = driverName;
		this.cityName = cityName;
		this.timeInterval = timeInterval;
		this.cityRank = cityRank;
		this.customColumnValue = customColumnValue;
		this.isUser = isUser;
	}


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
	 * @return The customColumnValue
	 */
	public Integer getCustomColumnValue() {
		return customColumnValue;
	}

	/**
	 * @param customColumnValue The custom_column_value
	 */
	public void setCustomColumnValue(Integer customColumnValue) {
		this.customColumnValue = customColumnValue;
	}

	public boolean getIsUser() {
		return isUser;
	}

	public void setIsUser(boolean isUser) {
		this.isUser = isUser;
	}


}
