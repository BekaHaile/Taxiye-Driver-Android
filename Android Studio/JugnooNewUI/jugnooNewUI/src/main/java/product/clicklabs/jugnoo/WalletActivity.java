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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.AddPaymentPath;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.HelpSection;
import product.clicklabs.jugnoo.datastructure.TransactionInfo;
import product.clicklabs.jugnoo.datastructure.TransactionType;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import rmn.androidscreenlibrary.ASSL;

public class WalletActivity extends Activity{
	
	
	LinearLayout relative;
	
	ImageView imageViewBack;
	TextView textViewTitle;
	
	TextView textViewPromotion, textViewAccountBalance, 
			textViewAccountBalanceValue, textViewRecentTransactions;
	Button buttonAddPayment;
	
	TextView textViewInfo;
	ProgressBar progressBar;
	
	ListView listViewTransactions;
	TransactionListAdapter transactionListAdapter;
	
	RelativeLayout relativeLayoutShowMore;
	TextView textViewShowMore;
	
	AsyncHttpClient fetchTransactionInfoClient;
	
	public double jugnooBalance = 0;
	public int totalTransactions = 0, pageSize = 0;
	public String promoBanner = "";
	ArrayList<TransactionInfo> transactionInfoList = new ArrayList<TransactionInfo>();
	
//	DecimalFormat decimalFormat = new DecimalFormat("#.##");
    DecimalFormat decimalFormat = new DecimalFormat("####0.00");
	
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.init(this, Config.getFlurryKey());
		FlurryAgent.onStartSession(this, Config.getFlurryKey());
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
		
		promoBanner = "";
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(WalletActivity.this, relative, 1134, 720, false);
		
		
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack); 
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
		
		textViewPromotion = (TextView) findViewById(R.id.textViewPromotion); textViewPromotion.setTypeface(Fonts.latoRegular(this));
		textViewAccountBalance = (TextView) findViewById(R.id.textViewAccountBalance); textViewAccountBalance.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
		textViewAccountBalanceValue = (TextView) findViewById(R.id.textViewAccountBalanceValue); textViewAccountBalanceValue.setTypeface(Fonts.latoRegular(this));
		textViewRecentTransactions = (TextView) findViewById(R.id.textViewRecentTransactions); textViewRecentTransactions.setTypeface(Fonts.latoRegular(this));
		textViewRecentTransactions.setText("");
		textViewPromotion.setVisibility(View.GONE);
		
		
		buttonAddPayment = (Button) findViewById(R.id.buttonAddPayment); buttonAddPayment.setTypeface(Fonts.latoRegular(this));
		
		listViewTransactions = (ListView) findViewById(R.id.listViewTransactions);
		
		
		LinearLayout viewF = (LinearLayout) getLayoutInflater().inflate(R.layout.list_item_show_more, null);
		listViewTransactions.addFooterView(viewF);
		viewF.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
		ASSL.DoMagic(viewF);
		
		relativeLayoutShowMore = (RelativeLayout) viewF.findViewById(R.id.relativeLayoutShowMore);
		textViewShowMore = (TextView) viewF.findViewById(R.id.textViewShowMore); textViewShowMore.setTypeface(Fonts.latoLight(this), Typeface.BOLD);
		relativeLayoutShowMore.setVisibility(View.GONE);
		
		
		transactionListAdapter = new TransactionListAdapter(this);
		listViewTransactions.setAdapter(transactionListAdapter);
		
		textViewInfo = (TextView) findViewById(R.id.textViewInfo); textViewInfo.setTypeface(Fonts.latoRegular(getApplicationContext()));
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		
		textViewInfo.setVisibility(View.GONE);
		progressBar.setVisibility(View.GONE);
		
		
		imageViewBack.setOnClickListener(new View.OnClickListener() {
		
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
				WalletAddPaymentActivity.addPaymentPath = AddPaymentPath.FROM_WALLET;
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
		
		relativeLayoutShowMore.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				fetchTransactionInfoClient = null;
				getTransactionInfoAsync(WalletActivity.this);
			}
		});
		
		
		try{
			if(getIntent().hasExtra("payment")){
				String payment = getIntent().getStringExtra("payment");
				if("success".equalsIgnoreCase(payment)){
					String amount = getIntent().getStringExtra("amount");
					DialogPopup.dialogBanner(WalletActivity.this, "Payment successful, Added Rs. "+amount);
					if(AddPaymentPath.FROM_IN_RIDE == WalletAddPaymentActivity.addPaymentPath){
						HomeActivity.rechargedOnce = true;
					}
				}
			}
		} catch(Exception e){
			e.printStackTrace();
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
				textViewAccountBalanceValue.setText(getResources().getString(R.string.rupee)+" "+decimalFormat.format(Data.userData.jugnooBalance));
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		
		getTransactionInfoAsync(this);
		
	}

    public ArrayList<TransactionInfo> getTransactionInfoList(){
        if(transactionInfoList == null){
            transactionInfoList = new ArrayList<TransactionInfo>();
        }
        return transactionInfoList;
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
			relativeLayoutShowMore.setVisibility(View.GONE);
		}
		else{
			if(transactionInfoList.size() == 0){
				textViewRecentTransactions.setText("");
				relativeLayoutShowMore.setVisibility(View.GONE);
				textViewInfo.setVisibility(View.VISIBLE);
				textViewInfo.setText(message);
			}
			else{
				textViewRecentTransactions.setText("Recent Transactions");
				relativeLayoutShowMore.setVisibility(View.VISIBLE);
				textViewInfo.setVisibility(View.GONE);
			}
			transactionListAdapter.notifyDataSetChanged();
			textViewAccountBalanceValue.setText(getResources().getString(R.string.rupee)+" "+decimalFormat.format(jugnooBalance));
		}
		Utils.expandListForVariableHeight(listViewTransactions);
	}
	
	
	class ViewHolderTransaction {
		TextView textViewTransactionDate, textViewTransactionAmount, textViewTransactionTime, textViewTransactionType;
		LinearLayout relative;
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
				
				holder.textViewTransactionDate = (TextView) convertView.findViewById(R.id.textViewTransactionDate); holder.textViewTransactionDate.setTypeface(Fonts.latoRegular(context));
				holder.textViewTransactionAmount = (TextView) convertView.findViewById(R.id.textViewTransactionAmount); holder.textViewTransactionAmount.setTypeface(Fonts.latoRegular(context), Typeface.BOLD);
				holder.textViewTransactionTime = (TextView) convertView.findViewById(R.id.textViewTransactionTime); holder.textViewTransactionTime.setTypeface(Fonts.latoLight(context), Typeface.BOLD);
				holder.textViewTransactionType = (TextView) convertView.findViewById(R.id.textViewTransactionType); holder.textViewTransactionType.setTypeface(Fonts.latoLight(context), Typeface.BOLD);
				
				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative); 
				
				holder.relative.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderTransaction) convertView.getTag();
			}
			
			holder.id = position;
			
			TransactionInfo transactionInfo = transactionInfoList.get(position);
			
			holder.textViewTransactionDate.setText(transactionInfo.date);
			holder.textViewTransactionAmount.setText(getResources().getString(R.string.rupee)+" "+decimalFormat.format(transactionInfo.amount));
			holder.textViewTransactionTime.setText(transactionInfo.time);
			holder.textViewTransactionType.setText(transactionInfo.transactionText);
			
			if(TransactionType.CREDIT.getOrdinal() == transactionInfo.transactionType){
				holder.textViewTransactionType.setTextColor(getResources().getColor(R.color.green_transaction_type));
			}
			else{
				holder.textViewTransactionType.setTextColor(getResources().getColor(R.color.grey_dark));
			}
			
			return convertView;
		}
		
		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
			if(totalTransactions > transactionInfoList.size()){
				relativeLayoutShowMore.setVisibility(View.VISIBLE);
			}
			else{
				relativeLayoutShowMore.setVisibility(View.GONE);
			}
		}

	}

	
	
	public void getTransactionInfoAsync(final Activity activity) {
        try {
            if(!HomeActivity.checkIfUserDataNull(activity)) {
                relativeLayoutShowMore.setVisibility(View.GONE);
                if (fetchTransactionInfoClient == null) {
                    if (AppStatus.getInstance(activity).isOnline(activity)) {

                        progressBar.setVisibility(View.VISIBLE);
                        textViewInfo.setVisibility(View.GONE);

                        RequestParams params = new RequestParams();
                        params.put("access_token", Data.userData.accessToken);
                        params.put("client_id", Config.getClientId());
                        params.put("is_access_token_new", "1");
                        params.put("start_from", "" + getTransactionInfoList().size());


                        fetchTransactionInfoClient = Data.getClient();
                        fetchTransactionInfoClient.post(Config.getServerUrl() + "/get_transaction_history", params,
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
                                        if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                            int flag = jObj.getInt("flag");
                                            if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
                                                String error = jObj.getString("error");
                                                updateListData(error, true);
                                            } else if (ApiResponseFlags.TRANSACTION_HISTORY.getOrdinal() == flag) {

//											{
//											    "flag": 423,
//											    "banner": "Get Jugnoo Cash at 50% discount!!!",
//											    "balance": 1,
//											    "num_txns": 26,
//											    "page_size": 10,
//											    "transactions": [
//											        {
//											            "txn_id": 72,
//											            "txn_type": 1,
//											            "amount": 1,
//											            "txn_date": "February 27th, 2015",
//											            "txn_time": "12:58 PM",
//											            "txn_text": "Cash Added"
//											        }
//											    ]
//											}

                                                jugnooBalance = jObj.getDouble("balance");
                                                totalTransactions = jObj.getInt("num_txns");
                                                pageSize = jObj.getInt("page_size");

                                                promoBanner = "";
                                                if (jObj.has("banner")) {
                                                    promoBanner = jObj.getString("banner");
                                                }


                                                JSONArray jTransactions = jObj.getJSONArray("transactions");
                                                for (int i = 0; i < jTransactions.length(); i++) {
                                                    JSONObject jTransactionI = jTransactions.getJSONObject(i);
                                                    transactionInfoList.add(new TransactionInfo(jTransactionI.getInt("txn_id"),
                                                        jTransactionI.getInt("txn_type"),
                                                        jTransactionI.getString("txn_time"),
                                                        jTransactionI.getString("txn_date"),
                                                        jTransactionI.getString("txn_text"),
                                                        jTransactionI.getDouble("amount")));
                                                }

                                                if (Data.userData != null) {
                                                    Data.userData.jugnooBalance = jugnooBalance;
                                                }

                                                showPromoBanner();
                                                updateListData("No transactions currently", false);
                                            } else {
                                                updateListData("Some error occurred. Tap to retry", true);
                                            }
                                        } else {
                                            updateListData("Some error occurred. Tap to retry", true);
                                        }

                                    } catch (Exception exception) {
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
                    } else {
                        updateListData("No Internet connection. Tap to retry", true);
                    }
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
	}

	
	public void showPromoBanner(){
		if(!"".equalsIgnoreCase(promoBanner)){
			textViewPromotion.setVisibility(View.VISIBLE);
			textViewPromotion.setText(promoBanner);
		}
		else{
			textViewPromotion.setVisibility(View.GONE);
		}
	}
	
	
}
