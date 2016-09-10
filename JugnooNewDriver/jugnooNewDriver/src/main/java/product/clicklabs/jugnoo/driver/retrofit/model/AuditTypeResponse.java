package product.clicklabs.jugnoo.driver.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by aneeshbansal on 18/08/16.
 */
public class AuditTypeResponse {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("sa_flag")
	@Expose
	private Integer saFlag;
	@SerializedName("sa_status")
	@Expose
	private Integer saStatus;
	@SerializedName("sa_last_audit_string")
	@Expose
	private String saLastAuditString;
	@SerializedName("sa_next_audit_string")
	@Expose
	private String saNextAuditString;
	@SerializedName("njb_flag")
	@Expose
	private Integer njbFlag;
	@SerializedName("njb_status")
	@Expose
	private Integer njbStatus;
	@SerializedName("njb_promo_string")
	@Expose
	private String njbPromoString;
	@SerializedName("njb_count_string")
	@Expose
	private String njbCountString;
	@SerializedName("nja_flag")
	@Expose
	private Integer njaFlag;
	@SerializedName("nja_status")
	@Expose
	private Integer njaStatus;
	@SerializedName("nja_promo_string")
	@Expose
	private String njaPromoString;
	@SerializedName("nja_count_string")
	@Expose
	private String njaCountString;

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
	 * The saFlag
	 */
	public Integer getSaFlag() {
		return saFlag;
	}

	/**
	 *
	 * @param saFlag
	 * The sa_flag
	 */
	public void setSaFlag(Integer saFlag) {
		this.saFlag = saFlag;
	}

	/**
	 *
	 * @return
	 * The saStatus
	 */
	public Integer getSaStatus() {
		return saStatus;
	}

	/**
	 *
	 * @param saStatus
	 * The sa_status
	 */
	public void setSaStatus(Integer saStatus) {
		this.saStatus = saStatus;
	}

	/**
	 *
	 * @return
	 * The saLastAuditString
	 */
	public String getSaLastAuditString() {
		return saLastAuditString;
	}

	/**
	 *
	 * @param saLastAuditString
	 * The sa_last_audit_string
	 */
	public void setSaLastAuditString(String saLastAuditString) {
		this.saLastAuditString = saLastAuditString;
	}

	/**
	 *
	 * @return
	 * The saNextAuditString
	 */
	public String getSaNextAuditString() {
		return saNextAuditString;
	}

	/**
	 *
	 * @param saNextAuditString
	 * The sa_next_audit_string
	 */
	public void setSaNextAuditString(String saNextAuditString) {
		this.saNextAuditString = saNextAuditString;
	}

	/**
	 *
	 * @return
	 * The njbFlag
	 */
	public Integer getNjbFlag() {
		return njbFlag;
	}

	/**
	 *
	 * @param njbFlag
	 * The njb_flag
	 */
	public void setNjbFlag(Integer njbFlag) {
		this.njbFlag = njbFlag;
	}

	/**
	 *
	 * @return
	 * The njbStatus
	 */
	public Integer getNjbStatus() {
		return njbStatus;
	}

	/**
	 *
	 * @param njbStatus
	 * The njb_status
	 */
	public void setNjbStatus(Integer njbStatus) {
		this.njbStatus = njbStatus;
	}

	/**
	 *
	 * @return
	 * The njbPromoString
	 */
	public String getNjbPromoString() {
		return njbPromoString;
	}

	/**
	 *
	 * @param njbPromoString
	 * The njb_promo_string
	 */
	public void setNjbPromoString(String njbPromoString) {
		this.njbPromoString = njbPromoString;
	}

	/**
	 *
	 * @return
	 * The njbCountString
	 */
	public String getNjbCountString() {
		return njbCountString;
	}

	/**
	 *
	 * @param njbCountString
	 * The njb_count_string
	 */
	public void setNjbCountString(String njbCountString) {
		this.njbCountString = njbCountString;
	}

	/**
	 *
	 * @return
	 * The njaFlag
	 */
	public Integer getNjaFlag() {
		return njaFlag;
	}

	/**
	 *
	 * @param njaFlag
	 * The nja_flag
	 */
	public void setNjaFlag(Integer njaFlag) {
		this.njaFlag = njaFlag;
	}

	/**
	 *
	 * @return
	 * The njaStatus
	 */
	public Integer getNjaStatus() {
		return njaStatus;
	}

	/**
	 *
	 * @param njaStatus
	 * The nja_status
	 */
	public void setNjaStatus(Integer njaStatus) {
		this.njaStatus = njaStatus;
	}

	/**
	 *
	 * @return
	 * The njaPromoString
	 */
	public String getNjaPromoString() {
		return njaPromoString;
	}

	/**
	 *
	 * @param njaPromoString
	 * The nja_promo_string
	 */
	public void setNjaPromoString(String njaPromoString) {
		this.njaPromoString = njaPromoString;
	}

	/**
	 *
	 * @return
	 * The njaCountString
	 */
	public String getNjaCountString() {
		return njaCountString;
	}

	/**
	 *
	 * @param njaCountString
	 * The nja_count_string
	 */
	public void setNjaCountString(String njaCountString) {
		this.njaCountString = njaCountString;
	}

}
