package product.clicklabs.jugnoo;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.json.JSONObject;

import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.PromotionApplyMode;
import product.clicklabs.jugnoo.datastructure.PromotionDialogEventHandler;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

public class PromotionDialog {

	
	private Dialog dialog;
	
	private FrameLayout frameLayout;
	private TextView textViewTitle, textViewMessage, textViewRequestConfirm;
	private Button btnOk, btnCancel, btnOkOnly;
	private ListView listViewPromotions;
	
	private ArrayList<PromoCoupon> promoCouponList;
	private PromotionsListAdapter promotionsListAdapter;
	
	
	private PromoCoupon selectedCoupon;
	
	private LatLng promoLatLng;
	
	private PromotionApplyMode promotionApplyMode = PromotionApplyMode.BEFORE_RIDE;
	
	private double dynamicFactor = 1.0;
	private String pickupId = "";
	
	
	private DecimalFormat decimalFormat = new DecimalFormat("#.#");
	
	public PromotionDialog(LatLng latLng, PromotionApplyMode promotionApplyMode){
		this.promoCouponList = new ArrayList<PromoCoupon>();
		this.selectedCoupon = null;
		this.promoLatLng = latLng;
		this.promotionApplyMode = promotionApplyMode;
	}
	
	
	public void updateList(ArrayList<PromoCoupon> promoCouponList, String pickupId){
		this.promoCouponList.clear();
		this.promoCouponList.addAll(promoCouponList);
		
		this.pickupId = pickupId;
	}
	
	public void showPromoAlert(final Activity activity, final PromotionDialogEventHandler promotionDialogEventHandler) {
		try {
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_ride_promotion);

			frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);

			textViewTitle = (TextView) dialog.findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Data.latoRegular(activity));
			textViewMessage = (TextView) dialog.findViewById(R.id.textViewMessage); textViewMessage.setTypeface(Data.latoRegular(activity));
			textViewRequestConfirm = (TextView) dialog.findViewById(R.id.textViewRequestConfirm); textViewRequestConfirm.setTypeface(Data.latoRegular(activity));

			listViewPromotions = (ListView) dialog.findViewById(R.id.listViewPromotions);
			promotionsListAdapter = new PromotionsListAdapter(activity);
			listViewPromotions.setAdapter(promotionsListAdapter);
			
			btnOk = (Button) dialog.findViewById(R.id.btnOk);
			btnOk.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
			
			btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
			btnCancel.setTypeface(Data.latoRegular(activity));
			
			btnOkOnly = (Button) dialog.findViewById(R.id.btnOkOnly); btnOkOnly.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
					
			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					performBtnOk(activity, promotionDialogEventHandler);
				}
			});
			
			btnCancel.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dismissAlert();
					promotionDialogEventHandler.onCancelPressed();
				}
			});
			
			btnOkOnly.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					performBtnOkOnly(activity, promotionDialogEventHandler);
				}
			});
			
			

			if(PromotionApplyMode.AFTER_SCHEDULE.getOrdinal() == promotionApplyMode.getOrdinal()){
				textViewMessage.setText("Please note that price rates applicable at the scheduled time might be different");
				
				SpannableString sstr = new SpannableString("Choose one for this ride");
				final StyleSpan bss = new StyleSpan(Typeface.BOLD);
				sstr.setSpan(bss, 0, sstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				
				textViewTitle.setText("Ride Scheduled. You have coupons available.\n");
				textViewTitle.append(sstr);
				
				btnOk.setVisibility(View.GONE);
				btnCancel.setVisibility(View.GONE);
				btnOkOnly.setVisibility(View.VISIBLE);
				
			}
			else{
				
				if(dynamicFactor > 1){
					textViewMessage.setVisibility(View.VISIBLE);
					textViewMessage.setText("Current rates are "+decimalFormat.format(dynamicFactor)
							+"x higher than normal to maintain availability");
				}
				else if(dynamicFactor < 1){
					textViewMessage.setVisibility(View.VISIBLE);
					textViewMessage.setText("Current rates are "+decimalFormat.format(dynamicFactor)
							+"x lower than normal to maintain availability");
				}
				else{
					textViewMessage.setVisibility(View.GONE);
				}
				
				
				textViewTitle.setText("You have coupons available.\nChoose one");
				
				btnOk.setVisibility(View.VISIBLE);
				btnCancel.setVisibility(View.VISIBLE);
				btnOkOnly.setVisibility(View.GONE);
			}
			
			if(promoCouponList.size() > 0){
				promoCouponList.add(new CouponInfo(-1, "Don't apply coupon on this ride"));
				selectedCoupon = promoCouponList.get(0);
				
				listViewPromotions.setVisibility(View.VISIBLE);
				textViewRequestConfirm.setVisibility(View.GONE);
				
				dialog.show();
				
				startDismissHandler();
				
				promotionsListAdapter.notifyDataSetChanged();
			}
			else{
				selectedCoupon = new CouponInfo(0, "");
				
				listViewPromotions.setVisibility(View.GONE);
				textViewRequestConfirm.setVisibility(View.VISIBLE);
				
				textViewTitle.setText("Chalo Jugnoo Se");
				
				if(PromotionApplyMode.AFTER_SCHEDULE.getOrdinal() == promotionApplyMode.getOrdinal()){
					performBtnOkOnly(activity, promotionDialogEventHandler);
				}
				else{
					performBtnOk(activity, promotionDialogEventHandler);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void performBtnOk(Activity activity, PromotionDialogEventHandler promotionDialogEventHandler){
		if(promoCouponList.size() == 0){
			selectedCoupon = new CouponInfo(0, "");
		}
		if(selectedCoupon != null){
			dismissAlert();
			promotionDialogEventHandler.onOkPressed(selectedCoupon, promoCouponList.size());
		}
		else{
			Toast.makeText(activity, "Please select some coupon first", Toast.LENGTH_LONG).show();
		}
	}
	
	public void performBtnOkOnly(Activity activity, PromotionDialogEventHandler promotionDialogEventHandler){
		if(promoCouponList.size() == 0){
			selectedCoupon = new CouponInfo(0, "");
		}
		if(selectedCoupon != null){
			promotionDialogEventHandler.onOkOnlyPressed(PromotionDialog.this, selectedCoupon, pickupId);
		}
		else{
			Toast.makeText(activity, "Please select some coupon first", Toast.LENGTH_LONG).show();
		}
	}
	
	
	Handler dismissHandler;
	Runnable dismissRunnable;
	public void startDismissHandler(){
		stopDismissHandler();
		try {
			dismissHandler = new Handler();
			dismissRunnable = new Runnable() {
				
				@Override
				public void run() {
					try {dismissAlert();} catch (Exception e) {e.printStackTrace();}
				}
			};
			dismissHandler.postDelayed(dismissRunnable, 2 * 60000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void stopDismissHandler(){
		try{
			if(dismissHandler != null && dismissRunnable != null){
				dismissHandler.removeCallbacks(dismissRunnable);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	public void dismissAlert(){
		try{
			if(dialog != null){
				dialog.dismiss();
			}
			stopDismissHandler();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	
	class ViewHolderPromotion {
		TextView textViewCouponTitle, textViewTNC;
		LinearLayout relative;
		int id;
	}

	class PromotionsListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderPromotion holder;
		Activity context;
		public PromotionsListAdapter(Activity context) {
			this.context = context;
			this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return promoCouponList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				holder = new ViewHolderPromotion();
				convertView = mInflater.inflate(R.layout.list_item_promo_coupon, null);
				
				holder.textViewCouponTitle = (TextView) convertView.findViewById(R.id.textViewCouponTitle); holder.textViewCouponTitle.setTypeface(Data.latoRegular(context));
				holder.textViewTNC = (TextView) convertView.findViewById(R.id.textViewTNC); holder.textViewTNC.setTypeface(Data.latoLight(context), Typeface.BOLD);
				
				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative); 
				
				holder.relative.setTag(holder);
				holder.textViewTNC.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(656, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderPromotion) convertView.getTag();
			}
			
			holder.id = position;
			
			PromoCoupon promoCoupon = promoCouponList.get(position);

			if(promoCoupon instanceof CouponInfo){
				holder.textViewCouponTitle.setText(((CouponInfo)promoCoupon).title);
			}
			else if(promoCoupon instanceof PromotionInfo){
				holder.textViewCouponTitle.setText(((PromotionInfo)promoCoupon).title);
			}
			
			if(selectedCoupon.id == promoCoupon.id){
				holder.relative.setBackgroundColor(context.getResources().getColor(R.color.yellow));
				holder.textViewCouponTitle.setTextColor(context.getResources().getColor(R.color.white));
				holder.textViewTNC.setTextColor(context.getResources().getColor(R.color.white));
			}
			else{
				holder.relative.setBackgroundColor(context.getResources().getColor(R.color.white));
				holder.textViewCouponTitle.setTextColor(context.getResources().getColor(R.color.grey_dark_less));
				holder.textViewTNC.setTextColor(context.getResources().getColor(R.color.grey_dark_less));
			}
			
			if(promoCoupon.id > -1){
				holder.textViewTNC.setVisibility(View.VISIBLE);
			}
			else{
				holder.textViewTNC.setVisibility(View.GONE);
			}
			
			
			
			holder.relative.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					holder = (ViewHolderPromotion) v.getTag();
					selectedCoupon = promoCouponList.get(holder.id);
					notifyDataSetChanged();
				}
			});
			
			holder.textViewTNC.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					holder = (ViewHolderPromotion) v.getTag();
					PromoCoupon promoCoupon = promoCouponList.get(holder.id);
					if(promoCoupon instanceof CouponInfo){
						DialogPopup.alertPopupLeftOriented(context, "", ((CouponInfo)promoCoupon).description);
					}
					else if(promoCoupon instanceof PromotionInfo){
						if(((PromotionInfo)promoCoupon).id > 0){
							DialogPopup.alertPopupHtml(context, "", ((PromotionInfo)promoCoupon).terms);
						}
					}
				}
			});
			
			return convertView;
		}

	}
	
	
	
	
	public void fetchPromotionsAPI(final Activity activity, final PromotionDialogEventHandler promotionDialogEventHandler) {
		if (AppStatus.getInstance(activity).isOnline(activity)) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
			
			params.put("access_token", Data.userData.accessToken);
			params.put("latitude", ""+promoLatLng.latitude);
			params.put("longitude", ""+promoLatLng.longitude);

			Log.i("params", "=" + params);
			
			AsyncHttpClient fetchPromotionClient = Data.getClient();
			fetchPromotionClient.post(Data.SERVER_URL + "/show_available_promotions", params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;
					
						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
							DialogPopup.dismissLoadingDialog();
						}
						
						@Override
						public void onSuccess(String response) {
							Log.e("Server response show_available_promotions", "response = " + response);
							
							try {
								jObj = new JSONObject(response);
								
								if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
									int flag = jObj.getInt("flag");
									if(ApiResponseFlags.AVAILABLE_PROMOTIONS.getOrdinal() == flag){
										
										promoCouponList.clear();
										promoCouponList.addAll(JSONParser.parsePromoCoupons(jObj));
										
										dynamicFactor = jObj.getDouble("dynamic_factor");
										
										showPromoAlert(activity, promotionDialogEventHandler);
										
									}
									else{
										DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
									}
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
							
							DialogPopup.dismissLoadingDialog();
						}
						
					});
		}
	}
	
	
}
