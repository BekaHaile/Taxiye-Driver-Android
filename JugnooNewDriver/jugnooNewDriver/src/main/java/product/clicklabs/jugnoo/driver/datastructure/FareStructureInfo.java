package product.clicklabs.jugnoo.driver.datastructure;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class FareStructureInfo {

	@SerializedName("info")
	@Expose
	private String info;
	@SerializedName("value")
	@Expose
	private String value;

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
