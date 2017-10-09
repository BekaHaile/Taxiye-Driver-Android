package product.clicklabs.jugnoo.driver.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aneeshbansal on 14/09/16.
 */


public class InfoTileResponse {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("tiles")
	@Expose
	private List<Tile> tiles = new ArrayList<Tile>();

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
	 * The tiles
	 */
	public List<Tile> getTiles() {
		return tiles;
	}

	/**
	 *
	 * @param tiles
	 * The tiles
	 */
	public void setTiles(List<Tile> tiles) {
		this.tiles = tiles;
	}

}



