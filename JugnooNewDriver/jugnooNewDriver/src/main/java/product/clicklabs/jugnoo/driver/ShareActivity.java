package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;

import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.Log;
import rmn.androidscreenlibrary.ASSL;

public class ShareActivity extends Activity{
	
	
	LinearLayout relative;
	
	Button backBtn;
	TextView title;
	
	TextView textViewReferralCodeDisplay;
	ImageView shareWhatsappImg, shareSMSImg, shareEMailImg;
	TextView textViewShareReferral;
	
	
	String str1 = "Share your referral code ",
			str2 = " with your friends and they will get a FREE ride because of your referral and once they have used Jugnoo, " +
					"you will earn a FREE ride (upto Rs. 100) as well.",
					str3 = "Your Referral Code is ";


	
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
		title = (TextView) findViewById(R.id.title); title.setTypeface(Data.latoRegular(getApplicationContext()));
		
		textViewReferralCodeDisplay = (TextView) findViewById(R.id.textViewReferralCodeDisplay); textViewReferralCodeDisplay.setTypeface(Data.latoRegular(getApplicationContext()));
		
		shareWhatsappImg = (ImageView) findViewById(R.id.shareWhatsappImg);
		shareSMSImg = (ImageView) findViewById(R.id.shareSMSImg);
		shareEMailImg = (ImageView) findViewById(R.id.shareEMailImg);
		
		textViewShareReferral = (TextView) findViewById(R.id.textViewShareReferral); textViewShareReferral.setTypeface(Data.latoRegular(getApplicationContext()));

		try {
			SpannableString sstr = new SpannableString(Data.userData.referralCode);
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
			textViewReferralCodeDisplay.append(sstr);


			//कस्टमर को अपने  Referral code             से Jugnoo App डाउनलोड करवाएऔर पाए 3० रुपए और कस्टमर को दिलवाए Jugnoo कैश ।

//			String hindiMessage = "आमंत्रण बोनस! कस्टमर को अपने "+  + " Jugnoo App डाउनलोड करवांए और पांए "+ getResources().getString(R.string.rupee) + " 30 और कस्टमर को दिलवाए Jugnoo कैश ।";

			String hindi1 = "कस्टमर को अपने Referral code ";
			String hindi2 =  " से Jugnoo App डाउनलोड करवाएँ और पाएँ "+ getResources().getString(R.string.rupee) + " 30 और कस्टमर को दिलवाएँ Jugnoo कैश ।";

			textViewShareReferral.setText("");
			textViewShareReferral.append(hindi1);
			textViewShareReferral.append(sstr);
			textViewShareReferral.append(hindi2);


		} catch (Exception e) {
			e.printStackTrace();
		}


		backBtn.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
		});
		

		shareWhatsappImg.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				shareToWhatsapp(Data.userData.referralCode);
				FlurryEventLogger.sharedViaWhatsapp(Data.userData.accessToken);
			}
		});
		
		
		shareSMSImg.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendSMSIntent(Data.userData.referralCode);
				FlurryEventLogger.sharedViaSMS(Data.userData.accessToken);
			}
		});

		shareEMailImg.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openMailIntent(Data.userData.referralCode);
				FlurryEventLogger.sharedViaEmail(Data.userData.accessToken);
			}
		});
		
		
		
	}

	
	public void shareToWhatsapp(String referralCode) {
		PackageManager pm = getPackageManager();
		try {
			Intent waIntent = new Intent(Intent.ACTION_SEND);
			waIntent.setType("text/plain");
			String text = shareStr1 + referralCode + shareStr2;

			PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
			Log.d("info", "="+info);
			waIntent.setPackage("com.whatsapp");

			waIntent.putExtra(Intent.EXTRA_TEXT, text);
			startActivity(Intent.createChooser(waIntent, "Share with"));

		} catch (NameNotFoundException e) {
			Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	public void sendSMSIntent(String referralCode){
		Uri sms_uri = Uri.parse("smsto:"); 
        Intent sms_intent = new Intent(Intent.ACTION_SENDTO, sms_uri); 
        sms_intent.putExtra("sms_body", shareStr1 + referralCode + shareStr2); 
        startActivity(sms_intent); 
	}
	
	
	public void openMailIntent(String referralCode){
		Intent email = new Intent(Intent.ACTION_SEND);
		email.putExtra(Intent.EXTRA_EMAIL, new String[] { "" });
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

}
