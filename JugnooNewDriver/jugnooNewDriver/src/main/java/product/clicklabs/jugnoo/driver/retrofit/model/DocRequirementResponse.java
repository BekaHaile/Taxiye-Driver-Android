package product.clicklabs.jugnoo.driver.retrofit.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class DocRequirementResponse {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("data")
	@Expose
	private List<DocumentData> data = new ArrayList<DocumentData>();
	@SerializedName("phone_no")
	@Expose
	private String userPhoneNo;
	@SerializedName("img_pixel")
	@Expose
	private int imgPixel;

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
	 * @return The data
	 */
	public List<DocumentData> getData() {
		return data;
	}

	/**
	 * @param data The data
	 */
	public void setData(List<DocumentData> data) {
		this.data = data;
	}


	/**
	 * @return The userPhoneNo
	 */
	public String getuserPhoneNo() {
		return userPhoneNo;
	}

	/**
	 * @param userPhoneNo The user_email
	 */
	public void setuserPhoneNo(String userPhoneNo) {
		this.userPhoneNo = userPhoneNo;
	}

	public int getImgPixel() {
		return imgPixel;
	}

	public void setImgPixel(int imgPixel) {
		this.imgPixel = imgPixel;
	}

	public class DocumentData {

		@SerializedName("doc_type_num")
		@Expose
		private Integer docTypeNum;
		@SerializedName("doc_type_text")
		@Expose
		private String docTypeText;
		@SerializedName("doc_requirement")
		@Expose
		private Integer docRequirement;
		@SerializedName("doc_status")
		@Expose
		private String docStatus;
		@SerializedName("doc_count")
		@Expose
		private Integer docCount;
		@SerializedName("is_editable")
		@Expose
		private Integer isEditable;
		@SerializedName("doc_url")
		@Expose
		private ArrayList<String> docUrl = new ArrayList<String>();
		@SerializedName("reason")
		@Expose
		private String reason;
		@SerializedName("instructions")
		@Expose
		private String instructions;

		/**
		 *
		 * @return
		 * The docTypeNum
		 */
		public Integer getDocTypeNum() {
			return docTypeNum;
		}

		/**
		 *
		 * @param docTypeNum
		 * The doc_type_num
		 */
		public void setDocTypeNum(Integer docTypeNum) {
			this.docTypeNum = docTypeNum;
		}

		/**
		 *
		 * @return
		 * The docTypeText
		 */
		public String getDocTypeText() {
			return docTypeText;
		}

		/**
		 *
		 * @param docTypeText
		 * The doc_type_text
		 */
		public void setDocTypeText(String docTypeText) {
			this.docTypeText = docTypeText;
		}

		/**
		 *
		 * @return
		 * The docRequirement
		 */
		public Integer getDocRequirement() {
			return docRequirement;
		}

		/**
		 *
		 * @param docRequirement
		 * The doc_requirement
		 */
		public void setDocRequirement(Integer docRequirement) {
			this.docRequirement = docRequirement;
		}

		/**
		 *
		 * @return
		 * The docStatus
		 */
		public String getDocStatus() {
			return docStatus;
		}

		/**
		 *
		 * @param docStatus
		 * The doc_status
		 */
		public void setDocStatus(String docStatus) {
			this.docStatus = docStatus;
		}


		public Integer getDocCount() {
			return docCount;
		}

		public void setDocCount(Integer docCount) {
			this.docCount = docCount;
		}

		public Integer getIsEditable() {
			return isEditable;
		}

		public void setIsEditable(Integer isEditable) {
			this.isEditable = isEditable;
		}

		/**
		 *
		 * @return
		 * The docUrl
		 */
		public ArrayList<String> getDocUrl() {
			return docUrl;
		}

		/**
		 *
		 * @param docUrl
		 * The doc_url
		 */
		public void setDocUrl(ArrayList<String> docUrl) {
			this.docUrl = docUrl;
		}

		/**
		 *
		 * @return
		 * The reason
		 */
		public String getReason() {
			return reason;
		}

		/**
		 *
		 * @param reason
		 * The reason
		 */
		public void setReason(String reason) {
			this.reason = reason;
		}

		/**
		 * Getter for instructions
		 *
		 * @return : Returns the value of instructions
		 */
		public String getInstructions() {
			return instructions;
		}
	}

}



