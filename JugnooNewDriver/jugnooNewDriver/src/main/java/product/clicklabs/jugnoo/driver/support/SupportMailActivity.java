package product.clicklabs.jugnoo.driver.support;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;

public class SupportMailActivity extends BaseActivity implements View.OnClickListener{

	private EditText etMessage;
	private Button bSubmit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_support_mail);

		ImageView backBtn = (ImageView) findViewById(R.id.backBtn);
		backBtn.setOnClickListener(this);
		((TextView) findViewById(R.id.title)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.title)).setText(R.string.support);
		((TextView) findViewById(R.id.tvSendUsMail)).setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);

		etMessage = (EditText) findViewById(R.id.etMessage);
		etMessage.setTypeface(Fonts.mavenRegular(this));

		bSubmit = (Button) findViewById(R.id.bSubmit);
		bSubmit.setTypeface(Fonts.mavenRegular(this));
		bSubmit.setOnClickListener(this);

		etMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				bSubmit.performClick();
				return true;
			}
		});


	}


	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.backBtn:
				onBackPressed();
				break;

			case R.id.bSubmit:
				if(etMessage.getText().toString().trim().length() == 0){
					Utils.showToast(this, getString(R.string.please_enter_something));
					return;
				}
				if(Data.userData != null) {
					Utils.openMailIntent(this,
							new String[]{Prefs.with(this).getString(Constants.DRIVER_SUPPORT_EMAIL, getString(R.string.support_email))},
							Prefs.with(this).getString(Constants.DRIVER_SUPPORT_EMAIL_SUBJECT, getString(R.string.support_email_subject)),
							etMessage.getText().toString().trim());
					etMessage.setText("");
				}
				break;
		}
	}

}
