package product.clicklabs.jugnoo.driver.datastructure;

/**
 * Created by aneeshbansal on 29/01/16.
 */
public class CityInfo {
	public Integer cityId;
	public String cityName;


	public CityInfo(Integer cityId, String cityName) {
		this.cityId = cityId;
		this.cityName = cityName;

	}

	@Override
	public String toString() {
		return cityId + " " + cityName;
	}

}
