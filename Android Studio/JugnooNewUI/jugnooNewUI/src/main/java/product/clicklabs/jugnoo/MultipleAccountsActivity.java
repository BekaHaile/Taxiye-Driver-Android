package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.EnglishNumberToWords;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import rmn.androidscreenlibrary.ASSL;

public class MultipleAccountsActivity extends Activity {

	LinearLayout relative;

	TextView textViewTitle;
	ImageView imageViewBack;

    ScrollView scrollView;

    TextView textViewMultipleAccountsCreated, textViewPleaseLogin;
    ListView listViewPreviousAccounts;
    TextView textViewLikeToCreate;
    RelativeLayout relativeLayoutMailUs;
    TextView textViewContactUs, textViewMailUs;

    PreviousAccountsAdapter previousAccountsAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multiple_accounts);


		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(this, relative, 1134, 720, false);

		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        textViewMultipleAccountsCreated = (TextView) findViewById(R.id.textViewMultipleAccountsCreated); textViewMultipleAccountsCreated.setTypeface(Data.latoRegular(this));
        textViewPleaseLogin = (TextView) findViewById(R.id.textViewPleaseLogin); textViewPleaseLogin.setTypeface(Data.latoLight(this), Typeface.BOLD);

        listViewPreviousAccounts = (ListView) findViewById(R.id.listViewPreviousAccounts);

        textViewLikeToCreate = (TextView) findViewById(R.id.textViewLikeToCreate); textViewLikeToCreate.setTypeface(Data.latoRegular(this));

        relativeLayoutMailUs = (RelativeLayout) findViewById(R.id.relativeLayoutMailUs);
        textViewContactUs = (TextView) findViewById(R.id.textViewContactUs); textViewContactUs.setTypeface(Data.latoLight(this), Typeface.BOLD);
        textViewMailUs = (TextView) findViewById(R.id.textViewMailUs); textViewMailUs.setTypeface(Data.latoRegular(this));


        previousAccountsAdapter = new PreviousAccountsAdapter(this);
        listViewPreviousAccounts.setAdapter(previousAccountsAdapter);



		imageViewBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});


        relativeLayoutMailUs.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MultipleAccountsActivity.this, RequestDuplicateRegistrationActivity.class));
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });


        previousAccountsAdapter.notifyDataSetChanged();
        Utils.expandListForFixedHeight(listViewPreviousAccounts);

        scrollView.smoothScrollTo(0,0);

        try {
            textViewMultipleAccountsCreated.setText(EnglishNumberToWords.convert(Data.previousAccountInfoList.size()) + " accounts have already been created on this device");
        } catch(Exception e){
            e.printStackTrace();
            performBackPressed();
        }

    }

	
	
	public void performBackPressed(){
        Intent intent = new Intent(MultipleAccountsActivity.this, RegisterScreen.class);
        intent.putExtra("back_from_otp", true);
        startActivity(intent);
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	
	@Override
	public void onBackPressed() {
		performBackPressed();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		HomeActivity.checkForAccessTokenChange(this);

	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ASSL.closeActivity(relative);
		System.gc();
	}
	
	

	
	class ViewHolderPreviousAccount {
		TextView textViewAccountEmail, textViewAccountPhone, textViewLogin;
		LinearLayout relative;
		int id;
	}

	class PreviousAccountsAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderPreviousAccount holder;
		Context context;
		public PreviousAccountsAdapter(Context context) {
			this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.context = context;
		}

		@Override
		public int getCount() {
			return Data.previousAccountInfoList.size();
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
				holder = new ViewHolderPreviousAccount();
				convertView = mInflater.inflate(R.layout.list_item_previous_account, null);
				
				holder.textViewAccountEmail = (TextView) convertView.findViewById(R.id.textViewAccountEmail); holder.textViewAccountEmail.setTypeface(Data.latoRegular(context));
				holder.textViewAccountPhone = (TextView) convertView.findViewById(R.id.textViewAccountPhone); holder.textViewAccountPhone.setTypeface(Data.latoLight(context), Typeface.BOLD);
				holder.textViewLogin = (TextView) convertView.findViewById(R.id.textViewLogin); holder.textViewLogin.setTypeface(Data.latoRegular(context));

				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative);

				holder.relative.setTag(holder);

				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderPreviousAccount) convertView.getTag();
			}
			
			
			holder.id = position;

            holder.textViewAccountEmail.setText(Data.previousAccountInfoList.get(position).userEmail);
            holder.textViewAccountPhone.setText(Utils.hidePhoneNoString(Data.previousAccountInfoList.get(position).phoneNo));


			holder.relative.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    holder = (ViewHolderPreviousAccount) v.getTag();
                    String previousEmail = Data.previousAccountInfoList.get(holder.id).userEmail;
                    Intent intent = new Intent(MultipleAccountsActivity.this, SplashLogin.class);
                    intent.putExtra("previous_login_email", previousEmail);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                }
            });
			
			
			return convertView;
		}


		
	}





    /**
     * ASync for initiating OTP Call from server
     */
    public void requestDupRegistrationsAPI(final Activity activity, String phoneNo) {
        if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

            DialogPopup.showLoadingDialog(activity, "Loading...");

            RequestParams params = new RequestParams();

            params.put("client_id", Data.CLIENT_ID);
            params.put("access_token", Data.userData.accessToken);
            params.put("is_access_token_new", "1");
            params.put("phone_no", phoneNo);
            Log.i("params", ">" + params);

            AsyncHttpClient client = Data.getClient();
            client.post(Data.SERVER_URL + "/send_new_number_otp_via_call", params,
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
                                    DialogPopup.dialogBanner(activity, error);
                                }
                                else if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
                                    String message = jObj.getString("message");
                                    DialogPopup.dialogBanner(activity, message);
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
