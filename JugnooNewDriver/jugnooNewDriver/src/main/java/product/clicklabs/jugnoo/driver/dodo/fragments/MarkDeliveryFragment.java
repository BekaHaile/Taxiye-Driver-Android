package product.clicklabs.jugnoo.driver.dodo.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.JSONParser;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.SplashNewActivity;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.dodo.TransactionUtils;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.EarningsDetailResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


@SuppressLint("ValidFragment")

public class MarkDeliveryFragment extends Fragment {

	private LinearLayout relative;

	private TextView textViewOrderId, textViewTakeCash, textViewAmount;
	private TransactionUtils transactionUtils;
	private Button btnCollected,btnReturned;

	private View rootView;
	private HomeActivity activity;
	EarningsDetailResponse earningsDetailResponse;

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_dodo_mark_delivered, container, false);


		activity = (HomeActivity) getActivity();

		relative = (LinearLayout) rootView.findViewById(R.id.relative);
		try {
			if (relative != null) {
				new ASSL(activity, relative, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}



		textViewOrderId = (TextView) rootView.findViewById(R.id.textViewOrderId);
		textViewOrderId.setTypeface(Data.latoRegular(activity));
		textViewTakeCash = (TextView) rootView.findViewById(R.id.textViewTakeCash);
		textViewTakeCash.setTypeface(Data.latoRegular(activity));
		textViewAmount = (TextView) rootView.findViewById(R.id.textViewAmount);
		textViewAmount.setTypeface(Data.latoRegular(activity));

		btnCollected = (Button) rootView.findViewById(R.id.btnCollected);
		btnReturned = (Button) rootView.findViewById(R.id.btnReturned);


		btnCollected.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				markDelivered();
			}
		});

		btnReturned.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getTransactionUtils().openDeliveryReturnFragment(activity, activity.getRelativeLayoutContainer());
			}
		});





		update();

		return rootView;
	}


	public void update() {
		try {
			if (earningsDetailResponse != null) {
				textViewOrderId.setText("");
				textViewTakeCash.setText("");
				textViewAmount.setText(getResources().getString(R.string.rupee) + "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			ASSL.closeActivity(relative);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.gc();
	}

	public TransactionUtils getTransactionUtils(){
		if(transactionUtils == null){
			transactionUtils = new TransactionUtils();
		}
		return transactionUtils;
	}

	public void markDelivered() {
		if (!(activity).checkIfUserDataNull() && AppStatus.getInstance(activity).isOnline(activity)) {
			DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
			RestClient.getApiServices().markDelivered(Data.userData.accessToken, Data.LOGIN_TYPE,
					new Callback<EarningsDetailResponse>() {
						@Override
						public void success(EarningsDetailResponse earningsDetailResponse, Response response) {
							DialogPopup.dismissLoadingDialog();
							try {
								String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
								JSONObject jObj;
								jObj = new JSONObject(jsonString);
								int flag = jObj.optInt("flag", ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
								String message = JSONParser.getServerMessage(jObj);
								if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)) {
									if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {

									} else {
										DialogPopup.alertPopup(activity, "", message);
									}
								}
							} catch (Exception exception) {
								exception.printStackTrace();
							}
						}

						@Override
						public void failure(RetrofitError error) {
							DialogPopup.dismissLoadingDialog();
						}
					});
		}
	}


}
