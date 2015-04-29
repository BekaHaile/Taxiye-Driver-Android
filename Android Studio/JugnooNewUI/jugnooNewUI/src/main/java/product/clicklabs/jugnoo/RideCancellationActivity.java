package product.clicklabs.jugnoo;

import org.json.JSONObject;

import product.clicklabs.jugnoo.datastructure.ActivityCloser;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.CancelOption;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

public class RideCancellationActivity extends Activity implements ActivityCloser{
	
	
	RelativeLayout relative;
	
	ImageView imageViewBack;
	TextView textViewTitle;

	TextView textViewWantToCancel;
	
	ListView listViewCancelOptions;
	CancelOptionsListAdapter cancelOptionsListAdapter;
	
	Button buttonCancelRide;
	
	TextView textViewCancelInfo;
	
	public static ActivityCloser activityCloser = null;
	
	
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
		HomeActivity.checkForAccessTokenChange(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cancel_ride);
		
		for(int i=0; i<Data.cancelOptionsList.cancelOptions.size(); i++){
			Data.cancelOptionsList.cancelOptions.get(i).checked = false;
		}
		
		
		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(RideCancellationActivity.this, relative, 1134, 720, false);
		
		
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack); 
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Data.latoRegular(this), Typeface.BOLD);

		textViewWantToCancel = (TextView) findViewById(R.id.textViewWantToCancel); textViewWantToCancel.setTypeface(Data.latoLight(this), Typeface.BOLD);
		
		listViewCancelOptions = (ListView) findViewById(R.id.listViewCancelOptions);
		cancelOptionsListAdapter = new CancelOptionsListAdapter(RideCancellationActivity.this);
		listViewCancelOptions.setAdapter(cancelOptionsListAdapter);
		
		buttonCancelRide = (Button) findViewById(R.id.buttonCancelRide); buttonCancelRide.setTypeface(Data.latoRegular(this));
		
		textViewCancelInfo = (TextView) findViewById(R.id.textViewCancelInfo); textViewCancelInfo.setTypeface(Data.latoLight(this), Typeface.BOLD);
		
		imageViewBack.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		
		
		buttonCancelRide.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String cancelReasonsStr = "";
				
				for(int i=0; i<Data.cancelOptionsList.cancelOptions.size(); i++){
					if(Data.cancelOptionsList.cancelOptions.get(i).checked){
						cancelReasonsStr = Data.cancelOptionsList.cancelOptions.get(i).name;
						break;
					}
				}
				
				if("".equalsIgnoreCase(cancelReasonsStr)){
					DialogPopup.alertPopup(RideCancellationActivity.this, "", "Please select one reason");
				}
				else{
					cancelRideAPI(RideCancellationActivity.this, cancelReasonsStr);
				}
				
			}
		});
		
		textViewCancelInfo.setText(Data.cancelOptionsList.message);
		
		
		RideCancellationActivity.activityCloser = this;
		
	}
	
	
	
	public void performBackPressed(){
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
		RideCancellationActivity.activityCloser = null;
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	
	
	
	class ViewHolderCancelOption {
		TextView textViewCancelOption;
		ImageView imageViewCancelOptionCheck;
		LinearLayout relative;
		int id;
	}

	class CancelOptionsListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderCancelOption holder;
		Context context;
		
		public CancelOptionsListAdapter(Context context) {
			this.context = context;
			this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return Data.cancelOptionsList.cancelOptions.size();
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
				holder = new ViewHolderCancelOption();
				convertView = mInflater.inflate(R.layout.list_item_cancel_option, null);
				
				holder.textViewCancelOption = (TextView) convertView.findViewById(R.id.textViewCancelOption); holder.textViewCancelOption.setTypeface(Data.latoRegular(context));
				holder.imageViewCancelOptionCheck = (ImageView) convertView.findViewById(R.id.imageViewCancelOptionCheck);
				
				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative); 
				
				holder.relative.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderCancelOption) convertView.getTag();
			}
			
			holder.id = position;
			
			CancelOption cancelOption = Data.cancelOptionsList.cancelOptions.get(position);

			holder.textViewCancelOption.setText(cancelOption.name);
			
			if(cancelOption.checked){
				holder.relative.setBackgroundColor(Color.WHITE);
				holder.imageViewCancelOptionCheck.setImageResource(R.drawable.check_box_checked_new);
			}
			else{
				holder.relative.setBackgroundColor(Color.TRANSPARENT);
				holder.imageViewCancelOptionCheck.setImageResource(R.drawable.check_box_unchecked_new);
			}
			
			holder.relative.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					holder = (ViewHolderCancelOption) v.getTag();
					for(int i=0; i<Data.cancelOptionsList.cancelOptions.size(); i++){
						if(holder.id == i){
							Data.cancelOptionsList.cancelOptions.get(i).checked = true;
						}
						else{
							Data.cancelOptionsList.cancelOptions.get(i).checked = false;
						}
					}
					notifyDataSetChanged();
				}
			});
			
			return convertView;
		}

	}
	

	public void cancelRideAPI(final Activity activity, final String reasons) {
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
				DialogPopup.showLoadingDialog(activity, "Loading...");
				
				RequestParams params = new RequestParams();
				
				params.put("access_token", Data.userData.accessToken);
				params.put("reasons", reasons);
			
				AsyncHttpClient asyncHttpClient = Data.getClient();
				asyncHttpClient.post(Data.SERVER_URL + "/cancel_ride_by_customer", params,
						new CustomAsyncHttpResponseHandler() {
						private JSONObject jObj;
	
							@Override
							public void onFailure(Throwable arg3) {
								Log.e("request fail", arg3.toString());
								DialogPopup.dismissLoadingDialog();
								DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
							}
							
	
							@Override
							public void onSuccess(String response) {
								Log.i("Server response", "response = " + response);
								try {
									jObj = new JSONObject(response);
									
									if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
										int flag = jObj.getInt("flag");
										if(ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag){
											String error = jObj.getString("error");
											DialogPopup.alertPopup(activity, "", error);
										}
										else if(ApiResponseFlags.RIDE_CANCELLED_BY_CUSTOMER.getOrdinal() == flag){
											String message = jObj.getString("message");
											
											if(jObj.has("jugnoo_balance")){
												Data.userData.jugnooBalance = jObj.getDouble("jugnoo_balance");
											}
											
											if(HomeActivity.appInterruptHandler != null){
												HomeActivity.appInterruptHandler.onCancelCompleted();
											}
											
											DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
												
												@Override
												public void onClick(View v) {
													performBackPressed();
												}
											});
										}
										else{
											DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
										}
									}
									else{
									}
									
								}  catch (Exception exception) {
									exception.printStackTrace();
									DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								}
								DialogPopup.dismissLoadingDialog();
							}
							
							@Override
							public void onFinish() {
								super.onFinish();
							}
							
						});
			}
			else {
				DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}
	}

	@Override
	public void close() {
		performBackPressed();
	}
	
}
