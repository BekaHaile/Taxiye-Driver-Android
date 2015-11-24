package product.clicklabs.jugnoo.driver.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aneeshbansal on 24/11/15.
 */

public class HeatMapResponse {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("regions")
	@Expose
	private List<Region> regions = new ArrayList<Region>();

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
	 * @return The regions
	 */
	public List<Region> getRegions() {
		return regions;
	}

	/**
	 * @param regions The regions
	 */
	public void setRegions(List<Region> regions) {
		this.regions = regions;
	}

	public class Region {

		@SerializedName("region_id")
		@Expose
		private Integer regionId;
		@SerializedName("region")
		@Expose
		private List<List<Region_>> region = new ArrayList<List<Region_>>();
		@SerializedName("city_id")
		@Expose
		private Integer cityId;
		@SerializedName("driver_fare_factor")
		@Expose
		private Double driverFareFactor;
		@SerializedName("driver_fare_factor_priority")
		@Expose
		private Integer driverFareFactorPriority;
		@SerializedName("color")
		@Expose
		private String color;

		/**
		 * @return The regionId
		 */
		public Integer getRegionId() {
			return regionId;
		}

		/**
		 * @param regionId The region_id
		 */
		public void setRegionId(Integer regionId) {
			this.regionId = regionId;
		}

		/**
		 * @return The region
		 */
		public List<List<Region_>> getRegion() {
			return region;
		}

		/**
		 * @param region The region
		 */
		public void setRegion(List<List<Region_>> region) {
			this.region = region;
		}

		/**
		 * @return The cityId
		 */
		public Integer getCityId() {
			return cityId;
		}

		/**
		 * @param cityId The city_id
		 */
		public void setCityId(Integer cityId) {
			this.cityId = cityId;
		}

		/**
		 * @return The driverFareFactor
		 */
		public Double getDriverFareFactor() {
			return driverFareFactor;
		}

		/**
		 * @param driverFareFactor The driver_fare_factor
		 */
		public void setDriverFareFactor(Double driverFareFactor) {
			this.driverFareFactor = driverFareFactor;
		}

		/**
		 * @return The driverFareFactorPriority
		 */
		public Integer getDriverFareFactorPriority() {
			return driverFareFactorPriority;
		}

		/**
		 * @param driverFareFactorPriority The driver_fare_factor_priority
		 */
		public void setDriverFareFactorPriority(Integer driverFareFactorPriority) {
			this.driverFareFactorPriority = driverFareFactorPriority;
		}

		/**
		 * @return The color
		 */
		public String getColor() {
			return color;
		}

		/**
		 * @param color The color
		 */
		public void setColor(String color) {
			this.color = color;
		}

	}

	public class Region_ {

		@SerializedName("x")
		@Expose
		private Double x;
		@SerializedName("y")
		@Expose
		private Double y;

		/**
		 * @return The x
		 */
		public Double getX() {
			return x;
		}

		/**
		 * @param x The x
		 */
		public void setX(Double x) {
			this.x = x;
		}

		/**
		 * @return The y
		 */
		public Double getY() {
			return y;
		}

		/**
		 * @param y The y
		 */
		public void setY(Double y) {
			this.y = y;
		}

	}
}
