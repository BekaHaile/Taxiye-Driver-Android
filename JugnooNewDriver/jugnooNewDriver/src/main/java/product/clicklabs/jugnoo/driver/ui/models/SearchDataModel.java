package product.clicklabs.jugnoo.driver.ui.models;

import android.content.Context;

/**
 * Created by Parminder Saini on 22/06/18.
 */
public  abstract class SearchDataModel {


    private String name;

    public abstract int getImage(Context context);

    public String getName() {
        return name;
    }
}
