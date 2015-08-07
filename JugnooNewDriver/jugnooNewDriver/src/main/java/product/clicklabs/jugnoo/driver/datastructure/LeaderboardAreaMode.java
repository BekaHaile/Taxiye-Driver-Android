package product.clicklabs.jugnoo.driver.datastructure;

/**
 * Created by socomo20 on 7/24/15.
 */
public enum LeaderboardAreaMode{
    LOCAL(0), OVERALL(1);

    private int ordinal;

    LeaderboardAreaMode(int ordinal){
        this.ordinal = ordinal;
    }

    public int getOrdinal(){
        return ordinal;
    }
}