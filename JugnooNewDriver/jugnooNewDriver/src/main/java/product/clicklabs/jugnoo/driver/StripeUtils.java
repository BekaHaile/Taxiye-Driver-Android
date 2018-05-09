package product.clicklabs.jugnoo.driver;

import android.net.Uri;

/**
 * Created by Parminder Saini on 09/05/18.
 */
public abstract class StripeUtils {
    public static  final String STRIPE_CONNECT_URL = "https://connect.stripe.com/express/oauth/authorize?";
    private static final String KEY_CLIENT_ID = "client_id";
    private static final String KEY_RESPONSE_TYPE = "response_type";
    public static final String STRIPE_CALLBACK_URL = "http://www.jugnoo.in";

    public static Uri.Builder stripeConnectBuilder(){
        return Uri.parse(STRIPE_CONNECT_URL)
                .buildUpon().appendQueryParameter(KEY_CLIENT_ID,BuildConfig.STRIPE_CLIENT_ID)
                .appendQueryParameter(KEY_RESPONSE_TYPE,"code");

    }

}
