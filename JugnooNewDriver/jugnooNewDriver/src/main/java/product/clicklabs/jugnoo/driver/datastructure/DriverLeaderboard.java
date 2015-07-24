package product.clicklabs.jugnoo.driver.datastructure;

/**
 * Created by socomo20 on 7/24/15.
 */
public class DriverLeaderboard {

    public int userId;
    public String userName;
    public String cityName;
    public int numRides;
    public int leaderboardAreaMode;
    public int leaderboardMode;

    public DriverLeaderboard(int userId, String userName, String cityName, int numRides, int leaderboardAreaMode, int leaderboardMode){
        this.userId = userId;
        this.userName = userName;
        this.cityName = cityName;
        this.numRides = numRides;
        this.leaderboardAreaMode = leaderboardAreaMode;
        this.leaderboardMode = leaderboardMode;
    }

}
