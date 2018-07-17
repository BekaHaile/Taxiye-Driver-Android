package product.clicklabs.jugnoo.driver.stripe;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import product.clicklabs.jugnoo.driver.BuildConfig;
import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.utils.Prefs;

/**
 * Created by Parminder Saini on 09/05/18.
 */
public abstract class StripeUtils {
    public static  final String STRIPE_CONNECT_EXPRESS_URL = "https://connect.stripe.com/express/oauth/authorize?";
    public static final  String  STRIPE_CONNECT_STANDARD_URL = "https://connect.stripe.com/oauth/authorize?";
    public static final  String  STRIPE_CONNECT_STANDARD_LOGIN_URL = "https://connect.stripe.com/login?";

    private static final String KEY_CLIENT_ID = "client_id";
    private static final String KEY_STATE = "state";
    private static final String KEY_REDIRECT_URI = "redirect_uri";
    private static final String KEY_SCOPE = "scope";
    private static final String KEY_FORCE_LOGIN = "true";
    private static final String KEY_RESPONSE_TYPE = "response_type";
    public static final String STRIPE_SUCCESS_URL = /*Data.DEFAULT_SERVER_URL +*/ "/static/success";
    public static final String STRIPE_FAILURE_URL = /*Data.DEFAULT_SERVER_URL + */"/static/failure";
    public static final int STRIPE_EXPRESS_ACCOUNT_CONNECTED = 2;
    public static final int STRIPE_EXPRESS_ACCOUNT_AVAILABLE = 1;
    public static final int STRIPE_STANDARD_ACCOUNT_AVAILABLE = 3;
    public static final int STRIPE_STANDARD_ACCOUNT_CONNECTED = 4;



    public static Uri.Builder stripeExpressConnectBuilder(Context context){

        return commonStripeQueryBuilder(context,STRIPE_CONNECT_EXPRESS_URL,true);

    }


    public static Uri.Builder stripeStandardConnectBuilder(Context context){
        Uri.Builder uriBuilder = commonStripeQueryBuilder(context,STRIPE_CONNECT_STANDARD_URL,true);
        uriBuilder.appendQueryParameter(KEY_SCOPE,"read_write");
        uriBuilder.appendQueryParameter(KEY_RESPONSE_TYPE,"code");
        return uriBuilder;

    }

    public static Uri.Builder stripeConnectLoginBuilder(Context context){
        Uri.Builder uriBuilder = commonStripeQueryBuilder(context,STRIPE_CONNECT_STANDARD_LOGIN_URL,false);
        uriBuilder.appendQueryParameter(KEY_SCOPE,"read_write");
        uriBuilder.appendQueryParameter(KEY_FORCE_LOGIN,"true");
        return uriBuilder;

    }


    private static Uri.Builder commonStripeQueryBuilder(Context context, String queryUrl,boolean register){
        Uri.Builder uriBuilder =  Uri.parse(queryUrl)
                .buildUpon().appendQueryParameter(KEY_CLIENT_ID, Data.SERVER_URL.equalsIgnoreCase(Data.LIVE_SERVER_URL)?
                        BuildConfig.STRIPE_CLIENT_ID_LIVE:BuildConfig.STRIPE_CLIENT_ID_DEV);

                if(register){
                    uriBuilder.appendQueryParameter(KEY_STATE,Data.userData.accessToken);
                    String stripeRedirectUri = Prefs.with(context).getString(Constants.STRIPE_REDIRECT_URI,null);
                    if(!TextUtils.isEmpty(stripeRedirectUri)){
                        uriBuilder.appendQueryParameter(KEY_REDIRECT_URI,stripeRedirectUri);
                    }
                }

        return uriBuilder;

    }

    public static String getStripeCardDisplayString(Context context,String last_4){
        StringBuilder formString = new StringBuilder();

        for (int i = 0; i < 12; i++) {
            if (i != 0 && i % 4 == 0) {
                formString.append(" ");
            }
            formString.append(context.getString(R.string.bullet));

        }
        formString.append(" ");
        formString.append(last_4);
        return formString.toString();

    }



}
