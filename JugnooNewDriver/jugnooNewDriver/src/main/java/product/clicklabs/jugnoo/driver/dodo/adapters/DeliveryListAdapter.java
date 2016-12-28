package product.clicklabs.jugnoo.driver.dodo.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryInfo;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryStatus;
import product.clicklabs.jugnoo.driver.dodo.dialog.DeliveryReturnListDialog;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class DeliveryListAdapter extends PagerAdapter {

    private HomeActivity activity;

    private ArrayList<DeliveryInfo> tasksList;
    private LayoutInflater layoutInflater;
	int engagemnetId;

    public DeliveryListAdapter(HomeActivity activity, ArrayList<DeliveryInfo> tasks, int engagemnetId) {

        this.activity = activity;
        this.tasksList = tasks;
		this.engagemnetId = engagemnetId;
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return tasksList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object == view;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View taskItemView = layoutInflater.inflate(R.layout.layout_task_item, container, false);

		LinearLayout linearLayoutDeliveryItem = (LinearLayout) taskItemView.findViewById(R.id.linearLayoutDeliveryItem);
		RelativeLayout linearLayoutDeliveryItemHeader = (RelativeLayout) taskItemView.findViewById(R.id.linearLayoutDeliveryItemHeader);
        TextView textViewCustomerName = (TextView) taskItemView.findViewById(R.id.textViewCustomerName);
        TextView textViewListCount = (TextView) taskItemView.findViewById(R.id.textViewListCount);
        TextView textViewCustomerDeliveryAddress = (TextView) taskItemView.findViewById(R.id.textViewCustomerDeliveryAddress);
		TextView textViewCashCollected = (TextView) taskItemView.findViewById(R.id.textViewCashCollected);
		TextView textViewReturnText = (TextView) taskItemView.findViewById(R.id.textViewReturnText);

		RelativeLayout call = (RelativeLayout) taskItemView.findViewById(R.id.relativeLayoutCall);
		Button buttonMarkDeliver = (Button) taskItemView.findViewById(R.id.buttonMarkDeliver);
		Button buttonMarkReturn = (Button) taskItemView.findViewById(R.id.buttonMarkReturn);
		Button buttonMarkFailed = (Button) taskItemView.findViewById(R.id.buttonMarkFailed);

		RelativeLayout relativeLayoutDeliveredAmnt = (RelativeLayout) taskItemView.findViewById(R.id.relativeLayoutDeliveredAmnt);
		TextView textViewCashCollectedValue = (TextView) taskItemView.findViewById(R.id.textViewCashCollectedValue);

		RelativeLayout relativeLayoutTotalCashCollected = (RelativeLayout) taskItemView.findViewById(R.id.relativeLayoutTotalCashCollected);
		TextView textViewTotalCashCollectedValue = (TextView) taskItemView.findViewById(R.id.textViewTotalCashCollectedValue);

		call.setTag(position);
		buttonMarkDeliver.setTag(position);
		buttonMarkReturn.setTag(position);
		buttonMarkFailed.setTag(position);

		final DeliveryInfo task = tasksList.get(position);

		if(!"".equalsIgnoreCase(task.getCustomerName())) {
			textViewReturnText.setVisibility(View.GONE);
			textViewCustomerName.setVisibility(View.VISIBLE);
			textViewCustomerName.setText(task.getCustomerName().toUpperCase());
		} else {
			textViewReturnText.setVisibility(View.VISIBLE);
			textViewCustomerName.setVisibility(View.GONE);
			textViewReturnText.setText(activity.getResources().getString(R.string.return_to_merchant));
		}

		textViewCustomerDeliveryAddress.setText(task.getDeliveryAddress());
		int totalDeliveries = 0;
		double totalCashCollected = 0;
		for(DeliveryInfo deliveryInfo : tasksList){
			if(deliveryInfo.getStatus() != DeliveryStatus.RETURN.getOrdinal()){
				totalDeliveries++;
			}
			if(deliveryInfo.getStatus() == DeliveryStatus.COMPLETED.getOrdinal()){
				totalCashCollected = totalCashCollected + deliveryInfo.getAmount();
			}
		}
		textViewListCount.setText(position+1 +"/"+totalDeliveries);

		if(task.getAmount() > 0 ){
			textViewCashCollected.setVisibility(View.GONE);
			textViewCashCollected.setText(activity.getResources().getString(R.string.take_cash)
					+ " " + activity.getResources().getString(R.string.rupee)
					+ Utils.getDecimalFormatForMoney().format(task.getAmount()));
		} else {
			textViewCashCollected.setVisibility(View.GONE);
		}

		taskItemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		ASSL.DoMagic(taskItemView);


		relativeLayoutDeliveredAmnt.setVisibility(View.GONE);
		relativeLayoutTotalCashCollected.setVisibility(View.GONE);
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) buttonMarkFailed.getLayoutParams();
		params.setMargins((int)(35f*ASSL.Xscale()), 0, 0, 0);
		if(task.getStatus() != DeliveryStatus.PENDING.getOrdinal()){

			if(task.getStatus() == DeliveryStatus.COMPLETED.getOrdinal()){
				linearLayoutDeliveryItem.setBackgroundColor(activity.getResources().getColor(R.color.grey_light_alpha));
				linearLayoutDeliveryItemHeader.setBackgroundColor(activity.getResources().getColor(R.color.grey_light_alpha));
				call.setBackgroundResource(R.drawable.background_grey_alpha_66_rounded_bordered);
				buttonMarkDeliver.setBackgroundResource(R.drawable.background_grey_alpha_rounded_bordered);
				buttonMarkDeliver.setTextColor(activity.getResources().getColor(R.color.red_v2));
				buttonMarkDeliver.setText(activity.getResources().getString(R.string.delivered));
				buttonMarkDeliver.setEnabled(false);
				call.setEnabled(false);
				buttonMarkFailed.setVisibility(View.GONE);
				buttonMarkReturn.setVisibility(View.GONE);
				relativeLayoutDeliveredAmnt.setVisibility(View.VISIBLE);
				textViewCashCollectedValue.setText(activity.getResources().getString(R.string.rupee)
						+ Utils.getDecimalFormatForMoney().format(task.getAmount()));
				call.setVisibility(View.GONE);
			}
			else if(task.getStatus() == DeliveryStatus.CANCELLED.getOrdinal()) {
				linearLayoutDeliveryItem.setBackgroundColor(activity.getResources().getColor(R.color.grey_light_alpha));
				linearLayoutDeliveryItemHeader.setBackgroundColor(activity.getResources().getColor(R.color.grey_light_alpha));
				call.setBackgroundResource(R.drawable.background_grey_alpha_66_rounded_bordered);
				buttonMarkFailed.setBackgroundResource(R.drawable.background_grey_alpha_rounded_bordered);
				buttonMarkFailed.setTextColor(activity.getResources().getColor(R.color.black_text_v2));
				buttonMarkFailed.setText(activity.getResources().getString(R.string.failed));
				buttonMarkFailed.setEnabled(false);
				params.setMargins(0, 0, 0, 0);
				call.setEnabled(false);
				buttonMarkDeliver.setVisibility(View.GONE);
				buttonMarkReturn.setVisibility(View.GONE);
			}
		}
		buttonMarkFailed.setLayoutParams(params);

		if(task.getStatus() == DeliveryStatus.RETURN.getOrdinal()){
//			linearLayoutDeliveryItemHeader.setBackgroundColor(activity.getResources().getColor(R.color.red_v2));
			textViewCashCollected.setVisibility(View.GONE);
			textViewListCount.setVisibility(View.GONE);
			textViewCashCollected.setTextColor(activity.getResources().getColor(R.color.white));
			call.setVisibility(View.GONE);
			buttonMarkFailed.setVisibility(View.GONE);
			buttonMarkDeliver.setVisibility(View.GONE);
			buttonMarkReturn.setVisibility(View.VISIBLE);
			relativeLayoutTotalCashCollected.setVisibility(View.VISIBLE);
			textViewTotalCashCollectedValue.setText(activity.getResources().getString(R.string.rupee)
					+ Utils.getDecimalFormatForMoney().format(totalCashCollected));
		}



		call.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					int pos = (int)v.getTag();
					Utils.openCallIntent(activity, tasksList.get(pos).getCustomerNo());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		buttonMarkDeliver.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int pos = (int)v.getTag();
				final DeliveryInfo task = tasksList.get(pos);
				DialogPopup.alertPopupDeliveryTwoButtonsWithListeners(activity,
						activity.getResources().getString(R.string.delivery_id) + ": " + task.getId(),
						activity.getResources().getString(R.string.take_cash)
								+ " " + activity.getResources().getString(R.string.rupee)
								+ Utils.getDecimalFormatForMoney().format(task.getAmount()),
						task.getCustomerName(), task.getDeliveryAddress(),
						activity.getResources().getString(R.string.delivery_conf),
						activity.getResources().getString(R.string.deliver),
						activity.getResources().getString(R.string.cancel),
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								markDelivered(String.valueOf(engagemnetId), task);
							}
						},
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {

							}
						}, false, true);
			}
		});

		buttonMarkReturn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int pos = (int)v.getTag();
				final DeliveryInfo task = tasksList.get(pos);
				DialogPopup.alertPopupDeliveryReturnWithListeners(activity,
						activity.getResources().getString(R.string.order_id) + ": " + task.getId(),
						activity.getResources().getString(R.string.deposite_cash_1)
								+ " " + activity.getResources().getString(R.string.rupee)
								+ Utils.getDecimalFormatForMoney().format(task.getAmount()),
						task.getTotalDelivery(), task.getDelSuccess(), task.getDelFail(),
						activity.getResources().getString(R.string.deposite_cash),
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								activity.setDeliveryState(null,Data.getCustomerInfo(String.valueOf(engagemnetId)));
							}
						},
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {

							}
						}, false, true);
			}
		});

		buttonMarkFailed.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DeliveryReturnListDialog deliveryReturnListDialog = new DeliveryReturnListDialog(activity, engagemnetId, task.getId());
				deliveryReturnListDialog.deliveryReturnListDialog(activity,
						activity.getResources().getString(R.string.return_reasons_confirmation),
						activity.getResources().getString(R.string.submit_small),
						activity.getResources().getString(R.string.cancel), true);
			}
		});

        container.addView(taskItemView);

        return taskItemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {

        return POSITION_NONE;
    }

	public void markDelivered(String engagementId, final DeliveryInfo deliveryInfo) {
		try {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

				final CustomerInfo customerInfo = Data.getCustomerInfo(String.valueOf(engagementId));

				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_ENGAGEMENT_ID, String.valueOf(customerInfo.getEngagementId()));
				params.put(Constants.KEY_REFERENCE_ID, String.valueOf(customerInfo.getReferenceId()));
				params.put(Constants.KEY_DELIVERY_ID, String.valueOf(deliveryInfo.getId()));
				params.put(Constants.KEY_LATITUDE, String.valueOf(activity.getMyLocation().getLatitude()));
				params.put(Constants.KEY_LONGITUDE, String.valueOf(activity.getMyLocation().getLongitude()));
				params.put("app_version", "" + Data.appVersion);
				final double distance = activity.getCurrentDeliveryDistance(customerInfo);
				final long deliveryTime = activity.getCurrentDeliveryTime(customerInfo);
				final long waitTime = activity.getCurrentDeliveryWaitTime(customerInfo);

				params.put(Constants.KEY_DISTANCE, String.valueOf(distance));
				params.put(Constants.KEY_RIDE_TIME, String.valueOf(deliveryTime/1000l));
				params.put(Constants.KEY_WAIT_TIME, String.valueOf(waitTime/1000l));

				RestClient.getApiServices().markDelivered(params,
						new Callback<RegisterScreenResponse>() {
							@Override
							public void success(RegisterScreenResponse registerScreenResponse, Response response) {
								DialogPopup.dismissLoadingDialog();
								try {
									String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
									JSONObject jObj = new JSONObject(jsonString);
									int flag = jObj.optInt(Constants.KEY_FLAG, ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
									String message = JSONParser.getServerMessage(jObj);
									if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)) {
										if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
											deliveryInfo.setStatus(DeliveryStatus.COMPLETED.getOrdinal());
											deliveryInfo.setDeliveryValues(distance, deliveryTime, waitTime);
											try {
												activity.setNevigationButtonVisibiltyDelivery(deliveryInfo.getIndex());
											} catch (Exception e) {
												e.printStackTrace();
											}
											activity.setDeliveryState(jObj, customerInfo);
											Toast toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT);
											toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
											toast.show();
											if(jObj.optInt("status", 0) == 0) {
//												DialogPopup.alertPopupWithListener(activity, "", message,
//														new View.OnClickListener() {
//															@Override
//															public void onClick(View v) {
////															activity.onBackPressed();
//															}
//														});
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
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<DeliveryInfo> getTasksList(){
		return tasksList;
	}

}
