package product.clicklabs.jugnoo.driver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Prefs;

/**
 * Created by aneeshbansal on 16/08/16.
 */


public class DriverResourceActivity extends BaseFragmentActivity {

	RelativeLayout relative;
	String accessToken;
	View backBtn;
	TextView title, textViewResource, textViewTraining, textViewSupport;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_resources);

		relative = (RelativeLayout) findViewById(R.id.relative);

		title = (TextView) findViewById(R.id.title) ;
		title.setTypeface(Fonts.mavenRegular(getApplicationContext()));
		title.setText(R.string.driver_resources);
		textViewResource = (TextView) findViewById(R.id.textViewResource) ;
		textViewResource.setTypeface(Fonts.mavenRegular(getApplicationContext()));
		textViewTraining = (TextView) findViewById(R.id.textViewTraining) ;
		textViewTraining.setTypeface(Fonts.mavenRegular(getApplicationContext()));
		textViewSupport = (TextView) findViewById(R.id.textViewSupport) ;
		textViewSupport.setTypeface(Fonts.mavenRegular(getApplicationContext()));
		backBtn = findViewById(R.id.backBtn);

		TextView tvBrandingImages = findViewById(R.id.tvBrandingImages);

		textViewResource.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DriverResourceActivity.this, DriverDocumentActivity.class);
				intent.putExtra("access_token",Data.userData.accessToken);
				intent.putExtra("in_side", true);
				intent.putExtra("doc_required", 0);
				startActivity(intent);
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});

		textViewTraining.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				performBackPressed(true);
			}
		});

		textViewSupport.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DriverResourceActivity.this, DriverTicketHistory.class);
				startActivity(intent);
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});

		tvBrandingImages.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(DriverResourceActivity.this, DriverDocumentActivity.class);
//				intent.putExtra("access_token",Data.userData.accessToken);
//				intent.putExtra("in_side", true);
//				intent.putExtra("doc_required", 0);
//				intent.putExtra(Constants.BRANDING_IMAGES_ONLY, 1);
				Intent intent = new Intent(DriverResourceActivity.this, DriverTasksActivity.class);
				intent.putExtra("access_token",Data.userData.accessToken);
				intent.putExtra("in_side", true);
				intent.putExtra("doc_required", 0);
				intent.putExtra(Constants.BRANDING_IMAGES_ONLY, 1);
				startActivity(intent);
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});

		if(Prefs.with(DriverResourceActivity.this).getInt(Constants.SET_DRIVER_TUTORIAL_STATUS, 0) == 1){
			textViewTraining.setVisibility(View.VISIBLE);
			findViewById(R.id.vTraining).setVisibility(View.VISIBLE);
		} else {
			textViewTraining.setVisibility(View.GONE);
			findViewById(R.id.vTraining).setVisibility(View.GONE);
		}

		if(Prefs.with(DriverResourceActivity.this).getInt(SPLabels.SHOW_SUPPORT_IN_RESOURCES, 0) == 1){
			textViewSupport.setVisibility(View.VISIBLE);
			findViewById(R.id.vSupport).setVisibility(View.VISIBLE);
		} else {
			textViewSupport.setVisibility(View.GONE);
			findViewById(R.id.vSupport).setVisibility(View.GONE);
		}

		if(Prefs.with(this).getInt(Constants.BRANDING_IMAGE, 0) == 1){
			tvBrandingImages.setVisibility(View.VISIBLE);
			findViewById(R.id.vBranding).setVisibility(View.VISIBLE);
		} else {
			tvBrandingImages.setVisibility(View.GONE);
			findViewById(R.id.vBranding).setVisibility(View.GONE);
		}

		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				performBackPressed(false);
			}
		});
	}

	public void performBackPressed(boolean result) {
		Intent intent=new Intent();
		intent.putExtra("result",result);
		setResult(14,intent);
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}

}
