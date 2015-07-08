package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import product.clicklabs.jugnoo.driver.datastructure.BusinessType;
import product.clicklabs.jugnoo.driver.datastructure.PaymentMode;
import product.clicklabs.jugnoo.driver.datastructure.RideInfo;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import rmn.androidscreenlibrary.ASSL;

public class RideDetailsActivity extends Activity{
	
	LinearLayout relative;
	
	Button backBtn;
	TextView title;
	
	TextView dateTimeValue, textViewRideId, textViewStatusString, textViewBalance, textViewJugnooSubsidy,
		textViewCustomerPaid, textViewPaidToMerchant, textViewPaidByCustomer, textViewFare, distanceValue, rideTimeValue, textViewFromValue, textViewToValue;
	ImageView couponImg, jugnooCashImg, imageViewRequestType;
	
	public static RideInfo openedRideInfo;
	
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
		setContentView(R.layout.activity_ride_details);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(RideDetailsActivity.this, relative, 1134, 720, false);
		
		backBtn = (Button) findViewById(R.id.backBtn); 
		title = (TextView) findViewById(R.id.title); title.setTypeface(Data.latoRegular(this));
		
		dateTimeValue = (TextView) findViewById(R.id.dateTimeValue); dateTimeValue.setTypeface(Data.latoRegular(this));
		textViewRideId = (TextView) findViewById(R.id.textViewRideId); textViewRideId.setTypeface(Data.latoRegular(this));
        textViewStatusString = (TextView) findViewById(R.id.textViewStatusString); textViewStatusString.setTypeface(Data.latoRegular(this));
		textViewBalance = (TextView) findViewById(R.id.textViewBalance); textViewBalance.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		textViewJugnooSubsidy = (TextView) findViewById(R.id.textViewJugnooSubsidy); textViewJugnooSubsidy.setTypeface(Data.latoRegular(this));
		textViewCustomerPaid = (TextView) findViewById(R.id.textViewCustomerPaid); textViewCustomerPaid.setTypeface(Data.latoRegular(this));
		textViewPaidToMerchant = (TextView) findViewById(R.id.textViewPaidToMerchant); textViewPaidToMerchant.setTypeface(Data.latoRegular(this));
        textViewPaidByCustomer = (TextView) findViewById(R.id.textViewPaidByCustomer); textViewPaidByCustomer.setTypeface(Data.latoRegular(this));
		textViewFare = (TextView) findViewById(R.id.textViewFare); textViewFare.setTypeface(Data.latoRegular(this));
		distanceValue = (TextView) findViewById(R.id.distanceValue); distanceValue.setTypeface(Data.latoRegular(this));
		rideTimeValue = (TextView) findViewById(R.id.rideTimeValue); rideTimeValue.setTypeface(Data.latoRegular(this));
		
		textViewFromValue = (TextView) findViewById(R.id.textViewFromValue); textViewFromValue.setTypeface(Data.latoRegular(this));
		textViewToValue = (TextView) findViewById(R.id.textViewToValue); textViewToValue.setTypeface(Data.latoRegular(this));
		
		((TextView) findViewById(R.id.textViewFrom)).setTypeface(Data.latoRegular(this), Typeface.BOLD);
		((TextView) findViewById(R.id.textViewTo)).setTypeface(Data.latoRegular(this), Typeface.BOLD);
		
		couponImg = (ImageView) findViewById(R.id.couponImg);
		jugnooCashImg = (ImageView) findViewById(R.id.jugnooCashImg);
        imageViewRequestType = (ImageView) findViewById(R.id.imageViewRequestType);
		
		backBtn.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		
		
		if(openedRideInfo != null){
			dateTimeValue.setText(DateOperations.convertDate(DateOperations.utcToLocal(openedRideInfo.dateTime)));
			textViewRideId.setText("Ride ID: "+openedRideInfo.id);

            if("".equalsIgnoreCase(openedRideInfo.statusString)){
                textViewStatusString.setVisibility(View.GONE);
            }
            else{
                textViewStatusString.setVisibility(View.VISIBLE);
                textViewStatusString.setText(openedRideInfo.statusString);
            }


			
			double balance = Double.parseDouble(openedRideInfo.balance);
			if(balance < 0){
				textViewBalance.setTextColor(getResources().getColor(R.color.red_status));
			}
			else{
				textViewBalance.setTextColor(getResources().getColor(R.color.bg_grey_opaque));
			}
			textViewBalance.setText("Bal. from Jugnoo: Rs. " + openedRideInfo.balance);
			
			if("0".equalsIgnoreCase(openedRideInfo.subsidy)){
				textViewJugnooSubsidy.setVisibility(View.GONE);
			}
			else{
				textViewJugnooSubsidy.setVisibility(View.VISIBLE);
				textViewJugnooSubsidy.setText("Jugnoo Subsidy: Rs. " + openedRideInfo.subsidy);
			}
			textViewFare.setText("Fare: Rs. "+openedRideInfo.fare);
			
			distanceValue.setText(openedRideInfo.distance + " km");
			rideTimeValue.setText(openedRideInfo.rideTime + " min");
			
			textViewFromValue.setText(openedRideInfo.fromLocation);
			textViewToValue.setText(openedRideInfo.toLocation);
			
			
			if(1 == openedRideInfo.couponUsed){
				couponImg.setVisibility(View.VISIBLE);
			}
			else{
				couponImg.setVisibility(View.GONE);
			}
			
			if(PaymentMode.WALLET.getOrdinal() == openedRideInfo.paymentMode){
				jugnooCashImg.setVisibility(View.VISIBLE);
			}
			else{
				jugnooCashImg.setVisibility(View.GONE);
			}

            if(BusinessType.AUTOS.getOrdinal() == openedRideInfo.businessId){
                textViewCustomerPaid.setVisibility(View.VISIBLE);
                textViewPaidToMerchant.setVisibility(View.GONE);
                textViewPaidByCustomer.setVisibility(View.GONE);

                textViewCustomerPaid.setText("Paid by Customer: Rs. " + openedRideInfo.customerPaid);
                imageViewRequestType.setImageResource(R.drawable.request_autos);
            }
            else if(BusinessType.FATAFAT.getOrdinal() == openedRideInfo.businessId){
                textViewCustomerPaid.setVisibility(View.GONE);
                textViewPaidToMerchant.setVisibility(View.VISIBLE);
                textViewPaidByCustomer.setVisibility(View.VISIBLE);

                textViewPaidToMerchant.setText("Paid to Merchant: Rs. "+openedRideInfo.paidToMerchant);
                textViewPaidByCustomer.setText("Paid by Customer: Rs. "+openedRideInfo.paidByCustomer);
                imageViewRequestType.setImageResource(R.drawable.request_fatafat);
            }
            else if(BusinessType.MEALS.getOrdinal() == openedRideInfo.businessId){
                textViewCustomerPaid.setVisibility(View.VISIBLE);
                textViewPaidToMerchant.setVisibility(View.GONE);
                textViewPaidByCustomer.setVisibility(View.GONE);

                textViewCustomerPaid.setText("Paid by Customer: Rs. " + openedRideInfo.customerPaid);
                imageViewRequestType.setImageResource(R.drawable.request_meals);
            }

        }
		else{
			performBackPressed();
		}
		
		
		
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
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	
	
	
}
