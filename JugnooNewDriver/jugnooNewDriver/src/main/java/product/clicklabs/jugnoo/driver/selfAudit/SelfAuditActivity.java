package product.clicklabs.jugnoo.driver.selfAudit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.AuditStateResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by aneeshbansal on 16/08/16.
 */

@SuppressLint("ValidFragment")

public class SelfAuditActivity extends BaseFragmentActivity {




	RelativeLayout relative;
	String accessToken;
	AuditStateResponse auditStateResponse;
	int auditType;
	String selfAudit;

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

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			selfAudit = extras.getString("self_audit");
		}

		try {
			if(selfAudit.equalsIgnoreCase("yes")){
				Prefs.with(SelfAuditActivity.this).save(SPLabels.SET_AUDIT_STATUS_POPUP, 0);
				getAuditState(SelfAuditActivity.this, 0);
			}else {
				getSupportFragmentManager().beginTransaction()
						.add(R.id.relative, selectAuditFragment, SelectAuditFragment.class.getName())
						.addToBackStack(SelectAuditFragment.class.getName())
						.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
			getSupportFragmentManager().beginTransaction()
					.add(R.id.relative, selectAuditFragment, SelectAuditFragment.class.getName())
					.addToBackStack(SelectAuditFragment.class.getName())
					.commit();
		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		try {
			SelfAuditActivity.this.getTransactionUtils().openSelectAuditFragment(SelfAuditActivity.this,
					SelfAuditActivity.this.getRelativeLayoutContainer());
		} catch (Exception e) {
			e.printStackTrace();
		}
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


		try {
			Fragment myFragment = getSupportFragmentManager().findFragmentByTag(SelfAuditCameraFragment.class.getName());
			if (myFragment != null && myFragment.isVisible()) {
				((SelfAuditCameraFragment)myFragment).performBackPressed();
			}

			Fragment myFragment1 = getSupportFragmentManager().findFragmentByTag(SelectAuditFragment.class.getName());
			if (myFragment1 != null && myFragment1.isVisible()) {
				performbackPressed();
			}

			Fragment myFragment2= getSupportFragmentManager().findFragmentByTag(SubmitAuditFragment.class.getName());
			if (myFragment2 != null && myFragment2.isVisible()) {
				((SubmitAuditFragment)myFragment).performBackPress();
			}

			Fragment myFragment3= getSupportFragmentManager().findFragmentByTag(NonJugnooAuditFragment.class.getName());
			if (myFragment3 != null && myFragment3.isVisible()) {
				((NonJugnooAuditFragment)myFragment).performBackPress();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}


	}



	@Override
	protected void onDestroy() {
		ASSL.closeActivity(relative);
		System.gc();
		super.onDestroy();
	}

	public void performbackPressed() {
		Intent intent = new Intent(SelfAuditActivity.this, HomeActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}

	public void getAuditState(final Activity activity, final Integer auditType) {
		try {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
				HashMap<String, String> params = new HashMap<String, String>();

				params.put("access_token", Data.userData.accessToken);
				params.put("audit_type", String.valueOf(auditType));
				Log.i("params", "=" + params);

				RestClient.getApiServices().fetchAuditTypeStatus(params, new Callback<AuditStateResponse>() {
					@Override
					public void success(AuditStateResponse auditStateResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						try {
							JSONObject jObj = new JSONObject(responseStr);
							int flag = jObj.getInt("flag");
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								SelfAuditActivity.this.auditStateResponse = auditStateResponse;
								SelfAuditActivity selfAuditActivity = new SelfAuditActivity();
								selfAuditActivity.setAuditStateResponse(auditStateResponse);
								setFragmentState(auditType);
							} else {
								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
						} catch (Exception exception) {
							exception.printStackTrace();
							DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
						}
						DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e("request fail", error.toString());
						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
					}
				});
			} else {
				DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setFragmentState(Integer auditType){

		if(auditStateResponse != null) {

			if (auditStateResponse.getAction() == 5) {

				if(auditType == 0){

					SelfAuditActivity.this.getTransactionUtils().openAuditCameraFragment(SelfAuditActivity.this,
							SelfAuditActivity.this.getRelativeLayoutContainer(), 0, auditType, 0);

				} else {
					SelfAuditActivity.this.getTransactionUtils().openNonJugnooAuditFragment(SelfAuditActivity.this,
							SelfAuditActivity.this.getRelativeLayoutContainer(), auditType);
				}

			} else if (auditStateResponse.getAction() == 10) {

				SelfAuditActivity.this.getTransactionUtils().openAuditCameraFragment(SelfAuditActivity.this,
						SelfAuditActivity.this.getRelativeLayoutContainer(), auditStateResponse.getLastUnavailableImageType(), auditType, 0);

			} else if (auditStateResponse.getAction() == 15) {

				SelfAuditActivity.this.getTransactionUtils().openSubmitAuditFragment(SelfAuditActivity.this,
						SelfAuditActivity.this.getRelativeLayoutContainer(), auditType);

			} else if (auditStateResponse.getAction() == 25) {

				SelfAuditActivity.this.getTransactionUtils().openSubmitAuditFragment(SelfAuditActivity.this,
						SelfAuditActivity.this.getRelativeLayoutContainer(), auditType);

			}
		}
	}

}
