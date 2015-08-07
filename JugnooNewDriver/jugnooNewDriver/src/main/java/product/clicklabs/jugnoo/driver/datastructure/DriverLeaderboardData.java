package product.clicklabs.jugnoo.driver.datastructure;

import java.util.ArrayList;

/**
 * Created by socomo20 on 7/24/15.
 */
public class DriverLeaderboardData {

    public String cityName;
    public int cityPositionDay, cityPositionWeek, cityPositionMonth, cityTotalDay, cityTotalWeek, cityTotalMonth;
    public int overallPositionDay, overallPositionWeek, overallPositionMonth, overallTotalDay, overallTotalWeek, overallTotalMonth;

    public ArrayList<DriverLeaderboard> driverLeaderboardsAll;

    public DriverLeaderboardData(String cityName,
                                 int cityPositionDay, int cityPositionWeek, int cityPositionMonth, int cityTotalDay, int cityTotalWeek, int cityTotalMonth,
                                 int overallPositionDay, int overallPositionWeek, int overallPositionMonth, int overallTotalDay, int overallTotalWeek, int overallTotalMonth,
                                 ArrayList<DriverLeaderboard> driverLeaderboardsAll){
        this.cityName = cityName;
        this.cityPositionDay = cityPositionDay;
        this.cityPositionWeek = cityPositionWeek;
        this.cityPositionMonth = cityPositionMonth;
        this.cityTotalDay = cityTotalDay;
        this.cityTotalWeek = cityTotalWeek;
        this.cityTotalMonth = cityTotalMonth;
        this.overallPositionDay = overallPositionDay;
        this.overallPositionWeek = overallPositionWeek;
        this.overallPositionMonth = overallPositionMonth;
        this.overallTotalDay = overallTotalDay;
        this.overallTotalWeek = overallTotalWeek;
        this.overallTotalMonth = overallTotalMonth;
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
        if(driverLeaderboards.size() == 0){
            driverLeaderboards.add(new DriverLeaderboard(0, "", "", 0, 0, 0, true));
        }
        return driverLeaderboards;
    }

}
