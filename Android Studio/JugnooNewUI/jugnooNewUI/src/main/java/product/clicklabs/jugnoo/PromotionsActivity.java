package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.flurry.android.FlurryAgent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DateComparator;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import rmn.androidscreenlibrary.ASSL;

public class PromotionsActivity extends Activity{
	
	
	LinearLayout relative;
	
	ImageView imageViewBack;
	TextView textViewTitle;

	EditText editTextPromoCode;
	Button buttonApplyPromo;
	TextView textViewCouponsAvailable;
	
	ListView listViewCoupons;
	CouponsListAdapter couponsListAdapter;

	TextView textViewInfo;
	ProgressBar progressBar;
	
	
	
	
	AsyncHttpClient fetchAccountInfoClient;
	
	ArrayList<CouponInfo> couponInfosList = new ArrayList<CouponInfo>();
	
	
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.init(this, Config.getFlurryKey());
		FlurryAgent.onStartSession(this, Config.getFlurryKey());
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
		setContentView(R.layout.activity_promotions);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(PromotionsActivity.this, relative, 1134, 720, false);
		
		
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack); 
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);

		editTextPromoCode = (EditText) findViewById(R.id.editTextPromoCode); editTextPromoCode.setTypeface(Fonts.latoRegular(this));
		buttonApplyPromo = (Button) findViewById(R.id.buttonApplyPromo); buttonApplyPromo.setTypeface(Fonts.latoRegular(this));
		textViewCouponsAvailable = (TextView) findViewById(R.id.textViewCouponsAvailable); textViewCouponsAvailable.setTypeface(Fonts.latoRegular(this));
		textViewCouponsAvailable.setVisibility(View.GONE);
		
		couponInfosList.clear();
		
		listViewCoupons = (ListView) findViewById(R.id.listViewCoupons);
		couponsListAdapter = new CouponsListAdapter(PromotionsActivity.this);
		listViewCoupons.setAdapter(couponsListAdapter);
		
		textViewInfo = (TextView) findViewById(R.id.textViewInfo); textViewInfo.setTypeface(Fonts.latoRegular(this));
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		
		
		textViewInfo.setVisibility(View.GONE);
		progressBar.setVisibility(View.GONE);
		
		
		imageViewBack.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		
		
		textViewInfo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getAccountInfoAsync(PromotionsActivity.this);
			}
		});
		
		
		buttonApplyPromo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String promoCode = editTextPromoCode.getText().toString().trim();
				if(promoCode.length() > 0){
					applyPromoCodeAPI(PromotionsActivity.this, promoCode);
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
						buttonApplyPromo.performClick();
						return false;

					case EditorInfo.IME_ACTION_NEXT:
						return false;

					default:
						return false;
				}
			}
		});
		
		getAccountInfoAsync(PromotionsActivity.this);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
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
		if(fetchAccountInfoClient != null){
			fetchAccountInfoClient.cancelAllRequests(true);
		}
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	
	
	public void updateListData(String message, boolean errorOccurred){
		if(errorOccurred){
			textViewInfo.setText(message);
			textViewInfo.setVisibility(View.VISIBLE);
			
			couponInfosList.clear();
			couponsListAdapter.notifyDataSetChanged();
		}
		else{
			if(couponInfosList.size() == 0){
				textViewInfo.setText(message);
				textViewInfo.setVisibility(View.VISIBLE);
				textViewCouponsAvailable.setVisibility(View.GONE);
			}
			else{
				textViewInfo.setVisibility(View.GONE);
				textViewCouponsAvailable.setVisibility(View.VISIBLE);
			}
			couponsListAdapter.notifyDataSetChanged();
		}
	}
	
	
	class ViewHolderCoupon {
		TextView textViewCouponTitle, textViewExpiryDate;
		LinearLayout relative;
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
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				holder = new ViewHolderCoupon();
				convertView = mInflater.inflate(R.layout.list_item_coupon, null);
				
				holder.textViewCouponTitle = (TextView) convertView.findViewById(R.id.textViewCouponTitle); holder.textViewCouponTitle.setTypeface(Fonts.latoRegular(context));
				holder.textViewExpiryDate = (TextView) convertView.findViewById(R.id.textViewExpiryDate); holder.textViewExpiryDate.setTypeface(Fonts.latoLight(context), Typeface.BOLD);
				
				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative); 
				
				holder.relative.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderCoupon) convertView.getTag();
			}
			
			holder.id = position;
			
			CouponInfo couponInfo = couponInfosList.get(position);

			holder.textViewCouponTitle.setText(couponInfo.title);
			holder.textViewExpiryDate.setText("Expiring on "+DateOperations.getDate(DateOperations.utcToLocal(couponInfo.expiryDate)));
			
			holder.relative.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					holder = (ViewHolderCoupon) v.getTag();
					CouponInfo couponInfo = couponInfosList.get(holder.id);
					DialogPopup.alertPopupLeftOriented(PromotionsActivity.this, "", couponInfo.description);
					FlurryEventLogger.couponInfoOpened(Data.userData.accessToken, couponInfo.type);
				}
			});
			
			return convertView;
		}

	}
	
	
	/**
	 * ASync for get Account info from server
	 */
	public void getAccountInfoAsync(final Activity activity) {
        if(!HomeActivity.checkIfUserDataNull(activity)) {
            if (fetchAccountInfoClient == null) {
                if (AppStatus.getInstance(activity).isOnline(activity)) {
                    progressBar.setVisibility(View.VISIBLE);
                    couponInfosList.clear();
                    couponsListAdapter.notifyDataSetChanged();
                    textViewInfo.setVisibility(View.GONE);
                    RequestParams params = new RequestParams();
                    params.put("access_token", Data.userData.accessToken);
                    fetchAccountInfoClient = Data.getClient();
                    fetchAccountInfoClient.post(Config.getServerUrl() + "/get_coupons", params,
                        new CustomAsyncHttpResponseHandler() {
                            private JSONObject jObj;

                            @Override
                            public void onFailure(Throwable arg3) {
                                Log.e("request fail", arg3.toString());
                                progressBar.setVisibility(View.GONE);
                                updateListData("Some error occurred. Tap to retry", true);
                            }

                            @Override
                            public void onSuccess(String response) {
                                Log.e("Server response", "response = " + response);
                                try {
                                    jObj = new JSONObject(response);

                                    if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                        int flag = jObj.getInt("flag");
                                        if (ApiResponseFlags.COUPONS.getOrdinal() == flag) {

//											{
//											    "coupons": [
//											        {
//											            "title": "Free ride",
//											            "description": "upte 100/-",
//											            "discount": 100,
//											            "maximum": 100,
//											            "image": null,
//											            "type": 0,
//											            "redeemed_on": null,
//											            "status": 1,
//											            "expiry_date": "2014-11-07T18:29:59.000Z"
//											        }
//											    ]
//											}

                                            couponInfosList.clear();

                                            if (jObj.has("coupons")) {
                                                JSONArray couponsData = jObj.getJSONArray("coupons");
                                                if (couponsData.length() > 0) {
                                                    for (int i = 0; i < couponsData.length(); i++) {
                                                        JSONObject coData = couponsData.getJSONObject(i);

                                                        CouponInfo couponInfo = new CouponInfo(1,
                                                            coData.getInt("type"),
                                                            coData.getInt("status"),
                                                            coData.getString("title"),
                                                            coData.getString("subtitle"),
                                                            coData.getString("description"),
                                                            coData.getString("image"),
                                                            coData.getString("redeemed_on"),
                                                            coData.getString("expiry_date"),
                                                            coData.getDouble("discount"),
                                                            coData.getDouble("maximum"));

                                                        couponInfosList.add(couponInfo);
                                                    }
                                                    Collections.sort(couponInfosList, new DateComparator());
                                                }
                                            }
                                            updateListData("No Coupons available", false);

                                            if (Data.userData != null) {
                                                Data.userData.numCouponsAvaliable = couponInfosList.size();
                                            }
                                        } else {
                                            updateListData("Some error occurred. Tap to retry", true);
                                        }
                                    } else {
                                        updateListData("Some error occurred. Tap to retry", true);
                                    }

                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                    updateListData("Some error occurred. Tap to retry", true);
                                }
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onFinish() {
                                fetchAccountInfoClient = null;
                                super.onFinish();
                            }

                        });
                } else {
                    updateListData("No Internet connection. Tap to retry", true);
                }
            }
        }
	}

	/**
	 * API call for applying promo code to server
	 */
	public void applyPromoCodeAPI(final Activity activity, final String promoCode) {
        if(!HomeActivity.checkIfUserDataNull(activity)) {
            if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                DialogPopup.showLoadingDialog(activity, "Loading...");

                RequestParams params = new RequestParams();

                params.put("access_token", Data.userData.accessToken);
                params.put("code", promoCode);

                AsyncHttpClient asyncHttpClient = Data.getClient();
                asyncHttpClient.post(Config.getServerUrl() + "/enter_code", params,
                    new CustomAsyncHttpResponseHandler() {
                        private JSONObject jObj;

                        @Override
                        public void onFailure(Throwable arg3) {
                            Log.e("request fail", arg3.toString());
                            DialogPopup.dismissLoadingDialog();
                            DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
                        }


                        @Override
                        public void onSuccess(String response) {
                            Log.i("Server response", "response = " + response);
                            try {
                                jObj = new JSONObject(response);
                                int flag = jObj.getInt("flag");
                                if (ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal() == flag) {
                                    HomeActivity.logoutUser(activity);
                                } else if (ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag) {
                                    String errorMessage = jObj.getString("error");
                                    DialogPopup.alertPopup(activity, "", errorMessage);
                                } else if (ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag) {
                                    String message = jObj.getString("message");
                                    DialogPopup.dialogBanner(activity, message);
                                    getAccountInfoAsync(activity);
                                    FlurryEventLogger.promoCodeApplied(Data.userData.accessToken, promoCode, message);
                                } else {
                                    DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                                }
                            } catch (Exception exception) {
                                exception.printStackTrace();
                                DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);

                            }
                            DialogPopup.dismissLoadingDialog();
                        }

                        @Override
                        public void onFinish() {
                            super.onFinish();
                        }

                    });
            } else {
                DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
            }
        }
	}
	
}
