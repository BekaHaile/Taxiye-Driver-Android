package product.clicklabs.jugnoo.driver.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aneeshbansal on 19/08/16.
 */
public class AuditStateResponse {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("action")
	@Expose
	private Integer action;
	@SerializedName("images")
	@Expose
	private List<Image> images = new ArrayList<Image>();
	@SerializedName("last_unavailable_image_type")
	@Expose
	private Integer lastUnavailableImageType;

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
	 * The action
	 */
	public Integer getAction() {
		return action;
	}

	/**
	 *
	 * @param action
	 * The action
	 */
	public void setAction(Integer action) {
		this.action = action;
	}

	/**
	 *
	 * @return
	 * The images
	 */
	public List<Image> getImages() {
		return images;
	}

	/**
	 *
	 * @param images
	 * The images
	 */
	public void setImages(List<Image> images) {
		this.images = images;
	}

	/**
	 *
	 * @return
	 * The lastUnavailableImageType
	 */
	public Integer getLastUnavailableImageType() {
		return lastUnavailableImageType;
	}

	/**
	 *
	 * @param lastUnavailableImageType
	 * The last_unavailable_image_type
	 */
	public void setLastUnavailableImageType(Integer lastUnavailableImageType) {
		this.lastUnavailableImageType = lastUnavailableImageType;
	}


	public class Image {

		@SerializedName("image_type")
		@Expose
		private Integer imageType;
		@SerializedName("image_url")
		@Expose
		private String imageUrl;
		@SerializedName("rejection_reason")
		@Expose
		private String rejectionReason;

		/**
		 *
		 * @return
		 * The imageType
		 */
		public Integer getImageType() {
			return imageType;
		}

		/**
		 *
		 * @param imageType
		 * The image_type
		 */
		public void setImageType(Integer imageType) {
			this.imageType = imageType;
		}

		/**
		 *
		 * @return
		 * The imageUrl
		 */
		public String getImageUrl() {
			return imageUrl;
		}

		/**
		 *
		 * @param imageUrl
		 * The image_url
		 */
		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}

		/**
		 *
		 * @return
		 * The rejectionReason
		 */
		public String getRejectionReason() {
			return rejectionReason;
		}

		/**
		 *
		 * @param rejectionReason
		 * The rejection_reason
		 */
		public void setRejectionReason(String rejectionReason) {
			this.rejectionReason = rejectionReason;
		}

	}

}

