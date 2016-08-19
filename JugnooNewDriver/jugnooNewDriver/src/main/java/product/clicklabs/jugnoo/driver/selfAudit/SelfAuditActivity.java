package product.clicklabs.jugnoo.driver.selfAudit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;

import product.clicklabs.jugnoo.driver.DocumentListFragment;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.JSONParser;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.TransactionUtils;
import product.clicklabs.jugnoo.driver.retrofit.model.AuditStateResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;

/**
 * Created by aneeshbansal on 16/08/16.
 */
public class SelfAuditActivity extends BaseFragmentActivity {




	RelativeLayout relative;
	String accessToken;
	AuditStateResponse auditStateResponse;

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
