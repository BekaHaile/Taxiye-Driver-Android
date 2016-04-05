package product.clicklabs.jugnoo.driver.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aneeshbansal on 02/04/16.
 */
public class DestinationDataResponse {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("regions")
	@Expose
	private List<Region> regions = new ArrayList<Region>();

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
	 * The regions
	 */
	public List<Region> getRegions() {
		return regions;
	}

	/**
	 *
	 * @param regions
	 * The regions
	 */
	public void setRegions(List<Region> regions) {
		this.regions = regions;
	}
	public class Region {

		@SerializedName("region_name")
		@Expose
		private String region;
		@SerializedName("region_id")
		@Expose
		private Integer regionId;
		@SerializedName("is_selected")
		@Expose
		private Integer isSelected;

		/**
		 *
		 * @return
		 * The region
		 */
		public String getRegion() {
			return region;
		}

		/**
		 *
		 * @param region
		 * The region
		 */
		public void setRegion(String region) {
			this.region = region;
		}

		/**
		 *
		 * @return
		 * The regionId
		 */
		public Integer getRegionId() {
			return regionId;
		}

		/**
		 *
		 * @param regionId
		 * The region_id
		 */
		public void setRegionId(Integer regionId) {
			this.regionId = regionId;
		}

		/**
		 *
		 * @return
		 * The isSelected
		 */
		public Integer getIsSelected() {
			return isSelected;
		}

		/**
		 *
		 * @param isSelected
		 * The is_selected
		 */
		public void setIsSelected(Integer isSelected) {
			this.isSelected = isSelected;
		}

	}

}

