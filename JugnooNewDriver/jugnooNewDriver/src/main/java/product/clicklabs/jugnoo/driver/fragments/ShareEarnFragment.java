package product.clicklabs.jugnoo.driver.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.ShareActivity;
import product.clicklabs.jugnoo.driver.ShareActivity1;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Utils;


public class ShareEarnFragment extends Fragment {

	private LinearLayout linearLayoutRoot;

	Button buttonShare;

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


	private View rootView;
    private ShareActivity activity;

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Data.FLURRY_KEY);
        FlurryAgent.onStartSession(activity, Data.FLURRY_KEY);
        FlurryAgent.onEvent(ShareEarnFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
        FlurryAgent.onEndSession(activity);
    }
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_share_earn, container, false);

		activity = (ShareActivity) getActivity();

		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		new ASSL(activity, linearLayoutRoot, 1134, 720, false);


		buttonShare = (Button) rootView.findViewById(R.id.buttonShare);

		textViewReferralCodeDisplay = (TextView)rootView.findViewById(R.id.textViewReferralCodeDisplay);
		textViewReferralCodeDisplay.setTypeface(Data.latoRegular(activity));
		textViewReferralCodeValue = (TextView) rootView.findViewById(R.id.textViewReferralCodeValue);
		textViewReferralCodeValue.setTypeface(Data.latoRegular(activity));
		textViewReferralCodeValue.setTextColor(getResources().getColor(R.color.musturd_jugnoo));
		textViewShareReferral = (TextView) rootView.findViewById(R.id.textViewShareReferral);
		textViewShareReferral.setTypeface(Data.latoRegular(activity));

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
			textViewReferralCodeValue.setTypeface(Data.latoHeavy(activity));

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
			activity.finish();
			activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
		}



		buttonShare.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				confirmCustomerNumberPopup(activity);
			}
		});




		return rootView;
	}


    @Override
	public void onDestroy() {
		super.onDestroy();
		try {
			ASSL.closeActivity(linearLayoutRoot);
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
								sendSMS("+91" + code, Data.userData.referralSMSToCustomer);
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
	private void sendSMS(final String phoneNumber, String message)
	{
		String SENT = "SMS_SENT";
		String DELIVERED = "SMS_DELIVERED";

		PendingIntent sentPI = PendingIntent.getBroadcast(activity, 0,
				new Intent(SENT), 0);

		PendingIntent deliveredPI = PendingIntent.getBroadcast(activity, 0,
				new Intent(DELIVERED), 0);

		//---when the SMS has been sent---
		activity.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
					case Activity.RESULT_OK:
						DialogPopup.alertPopup(activity, "", "आपका रेफ़रल कोड कस्टमर " + phoneNumber + " के साथ शेयर कर दिया गया है।");
						break;
					case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
						DialogPopup.alertPopup(activity, "", "आपका रेफ़रल कोड कस्टमर " + phoneNumber + " के साथ शेयर नहीं हो पाया है।");
						break;
					case SmsManager.RESULT_ERROR_NO_SERVICE:
						DialogPopup.alertPopup(activity, "", "आपका रेफ़रल कोड कस्टमर " + phoneNumber + " के साथ शेयर नहीं हो पाया है।");
						break;
					case SmsManager.RESULT_ERROR_NULL_PDU:
						DialogPopup.alertPopup(activity, "", "आपका रेफ़रल कोड कस्टमर " + phoneNumber + " के साथ शेयर नहीं हो पाया है।");
						break;
					case SmsManager.RESULT_ERROR_RADIO_OFF:
						DialogPopup.alertPopup(activity, "", "आपका रेफ़रल कोड कस्टमर " + phoneNumber + " के साथ शेयर नहीं हो पाया है।");
						break;
				}
			}
		}, new IntentFilter(SENT));

		//---when the SMS has been delivered---
		activity.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
					case Activity.RESULT_OK:
						break;
//					case Activity.RESULT_CANCELED:
//						DialogPopup.alertPopup(ShareActivity1.this, "", "आपका रेफ़रल कोड कस्टमर " + phoneNumber + " के साथ शेयर कर दिया गया है।");
//						break;
				}
			}
		}, new IntentFilter(DELIVERED));

		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
	}


}
