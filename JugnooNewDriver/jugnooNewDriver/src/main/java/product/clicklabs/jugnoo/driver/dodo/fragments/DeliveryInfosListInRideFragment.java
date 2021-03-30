package product.clicklabs.jugnoo.driver.dodo.fragments;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.Database2;
import product.clicklabs.jugnoo.driver.DialogReviewImagesFragment;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.adapters.DeliveryInfoInRideAdapter;
import product.clicklabs.jugnoo.driver.adapters.ImageWithTextAdapter;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryInfoInRideDetails;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.NotesDialog;
import product.clicklabs.jugnoo.driver.utils.Utils;


@SuppressLint("ValidFragment")
public class DeliveryInfosListInRideFragment extends Fragment {

	private RelativeLayout relativeLayoutRoot;

	private Button helpBtn,changeButton, driverStartRideBtn, driverCancelRideBtn;
	private TextView textViewTitle, textViewMerchantName, textViewCashReq, tvCustomerNotes;
	private RecyclerView recyclerViewDeliveryInfoInRide, rvPickupFeedImages;
	private DeliveryInfoInRideAdapter deliveryInfoInRideAdapter;
	private RelativeLayout relativeLayoutCall, relativeLayoutLoading,relativeLayoutMerchantDetails;
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
		tvCustomerNotes = (TextView) rootView.findViewById(R.id.tvCustomerNotes);
		tvCustomerNotes.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);

		relativeLayoutCall = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutCall);
		relativeLayoutLoading = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutLoading);
		relativeLayoutMerchantDetails = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutMerchantDetails);

		helpBtn = (Button) rootView.findViewById(R.id.helpBtn);
		changeButton = (Button) rootView.findViewById(R.id.changeButton);
		changeButton.setTypeface(Fonts.mavenRegular(activity));
		try {
			ArrayList<CustomerInfo> customerEngCount = Data.getAssignedCustomerInfosListForEngagedStatus();
			if (customerEngCount.size() > 1) {
				changeButton.setVisibility(View.VISIBLE);
			} else {
				changeButton.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		driverStartRideBtn = (Button) rootView.findViewById(R.id.driverStartRideBtn);
		driverCancelRideBtn = (Button) rootView.findViewById(R.id.driverCancelRideBtn);

		textViewTitle = (TextView) rootView.findViewById(R.id.title);
		textViewTitle.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);

		recyclerViewDeliveryInfoInRide = (RecyclerView) rootView.findViewById(R.id.recyclerViewDeliveryInfoInRide);
		recyclerViewDeliveryInfoInRide.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewDeliveryInfoInRide.setItemAnimator(new DefaultItemAnimator());
		recyclerViewDeliveryInfoInRide.setHasFixedSize(false);

		rvPickupFeedImages = (RecyclerView) rootView.findViewById(R.id.rvPickupFeedImages);
		rvPickupFeedImages.setItemAnimator(new DefaultItemAnimator());
		rvPickupFeedImages.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false));


		deliveryInfoInRideAdapter = new DeliveryInfoInRideAdapter(activity, deliveryInfos.getDeliveryData());

		recyclerViewDeliveryInfoInRide.setAdapter(deliveryInfoInRideAdapter);

		textViewMerchantName.setText(deliveryInfos.getPickupData().getName());

		CustomerInfo customerInfo = Data.getCurrentCustomerInfo();
		if (customerInfo.getCustomerOrderImagesList() != null && customerInfo.getCustomerOrderImagesList().size() > 0) {
			rvPickupFeedImages.setAdapter(new ImageWithTextAdapter(customerInfo.getCustomerOrderImagesList(), new ImageWithTextAdapter.OnItemClickListener() {
				@Override
				public void onItemClick(String image, int pos) {
					ArrayList<String> imagesCustomer = new ArrayList<>();
					imagesCustomer.addAll(customerInfo.getCustomerOrderImagesList());
					DialogReviewImagesFragment dialog = DialogReviewImagesFragment.newInstance(pos,imagesCustomer);
					dialog.show(activity.getFragmentManager(),DialogReviewImagesFragment.class.getSimpleName());
				}
			}));
			rvPickupFeedImages.setVisibility(View.VISIBLE);
		} else {
			rvPickupFeedImages.setVisibility(View.GONE);
		}
		if(!TextUtils.isEmpty(customerInfo.getCustomerNotes()) || !TextUtils.isEmpty(customerInfo.getVendorMessage())){
			tvCustomerNotes.setVisibility(View.VISIBLE);
			if(!TextUtils.isEmpty(customerInfo.getCustomerNotes())){
				if(customerInfo.getCustomerNotes().length() > 20){
					tvCustomerNotes.setText(R.string.click_to_view_notes);
					tvCustomerNotes.setEnabled(true);
				} else {
					tvCustomerNotes.setText(activity.getString(R.string.note)+ ": "+customerInfo.getCustomerNotes());
					tvCustomerNotes.setEnabled(false);
				}
			} else {
				if(customerInfo.getVendorMessage().length() > 20){
					tvCustomerNotes.setText(R.string.click_to_view_notes);
					tvCustomerNotes.setEnabled(true);
				} else {
					tvCustomerNotes.setText(activity.getString(R.string.note)+": "+customerInfo.getVendorMessage());
					tvCustomerNotes.setEnabled(false);
				}
			}
		} else {
			tvCustomerNotes.setVisibility(View.GONE);
			tvCustomerNotes.setText("");
		}
		tvCustomerNotes.setOnClickListener(view -> openNotesDialog(TextUtils.isEmpty(customerInfo.getCustomerNotes())? customerInfo.getVendorMessage(): customerInfo.getCustomerNotes()));


		if(deliveryInfos.getPickupData().getLoadingStatus() ==1){
			relativeLayoutLoading.setVisibility(View.VISIBLE);
		}else {
			relativeLayoutLoading.setVisibility(View.GONE);
		}

		textViewCashReq.setText(String.valueOf(activity.getResources().getString(R.string.cash_to_collected)
				+": "+Utils.formatCurrencyValue(deliveryInfos.getCurrencyUnit(), deliveryInfos.getPickupData().getCashToCollect())));

		helpBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.openCallIntent(activity, Data.userData.driverSupportNumber);
				Log.i("completeRingData", Database2.getInstance(activity).getRingCompleteData());
			}
		});
		changeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.changeButton.performClick();
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

		relativeLayoutMerchantDetails.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

			}
		});
//		updateList(deliveryStatusOpened);
		return rootView;
	}
	private void openNotesDialog(final String customerNotes) {
		try {
			NotesDialog notesDialog = new NotesDialog(activity, customerNotes, notes -> {
			});
			notesDialog.show(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void backPress() {
		try {
			activity.onBackPressed();
		} catch (Exception e) {
			e.printStackTrace();
			activity.onBackPressed();
		}
	}

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
