package product.clicklabs.jugnoo.driver.dodo.fragments;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.adapters.DeliveryInfoInRideAdapter;
import product.clicklabs.jugnoo.driver.dodo.adapters.DeliveryInfoAdapter;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryInfo;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryStatus;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.Fonts;


@SuppressLint("ValidFragment")
public class DeliveryInfosListInRideFragment extends Fragment {

	private LinearLayout linearLayoutRoot;

	private Button buttonBack;
	private TextView textViewTitle, textViewMerchantName, textViewCashReq;
	private ImageView imageViewCompleted, imageViewCurrent;
	private RecyclerView recyclerViewDeliveryInfoInRide;
	private DeliveryInfoInRideAdapter deliveryInfoInRideAdapter;
	private RelativeLayout relativeLayoutCall;

	private ArrayList<DeliveryInfo> deliveryInfos = new ArrayList<>();
	private DeliveryStatus deliveryStatusOpened = DeliveryStatus.PENDING;
	private int engagementId;

	private View rootView;
	private HomeActivity activity;

	public DeliveryInfosListInRideFragment(int engagementId, DeliveryStatus deliveryStatus) {
		this.engagementId = engagementId;
		deliveryStatusOpened = deliveryStatus;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_delivery_info_list_inride, container, false);
		activity = (HomeActivity) getActivity();

		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		try {
			linearLayoutRoot.setLayoutParams(new LayoutParams(720, 1134));
			ASSL.DoMagic(linearLayoutRoot);
		} catch (Exception e) {
			e.printStackTrace();
		}


		textViewMerchantName = (TextView) rootView.findViewById(R.id.textViewMerchantName);
		textViewMerchantName.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		textViewCashReq = (TextView) rootView.findViewById(R.id.textViewCashReq);
		textViewCashReq.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);

		relativeLayoutCall = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutCall);

		imageViewCompleted = (ImageView) rootView.findViewById(R.id.imageViewCompleted);
		imageViewCurrent = (ImageView) rootView.findViewById(R.id.imageViewCurrent);

		buttonBack = (Button) rootView.findViewById(R.id.buttonBack);
		textViewTitle = (TextView) rootView.findViewById(R.id.title);
		textViewTitle.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);

		recyclerViewDeliveryInfoInRide = (RecyclerView) rootView.findViewById(R.id.recyclerViewDeliveryInfoInRide);
		recyclerViewDeliveryInfoInRide.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewDeliveryInfoInRide.setItemAnimator(new DefaultItemAnimator());
		recyclerViewDeliveryInfoInRide.setHasFixedSize(false);


		deliveryInfoInRideAdapter = new DeliveryInfoInRideAdapter(activity, deliveryInfos);

		recyclerViewDeliveryInfoInRide.setAdapter(deliveryInfoInRideAdapter);

		buttonBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				backPress();
			}
		});

		updateList(deliveryStatusOpened);
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


	private void updateList(DeliveryStatus deliveryStatus) {
		try {
			deliveryInfos.clear();
			for (DeliveryInfo deliveryInfo : Data.getCustomerInfo(String.valueOf(engagementId)).getDeliveryInfos()) {
				deliveryInfos.add(deliveryInfo);
			}
			deliveryInfoAdapter.notifyDataSetChanged();
			deliveryStatusOpened = deliveryStatus;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		try {
			if (!hidden) {
				if (deliveryStatusOpened == DeliveryStatus.PENDING
						&& deliveryInfos.size() == 1
						&& deliveryInfos.get(0).getStatus() != DeliveryStatus.PENDING.getOrdinal()) {
					deliveryStatusOpened = DeliveryStatus.COMPLETED;
				}
				updateList(deliveryStatusOpened);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			ASSL.closeActivity(linearLayoutRoot);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.gc();
	}

}
