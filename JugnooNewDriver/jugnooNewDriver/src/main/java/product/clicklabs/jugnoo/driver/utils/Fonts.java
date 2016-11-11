package product.clicklabs.jugnoo.driver.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by shankar on 4/29/15.
 */
public class Fonts {

    private static Typeface latoRegular, latoLight, mavenRegular, mavenLight, mavenBold, avenirNext;																// fonts declaration


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

    public static Typeface mavenBold(Context appContext) {											// accessing fonts functions
        if (mavenBold == null) {
            mavenBold = Typeface.createFromAsset(appContext.getAssets(), "fonts/MavenPro-Bold.ttf");
        }
        return mavenBold;
    }

    public static Typeface avenirNext(Context appContext) {											// accessing fonts functions
        if (avenirNext == null) {
            avenirNext = Typeface.createFromAsset(appContext.getAssets(), "fonts/avenir_next_demi.otf");
        }
        return avenirNext;
    }

}
