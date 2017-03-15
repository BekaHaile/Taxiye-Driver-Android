package product.clicklabs.jugnoo.driver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;

/**
 * Created by aneeshbansal on 16/08/16.
 */


public class DriverResourceActivity extends BaseFragmentActivity {

	RelativeLayout relative;
	String accessToken;
	Button backBtn;
	TextView title, textViewResource;
	LinearLayout linearLayoutResources;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_resources);

		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(DriverResourceActivity.this, relative, 1134, 720, false);

		title = (TextView) findViewById(R.id.title) ;
		title.setTypeface(Data.latoRegular(getApplicationContext()));
		textViewResource = (TextView) findViewById(R.id.textViewResource) ;
		textViewResource.setTypeface(Data.latoRegular(getApplicationContext()));
		linearLayoutResources = (LinearLayout) findViewById(R.id.linearLayoutResources);
		backBtn = (Button) findViewById(R.id.backBtn);

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

		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				performBackPressed();
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

	public void performBackPressed() {
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}

}
