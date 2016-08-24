package product.clicklabs.jugnoo.driver.selfAudit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.DocumentListFragment;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.JSONParser;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.TransactionUtils;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.AuditStateResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
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
	String accessToken;
	AuditStateResponse auditStateResponse;
	int auditType;

	SelfAuditCameraFragment selfAuditCameraFragment;
	SelectAuditFragment selectAuditFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_self_auditing);

		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(SelfAuditActivity.this, relative, 1134, 720, false);
		selectAuditFragment = new SelectAuditFragment();

//		Bundle bundle = new Bundle();
//		accessToken = getIntent().getExtras().getString("access_token");
//		bundle.putString("access_token", accessToken);
//		selfAuditCameraFragment.setArguments(bundle);

		getSupportFragmentManager().beginTransaction()
				.add(R.id.relative, selectAuditFragment, SelectAuditFragment.class.getName())
				.addToBackStack(SelectAuditFragment.class.getName())
				.commit();


	}

	public AuditStateResponse getAuditStateResponse() {
		return auditStateResponse;
	}

	public void setAuditStateResponse(AuditStateResponse auditStateResponse) {
		this.auditStateResponse = auditStateResponse;
	}

	public int getAuditType() {
		return auditType;
	}

	public void setAuditType(int auditType) {
		this.auditType = auditType;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		selectAuditFragment.onActivityResult(requestCode, resultCode, data);
	}

	public RelativeLayout getRelativeLayoutContainer(){
		return relative;
	}

	private TransactionUtils transactionUtils;
	public TransactionUtils getTransactionUtils(){
		if(transactionUtils == null){
			transactionUtils = new TransactionUtils();
		}
		return transactionUtils;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();


		Fragment myFragment = getSupportFragmentManager().findFragmentByTag(SelfAuditCameraFragment.class.getName());
		if (myFragment != null && myFragment.isVisible()) {
			((SelfAuditCameraFragment)myFragment).performBackPressed();
		}

		Fragment myFragment1 = getSupportFragmentManager().findFragmentByTag(SelectAuditFragment.class.getName());
		if (myFragment1 != null && myFragment1.isVisible()) {
			Intent intent = new Intent(SelfAuditActivity.this, HomeActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.left_in, R.anim.left_out);
		}

		Fragment myFragment2= getSupportFragmentManager().findFragmentByTag(SubmitAuditFragment.class.getName());
		if (myFragment2 != null && myFragment2.isVisible()) {
			((SelfAuditCameraFragment)myFragment).performBackPressed();
		}

		Fragment myFragment3= getSupportFragmentManager().findFragmentByTag(NonJugnooAuditFragment.class.getName());
		if (myFragment3 != null && myFragment3.isVisible()) {
			((SelfAuditCameraFragment)myFragment).performBackPressed();
		}


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
