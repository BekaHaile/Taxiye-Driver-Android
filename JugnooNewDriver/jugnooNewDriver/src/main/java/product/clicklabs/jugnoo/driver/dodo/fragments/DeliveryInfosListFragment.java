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

import java.lang.reflect.Array;
import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.dodo.adapters.DeliveryInfoAdapter;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryInfo;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryStatus;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Utils;


@SuppressLint("ValidFragment")
public class DeliveryInfosListFragment extends Fragment {

	private LinearLayout linearLayoutRoot;

	private Button buttonBack;
	private LinearLayout currentLLayout, completedLLayout;
	private TextView textViewTitle, textViewCompleted, textViewCurrent, textViewMerchantMessage;
	private ImageView imageViewCompleted, imageViewCurrent;
	private RecyclerView recyclerViewDeliveryInfo;
	private DeliveryInfoAdapter deliveryInfoAdapter;

	private ArrayList<DeliveryInfo> deliveryInfos = new ArrayList<>();

	private View rootView;
	private HomeActivity activity;

	public DeliveryInfosListFragment() {}


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

		textViewCompleted = (TextView) rootView.findViewById(R.id.textViewCompleted);
		textViewCompleted.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		textViewCurrent = (TextView) rootView.findViewById(R.id.textViewCurrent);
		textViewCurrent.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);

		imageViewCompleted = (ImageView) rootView.findViewById(R.id.imageViewCompleted);
		imageViewCurrent = (ImageView) rootView.findViewById(R.id.imageViewCurrent);

		buttonBack = (Button) rootView.findViewById(R.id.buttonBack);
		textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle);
		textViewTitle.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		textViewTitle.setText(activity.getResources().getString(R.string.deliveries));

		textViewMerchantMessage = (TextView) rootView.findViewById(R.id.textViewMerchantMessage);
		textViewMerchantMessage.setTypeface(Fonts.mavenRegular(activity), Typeface.NORMAL);

		recyclerViewDeliveryInfo = (RecyclerView) rootView.findViewById(R.id.recyclerViewDeliveryInfo);
		recyclerViewDeliveryInfo.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewDeliveryInfo.setItemAnimator(new DefaultItemAnimator());
		recyclerViewDeliveryInfo.setHasFixedSize(false);

		currentLLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				textViewCurrent.setTextColor(getResources().getColor(R.color.new_orange));
				textViewCompleted.setTextColor(getResources().getColor(R.color.text_color));
				imageViewCurrent.setBackgroundColor(getResources().getColor(R.color.new_orange));
				imageViewCompleted.setBackgroundColor(getResources().getColor(R.color.transparent));
				updateList(DeliveryStatus.PENDING);

			}
		});

		completedLLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				textViewCurrent.setTextColor(getResources().getColor(R.color.text_color));
				textViewCompleted.setTextColor(getResources().getColor(R.color.new_orange));
				imageViewCurrent.setBackgroundColor(getResources().getColor(R.color.transparent));
				imageViewCompleted.setBackgroundColor(getResources().getColor(R.color.new_orange));
				updateList(DeliveryStatus.COMPLETED);
			}
		});


		deliveryInfoAdapter = new DeliveryInfoAdapter(activity,
				deliveryInfos,
				new DeliveryInfoAdapter.Callback() {
					@Override
					public void onClick(int position, DeliveryInfo deliveryInfo) {
						activity.getTransactionUtils().openMarkDeliveryFragment(activity,
								activity.getRelativeLayoutContainer(), deliveryInfo.getId());
					}
				});

		recyclerViewDeliveryInfo.setAdapter(deliveryInfoAdapter);

		buttonBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.onBackPressed();
			}
		});

		updateList(DeliveryStatus.PENDING);

		return rootView;
	}


	private void updateList(DeliveryStatus deliveryStatus){
		deliveryInfos.clear();
		for(DeliveryInfo deliveryInfo : Data.getCurrentCustomerInfo().getDeliveryInfos()){
			if(deliveryStatus.getOrdinal() == DeliveryStatus.PENDING.getOrdinal()
					&& deliveryInfo.getStatus() == DeliveryStatus.PENDING.getOrdinal()) {
				deliveryInfos.add(deliveryInfo);
			} else if(deliveryStatus.getOrdinal() != DeliveryStatus.PENDING.getOrdinal()
					&& deliveryInfo.getStatus() != DeliveryStatus.PENDING.getOrdinal()){
				deliveryInfos.add(deliveryInfo);
			}
		}
		deliveryInfoAdapter.notifyDataSetChanged();
	}


	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		try {
			if (!hidden) {
                deliveryInfoAdapter.notifyDataSetChanged();
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
