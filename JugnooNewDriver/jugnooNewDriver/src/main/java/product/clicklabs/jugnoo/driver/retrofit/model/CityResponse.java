package product.clicklabs.jugnoo.driver.retrofit.model;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aneeshbansal on 29/01/16.
 */
public class CityResponse implements Serializable {


	@SerializedName("flag")
	@Expose
	private Integer flag;

	@SerializedName("cities")
	@Expose
	private List<City> cities = new ArrayList<City>();

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

	public class City implements Serializable{

		@SerializedName("city_id")
		@Expose
		private Integer cityId;
		@SerializedName("city_name")
		@Expose
		private String cityName;

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
}





