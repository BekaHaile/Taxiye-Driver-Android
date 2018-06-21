package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.datastructure.RideInfo;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.InvoiceDetailResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class InvoiceDetailsActivity extends BaseActivity {

	LinearLayout linearLayoutRoot;

	ImageView backBtn;
	TextView title;

	TextView textViewCurrentInvoiceId, textViewJugnooCmsnValue, textViewReferralValue, textViewPaytmCashValue,
			textViewTotalAmntValue, textViewOutstandingAmntValue, textViewManualAdjValue, textViewPhoneDeductionValue,
			textViewCancelSubsidyValue, textViewPaidByJugnooValue, textViewPaidUsingCstmrValue, textViewPaidByCstmrValue,
			textViewCurrentInvoiceGeneratedOn,textViewCurrentInvoiceStatus, dateTimeValueFrom, dateTimeValueTo, textViewTotalAmnt,
			textViewRideMoneyValue, textViewTotalJugnooAmntValue;

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

		backBtn = findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		title.setText(R.string.invoice_detail);
		title.setTypeface(Fonts.mavenRegular(this));

//		((TextView) findViewById(R.id.textViewUpcomingInvoice)).setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
		((TextView) findViewById(R.id.dateTimeTextFrom)).setTypeface(Fonts.mavenRegular(this), Typeface.NORMAL);
		((TextView) findViewById(R.id.dateTimeTextTo)).setTypeface(Fonts.mavenRegular(this), Typeface.NORMAL);
		((TextView) findViewById(R.id.textViewPaidByCstmr)).setTypeface(Fonts.mavenRegular(this), Typeface.NORMAL);
		((TextView) findViewById(R.id.textViewPaidUsingCstmr)).setTypeface(Fonts.mavenRegular(this), Typeface.NORMAL);
		((TextView) findViewById(R.id.textViewPaidByJugnoo)).setTypeface(Fonts.mavenRegular(this), Typeface.NORMAL);
		((TextView) findViewById(R.id.textViewPaidByJugnoo)).setText(getString(R.string.paid_by_jugnoo, getString(R.string.appname)));
		((TextView) findViewById(R.id.textViewCancelSubsidy)).setTypeface(Fonts.mavenRegular(this), Typeface.NORMAL);
		((TextView) findViewById(R.id.textViewPhoneDeduction)).setTypeface(Fonts.mavenRegular(this), Typeface.NORMAL);
		((TextView) findViewById(R.id.textViewReferral)).setTypeface(Fonts.mavenRegular(this), Typeface.NORMAL);
		((TextView) findViewById(R.id.textViewManualAdj)).setTypeface(Fonts.mavenRegular(this), Typeface.NORMAL);
		((TextView) findViewById(R.id.textViewJugnooCmsn)).setTypeface(Fonts.mavenRegular(this), Typeface.NORMAL);
		((TextView) findViewById(R.id.textViewJugnooCmsn)).setText(getString(R.string.jugnoo_cmsn, getString(R.string.appname)));
		((TextView) findViewById(R.id.textViewOutstandingAmnt)).setTypeface(Fonts.mavenRegular(this), Typeface.NORMAL);
		((TextView) findViewById(R.id.textViewPaytmCash)).setTypeface(Fonts.mavenRegular(this), Typeface.NORMAL);
		((TextView) findViewById(R.id.textViewRideMoney)).setTypeface(Fonts.mavenRegular(this), Typeface.NORMAL);
		((TextView) findViewById(R.id.textViewTotalJugnooAmnt)).setTypeface(Fonts.mavenRegular(this), Typeface.NORMAL);


		textViewTotalAmnt = (TextView) findViewById(R.id.textViewTotalAmnt);
		textViewTotalAmnt.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
		textViewCurrentInvoiceGeneratedOn = (TextView) findViewById(R.id.textViewCurrentInvoiceGeneratedOn);
		textViewCurrentInvoiceGeneratedOn.setTypeface(Fonts.mavenRegular(this), Typeface.NORMAL);
		textViewPaidByCstmrValue = (TextView) findViewById(R.id.textViewPaidByCstmrValue);
		textViewPaidByCstmrValue.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
		textViewPaidUsingCstmrValue = (TextView) findViewById(R.id.textViewPaidUsingCstmrValue);
		textViewPaidUsingCstmrValue.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
		textViewPaidByJugnooValue = (TextView) findViewById(R.id.textViewPaidByJugnooValue);
		textViewPaidByJugnooValue.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
		textViewCancelSubsidyValue = (TextView) findViewById(R.id.textViewCancelSubsidyValue);
		textViewCancelSubsidyValue.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
		textViewPhoneDeductionValue = (TextView) findViewById(R.id.textViewPhoneDeductionValue);
		textViewPhoneDeductionValue.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
		textViewReferralValue = (TextView) findViewById(R.id.textViewReferralValue);
		textViewReferralValue.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
		textViewManualAdjValue = (TextView) findViewById(R.id.textViewManualAdjValue);
		textViewManualAdjValue.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
		textViewJugnooCmsnValue = (TextView) findViewById(R.id.textViewJugnooCmsnValue);
		textViewJugnooCmsnValue.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
		textViewOutstandingAmntValue = (TextView) findViewById(R.id.textViewOutstandingAmntValue);
		textViewOutstandingAmntValue.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
		textViewTotalAmntValue = (TextView) findViewById(R.id.textViewTotalAmntValue);
		textViewTotalAmntValue.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
		textViewCurrentInvoiceId = (TextView) findViewById(R.id.textViewCurrentInvoiceId);
		textViewCurrentInvoiceId.setTypeface(Fonts.mavenRegular(this));
		textViewCurrentInvoiceStatus = (TextView) findViewById(R.id.textViewCurrentInvoiceStatus);
		textViewCurrentInvoiceStatus.setTypeface(Fonts.mavenRegular(this));
		dateTimeValueFrom = (TextView) findViewById(R.id.dateTimeValueFrom);
		dateTimeValueFrom.setTypeface(Fonts.mavenRegular(this));
		dateTimeValueTo = (TextView) findViewById(R.id.dateTimeValueTo);
		dateTimeValueTo.setTypeface(Fonts.mavenRegular(this));
		textViewPaytmCashValue = (TextView) findViewById(R.id.textViewPaytmCashValue);
		textViewPaytmCashValue.setTypeface(Fonts.mavenRegular(this));
		textViewRideMoneyValue = (TextView) findViewById(R.id.textViewRideMoneyValue);
		textViewRideMoneyValue.setTypeface(Fonts.mavenRegular(this));
		textViewTotalJugnooAmntValue = (TextView) findViewById(R.id.textViewTotalJugnooAmntValue);
		textViewTotalJugnooAmntValue.setTypeface(Fonts.mavenRegular(this));

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
				textViewJugnooCmsnValue.setText("-"+Utils.formatCurrencyValue(invoiceDetailResponse.getInvoiceDetails().getCurrencyUnit(),invoiceDetailResponse.getInvoiceDetails().getJugnooCommision()));
			}

			if(invoiceDetailResponse.getInvoiceDetails().getOutstandingAmount() ==null){
				textViewOutstandingAmntValue.setText(getResources().getString(R.string.NA));
			}else {
				textViewOutstandingAmntValue.setText("-"+Utils.formatCurrencyValue(invoiceDetailResponse.getInvoiceDetails().getCurrencyUnit(),invoiceDetailResponse.getInvoiceDetails().getOutstandingAmount()));
			}

			textViewReferralValue.setText(Utils.formatCurrencyValue(invoiceDetailResponse.getInvoiceDetails().getCurrencyUnit(),invoiceDetailResponse.getInvoiceDetails().getReferralAmount()));


			textViewTotalAmntValue.setText(Utils.formatCurrencyValue(invoiceDetailResponse.getInvoiceDetails().getCurrencyUnit(),invoiceDetailResponse.getInvoiceDetails().getAmountToBePaid()));

//			textViewOutstandingAmntValue.setText(getResources().getString(R.string.rupee)
//					+ invoiceDetailResponse.getInvoiceDetails().getOutstandingAmount());


			if(invoiceDetailResponse.getInvoiceDetails().getManualCharges() < 0){
				imageViewNegetive5.setVisibility(View.VISIBLE);
				textViewManualAdjValue.setTextColor(getResources().getColor(R.color.red_status));
				textViewManualAdjValue.setText(Utils.formatCurrencyValue(invoiceDetailResponse.getInvoiceDetails().getCurrencyUnit(),Math.abs(invoiceDetailResponse.getInvoiceDetails().getManualCharges())));
			}else {
				imageViewNegetive5.setVisibility(View.GONE);
				textViewManualAdjValue.setTextColor(getResources().getColor(R.color.black));
				textViewManualAdjValue.setText(Utils.formatCurrencyValue(invoiceDetailResponse.getInvoiceDetails().getCurrencyUnit(),invoiceDetailResponse.getInvoiceDetails().getManualCharges()));
			}


			textViewPhoneDeductionValue.setText("-"+Utils.formatCurrencyValue(invoiceDetailResponse.getInvoiceDetails().getCurrencyUnit(),invoiceDetailResponse.getInvoiceDetails().getPhoneDeductions()));
			textViewCancelSubsidyValue.setText(Utils.formatCurrencyValue(invoiceDetailResponse.getInvoiceDetails().getCurrencyUnit(),invoiceDetailResponse.getInvoiceDetails().getCancelDistanceSubsidy()));
			textViewPaidByJugnooValue.setText(Utils.formatCurrencyValue(invoiceDetailResponse.getInvoiceDetails().getCurrencyUnit(),invoiceDetailResponse.getInvoiceDetails().getPaidByJugnoo()));
			textViewPaidUsingCstmrValue.setText(Utils.formatCurrencyValue(invoiceDetailResponse.getInvoiceDetails().getCurrencyUnit(),invoiceDetailResponse.getInvoiceDetails().getPaidUsingWallet()));
			textViewPaidByCstmrValue.setText("-"+Utils.formatCurrencyValue(invoiceDetailResponse.getInvoiceDetails().getCurrencyUnit(),invoiceDetailResponse.getInvoiceDetails().getPaidByCustomer()));
			textViewPaytmCashValue.setText(Utils.formatCurrencyValue(invoiceDetailResponse.getInvoiceDetails().getCurrencyUnit(),invoiceDetailResponse.getInvoiceDetails().getPaytmCash()));

			textViewRideMoneyValue.setText(Utils.formatCurrencyValue(invoiceDetailResponse.getInvoiceDetails().getCurrencyUnit(),invoiceDetailResponse.getInvoiceDetails().getRideMoney()));
			textViewTotalJugnooAmntValue.setText(Utils.formatCurrencyValue(invoiceDetailResponse.getInvoiceDetails().getCurrencyUnit(),invoiceDetailResponse.getInvoiceDetails().getTotalAmount()));

		} else {
			performBackPressed();
		}

	}

	private void getInvoiceDetails(final Activity activity) {
		try {
			HashMap<String, String> params = new HashMap<>();
			params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
			params.put("invoice_id", String.valueOf(invoice_id));
			HomeUtil.putDefaultParams(params);
			RestClient.getApiServices().invoiceDetail(params, new Callback<InvoiceDetailResponse>() {
				@Override
				public void success(InvoiceDetailResponse invoiceDetailResponse, Response response) {
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj;
						jObj = new JSONObject(jsonString);
						if (!jObj.isNull("error")) {
							String errorMessage = jObj.getString("error");
							if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
								HomeActivity.logoutUser(activity, null);
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
