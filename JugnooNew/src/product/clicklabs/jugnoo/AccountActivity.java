package product.clicklabs.jugnoo;

import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONObject;

import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.CouponStatus;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DateComparator;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.Log;
import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.flurry.android.FlurryAgent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

public class AccountActivity extends Activity{
	
	
	LinearLayout relative;
	
	Button backBtn;
	TextView title;
	
	TextView textViewAccountInfo;
	ProgressBar progressBarAccount;
	
	ListView listViewCoupons;
	CouponsListAdapter couponsListAdapter;
	
	TextView textViewWantMoreRides;
	Button buttonReferUs;
	
	TextView textViewEnterCouponCode;
	EditText editTextPromoCode;
	Button buttonApplyPromoCode;
	
	
	
	
	AsyncHttpClient fetchAccountInfoClient;
	
	ArrayList<CouponInfo> couponInfosList = new ArrayList<CouponInfo>();
	
	
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
	protected void onResume() {
		super.onResume();
		HomeActivity.checkForAccessTokenChange(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(AccountActivity.this, relative, 1134, 720, false);
		
		
		backBtn = (Button) findViewById(R.id.backBtn); 
		title = (TextView) findViewById(R.id.title); title.setTypeface(Data.latoRegular(getApplicationContext()));
		
		textViewAccountInfo = (TextView) findViewById(R.id.textViewAccountInfo); textViewAccountInfo.setTypeface(Data.latoRegular(getApplicationContext()));
		progressBarAccount = (ProgressBar) findViewById(R.id.progressBarAccount);
		
		couponInfosList.clear();
		
		listViewCoupons = (ListView) findViewById(R.id.listViewCoupons);
		couponsListAdapter = new CouponsListAdapter(AccountActivity.this);
		listViewCoupons.setAdapter(couponsListAdapter);
		
		textViewWantMoreRides = (TextView) findViewById(R.id.textViewWantMoreRides); textViewWantMoreRides.setTypeface(Data.latoRegular(getApplicationContext()));
		buttonReferUs = (Button) findViewById(R.id.buttonReferUs); buttonReferUs.setTypeface(Data.latoRegular(getApplicationContext()));

		textViewEnterCouponCode = (TextView) findViewById(R.id.textViewEnterCouponCode); textViewEnterCouponCode.setTypeface(Data.latoRegular(getApplicationContext()));
		editTextPromoCode = (EditText) findViewById(R.id.editTextPromoCode); editTextPromoCode.setTypeface(Data.latoRegular(getApplicationContext()));
		buttonApplyPromoCode = (Button) findViewById(R.id.buttonApplyPromoCode); buttonApplyPromoCode.setTypeface(Data.latoRegular(getApplicationContext()));
		buttonApplyPromoCode.setVisibility(View.GONE);
		
		textViewAccountInfo.setVisibility(View.GONE);
		progressBarAccount.setVisibility(View.GONE);
		
		
		backBtn.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
		});
		
		
		textViewAccountInfo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getAccountInfoAsync(AccountActivity.this);
			}
		});
		
		
		buttonReferUs.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(AccountActivity.this, ShareActivity.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
				FlurryEventLogger.shareScreenOpenedThroughCoupons(Data.userData.accessToken);
			}
		});
		
		buttonApplyPromoCode.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String promoCode = editTextPromoCode.getText().toString().trim();
				if(promoCode.length() > 0){
					applyPromoCodeAPI(AccountActivity.this, promoCode);
					FlurryEventLogger.promoCodeTried(Data.userData.accessToken, promoCode);
				}
				else{
					editTextPromoCode.requestFocus();
					editTextPromoCode.setError("Code can't be empty");
				}
			}
		});
		
		editTextPromoCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				editTextPromoCode.setError(null);
			}
		});
		
		editTextPromoCode.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				int result = actionId & EditorInfo.IME_MASK_ACTION;
				switch (result) {
					case EditorInfo.IME_ACTION_DONE:
						buttonApplyPromoCode.performClick();
						return false;

					case EditorInfo.IME_ACTION_NEXT:
						return false;

					default:
						return false;
				}
			}
		});
		
		editTextPromoCode.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(s.length() > 0){
					if(buttonApplyPromoCode.getVisibility() == View.GONE){
						buttonApplyPromoCode.setVisibility(View.VISIBLE);
					}
				}
				else{
					if(buttonApplyPromoCode.getVisibility() == View.VISIBLE){
						buttonApplyPromoCode.setVisibility(View.GONE);
					}
				}
			}
		});
		
		getAccountInfoAsync(AccountActivity.this);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
	}
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
		super.onBackPressed();
	}
	
	
	
	@Override
	public void onDestroy() {
		if(fetchAccountInfoClient != null){
			fetchAccountInfoClient.cancelAllRequests(true);
		}
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	
	
	public void updateListData(String message, boolean errorOccurred){
		if(errorOccurred){
			textViewAccountInfo.setText(message);
			textViewAccountInfo.setVisibility(View.VISIBLE);
			
			couponInfosList.clear();
			couponsListAdapter.notifyDataSetChanged();
		}
		else{
			if(couponInfosList.size() == 0){
				textViewAccountInfo.setText(message);
				textViewAccountInfo.setVisibility(View.VISIBLE);
			}
			else{
				textViewAccountInfo.setVisibility(View.GONE);
			}
			couponsListAdapter.notifyDataSetChanged();
		}
	}
	
	
	class ViewHolderCoupon {
		TextView textViewYouHave, textViewCouponTitle, textViewCouponSubTitle, textViewExpiryDate;
		RelativeLayout relative;
		int id;
	}

	class CouponsListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderCoupon holder;
		Context context;
		public CouponsListAdapter(Context context) {
			this.context = context;
			this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return couponInfosList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				holder = new ViewHolderCoupon();
				convertView = mInflater.inflate(R.layout.list_item_coupon, null);
				
				holder.textViewYouHave = (TextView) convertView.findViewById(R.id.textViewYouHave); holder.textViewYouHave.setTypeface(Data.museoSlab(context), Typeface.BOLD);
				holder.textViewCouponTitle = (TextView) convertView.findViewById(R.id.textViewCouponTitle); holder.textViewCouponTitle.setTypeface(Data.museoSlab(context), Typeface.BOLD);
				holder.textViewCouponSubTitle = (TextView) convertView.findViewById(R.id.textViewCouponSubTitle); holder.textViewCouponSubTitle.setTypeface(Data.museoSlab(context), Typeface.BOLD);
				holder.textViewExpiryDate = (TextView) convertView.findViewById(R.id.textViewExpiryDate); holder.textViewExpiryDate.setTypeface(Data.museoSlab(context));
				
				holder.relative = (RelativeLayout) convertView.findViewById(R.id.relative); 
				
				holder.relative.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderCoupon) convertView.getTag();
			}
			
			holder.id = position;
			
			CouponInfo couponInfo = couponInfosList.get(position);
			
			if(couponInfo.enabled){
				holder.textViewYouHave.setVisibility(View.GONE);
				if(couponInfo.count > 1){
					holder.textViewCouponTitle.setText(couponInfo.count + " " + couponInfo.title + "s");
				}
				else{
					holder.textViewCouponTitle.setText(couponInfo.count + " " + couponInfo.title);
				}
				holder.textViewCouponSubTitle.setVisibility(View.VISIBLE);
				holder.textViewExpiryDate.setVisibility(View.VISIBLE);
				holder.textViewCouponSubTitle.setText(couponInfo.subtitle);
				holder.textViewExpiryDate.setText("Expiring on "+DateOperations.getDate(DateOperations.utcToLocal(couponInfo.expiryDate)));
				holder.textViewCouponTitle.setAlpha(1.0f);
			}
			else{
				holder.textViewYouHave.setVisibility(View.VISIBLE);
				holder.textViewCouponTitle.setText("0 Free rides");
				holder.textViewCouponSubTitle.setVisibility(View.GONE);
				holder.textViewExpiryDate.setVisibility(View.GONE);
				holder.textViewYouHave.setAlpha(0.5f);
				holder.textViewCouponTitle.setAlpha(0.5f);
				
			}
			
			
			
			holder.relative.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					holder = (ViewHolderCoupon) v.getTag();
					CouponInfo couponInfo = couponInfosList.get(holder.id);
					if(couponInfo.enabled){
						alertPopup(AccountActivity.this, couponInfo.description);
						FlurryEventLogger.couponInfoOpened(Data.userData.accessToken, couponInfo.type);
					}
				}
			});
			
			return convertView;
		}

	}
	
	Dialog dialog;
	
	void alertPopup(Activity activity, String message) {
		try {
			try{
				if(dialog != null && dialog.isShowing()){
					dialog.dismiss();
				}
			}catch(Exception e){
				
			}
			
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.coupon_description_dialog);

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);
			
			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			
			
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.latoRegular(activity));

			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int)(800.0f*ASSL.Yscale()));
			
			textHead.setText("");
			textMessage.setText(message);
			
			textHead.setVisibility(View.GONE);
			
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.latoRegular(activity));
			
			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
				}
				
			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * ASync for get Account info from server
	 */
	public void getAccountInfoAsync(final Activity activity) {
		if(fetchAccountInfoClient == null){
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				progressBarAccount.setVisibility(View.VISIBLE);
				couponInfosList.clear();
				couponsListAdapter.notifyDataSetChanged();
				textViewAccountInfo.setVisibility(View.GONE);
				RequestParams params = new RequestParams();
				params.put("access_token", Data.userData.accessToken);
				fetchAccountInfoClient = Data.getClient();
				fetchAccountInfoClient.post(Data.SERVER_URL + "/get_coupons", params,
						new CustomAsyncHttpResponseHandler() {
						private JSONObject jObj;
	
							@Override
							public void onFailure(Throwable arg3) {
								Log.e("request fail", arg3.toString());
								progressBarAccount.setVisibility(View.GONE);
								updateListData("Some error occurred. Tap to retry", true);
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
											updateListData("Some error occurred. Tap to retry", true);
										}
									}
									else{
										
//										{
//										    "coupons": [
//										        {
//										            "title": "Free ride",
//										            "description": "upte 100/-",
//										            "discount": 100,
//										            "maximum": 100,
//										            "image": null,
//										            "type": 0,
//										            "redeemed_on": null,
//										            "status": 1,
//										            "expiry_date": "2014-11-07T18:29:59.000Z"
//										        }
//										    ]
//										}
										
										couponInfosList.clear();
										
										if(jObj.has("coupons")){
											JSONArray couponsData = jObj.getJSONArray("coupons");
											if(couponsData.length() > 0){
												for(int i=0; i<couponsData.length(); i++){
													JSONObject coData = couponsData.getJSONObject(i);
													
													CouponInfo couponInfo = new CouponInfo(coData.getInt("type"), 
															coData.getInt("status"), 
															coData.getString("title"), 
															coData.getString("subtitle"), 
															coData.getString("description"), 
															coData.getString("image"), 
															coData.getString("redeemed_on"), 
															coData.getString("expiry_date"), 
															coData.getDouble("discount"), 
															coData.getDouble("maximum"));
													
													if(CouponStatus.ACTIVE.getOrdinal() == couponInfo.status){
														if(couponInfosList.contains(couponInfo)){
															couponInfosList.get(couponInfosList.indexOf(couponInfo)).count++;
														}
														else{
															couponInfosList.add(couponInfo);
														}
													}
												}
												
												Collections.sort(couponInfosList, new DateComparator());
												
											}
										}
										else{
											CouponInfo couponInfo = new CouponInfo(1, 
													1, 
													"", 
													"", 
													"", 
													"", 
													"", 
													"", 
													0, 
													0);
											couponInfo.enabled = false;
											couponInfosList.add(couponInfo);
										}
										
										updateListData("Account info fetched", false);
									}
								}  catch (Exception exception) {
									exception.printStackTrace();
									updateListData("Some error occurred. Tap to retry", true);
								}
								progressBarAccount.setVisibility(View.GONE);
							}
							
							@Override
							public void onFinish() {
								fetchAccountInfoClient = null;
								super.onFinish();
							}
							
						});
			}
			else {
				updateListData("No Internet connection. Tap to retry", true);
			}
		}

	}

	/**
	 * API call for applying promo code to server
	 */
	public void applyPromoCodeAPI(final Activity activity, final String promoCode) {
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
				DialogPopup.showLoadingDialog(activity, "Loading...");
				
				RequestParams params = new RequestParams();
				
				params.put("access_token", Data.userData.accessToken);
				params.put("code", promoCode);
			
				AsyncHttpClient asyncHttpClient = Data.getClient();
				asyncHttpClient.post(Data.SERVER_URL + "/enter_code", params,
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
								Log.i("Server response", "response = " + response);
								try {
									jObj = new JSONObject(response);
									if (!jObj.isNull("error")) {
										String errorMessage = jObj.getString("error");
										if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
											HomeActivity.logoutUser(activity);
										} else {
											new DialogPopup().alertPopup(activity, "", errorMessage);
										}
									} else {
										int flag = jObj.getInt("flag");
										if(ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag){
											String message = jObj.getString("message");
											new DialogPopup().alertPopup(activity, "", message);
											getAccountInfoAsync(activity);
											FlurryEventLogger.promoCodeApplied(Data.userData.accessToken, promoCode, message);
										}
									}
								}  catch (Exception exception) {
									exception.printStackTrace();
									new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
									
								}
								DialogPopup.dismissLoadingDialog();
							}
							
							@Override
							public void onFinish() {
								super.onFinish();
							}
							
						});
			}
			else {
				new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}
	}
	
}
