package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;


import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.UserData;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class ShareActivity extends Activity {


	LinearLayout relative;

	Button backBtn, buttonShare;
	TextView title;

	TextView textViewReferralCodeDisplay, textViewReferralCodeValue;
	TextView textViewShareReferral;


//	 str1 = "Share your referral code ",
//			str2 = " with your friends and they will get a FREE ride because of your referral and once they have used Jugnoo, " +
//					"you will earn a FREE ride (upto Rs. 100) as well.",
	String str3 = "Your Referral Code is ";
	SpannableString sstr;


//	String shareStr1 = "Hey, \nUse Jugnoo app to call an auto at your doorsteps. It is cheap, convenient and zero haggling. Use this referral code: ";
//	String shareStr11 = "Use Jugnoo app to call an auto at your doorsteps. It is cheap, convenient and zero haggling. Use this referral code: ";
//	String shareStr2 = " to get FREE ride upto Rs. 100.\nDownload it from here: http://smarturl.it/jugnoo";

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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			String type = getIntent().getStringExtra("type");
			if(type.equalsIgnoreCase("cancel")){
				Intent intent = new Intent(ShareActivity.this, HomeActivity.class);
				intent.putExtras(getIntent().getExtras());
//				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
				finish();
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


		setContentView(R.layout.activity_share);

		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(ShareActivity.this, relative, 1134, 720, false);


		backBtn = (Button) findViewById(R.id.backBtn);
		buttonShare = (Button) findViewById(R.id.buttonShare);
		buttonShare.setText(Data.userData.referralButtonText);

		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Data.latoRegular(getApplicationContext()));

		textViewReferralCodeDisplay = (TextView) findViewById(R.id.textViewReferralCodeDisplay);
		textViewReferralCodeDisplay.setTypeface(Data.latoRegular(getApplicationContext()));
		textViewReferralCodeValue = (TextView) findViewById(R.id.textViewReferralCodeValue);
		textViewReferralCodeValue.setTypeface(Data.latoRegular(getApplicationContext()));
		textViewReferralCodeValue.setTextColor(getResources().getColor(R.color.musturd_jugnoo));
		textViewShareReferral = (TextView) findViewById(R.id.textViewShareReferral);
		textViewShareReferral.setTypeface(Data.latoRegular(getApplicationContext()));

		try {
			sstr = new SpannableString(Data.userData.referralCode);
			final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
			final ForegroundColorSpan clrs = new ForegroundColorSpan(Color.parseColor("#FAA31C"));
			sstr.setSpan(bss, 0, sstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			sstr.setSpan(clrs, 0, sstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

//			textViewShareReferral.setText("");
//			textViewShareReferral.append(str1);
//			textViewShareReferral.append(sstr);
//			textViewShareReferral.append(str2);

			textViewReferralCodeDisplay.setText("");
			textViewReferralCodeDisplay.append(getResources().getString(R.string.your_referral_code));
			textViewReferralCodeValue.setText(sstr);
			textViewReferralCodeValue.setTypeface(Data.latoHeavy(getApplicationContext()));

			//कस्टमर को अपने  Referral code             से Jugnoo App डाउनलोड करवाएऔर पाए 3० रुपए और कस्टमर को दिलवाए Jugnoo कैश ।

//			String hindiMessage = "आमंत्रण बोनस! कस्टमर को अपने "+  + " Jugnoo App डाउनलोड करवांए और पांए "+ getResources().getString(R.string.rupee) + " 30 और कस्टमर को दिलवाए Jugnoo कैश ।";

			String hindi1 = "कस्टमर को अपने Referral code ";
			String hindi2 = " से Jugnoo App डाउनलोड करवाएँ और पाएँ " + getResources().getString(R.string.rupee) + " " + Data.userData.customerReferralBonus + " और कस्टमर को दिलवाएँ Jugnoo कैश ।";

			textViewShareReferral.setText(Data.userData.referralMessage);
//			textViewShareReferral.append(hindi1);
//			textViewShareReferral.append(sstr);
//			textViewShareReferral.append(hindi2);


		} catch (Exception e) {
			e.printStackTrace();
			finish();
			overridePendingTransition(R.anim.left_in, R.anim.left_out);
		}


		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
		});

		buttonShare.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				confirmCustomerNumberPopup(ShareActivity.this);
			}
		});



	}





	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
		super.onBackPressed();
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			ASSL.closeActivity(relative);
		} catch (Exception e) {
		}
		System.gc();
	}

	public void confirmCustomerNumberPopup(final Activity activity) {

		try {
			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_share_enter_number);

			RelativeLayout frameLayout = (RelativeLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);


			TextView textViewDialogTitle = (TextView) dialog.findViewById(R.id.textViewDialogTitle);
			textViewDialogTitle.setTypeface(Data.latoRegular(activity));
			final EditText customerNumber = (EditText) dialog.findViewById(R.id.customerNumber);
			customerNumber.setTypeface(Data.latoRegular(activity));
			customerNumber.setHint(Data.userData.referralDialogHintText);
			textViewDialogTitle.setText(Data.userData.referralDialogText);

			final Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
			btnOk.setTypeface(Data.latoRegular(activity));
			final Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
			btnCancel.setTypeface(Data.latoRegular(activity));

			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					try {
						String code = customerNumber.getText().toString().trim();
						if ("".equalsIgnoreCase(code)) {
							customerNumber.requestFocus();
							customerNumber.setError(getResources().getString(R.string.Phone_number_not_empty));
						} else {
							code = Utils.retrievePhoneNumberTenChars(code);
							if (!Utils.validPhoneNumber(code)) {
								customerNumber.requestFocus();
								customerNumber.setError(getResources().getString(R.string.enter_valid_phone_number));
							} else {

								sendReferralMessage(ShareActivity.this, "+91"+code);
								dialog.dismiss();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();

					}

				}

			});
			btnCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					dialog.dismiss();
				}
			});
			customerNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
					int result = actionId & EditorInfo.IME_MASK_ACTION;
					switch (result) {
						case EditorInfo.IME_ACTION_DONE:
							btnOk.performClick();
							break;

						case EditorInfo.IME_ACTION_NEXT:
							break;

						default:
					}
					return true;
				}
			});

			dialog.show();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					Utils.showSoftKeyboard(activity, customerNumber);
				}
			}, 200);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	//---sends an SMS message to another device---
//	private void sendSMS(final String phoneNumber, String message)
//	{
//		String SENT = "SMS_SENT";
//		String DELIVERED = "SMS_DELIVERED";
//
//		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
//				new Intent(SENT), 0);
//
//		PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
//				new Intent(DELIVERED), 0);
//
//		//---when the SMS has been sent---
//		registerReceiver(new BroadcastReceiver(){
//			@Override
//			public void onReceive(Context arg0, Intent arg1) {
//				switch (getResultCode())
//				{
//					case Activity.RESULT_OK:
//						DialogPopup.alertPopup(ShareActivity.this, "", "आपका रेफ़रल कोड कस्टमर " + phoneNumber + " के साथ शेयर कर दिया गया है।");
//						break;
//					case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
//						DialogPopup.alertPopup(ShareActivity.this, "", "आपका रेफ़रल कोड कस्टमर " + phoneNumber + " के साथ शेयर नहीं हो पाया है।");
//						break;
//					case SmsManager.RESULT_ERROR_NO_SERVICE:
//						DialogPopup.alertPopup(ShareActivity.this, "", "आपका रेफ़रल कोड कस्टमर " + phoneNumber + " के साथ शेयर नहीं हो पाया है।");
//						break;
//					case SmsManager.RESULT_ERROR_NULL_PDU:
//						DialogPopup.alertPopup(ShareActivity.this, "", "आपका रेफ़रल कोड कस्टमर " + phoneNumber + " के साथ शेयर नहीं हो पाया है।");
//						break;
//					case SmsManager.RESULT_ERROR_RADIO_OFF:
//						DialogPopup.alertPopup(ShareActivity.this, "", "आपका रेफ़रल कोड कस्टमर " + phoneNumber + " के साथ शेयर नहीं हो पाया है।");
//						break;
//				}
//			}
//		}, new IntentFilter(SENT));
//
//		//---when the SMS has been delivered---
//		registerReceiver(new BroadcastReceiver(){
//			@Override
//			public void onReceive(Context arg0, Intent arg1) {
//				switch (getResultCode())
//				{
//					case Activity.RESULT_OK:
//						break;
////					case Activity.RESULT_CANCELED:
////						DialogPopup.alertPopup(ShareActivity.this, "", "आपका रेफ़रल कोड कस्टमर " + phoneNumber + " के साथ शेयर कर दिया गया है।");
////						break;
//				}
//			}
//		}, new IntentFilter(DELIVERED));
//
//		SmsManager sms = SmsManager.getDefault();
//		sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
//	}

	public void sendReferralMessage(final Activity activity, String phone_no) {
		try {
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

				DialogPopup.showLoadingDialog(activity, "Loading...");

				HashMap<String, String> params = new HashMap<String, String>();

				params.put("access_token", Data.userData.accessToken);
				params.put("phone_no", phone_no);
				Log.i("params", "=" + params);

				RestClient.getApiServices().sendReferralMessage(params, new Callback<RegisterScreenResponse>() {
					@Override
					public void success(RegisterScreenResponse registerScreenResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						try {
							JSONObject jObj = new JSONObject(responseStr);
							int flag = jObj.getInt("flag");
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								DialogPopup.alertPopup(ShareActivity.this, "", jObj.getString("message"));
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
					public void failure(RetrofitError error) {
						Log.e("request fail", error.toString());
						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
					}
				});
			} else {
				DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
