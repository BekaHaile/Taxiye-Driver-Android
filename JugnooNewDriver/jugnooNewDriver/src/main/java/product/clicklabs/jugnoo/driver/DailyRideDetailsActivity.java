package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Intent;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.adapters.DailyRideDetailsAdapter;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.DailyEarningItem;
import product.clicklabs.jugnoo.driver.datastructure.RideInfo;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.DailyEarningResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.InfoTileResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.InvoiceDetailResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.InvoiceDetailResponseNew;
import product.clicklabs.jugnoo.driver.retrofit.model.NewBookingHistoryRespose;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class DailyRideDetailsActivity extends BaseFragmentActivity {

	LinearLayout linear;

	Button backBtn;
	TextView title;
	String date = "";
	TextView textViewInfoDisplay;
	ArrayList<DailyEarningItem> dailyEarningItems = new ArrayList<>();
	RecyclerView recyclerViewDailyInfo;
	DailyRideDetailsAdapter dailyRideDetailsAdapter;
	Shader textShader;
	public static RideInfo openedRideInfo;
	public ASSL assl;
	int invoice_id = 0;
	CustomerInfo customerInfo;


	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.init(this, Data.FLURRY_KEY);
		FlurryAgent.onStartSession(this, Data.FLURRY_KEY);
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_daily_details_new);


		try {
			Intent intent = getIntent();

			if(intent.getStringExtra("date") != null) {
				date = intent.getStringExtra("date");
			}
			if(intent.getIntExtra("invoice_id", 0) != 0) {
				invoice_id = intent.getIntExtra("invoice_id", 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


		linear = (LinearLayout) findViewById(R.id.linear);
		assl = new ASSL(DailyRideDetailsActivity.this, linear, 1134, 720, false);

		backBtn = (Button) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Data.latoRegular(this));
//		textShader=new LinearGradient(0, 0, 0, 20,
//				new int[]{getResources().getColor(R.color.gradient_orange_v2), getResources().getColor(R.color.gradient_yellow_v2)},
//				new float[]{0, 1}, Shader.TileMode.CLAMP);
//		title.getPaint().setShader(textShader);

		textViewInfoDisplay = (TextView) findViewById(R.id.textViewInfoDisplay);
		textViewInfoDisplay.setTypeface(Data.latoRegular(this));
		textViewInfoDisplay.setVisibility(View.GONE);

		textViewInfoDisplay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!"".equalsIgnoreCase(date)) {
					getRidesAsync(date, DailyRideDetailsActivity.this);
				} else if(invoice_id != 0){
					getInvoiceDetails(DailyRideDetailsActivity.this);
				}
			}
		});

		recyclerViewDailyInfo = (RecyclerView) findViewById(R.id.recyclerViewDailyInfo);
		recyclerViewDailyInfo.setHasFixedSize(true);
		LinearLayoutManager llm = new LinearLayoutManager(this);
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerViewDailyInfo.setLayoutManager(llm);
		recyclerViewDailyInfo.setItemAnimator(new DefaultItemAnimator());

		dailyEarningItems = new ArrayList<>();
		dailyRideDetailsAdapter = new DailyRideDetailsAdapter(this, dailyEarningItems, new DailyRideDetailsAdapter.Callback() {
			@Override
			public void onRideClick(int position, InfoTileResponse.Tile.Extras extras, String date) {

				if(extras !=null) {
					Intent intent = new Intent(DailyRideDetailsActivity.this, RideDetailsNewActivity.class);
					Gson gson = new Gson();
					intent.putExtra("extras", gson.toJson(extras, InfoTileResponse.Tile.Extras.class));
					DailyRideDetailsActivity.this.startActivity(intent);
					DailyRideDetailsActivity.this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
				} else if(date != null) {
					Intent intent = new Intent(DailyRideDetailsActivity.this, DailyRideDetailsActivity.class);
					intent.putExtra("date", date);
					DailyRideDetailsActivity.this.startActivity(intent);
					DailyRideDetailsActivity.this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
				}
			}
		});
		recyclerViewDailyInfo.setAdapter(dailyRideDetailsAdapter);

		if(!"".equalsIgnoreCase(date)) {
			title.setText(getResources().getString(R.string.daily_earnings));
			getRidesAsync(date, this);
		} else if(invoice_id != 0){
			title.setText(getResources().getString(R.string.invoice_detail));
			getInvoiceDetails(this);
		}

		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});

	}

	public void updateListData(String message, boolean errorOccurred, DailyEarningResponse dailyEarningResponse, InvoiceDetailResponseNew invoiceDetailResponseNew) {
		if (errorOccurred) {
			textViewInfoDisplay.setText(message);
			textViewInfoDisplay.setVisibility(View.VISIBLE);
			dailyRideDetailsAdapter.notifyDataSetChanged();
		} else {
			dailyRideDetailsAdapter.setList(dailyEarningItems, dailyEarningResponse, invoiceDetailResponseNew);
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
		super.onDestroy();
		ASSL.closeActivity(linear);
		System.gc();
	}

	private void getRidesAsync(String date, final Activity activity) {
		try {
			DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
			RestClient.getApiServices().getDailyRidesAsync(date, Data.userData.accessToken,
					new Callback<DailyEarningResponse>() {
						@Override
						public void success(DailyEarningResponse dailyEarningResponse, Response response) {
							try {
								if(activity != null) {
									String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
									JSONObject jObj;
									jObj = new JSONObject(jsonString);
									if (!jObj.isNull("error")) {
										String errorMessage = jObj.getString("error");
										if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
											HomeActivity.logoutUser(activity);
										} else {
											updateListData(getResources().getString(R.string.error_occured_tap_to_retry), true, null, null);
										}

									} else {
										DialogPopup.dismissLoadingDialog();
										dailyEarningItems.clear();
										dailyEarningItems.add(new DailyEarningItem(null,0,null,null, 0, null,0,null,DailyRideDetailsAdapter.ViewType.TOTAL_AMNT));

										for (int i=0; i<dailyEarningResponse.getDailyParam().size(); i++) {
											dailyEarningItems.add(new DailyEarningItem(dailyEarningResponse.getDailyParam().get(i).getText()
													, dailyEarningResponse.getDailyParam().get(i).getValue(),
													null, null, 0, null , 0, null, DailyRideDetailsAdapter.ViewType.EARNING_PARAM));
										}
										dailyEarningItems.add(new DailyEarningItem(null,0,null,null, 0, null,0,null,DailyRideDetailsAdapter.ViewType.TOTAL_VALUES));

										for (int i=0; i<dailyEarningResponse.getTrips().size(); i++) {
											dailyEarningItems.add(new DailyEarningItem(null, 0,dailyEarningResponse.getTrips().get(i).getTime(),
													dailyEarningResponse.getTrips().get(i).getDate(),
													dailyEarningResponse.getTrips().get(i).getType(),
													dailyEarningResponse.getTrips().get(i).getStatus(),
													dailyEarningResponse.getTrips().get(i).getEarning(),
													dailyEarningResponse.getTrips().get(i).getExtras(), DailyRideDetailsAdapter.ViewType.RIDE_INFO));
										}

										updateListData(getResources().getString(R.string.no_rides), false, dailyEarningResponse, null);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
								try {
									DialogPopup.dismissLoadingDialog();
									updateListData(getResources().getString(R.string.error_occured_tap_to_retry), true, null, null);
								} catch (Exception e1) {
									e1.printStackTrace();
									DialogPopup.dismissLoadingDialog();
								}
							}

						}

						@Override
						public void failure(RetrofitError error) {
							try {
								DialogPopup.dismissLoadingDialog();
								updateListData(getResources().getString(R.string.error_occured_tap_to_retry), true, null, null);
							} catch (Exception e) {
								DialogPopup.dismissLoadingDialog();
								e.printStackTrace();
							}
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
			DialogPopup.dismissLoadingDialog();
		}
	}

	private void getInvoiceDetails(final Activity activity) {
		try {
			DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
			RestClient.getApiServices().invoiceDetailNew(Data.userData.accessToken, String.valueOf(invoice_id), new Callback<InvoiceDetailResponseNew>() {
				@Override
				public void success(InvoiceDetailResponseNew invoiceDetailResponse, Response response) {
					try {
						if (activity != null) {
							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
							JSONObject jObj;
							jObj = new JSONObject(jsonString);
							if (!jObj.isNull("error")) {
								String errorMessage = jObj.getString("error");
								if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
									HomeActivity.logoutUser(activity);
								} else {
									updateListData(getResources().getString(R.string.error_occured_tap_to_retry), true, null, null);
								}

							} else {
								DialogPopup.dismissLoadingDialog();
								dailyEarningItems.clear();
								dailyEarningItems.add(new DailyEarningItem(null,0,null,null,0,null,0,null,DailyRideDetailsAdapter.ViewType.TOTAL_AMNT));

								for (int i=0; i<invoiceDetailResponse.getEarningParams().size(); i++) {
									dailyEarningItems.add(new DailyEarningItem(invoiceDetailResponse.getEarningParams().get(i).getText()
											, invoiceDetailResponse.getEarningParams().get(i).getValue(),
											null, null, 0, null, 0, null, DailyRideDetailsAdapter.ViewType.EARNING_PARAM));
								}
								dailyEarningItems.add(new DailyEarningItem(null,0,null,null,0,null,0,null,DailyRideDetailsAdapter.ViewType.TOTAL_VALUES));

								for (int i=0; i<invoiceDetailResponse.getDailyBreakup().size(); i++) {
									dailyEarningItems.add(new DailyEarningItem(null, 0,null,
											invoiceDetailResponse.getDailyBreakup().get(i).getDate(),0,null,
											invoiceDetailResponse.getDailyBreakup().get(i).getEarnings(),
											null, DailyRideDetailsAdapter.ViewType.RIDE_INFO));
								}

								updateListData(getResources().getString(R.string.no_rides), false, null, invoiceDetailResponse);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						try {
							DialogPopup.dismissLoadingDialog();
							updateListData(getResources().getString(R.string.error_occured_tap_to_retry), true, null, null);
						} catch (Exception e1) {
							e1.printStackTrace();
							DialogPopup.dismissLoadingDialog();
						}
					}

				}

				@Override
				public void failure(RetrofitError error) {
					Log.i("error", String.valueOf(error));
					DialogPopup.dismissLoadingDialog();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			DialogPopup.dismissLoadingDialog();
		}
	}

}
