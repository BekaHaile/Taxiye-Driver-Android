package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Shader;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.driver.adapters.DailyRideDetailsAdapter;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.DailyEarningItem;
import product.clicklabs.jugnoo.driver.datastructure.RideInfo;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.DailyEarningResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.DriverEarningsResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.InvoiceDetailResponseNew;
import product.clicklabs.jugnoo.driver.retrofit.model.Tile;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FirebaseEvents;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class DailyEarningActivity extends BaseFragmentActivity {

	public static final String EARNING_DATA = "earning_data";
    RelativeLayout linear;
	ImageView backBtn;
	TextView title;
	String date = "";
	LinearLayout linearLayoutNoItems;
	TextView textViewInfoDisplay;
	ArrayList<DailyEarningItem> dailyEarningItems = new ArrayList<>();
	RecyclerView recyclerViewDailyInfo;
	DailyRideDetailsAdapter dailyRideDetailsAdapter;
	Shader textShader;
	public static RideInfo openedRideInfo;
	public ASSL assl;
	int invoice_id = 0;
	CustomerInfo customerInfo;
	DriverEarningsResponse.Earning earning;
	private Gson gson = new Gson();
	@Override
	protected void onStart() {
		super.onStart();


	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_daily_earning);


		try {
			Intent intent = getIntent();

			if(intent.getStringExtra("date") != null) {
				date = intent.getStringExtra("date");
			}
			if(intent.getStringExtra(EARNING_DATA)!=null){
				earning =gson.fromJson(intent.getStringExtra(EARNING_DATA),DriverEarningsResponse.Earning.class);
			}
			if(intent.getIntExtra("invoice_id", 0) != 0) {
				invoice_id = intent.getIntExtra("invoice_id", 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


		linear = (RelativeLayout) findViewById(R.id.linear);
		assl = new ASSL(DailyEarningActivity.this, linear, 1134, 720, false);

		backBtn = (ImageView) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Fonts.mavenRegular(this));
		title.setText(R.string.daily_earnings);
//		textShader=new LinearGradient(0, 0, 0, 20,
//				new int[]{getResources().getColor(R.color.gradient_orange_v2), getResources().getColor(R.color.gradient_yellow_v2)},
//				new float[]{0, 1}, Shader.TileMode.CLAMP);
//		title.getPaint().setShader(textShader);


		textViewInfoDisplay = (TextView) findViewById(R.id.textViewInfoDisplay);
		textViewInfoDisplay.setTypeface(Fonts.mavenRegular(this));
		linearLayoutNoItems = findViewById(R.id.linearLayoutNoItems);
		linearLayoutNoItems.setVisibility(View.GONE);

		textViewInfoDisplay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!"".equalsIgnoreCase(date)) {
					getRidesAsync(date, DailyEarningActivity.this);
				} else if(invoice_id != 0){
					getInvoiceDetails(DailyEarningActivity.this);
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
			public void onRideClick(int position, Tile.Extras extras, String date) {

				if(extras !=null) {
					Intent intent = new Intent(DailyEarningActivity.this, RideDetailsNewActivity.class);
					Gson gson = new Gson();
					intent.putExtra("extras", gson.toJson(extras, Tile.Extras.class));
					DailyEarningActivity.this.startActivity(intent);
					DailyEarningActivity.this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
				} else if(date != null) {
					Intent intent = new Intent(DailyEarningActivity.this, DailyEarningActivity.class);
					intent.putExtra("date", date);
					DailyEarningActivity.this.startActivity(intent);
					DailyEarningActivity.this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
				}
			}
		},invoice_id);
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
				MyApplication.getInstance().logEvent(FirebaseEvents.DAILY_EARNING+"_"+FirebaseEvents.BACK, null);
				performBackPressed();
			}
		});

	}

	public void updateListData(String message, boolean errorOccurred, DailyEarningResponse dailyEarningResponse, InvoiceDetailResponseNew invoiceDetailResponseNew) {
		if (errorOccurred) {
			textViewInfoDisplay.setText(message);
			linearLayoutNoItems.setVisibility(View.VISIBLE);
			dailyRideDetailsAdapter.notifyDataSetChanged();
		} else {
			linearLayoutNoItems.setVisibility(View.GONE);
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
			if (AppStatus.getInstance(activity).isOnline(activity)) {
			DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
				HashMap<String, String> params = new HashMap<>();
				params.put("access_token", Data.userData.accessToken);
				params.put("start_from", "" + 0);
				params.put("engagement_date", "" + date);
				HomeUtil.putDefaultParams(params);
				if(earning!=null && earning.getLastInvoiceDate()!=null){
					params.put("last_invoice_date", "" + earning.getLastInvoiceDate());
				}

			    RestClient.getApiServices().getDailyRidesAsync(params, new Callback<DailyEarningResponse>() {
						@Override
						public void success(DailyEarningResponse dailyEarningResponse, Response response) {
							try {
								if(activity != null) {
									DialogPopup.dismissLoadingDialog();
									String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
									JSONObject jObj = new JSONObject(jsonString);
									int flag = jObj.optInt(Constants.KEY_FLAG, ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
									String message = JSONParser.getServerMessage(jObj);
									if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag, null)){
										if(flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()){

											dailyEarningItems.clear();
											dailyEarningItems.add(new DailyEarningItem(null,0,null,null, 0, null,0,null,DailyRideDetailsAdapter.ViewType.TOTAL_AMNT, null));

											if(Prefs.with(DailyEarningActivity.this).getInt(Constants.KEY_SHOW_BANK_DEPOSIT, 1) == 1) {
												if (Data.isCaptive() && invoice_id == 0) {
													if (dailyEarningResponse.getExtrasData() != null && dailyEarningResponse.getExtrasData().getCaptiveSlots() != null) {
														for (int i = 0; i < dailyEarningResponse.getExtrasData().getCaptiveSlots().size(); i++) {
															dailyEarningItems.add(new DailyEarningItem(dailyEarningResponse.getExtrasData().getCaptiveSlots().get(i).getSlotName()
																	, dailyEarningResponse.getExtrasData().getCaptiveSlots().get(i).getOnlineMin(),
																	null, null, 0, null, 0, null, DailyRideDetailsAdapter.ViewType.EARNING_PARAM, null));
														}
													}
												} else {
													for (int i = 0; i < dailyEarningResponse.getDailyParam().size(); i++) {
														dailyEarningItems.add(new DailyEarningItem(dailyEarningResponse.getDailyParam().get(i).getText()
																, dailyEarningResponse.getDailyParam().get(i).getValue(),
																null, null, 0, null, 0, null, DailyRideDetailsAdapter.ViewType.EARNING_PARAM, dailyEarningResponse.getDailyParam().get(i).getCurrencyUnit()));
													}
												}
											}

											if(!Data.isCaptive()){
												dailyEarningItems.add(new DailyEarningItem(null,0,null,null, 0, null,0,null,DailyRideDetailsAdapter.ViewType.TOTAL_VALUES, null));

											}
											dailyEarningItems.add(new DailyEarningItem(null,0,null,null, 0, null,0,null,DailyRideDetailsAdapter.ViewType.TRIP_HEADING, null));

											for (int i=0; i<dailyEarningResponse.getTrips().size(); i++) {
												dailyEarningItems.add(new DailyEarningItem(null, 0,dailyEarningResponse.getTrips().get(i).getTime(),
														dailyEarningResponse.getTrips().get(i).getDate(),
														dailyEarningResponse.getTrips().get(i).getType(),
														dailyEarningResponse.getTrips().get(i).getStatus(),
														dailyEarningResponse.getTrips().get(i).getEarning(),
														dailyEarningResponse.getTrips().get(i).getExtras(), DailyRideDetailsAdapter.ViewType.RIDE_INFO,dailyEarningResponse.getTrips().get(i).getCurrencyUnit()));
											}

											updateListData(getResources().getString(R.string.no_rides), false, dailyEarningResponse, null);
										} else{
											DialogPopup.alertPopup(activity, "", message);
										}
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
			} else {
				DialogPopup.alertPopup(DailyEarningActivity.this, "", Data.CHECK_INTERNET_MSG);
			}
		} catch (Exception e) {
			e.printStackTrace();
			DialogPopup.dismissLoadingDialog();
		}
	}

	private void getInvoiceDetails(final Activity activity) {
		try {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
			DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put("invoice_id", String.valueOf(invoice_id));
				HomeUtil.putDefaultParams(params);
			RestClient.getApiServices().invoiceDetailNew(params, new Callback<InvoiceDetailResponseNew>() {
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
									HomeActivity.logoutUser(activity, null);
								} else {
									updateListData(getResources().getString(R.string.error_occured_tap_to_retry), true, null, null);
								}

							} else {
								DialogPopup.dismissLoadingDialog();
								dailyEarningItems.clear();
								dailyEarningItems.add(new DailyEarningItem(null,0,null,null,0,null,0,null,DailyRideDetailsAdapter.ViewType.TOTAL_AMNT, null));

								if(Prefs.with(DailyEarningActivity.this).getInt(Constants.KEY_SHOW_BANK_DEPOSIT, 1) == 1) {
									for (int i = 0; i < invoiceDetailResponse.getEarningParams().size(); i++) {
										dailyEarningItems.add(new DailyEarningItem(invoiceDetailResponse.getEarningParams().get(i).getText()
												, invoiceDetailResponse.getEarningParams().get(i).getValue(),
												null, null, 0, null, 0, null, DailyRideDetailsAdapter.ViewType.EARNING_PARAM, null));
									}
								}
								dailyEarningItems.add(new DailyEarningItem(null,0,null,null,0,null,0,null,DailyRideDetailsAdapter.ViewType.TOTAL_VALUES, null));
								dailyEarningItems.add(new DailyEarningItem(null,0,null,null, 0, null,0,null,DailyRideDetailsAdapter.ViewType.TRIP_HEADING, null));

								for (int i=0; i<invoiceDetailResponse.getDailyBreakup().size(); i++) {
									dailyEarningItems.add(new DailyEarningItem(null, 0,null,
											invoiceDetailResponse.getDailyBreakup().get(i).getDate(),0,null,
											invoiceDetailResponse.getDailyBreakup().get(i).getEarnings(),
											null, DailyRideDetailsAdapter.ViewType.RIDE_INFO, null));
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
			} else {
				DialogPopup.alertPopup(DailyEarningActivity.this, "", Data.CHECK_INTERNET_MSG);
			}
		} catch (Exception e) {
			e.printStackTrace();
			DialogPopup.dismissLoadingDialog();
		}
	}


}
