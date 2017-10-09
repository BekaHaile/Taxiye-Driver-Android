package product.clicklabs.jugnoo.driver.datastructure;

import product.clicklabs.jugnoo.driver.adapters.DailyRideDetailsAdapter;
import product.clicklabs.jugnoo.driver.retrofit.model.Tile;

/**
 * Created by aneeshbansal on 20/09/16.
 */
public class DailyEarningItem {

	private String text;
	private double value;
	private String time;
	private String date;
	private int type;
	private String status;
	private double earning;
	private Tile.Extras extras;

	private DailyRideDetailsAdapter.ViewType viewType;

	public DailyEarningItem(String text, double value, String time, String date, int type, String status, double earning, Tile.Extras extras, DailyRideDetailsAdapter.ViewType viewType) {
		this.text = text;
		this.value = value;
		this.time = time;
		this.date = date;
		this.type = type;
		this.status = status;
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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public Tile.Extras getExtras() {
		return extras;
	}

	public void setExtras(Tile.Extras extras) {
		this.extras = extras;
	}
}
