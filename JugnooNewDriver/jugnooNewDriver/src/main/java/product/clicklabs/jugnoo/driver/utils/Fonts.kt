package product.clicklabs.jugnoo.driver.utils

import android.content.Context
import android.graphics.Typeface

class Fonts {

    companion object {

        public var mavenMedium: Typeface? = null
        public var mavenRegular: Typeface? = null
        public var mavenLight: Typeface? = null
        public var mavenBold: Typeface? = null
        public var digitalRegular: Typeface? = null

        @JvmStatic
        fun mavenMedium(appContext: Context): Typeface? {                                            // accessing fonts functions
            if (mavenMedium == null) {
                mavenMedium = Typeface.createFromAsset(appContext.assets, "fonts/maven_pro_medium.ttf")
            }
            return mavenMedium
        }

        @JvmStatic
        fun mavenRegular(appContext: Context): Typeface? {                                            // accessing fonts functions
            if (mavenRegular == null) {
                mavenRegular = Typeface.createFromAsset(appContext.assets, "fonts/maven_pro_regular.otf")
            }
            return mavenRegular
        }

        @JvmStatic
        fun mavenLight(appContext: Context): Typeface? {                                            // accessing fonts functions
            if (mavenLight == null) {
                mavenLight = Typeface.createFromAsset(appContext.assets, "fonts/maven_pro_light.otf")
            }
            return mavenLight
        }

        @JvmStatic
        fun mavenBold(appContext: Context): Typeface? {                                            // accessing fonts functions
            if (mavenBold == null) {
                mavenBold = Typeface.createFromAsset(appContext.assets, "fonts/maven_pro_bold.ttf")
            }
            return mavenBold
        }

        @JvmStatic
        fun digitalRegular(appContext: Context): Typeface? {                                            // accessing fonts functions
            if (digitalRegular == null) {
                digitalRegular = Typeface.createFromAsset(appContext.assets, "fonts/digital_regular.ttf")
            }
            return digitalRegular
        }
    }


}