package product.clicklabs.jugnoo.driver.retrofit.model;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponse;
import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponseKotlin;

/**
 * Created by aneeshbansal on 29/01/16.
 */
public class CityResponse extends FeedCommonResponseKotlin implements Serializable {


	@SerializedName("cities")
	@Expose
	private List<City> cities = new ArrayList<City>();
	@SerializedName("vehicle_types")
	@Expose
	private List<VehicleType> vehicleTypes = new ArrayList<VehicleType>();
	@SerializedName("current_city")
	@Expose
	private String currentCity;
	@SerializedName("city_id" )
	@Expose
	private String currentCityId = null;
	@SerializedName("offering_types")
	@Expose
	private List<OfferingType> offeringTypes = null;

	/**
	 * @return The currentCity
	 */
	public String getCurrentCity() {
		return currentCity;
	}

	/**
	 * @param currentCity The currentCity
	 */
	public void setCurrentCity(String currentCity) {
		this.currentCity = currentCity;
	}

	/**
	 *
	 * @return
	 * The cities
	 */
	public List<City> getCities() {
		return cities;
	}

	/**
	 *
	 * @param cities
	 * The cities
	 */
	public void setCities(List<City> cities) {
		this.cities = cities;
	}

	/**
	 *
	 * @return
	 * The vehicleTypes
	 */
	public List<VehicleType> getVehicleTypes() {
		return vehicleTypes;
	}

	/**
	 *
	 * @param vehicleTypes
	 * The vehicle_types
	 */
	public void setVehicleTypes(List<VehicleType> vehicleTypes) {
		this.vehicleTypes = vehicleTypes;
	}

	public List<OfferingType> getOfferingTypes() {
		return offeringTypes;
	}

	public void setOfferingTypes(List<OfferingType> offeringTypes) {
		this.offeringTypes = offeringTypes;
	}

	public String getCurrentCityId() {
		return currentCityId;
	}

	public void setCurrentCityId(String currentCityId) {
		this.currentCityId = currentCityId;
	}

	public class City implements Serializable{

		@SerializedName("city_id")
		@Expose
		private Integer cityId;
		@SerializedName("city_name")
		@Expose
		private String cityName;

		public City(Integer cityId, String cityName) {
			this.cityId = cityId;
			this.cityName = cityName;
		}

		/**
		 *
		 * @return
		 * The cityId
		 */
		public Integer getCityId() {
			return cityId;
		}

		/**
		 *
		 * @param cityId
		 * The city_id
		 */
		public void setCityId(Integer cityId) {
			this.cityId = cityId;
		}

		/**
		 *
		 * @return
		 * The cityName
		 */
		public String getCityName() {
			return cityName;
		}

		/**
		 *
		 * @param cityName
		 * The city_name
		 */
		public void setCityName(String cityName) {
			this.cityName = cityName;
		}

	}

	public class VehicleType implements Serializable {

		@SerializedName("vehicle_name")
		@Expose
		private String vehicleName;
		@SerializedName("vehicle_type")
		@Expose
		private Integer vehicleType;
		@SerializedName("images")
		@Expose
		private Images images;
		@SerializedName("driver_icon")
		@Expose
		private String driverIcon;

		private boolean selected;

		public VehicleType(String vehicleName, Integer vehicleType) {
			this.vehicleName = vehicleName;
			this.vehicleType = vehicleType;
		}

		/**
		 *
		 * @return
		 * The vehicleName
		 */
		public String getVehicleName() {
			return vehicleName;
		}

		/**
		 *
		 * @param vehicleName
		 * The vehicle_name
		 */
		public void setVehicleName(String vehicleName) {
			this.vehicleName = vehicleName;
		}

		/**
		 *
		 * @return
		 * The vehicleType
		 */
		public Integer getVehicleType() {
			return vehicleType;
		}

		/**
		 *
		 * @param vehicleType
		 * The vehicle_type
		 */
		public void setVehicleType(Integer vehicleType) {
			this.vehicleType = vehicleType;
		}

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}

		private String getImage() {
			if (images != null && images.getI0() != null && images.getI0().getDriverIcon() != null){
				return images.getI0().getDriverIcon();
			} else {
				return "";
			}
		}

		public String getDriverIcon() {
			if(!TextUtils.isEmpty(driverIcon)) {
				return driverIcon;
			} else {
				return getImage();
			}
		}

		public void setDriverIcon(String driverIcon) {
			this.driverIcon = driverIcon;
		}

		public class Images{
			@SerializedName("0")
			@Expose
			private I0 i0;

			public I0 getI0() {
				return i0;
			}

			public void setI0(I0 i0) {
				this.i0 = i0;
			}

			public class I0{
				@SerializedName("driver_icon")
				@Expose
				private String driverIcon;

				public String getDriverIcon() {
					return driverIcon;
				}

				public void setDriverIcon(String driverIcon) {
					this.driverIcon = driverIcon;
				}
			}
		}
	}

	public class OfferingType implements Serializable {

		@SerializedName("offering_name")
		@Expose
		private String offeringName;
		@SerializedName("offering_type")
		@Expose
		private Integer offeringType;

		public OfferingType(String offeringName, Integer offeringType) {
			this.offeringName = offeringName;
			this.offeringType = offeringType;
		}

		public String getOfferingName() {
			return offeringName;
		}

		public void setOfferingName(String offeringName) {
			this.offeringName = offeringName;
		}

		public Integer getOfferingType() {
			return offeringType;
		}

		public void setOfferingType(Integer offeringType) {
			this.offeringType = offeringType;
		}

	}
}





