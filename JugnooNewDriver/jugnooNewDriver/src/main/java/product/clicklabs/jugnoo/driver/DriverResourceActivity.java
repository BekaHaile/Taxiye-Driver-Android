package product.clicklabs.jugnoo.driver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.utils.ASSL;
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
	LinearLayout linearLayoutResources, linearLayoutTraining, linearLayoutSupport;
	ImageView imageView2, imageView3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_resources);

		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(DriverResourceActivity.this, relative, 1134, 720, false);

		title = (TextView) findViewById(R.id.title) ;
		title.setTypeface(Fonts.mavenRegular(getApplicationContext()));
		title.setText(R.string.driver_resources);
		textViewResource = (TextView) findViewById(R.id.textViewResource) ;
		textViewResource.setTypeface(Fonts.mavenRegular(getApplicationContext()));
		textViewTraining = (TextView) findViewById(R.id.textViewTraining) ;
		textViewTraining.setTypeface(Fonts.mavenRegular(getApplicationContext()));
		linearLayoutResources = (LinearLayout) findViewById(R.id.linearLayoutResources);
		linearLayoutTraining = (LinearLayout) findViewById(R.id.linearLayoutTraining);
		textViewSupport = (TextView) findViewById(R.id.textViewSupport) ;
		textViewSupport.setTypeface(Fonts.mavenRegular(getApplicationContext()));
		linearLayoutSupport = (LinearLayout) findViewById(R.id.linearLayoutSupport);
		backBtn = findViewById(R.id.backBtn);

		imageView2 = (ImageView) findViewById(R.id.imageView2);
		imageView3 = (ImageView) findViewById(R.id.imageView3);

		RelativeLayout rlBrandingImages = (RelativeLayout) findViewById(R.id.rlBrandingImages);
		((TextView)findViewById(R.id.tvBrandingImages)).setTypeface(Fonts.mavenRegular(this));

		linearLayoutResources.setOnClickListener(new View.OnClickListener() {
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

		linearLayoutTraining.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				performBackPressed(true);
			}
		});

		linearLayoutSupport.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DriverResourceActivity.this, DriverTicketHistory.class);
				startActivity(intent);
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});

		rlBrandingImages.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DriverResourceActivity.this, DriverDocumentActivity.class);
				intent.putExtra("access_token",Data.userData.accessToken);
				intent.putExtra("in_side", true);
				intent.putExtra("doc_required", 0);
				intent.putExtra(Constants.BRANDING_IMAGES_ONLY, 1);
				startActivity(intent);
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});

		if(Prefs.with(DriverResourceActivity.this).getInt(Constants.SET_DRIVER_TUTORIAL_STATUS, 0) == 1){
			linearLayoutTraining.setVisibility(View.VISIBLE);
			imageView2.setVisibility(View.VISIBLE);
		} else {
			linearLayoutTraining.setVisibility(View.GONE);
			imageView2.setVisibility(View.GONE);
		}

		if(Prefs.with(DriverResourceActivity.this).getInt(SPLabels.SHOW_SUPPORT_IN_RESOURCES, 0) == 1){
			linearLayoutSupport.setVisibility(View.VISIBLE);
			imageView3.setVisibility(View.VISIBLE);
		} else {
			linearLayoutSupport.setVisibility(View.GONE);
			imageView3.setVisibility(View.GONE);
		}

		if(Prefs.with(this).getInt(Constants.BRANDING_IMAGE, 0) == 1){
			rlBrandingImages.setVisibility(View.VISIBLE);
		} else {
			rlBrandingImages.setVisibility(View.GONE);
		}

		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				performBackPressed(false);
			}
		});
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		ASSL.closeActivity(relative);
		System.gc();
		super.onDestroy();
	}

	public void performBackPressed(boolean result) {
		Intent intent=new Intent();
		intent.putExtra("result",result);
		setResult(14,intent);
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}

}
