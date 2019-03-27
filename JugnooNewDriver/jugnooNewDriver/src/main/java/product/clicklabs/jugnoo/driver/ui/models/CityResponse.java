package product.clicklabs.jugnoo.driver.ui.models;

import android.content.Context;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aneeshbansal on 29/01/16.
 */
public class CityResponse extends FeedCommonResponseKotlin {


	@SerializedName("cities")
	@Expose
	private List<City> cities = new ArrayList<City>();

	@SerializedName("current_city_details")
	@Expose
	private City currentCity;

	@SerializedName("referral_code_used")
	@Expose
	private String promoCode;

	@SerializedName("show_referral")
	@Expose
	private int showPromo;

	public String getPromoCode() {
		return promoCode;
	}

	public boolean getShowPromo() {
		return showPromo==1;
	}

	/**
	 * @return The currentCity
	 */
	public City getCurrentCity() {
		return currentCity;
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




	public class City extends SearchDataModel {

		@SerializedName("city_id")
		@Expose
		private Integer cityId;
		@SerializedName("city_name")
		@Expose
		private String cityName;

		@SerializedName("vehicle_types")
		@Expose
		private List<VehicleType> vehicleTypes = new ArrayList<VehicleType>();

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
		 * @return
		 * The cityName
		 */
		public String getCityName() {
			return cityName;
		}

		public List<VehicleType> getVehicleTypes() {
			return vehicleTypes;
		}

		@Override
		public String getLabel() {
			return cityName;
		}

		@Override
		public int getImage(Context context) {
			return -1;
		}

		@Override
		public boolean showImage() {
			return false;
		}

		@Override
		public boolean isSelected() {
			return false;
		}
	}

	public class VehicleType{

		@SerializedName("vehicle_name")
		@Expose
		private String vehicleName;
		@SerializedName("vehicle_type")
		@Expose
		private Integer vehicleType;
		@SerializedName("region_id")
		@Expose
		private int regionId;
		@SerializedName("applicable_gender")
		@Expose
		private int applicableGender;
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


		public String getDriverIcon() {
			return driverIcon;
		}

		public void setDriverIcon(String driverIcon) {
			this.driverIcon = driverIcon;
		}


		public int getRegionId() {
			return regionId;
		}

		public void setRegionId(int regionId) {
			this.regionId = regionId;
		}

		public int getApplicableGender() {
			return applicableGender;
		}

		public void setApplicableGender(int applicableGender) {
			this.applicableGender = applicableGender;
		}
	}

}





