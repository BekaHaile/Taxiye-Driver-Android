package product.clicklabs.jugnoo.driver.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.home.adapters.CustomerInfoAdapter;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.Fonts;


public class SwitchCustomerFragment extends Fragment {

	private RelativeLayout relativeLayoutRoot, relativeLayoutReset;
	private Button backBtn, resetBtn;

	private RecyclerView recyclerViewCustomer;

	private View rootView;
	private HomeActivity activity;
	private CustomerInfoAdapter customerInfoAdapter;
	private TextView title, restText;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_switch_ride, container, false);


		activity = (HomeActivity) getActivity();
		relativeLayoutRoot = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutRoot);
		try {
			if (relativeLayoutRoot != null) {
				new ASSL(activity, relativeLayoutRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


		relativeLayoutReset = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutReset);
		backBtn = (Button) rootView.findViewById(R.id.backBtn);
		resetBtn = (Button) rootView.findViewById(R.id.resetBtn);
		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.onBackPressed();
			}
		});


		recyclerViewCustomer = (RecyclerView) rootView.findViewById(R.id.recyclerViewCustomer);
		recyclerViewCustomer.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
		recyclerViewCustomer.setItemAnimator(new DefaultItemAnimator());
		recyclerViewCustomer.setHasFixedSize(false);

		restText = (TextView) rootView.findViewById(R.id.restText);
		restText.setTypeface(Fonts.mavenRegular(activity));
		title = (TextView) rootView.findViewById(R.id.title);
		title.setTypeface(Fonts.mavenRegular(activity));

		if(Data.getCurrentCustomerInfo().getIsDeliveryPool() == 1){
			title.setText(activity.getResources().getString(R.string.pool_only));
		} else {
			title.setText(activity.getResources().getString(R.string.select_customer));
		}

		relativeLayoutReset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.changeCustomerState(true);
				activity.switchDriverScreen(HomeActivity.driverScreenMode);
				activity.onBackPressed();
			}
		});

		customerInfoAdapter = new CustomerInfoAdapter(activity, new CustomerInfoAdapter.Callback() {
			@Override
			public void onClick(int position, CustomerInfo customerInfo) {
				Data.setCurrentEngagementId(String.valueOf(customerInfo.getEngagementId()));
				activity.changeCustomerState(false);
				activity.switchDriverScreen(HomeActivity.driverScreenMode);
				activity.onBackPressed();
			}

			@Override
			public void onCancelClick(int position, CustomerInfo customerInfo) {
				activity.onBackPressed();
			}

		});
		recyclerViewCustomer.setAdapter(customerInfoAdapter);

		try {
		} catch (Exception e) {
			e.printStackTrace();
		}


		return rootView;
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
