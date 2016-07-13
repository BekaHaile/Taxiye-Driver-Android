package product.clicklabs.jugnoo.driver;

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

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class DriverRateCard extends BaseActivity {

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

	String engagementId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_paytm_recharge);

		if(getIntent().hasExtra(Constants.KEY_ENGAGEMENT_ID)){
			engagementId = getIntent().getStringExtra(Constants.KEY_ENGAGEMENT_ID);
		}

		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(DriverRateCard.this, relative, 1134, 720, false);

		enterAmountLL = (LinearLayout) findViewById(R.id.enterAmountLL);
		scrollView = (ScrollView) findViewById(R.id.scrollView);
		mainLinear = (LinearLayout) findViewById(R.id.mainLinear);
		textViewScroll = (TextView) findViewById(R.id.textViewScroll);
		backBtn = (Button) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Data.latoRegular(this), Typeface.BOLD);


		editTextCashAmount = (EditText) findViewById(R.id.editTextCashAmount);
		editTextCashAmount.setTypeface(Data.latoRegular(this));
		editTextCashAmount.setHint(getStringText(R.string.enter_amount));
		editTextPhone = (EditText) findViewById(R.id.editTextPhone);
		editTextPhone.setTypeface(Data.latoRegular(this));
		editTextPhone.setHint(getStringText(R.string.enter_phone_number));

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




		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		btnConfirm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (validAmount() && validPhoneNo()) {
					confirmRechargePopup(DriverRateCard.this);
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


		editTextCashAmount.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				btnConfirm.requestFocus();
				return true;
			}
		});
		editTextPhone.setOnEditorActionListener(new OnEditorActionListener() {

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


	boolean validAmount() {
		try {
			String amountStr = editTextCashAmount.getText().toString().trim();
			if ("".equalsIgnoreCase(amountStr)) {
				editTextCashAmount.requestFocus();
				editTextCashAmount.setError(getResources().getString(R.string.enter_some_amount));
			} else {
				amount = Double.parseDouble(editTextCashAmount.getText().toString().trim());
				if (AppStatus.getInstance(DriverRateCard.this).isOnline(DriverRateCard.this)) {
					if (amount > 1000) {
						editTextCashAmount.requestFocus();
						editTextCashAmount.setError(getResources().getString(R.string.enter_less_amount));
					} else if (amount <= 0) {
						editTextCashAmount.requestFocus();
						editTextCashAmount.setError(getResources().getString(R.string.enter_some_amount));
					} else {
						return true;

					}
				} else {
					DialogPopup.alertPopup(DriverRateCard.this, "", Data.CHECK_INTERNET_MSG);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();

			editTextCashAmount.requestFocus();
			editTextCashAmount.setError(getResources().getString(R.string.enter_valid_amount));
			Utils.showSoftKeyboard(DriverRateCard.this, editTextCashAmount);
		}
		return false;
	}

	boolean validPhoneNo() {
		if (editTextPhone.isEnabled()) {
			String phoneChanged = editTextPhone.getText().toString().trim();
			if ("".equalsIgnoreCase(phoneChanged)) {
				editTextPhone.requestFocus();
				editTextPhone.setError(getResources().getString(R.string.phone_no_cnt_be_empty));
			} else {
				phoneChanged = Utils.retrievePhoneNumberTenChars(phoneChanged);
				if (Utils.validPhoneNumber(phoneChanged)) {
					return true;
				} else {
					editTextPhone.requestFocus();
					editTextPhone.setError(getResources().getString(R.string.valid_phone_number));
				}
			}
		} else {
			editTextPhone.requestFocus();
			editTextPhone.setEnabled(true);
			editTextPhone.setSelection(editTextPhone.getText().length());
			Utils.showSoftKeyboard(DriverRateCard.this, editTextPhone);
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

			textMessage.setText(getResources().getString(R.string.recharge_amount_for_customer, editTextCashAmount.getText() , editTextPhone.getText()));


			Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
			btnOk.setTypeface(Data.latoRegular(activity));
			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
			btnCancel.setTypeface(Data.latoRegular(activity));

			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					addCustomerCashAPI(DriverRateCard.this, "" + amount);
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
			DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));

//			RequestParams params = new RequestParams();

			HashMap<String, String> params = new HashMap<String, String>();

			params.put("access_token", Data.userData.accessToken);
			params.put("receiver_phone", String.valueOf(editTextPhone.getText()));
			params.put("amount", amount);

			RestClient.getApiServices().addCustomerCashRetro(params, new Callback<RegisterScreenResponse>() {
				@Override
				public void success(RegisterScreenResponse registerScreenResponse, Response response) {
					Log.i("Server response", "response = " + response);
					DialogPopup.dismissLoadingDialog();
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj = new JSONObject(jsonString);
						String message = JSONParser.getServerMessage(jObj);
						int flag = jObj.getInt("flag");
						if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
							DialogPopup.alertPopup(activity, "", message);
						} else {
							DialogPopup.alertPopup(activity, "", message);
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
