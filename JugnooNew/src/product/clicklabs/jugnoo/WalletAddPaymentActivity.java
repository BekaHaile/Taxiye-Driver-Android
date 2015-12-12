package product.clicklabs.jugnoo;

import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

public class WalletAddPaymentActivity extends Activity{
	
	LinearLayout relative;
	
	Button backBtn;
	TextView title;
	
	TextView textViewCurrentTransactionInfo, textViewHelp;
	EditText editTextAmount;
	Button buttonMakePayment;
	
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
		setContentView(R.layout.activity_wallet_add_payment);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(WalletAddPaymentActivity.this, relative, 1134, 720, false);
		
		
		backBtn = (Button) findViewById(R.id.backBtn); 
		title = (TextView) findViewById(R.id.title); title.setTypeface(Data.latoRegular(getApplicationContext()));
		
		textViewCurrentTransactionInfo = (TextView) findViewById(R.id.textViewCurrentTransactionInfo); textViewCurrentTransactionInfo.setTypeface(Data.latoRegular(this));
		textViewHelp = (TextView) findViewById(R.id.textViewHelp); textViewHelp.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		
		editTextAmount = (EditText) findViewById(R.id.editTextAmount); editTextAmount.setTypeface(Data.latoRegular(this));
		
		buttonMakePayment = (Button) findViewById(R.id.buttonMakePayment); buttonMakePayment.setTypeface(Data.latoRegular(this));
		
		backBtn.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				startActivity(new Intent(WalletAddPaymentActivity.this, WalletActivity.class));
				finish();
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
		});
		
		buttonMakePayment.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String amount = editTextAmount.getText().toString().trim();
				if(AppStatus.getInstance(WalletAddPaymentActivity.this).isOnline(WalletAddPaymentActivity.this)){
					if("".equalsIgnoreCase(amount)){
						editTextAmount.requestFocus();
						editTextAmount.setError("Please enter some amount");
					}
					else{
						Intent intent = new Intent(WalletAddPaymentActivity.this, WalletWebviewActivity.class);
						intent.putExtra("amount", amount);
						startActivity(intent);
						finish();
						overridePendingTransition(R.anim.right_in, R.anim.right_out);
					}
				}
				else{
					new DialogPopup().alertPopup(WalletAddPaymentActivity.this, "", Data.CHECK_INTERNET_MSG);
				}
			}
		});
		
		
		try{
			if(getIntent().hasExtra("payment")){
				String payment = getIntent().getStringExtra("payment");
				if("failure".equalsIgnoreCase(payment)){
					SpannableString sstr = new SpannableString("Transaction failed");
					final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
					sstr.setSpan(bss, 0, sstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					textViewCurrentTransactionInfo.setText("");
					textViewCurrentTransactionInfo.append(sstr);
					textViewCurrentTransactionInfo.append(", Please try again");
					
					textViewCurrentTransactionInfo.setVisibility(View.VISIBLE);
					
					new Handler().postDelayed(new Runnable() {
						
						@Override
						public void run() {
							try {
								textViewCurrentTransactionInfo.setVisibility(View.GONE);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, 5000);
				}
				else{
					textViewCurrentTransactionInfo.setVisibility(View.GONE);
				}
			}
		} catch(Exception e){
			e.printStackTrace();
			textViewCurrentTransactionInfo.setVisibility(View.GONE);
		}
		
		
	}
	
	@Override
	public void onBackPressed() {
		startActivity(new Intent(WalletAddPaymentActivity.this, WalletActivity.class));
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
