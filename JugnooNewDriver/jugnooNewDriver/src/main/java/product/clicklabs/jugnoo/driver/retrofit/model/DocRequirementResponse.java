package product.clicklabs.jugnoo.driver.retrofit.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class DocRequirementResponse {

	@SerializedName("log")
	@Expose
	private String log;
	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("data")
	@Expose
	private List<DocumentData> data = new ArrayList<DocumentData>();
	@SerializedName("id")
	@Expose
	private Integer id;
	@SerializedName("user_email")
	@Expose
	private String userEmail;

	/**
	 * @return The log
	 */
	public String getLog() {
		return log;
	}

	/**
	 * @param log The log
	 */
	public void setLog(String log) {
		this.log = log;
	}

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
	 * @return The id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id The id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return The userEmail
	 */
	public String getUserEmail() {
		return userEmail;
	}

	/**
	 * @param userEmail The user_email
	 */
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public class DocumentData {

		@SerializedName("doc_type_text")
		@Expose
		private String docType;
		@SerializedName("doc_type_num")
		@Expose
		private Integer docTypeNum;
		@SerializedName("doc_requirement")
		@Expose
		private String docRequirement;
		@SerializedName("doc_status")
		@Expose
		private String docStatus;

		/**
		 * @return The docType
		 */
		public String getDocType() {
			return docType;
		}

		/**
		 * @param docType The doc_type
		 */
		public void setDocType(String docType) {
			this.docType = docType;
		}

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
		 * @return The docRequirement
		 */
		public String getDocRequirement() {
			return docRequirement;
		}

		/**
		 * @param docRequirement The doc_requirement
		 */
		public void setDocRequirement(String docRequirement) {
			this.docRequirement = docRequirement;
		}

		/**
		 * @return The docStatus
		 */
		public String getDocStatus() {
			return docStatus;
		}

		/**
		 * @param docStatus The doc_status
		 */
		public void setDocStatus(String docStatus) {
			this.docStatus = docStatus;
		}

	}

}



