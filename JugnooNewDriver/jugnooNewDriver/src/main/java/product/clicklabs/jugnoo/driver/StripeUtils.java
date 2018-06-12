package product.clicklabs.jugnoo.driver;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import org.apache.http.client.utils.URIBuilder;

import product.clicklabs.jugnoo.driver.utils.Prefs;

/**
 * Created by Parminder Saini on 09/05/18.
 */
public abstract class StripeUtils {
    public static  final String STRIPE_CONNECT_URL = "https://connect.stripe.com/express/oauth/authorize?";
    private static final String KEY_CLIENT_ID = "client_id";
    private static final String KEY_RESPONSE_TYPE = "response_type";
    private static final String KEY_STATE = "state";
    private static final String KEY_REDIRECT_URI = "redirect_uri";
    public static final String STRIPE_SUCCESS_URL = /*Data.DEFAULT_SERVER_URL +*/ "/static/success";
    public static final String STRIPE_FAILURE_URL = /*Data.DEFAULT_SERVER_URL + */"/static/failure";
    public static final int STRIPE_ACCOUNT_CONNECTED = 2;
    public static final int STRIPE_ACCOUNT_AVAILABLE = 1;



    public static Uri.Builder stripeConnectBuilder(Context context){
        Uri.Builder uriBuilder =  Uri.parse(STRIPE_CONNECT_URL)
                .buildUpon().appendQueryParameter(KEY_CLIENT_ID, Data.SERVER_URL.equalsIgnoreCase(Data.LIVE_SERVER_URL)?
                 BuildConfig.STRIPE_CLIENT_ID_LIVE:BuildConfig.STRIPE_CLIENT_ID_DEV).appendQueryParameter(KEY_STATE,Data.userData.accessToken);
        String stripeRedirectUri = Prefs.with(context).getString(Constants.STRIPE_REDIRECT_URI,null);
        if(!TextUtils.isEmpty(stripeRedirectUri)){
            uriBuilder.appendQueryParameter(KEY_REDIRECT_URI,stripeRedirectUri);
        }
        return uriBuilder;

    }

}
