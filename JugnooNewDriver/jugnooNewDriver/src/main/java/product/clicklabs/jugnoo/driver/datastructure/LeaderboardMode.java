package product.clicklabs.jugnoo.driver.datastructure;

/**
 * Created by socomo20 on 7/24/15.
 */
public enum LeaderboardMode{
    DAILY(0), WEEKLY(1), MONTHLY(2);

    private int ordinal;

    LeaderboardMode(int ordinal){
        this.ordinal = ordinal;
    }

    public int getOrdinal(){
        return ordinal;
    }
}
