package product.clicklabs.jugnoo.driver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import org.json.JSONObject;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.DocRequirementResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

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
