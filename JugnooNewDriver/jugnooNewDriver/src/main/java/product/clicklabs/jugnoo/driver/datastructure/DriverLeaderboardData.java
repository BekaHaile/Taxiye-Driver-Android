package product.clicklabs.jugnoo.driver.datastructure;

import java.util.ArrayList;

/**
 * Created by socomo20 on 7/24/15.
 */
public class DriverLeaderboardData {

    public String cityName;
    public int cityPosition, cityTotal;
    public int overallPosition, overallTotal;

    public ArrayList<DriverLeaderboard> driverLeaderboardsAll;

    public DriverLeaderboardData(String cityName, int cityPosition, int cityTotal, int overallPosition, int overallTotal,
                                 ArrayList<DriverLeaderboard> driverLeaderboardsAll){
        this.cityName = cityName;
        this.cityPosition = cityPosition;
        this.cityTotal = cityTotal;
        this.overallPosition = overallPosition;
        this.overallTotal = overallTotal;
        this.driverLeaderboardsAll = new ArrayList<DriverLeaderboard>();
        this.driverLeaderboardsAll.addAll(driverLeaderboardsAll);
    }

    public ArrayList<DriverLeaderboard> getDriverLeaderboardsList(LeaderboardAreaMode leaderboardAreaMode, LeaderboardMode leaderboardMode){
        ArrayList<DriverLeaderboard> driverLeaderboards = new ArrayList<DriverLeaderboard>();
        for(DriverLeaderboard driverLeaderboard : driverLeaderboardsAll){
            if(leaderboardAreaMode.getOrdinal() == driverLeaderboard.leaderboardAreaMode && leaderboardMode.getOrdinal() == driverLeaderboard.leaderboardMode){
                driverLeaderboards.add(driverLeaderboard);
            }
        }
        return driverLeaderboards;
    }

}
