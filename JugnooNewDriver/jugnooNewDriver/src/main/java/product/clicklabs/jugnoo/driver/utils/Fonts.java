package product.clicklabs.jugnoo.driver.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by shankar on 4/29/15.
 */
public class Fonts {

    private static Typeface latoRegular, latoLight, mavenRegular, mavenLight;																// fonts declaration


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

    public static Typeface mavenRegular(Context appContext) {											// accessing fonts functions
        if (mavenRegular == null) {
            mavenRegular = Typeface.createFromAsset(appContext.getAssets(), "fonts/maven_pro_regular.otf");
        }
        return mavenRegular;
    }

    public static Typeface mavenLight(Context appContext) {											// accessing fonts functions
        if (mavenLight == null) {
            mavenLight = Typeface.createFromAsset(appContext.getAssets(), "fonts/maven_pro_light_300.otf");
        }
        return mavenLight;
    }

}
