package product.clicklabs.jugnoo.driver.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shankar on 12/30/15.
 */
public class Ranklist {

	@SerializedName("downloads")
	@Expose
	private Integer downloads;
	@SerializedName("rank")
	@Expose
	private Integer rank;
	@SerializedName("name")
	@Expose
	private String name;

	private boolean isUser = false;

	public Ranklist(Integer rank, Integer downloads, String name, boolean isUser){
		this.rank = rank;
		this.downloads = downloads;
		this.name = name;
		this.isUser = isUser;
	}

	/**
	 * @return The downloads
	 */
	public Integer getDownloads() {
		return downloads;
	}

	public String getDownloadsStr(){
		if(downloads > 0){
			return ""+downloads;
		}
		else{
			return "-";
		}
	}

	/**
	 * @param downloads The downloads
	 */
	public void setDownloads(Integer downloads) {
		this.downloads = downloads;
	}

	/**
	 * @return The rank
	 */
	public Integer getRank() {
		return rank;
	}

	public String getRankStr(){
		if(rank > 0){
			return ""+rank;
		}
		else{
			return "-";
		}
	}

	/**
	 * @param rank The rank
	 */
	public void setRank(Integer rank) {
		this.rank = rank;
	}

	/**
	 * @return The name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		try{
			if(((Ranklist)o).getRank().equals(this.getRank())){
				return true;
			} else{
				return false;
			}
		} catch(Exception e){
			return false;
		}
	}

	public boolean getIsUser() {
		return isUser;
	}

	public void setIsUser(boolean isUser) {
		this.isUser = isUser;
	}
}
