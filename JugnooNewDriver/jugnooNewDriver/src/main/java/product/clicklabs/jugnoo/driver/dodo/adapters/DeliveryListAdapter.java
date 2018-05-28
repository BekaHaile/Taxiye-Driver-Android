package product.clicklabs.jugnoo.driver.dodo.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import product.clicklabs.jugnoo.driver.HomeUtil;
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
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class DeliveryListAdapter extends PagerAdapter {

    private HomeActivity activity;

    private ArrayList<DeliveryInfo> tasksList;
    private LayoutInflater layoutInflater;
	int engagemnetId, falseDeliveries, orderId;
	boolean currentStatus = true;

    public DeliveryListAdapter(HomeActivity activity, ArrayList<DeliveryInfo> tasks, int engagemnetId, int falseDeliveries, int orderId) {

        this.activity = activity;
        this.tasksList = tasks;
		this.engagemnetId = engagemnetId;
		this.falseDeliveries = falseDeliveries;
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.orderId = orderId;
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
    public Object instantiateItem(ViewGroup container, final int position) {

        View taskItemView = layoutInflater.inflate(R.layout.layout_task_item, container, false);

		LinearLayout linearLayoutDeliveryItem = (LinearLayout) taskItemView.findViewById(R.id.linearLayoutDeliveryItem);
		final LinearLayout linearLayoutProgress = (LinearLayout) taskItemView.findViewById(R.id.linearLayoutProgress);
		LinearLayout linearLayoutDeliveryData = (LinearLayout) taskItemView.findViewById(R.id.linearLayoutDeliveryData);

		final CardView cvTask = (CardView) taskItemView.findViewById(R.id.cvTask);

		RelativeLayout linearLayoutDeliveryItemHeader = (RelativeLayout) taskItemView.findViewById(R.id.linearLayoutDeliveryItemHeader);
        RelativeLayout relativeLayoutLoading = (RelativeLayout) taskItemView.findViewById(R.id.relativeLayoutLoading);

		TextView textViewCustomerName = (TextView) taskItemView.findViewById(R.id.textViewCustomerName);
        TextView textViewListCount = (TextView) taskItemView.findViewById(R.id.textViewListCount);
        TextView textViewCustomerDeliveryAddress = (TextView) taskItemView.findViewById(R.id.textViewCustomerDeliveryAddress);
		TextView textViewCashCollected = (TextView) taskItemView.findViewById(R.id.textViewCashCollected);
		textViewCashCollected.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		TextView textViewReturnText = (TextView) taskItemView.findViewById(R.id.textViewReturnText);

		RelativeLayout call = (RelativeLayout) taskItemView.findViewById(R.id.relativeLayoutCall);
		final RelativeLayout relativelayoutProgressInfo = (RelativeLayout) taskItemView.findViewById(R.id.relativelayoutProgressInfo);
		TextView textViewDeliveryText = (TextView) taskItemView.findViewById(R.id.textViewDeliveryText);
		textViewDeliveryText.setTypeface(Fonts.mavenRegular(activity));

		Button buttonMarkDeliver = (Button) taskItemView.findViewById(R.id.buttonMarkDeliver);
		Button buttonMarkReturn = (Button) taskItemView.findViewById(R.id.buttonMarkReturn);
		Button buttonMarkFailed = (Button) taskItemView.findViewById(R.id.buttonMarkFailed);
		ImageView imageViewSeprator = (ImageView) taskItemView.findViewById(R.id.imageViewSeprator);

		call.setTag(position);
		buttonMarkDeliver.setTag(position);
		buttonMarkReturn.setTag(position);
		buttonMarkFailed.setTag(position);
		relativelayoutProgressInfo.setVisibility(View.GONE);
		linearLayoutProgress.setVisibility(View.GONE);

		final DeliveryInfo task = tasksList.get(position);

		if(!"".equalsIgnoreCase(task.getCustomerName())) {
			textViewReturnText.setVisibility(View.GONE);
			textViewCustomerName.setVisibility(View.VISIBLE);
			textViewCustomerName.setText(task.getCustomerName().toUpperCase());
		} else if(task.getStatus() == DeliveryStatus.RETURN.getOrdinal()) {
			textViewReturnText.setVisibility(View.VISIBLE);
			textViewCustomerName.setVisibility(View.GONE);
			textViewReturnText.setText(activity.getResources().getString(R.string.return_to_merchant));
		} else {
			textViewReturnText.setVisibility(View.GONE);
			textViewCustomerName.setVisibility(View.VISIBLE);
			textViewCustomerName.setText(activity.getResources().getString(R.string.NA));
		}

		String address = task.getDeliveryAddress();
		int sepratorIndex = address.indexOf(":::");
		String onlyAddress = "";
		if(sepratorIndex != -1) {
			onlyAddress = address.substring(0, sepratorIndex);
		} else {
			onlyAddress = address;
		}

		textViewCustomerDeliveryAddress.setText(onlyAddress);
		int totalDeliveries = 0;
		double totalCashCollected = 0;
		String totalCashCollectedCurrency   = "";
		for(DeliveryInfo deliveryInfo : tasksList){
			if(deliveryInfo.getStatus() != DeliveryStatus.RETURN.getOrdinal()){
				totalDeliveries++;
			}
			if(deliveryInfo.getStatus() == DeliveryStatus.COMPLETED.getOrdinal()){
				totalCashCollected = totalCashCollected + deliveryInfo.getAmount();
				totalCashCollectedCurrency = deliveryInfo.getCurrency();
			}
		}

		textViewListCount.setText(ordinal(position+1)+" "+ activity.getResources().getString(R.string.delivery)+":");

		if(task.getAmount() > 0 ){
			textViewCashCollected.setVisibility(View.VISIBLE);
			textViewCashCollected.setText(activity.getResources().getString(R.string.cash_to_collected)
					+ ": " + Utils.formatCurrencyValue(task.getCurrency(),task.getAmount()));
		} else {
			textViewCashCollected.setVisibility(View.GONE);
		}

		if(task.getLoadUnload() == 1){
			relativeLayoutLoading.setVisibility(View.VISIBLE);
		} else {
			relativeLayoutLoading.setVisibility(View.GONE);
		}

		taskItemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		ASSL.DoMagic(taskItemView);


		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) buttonMarkFailed.getLayoutParams();
		params.setMargins((int)(30f*ASSL.Xscale()), 0, 0, 0);
		params.setMarginStart((int)(30f*ASSL.Xscale()));
		params.setMarginEnd(0);
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
				textViewCashCollected.setText(activity.getResources().getString(R.string.cash_collected)+": "+Utils.formatCurrencyValue(task.getCurrency(),task.getAmount()));
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
				params.setMarginStart(0);
				params.setMarginEnd(0);
				call.setEnabled(false);
				buttonMarkDeliver.setVisibility(View.GONE);
				buttonMarkReturn.setVisibility(View.GONE);
			}
		}
		buttonMarkFailed.setLayoutParams(params);

		if(task.getStatus() == DeliveryStatus.RETURN.getOrdinal()){
//			linearLayoutDeliveryItemHeader.setBackgroundColor(activity.getResources().getColor(R.color.red_v2));
			textViewCashCollected.setVisibility(View.VISIBLE);
			textViewListCount.setVisibility(View.GONE);
			call.setVisibility(View.GONE);
			buttonMarkFailed.setVisibility(View.GONE);
			buttonMarkDeliver.setVisibility(View.GONE);
			buttonMarkReturn.setVisibility(View.VISIBLE);
			textViewCashCollected.setText(Utils.formatCurrencyValue(totalCashCollectedCurrency,totalCashCollected));
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
				if(falseDeliveries == 1){

					DialogPopup.alertPopupTwoButtonsWithListeners(activity,"",
							activity.getResources().getString(R.string.delivery_conf_new),
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
							}, false, false);

				} else {
					final DeliveryInfo task = tasksList.get(pos);
					String address = task.getDeliveryAddress();
					int sepratorIndex = address.indexOf(":::");
					String onlyAddress = "", itemDetails = "-1";
					if(sepratorIndex != -1) {
						onlyAddress = address.substring(0, sepratorIndex);
						itemDetails = address.substring(sepratorIndex + 3, address.length());
						itemDetails = itemDetails.replace("\\n", "\n");
					} else {
						onlyAddress = address;
					}
					DialogPopup.alertPopupDeliveryTwoButtonsWithListeners(activity,
							activity.getResources().getString(R.string.delivery_id) + ": " + Math.abs(orderId),
							activity.getResources().getString(R.string.take_cash)
									+ " " +Utils.formatCurrencyValue(task.getCurrency(),task.getAmount()),
							task.getCustomerName(), onlyAddress, itemDetails,
							activity.getResources().getString(R.string.delivery_conf_new),
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
								+  Utils.formatCurrencyValue(task.getCurrency(),task.getAmount()),
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


		activity.buttonDriverNavigationSetVisibility(View.VISIBLE);
		if(falseDeliveries == 1){
			linearLayoutProgress.setVisibility(View.VISIBLE);
			linearLayoutDeliveryItemHeader.setVisibility(View.GONE);
			linearLayoutDeliveryData.setVisibility(View.GONE);
			imageViewSeprator.setVisibility(View.GONE);
			relativelayoutProgressInfo.setVisibility(View.VISIBLE);
			activity.buttonDriverNavigationSetVisibility(View.GONE);
			final ImageView ivCircleCurrent = new ImageView(activity);
			final int i = tasksList.size();
			int j = 0;

			for(DeliveryInfo deliveryInfo : tasksList) {
				j++;
				if (deliveryInfo.getStatus() == DeliveryStatus.COMPLETED.getOrdinal() ||
						deliveryInfo.getStatus() == DeliveryStatus.CANCELLED.getOrdinal()) {

					ImageView ivCircle = new ImageView(activity);
					ivCircle.setImageResource(R.drawable.circle_orange);
					LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(14, 14);
//					params.weight = 1f;
					linearLayoutProgress.addView(ivCircle, params2);

					ImageView ivLine = new ImageView(activity);
					ivLine.setImageResource(R.color.red_v2);
					LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 2);
					params1.weight = 1f;
					linearLayoutProgress.addView(ivLine, params1);
					currentStatus = true;

				} else {

					if(currentStatus){
						currentStatus = false;
						ivCircleCurrent.setImageResource(R.drawable.circle_orange);
						LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(14, 14);
//						params.weight = 1f;
						linearLayoutProgress.addView(ivCircleCurrent, params2);
						if(deliveryInfo.getStatus() == DeliveryStatus.RETURN.getOrdinal()){
							linearLayoutProgress.setVisibility(View.GONE);
							linearLayoutDeliveryItemHeader.setVisibility(View.VISIBLE);
							linearLayoutDeliveryData.setVisibility(View.VISIBLE);
							imageViewSeprator.setVisibility(View.VISIBLE);
							relativelayoutProgressInfo.setVisibility(View.GONE);
						} else {
							textViewDeliveryText.setText(activity.getResources().getString(R.string.delivery)+" #"+j);
						}

						final int finalJ = j;
						new android.os.Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								LinearLayout.LayoutParams rp = (LinearLayout.LayoutParams) relativelayoutProgressInfo.getLayoutParams();

								int width =  (linearLayoutProgress.getWidth() / i)* (finalJ -1);
								Log.e("doori4", String.valueOf(linearLayoutProgress.getWidth() +" i="+i+" j="+finalJ));
								Log.e("doori3", String.valueOf(width));
								rp.setMarginStart(width + 4);
								relativelayoutProgressInfo.setLayoutParams(rp);
							}
						}, 200);

					} else {
						ImageView ivCircle = new ImageView(activity);
						ivCircle.setImageResource(R.drawable.circle_grey);
						LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(14, 14);
//						params.weight = 1f;
						linearLayoutProgress.addView(ivCircle, params2);
					}

					ImageView ivLine = new ImageView(activity);
					ivLine.setImageResource(R.color.white_grey_v2);
					LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 2);
					params1.weight = 1f;
					linearLayoutProgress.addView(ivLine, params1);
				}


			}

			ImageView ivCircle = new ImageView(activity);
			ivCircle.setImageResource(R.drawable.circle_grey);
			LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(14, 14);
//			params.weight = 1f;
			linearLayoutProgress.addView(ivCircle, params2);

		}

		if(task.getStatus() == DeliveryStatus.RETURN.getOrdinal()) {
			new android.os.Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					activity.buttonDriverNavigationSetVisibility(View.VISIBLE);
				}
			}, 200);

		}


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
				HomeUtil.putDefaultParams(params);
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
			} else {
				DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<DeliveryInfo> getTasksList(){
		return tasksList;
	}

	private int getRelativeLeft(View myView) {
		if (myView.getParent() == myView.getRootView())
			return myView.getLeft();
		else
			return myView.getLeft() + getRelativeLeft((View) myView.getParent());
	}

	public static String ordinal(int i) {
		String[] sufixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
		switch (i % 100) {
			case 11:
			case 12:
			case 13:
				return i + "th";
			default:
				return i + sufixes[i % 10];

		}
	}

}
