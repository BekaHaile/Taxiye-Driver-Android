package product.clicklabs.jugnoo.config;


import com.loopj.android.http.AsyncHttpClient;

import product.clicklabs.jugnoo.utils.CustomAsyncHttpClient;
import product.clicklabs.jugnoo.utils.CustomHttpRequesterFinal;
import product.clicklabs.jugnoo.utils.HttpRequesterFinal;


public class Config {

    private static final AppRunMode appRunMode = AppRunMode.DEVELOPMENT;


    // final variables
    private static final String SUPPORT_NUMBER = "+919023121121";
    private static final String DEBUG_PASSWORD = "3131";
    private static final String CLIENT_ID = "EEBUOvQq7RRJBxJm";
    private static final String CLIENT_SHARED_SECRET = "nqaK7HTwDT3epcpR5JuMWwojFv0KJnIv";
    public static final String GOOGLE_PROJECT_ID = "506849624961";
    public static final String MAPS_BROWSER_KEY = "AIzaSyAPIQoWfHI2iRZkSV8jU4jT_b9Qth4vMdY";
    public static final String FACEBOOK_APP_ID = "782131025144439";



    private static final String STATIC_FLURRY_KEY = "H8Y94ND8GPQTKKG5R2VY";
    private static final String DEV_SERVER_URL = "https://test.jugnoo.in:8012";
    private static final String LIVE_SERVER_URL = "https://dev.jugnoo.in:4012";
    private static final String TRIAL_SERVER_URL = "https://test.jugnoo.in:8200";





    private static final String DEFAULT_SERVER_URL = LIVE_SERVER_URL;

    private static ConfigMode configMode = ConfigMode.LIVE;




    // modifiable static variables
    private static String FLURRY_KEY = STATIC_FLURRY_KEY;
    private static String SERVER_URL = DEFAULT_SERVER_URL;



    /**
     * Initialize all the variable in this method
     *
     * @param appMode
     */
    public static void init(ConfigMode appMode) {

        switch (appMode) {
            case DEV:

                FLURRY_KEY = "abcd";
                SERVER_URL = DEV_SERVER_URL;

                break;

            case TRIAL:

                FLURRY_KEY = "abcd";
                SERVER_URL = TRIAL_SERVER_URL;

                break;

            case LIVE:

                FLURRY_KEY = STATIC_FLURRY_KEY;
                SERVER_URL = LIVE_SERVER_URL;

                break;

        }


    }




    // modifiable fields

    public static ConfigMode getConfigMode() {
        return configMode;
    }

    public static void setConfigMode(ConfigMode configMode) {
        Config.configMode = configMode;
        init(configMode);
    }

    public static String getFlurryKey() {
        init(configMode);
        return FLURRY_KEY;
    }

    public static String getServerUrl() {
        init(configMode);
        return SERVER_URL;
    }













    // final feilds

    public static String getSupportNumber() {
        return SUPPORT_NUMBER;
    }

    public static String getDebugPassword() {
        return DEBUG_PASSWORD;
    }

    public static String getClientId() {
        return CLIENT_ID;
    }

    public static String getClientSharedSecret() {
        return CLIENT_SHARED_SECRET;
    }

    public static String getGoogleProjectId() {
        return GOOGLE_PROJECT_ID;
    }

    public static String getMapsBrowserKey() {
        return MAPS_BROWSER_KEY;
    }

    public static String getFacebookAppId() {
        return FACEBOOK_APP_ID;
    }


    public static String getDevServerUrl() {
        return DEV_SERVER_URL;
    }

    public static String getLiveServerUrl() {
        return LIVE_SERVER_URL;
    }

    public static String getTrialServerUrl() {
        return TRIAL_SERVER_URL;
    }

    public static String getDefaultServerUrl() {
        return DEFAULT_SERVER_URL;
    }









    // final modules

    public static AsyncHttpClient getAsyncHttpClient(){
        if(AppRunMode.TESTING == appRunMode){
            return new CustomAsyncHttpClient();
        }
        else{
            return new AsyncHttpClient();
        }
    }


    public static HttpRequesterFinal getHttpRequester(){
        if(AppRunMode.TESTING == appRunMode){
            return new CustomHttpRequesterFinal();
        }
        else{
            return new HttpRequesterFinal();
        }
    }

}