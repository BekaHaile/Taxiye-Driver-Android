package product.clicklabs.jugnoo.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by shankar on 4/29/15.
 */
public class Fonts {

    private static Typeface latoRegular, latoLight;																// fonts declaration


    public static Typeface latoRegular(Context appContext) {											// accessing fonts functions
        if (latoRegular == null) {
            latoRegular = Typeface.createFromAsset(appContext.getAssets(), "fonts/lato_regular.ttf");
        }
        return latoRegular;
    }

    public static Typeface latoLight(Context appContext) {											// accessing fonts functions
        if (latoLight == null) {
            latoLight = Typeface.createFromAsset(appContext.getAssets(), "fonts/lato_light.ttf");
        }
        return latoLight;
    }

}
