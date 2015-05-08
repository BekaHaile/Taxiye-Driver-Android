package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.flurry.android.FlurryAgent;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.AddPaymentPath;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import rmn.androidscreenlibrary.ASSL;

public class WalletAddPaymentActivity extends Activity{
	
	LinearLayout relative;
	
	ImageView imageViewBack;
	TextView textViewTitle;
	
	TextView textViewHelp;
	EditText editTextAmount;
	Button button100, button200, button500, buttonMakePayment;
	TextView textViewCurrentBalance, textViewCurrentBalanceValue;
	
	public static AddPaymentPath addPaymentPath = AddPaymentPath.FROM_WALLET;
	
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
		setContentView(R.layout.activity_wallet_add_payment);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(WalletAddPaymentActivity.this, relative, 1134, 720, false);
		
		
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack); 
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
		
		textViewHelp = (TextView) findViewById(R.id.textViewHelp); textViewHelp.setTypeface(Fonts.latoLight(this), Typeface.BOLD);
		
		editTextAmount = (EditText) findViewById(R.id.editTextAmount); editTextAmount.setTypeface(Fonts.latoRegular(this));
		
		button100 = (Button) findViewById(R.id.button100); button100.setTypeface(Fonts.latoRegular(this));
		button200 = (Button) findViewById(R.id.button200); button200.setTypeface(Fonts.latoRegular(this));
		button500 = (Button) findViewById(R.id.button500); button500.setTypeface(Fonts.latoRegular(this));
		buttonMakePayment = (Button) findViewById(R.id.buttonMakePayment); buttonMakePayment.setTypeface(Fonts.latoRegular(this));
		
		textViewCurrentBalance = (TextView) findViewById(R.id.textViewCurrentBalance); textViewCurrentBalance.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
		textViewCurrentBalanceValue = (TextView) findViewById(R.id.textViewCurrentBalanceValue); textViewCurrentBalanceValue.setTypeface(Fonts.latoRegular(this));
		
		imageViewBack.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		
		
		button100.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				editTextAmount.setText("100");
				editTextAmount.setSelection(editTextAmount.getText().length());
			}
		});
		
		button200.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				editTextAmount.setText("200");
				editTextAmount.setSelection(editTextAmount.getText().length());
			}
		});

		button500.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				editTextAmount.setText("500");
				editTextAmount.setSelection(editTextAmount.getText().length());
			}
		});
		
		buttonMakePayment.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					String amountStr = editTextAmount.getText().toString().trim();
					if("".equalsIgnoreCase(amountStr)){
						editTextAmount.requestFocus();
						editTextAmount.setError("Please enter some amount");
					}
					else{
						double amount = Double.parseDouble(editTextAmount.getText().toString().trim());
						if(AppStatus.getInstance(WalletAddPaymentActivity.this).isOnline(WalletAddPaymentActivity.this)){
							if(amount < 1){
								editTextAmount.requestFocus();
								editTextAmount.setError("Please enter some amount");
							}
							else{
								Intent intent = new Intent(WalletAddPaymentActivity.this, WalletWebviewActivity.class);
								intent.putExtra("amount", ""+amount);
								startActivity(intent);
								finish();
								overridePendingTransition(R.anim.right_in, R.anim.right_out);
							}
						}
						else{
							DialogPopup.alertPopup(WalletAddPaymentActivity.this, "", Data.CHECK_INTERNET_MSG);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					
					editTextAmount.requestFocus();
					editTextAmount.setError("Please enter valid amount");
				}
			}
		});
		
		editTextAmount.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				int result = actionId & EditorInfo.IME_MASK_ACTION;
				switch (result) {
					case EditorInfo.IME_ACTION_DONE:
						buttonMakePayment.performClick();
					break;

					case EditorInfo.IME_ACTION_NEXT:
					break;

					default:
				}
				return true;
			}
		});
		
		
		try{
			
			textViewCurrentBalanceValue.setText(getResources().getString(R.string.rupee)+" "+Data.userData.jugnooBalance);
			
			if(getIntent().hasExtra("payment")){
				String payment = getIntent().getStringExtra("payment");
				if("failure".equalsIgnoreCase(payment)){
					DialogPopup.dialogBanner(WalletAddPaymentActivity.this, "Transaction failed, Please try again");
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		
		
	}
	
	
	public void performBackPressed(){
		if(AddPaymentPath.FROM_WALLET == addPaymentPath){
			startActivity(new Intent(WalletAddPaymentActivity.this, WalletActivity.class));
		}
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
	
}
