package product.clicklabs.jugnoo.driver;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

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


    private class StripeWebViewClient extends WebViewClient {

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            handleUrlLoading(view, url);
            return false;
        }

        private void handleUrlLoading(WebView view, String url) {

            if(view==null || StripeConnectActivity.this.isFinishing()){
                return;
            }
           // UrlQuerySanitizer sanitizer = new UrlQuerySanitizer(url);

            if(url.contains(StripeUtils.STRIPE_SUCCESS_URL)){
       /*         if(sanitizer.getValue("code")!=null){

                    Log.i(TAG,"success "+url);
                    Log.i(TAG,"auth code= "+sanitizer.getValue("code"));
                    Log.i(TAG,"auth state= "+sanitizer.getValue("state"));
                    String message = sanitizer.getValue("message");
                    if(message==null){
                       String message = getString(R.string.stripe_success_message);
                    }

              }*/

               String message = getString(R.string.stripe_success_message);
                DialogPopup.alertPopupWithListener(StripeConnectActivity.this, "", message, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setResult(RESULT_OK, new Intent());
                        StripeConnectActivity.this.finish();
                    }
                });

            }else if(url.contains(StripeUtils.STRIPE_FAILURE_URL)){
             /*   Log.i(TAG,"failure "+url);
                String message = sanitizer.getValue("error");
                if(message==null){
                }*/
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
            if(url.contains(StripeUtils.STRIPE_CONNECT_URL)){
                DialogPopup.showLoadingDialog(StripeConnectActivity.this,StripeConnectActivity.this.getString(R.string.loading));
            }else{
                handleUrlLoading(view,url);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if(url.contains(StripeUtils.STRIPE_CONNECT_URL)){

                if(DialogPopup.isDialogShowing()){
                    DialogPopup.dismissLoadingDialog();

                }
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
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        webView.loadUrl(StripeUtils.stripeConnectBuilder().toString());
        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    onBackPressed();
            }
        });

    }






  /*  public void getParams(String url,String... params){
        UrlQuerySanitizer sanitizer = new UrlQuerySanitizer(url);
        String value = sanitizer.getValue("paramname");
    }*/

}
