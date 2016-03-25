package product.clicklabs.jugnoo.driver.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by socomo on 12/30/15.
 */
public class LeaderboardActivityResponse {

    @SerializedName("flag")
    @Expose
    private Integer flag;
    @SerializedName("nDownloads")
    @Expose
    private Integer nDownloads;
    @SerializedName("nFirstRides")
    @Expose
    private Integer nFirstRides;
    @SerializedName("nMoneyEarned")
    @Expose
    private Integer nMoneyEarned;
    @SerializedName("date")
    @Expose
    private String date;

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
     * The nDownloads
     */
    public Integer getNDownloads() {
        return nDownloads;
    }

    /**
     *
     * @param nDownloads
     * The nDownloads
     */
    public void setNDownloads(Integer nDownloads) {
        this.nDownloads = nDownloads;
    }

    /**
     *
     * @return
     * The nFirstRides
     */
    public Integer getNFirstRides() {
        return nFirstRides;
    }

    /**
     *
     * @param nFirstRides
     * The nFirstRides
     */
    public void setNFirstRides(Integer nFirstRides) {
        this.nFirstRides = nFirstRides;
    }

    /**
     *
     * @return
     * The nMoneyEarned
     */
    public Integer getNMoneyEarned() {
        return nMoneyEarned;
    }

    /**
     *
     * @param nMoneyEarned
     * The nMoneyEarned
     */
    public void setNMoneyEarned(Integer nMoneyEarned) {
        this.nMoneyEarned = nMoneyEarned;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
