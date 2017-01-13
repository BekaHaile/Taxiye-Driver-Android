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
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.dodo.adapters.DeliveryInfoAdapter;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryInfo;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryStatus;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.Fonts;


@SuppressLint("ValidFragment")
public class DeliveryInfosListFragment extends Fragment {

	private LinearLayout linearLayoutRoot;

	private Button buttonBack;
	private LinearLayout currentLLayout, completedLLayout;
	private TextView textViewTitle, textViewCompleted, textViewCurrent, textViewMerchantMessage, textViewPlaceholderMessage;
	private ImageView imageViewCompleted, imageViewCurrent;
	private RecyclerView recyclerViewDeliveryInfo;
	private DeliveryInfoAdapter deliveryInfoAdapter;

	private ArrayList<DeliveryInfo> deliveryInfos = new ArrayList<>();
	private DeliveryStatus deliveryStatusOpened = DeliveryStatus.PENDING;
	private int engagementId;

	private View rootView;
	private HomeActivity activity;

	public DeliveryInfosListFragment(int engagementId, DeliveryStatus deliveryStatus) {
		this.engagementId = engagementId;
		deliveryStatusOpened = deliveryStatus;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_delivery_info_list, container, false);
		activity = (HomeActivity) getActivity();

		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		try {
			linearLayoutRoot.setLayoutParams(new LayoutParams(720, 1134));
			ASSL.DoMagic(linearLayoutRoot);
		} catch (Exception e) {
			e.printStackTrace();
		}

		currentLLayout = (LinearLayout) rootView.findViewById(R.id.currentLLayout);
		completedLLayout = (LinearLayout) rootView.findViewById(R.id.completedLLayout);

		currentLLayout.setVisibility(View.GONE);
		completedLLayout.setVisibility(View.GONE);

		textViewCompleted = (TextView) rootView.findViewById(R.id.textViewCompleted);
		textViewCompleted.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		textViewCurrent = (TextView) rootView.findViewById(R.id.textViewCurrent);
		textViewCurrent.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);

		imageViewCompleted = (ImageView) rootView.findViewById(R.id.imageViewCompleted);
		imageViewCurrent = (ImageView) rootView.findViewById(R.id.imageViewCurrent);

		buttonBack = (Button) rootView.findViewById(R.id.buttonBack);
		textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle);
		textViewTitle.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		textViewTitle.setText(activity.getResources().getString(R.string.select_delivery));

		textViewMerchantMessage = (TextView) rootView.findViewById(R.id.textViewMerchantMessage);
		textViewMerchantMessage.setTypeface(Fonts.mavenRegular(activity));
		textViewPlaceholderMessage = (TextView) rootView.findViewById(R.id.textViewPlaceholderMessage);
		textViewPlaceholderMessage.setTypeface(Fonts.mavenRegular(activity));
		textViewPlaceholderMessage.setVisibility(View.GONE);

		recyclerViewDeliveryInfo = (RecyclerView) rootView.findViewById(R.id.recyclerViewDeliveryInfo);
		recyclerViewDeliveryInfo.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewDeliveryInfo.setItemAnimator(new DefaultItemAnimator());
		recyclerViewDeliveryInfo.setHasFixedSize(false);

		currentLLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updateList(DeliveryStatus.PENDING);
			}
		});

		completedLLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updateList(DeliveryStatus.COMPLETED);
			}
		});


		deliveryInfoAdapter = new DeliveryInfoAdapter(activity,
				deliveryInfos,
				new DeliveryInfoAdapter.Callback() {
					@Override
					public void onClick(int position) {
						activity.setDeliveryPos(position);
						backPress();
					}

					@Override
					public void onCancelClick(int position) {
						backPress();
					}
				});

		recyclerViewDeliveryInfo.setAdapter(deliveryInfoAdapter);

		buttonBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				backPress();
			}
		});

		updateList(deliveryStatusOpened);

		try {
			String message = Data.getCustomerInfo(String.valueOf(engagementId)).getVendorMessage();
			textViewMerchantMessage.setVisibility(View.GONE);
			if (!"".equalsIgnoreCase(message)) {
				textViewMerchantMessage.setVisibility(View.GONE);
				textViewMerchantMessage.setText(message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


		return rootView;
	}

	public void backPress(){
		try {
			if(deliveryInfos != null) {
				for (int i = 0; i < deliveryInfos.size(); i++) {
					deliveryInfos.get(i).setSate(false);
				}
			}
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
// if(deliveryStatus.getOrdinal() == DeliveryStatus.PENDING.getOrdinal()
//						&& deliveryInfo.getStatus() == DeliveryStatus.PENDING.getOrdinal()) {
//					deliveryInfos.add(deliveryInfo);
//				} else if(deliveryStatus.getOrdinal() != DeliveryStatus.PENDING.getOrdinal()
//						&& deliveryInfo.getStatus() != DeliveryStatus.PENDING.getOrdinal()){
//					deliveryInfos.add(deliveryInfo);
//				}
			}
			deliveryInfoAdapter.notifyDataSetChanged();

			textViewPlaceholderMessage.setVisibility(View.GONE);
//			if(deliveryStatus.getOrdinal() == DeliveryStatus.PENDING.getOrdinal()){
//				textViewCurrent.setTextColor(getResources().getColor(R.color.new_orange));
//				textViewCompleted.setTextColor(getResources().getColor(R.color.text_color));
//				imageViewCurrent.setBackgroundColor(getResources().getColor(R.color.new_orange));
//				imageViewCompleted.setBackgroundColor(getResources().getColor(R.color.transparent));
//				if(deliveryInfos.size() == 0){
//					textViewPlaceholderMessage.setVisibility(View.VISIBLE);
//					textViewPlaceholderMessage.setText(activity.getResources().getString(R.string.no_deliveries_pending));
//				}
//			} else{
//				textViewCurrent.setTextColor(getResources().getColor(R.color.text_color));
//				textViewCompleted.setTextColor(getResources().getColor(R.color.new_orange));
//				imageViewCurrent.setBackgroundColor(getResources().getColor(R.color.transparent));
//				imageViewCompleted.setBackgroundColor(getResources().getColor(R.color.new_orange));
//				if(deliveryInfos.size() == 0){
//					textViewPlaceholderMessage.setVisibility(View.VISIBLE);
//					textViewPlaceholderMessage.setText(activity.getResources().getString(R.string.no_deliveries_completed));
//				}
//			}
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
