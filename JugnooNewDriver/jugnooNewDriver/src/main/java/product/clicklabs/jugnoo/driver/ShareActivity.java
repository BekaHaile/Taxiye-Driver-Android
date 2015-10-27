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


	String str1 = "Share your referral code ",
			str2 = " with your friends and they will get a FREE ride because of your referral and once they have used Jugnoo, " +
					"you will earn a FREE ride (upto Rs. 100) as well.",
			str3 = "Your Referral Code is ";
	SpannableString sstr;


	String shareStr1 = "Hey, \nUse Jugnoo app to call an auto at your doorsteps. It is cheap, convenient and zero haggling. Use this referral code: ";
	String shareStr11 = "Use Jugnoo app to call an auto at your doorsteps. It is cheap, convenient and zero haggling. Use this referral code: ";
	String shareStr2 = " to get FREE ride upto Rs. 100.\nDownload it from here: http://smarturl.it/jugnoo";

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

			textViewShareReferral.setText("");
			textViewShareReferral.append(str1);
			textViewShareReferral.append(sstr);
			textViewShareReferral.append(str2);

			textViewReferralCodeDisplay.setText("");
			textViewReferralCodeDisplay.append(str3);
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


//		shareWhatsappImg.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				shareToWhatsapp(Data.userData.referralCode);
//				FlurryEventLogger.sharedViaWhatsapp(Data.userData.accessToken);
//			}
//		});
//
//
//		shareSMSImg.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				sendSMSIntent(Data.userData.referralCode);
//				FlurryEventLogger.sharedViaSMS(Data.userData.accessToken);
//			}
//		});
//
//		shareEMailImg.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				openMailIntent(Data.userData.referralCode);
//				FlurryEventLogger.sharedViaEmail(Data.userData.accessToken);
//			}
//		});


	}


	public void shareToWhatsapp(String referralCode) {
		PackageManager pm = getPackageManager();
		try {
			Intent waIntent = new Intent(Intent.ACTION_SEND);
			waIntent.setType("text/plain");
			String text = shareStr1 + referralCode + shareStr2;

			PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
			Log.d("info", "=" + info);
			waIntent.setPackage("com.whatsapp");

			waIntent.putExtra(Intent.EXTRA_TEXT, text);
			startActivity(Intent.createChooser(waIntent, "Share with"));

		} catch (NameNotFoundException e) {
			Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
		}
	}


	public void sendSMSIntent(String referralCode) {
		Uri sms_uri = Uri.parse("smsto:");
		Intent sms_intent = new Intent(Intent.ACTION_SENDTO, sms_uri);
		sms_intent.putExtra("sms_body", shareStr1 + referralCode + shareStr2);
		startActivity(sms_intent);
	}


	public void openMailIntent(String referralCode) {
		Intent email = new Intent(Intent.ACTION_SEND);
		email.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
		email.putExtra(Intent.EXTRA_SUBJECT, "Jugnoo Invite");
		email.putExtra(Intent.EXTRA_TEXT, shareStr1 + referralCode + shareStr2);
		email.setType("message/rfc822");
		startActivity(Intent.createChooser(email, "Choose an Email client:"));
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
							customerNumber.setError("Phone Number can't be empty.");
						} else {
							code = Utils.retrievePhoneNumberTenChars(code);
							if (!Utils.validPhoneNumber(code)) {
								customerNumber.requestFocus();
								customerNumber.setError("Please enter valid phone number");
							} else {
								SmsManager smsManager = SmsManager.getDefault();
								smsManager.sendTextMessage("+91" + code, null, Data.userData.referralSMSToCustomer, null, null);

								DialogPopup.alertPopup(ShareActivity.this, "", "आपका रेफ़रल कोड कस्टमर " + code + " के साथ शेयर कर दिया गया है।");
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

}
