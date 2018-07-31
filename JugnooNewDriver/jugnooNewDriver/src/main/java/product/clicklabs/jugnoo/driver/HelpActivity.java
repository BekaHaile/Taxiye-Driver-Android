package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;



import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.HelpSection;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.BookingHistoryResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class HelpActivity extends BaseFragmentActivity implements FlurryEventNames {
	
	
	RelativeLayout relative;
	
	View backBtn;
	TextView title;
	
	ListView listViewHelp;
	RelativeLayout helpExpandedRl;
	
	ProgressBar progressBarHelp;
	TextView textViewInfoDisplay;
	WebView helpWebview;
	
	HelpListAdapter helpListAdapter;
	
	ArrayList<HelpSection> helpSections = new ArrayList<HelpSection>();
	HelpSection selectedHelpSection;
	private LinearLayout layoutConfirmation;
	private String countryCode;

	// *****************************Used for flurry work***************//
	@Override
	protected void onStart() {
		super.onStart();


	}

	@Override
	protected void onStop() {
		super.onStop();

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		
		relative = (RelativeLayout) findViewById(R.id.relative);
		layoutConfirmation = (LinearLayout)findViewById(R.id.layout_confirmation);
		new ASSL(HelpActivity.this, relative, 1134, 720, false);
		
		
		backBtn = findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title); title.setTypeface(Fonts.mavenRegular(getApplicationContext()));

		listViewHelp = (ListView) findViewById(R.id.listViewHelp);
		helpListAdapter = new HelpListAdapter();
		//listViewHelp.setAdapter(helpListAdapter);
		
		helpExpandedRl = (RelativeLayout) findViewById(R.id.helpExpandedRl);
		helpExpandedRl.setVisibility(View.VISIBLE);
		
		
		progressBarHelp = (ProgressBar) findViewById(R.id.progressBarHelp);
		textViewInfoDisplay = (TextView) findViewById(R.id.textViewInfoDisplay); textViewInfoDisplay.setTypeface(Fonts.mavenRegular(getApplicationContext()));
		helpWebview = (WebView) findViewById(R.id.helpWebview);


		
		backBtn.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});



		textViewInfoDisplay.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (selectedHelpSection != null) {
					getHelpAsync(HelpActivity.this, selectedHelpSection, false);
				}
			}
		});
		
		
		helpSections.clear();
		helpSections.add(HelpSection.MAIL_US);
		helpSections.add(HelpSection.CALL_US);
		helpSections.add(HelpSection.FAQ);
		helpSections.add(HelpSection.ABOUT);
		helpSections.add(HelpSection.TERMS);
		helpSections.add(HelpSection.PRIVACY);
		
		
		//helpListAdapter.notifyDataSetChanged();

		if(Data.userData!=null){
			countryCode = Data.userData.getCountryCode();

		}

		if(getIntent().getExtras()!=null && getIntent().hasExtra(Constants.ASK_USER_CONFIRMATION)){
			findViewById(R.id.btn_agree).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					agreeTermsAndSwitchToHome();
				}
			});
			findViewById(R.id.btn_disagree).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					setResult(Activity.RESULT_CANCELED,intent);
					finish();
				}
			});
			getHelpAsync(HelpActivity.this, helpSections.get(4), true);


		}else{
			getHelpAsync(HelpActivity.this, helpSections.get(4), false);

		}

		title.setText(getResources().getString(R.string.terms));

	}

	private void agreeTermsAndSwitchToHome() {
		if(!AppStatus.getInstance(HelpActivity.this).isOnline(HelpActivity.this))
			return;

		DialogPopup.showLoadingDialog(HelpActivity.this,getString(R.string.loading));
		HashMap<String,String> params = new HashMap<>();
		params.put("access_token",Data.userData.accessToken);
		params.put("i_agree",String.valueOf(1));
		HomeUtil.putDefaultParams(params);
		RestClient.getApiServices().agreeTerms(params, new Callback<SettleUserDebt>() {
			@Override
			public void success(SettleUserDebt registerScreenResponse, Response response) {

				if(HelpActivity.this.isFinishing())
					return;

				DialogPopup.dismissLoadingDialog();

				if(registerScreenResponse.getFlag()== ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
					Intent intent = new Intent();
					setResult(Activity.RESULT_OK,intent);
					finish();
				}else{
					DialogPopup.alertPopup(HelpActivity.this,"",registerScreenResponse.getMessage());
				}
			}

			@Override
			public void failure(RetrofitError error) {
				DialogPopup.dismissLoadingDialog();

				DialogPopup.alertPopup(HelpActivity.this,"",getString(R.string.some_error_occured));
			}
		});
	}


	public void openHelpData(HelpSection helpSection, String data, boolean errorOccured, boolean showConfirmationViewOnLoad) {
		if (errorOccured) {
			textViewInfoDisplay.setVisibility(View.VISIBLE);
			textViewInfoDisplay.setText(data);
			helpWebview.setVisibility(View.GONE);
		} else {
			textViewInfoDisplay.setVisibility(View.GONE);
			helpWebview.setVisibility(View.VISIBLE);
			loadHTMLContent(data,showConfirmationViewOnLoad);
		}
		selectedHelpSection = helpSection;
		title.setText(getResources().getString(R.string.terms));

	}
	
	public void loadHTMLContent(String data, final boolean showConfirmationViewOnLoad){
		final String mimeType = "text/html";
        final String encoding = "UTF-8";
        helpWebview.loadDataWithBaseURL("", data, mimeType, encoding, "");
        helpWebview.setWebViewClient(new WebViewClient(){

			@Override
			public void onPageFinished(WebView view, String url) {
				if(view!=null && !HelpActivity.this.isFinishing() && showConfirmationViewOnLoad){
					layoutConfirmation.setVisibility(View.VISIBLE);
				}
			}
		});
	}
	
	
	class ViewHolderHelp {
		TextView name;
		LinearLayout relative;
		int id;
	}

	class HelpListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderHelp holder;

		public HelpListAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return helpSections.size();
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
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				holder = new ViewHolderHelp();
				convertView = mInflater.inflate(R.layout.list_item_help, null);
				
				holder.name = (TextView) convertView.findViewById(R.id.name); holder.name.setTypeface(Fonts.mavenRegular(getApplicationContext()));
				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative); 
				
				holder.relative.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderHelp) convertView.getTag();
			}
			
			holder.id = position;
			
			holder.name.setText(helpSections.get(position).getName(HelpActivity.this));
			
			holder.relative.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					try {
						holder = (ViewHolderHelp) v.getTag();

						switch(helpSections.get(holder.id)){
							case MAIL_US:
								openMailIntentToSupport();
								FlurryEventLogger.event(SEND_US_AN_EMAIL);
								break;

							case CALL_US:
								Utils.openCallIntent(HelpActivity.this, Data.userData.driverSupportNumber);
								FlurryEventLogger.event(CALL_US);
								break;

							case FAQ:
								getHelpAsync(HelpActivity.this, helpSections.get(holder.id), false);
								FlurryEventLogger.event(FAQS);
								break;

							case ABOUT:
								getHelpAsync(HelpActivity.this, helpSections.get(holder.id), false);
								FlurryEventLogger.event(ABOUT_JUGNOO);
								break;

							case TERMS:
								getHelpAsync(HelpActivity.this, helpSections.get(holder.id), false);
								FlurryEventLogger.event(TERMS_OF_USE);
								break;

							case PRIVACY:
								getHelpAsync(HelpActivity.this, helpSections.get(holder.id), false);
								FlurryEventLogger.event(PRIVACY_POLICY);
								break;

							default:
								getHelpAsync(HelpActivity.this, helpSections.get(holder.id), false);
								FlurryEventLogger.event(helpSections.get(holder.id).getName(HelpActivity.this));


						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			return convertView;
		}

	}
	
	
	
	public void openMailIntentToSupport(){
		Intent email = new Intent(Intent.ACTION_SEND);
		email.putExtra(Intent.EXTRA_EMAIL, new String[] { getResources().getString(R.string.support_email) });
		email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject, getString(R.string.appname)));
		email.putExtra(Intent.EXTRA_TEXT, "");
		email.setType("message/rfc822");
		startActivity(Intent.createChooser(email, getString(R.string.choose_email_client)));
	}
	


	// Retrofit


	public void getHelpAsync(final Activity activity, final HelpSection helpSection, final boolean showAgreeButtonsOnLoad) {
			if (AppStatus.getInstance(activity).isOnline(activity)) {

				helpExpandedRl.setVisibility(View.VISIBLE);
				progressBarHelp.setVisibility(View.VISIBLE);
				textViewInfoDisplay.setVisibility(View.GONE);
				helpWebview.setVisibility(View.GONE);
				loadHTMLContent("",false);
				HashMap<String, String> params = new HashMap<>();
				params.put("section", helpSection.getOrdinal()+"");
				params.put("login_type", "1");
				if(countryCode!=null){
					params.put("country_code",countryCode);
				}
				HomeUtil.putDefaultParams(params);


				RestClient.getApiServices().gethelp(params, new Callback<BookingHistoryResponse>() {


					@Override
					public void success(BookingHistoryResponse bookingHistoryResponse, Response response) {
						try {
							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
							JSONObject jObj;
							jObj = new JSONObject(jsonString);
							if (!jObj.isNull("error")) {
								String errorMessage = jObj.getString("error");
								if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
									HomeActivity.logoutUser(activity, null);
								} else {
									openHelpData(helpSection, getResources().getString(R.string.error_occured_tap_to_retry), true, false);
								}
							} else {
								String data = jObj.getString("data");
								openHelpData(helpSection, data, false, showAgreeButtonsOnLoad);
							}

						} catch (Exception exception) {
							exception.printStackTrace();
							openHelpData(helpSection, getResources().getString(R.string.error_occured_tap_to_retry), true, false);
						}
						progressBarHelp.setVisibility(View.GONE);
					}

					@Override
					public void failure(RetrofitError error) {
						progressBarHelp.setVisibility(View.GONE);
						openHelpData(helpSection, getResources().getString(R.string.error_occured_tap_to_retry), true, false);

					}
				});
			}
			else {
				openHelpData(helpSection, getResources().getString(R.string.no_internet_tap_to_retry), true, false);
			}
	}

	
	
	public void performBackPressed(){
		/*if(helpExpandedRl.getVisibility() == View.VISIBLE){
			helpExpandedRl.setVisibility(View.GONE);
			title.setText(getResources().getString(R.string.help));
		}
		else{*/
			finish();
			overridePendingTransition(R.anim.left_in, R.anim.left_out);
		//}
	}
	
	@Override
	public void onBackPressed() {
		performBackPressed();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}

}

