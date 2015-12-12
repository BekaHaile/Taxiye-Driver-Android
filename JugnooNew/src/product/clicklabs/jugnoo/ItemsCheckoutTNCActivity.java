package product.clicklabs.jugnoo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.ItemInfo;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

public class ItemsCheckoutTNCActivity extends Activity{
	
	LinearLayout relative;
	
	Button backBtn;
	TextView title;
	
	WebView webview;
	
	RelativeLayout relativeLayoutAD;
	Button buttonAgree, buttonDisagree;
	
	
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, Data.FLURRY_KEY);
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_items_checkout_tnc);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(ItemsCheckoutTNCActivity.this, relative, 1134, 720, false);
		
		
		backBtn = (Button) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title); title.setTypeface(Data.latoRegular(getApplicationContext()));
		title.setText("TERMS & CONDITIONS");
		
		webview = (WebView) findViewById(R.id.webview);
		
		relativeLayoutAD = (RelativeLayout) findViewById(R.id.relativeLayoutAD);
		buttonAgree = (Button) findViewById(R.id.buttonAgree); buttonAgree.setTypeface(Data.latoRegular(getApplicationContext()));
		buttonDisagree = (Button) findViewById(R.id.buttonDisagree); buttonDisagree.setTypeface(Data.latoRegular(getApplicationContext()));
		
		
		backBtn.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		
		
		buttonAgree.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				checkoutAsync(ItemsCheckoutTNCActivity.this);
			}
		});
		
		buttonDisagree.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendBackToInfoList();
			}
		});
		
		
		if(getIntent() != null){
			if(getIntent().hasExtra("only_info")){
				relativeLayoutAD.setVisibility(View.GONE);
			}
			else{
				relativeLayoutAD.setVisibility(View.VISIBLE);
			}
		}
		else{
			relativeLayoutAD.setVisibility(View.GONE);
		}
		
		loadHTMLContent(ItemInfosListActivity.terms);
		
	}
	
	public void loadHTMLContent(String data){
		final String mimeType = "text/html";
        final String encoding = "UTF-8";
        webview.loadDataWithBaseURL("", data, mimeType, encoding, "");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		HomeActivity.checkForAccessTokenChange(this);
	}
	
	
	public void sendBackToInfoList(){
		Intent intent = new Intent(ItemsCheckoutTNCActivity.this, ItemInfosListActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	
	
	
	
	public void checkoutAsync(final Activity activity) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			try {
				JSONArray selectedItemsArray = new JSONArray();
				for(ItemInfo itemInfo : ItemsCheckoutActivity.selectedItemInfosList){
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("item_id", itemInfo.id);
					jsonObject.put("quantity", itemInfo.countSelected);
					selectedItemsArray.put(jsonObject);
				}
				Log.i("selectedItemsArray", "=" + selectedItemsArray);
				
				DialogPopup.showLoadingDialog(activity, "Loading...");
				
				RequestParams params = new RequestParams();
				params.put("access_token", Data.userData.accessToken);
				params.put("latitude", ""+ItemsCheckoutActivity.currentLatLng.latitude);
				params.put("longitude", ""+ItemsCheckoutActivity.currentLatLng.longitude);
				params.put("address", ""+ItemsCheckoutActivity.completeAddress);
				params.put("ordered_items", ""+selectedItemsArray.toString());

				Log.i("access_token", "=" + Data.userData.accessToken);
				Log.i("latitude", ""+ItemsCheckoutActivity.currentLatLng.latitude);
				Log.i("longitude", ""+ItemsCheckoutActivity.currentLatLng.longitude);
				
				AsyncHttpClient client = Data.getClient();
				client.post(ItemInfosListActivity.SERVER_URL + "/place_my_order", params,
						new CustomAsyncHttpResponseHandler() {
						private JSONObject jObj;
						
							@Override
							public void onFailure(Throwable arg3) {
								Log.e("request fail", arg3.toString());
								DialogPopup.dismissLoadingDialog();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
							}

							@Override
							public void onSuccess(String response) {
								Log.e("Server response", "response = " + response);

								try {
									jObj = new JSONObject(response);
									
									if(!jObj.isNull("error")){
										String errorMessage = jObj.getString("error");
										if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
											HomeActivity.logoutUser(activity);
										}
										else{
											if(jObj.has("flag")){
												int flag = jObj.getInt("flag");
												if(ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag){
													new DialogPopup().alertPopup(activity, "", errorMessage);
												}
											}
										}
									}
									else{
										if(jObj.has("flag")){
											int flag = jObj.getInt("flag");
											if(ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag){
												new DialogPopup().alertPopup(activity, "", jObj.getString("message"));
											}
											else if(ApiResponseFlags.ORDER_PLACED.getOrdinal() == flag){
												orderConfirmedDialog(activity, jObj.getString("message"));
											}
										}
									}
								}  catch (Exception exception) {
									exception.printStackTrace();
									new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								}

								DialogPopup.dismissLoadingDialog();
							}
						});
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		else {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}
	}
	
	
	public void orderConfirmedDialog(Activity activity, String message){
		new DialogPopup().alertPopupWithListener(activity, "", message, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendBackToInfoList();
			}
		});
	}
	
	
	
	
	public void performBackPressed(){
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	
	@Override
	public void onBackPressed() {
		performBackPressed();
		super.onBackPressed();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	
}
