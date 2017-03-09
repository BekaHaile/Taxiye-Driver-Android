package product.clicklabs.jugnoo.driver.dodo.fragments;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.Database2;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.adapters.DeliveryInfoInRideAdapter;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.dodo.adapters.DeliveryInfoAdapter;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryInfo;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryInfoInRideDetails;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryStatus;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Utils;


@SuppressLint("ValidFragment")
public class DeliveryInfosListInRideFragment extends Fragment {

	private RelativeLayout relativeLayoutRoot;

	private Button helpBtn, driverStartRideBtn, driverCancelRideBtn;
	private TextView textViewTitle, textViewMerchantName, textViewCashReq;
	private RecyclerView recyclerViewDeliveryInfoInRide;
	private DeliveryInfoInRideAdapter deliveryInfoInRideAdapter;
	private RelativeLayout relativeLayoutCall;
	private DeliveryInfoInRideDetails deliveryInfos = new DeliveryInfoInRideDetails();

	private View rootView;
	private HomeActivity activity;

	public DeliveryInfosListInRideFragment(DeliveryInfoInRideDetails deliveryInfoInRideDetails) {
		this.deliveryInfos = deliveryInfoInRideDetails;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_delivery_info_list_inride, container, false);
		activity = (HomeActivity) getActivity();

		relativeLayoutRoot = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutRoot);
		try {
			relativeLayoutRoot.setLayoutParams(new LayoutParams(720, 1134));
			ASSL.DoMagic(relativeLayoutRoot);
		} catch (Exception e) {
			e.printStackTrace();
		}


		textViewMerchantName = (TextView) rootView.findViewById(R.id.textViewMerchantName);
		textViewMerchantName.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		textViewCashReq = (TextView) rootView.findViewById(R.id.textViewCashReq);
		textViewCashReq.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);

		relativeLayoutCall = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutCall);

		helpBtn = (Button) rootView.findViewById(R.id.helpBtn);
		driverStartRideBtn = (Button) rootView.findViewById(R.id.driverStartRideBtn);
		driverCancelRideBtn = (Button) rootView.findViewById(R.id.driverCancelRideBtn);

		textViewTitle = (TextView) rootView.findViewById(R.id.title);
		textViewTitle.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);

		recyclerViewDeliveryInfoInRide = (RecyclerView) rootView.findViewById(R.id.recyclerViewDeliveryInfoInRide);
		recyclerViewDeliveryInfoInRide.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewDeliveryInfoInRide.setItemAnimator(new DefaultItemAnimator());
		recyclerViewDeliveryInfoInRide.setHasFixedSize(false);

		deliveryInfoInRideAdapter = new DeliveryInfoInRideAdapter(activity, deliveryInfos.getDeliveryData());

		recyclerViewDeliveryInfoInRide.setAdapter(deliveryInfoInRideAdapter);

		textViewMerchantName.setText(deliveryInfos.getPickupData().getName());
		textViewCashReq.setText(String.valueOf(activity.getResources().getString(R.string.cash_to_collected)
				+": "+activity.getResources().getString(R.string.rupee)+ deliveryInfos.getPickupData().getCashToCollect()));

		helpBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.openCallIntent(activity, Data.userData.driverSupportNumber);
				Log.i("completeRingData", Database2.getInstance(activity).getRingCompleteData());
			}
		});

		relativeLayoutCall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.openCallIntent(activity, deliveryInfos.getPickupData().getPhone());
				Log.i("completeRingData", Database2.getInstance(activity).getRingCompleteData());
			}
		});

		driverStartRideBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.driverStartRideBtn.performClick();
			}
		});

		driverCancelRideBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.driverCancelRideBtn.performClick();
			}
		});

//		updateList(deliveryStatusOpened);
		return rootView;
	}

	public void backPress() {
		try {
			activity.onBackPressed();
		} catch (Exception e) {
			e.printStackTrace();
			activity.onBackPressed();
		}
	}


//	private void updateList(DeliveryStatus deliveryStatus) {
//		try {
//			deliveryInfos.clear();
//			for (DeliveryInfo deliveryInfo : Data.getCustomerInfo(String.valueOf(engagementId)).getDeliveryInfos()) {
//				deliveryInfos.add(deliveryInfo);
//			}
//			deliveryInfoInRideAdapter.notifyDataSetChanged();
//			deliveryStatusOpened = deliveryStatus;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}


//	@Override
//	public void onHiddenChanged(boolean hidden) {
//		super.onHiddenChanged(hidden);
//		try {
//			if (!hidden) {
//				if (deliveryStatusOpened == DeliveryStatus.PENDING
//						&& deliveryInfos.size() == 1
//						&& deliveryInfos.get(0).getStatus() != DeliveryStatus.PENDING.getOrdinal()) {
//					deliveryStatusOpened = DeliveryStatus.COMPLETED;
//				}
//				updateList(deliveryStatusOpened);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			ASSL.closeActivity(relativeLayoutRoot);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.gc();
	}

}
