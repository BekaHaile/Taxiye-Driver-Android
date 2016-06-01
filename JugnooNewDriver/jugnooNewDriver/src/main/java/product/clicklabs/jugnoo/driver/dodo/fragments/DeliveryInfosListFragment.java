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
import android.widget.LinearLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.dodo.adapters.DeliveryInfoAdapter;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryInfo;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Utils;


@SuppressLint("ValidFragment")
public class DeliveryInfosListFragment extends Fragment {

	private LinearLayout linearLayoutRoot;

	private Button buttonBack;
	private TextView textViewTitle;
	private RecyclerView recyclerViewDeliveryInfo;
	private DeliveryInfoAdapter deliveryInfoAdapter;


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

		buttonBack = (Button) rootView.findViewById(R.id.buttonBack);
		textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle);
		textViewTitle.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		textViewTitle.setText("Title");

		recyclerViewDeliveryInfo = (RecyclerView) rootView.findViewById(R.id.recyclerViewDeliveryInfo);
		recyclerViewDeliveryInfo.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewDeliveryInfo.setItemAnimator(new DefaultItemAnimator());
		recyclerViewDeliveryInfo.setHasFixedSize(false);

		deliveryInfoAdapter = new DeliveryInfoAdapter(activity,
				Data.getCurrentCustomerInfo().getDeliveryInfos(),
				new DeliveryInfoAdapter.Callback() {
					@Override
					public void onClick(int position, DeliveryInfo deliveryInfo) {
						activity.getTransactionUtils().openMarkDeliveryFragment(activity,
								activity.getRelativeLayoutContainer(), deliveryInfo.getId());
					}

					@Override
					public void onCallClick(int position, DeliveryInfo deliveryInfo) {
						Utils.openCallIntent(activity, deliveryInfo.getCustomerNo());
					}
				});

		recyclerViewDeliveryInfo.setAdapter(deliveryInfoAdapter);

		buttonBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.onBackPressed();
			}
		});


		return rootView;
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
