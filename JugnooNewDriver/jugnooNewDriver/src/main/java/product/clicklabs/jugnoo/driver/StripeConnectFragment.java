/*
package product.clicklabs.jugnoo.driver;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.sylvan.tutor.BuildConfig;
import com.sylvan.tutor.R;
import com.sylvan.tutor.base.BaseFragment;
import com.sylvan.tutor.constant.AppConstant;
import com.sylvan.tutor.retrofit.APIError;
import com.sylvan.tutor.retrofit.ApiResponse;
import com.sylvan.tutor.retrofit.ResponseResolver;
import com.sylvan.tutor.retrofit.RestClient;
import com.sylvan.tutor.ui.homeDashboard.HomeActivity;
import com.sylvan.tutor.util.CommonData;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.sylvan.tutor.BuildConfig.CONNECT_STRIPE_LINK;
import static com.sylvan.tutor.constant.ApiKeyConstant.AMPERSAND;
import static com.sylvan.tutor.constant.ApiKeyConstant.ENCODING_LNG;
import static com.sylvan.tutor.constant.ApiKeyConstant.ERROR;
import static com.sylvan.tutor.constant.ApiKeyConstant.ERROR_DESC;
import static com.sylvan.tutor.constant.ApiKeyConstant.UNSUPPORTED_ENCODING_EXCEPTION;
import static javax.xml.datatype.DatatypeConstants.EQUAL;

*/
/**
 * Stripe Connect Fragment : which opens stripe dashboard in webView
 *//*

public class StripeConnectFragment extends BaseFragment implements AppConstant {

    */
/**
     * The constant RESULT_CONNECTED.
     *//*

    public static final int RESULT_CONNECTED = 1;
    */
/**
     * The constant RESULT_ERROR.
     *//*

    public static final int RESULT_ERROR = 2;

    private static final String CALLBACK_URL = BuildConfig.REDIRECT_URL_STRIPE;

    private static final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);
    private LinearLayout mContent;
    private String tutorId;
    private String stripeCode;
    private View rootView;
    private Activity mContext;
    private AppCompatTextView tvHeading, tvSignOut;
    private WebView webView;
    private LinearLayout llMyAccount;

    */
/**
     * Creating map to encode URL
     *
     * @param query mQuery
     * @return pairs
     *//*

    private static Map<String, String> splitQuery(final String query) {
        final Map<String, String> queryPairs = new LinkedHashMap<String, String>();
        try {
            final String[] pairs = query.split(AMPERSAND);
            for (final String pair : pairs) {
                final int idx = pair.indexOf(EQUAL);
                queryPairs.put(URLDecoder.decode(pair.substring(0, idx), ENCODING_LNG),
                        URLDecoder.decode(pair.substring(idx + 1), ENCODING_LNG));
            }
        } catch (final UnsupportedEncodingException e) {
            queryPairs.put(ERROR, UNSUPPORTED_ENCODING_EXCEPTION);
            queryPairs.put(ERROR_DESC, e.getMessage());
        }
        return queryPairs;
    }

    */
/**
     * Removing cookies if exists from webView
     *
     * @param context mContext
     *//*

    private static void removeAllCookies(final Context context) {
        //On Lollipop and beyond, the CookieManager singleton works fine by itself.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(context);
        }
        final CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        mContext = (Activity) context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tutorId = CommonData.getUserData().getId();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_my_wallet, container, false);
        }

        // setting toolbar
        setToolbar();
        setUpWebView(CONNECT_STRIPE_LINK + tutorId);

        return rootView;
    }

    */
/**
     * Setting webView to communicate
     *
     * @param mAuthUrl url to be redirected on
     *//*

    @SuppressWarnings("deprecation")
    @SuppressLint({"SetJavaScriptEnabled", "NewApi"})
    private void setUpWebView(final String mAuthUrl) {

        showLoading();

        // existing web view
        webView = rootView.findViewById(R.id.webView);
        webView.setVisibility(View.VISIBLE);

        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(new OAuthWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);

        // load authentication url
        webView.loadUrl(mAuthUrl);

        removeAllCookies(mContext);
    }

    private void setToolbar() {

        (rootView.findViewById(R.id.tvCancel)).setVisibility(View.GONE);
        (rootView.findViewById(R.id.ivBack)).setVisibility(View.VISIBLE);

        llMyAccount = rootView.findViewById(R.id.llMyAccount);

        tvHeading = rootView.findViewById(R.id.tvTbHeading);
        tvSignOut = rootView.findViewById(R.id.tvSignOut);

        tvSignOut.setVisibility(View.VISIBLE);
        tvHeading.setText(R.string.text_stripe_connect);

        tvSignOut.setOnClickListener(this);
        (rootView.findViewById(R.id.ivBack)).setOnClickListener(this);

        llMyAccount.setVisibility(View.GONE);
    }

    */
/**
     * Method is called when an error comes while loading a webView.
     *
     * @param parameters params
     *//*

    private void onError(final Map<String, String> parameters) {

        hideLoading();

        if (getTargetFragment() != null) {

            final Intent returnIntent = new Intent();
            returnIntent.putExtra(ERROR, parameters.get(ERROR));
            returnIntent.putExtra(ERROR_DESC, parameters.get(ERROR_DESC));

            getTargetFragment().onActivityResult(
                    getTargetRequestCode(),
                    RESULT_ERROR,
                    returnIntent
            );

            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onClick(final View view) {

        switch (view.getId()) {
            case R.id.tvSignOut:
                ((HomeActivity) mContext).showConfirmationDialogForLogOut();
                break;

            case R.id.ivBack:
                getFragmentManager().popBackStack();
                break;

            default:
                break;
        }
    }

    */
/**
     * Method is  called when interaction begins with URL
     *//*

    private class OAuthWebViewClient extends WebViewClient {
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, final String url) {

            if (url.contains(CALLBACK_URL)) {
                final String queryString = url.replace(CALLBACK_URL + "/?", "");

                if (!url.contains(ERROR)) {
                    //onComplete(parameters);
                    webView.loadUrl(url);
                }

                return true;
            }
            return false;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, final WebResourceRequest request) {

            if (request.getUrl().toString().contains(CALLBACK_URL)) {

                final String queryString = request.getUrl().toString().replace(CALLBACK_URL + "/?", "");

                final Map<String, String> parameters = splitQuery(queryString);
                if (!request.getUrl().toString().contains(ERROR)) {
                    //onComplete(parameters);
                    if (request.getUrl().toString().contains(CALLBACK_URL)) {
                        hideLoading();

                        if (getTargetFragment() != null) {

                            final Intent returnIntent = new Intent();
                            getTargetFragment().onActivityResult(
                                    getTargetRequestCode(),
                                    RESULT_CONNECTED,
                                    returnIntent
                            );

                            getFragmentManager().popBackStack();
                        }
                    }

                } else {
                    onError(parameters);
                }
                return true;
            }
            return false;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onReceivedError(final WebView view, final int errorCode,
                                    final String description, final String failingUrl) {
            hideLoading();

            super.onReceivedError(view, errorCode, description, failingUrl);
            final Map<String, String> error = new LinkedHashMap<String, String>();
            error.put(ERROR, String.valueOf(errorCode));
            error.put(ERROR_DESC, description);
            onError(error);
        }


        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public void onReceivedError(final WebView view, final WebResourceRequest request, final WebResourceError err) {
            hideLoading();

            super.onReceivedError(view, request, err);
            final Map<String, String> error = new LinkedHashMap<String, String>();
            error.put(ERROR, String.valueOf(err.getErrorCode()));
            error.put(ERROR_DESC, err.getDescription().toString());
            onError(error);
        }

        @Override
        public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            if (url.contains(CALLBACK_URL)) {
                int codeStartindex = url.indexOf(getString(R.string.str_code_equal_to));
                int codeEndindex = url.indexOf(getString(R.string.str_and_state_equal_to));
                stripeCode = url.substring(codeStartindex + 5, codeEndindex);
                showLoading();
                makeServerHitToAddStripeId(stripeCode);
            }
        }

        @Override
        public void onPageFinished(final WebView view, final String url) {
            hideLoading();
            super.onPageFinished(view, url);
        }

        */
/**
         * backend Server Hit To Add Stripe Account Id against the logged in tutor
         *//*

        private void makeServerHitToAddStripeId(final String code) {
            showLoading();

            RestClient.getApiInterface().sendStripeAccountIdToServer(code, tutorId)
                    .enqueue(new ResponseResolver<ApiResponse>() {
                        @Override
                        public void success(final ApiResponse apiResponse) {
                            hideLoading();
                            if (getTargetFragment() != null) {

                                final Intent returnIntent = new Intent();
                                getTargetFragment().onActivityResult(
                                        getTargetRequestCode(),
                                        RESULT_CONNECTED,
                                        returnIntent
                                );

                                getFragmentManager().popBackStack();
                            }
                        }

                        @Override
                        public void failure(final APIError error) {
                            hideLoading();
                            showErrorMessage(error.getMessage());
                        }
                    });
        }

    }
}
*/
