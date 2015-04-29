package product.clicklabs.jugnoo;

import org.json.JSONObject;

import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.FeedbackMode;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.Log;
import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

public class FeedbackActivity extends Activity {

	RelativeLayout relative;
	
	TextView textViewTitle;
	ImageView imageViewBack;
	
	RatingBar ratingBarFeedback;
	EditText editTextFeedback;
	Button buttonSubmitFeedback;
	
	RelativeLayout relativeLayoutSkip;
	TextView textViewSkip;
	
	ScrollView scrollView;
	TextView textViewScroll;
	
	FeedbackMode feedbackMode = FeedbackMode.SUPPORT;
	
	@Override
	protected void onResume() {
		super.onResume();
		HomeActivity.checkForAccessTokenChange(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		
		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(this, (ViewGroup) relative, 1134, 720, false);
		
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
		
		ratingBarFeedback = (RatingBar) findViewById(R.id.ratingBarFeedback);
		ratingBarFeedback.setRating(0);
		
		editTextFeedback = (EditText) findViewById(R.id.editTextFeedback); editTextFeedback.setTypeface(Data.latoRegular(this));
		buttonSubmitFeedback = (Button) findViewById(R.id.buttonSubmitFeedback); buttonSubmitFeedback.setTypeface(Data.latoRegular(this));
		
		relativeLayoutSkip = (RelativeLayout) findViewById(R.id.relativeLayoutSkip);
		textViewSkip = (TextView) findViewById(R.id.textViewSkip); textViewSkip.setTypeface(Data.latoRegular(this));
		
		scrollView = (ScrollView) findViewById(R.id.scrollView);
		textViewScroll = (TextView) findViewById(R.id.textViewScroll);
		
		
		imageViewBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		
		
		buttonSubmitFeedback.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String feedbackStr = editTextFeedback.getText().toString().trim();
				int rating = (int) ratingBarFeedback.getRating();
				rating = Math.abs(rating);
				Log.e("rating screen =", "= feedbackStr = "+feedbackStr+" , rating = "+rating);
				
				if("".equalsIgnoreCase(feedbackStr) && 0 == rating){
					editTextFeedback.requestFocus();
					editTextFeedback.setError("Please enter some feedback");
				}
				else{
					if(feedbackStr.length() > 300){
						editTextFeedback.requestFocus();
						editTextFeedback.setError("Review must be in 300 letters.");
					}
					else{
						if(FeedbackMode.AFTER_RIDE == feedbackMode){
							submitFeedbackToDriverAsync(FeedbackActivity.this, Data.cEngagementId, Data.cDriverId, rating, feedbackStr);
							FlurryEventLogger.reviewSubmitted(Data.userData.accessToken, Data.cEngagementId);
						}
						else{
							submitFeedbackSupportAsync(FeedbackActivity.this, rating, feedbackStr);
						}
					}
				}
				
			}
		});
		
		relativeLayoutSkip.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				skipFeedbackForCustomerAsync(FeedbackActivity.this, Data.cEngagementId);
			}
		});
		
		
		try {
			if(getIntent().hasExtra(FeedbackMode.class.getName())){
				int mode = getIntent().getIntExtra(FeedbackMode.class.getName(), feedbackMode.getOrdinal());
				if(FeedbackMode.AFTER_RIDE.getOrdinal() == mode){
					feedbackMode = FeedbackMode.AFTER_RIDE;
				}
				else{
					feedbackMode = FeedbackMode.SUPPORT;
				}
			}
			else{
				performBackPressed();
			}
		} catch (Exception e) {
			e.printStackTrace();
			performBackPressed();
		}
		
		
		if(FeedbackMode.AFTER_RIDE == feedbackMode){
			textViewTitle.setText("RATE YOUR EXPERIENCE");
			relativeLayoutSkip.setVisibility(View.VISIBLE);
		}
		else{
			textViewTitle.setText("FEEDBACK");
			relativeLayoutSkip.setVisibility(View.GONE);
		}
		
		
		
		final View activityRootView = findViewById(R.id.linearLayoutMain);
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						Rect r = new Rect();
						// r will be populated with the coordinates of your view
						// that area still visible.
						activityRootView.getWindowVisibleDisplayFrame(r);

						int heightDiff = activityRootView.getRootView()
								.getHeight() - (r.bottom - r.top);
						if (heightDiff > 100) { // if more than 100 pixels, its
												// probably a keyboard...

							/************** Adapter for the parent List *************/

							ViewGroup.LayoutParams params_12 = textViewScroll
									.getLayoutParams();

							params_12.height = (int)(heightDiff);

							textViewScroll.setLayoutParams(params_12);
							textViewScroll.requestLayout();

						} else {

							ViewGroup.LayoutParams params = textViewScroll
									.getLayoutParams();
							params.height = 0;
							textViewScroll.setLayoutParams(params);
							textViewScroll.requestLayout();

						}
					}
				});
		
		
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
	}

	
	public void performBackPressed(){
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	
	@Override
	public void onBackPressed() {
		performBackPressed();
	}
	
	
	@Override
	protected void onDestroy() {
        ASSL.closeActivity(relative);
        System.gc();
		super.onDestroy();
	}
	
	
	
	
	public void submitFeedbackToDriverAsync(final Activity activity, String engagementId, String ratingReceiverId, final int givenRating, String feedbackText) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
			
			params.put("access_token", Data.userData.accessToken);
			params.put("given_rating", ""+givenRating);
			params.put("engagement_id", engagementId);
			params.put("driver_id", ratingReceiverId);
			params.put("feedback", feedbackText);

			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("given_rating", ""+givenRating);
			Log.i("engagement_id", engagementId);
			Log.i("driver_id", ratingReceiverId);
			Log.i("feedback", feedbackText);
			
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/rate_the_driver", params,
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
								int flag = jObj.getInt("flag");
								if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
									if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
										Toast.makeText(activity, "Thank you for the feedback.", Toast.LENGTH_SHORT).show();
										HomeActivity.appInterruptHandler.onAfterRideFeedbackSubmitted(givenRating);
										finish();
										overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
		else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}
	}
	
	
	
	public void skipFeedbackForCustomerAsync(final Activity activity, String engagementId) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
			
			params.put("access_token", Data.userData.accessToken);
			params.put("engagement_id", engagementId);

			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("engagement_id", engagementId);
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/skip_rating_by_customer", params,
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
								int flag = jObj.getInt("flag");
								if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
									if(ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag){
										String error = jObj.getString("error");
										DialogPopup.alertPopup(activity, "", error);
									}
									else if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
										HomeActivity.appInterruptHandler.onAfterRideFeedbackSubmitted(0);
										finish();
										overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
		else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}
	}
	
	
	
	public void submitFeedbackSupportAsync(final Activity activity, int givenRating, String feedbackText) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
			
			params.put("access_token", Data.userData.accessToken);
			params.put("given_rating", ""+givenRating);
			params.put("feedback", feedbackText);

			Log.i("params", "=" + params);
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/submit_feedback", params,
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
								int flag = jObj.getInt("flag");
								if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
									if(ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag){
										String error = jObj.getString("error");
										DialogPopup.alertPopup(activity, "", error);
									}
									else if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
										Toast.makeText(activity, "Thank you for the feedback.", Toast.LENGTH_SHORT).show();
										performBackPressed();
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
		else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}
	}
	
	
}
