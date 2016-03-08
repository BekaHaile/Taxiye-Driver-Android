package product.clicklabs.jugnoo.driver.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aneeshbansal on 24/11/15.
 */


public class NotificationAlarmResponse {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("links")
	@Expose
	private List<Link> links = new ArrayList<Link>();

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
	 * The links
	 */
	public List<Link> getLinks() {
		return links;
	}

	/**
	 *
	 * @param links
	 * The links
	 */
	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public class Link {

		@SerializedName("file_id")
		@Expose
		private String fileId;
		@SerializedName("file_url")
		@Expose
		private String fileUrl;

		/**
		 *
		 * @return
		 * The fileId
		 */
		public String getFileId() {
			return fileId;
		}

		/**
		 *
		 * @param fileId
		 * The file_id
		 */
		public void setFileId(String fileId) {
			this.fileId = fileId;
		}

		/**
		 *
		 * @return
		 * The fileUrl
		 */
		public String getFileUrl() {
			return fileUrl;
		}

		/**
		 *
		 * @param fileUrl
		 * The file_url
		 */
		public void setFileUrl(String fileUrl) {
			this.fileUrl = fileUrl;
		}

	}

}
