package product.clicklabs.jugnoo.driver.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SupportOption {

	@SerializedName("name")
	@Expose
	private String name;
	@SerializedName("tag")
	@Expose
	private String tag;
	@SerializedName("amount")
	@Expose
	private Double amount;

	public SupportOption(String name, String tag) {
		this.name = name;
		this.tag = tag;
	}


	public SupportOption(String name, String tag,Double amount) {
		this.name = name;
		this.tag = tag;
		this.amount = amount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}
}
