package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import org.json.JSONObject;

import product.clicklabs.jugnoo.driver.datastructure.BusinessType;
import product.clicklabs.jugnoo.driver.datastructure.InvoiceInfo;
import product.clicklabs.jugnoo.driver.datastructure.RideInfo;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.InvoiceDetailResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.InvoiceHistoryResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.LeaderboardActivityResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class InvoiceDetailsActivity extends Activity {

	LinearLayout linearLayoutRoot;

	Button backBtn;
	TextView title;

	TextView textViewCurrentInvoiceId, textViewJugnooCmsnValue, textViewReferralValue, textViewPaytmCashValue,
			textViewTotalAmntValue, textViewOutstandingAmntValue, textViewManualAdjValue, textViewPhoneDeductionValue,
			textViewCancelSubsidyValue, textViewPaidByJugnooValue, textViewPaidUsingCstmrValue, textViewPaidByCstmrValue,
			textViewCurrentInvoiceGeneratedOn,textViewCurrentInvoiceStatus, dateTimeValueFrom, dateTimeValueTo, textViewTotalAmnt;

	ImageView imageViewRequestType, imageViewNegetive5;


	public static RideInfo openedRideInfo;

	int invoice_id;

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
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_invoice);



		linearLayoutRoot = (LinearLayout) findViewById(R.id.linearLayoutRoot);
		new ASSL(InvoiceDetailsActivity.this, linearLayoutRoot, 1134, 720, false);

		Bundle extras = getIntent().getExtras();
		if(extras != null){
			invoice_id = extras.getInt("invoice_id");
		}

		backBtn = (Button) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Data.latoRegular(this));

//		((TextView) findViewById(R.id.textViewUpcomingInvoice)).setTypeface(Data.latoRegular(this), Typeface.BOLD);
		((TextView) findViewById(R.id.dateTimeTextFrom)).setTypeface(Data.latoRegular(this), Typeface.NORMAL);
		((TextView) findViewById(R.id.dateTimeTextTo)).setTypeface(Data.latoRegular(this), Typeface.NORMAL);
		((TextView) findViewById(R.id.textViewPaidByCstmr)).setTypeface(Data.latoRegular(this), Typeface.NORMAL);
		((TextView) findViewById(R.id.textViewPaidUsingCstmr)).setTypeface(Data.latoRegular(this), Typeface.NORMAL);
		((TextView) findViewById(R.id.textViewPaidByJugnoo)).setTypeface(Data.latoRegular(this), Typeface.NORMAL);
		((TextView) findViewById(R.id.textViewCancelSubsidy)).setTypeface(Data.latoRegular(this), Typeface.NORMAL);
		((TextView) findViewById(R.id.textViewPhoneDeduction)).setTypeface(Data.latoRegular(this), Typeface.NORMAL);
		((TextView) findViewById(R.id.textViewReferral)).setTypeface(Data.latoRegular(this), Typeface.NORMAL);
		((TextView) findViewById(R.id.textViewManualAdj)).setTypeface(Data.latoRegular(this), Typeface.NORMAL);
		((TextView) findViewById(R.id.textViewJugnooCmsn)).setTypeface(Data.latoRegular(this), Typeface.NORMAL);
		((TextView) findViewById(R.id.textViewOutstandingAmnt)).setTypeface(Data.latoRegular(this), Typeface.NORMAL);
		((TextView) findViewById(R.id.textViewPaytmCash)).setTypeface(Data.latoRegular(this), Typeface.NORMAL);


		textViewTotalAmnt = (TextView) findViewById(R.id.textViewTotalAmnt);
		textViewTotalAmnt.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		textViewCurrentInvoiceGeneratedOn = (TextView) findViewById(R.id.textViewCurrentInvoiceGeneratedOn);
		textViewCurrentInvoiceGeneratedOn.setTypeface(Data.latoRegular(this), Typeface.NORMAL);
		textViewPaidByCstmrValue = (TextView) findViewById(R.id.textViewPaidByCstmrValue);
		textViewPaidByCstmrValue.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		textViewPaidUsingCstmrValue = (TextView) findViewById(R.id.textViewPaidUsingCstmrValue);
		textViewPaidUsingCstmrValue.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		textViewPaidByJugnooValue = (TextView) findViewById(R.id.textViewPaidByJugnooValue);
		textViewPaidByJugnooValue.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		textViewCancelSubsidyValue = (TextView) findViewById(R.id.textViewCancelSubsidyValue);
		textViewCancelSubsidyValue.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		textViewPhoneDeductionValue = (TextView) findViewById(R.id.textViewPhoneDeductionValue);
		textViewPhoneDeductionValue.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		textViewReferralValue = (TextView) findViewById(R.id.textViewReferralValue);
		textViewReferralValue.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		textViewManualAdjValue = (TextView) findViewById(R.id.textViewManualAdjValue);
		textViewManualAdjValue.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		textViewJugnooCmsnValue = (TextView) findViewById(R.id.textViewJugnooCmsnValue);
		textViewJugnooCmsnValue.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		textViewOutstandingAmntValue = (TextView) findViewById(R.id.textViewOutstandingAmntValue);
		textViewOutstandingAmntValue.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		textViewTotalAmntValue = (TextView) findViewById(R.id.textViewTotalAmntValue);
		textViewTotalAmntValue.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		textViewCurrentInvoiceId = (TextView) findViewById(R.id.textViewCurrentInvoiceId);
		textViewCurrentInvoiceId.setTypeface(Data.latoRegular(this));
		textViewCurrentInvoiceStatus = (TextView) findViewById(R.id.textViewCurrentInvoiceStatus);
		textViewCurrentInvoiceStatus.setTypeface(Data.latoRegular(this));
		dateTimeValueFrom = (TextView) findViewById(R.id.dateTimeValueFrom);
		dateTimeValueFrom.setTypeface(Data.latoRegular(this));
		dateTimeValueTo = (TextView) findViewById(R.id.dateTimeValueTo);
		dateTimeValueTo.setTypeface(Data.latoRegular(this));
		textViewPaytmCashValue = (TextView) findViewById(R.id.textViewPaytmCashValue);
		textViewPaytmCashValue.setTypeface(Data.latoRegular(this));


		imageViewRequestType = (ImageView) findViewById(R.id.imageViewRequestType);
		imageViewNegetive5 = (ImageView) findViewById(R.id.imageViewNegetive5);
		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});

		getInvoiceDetails(this);
	}


	public void performBackPressed() {
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
		super.onDestroy();
		ASSL.closeActivity(linearLayoutRoot);
		System.gc();
	}

	public void updateData(InvoiceDetailResponse invoiceDetailResponse){

		if (invoiceDetailResponse != null) {

			if(invoiceDetailResponse.getInvoiceDetails().getInvoiceDate().equalsIgnoreCase("0")){
				textViewTotalAmnt.setText(getResources().getString(R.string.total_amnt_till_now) );
			}else {
				textViewTotalAmnt.setText(getResources().getString(R.string.amnt_credited));
			}

			if(invoiceDetailResponse.getInvoiceDetails().getInvoiceDate().equalsIgnoreCase("0")){
				textViewCurrentInvoiceGeneratedOn.setText(getResources().getString(R.string.invoice_date)+": "+getResources().getString(R.string.NA) );
			}else {
				textViewCurrentInvoiceGeneratedOn.setText(getResources().getString(R.string.invoice_date)+": " + DateOperations.reverseDate(invoiceDetailResponse.getInvoiceDetails().getInvoiceDate()));
			}
			if(invoiceDetailResponse.getInvoiceDetails().getInvoiceId()==0){
				textViewCurrentInvoiceId.setText(getResources().getString(R.string.invoice_id)+": "+getResources().getString(R.string.NA)  );
			}else {
				textViewCurrentInvoiceId.setText(getResources().getString(R.string.invoice_id)+": " + String.valueOf(invoiceDetailResponse.getInvoiceDetails().getInvoiceId()));
			}
			textViewCurrentInvoiceStatus.setText(invoiceDetailResponse.getInvoiceDetails().getStatus());
			dateTimeValueTo.setText(DateOperations.reverseDate(invoiceDetailResponse.getInvoiceDetails().getInvoicingToDate()));
			dateTimeValueFrom.setText(DateOperations.reverseDate(invoiceDetailResponse.getInvoiceDetails().getInvoicingFromDate()));

			if(invoiceDetailResponse.getInvoiceDetails().getJugnooCommision() ==null){
				textViewJugnooCmsnValue.setText(getResources().getString(R.string.NA));
			}else {
				textViewJugnooCmsnValue.setText(getResources().getString(R.string.rupee)
						+ Utils.getDecimalFormatForMoney().format(invoiceDetailResponse.getInvoiceDetails().getJugnooCommision()));
			}

			if(invoiceDetailResponse.getInvoiceDetails().getOutstandingAmount() ==null){
				textViewOutstandingAmntValue.setText(getResources().getString(R.string.NA));
			}else {
				textViewOutstandingAmntValue.setText(getResources().getString(R.string.rupee)
						+ invoiceDetailResponse.getInvoiceDetails().getOutstandingAmount());
			}

			textViewReferralValue.setText(getResources().getString(R.string.rupee)
					+ invoiceDetailResponse.getInvoiceDetails().getReferralAmount());


			textViewTotalAmntValue.setText(getResources().getString(R.string.rupee)
					+ invoiceDetailResponse.getInvoiceDetails().getAmountToBePaid());

//			textViewOutstandingAmntValue.setText(getResources().getString(R.string.rupee)
//					+ invoiceDetailResponse.getInvoiceDetails().getOutstandingAmount());


			if(invoiceDetailResponse.getInvoiceDetails().getManualCharges() < 0){
				imageViewNegetive5.setVisibility(View.VISIBLE);
				textViewManualAdjValue.setTextColor(getResources().getColor(R.color.red_status));
				textViewManualAdjValue.setText(getResources().getString(R.string.rupee)
						+ Math.abs(invoiceDetailResponse.getInvoiceDetails().getManualCharges()));
			}else {
				imageViewNegetive5.setVisibility(View.GONE);
				textViewManualAdjValue.setTextColor(getResources().getColor(R.color.black));
				textViewManualAdjValue.setText(getResources().getString(R.string.rupee)
						+ invoiceDetailResponse.getInvoiceDetails().getManualCharges());
			}


			textViewPhoneDeductionValue.setText(getResources().getString(R.string.rupee)
					+ invoiceDetailResponse.getInvoiceDetails().getPhoneDeductions());
			textViewCancelSubsidyValue.setText(getResources().getString(R.string.rupee)
					+ invoiceDetailResponse.getInvoiceDetails().getCancelDistanceSubsidy());
			textViewPaidByJugnooValue.setText(getResources().getString(R.string.rupee)
					+ invoiceDetailResponse.getInvoiceDetails().getPaidByJugnoo());
			textViewPaidUsingCstmrValue.setText(getResources().getString(R.string.rupee)
					+ invoiceDetailResponse.getInvoiceDetails().getPaidUsingWallet());
			textViewPaidByCstmrValue.setText(getResources().getString(R.string.rupee)
					+ invoiceDetailResponse.getInvoiceDetails().getPaidByCustomer());
			textViewPaytmCashValue.setText(getResources().getString(R.string.rupee)
					+ invoiceDetailResponse.getInvoiceDetails().getPaytmCash());

		} else {
			performBackPressed();
		}

	}

	private void getInvoiceDetails(final Activity activity) {
		try {
			RestClient.getApiServices().invoiceDetail(Data.userData.accessToken, String.valueOf(invoice_id), new Callback<InvoiceDetailResponse>() {
				@Override
				public void success(InvoiceDetailResponse invoiceDetailResponse, Response response) {
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj;
						jObj = new JSONObject(jsonString);
						if (!jObj.isNull("error")) {
							String errorMessage = jObj.getString("error");
							if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
								HomeActivity.logoutUser(activity);
							}
						} else {
							updateData(invoiceDetailResponse);
						}
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}

				@Override
				public void failure(RetrofitError error) {
					Log.i("error", String.valueOf(error));
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
