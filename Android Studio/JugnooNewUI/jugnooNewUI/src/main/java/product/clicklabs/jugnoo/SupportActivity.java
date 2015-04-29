package product.clicklabs.jugnoo;

import product.clicklabs.jugnoo.datastructure.FeedbackMode;
import product.clicklabs.jugnoo.datastructure.HelpSection;
import product.clicklabs.jugnoo.utils.Utils;
import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SupportActivity extends Activity {

	RelativeLayout relative;
	
	TextView textViewTitle;
	ImageView imageViewBack;
	
	RelativeLayout relativeLayoutSendUsEmail, relativeLayoutCallUs, relativeLayoutFAQ, relativeLayoutFeedback;
	TextView textViewSendUsEmail, textViewCallUs, textViewFAQ, textViewFeedback;
	
	
	@Override
	protected void onResume() {
		super.onResume();
		HomeActivity.checkForAccessTokenChange(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_support);
		
		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(this, (ViewGroup) relative, 1134, 720, false);
		
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
		
		relativeLayoutSendUsEmail = (RelativeLayout) findViewById(R.id.relativeLayoutSendUsEmail);
		relativeLayoutCallUs = (RelativeLayout) findViewById(R.id.relativeLayoutCallUs);
		relativeLayoutFAQ = (RelativeLayout) findViewById(R.id.relativeLayoutFAQ);
		relativeLayoutFeedback = (RelativeLayout) findViewById(R.id.relativeLayoutFeedback);
		
		textViewSendUsEmail = (TextView) findViewById(R.id.textViewSendUsEmail); textViewSendUsEmail.setTypeface(Data.latoRegular(this));
		textViewCallUs = (TextView) findViewById(R.id.textViewCallUs); textViewCallUs.setTypeface(Data.latoRegular(this));
		textViewFAQ = (TextView) findViewById(R.id.textViewFAQ); textViewFAQ.setTypeface(Data.latoRegular(this));
		textViewFeedback = (TextView) findViewById(R.id.textViewFeedback); textViewFeedback.setTypeface(Data.latoRegular(this));

		
		relativeLayoutSendUsEmail.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent email = new Intent(Intent.ACTION_SEND);
				email.putExtra(Intent.EXTRA_EMAIL, new String[] { "support@jugnoo.in" });
				email.putExtra(Intent.EXTRA_SUBJECT, "Jugnoo Support");
				email.putExtra(Intent.EXTRA_TEXT, "");
				email.setType("message/rfc822");
				try {
					startActivity(Intent.createChooser(email, "Choose an Email client:"));
					Log.i("Finished sending email...", "");
				} catch (android.content.ActivityNotFoundException ex) {
					Toast.makeText(getApplicationContext(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		relativeLayoutCallUs.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
		        Utils.openCallIntent(SupportActivity.this, Data.SUPPORT_NUMBER);
			}
		});
		
		relativeLayoutFAQ.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				HelpParticularActivity.helpSection = HelpSection.FAQ;
				startActivity(new Intent(SupportActivity.this, HelpParticularActivity.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});

		relativeLayoutFeedback.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SupportActivity.this, FeedbackActivity.class);
				intent.putExtra(FeedbackMode.class.getName(), FeedbackMode.SUPPORT.getOrdinal());
				startActivity(intent);
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});
		
		
		imageViewBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
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
	}
	
	
	@Override
	protected void onDestroy() {
        ASSL.closeActivity(relative);
        System.gc();
		super.onDestroy();
	}
	
}
