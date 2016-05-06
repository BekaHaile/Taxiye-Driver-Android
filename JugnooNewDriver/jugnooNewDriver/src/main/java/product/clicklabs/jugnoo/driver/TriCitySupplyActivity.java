package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.driver.adapters.DestinationOptionsListAdapter;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.DestinationDataResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.HeatMapResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.NonScrollListView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class TriCitySupplyActivity extends BaseActivity implements ActivityCloser {


	LinearLayout relative;

	ImageView imageViewBack;
	TextView textViewTitle;

	NonScrollListView listViewDestinationOptions;
	DestinationOptionsListAdapter destinationOptionsListAdapter;
	DestinationDataResponse destinationDataResponseGlobal;

	Button buttonOk;

	ScrollView scrollView;
	LinearLayout linearLayoutMain;
	ArrayList<DestinationDataResponse.Region> destinationOptionList = new ArrayList<>();

	public static ActivityCloser activityCloser = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tricity_supply);


		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(TriCitySupplyActivity.this, relative, 1134, 720, false);


		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
		textViewTitle = (TextView) findViewById(R.id.textViewTitle);
		textViewTitle.setTypeface(Data.latoRegular(this), Typeface.BOLD);


		listViewDestinationOptions = (NonScrollListView) findViewById(R.id.listViewDestinationOptions);
		destinationOptionsListAdapter = new DestinationOptionsListAdapter(TriCitySupplyActivity.this, destinationOptionList);
		listViewDestinationOptions.setAdapter(destinationOptionsListAdapter);


		buttonOk = (Button) findViewById(R.id.buttonOk);
		buttonOk.setTypeface(Data.latoRegular(this));

		scrollView = (ScrollView) findViewById(R.id.scrollView);
		linearLayoutMain = (LinearLayout) findViewById(R.id.linearLayoutMain);


		imageViewBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});


		buttonOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (destinationOptionList != null) {
					ArrayList<Integer> destiantionData = new ArrayList<>();
					for (int i = 0; i < destinationOptionList.size(); i++) {
						if (destinationOptionList.get(i).getIsSelected() == 1) {
							destiantionData.add(destinationOptionList.get(i).getRegionId());
						}
					}

					if (destiantionData.size() == 0) {
						DialogPopup.alertPopup(TriCitySupplyActivity.this, "", getResources().getString(R.string.select_destination));
					} else {
						driverDestinationAsync(TriCitySupplyActivity.this, destiantionData);
					}
				}

			}
		});


		TriCitySupplyActivity.activityCloser = this;

		fetchDestinationData(TriCitySupplyActivity.this);
	}


	private void setDestinationOptions() {
		try {
			if (destinationOptionList != null) {
				destinationOptionsListAdapter.notifyDataSetChanged();
			} else {
				performBackPressed();
			}
		} catch (Exception e) {
			e.printStackTrace();
			performBackPressed();
		}
	}


	public void performBackPressed() {
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}

	@Override
	public void onBackPressed() {
		performBackPressed();
		super.onBackPressed();
	}


	@Override
	public void onDestroy() {
		TriCitySupplyActivity.activityCloser = null;
		super.onDestroy();
		ASSL.closeActivity(relative);
		System.gc();
	}


	@Override
	public void close() {
		performBackPressed();
	}


	public void driverDestinationAsync(final Activity activity, ArrayList<Integer> destination) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));

			HashMap<String, String> params = new HashMap<String, String>();
			params.put("access_token", Data.userData.accessToken);
			params.put("region_ids", String.valueOf(destination));

			RestClient.getApiServices().updateDriverRegion(params, new Callback<RegisterScreenResponse>() {
				@Override
				public void success(RegisterScreenResponse registerScreenResponse, Response response) {
					DialogPopup.dismissLoadingDialog();
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj;
						jObj = new JSONObject(jsonString);
						String message = JSONParser.getServerMessage(jObj);
						int flag = jObj.optInt("flag", ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
						if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)) {
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								DialogPopup.alertPopup(activity, "", message);
								performBackPressed();
							}
						}
					} catch (Exception exception) {
						exception.printStackTrace();
						DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
					}
				}

				@Override
				public void failure(RetrofitError error) {
					DialogPopup.dismissLoadingDialog();
					performBackPressed();
				}
			});
		} else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}
	}

	public void fetchDestinationData(final Activity activity) {
		try {
			RestClient.getApiServices().getDestinationData(Data.userData.accessToken, new Callback<DestinationDataResponse>() {
				@Override
				public void success(DestinationDataResponse destinationDataResponse, Response response) {
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj;
						jObj = new JSONObject(jsonString);
						String message = JSONParser.getServerMessage(jObj);
						int flag = jObj.optInt("flag", ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
						if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)) {
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								destinationDataResponseGlobal = destinationDataResponse;
								parseDestinationData(destinationDataResponseGlobal);
							}
						}
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}

				@Override
				public void failure(RetrofitError error) {
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void parseDestinationData(DestinationDataResponse destinationDataResponse) {
		try {
			destinationOptionList.addAll(destinationDataResponse.getRegions());
			setDestinationOptions();

		} catch (Exception e) {
		}
	}


}
