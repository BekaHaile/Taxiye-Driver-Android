package product.clicklabs.jugnoo.driver.datastructure;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import product.clicklabs.jugnoo.driver.retrofit.CurrencyModel;


public class FareStructureInfo extends CurrencyModel {

	@SerializedName("text")
	@Expose
	private String info;
	@SerializedName("value")
	@Expose
	private double value;

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
}
