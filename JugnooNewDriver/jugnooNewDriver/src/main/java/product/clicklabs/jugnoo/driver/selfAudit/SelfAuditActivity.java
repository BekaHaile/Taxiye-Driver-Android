package product.clicklabs.jugnoo.driver.selfAudit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;

import product.clicklabs.jugnoo.driver.DocumentListFragment;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.JSONParser;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;

/**
 * Created by aneeshbansal on 16/08/16.
 */
public class SelfAuditActivity extends BaseFragmentActivity {




	RelativeLayout relative;

	Button backBtn, submitButton;

	RelativeLayout relativeLayoutRides;
	String accessToken;

	SelfAuditCameraFragment selfAuditCameraFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_self_auditing);

		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(SelfAuditActivity.this, relative, 1134, 720, false);
		selfAuditCameraFragment = new SelfAuditCameraFragment();

//		Bundle bundle = new Bundle();
//		accessToken = getIntent().getExtras().getString("access_token");
//		bundle.putString("access_token", accessToken);
//		selfAuditCameraFragment.setArguments(bundle);

		getSupportFragmentManager().beginTransaction()
				.add(R.id.relative, selfAuditCameraFragment, SelfAuditCameraFragment.class.getName())
				.addToBackStack(DocumentListFragment.class.getName())
				.commit();


	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		selfAuditCameraFragment.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onBackPressed() {
		JSONParser.saveAccessToken(SelfAuditActivity.this, "");
		Intent intent = new Intent(SelfAuditActivity.this, HomeActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
		super.onBackPressed();
	}


	@Override
	protected void onDestroy() {
		ASSL.closeActivity(relative);
		System.gc();
		super.onDestroy();
	}

	public void performbackPressed() {
		JSONParser.saveAccessToken(SelfAuditActivity.this, "");
		Intent intent = new Intent(SelfAuditActivity.this, HomeActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}




}
