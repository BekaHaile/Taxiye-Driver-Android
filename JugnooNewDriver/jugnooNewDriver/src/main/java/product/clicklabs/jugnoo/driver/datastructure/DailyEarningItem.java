package product.clicklabs.jugnoo.driver.datastructure;

import product.clicklabs.jugnoo.driver.adapters.DailyRideDetailsAdapter;
import product.clicklabs.jugnoo.driver.retrofit.model.InfoTileResponse;

/**
 * Created by aneeshbansal on 20/09/16.
 */
public class DailyEarningItem {

	private String text;
	private double value;
	private String time;
	private double earning;
	private InfoTileResponse.Tile.Extras extras;

	private DailyRideDetailsAdapter.ViewType viewType;

	public DailyEarningItem(String text, double value, String time, double earning, InfoTileResponse.Tile.Extras extras, DailyRideDetailsAdapter.ViewType viewType) {
		this.text = text;
		this.value = value;
		this.time = time;
		this.earning = earning;
		this.extras = extras;
		this.viewType = viewType;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public double getEarning() {
		return earning;
	}

	public void setEarning(double earning) {
		this.earning = earning;
	}

	public DailyRideDetailsAdapter.ViewType getViewType() {
		return viewType;
	}

	public void setViewType(DailyRideDetailsAdapter.ViewType viewType) {
		this.viewType = viewType;
	}

	public InfoTileResponse.Tile.Extras getExtras() {
		return extras;
	}

	public void setExtras(InfoTileResponse.Tile.Extras extras) {
		this.extras = extras;
	}
}
