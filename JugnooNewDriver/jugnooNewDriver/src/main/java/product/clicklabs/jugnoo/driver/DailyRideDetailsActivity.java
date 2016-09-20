package product.clicklabs.jugnoo.driver;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.driver.adapters.RideInfoTilesAdapter;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.FareStructureInfo;
import product.clicklabs.jugnoo.driver.datastructure.RideInfo;
import product.clicklabs.jugnoo.driver.datastructure.SearchResult;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.InfoTileResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.CustomMapMarkerCreator;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.MapLatLngBoundsCreator;
import product.clicklabs.jugnoo.driver.utils.MapUtils;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class DailyRideDetailsActivity extends BaseFragmentActivity {

	LinearLayout relative;

	Button backBtn;
	TextView title;

	TextView idValue, dateTimeValue, textViewActualFare, textViewCustomerPaid, textViewAccountBalance, textViewAccountBalanceText;

	ImageView imageViewRequestType;
	RelativeLayout relativeLayoutConvenienceCharges, relativeLayoutLuggageCharges,
			relativeLayoutCancelSubsidy, relativeLayoutJugnooCut;
	ArrayList<FareStructureInfo> fareStructureInfos = new ArrayList<>();
	RecyclerView recyclerViewRideInfo;
	RideInfoTilesAdapter rideInfoTilesAdapter;

	public static RideInfo openedRideInfo;
	public ASSL assl;
	CustomerInfo customerInfo;


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
		setContentView(R.layout.activity_daily_details_new);


		try {
			Intent intent = getIntent();
			String extra = intent.getStringExtra("extra");
			InfoTileResponse.Tile.Extras extras = new Gson().fromJson(extra, InfoTileResponse.Tile.Extras.class);


		} catch (Exception e) {
			e.printStackTrace();
		}


		relative = (LinearLayout) findViewById(R.id.relative);
		assl = new ASSL(DailyRideDetailsActivity.this, relative, 1134, 720, false);

		backBtn = (Button) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Data.latoRegular(this));

		relativeLayoutConvenienceCharges = (RelativeLayout) findViewById(R.id.relativeLayoutConvenienceCharges);
		relativeLayoutLuggageCharges = (RelativeLayout) findViewById(R.id.relativeLayoutLuggageCharges);
		relativeLayoutCancelSubsidy = (RelativeLayout) findViewById(R.id.relativeLayoutCancelSubsidy);
		relativeLayoutJugnooCut = (RelativeLayout) findViewById(R.id.relativeLayoutJugnooCut);


		recyclerViewRideInfo = (RecyclerView) findViewById(R.id.recyclerViewRideInfo);
		recyclerViewRideInfo.setHasFixedSize(true);
		LinearLayoutManager llm = new LinearLayoutManager(this);
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerViewRideInfo.setLayoutManager(llm);
		recyclerViewRideInfo.setItemAnimator(new DefaultItemAnimator());

		fareStructureInfos = new ArrayList<>();
		rideInfoTilesAdapter = new RideInfoTilesAdapter(this, fareStructureInfos);
		recyclerViewRideInfo.setAdapter(rideInfoTilesAdapter);


		idValue = (TextView) findViewById(R.id.idValue);
		idValue.setTypeface(Data.latoRegular(this));
		dateTimeValue = (TextView) findViewById(R.id.dateTimeValue);
		dateTimeValue.setTypeface(Data.latoRegular(this));


		textViewActualFare = (TextView) findViewById(R.id.textViewActualFare);
		textViewActualFare.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		textViewAccountBalance = (TextView) findViewById(R.id.textViewAccountBalance);
		textViewAccountBalance.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		textViewCustomerPaid = (TextView) findViewById(R.id.textViewCustomerPaid);
		textViewCustomerPaid.setTypeface(Data.latoRegular(this), Typeface.BOLD);

		textViewAccountBalanceText = (TextView) findViewById(R.id.textViewAccountBalanceText);
		textViewAccountBalanceText.setTypeface(Data.latoRegular(this));

		((TextView) findViewById(R.id.dateTimeValue)).setTypeface(Data.latoRegular(this));

		((TextView) findViewById(R.id.textViewRideFare)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewRideFareRupee)).setTypeface(Data.latoRegular(this));

		((TextView) findViewById(R.id.textViewActualFareText)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewCustomerPaidText)).setTypeface(Data.latoRegular(this));

		imageViewRequestType = (ImageView) findViewById(R.id.imageViewRequestType);

		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});


		if (openedRideInfo != null) {

			customerInfo = Data.getCustomerInfo("");

			idValue.setText(getResources().getString(R.string.ride_id) + " " + openedRideInfo.id);
			dateTimeValue.setText(DateOperations.convertDate(DateOperations.utcToLocal(openedRideInfo.dateTime)));


			textViewActualFare.setText(getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(Double.parseDouble(openedRideInfo.actualFare)));
			textViewCustomerPaid.setText(getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(Double.parseDouble(openedRideInfo.customerPaid)));

			if (Double.parseDouble(openedRideInfo.accountBalance) < 0) {
				textViewAccountBalance.setText((getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(Math.abs(Double.parseDouble(openedRideInfo.accountBalance)))));
				textViewAccountBalanceText.setTextColor(getResources().getColor(R.color.grey_ride_history));
				textViewAccountBalance.setTextColor(getResources().getColor(R.color.grey_ride_history));
				textViewAccountBalanceText.setText(getResources().getString(R.string.money_to));
			} else {
				textViewAccountBalance.setText(getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(Double.parseDouble(openedRideInfo.accountBalance)));
				textViewAccountBalanceText.setTextColor(getResources().getColor(R.color.grey_ride_history));
				textViewAccountBalance.setTextColor(getResources().getColor(R.color.grey_ride_history));
				textViewAccountBalanceText.setText(getResources().getString(R.string.account));
			}

			imageViewRequestType.setImageResource(R.drawable.request_autos);

		} else {
			performBackPressed();
		}

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
		ASSL.closeActivity(relative);
		System.gc();
	}

}
