package product.clicklabs.jugnoo.driver;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Log;

/**
 * Created by Parminder Saini on 07/05/18.
 */
public class StripeConnectActivity extends BaseFragmentActivity  {


    private static final String TAG = StripeConnectActivity.class.getName();
    private StripeWebViewClient stripeWebViewClient;
    private WebView webView;

    public static final String ARGS_URL_TO_OPEN = "url_to_open";
    private String urlToLoad;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent().getExtras()==null || !getIntent().hasExtra(ARGS_URL_TO_OPEN)){
            finish();
        }

        urlToLoad = getIntent().getStringExtra(ARGS_URL_TO_OPEN);
        removeAllCookies(StripeConnectActivity.this);
        setContentView(R.layout.activity_stipe_connect);
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setPadding(0,0,0,0);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setBuiltInZoomControls(true);
        stripeWebViewClient = new StripeWebViewClient();
        webView.setWebViewClient(stripeWebViewClient);
        webView.loadUrl(urlToLoad);
        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private class StripeWebViewClient extends WebViewClient {

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            handleUrlLoading(view, url);
            return false;
        }

        private void handleUrlLoading(WebView view, String url) {

            if(view==null || StripeConnectActivity.this.isFinishing()){
                return;
            }


            if(url.contains(StripeUtils.STRIPE_SUCCESS_URL)){

                //   UrlQuerySanitizer sanitizer = new UrlQuerySanitizer(url);
           String message = getString(R.string.stripe_success_message);
                DialogPopup.alertPopupWithListener(StripeConnectActivity.this, "", message, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setResult(RESULT_OK, new Intent());
                        StripeConnectActivity.this.finish();
                    }
                });

            }else if(url.contains(StripeUtils.STRIPE_FAILURE_URL)){

                String message = getString(R.string.stripe_error_message);

                DialogPopup.alertPopupWithListener(StripeConnectActivity.this, "", message, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setResult(RESULT_CANCELED, new Intent());
                        StripeConnectActivity.this.finish();
                    }
                });


            }


        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if(url.contains(urlToLoad)){
                DialogPopup.showLoadingDialog(StripeConnectActivity.this,StripeConnectActivity.this.getString(R.string.loading));
            }else{
                handleUrlLoading(view,url);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if(DialogPopup.isDialogShowing()){
                DialogPopup.dismissLoadingDialog();

            }
        }


        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            if(DialogPopup.isDialogShowing()){
                DialogPopup.dismissLoadingDialog();

            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            if(DialogPopup.isDialogShowing()){
                DialogPopup.dismissLoadingDialog();

            }
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            if(DialogPopup.isDialogShowing()){
                DialogPopup.dismissLoadingDialog();

            }
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            handleUrlLoading(view, request.getUrl().toString());
            return false;
        }

        @Override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
            Log.i(TAG,host);
        }
    }


    @Override
    public void onBackPressed() {
        if(webView!=null && webView.canGoBack()) {
            webView.goBack();
        } else {
            if(webView!=null){
                webView.destroy();
            }
            super.onBackPressed();
        }
    }



    private static void removeAllCookies(final Context context) {
        //On Lollipop and beyond, the CookieManager singleton works fine by itself.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(context);
        }
        final CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }




  /*  public void getParams(String url,String... params){
        UrlQuerySanitizer sanitizer = new UrlQuerySanitizer(url);
        String value = sanitizer.getValue("paramname");
    }*/

}
