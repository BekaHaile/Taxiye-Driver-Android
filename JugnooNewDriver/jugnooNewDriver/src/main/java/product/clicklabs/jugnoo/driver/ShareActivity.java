package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;

import product.clicklabs.jugnoo.driver.utils.BingTranslator;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Utils;
import rmn.androidscreenlibrary.ASSL;

public class ShareActivity extends Activity {


	LinearLayout relative;

	Button backBtn, buttonShare;
	TextView title;

	TextView textViewReferralCodeDisplay, textViewReferralCodeValue;
	TextView textViewShareReferral;


//	String str1 = "Share your referral code ",
//			str2 = " with your friends and they will get a FREE ride because of your referral and once they have used Jugnoo, " +
//					"you will earn a FREE ride (upto Rs. 100) as well.",
//	String	str3 = getResources().getString(R.string.your_referral_code);
	SpannableString sstr;



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
		setContentView(R.layout.activity_share);

		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(ShareActivity.this, relative, 1134, 720, false);


		backBtn = (Button) findViewById(R.id.backBtn);
		buttonShare = (Button) findViewById(R.id.buttonShare);

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

			textViewShareReferral.setText("");
			textViewShareReferral.append(hindi1);
			textViewShareReferral.append(sstr);
			textViewShareReferral.append(hindi2);


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
		ASSL.closeActivity(relative);
		System.gc();
	}

	public void confirmCustomerNumberPopup(final Activity activity) {

		try {
			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_share_enter_number);

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);


			TextView textViewTitle = (TextView) dialog.findViewById(R.id.textViewTitle);
			textViewTitle.setTypeface(Data.latoRegular(activity));
			final EditText customerNumber = (EditText) dialog.findViewById(R.id.customerNumber);
			customerNumber.setTypeface(Data.latoRegular(activity));


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
								final String phone = code;
								SmsManager smsManager = SmsManager.getDefault();
								smsManager.sendTextMessage("+91" + code, null, Data.userData.referralSMSToCustomer, null, null);

								new BingTranslator().startTranslation("आपका referral code ", new BingTranslator.BingCallback() {
									@Override
									public void onSuccess(String translatedStr) {
										DialogPopup.alertPopup(ShareActivity.this, "", " " + "आपका referral code "+" " + phone + " के साथ शेयर कर दिया गया है।");
									}

									@Override
									public void onFailure() {
										DialogPopup.alertPopup(ShareActivity.this, "", " "+"आपका referral code " + phone + " के साथ शेयर कर दिया गया है।");
									}
								});
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

			dialog.show();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

//	public String translate(String text) throws Exception {
//		// Set the Client ID / Client Secret once per JVM. It is set statically and applies to all services
//		try {
//			Translate.setClientId("aneesh_jugnoo");
//			Translate.setClientSecret("wzZGiM3+xESz/ssyCAZLakFuAb5o7sDbAr3N0dpRX3g=");
//
//			String translatedText = "";
//
//			// English AUTO_DETECT -> gERMAN Change this if u wanna other languages
////			translatedText = Translate.execute(text, Language.GERMAN);
//			translatedText = Translate.execute(text, Language.ENGLISH,
//					Language.ENGLISH);
//			return translatedText;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return "";
//		}
//	}

}
