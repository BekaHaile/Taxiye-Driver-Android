package product.clicklabs.jugnoo.driver.dodo.dialog;

import android.app.Dialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.JSONParser;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.SplashNewActivity;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.dodo.adapters.ReturnOptionsListAdapter;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryInfo;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryStatus;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by aneeshbansal on 22/10/16.
 */
public class DeliveryReturnListDialog {

	private Dialog dialog = null;
	private ReturnOptionsListAdapter returnOptionsListAdapter;
	private HomeActivity activity;
	private ArrayList<DeliveryInfo> tasksList;
	private DeliveryInfo deliveryInfo;
	private int deliveryInfoId;
	private int engagementId;

	public DeliveryReturnListDialog(HomeActivity activity, int engagementId, int deliveryInfoId){
		this.activity = activity;
		this.engagementId = engagementId;
		this.deliveryInfoId = deliveryInfoId;
	}

	public void deliveryReturnListDialog(final HomeActivity activity, String message, String okText, String canceltext,
										 final boolean cancelable) {
		try {
			dismissAlertPopup();
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_delivery_return_reason);

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(cancelable);
			dialog.setCanceledOnTouchOutside(cancelable);

			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage);
			textMessage.setTypeface(Data.latoRegular(activity));

			RecyclerView recyclerViewReturnOptions;
			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));

			textMessage.setText(message);

			recyclerViewReturnOptions = (RecyclerView) dialog.findViewById(R.id.recyclerViewReturnOptions);
			recyclerViewReturnOptions.setLayoutManager(new LinearLayoutManager(activity));
			recyclerViewReturnOptions.setItemAnimator(new DefaultItemAnimator());
			recyclerViewReturnOptions.setHasFixedSize(false);
			returnOptionsListAdapter = new ReturnOptionsListAdapter(activity);
			recyclerViewReturnOptions.setAdapter(returnOptionsListAdapter);

			if(Data.deliveryReturnOptionList.size() < 10){
				float heightToSet = (float)Data.deliveryReturnOptionList.size() * 65f;
				LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) recyclerViewReturnOptions.getLayoutParams();
				params.height = (int) (heightToSet * ASSL.Yscale());
				recyclerViewReturnOptions.setLayoutParams(params);
			}

			Button btnOk = (Button) dialog.findViewById(R.id.btnSubmit);
			btnOk.setTypeface(Data.latoRegular(activity));
			if(!"".equalsIgnoreCase(okText)){
				btnOk.setText(okText);
			}

			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
			btnCancel.setTypeface(Data.latoRegular(activity));
			if(!"".equalsIgnoreCase(canceltext)){
				btnCancel.setText(canceltext);
			}

			setReturnOptions();

			try{
				CustomerInfo customerInfo = Data.getCustomerInfo(String.valueOf(engagementId));
				deliveryInfo = customerInfo.getDeliveryInfos().get(customerInfo.getDeliveryInfos()
						.indexOf(new DeliveryInfo(deliveryInfoId)));
			} catch(Exception e){
				e.printStackTrace();
				activity.onBackPressed();
			}

			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					if (Data.deliveryReturnOptionList != null) {
						String returnReasonString = "";
						for (int i = 0; i < Data.deliveryReturnOptionList.size(); i++) {
							if (Data.deliveryReturnOptionList.get(i).isChecked()) {
								returnReasonString = Data.deliveryReturnOptionList.get(i).getName();
								break;
							}
						}
						if ("".equalsIgnoreCase(returnReasonString)) {
							DialogPopup.alertPopup(activity, "", activity.getResources().getString(R.string.select_reason));
						} else {
							deliveryReturnRequest(returnReasonString);
						}
					}
				}
			});

			btnCancel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});


			dialog.findViewById(R.id.rl1).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
				}
			});


			dialog.findViewById(R.id.rv).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (cancelable) {
						dismissAlertPopup();
					}
				}
			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setReturnOptions() {
		try {
			if (Data.deliveryReturnOptionList != null) {
				for (int i = 0; i < Data.deliveryReturnOptionList.size(); i++) {
					Data.deliveryReturnOptionList.get(i).setChecked(false);
				}
				returnOptionsListAdapter.notifyDataSetChanged();
			} else {
				activity.onBackPressed();
			}
		} catch (Exception e) {
			e.printStackTrace();
			activity.onBackPressed();
		}
	}


	public void dismissAlertPopup(){
		try{
			if(dialog != null && dialog.isShowing()){
				dialog.dismiss();
			}
		}catch(Exception e){
		}
	}

	public void deliveryReturnRequest(final String reason) {
		try {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

				final CustomerInfo customerInfo = Data.getCustomerInfo(String.valueOf(engagementId));

				HashMap<String, String> params = new HashMap<String, String>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_ENGAGEMENT_ID, String.valueOf(customerInfo.getEngagementId()));
				params.put(Constants.KEY_REFERENCE_ID, String.valueOf(customerInfo.getReferenceId()));
				params.put(Constants.KEY_DELIVERY_ID, String.valueOf(deliveryInfo.getId()));
				params.put(Constants.KEY_CANCEL_REASON, reason);
				params.put(Constants.KEY_LATITUDE, String.valueOf(activity.getMyLocation().getLatitude()));
				params.put(Constants.KEY_LONGITUDE, String.valueOf(activity.getMyLocation().getLongitude()));
				params.put("app_version", "" + Data.appVersion);

				final double distance = activity.getCurrentDeliveryDistance(customerInfo);
				final long deliveryTime = activity.getCurrentDeliveryTime(customerInfo);
				final long waitTime = activity.getCurrentDeliveryWaitTime(customerInfo);

				params.put(Constants.KEY_DISTANCE, String.valueOf(distance));
				params.put(Constants.KEY_RIDE_TIME, String.valueOf(deliveryTime/1000l));
				params.put(Constants.KEY_WAIT_TIME, String.valueOf(waitTime/1000l));

				RestClient.getApiServices().cancelDelivery(params, new Callback<RegisterScreenResponse>() {
					@Override
					public void success(RegisterScreenResponse registerScreenResponse, Response response) {
						DialogPopup.dismissLoadingDialog();
						try {
							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
							JSONObject jObj = new JSONObject(jsonString);
							int flag = jObj.optInt(Constants.KEY_FLAG, ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
							String message = JSONParser.getServerMessage(jObj);
							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag, null)) {
								if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
									deliveryInfo.setStatus(DeliveryStatus.CANCELLED.getOrdinal());
									deliveryInfo.setDeliveryValues(distance, deliveryTime, waitTime);
									deliveryInfo.setCancelReason(reason);
//									activity.onBackPressed();
									activity.setDeliveryState(jObj,customerInfo);
									try {
										activity.setNevigationButtonVisibiltyDelivery(deliveryInfo.getIndex() - 1);
									} catch (Exception e) {
										e.printStackTrace();
									}
									if(jObj.optInt("status", 0) == 0) {
										DialogPopup.alertPopupWithListener(activity, "", message,
												new View.OnClickListener() {
													@Override
													public void onClick(View v) {
//													activity.onBackPressed();

													}
												});
									}
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
			} else {
				DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
