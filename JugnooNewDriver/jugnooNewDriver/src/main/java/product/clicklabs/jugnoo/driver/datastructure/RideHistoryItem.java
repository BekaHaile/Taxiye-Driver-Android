package product.clicklabs.jugnoo.driver.datastructure;

import product.clicklabs.jugnoo.driver.retrofit.model.Tile;

/**
 * Created by aneeshbansal on 20/09/16.
 */
public class RideHistoryItem {

	private String date;
	private String time;
	private double earning;
	private int type;
	private String status;
	private Tile.Extras extras;


	public RideHistoryItem(String date, String time, double earning, int type, String status, Tile.Extras extras) {
		this.date = date;
		this.time = time;
		this.earning = earning;
		this.type = type;
		this.status = status;
		this.extras = extras;
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Tile.Extras getExtras() {
		return extras;
	}

	public void setExtras(Tile.Extras extras) {
		this.extras = extras;
	}
}
