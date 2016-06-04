package product.clicklabs.jugnoo;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.HelpSection;
import product.clicklabs.jugnoo.datastructure.TransactionInfo;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.Log;
import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

public class WalletActivity extends Activity{
	
	
	LinearLayout relative;
	
	Button backBtn;
	TextView title;
	
	TextView textViewPromotion, textViewCurrentTransactionInfo, textViewAccountBalance, 
			textViewAccountBalanceValue, textViewRecentTransactions;
	Button buttonAddPayment;
	
	TextView textViewInfo;
	ProgressBar progressBar;
	
	ListView listViewTransactions;
	TransactionListAdapter transactionListAdapter;
	
	AsyncHttpClient fetchTransactionInfoClient;
	
	public double jugnooBalance = 0;
	public int totalTransactions = 0, pageSize = 0;
	public String promoBanner = "";
	ArrayList<TransactionInfo> transactionInfoList = new ArrayList<TransactionInfo>();
	
	DecimalFormat decimalFormat = new DecimalFormat("#.##");
	
	@Override
	protected void onStart() {
		super.onStart();
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
		setContentView(R.layout.activity_wallet);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(WalletActivity.this, relative, 1134, 720, false);
		
		
		backBtn = (Button) findViewById(R.id.backBtn); 
		title = (TextView) findViewById(R.id.title); title.setTypeface(Data.latoRegular(getApplicationContext()));
		
		textViewPromotion = (TextView) findViewById(R.id.textViewPromotion); textViewPromotion.setTypeface(Data.latoRegular(this));
		textViewCurrentTransactionInfo = (TextView) findViewById(R.id.textViewCurrentTransactionInfo); textViewCurrentTransactionInfo.setTypeface(Data.latoRegular(this));
		textViewAccountBalance = (TextView) findViewById(R.id.textViewAccountBalance); textViewAccountBalance.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		textViewAccountBalanceValue = (TextView) findViewById(R.id.textViewAccountBalanceValue); textViewAccountBalanceValue.setTypeface(Data.latoRegular(this));
		textViewRecentTransactions = (TextView) findViewById(R.id.textViewRecentTransactions); textViewRecentTransactions.setTypeface(Data.latoRegular(this));
		textViewRecentTransactions.setVisibility(View.GONE);
		textViewCurrentTransactionInfo.setVisibility(View.GONE);
		textViewPromotion.setVisibility(View.GONE);
		
		
		buttonAddPayment = (Button) findViewById(R.id.buttonAddPayment); buttonAddPayment.setTypeface(Data.latoRegular(this));
		
		listViewTransactions = (ListView) findViewById(R.id.listViewTransactions);
		
		transactionListAdapter = new TransactionListAdapter(this);
		listViewTransactions.setAdapter(transactionListAdapter);
		
		textViewInfo = (TextView) findViewById(R.id.textViewInfo); textViewInfo.setTypeface(Data.latoRegular(getApplicationContext()));
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		
		textViewInfo.setVisibility(View.GONE);
		progressBar.setVisibility(View.GONE);
		
		
		backBtn.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
		});
		
		textViewInfo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getTransactionInfoAsync(WalletActivity.this);
			}
		});
		
		buttonAddPayment.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(WalletActivity.this, WalletAddPaymentActivity.class));
				finish();
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});
		
		
		textViewPromotion.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				HelpParticularActivity.helpSection = HelpSection.WALLET_PROMOTIONS;
				startActivity(new Intent(WalletActivity.this, HelpParticularActivity.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});
		
		
		
		try{
			if(getIntent().hasExtra("payment")){
				String payment = getIntent().getStringExtra("payment");
				if("success".equalsIgnoreCase(payment)){
					String amount = getIntent().getStringExtra("amount");
					
					SpannableString sstr = new SpannableString("Successful Transaction");
					final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
					sstr.setSpan(bss, 0, sstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					
					textViewCurrentTransactionInfo.setText("");
					textViewCurrentTransactionInfo.append(sstr);
					textViewCurrentTransactionInfo.append(", Added Rs. "+amount);
					
					textViewCurrentTransactionInfo.setVisibility(View.VISIBLE);
					showPromoBanner();
					
					new Handler().postDelayed(new Runnable() {
						
						@Override
						public void run() {
							try {
								textViewCurrentTransactionInfo.setVisibility(View.GONE);
								showPromoBanner();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, 5000);
					
				}
				else{
					textViewCurrentTransactionInfo.setVisibility(View.GONE);
				}
			}
		} catch(Exception e){
			e.printStackTrace();
			textViewCurrentTransactionInfo.setVisibility(View.GONE);
		}
		
		
		jugnooBalance = 0;
		totalTransactions = 0;
		pageSize = 0;
		
		fetchTransactionInfoClient = null;
		
		if(transactionInfoList == null){
			transactionInfoList = new ArrayList<TransactionInfo>();
		}
		transactionInfoList.clear();
		
		try{
			if(Data.userData != null){
				textViewAccountBalanceValue.setText(getResources().getString(R.string.rupee)+" "+Data.userData.jugnooBalance);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		
		getTransactionInfoAsync(this);
		
	}
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
		super.onBackPressed();
	}
	
	
	
	@Override
	public void onDestroy() {
		if(fetchTransactionInfoClient != null){
			fetchTransactionInfoClient.cancelAllRequests(true);
		}
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	
	
	public void updateListData(String message, boolean errorOccurred){
		if(errorOccurred){
			textViewInfo.setText(message);
			textViewInfo.setVisibility(View.VISIBLE);
			
			transactionInfoList.clear();
			transactionListAdapter.notifyDataSetChanged();
		}
		else{
			textViewInfo.setVisibility(View.GONE);
			if(transactionInfoList.size() == 0){
				textViewRecentTransactions.setVisibility(View.GONE);
			}
			else{
				textViewRecentTransactions.setVisibility(View.VISIBLE);
			}
			transactionListAdapter.notifyDataSetChanged();
			textViewAccountBalanceValue.setText(getResources().getString(R.string.rupee)+" "+jugnooBalance);
		}
	}
	
	
	class ViewHolderTransaction {
		TextView textViewTransactionDate, textViewTransactionAmount, textViewTransactionTime, textViewTransactionType, textViewShowMore;
		LinearLayout relative;
		RelativeLayout relativeLayoutShowMore;
		int id;
	}

	class TransactionListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderTransaction holder;
		Context context;
		public TransactionListAdapter(Context context) {
			this.context = context;
			this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return transactionInfoList.size();
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
				holder = new ViewHolderTransaction();
				convertView = mInflater.inflate(R.layout.list_item_transaction, null);
				
				holder.textViewTransactionDate = (TextView) convertView.findViewById(R.id.textViewTransactionDate); holder.textViewTransactionDate.setTypeface(Data.latoRegular(context));
				holder.textViewTransactionAmount = (TextView) convertView.findViewById(R.id.textViewTransactionAmount); holder.textViewTransactionAmount.setTypeface(Data.latoRegular(context), Typeface.BOLD);
				holder.textViewTransactionTime = (TextView) convertView.findViewById(R.id.textViewTransactionTime); holder.textViewTransactionTime.setTypeface(Data.latoLight(context));
				holder.textViewTransactionType = (TextView) convertView.findViewById(R.id.textViewTransactionType); holder.textViewTransactionType.setTypeface(Data.latoLight(context));
				holder.textViewShowMore = (TextView) convertView.findViewById(R.id.textViewShowMore); holder.textViewShowMore.setTypeface(Data.latoRegular(context));
				
				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative); 
				holder.relativeLayoutShowMore = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutShowMore); 
				
				holder.relative.setTag(holder);
				holder.relativeLayoutShowMore.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderTransaction) convertView.getTag();
			}
			
			holder.id = position;
			
			if(totalTransactions > transactionInfoList.size() && position == transactionInfoList.size()-1){
				holder.relativeLayoutShowMore.setVisibility(View.VISIBLE);
			}
			else{
				holder.relativeLayoutShowMore.setVisibility(View.GONE);
			}
			
			TransactionInfo transactionInfo = transactionInfoList.get(position);
			
			holder.textViewTransactionDate.setText(transactionInfo.date);
			holder.textViewTransactionAmount.setText(getResources().getString(R.string.rupee)+" "+decimalFormat.format(transactionInfo.amount));
			holder.textViewTransactionTime.setText(transactionInfo.time);
			holder.textViewTransactionType.setText(transactionInfo.transactionType);
			
			holder.relativeLayoutShowMore.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					holder = (ViewHolderTransaction) v.getTag();
					
					fetchTransactionInfoClient = null;
					getTransactionInfoAsync(WalletActivity.this);
				}
			});
			
			
			return convertView;
		}

	}

	
	
	public void getTransactionInfoAsync(final Activity activity) {
		if(fetchTransactionInfoClient == null){
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				
				progressBar.setVisibility(View.VISIBLE);
				textViewInfo.setVisibility(View.GONE);
				
				RequestParams params = new RequestParams();
				params.put("access_token", Data.userData.accessToken);
				params.put("client_id", Data.CLIENT_ID);
				params.put("is_access_token_new", "1");
				params.put("start_from", ""+transactionInfoList.size());
				
				
				fetchTransactionInfoClient = Data.getClient();
				fetchTransactionInfoClient.post(Data.SERVER_URL + "/get_transaction_history", params,
						new CustomAsyncHttpResponseHandler() {
						private JSONObject jObj;
	
							@Override
							public void onFailure(Throwable arg3) {
								Log.e("request fail", arg3.toString());
								progressBar.setVisibility(View.GONE);
								updateListData("Some error occurred. Tap to retry", true);
							}
	
							@Override
							public void onSuccess(String response) {
								Log.e("Server response", "response = " + response);
								try {
									jObj = new JSONObject(response);
									if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
										int flag = jObj.getInt("flag");
										if(ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag){
											String error = jObj.getString("error");
											updateListData(error, true);
										}
										else if(ApiResponseFlags.TRANSACTION_HISTORY.getOrdinal() == flag){
											
//											{
//												   flag         	: constants.responseFlags.TRANSACTION_HISTORY,
//												   balance      	: balance,
//												   num_txns	: <number>,
//												   page_size	: <number>,
//												   transactions : [
//												{
//													txn_id		: <a random string> <not to be shown>,
//													txn_type	: <string><credited | autos | meals>,
//													amount	: <amount>,
//													txn_date	: <%d/%m/%y>,
//													txn_time	: <%h:%i %p>
//												}
//													banner		:
//											] 
//											};
											
											jugnooBalance = jObj.getDouble("balance");
											totalTransactions = jObj.getInt("num_txns");
											pageSize = jObj.getInt("page_size");
											
											promoBanner = "";
											if(jObj.has("banner")){
												promoBanner = jObj.getString("banner");
											}
											
											
											JSONArray jTransactions = jObj.getJSONArray("transactions");
											for(int i=0; i<jTransactions.length(); i++){
												JSONObject jTransactionI = jTransactions.getJSONObject(i);
												transactionInfoList.add(new TransactionInfo(jTransactionI.getInt("txn_id"), 
														jTransactionI.getString("txn_time"), 
														jTransactionI.getString("txn_date"), 
														jTransactionI.getString("txn_type"), 
														jTransactionI.getDouble("amount")));
											}
											
											if(Data.userData != null){
												Data.userData.jugnooBalance = jugnooBalance;
											}
											
											showPromoBanner();
											updateListData("No transactions currently", false);
										}
										else{
											updateListData("Some error occurred. Tap to retry", true);
										}
									}
									else{
										updateListData("Some error occurred. Tap to retry", true);
									}
									
								}  catch (Exception exception) {
									exception.printStackTrace();
									updateListData("Some error occurred. Tap to retry", true);
								}
								progressBar.setVisibility(View.GONE);
							}
							
							@Override
							public void onFinish() {
								fetchTransactionInfoClient = null;
								super.onFinish();
							}
							
						});
			}
			else {
				updateListData("No Internet connection. Tap to retry", true);
			}
		}
	}

	
	public void showPromoBanner(){
		
		if(!"".equalsIgnoreCase(promoBanner)){
			if(textViewCurrentTransactionInfo.getVisibility() == View.GONE){
				textViewPromotion.setVisibility(View.VISIBLE);
				textViewPromotion.setText(promoBanner);
			}
			else{
				textViewPromotion.setVisibility(View.GONE);
			}
		}
		else{
			textViewPromotion.setVisibility(View.GONE);
		}
		
	}
	
	
}
