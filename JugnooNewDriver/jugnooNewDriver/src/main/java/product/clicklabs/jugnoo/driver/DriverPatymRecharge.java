package product.clicklabs.jugnoo.driver;

import org.json.JSONObject;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.driver.datastructure.ProfileUpdateMode;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import rmn.androidscreenlibrary.ASSL;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.flurry.android.FlurryAgent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import java.util.HashMap;

public class DriverPatymRecharge extends Activity {

	LinearLayout relative;

	Button backBtn;
	TextView title;
	ScrollView scrollView;
	LinearLayout mainLinear, enterAmountLL;
	TextView textViewScroll;
	double amount;
	EditText editTextCashAmount, editTextPhone;
	Button btnConfirm, btnRupee500, btnRupee200, btnRupee100;

	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.init(this, Data.FLURRY_KEY);
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
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_paytm_recharge);

		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(DriverPatymRecharge.this, relative, 1134, 720, false);

		enterAmountLL = (LinearLayout) findViewById(R.id.enterAmountLL);
		scrollView = (ScrollView) findViewById(R.id.scrollView);
		mainLinear = (LinearLayout) findViewById(R.id.mainLinear);
		textViewScroll = (TextView) findViewById(R.id.textViewScroll);
		backBtn = (Button) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Data.latoRegular(this), Typeface.BOLD);


		editTextCashAmount = (EditText) findViewById(R.id.editTextCashAmount);
		editTextCashAmount.setTypeface(Data.latoRegular(this));
		editTextPhone = (EditText) findViewById(R.id.editTextPhone);
		editTextPhone.setTypeface(Data.latoRegular(this));

		btnConfirm = (Button) findViewById(R.id.btnConfirm);
		btnConfirm.setTypeface(Data.latoRegular(this));
		btnRupee500 = (Button) findViewById(R.id.btnRupee500);
		btnRupee500.setTypeface(Data.latoRegular(this));
		btnRupee200 = (Button) findViewById(R.id.btnRupee200);
		btnRupee200.setTypeface(Data.latoRegular(this));
		btnRupee100 = (Button) findViewById(R.id.btnRupee100);
		btnRupee100.setTypeface(Data.latoRegular(this));

		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});

		btnRupee500.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				editTextCashAmount.setText("500");

			}
		});

		btnRupee200.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				editTextCashAmount.setText("200");
				;

			}
		});

		btnRupee100.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				editTextCashAmount.setText("100");
				;

			}
		});

		editTextCashAmount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				scrollView.scrollTo(0, btnConfirm.getBottom());
			}
		});

		if (DriverScreenMode.D_REQUEST_ACCEPT == HomeActivity.driverScreenMode ||
				DriverScreenMode.D_RIDE_END == HomeActivity.driverScreenMode) {
			editTextPhone.setText(Data.assignedCustomerInfo.phoneNumber);
		} else {
			editTextPhone.setText(Prefs.with(DriverPatymRecharge.this).getString(SPLabels.CUSTOMER_PHONE_NUMBER, ""));
		}

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		btnConfirm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (validAmount() && validPhoneNo()) {
					confirmRechargePopup(DriverPatymRecharge.this);
				}

			}
		});

		editTextCashAmount.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				int result = actionId & EditorInfo.IME_MASK_ACTION;
				switch (result) {
					case EditorInfo.IME_ACTION_DONE:
						btnConfirm.performClick();
						break;

					case EditorInfo.IME_ACTION_NEXT:
						btnConfirm.performClick();
						break;

					default:
				}
				return true;
			}
		});


		editTextCashAmount.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				btnConfirm.requestFocus();
				return true;
			}
		});
		editTextPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				editTextCashAmount.requestFocus();
				return true;
			}
		});

		editTextCashAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					editTextCashAmount.setError(null);
					scrollView.scrollTo(0, btnConfirm.getBottom());
				}
			}
		});

		editTextPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					editTextPhone.setError(null);
				}
			}
		});

		relative.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editTextCashAmount.setError(null);
				editTextPhone.setError(null);

			}
		});

//		KeyboardLayoutListener keyboardLayoutListener = new KeyboardLayoutListener(mainLinear, textViewScroll,
//				new KeyboardLayoutListener.KeyBoardStateHandler() {
//					@Override
//					public void keyboardOpened() {
//						scrollView.scrollTo(0, btnConfirm.getBottom());
//					}
//
//					@Override
//					public void keyBoardClosed() {
//
//					}
//				});
//		keyboardLayoutListener.setResizeTextView(false);
//		mainLinear.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);

	}


	public void performBackPressed() {
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


	boolean validAmount(){
		try {
			String amountStr = editTextCashAmount.getText().toString().trim();
			if ("".equalsIgnoreCase(amountStr)) {
				editTextCashAmount.requestFocus();
				editTextCashAmount.setError("Please enter some amount");
			} else {
				amount = Double.parseDouble(editTextCashAmount.getText().toString().trim());
				if (AppStatus.getInstance(DriverPatymRecharge.this).isOnline(DriverPatymRecharge.this)) {
					if (amount > 1000) {
						editTextCashAmount.requestFocus();
						editTextCashAmount.setError("Please enter less amount");
					} else {
						return true;

					}
				} else {
					DialogPopup.alertPopup(DriverPatymRecharge.this, "", Data.CHECK_INTERNET_MSG);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();

			editTextCashAmount.requestFocus();
			editTextCashAmount.setError("Please enter valid amount");
			Utils.showSoftKeyboard(DriverPatymRecharge.this, editTextCashAmount);
		}
		return false;
	}

	boolean validPhoneNo(){
		if (editTextPhone.isEnabled()) {
			String phoneChanged = editTextPhone.getText().toString().trim();
			if ("".equalsIgnoreCase(phoneChanged)) {
				editTextPhone.requestFocus();
				editTextPhone.setError("Phone number can't be empty");
			} else {
				phoneChanged = Utils.retrievePhoneNumberTenChars(phoneChanged);
				if (Utils.validPhoneNumber(phoneChanged)) {
					return true;
				} else {
					editTextPhone.requestFocus();
					editTextPhone.setError("Phone number is invalid");
				}
			}
		} else {
			editTextPhone.requestFocus();
			editTextPhone.setEnabled(true);
			editTextPhone.setSelection(editTextPhone.getText().length());
			Utils.showSoftKeyboard(DriverPatymRecharge.this, editTextPhone);
		}

		return false;
	}



	void confirmRechargePopup(final Activity activity) {
		try {
			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_custom_two_buttons);

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);


			TextView textHead = (TextView) dialog.findViewById(R.id.textHead);
			textHead.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage);
			textMessage.setTypeface(Data.latoRegular(activity));

			textMessage.setText("You are recharging " + getResources().getString(R.string.rupee) +" "+ editTextCashAmount.getText() + " for customer with paytm account " + editTextPhone.getText());


			Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
			btnOk.setTypeface(Data.latoRegular(activity));
			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
			btnCancel.setTypeface(Data.latoRegular(activity));

			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					addCustomerCashAPI(DriverPatymRecharge.this, "" + amount);
					dialog.dismiss();

				}


			});


			btnCancel.setOnClickListener(new View.OnClickListener() {
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

//	Retrofit


	public void addCustomerCashAPI(final Activity activity, final String amount) {
		if (AppStatus.getInstance(activity).isOnline(activity)) {
			DialogPopup.showLoadingDialog(activity, "Loading...");

//			RequestParams params = new RequestParams();

			HashMap<String, String> params = new HashMap<String, String>();

			params.put("access_token", Data.userData.accessToken);
			params.put("engagement_id", Data.dEngagementId);
			params.put("customer_phone_number", String.valueOf(editTextPhone.getText()));
			params.put("money_added", amount);

			RestClient.getApiServices().addCustomerCashRetro(params, new Callback<RegisterScreenResponse>() {
				@Override
				public void success(RegisterScreenResponse registerScreenResponse, Response response) {
					Log.i("Server response", "response = " + response);
					DialogPopup.dismissLoadingDialog();
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj;
						jObj = new JSONObject(jsonString);
						int flag = jObj.getInt("flag");
						if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)) {
							if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
								String errorMessage = jObj.getString("error");
								DialogPopup.alertPopup(activity, "", errorMessage);
							} else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								if ("0".equalsIgnoreCase(amount)) {
									if (HomeActivity.appInterruptHandler != null) {
										HomeActivity.appInterruptHandler.onCustomerCashDone();
										performBackPressed();
									}
								} else {
									String message = jObj.getString("message");
									DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {

										@Override
										public void onClick(View v) {
											if (HomeActivity.appInterruptHandler != null) {
												HomeActivity.appInterruptHandler.onCustomerCashDone();
												performBackPressed();
											}
										}
									});
								}
							} else {
								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
						}

					} catch (Exception exception) {
						exception.printStackTrace();
						DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);

					}
				}

				@Override
				public void failure(RetrofitError error) {
					DialogPopup.dismissLoadingDialog();
					DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);

				}
			});


		} else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}
	}

}
