package product.clicklabs.jugnoo.driver.dodo.datastructure;

/**
 * Created by aneesh on 6/2/16.
 */
public enum DeliveryStatus {
    PENDING(0),
    CANCELLED(1),
    COMPLETED(2),
    RETURN(3)
    ;

    private int ordinal;

    DeliveryStatus(int ordinal){
        this.ordinal = ordinal;
    }

    public int getOrdinal(){
        return ordinal;
    }

}
