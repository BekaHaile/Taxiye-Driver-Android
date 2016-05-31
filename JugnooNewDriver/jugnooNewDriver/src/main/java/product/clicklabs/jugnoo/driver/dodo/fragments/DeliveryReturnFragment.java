package product.clicklabs.jugnoo.driver.dodo.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;
import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.DriverTimeoutCheck;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.dodo.adapters.ReturnOptionsListAdapter;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.NonScrollListView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

@SuppressLint("ValidFragment")
public class DeliveryReturnFragment extends Fragment {


	LinearLayout relative;

	ImageView imageViewBack;
	TextView textViewTitle;

	ReturnOptionsListAdapter returnOptionsListAdapter;
	RecyclerView recyclerViewReturnOptions;

	Button buttonSubmit;

	ScrollView scrollView;
	LinearLayout linearLayoutMain;

	private FragmentActivity activity;

	String engagementId = "";
	LinearLayout main;

	public DeliveryReturnFragment(){
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							Bundle savedInstanceState) {


		View rootView = inflater.inflate(R.layout.fragment_dodo_return_reasons, container, false);

		main = (LinearLayout) rootView.findViewById(R.id.relative);
		main.setLayoutParams(new ViewGroup.LayoutParams(1134, 720));
		ASSL.DoMagic(main);




		imageViewBack = (ImageView) rootView.findViewById(R.id.imageViewBack);
		textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle);
		textViewTitle.setTypeface(Data.latoRegular(getActivity()), Typeface.BOLD);

		returnOptionsListAdapter = new ReturnOptionsListAdapter(getActivity());

		recyclerViewReturnOptions = (RecyclerView) rootView.findViewById(R.id.recyclerViewReturnOptions);
		recyclerViewReturnOptions.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewReturnOptions.setItemAnimator(new DefaultItemAnimator());
		recyclerViewReturnOptions.setHasFixedSize(false);


		recyclerViewReturnOptions.setAdapter(returnOptionsListAdapter);


		buttonSubmit = (Button) rootView.findViewById(R.id.buttonSubmit);
		buttonSubmit.setTypeface(Data.latoRegular(getActivity()));

		scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
		linearLayoutMain = (LinearLayout) rootView.findViewById(R.id.linearLayoutMain);


		imageViewBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});


		buttonSubmit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Data.returnOptionsList != null) {
					String returnReasonString = "";
					for (int i = 0; i < Data.returnOptionsList.size(); i++) {
						if (Data.returnOptionsList.get(i).checked) {
							returnReasonString = Data.returnOptionsList.get(i).name;
							break;
						}
					}

					if ("".equalsIgnoreCase(returnReasonString)) {
						DialogPopup.alertPopup(getActivity(), "", getResources().getString(R.string.select_reason));
					} else {
						deliveryReturnRequest(getActivity(), returnReasonString);
					}
				}

			}
		});


		setReturnOptions();
		return rootView;
	}


	private void setReturnOptions() {
		try {
			if (Data.returnOptionsList != null) {
				for (int i = 0; i < Data.returnOptionsList.size(); i++) {
					Data.returnOptionsList.get(i).checked = false;
				}
				returnOptionsListAdapter.notifyDataSetChanged();
			} else {
				performBackPressed();
			}
		} catch (Exception e) {
			e.printStackTrace();
			performBackPressed();
		}
	}


	public void performBackPressed() {
		
	}
	


	@Override
	public void onDestroy() {
		super.onDestroy();
		ASSL.closeActivity(relative);
		System.gc();
	}



	public void deliveryReturnRequest(final Activity activity, final String reason) {
		try {
			if (AppStatus.getInstance(getActivity()).isOnline(getActivity())) {
				DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));

				HashMap<String, String> params = new HashMap<String, String>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_RETURN_REASON, reason);

				RestClient.getApiServices().deliveryReturnRequest(params, new Callback<RegisterScreenResponse>() {
					@Override
					public void success(RegisterScreenResponse registerScreenResponse, Response response) {
						DialogPopup.dismissLoadingDialog();
						try {
							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
							JSONObject jObj;
							jObj = new JSONObject(jsonString);

							if (!jObj.isNull("error")) {
								String errorMessage = jObj.getString("error");
								if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
									HomeActivity.logoutUser(activity);
								} else {
									DialogPopup.alertPopup(activity, "", errorMessage);
								}
							} else {
								try {
									int flag = jObj.getInt("flag");
									if (ApiResponseFlags.REQUEST_TIMEOUT.getOrdinal() == flag) {
										String log = jObj.getString("log");
										DialogPopup.alertPopup(activity, "", "" + log);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

							new DriverTimeoutCheck().timeoutBuffer(activity, 2);


						} catch (Exception exception) {
							exception.printStackTrace();
							DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
						}
					}

					@Override
					public void failure(RetrofitError error) {
						DialogPopup.dismissLoadingDialog();
					}
				});
			} else {
				DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
